<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSummary" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSearch" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String staticRoot = System.getProperty("web.static", "/patric");
String resourceRoot = System.getProperty("web.resource", "");
String ncbi_taxon_id = null;//request.getParameter("ncbi_taxon_id");
String genome_info_id = null;//request.getParameter("genome_info_id");

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
if (cType.equals("taxon")) {
	ncbi_taxon_id = cId;
} else if (cType.equals("genome")) {
	genome_info_id = cId;
}

String featuretype = request.getParameter("featuretype");
String annotation = request.getParameter("annotation");
String filtertype = request.getParameter("filtertype");

if (ncbi_taxon_id == null) { ncbi_taxon_id = ""; }
if (genome_info_id == null) { genome_info_id = ""; }
if (featuretype == null || featuretype.equals("")) { featuretype = "CDS"; }
if (annotation == null || annotation.equals("")) { annotation = "PATRIC"; }
if (filtertype == null || filtertype.equals("")) { filtertype = ""; }

DBSummary conn_summary = new DBSummary();
HashMap<String,String> key = new HashMap<String,String>();

if (ncbi_taxon_id!=null && !ncbi_taxon_id.equals("")) {
	key.put("ncbi_taxon_id",ncbi_taxon_id);
}
if (genome_info_id!=null && !genome_info_id.equals("")) {
	key.put("genome_info_id",genome_info_id);
}
Iterator<String> itr = null;
String selected = "";

DBSearch db_search = new DBSearch();
String gid = "";	

if(cType.equals("genome")){
	gid += cId;
} else {
	
	if(!cId.equals("2")){	
		ArrayList<ResultType> items = db_search.getTaxonIdList(cId, cType, "", "", "");
		if (items.size() > 0) {
			gid += items.get(0).get("id");
			for (int i = 1; i < items.size(); i++) {
				gid += "##" + items.get(i).get("id");
			}
		} else {
			gid = "0";
		}
	}
}

%>
<form id="fTableForm" action="#" method="post">
	<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
	<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
	<input type="hidden" id="tablesource" name="tablesource" value="Feature" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />
	<input type="hidden" id="taxonId" name="taxonId" value="<%=ncbi_taxon_id%>" />
	<input type="hidden" id="genomeId" name="genomeId" value="<%=genome_info_id%>" />
	<input type="hidden" id="sort" name="sort" value="" />
	<input type="hidden" id="dir" name="dir" value="" />
	
	<!-- fasta download specific param -->
	<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
	<input type="hidden" id="fids" name="fids" value="" />
	<input type="hidden" id="download_keyword" name="download_keyword" value="" />
</form>

<div id="copy-button" class="x-hidden"></div>
<div class="far">
	<p>
	Feature tables contain all of the identified features for all of the genomes in a particular genus.  
	Tables may be refined to show subsets of features via various user controls, as described in <a href="http://enews.patricbrc.org/faqs/feature-table-faqs/" target="_blank">Feature Table FAQs</a>.
	</p>	
	<div id="panelFilter" style="display:block;">
		<div id="f_feature_type" style="float:left; padding:7px;"></div>
		<div id="f_annotation" style="float:left; padding:7px;"></div>
		<div id="f_keyword" style="float:left; padding:7px;"></div>
		<img src="/patric/images/filter_table.png" alt="Filter Table" onclick="filterFeatureTable()" style="cursor:pointer;vertical-align:middle; float:left; padding:7px;" />
	</div>
</div>
<div class="clear"></div>
<div id="grid_result_summary"></div>
<div id='PATRICGrid'></div>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/checkcolumn.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/featuretable.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="<%=staticRoot%>/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>

