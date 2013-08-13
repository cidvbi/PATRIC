<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<portlet:defineObjects />
<%

String name = "TranscriptomicsGene";
String defaultpath = renderResponse.encodeURL(renderRequest.getContextPath());

String nameSpaceAids = defaultpath + "/js/namespace.js";
String runBrowser = defaultpath + "/js/TranscriptomicsGene.js";
String gridObject = defaultpath + "/js/TranscriptomicsGrid.js";
String gridState = defaultpath +  "/js/TranscriptomicsGridState.js";
String gridSupport = "/patric-common/js/grid/gridoptions.js";
String checkColumn = "/patric-common/js/grid/checkcolumn.js";
String checkBoxes = "/patric-common/js/grid/table_checkboxes.js";
String toolbar = "/patric-common/js/grid/toolbar.js";
String pagingBar = "/patric-common/js/grid/pagingbar.js";			
String hashTracker = defaultpath + "/js/HashTracker.js";
String heatTypes = "/patric-common/js/heatmap/heatmapDatatypes.js";
String swfObject = "/patric-common/js/heatmap/swfobject.js";
String loadHeat = defaultpath + "/js/heatmap/loadHeatmap.js";
String heatMediate = defaultpath + "/js/heatmap/heatmapMediator.js";

String windowID = renderRequest.getWindowID();
String resourceURL = (renderResponse.createResourceURL()).toString();
String contextPath = renderResponse.encodeURL(renderRequest.getContextPath());

String sampleId = request.getParameter("sampleId");

if (sampleId == null) {
	sampleId = "";
}

String expId = request.getParameter("expId");

if (expId == null) {
	expId = "";
}

String colId = request.getParameter("colId");

if (colId == null) {
	colId = "";
}

String log_ratio = request.getParameter("log_ratio");

if (log_ratio == null || log_ratio == "") {
	log_ratio = "-";
}

String zscore = request.getParameter("zscore");

if (zscore == null || zscore == "") {
	zscore = "-";
}

String cType = request.getParameter("context_type");

if (cType == null || cType == "") {
	cType = "";
}

String cId = request.getParameter("context_id");

if (cId == null || cId == "") {
	cId = "";
}
%>

<script type="text/javascript" src="<%=nameSpaceAids%>"></script>

<script type="text/javascript" src="<%=runBrowser%>"></script>

<script type="text/javascript" src="<%=gridObject%>"></script>
<script type="text/javascript" src="<%=gridState%>"></script>
<script type="text/javascript" src="<%=gridSupport%>"></script>
<script type="text/javascript" src="<%=hashTracker%>"></script>
<script type="text/javascript" src="<%=checkColumn%>"></script>
<script type="text/javascript" src="<%=checkBoxes%>"></script>
<script type="text/javascript" src="<%=pagingBar%>"></script>
<script type="text/javascript" src="<%=heatTypes%>"></script>
<script type="text/javascript" src="<%=loadHeat%>"></script>
<script type="text/javascript" src="<%=heatMediate%>"></script>
<script type="text/javascript" src="<%=swfObject%>"></script>
<script type="text/javascript" src="<%=toolbar%>"></script>
<div id="copy-button"  style="display: none"></div>

<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<form id="<%=name%>_fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="FeatureTable" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
</form>
<form id="fTableForm_Cell" action="#" method="post">
<input type="hidden" id="_tablesource" name="_tablesource" value="Table_Cell" />
<input type="hidden" id="_fileformat" name="_fileformat" value="" />
<input type="hidden" id="_data" name="_data" value="" />
</form>
<form id="fTableForm_Feature" action="#" method="post">
<input type="hidden" id="_tablesource" name="_tablesource" value="Table_Feature" />
<input type="hidden" id="_fileformat" name="_fileformat" value="" />
<input id = "featureIds" name='featureIds' type='hidden' value="" />
</form>

<div id="information_panel"></div>
<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<div id="information" style="background-color:#DDE8F4; visibility:hidden; height:0px;">
<table width="100%"><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
	<div id="grid_result_summary"><b>Loading...</b><br/>
	</div>
</td></tr></table>
</div>

<form id='<%=name%>_geneToFile'
	action='<%=contextPath%>/jsp/GetMainTable.jsp'
	method="post">
	<input name='GeneFileType' type='hidden'
		value='xls' />
	<input name='GeneFileName' type='hidden'
		value='mainName' />
	<input name='data' type='hidden'
		value='' />
</form>

<script type="text/javascript">
//<![CDATA[
var $Page;


Ext.onReady(function() {

	var state = Ext.state.Manager.get('TRfeaturelist');
	
	var pageProperties = {
		cart:true, 
		name: "<%=name%>", 
		stateId: ['TRfeaturelist'],
		pagingBarMsg: ['Displaying genes {0} - {1} of {2}'],
		sort: (state && state.sort)?state.sort:[{property:'locus_tag', 
			direction:'ASC'
		}],
		hash:{
			
		}
	};
	Ext.QuickTips.init();
	SetPageProperties(pageProperties);
	TranscriptomicsGeneOnReady('<%=name%>', '<%=resourceURL%>',
		'<%=contextPath%>', '<%=cType%>', '<%=cId%>', '<%=sampleId%>', '<%=expId%>', '<%=colId%>', '<%=log_ratio%>', '<%=zscore%>');
	
	if (Ext.get("tabs_explist")!=null) {
		Ext.get("tabs_explist").addCls("sel");
	}
});
//]]>
</script>
