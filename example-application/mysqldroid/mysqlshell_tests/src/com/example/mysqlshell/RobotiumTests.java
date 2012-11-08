package com.example.mysqlshell.tests;
import android.widget.EditText;
import android.view.View;
import android.view.KeyEvent;
import com.example.mysqlshell.*;
import com.example.mysqlshell.tests.OkConnection;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;

public class RobotiumTests extends BasicActions {

    public void testAddConnection() 
        throws Exception
    {
        fillTextsOkConnection();
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_save_conn, 0);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_list_conns, 0);
        Thread.sleep(5000);
        assertTrue(solo.searchText(OkConnection.name));
    }

    public void testRemoveConnection() 
        throws Exception 
    {
        fillTextsOkConnection();
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_save_conn, 0);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_delete_conn, 0);
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_list_conns, 0);
        Thread.sleep(5000);
        assertTrue(!solo.searchText(OkConnection.name));
    }

    public void testOkConnection() 
        throws Exception 
    {
        fillTextsOkConnection();
        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_connect, 0);
        Thread.sleep(5000);
        assertTrue(solo.searchText(getRStr(R.string.connected_title)));
    }

    public void testWrongConnection() 
        throws Exception 
    {
        EditText ed = (EditText) solo.getView(R.id.conn_id);
        solo.enterText(ed, "Test Wrong connection example");

        ed = (EditText) solo.getView(R.id.host_addr);
        solo.enterText(ed, "10.60.71.59");

        ed = (EditText) solo.getView(R.id.username);
        solo.enterText(ed, "rvs");

        ed = (EditText) solo.getView(R.id.password);
        solo.enterText(ed, "rvs");

        ed = (EditText) solo.getView(R.id.dbname);
        solo.enterText(ed, "rvs1");

        ed = (EditText) solo.getView(R.id.port);
        solo.enterText(ed, "8307");

        getInstrumentation().invokeMenuActionSync(getActivity(), R.id.btn_connect, 0);
        assertTrue( !solo.searchText(getRStr(R.string.connected_title)));
    }
}
