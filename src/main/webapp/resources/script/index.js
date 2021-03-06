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
function yyyymm() {
	var year = new Date().getYear() + 1900;
	var month = new Date().getMonth() + 1;
	var monthStr = month < 10 ? "0" + month : month;
	return year  + "" + monthStr;
}
function contractHHIClick() {
	$("[name='secType']").val("FUT");
	$("[name='symbol']").val("HHI.HK");
	$("[name='currency']").val("HKD");
	$("[name='exchange']").val("HKFE");
	$("[name='localsymbol']").val("");
	$("[name='expirary']").val(yyyymm());
	$("[name='tif']").val("IOC");
}
function contractKM1Click() {
	$("[name='secType']").val("FUT");
	$("[name='symbol']").val("K200");
	$("[name='currency']").val("KRW");
	$("[name='exchange']").val("KSE");
	$("[name='localsymbol']").val("");
	$("[name='expirary']").val(yyyymm());
	$("[name='tif']").val("MTL");
}
function contractNK1Click() {
	$("[name='secType']").val("FUT");
	$("[name='symbol']").val("N225");
	$("[name='currency']").val("JPY");
	$("[name='exchange']").val("OSE.JPN");
	$("[name='localsymbol']").val("");
	$("[name='expirary']").val(yyyymm());
	$("[name='tif']").val("IOC");
}
function contractHSIClick() {
	$("[name='secType']").val("FUT");
	$("[name='symbol']").val("HSI");
	$("[name='currency']").val("HKD");
	$("[name='exchange']").val("HKFE");
	$("[name='localsymbol']").val("");
	$("[name='expirary']").val(yyyymm());
	$("[name='tif']").val("IOC");
}
function defaultConnectClick() {
	$("[name='host']").val("127.0.0.1");
	$("[name='port']").val("7496");
	$("[name='clientId']").val("1");
	$("[name='account']").val("U8979091");
}
function searchClick() {
	if(!confirm("Are you sure to start a new search, the previous search will be stopped.")) {
		return;
	}
	var secType = $.trim($("[name='secType']").val());
	var symbol = $.trim($("[name='symbol']").val());
	var currency = $.trim($("[name='currency']").val());
	var exchange = $.trim($("[name='exchange']").val());
	var localSymbol = $.trim($("[name='localSymbol']").val());
	var expirary = $.trim($("[name='expirary']").val());
	var startTime = $.trim($("[name='startTime']").val());
	var endTime = $.trim($("[name='endTime']").val());
	var tif = $.trim($("[name='tif']").val());
	$.ajax({
	    type: "POST",
	    url: "search",
	    data: JSON.stringify({
	    	"secType" : secType,
	    	"symbol" : symbol,
	    	"currency" : currency,
	    	"exchange" : exchange,
	    	"localSymbol" : localSymbol,
	    	"expirary" : expirary,
	    	"startTime" : startTime,
	    	"endTime" : endTime,
	    	"tif" : tif
	    }),
	    contentType:"application/json;charset=utf-8",
	    success: function(data) {
	    	if(data == "Success") {
	    		renderLiveDataFileList();
	    	}
	    	alert(data);
	    },
	    error: function() {		        
	    }
	});
}
function connectClick() {
	var host = $.trim($("[name='host']").val());
	var port = $.trim($("[name='port']").val());
	var clientId = $.trim($("[name='clientId']").val());
	var account = $.trim($("[name='account']").val());
	if(host == "" || port == "" || clientId == "" || account == "") {
		alert("Please input all the connection infomation");
		return;
	}
	$.ajax({
	    type: "POST",
	    url: "connect",
	    data: JSON.stringify({
	    	"host" : host,
	    	"port" : port,
	    	"clientId" : clientId,
	    	"account" : account
	    }),
	    contentType:"application/json;charset=utf-8",
	    success: function(data) {
	    	updateConnectStatus();
	    	if(!data) {
	    		alert("Connect failed!!!");
	    	}
	    },
	    error: function() {
	    	alert("Connect failed with error, pls check.");
	    }
	});
}

function disconnectClick() {
	if(!confirm("Are you sure to disconnect the connection.")) {
		return;
	}
	$.ajax({
	    type: "GET",
	    url: "disconnect",
	    success: function(data) {
	    	updateConnectStatus();
	    	if(!data) {
	    		alert("Disonnect failed!!!");
	    	} else {
	    		alert("Disconnect success!!!");
	    	}
	    },
	    error: function() {
	    }
	});
}

