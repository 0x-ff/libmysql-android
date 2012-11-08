package android.database.mysql;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Абстрактный класс для логического представления запросов MySQL. 
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public abstract class Query {
	
    protected String name;
    
    protected ArrayList<String> options;
    
    /**
     * Выяснить, является ли строка запроса - запросом на чтение.
     * 
     * @param sql строка запроса 
     * @return является ли строка запросом на чтение 
     */ 
	public static boolean isSelectQuery(String sql) {

		Pattern regex = Pattern.compile( "^[\\s]*(select|show|desc)", 
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher match = regex.matcher(sql);
		return match.find(0);
	}

    /**
     * Конкатенация массива строкой splitter.
     * 
     * @param splitter разделитель при конкатенации 
     * @param arr массив для конкатенации в строку 
     * @return строка <arr[0]><splitter><arr[1]><splitter>....<splitter><arr[last]>
     */
	public static String join(String splitter, ArrayList<String> arr) 
		{ return join(splitter, "", arr); }

    /**
     * Конкатенация массива строкой splitter, и ограничение его элементов строками border.
     * 
     * @param splitter разделитель при конкатенации 
     * @param border строки границы элементов массива
     * @param arr массив для конкатенации в строку 
     * @return строка <border><arr[0]><border><splitter><border><arr[1]><border><splitter>....<splitter><border><arr[last]><border>
     */
	public static String join(String splitter, String border, 
		ArrayList<String> arr) 
	{
		int sz = arr.size();
		if (sz <= 0) { return ""; }
		String res = "";
		
		int i = 0;
		while (i < sz) {
			
			res += border + arr.get(i) + border;
			if (sz != i + 1) 
				{ res += splitter; }
			i++;
		}
		return res;
	}

    /**
     * Установка различных опций запроса.
     * 
     * @param name опция 
     */
    public void option(String name) 
        { options.add(name); }

    /**
     * Функция возвращает <code>true</code> если запрос, представляемый этим объектом, 
     * является запросом на чтение.
     * 
     * @return является ли этот объект запросом на чтение 
     */
    public boolean isSelectQuery() 
        { return isSelectQuery(name); }

    /**
     * Конструктор
     * 
     * @param n имя запроса (INSERT|SELECT|UPDATE|REPLACE)
     */
    public Query(String n) {
        name = n;
    }



    /**
     * Сборка строки запроса
     * 
     * @return строка запроса 
     */
	public abstract String toString();
};
