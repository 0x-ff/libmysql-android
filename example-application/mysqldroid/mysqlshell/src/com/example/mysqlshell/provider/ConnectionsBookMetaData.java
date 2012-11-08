package com.example.mysqlshell.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ConnectionsBookMetaData {
	
	public static final String AUTHOITY = 
		"com.example.mysqlshell.provider.ConnectionsBook";
	
	public static final String DATABASE_NAME =
		"mysqlshell_connections.db";
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "connections";
	
	private ConnectionsBookMetaData () {}
	
	public static final class ConnectionsBookTableMetaData 
		implements BaseColumns 
	{
		
		private ConnectionsBookTableMetaData() {}
		
		public static final Uri CONTENT_URI = 
			Uri.parse("content://" + AUTHOITY + "/connections");
			
		public static final String CONTENT_TYPE = 
			"vnd.android.cursor.dir/vnd.example.connections";
		
		public static final String CONTENT_ITEM_TYPE = 
			"vnd.android.cursor.item/vnd.example.connections";
		
		public static final String DEFAULT_SORT_ORDER = "modifiedAt desc";
		
		public static final String UNIQUE_NAME = "uniqueName";
		public static final String HOST_ADDR   = "hostAddr";
		public static final String USER_NAME   = "userName";
		public static final String PASS_WORD   = "passWord";
		public static final String DB_NAME     = "dbName";
		public static final String PORT_NUM    = "portNum";
		public static final String MODIFIED_AT = "modifiedAt";
	}
};
