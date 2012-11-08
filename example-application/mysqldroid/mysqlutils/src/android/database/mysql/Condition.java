package android.database.mysql;

/**
 * Класс для сборки строки условия WHERE или HAVING 
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public abstract class Condition {
	
    /**
     * Сборка строки условия.
     * 
     * @return строка условия 
     */
	public abstract String toString();
};
