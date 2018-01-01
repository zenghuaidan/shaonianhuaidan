package com.yoson.csv;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;  
  
public abstract class BigExcelReader{  
      
    enum xssfDataType {  
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,  
    }  
      
    public static final int ERROR = 1;  
    public static final int BOOLEAN = 1;  
    public static final int NUMBER = 2;  
    public static final int STRING = 3;  
    public static final int DATE = 4;  
      
      
//  private DataFormatter formatter = new DataFormatter();  
    private InputStream sheet;  
    private XMLReader parser;  
    private InputSource sheetSource;  
      
    public BigExcelReader(String filename) throws IOException, OpenXML4JException, SAXException{  
        OPCPackage pkg = OPCPackage.open(filename);  
        init(pkg);
    }  
      
    public BigExcelReader(File file) throws IOException, OpenXML4JException, SAXException{  
        OPCPackage pkg = OPCPackage.open(file);  
        init(pkg);  
    }  
      
    public BigExcelReader(InputStream in) throws IOException, OpenXML4JException, SAXException{  
        OPCPackage pkg = OPCPackage.open(in);  
        init(pkg);  
    }  
    
    private void init(OPCPackage pkg) throws IOException, OpenXML4JException, SAXException{  
        XSSFReader xssfReader = new XSSFReader(pkg);  
        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();  
        StylesTable stylesTable = xssfReader.getStylesTable();  
        
        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        int sheetIndex = 0;
        while (sheets.hasNext()) {  
        	sheet = sheets.next();  
        	parser = fetchSheetParser(sharedStringsTable, stylesTable, sheetIndex++, !sheets.hasNext());  
        	sheetSource = new InputSource(sheet); 
        	this.parse();  
        }
    }  
     
