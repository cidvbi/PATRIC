<%@ page import="javax.portlet.ResourceURL" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String qKeyword = request.getParameter("keyword");
if (qKeyword == null) { qKeyword = ""; }

%>
<nav class="block no-underline-links" id="tbl_pubmedpanel">
	<span style="float:right">Retrieving data...&nbsp;
		<img src="/patric/images/icon_please_wait.gif" alt="Please Wait" style="vertical-align:middle" />
	</span>
</nav>
<script type="text/javascript">
//<![CDATA[
Ext.onReady(function () {
	Ext.Ajax.request({
		url: '/portal/portal/patric/Taxon/PubMedPanelWindow?action=b&cacheability=PAGE',
		method: 'GET',
		params: {context_type:'<%=cType%>',context_id:'<%=cId%>',keyword:'<%=qKeyword%>'},
		success: function(rs) {
			/*
			if (rs.responseText == "no pubmed result") {
				
				// query on pmc again
				Ext.Ajax.request({
					url: '/portal/portal/patric/Taxon/PubMedPanelWindow?action=b&cacheability=PAGE',
					method: 'GET',
					params: {context_type:'<%=cType%>',context_id:'<%=cId%>',keyword:'<%=qKeyword%>',db:"pmc"},
					success: function(rs) {
						Ext.getDom("tbl_pubmedpanel").innerHTML = rs.responseText;
					}
				});
				
			} else {
				*/
				Ext.getDom("tbl_pubmedpanel").innerHTML = rs.responseText;
			//}
		}
	});
});
//]]>
</script>