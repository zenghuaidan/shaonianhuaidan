package com.yoson.tws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.yoson.date.DateUtils;

public class ScheduledDataCSVWriter {


	public static void WriteCSV(String filePath, String instrumentName, List<ScheduledDataRecord> scheduledDataRecords)
	{
	    FileWriter writer = null;
		try {
			if(new File(filePath).exists())
				new File(filePath).delete();
			writer = new FileWriter(filePath);
			writeHeader(writer);
			writeContent(writer, scheduledDataRecords);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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
	
	private static void writeHeader(FileWriter writer)
	{
	    try
	    {
	    	writer.append("Date");
		    writer.append(",");
		    writer.append(DateUtils.yyyyMMdd().format(new Date()));
		    writer.append("\n"); // Next Line
		    writer.append("***START");
		    writer.append("\n"); // Next Line
		    
		    writer.append("Time");
		    writer.append(",");
		    writer.append("LASTAVG");
		    writer.append(",");
		    writer.append("LASTLAST");
		    writer.append(",");
		    writer.append("LASTMAX");
		    writer.append(",");
		    writer.append("LASTMIN");
		    writer.append(",");
		    writer.append(" ");
		    writer.append(",");
		    
		    writer.append("ASKAVG");
		    writer.append(",");
		    writer.append("ASKLAST");
		    writer.append(",");
		    writer.append("ASKMAX");
		    writer.append(",");
		    writer.append("ASKMIN");
		    writer.append(",");
		    writer.append(" ");
		    writer.append(",");
		  		    	    
		    writer.append("BIDAVG");
		    writer.append(",");
		    writer.append("BIDLAST");
		    writer.append(",");
		    writer.append("BIDMAX");
		    writer.append(",");
		    writer.append("BIDMIN");
		 
		    writer.append("\n"); // Next Line
	    }
		catch(IOException e)
		{
		    e.printStackTrace();
		} 
	}
	
	//Write the CSV content
	private static void writeContent(FileWriter writer, List<ScheduledDataRecord> scheduledDataRecords) throws IOException, ParseException
	{
		for (int i = 0; i< scheduledDataRecords.size(); i++)
		{
			 writer.append(DateUtils.yyyyMMddHHmmss().format(DateUtils.yyyyMMddHHmmss2().parse(scheduledDataRecords.get(i).getTime())));
			 writer.append(",");

			 writer.append(Double.toString(scheduledDataRecords.get(i).getTradeavg()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getTradelast()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getTrademax()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getTrademin()));
			 writer.append(",");
			 writer.append(" ");
			 writer.append(",");
			 
			 writer.append(Double.toString(scheduledDataRecords.get(i).getAskavg()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getAsklast()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getAskmax()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getAskmin()));
			 writer.append(",");
			 writer.append(" ");
			 writer.append(",");
			 
			 writer.append(Double.toString(scheduledDataRecords.get(i).getBidavg()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getBidlast()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getBidmax()));
			 writer.append(",");
			 writer.append(Double.toString(scheduledDataRecords.get(i).getBidmin()));
			 

			 writer.append("\n"); // Next Line
		}
		writer.flush();
		writer.close();
	}
	

	
}

