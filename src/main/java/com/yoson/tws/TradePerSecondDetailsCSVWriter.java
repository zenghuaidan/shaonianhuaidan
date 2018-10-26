package com.yoson.tws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.yoson.csv.BackTestCSVWriter;
import com.yoson.model.MainUIParam;
import com.yoson.model.PerSecondRecord;

public class TradePerSecondDetailsCSVWriter {

	public static void WriteCSV(String folderPath, Strategy strategy, String symbol, List<PerSecondRecord> dailyPerSecondRecord)
	{
		FileWriter writer = null;
		String filePath = FilenameUtils.concat(folderPath, strategy.getStrategyName()  + "_TradingDayPerSecondDetails.csv");
		try {
			if(new File(filePath).exists())
				new File(filePath).delete();
			writer = new FileWriter(filePath);
			writeHeader(writer, strategy.getMainUIParam(), symbol);
			writeContent(writer, dailyPerSecondRecord);
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
	
	private static void writeHeader(FileWriter writer, MainUIParam mainUIParam, String symbol)
	{
	    try
	    {
	    	writer.append("Source");
			writer.append(",");
			writer.append(symbol);
			writer.append("\n");
			writer.append("Trade");
			writer.append(",");
			writer.append(mainUIParam.getTradeDataField());
			writer.append("\n");
			writer.append("Ask");
			writer.append(",");
			writer.append(mainUIParam.getAskDataField());
			writer.append("\n");
			writer.append("Bid");
			writer.append(",");
			writer.append(mainUIParam.getBidDataField());
			writer.append("\n");
			writer.append("CP timer");
			writer.append(",");
			writer.append(mainUIParam.getCpTimer() + "");
			writer.append("\n");
			writer.append("CP Buffer");
			writer.append(",");
			writer.append(mainUIParam.getCpBuffer()+"");
			writer.append("\n");
			writer.append("CP Hit Rate");
			writer.append(",");
			writer.append(mainUIParam.getCpHitRate()+"");
			writer.append("\n");
			writer.append("CP smooth");
			writer.append(",");
			writer.append(mainUIParam.getCpSmooth() + "");
			writer.append("\n");
			writer.append("estimation buffer");
			writer.append(",");
			writer.append(mainUIParam.getEstimationBuffer() + "");
			writer.append("\n");
			writer.append("action trigger");
			writer.append(",");
			writer.append(mainUIParam.getActionTrigger() + "");
			writer.append("\n");
			writer.append("action counting");
			writer.append(",");
			writer.append(mainUIParam.getActionCounting() + "");
			writer.append("\n");
			writer.append("% trade stoploss trigger");
			writer.append(",");
			writer.append(mainUIParam.getTradeStopLossTrigger() + "");
			writer.append("\n");
			writer.append("% trade stoploss");
			writer.append(",");
			writer.append(mainUIParam.getTradeStopLossTriggerPercent() + "");
			writer.append("\n");
			writer.append("Absolute trade stoploss");
			writer.append(",");
			writer.append(mainUIParam.getAbsoluteTradeStopLoss() + "");
			writer.append("\n");
			writer.append("Market Start Time");
			writer.append(",");
			writer.append(mainUIParam.getMarketStartTime());
			writer.append("\n");
			writer.append("Lunch Time Start");
			writer.append(",");
			writer.append(mainUIParam.getLunchStartTimeFrom());
			writer.append("\n");
			writer.append("Lunch Time End");
			writer.append(",");
			writer.append(mainUIParam.getLunchStartTimeTo());
			writer.append("\n");
			writer.append("Market End Time");
			writer.append(",");
			writer.append(mainUIParam.getMarketCloseTime());
			writer.append("\n");
			writer.append("Cash per index point");
			writer.append(",");
			writer.append(mainUIParam.getCashPerIndexPoint() + "");
			writer.append("\n");
			writer.append("Trading Fee");
			writer.append(",");
			writer.append(mainUIParam.getTradingFee() + "");
			writer.append("\n");
			writer.append("Other cost per trade");
			writer.append(",");
			writer.append(mainUIParam.getOtherCostPerTrade() + "");
			writer.append("\n");
			writer.append("Unit");
			writer.append(",");
			writer.append(mainUIParam.getUnit() + "");
			writer.append("\n");
			writer.append("LastMinClrPos");
			writer.append(",");
			writer.append(mainUIParam.getLastNumberOfMinutesClearPosition() + "");
			writer.append("\n");
			writer.append("LunchLastMinClrPos");
			writer.append(",");
			writer.append(mainUIParam.getLunchLastNumberOfMinutesClearPosition() + "");
			writer.append("\n");
			writer.append("Include Morning Data");
			writer.append(",");
			writer.append(mainUIParam.isIncludeMorningData() + "");
			writer.append("\n");
			writer.append("Ignore Lunch Time");
			writer.append(",");
			writer.append(mainUIParam.isIgnoreLunchTime() + "");
			writer.append("\n");
			writer.append("Average Step Size");
			writer.append(",");
			writer.append(mainUIParam.getAvgStep() + "");
			writer.append("\n");
			
	    	writer.append(BackTestCSVWriter.getATradingDayHeader());
	    }
		catch(IOException e)
		{
		    e.printStackTrace();
		    System.out.println("Error in writing header");
		} 
	    
	}
	
	private static void writeContent(FileWriter writer, List<PerSecondRecord> dailyPerSecondRecord) throws IOException {
		for (PerSecondRecord perSecondRecord : dailyPerSecondRecord) {
			StringBuilder content = new StringBuilder();
			BackTestCSVWriter.constructPerSecondRecord(content, perSecondRecord);
			writer.append(content.toString());
		}
		writer.flush();
		writer.close();
	}
}
