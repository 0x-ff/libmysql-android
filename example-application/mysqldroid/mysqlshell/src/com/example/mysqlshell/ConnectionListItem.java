package com.example.mysqlshell;

import java.util.HashMap;

public class ConnectionListItem {
	
	public String uniqueName;
	
	public String hostAddr;
	
	public String userName;
	
	public String password;
	
	public String dbName;
	 
	public String port;
	
	public ConnectionListItem() {
		
		uniqueName = "";
		hostAddr   = "";
		userName   = "";
		password   = "";
		dbName     = "";
		port       = "";
	}
	
	public String toString() 
		{ return uniqueName; }
	
	public HashMap<String,String>
		toHashMap() 
	{
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("hostAddr", hostAddr);
		map.put("userName", userName);
		map.put("passWord", password);
		map.put("dbName", dbName);
		map.put("portNum", port);
		map.put("__descr", uniqueName);
		return map;
	}
	
};
