package com.example.mysqlshell;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.util.Linkify;
import android.text.Html;

public class AboutActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

		TextView tv = (TextView)this.findViewById(R.id.info_about);
		tv.setText(Html.fromHtml(getText(R.string.cap_about).toString()));
        Linkify.addLinks(tv, Linkify.ALL);
	}
};
