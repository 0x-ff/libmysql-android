package android.database.mysql;

/**
 * Ошибки в конфигурации db_config 
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class ConfigException extends SQLException {
	
    /**
     * Конструктор
     * 
     * @param message описание ошибки 
     */
	public ConfigException(String message) {
		super(message);
	}
};
