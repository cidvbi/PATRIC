<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");

//HashMap<String,String> key = (HashMap<String,String>) portletSession.getAttribute("key"+pk);
ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);

String keyword = "";
String search_on = "", exact_search_term = "";


if(key != null && key.containsKey("keyword")){
	keyword = key.get("keyword");
}

if(key != null && key.containsKey("search_on")){
	search_on = key.get("search_on");
}

if(key != null && key.containsKey("exact_search_term")){
	exact_search_term = key.get("exact_search_term");
}

String algorithm = "";
if(keyword.contains("annotation:(")){
	algorithm = keyword.split("annotation:\\(")[1].split("\\)")[0];	
}
%>

<div style="display:none">
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="GO" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword %>" />
<input type="hidden" name="cType" id="cType" value="<%=cType %>" />
<input type="hidden" name="cId" id="cId" value="<%=cId %>" />
<input type="hidden" id="dir" name="dir" value="" />
<input type="hidden" id="sort" name="sort" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" name="download_keyword" id="download_keyword" value="" />
<input type="hidden" id="fileformat" name="fileformat" value=""/>

</form>
</div>
<div id="copy-button"style="display:none;"></div>
<div style="padding:3px;">
<input type="button" class="button leftarrow"  id="search_modify" value="Modify Search Criteria" onclick="returntoSearchPage();"/> 
<span style="color:#999;font-size: 13px;top: 23px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;padding-left: 10px;padding-top: 6px;padding-bottom: 6px;">Showing results for: <b><%=exact_search_term %></b></span>
</div>
<div class="table-container" id="SearchSummary">
<table><tbody><tr><td>
	<div id="grid_result_summary"></div>
	<p>For a description of what you can do from this GO Search Results table, please see <a target="_blank" href="http://enews.patricbrc.org/go-search-faqs/">GO Search FAQs</a>.</p>
</td></tr></tbody></table>
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

Ext.onReady(function() {

var checkbox = createCheckBox("GO");
	
	var pageProperties = {
		name: "GO",
		model: ["GO"],
		items: 1,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		tree:null,
		treeDS:null,
		scm:[[checkbox,
			{header:"Genome Name",	dataIndex:'genome_name',	flex:1, renderer:renderGenomeName},
			{header:"Accession",	dataIndex:'accession',		flex:1, hidden:true, align:'center', renderer:renderAccession},
			{header:"Locus Tag",	dataIndex:'locus_tag', 		flex:1, align:'center', renderer:renderLocusTag},
			{header:"Gene Symbol",	dataIndex:'gene', 			flex:1, renderer:BasicRenderer},
			{header:"Product Description",	dataIndex:'product', 		flex:2, renderer:BasicRenderer},
			{header:"Annotation",	dataIndex:'annotation', 	flex:1, align:'center', renderer:BasicRenderer},
			{header:"GO Term ID",	dataIndex:'go_id', 			flex:1, align:'center', renderer:renderGOID},
			{header:"GO Term Description", dataIndex:'go_term', flex:2, renderer:BasicRenderer}]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{
			property: 'genome_name',
			direction:'ASC'
		},{
			property: 'locus_tag',
			direction:'ASC'
		}]],
		hash:{
			aP: [1],
			key: "<%=pk%>"
		},
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/GOSearch/GOSearchWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['golist']
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
	document.location.href = "GOSearch?cType="+Ext.getDom("cType").value+"&cId="+Ext.getDom("cId").value+"&dm=#keyword="+key+"&search_on=<%=search_on%>&annotation=<%=algorithm%>";
}
//]]>
</script>