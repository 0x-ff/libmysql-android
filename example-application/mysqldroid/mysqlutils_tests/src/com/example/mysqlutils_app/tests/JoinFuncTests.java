package android.util.mysqlutils.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import android.util.Log;
import android.database.mysql.Query;

public class JoinFuncTests extends AndroidTestCase {

    public void testJoinDummy() throws Throwable {
		
	   ArrayList<String> test = new ArrayList<String>();
	   Assert.assertTrue(Query.join(",",test).equals(""));
    }
    
    public void testJoinSingle() throws Throwable {
		
	   ArrayList<String> test = new ArrayList<String>();
	   test.add("1");
	   Assert.assertTrue(Query.join(",",test).equals("1"));
    }
    
    public void testJoin() throws Throwable {
		
	   ArrayList<String> test = new ArrayList<String>();
	   test.add("1");
	   test.add("2");
	   Assert.assertTrue(Query.join(",",test).equals("1,2"));
    }
    
    public void testJoinComplex() throws Throwable {
		
	   ArrayList<String> test = new ArrayList<String>();
	   test.add("1");
	   test.add("2");
	   test.add("3");
	   test.add("4");
	   Assert.assertTrue(Query.join(",","'",test).equals("'1','2','3','4'"));
    }
}
