package tech.linard.android.unleash.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.fragments.ExchangesFragment;
import tech.linard.android.unleash.fragments.MainFragment;
import tech.linard.android.unleash.fragments.OrderbookFragment;
import tech.linard.android.unleash.fragments.StopLossFragment;
import tech.linard.android.unleash.fragments.TradeFragment;
import tech.linard.android.unleash.fragments.WelcomeFragment;
import tech.linard.android.unleash.fragments.dummy.DummyContent;
import tech.linard.android.unleash.model.StopLoss;
import tech.linard.android.unleash.model.Trade;
import tech.linard.android.unleash.model.User;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ExchangesFragment.OnListFragmentInteractionListener,
        StopLossFragment.OnListFragmentInteractionListener,
        NavigationView.OnClickListener,
        MainFragment.OnFragmentInteractionListener,
        OrderbookFragment.OnFragmentInteractionListener,
        TradeFragment.OnListFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    Fragment mFragmentNew = null;
    Fragment mFragmentOld = null;
    Fragment mFragmentWelcome = null;
    TextView mUserName;
    ImageView mUserImage;
    FirebaseUser mCurrentUser;

    private static final long MOVE_DEFAULT_TIME = 1000;
    private static final long FADE_DEFAULT_TIME = 300;

    // SYNC [START]
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;

    // Constants
    private static final int RC_SIGN_IN = 1001;


    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "tech.linard.android.unleash";

    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "linard.tech";

    // The account name
    public static final String ACCOUNT = "dummyaccount";


    // Firebase instance fields
    private FirebaseAuth mAuth;
    Account mAccount;
    FirebaseUser mUser;

    // SYNC [END]

    int fragmentId = 0;

    // User INFO
    String mName;
    String mEmail;
    Uri mPhotoUrl;

    private ShareActionProvider mShareActionProvider;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("fragmentId", fragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        mUserName = headerView.findViewById(R.id.nav_user_id);
        mUserImage = headerView.findViewById(R.id.image_view_user);
        mUserImage.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String interval = prefs.getString("sync_frequency", "180");

        if (Long.valueOf(interval) > 0) {
            
            /*
             * Turn on periodic syncing
             */

            long SYNC_INTERVAL =
                    Long.valueOf(interval) *
                            SECONDS_PER_MINUTE;

            ContentResolver.addPeriodicSync(
                    mAccount,
                    AUTHORITY,
                    Bundle.EMPTY,
                    SYNC_INTERVAL);
        }
        syncNow();
        mFragmentWelcome = new WelcomeFragment();


        if (!networkUp()) {
            //  não possui conexão.
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment, mFragmentWelcome);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            // Possui conexão
            if (savedInstanceState != null) {
                // estamos recriando uma activity
                int currentFragmentId =  savedInstanceState.getInt("fragmentId");
                handleItemClick(currentFragmentId);
                changeFragment();
            } else {
                handleItemClick(R.id.nav_home);
                changeFragment();
            }
        }




    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                startPreferenceActivity();
                break;
            case R.id.action_refresh:
                onRefreshButtonClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefreshButtonClick() {

        syncNow();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();

            // Name, email address, and profile photo Url
            mName = mCurrentUser.getDisplayName();
            mEmail = mCurrentUser.getEmail();
            mPhotoUrl = mCurrentUser.getPhotoUrl();



            User user = new User(mCurrentUser.getUid()
                    , FirebaseInstanceId.getInstance().getToken());

            // Access a Cloud Firestore instance from your Activity

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            StopLoss stopLoss = new StopLoss(mCurrentUser.getUid(), 1, 44000.00123, 1.0);
            db.collection("stop_loss")
                    .add(stopLoss)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUuid())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });


            // Check if user's email is verified
            boolean emailVerified = mCurrentUser.isEmailVerified();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserName.setText(mName);
        Picasso.with(this)
                .load(mPhotoUrl)
                .placeholder(R.drawable.ic_currency_btc_white_48dp)
                .error(R.drawable.ic_currency_btc_white_48dp)
                .resize(250, 250)
                .into(mUserImage);
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void syncNow() {

        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        String itemName = null;
        String contentType = "nav_option";

        // Handle navigation view item clicks here.
        itemName = handleItemClick(item.getItemId());

        changeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        logData(itemId, itemName, contentType);
        return true;
    }

    private void changeFragment() {

        if (mFragmentNew != null && networkUp()) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            if (mFragmentOld == null) {
                mFragmentOld = new WelcomeFragment();
            }
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_in);

            fragmentTransaction.replace(R.id.fragment, mFragmentNew);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment, mFragmentWelcome);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }

    private String handleItemClick(int itemId) {
        String itemName = null;
        mFragmentOld = mFragmentNew;
        switch (itemId) {
            case R.id.nav_home:
                fragmentId = R.id.nav_home;
                mFragmentNew = new MainFragment();
                itemName = getResources().getString(R.string.ticker);
                break;
            case R.id.orderbook:
                fragmentId = R.id.orderbook;
                mFragmentNew = new OrderbookFragment();
                itemName = getResources().getString(R.string.orderbook);
                break;
            case R.id.trade_list:
                fragmentId = R.id.trade_list;
                mFragmentNew = new TradeFragment();
                itemName = getResources().getString(R.string.negociacoes);
                break;
            case R.id.stop_loss:
                fragmentId = R.id.stop_loss;
                mFragmentNew = new StopLossFragment();
                itemName = getResources().getString(R.string.stop_loss);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("stop_loss")
                        .whereEqualTo("token", mCurrentUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });


                break;
            case R.id.exchanges:
                fragmentId = R.id.trade_list;
                mFragmentNew = new ExchangesFragment();
                itemName = getResources().getString(R.string.exchanges);
                break;
            case R.id.nav_manage:
                fragmentId = R.id.nav_manage;
                startPreferenceActivity();

                itemName = getResources().getString(R.string.confiiguracao);
                break;
            case R.id.nav_send:
                fragmentId = R.id.nav_send;
                itemName = getResources().getString(R.string.enviar_cotacao);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String shareText = fetchQuoteFromContentProvider();
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
        return itemName;
    }

    private void startPreferenceActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private String fetchQuoteFromContentProvider() {
        String sortOrder = UnleashContract.TickerEntry.COLUMN_DATE + " DESC";

        String[] columns = {UnleashContract.TickerEntry.COLUMN_LAST
                , UnleashContract.TickerEntry.COLUMN_DATE};

        Cursor cursor = getContentResolver().query(UnleashContract.TickerEntry.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder);

        cursor.moveToFirst();
        String price = cursor.getString(cursor.getColumnIndex(UnleashContract.TickerEntry.COLUMN_LAST));
        int time = cursor.getInt(cursor.getColumnIndex(UnleashContract.TickerEntry.COLUMN_DATE));
        String timestamp = Util.getReadableDateFromUnixTime(time);

        cursor.close();
        return "Unleash Bitcoin: Última transação no Mercado Bitcoin a R$: " + price
                + " por Bitcoin. " + timestamp;
    }

    private void logData(int itemId, String itemName, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Trade item) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_user:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

}
