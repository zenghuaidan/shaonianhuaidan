
function defaultConnectClick() {
	$("[name='host']").val("127.0.0.1");
	$("[name='port']").val("7496");
	$("[name='clientId']").val("1");
	$("[name='account']").val("U8979091");
}

function searchClick() {
	var file = $("#contractTemplatefm input[type=file]").val();
	var ext = file.split(".")[file.split(".").length - 1].toLocaleLowerCase();
	if (file == "" || (ext != "xls" && ext != "xlsx")) {
		alert("Please select xls or xlsx file to upload.");
		return;
	}
	if(!$("#fundamentalData").is(":checked") && !$("#marketData").is(":checked")) {
		alert("Please select download Market Data or Fundamental Data.");
		return;
	}	
	if(!confirm("Are you sure to start a new search, the previous search will be stopped.")) {
		return;
	}
	var options = {
        success: function (data) {
        	if(data != 'Success'){
        		alert(data);
        	} else {
        		window.location.reload();	        		
        	}
        }
   };
   
   $("#contractTemplatefm").ajaxForm(options);
   $("#contractTemplatefm").ajaxSubmit(options);   
}
function connectClick() {
	var host = $.trim($("[name='host']").val());
	var port = $.trim($("[name='port']").val());
	var clientId = $.trim($("[name='clientId']").val());
	var account = "";//$.trim($("[name='account']").val());
	if(host == "" || port == "" || clientId == "") {
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
	    		//alert("Connect failed!!!");
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
	    				"<input onclick='uploadData(\"" + data[i] + "\")' type='button' value='Upload'/>" + 
	    				"<input onclick='deleteLiveItem(\"" + data[i] + "\")' type='button' value='Delete'/>" +
	    				"<br/>"
	    	}
	    	$("#livefiles").html(lis);
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

function uploadData(id) {
	$.ajax({
	    type: "GET",
	    url: "uploadData?id=" + id,
	    success: function(data) {
	    	alert("Data uploaded!");
	    },
	    error: function() {
	    }
	});
	alert("Request submitted!");
}

$(function() {
	$('.timepicker').TimePickerAlone();
	jQuery('.datepicker').datetimepicker({
		 timepicker:false,			 
		 format:'Y-m-d'
	});
	jQuery('.datetimepicker').datetimepicker({
		 timepicker:true,			 
		 format:'Y-m-d H:i'
	});
	renderLiveDataFileList();
	
	$("#contractSelect").change(function() {
		$("tr.contract").hide();
		$("tr.contract-" + $(this).val()).show();
	});
	$("#contractSelect").change();
	updateConnectStatus();
});
