package android.database.mysql;

import java.util.HashMap;
import java.util.ArrayList;


/**
 * Класс обертка для JNI класса {@link natives.Connector} 
 * для работы с БД MySQL, через библиотеку libmysqlclient.
 * 
 * Типичные примеры использования
 * <pre>
 * {@code
 * // Создание объекта и подключение к БД 
 * HashMap<String,String> db_config = new HashMap<String,String>();
 * db_config.put("hostAddr", "localhost");
 * db_config.put("userName", "root");
 * db_config.put("passWord", "<your root password>");
 * db_config.put("dbName", "mysql");
 * // необязательный параметр (по умолчанию 3306)
 * db_config.put("portNum", "3306");
 * 
 * try {
 *     Connector connector = new Connector(db_config);
 * } catch (ConfigException e) {
 *     ...
 * }
 * 
 * try {
 *     connector.connect();
 * } catch (ConnectFailedException e1) {
 *     ...
 * }
 * 
 * // Или можно так 
 * try {
 *     Connector connector = new Connector(db_config);
 *     connector.connect();
 * } catch (SQLException e2) {
 *     ...
 * }
 * }
 * </pre>
 * 
 * Выполнение запросов при помощи <code>hasMoreRows()</code>
 * <pre>
 * {@code
 * 
 *  connector.query("SELECT * FROM `table1`;");
 *  while (connector.hasMoreRows()) {
 *      
 *      HashMap<String, String> row = connector.fetchRowHash();
 *      ArrayList<String> row_array = connector.fetchRowArray(); // или так 
 *      String value = connector.fetchOne(); // или так 
 *      // row, row_array, value - будут содержать текущую запись в виде ассоциативного, линейного массива и 
 *      // первого значения линейного массива
 *  }
 *  
 *  // но  
 *  connector.query("SELECT * FROM `table1`;");
 *  ArrayList<String> col = connector.fetchCol();
 *  connector.query("SELECT * FROM `table1`;");
 *  ArrayList<ArrayList<String>> all = connector.fetchAllArray();
 *  connector.query("SELECT * FROM `table1`;");
 *  ArrayList<Hash<String>> all = connector.fetchAllHash();
 * }
 * </pre>
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Connector {

	/**
	 * Порт по умолчанию 
	 */
	public static final String DEFAULT_PORT = "3306";

    HashMap<String,String> config;
	android.database.mysql.natives.Connector __connector;

	/**
	 * Кодировка запроса. Если эта строка непустая, то метод {@link query}
	 * будет перед выполнением каждого запроса выполнять запрос SET CHARACTER SET
	 */
    public String encoding;

	/**
	 * Конструктор
	 * 
	 * @param db_config конфигурация для соединения с сервером MySQL
	 * @throws ConfigException
	 */
    public Connector(HashMap<String,String> db_config) 
        throws ConfigException 
    {
        encoding = "";
        config = db_config;
        if (!db_config.containsKey("hostAddr")) {
			throw new ConfigException("hostAddr is not set");
		}
        String host = db_config.get("hostAddr");
        if (host.length() <= 0) {
			throw new ConfigException("hostAddr is empty");
		}
        
        if (!db_config.containsKey("userName")) {
			throw new ConfigException("userName is not set");
		}
        String user = db_config.get("userName");
        if (user.length() <= 0) {
			throw new ConfigException("userName is empty");
		}

        if (!db_config.containsKey("passWord")) {
			throw new ConfigException("passWord is not set");
		}
        String pass = db_config.get("passWord");
        
        if (!db_config.containsKey("dbName")) {
			throw new ConfigException("dbName is not set");
		}
        String db = db_config.get("dbName");
        if (db.length() <= 0) {
			throw new ConfigException("dbName is empty");
		}
		
        String port = db_config.get("portNum");
        if (port == null) {
			port = DEFAULT_PORT;
		}

        __connector = new android.database.mysql.natives.Connector(host, 
			user, pass, db, port);
    }

    /**
     * Соединение с сервером MySQL
     * 
     * @throws ConnectFailedException
     */
    public void connect() 
        throws ConnectFailedException 
    {
        if ( !__connector.connect() ) {
			throw new ConnectFailedException("connect failed: '" + getLastError() + "'");
		}
    }

    /**
     * Разорвать соединение 
     * 
     */
    public void disconnect() 
    {
        __connector.disconnect();
    }

    /**
     * Выполнить запрос 
     * 
     * @param sql строка запроса 
     */
    public void query(String sql) 
        throws QueryException 
    {
        if (encoding.length() > 0) {
            if (!__connector.query("SET CHARACTER SET " + encoding + ";")) {
                throw new QueryException("query() error: '" + getLastError() + "'");
            }
        }

        if (!__connector.query(sql)) {
			throw new QueryException("query() error: '" + getLastError() + "'");
		}
    }

    /**
     * Функция проверяет есть ли еще строки для получения их функциями fetchOne, fetchRowArray, fetchRowHash
     * и сдвигает курсор на следующую строку.
     * 
     * @return есть ли еще строки (records) для получения их функциями fetchOne, fetchRowArray, fetchRowHash
     */
    public boolean hasMoreRows() 
        throws QueryException 
    {
		int val = __connector.hasMoreRows();
        if (val == -1) {
			throw new QueryException("hasMoreRows() error: '" + getLastError() + "'");
		}
		return val > 0;
    }
    
    /**
     * Функция выбирает список имен полей в контексте текущего select запроса
     * 
     * @return список имен полей
     */
    public ArrayList<String> fields() 
        throws QueryException 
    {
        ArrayList<String> fields = new ArrayList<String>();
        __connector.lastError = "";
        Object[] flds = __connector.fields();
        if (__connector.lastError.length() > 0) {
			throw new QueryException("fields() error: '" + getLastError() + "'");
		}
        if (flds != null) {
			for (int i = 0; i < flds.length; i++) {
				fields.add(i, (String) flds[i]);
			}
		}
        return fields;
    }
    
    /**
     * Функция возвращает все результаты select запроса в виде ассоциативных массивов.
     * 
     * @return все результаты select запроса в виде ассоциативных массивов
     */
    public ArrayList<HashMap<String,String>> 
		fetchAllHash() throws SQLException 
	{
		ArrayList<HashMap<String,String>> all = 
			new ArrayList<HashMap<String,String>>();
		
		while (hasMoreRows()) 
			{ all.add(fetchRowHash()); }
		return all;
	}
    
    /**
     * Функция возвращает все результаты select запроса в виде линейных массивов.
     * 
     * @return все результаты select запроса в виде линейных массивов
     */
    public ArrayList<ArrayList<String>> 
		fetchAllArray() throws SQLException 
	{
		ArrayList<ArrayList<String>> all = 
			new ArrayList<ArrayList<String>>();
		
		while (hasMoreRows()) 
			{ all.add(fetchRowArray()); }
		return all;
	}
    
    /**
     * Функция для получения результатов запроса в цикле, в виде ассоциативных массивов.
     * 
     * @return очередная запись
     */
    public HashMap<String,String> fetchRowHash() 
        throws QueryException 
    {
        HashMap<String, String> row = new HashMap<String, String>();
        ArrayList<String> fields = fields();
        ArrayList<String> row_array = fetchRowArray();
        
        for (int i = 0; i < fields.size(); i++ ) 
			{ row.put(fields.get(i), row_array.get(i)); }

        return row;
    }
    
    /**
     * Функция для получения результатов запроса в цикле, в виде линейных массивов.
     * 
     * @return очередная запись
     */
    public ArrayList<String> fetchRowArray() 
        throws QueryException 
    {
        ArrayList<String> row = new ArrayList<String>();
        __connector.lastError = "";
        Object[] r = __connector.fetchRowArray();
        if (__connector.lastError.length() > 0) {
			throw new QueryException("fetchRowArray() error: '" + getLastError() + "'");
		}
        if (r != null) {
			for (int i = 0; i < r.length; i++) {
				row.add(i, (String) r[i]);
			}
		}
        return row;
    }
    
    /**
     * Функция для получения первого столбца результатов запроса.
     * 
     * @return столбец с данными
     */
    public ArrayList<String> fetchCol() 
        throws QueryException 
    {
        ArrayList<String> col = new ArrayList<String>();
        while (hasMoreRows()) 
			{ col.add(fetchOne()); }
        return col;
    }
    
    /**
     * Функция для получения результатов запроса в цикле, в виде первого значения
     * массива, возвращаемого функцией fetchRowArray.
     * 
     * @return первый элемент записи
     */
    public String fetchOne()
		{ return __connector.fetchOne(); }
    
    /**
     * Функция возвращает последний добавленный Id в текущем соединении.
     * 
     * @return последний добавленный Id
     */
    public int lastInsertId() 
		throws SQLException
    {
		int id = 0;
		id = __connector.lastInsertId();
		if (id < 0) {
			throw new SQLException("lastInsertId() negative error: " + 
				getLastError());
		}
		return id;
	}
    
    /**
     * Экранирование специальных символов
     * 
     * @return экранированная строка
     */
    public String escape(String str) 
		throws SQLException
	{
		if (str == "") { return ""; }
		String res = __connector.escape(str);
		if (res.length() <= 0) {
			throw new SQLException("escape error: '" + getLastError() + "'");
		}
		return res;
	}

    /**
     * Функции обработки транзакций 
     */
    
    /**
     * SET AUTOCOMMIT 1;
     */
    public void enableAutocommit() 
        throws SQLException 
    { query("SET AUTOCOMMIT 1;"); }

    /**
     * SET AUTOCOMMIT 0;
     */
    public void disableAutocommit() 
        throws SQLException 
    { query("SET AUTOCOMMIT 0;"); }

    /**
     * START TRANSACTION;
     */
    public void startTransaction() 
        throws SQLException 
    { query("START TRANSACTION;"); }
    
    /**
     * COMMIT;
     */
    public void commit() 
        throws SQLException 
    { query("COMMIT;"); }

    /**
     * ROLLBACK;
     */
    public void rollback() 
        throws SQLException 
    { query("ROLLBACK;"); }

	/**
     * Free result before all rows fetched
     */
	public void forceFreeResult() {
		__connector.forceFreeResult();
	}

    /**
     * Функция возвращает последнюю возникшую ошибку libmysqlclient
     * 
     * @return описание последней ошибки
     */
    public String getLastError() 
		{ return __connector.lastError; }
};
