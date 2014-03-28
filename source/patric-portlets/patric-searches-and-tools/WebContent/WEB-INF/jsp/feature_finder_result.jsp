<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");

//HashMap<String,String> key = (HashMap<String,String>) portletSession.getAttribute("key"+pk);
ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);

String taxonId = "";
String genomeId = "";
String keyword = "";
String exact_search_term = "";

if(key != null && key.containsKey("taxonId")){
	taxonId = key.get("taxonId");
}

if(key != null && key.containsKey("genomeId")){
	genomeId = key.get("genomeId");
}

if(key != null && key.containsKey("keyword")){
	keyword = key.get("keyword");
}

if(key != null && key.containsKey("exact_search_term")){
	exact_search_term = key.get("exact_search_term");
}
String algorithm = "";
if(keyword.contains("annotation:)")){
	algorithm = keyword.split("annotation:\\(")[1].split("\\)")[0];	
}

String feature_type = "";
if(keyword.contains("feature_type:")){
	feature_type = keyword.split("feature_type:\\(")[1].split("\\)")[0];
}

%>
<div style="display:none">
<form id="fTableForm" name="searchForm" action="#" method="post" onsubmit="return false;">
	<input type="hidden" id="tablesource" name="tablesource" value="Feature" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />
	<input type="hidden" id="taxonId" name="taxonId" value="<%=taxonId %>" />
	<input type="hidden" id="genomeId"name="genomeId" value="<%=genomeId %>" />
	<input type="hidden" id="keyword" name="keyword" value="<%=keyword %>" />
	<input type="hidden" id="annotation" name="annotation" value="<%=algorithm %>" />
	
	<!-- fasta download specific param -->
	<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
	<input type="hidden" id="fids" name="fids" value="" />
	<input type="hidden" id="download_keyword" name="download_keyword" value="" />
	
<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
<input type="hidden" id="cType" name="cType" value="<%=cType %>" />	
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />

</form>
</div>

<div id="copy-button" style="display:none;"></div>
<div style="padding:3px;">
<input type="button" class="button leftarrow" id="search_modify" value="Modify Search Criteria" onclick="returntoSearchPage();"/>
<span style="color:#999;font-size: 13px;top: 23px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;padding-left: 10px;padding-top: 6px;padding-bottom: 6px;">Showing results for: <b><%=exact_search_term %></b></span></div>
<div class="table-container" id="SearchSummary">
<table><tbody><tr><td>
	<div id="grid_result_summary"></div>
To learn how to filer, sort, manipulate, refine, and save data within PATRIC feature tables, 
	please see <a href="http://enews.patricbrc.org/feature-table-faqs/" target="_blank">Feature Table FAQs</a></td></tr></tbody></table>
</div>
<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/checkcolumn.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/createtree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/ec_go_feature_grids.js"></script> 
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<script type="text/javascript">
//<![CDATA[

var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("Feature");
	
	var pageProperties = {
		name: "Feature",
		items: 1,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		tree:null,
		treeDS:null,
		model:["Feature"],
		sort: [[{
			property: 'genome_name',
			direction:'ASC'
		},{
			property: 'locus_tag',
			direction:'ASC'
		}]],
		hash:{
			aP: [1],
			pS : [20],
			key: "<%=pk%>"
		},
		extraParams:getExtraParams,
		callBackFn:CallBack,
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		scm:[[checkbox, 
			{header:'Genome Name',			dataIndex:'genome_name', 		flex:2, renderer:renderGenomeName},
			{header:'Accession',			dataIndex:'accession',			hidden:true, flex:1, align:'center', renderer:renderAccession},
			{header:'Locus Tag',			dataIndex:'locus_tag', 			flex:2, renderer:renderLocusTag},
			{header:'RefSeq Locus Tag',		dataIndex:'refseq_locus_tag', 	flex:2, renderer:renderLocusTag},
			{header:'Gene Symbol',			dataIndex:'gene', 				flex:1, align:'center', renderer:BasicRenderer},
			{header:'Genome Browser',		dataIndex:'na_feature_id',		flex:1, align:'center', hidden:true, renderer:renderGenomeBrowserByFeature},
			{header:'Annotation',			dataIndex:'annotation', 		flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Feature Type',			dataIndex:'feature_type',		flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Start',				dataIndex:'start_max',			flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'End',					dataIndex:'end_min',			flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Length (NT)',			dataIndex:'na_length',			flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Strand',				dataIndex:'strand',				flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Protein ID',			dataIndex:'refseq_protein_id',	flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Length (AA)',			dataIndex:'aa_length',			flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'AntiCodon',			dataIndex:'anticodon',			flex:1, align:'center', hidden:true, renderer:BasicRenderer},
			{header:'Product Description',	dataIndex:'product',			flex:4, renderer:BasicRenderer},
			{header:'Bound Moiety',			dataIndex:'bound_moiety',		flex:1, align:'center', hidden:true, renderer:BasicRenderer}
		]],
		stateId: ['featurelist'],
		pagingBarMsg: ['Displaying features {0} - {1} of {2}']
	};
	
	SetPageProperties(pageProperties),
	createLayout(),
	$Page.checkbox = checkbox,
	SetLoadParameters();
	
	// SetIntervalOrAPI(),
	Ext.QuickTips.init(),
	overrideButtonActions();	
	loadGrid(),
	$Page.doLayout();
});

function getOriginalKeyword(){
	return "<%=keyword%>";
}


function returntoSearchPage(){
	var key = DecodeKeyword('<%=java.net.URLEncoder.encode(exact_search_term, "UTF-8") %>');
	document.location.href = "GenomicFeature?cType=<%=cType%>&cId=<%=cId%>&dm=#feature_type=<%=feature_type%>&keyword="+key+"&annotation=<%=algorithm%>&feature_type=<%=feature_type%>";
}

//]]>
</script>