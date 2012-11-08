package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.app.AlertDialog;

import java.util.HashMap;
import java.util.ArrayList;

// мои велосипеды
import android.database.mysql.Query;
import android.database.mysql.Connector;
import android.database.mysql.SQLException;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class SqlResultSheetActivity extends Activity {
	
	AlertDialog alert;
	ArrayAdapter<String> adapter_all;
    
    
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sql_result);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();
        
		Intent intent = getIntent();
		HashMap<String, String> db_config = new HashMap<String,String>();
		ShellApplication app = ((ShellApplication)getApplicationContext());
		db_config = app.current.toHashMap();

		TableLayout grid = (TableLayout) findViewById(R.id.sql_result);
        grid.removeAllViews();

        try {
			int sz = 0;
            Connector c = new Connector(db_config);
            c.encoding = "utf8";
            c.connect();
            String sql = intent.getStringExtra("sql_string");
            c.query(sql);
            int i;
            int ratio = 1, all_sym = 400;
            while( c.hasMoreRows() ) {

				if (sz == 0){
					ArrayList<String> fields = c.fields();
					sz = fields.size();
                    
                    ratio = all_sym/sz;
                    if (ratio == 0) {
                        ratio = 1;
                    }
                    
                    TableRow tr = new TableRow(this);
					tr.setLayoutParams( new LayoutParams(
					  LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					tr.setPadding(4,4,4,4);
                    
					for (i = 0; i < sz; i++) {
                        
                        String val = fields.get(i);
                        String reduced = (val.length() > ratio 
                            ? val.substring(0,ratio) : val);
						TextView tv = new TextView(this);
						tv.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
						tv.append(reduced);
						tv.setPadding(4,4,4,4);
						tr.addView(tv);						
					}
					grid.addView(tr,new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				}

				TableRow tr = new TableRow(this);
				tr.setLayoutParams( new LayoutParams(
				  LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				tr.setPadding(4,4,4,4);
				ArrayList<String> row = c.fetchRowArray();
				for (i = 0; i < row.size(); i++) {

                    String val     = row.get(i);
                    String reduced = (val.length() > ratio
                          ? val.substring(0,ratio) : val);

                    TextView tv = new TextView(this);
					tv.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					tv.append(reduced);
					tv.setPadding(4,4,4,4);
					tr.addView(tv);
				}
				grid.addView(tr,new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
            c.disconnect();
            
        } catch (SQLException e) {
            alert.setMessage(e.getMessage());
            alert.show();
        }

    }
};