    public void parse(){  
        try {  
            parser.parse(sheetSource);  
        }   
        catch (IOException e) {  
            e.printStackTrace();  
        }   
        catch (SAXException e) {  
            e.printStackTrace();  
        }  
        finally{  
            if(sheet != null){  
                try {  
                    sheet.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
      
    private XMLReader fetchSheetParser(SharedStringsTable sharedStringsTable, StylesTable stylesTable, int sheetIndex, boolean isLast) throws SAXException {  
        XMLReader parser =  
            XMLReaderFactory.createXMLReader(  
                    "org.apache.xerces.parsers.SAXParser"  
            );  
        ContentHandler handler = new SheetHandler(sharedStringsTable, stylesTable, sheetIndex, isLast);  
        parser.setContentHandler(handler);  
        return parser;  
    }  
      
    /** 
     * SAX解析的处理类 
     * 每解析一行数据后通过outputRow(String[] datas, int[] rowTypes, int rowIndex)方法进行输出 
     *  
     * @author zpin 
     */  
    private class SheetHandler extends DefaultHandler {  
        private SharedStringsTable sharedStringsTable; // 存放映射字符串  
        private StylesTable stylesTable;// 存放单元格样式  
        private String readValue;// 存放读取值  
        private xssfDataType dataType;// 单元格类型  
        private List<Object> rowDatas;// 存放一行中的所有数据  
        private List<Integer> rowTypes;// 存放一行中所有数据类型  
        private int colIdx;// 当前所在列  
        private int sheetIndex;
        private int rowIdx = 0;
        private boolean isLastSheet;
          
        private short formatIndex;  
//      private String formatString;// 对数值型的数据直接读为数值，不对其格式化，所以隐掉此处  
          
        private SheetHandler(SharedStringsTable sst,StylesTable stylesTable, int sheetIndex, boolean isLastSheet) {  
            this.sharedStringsTable = sst;  
            this.stylesTable = stylesTable; 
            this.sheetIndex = sheetIndex;
            this.isLastSheet = isLastSheet;
        }  
          
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {  
            if(name.equals("c")) {// c > 单元格  
                colIdx = getColumn(attributes);  
                String cellType = attributes.getValue("t");  
                String cellStyle = attributes.getValue("s");  
                  
                this.dataType = xssfDataType.NUMBER;  
                if ("b".equals(cellType)){  
                    this.dataType = xssfDataType.BOOL;  
                }  
                else if ("e".equals(cellType)){  
                    this.dataType = xssfDataType.ERROR;  
                }  
                else if ("inlineStr".equals(cellType)){  
                    this.dataType = xssfDataType.INLINESTR;  
                }  
                else if ("s".equals(cellType)){  
                    this.dataType = xssfDataType.SSTINDEX;  
                }  
                else if ("str".equals(cellType)){  
                    this.dataType = xssfDataType.FORMULA;  
                }  
                else if(cellStyle != null){  
                    int styleIndex = Integer.parseInt(cellStyle);    
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);    
                    this.formatIndex = style.getDataFormat();    
//                  this.formatString = style.getDataFormatString();    
                }  
            }  
            // 解析到一行的开始处时，初始化数组  
            else if(name.equals("row")){  
                int cols = getColsNum(attributes);// 获取该行的单元格数  
                rowDatas = new ArrayList<Object>();  
                rowTypes = new ArrayList<Integer>();  
            }  
            readValue = "";  
        }  
          
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
            if(name.equals("v")) { // 单元格的值  
                switch(this.dataType){  
                    case BOOL: {  
                        char first = readValue.charAt(0);  
                        rowDatas.add(first == '0' ? false : true);
                        rowTypes.add(BOOLEAN);                        
                        break;  
                    }  
                    case ERROR: {  
                        rowDatas.add("ERROR:" + readValue.toString());
                        rowTypes.add(ERROR); 
                        break;  
                    }  
                    case INLINESTR: {  
                        rowDatas.add(new XSSFRichTextString(readValue).toString());
                        rowTypes.add(STRING);
                        break;  
                    }  
                    case SSTINDEX:{  
                        int idx = Integer.parseInt(readValue);    
                        rowDatas.add(new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString());
                        rowTypes.add(STRING);
                        break;  
                    }  
                    case FORMULA:{  
                        rowDatas.add(readValue);
                        rowTypes.add(STRING);
                        break;  
                    }  
                    case NUMBER:{  
                        // 判断是否是日期格式    
                    	if(sheetIndex == 3) {
                    		int i = 0;
                    	}
                        if (HSSFDateUtil.isADateFormat(formatIndex, readValue)) {    
                            Double d = Double.parseDouble(readValue);    
                            Date date = HSSFDateUtil.getJavaDate(d);    
                            rowDatas.add(date);
                            rowTypes.add(DATE);
                        }   
//                      else if (formatString != null){  
//                          cellData.value = formatter.formatRawCellContents(Double.parseDouble(cellValue), formatIndex, formatString);  
//                          cellData.dataType = NUMBER;  
//                      }  
                        else{   
                            rowDatas.add(readValue);
                            rowTypes.add(NUMBER);
                        }  
                        break;  
                    }  
                }  
            }  
            // 当解析的一行的末尾时，输出数组中的数据  
            else if(name.equals("row")){  
                outputRow(this.sheetIndex, rowIdx++, isLastSheet, rowDatas, rowTypes);  
            }  
        }  
  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            readValue += new String(ch, start, length);  
        }  
    }  
      
    /** 
     * 输出每一行的数据 
     *  
     * @param datas 数据 
     * @param rowTypes 数据类型 
     * @param rowIndex 所在行 
     */  
    protected abstract void outputRow(int sheetIndex, int rowIndex, boolean isLastSheet, List<Object> datas, List<Integer> rowTypes);  
      
    private int getColumn(Attributes attrubuts) {    
        String name = attrubuts.getValue("r");   
        int column = -1;    
        for (int i = 0; i < name.length(); ++i) {  
            if (Character.isDigit(name.charAt(i))) {  
                break;  
            }  
            int c = name.charAt(i);    
            column = (column + 1) * 26 + c - 'A';    
        }    
        return column;    
    }  
      
    private int getColsNum(Attributes attrubuts){  
        String spans = attrubuts.getValue("spans");  
        String cols = spans.substring(spans.indexOf(":") + 1);  
        return Integer.parseInt(cols);  
    }  
}  
  
