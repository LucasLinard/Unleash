package tech.linard.android.unleash.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.linard.android.unleash.data.UnleashContract.TickerEntry;
import tech.linard.android.unleash.data.UnleashContract.TradeEntry;

/**
 * Created by llinard on 07/07/17.
 */

public class UnleashProvider extends ContentProvider {
    //The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private UnleashDbHelper dbHelper;
    static final int TICKER = 100;
    static final int TRADE = 101;



    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = UnleashContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, UnleashContract.PATH_TICKER, TICKER);
        matcher.addURI(authority, UnleashContract.PATH_TRADE, TRADE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new UnleashDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TICKER:
                retCursor = dbHelper.getReadableDatabase().query(
                        TickerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case TRADE:
                retCursor = dbHelper.getReadableDatabase().query(
                        TradeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TICKER:
                return TickerEntry.CONTENT_ITEM_TYPE;
            case TRADE:
                return TradeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnUri;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long _id = -1;
        switch (match) {
            case TICKER:
                _id = db.insert(TickerEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 ) {
                    returnUri = TickerEntry.buildTickerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRADE:
                _id = db.insert(TradeEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 ) {
                    returnUri = TradeEntry.buildTradeUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRADE:
                delete(uri,null,null);
                return super.bulkInsert(uri, values);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case TICKER:
                rowsDeleted = db.delete(TickerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRADE:
                rowsDeleted = db.delete(TradeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }
}
