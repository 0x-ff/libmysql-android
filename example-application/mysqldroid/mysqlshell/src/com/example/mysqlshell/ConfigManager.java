package com.example.mysqlshell;

import com.example.mysqlshell.provider.ConnectionsBook;
import com.example.mysqlshell.provider.ConnectionsBookMetaData;
import com.example.mysqlshell.provider.ConnectionsBookMetaData.ConnectionsBookTableMetaData;
import com.example.mysqlshell.provider.QueryBook;
import com.example.mysqlshell.provider.QueryBookMetaData;
import com.example.mysqlshell.provider.QueryBookMetaData.QueryBookTableMetaData;
import android.app.Activity;
import android.database.SQLException;
import android.database.Cursor;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.content.ContentValues;
import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/* Мой велосипед для записи XML файлов. */
import android.util.xmlutils.XmlDocumentWriter;

public class ConfigManager {
	
	public String lastError;
	Document doc;
	Activity parent;
	
	public ConfigManager(Activity p) {
		lastError = "";
		parent    = p;
	}
	
	public boolean export_all(String filePath) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder        = factory.newDocumentBuilder();
			doc                            = builder.newDocument();
			doc.appendChild(doc.createElement("config"));
		} catch (ParserConfigurationException e) {
			lastError = e.getMessage();
            return false;
		}

		if (!export_connections()) {
			return false;
		}
		
		if (!export_sqls()) {
			return false;
		}
		
		if (!export_preferences()) {
			return false;
		}

		FileOutputStream outStream;
		try {
            outStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException exc) {
			lastError = exc.getMessage();
            return false;
        }

		try {
			XmlDocumentWriter xml = new XmlDocumentWriter(doc);
			OutputStreamWriter writer = new OutputStreamWriter(outStream);
			xml.write(writer);
			outStream.close();
		} catch (IOException exc) {
			lastError = exc.getMessage();
            return false;
		}
		return true;
	}
	
	public boolean import_all(String filePath) {
		
		FileInputStream inStream = null;
		try {
            inStream = new FileInputStream(filePath);
        } catch (FileNotFoundException exc) {
			lastError = "File '" + filePath + "' not found: " + exc.getMessage();
            return false;
        }
        
		DocumentBuilder builder;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			lastError = "ParseConfiguration " + e.getMessage();
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ioexc) {
				}
			}
            return false;
		}

		try {
			doc = builder.parse(inStream);
		} catch (SAXException saxexc) {
			lastError = saxexc.getMessage();
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ioexc) {
					
				}
			}
			return false;
		} catch (IOException ioexc) {
			lastError = ioexc.getMessage();
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ioexcc) {
					
				}
			}
			return false;
		}
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException ioexcc) {
				
			}
		}

		if (!import_connections()) {
			return false;
		}
	
		if (!import_sqls()) {
			return false;
		}

		if (!import_preferences()) {
			lastError = "error at writing shared preferences";
			return false;
		}

		return true;
	}

	protected boolean import_sqls() {
		
		NodeList sqls;
		try {
			sqls = doc.getElementsByTagName("query");
		} catch (DOMException exc) {
			lastError = exc.getMessage();
			return false;
		}

		try {
			parent.getContentResolver().delete(QueryBookTableMetaData.CONTENT_URI,
				null, null);

			for (int i = 0; i < sqls.getLength(); i++) {
				
				try {
					Element element    = (Element) sqls.item(i);
					ContentValues cv = new ContentValues();
					cv.put(QueryBookTableMetaData.UNIQUE_NAME, 
						element.getAttribute("unique_name"));
					cv.put(QueryBookTableMetaData.SQL_STRING, 
						element.getNodeValue());
					parent.getContentResolver().insert(QueryBookTableMetaData.CONTENT_URI,
						cv);
				} catch (DOMException domexc) {
					
				}
			}
		} catch (SQLException e) {
			lastError = e.getMessage();
			return false;
		}
		return true;
	}
	
		protected boolean export_sqls() {
		
		try {
			Cursor c = parent.getContentResolver().query(
				QueryBookTableMetaData.CONTENT_URI,
				new String [] {
					QueryBookTableMetaData.UNIQUE_NAME,
					QueryBookTableMetaData.SQL_STRING
				}, null, null, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					Element element = doc.createElement("query");
					element.setAttribute("unique_name", c.getString(0));
					CDATASection sql = doc.createCDATASection(c.getString(1));
					element.appendChild(sql);
					doc.getDocumentElement().appendChild(element);
				} while (c.moveToNext());
			}
			c.close();
		} catch (SQLException e) {
			lastError = e.getMessage();
			return false;
		}
		return true;
	}
	
	protected boolean import_connections() {

		NodeList connections;
		try {
			connections = doc.getElementsByTagName("connection");
		} catch (DOMException exc) {
			lastError = exc.getMessage();
			return false;
		}

		try {
			parent.getContentResolver().delete(ConnectionsBookTableMetaData.CONTENT_URI,
				null, null);

			for (int i = 0; i < connections.getLength(); i++) {
				
				try {
					Element element    = (Element) connections.item(i);
					ContentValues cv = new ContentValues();
					cv.put(ConnectionsBookTableMetaData.UNIQUE_NAME, 
						element.getAttribute("unique_name"));
					cv.put(ConnectionsBookTableMetaData.HOST_ADDR, 
						element.getAttribute("host_addr"));
					cv.put(ConnectionsBookTableMetaData.USER_NAME, 
						element.getAttribute("user_name"));
					cv.put(ConnectionsBookTableMetaData.PASS_WORD, 
						element.getAttribute("pass_word"));
					cv.put(ConnectionsBookTableMetaData.DB_NAME, 
						element.getAttribute("db_name"));
					cv.put(ConnectionsBookTableMetaData.PORT_NUM, 
						element.getAttribute("port_num"));
					parent.getContentResolver().insert(ConnectionsBookTableMetaData.CONTENT_URI,
						cv);
				} catch (DOMException domexc) {
					
				}
			}
		} catch (SQLException e) {
			lastError = e.getMessage();
			return false;
		}
		return true;
	}

	protected boolean export_connections() {

		try {
			Cursor c = parent.getContentResolver().query(
				ConnectionsBookTableMetaData.CONTENT_URI,
				new String [] {
					ConnectionsBookTableMetaData.UNIQUE_NAME,
					ConnectionsBookTableMetaData.HOST_ADDR,
					ConnectionsBookTableMetaData.USER_NAME,
					ConnectionsBookTableMetaData.PASS_WORD,
					ConnectionsBookTableMetaData.DB_NAME,
					ConnectionsBookTableMetaData.PORT_NUM
				}, null, null, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					Element element = doc.createElement("connection");
					element.setAttribute("unique_name", c.getString(0));
					element.setAttribute("host_addr", c.getString(1));
					element.setAttribute("user_name", c.getString(2));
					element.setAttribute("pass_word", c.getString(3));
					element.setAttribute("db_name", c.getString(4));
					element.setAttribute("port_num", c.getString(5));
					doc.getDocumentElement().appendChild(element);
				} while (c.moveToNext());
			}
			c.close();
		} catch (SQLException e) {
			lastError = e.getMessage();
			return false;
		}
		return true;
	}
	
	protected boolean import_preferences() {

		NodeList preferences;
		try {
			preferences = doc.getElementsByTagName("preference");
		} catch (DOMException domexc) {
			lastError = domexc.getMessage();
			return false;
		}

		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(parent);
		/*parent.getSharedPreferences(
			"com.example.mysqlshell_preferences", Context.MODE_WORLD_WRITEABLE);*/
		Editor edit = prefs.edit();
		
		try  {
			
			for (int i = 0; i < preferences.getLength(); i++) {
				
				Element element = (Element) preferences.item(i);
				String value    = element.getAttribute("value");
				String name     = element.getAttribute("checkname");
				if (!name.equals("")) {
					edit.putBoolean(name, (value.equals("1") ? true : false));
					continue;
				}
				name = element.getAttribute("stringname");
				if (!name.equals("")) {
					edit.putString(name, value);
				}
			}

		} catch (DOMException exc) {
			lastError = exc.getMessage();
			return false;
		}
		edit.commit();
		return true;
	}
		
	protected boolean export_preferences() {
		
		try {
			SharedPreferences prefs = parent.getSharedPreferences(
				"com.example.mysqlshell_preferences", Context.MODE_PRIVATE);
		
			boolean veryCompact  = prefs.getBoolean("very_compact_output", false),
					persConns    = prefs.getBoolean("use_persistance_connections", false),
					verboseLog   = prefs.getBoolean("verbose_logging", false),
					donUseDefEnc = prefs.getBoolean("dont_use_default_encoding", false);

			String maxLogFileSize     = prefs.getString("max_log_file_size", "1"),
				   maxRowsPerXlsSheet = prefs.getString("max_xls_row_size", "40000"),
				   taskManagerTimeout = prefs.getString("taskman_timeout", "1"),
				   defEncoding        = prefs.getString("redefine_default_encoding", "");

			String outputFormat = prefs.getString("output_format", "csv");

			Element element = doc.createElement("preference");
			element.setAttribute("checkname", "very_compact_output");
			element.setAttribute("value", (veryCompact ? "1" : "0"));
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("checkname", "use_persistance_connections");
			element.setAttribute("value", (persConns ? "1" : "0"));
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("checkname", "verbose_logging");
			element.setAttribute("value", (verboseLog ? "1" : "0"));
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("checkname", "dont_use_default_encoding");
			element.setAttribute("value", (donUseDefEnc ? "1" : "0"));
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("stringname", "max_log_file_size");
			element.setAttribute("value", maxLogFileSize);
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("stringname", "max_xls_row_size");
			element.setAttribute("value", maxRowsPerXlsSheet);
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("stringname", "taskman_timeout");
			element.setAttribute("value", taskManagerTimeout);
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("stringname", "redefine_default_encoding");
			element.setAttribute("value", defEncoding);
			doc.getDocumentElement().appendChild(element);
			element = doc.createElement("preference");
			element.setAttribute("stringname", "output_format");
			element.setAttribute("value", outputFormat);
			doc.getDocumentElement().appendChild(element);

		} catch (DOMException e) {
			lastError = e.getMessage();
			return false;
		}
		 
		return true;
	}
};

