package tech.linard.android.unleash.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import tech.linard.android.unleash.R;
import tech.linard.android.unleash.Util;
import tech.linard.android.unleash.activities.MainActivity;
import tech.linard.android.unleash.data.UnleashContract;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        String sortOrder = UnleashContract.TradeEntry.COLUMN_DATE + " DESC";

        String[] columns = {UnleashContract.TradeEntry.COLUMN_PRICE,
                UnleashContract.TradeEntry.COLUMN_AMMOUNT,
                UnleashContract.TradeEntry.COLUMN_DATE,
                UnleashContract.TradeEntry.COLUMN_TYPE,
                UnleashContract.TradeEntry._ID};

        Cursor cursor = context.getContentResolver().query(UnleashContract.TradeEntry.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder);

        cursor.moveToFirst();
        int timestamp = cursor.getInt(cursor.getColumnIndex(UnleashContract.TradeEntry.COLUMN_DATE));
        String txtTimestamp = Util.getReadableDateFromUnixTime(timestamp);

        String priceText = "R$ " + String.valueOf(
                cursor.getInt(cursor.getColumnIndex(UnleashContract.TradeEntry.COLUMN_PRICE)));

        CharSequence widgetText = priceText;
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewText(R.id.widget_timestamp, txtTimestamp);
        // Create intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

