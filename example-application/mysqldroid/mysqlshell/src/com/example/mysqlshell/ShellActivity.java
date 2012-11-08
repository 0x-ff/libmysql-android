package com.example.mysqlshell;

import com.example.mysqlshell.provider.ConnectionsBook;
import com.example.mysqlshell.provider.ConnectionsBookMetaData;
import com.example.mysqlshell.provider.ConnectionsBookMetaData.ConnectionsBookTableMetaData;
import com.example.mysqlshell.provider.ConnNameEmptyException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.net.Uri;
import android.content.ContentValues;
import android.app.AlertDialog;
import android.database.SQLException;

/* Велосипед одного мексиканца для отображения диалога выбора файла */
import android.widget.filedialog.FileDialog;

public class ShellActivity extends Activity
{
	ConnectionListItem connection;
	
	AlertDialog alert;
	
	static int fontSize = 12;
	
	public ShellActivity () {
		super();
		connection = new ConnectionListItem();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tv = (TextView)this.findViewById(R.id.local_name_cap);
        tv.setTextSize(fontSize);
        tv = (TextView)this.findViewById(R.id.host_addr_cap);
        tv.setTextSize(fontSize);
        tv = (TextView)this.findViewById(R.id.username_cap);
        tv.setTextSize(fontSize);
        tv = (TextView)this.findViewById(R.id.password_cap);
        tv.setTextSize(fontSize);
        tv = (TextView)this.findViewById(R.id.dbname_cap);
        tv.setTextSize(fontSize);
        tv = (TextView)this.findViewById(R.id.port_cap);
        tv.setTextSize(fontSize);

        setOptions();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) 
    {
		Intent intent;
		TextView tv;
		switch (item.getItemId()) 
		{
			case R.id.btn_info:
				intent = new Intent().
					setClass(this,AboutActivity.class);
				this.startActivity(intent);
				return true;
			case R.id.btn_pref:
				intent = new Intent().
					setClass(this,ShellPreferenceActivity.class);
				this.startActivityForResult(intent, 0);
				return true;
			case R.id.btn_exit:
				intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return true;

			case R.id.btn_connect:
				intent = new Intent().
					setClass(this,TestConnectionActivity.class);
				ShellApplication app = ((ShellApplication)getApplicationContext());
				app.current = readCurrentConnection();
				EditText txt = (EditText) this.findViewById(R.id.conn_id);
				intent.putExtra("uniqueName", txt.getText().toString());
				startActivityForResult(intent, 4);
				return true;
			case R.id.btn_list_conns:
				intent = new Intent(Intent.ACTION_INSERT);
				intent.setData(ConnectionsBookTableMetaData.CONTENT_URI);
				intent.setClass(this, ListConnectionsActivity.class);
				startActivityForResult(intent, 1);
				return true;
			case R.id.btn_save_conn:
				ContentValues cv = new ContentValues();
				tv = (TextView)this.findViewById(R.id.conn_id);
				
				cv.put(ConnectionsBookTableMetaData.UNIQUE_NAME, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.host_addr);
				cv.put(ConnectionsBookTableMetaData.HOST_ADDR, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.username);
				cv.put(ConnectionsBookTableMetaData.USER_NAME, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.password);
				cv.put(ConnectionsBookTableMetaData.PASS_WORD, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.dbname);
				cv.put(ConnectionsBookTableMetaData.DB_NAME, tv.getText().toString());
				tv = (TextView)this.findViewById(R.id.port);
				cv.put(ConnectionsBookTableMetaData.PORT_NUM, tv.getText().toString());
				try {
					getContentResolver().insert(ConnectionsBookTableMetaData.CONTENT_URI, cv);
				} catch (ConnNameEmptyException e) {
					alert.setMessage(getText(R.string.conn_name_empty));
					alert.show();
					return false;
				} catch (SQLException e) {
					alert.setMessage("Internal Error " + e.getMessage());
					alert.show();
					return false;
				}
				return true;
			case R.id.btn_delete_conn:
				tv = (TextView)this.findViewById(R.id.conn_id);
				String uniqueName = tv.getText().toString();
				if (uniqueName.length() <= 0) {
					alert.setMessage(getText(R.string.conn_name_empty));
					alert.show();
					return false;
				}
				try {
					getContentResolver().delete(ConnectionsBookTableMetaData.CONTENT_URI, 
						"uniqueName = ?", new String [] { uniqueName } );
				} catch (SQLException e) {
					alert.setMessage("Internal Error " + e.getMessage());
					alert.show();
					return false;
				}
				return true;
			case R.id.btn_import:
				intent = new Intent(this, FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/mnt/sdcard");
				startActivityForResult(intent, 2);
				return true;
			case R.id.btn_export:
				intent = new Intent(this, FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/mnt/sdcard");
				startActivityForResult(intent, 3);
				return true;
		}
        return false;
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) 
    {
		super.onActivityResult(reqCode, resCode, data);
		if (resCode != RESULT_OK) {
			
			if (reqCode == 4) {
				alert.setMessage(getString(R.string.conn_failed) + " " + 
					data.getStringExtra("lastError"));
				alert.show();
			}
			
			return;
		}
		String filePath;
		
		ConfigManager cfg = new ConfigManager(this);
		if (reqCode == 0) {
			setOptions();
		} else if (reqCode == 1) {
			connection.uniqueName  = data.getStringExtra("uniqueName");
			connection.hostAddr    = data.getStringExtra("hostAddr");
			connection.userName    = data.getStringExtra("userName");
			connection.password    = data.getStringExtra("password");
			connection.dbName      = data.getStringExtra("dbName");
			connection.port        = data.getStringExtra("port");
			refresh();
		} else if (reqCode == 2) { // import configuration from file
			
			filePath = data.getStringExtra(FileDialog.RESULT_PATH);
			if (!cfg.export_all(filePath + ".bak")) {
				alert.setMessage("backup to " + filePath + ".bak failed "  + cfg.lastError);
				alert.show();
			}
			if (!cfg.import_all(filePath)) {
				alert.setMessage(cfg.lastError);
				alert.show();
			}
			
		} else if (reqCode == 3) { // export configuration to file
			
			filePath = data.getStringExtra(FileDialog.RESULT_PATH);
			if (!cfg.export_all(filePath)) {
				alert.setMessage(cfg.lastError);
				alert.show();
			}
		} else if (reqCode == 4) {
			Intent intent = new Intent().setClass(this,ConnectedActivity.class);
			startActivity(intent);
		}
	}
	
	public ConnectionListItem readCurrentConnection() 
	{
		ConnectionListItem item = new ConnectionListItem();
		EditText txt;
		
		txt = (EditText) this.findViewById(R.id.conn_id);
		item.uniqueName = txt.getText().toString();
		
		txt = (EditText) this.findViewById(R.id.host_addr);
		item.hostAddr = txt.getText().toString();
		
		txt = (EditText) this.findViewById(R.id.username);
		item.userName = txt.getText().toString();
		
		txt = (EditText) this.findViewById(R.id.password);
		item.password = txt.getText().toString();
		
		txt = (EditText) this.findViewById(R.id.dbname);
		item.dbName = txt.getText().toString();
		
		txt = (EditText) this.findViewById(R.id.port);
		item.port = txt.getText().toString();
		return item;
	}
	
	public void refresh() 
    {
        EditText txt = (EditText) this.findViewById(R.id.conn_id);
        txt.setText(connection.uniqueName);
        txt.invalidate();
        
        txt = (EditText) this.findViewById(R.id.host_addr);
        txt.setText(connection.hostAddr);
        txt.invalidate();
        
        txt = (EditText) this.findViewById(R.id.username);
        txt.setText(connection.userName);
        txt.invalidate();
        
        txt = (EditText) this.findViewById(R.id.password);
        txt.setText(connection.password);
        txt.invalidate();
        
        txt = (EditText) this.findViewById(R.id.dbname);
        txt.setText(connection.dbName);
        txt.invalidate();
        
        txt = (EditText) this.findViewById(R.id.port);
        txt.setText(connection.port);
        txt.invalidate();
    }
	
	public void setOptions() 
	{
		SharedPreferences prefs = getSharedPreferences(
			"com.example.mysqlshell_preferences", MODE_PRIVATE);
		
		boolean veryCompact = prefs.getBoolean("very_compact_output", false),
				persConns   = prefs.getBoolean("use_persistance_connections", false),
				verboseLog  = prefs.getBoolean("verbose_logging", false);
		String debug_text = "Very compact output is " + (veryCompact ? "On" : "Off") + "\n" + 
			"Persistance connections is " + (persConns ? "On" : "Off") + 
			"\nVerbose log is " + (verboseLog ? "On" : "Off") + "\n";
		
		String maxLogFileSize = prefs.getString("max_log_file_size", "1"),
			maxRowsPerXlsSheet = prefs.getString("max_xls_row_size", "40000"),
			taskManagerTimeout = prefs.getString("taskman_timeout", "1");
		
		debug_text += "Max log file size is " + maxLogFileSize + " Mb\n" + 
			"Max rows per XLS sheet is " + maxRowsPerXlsSheet + "\n" + 
			"Task manager timeout is " + taskManagerTimeout + " seconds\n";
		
		String outputFormat = prefs.getString("output_format", "csv");
		debug_text += "Output format is " + outputFormat + "\n";
	}
}
