<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"
%><%
String cType = request.getParameter("context_type");
String cId = (request.getParameter("context_id")!=null)?request.getParameter("context_id"):"";
String sampleId = (request.getParameter("sampleId")!=null)?request.getParameter("sampleId"):"";
String log_ratio = (request.getParameter("log_ratio")!=null && !request.getParameter("log_ratio").equals(""))?request.getParameter("log_ratio"):"0";
String zscore = (request.getParameter("zscore")!=null && !request.getParameter("zscore").equals(""))?request.getParameter("zscore"):"0";
%>
<script type="text/javascript" src="/patric/js/vbi/GeneExpression.min.js"></script>
<p>
The data below summarizes the transcriptomics data associated with this gene. The list of comparisons (and respective visual summaries) can be filtered by keyword, log ratio and Z-score.  
To learn more, see <a href=" http://enews.patricbrc.org/faqs/transcriptomics-faqs/gene-page-transcriptomics-faqs/" target=_blank>Gene Page Transcriptomics FAQs</a>.</p>
<div id="expression_panel"></div>
<form id="expression_param" action="#" method="post">
	<input type="hidden" id="featureId" name="featureId" value="<%=cId %>" />
	<input type="hidden" id="sampleId" 	name="sampleId"  value="<%=sampleId %>" />
	<input type="hidden" id="log_ratio" name="log_ratio" value="<%=log_ratio %>" />
	<input type="hidden" id="zscore" 	name="zscore" 	 value="<%=zscore %>" />
</form>
<form id="fTableForm" action="#" method="post">
	<input type="hidden" id="tablesource" name="tablesource" value="" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />
	<input type="hidden" id="fids" name="fids" value="" />	
	<input type="hidden" id="idType" name="idType" value="" />
</form>
<script type="text/javascript">
//<![CDATA[
Ext.onReady(function () {
	
	//set tabs
	if (Ext.get("tabs_expression")!=null) {
		Ext.get("tabs_expression").addCls("sel");
	}
});
//]]>
</script>