package android.database.mysql;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Класс для представления запросов SELECT.
 * 
 * Пример использования 
 * <pre>
 * {@code
 *      Select s = new Select();
 *      s.options("DISTINCT")
 *       .field("A")
 *       .field("B", "C")
 *       .from("D")
 *       .from("E","F")
 *       .from("G","H", "LEFT JOIN")
 *       .where("A>1")
 *       .order("B desc")
 *       .limit(10,10)
 *      ;
 *      String sql = s.toString();
 *      // SELECT DISTINCT A,B AS `C` FROM `D`,`E` AS `F` LEFT JOIN `G` AS `H` WHERE A>1 ORDER BY B DESC LIMIT 10,10
 * }
 * </pre>
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Select extends Query {
	
	ComplexCondition wheres;
	ComplexCondition havings;

	// name => alias
	LinkedHashMap<String,String> fields;
	LinkedHashMap<String,String> tables;
	// table name => join type
	LinkedHashMap<String,String> join_rules;
	
    // LinkedHashMap - для сохранения порядка добавления элементов в хэш при сборке запроса 
    
	ArrayList<String> orders;
	ArrayList<String> groups;
	
	int offset;
	int number;

    /**
     * Сборка строки запроса 
     * 
     * @return строка запроса 
     */
	public String toString() {

		if (tables.size() <= 0) { return ""; }
		
		String sql = name + (options.size() > 0 ? " " : "") + join(" ", options) + " ";
		
		Object[] names = (Object[]) fields.keySet().toArray();
		
		int i = 0; int sz = names.length;
		if (sz > 0) {
			while (i < sz) {
				
				String n = names[i].toString(),
						alias = fields.get(n);
				sql += n;
				if (alias.length() > 0) 
					{ sql += " AS `" + alias + "`";	}
				if (sz != i + 1) 
					{ sql += ","; }
				i++;
			}
		} else {
			// select * from 
			sql += "*";
		}
		sql += " FROM";
		
		i = 0; sz = tables.size();
		names = (Object[]) tables.keySet().toArray();
		while (i < sz) {

			String n = names[i].toString(),
				alias = tables.get(n);
			
            if (i > 0) { 
                String join = join_rules.get(n);
				sql += " " + join; 
			}

			sql += " `" + n + "`";
			if (alias.length() > 0) 
				{ sql += " AS `" + alias + "`"; }
			
			i++;
		}
		
		String where = wheres.toString();
		if (where.length() > 0) 
			{ sql += " WHERE " + where; }
		String having = havings.toString();
		if (having.length() > 0) 
			{ sql += " HAVING " + having; }
		i = 0; sz = groups.size();
		if (sz > 0) {
			sql += " GROUP BY " + join(",", groups);
		}
		
		i = 0; sz = orders.size();
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
     * Конструктор 
     * 
     */
	public Select() {

		super("SELECT");
		wheres     = new ComplexCondition("AND");
		havings    = new ComplexCondition("AND");
		offset     = number = 0;
		orders     = new ArrayList<String>();
		groups     = new ArrayList<String>();
		fields     = new LinkedHashMap<String,String>();
		tables     = new LinkedHashMap<String,String>();
		join_rules = new LinkedHashMap<String, String>();
        options = new ArrayList<String>();
	}

    /**
     * Добавить таблицу 
     * 
     * @param name имя таблицы  
     * @return Select
     */
	public Select from(String name) {
		
		if (name != null && name.length() > 0) {
			tables.put(name, "");
			join_rules.put(name, ",");
		}
		return this;
	}
	
    /**
     * Добавить таблицу 
     * 
     * @param name имя таблицы  
     * @param alias псевдоним 
     * @return Select
     */
	public Select from(String name, String alias) {
		
		if (name != null && name.length() > 0) {
			tables.put(name, (alias == null ? "" : alias));
			join_rules.put(name, ",");
		}
		return this;
	} 
	
    /**
     * Добавить таблицу 
     * 
     * @param name имя таблицы  
     * @param alias псевдоним 
     * @param join_rule (INNER JOIN|LEFT JOIN|RIGHT JOIN|,)
     * @return Select
     */
	public Select from(String name, String alias, String join_rule) {
		
		if (name != null && name.length() > 0) {
			tables.put(name, (alias == null ? "" : alias));
			join_rules.put(name, join_rule);
		}
		return this;
	}
	
    /**
     * Добавить поле
     * 
     * @param name имя
     * @return Select
     */
	public Select field(String name) {
		
		if (name != null && name.length() > 0) {
			fields.put(name, "");
		}
		return this;
	}
	
    /**
     * Добавить поле
     * 
     * @param name имя
     * @param alias псевдоним 
     * @return Select
     */
	public Select field(String name, String alias) {
		
		if (name != null && name.length() > 0) {
			fields.put(name, (alias == null ? "" : alias));
		}
		return this;
	}
	
    /**
     * Добавить условие 
     * 
     * @param where условие 
     * @return Select
     */
	public Select where(String where) {

		wheres.add(where);
		return this;
	}
	
    /**
     * Добавить условие 
     * 
     * @param where условие 
     * @return Select
     */
	public Select where(Condition where) {
		
		wheres.add(where);
		return this;
	}
	
    /**
     * Добавить условие HAVING 
     * 
     * @param hav условие 
     * @return Select
     */
	public Select having(String hav) {

		havings.add(hav);
		return this;
	}

    /**
     * Добавить условие HAVING 
     * 
     * @param hav условие 
     * @return Select
     */
	public Select having(Condition hav) {

		havings.add(hav);
		return this;
	}

    /**
     * Добавить выражение сортировки 
     * 
     * @param o выражение 
     * @return Select
     */
	public Select order(String o) {

		orders.add(o);
		return this;
	}

    /**
     * Добавить выражение группировки  
     * 
     * @param g выражение 
     * @return Select
     */
	public Select group(String g) {

		groups.add(g);
		return this;
	}
	
    /**
     * Ограничения 
     * 
     * @param of смещение 
     * @param num количество 
     * @return Select
     */
	public Select limit(int of, int num) {

		offset = of;
		number = num;
		return this;
	}
};
