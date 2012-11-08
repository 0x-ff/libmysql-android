package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

public class TaskViewActivity extends Activity 
{
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskview);
        
        Intent data = getIntent();
        
        TextView tv = (TextView)this.findViewById(R.id.task_id);
        tv.setText(data.getStringExtra("id"));
        tv = (TextView)this.findViewById(R.id.task_user);
        tv.setText(data.getStringExtra("user"));
        tv = (TextView)this.findViewById(R.id.task_host);
        tv.setText(data.getStringExtra("host"));
        tv = (TextView)this.findViewById(R.id.task_db);
        tv.setText(data.getStringExtra("db"));
        tv = (TextView)this.findViewById(R.id.task_command);
        tv.setText(data.getStringExtra("command"));
        tv = (TextView)this.findViewById(R.id.task_time);
        tv.setText(data.getStringExtra("time"));
        tv = (TextView)this.findViewById(R.id.task_state);
        tv.setText(data.getStringExtra("state"));
        tv = (TextView)this.findViewById(R.id.task_info);
        tv.setText(data.getStringExtra("info"));
        
        View.OnClickListener killAction = 
        new View.OnClickListener () {
			public void onClick(View v) 
				{ killQuery(v); }
		};
        Button btn = (Button) this.findViewById(R.id.btn_kill_task);
        btn.setOnClickListener(killAction);
    }
    
    public void killQuery(View v) 
    {
		Intent data = getIntent();
		Intent intent = new Intent();
        intent.putExtra("id", data.getStringExtra("id"));
        setResult(RESULT_OK, intent);
		finish();
	}
};
