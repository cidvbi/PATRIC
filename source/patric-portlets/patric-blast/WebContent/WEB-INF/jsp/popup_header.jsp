<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<portlet:defineObjects />
<%
String initAnchor = resourceRequest.getParameter("scrollTo");
%>
<!DOCTYPE html>
<html class="no-js" lang="en">
	<head>
		<meta charset="utf-8"/>

		<title></title>

		<link rel="stylesheet" href="/patric/css/popup.css"/>
		
		<script type="text/javascript">
		function doInitScroll(initAnchor) {
			if (initAnchor != null) {
				document.getElementById(initAnchor).scrollIntoView(true);
			}
		}
		</script>
	</head>
	<body id="popup" onload="doInitScroll('<%=initAnchor%>');">
		<div id="header">
			<div id="masthead"><a href="/">PATRIC <span class="sub">Pathosystems Resouce Integration Center</span></a></div>
		</div>
		<div id="toppage">
			<div class="content">
