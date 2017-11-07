<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>BackTest</title>
<script type="text/javascript">
</script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/script/mmnt.css"/>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/index.css"/>
<script src="<%=request.getContextPath()%>/resources/script/jquery.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.full.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/mmnt.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/index.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.form.min.js"></script>
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
    			<li tab="uploadDataTab">Upload Data</li>
                <c:forEach items="${strategies}" var="strategy" varStatus="i"> 
        			<li tab="strategyTab">${ strategy.strategyName }</li>
        		</c:forEach>
            </ul>  
        </div>  
        <div id="tab-content">  
            <div class="dom" style="display: block;">  
				<table style="width:100%">
					<tr>
						<td style="width:40%;text-align:start;vertical-align:baseline;">
							<table>
							   <tr>
							      <td>Strategy</td>
							      <td>
							         <input name="strategyName" />	         
							      </td>
							      <td><input type="button" onclick="addStrategy(this);" value="Add Strategy"/></td>
							      <td></td>
							   </tr>	   
							   <tr>
							      <td>Source</td>
							      <td>
							         <select name="source">
								         <c:forEach items="${sources}" var="source" varStatus="i">
								            <option ${source eq mainUIParam.source ? "selected" : ""}>${source}</option>
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
							      <td>CP timer</td>
							      <td><input name="cpTimer" value="${mainUIParam.cpTimer}" type="text"/></td>
							      <td><input name="cpTimerTo" value="${mainUIParam.cpTimerTo}" type="text"/></td>
							      <td><input name="cpTimerLiteral" value="${mainUIParam.cpTimerLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>CP Buffer</td>
							      <td><input name="cpBuffer" value="${mainUIParam.cpBuffer}" type="text"/></td>
							      <td><input name="cpBufferTo" value="${mainUIParam.cpBufferTo}" type="text"/></td>
							      <td><input name="cpBufferLiteral" value="${mainUIParam.cpBufferLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>CP Hit Rate</td>
							      <td><input name="cpHitRate" value="${mainUIParam.cpHitRate}" type="text"/></td>
							      <td><input name="cpHitRateTo" value="${mainUIParam.cpHitRateTo}" type="text"/></td>
							      <td><input name="cpHitRateLiteral" value="${mainUIParam.cpHitRateLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>CP smooth</td>
							      <td><input name="cpSmooth" value="${mainUIParam.cpSmooth}" type="text"/></td>
							      <td><input name="cpSmoothTo" value="${mainUIParam.cpSmoothTo}" type="text"/></td>
							      <td><input name="cpSmoothLiteral" value="${mainUIParam.cpSmoothLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>estimation buffer</td>
							      <td><input name="estimationBuffer" value="${mainUIParam.estimationBuffer}" type="text"/></td>
							      <td><input name="estimationBufferTo" value="${mainUIParam.estimationBufferTo}" type="text"/></td>
							      <td><input name="estimationBufferLiteral" value="${mainUIParam.estimationBufferLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>action trigger</td>
							      <td><input name="actionTrigger" value="${mainUIParam.actionTrigger}" type="text"/></td>
							      <td><input name="actionTriggerTo" value="${mainUIParam.actionTriggerTo}" type="text"/></td>
							      <td><input name="actionTriggerLiteral" value="${mainUIParam.actionTriggerLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>action counting</td>
							      <td><input name="actionCounting" value="${mainUIParam.actionCounting}" type="text"/></td>
							      <td><input name="actionCountingTo" value="${mainUIParam.actionCountingTo}" type="text"/></td>
							      <td><input name="actionCountingLiteral" value="${mainUIParam.actionCountingLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>% trade stoploss trigger</td>
							      <td><input name="tradeStopLossTrigger" value="${mainUIParam.tradeStopLossTrigger}" type="text"/></td>
							      <td><input name="tradeStopLossTriggerTo" value="${mainUIParam.tradeStopLossTriggerTo}" type="text"/></td>
							      <td><input name="tradeStopLossTriggerLiteral" value="${mainUIParam.tradeStopLossTriggerLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>% trade stoploss</td>
							      <td><input name="tradeStopLossTriggerPercent" value="${mainUIParam.tradeStopLossTriggerPercent}" type="text"/></td>
							      <td><input name="tradeStopLossTriggerPercentTo" value="${mainUIParam.tradeStopLossTriggerPercentTo}" type="text"/></td>
							      <td><input name="tradeStopLossTriggerPercentLiteral" value="${mainUIParam.tradeStopLossTriggerPercentLiteral}" type="text"/></td>
							   </tr>
							   <tr>
							      <td>Absolute trade stoploss</td>
							      <td><input name="absoluteTradeStopLoss" value="${mainUIParam.absoluteTradeStopLoss}" type="text"/></td>
							      <td><input name="absoluteTradeStopLossTo" value="${mainUIParam.absoluteTradeStopLossTo}" type="text"/></td>
							      <td><input name="absoluteTradeStopLossLiteral" value="${mainUIParam.absoluteTradeStopLossLiteral}" type="text"/></td>
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
							      <td></td>
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
							      <td><input type="button" onclick="hkClick();" style="width:100%" value="HK"/></td>
							      <td><input type="button" onclick="km1Click();" style="width:100%" value="KM1"/></td>
							      <td><input type="button" onclick="nk1Click();" style="width:100%" value="NK1"/></td>
							      <td><input type="button" onclick="runClick();" style="width:100%" value="Run Test"/></td>
							   </tr>
							</table>
						</td>
						<td style="width:50%;">
							<div id="status" style="border:2px solid black;width:100%;height: 900px;margin: 0;padding: 0;overflow-y: scroll" ></div>
						</td>
						<td style="width:10%;">
							<ul id="files" style="border:2px solid black;width:100%;height: 900px;margin: 0;padding: 0;overflow-y: scroll">			   
							</ul>
						</td>
					</tr>
				</table>  
            </div>  
            <div class="dom">  
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
				      <td></td>
				      <td>
				      	<input name="defaultConnect" onclick="defaultConnectClick();" value="Default Connection" type="button"/>
				      	<input name="connect" onclick="connectClick();" value="Connect" type="button"/>
				      	<input name="disconnect" onclick="disconnectClick();" value="Disconnect" type="button"/>
				      	<input name="refresh" onclick="startRefresh();" value="Refresh Data" type="button"/>
				      	<input name="stoprefresh" onclick="stopRefresh();" value="Stop Refresh" type="button"/>
				      	<label name="status">Offline</label>
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
				      <td><input class="yearmonthpicker" name="expirary" value="${contract.expirary}" type="text"/></td>
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
						      <td><input type="button" onclick="searchClick();" style="width:100%" value="Search" /></td>
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
			</div>
			<div class="dom">
				<table id="orderDataTable" border="1" cellspacing="0" cellpadding="0" width="100%">					
				</table>
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
						<td><a href="saveAllStrategy"><input type="button" value="Save As Template"/></a></td>
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
				<form id='uploadDatafm' method='post' action='uploadData' enctype='multipart/form-data' style="margin-top:10px; margin-bottom:10px;">
					<input type="hidden" name="uploadAction" />
					Start:<input type="text" name="dataStartTime" class="timepicker"/>
					End:<input type="text" name="dataEndTime" class="timepicker"/>
					<input type="file" name="liveData" accept="text/txt" value="Select File"/>
					<input type="button" onclick="uploadDataWithChecking()" value="Check"/>
					<input type="button" onclick="uploadDataWithReplace()" value="Upload by replace"/>
					<input type="button" onclick="uploadDataWithSkip()" value="Upload by skip"/>
				</form>
				<div id="uploadStatus"></div>
            </div>
            <c:forEach items="${strategies}" var="strategy" varStatus="i">
            	<div class="dom" strategyName="${strategy.strategyName}">  
					<table style="width:100%">
						<tr>
							<td style="width:40%;text-align:start;vertical-align:baseline;">
								<table>
								   <tr>
								      <td>Source</td>
								      <td>
								         <select>
									         <c:forEach items="${sources}" var="source" varStatus="i">
									            <option ${source eq strategy.mainUIParam.source ? "selected" : ""}>${source}</option>
									         </c:forEach>
								         </select>	         
								      </td>
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
								      <td>CP timer</td>
								      <td><input name="tShort" value="${strategy.mainUIParam.cpTimer}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>CP Buffer</td>
								      <td><input value="${strategy.mainUIParam.cpBuffer}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>CP Hit Rate</td>
								      <td><input value="${strategy.mainUIParam.cpHitRate}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>CP smooth</td>
								      <td><input value="${strategy.mainUIParam.cpSmooth}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>estimation buffer</td>
								      <td><input value="${strategy.mainUIParam.estimationBuffer}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>action trigger</td>
								      <td><input value="${strategy.mainUIParam.actionTrigger}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>action counting</td>
								      <td><input value="${strategy.mainUIParam.actionCounting}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>% trade stoploss trigger</td>
								      <td><input value="${strategy.mainUIParam.tradeStopLossTrigger}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>% trade stoploss</td>
								      <td><input value="${strategy.mainUIParam.tradeStopLossTriggerPercent}" type="text"/></td>
								      <td></td>
								      <td></td>
								   </tr>
								   <tr>
								      <td>Absolute trade stoploss</td>
								      <td><input value="${strategy.mainUIParam.absoluteTradeStopLoss}" type="text"/></td>
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
								</table>
							</td>
						</tr>
					</table>  
           		</div> 
	        </c:forEach>
        </div>  
    </div>  
</body>
</html>