<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBTranscriptomics" %>
<%@ page import="edu.vt.vbi.patric.common.SolrInterface" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="org.json.simple.JSONObject" %>
<%

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String eid = request.getParameter("eid");

ResultType key = new ResultType();
key.put("keyword", "eid:("+eid+")");

SolrInterface solr = new SolrInterface();
solr.setCurrentInstance("GENEXP_Experiment");
JSONObject obj = solr.getData((ResultType)key, null, null, 0, -1, false, false, false);

%>
<h3 class="section-title normal-case close2x" style="
    width: 900px;
    margin-left: auto;
    margin-right: auto;
"><span class="wrap">Single Experiment</span></h3>
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
<input type="hidden" id="eid" name="eid" value="<%=eid %>" />
<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
<input type="hidden" id="tablesource" name="tablesource" value="SingleExperiment" />
<input type="hidden" id="fileformat" name="fileformat" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />
</form>

<div id="copy-button" style="display:none"></div>
<div id="information" class="table-container"  style="background-color:#DDE8F4;">
<table style="width:100%"><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
	<div id="grid_result_summary"><b>Loading...</b></div>
</td></tr></table>
</div>
<div id="sample-layout" style="width:900px; margin:0 auto;"></div>

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
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/experiment_list_grids.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-transcriptomics/js/SingleExperiment.js"></script>
<script type="text/javascript">
//<![CDATA[

var $Page;

Ext.onReady(function () {
	
	var checkbox = createCheckBox("Sample");
	
	var pageProperties = {
		name: "Sample",
		model: ["Sample"],
		items: 1,
		cart: true,
		cartType: 'exp_list',
		WoWorkspace: true,
		plugin:true,
		plugintype:"checkbox",	
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort:[[{
			property: '',
			direction: ''
		}]],
		hash:{
			aP: [1]
		},
		remoteSort:true,		
		fids: [],
		gridType: "ExpressionExperiment",
		scm: [[checkbox,
		       	{header: "Title",						flex:3, dataIndex: 'expname',	renderer:BasicRenderer},
				{header: "Genes",						flex:1, dataIndex: 'genes',		renderer: linkToGeneList, align: 'center'},
				{header: "Significant genes(Log Ratio)",flex:1, dataIndex: 'sig_log_ratio',	renderer: linkToGeneListFold, align: 'center'},
				{header: "Significant genes(Z Score)",	flex:1, dataIndex: 'sig_z_score',	renderer: linkToGeneListZScore, align: 'center'},
				{header: "PubMed",						flex:1, dataIndex: 'pmid',		hidden:true, renderer: renderPubMed},
				{header: "Link Out",					flex:1, dataIndex: 'accession',	hidden:true, renderer: renderID},
				{header: "Organism",					flex:1, dataIndex: 'organism',	hidden:true, renderer: BasicRenderer},
				{header: "Strain",						flex:1, dataIndex: 'strain',	renderer: BasicRenderer},
				{header: "Gene Modification",			flex:1, dataIndex: 'mutant',	renderer: BasicRenderer, align: 'center'},
				{header: "Experimental Condition",		flex:1, dataIndex: 'condition',	renderer:BasicRenderer},
				{header: "Time Point",					flex:1, dataIndex: 'timepoint',	renderer: BasicRenderer, align: 'center'}
				// {header: "Release Date",				flex:1, dataIndex: 'release_date',	renderer: BasicRenderer}
			]],
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['singleexperimentlist'],
		pagingBarMsg: ['Displaying comparisons {0} - {1} of {2}']
	};
		
	SetPageProperties(pageProperties);
	$Page.checkbox = checkbox,
	// SetIntervalOrAPI();
	Ext.QuickTips.init();
	overrideButtonActions();
	createLayout();
	loadGrid();
});

// ]]
</script>
