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

import com.example.mysqlshell.provider.QueryBookMetaData.QueryBookTableMetaData;
import java.util.HashMap;

public class QueryBook extends ContentProvider {
	
	private static HashMap<String, String> QueryProjMap;
	static 
	{
		QueryProjMap = new HashMap<String, String>();
		QueryProjMap.put(QueryBookTableMetaData._ID, 
			QueryBookTableMetaData._ID);
		
		QueryProjMap.put(QueryBookTableMetaData.UNIQUE_NAME,
			QueryBookTableMetaData.UNIQUE_NAME);
		QueryProjMap.put(QueryBookTableMetaData.SQL_STRING,
			QueryBookTableMetaData.SQL_STRING);
		QueryProjMap.put(QueryBookTableMetaData.MODIFIED_AT,
			QueryBookTableMetaData.MODIFIED_AT);
	}
	
	private static final UriMatcher sUriMatcher;
	
	private static final int INCOMING_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_URI_INDICATOR = 2;
	
	static 
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(QueryBookMetaData.AUTHOITY, "queries",
			INCOMING_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(QueryBookMetaData.AUTHOITY, "queries/#",
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

			super(context, QueryBookMetaData.DATABASE_NAME, null,
				QueryBookMetaData.DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL("CREATE TABLE " + 
				QueryBookMetaData.TABLE_NAME + " (" + 
				QueryBookMetaData.QueryBookTableMetaData._ID + 
				" INTEGER PRIMARY KEY, " + 
				QueryBookTableMetaData.UNIQUE_NAME + " TEXT, " + 
				QueryBookTableMetaData.SQL_STRING + " TEXT, " + 
				QueryBookTableMetaData.MODIFIED_AT + " INTEGER);");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TAG", "Upgrading database from version " + oldVersion + " to " + 
				newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + QueryBookMetaData.TABLE_NAME);
			onCreate(db);
		}
	}
	
	public String getType(Uri uri) {
		
		switch( sUriMatcher.match(uri) ) {
			
			case INCOMING_COLLECTION_URI_INDICATOR:
				return QueryBookTableMetaData.CONTENT_TYPE;
			case INCOMING_SINGLE_URI_INDICATOR:
				return QueryBookTableMetaData.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}
	
	public long getIdByUniqueName(String uniqueName) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(QueryBookMetaData.TABLE_NAME);
		qb.appendWhere(QueryBookTableMetaData.UNIQUE_NAME + 
			"='" + uniqueName + "'");
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, new String[] 
			{QueryBookTableMetaData._ID}, 
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
				qb.setTables(QueryBookMetaData.TABLE_NAME);
				qb.setProjectionMap(QueryProjMap);
			break;
			case INCOMING_SINGLE_URI_INDICATOR:
				qb.setTables(QueryBookMetaData.TABLE_NAME);
				qb.setProjectionMap(QueryProjMap);
				qb.appendWhere(QueryBookTableMetaData._ID + "=" + 
					uri.getPathSegments().get(1));
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = QueryBookTableMetaData.DEFAULT_SORT_ORDER;
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
		values.put(QueryBookTableMetaData.MODIFIED_AT, now);
		
		if (values.containsKey(QueryBookTableMetaData.UNIQUE_NAME) == false) {
			throw new SQLException("Required " + QueryBookTableMetaData.UNIQUE_NAME + 
				" " + uri);
		}
		
		String uniqueName = values.getAsString(QueryBookTableMetaData.UNIQUE_NAME);
		
		if (uniqueName.length() <= 0) {
			throw new QueryNameEmptyException();
		}
		
		long rowId = getIdByUniqueName(uniqueName);
		if (rowId > 0) {

			Uri ret = ContentUris.withAppendedId(
				QueryBookTableMetaData.CONTENT_URI, 
				rowId);
			int i = update(ret, values, null, null);
			return ret;
		}
		
		if (values.containsKey(QueryBookTableMetaData.SQL_STRING) == false) {
			throw new SQLException("Required " + QueryBookTableMetaData.SQL_STRING + 
				" " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		rowId = db.insert(QueryBookMetaData.TABLE_NAME, 
			QueryBookTableMetaData.UNIQUE_NAME, values);
		if (rowId > 0) {

			Uri insertedBookUri = ContentUris.withAppendedId(
				QueryBookTableMetaData.CONTENT_URI, rowId);
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
				count = db.update(QueryBookMetaData.TABLE_NAME, 
					values, where, whereArgs);
				break;
			case INCOMING_SINGLE_URI_INDICATOR:
				String rowId = uri.getPathSegments().get(1);
				count = db.update(QueryBookMetaData.TABLE_NAME, 
					values, QueryBookTableMetaData._ID + "=" + rowId + 
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
				count = db.delete(QueryBookMetaData.TABLE_NAME, where, whereArgs);
				break;
			case INCOMING_SINGLE_URI_INDICATOR:
				String rowId = uri.getPathSegments().get(1);
				count = db.delete(QueryBookMetaData.TABLE_NAME, 
					QueryBookTableMetaData._ID + "=" + rowId + 
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