function updateConnectStatus() {
	$.ajax({
	    type: "GET",
	    url: "isConnect",
	    success: function(data) {
	    	if(data) {
	    		$("[name='status']").text("Online");
	    		$("[name='status']").css("color", "red");
	    	} else {
	    		$("[name='status']").text("Offline");
	    		$("[name='status']").css("color", "dimgrey");
	    	}
	    },
	    error: function() {
	    }
	});
}

function updateControlTab() {
	$.ajax({
	    type: "GET",
	    url: "getAllStrategy",
	    success: function(data) {
	    	var trs = "<tr>" +
	    			"<td>Pannel Number</td>" +
	    			"<td>Execute</td>" +
	    			"<td>T-Short</td>" +
	    			"<td>T-Long</td>" +
	    			"<td>T-Long2</td>" +
	    			"<td>HLD(Without %)</td>" +
	    			"<td>stoploss</td>" +
	    			"<td>tradestoploss</td>" +
	    			"<td>Instant trade stoploss</td>" +
	    			"<td>ITS counter</td>" +
	    			"<td>Stop gain percent</td>" +
	    			"<td>Stop gain trigger</td>" +
	    			"<td>Trade Count</td>" +
	    			"<td>pnl</td>" +
	    			"</tr>";
	    	for(var i = 0; i < data.length; i++) {	    			    		
	    		trs += "<tr>" +
	    				"<td>" + data[i].strategyName + "</td>" +
						"<td>" + data[i].active + "</td>" +
						"<td>" + data[i].mainUIParam.tShort + "</td>" +						
						"<td>" + data[i].mainUIParam.tLong + "</td>" +
						"<td>" + data[i].mainUIParam.tLong2 + "</td>" +
						"<td>" + data[i].mainUIParam.hld + "</td>" +
						"<td>" + data[i].mainUIParam.stopLoss + "</td>" +
						"<td>" + data[i].mainUIParam.tradeStopLoss + "</td>" +
						"<td>" + data[i].mainUIParam.instantTradeStoploss + "</td>" +
						"<td>" + data[i].mainUIParam.itsCounter + "</td>" +						
						"<td>" + data[i].mainUIParam.stopGainPercent + "</td>" +
						"<td>" + data[i].mainUIParam.stopGainTrigger + "</td>" +						
						"<td>" + data[i].tradeCount + "</td>" +
						"<td>" + data[i].pnl + "</td>" +
						"</tr>";
	    	}
	    	$("#controlDataTable").html(trs);
	    },
	    error: function() {
	    }
	});
}

