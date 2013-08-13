<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBTranscriptomics" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface"  
%><%

	String cType = request.getParameter("context_type");
	String cId = request.getParameter("context_id");
	String experimentId = "";

	SolrInterface solr = new SolrInterface();
	solr.setCurrentInstance("Proteomics_Protein");
	experimentId = solr.getProteomicsTaxonIdFromFeatureId(cId);		
	
	String keyword = "(*)";

%>
<div style="display:none">
<form id="fTableForm" action="" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="Proteomics_Experiment" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword %>" />
<input type="hidden" name="cType" id="cType" value="<%=cType %>" />
<input type="hidden" name="cId" id="cId" value="<%=cId %>" />
<input type="hidden" name="experimentId" id="experimentId" value="<%=experimentId %>" />

<input type="hidden" id="aT" name="aT" value="0" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fileformat" name="fileformat" value=""/>
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="download_keyword" name="download_keyword" value="" />
</form>
</div>
<div id="copy-button" class="x-hidden"></div>
<p>
	The list below provides all of the available proteomics experiments and associated metadata 
	for this feature. The list of experiments can be filtered by metadata or keyword. 
	To learn more about PATRIC's proteomics data and associated  metadata, 
	see <a href="http://enews.patricbrc.org/faqs/transcriptomics-faqs/transcriptomics-experiment-and-comparison-list-faqs" target=_blank>Transcriptomics FAQs</a>.
</p>

<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<div id="information" class="table-container"  style="background-color:#DDE8F4;">
<table style="width:100%"><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
	<div id="grid_result_summary"><b>Loading...</b></div>
</td></tr></table>
</div>
<div id="PATRICGrid" style=""></div>

<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript">
//<![CDATA[
           
var $Page;

Ext.onReady(function()
{

	var pageProperties = {
		name: "Proteomics_Experiment",
		model:["Proteomics_Experiment"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:false,
		WoWorkspace: true,
		scm: [[{header:'Sample Name',				flex:2, dataIndex: 'sample_name',			renderer:BasicRenderer},
				{header:'Taxon Name',			flex:2, dataIndex: 'taxon_name',		renderer:BasicRenderer},
				{header:'Project Name',				flex:1, dataIndex: 'project_name',			renderer:BasicRenderer},
				{header:'Experiment Label',				flex:1, dataIndex: 'experiment_label',		renderer:BasicRenderer}, 
				{header:'Experiment Title',				flex:2, dataIndex: 'experiment_title',		renderer:BasicRenderer}, 
				{header:'Experiment Type',				flex:2, dataIndex: 'experiment_type',		renderer:BasicRenderer}, 
				{header:'Source',				flex:1, dataIndex: 'source',		renderer:BasicRenderer},
				{header:'Contact Name',	flex:2, dataIndex: 'contact_name',		renderer:BasicRenderer},
				{header:'Institution',	flex:2, dataIndex: 'institution',		renderer:BasicRenderer},
			]],
		extraParams: getExtraParams,
		callBackFn: CallBack,
		sort: [[{
				property:"sample_name",
				direction: "ASC"
			}]
		],
		hash:{
			aP: [1]
		},
		remoteSort:true,
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['proteomicsfeatureexperimentlist'],
		pagingBarMsg: ['Displaying experiments {0} - {1} of {2}']
	};
	SetPageProperties(pageProperties);
	Ext.QuickTips.init();
	SetIntervalOrAPI(),
	loadGrid();
	$Page.doLayout();
	
	if (Ext.get("tabs_proteomics")) {
		Ext.get("tabs_proteomics").addCls("sel");
	}
});

function getExtraParams(){
	var list_object ={};

	list_object['experiment_id'] = '<%=experimentId%>';
	
	Ext.getDom("download_keyword").value = constructKeyword(list_object, "Proteomics_Experiment");
	
	return {
		pk : null,
		need : 0,
		keyword : constructKeyword(list_object, "Proteomics_Experiment"),
		facet : null
	};
}

function CallBack(){
	Ext.getDom('grid_result_summary').innerHTML = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in Proteomics experiments</b><br/>";
}
	
function DownloadFile() {
	"use strict";
	
	var form = Ext.getDom("fTableForm");

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
	form.fileformat.value = arguments[0];
	form.target = "";
	getHashFieldsToDownload(form);
	form.submit();

}
// ]]>
</script>
