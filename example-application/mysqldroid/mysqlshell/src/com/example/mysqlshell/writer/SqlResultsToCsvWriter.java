package com.example.mysqlshell.writer;
import java.util.ArrayList;
import java.io.IOException;

public class SqlResultsToCsvWriter extends SqlResultsToFileWriter {

    
    public SqlResultsToCsvWriter(String fPath) {
        super(fPath);
    }
    
    public boolean writeRow(ArrayList<String> row) {

		for (int i = 0; i < row.size(); i++) {
			
			writeString("\"");
			writeString(row.get(i).replaceAll("([^\\\\])\"","\1\\\""));
			writeString("\"");
			if (i != row.size() - 1) {
				writeString(";");
			}
		}
		writeString("\r\n");
        return true;
    }
};
