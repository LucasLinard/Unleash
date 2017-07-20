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

import org.json.JSONObject;

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
        Log.d(LOG_TAG, "onPerformSync");

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = "https://www.mercadobitcoin.net/api/ticker/";
        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(), future, future);
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);

        try {
            JSONObject response = future.get();
            Ticker ticker = Util.tickerFromJSon(response.optJSONObject("ticker"));
            ContentValues contentValues = new ContentValues();
            contentValues.put(UnleashContract.TickerEntry.COLUMN_HIGH, ticker.getHigh());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_LOW, ticker.getLow());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_BUY, ticker.getBuy());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_SELL, ticker.getSell());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_LAST, ticker.getVol());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_VOL, ticker.getVol());
            contentValues.put(UnleashContract.TickerEntry.COLUMN_DATE, ticker.getDate());

            Uri newUri = getContext().getContentResolver()
                    .insert(UnleashContract.TickerEntry.CONTENT_URI, contentValues);
            if (newUri != null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), "Ticker GRAVADO!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        }

    }
}
