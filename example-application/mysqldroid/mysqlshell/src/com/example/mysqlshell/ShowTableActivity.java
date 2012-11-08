package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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

public class ShowTableActivity extends Activity 
	implements View.OnCreateContextMenuListener 
{
    AlertDialog alert;

	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_table);
        
        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();

        HashMap<String, String> db_config = new HashMap<String,String>();
		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();
		
		String table = getIntent().getStringExtra("table");
        try {
			ArrayList<String> f;
            ArrayList<String> r;
            String txt; int i;
            TextView tv;
            Connector c = new Connector(db_config);
            c.encoding = "utf8";
            c.connect();
            c.query("show table status like '" + table + "'");

            if ( c.hasMoreRows() ) {

				f = c.fields();
				r = c.fetchRowArray();

				txt = "TABLE STATUS\n";
				for (i = 0; i < f.size(); i++) {
					txt += f.get(i) + ":" + r.get(i) + "\n";
				}
				tv = (TextView) this.findViewById(R.id.show_table_status);
				tv.setText(txt);
			}
			c.forceFreeResult();
			c.query("SHOW CREATE TABLE `" + table + "`");
			
			if (c.hasMoreRows()) {
				tv = (TextView) this.findViewById(R.id.show_create_table);
				r = c.fetchRowArray();
				tv.setText(r.get(1));
			}
			c.forceFreeResult();
			c.query("SHOW FULL COLUMNS FROM `" + table + "`");
			txt = "\n\nSHOW FULL COLUMNS FROM\n";
			while (c.hasMoreRows()) {
				
				r = c.fetchRowArray();
				for (i = 0; i < r.size(); i++) {
					txt += r.get(i) + " ";
				}
				txt += "\n";
			}
			tv = (TextView) this.findViewById(R.id.show_full_columns);
			tv.setText(txt);
			
			c.query("SHOW INDEXES FROM `" + table + "`");
			txt = "\n\nSHOW INDEXES FROM\n";
			while (c.hasMoreRows()) {
				
				r = c.fetchRowArray();
				for (i = 0; i < r.size(); i++) {
					txt += r.get(i) + " ";
				}
				txt += "\n";
			}
			tv = (TextView) this.findViewById(R.id.show_indexes);
			tv.setText(txt);
			
            c.disconnect();
        } catch (SQLException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            return;
        }
    }
};
