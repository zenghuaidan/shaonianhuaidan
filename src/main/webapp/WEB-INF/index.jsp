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
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.datetimepicker.full.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/mmnt.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/index.js"></script>
<script src="<%=request.getContextPath()%>/resources/script/jquery.form.min.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>
<body>
	<input type="hidden" value="${ mainUIParam.sourcePath }">
	<div id="tab">  
        <div id="tab-header">  
            <ul>                  
                <li tab="connectionTab" class="selected">Connection</li>
            </ul>  
        </div>  
        <div id="tab-content">  
            <div class="dom" style="display: block;">  
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
   	   			   <!--tr>
				      <td>Account</td>
				      <td><input name="account" value="${connectionInfo.account}" type="text"/></td>
				   </tr-->
   	   			   <tr>
				      <td>TimeZone</td>
				      <td>
			      		<select name="timeZone">
			      			<c:forEach items="${timeZones}" var="timeZone" varStatus="i">
			      				<option value="${timeZone}" ${timeZone eq connectionInfo.timeZone ? 'selected' : ''}>${timeZone}</option>
			      			</c:forEach>
			      		</select><font color="red"><lable id="nowTime"></lable></font>
				      </td>
				   </tr>
   	   			   <tr>
				      <td></td>
				      <td>
				      	<input name="defaultConnect" onclick="defaultConnectClick();" value="Default Connection" type="button"/>
				      	<input name="connect" onclick="connectClick();" value="Update" type="button"/>
				      	<!--input name="disconnect" onclick="disconnectClick();" value="Disconnect" type="button"/-->				      	
				      	<label name="status">Offline</label>
				      </td>
				   </tr>
  				</table>
  				<hr/>
        		<table>
       				<tr>
				      	<td>Contract Info</td>
				      	<td>
				      		<select id="contractSelect">
				      			<c:forEach items="${contracts}" var="contract" varStatus="i">
				      				<option value="${i.count}">${contract.secType}_${contract.symbol}_${contract.currency}_${contract.exchange}</option>
				      			</c:forEach>
				      		</select>
						</td>
				   	</tr>
	  				<c:forEach items="${contracts}" var="contract" varStatus="i"> 
						<tr class="contract contract-${i.count}">
					      <td>Sec Type</td>
					      <td><input name="secType" disabled="disabled" value="${contract.secType}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Symbol</td>
					      <td><input name="symbol" disabled="disabled" value="${contract.symbol}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Currency</td>
					      <td><input name="currency" disabled="disabled" value="${contract.currency}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Exchange</td>
					      <td><input name="exchange" disabled="disabled" value="${contract.exchange}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Local symbol</td>
					      <td><input name="localSymbol" disabled="disabled" value="${contract.localSymbol}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Expirary</td>
					      <td><input class="yearmonthpicker" name="expirary" disabled="disabled" value="${contract.expirary}" type="text"/></td>
					   	</tr>
					   	<tr class="contract contract-${i.count}">
					      <td>Start Time</td>
					      <td><input class="datetimepicker" name="startTime" disabled="disabled" value="${contract.startTime}" type="text"/></td>
					    </tr>
		   			   	<tr class="contract contract-${i.count}">
					      <td>End Time</td>
					      <td><input class="datetimepicker" name="endTime" disabled="disabled" value="${contract.endTime}" type="text"/></td>
					    </tr>
					    <tr class="contract contract-${i.count}">
					      <td>Source</td>
					      <td><input style="color: blue;font-weight: bold;" disabled="disabled" value="${contract.dbSource}" type="text"/><font color="red">* This value is used as Source value when uploading data to DB, you can change it thot within the excel template</font></td>
					    </tr>
					    <tr class="contract contract-${i.count}">
					      <td>Ticker</td>
					      <td><input style="color: blue;font-weight: bold;" disabled="disabled" value="${contract.dbTicker}" type="text"/><font color="red">* This value is used as Ticker value when uploading data to DB, you can change it thot within the excel template</font></td>
					    </tr>
	        		</c:forEach>
  				</table>
  				<hr/>
				<form id='contractTemplatefm' method='post' action='search' enctype='multipart/form-data'>					
					<table>			
	   			   		<tr>
					      <td>Contracts</td>
					      <td><input type="file" name="contractTemplate" accept="text/txt" value="Select Template"/></td>
					   	</tr>
	   			   		<tr>
					      <td>Start Time</td>
					      <td><input class="timepicker" name="startTime" value="${startTime}" type="text"/></td>
					    </tr>
		   			   	<tr>
					      <td>End Time</td>
					      <td><input class="timepicker" name="endTime" value="${endTime}" type="text"/></td>
					    </tr>
					    <tr style="display:none">
					      <td>Market Data</td>
					      <td><input id="marketData" name="marketData" type="checkbox" ${isMarketData ? 'checked' : ''}/></td>
					    </tr>
					    <tr style="display:none">
					      <td>Fundamental Data</td>
					      <td><input id="fundamentalData" name="fundamentalData" type="checkbox" ${isFundamentalData ? 'checked' : ''} /></td>
					    </tr>
					    <tr>
					   	  <td><input type="button" onclick="searchClick();" style="width:100%" value="Search" /></td>
					      <td><a style="color:blue;font-weight:bold;text-decoration:underline" href="../upload/default/Downloader.xlsx">Click to download template file</a></td>
					    </tr>
	  				</table>
				</form>
  				<hr/>
  				<div style="text-align:center"><lable id="incomingDataInfo"></lable>&nbsp;&nbsp;<label onclick="showMissing()" style="cursor:pointer;text-decoration:underline">Click to view missing data contracts</label></div>
  				<lable id="missiongDataInfo"></lable>
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
			
			
        </div>  
    </div>  
</body>
</html>