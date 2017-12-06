package tech.linard.android.unleash.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by llinard on 07/07/17.
 */

public class UnleashContract {
    public static final String CONTENT_AUTHORITY = "tech.linard.android.unleash";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TICKER = "ticker";
    public static final String PATH_TRADE = "trade";
    public static final String PATH_API_KEY = "api_key";
    public static final String PATH_STOP_LOSS = "stop_loss";
    public static final String PATH_EXCHANGES = "exchanges";

    public static final class TickerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TICKER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TICKER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TICKER;

        // Table name
        public static final String TABLE_NAME = "ticker";

        // Fields
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_VOL = "vol";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_BUY = "buy";
        public static final String COLUMN_SELL = "sell";
        public static final String COLUMN_DATE = "date";


        public static Uri buildTickerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TradeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRADE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRADE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRADE;

        // Table name
        public static final String TABLE_NAME = "trade";

        // Fields
        private Integer date;
        private double price;
        private double ammount;
        private Integer transactionID;
        private String type;

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_AMMOUNT = "ammount";
        public static final String COLUMN_TRANSACTION_ID = "transactionid";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildTradeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ApiKeysEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_API_KEY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_API_KEY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_API_KEY;
    }

    public static final class ExchangesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXCHANGES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXCHANGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXCHANGES;

    }

    public static final class StopLossEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOP_LOSS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOP_LOSS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOP_LOSS;

    }
}
