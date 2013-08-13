<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="java.util.*" %>
<%
String nameSpaceAids =
	renderResponse.encodeURL(renderRequest.getContextPath() 
			+ "/js/namespace.js");

String runBrowser =
	renderResponse.encodeURL(renderRequest.getContextPath() 
			+ "/js/TreeAligner.js");

String actionURL = (renderResponse.createActionURL()).toString();

String windowID = renderRequest.getWindowID();

String resourceURL = (renderResponse.createResourceURL()).toString();

String contextPath = renderResponse.encodeURL(renderRequest.getContextPath());

String pk = request.getParameter("param_key");
String featureIds = "";
String figfamId = "";
String product = "";
ResultType key = (ResultType) (renderRequest.getPortletSession()).getAttribute("key" + pk, 1);
if (key != null) {
	featureIds = key.get("featureIds");
	figfamId = key.get("figfamId");
	if (figfamId == null) {
		figfamId = "";
	}
	product = key.get("product");
	if (product == null) {
		product = "";
	}
}
%>

<script type="text/javascript" src="<%=nameSpaceAids%>"></script>
<script type="text/javascript" src="<%=runBrowser%>"></script>

<div id="<%=windowID%>">
	<div id = '<%=windowID%>_summary'></div>
	<div id = '<%=windowID%>_forApplet' style='width:100%; height:500px'></div>
</div>


<form id="<%=windowID%>_form" action="#" method="post">
	<input type="hidden" id="data" name="data" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />
</form>

<script type="text/javascript">
//<![CDATA[
var path="";

Ext.onReady(function() {
	TreeAlignerOnReady("<%=windowID%>", "<%=resourceURL%>", "<%=contextPath%>",
		"<%=featureIds%>", "<%=figfamId%>", "<%=product%>");
});
//]]>
</script>
