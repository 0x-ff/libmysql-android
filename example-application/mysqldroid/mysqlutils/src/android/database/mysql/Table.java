package android.database.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Класс для изменения схемы или данных какой-либо одной таблицы.
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Table {
	
	String name;
	Connector mysql;
	
    /**
     * Конструктор
     * 
     * @param n имя таблицы 
     * @param c класс для которого уже выполнен вызов connect()
     */
	public Table(String n, Connector c) {
		name  = n;
		mysql = c;
	}

    /**
     * Функция возвращает имя таблицы 
     * 
     * @return имя таблицы 
     */ 
	public String getName() 
		{ return name; }

    /**
     * Выполнение запроса DROP TABLE.
     * 
     * @throws SQLException
     */
	public void drop () throws SQLException 
		{ mysql.query("DROP TABLE `" + name + "`"); }
    
    /**
     * Выполнение запроса TRUNCATE TABLE.
     * 
     * @throws SQLException
     */
	public void truncate () throws SQLException 
		{ mysql.query("TRUNCATE TABLE `" + name + "`"); }

    /**
     * Выполнение запроса DELETE FROM
     * 
     * @param where строка условия WHERE
     * @throws SQLException
     * @return количество удаленных записей 
     */
	public int delete(String where) throws SQLException 
		{ return delete(new SimpleCondition(where)); }

    /**
     * Выполнение запроса UPDATE TABLE.
     * 
     * @param row значения и поля для обновления 
     * @param where строка условия WHERE 
     * @throws SQLException
     * @return количество записей которые обновились 
     */
	public int update(HashMap<String, String> row, String where) throws SQLException
		{ return update(row, new SimpleCondition(where)); }

    /**
     * Выполнение запроса UPDATE TABLE.
     * 
     * @param row значения и поля для обновления 
     * @param where условие WHERE {@link Condition}
     * @throws SQLException
     * @return количество записей которые обновились 
     */
	public int update(HashMap<String, String> row, Condition where) 
		throws SQLException
	{
		if ( row.size() <= 0 ) { return 0; }
		Update sql = new Update();
		sql.table(name).where(where);
		String[] fields = (String[]) row.keySet().toArray();
		for (int i = 0; i < fields.length; i++) {
			
			String f = fields[i];
			sql.set(f + "'" + mysql.escape(row.get(f)) + "'");
		}
		mysql.query(sql.toString());
		return 1;
	}

    /**
     * Добавление записи и перезапись в случае если есть конфликты 
     * с уникальными ключами.
     * 
     * @param row запись 
     * @throws SQLException
     * @return 0 | последний добавленный ID auto_increment 
     */
    public int replace(LinkedHashMap<String, String> row) 
		throws SQLException 
	{
		Replace sql = new Replace();
		sql.into(name);
		String[] fields = (String[]) row.keySet().toArray();
		ArrayList<String> r = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			
			String f = fields[i];
			sql.field(f);
			r.add(mysql.escape(row.get(f)));
		}
		sql.row(r);
		mysql.query(sql.toString());
		return 1;
	}
	
    /**
     * Добавление записи и перезапись в случае если есть конфликты 
     * с уникальными ключами.
     * 
     * @param row запись 
     * @throws SQLException 
     * @return 0 | последний добавленный ID auto_increment 
     */
	public int replace(ArrayList<String> row) 
		throws SQLException 
	{
		Replace sql = new Replace();
        
        for (int i = 0; i < row.size(); i++) 
            { row.set( i, mysql.escape( row.get(i) ) ); }

		sql.into(name).row(row);
		mysql.query(sql.toString());
		return 1;
	}

    /**
     * Добавление записи.
     * 
     * @param row запись 
     * @throws SQLException 
     * @return 0 | последний добавленный ID auto_increment 
     */
	public int insert(LinkedHashMap<String, String> row) 
		throws SQLException 
	{
		Insert sql = new Insert();
		sql.into(name);
		String[] fields = (String[]) row.keySet().toArray();
		ArrayList<String> r = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			
			String f = fields[i];
			sql.field(f);
			r.add(mysql.escape(row.get(f)));
		}
		sql.row(r);
		mysql.query(sql.toString());
		return 1;
	}
	
    /**
     * Добавление записи.
     * 
     * @param row запись 
     * @throws SQLException 
     * @return 0 | последний добавленный ID auto_increment 
     */
	public int insert(ArrayList<String> row) 
		throws SQLException 
	{
		Insert sql = new Insert();
        
        for (int i = 0; i < row.size(); i++) 
            { row.set( i, mysql.escape( row.get(i) ) ); }

		sql.into(name).row(row);
		mysql.query(sql.toString());
		return 1;
	}

    /**
     * Удаление записей из таблицы, удовлетворяющих условию.
     * 
     * @param where {@link Condition} условие WHERE
     * @throws SQLException
     * @return количество удаленных строк 
     */
	public int delete(Condition where) 
		throws SQLException 
	{
		
		String w = where.toString();
		String sql = "DELETE FROM `" + name + "`";
		if (w.length() > 0) {
			sql += " WHERE " + w;
		}
		mysql.query(sql);
		return 1;
	}
	
    /**
     * Функция возвращает количество записей в таблице.
     * 
     * @throws SQLException 
     * @return количество записей в таблице
     */
	public int getCount() 
		throws SQLException 
	{
		String sql = "SELECT COUNT(*) FROM `" + name + "`";
		mysql.query(sql);
		mysql.hasMoreRows();
		return Integer.parseInt(mysql.fetchOne());
	}
	
    /**
     * Добавление столбца в таблицу.
     * 
     * @param declaration объявление столбца 
     * @throws SQLException 
     */
	public void addColumn(String declaration) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + "` ADD COLUMN " + declaration;
		mysql.query(sql);
	}
	
    /**
     * Замена объявления столбца.
     * 
     * @param name имя столбца 
     * @param declaration новое объявление столбца 
     * @throws SQLException 
     */
	public void changeColumn(String name, String declaration) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + "` CHANGE COLUMN `" + 
			name + "`" + declaration;
		mysql.query(sql);
	}
	
    /**
     * Удаление столбца 
     * 
     * @param name имя столбца 
     * @throws SQLException 
     */
	public void dropColumn(String name) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + "` DROP COLUMN " + name;
		mysql.query(sql);
	}
	
    /**
     * Удаление PRIMARY KEY
     * 
     * @throws SQLException 
     */
	public void dropPrimaryKey() 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + "` DROP PRIMARY KEY";
		mysql.query(sql);
	}
	
    /**
     * Создание PRIMARY KEY
     * 
     * @param fields список полей, включенных в primary key
     * @throws SQLException 
     */
    public void makePrimaryKey(ArrayList<String> fields) 
        throws SQLException 
    {
        String sql = "ALTER TABLE `" + this.name + "` ADD PRIMARY KEY (`" + 
            Query.join(",", "`", fields) + "`)";
        mysql.query(sql);
    }
    
    /**
     * Создание уникального ключа
     * 
     * @param name имя ключа 
     * @param fields список полей, включенных в ключ
     * @throws SQLException 
     */
	public void addUniqueIndex(String name, ArrayList<String> fields) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + 
			"` ADD UNIQUE INDEX `" + name + "` (`" + 
			Query.join(",", "`", fields) + "`)";
		mysql.query(sql);
	}
	
    /**
     * Создание ключа
     * 
     * @param name имя ключа 
     * @param fields список полей, включенных в ключ
     * @throws SQLException 
     */
	public void addIndex(String name, ArrayList<String> fields) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + 
			"` ADD INDEX `" + name + "` (`" + 
			Query.join(",", "`", fields) + "`)";
		mysql.query(sql);
	}
	
    /**
     * Удаление ключа
     * 
     * @param name имя ключа 
     * @throws SQLException 
     */
	public void dropIndex(String name) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + this.name + "` DROP INDEX `" + name + "`";
		mysql.query(sql);
	}
	
    /**
     * Переименовать таблицу 
     * 
     * @param n новое имя таблицы 
     * @throws SQLException 
     */
	public void rename(String n) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + name + "` RENAME TO `" + n + "`";
		mysql.query(sql);
		name = n;
	}
	
    /**
     * Изменить механизм хранения для таблицы.
     * 
     * @param engine новый механизм хранения  
     * @throws SQLException 
     */
	public void alterEngine(String engine) 
		throws SQLException
	{
		String sql = "ALTER TABLE `" + name + "` ENGINE=" + engine;
		mysql.query(sql);
	}
	
    /**
     * Получить список имен полей таблицы.
     * 
     * @throws SQLException 
     * @return список имен полей таблицы 
     */
	public ArrayList<String> getFieldNames() 
		throws SQLException
	{
		mysql.query("DESC `" + name + "`");
		return mysql.fetchCol();
	}
};
