package com.uni.ailab.scp.receiver;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMPONENTS = "components";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_SCHEME = "scheme";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_POLICIES = "policies";
    public static final String COLUMN_PERMISSIONS = "permissions";

    private static final String DATABASE_NAME = "components.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMPONENTS + "(" +
            COLUMN_ID + " text primary key, " +
            COLUMN_NAME + " text not null, " +
            COLUMN_TYPE + " text not null, " +
            COLUMN_POLICIES + " text, " +
            COLUMN_PERMISSIONS + " text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPONENTS);
        onCreate(db);
    }

    public String getQuery(String action, Uri data) {
        return "SELECT * FROM " + TABLE_COMPONENTS +" WHERE " + COLUMN_ACTION +" = "+ action;
    }

    public Cursor doQuery(String query) {

        SQLiteDatabase database = getReadableDatabase();

        // TODO: should check Uri scheme
        return database.rawQuery(query, null);
    }

    public Cursor getReceivers(String action, Uri data) {
        SQLiteDatabase database = getReadableDatabase();

        // TODO: should check Uri scheme
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_COMPONENTS +" WHERE " + COLUMN_ACTION +" = "+ action, null);

        return cursor;
    }

} 