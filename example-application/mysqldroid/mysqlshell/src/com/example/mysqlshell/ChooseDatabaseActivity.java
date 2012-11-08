package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.app.AlertDialog;

import java.util.HashMap;
import java.util.ArrayList;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

public class ChooseDatabaseActivity extends Activity 
{
    AlertDialog alert;

	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_database);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_1);

        HashMap<String, String> db_config = new HashMap<String,String>();
		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();

        try {
            Connector c = new Connector(db_config);
            c.encoding = "utf8";
            c.connect();
            c.query("SHOW DATABASES;");
            while( c.hasMoreRows() ) {

                adapter.add(c.fetchOne());
            }
            c.disconnect();
        } catch (SQLException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            return;
        }

        ListView list = (ListView) this.findViewById(R.id.choose_database_list);
        list.setAdapter(adapter);
        
        OnItemClickListener chooseAction = 
        new OnItemClickListener() 
        {
			 public void onItemClick (AdapterView<?> parent, 
				View view, int position, long id) 
				{ clickItem(parent, view, position, id); }
		};
        list.setOnItemClickListener(chooseAction);
    }
    
    private void clickItem(AdapterView<?> parent, 
				View view, int position, long id) 
    {
		String item = (String)parent.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("dbname", item);
        setResult(RESULT_OK, intent);
		finish();
	}
};
