package com.example.mysqlshell.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class QueryBookMetaData {
	
	public static final String AUTHOITY = 
		"com.example.mysqlshell.provider.QueryBook";
	
	public static final String DATABASE_NAME =
		"mysqlshell_query.db";
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "queries";
	
	private QueryBookMetaData () {}
	
	public static final class QueryBookTableMetaData 
		implements BaseColumns 
	{
		
		private QueryBookTableMetaData() {}

		public static final Uri CONTENT_URI = 
			Uri.parse("content://" + AUTHOITY + "/queries");

		public static final String CONTENT_TYPE = 
			"vnd.android.cursor.dir/vnd.example.queries";

		public static final String CONTENT_ITEM_TYPE = 
			"vnd.android.cursor.item/vnd.example.queries";

		public static final String DEFAULT_SORT_ORDER = "modifiedAt desc";

		public static final String UNIQUE_NAME = "uniqueName";
		public static final String SQL_STRING  = "sqlString";
		public static final String MODIFIED_AT = "modifiedAt";
	}
};
