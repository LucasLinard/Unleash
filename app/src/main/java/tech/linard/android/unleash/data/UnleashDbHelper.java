package tech.linard.android.unleash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import tech.linard.android.unleash.data.UnleashContract.*;
/**
 * Created by llinard on 07/07/17.
 */

public class UnleashDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "unleash.db";

    public UnleashDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TICKER_TABLE =
                "CREATE TABLE " + TickerEntry.TABLE_NAME + " (" +
                        TickerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TickerEntry.COLUMN_HIGH + " REAL, " +
                        TickerEntry.COLUMN_LOW + " REAL, " +
                        TickerEntry.COLUMN_VOL + " REAL, " +
                        TickerEntry.COLUMN_LAST + " REAL, " +
                        TickerEntry.COLUMN_BUY + " REAL, " +
                        TickerEntry.COLUMN_SELL + " REAL, " +
                        TickerEntry.COLUMN_DATE + " INTEGER " +
                        " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TICKER_TABLE);


        final String SQL_CREATE_TRADE_TABLE =
                "CREATE TABLE " + TradeEntry.TABLE_NAME + " (" +
                        TradeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TradeEntry.COLUMN_DATE + " INTEGER, " +
                        TradeEntry.COLUMN_PRICE + " REAL, " +
                        TradeEntry.COLUMN_AMMOUNT + " REAL, " +
                        TradeEntry.COLUMN_TRANSACTION_ID + " INTEGER, " +
                        TradeEntry.COLUMN_TYPE+ " TEXT " +
                        " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRADE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TickerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TradeEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

}
