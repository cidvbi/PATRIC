<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<portlet:defineObjects />
<%
String initAnchor = resourceRequest.getParameter("scrollTo");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	<title></title>
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<link rel="stylesheet" href="/patric/css/reset.css" type="text/css" />
	<link rel="stylesheet" href="/patric/css/popup.css" type="text/css" />
		
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
</div><!-- #header -->
<div id="toppage">
	<div class="content">
