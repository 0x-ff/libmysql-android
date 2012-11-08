package com.example.mysqlshell.provider;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.database.Cursor;
import android.util.Log;
import android.text.TextUtils;
import android.content.ContentValues;
import android.database.SQLException;
import android.content.ContentUris;

import com.example.mysqlshell.provider.ConnectionsBookMetaData.ConnectionsBookTableMetaData;
import java.util.HashMap;

public class ConnectionsBook extends ContentProvider {
	
	private static HashMap<String, String> ConnProjMap;
	static 
	{
		ConnProjMap = new HashMap<String, String>();
		ConnProjMap.put(ConnectionsBookTableMetaData._ID, 
			ConnectionsBookTableMetaData._ID);
		
		ConnProjMap.put(ConnectionsBookTableMetaData.UNIQUE_NAME,
			ConnectionsBookTableMetaData.UNIQUE_NAME);
		ConnProjMap.put(ConnectionsBookTableMetaData.HOST_ADDR,
			ConnectionsBookTableMetaData.HOST_ADDR);
		ConnProjMap.put(ConnectionsBookTableMetaData.USER_NAME,
			ConnectionsBookTableMetaData.USER_NAME);
		ConnProjMap.put(ConnectionsBookTableMetaData.PASS_WORD,
			ConnectionsBookTableMetaData.PASS_WORD);
		ConnProjMap.put(ConnectionsBookTableMetaData.DB_NAME,
			ConnectionsBookTableMetaData.DB_NAME);
		ConnProjMap.put(ConnectionsBookTableMetaData.PORT_NUM,
			ConnectionsBookTableMetaData.PORT_NUM);
		ConnProjMap.put(ConnectionsBookTableMetaData.MODIFIED_AT,
			ConnectionsBookTableMetaData.MODIFIED_AT);
	}
	
	private static final UriMatcher sUriMatcher;
	
	private static final int INCOMING_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_URI_INDICATOR = 2;
	
	static 
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(ConnectionsBookMetaData.AUTHOITY, "connections",
			INCOMING_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(ConnectionsBookMetaData.AUTHOITY, "connections/#",
			INCOMING_SINGLE_URI_INDICATOR);
	}
	
	private DatabaseHelper mOpenHelper;
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	
	private static class DatabaseHelper 
		extends SQLiteOpenHelper 
	{
		DatabaseHelper(Context context) {

			super(context, ConnectionsBookMetaData.DATABASE_NAME, null,
				ConnectionsBookMetaData.DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL("CREATE TABLE " + 
				ConnectionsBookMetaData.TABLE_NAME + " (" + 
				ConnectionsBookMetaData.ConnectionsBookTableMetaData._ID + 
				" INTEGER PRIMARY KEY, " + 
				ConnectionsBookTableMetaData.UNIQUE_NAME + " TEXT, " + 
				ConnectionsBookTableMetaData.HOST_ADDR + " TEXT, " + 
				ConnectionsBookTableMetaData.USER_NAME + " TEXT, " + 
				ConnectionsBookTableMetaData.PASS_WORD + " TEXT, " + 
				ConnectionsBookTableMetaData.DB_NAME + " TEXT, " + 
				ConnectionsBookTableMetaData.PORT_NUM + " INTEGER, " + 
				ConnectionsBookTableMetaData.MODIFIED_AT + " INTEGER);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TAG", "Upgrading database from version " + oldVersion + " to " + 
				newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + ConnectionsBookMetaData.TABLE_NAME);
			onCreate(db);
		}
	}
	
	public String getType(Uri uri) {
		
		switch( sUriMatcher.match(uri) ) {
			
			case INCOMING_COLLECTION_URI_INDICATOR:
				return ConnectionsBookTableMetaData.CONTENT_TYPE;
			case INCOMING_SINGLE_URI_INDICATOR:
				return ConnectionsBookTableMetaData.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}
	
	public long getIdByUniqueName(String uniqueName) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ConnectionsBookMetaData.TABLE_NAME);
		qb.appendWhere(ConnectionsBookTableMetaData.UNIQUE_NAME + 
			"='" + uniqueName + "'");
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, new String[] 
			{ConnectionsBookTableMetaData._ID}, 
			null, null, null, null, null);
		int i = c.getCount();
		
