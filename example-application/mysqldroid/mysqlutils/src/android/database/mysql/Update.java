package android.database.mysql;

import java.util.ArrayList;

/**
 * Класс для обновления данных в таблицах.
 * 
 * Пример использования
 * <pre>
 * {@code
 *      Update s = new Update();
 *      s.table("table1").
 *       .table("table2")
 *       .set("`table1`.`A`=`table2`.`A`")
 *       .set("`table1`.`B`=`table2`.`B`")
 *       .where("`table1`.`A`>1")
 *      ;
 *      String sql = s.toString();
 *      // UPDATE `table1`,`table2` SET `table1`.`A`=`table2`.`A`,`table1`.`B`=`table2`.`B` WHERE `table1`.`A`>1
 * }
 * </pre>
 * 
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Update extends Query {
	
	ArrayList<String> tables;
	ArrayList<String> sets;
	ComplexCondition wheres;
    ArrayList<String> orders;
    int offset;
	int number;
	
    /**
     * Конструктор
     * 
     */
	public Update() {
		super("UPDATE");
		
		tables = new ArrayList<String>();
		sets   = new ArrayList<String>();
		wheres = new ComplexCondition("AND");
        offset     = number = 0;
		orders     = new ArrayList<String>();
        options = new ArrayList<String>();
	}
	
    /**
     * Добавить таблицу в список обновления.
     * 
     * @param name имя таблицы 
     * @return Update
     */
	public Update table(String name) 
	{
		if (name != null && name.length() > 0) 
			{ tables.add(name); }
		return this;
	}
	
    
    /**
     * Добавить выражение в список обновления
     * 
     * @param expr выражение для добавления в набор SET
     * @return Update
     */
	public Update set(String expr) {
		
		if (expr != null && expr.length() > 0) 
			{ sets.add(expr); }
		return this;
	}
	
    /**
     * Добавить условие WHERE
     * 
     * @param where условие 
     * @return Update
     */
	public Update where(String where) 
		{ return where(new SimpleCondition(where)); }
	
    /**
     * Добавить условие WHERE
     * 
     * @param where условие 
     * @return Update
     */
	public Update where(Condition where) {
		
		wheres.add(where);
		return this;
	}

    /**
     * Сборка строки запроса 
     * 
     * @return строка запроса Update
     */
	public String toString() {
		
		if (tables.size() <= 0) 
			{ return ""; }
		if (sets.size() <= 0) 
			{ return ""; }
		
		String sql = name + (options.size() > 0 ? " " : "") + join(" ", options) + " ";
		sql += join(",", tables);
		sql += " SET ";
		sql += join(",", sets);
		String where = wheres.toString();
		if (where.length() > 0) 
			{ sql += " WHERE " + where; }
        int i = 0; int sz = orders.size();
		if (sz > 0) {
			sql += " ORDER BY " + join(",", orders);
		}
		
		if (number > 0) {
			
			sql += " LIMIT ";
			Integer val;
			if (offset > 0) {
				val = offset;
				sql += val.toString() + ",";
			}
			val = number;
			sql += val.toString();
		}
		return sql;
	}
    
    /**
     * Добавить выражение сортировки 
     * 
     * @param o выражение 
     * @return Select
     */
	public Update order(String o) {

		orders.add(o);
		return this;
	}
    
    /**
     * Ограничения 
     * 
     * @param of смещение 
     * @param num количество 
     * @return Select
     */
	public Update limit(int of, int num) {

		offset = of;
		number = num;
		return this;
	}
};
