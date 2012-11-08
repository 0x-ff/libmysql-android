package android.util.mysqlutils.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.database.mysql.Select;
import android.database.mysql.ComplexCondition;

public class SelectQueryTests extends AndroidTestCase {

    public void testSelectSimplest() throws Throwable {
        
        Select sql = new Select();
        sql.from("t");
        Assert.assertTrue(sql.toString().equals("SELECT * FROM `t`"));
    }
    
    public void testSelect1() throws Throwable {
        
        Select sql = new Select();
        sql.field("COUNT(*)", "counts")
           .field("`t`.`whatever`")
           .from("veeeeeeeryLongTableName", "t")
           .where("`t`.`whatever` > 'piece of shit'")
           .group("whatever")
           .limit(0,5);
        Assert.assertTrue(sql.toString().equals("SELECT COUNT(*) AS `counts`,`t`.`whatever` FROM `veeeeeeeryLongTableName` AS `t` WHERE `t`.`whatever` > 'piece of shit' GROUP BY whatever LIMIT 5"));
    }
    
    public void testSelectLeftJoin() throws Throwable {
        
        Select sql = new Select();
        
        ComplexCondition cond = new ComplexCondition("OR");
        cond.add("`t1`.`abrakadabra` IS NOT NULL");
        cond.add("`t2`.`krakazyabla` IS NOT NULL");

        sql.field("`t1`.`abrakadabra`", "A")
           .field("`t2`.`krakazyabla`", "B")
           .from("table1", "t1")
           .from("table2", "t2", "LEFT JOIN")
           .where("`t1`.`id` = `t2`.`id`")
           .where(cond)
           .order("abracadabra desc")
           .limit(10,10);
        Assert.assertTrue(sql.toString().equals("SELECT `t1`.`abrakadabra` AS `A`,`t2`.`krakazyabla` AS `B` FROM `table1` AS `t1` LEFT JOIN `table2` AS `t2` WHERE (`t1`.`id` = `t2`.`id`) AND ((`t1`.`abrakadabra` IS NOT NULL) OR (`t2`.`krakazyabla` IS NOT NULL)) ORDER BY abracadabra desc LIMIT 10,10"));
    }
    
    
}
