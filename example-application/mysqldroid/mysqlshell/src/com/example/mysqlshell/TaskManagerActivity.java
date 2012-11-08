package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.os.Handler;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

public class TaskManagerActivity extends Activity 
{
	ArrayAdapter<TaskItem> adapter;
	AlertDialog alert;
	
	public static Runnable r = null;
	public static Handler handler = null;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskman);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();
        
        adapter = new ArrayAdapter<TaskItem>(
			this, android.R.layout.simple_list_item_1);
        
        OnItemClickListener chooseAction = 
        new OnItemClickListener() 
        {
			 public void onItemClick (AdapterView<?> parent, 
				View view, int position, long id) 
				{ clickItem(parent, view, position, id); }
		};
		ListView list = (ListView) this.findViewById(R.id.taskman_list);
        list.setOnItemClickListener(chooseAction);
		list.setAdapter(adapter);
		if (r != null) {
			handler.removeCallbacks(r);
		}
		r = null;
		r = new Runnable() 
		{ public void run() { taskLoop(); } };
		handler = new Handler();
		handler.postDelayed(r, 2000);
    }
    
    private void taskLoop() 
    {
		HashMap<String, String> db_config = 
			new HashMap<String,String>();

		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();

        try {
            Connector c = new Connector(db_config);
            c.encoding = "utf8";
            c.connect();
            c.query("SHOW FULL PROCESSLIST;");
            adapter.clear();
            while( c.hasMoreRows() ) {

				TaskItem task = new TaskItem();
				ArrayList<String> row = c.fetchRowArray();
				task.id      = row.get(0);
				task.user    = row.get(1);
				task.host    = row.get(2);
				task.db      = row.get(3);
				task.command = row.get(4);
				task.time    = row.get(5);
				task.state   = row.get(6);
				task.info    = row.get(7);
				adapter.add(task);
            }
            c.disconnect();
        } catch (SQLException e) {
            alert.setMessage(e.getMessage());
            alert.show();
        }
		handler.postDelayed(r, 2000);
	}
    
    public void onActivityResult(int reqCode, int resCode, Intent data) 
    {
		super.onActivityResult(reqCode, resCode, data);
		if (reqCode == 1 && resCode != RESULT_CANCELED) 
		{
			String id = data.getStringExtra("id");
			if (id != null) {				
				HashMap<String, String> db_config = 
					new HashMap<String,String>();

				ShellApplication app = ((ShellApplication)getApplicationContext());
				db_config = app.current.toHashMap();
				try {
					Connector c = new Connector(db_config);
					c.connect();
					c.query("KILL " + id + ";");
					c.disconnect();
				} catch (SQLException e) {
					alert.setMessage(e.getMessage());
					alert.show();
				}
			}
		}
		handler.postDelayed(r, 2000);
	}
    
    private void clickItem(AdapterView<?> parent, 
				View view, int position, long id) 
    {
		TaskItem item = (TaskItem)parent.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.setClass(this,TaskViewActivity.class);
        intent.putExtra("id", item.id);
        intent.putExtra("user", item.user);
        intent.putExtra("host", item.host);
        intent.putExtra("db", item.db);
        intent.putExtra("command", item.command);
        intent.putExtra("time", item.time);
        intent.putExtra("state", item.state);
        intent.putExtra("info", item.info);
        handler.removeCallbacks(r);
		startActivityForResult(intent, 1);
	}
};
