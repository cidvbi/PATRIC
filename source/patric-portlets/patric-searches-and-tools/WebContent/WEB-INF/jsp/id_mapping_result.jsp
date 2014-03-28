<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.SiteHelper"%> 
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");

ResultType key = (ResultType) portletSession.getAttribute("key"+pk);
String to = "", from = "", keyword = "";

if(key != null){
	to = key.get("to");
	from = key.get("from");
	keyword = key.get("keyword");
}
String renderURL = "";
if(to.equals("PATRIC Locus Tag"))
	renderURL = SiteHelper.getExternalLinks(from);
else
	renderURL = SiteHelper.getExternalLinks(to);
	
String x = keyword; 
int keyword_size = x.replaceAll("[,\\s]+",",").split(",").length;


%>
<div style="display:none">
<form id="fTableForm" name="searchForm" action="#" method="post" onsubmit="return false;">
<input type="hidden" id="tablesource" name="tablesource" value="IDMapping" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />

<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
<input type="hidden" id="keyword" name="keyword" value="<%=keyword %>" />
<input type="hidden" id="to" name="to" value="<%=to %>" />
<input type="hidden" id="from" name="from" value="<%=from %>" />
</form>
</div>
<div style="padding:3px;">
<input type="button" class="button leftarrow" id="search_modify" value="Modify Search Criteria" onclick="returntoSearchPage();"/>
</div>

<div id="SearchSummary">
	<div id="grid_result_summary"></div>
	<p>To learn how to filer, sort, manipulate, refine, and save data within PATRIC feature tables, 
	please see <a href="http://enews.patricbrc.org/feature-table-faqs/" target="_blank">Feature Table FAQs</a>.</p>
</div>
<div id="copy-button" class="x-hidden"></div>
<div id='PATRICGrid'></div>
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
<script type="text/javascript" src="/patric-searches-and-tools/js/idmapping_grid.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function() {

	Ext.define('IDMap', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'genome_info_id',	type:'string'},
			{name:'genome_name',	type:'string'},
			{name:'accession',	type:'string'},
			{name:'locus_tag',	type:'string'},
			{name:'na_feature_id',	type:'string'},
			{name:'algorithm',	type:'string'},
			{name:'name',		type:'string'},
			{name:'start_max',	type:'string'},
			{name:'end_min',	type:'string'},
			{name:'na_length',	type:'string'},
			{name:'strand',	type:'string'},
			{name:'protein_id',	type:'string'},
			{name:'aa_length',	type:'string'},
			{name:'gene',	type:'string'},
			{name:'bound_moiety',	type:'string'},
			{name:'anticodon',	type:'string'},
			{name:'product',	type:'string'},
			{name:'debug_field',	type:'string'},
			{name:'uniprotkb_accession',	type:'string'},
			{name:'uniprot_id',	type:'string'},
			{name:'refseq_source_id',	type:'string'},
			{name:'gene_id',	type:'string'},
			{name:'gi_number',	type:'string'},
			{name:'requested_data',	type:'string'},
			{name:'pseed_id',	type:'string'}
		]
	});

	var pageProperties = {
		name: "IDMapping",
		model: ["IDMap"],
		items: 1,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		scm:[],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{/*
			property: 'genome_name',
			direction: 'ASC'
		},{*/
			property: 'locus_tag',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1],
			key: '<%=pk%>',
			to:'<%=to%>',
			from:'<%=from%>'
		},
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){SetLoadParameters();loadFBCD();},
		renderURL:'<%=renderURL%>',
		keyword_size: '<%=keyword_size%>',
		stateId: ['idmapping']
	};
	
	SetPageProperties(pageProperties),
	SetLoadParameters();
	
	//SetIntervalOrAPI(),
	Ext.QuickTips.init(),
	overrideButtonActions();
	loadFBCD(),
	$Page.doLayout();
});

function returntoSearchPage(){
	document.location.href = "IDMapping?cType="+Ext.getDom("cType").value+"&cId="+Ext.getDom("cId").value+"&dm=X&pk=<%=pk%>";
}
//]]>
</script>
