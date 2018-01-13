package com.yoson.csv;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
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

import com.yoson.date.DateUtils;  
  
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
//        ContentHandler handler = new SheetHandler(sharedStringsTable, stylesTable, sheetIndex, isLast);
        ContentHandler handler = new SheetHandler2(sharedStringsTable, stylesTable, sheetIndex);
        parser.setContentHandler(handler);  
        return parser;  
    }  
    
  //用一个enum表示单元格可能的数据类型  
    enum CellDataType {   
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL   
    }  
    
    private class SheetHandler2 extends DefaultHandler {  
        
        private SharedStringsTable sst;  
        private String lastContents;  
        private boolean nextIsString;  
          
        private List<String> rowlist = new ArrayList<String>();   
        private int curRow = 0;   
        private int curCol = 0;  
          
        //定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等  
        private String preRef = null, ref = null;  
        //定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格  
        private String maxRef = null;  
          
        private CellDataType nextDataType = CellDataType.SSTINDEX;   
        private final DataFormatter formatter = new DataFormatter();   
        private short formatIndex;   
        private String formatString;   
        private StylesTable stylesTable;
        private int sheetIndex;
          
        
          
        private SheetHandler2(SharedStringsTable sst, StylesTable stylesTable, int sheetIndex) {  
            this.sst = sst;  
            this.stylesTable = stylesTable;
            this.sheetIndex = sheetIndex;
        }  
          
        /** 
         * 解析一个element的开始时触发事件 
         */  
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {  
              
        	if(name.equals("row")) {
        		preRef = "A" + attributes.getValue("r");
        		ref = preRef;
        	} else if(name.equals("c")) {  
        		// c => cell  
                //前一个单元格的位置  
                if(preRef == null){  
                    preRef = attributes.getValue("r");  
                }else{  
                    preRef = ref;  
                }  
                //当前单元格的位置  
                ref = attributes.getValue("r");  
                  
                this.setNextDataType(attributes);   
                  
                // Figure out if the value is an index in the SST  
                String cellType = attributes.getValue("t");  
                if(cellType != null && cellType.equals("s")) {  
                    nextIsString = true;  
                } else {  
                    nextIsString = false;  
                }  
                  
            }  
            // Clear contents cache  
            lastContents = "";  
        }  
          
        /** 
         * 根据element属性设置数据类型 
         * @param attributes 
         */  
        public void setNextDataType(Attributes attributes){   
  
            nextDataType = CellDataType.NUMBER;   
            formatIndex = -1;   
            formatString = null;   
            String cellType = attributes.getValue("t");   
            String cellStyleStr = attributes.getValue("s");   
            if ("b".equals(cellType)){   
                nextDataType = CellDataType.BOOL;  
            }else if ("e".equals(cellType)){   
                nextDataType = CellDataType.ERROR;   
            }else if ("inlineStr".equals(cellType)){   
                nextDataType = CellDataType.INLINESTR;   
            }else if ("s".equals(cellType)){   
                nextDataType = CellDataType.SSTINDEX;   
            }else if ("str".equals(cellType)){   
                nextDataType = CellDataType.FORMULA;   
            }  
            if (cellStyleStr != null){   
                int styleIndex = Integer.parseInt(cellStyleStr);   
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);   
                formatIndex = style.getDataFormat();   
                formatString = style.getDataFormatString();   
                if ("m/d/yy" == formatString){   
                    nextDataType = CellDataType.DATE;   
                    //full format is "yyyy-MM-dd hh:mm:ss.SSS";  
                    formatString = "yyyy-MM-dd";  
                } else if("m/d/yyyy\\ h:mm:ss\\ AM/PM".equals(formatString)) {
                	nextDataType = CellDataType.DATE;   
                    //full format is "yyyy-MM-dd hh:mm:ss.SSS";  
                    formatString = "yyyy-MM-dd hh:mm:ss";  
                }
                if (formatString == null){   
                    nextDataType = CellDataType.NULL;   
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);   
                }   
            }   
        }  
          
        /** 
         * 解析一个element元素结束时触发事件 
         */  
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
            // Process the last contents as required.  
            // Do now, as characters() may be called more than once  
            if(nextIsString) {  
                int idx = Integer.parseInt(lastContents);  
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();  
                nextIsString = false;  
            }  
  
            // v => contents of a cell  
            // Output after we've seen the string contents  
            if (name.equals("v")) {   
                String value = this.getDataValue(lastContents.trim(), "");   
                //补全单元格之间的空单元格  
                if(!ref.equals(preRef)){  
                    int len = countNullCell(ref, preRef); 
                    if(rowlist.size() == 0 && preRef.replaceAll("\\d+", "").equals("A")) len++;
                    for(int i=0;i<len;i++){  
                        rowlist.add(curCol, "");  
                        curCol++;  
                    }  
                }  
                rowlist.add(curCol, value);  
                curCol++;   
            }else {   
                //如果标签名称为 row，这说明已到行尾，调用 optRows() 方法   
                if (name.equals("row")) {  
                    String value = "";  
                    //默认第一行为表头，以该行单元格数目为最大数目  
                    if(curRow == 0){  
                        maxRef = ref;  
                    }  
                    //补全一行尾部可能缺失的单元格  
                    if(maxRef != null){  
                        int len = countNullCell(maxRef, ref);  
                        for(int i=0;i<=len;i++){  
                            rowlist.add(curCol, "");  
                            curCol++;  
                        }  
                    }  
                    //拼接一行的数据  
                    for(int i=0;i<rowlist.size();i++){  
                        if(rowlist.get(i).contains(",")){  
                            value += "\""+rowlist.get(i)+"\",";  
                        }else{  
                            value += rowlist.get(i)+",";  
                        }  
                    }  
                    //加换行符  
                    value += "\n";  
//                    try {  
//                        writer.write(value);  
//                    } catch (IOException e) {  
//                        e.printStackTrace();  
//                    }  
                    outputRow(sheetIndex, curRow, curCol, rowlist);
                    curRow++;
                    //一行的末尾重置一些数据  
                    rowlist.clear();   
                    curCol = 0;   
                    preRef = null;  
                    ref = null;  
                }   
            }   
        }  
          
        /** 
         * 根据数据类型获取数据 
         * @param value 
         * @param thisStr 
         * @return 
         */  
        public String getDataValue(String value, String thisStr)   
  
        {   
            switch (nextDataType)   
            {   
                //这几个的顺序不能随便交换，交换了很可能会导致数据错误   
                case BOOL:   
                char first = value.charAt(0);   
                thisStr = first == '0' ? "FALSE" : "TRUE";   
                break;   
                case ERROR:   
                thisStr = "\"ERROR:" + value.toString() + '"';   
                break;   
                case FORMULA:   
                thisStr = '"' + value.toString() + '"';   
                break;   
                case INLINESTR:   
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());   
                thisStr = rtsi.toString();   
                rtsi = null;   
                break;   
                case SSTINDEX:   
                String sstIndex = value.toString();   
                thisStr = value.toString();   
                break;   
                case NUMBER:   
                if (formatString != null){   
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();   
                }else{  
                    thisStr = value;   
                }   
                thisStr = thisStr.replace("_", "").trim();   
                break;   
                case DATE:   
                    try{  
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);   
                    }catch(NumberFormatException ex){  
                        thisStr = value.toString();  
                    }  
