package android.database.mysql;


/**
 * Класс для представления строки условия WHERE или HAVING 
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class SimpleCondition extends Condition {
	
	String where;
	
    /**
     * Конструктор
     * 
     * @param _where строка условия WHERE или HAVING 
     */
	public SimpleCondition(String _where) 
		{ where = _where; }
	
    /**
     * Сборка строки условия.
     * 
     * @return строка условия
     */
	public String toString() 
		{ return where; }
};
