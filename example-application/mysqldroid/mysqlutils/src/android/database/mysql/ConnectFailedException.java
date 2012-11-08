package android.database.mysql;

/**
 * Ошибки при выполнении функции connect()
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class ConnectFailedException extends SQLException {
	
    /**
     * Конструктор
     * 
     * @param message описание ошибки 
     */
	public ConnectFailedException(String message) {
		super(message);
	}
};