//                thisStr = thisStr.replace(" ", "");  
                break;   
                default:   
                thisStr = "";   
                break;   
            }   
            return thisStr;   
        }   
  
        /** 
         * 获取element的文本数据 
         */  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            lastContents += new String(ch, start, length);  
        }  
          
        /** 
         * 计算两个单元格之间的单元格数目(同一行) 
         * @param ref 
         * @param preRef 
         * @return 
         */  
        public int countNullCell(String ref, String preRef){  
            //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD  
            String xfd = ref.replaceAll("\\d+", "");  
            String xfd_1 = preRef.replaceAll("\\d+", "");  
              
            xfd = fillChar(xfd, 3, '@', true);  
            xfd_1 = fillChar(xfd_1, 3, '@', true);  
              
            char[] letter = xfd.toCharArray();  
            char[] letter_1 = xfd_1.toCharArray();  
            int res = (letter[0]-letter_1[0])*26*26 + (letter[1]-letter_1[1])*26 + (letter[2]-letter_1[2]);  
            return res-1;  
        }  
          
        /** 
         * 字符串的填充 
         * @param str 
         * @param len 
         * @param let 
         * @param isPre 
         * @return 
         */  
        String fillChar(String str, int len, char let, boolean isPre){  
            int len_1 = str.length();  
            if(len_1 <len){  
                if(isPre){  
                    for(int i=0;i<(len-len_1);i++){  
                        str = let+str;  
                    }  
                }else{  
                    for(int i=0;i<(len-len_1);i++){  
                        str = str+let;  
                    }  
                }  
            }  
            return str;  
        }  
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
        	System.out.println(name);
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
                    default:
                    	rowDatas.add("");
                        rowTypes.add(STRING);
                }  
            }  
            // 当解析的一行的末尾时，输出数组中的数据  
            else if(name.equals("row")){  
//                outputRow(this.sheetIndex, rowIdx++, isLastSheet, rowDatas, rowTypes);  
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
//    protected abstract void outputRow(int sheetIndex, int rowIndex, boolean isLastSheet, List<Object> datas, List<Integer> rowTypes);  
    protected abstract void outputRow(int sheetIndex, int curRow, int curCol, List<String> rowlist);
    
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
  
