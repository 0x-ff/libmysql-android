package android.database.mysql;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для добавления записей в таблицу.
 * 
 * Примеры использования
 * <pre>
 * {@code
 *      Insert s = new Insert();
 * 
 *      ArrayList<String> r1 = new ArrayList<String>();
 *      ArrayList<String> r2 = new ArrayList<String>();
 *      
 *      r1.add("value11");
 *      r1.add("value12");
 *      r1.add("value13");
 * 
 *      r2.add("value21");
 *      r2.add("value22");
 *      r2.add("value23");
 * 
 *      s.into("table1")
 *        .row(r1)
 *        .row(r2)
 *      ;
 * 
 *      String sql = s.toString();
 *      // INSERT INTO `table1` VALUES('value11','value12','value13'), ('value21','value22','value23')
 *      
 *      s = new Insert();
 *      s.into("table2")
 *        .field("A")
 *        .field("B")
 *        .field("C")
 *      .row(r1)
 *      .row(r2)
 *      ;
 *      sql = s.toString();
 *      // INSERT INTO `table2`(`A`,`B`,`C`) VALUES('value11','value12','value13'),('value21','value22','value23')
 * 
 *      HashMap<String,String> ifDup = new HashMap<String,String>();
 *      ifDup.put("A", "valueA");
 *      ifDup.put("B", "valueB");
 *      ifDup.put("C", "valueC");
 *      
 *      s = new Insert();
 *      s.into("table3")
 *        .row(r1)
 *         .onDuplicate(ifDup)
 *      ;
 *      sql = s.toString();
 *      // INSERT INTO `table1` VALUES('value11','value12','value13') ON DUPLICATE KEY UPDATE `A`='valueA',`B`='valueB',`C`='valueC'
 * 
 *      
 *      Select sel = new Select();
 *      sel.field("A").field("B").field("C").from("table2").where("A>1");
 *      s = new Insert();
 *      s.into("table1")
 *         .select(sel)
 *      ;
 *      sql = s.toString();
 *      // INSERT INTO `table1` SELECT A,B,C FROM `table2` WHERE A>1
 * }
 * </pre>
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Insert extends Query {

	Select select;
	ArrayList<String> fields;
	ArrayList<ArrayList<String>> rows;
	String table;

	HashMap<String, String> onDup;

    /**
     * Конструктор
     * 
     */
	public Insert() {
		super("INSERT");
		select = null;
		rows   = new ArrayList<ArrayList<String>>();
		fields = new ArrayList<String>();
		onDup  = new HashMap<String,String>();
		table  = "";
        options = new ArrayList<String>();
	}
	
    /**
     * В какую таблицу добавлять значения.
     * 
     * @param tab имя таблицы 
     * @return объект Insert 
     */
	public Insert into(String tab) 
		{ table = tab; return this; }
	
    /**
     * Для запросов INSERT ... SELECT ...
     * 
     * @param sel объект Select 
     * @return объект Insert 
     */
	public Insert select(Select sel) 
		{ select = sel; return this; }
	
    /**
     * Добавить имя поля для вставки 
     * 
     * @param field имя поля 
     * @return объект Insert 
     */
	public Insert field(String field) {

		if (field != null && field.length() > 0) {
			fields.add(field);
		}
		return this;
	}
	
    /**
     * Добавить строку
     * 
     * @param r строка запроса 
     * @return объект Insert 
     */
	public Insert row(ArrayList<String> r) {
		
		if (r != null && r.size() > 0) {
			rows.add(r);
		}
		return this;
	}
	
    /**
     * Для запросов INSERT ... ON DUPLICATE KEY UPDATE ...
     * 
     * @param r строка запроса 
     * @return объект Insert 
     */
	public Insert onDuplicate(HashMap<String,String> r) {
		
		if (r != null && r.size() > 0) {
			onDup = r;
		}
		return this;
	}
	
    /**
     * Сборка строки запроса INSERT
     * 
     * @return строка запроса 
     */
	public String toString() {
		
		if (rows.size() <= 0 && select == null) 
			{ return ""; }
		
		if (table.length() <= 0) { return ""; }
		int i = 0;
		String sql = name + (options.size() > 0 ? " " : "") + join(" ", options) + " INTO `" + table + "`";
		
		if (fields.size() > 0) {
			sql += "(" + join(",", "`", fields) + ")";
		}
		
		if (select != null) {
			
			sql += " " + select;
		} else {
			
			sql += " VALUES ";
			i = 0;
			while (i < rows.size()) {

				ArrayList<String> row = rows.get(i);
				sql += "(" + join(",", "'", row) + ")";
				if (i != rows.size() - 1) 
					{ sql += ","; }
				i++;
			}
			
			if (onDup.size() > 0) {
				
				i = 0;
				sql += " ON DUPLICATE KEY UPDATE ";
				Object[] keys = onDup.keySet().toArray();
				while (i < keys.length) {
					
					sql += "`" + keys[i].toString() + "`='" + onDup.get(keys[i].toString()) + "'";
					if (i + 1 != keys.length) {
						sql += ",";
					}
					i++;
				}
			}
		}
		return sql;
	}
};
