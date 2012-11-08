package com.example.mysqlshell.writer;
import java.util.ArrayList;
import java.io.IOException;

public class SqlResultsPlainWriter extends SqlResultsToFileWriter {

    
    public SqlResultsPlainWriter(String fPath) {
        super(fPath);
    }
    
    public boolean writeRow(ArrayList<String> row) {

		for (int i = 0; i < row.size(); i++) {
			
			writeString("\"");
			writeString(row.get(i).replaceAll("([^\\\\])\\\"","\1\\\""));
			writeString("\"");
			if (i != row.size() - 1) {
				writeString("\t");
			}
		}
		writeString("\r\n");
        return true;
    }
};
