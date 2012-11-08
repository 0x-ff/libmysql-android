package android.database.mysql;

/**
 * Прочие ошибки libmysqlclient  
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class QueryException extends SQLException {

    /**
     * Конструктор
     * 
     * @param message описание ошибки 
     */
	public QueryException(String message) {
		super(message);
	}
};
