package android.util.mysqlutils.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import android.util.Log;
import android.database.mysql.Update;

public class UpdateQueryTests extends AndroidTestCase {

    public void testDummy() throws Throwable {
		
	   Update sql = new Update();
	   sql.table("testtable")
		  .set("`field1`='value1'")
		  .set("`field2`='value2'")
		  .where("1=1");
       Assert.assertTrue(sql.toString().equals("UPDATE testtable SET `field1`='value1',`field2`='value2' WHERE 1=1"));
    }
}
