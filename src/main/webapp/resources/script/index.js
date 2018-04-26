function uploadData(action) {
	var file = $("#uploadDatafm input[type=file]").val();
	if (file == "" || file.split(".")[file.split(".").length - 1].toLocaleLowerCase() != "zip") {
		alert("Please select zip file to upload.");
		return;
	}	
	
	if(action != 'transfer' && ($.trim($("input[name='source']").val()) == '' || $.trim($("input[name='ticker']").val()) == '')) {
		alert("Please input both source and ticker.");
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

function uploadDataWithTransfer() {
	uploadData("transfer");
}

function downloadSampleDate(){
	var sampleDate = $.trim($("#downloadSampleDate").val());
	if(sampleDate == "") {
		alert("Please select sample date for download.")
	} else {
		window.open("downloadSampleDate?sampleDate=" + sampleDate);
	}
}

function downloadSummary(){
	var ticker = $.trim($("#downloadSummary").val());
	if(ticker == "") {
		alert("Please select the ticker value for download.")
	} else {
		window.open("downloadSummary?ticker=" + ticker);
	}
}

var tab = "uploadDataTab";
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
			doInitTab();
		}  
	}  
}

function doInitTab() {
	var html = $("#html").clone();
	if (tab == 'uploadDataTab') {
		$(".dom").eq(0).html(html.prop("outerHTML"));
		$(".dom").eq(1).html("");
		$("#ltoDatabase").show();
		$("#ltoCSV").show();
		$("#toDatabase").show();
		$("#toCSV").show();
		$("label[name='dataType']").show();
		$("input[name='dataType']").show();
		$("label[name='source']").show();
		$("label[name='ticker']").show();
		$("input[name='source']").show();
		$("input[name='ticker']").show();
		$("#uploadDataWithChecking").show();
		$("#uploadDataWithReplace").show();
		$("#uploadDataWithSkip").show();
		$("#uploadDataWithTransfer").hide();
		$("#downloadSampleDateDiv").show();
	}	
	if (tab == 'transferDataTab') {
		$(".dom").eq(0).html("");
		$(".dom").eq(1).html(html.prop("outerHTML"));
		$("#ltoDatabase").hide();
		$("#ltoCSV").hide();
		$("#toDatabase").hide();
		$("#toCSV").hide();
		$("label[name='dataType']").hide();
		$("input[name='dataType']").hide();
		$("label[name='source']").hide();
		$("label[name='ticker']").hide();
		$("input[name='source']").hide();
		$("input[name='ticker']").hide();
		$("#uploadDataWithChecking").hide();
		$("#uploadDataWithReplace").hide();
		$("#uploadDataWithSkip").hide();
		$("#uploadDataWithTransfer").show();
		$("#downloadSampleDateDiv").hide();
	}
	$("#uploadDataWithChecking").click(function(){
		uploadDataWithChecking();
	});
	$("#uploadDataWithReplace").click(function(){
		uploadDataWithReplace();
	});
	$("#uploadDataWithSkip").click(function(){
		uploadDataWithSkip();
	});
	$("#uploadDataWithTransfer").click(function(){
		uploadDataWithTransfer();
	});
	
	$("#downloadSampleDateBtn").click(function(){
		downloadSampleDate();
	});
	
	$("#downloadSummaryBtn").click(function(){
		downloadSummary();
	});
	
	$('.timepicker').TimePickerAlone();
	jQuery('.datepicker').datetimepicker({
		 timepicker:false,			 
		 format:'Y-m-d'
	});
	jQuery('.datetimepicker').datetimepicker({
		 timepicker:true,			 
		 format:'Y-m-d H:i'
	});
}

$(function() {
	initTab();
	$("li[tab='uploadDataTab']").click();
	setInterval(uploadStatus, 3000);
});
