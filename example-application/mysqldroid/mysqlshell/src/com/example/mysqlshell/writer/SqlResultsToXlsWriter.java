package com.example.mysqlshell.writer;
import java.util.ArrayList;
import java.io.IOException;

public class SqlResultsToXlsWriter extends SqlResultsToFileWriter {
    
    public int maxSheetSize;
    public String sheetPrefix;
    
    private int pos;
    private int rowsc;
    
    public SqlResultsToXlsWriter(String fPath) {
        super(fPath);
        maxSheetSize = 40000;
        sheetPrefix  = "sheet";
        rowsc = pos = 0;
    }
    
    public boolean startWrite() {
        
        pos = 0;
        if (!super.startWrite()) 
            { return false; }
        
        String start = 
			"<?xml version=\"1.0\"?>\n" +
			"<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"" +
			" xmlns:o=\"urn:schemas-microsoft-com:office:office\"" +
			" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"" +
			" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"" +
			" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n" +
			"<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\">\n" +
			"<WindowHeight>10005</WindowHeight>\n" +
			"<WindowWidth>10005</WindowWidth>\n" +
			"<WindowTopX>120</WindowTopX>\n" +
			"<WindowTopY>135</WindowTopY>\n" +
			"<ProtectStructure>False</ProtectStructure>\n" +
			"<ProtectWindows>False</ProtectWindows>\n" +
			"</ExcelWorkbook>\n" +
			"<Styles>\n" +
			"<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n" +
			"<Alignment ss:Vertical=\"Bottom\"/>\n" +
			"<Borders/>\n" +
			"<Font ss:FontName=\"Arial Cyr\" x:CharSet=\"204\"/>\n" +
			"<Interior/>\n" +
			"<NumberFormat/>\n" +
			"<Protection/>\n" +
			"</Style>\n" +
			"</Styles>\n" +
			" <Worksheet ss:Name=\"";
			start = start + sheetPrefix;
			start += "\">\n<Table>\n";
        
        return writeString(start);
    }
    
    public boolean forceNextSheetCreate() {
		pos++;
		return writeString("</Table>\n</Worksheet>\n<Worksheet ss:Name=\"" +
			sheetPrefix + "\">\n<Table>");
	}
    
    public boolean endWrite() {
        
        if (!writeString("</Table>\n</Worksheet>\n</Workbook>\n")) {
			return false;
		}
        return super.endWrite();
    }

    public boolean writeRow(ArrayList<String> row) {
        
        rowsc++;
        if (rowsc > maxSheetSize) {
			forceNextSheetCreate();
			rowsc = 0;
		}
		writeString("<Row>\n");
		for (int i = 0; i < row.size(); i++) {

			writeString("<Cell>\n<Data ss:Type=\"String\"> ");
			writeString(row.get(i));
			writeString("</Data>\n</Cell>\n");
		}
        return writeString("</Row>\n");
    }
};
