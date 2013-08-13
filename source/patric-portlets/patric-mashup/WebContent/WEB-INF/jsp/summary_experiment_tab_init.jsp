<%@ page import="javax.portlet.ResourceURL" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

%>
<div id="tbl_transcriptomics">
	<span class="right far2x">Retrieving data...&nbsp;
		<img src="/patric/images/icon_please_wait.gif" alt="Please Wait" style="vertical-align:middle" />
	</span>
	<br/>
	<div style="clear:both"></div>
</div>

<script type="text/javascript">
//<![CDATA[
Ext.onReady(function () {
	Ext.Ajax.request({
		url: '<portlet:resourceURL />',
		method: 'GET',
		params: {cType:'<%=cType%>',cId:'<%=cId%>'},
		success: function(rs) {
			Ext.getDom("tbl_transcriptomics").innerHTML = rs.responseText;
		}
	});
});
//]]>
</script>
