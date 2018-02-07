package com.yoson.tws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.yoson.date.DateUtils;

public class RawDataCSVWriter {

	public static void WriteCSV(String filePath, String instrumentName, List<Record> tradeList, List<Record> askList, List<Record> bidList)
	{
	    FileWriter writer = null;
		try {
			if(new File(filePath).exists())
				new File(filePath).delete();
			writer = new FileWriter(filePath);
			if(tradeList != null && tradeList.size() > 0)
				writeHeader(writer, instrumentName, tradeList.get(0).getTime());
			writeContent(writer, tradeList, askList, bidList);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void writeHeader(FileWriter writer, String instrumentName, Date date)
	{
	    try
	    {
		    writer.append(instrumentName);
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(date));
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(instrumentName);
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(date));
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(instrumentName);
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(date));
		    writer.append("\n"); // Next Line
		    
		    writer.append("TRADE");
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(org.apache.commons.lang.time.DateUtils.addDays(date, 1)));
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append("ASK");
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(org.apache.commons.lang.time.DateUtils.addDays(date, 1)));
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append("BID");
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMddHHmmss().format(org.apache.commons.lang.time.DateUtils.addDays(date, 1)));
		    writer.append("\n"); // Next Line
		   
		    
		    writer.append("Date");
		    writer.append(",");
		    writer.append("Type"); 
		    writer.append(",");
		    writer.append("Price"); 
		    writer.append(",");
		    writer.append("Size"); 
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append("Date");
		    writer.append(",");
		    writer.append("Type"); 
		    writer.append(",");
		    writer.append("Price"); 
		    writer.append(",");
		    writer.append("Size"); 
		    writer.append(",");
		    writer.append(" "); //Intended to write a blank
		    writer.append(",");
		    writer.append("Date");
		    writer.append(",");
		    writer.append("Type"); 
		    writer.append(",");
		    writer.append("Price"); 
		    writer.append(",");
		    writer.append("Size"); 
		    writer.append("\n"); // Next Line

		
	    }
		catch(IOException e)
		{
		    e.printStackTrace();
		} 
	}
	
	private static void writeContent(FileWriter writer, List<Record> tradeList, List<Record> askList, List<Record> bidList)
	{
		//Find the greatest array size
		int greatestsize = Math.max(Math.max(tradeList.size(), askList.size()), bidList.size());
		try{
			for (int i = 0; i<= (greatestsize - 1); i++)
			{
				
				if ((tradeList.size() -1) >= i)
				{
					writeTradeRecord(writer, tradeList.get(i));
				}
				else // no such record, then fill blank
				{
					writeBlank(writer,false);
				}
				if ((askList.size() - 1) >= i)
				{
					writeAskRecord(writer, askList.get(i));
				}
				else // no such record, then fill blank
				{
					writeBlank(writer,false);
				}
				
				if ((bidList.size() - 1) >= i)
				{
					writeBidRecord(writer, bidList.get(i));
				}
				else // no such record, then fill blank
				{
					writeBlank(writer,true);
				}	
				
				
				
			}
			writer.flush();
			writer.close();	
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	//Write the TRADE Record
	private static void writeTradeRecord(FileWriter writer, Record record)
	{
		try
		{
			writer.append(DateUtils.yyyyMMddHHmmss().format(record.getTime()));
		    writer.append(",");
			writer.append("TRADE");
			writer.append(",");
			writer.append(record.getData()+"");				
			writer.append(",");
			writer.append(record.getSize() + "");
			writer.append(",");
			writer.append(" "); //Intended to write a blank
			writer.append(",");
		}
		catch(IOException e)
		{
			System.out.println("Error in Trade!");
			e.printStackTrace();
		}
	}
	
	//Write the ASK Record
	private static void writeAskRecord(FileWriter writer, Record record )
	{
		try
		{
			writer.append(DateUtils.yyyyMMddHHmmss().format(record.getTime()));
			writer.append(",");
			writer.append("ASK");
			writer.append(",");
			writer.append(record.getData()+"");				
			writer.append(",");
			writer.append(record.getSize() + "");
			writer.append(",");
			writer.append(" "); //Intended to write a blank
			writer.append(",");
		}
		catch(IOException e)
		{
			System.out.println("Error in Ask!");
			e.printStackTrace();
		}
	}
	
	//Write the Bid Record
	private static void writeBidRecord(FileWriter writer, Record record)
	{
		try
		{
			writer.append(DateUtils.yyyyMMddHHmmss().format(record.getTime()));
			writer.append(",");
			writer.append("BID");
			writer.append(",");
			writer.append(record.getData()+"");				
			writer.append(",");
			writer.append(record.getSize() + "");
			writer.append("\n"); //Next Line
		}
		catch(IOException e)
		{
			System.out.println("Error in Bid!");
			e.printStackTrace();
		}
	}
	
	//Write blank
	private static void writeBlank(FileWriter writer, boolean islast)
	{
		try
		{
			writer.append(" ");  //Intended to write a blank
		    writer.append(",");
			writer.append(" ");  //Intended to write a blank
			writer.append(",");
			writer.append(" ");	 //Intended to write a blank			
			writer.append(",");
			writer.append(" ");  //Intended to write a blank
			writer.append(",");
			writer.append(" "); //Intended to write a blank
			if (islast)
			{
				writer.append("\n"); //Next Line
			}
			else
			{
				writer.append(",");
			}
		}
		catch(IOException e)
		{
			System.out.println("Error in Blank!");
			e.printStackTrace();
		}
			
	}
}

