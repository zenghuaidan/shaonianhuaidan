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
		$("#llivetradingdata").show();
		$("#liveTradingData").show();
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
		$("#llivetradingdata").hide();
		$("#liveTradingData").hide();
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
