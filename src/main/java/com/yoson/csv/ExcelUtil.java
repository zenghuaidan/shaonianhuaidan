package com.yoson.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	private static DecimalFormat df = new DecimalFormat("0");
	private static DecimalFormat nf = new DecimalFormat("0.00"); 
	
	public static void main(String[] args) {
		ArrayList<ArrayList<ArrayList<Object>>> _list1 = readExcel(new File("I:\\githome\\shaonianhuaidan\\src\\main\\IDEA6_data\\20180817171151\\A Trading Day FOR CHECK.xlsx"));
		ArrayList<ArrayList<ArrayList<Object>>> _list2 = readExcel(new File("I:\\githome\\shaonianhuaidan\\src\\main\\IDEA6_data\\20180817171151\\sample.xlsx"));
//		for(ArrayList<ArrayList<Object>> list : _list) {
//			for(ArrayList<Object> list2 : list) {
//				for(Object object : list2) {
//					System.out.print(object.toString());
//				}
//				System.out.println();
//			}
//			System.out.println("#################################################################################################################################");
//		}
		//time,bid size,bid price,ask price,ask size,last trade,last trade Size,Nominal Px,check time,Reference,CP counting,CP,CPS,CPS Av. L,CP Acc count,Previous Max CPAC,counting after CP,Est.,ON/ OFF,Pre.Action,Action,smooth Action,Position,Pos. counting,MTM,Max. MTM,PnL,No. Trades,Total PnL,
		//Time,Bid Price,Ask Price,Last Trade,Check Market Time,Reference,CP counting,CP,CPS,CPS Av. L,CP Acc count,Previous Max CPAC,counting after CP,Est.,ON/ OFF,Pre.Action,Action,smooth Action,Position,Pos. counting,MTM,Max. MTM,PnL,No. Trades,Total PnL		
		int columnIndex = 0;
		Map<String, Integer> columnMap = new HashMap<String, Integer>();
		columnMap.put("max range", columnIndex++);
		columnMap.put("min range", columnIndex++);
		columnMap.put("range", columnIndex++);
		columnMap.put("upper", columnIndex++);
		columnMap.put("lower", columnIndex++);
		columnMap.put("check", columnIndex++);		
		columnMap.put("stationary check", columnIndex++);
		columnMap.put("stationary slope", columnIndex++);
		columnMap.put("Action", columnIndex++);
		columnMap.put("Smooth Action", columnIndex++);
		columnMap.put("Position", columnIndex++);
		columnMap.put("Pos. counting", columnIndex++);
		columnMap.put("MTM", columnIndex++);
		columnMap.put("Max. MTM", columnIndex++);
		columnMap.put("PnL", columnIndex++);
		columnMap.put("No. Trades", columnIndex++);
		columnMap.put("Total PnL", columnIndex++);
		
		int columnStart1 = 6;
		int columnStart2 = 6;
		
		int rowStart1 = 1;
		int rowEnd1 = 21603;
		
		int rowStart2 = 5;
		
		for (String key : columnMap.keySet()) {
			int compareColumn1 = columnMap.get(key) + columnStart1;
			int compareColumn2 = columnMap.get(key) + columnStart2;
			int j = 0;
			for(int i = rowStart1; i <= rowEnd1; i++, j++) {
				String value1 = _list1.get(0).get(i).get(compareColumn1).toString();
				if(value1.indexOf(".") >= 0)
					value1 = value1.substring(0, value1.indexOf("."));
				String value2 = _list2.get(0).get(rowStart2 + j).get(compareColumn2).toString();
				if(value2.indexOf(".") >= 0)
					value2 = value2.substring(0, value2.indexOf("."));
				String time1 = _list1.get(0).get(i).get(0).toString();
				if(!value1.equals(value2)) {
					System.out.println(key + " => reference=" + i + " ===> " + time1 + " =======> " + value1 + " &&&&& " + value2);
					break;
				}
				
			}			
		}
	}
	
	public static ArrayList<ArrayList<ArrayList<Object>>> readExcel(File file){
		if(file == null){
			return null;
		}
		if(file.getName().endsWith("xlsx") || file.getName().endsWith("xlsm")){
			return readExcel2007(file);
		}else{
			return readExcel2003(file);
		}
	}
	
	public static Map<Integer, List<Object>> readExcelMetaForCheck(File file){
		if(file == null){
			return null;
		}
		if(file.getName().endsWith("xlsx") || file.getName().endsWith("xlsm")){
			return readExcel2007MetaForCheck(file);
		}else{
			return readExcel2003MetaForCheck(file);
		}
	}
	
	public static Map<Integer, List<Object>> readExcel2003MetaForCheck(File file){
		HSSFWorkbook wb = null;
		Map<Integer, List<Object>> data = new TreeMap<Integer, List<Object>>();
		try {
			wb = new HSSFWorkbook(new FileInputStream(file));
			for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
				int key = sheetIndex+1;
				List<Object> list = new ArrayList<Object>();
				try {
					list.add(getExcel2003CellValue(wb.getSheetAt(sheetIndex).getRow(0).getCell(1)));//source
					list.add(getExcel2003CellValue(wb.getSheetAt(sheetIndex).getRow(0).getCell(2)));//datetime
				} catch (Exception e) {
//					System.out.println("Skip sheet" + key + " for " + file.getAbsolutePath());
				}
				data.put(key, list);					
			}
		} catch (Exception e) {

		} finally {
			if(wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	
	public static Map<Integer, List<Object>> readExcel2007MetaForCheck(File file){		
		XSSFWorkbook wb = null;
		Map<Integer, List<Object>> data = new TreeMap<Integer, List<Object>>();
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
			for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
				int key = sheetIndex+1;
				List<Object> list = new ArrayList<Object>();
				try {
					list.add(getExcel2007CellValue(wb.getSheetAt(sheetIndex).getRow(0).getCell(1)));//source
					list.add(getExcel2007CellValue(wb.getSheetAt(sheetIndex).getRow(0).getCell(2)));//datetime
				} catch (Exception e) {
//					System.out.println("Skip sheet" + key + " for " + file.getAbsolutePath());
				}
				data.put(key, list);					
			}			
		} catch (Exception e) {
		} finally {
			if(wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return data;
	}
	
	public static ArrayList<ArrayList<ArrayList<Object>>> readExcel2003(File file){
		ArrayList<ArrayList<ArrayList<Object>>> sheetList = new ArrayList<ArrayList<ArrayList<Object>>>();
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(new FileInputStream(file));
			for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
				try{
					ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
					ArrayList<Object> colList;
					HSSFSheet sheet = wb.getSheetAt(sheetIndex);
					HSSFRow row;
					HSSFCell cell;
					Object value;
					for(int i = sheet.getFirstRowNum() , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
						row = sheet.getRow(i);
						colList = new ArrayList<Object>();
						if(row == null){
							//当读取行为空时
							if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
								rowList.add(colList);
							}
							continue;
						}else{
							rowCount++;
						}
						for( int j = 0 ; j <= row.getLastCellNum() ;j++){
							cell = row.getCell(j);
							if(cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK){
								//当该单元格为空
								if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
									colList.add("");
								}
								continue;
							}
							value = getExcel2003CellValue(cell);
							colList.add(value);
						}//end for j
						rowList.add(colList);
					}//end for i
					
					sheetList.add(rowList);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			return null;
		} finally {
			if(wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return sheetList;
	}

	private static Object getExcel2003CellValue(HSSFCell cell) {
		Object value;
		switch(cell.getCellType()){
		case XSSFCell.CELL_TYPE_STRING:  
			value = cell.getStringCellValue();  
			break;  
		case XSSFCell.CELL_TYPE_NUMERIC:  
			if ("@".equals(cell.getCellStyle().getDataFormatString())) {  
				value = df.format(cell.getNumericCellValue());  
			} else if ("General".equals(cell.getCellStyle()  
					.getDataFormatString())) {  
				value = nf.format(cell.getNumericCellValue());  
			} else {  
				value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());  
			}  
			break;  
		case XSSFCell.CELL_TYPE_BOOLEAN:  
			value = Boolean.valueOf(cell.getBooleanCellValue());
			break;  
		case XSSFCell.CELL_TYPE_BLANK:  
			value = "";  
			break;  
		default:  
			value = cell.toString();  
		}// end switch
		return value;
	}
	
	public static ArrayList<ArrayList<ArrayList<Object>>> readExcel2007(File file){
		ArrayList<ArrayList<ArrayList<Object>>> sheetList = new ArrayList<ArrayList<ArrayList<Object>>>();
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
			for(int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
				try{
					ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
					ArrayList<Object> colList;
					XSSFSheet sheet = wb.getSheetAt(sheetIndex);
					XSSFRow row;
					XSSFCell cell;
					Object value;
					for(int i = sheet.getFirstRowNum() , rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows() ; i++ ){
						row = sheet.getRow(i);
						colList = new ArrayList<Object>();
						if(row == null){
							//当读取行为空时
							if(i != sheet.getPhysicalNumberOfRows()){//判断是否是最后一行
								rowList.add(colList);
							}
							continue;
						}else{
							rowCount++;
						}
						for( int j = 0 ; j <= row.getLastCellNum() ;j++){
							cell = row.getCell(j);
							if(cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK){
								//当该单元格为空
								if(j != row.getLastCellNum()){//判断是否是该行中最后一个单元格
									colList.add("");
								}
								continue;
							}
							value = getExcel2007CellValue(cell);
							colList.add(value);
						}//end for j
						rowList.add(colList);
					}//end for i
					
					sheetList.add(rowList);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			return null;
		} finally {
			if(wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return sheetList;
	}

	private static Object getExcel2007CellValue(XSSFCell cell) {
		Object value;
		switch(cell.getCellType()){
		case XSSFCell.CELL_TYPE_STRING:  
			value = cell.getStringCellValue();  
			break;  
		case XSSFCell.CELL_TYPE_NUMERIC:  
			if ("@".equals(cell.getCellStyle().getDataFormatString())) {  
				value = df.format(cell.getNumericCellValue());  
			} else if ("General".equals(cell.getCellStyle()  
					.getDataFormatString())) {  
				value = nf.format(cell.getNumericCellValue());  
			} else {  
				value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());  
			}  
			break;  
		case XSSFCell.CELL_TYPE_BOOLEAN:  
			value = Boolean.valueOf(cell.getBooleanCellValue());
			break;  
		case XSSFCell.CELL_TYPE_BLANK:  
			value = "";  
			break;  
		default:  
			value = cell.toString();  
		}// end switch
		return value;
	}
	
	public static void writeExcel(ArrayList<ArrayList<Object>> result,String path){
		if(result == null){
			return;
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		for(int i = 0 ;i < result.size() ; i++){
			 HSSFRow row = sheet.createRow(i);
			if(result.get(i) != null){
				for(int j = 0; j < result.get(i).size() ; j ++){
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(result.get(i).get(j).toString());
				}
			}
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            wb.write(os);
        } catch (IOException e){
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File(path);//Excel文件生成后存储的位置。
        OutputStream fos  = null;
        try
        {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }           
	}
	
}