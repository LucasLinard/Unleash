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
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.fragments.MainFragment;
import tech.linard.android.unleash.fragments.OrderbookFragment;
import tech.linard.android.unleash.fragments.TradeFragment;
import tech.linard.android.unleash.fragments.WelcomeFragment;
import tech.linard.android.unleash.model.Trade;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener,
        OrderbookFragment.OnFragmentInteractionListener,
        TradeFragment.OnListFragmentInteractionListener {

    Fragment mFragmentNew = null;
    Fragment mFragmentOld = null;
    Fragment mFragmentWelcome = null;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
        }
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

}
