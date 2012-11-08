package android.database.mysql;

/**
 * Класс наследник класса Insert для выполнения запросов REPLACE,
 * которые формируются также как и INSERT.
 * 
 * @author Pavel V. Ivanisenko, pahanmipt@mail.ru
 * @version %I%, %G%
 */
public class Replace extends Insert {
	
    /**
     *  Конструктор
     */
	public Replace() {
		super();
		name = "REPLACE";
	}
};
