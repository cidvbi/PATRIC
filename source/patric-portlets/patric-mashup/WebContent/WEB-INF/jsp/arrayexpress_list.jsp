<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String keyword = request.getParameter("keyword");

DBShared conn_shared = new DBShared();
String organism_name = "";
if (cType.equals("taxon")) {
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
	if (parents.size() > 0) {
		organism_name = parents.get(0).get("name");
	}
} else if (cType.equals("genome")) {
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	organism_name = names.get("genome_name");
}
%>

<div id="grid_result_summary"></div>
<p><a href="ExperimentData?cType=<%=cType %>&amp;cId=<%=cId %>&amp;kw=Experiment%20Data">View all Experiment Data for <%=organism_name %>.</a></p>

<div id="PATRICGrid" style=""></div>
<form action="#" id="fTableForm">
<input type="hidden" id="context_type" name="context_type" value="<%=cType %>" />
<input type="hidden" id="context_id" name="context_id" value="<%=cId %>" />
<input type="hidden" id="keyword" name="keyword" value="<%=keyword %>" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="tablesource" name="tablesource" value="ArrayExpress" />
</form>
<div id="copy-button" class="x-hidden"></div>
<script type="text/javascript" src="/patric/js/extjs/extjs/examples/ux/RowExpander.js"></script>
<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>
<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	Ext.define('ArrayExpress', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'id',			type:'string'},
			{name:'accession',	type:'string'},
			{name:'name',		type:'string'},
			{name:'species',	type:'string'},
			{name:'experimenttype',	type:'string'},
			{name:'assays',		type:'string'},
			{name:'samples',	type:'string'},
			{name:'pubmed_id',	type:'string'},
			{name:'link_data',	type:'string'},
			{name:'releasedate',type:'string'},
			{name:'description',type:'string'},
			{name:'experimentdesign',	type:'string'},
			{name:'secondaryaccession',	type:'string'}
		]
	});
	
	var pageProperties = {
		name: "ArrayExpress",
		model: ["ArrayExpress"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
			id :'RowExpander',
			ptype: 'rowexpander',
			rowBodyTpl : ['<p><b>Title:</b>{name}<br/><b>Description:</b> {description}<br/><b>Experiment Design:</b> {experimentdesign}</p>']
		}],
		scm:[[
				{header:"ID",		dataIndex:'accession',		flex:1, sortable:false, renderer:renderArExAccession},
				{header:"Title",	dataIndex:'name',			flex:2, sortable:false, renderer:BasicRenderer},
				{header:"Organism",	dataIndex:'species',		flex:2, sortable:false, renderer:BasicRenderer},
				{header:"Type",		dataIndex:'experimenttype',	flex:2, sortable:false, renderer:BasicRenderer},
				{header:"Assays",	dataIndex:'assays',			flex:1, sortable:false, align:'right', renderer:BasicRenderer},
				{header:"Samples",	dataIndex:'samples',		flex:1, sortable:false, align:'right', renderer:BasicRenderer},
				{header:"Publication",	dataIndex:'pubmed_id',	flex:1, sortable:false, align:'center', renderer:renderPubMed},
				{header:"Data",		dataIndex:'link_data',		flex:1, sortable:false, renderer:renderDataLink},
				{header:"Date",		dataIndex:'releasedate',	flex:1, sortable:false, renderer:BasicRenderer}
		]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		hash:{
			aP: [1]
		},
		remoteSort:false,
		gridType: "",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['arrayexpress'],
		pagingBarMsg: ['Displaying experiments {0} - {1} of {2}']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('ArrayExpress', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'id',			type:'string'},
			{name:'accession',	type:'string'},
			{name:'name',		type:'string'},
			{name:'species',	type:'string'},
			{name:'experimenttype',	type:'string'},
			{name:'assays',		type:'string'},
			{name:'samples',	type:'string'},
			{name:'pubmed_id',	type:'string'},
			{name:'link_data',	type:'string'},
			{name:'releasedate',type:'string'},
			{name:'description',type:'string'},
			{name:'experimentdesign',	type:'string'},
			{name:'secondaryaccession',	type:'string'}
		]
	}),
	SetIntervalOrAPI(),
	loadGrid(),
	$Page.doLayout();
});

function getExtraParams(){
	return {
		cType:	Ext.getDom("context_type").value,
		cId:	Ext.getDom("context_id").value,
		keyword:Ext.getDom("keyword").value
	};
}

function CallBack(){
	var Page = $Page,
		store = Page.getStore(0);
	
	Ext.getDom("grid_result_summary").innerHTML = '<b>'+store.totalCount+' records found in ArrayExpress</b>';
}

function DownloadFile() {
	"use strict";
	
	var form = Ext.getDom("fTableForm");
	form.action = "/patric-mashup/jsp/table_download_handler.jsp",
	form.target = "",
	form.fileformat.value = arguments[0];
	getHashFieldsToDownload(form);
	form.submit();
}

function renderArExAccession(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://www.ebi.ac.uk/microarray-as/ae/browse.html?keywords={0}&amp;detailedview=on" target="_blank">{0}</a>', value);
}

function renderDataLink(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="Download" data-qclass="x-tip"';
	return Ext.String.format('<a href="{0}" target="_blank">Download</a>', value);
}
//]]>
</script>