		if (i > 0) {
			
			c.moveToFirst();
			long rowId = c.getLong(0);
			c.close();
			db.close();
			return rowId;
		}
		return 0;
	}
	
	public Cursor query(Uri uri, String[] projection, String selection, 
		String[] selectionArgs, String sortOrder ) 
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
			
			case INCOMING_COLLECTION_URI_INDICATOR:
				qb.setTables(ConnectionsBookMetaData.TABLE_NAME);
				qb.setProjectionMap(ConnProjMap);
			break;
			case INCOMING_SINGLE_URI_INDICATOR:
				qb.setTables(ConnectionsBookMetaData.TABLE_NAME);
				qb.setProjectionMap(ConnProjMap);
				qb.appendWhere(ConnectionsBookTableMetaData._ID + "=" + 
					uri.getPathSegments().get(1));
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ConnectionsBookTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, 
			null, null, orderBy);
		int i = c.getCount();
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	public Uri insert(Uri uri, ContentValues values) {
		
		if (sUriMatcher.match(uri) != INCOMING_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException();
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		values.put(ConnectionsBookTableMetaData.MODIFIED_AT, now);
		
		if (values.containsKey(ConnectionsBookTableMetaData.UNIQUE_NAME) == false) {
			throw new SQLException("Required " + ConnectionsBookTableMetaData.UNIQUE_NAME + 
				" " + uri);
		}
		
		String uniqueName = values.getAsString(ConnectionsBookTableMetaData.UNIQUE_NAME);
		
		if (uniqueName.length() <= 0) {
			throw new ConnNameEmptyException();
		}
		
		long rowId = getIdByUniqueName(uniqueName);
		if (rowId > 0) {

			Uri ret = ContentUris.withAppendedId(
				ConnectionsBookTableMetaData.CONTENT_URI, 
				rowId);
			int i = update(ret, values, null, null);
			return ret;
		}
		
		if (values.containsKey(ConnectionsBookTableMetaData.HOST_ADDR) == false) {
			throw new SQLException("Required " + ConnectionsBookTableMetaData.HOST_ADDR + 
				" " + uri);
		}
		
		if (values.containsKey(ConnectionsBookTableMetaData.USER_NAME) == false) {
			throw new SQLException("Required " + ConnectionsBookTableMetaData.USER_NAME + 
				" " + uri);
		}
		
		if (values.containsKey(ConnectionsBookTableMetaData.PASS_WORD) == false) {
			throw new SQLException("Required " + ConnectionsBookTableMetaData.PASS_WORD + 
				" " + uri);
		}
		
		if (values.containsKey(ConnectionsBookTableMetaData.DB_NAME) == false) {
			throw new SQLException("Required " + ConnectionsBookTableMetaData.DB_NAME + 
				" " + uri);
		}
		
		if (values.containsKey(ConnectionsBookTableMetaData.PORT_NUM) == false) {
			values.put(ConnectionsBookTableMetaData.PORT_NUM, 3306);
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		rowId = db.insert(ConnectionsBookMetaData.TABLE_NAME, 
			ConnectionsBookTableMetaData.UNIQUE_NAME, values);
		if (rowId > 0) {

			Uri insertedBookUri = ContentUris.withAppendedId(
				ConnectionsBookTableMetaData.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertedBookUri, null);
			return insertedBookUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	public int update(Uri uri, ContentValues values, String where, 
		String[] whereArgs) 
	{
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri)) {
			case INCOMING_COLLECTION_URI_INDICATOR:
				count = db.update(ConnectionsBookMetaData.TABLE_NAME, 
					values, where, whereArgs);
				break;
			case INCOMING_SINGLE_URI_INDICATOR:
				String rowId = uri.getPathSegments().get(1);
				count = db.update(ConnectionsBookMetaData.TABLE_NAME, 
					values, ConnectionsBookTableMetaData._ID + "=" + rowId + 
					((!TextUtils.isEmpty(where)) ? " AND (" + where + ")" : ""),
					whereArgs);
				break;
			default: 
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri,null);
		return count;
	}
	
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		
		switch(sUriMatcher.match(uri)) {
			case INCOMING_COLLECTION_URI_INDICATOR:
				count = db.delete(ConnectionsBookMetaData.TABLE_NAME, where, whereArgs);
				break;
			case INCOMING_SINGLE_URI_INDICATOR:
				String rowId = uri.getPathSegments().get(1);
				count = db.delete(ConnectionsBookMetaData.TABLE_NAME, 
					ConnectionsBookTableMetaData._ID + "=" + rowId + 
					(!TextUtils.isEmpty(where) ? " AND ( " + where + ") " : ""),
					whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
};
