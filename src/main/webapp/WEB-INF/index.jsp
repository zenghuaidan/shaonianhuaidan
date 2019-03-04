<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>${ title }</title>
<script type="text/javascript">
</script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/script/mmnt.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/index.css"/>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="<%=request.getContextPath()%>/resources/script/jquery.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.full.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/mmnt.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/index.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.form.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>
<body>
	<input type="hidden" value="${ mainUIParam.sourcePath }">
	<div id="tab">  
        <div id="tab-header">  
            <ul>  
                <li tab="backtestTab" class="selected">Back Test</li>  
                <li tab="connectionTab">Connection</li>
    			<li tab="marketDataTab">Market Data</li>
    			<li tab="orderStatusTab">Order Status</li>
    			<li tab="controlTab">Control</li>
    			<li tab="strategyTab">Strategy</li>
    			<li tab="expiryTab">Expiry Dates</li>
    			<li tab="scheduleTab">Schedule Dates</li>
    			<li tab="btLogTab">BT Status</li>
            </ul>  
        </div>  
        <div id="tab-content">  
            <div class="dom" style="display: block;">  
				<table style="width:100%">
					<tr>
						<td style="text-align:start;vertical-align:baseline;">
							<table>
							   <tr>
							      <td>Strategy</td>
							      <td>
							         <input name="strategyName" />	         
							      </td>
							      <td><input type="button" onclick="addStrategy(this);" value="Add Strategy"/></td>
							      <td>${comment}</td>
							   </tr>	   
							   <tr>
							      <td>Source</td>
							      <td>
							         <select name="source" onchange="sourceChange();">
							         	 <option value="">-----Please Select-----</option>
								         <c:forEach items="${sources}" var="source" varStatus="i">
								            <option ${source eq mainUIParam.source ? "selected" : ""}>${source}</option>
								         </c:forEach>
							         </select>	         
							      </td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Ticker</td>
							      <td>
							         <select name="ticker" onchange="tickerChange();">
							         	<option value="">-----Please Select-----</option>
								         <c:forEach items="${tickers}" var="ticker" varStatus="i">
								            <option ${ticker eq mainUIParam.ticker ? "selected" : ""}>${ticker}</option>
								         </c:forEach>
							         </select>	         
							      </td>
							      <td></td>
							      <td></td>
							   </tr>							   
   						   	   <tr>
							   		<td>
							   	  		<table>
										   <tr>
										      <td>&nbsp;</td>
										   </tr>
										   <tr>
										      <td>Output Data</td>
										   </tr>
							   	  		</table>
							   		</td>
							   		<td>
							   	  		<table>
										   <tr>
										      <td>Open</td>
										   </tr>
										   <tr>
										      <td><input name="outputDataOpen" ${ mainUIParam.outputDataOpen ? "checked" : "" } type="checkbox"/></td>
										   </tr>										   
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Avg</td>
										   </tr>
										   <tr>
										      <td><input name="outputDataAvg" ${ mainUIParam.outputDataAvg ? "checked" : "" } type="checkbox"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Last</td>
										   </tr>
										   <tr>
										      <td><input name="outputDataLast" ${ mainUIParam.outputDataLast ? "checked" : "" } type="checkbox"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Max</td>
										   </tr>
										   <tr>
										      <td><input name="outputDataMax" ${ mainUIParam.outputDataMax ? "checked" : "" } type="checkbox"/></td>
										   </tr>										   
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Min</td>
										   </tr>
										   <tr>
										      <td><input name="outputDataMin" ${ mainUIParam.outputDataMin ? "checked" : "" } type="checkbox"/></td>
										   </tr>										   
							   	  		</table>
							   	  	</td>
							   </tr>							   							   	   
						   	   <tr>
							   		<td>
							   	  		<table>
										   <tr>
										      <td>&nbsp;</td>
										   </tr>
										   <tr>
										      <td>Trade</td>
										   </tr>
										   <tr>
										      <td>Ask</td>
										   </tr>
										   <tr>
										      <td>Bid</td>
										   </tr>
							   	  		</table>
							   		</td>
							   		<td>
							   	  		<table>
										   <tr>
										      <td>Open</td>
										   </tr>
										   <tr>
										      <td><input name="tradeDataField" ${ mainUIParam.tradeDataField eq "tradeopen" ? "checked" : "" } value="tradeopen" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="askDataField" ${ mainUIParam.askDataField eq "askopen" ? "checked" : "" } value="askopen" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="bidDataField" ${ mainUIParam.bidDataField eq "bidopen" ? "checked" : "" } value="bidopen" type="radio"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Avg</td>
										   </tr>
										   <tr>
										      <td><input name="tradeDataField" ${ mainUIParam.tradeDataField eq "tradeavg" ? "checked" : "" } value="tradeavg" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="askDataField" ${ mainUIParam.askDataField eq "askavg" ? "checked" : "" } value="askavg" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="bidDataField" ${ mainUIParam.bidDataField eq "bidavg" ? "checked" : "" } value="bidavg" type="radio"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Last</td>
										   </tr>
										   <tr>
										      <td><input name="tradeDataField" ${ mainUIParam.tradeDataField eq "tradelast" ? "checked" : "" } value="tradelast" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="askDataField" ${ mainUIParam.askDataField eq "asklast" ? "checked" : "" } value="asklast" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="bidDataField" ${ mainUIParam.bidDataField eq "bidlast" ? "checked" : "" } value="bidlast" type="radio"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Max</td>
										   </tr>
										   <tr>
										      <td><input name="tradeDataField" ${ mainUIParam.tradeDataField eq "trademax" ? "checked" : "" } value="trademax" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="askDataField" ${ mainUIParam.askDataField eq "askmax" ? "checked" : "" } value="askmax" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="bidDataField" ${ mainUIParam.bidDataField eq "bidmax" ? "checked" : "" } value="bidmax" type="radio"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   	  	<td>
							   	  		<table>
										   <tr>
										      <td>Min</td>
										   </tr>
										   <tr>
										      <td><input name="tradeDataField" ${ mainUIParam.tradeDataField eq "trademin" ? "checked" : "" } value="trademin" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="askDataField" ${ mainUIParam.askDataField eq "askmin" ? "checked" : "" } value="askmin" type="radio"/></td>
										   </tr>
										   <tr>
										      <td><input name="bidDataField" ${ mainUIParam.bidDataField eq "bidmin" ? "checked" : "" } value="bidmin" type="radio"/></td>
										   </tr>
							   	  		</table>
							   	  	</td>
							   </tr>
							   <tr>
							      <td></td>
							      <td>From</td>
							      <td>To</td>
							      <td>Literal</td>
							   </tr>
							   <tr>
							      <td>Average Step Size</td>
		      				      <td><input name="avgStep" value="${mainUIParam.avgStep}" type="text"/></td>
							      <td><input name="avgStepTo" value="${mainUIParam.avgStepTo}" type="text"/></td>
							      <td><input name="avgStepLiteral" value="${mainUIParam.avgStepLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>T-Short</td>
							      <td><input name="tShort" value="${mainUIParam.tShort}" type="text"/></td>
							      <td><input name="tShortTo" value="${mainUIParam.tShortTo}" type="text"/></td>
							      <td><input name="tShortLiteral" value="${mainUIParam.tShortLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>T-Long</td>
							      <td><input name="tLong" value="${mainUIParam.tLong}" type="text"/></td>
							      <td><input name="tLongTo" value="${mainUIParam.tLongTo}" type="text"/></td>
							      <td><input name="tLongLiteral" value="${mainUIParam.tLongLiteral}" type="text"/></td>
							   </tr>		
							    <tr>
							      <td>MA-S</td>
							      <td><input name="mas" value="${mainUIParam.mas}" type="text"/></td>
							      <td><input name="masTo" value="${mainUIParam.masTo}" type="text"/></td>
							      <td><input name="masLiteral" value="${mainUIParam.masLiteral}" type="text"/></td>
							   </tr>
   							   <tr>
							      <td>MA-L</td>
							      <td><input name="mal" value="${mainUIParam.mal}" type="text"/></td>
							      <td><input name="malTo" value="${mainUIParam.malTo}" type="text"/></td>
							      <td><input name="malLiteral" value="${mainUIParam.malLiteral}" type="text"/></td>
							   </tr>
   							   <tr>
							      <td>MA-T</td>
							      <td><input name="mat" value="${mainUIParam.mat}" type="text"/></td>
							      <td><input name="matTo" value="${mainUIParam.matTo}" type="text"/></td>
							      <td><input name="matLiteral" value="${mainUIParam.matLiteral}" type="text"/></td>
							   </tr>					   							   
							   <tr>
							      <td>stoploss</td>
							      <td><input name="stopLoss" value="${mainUIParam.stopLoss}" type="text"/></td>
							      <td><input name="stopLossTo" value="${mainUIParam.stopLossTo}" type="text"/></td>
							      <td><input name="stopLossLiteral" value="${mainUIParam.stopLossLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>tradestoploss</td>
							      <td><input name="tradeStopLoss" value="${mainUIParam.tradeStopLoss}" type="text"/></td>
							      <td><input name="tradeStopLossTo" value="${mainUIParam.tradeStopLossTo}" type="text"/></td>
							      <td><input name="tradeStopLossLiteral" value="${mainUIParam.tradeStopLossLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>Instant trade stoploss</td>
							      <td><input name="instantTradeStoploss" value="${mainUIParam.instantTradeStoploss}" type="text"/></td>
							      <td><input name="instantTradeStoplossTo" value="${mainUIParam.instantTradeStoplossTo}" type="text"/></td>
							      <td><input name="instantTradeStoplossLiteral" value="${mainUIParam.instantTradeStoplossLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>ITS counter</td>
							      <td><input name="itsCounter" value="${mainUIParam.itsCounter}" type="text"/></td>
							      <td><input name="itsCounterTo" value="${mainUIParam.itsCounterTo}" type="text"/></td>
							      <td><input name="itsCounterLiteral" value="${mainUIParam.itsCounterLiteral}" type="text"/></td>
							   </tr>							   
						   		<c:forEach items="${mainUIParam.brokenDateList}" var="brokenDate" varStatus="i">
						            <c:if test="${ i.index == 0 }" >
					       			   <tr class="brokendate">
									      <td>Data between</td>
									      <td><input id="dateFrom" class="datepicker" value="${brokenDate.from}" type="text"/></td>
									      <td><input id="dateTo" class="datepicker" value="${brokenDate.to}" type="text"/></td>
									      <td><input type="button" onclick="addDateClick();" value="Add More"/></td>
									   </tr>     	 
									</c:if>
									<c:if test="${ i.index > 0 }" >
										<tr class='brokendate'>
							      			<td></td>
							      			<td><input value="${brokenDate.from}" type="text" readonly /></td>
							      			<td><input value="${brokenDate.to}" type="text" readonly/></td>
							      			<td><input type="button" onclick="deleteDateClick(this);" value="Delete"/></td>
							   			</tr>   	 
									</c:if> 
								</c:forEach>
							   <tr style="height:10px"><td colspan="4"></td></tr>
							   <tr>
							      <td>Market Start Time</td>
							      <td><input name="marketStartTime" value="${mainUIParam.marketStartTime}" class="timepicker" type="text"/></td>
							      <td><input type="button" onclick="oldHKT();" value="oldHKT"/>&nbsp;&nbsp;<input type="button" onclick="newHKT();" value="newHKT"/></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Lunch Time</td>
							      <td><input name="lunchStartTimeFrom" value="${mainUIParam.lunchStartTimeFrom}" class="timepicker" type="text"/></td>
							      <td><input name="lunchStartTimeTo" value="${mainUIParam.lunchStartTimeTo}" class="timepicker" type="text"/></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Market End Time</td>
							      <td><input name="marketCloseTime" value="${mainUIParam.marketCloseTime}" class="timepicker" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr style="height:10px"><td colspan="4"></td></tr>
							   <tr>
							      <td>Cash per index point</td>
							      <td><input name="cashPerIndexPoint" value="${mainUIParam.cashPerIndexPoint}" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Trading Fee</td>
							      <td><input name="tradingFee" value="${mainUIParam.tradingFee}" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Other cost per trade</td>
							      <td><input name="otherCostPerTrade" value="${mainUIParam.otherCostPerTrade}" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
						          <td style="text-align:right">Unit</td>
							      <td><input name="unit" value="${mainUIParam.unit}" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
   							   <tr>
						          <td style="text-align:right">Order Ticker</td>
							      <td><input name="orderTicker" value="${mainUIParam.orderTicker}" type="text"/></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr style="height:10px"><td colspan="4"></td></tr>
							   <tr>
							      <td>LastMinClrPos</td>
							      <td><input name="lastNumberOfMinutesClearPosition" value="${mainUIParam.lastNumberOfMinutesClearPosition}" type="text"/></td>
							      <td style="text-align:right">LunchLastMinClrPos</td>
							      <td><input name="lunchLastNumberOfMinutesClearPosition" value="${mainUIParam.lunchLastNumberOfMinutesClearPosition}" type="text"/></td>
							   </tr>
							   <tr style="height:10px"><td colspan="4"></td></tr>
							   <tr>
							      <td>Pnl Threshold</td>
							      <td><input name="pnlThreshold" value="${mainUIParam.pnlThreshold}" type="text"/></td>
							      <td></td>
							   </tr>							   
							   <tr>
							      <td>Output char</td>
							      <td><input name="outputChart" type="checkbox" ${mainUIParam.outputChart ? 'checked' : ''} /></td>
							      <td></td>
							      <td></td>
							   </tr>	   
   							   <tr>
							      <td>Include Morning Data</td>
							      <td><input name="includeMorningData" type="checkbox" ${mainUIParam.includeMorningData ? 'checked' : ''} /></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Include Last Market Day Data</td>
							      <td><input name="includeLastMarketDayData" type="checkbox" ${mainUIParam.includeLastMarketDayData ? 'checked' : ''} /></td>
							      <td></td>
							      <td></td>
							   </tr>							   
							   <tr>
							      <td>Ignore Lunch Time</td>
							      <td><input name="ignoreLunchTime" type="checkbox" ${mainUIParam.ignoreLunchTime ? 'checked' : ''} /></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>n For Pnl(separate by comman, e.g. 2,4,6)</td>
							      <td><input name="nForPnl" type="text" value="${mainUIParam.nForPnl}" /></td>
							      <td></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td>Output Matrix File</td>
							      <td><input name="matrixFile" type="checkbox" ${mainUIParam.matrixFile ? 'checked' : ''} /></td>
							      <td></td>
							   </tr>
							   <tr>
							      <td><input type="button" onclick="hkClick();" style="width:100%" value="HK"/></td>
							      <td><input type="button" onclick="km1Click();" style="width:100%" value="KM1"/></td>
							      <td><input type="button" onclick="nk1Click();" style="width:100%" value="NK1"/></td>
							      <td>
							        <table>
							        	<tr>
							        		<td><input type="button" onclick="runClick();" style="width:100%" value="Run Test"/></td>
							        		<td><input type="button" onclick="runWithLiveTradingDataClick();" style="width:100%" value="Set BT Parameters for Live Tading"/></td>
							        		<td><input type="button" onclick="calCombinationClick();" style="width:100%" value="Combination Count"/></td>
							        	</tr>
							        </table>
						      	  </td>
							   </tr>
   							   <tr>
							      <td><input type="button" onclick="saveCombinationClick();" style="width:100%" value="Save Combination"/></td>
							      <td>
							      	<form id='combinationfm' method='post' action='runByUploadCombination' enctype='multipart/form-data'>
	      						        <table>
								        	<tr>
								        		<td><input type="file" name="combination" accept="text/txt" value="Select Combination"/></td>
								        		<td><input type="button" onclick="runByUploadCombination()" value="Run by Upload Combination"/></td>
								        	</tr>
								        </table>																				
									</form>
								  </td>							      							     
							   </tr>
							</table>
						</td>						
					</tr>
				</table>  
            </div>  
            <div class="dom">  
   				<div style="text-align:center;color:blue">************Important Notice************</div>
				<div style="text-align:center;color:blue">Below operation may have the chance to cause TWS open position issue</div>
				<div style="text-align:center;color:red">1. Input a Contract End time on Connection Tab is before Market End Time of any your strategy</div>
				<div style="text-align:center;color:red">2. Add or Delete strategy during Market Time</div>
				<div style="text-align:center;color:red">3. Active/Inactive strategy during Market Time</div>
				<div style="text-align:center;color:red">4. Upload strategy through CSV during Market Time</div>
				<div style="text-align:center;color:red">5. TWS disconnect our system connection unexpectedly, we have build the logic to reconnect to TWS, but this part logic have never been tested, since we did not encounter this case before</div>
				<div style="text-align:center;color:blue">Suggestion: Please do the Add/Delete/Active/Inactive/Upload strategy before Market Time or within Lunch Time</div>
				<br/>
				<div style="text-align:center;color:red">************Please read and accept below when you want to do Live Trading************</div>
				<div style="text-align:center;color:blue">Please follow the normal trading flow which you have fully tested during Paper Trading.</div>
				<div style="text-align:center;color:blue">The system won't forbid you to do any complicate flow/operation, but you need to first fully test that flow/operation are working and within your expectation on Paper Trading.</div>
				<div style="text-align:center;color:blue">Please just use the buttons/functions which you have already test/use on Paper Trading before, </div>
				<div style="text-align:center;color:blue">once you use those buttons/functions, it means you have accept and notice that those buttons/functions are fully tested and passed by you.</div>
				<div style="text-align:center;color:blue">And more, once you are doing the live trading, it means you have accept and trust the system, and the programmer won't response for the final Live Trading result.</div>
				<div style="text-align:center;color:red">But programmer will take responsibility to fix any problem occur/found during Paper Trading.</div>
  				<table>
				   <tr>
				      <td>Host</td>
				      <td><input name="host" value="${connectionInfo.host}" type="text"/></td>
				   </tr>
	   			   <tr>
				      <td>Port</td>
				      <td><input name="port" value="${connectionInfo.port}" type="text"/></td>
				   </tr>
	   			   <tr>
				      <td>Client ID</td>
				      <td><input name="clientId" value="${connectionInfo.clientId}" type="text"/></td>
				   </tr>
   	   			   <tr>
				      <td>Account</td>
				      <td><input name="account" value="${connectionInfo.account}" type="text"/></td>
				   </tr>
				   <tr>
				      <td>PaperTrading?</td>
				      <td>
				      	<input name="isPaperTrading" type="checkbox" ${connectionInfo.paperTrading ? 'checked' : ''} />
				      	<font color="red">Suggest to only select this checkbox during paper trading!!!!</font>
			      	  </td>
				   </tr>
				   <tr>
				      <td>Cancel And Retry If Order Exceed Tolerant Time(5 mins)?</td>
				      <td>
				      	<input name="isCancelAndRetryIfOrderExceedTolerantTime" type="checkbox" ${connectionInfo.cancelAndRetryIfOrderExceedTolerantTime ? 'checked' : ''} />
				      	<font color="red">This flag only take effective when you have checked above 'PaperTrading?' checkbox </font>
			      	  </td>
				   </tr>
   	   			   <tr>
				      <td></td>
				      <td>
				      	<input name="defaultConnect" onclick="defaultConnectClick();" value="Default Connection" type="button"/>
				      	<input name="connect" onclick="connectClick();" value="Connect" type="button"/>
				      	<input name="disconnect" onclick="disconnectClick();" value="Disconnect" type="button"/>
				      	<input name="refresh" onclick="startRefresh();" value="Refresh Data" type="button"/>
				      	<input name="stoprefresh" onclick="stopRefresh();" value="Stop Refresh" type="button"/>
				      	<label name="status">Offline</label> &nbsp;&nbsp;&nbsp; 
				      	<label id="incomingDataInfo" style="text-align:center"></label>
				      </td>
				   </tr>
  				</table>
  				<hr/>
  				<input type="hidden" name="tif" value="${contract.tif}"/>
				<table>
					<tr>
				      <td>Sec Type</td>
				      <td><input name="secType" value="${contract.secType}" type="text"/></td>
				   </tr>
				   	<tr>
				      <td>Symbol</td>
				      <td><input name="symbol" value="${contract.symbol}" type="text"/></td>
				   </tr>
				   	<tr>
				      <td>Currency</td>
				      <td><input name="currency" value="${contract.currency}" type="text"/></td>
				   </tr>
				   	<tr>
				      <td>Exchange</td>
				      <td><input name="exchange" value="${contract.exchange}" type="text"/></td>
				   </tr>
				   	<tr>
				      <td>Local symbol</td>
				      <td><input name="localSymbol" value="${contract.localSymbol}" type="text"/></td>
				   </tr>
				   	<tr>
				      <td>Expirary</td>
				      <td><input disabled="disabled" name="expirary" value="${contract.expirary}" type="text"/><label style="color:red">After you click the Search button, it will auto generated by system base on the expiry dates setting, no need user input</label></td>
				   </tr>
   			   		<tr>
				      <td>Start Time</td>
				      <td><input class="datetimepicker" name="startTime" value="${contract.startTime}" type="text"/></td>
				   </tr>
	   			   	<tr>
				      <td>End Time</td>
				      <td><input class="datetimepicker" name="endTime" value="${contract.endTime}" type="text"/></td>
				   </tr>
				   <tr>
				   	  <td colspan="2">
				   	  	<table>
				   	  		<tr>
						      <td><input type="button" onclick="contractHHIClick();" style="width:100%" value="HHI"/></td>
						      <td><input type="button" onclick="contractKM1Click();" style="width:100%" value="KM1"/></td>
						      <td><input type="button" onclick="contractNK1Click();" style="width:100%" value="NK1"/></td>
						      <td><input type="button" onclick="contractHSIClick();" style="width:100%" value="HSI"/></td>
						      <td><input type="button" id="searchBtn" onclick="searchClick();" style="width:100%" value="Search" /></td>
				   	  		</tr>
				   	  	</table>
				   	  </td>
				   </tr>
  				</table>
  				<hr/>
  				<table>
  					<tr>
						<td style="width:10%;">
							<div id="livefiles">			   
							</div>
						</td>
					</tr>
				</table>  
            </div>   
			<div class="dom">
				<table id="marketDataTable" border="1" cellspacing="0" cellpadding="0" width="100%">
					<tr><td>Description</td><td>Bid</td><td>Bid Size</td><td>Ask</td><td>Ask Size</td><td>Trade</td><td>Trade Size</td><td>Time</td><td>Change</td></tr>
					<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
				</table>
				<br/><br/>
				<table id="counterTable" border="1" cellspacing="0" cellpadding="0" width="100%">
					<tr><td>Type</td><td>0s</td><td>1s</td><td>2s</td><td>3s</td><td>4s</td></tr>
					<tr><td>Trade</td><td></td><td></td><td></td><td></td><td></td></tr>
					<tr><td>Ask</td><td></td><td></td><td></td><td></td><td></td></tr>
					<tr><td>Bid</td><td></td><td></td><td></td><td></td><td></td></tr>
				</table>
			</div>
			<div class="dom">
				<table id="orderDataTable" border="1" cellspacing="0" cellpadding="0" width="100%">					
				</table>
				<div id="tradeLog"></div>
			</div>
			<div class="dom">
				<table id="controlDataTable" border="1" cellspacing="0" cellpadding="0" width="100%">					
				</table>
				<table>
					<tr>
						<td><input type="button" onclick="inactiveAllStrategy();" value="Stop All"/></td>
						<td>&nbsp;&nbsp;</td>
						<td><input type="button" onclick="activeAllStrategy();" value="Start All"/></td>
						<td>&nbsp;&nbsp;</td>
						<td><input type="button" onclick="deleteAllStrategy();" value="Delete All"/></td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td><a href="saveAllStrategy"><input type="button" value="Save As CSV"/></a></td>
						<td>&nbsp;&nbsp;</td>
						<td>
							<form id='templatefm' method='post' action='loadTemplate' enctype='multipart/form-data'>
								<input type="file" name="template" accept="text/txt" value="Select Template"/>
								<input type="button" onclick="loadTemplate()" value="Load"/>
							</form>
						</td>
					</tr>					
				</table>
			</div>
			<div class="dom">
				<div style="text-align: center;color: blue;padding-top: 10px;padding-bottom: 10px;">
					Select the strategy to view details: 
					<select id="strategySelect">
						<c:forEach items="${strategies}" var="strategy" varStatus="i"> 
		        			<option value="${ strategy.strategyName }">${ strategy.strategyName }</option>
		        		</c:forEach>
        			</select>
        		</div>            
        		<c:forEach items="${strategies}" var="strategy" varStatus="i">
            	<div class="strategyName" strategyName="${strategy.strategyName}">  
					<table style="width:100%">
						<tr>
							<td style="width:40%;text-align:start;vertical-align:baseline;">
								<table>
								   <tr>
	   							 	  <c:if test="${strategy.mainUIParam.includeLastMarketDayData}" >
									  	<c:if test="${strategy.mainUIParam.fromSource}" ><td>Source:</td><td>${strategy.mainUIParam.source}</td></c:if>
										<c:if test="${not strategy.mainUIParam.fromSource}" ><td>Ticker:</td><td>${strategy.mainUIParam.ticker}</td></c:if>
									  </c:if>								      								      
								      <td>
								      	<input type="button" name="statusBtn" onclick="chageStrategyStatus('${strategy.strategyName}')" value="${strategy.active ? 'Stop' : 'Start'}"/>
								      	<input type="button" onclick="deleteStrategy('${strategy.strategyName}')" value="Delete"/>
								      </td>
								      <td><div style="color:red" name="strategyStatus">${strategy.active ? 'Running' : 'Pending'}</div></td>
								   </tr>	   
							   	   <tr>
								   		<td>
								   	  		<table>
											   <tr>
											      <td>&nbsp;</td>
											   </tr>
											   <tr>
											      <td>Trade</td>
											   </tr>
											   <tr>
											      <td>Ask</td>
											   </tr>
											   <tr>
											      <td>Bid</td>
											   </tr>
								   	  		</table>
								   		</td>
								   		<td>
								   	  		<table>
											   <tr>
											      <td>Open</td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.tradeDataField eq "tradeopen" ? "checked" : "" } value="tradeopen" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.askDataField eq "askopen" ? "checked" : "" } value="askopen" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.bidDataField eq "bidopen" ? "checked" : "" } value="bidopen" type="radio"/></td>
											   </tr>
								   	  		</table>
								   	  	</td>
								   	  	<td>
								   	  		<table>
											   <tr>
											      <td>Avg</td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.tradeDataField eq "tradeavg" ? "checked" : "" } value="tradeavg" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.askDataField eq "askavg" ? "checked" : "" } value="askavg" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.bidDataField eq "bidavg" ? "checked" : "" } value="bidavg" type="radio"/></td>
											   </tr>
								   	  		</table>
								   	  	</td>
								   	  	<td>
								   	  		<table>
											   <tr>
											      <td>Last</td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.tradeDataField eq "tradelast" ? "checked" : "" } value="tradelast" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.askDataField eq "asklast" ? "checked" : "" } value="asklast" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.bidDataField eq "bidlast" ? "checked" : "" } value="bidlast" type="radio"/></td>
											   </tr>
								   	  		</table>
								   	  	</td>
								   	  	<td>
								   	  		<table>
											   <tr>
											      <td>Max</td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.tradeDataField eq "trademax" ? "checked" : "" } value="trademax" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.askDataField eq "askmax" ? "checked" : "" } value="askmax" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.bidDataField eq "bidmax" ? "checked" : "" } value="bidmax" type="radio"/></td>
											   </tr>
								   	  		</table>
								   	  	</td>
								   	  	<td>
								   	  		<table>
											   <tr>
											      <td>Min</td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.tradeDataField eq "trademin" ? "checked" : "" } value="trademin" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.askDataField eq "askmin" ? "checked" : "" } value="askmin" type="radio"/></td>
											   </tr>
											   <tr>
											      <td><input ${ strategy.mainUIParam.bidDataField eq "bidmin" ? "checked" : "" } value="bidmin" type="radio"/></td>
											   </tr>
								   	  		</table>
								   	  	</td>
								   </tr>
								   <tr>
								      <td></td>
								      <td>From</td>
								      <td>To</td>
								      <td>Literal</td>
								   </tr>
   								   <tr>
								      <td>Average Step Size</td>
								      <td><input value="${strategy.mainUIParam.avgStep}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>T-Short</td>
								      <td><input value="${strategy.mainUIParam.tShort}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>T-Long</td>
								      <td><input value="${strategy.mainUIParam.tLong}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>								   
								   <tr>
								      <td>MA-S</td>
								      <td><input value="${strategy.mainUIParam.mas}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
   								   <tr>
								      <td>MA-L</td>
								      <td><input value="${strategy.mainUIParam.mal}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
  								   <tr>
								      <td>MA-T</td>
								      <td><input value="${strategy.mainUIParam.mat}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>								   
								   <tr>
								      <td>stoploss</td>
								      <td><input value="${strategy.mainUIParam.stopLoss}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>tradestoploss</td>
								      <td><input value="${strategy.mainUIParam.tradeStopLoss}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Instant trade stoploss</td>
								      <td><input value="${strategy.mainUIParam.instantTradeStoploss}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>ITS counter</td>
								      <td><input value="${strategy.mainUIParam.itsCounter}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr style="height:10px"><td colspan="4"></td></tr>
								   <tr>
								      <td>Market Start Time</td>
								      <td><input value="${strategy.mainUIParam.marketStartTime}" class="timepicker" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Lunch Time</td>
								      <td><input value="${strategy.mainUIParam.lunchStartTimeFrom}" class="timepicker" type="text"/></td>
								      <td><input value="${strategy.mainUIParam.lunchStartTimeTo}" class="timepicker" type="text"/></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Market End Time</td>
								      <td><input value="${strategy.mainUIParam.marketCloseTime}" class="timepicker" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr style="height:10px"><td colspan="4"></td></tr>
								   <tr>
								      <td>Cash per index point</td>
								      <td><input value="${strategy.mainUIParam.cashPerIndexPoint}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Trading Fee</td>
								      <td><input value="${strategy.mainUIParam.tradingFee}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Other cost per trade</td>
								      <td><input value="${strategy.mainUIParam.otherCostPerTrade}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
							          <td style="text-align:right">Unit</td>
								      <td><input value="${strategy.mainUIParam.unit}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
   								   <tr>
							          <td style="text-align:right">Order Ticket</td>
								      <td><input value="${strategy.mainUIParam.orderTicker}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr style="height:10px"><td colspan="4"></td></tr>
								   <tr>
								      <td>LastMinClrPos</td>
								      <td><input value="${strategy.mainUIParam.lastNumberOfMinutesClearPosition}" type="text"/></td>
								      <td style="text-align:right">LunchLastMinClrPos</td>
								      <td><input value="${strategy.mainUIParam.lunchLastNumberOfMinutesClearPosition}" type="text"/></td>
								   </tr>
								   <tr style="height:10px"><td colspan="4"></td></tr>
								   <tr>
								      <td>Pnl Threshold</td>
								      <td><input value="${strategy.mainUIParam.pnlThreshold}" type="text"/></td>
								      <td></td>
								   </tr>								   
      							   <tr>
								      <td>Include Morning Data</td>
								      <td><input type="checkbox" ${strategy.mainUIParam.includeMorningData ? 'checked' : ''} /></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Include Last Market Day Data</td>
								      <td><input type="checkbox" ${strategy.mainUIParam.includeLastMarketDayData ? 'checked' : ''} /></td>
								      <td></td>
								   </tr>								   
								   <tr>
								      <td>Ignore Lunch Time</td>
								      <td><input type="checkbox" ${strategy.mainUIParam.ignoreLunchTime ? 'checked' : ''} /></td>
								      <td></td>
								   </tr>
								</table>
							</td>
						</tr>
					</table>  
           		</div> 
	        	</c:forEach>
        	</div>
			<div class="dom">
				<form id="expiryDateForm" method="post"  action="uploadExpiryDate" enctype="multipart/form-data">
					Input a date: <input class="datepicker" id="expiryDate" value="" type="text"/> Or Upload a CSV: <input name="expiryDateFile" type="file"/>
					<input onclick="addExpiryDate()" type="button" value="Add as Expiry Date"/>&nbsp;&nbsp;<label style="color:red">Expiry date should be the last market day of every month</label>
				</form>				
				<table id="expiryDates">					
	        	</table>
			</div>
			<div class="dom">
				<form id="scheduleDateForm" method="post"  action="uploadScheduleDate" enctype="multipart/form-data">
					Input a date: <input class="datepicker" id="scheduleDate" value="" type="text"/> Or Upload a CSV: <input name="scheduleDateFile" type="file"/>
					<input onclick="addScheduleDate()" type="button" value="Add as Schedule Date"/>&nbsp;&nbsp;
				</form>
				<table id="scheduleDates">					
	        	</table>
			</div>
			<div class="dom">				
				<table>
					<tr>
						<td style="width:80%;">
							<div id="status" style="border:2px solid black;width:100%;height: 900px;margin: 0;padding: 0;overflow-y: scroll" ></div>
						</td>
						<td style="width:20%;">
							<ul id="files" style="border:2px solid black;width:100%;height: 900px;margin: 0;padding: 0;overflow-y: scroll">			   
							</ul>
						</td>
					</tr>
				</table> 
			</div>
        </div>  
    </div>  
</body>
</html>