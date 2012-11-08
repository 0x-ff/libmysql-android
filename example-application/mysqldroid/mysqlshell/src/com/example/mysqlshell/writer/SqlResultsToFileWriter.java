package com.example.mysqlshell.writer;

import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public abstract class SqlResultsToFileWriter {
    
    private String           filePath;
    private FileOutputStream outStream;
    
    public String lastError;
    
    public SqlResultsToFileWriter(String fPath) {
        filePath = fPath;
        outStream = null;
        lastError = "";
    }

    public boolean writeString(String str) {
		try {
			outStream.write(str.getBytes());
		} catch (IOException exc) {
			lastError = exc.getMessage();
			return false;
		}
		return true;
	}
    
    public boolean startWrite() {
        
        try {
            outStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException exc) {
			lastError = exc.getMessage();
            return false;
        }

        return true;
    }
    
    public boolean endWrite() {

        if (outStream == null) {
            return false;
        }
        try {
            outStream.flush();
            outStream.close();
        } catch (IOException exc) {
			
			lastError = exc.getMessage();
            return false;
        }
        return true;
    }
    
	public boolean writeFields(ArrayList<String> fields) 
		{ return writeRow(fields); }
    
    public abstract boolean writeRow(ArrayList<String> row);
};
