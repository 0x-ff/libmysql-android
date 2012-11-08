package com.example.mysqlshell;

import com.example.mysqlshell.provider.QueryBook;
import com.example.mysqlshell.provider.QueryBookMetaData;
import com.example.mysqlshell.provider.QueryBookMetaData.QueryBookTableMetaData;
import com.example.mysqlshell.provider.QueryNameEmptyException;
import com.example.mysqlshell.writer.*;
import android.widget.TextView;
import android.text.util.Linkify;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.net.Uri;
import android.content.ContentValues;
import android.database.SQLException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.Menu;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.content.Context;

/* Велосипед одного мексиканца для отображения диалога выбора файла */
import android.widget.filedialog.FileDialog;

/* Мой велосипед */
import android.database.mysql.Query;

import java.util.ArrayList;

public class ConnectedActivity extends Activity 
{
	
	public QueryListItem query;
	
	AlertDialog alert;
	
	public ConnectedActivity() {
		super();
		query = new QueryListItem();
		query.name = "";
		query.sql  = "";
	}
	
	public synchronized void onActivityResult(int reqCode, int resCode, Intent data) 
    {
		super.onActivityResult(reqCode, resCode, data);
		if (resCode != RESULT_OK) 
		{
			if (reqCode == 6) {
				try {
					String err = data.getStringExtra("error");
					alert.setMessage(getString(R.string.query_interrupted) + " " + 
							err);
					alert.show();
				} catch (NullPointerException e) {
					
				}
			}
			if (reqCode == 2) {
				TaskManagerActivity.handler.removeCallbacks(TaskManagerActivity.r);
			}
			return;
		}
		if (reqCode == 1) 
		{
			query.name = data.getStringExtra("name");
			query.sql  = data.getStringExtra("sql");
			refresh();
		}
		if (reqCode == 2)
		{
			// task manager
		}
		if (reqCode == 3)
		{
			setOptions();
		}
		if (reqCode == 4)
		{
			// save file
			Intent intent = new Intent();
			intent.setClass(this, RunQueryActivity.class);
			intent.putExtra("path", data.getStringExtra(FileDialog.RESULT_PATH));
			EditText txt = (EditText) this.findViewById(R.id.sql_name);
			intent.putExtra("sql_name", txt.getText().toString());
			txt = (EditText) this.findViewById(R.id.sql_string);
			intent.putExtra("sql_string", txt.getText().toString());
			startActivityForResult(intent, 6);
		}
		if (reqCode == 5)
		{
			ShellApplication app = ((ShellApplication)getApplicationContext());
			String dbname = data.getStringExtra("dbname");
			if (dbname.length() > 0) {
				app.current.dbName = dbname;
				String host = app.current.hostAddr;
				setTitle(getText(R.string.connected_title) + " (" + host + "," + dbname + ")");
			}
		}
		if (reqCode == 6) {
			alert.setMessage(getText(R.string.query_ok) + " " + 
				( data.getBooleanExtra("isselect", true) 
					? data.getStringExtra("path") : "" ));
			alert.show();
		}
	}
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connected);
        refresh();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();
		ShellApplication app = ((ShellApplication)getApplicationContext());
		String host = app.current.hostAddr;
		String dbname = app.current.dbName;
		setTitle(getText(R.string.connected_title) + " (" + host + "," + dbname + ")");
		
    }
    
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query, menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) 
    {
		Intent intent;
		TextView tv;
		EditText txt;
		switch (item.getItemId()) 
		{
			case R.id.btn_choose_db:
				intent = new Intent().
					setClass(this,ChooseDatabaseActivity.class);
				startActivityForResult(intent, 5);
				return true;
			case R.id.btn_pref:
				intent = new Intent().
					setClass(this,ShellPreferenceActivity.class);
				startActivityForResult(intent, 3);
				return true;
			case R.id.btn_run_query:

				txt = (EditText) this.findViewById(R.id.sql_string);
				String sql = txt.getText().toString();
				if (sql.length() <= 0) {
					alert.setMessage(getText(R.string.empty_query));
					alert.show();
					return false;
				}
				if (Query.isSelectQuery(sql)) {

					SharedPreferences prefs = getSharedPreferences(
						"com.example.mysqlshell_preferences", MODE_PRIVATE);
					String outputFormat = prefs.getString("output_format", "csv");
					if (outputFormat.equals("grid")) {
						Intent resultView = new Intent();
						resultView.setClass(this, SqlResultSheetActivity.class);
						txt = (EditText) this.findViewById(R.id.sql_name);
						resultView.putExtra("sql_name", txt.getText().toString());
						resultView.putExtra("sql_string", sql);
						startActivity(resultView);
						return true;
					} else {
						intent = new Intent(this, FileDialog.class);
						intent.putExtra(FileDialog.START_PATH, "/mnt/sdcard");
						startActivityForResult(intent, 4);
						return true;
					}
				} else {

					intent = new Intent();
					intent.setClass(this, RunQueryActivity.class);
					intent.putExtra("isselect", false);
					txt = (EditText) this.findViewById(R.id.sql_name);
					intent.putExtra("sql_name", txt.getText().toString());
					intent.putExtra("sql_string", sql);
					startActivityForResult(intent, 6);
				}
				
				
			case R.id.btn_save_query:
				ContentValues cv = new ContentValues();
				tv = (TextView)this.findViewById(R.id.sql_name);
				cv.put(QueryBookTableMetaData.UNIQUE_NAME, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.sql_string);
				cv.put(QueryBookTableMetaData.SQL_STRING, tv.getText().toString());

				try {
					getContentResolver().insert(QueryBookTableMetaData.CONTENT_URI, cv);
				} catch (QueryNameEmptyException e) {
					alert.setMessage(getText(R.string.query_name_empty));
					alert.show();
					return false;
				} catch (SQLException e) {
					alert.setMessage("Internal Error " + e.getMessage());
					alert.show();
					return false;
				}
				return true;

			case R.id.btn_drop_query:
				tv = (TextView)this.findViewById(R.id.sql_name);
				String uniqueName = tv.getText().toString();
				if (uniqueName.length() <= 0) {
					alert.setMessage(getText(R.string.query_name_empty));
					alert.show();
					return false;
				}
				try {
					getContentResolver().delete(QueryBookTableMetaData.CONTENT_URI, 
						"uniqueName = ?", new String [] { uniqueName } );
				} catch (SQLException e) {
					alert.setMessage("Internal Error " + e.getMessage());
					alert.show();
					return false;
				}
				return true;
			case R.id.btn_showtabs:
				intent = new Intent(Intent.ACTION_INSERT);
				intent.setClass(this, ShowTablesActivity.class);
				startActivity(intent);
				return true;
			case R.id.btn_list_query:
				intent = new Intent(Intent.ACTION_INSERT);
				intent.setData(QueryBookTableMetaData.CONTENT_URI);
				intent.setClass(this, ListQueriesActivity.class);
				startActivityForResult(intent, 1);
				return true;
			case R.id.btn_taskman:
				intent = new Intent()
					.setClass(this,TaskManagerActivity.class);
				startActivityForResult(intent,2);
				return true;
		}
        return false;
    }

    public void refresh() 
    {
        EditText txt = (EditText) this.findViewById(R.id.sql_name);
        txt.setText(query.name);
        txt.invalidate();
        txt = (EditText) this.findViewById(R.id.sql_string);
        txt.setText(query.sql);
        txt.invalidate();
    }
    
    
	public void setOptions() 
	{
		
	}
};
