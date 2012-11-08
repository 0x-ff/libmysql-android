package android.util.mysqlutils.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import android.util.Log;
import android.database.mysql.Condition;
import android.database.mysql.ComplexCondition;
import android.database.mysql.SimpleCondition;

public class ConditionTests extends AndroidTestCase {

    public void testDummy() throws Throwable {
		
	   ComplexCondition where = new ComplexCondition("AND");
       Assert.assertTrue(where.toString().equals(""));
    }
    
    public void testSimple() throws Throwable {
		
	   ComplexCondition where = new ComplexCondition("AND");
	   where.add("`field`='value'");
	   where.add("`field1`=`field`+2");
       Assert.assertTrue(where.toString().equals("(`field`='value') AND (`field1`=`field`+2)"));
    }
    
    public void testComplex() throws Throwable {
		
	   ComplexCondition where = new ComplexCondition("AND");
	   where.add("condition1");
	   
	   ComplexCondition or = new ComplexCondition("OR");
	   or.add("condition2");
	   where.add(or);
       Assert.assertTrue(where.toString().equals("(condition1) AND (condition2)"));
    }
    
    public void testComplex2() throws Throwable {
		
	   ComplexCondition where = new ComplexCondition("AND");
	   where.add("condition1");
	   
	   ComplexCondition or = new ComplexCondition("OR");
	   or.add("condition2");
	   or.add("condition3");
	   or.add("condition4");
	   where.add(or);
       Assert.assertTrue(where.toString().equals("(condition1) AND ((condition2) OR (condition3) OR (condition4))"));
    }
}
