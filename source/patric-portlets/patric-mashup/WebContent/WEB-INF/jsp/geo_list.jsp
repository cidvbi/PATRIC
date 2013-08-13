<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String filter = request.getParameter("filter");
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
	<p>
		<a href="ExperimentData?cType=<%=cType %>&amp;cId=<%=cId %>&amp;kw=Experiment%20Data">View all Experiment Data for <%=organism_name %>.</a>
	</p>

<div id="PATRICGrid" style=""></div>

<form action="#" id="fTableForm">
<input type="hidden" id="context_type" name="context_type" value="<%=cType %>" />
<input type="hidden" id="context_id" name="context_id" value="<%=cId %>" />
<input type="hidden" id="filter" name="filter" value="<%=filter %>" />
<input type="hidden" id="keyword" name="keyword" value="<%=keyword %>" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="tablesource" name="tablesource" value="GEO" />
</form>
<div id="copy-button" class="x-hidden"></div>
<div id="PATRICGrid" style=""></div>
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
	var pageProperties = {
		name: "GEO",
		model: ["GEO"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Title:</b>{title}<br/>',
                '<b>Summary:</b> {summary}<br/>',
                '<b>Subset Info</b>:{subsetInfo}</p>']
    	}],
		scm:[[
				{header:"Data Type",	dataIndex:'dataType',	flex:1, renderer:BasicRenderer},
			    {header:"ID",			dataIndex:'ID',			flex:1, renderer:renderID},
			    {header:"Title",		dataIndex:'title',		flex:2, renderer:BasicRenderer},
			    {header:"Organism",		dataIndex:'taxon',		flex:2, renderer:BasicRenderer},
			    {header:"Experiment Type",	dataIndex:'expType', flex:2, renderer:BasicRenderer},
			    {header:"Samples",		dataIndex:'n_samples',	flex:1, align:'right', renderer:BasicRenderer},
			    {header:"Publication",	dataIndex:'pubmed_id',	flex:1, align:'center', renderer:renderPubMed},
			    {header:"Download",		dataIndex:'',			flex:2, renderer:renderDownload},
			    {header:"Date",			dataIndex:'PDAT',		flex:1, renderer:BasicRenderer}
		]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		hash:{
			aP: [1]
		},
		remoteSort:false,
		gridType: "",
		border: true,
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['geo']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('GEO', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'dataType',	type:'string'},
			{name:'ID',	type:'string'},
			{name:'title',	type:'string'},
			{name:'taxon',	type:'string'},
			{name:'expType',	type:'string'},
			{name:'n_samples',	type:'string'},
			{name:'pubmed_id',		type:'string'},
			{name:'PDAT',	type:'string'},
			{name:'suppFile',	type:'string'},
			{name:'link_soft_format',	type:'string'},
			{name:'link_miniml_format',		type:'string'},
			{name:'link_seriesmatrix_format',	type:'string'},
			{name:'link_supplementary',	type:'string'},
			{name:'summary',		type:'string'},
			{name:'ptechType',	type:'string'},
			{name:'subsetInfo',	type:'string'}
		]
	}),
	SetIntervalOrAPI(),
	loadGrid(),
	$Page.doLayout();
});           
        
function getExtraParams(){
	return {
		cType:	Ext.getDom("context_type").value,
		cId: Ext.getDom("context_id").value,
    	filter:	Ext.getDom("filter").value,
    	keyword: Ext.getDom("keyword").value
	};
}

function CallBack(){
	var msg = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in GEO</b><br/>";
	Ext.getDom('grid_result_summary').innerHTML = msg;
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
   
function renderID(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc={0}" target="_blank">{0}</a>', value);
}

function renderDownload(value, p, record) {
	var link = "";
	
	if (record.data.dataType == 'Series') {
		// Series
		link += Ext.String.format('<a href="{0}">SOFT</a>', record.data.link_soft_format);
		link += Ext.String.format(' | <a href="{0}">MINiML</a>', record.data.link_miniml_format);
		link += Ext.String.format(' | <a href="{0}">SeriesMatrix</a>', record.data.link_seriesmatrix_format);
		if (record.data.suppFile != '') {
			link += Ext.String.format(' | <a href="{0}">Supplementary</a>', record.data.link_supplementary);
		}
	}
	else if (record.data.dataType == 'Platform') {
		// Platform
		link += Ext.String.format('<a href="{0}">SOFT</a>', record.data.link_soft_format);
		link += Ext.String.format(' | <a href="{0}">MINiML</a>', record.data.link_miniml_format);
		if (record.data.suppFile != '') {
			link += Ext.String.format(' | <a href="{0}" target="_blank">Supplementary</a>', record.data.link_supplementary);
		}
	}
	else if (record.data.dataType == 'Datasets') {
		// Datasets
		link = Ext.String.format('<a href="{0}">SOFT</a>', record.data.link_soft_format);
	}

	return link;
}

//]]>
</script>