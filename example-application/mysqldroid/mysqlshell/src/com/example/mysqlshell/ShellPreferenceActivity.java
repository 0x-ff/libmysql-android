package com.example.mysqlshell;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ShellPreferenceActivity extends PreferenceActivity {
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
