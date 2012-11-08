package android.util.mysqlutils.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.database.mysql.Table;
import com.example.mysqlutils_app.TestConnector;

public class TableTests extends AndroidTestCase {

    public static TestConnector mysql;

    static {
        
        mysql = new TestConnector();
    };

    public TableTests() {
        
    }

    public void testTableTruncate() throws Throwable {
        
        TestConnector.currentIndex = "testTableTruncate";
        Table t = new Table("t1", mysql);
        t.truncate();
    }
    
    public void testTableDrop() throws Throwable {
        
        TestConnector.currentIndex = "testTableDrop";
        Table t = new Table("t1", mysql);
        t.drop();
    }
    
}
