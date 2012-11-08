package com.example.mysqlshell.tests;

import android.widget.EditText;
import android.view.View;
import android.view.KeyEvent;
import com.example.mysqlshell.*;
import com.example.mysqlshell.tests.OkConnection;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;

public class BasicActions extends 
    ActivityInstrumentationTestCase2<ShellActivity>
{
    
    protected Solo solo;
    
    public BasicActions() 
        { super("com.example.mysqlshell", ShellActivity.class); }
    
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    protected void tearDown() throws Exception
        { solo.finishOpenedActivities(); }

    protected String getRStr(int id) 
        { return getActivity().getText(id).toString(); }
    
    protected void fillTextsOkConnection() {
        
        EditText ed = (EditText) solo.getView(R.id.conn_id);
        solo.enterText(ed, OkConnection.name);

        ed = (EditText) solo.getView(R.id.host_addr);
        solo.enterText(ed, OkConnection.host);

        ed = (EditText) solo.getView(R.id.username);
        solo.enterText(ed, OkConnection.user);

        ed = (EditText) solo.getView(R.id.password);
        solo.enterText(ed, OkConnection.pass);

        ed = (EditText) solo.getView(R.id.dbname);
        solo.enterText(ed, OkConnection.db);

        ed = (EditText) solo.getView(R.id.port);
        solo.enterText(ed, OkConnection.port);
    }
    
    
}
