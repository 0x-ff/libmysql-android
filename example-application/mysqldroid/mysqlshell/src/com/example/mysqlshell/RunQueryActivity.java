package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.os.Handler;
import com.example.mysqlshell.writer.*;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.AlertDialog;
import android.util.Log;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

public class RunQueryActivity extends Activity 
	
{
	boolean isSelectQuery;
	String filePath;
	String error;
	AlertDialog alert;

	Runnable r;
	Thread thr;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_running);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();

        Intent data = getIntent();
        isSelectQuery = data.getBooleanExtra("isselect", true);
        error = "";
        View.OnClickListener cancel = new View.OnClickListener() {
			
			public void onClick(View v) 
				{ cancelQuery(); }
		};
		
		Button btn = (Button) this.findViewById(R.id.query_cancel);
		btn.setOnClickListener(cancel);

		TextView tv = (TextView) this.findViewById(R.id.query_name_caption);
		String txt = getIntent().getStringExtra("sql_name");
		tv.setText(txt);
		tv.invalidate();

		r = new Runnable() 
			{ public void run() { runQuery(); } };
		thr = new Thread(r);
		thr.start();
    }

	private void runQuery() 
	{
		Intent data = getIntent();
		HashMap<String, String> db_config = new HashMap<String,String>();

		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();

		String sql = getIntent().getStringExtra("sql_string");

		if (isSelectQuery) {
		
			SharedPreferences prefs = getSharedPreferences(
				"com.example.mysqlshell_preferences", MODE_PRIVATE);
			String outputFormat = prefs.getString("output_format", "csv");
			SqlResultsToFileWriter fileWriter;
			
			filePath = data.getStringExtra("path");
			if (outputFormat.equals("txt")) {
				fileWriter = new SqlResultsPlainWriter(filePath);
			} else if (outputFormat.equals("csv")) {
				fileWriter = new SqlResultsToCsvWriter(filePath);
			} else { // xls
				fileWriter = new SqlResultsToXlsWriter(filePath);
			}
			
			if (!fileWriter.startWrite() ) {
				
				error = filePath + " " + fileWriter.lastError;
				stopMe();
				return;
			}
			
			try {
				Connector c = new Connector(db_config);
				c.encoding = "utf8";
				c.connect();
				c.query(sql);
				int sz = 0;
				ArrayList<String> row;
				while( c.hasMoreRows() ) {

					if (sz == 0) {
						row = c.fields();
						sz  = row.size();
						fileWriter.writeFields(row);
					}
					fileWriter.writeRow(c.fetchRowArray());
				}
				c.disconnect();
			} catch (SQLException e) {
				error = e.getMessage();
				fileWriter.endWrite();
				stopMe();
			}
			fileWriter.endWrite();
		} else {
			
			try {
				Connector c = new Connector(db_config);
				c.encoding  = "utf8";
				c.connect();
				c.query(sql);
				c.disconnect();
			} catch (SQLException e) {
				error = e.getMessage();
				stopMe();
			}
		}
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		intent.putExtra("error", error);
		intent.putExtra("path", filePath);
		finish();
	}

    private void cancelQuery() 
    {
		thr.stop();
		stopMe();
	}
	
	private void stopMe() 
	{
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		intent.putExtra("error", error);
		intent.putExtra("path", filePath);
		finish();
	}
};
