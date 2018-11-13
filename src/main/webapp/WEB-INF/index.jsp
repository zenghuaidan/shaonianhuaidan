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
    			<li tab="uploadDataTab">Upload Data</li>
    			<li tab="transferDataTab">Transfer Data</li>
            </ul>  
        </div>  
        <div id="tab-content">  
          	<div class="dom" style="display:block">
	          	<div id="html">
					<form id='uploadDatafm' method='post' action='uploadData' enctype='multipart/form-data' style="margin-top:10px; margin-bottom:10px;">
						<input type="hidden" name="uploadAction" />
						Start:<input type="text" name="dataStartTime" class="timepicker" value="09:15:00"/>
						Lunch Time From:<input type="text" name="lunchStartTime" class="timepicker" value="12:00:00"/>
						Lunch Time To:<input type="text" name="lunchEndTime" class="timepicker" value="13:00:00"/>
						End:<input type="text" name="dataEndTime" class="timepicker" value="16:15:00"/>
						<label name="source">Source:</label><input type="text" name="source"/>
						<label name="ticker">Ticker:</label><input type="text" name="ticker"/>
						<input type="file" name="liveData" accept="text/txt" value="Select File"/>
						<br/>
						Ignore Lunch Time:<input type="checkbox" name="ignoreLunchTime"/>
						<label id="ltoDatabase">To Database:</label><input type="checkbox" id="toDatabase" name="toDatabase" checked="checked"/>
						<label id="ltoCSV">To CSV:</label><input type="checkbox" id="toCSV" name="toCSV" checked="checked"/>
						<label name="dataType">Data Format:</label>
						<input type="radio" name="dataType" value="1" checked="checked"/><label name="dataType">BBG Data Format</label>
						<input type="radio" value="2" name="dataType" /><label name="dataType">TWS Download Format</label>
						<input type="radio" value="3" name="dataType" /><label name="dataType">Schedule Data Format</label>
						<input type="radio" value="4" name="dataType" /><label name="dataType">HKEX Data Format</label>
						CSV Path:<input type="text" name="csvPath"/>
						<input type="button" id="uploadDataWithChecking" value="Check"/>
						<input type="button" id="uploadDataWithReplace" value="Upload by replace"/>
						<input type="button" id="uploadDataWithSkip" value="Upload by skip"/>
						<input type="button" id="uploadDataWithTransfer" value="Transfer Data"/>
					</form>
					<div id="downloadSampleDateDiv">
					Date:<input type="text" id="downloadSampleDate" name="downloadSampleDate" class="datepicker"/>
					Ticker:<select id="tickerForDelete">
				         <c:forEach items="${tickers}" var="ticker" varStatus="i">
				            <option ${ticker}>${ticker}</option>
				         </c:forEach>
			         </select>
					<input id="downloadSampleDateBtn" type="button" value="Download"/>
					<input id="deleteSampleDateBtn" type="button" value="Delete"/></br>
					Download Summary:
					<select id="downloadSummary" name="downloadSummary">
				         <c:forEach items="${tickers}" var="ticker" varStatus="i">
				            <option ${ticker}>${ticker}</option>
				         </c:forEach>
			         </select>	
					<input id="downloadSummaryBtn" type="button" value="Download"/>
					</div>
					<div id="uploadStatus"></div>					
				</div>
            </div>
            <div class="dom"></div>
        </div>  
    </div>  
</body>
</html>