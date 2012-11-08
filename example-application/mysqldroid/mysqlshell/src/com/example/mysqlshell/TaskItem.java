package com.example.mysqlshell;


public class TaskItem {
	
	
	public String id;
	
	public String user;
	
	public String host;
	
	public String db;
	
	public String command;
	
	public String time;
	
	public String state;
	
	public String info;
	
	
	public TaskItem() {
		
		id = "";
		user = "";
		host = "";
		db = "";
		command = "";
		time = "";
		state = "";
		info = "";
	}
	
	public String toString() 
		{ return id + " " + (info.length() > 20 ? info.substring(0,20) : info); }
};
