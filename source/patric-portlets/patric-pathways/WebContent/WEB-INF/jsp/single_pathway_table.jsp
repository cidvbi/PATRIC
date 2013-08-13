<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%

DBPathways conn_pathways = new DBPathways();

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");

String ec_number = "", algorithm = "", map  = "", genomeId  = "";

ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);

if(key != null && key.containsKey("algorithm"))
	algorithm = key.get("algorithm");


if(key != null && key.containsKey("ec_number")) 
	ec_number = key.get("ec_number");


if(key != null && key.containsKey("map"))
	map = key.get("map");

if(key != null && key.containsKey("genomeId"))
	genomeId = key.get("genomeId");

%>
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="MapFeatureTable" />
<input type="hidden" id="pk" name="pk" value="<%=(pk!=null)?pk:"" %>" />
<input type="hidden" id="genomeId" name="genomeId" value="<%=(genomeId!=null)?genomeId:"" %>" />
<input type="hidden" id="ec_number" name="ec_number" value="<%=ec_number %>" /> 
<input type="hidden" id="algorithm" name="algorithm" value="<%=algorithm %>" /> 
<input type="hidden" id="map" name="map" value="<%= map%>" />

<!-- fasta download specific param -->
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="fileformat" name="fileformat" value="" />

</form>

<div id="copy-button"  style="display:none"></div>
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
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("GridPathwaySingle");
	
	Ext.define('Feature', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'genome_info_id',	type:'int'},
			{name:'genome_name',	type:'string'},
			{name:'accession',	type:'string'},
			{name:'locus_tag',	type:'string'},
			{name:'na_feature_id',	type:'int'},
			{name:'algorithm',	type:'string'},
			{name:'name',		type:'string'},
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
		name: "GridPathwaySingle",
		model: ['Feature'],
		items: 1,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		extraParams:getExtraParams,
		callBackFn:CallBack,
		pk:"<%=pk%>",
		sort: [[{
			property: 'genome_name',
			direction: 'ASC'
		},{
			property: 'accession',
			direction: 'ASC'
		},{
			property: 'start_max',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1]
		},
		remoteSort:true,
		fids: [],
		scm :[[checkbox, 
		 		{header:'Genome Name', flex:2, dataIndex:'genome_name', renderer:renderGenomeName}, 
				{header:'Accession', flex:1, dataIndex:'accession', hidden:true, renderer:renderAccession}, 
				{header:'Locus Tag', flex:1, dataIndex:'locus_tag', renderer:renderLocusTag},
				{header:'Gene Symbol', flex:1, dataIndex:'gene', renderer:BasicRenderer},
				{header:'Genome Browser', dataIndex:'', align:'center', hidden:true, renderer:renderGenomeBrowserByFeature}, 
				{header:'Annotation', flex:1, dataIndex:'algorithm', hidden:true, renderer:BasicRenderer}, 
				{header:'Feature Type', sortable: false, dataIndex:'name', hidden:true, align:'center', renderer:BasicRenderer}, 
				{header:'Start', flex:1, dataIndex:'start_max', hidden:true, align:'right', renderer:BasicRenderer}, 
				{header:'End', flex:1, dataIndex:'end_min',hidden:true,  align:'right', renderer:BasicRenderer}, 
				{header:'Length (NT)', flex:1, dataIndex:'na_length', hidden:true, align:'right', renderer:BasicRenderer}, 
				{header:'Strand', flex:1, dataIndex:'strand', hidden:true, align:'center', renderer:BasicRenderer},
				{header:'Product Description', flex:3, dataIndex:'product', align:'left', renderer:BasicRenderer}]],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['PWhmfeaturelist']
	};
	
	SetPageProperties(pageProperties),
	$Page.checkbox = checkbox,
	// SetIntervalOrAPI(),
	Ext.QuickTips.init();
	Ext.get("tabs_pathways") && Ext.get("tabs_pathways").addCls("sel");
	overrideButtonActions(),	
	loadGrid(),
	$Page.doLayout();
});

function getExtraParams(){
	var Page = $Page,
		property = Page.getPageProperties();
	
	return {
		pk: property.pk?property.pk:"", 
		callType: "show"};
}

function CallBack(){
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		which = hash.aT?hash.aT:0,
		store = Page.getStore(which),
		grid = Page.getGrid();

	if(grid.sortchangeOption)
		grid.setSortDirectionColumnHeader();
	
	Ext.getDom("grid_result_summary").innerHTML = '<b>'+store.totalCount+' features found</b>';
}

function getSelectedFeatures(actiontype, showdownload, fastatype, to){
	"use strict";
	
	var Page = $Page,
		property = Page.getPageProperties(),
		sl = Page.getCheckBox().getSelections(),
		i,
		fids = property.fids;
	
	for (i=0; i<sl.length;i++) 
		fids.push(sl[i].data.na_feature_id);
}

function DownloadFile(type){

	Ext.getDom("fTableForm").action = "/patric-pathways/jsp/grid_download_handler.jsp";
	Ext.getDom("fTableForm").target = "";
	Ext.getDom("fileformat").value = type;
	Ext.getDom("fTableForm").submit();
	

}
// ]]>
</script>