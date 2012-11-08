package android.database.mysql;

import java.util.ArrayList;

/**
 * Класс для представления набора условий WHERE или HAVING 
 * конкатенированных через AND или OR.
 * 
 * <pre>
 * {@code
 *  ComplexCondition where = new ComplexCondition("AND");
 *  
 *  where.add("field1='value1'")
 *       .add("field2>='value2'");
 *  
 *  ComplexCondition orWhere = new ComplexCondition("OR");
 *  orWhere.add("field4 like '4%'")
 *         .add("ROUND(field5) > 0");
 *  
 *  where.add(orWhere);
 *  String whereString = where.toString();
 *  // (field1='value1') AND (field2='value2') AND ((field4 like '4%') OR (ROUND(field5) > 0))
 * 
 * }
 * </pre>
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class ComplexCondition extends Condition {
	
	String concatExpr;
	ArrayList<Condition> where;
	
    /**
     * Конструктор 
     * 
     * @param concatWith либо "AND" либо "OR"
     */
	public ComplexCondition(String concatWith) {
		concatExpr = concatWith;
		where = new ArrayList<Condition>();
	}
	
    /**
     * Добавить условие в набор 
     * 
     * @param _where строка условия 
     * @return текущий объект ComplexCondition
     */
	public ComplexCondition add(String _where) 
		{ return add(new SimpleCondition(_where)); }
	
    /**
     * Добавить условие в набор 
     * 
     * @param _where условие Condition
     * @return текущий объект ComplexCondition
     */
	public ComplexCondition add(Condition _where) {
		where.add(_where);
		return this;
	}

    /**
     * Сборка строки условия 
     * 
     * @return строка условия
     */
	public String toString() {
		
		int size = where.size();
		if (size <= 0) { return ""; }
		int i = 0;
		ArrayList<String> notEmpty = new ArrayList<String>();
		while (i < size) {
			
			String item = where.get(i).toString();
			if (item.length() > 0) 
				{ notEmpty.add(item); }
			i++;
		}
		
		size = notEmpty.size();
		if (size <= 0) {
			return "";
		} else if (size == 1) {
			return notEmpty.get(0);
		} else {
			return "(" + Query.join(") " + concatExpr + " (", notEmpty) + ")";
		}
	}
};
