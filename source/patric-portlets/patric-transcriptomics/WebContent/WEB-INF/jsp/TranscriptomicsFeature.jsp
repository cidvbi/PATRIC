<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");
String featureIds = "";
HashMap<String, String> key = (HashMap<String, String>) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);

if(key != null && key.containsKey("feature_info_id")){
	featureIds = key.get("feature_info_id");
}

%>
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
<input type="hidden" id="_tablesource" name="_tablesource" value="TranscriptomicsGeneFeature" />
<input type="hidden" id="_fileformat" name="_fileformat" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="featureIds" name="featureIds" value="<%=featureIds %>" />
<input type="hidden" id="key" name="key" value="<%=pk%>" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />
</form>

<div id="copy-button" style="display:none;"></div>
<div class="table-container">
<table width="100%"><tr><td>
	<div id="grid_result_summary"></div>
	<p>
	Feature tables contain all of the identified features for all of the genomes in a particular genus.  
	Tables may be refined to show subsets of features via various user controls, as described in <a href="http://enews.patricbrc.org/faqs/feature-table-faqs/" target="_blank">Feature Table FAQs</a>.
	</p>
</td></tr></table>
</div>
<div id='PATRICGrid'></div>

<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/checkcolumn.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("Feature");

	Ext.define('Feature', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'genome_info_id',	type:'int'},
			{name:'gid',	type:'int'},
			{name:'genome_name',	type:'string'},
			{name:'accession',	type:'string'},
			{name:'locus_tag',	type:'string'},
			{name:'refseq_locus_tag',	type:'string'},
			{name:'na_feature_id',	type:'int'},
			{name:'annotation',	type:'string'},
			{name:'feature_type',		type:'string'},
			{name:'start_max',	type:'int'},
			{name:'end_min',	type:'int'},
			{name:'na_length',	type:'int'},
			{name:'strand',		type:'string'},
			{name:'protein_id',	type:'string'},
			{name:'aa_length',	type:'int'},
			{name:'gene',		type:'string'},
			{name:'bound_moiety',	type:'string'},
			{name:'anticodon',	type:'string'},
			{name:'product',	type:'string'},
			{name:'debug_field',	type:'string'}
		]
	});
	
	var pageProperties = {
		name: "Feature",
		model: ["Feature"],
		items: 1,
		cart: true,
		cartType: "cart",
		plugin:true,
		plugintype:"checkbox",
		scm:[[checkbox,
				{text:'Genome Name',			dataIndex:'genome_name',		flex:2, renderer:renderGenomeName},
				{text:'Accession',				dataIndex:'accession',			flex:1, hidden:true, renderer:renderAccession},
				{text:'Locus Tag',				dataIndex:'locus_tag',			flex:1, renderer:renderLocusTag},
				{text:'RefSeq Locus Tag',		dataIndex:'refseq_locus_tag',	flex:1, renderer:BasicRenderer},
				{text:'Gene Symbol',			dataIndex:'gene',				flex:1, renderer:BasicRenderer},
				{text:'Genome Browser',			dataIndex:'',					flex:1, hidden:true, align:'center', sortable:false, renderer:renderGenomeBrowserByFeature},
				{text:'Annotation',				dataIndex:'annotation',			flex:1, hidden:true, renderer:BasicRenderer},
				{text:'Feature Type',			dataIndex:'feature_type',		flex:1,	hidden:true, renderer:BasicRenderer}, 
				{text:'Start',					dataIndex:'start_max',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
				{text:'End', 					dataIndex:'end_min',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
				{text:'Length (NT)',			dataIndex:'na_length',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
				{text:'Strand',					dataIndex:'strand',				flex:1, hidden:true, align:'right', renderer:BasicRenderer},
				{text:'Length (AA)',			dataIndex:'aa_length',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
				{text:'Product Description',	dataIndex:'product',			flex:3, renderer:BasicRenderer}]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{
			property: 'locus_tag',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1]
		},
		remoteSort:true,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['TRhmfeaturelist']
	};
	
	SetPageProperties(pageProperties);
	$Page.checkbox = checkbox;
	SetIntervalOrAPI();
	Ext.QuickTips.init();
	if(Ext.get("tabs_explist"))
		Ext.get("tabs_explist").addCls("sel");
	overrideButtonActions(),
	loadGrid();
});           
        
function getExtraParams(){	
	return {
		pk:Ext.getDom("key").value
		,callType:'getFeatureTable'
	};
}

function CallBack(){
	
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		which = hash.aT?hash.aT:0;
	
	if(Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();	
	
	Ext.getDom("grid_result_summary").innerHTML = '<b>'+Page.getStore(which).getTotalCount()+' features found</b>';
	
}


function DownloadFile(type){
	"use strict";
	
	var form = Ext.getDom("fTableForm");
	
	form.action = "/patric-transcriptomics/jsp/GetDetailTable.jsp",
	form.target = "",
	form._fileformat.value = arguments[0];
	getHashFieldsToDownload(form);
	form.submit();	
	
};

function getSelectedFeatures() {
	"use strict";
	
	var Page = $Page,
		property = Page.getPageProperties(),
		sl = Page.getCheckBox().getSelections(),
		i,
		fids = property.fids;
	
	for (i=0; i<sl.length;i++) 
		fids.push(sl[i].data.na_feature_id);
};     
// ]]
</script>
