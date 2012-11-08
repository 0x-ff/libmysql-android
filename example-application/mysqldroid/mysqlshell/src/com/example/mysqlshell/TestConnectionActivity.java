package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.os.Handler;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

public class TestConnectionActivity extends Activity 
{
	Thread thr;
	Runnable r;
	
	String lastError;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);
        lastError = "";
        thr = null;
        
        View.OnClickListener cancel = new View.OnClickListener() {
			
			public void onClick(View v) 
				{ cancelConnection(); }
		};
		
		Button btn = (Button) this.findViewById(R.id.conn_cancel);
		btn.setOnClickListener(cancel);
		
		TextView tv = (TextView) this.findViewById(R.id.conn_name_caption);
		String txt = getIntent().getStringExtra("uniqueName");
		tv.setText(txt);
		tv.invalidate();

		Runnable r = new Runnable() {
			public void run(){ testConnection(); } };
		thr = new Thread(r);
		thr.start();
    }
    
    private void testConnection () {
		
		ShellApplication app = ((ShellApplication)getApplicationContext());
		
		try {
            Connector c = new Connector(app.current.toHashMap());
            c.connect();
            c.disconnect();
        } catch (SQLException e) {
			lastError = e.getMessage();
            stopMe();
            return;
        }
		
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
    
    private void cancelConnection() 
    {
		thr.stop();
		stopMe();
	}
	
	private void stopMe() 
	{
		Intent intent = new Intent();
		intent.putExtra("lastError", lastError);
		setResult(RESULT_CANCELED, intent);
		finish();
	}
};
