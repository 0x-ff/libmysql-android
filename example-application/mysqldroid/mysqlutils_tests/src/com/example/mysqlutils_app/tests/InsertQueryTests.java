package android.util.mysqlutils.tests;

import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.Assert;
import android.test.AndroidTestCase;

import android.util.Log;
import android.database.mysql.Insert;
import android.database.mysql.Select;

public class InsertQueryTests extends AndroidTestCase {

    public void testInsert1 () throws Throwable {
		
		ArrayList<String> row = new ArrayList<String>();
		row.add("val_A");
		row.add("val_B");
		row.add("val_C");

		Insert sql = new Insert();
		sql.into("t").row(row);
		Assert.assertTrue(sql.toString().equals("INSERT INTO `t` VALUES ('val_A','val_B','val_C')"));
	}
	
	public void testInsert2 () throws Throwable {
		
		ArrayList<String> row = new ArrayList<String>();
		row.add("val_A");
		row.add("val_B");
		row.add("val_C");

		Insert sql = new Insert();
		sql.into("t")
		   .field("A")
		   .field("B")
		   .field("C")
		   .row(row);
		Assert.assertTrue(sql.toString().equals("INSERT INTO `t`(`A`,`B`,`C`) VALUES ('val_A','val_B','val_C')"));
	}
	
	public void testInsert3 () throws Throwable {
		
		ArrayList<String> row = new ArrayList<String>();
		row.add("val_A");
		row.add("val_B");
		row.add("val_C");

		HashMap<String,String> ondup = new HashMap<String,String>();
		ondup.put("A", "dupA");
		ondup.put("B", "dupB");
		ondup.put("C", "dupC");

		Insert sql = new Insert();
		sql.into("t").row(row).onDuplicate(ondup);
		Assert.assertTrue(sql.toString().equals("INSERT INTO `t` VALUES ('val_A','val_B','val_C') ON DUPLICATE KEY UPDATE `A`='dupA',`B`='dupB',`C`='dupC'"));
	}
	
	public void testInsert4 () throws Throwable {
		
		Select select = new Select();
		select.from("t1");

		Insert sql = new Insert();
		sql.into("t").select(select);
		Assert.assertTrue(sql.toString().equals("INSERT INTO `t` SELECT * FROM `t1`"));
	}
}
