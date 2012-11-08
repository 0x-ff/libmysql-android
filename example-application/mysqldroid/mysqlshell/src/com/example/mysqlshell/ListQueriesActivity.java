package com.example.mysqlshell;

import com.example.mysqlshell.provider.QueryBook;
import com.example.mysqlshell.provider.QueryBookMetaData;
import com.example.mysqlshell.provider.QueryBookMetaData.QueryBookTableMetaData;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.app.ListActivity;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.content.ContentUris;
import android.app.AlertDialog;

public class ListQueriesActivity extends ListActivity 
{
	AlertDialog alert;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_queries);
        
        Cursor cursor = getContentResolver().query(
			QueryBookTableMetaData.CONTENT_URI, 
			new String[] { QueryBookTableMetaData._ID, 
						   QueryBookTableMetaData.UNIQUE_NAME }, 
			null, null, null);
        startManagingCursor(cursor); 
        String[] columns = new String[] { QueryBookTableMetaData.UNIQUE_NAME };
        int[] to = new int[] { R.id.sql_name_entry };
	    SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, 
			R.layout.list_queries_entry, cursor, columns, to);
		this.setListAdapter(mAdapter);
        
        ListView queryList = (ListView) this.findViewById(android.R.id.list);

        OnItemClickListener chooseAction = 
        new OnItemClickListener() 
        {
			 public void onItemClick (AdapterView<?> parent, 
				View view, int position, long id) 
				{ clickItem(parent, view, position, id); }
		};
        queryList.setOnItemClickListener(chooseAction);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ok")
		   .setCancelable(true);
		alert = builder.create();
    }

    private void clickItem(AdapterView<?> parent, 
				View view, int position, long id) 
    {
		Intent intent = new Intent();
		Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(
			QueryBookTableMetaData.CONTENT_URI, id), 
			new String[] {
				QueryBookTableMetaData.UNIQUE_NAME,
				QueryBookTableMetaData.SQL_STRING
			}, null, null, null);
		cursor.moveToFirst();
		intent.putExtra("name", cursor.getString(0));
		intent.putExtra("sql", cursor.getString(1));
		cursor.close();
        setResult(RESULT_OK, intent);
		finish();
	}
};
