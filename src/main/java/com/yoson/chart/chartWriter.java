package com.yoson.chart;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.yoson.model.BackTestResult;
import com.yoson.model.PerDayRecord;

public class chartWriter {
	private String Destiantion;
	private String instrument_name;
	private ArrayList<BackTestResult> All_Back_Test_Results;
	
	double lowestLow;
    double highestHigh;
    
    double lowestLowPnL;
    double highestHighPnL;
    
    double unit;
	
	public chartWriter(String dest, double unit, PerDayRecord perDayRecord) {
		
		String title = perDayRecord.date.getDate()+"-"+(perDayRecord.date.getMonth()+1)+"-"+(perDayRecord.date.getYear()+1900)+"Chart";
        //XYDataset dataset = createSampleDataset(for_Check_DayRecord);      
        
        
//        JFreeChart chart = ChartFactory.createXYLineChart(
//            title,
//            "X",
//            "Y",
//            dataset,
//            PlotOrientation.VERTICAL,
//            true,
//            false,
//            false
//        );
//        XYPlot plot = (XYPlot) chart.getPlot();
        final XYDataset dataset = createSampleDataset(perDayRecord);
        final XYLineAndShapeRenderer  renderer1 = new XYLineAndShapeRenderer ();
        final NumberAxis rangeAxis1 = new NumberAxis("Trade");
        final XYPlot subplot1 = new XYPlot(dataset, null, rangeAxis1, renderer1);
        subplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        //java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);
        renderer1.setSeriesShapesVisible(0, false);
        renderer1.setSeriesShapesVisible(1, true);
        renderer1.setSeriesShapesVisible(2, true);
        renderer1.setSeriesShapesVisible(3, false);
        renderer1.setSeriesLinesVisible(1, false);
        renderer1.setSeriesLinesVisible(2, false);
        renderer1.setSeriesPaint(3, Color.BLACK);
        
        
        

        ValueAxis yAxis = subplot1.getRangeAxis();
        yAxis.setRange(lowestLow-(50*unit), highestHigh+(50*unit));
        //System.out.println(lowestLow +" "+ highestHigh);
        
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesLinesVisible(0, true);
//        renderer.setSeriesShapesVisible(0, false);
//        renderer.setSeriesLinesVisible(1, false);
//        renderer.setSeriesShapesVisible(1, true);        
//        plot.setRenderer(renderer);
        
        //----------plot for pnl--------------
        XYDataset dataset2 = createDataset2(perDayRecord);
        final XYItemRenderer renderer2 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis2 = new NumberAxis("PnL");
        rangeAxis2.setAutoRangeIncludesZero(false);
        final XYPlot subplot2 = new XYPlot(dataset2, null, rangeAxis2, renderer2);
        subplot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        subplot2.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF));
        subplot2.setDomainGridlinePaint(new Color(0x00, 0x00, 0xff));
        subplot2.setRangeGridlinePaint(new Color(0xff, 0x00, 0x00));
        renderer2.setSeriesPaint(0, Color.BLUE);
        
        //---------combine plot------------------
        final CombinedDomainXYPlot combineplot = new CombinedDomainXYPlot(new NumberAxis("time"));
        combineplot.setGap(10.0);
        
        combineplot.add(subplot1, 1);
        combineplot.add(subplot2, 1);
        
        combineplot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart2 = new JFreeChart(title,
                JFreeChart.DEFAULT_TITLE_FONT, combineplot, true);
        
        DateAxis dateAxis = new DateAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("hh:mm:ss"));
        combineplot.setDomainAxis(dateAxis);
        
        File dir = new File(dest+"_Chart");
        dir.mkdir();
        
        File imageFile = new File(dest+"_Chart/"+title+".PNG");
        int width = 1080;
        int height = 480;
         
        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart2, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }
    
    /**
     * Creates a sample dataset.
     * @param for_Check_DayRecord 
     * 
     * @return A dataset.
     */
    private XYDataset createSampleDataset(PerDayRecord TheDayRecord) {
    	TimeSeries index = new TimeSeries("index");
    	TimeSeries buy = new TimeSeries("buy");
    	TimeSeries sell = new TimeSeries("sell");
    	TimeSeries tradeLine = new TimeSeries("tradeLine");
    	
    	lowestLow = TheDayRecord.dailyPerSecondRecordList.get(0).getLastTrade();
    	highestHigh = 0;
    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    	int currentAction = 0;
    	for (int i = 0; i<=(TheDayRecord.dailyPerSecondRecordList.size() - 1); i++){    		
    		///-------------find max and min for set range--------------------
    		if(TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade() < lowestLow){
    			lowestLow = TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade();
    		}
    		if(TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade() > highestHigh){
    			highestHigh = TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade();
    		}
    		
    		//--------add point of index------------
    		index.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    		//-------add buy and sell action-----------
    		//System.out.println(TheDayRecord.All_Details_List.get(i).time);
			//System.out.println(TheDayRecord.All_Details_List.get(i).time+100);
//    		if(TheDayRecord.All_Details_List.get(i).smooth_action == 0){
//    			if(currentAction > 0){//buying
//    				currentAction = 0;
//    				sell.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//    				tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//    				
//
//    				tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time+1000)),null);
//    			}else if(currentAction < 0){//selling
//    				currentAction = 0;
//        			buy.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        			tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        			tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time+1000)),null);
//    			}
//    		}else if(currentAction == 0){
//    			if(TheDayRecord.All_Details_List.get(i).smooth_action>0){ //buy
//        			currentAction = TheDayRecord.All_Details_List.get(i).smooth_action;
//        			buy.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        			tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        		}
//        		if(TheDayRecord.All_Details_List.get(i).smooth_action<0){ //sell
//        			currentAction = TheDayRecord.All_Details_List.get(i).smooth_action;
//        			sell.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        			tradeLine.add(new Second(new Date(TheDayRecord.All_Details_List.get(i).time)), TheDayRecord.All_Details_List.get(i).lasttrade);
//        		}
//    		}
    		
    		if(currentAction == 0){//--------------------not buying or selling-------------------------
    			if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction()>0){ // add buy record
        			currentAction = TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction();
        			buy.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        			tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        		}
        		if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction()<0){ //add sell record
        			currentAction = TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction();
        			sell.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        			tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        		}
        		//---------------------------in buying----------------------------
    		}else if (currentAction == 1){
    			if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction() == 0){// sell one time
    				currentAction = 0;
    				sell.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+1000)),null);
    				
    			}else if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction() == -1){// sell twice
    				currentAction = -1;
    				sell.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+1000)),null);
    				sell.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+2000)), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        			tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+2000)), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
 
    			}
    			//-----------------in selling----------------------------------------------
    		}else if (currentAction == -1){
    			if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction() == 0){// buy one time
    				currentAction = 0;
    				buy.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+1000)),null);
    				//System.out.println(TheDayRecord.All_Details_List.get(i).lasttrade);
    				
    			}else if(TheDayRecord.dailyPerSecondRecordList.get(i).getSmoothAction() == 1){// buy twice
    				currentAction = 1;
    				buy.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
    				tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+1000)),null);
    				buy.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+2000)), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
        			tradeLine.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime()+2000)), TheDayRecord.dailyPerSecondRecordList.get(i).getLastTrade());
 
    			}
    		}
    	}
        
        
    	TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(index);
        dataset.addSeries(buy);
        dataset.addSeries(sell);
        dataset.addSeries(tradeLine);
        return dataset;
    }
    
    private XYDataset createDataset2(PerDayRecord TheDayRecord) {

        // create dataset 2...
        TimeSeries PnL = new TimeSeries("PnL");
        lowestLowPnL = TheDayRecord.dailyPerSecondRecordList.get(0).getTotalPnl();
        highestHighPnL = 0;
        
        for (int i = 0; i<=(TheDayRecord.dailyPerSecondRecordList.size() - 1); i++){
        	PnL.add(new Second(new Date(TheDayRecord.dailyPerSecondRecordList.get(i).getTime())), TheDayRecord.dailyPerSecondRecordList.get(i).getTotalPnl());
        	
        	if(TheDayRecord.dailyPerSecondRecordList.get(i).getTotalPnl() < lowestLowPnL){
        		lowestLowPnL = TheDayRecord.dailyPerSecondRecordList.get(i).getTotalPnl();
    		}
    		if(TheDayRecord.dailyPerSecondRecordList.get(i).getTotalPnl() > highestHighPnL){
    			highestHighPnL = TheDayRecord.dailyPerSecondRecordList.get(i).getTotalPnl();
    		}
        }    
        

        return new TimeSeriesCollection(PnL);

    }
    
}
