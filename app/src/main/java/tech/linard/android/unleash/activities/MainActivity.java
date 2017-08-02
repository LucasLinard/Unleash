package tech.linard.android.unleash.activities;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.data.UnleashContract.TickerEntry;
import tech.linard.android.unleash.fragments.MainFragment;
import tech.linard.android.unleash.fragments.OrderbookFragment;
import tech.linard.android.unleash.fragments.TradeFragment;
import tech.linard.android.unleash.fragments.dummy.DummyContent;
import tech.linard.android.unleash.model.Ticker;
import tech.linard.android.unleash.model.Trade;
import tech.linard.android.unleash.network.VolleySingleton;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener,
        OrderbookFragment.OnFragmentInteractionListener,
        TradeFragment.OnListFragmentInteractionListener {

    Fragment mFragmentMain = null;

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "tech.linard.android.unleash";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "linard.tech";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;


    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testNetwork();
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
        navigationView.setNavigationItemSelectedListener(this);

        // Get the content resolver for your app
        mResolver = getContentResolver();
        // Create the dummy account
        mAccount = CreateSyncAccount(this);
        /*
         * Turn on periodic syncing
         */
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);

        mFragmentMain = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment, mFragmentMain);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private Account CreateSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
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
                onRefreshButtonClick();
                break;
            case R.id.action_refresh:
                testNetwork();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefreshButtonClick() {
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
    private void testNetwork() {
        String url = null;

        url = "https://www.mercadobitcoin.net/api/trades/";

        JsonArrayRequest tradeJsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Vector<ContentValues> cVVector
                                = new Vector<ContentValues>(response.length());
                        for (int i = 0; i < response.length();i++) {
                            ContentValues contentValues = new ContentValues();
                            JSONObject jsonObject = response.optJSONObject(i);
                            Integer date =  jsonObject.optInt("date");
                            double price = jsonObject.optDouble("price");
                            double ammount = jsonObject.optDouble("amount");
                            Integer transactionID = jsonObject.optInt("tid");
                            String type = jsonObject.optString("type");

                            contentValues.put(UnleashContract.TradeEntry.COLUMN_DATE, date);
                            contentValues.put(UnleashContract.TradeEntry.COLUMN_PRICE, price);
                            contentValues.put(UnleashContract.TradeEntry.COLUMN_AMMOUNT,ammount);
                            contentValues.put(UnleashContract.TradeEntry.COLUMN_TRANSACTION_ID,transactionID);
                            contentValues.put(UnleashContract.TradeEntry.COLUMN_TYPE,type);

                            cVVector.add(contentValues);

                        }
                        int inserted = 0;
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            inserted = getContentResolver().bulkInsert(UnleashContract.TradeEntry.CONTENT_URI,
                                    cvArray);
                            getContentResolver().notifyChange(UnleashContract.TradeEntry.CONTENT_URI, null);

                        }
                        if (inserted > 0) {
                            getContentResolver().notifyChange(UnleashContract.TradeEntry.CONTENT_URI, null);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Volley ERROR!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(tradeJsonArrayRequest);

        url = "https://www.mercadobitcoin.net/api/ticker/";
        JsonObjectRequest tickerJsObjRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Ticker ticker = Util.tickerFromJSon(response.optJSONObject("ticker"));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TickerEntry.COLUMN_HIGH, ticker.getHigh());
                        contentValues.put(TickerEntry.COLUMN_LOW, ticker.getLow());
                        contentValues.put(TickerEntry.COLUMN_BUY, ticker.getBuy());
                        contentValues.put(TickerEntry.COLUMN_SELL, ticker.getSell());
                        contentValues.put(TickerEntry.COLUMN_LAST, ticker.getLast());
                        contentValues.put(TickerEntry.COLUMN_VOL, ticker.getVol());
                        contentValues.put(TickerEntry.COLUMN_DATE, ticker.getDate());

                        Uri newUri = getContentResolver()
                                .insert(TickerEntry.CONTENT_URI, contentValues);
                        if (newUri != null) {
                            // If the new content URI is null, then there was an error with insertion.
                            Toast.makeText(MainActivity.this, "Sucesso no Ticker!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Volley ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(tickerJsObjRequest);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                mFragmentMain = new MainFragment();
                break;
            case R.id.orderbook:
                mFragmentMain = new OrderbookFragment();
                break;
            case R.id.trade_list:
                mFragmentMain = new TradeFragment();
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        if (mFragmentMain != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment, mFragmentMain);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Trade item) {

    }
}
