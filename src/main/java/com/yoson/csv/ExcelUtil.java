package com.yoson.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
		ArrayList<ArrayList<ArrayList<Object>>> _list = readExcel(new File("C:\\Users\\larry\\Desktop\\Intraday_Future_downloader_series_Marc_Gor_NEW_NK1_autosave_Aug2017_1_done.xlsm"));
//		for(ArrayList<ArrayList<Object>> list : _list) {
//			for(ArrayList<Object> list2 : list) {
//				for(Object object : list2) {
//					System.out.print(object.toString());
//				}
//				System.out.println();
//			}
//			System.out.println("#################################################################################################################################");
//		}
		System.out.println(_list.get(2).get(2).get(0).toString());
		System.out.println(_list.get(2).get(2).get(1).toString());
		System.out.println(_list.get(2).get(2).get(2).toString());
		System.out.println(_list.get(2).get(2).get(3).toString());
		System.out.println(_list.get(2).get(2).get(4).toString());
		System.out.println(_list.get(2).get(2).get(5).toString());
		System.out.println(_list.get(2).get(2).get(6).toString());
		System.out.println(_list.get(2).get(2).get(7).toString());
		
		
		
		System.out.println(_list.get(2).get(3).get(0).toString());
		System.out.println(_list.get(2).get(3).get(1).toString());
		System.out.println(_list.get(2).get(3).get(2).toString());
		System.out.println(_list.get(2).get(3).get(3).toString());
		System.out.println(_list.get(2).get(3).get(4).toString());
		System.out.println(_list.get(2).get(3).get(5).toString());
		System.out.println(_list.get(2).get(3).get(6).toString());
		System.out.println(_list.get(2).get(3).get(7).toString());
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