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
<script src="<%=request.getContextPath()%>/resources/script/jquery.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.full.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/mmnt.js"></script>
<script type="text/javascript">
	function hkClick() {
		$("[name='marketStartTime']").val("09:15:00");
		$("[name='lunchStartTimeFrom']").val("12:00:00");
		$("[name='lunchStartTimeTo']").val("13:00:00");
		$("[name='marketCloseTime']").val("16:15:00");
		$("[name='cashPerIndexPoint']").val("50");
		$("[name='tradingFee']").val("18");
		$("[name='otherCostPerTrade']").val("0");
		$("[name='unit']").val("1");
	}
	function km1Click() {
		$("[name='marketStartTime']").val("08:00:00");
		$("[name='lunchStartTimeFrom']").val("14:00:00");
		$("[name='lunchStartTimeTo']").val("23:00:00");
		$("[name='marketCloseTime']").val("23:15:00");
		$("[name='cashPerIndexPoint']").val("500000");
		$("[name='tradingFee']").val("5000");
		$("[name='otherCostPerTrade']").val("0");
		$("[name='unit']").val("0.05");
	}
	function nk1Click() {
		$("[name='marketStartTime']").val("08:00:00");
		$("[name='lunchStartTimeFrom']").val("14:00:00");
		$("[name='lunchStartTimeTo']").val("23:00:00");
		$("[name='marketCloseTime']").val("23:15:00");
		$("[name='cashPerIndexPoint']").val("1000");
		$("[name='tradingFee']").val("400");
		$("[name='otherCostPerTrade']").val("0");
		$("[name='unit']").val("5");
	}
	function addDateClick() {
		var from = $.trim($("#dateFrom").val());
		var to = $.trim($("#dateTo").val());
		if (from != '' && to != '')
			$(".brokendate:last").after(
				"<tr class='brokendate'>" +
			      "<td></td>" +
			      "<td><input value='" + from + "' type='text' readonly /></td>" +
			      "<td><input value='" + to + "' type='text' readonly/></td>" +
			      "<td><input type='button' onclick='deleteDateClick(this);' value='Delete'/></td>" +
			   "</tr>"
			);		
	}
	function deleteDateClick(_this) {
		$(_this).parent().parent().remove();
	}
	function runClick() {
		var brokendates = $(".brokendate");
		var brokendateArr = [];
		for(var i = 0; i < brokendates.length; i++) {			
			var from = $.trim(brokendates.eq(i).find("input").eq(0).val());
			var to = $.trim(brokendates.eq(i).find("input").eq(1).val());			
			if (from != '' && to != '') {
				brokendateArr.push(   	      
					{  
	    	         "from":from,
	    	         "to":to
	    	      	}
				);
			}
		}
		var paramData = {
    	   "tLong":$.trim($("[name='tLong']").val()),
    	   "tShort":$.trim($("[name='tShort']").val()),
    	   "hld":$.trim($("[name='hld']").val()),
    	   "stopLoss":$.trim($("[name='stopLoss']").val()),
    	   "tradeStopLoss":$.trim($("[name='tradeStopLoss']").val()),
    	   "instantTradeStoploss":$.trim($("[name='instantTradeStoploss']").val()),
    	   "itsCounter":$.trim($("[name='itsCounter']").val()),    	       	  
    	   "unit":$.trim($("[name='unit']").val()),
    	   "marketStartTime":$.trim($("[name='marketStartTime']").val()),
    	   "lunchStartTimeFrom":$.trim($("[name='lunchStartTimeFrom']").val()),
    	   "lunchStartTimeTo":$.trim($("[name='lunchStartTimeTo']").val()),
    	   "marketCloseTime":$.trim($("[name='marketCloseTime']").val()),
    	   "cashPerIndexPoint":$.trim($("[name='cashPerIndexPoint']").val()),
    	   "tradingFee":$.trim($("[name='tradingFee']").val()),
    	   "otherCostPerTrade":$.trim($("[name='otherCostPerTrade']").val()),
    	   "lastNumberOfMinutesClearPosition":$.trim($("[name='lastNumberOfMinutesClearPosition']").val()),
    	   "lunchLastNumberOfMinutesClearPosition":$.trim($("[name='lunchLastNumberOfMinutesClearPosition']").val()),
    	   "source":$.trim($("[name='source']").val()),
    	   "tShortTo":$.trim($("[name='tShortTo']").val()),
    	   "tShortLiteral":$.trim($("[name='tShortLiteral']").val()),
    	   "tLongTo":$.trim($("[name='tLongTo']").val()),
    	   "tLongLiteral":$.trim($("[name='tLongLiteral']").val()),    	   
    	   "hldTo":$.trim($("[name='hldTo']").val()),
    	   "hldLiteral":$.trim($("[name='hldLiteral']").val()),
    	   "stopLossTo":$.trim($("[name='stopLossTo']").val()),
    	   "stopLossLiteral":$.trim($("[name='stopLossLiteral']").val()),
    	   "tradeStopLossTo":$.trim($("[name='tradeStopLossTo']").val()),
    	   "tradeStopLossLiteral":$.trim($("[name='tradeStopLossLiteral']").val()),
    	   "instantTradeStoplossTo":$.trim($("[name='instantTradeStoplossTo']").val()),
    	   "instantTradeStoplossLiteral":$.trim($("[name='instantTradeStoplossLiteral']").val()),
    	   "itsCounterTo":$.trim($("[name='itsCounterTo']").val()),
    	   "itsCounterLiteral":$.trim($("[name='itsCounterLiteral']").val()),    	   
    	   "outputChart":$("[name='outputChart']").is(":checked"),
    	   "tradeDataField":$.trim($("[name='tradeDataField']:checked").val()),
    	   "askDataField":$.trim($("[name='askDataField']:checked").val()),
    	   "bidDataField":$.trim($("[name='bidDataField']:checked").val()),
    	   "brokenDateList":brokendateArr
    	};
		$.ajax({
		    type: "POST",
		    url: "",
		    data: JSON.stringify(paramData),
		    contentType:"application/json;charset=utf-8",
		    success: function(data) {
		    	$("#status").html("");
		    	updateStatus();
		    	renderDownFileList();
		    },
		    error: function() {		        
		    }
		});
	}
	
	var taskCompletedStr = "Task Completed!";
	function updateStatus() {
		if($("#status").html().indexOf(taskCompletedStr) == -1) {
			$.ajax({
			    type: "GET",
			    url: "status",
			    contentType:"application/json;charset=utf-8",
			    success: function(data) {
			    	$("#status").html(data);		    	 
			        $("#status")[0].scrollTop = 800 + $("#status")[0].scrollHeight;
			        if (!taskCompleted && data.indexOf(taskCompletedStr) != -1) {
			        	taskCompleted = true;
			        	renderDownFileList();
			        }
			    },
			    error: function() {
			    }
			});			
		}
	}
	
	function start(id) {
		$.ajax({
		    type: "GET",
		    url: "start?id=" + id,
		    success: function(data) {
		    	$("#status").html("");
		    	updateStatus();
		    	if(data) {
			    	$("#" + id).text(id + "(Running)");
		    	}
		    },
		    error: function() {
		    }
		});
	}
	
	function stop(id) {
		$.ajax({
		    type: "GET",
		    url: "stop?id=" + id,
		    success: function(data) {
		    	updateStatus();
		    	if(data) {
			    	$("#" + id).text(id + "(Paused)");
		    	}
		    },
		    error: function() {
		    }
		});
	}
	
	function deleteItem(id) {
		$.ajax({
		    type: "GET",
		    url: "delete?id=" + id,
		    success: function(data) {
		    	renderDownFileList();
		    },
		    error: function() {
		    }
		});
	}
	
	function renderDownFileList() {
		$.ajax({
		    type: "GET",
		    url: "list",
		    contentType:"application/json;charset=utf-8",
		    success: function(data) {
		    	var lis = "";
		    	for(var i = 0; i < data.length; i++) {
		    		var datas = data[i].split(",");
		    		lis += "<li style='margin-bottom:5px'><a id='" + datas[0] + "' href='download?id=" + datas[0] + "'>" + datas[0] + datas[1] + "</a>" +
		    				"<br/><input onclick='start(\"" + datas[0] + "\")' type='button' value='Start'/>" +
		    				"<input onclick='stop(\"" + datas[0] + "\")' type='button' value='Stop'/>" +
		    				"<input onclick='deleteItem(\"" + datas[0] + "\")' type='button' value='Delete'/>"
		    				+ "</li>"
		    	}
		    	$("#files").html(lis);
		    },
		    error: function() {
		    }
		});
	}
	
	var taskCompleted = false;
	$(function() {
		$('.timepicker').TimePickerAlone();
		jQuery('.datepicker').datetimepicker({
			 timepicker:false,			 
			 format:'Y-m-d'
		});
		updateStatus();
		renderDownFileList();
		setInterval(updateStatus, 1000);
	});
</script>
</head>
<body>
<input type="hidden" value="${ mainUIParam.sourcePath }">
<table style="width:100%">
	<tr>
		<td style="width:40%;text-align:start;vertical-align:baseline;">
			<table>
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
			      <td>HLD</td>
			      <td><input name="hld" value="${mainUIParam.hld}" type="text"/></td>
			      <td><input name="hldTo" value="${mainUIParam.hldTo}" type="text"/></td>
			      <td><input name="hldLiteral" value="${mainUIParam.hldLiteral}" type="text"/></td>
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
</body>
</html>