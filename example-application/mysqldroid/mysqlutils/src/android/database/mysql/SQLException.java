package android.database.mysql;

/**
 * Прочие ошибки libmysqlclient и java обертки для нее
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class SQLException extends 
    android.database.SQLException 
{ 
    /**
     * Конструктор
     * 
     * @param message описание ошибки 
     */
	public SQLException(String message) {
		super(message);
	}
};