function updateOrderTab() {
	$.ajax({
	    type: "GET",
	    url: "getAllStrategy",
	    success: function(data) {
	    	var trs = "<tr><td>Time</td><td>Order Id</td><td>Strategy Name</td><td>Action</td><td>Quantity</td></tr>";
	    	for(var i = 0; i < data.length; i++) {	    			    		
	    		trs += "<tr><td>" + data[i].orderTime + "</td><td>" + data[i].orderId + "</td><td>" + data[i].strategyName + "</td><td>" + data[i].action + "</td><td>" + data[i].quantity + "</td></tr>";
	    	}
	    	$("#orderDataTable").html(trs);
	    },
	    error: function() {
	    }
	});
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

function getMainUIParam() {
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
	return {
	   "tLong":$.trim($("[name='tLong']").val()),
	   "tLong2":$.trim($("[name='tLong2']").val()),
	   "tShort":$.trim($("[name='tShort']").val()),
	   "hld":$.trim($("[name='hld']").val()),
	   "stopLoss":$.trim($("[name='stopLoss']").val()),
	   "tradeStopLoss":$.trim($("[name='tradeStopLoss']").val()),
	   "instantTradeStoploss":$.trim($("[name='instantTradeStoploss']").val()),
	   "itsCounter":$.trim($("[name='itsCounter']").val()),
	   "stopGainPercent":$.trim($("[name='stopGainPercent']").val()),
	   "stopGainTrigger":$.trim($("[name='stopGainTrigger']").val()),
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
	   "tLong2To":$.trim($("[name='tLong2To']").val()),
	   "tLong2Literal":$.trim($("[name='tLong2Literal']").val()),
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
	   "stopGainPercentTo":$.trim($("[name='stopGainPercentTo']").val()),
	   "stopGainPercentLiteral":$.trim($("[name='stopGainPercentLiteral']").val()),
	   "stopGainTriggerTo":$.trim($("[name='stopGainTriggerTo']").val()),
	   "stopGainTriggerLiteral":$.trim($("[name='stopGainTriggerLiteral']").val()),
	   "outputChart":$("[name='outputChart']").is(":checked"),
	   "tradeDataField":$.trim($("[name='tradeDataField']:checked").val()),
	   "askDataField":$.trim($("[name='askDataField']:checked").val()),
	   "bidDataField":$.trim($("[name='bidDataField']:checked").val()),
	   "brokenDateList":brokendateArr,
	   "orderTicker":$.trim($("[name='orderTicker']").val())
	};
}

function runClick() {
	var paramData = getMainUIParam();
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

function addStrategy(_this) {
	var paramData = getMainUIParam();
	$.ajax({
	    type: "POST",
	    url: "addStrategy",
	    data: JSON.stringify({
	    	"mainUIParam" : paramData,
	    	"strategyName" : $.trim($("[name='strategyName']").val())
	    }),
	    contentType:"application/json;charset=utf-8",
	    success: function(data) {
	    	window.location.reload();
	    },
	    error: function() {		        
	    }
	});
}

function deleteStrategy(strategyName) {
	$.ajax({
	    type: "POST",
	    url: "deleteStrategy",
	    data: {
	    	"strategyName" : strategyName
	    },
	    success: function(data) {
	    	window.location.reload();
	    },
	    error: function() {		        
	    }
	});
}

function chageStrategyStatus(strategyName) {
	$.ajax({
	    type: "POST",
	    url: "chageStrategyStatus",
	    data: {
	    	"strategyName" : strategyName
	    },
	    success: function(data) {
	    	updateStrategyStatus(strategyName);
	    },
	    error: function() {		        
	    }
	});
}

function inactiveAllStrategy() {
	$.ajax({
	    type: "POST",
	    url: "inactiveAllStrategy",
	    data: {},
	    success: function(data) {
	    	updateControlTab();
	    },
	    error: function() {		        
	    }
	});
}

function deleteAllStrategy() {
	$.ajax({
	    type: "POST",
	    url: "deleteAllStrategy",
	    data: {},
	    success: function(data) {
	    	if(data) {
	    		window.location.reload();
	    	}
	    },
	    error: function() {		        
	    }
	});
}

function activeAllStrategy() {
	$.ajax({
	    type: "POST",
	    url: "activeAllStrategy",
	    data: {},
	    success: function(data) {	
	    	updateControlTab();
	    },
	    error: function() {		        
	    }
	});
}

function updateStrategyStatus(strategyName) {
	$.ajax({
	    type: "POST",
	    url: "strategyStatus",
	    data: {
	    	"strategyName" : strategyName
	    },
	    success: function(data) {
	    	if(data) {
	    		$("div[strategyName='" + strategyName + "'] input[name='statusBtn']").val("Stop");
	    		$("div[strategyName='" + strategyName + "'] div[name='strategyStatus']").html("Running");
	    	} else {
	    		$("div[strategyName='" + strategyName + "'] input[name='statusBtn']").val("Start");
	    		$("div[strategyName='" + strategyName + "'] div[name='strategyStatus']").html("Pending");
	    	}
	    },
	    error: function() {		        
	    }
	});
}

function getMarketData() {
	$.ajax({
	    type: "GET",
	    url: "getMarketData",
	    success: function(data) {	
	    	var datas = data.split("@");
	    	var tds = $("#marketDataTable").find("tr").eq(1).find("td");
	    	for(var i = 0; i < datas.length; i++) {
	    		tds.eq(i).text(datas[i]);
	    	}
	    },
	    error: function() {		        
	    }
	});
}

function loadTemplate() {
		var file = $("#templatefm input[type=file]").val();
		if (file == "" || file.split(".")[file.split(".").length - 1].toLocaleLowerCase() != "txt") {
			alert("Please select txt file to upload.");
			return;
		}
		var options = {
	        success: function (data) {
	        	if(!data){
	        		alert("You are uploading not validate formate template..");
	        	} else {
	        		window.location.reload();	        		
	        	}
	        }
	   };
	   
	   $("#templatefm").ajaxForm(options);
	   $("#templatefm").ajaxSubmit(options);   
}

function uploadData(action) {
	var file = $("#uploadDatafm input[type=file]").val();
	if (file == "" || file.split(".")[file.split(".").length - 1].toLocaleLowerCase() != "zip") {
		alert("Please select zip file to upload.");
		return;
	}	
	$("[name='uploadAction']").val(action);
	var options = {
        success: function (data) {        	        
        },
	    error: function() {	    	
	    }
   };
   
   $("#uploadDatafm").ajaxForm(options);
   $("#uploadDatafm").ajaxSubmit(options);   
}

function uploadStatus() {
	$.ajax({
	    type: "GET",
	    url: "uploadStatus",
	    contentType:"application/json;charset=utf-8",
	    success: function(data) {
	    	$("#uploadStatus").html(data);
	    },
	    error: function() {
	    }
	});
}

function uploadDataWithReplace() {
	uploadData("replace");
}
function uploadDataWithChecking() {
	uploadData("check");
}
function uploadDataWithSkip() {
	uploadData("skip");
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

function renderLiveDataFileList() {
	$.ajax({
	    type: "GET",
	    url: "listLiveData",
	    contentType:"application/json;charset=utf-8",
	    success: function(data) {
	    	var lis = "";
	    	for(var i = 0; i < data.length; i++) {
	    		lis += "<a id='" + data[i] + "' href='downloadlive?id=" + data[i] + "'>" + data[i] + "</a>" +
//	    				"<input onclick='start(\"" + datas[0] + "\")' type='button' value='Start'/>" +
//	    				"<input onclick='stop(\"" + datas[0] + "\")' type='button' value='Stop'/>" +
	    				"<input onclick='deleteLiveItem(\"" + data[i] + "\")' type='button' value='Delete'/>" +
	    				"<br/>"
	    	}
	    	$("#livefiles").html(lis);
	    },
	    error: function() {
	    }
	});
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

function deleteLiveItem(id) {
	$.ajax({
	    type: "GET",
	    url: "deleteLiveItem?id=" + id,
	    success: function(data) {
	    	renderLiveDataFileList();
	    },
	    error: function() {
	    }
	});
}

var tab = "backtestTab";
function initTab() {
	var titles = document.getElementById('tab-header').getElementsByTagName('li');  
	var divs = document.getElementById('tab-content').getElementsByClassName('dom');  
	if(titles.length != divs.length) return;  
	for(var i=0; i<titles.length; i++){  
		var li = titles[i];  
		li.id = i;  
		li.onclick = function(){  
			for(var j=0; j<titles.length; j++){  
				titles[j].className = '';  
				divs[j].style.display = 'none';  
			}  
			this.className = 'selected';  
			divs[this.id].style.display = 'block'; 
			tab = $(this).attr("tab");
			
			// one time update here
			if (tab == 'strategyTab') {
				updateStrategyStatus($(this).text());
			}
			if (tab == 'controlTab') {
				updateControlTab();
			}		
			
		}  
	}  
}

function scheduleTask() {
	// schedule update here
	switch(tab) {
		case "orderStatusTab" : 
			updateOrderTab();
			break;
		case "backtestTab" : 
			updateStatus();
			break;
		case "marketDataTab" : 
			getMarketData();
			break;
		case "uploadDataTab" : 
			uploadStatus();
			break;
		default : 
			break;
	}
}

var taskCompleted = false;
var intervalId = -1;
$(function() {
	$('.timepicker').TimePickerAlone();
	jQuery('.datepicker').datetimepicker({
		 timepicker:false,			 
		 format:'Y-m-d'
	});
	jQuery('.datetimepicker').datetimepicker({
		 timepicker:true,			 
		 format:'Y-m-d H:i:s'
	});
//	jQuery('.yearmonthpicker').datetimepicker({
//		 timepicker:false,			 
//		 format:'Ym'
//	});
	initTab();
	updateStatus();
	getMarketData();	
	renderDownFileList();
	renderLiveDataFileList();
	updateConnectStatus();
	startRefresh();
});

function startRefresh() {
	if(intervalId == -1) {
		intervalId = setInterval(scheduleTask, 1000);		
	}
}

function stopRefresh() {
	clearInterval(intervalId);
	intervalId = -1;
}
