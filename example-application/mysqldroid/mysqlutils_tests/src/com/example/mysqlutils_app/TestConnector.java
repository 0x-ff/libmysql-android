package com.example.mysqlutils_app;


import junit.framework.Assert;
import android.database.mysql.SQLException;
import android.database.mysql.ConfigException;
import android.database.mysql.ConnectFailedException;
import android.database.mysql.QueryException;
import android.database.mysql.Connector;
import android.database.mysql.Query;
import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;


public class TestConnector extends Connector {
    
    public static String currentIndex;
    
    protected static HashMap<String,String> testSqls;
    
    protected static HashMap<String,String> db_config;
    
    static {
        testSqls = new HashMap<String,String>();
        testSqls.put("testTableTruncate", "TRUNCATE TABLE `t1`");
        testSqls.put("testTableDrop", "DROP TABLE `t1`");

        db_config = new HashMap<String,String>();
        db_config.put("hostAddr", "hostAddr");
        db_config.put("userName", "userName");
        db_config.put("passWord", "passWord");
        db_config.put("dbName", "dbName");
        db_config.put("portNum", "portNum");
    };
    
    public TestConnector(HashMap<String,String> db_config) 
        throws ConfigException 
    {
        super(db_config);
        currentIndex = "";
    }
    
    public TestConnector() 
        throws ConfigException 
    {
        super(db_config);
        currentIndex = "";
    }
    
    public void connect() 
        throws ConnectFailedException 
    {}

    public void disconnect() 
    {}

    public void query(String sql) 
        throws QueryException 
    {
       Assert.assertTrue(sql.equals(testSqls.get(currentIndex)));
    }

    public boolean hasMoreRows() 
        throws QueryException 
    {
		return false;
    }
    
    public ArrayList<String> fields() 
        throws QueryException 
    {
        ArrayList<String> fields = new ArrayList<String>();
        return fields;
    }
    
    public ArrayList<HashMap<String,String>> 
		fetchAllHash() throws SQLException 
	{
		ArrayList<HashMap<String,String>> all = 
			new ArrayList<HashMap<String,String>>();
		return all;
	}
    
    public ArrayList<ArrayList<String>> 
		fetchAllArray() throws SQLException 
	{
		ArrayList<ArrayList<String>> all = 
			new ArrayList<ArrayList<String>>();
		return all;
	}
    
    public HashMap<String,String> fetchRowHash() 
        throws QueryException 
    {
        HashMap<String, String> row = new HashMap<String, String>();
        ArrayList<String> fields = fields();
        ArrayList<String> row_array = fetchRowArray();
        
        for (int i = 0; i < fields.size(); i++ ) 
			{ row.put(fields.get(i), row_array.get(i)); }

        return row;
    }
    
    public ArrayList<String> fetchRowArray() 
        throws QueryException 
    {
        ArrayList<String> row = new ArrayList<String>();
        return row;
    }
    
    public ArrayList<String> fetchCol() 
        throws QueryException 
    {
        ArrayList<String> col = new ArrayList<String>();
        return col;
    }
    
    public String fetchOne()
		{ return ""; }
    
    public int lastInsertId() 
		throws SQLException
    {
		return 0;
	}
    
    public String escape(String str) 
		throws SQLException
	{
		return str.replace("[^\\]'", "\\'").replace("^'", "\\'");
	}

    public String getLastError() 
		{ return "This is TestConnector, it throws no Exceptions"; }
    
    
    
};
