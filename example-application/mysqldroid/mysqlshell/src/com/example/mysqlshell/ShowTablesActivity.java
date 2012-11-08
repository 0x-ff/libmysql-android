package com.example.mysqlshell;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.app.AlertDialog;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;

import java.util.HashMap;
import java.util.ArrayList;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

 

public class ShowTablesActivity extends Activity 
{
    AlertDialog alert;
	ArrayAdapter<String> adapter;


	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_tables);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();


        adapter = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_1);

        HashMap<String, String> db_config = new HashMap<String,String>();
		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();

        try {
            Connector c = new Connector(db_config);
            c.encoding = "utf8";
            c.connect();
            c.query("SHOW TABLES;");
            while( c.hasMoreRows() ) 
				{ adapter.add(c.fetchOne()); }
            c.disconnect();
        } catch (SQLException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            return;
        }

        ListView list = (ListView) this.findViewById(R.id.show_tables);
        list.setAdapter(adapter);
        
        OnItemClickListener chooseAction = 
        new OnItemClickListener() 
        {
			 public void onItemClick (AdapterView<?> parent, 
				View view, int position, long id) 
				{ clickItem(parent, view, position, id); }
		};
        list.setOnItemClickListener(chooseAction);
        registerForContextMenu(list);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
  
		if (v.getId()==R.id.show_tables) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.table, menu);
			menu.setHeaderTitle(adapter.getItem(info.position));
		}
	}
    
    public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String table = adapter.getItem(info.position);
		String sql = "";
		switch (menuItemIndex) {
			case R.id.btn_show_status:
				sql = "SHOW TABLE STATUS LIKE '" + table + "'";
				break;
			case R.id.btn_show_create:
				sql = "SHOW CREATE TABLE `" + table + "`";
				break;
			case R.id.btn_show_full_columns:
				sql = "SHOW FULL COLUMNS FROM `" + table + "`";
				break;
			case R.id.btn_show_indexes:
				sql = "SHOW INDEXES FROM `" + table + "`";
				break;
		}
		Intent resultView = new Intent();
		resultView.setClass(this, SqlResultSheetActivity.class);
		resultView.putExtra("sql_name", "from ShowTablesActivity");
		resultView.putExtra("sql_string", sql);
		startActivity(resultView);
		return true;
	}
    
    private void clickItem(AdapterView<?> parent, 
				View view, int position, long id) 
    {
		String item = (String)parent.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("table", item);
		intent.setClass(this, ShowTableActivity.class);
		startActivity(intent);
	}
};
