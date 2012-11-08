package com.example.mysqlshell;

import com.example.mysqlshell.provider.ConnectionsBook;
import com.example.mysqlshell.provider.ConnectionsBookMetaData;
import com.example.mysqlshell.provider.ConnectionsBookMetaData.ConnectionsBookTableMetaData;

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

public class ListConnectionsActivity extends ListActivity 
{
	AlertDialog alert;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_connections);
        
        Cursor cursor = getContentResolver().query(
			ConnectionsBookTableMetaData.CONTENT_URI, 
			new String[] { ConnectionsBookTableMetaData._ID, 
						   ConnectionsBookTableMetaData.UNIQUE_NAME }, 
			null, null, null);
        startManagingCursor(cursor); 
        String[] columns = new String[] { ConnectionsBookTableMetaData.UNIQUE_NAME };
        int[] to = new int[] { R.id.name_entry };
	    SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, 
			R.layout.list_connections_entry, cursor, columns, to);
		this.setListAdapter(mAdapter);
        
        ListView connList = (ListView) this.findViewById(android.R.id.list);

        OnItemClickListener chooseAction = 
        new OnItemClickListener() 
        {
			 public void onItemClick (AdapterView<?> parent, 
				View view, int position, long id) 
				{ clickItem(parent, view, position, id); }
		};
        connList.setOnItemClickListener(chooseAction);
        
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
			ConnectionsBookTableMetaData.CONTENT_URI, id), 
			new String[] {
				ConnectionsBookTableMetaData.UNIQUE_NAME,
				ConnectionsBookTableMetaData.HOST_ADDR,
				ConnectionsBookTableMetaData.USER_NAME,
				ConnectionsBookTableMetaData.PASS_WORD,
				ConnectionsBookTableMetaData.DB_NAME,
				ConnectionsBookTableMetaData.PORT_NUM
			}, null, null, null);
		cursor.moveToFirst();
		intent.putExtra("uniqueName", cursor.getString(0));
		intent.putExtra("hostAddr", cursor.getString(1));
		intent.putExtra("userName", cursor.getString(2));
		intent.putExtra("password", cursor.getString(3));
		intent.putExtra("dbName", cursor.getString(4));
		intent.putExtra("port", cursor.getString(5));
		cursor.close();
        setResult(RESULT_OK, intent);
		finish();
	}
    
};
