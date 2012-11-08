package android.database.mysql.natives;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

// Java interface for simplest libmysql "C" wrapper

public class Connector {

    static {
        System.loadLibrary("log");
        System.loadLibrary("mysql_android_facade");
    }

	int handle; // MYSQL*
	int res;    // MYSQL_RES*
	int row;    // MYSQL_ROW

	// connection config
    String hostAddr;
    String userName;
    String passWord;
    String dbName;
    String portNum;

	public String lastError;

    public Connector( String host, String user, 
		String pass, String dbname, String port ) 
	{
		hostAddr = host;
		userName = user;
		passWord = pass;
		dbName   = dbname;
		portNum  = port;
		handle   = 0;
		res      = 0;
		row      = 0;
	}

    public native boolean connect();
    
    public native void disconnect();
    
    /* bin == true  => mysql_real_query,
	   bin == false => mysql_query */
    public native boolean query(String sql, boolean bin);
    
    public boolean query(String sql)
		{ return query(sql, false); }
    
    // -1 => mysql_error occured
    public native int hasMoreRows();
    
    public native String fetchOne();
    
    public native int lastInsertId();
    
    public native String escape(String str);
    
    public native Object[] fields();
    
    public native Object[] fetchRowArray();
    
    public native boolean forceFreeResult();
};
