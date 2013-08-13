<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBTranscriptomics" %>
<%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%

DBTranscriptomics db = new DBTranscriptomics();
String pk = request.getParameter("param_key");

HashMap<String, String> key = (HashMap<String, String>)portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String featureList = key.get("feature_info_id");
System.out.println("jsp page"+featureList);

int featureList_length = featureList.split(",").length;

int found_length = db.getPathwayEnrichmentNoofGenesSQL(key);
%>
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
<input type="hidden" id="tablesource" name="tablesource" value="TranscriptomicsEnrichment" />
<input type="hidden" id="fileformat" name="fileformat" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="featureList" name="featureList" value="<%=featureList %>" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />
</form>

<div id="copy-button" style="display:none"></div>
<div class="table-container">
<table width="100%"><tr><td>
	<div id="grid_result_summary"></div>
</td></tr></table>
</div>
<div id='PATRICGrid'></div>

<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/checkcolumn.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-transcriptomics/js/TranscriptomicsEnrichment.js"></script>
<script type="text/javascript">
//<![CDATA[

var $Page;

Ext.onReady(function () {
	
	Ext.define('Feature', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'pathway_id',	type:'string'},
			{name:'pathway_name',	type:'string'},
			{name:'ocnt',	type:'string'},
			{name:'ecnt',	type:'string'},
			{name:'percentage',	type:'float'}
			]
		});
	var checkbox = createCheckBox("Enrichment");
	
	var pageProperties = {
		name: "Enrichment",
		model: ["Feature"],
		items: 1,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort:[[{
			property: 'percentage',
			direction: 'DESC'
		}]],
		hash:{
			aP: [1],
			key:'<%=pk%>'
		},
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		scm: [[checkbox,
				{header:'Pathway Name',	dataIndex:'pathway_name',  flex:2, renderer:renderPathwayEnrichment},
				{header:'# of Genes Selected', dataIndex:'ocnt', 	align:'center',	flex:1, renderer:BasicRenderer}, 
				{header:'# of Genes Annotated',dataIndex:'ecnt', 	align:'center',	flex:2, renderer:BasicRenderer}, 
				{header:'% Coverage',	dataIndex:'percentage', 	align:'center',	flex:2, renderer:BasicRenderer}
			]],
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();}
	};
		
	SetPageProperties(pageProperties);
	$Page.checkbox = checkbox;
	// SetIntervalOrAPI();
	Ext.QuickTips.init();
	overrideButtonActions();
	loadGrid();
	$Page.doLayout();
});

function getFoundCount(){
	return '<%=found_length%>';
}
// ]]
</script>
