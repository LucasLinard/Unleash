package tech.linard.android.unleash.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
import java.util.concurrent.ExecutionException;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.activities.MainActivity;
import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.model.Ticker;
import tech.linard.android.unleash.network.VolleySingleton;

/**
 * Created by llinard on 10/07/17.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = SyncAdapter.class.getSimpleName();


    ContentResolver mContentResolver;

    public SyncAdapter(Context context,
                       boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context,
                       boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        // Get TICKER data
        Ion.with(getContext())
                .load("https://www.mercadobitcoin.net/api/ticker/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        String string = result.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(string);
                            Ticker ticker = Util.tickerFromJSon(jsonObject.optJSONObject("ticker"));

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_HIGH, ticker.getHigh());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_LOW, ticker.getLow());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_BUY, ticker.getBuy());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_SELL, ticker.getSell());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_LAST, ticker.getLast());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_VOL, ticker.getVol());
                            contentValues.put(UnleashContract.TickerEntry.COLUMN_DATE, ticker.getDate());

                            Uri newUri = mContentResolver
                                    .insert(UnleashContract.TickerEntry.CONTENT_URI, contentValues);
                            mContentResolver.notifyChange(UnleashContract.TickerEntry.CONTENT_URI, null);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });

        // Get TRADE data

        Ion.with(getContext())
                .load("https://www.mercadobitcoin.net/api/trades/")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        String string = result.toString();
                        try {
                            JSONArray jsonArray  = new JSONArray(string);
                            Vector<ContentValues> cVVector
                                    = new Vector<ContentValues>(jsonArray.length());
                                for (int i = 0; i < jsonArray.length() ;i++) {
                                    ContentValues contentValues = new ContentValues();
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    Integer date =  jsonObject.optInt("date");
                                    double price = jsonObject.optDouble("price");
                                    double ammount = jsonObject.optDouble("amount");
                                    Integer transactionID = jsonObject.optInt("tid");
                                    String type = jsonObject.optString("type");

                                    contentValues
                                            .put(UnleashContract.TradeEntry.COLUMN_DATE,
                                                    date);
                                    contentValues
                                            .put(UnleashContract.TradeEntry.COLUMN_PRICE,
                                                    price);
                                    contentValues
                                            .put(UnleashContract.TradeEntry.COLUMN_AMMOUNT,
                                                    ammount);
                                    contentValues
                                            .put(UnleashContract.TradeEntry.COLUMN_TRANSACTION_ID,
                                                    transactionID);
                                    contentValues
                                            .put(UnleashContract.TradeEntry.COLUMN_TYPE,
                                                    type);
                                    cVVector.add(contentValues);
                                }

                                if (cVVector.size() > 0) {
                                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                                    cVVector.toArray(cvArray);
                                    mContentResolver.bulkInsert(UnleashContract.TradeEntry.CONTENT_URI,
                                            cvArray);
                                    mContentResolver.notifyChange(UnleashContract.TradeEntry.CONTENT_URI, null);
                                }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });

    }
}