<script type="text/javascript">
//<![CDATA[ 
var $Page;
// Modernizr.history = false;
Ext.onReady(function()
{
	var checkbox = createCheckBox("Feature");
	var all_hidden = ["refseq_protein_id", "aa_length", "gene", "anticodon", "bound_moeity", "product"];
	var random = Math.floor(Math.random()*1000001);
	var pageProperties = {
		name: "Feature",
		model: ["Feature"],
		items: 1,
		cart: true,
		cartType: "cart",
		plugin:true,
		plugintype:"checkbox",
		scm:[[checkbox,
			{text:'Genome Name',		dataIndex:'genome_name',		orig_hidden_value: ("<%=cType%>" == "genome"),	hidden: ("<%=cType%>" == "genome")?true:false, flex:2,	renderer:renderGenomeName},
			{text:'Accession',			dataIndex:'accession',			orig_hidden_value: true,	hidden: true,	flex:1, renderer:renderAccession},
			{text:'Locus Tag',			dataIndex:'locus_tag',			orig_hidden_value: false,	flex:2, renderer:renderLocusTag},
			{text:'RefSeq Locus Tag',	dataIndex:'refseq_locus_tag',	orig_hidden_value: false,	flex:2, renderer:BasicRenderer},
			{text:'Gene Symbol',		dataIndex:'gene',				orig_hidden_value: false,	flex:1, renderer:BasicRenderer},
			{text:'Genome Browser',		dataIndex:'na_feature_id',		orig_hidden_value: true,	hidden: true,	align:'center', flex:1, sortable: false, renderer:renderGenomeBrowserByFeature},
			{text:'Annotation',			dataIndex:'annotation',			orig_hidden_value: true,	hidden: true,	flex:1, renderer:BasicRenderer},
			{text:'Feature Type',		dataIndex:'feature_type',		orig_hidden_value: true,	hidden: true,	flex:1, renderer:BasicRenderer}, 
			{text:'Start',				dataIndex:'start_max',			orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'End', 				dataIndex:'end_min', 			orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'Length (NT)',		dataIndex:'na_length',			orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'Strand',				dataIndex:'strand',				orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'Protein ID',			dataIndex:'refseq_protein_id',	orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'Length (AA)',		dataIndex:'aa_length',			orig_hidden_value: true,	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
			{text:'Anticodon',			dataIndex:'anticodon',			orig_hidden_value: true,	hidden: true,	flex:1, renderer:BasicRenderer},
			{text:'Product Description',dataIndex:'product',			orig_hidden_value: false,	flex:4, renderer:BasicRenderer},
			{text:'Bound Moiety',		dataIndex:'bound_moiety',		orig_hidden_value: true,	hidden: true,	flex:1, renderer:BasicRenderer}]],
		featureHiddenCols:{"CDS":["anticodon","bound_moiety"],
			"misc_RNA":["refseq_protein_id","aa_length","gene","bound_moeity"],
			"misc_binding":["refseq_protein_id","aa_length","gene","anticodon"],
			"misc_feature":["refseq_protein_id","aa_length","gene","anticodon","bound_moeity"],
			"misc_signal":["refseq_protein_id","aa_length","gene","anticodon","bound_moeity"],
			"ncRNA":["protein_id","aa_length","anticodon","bound_moeity"],
			"pseudogene":all_hidden,
			"rRNA":["protein_id","aa_length","anticodon","bound_moeity"],
			"region":all_hidden,
			"repeat_region":all_hidden,
			"source":all_hidden,
			"tRNA":["refseq_protein_id","aa_length","gene","bound_moeity"],
			"tmRNA":all_hidden,
			"transcript":["anticodon","bound_moiety"],
			"ALL":["refseq_protein_id","aa_length","anticodon","bound_moeity"]},
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
			fT: '<%=featuretype%>',
			alg: '<%=annotation%>',
			filter: '<%=filtertype%>',
			kW: '',
			key: random
		},
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){loadFBCD();},
		stateId: ['featurelist'],
		border: true,
		pagingBarMsg: ['Displaying features {0} - {1} of {2}']
	};
	
	SetPageProperties(pageProperties),
	$Page.checkbox = checkbox,
	Ext.QuickTips.init();
	overrideButtonActions(),
	loadFBCD(),
	$Page.doLayout(),
	SetIntervalOrAPI(),
	createLoadComboBoxes();
	if (Ext.get("tabs_featuretable")) {
		Ext.get("tabs_featuretable").addCls("sel");
	}
});

function getGID(){
	return '<%=gid%>';
}
//]]
</script>
