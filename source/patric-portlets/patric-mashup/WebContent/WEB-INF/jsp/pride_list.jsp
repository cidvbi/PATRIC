<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String qKeyword = request.getParameter("keyword");

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
<form action="#" id="fTableForm">
<input type="hidden" id="context_type" name="context_type" value="<%=cType %>" />
<input type="hidden" id="context_id" name="context_id" value="<%=cId %>" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="tablesource" name="tablesource" value="PRIDE" />
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
		name: "PRIDE",
		model: ["PRIDE"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Experiment Title:</b>{experiment_title}<br/><b>Short Label:</b> {experiment_short_title}</p>']
    	}],
		scm:[[
	            {header:"Accession",	dataIndex:'experiment_ac',			flex:1, renderer:renderExAccession},
	            {header:"Title",		dataIndex:'experiment_title',		flex:3, renderer:BasicRenderer},
	            {header:"Short Label",	dataIndex:'experiment_short_title',	flex:2, renderer:BasicRenderer},
	            {header:"Organism",		dataIndex:'newt_name',				flex:2, renderer:BasicRenderer},
	            {header:"Publication",	dataIndex:'pubmed_id',				flex:1, align:'center', renderer:renderPubMed},
	            {header:"Data File",	dataIndex:'link_data_file',			flex:1, renderer:renderDataLink}
    	]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		hash:{
			aP: [1]
		},
		remoteSort:false,
		border:true,
		gridType: "",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['pride']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('PRIDE', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'project_id',		type:'string'},
			{name:'project_name',	type:'string'},
			{name:'experiment_ac',	type:'string'},
			{name:'experiment_title',	type:'string'},
			{name:'experiment_short_title',	type:'string'},
			{name:'pubmed_id',		type:'string'},
			{name:'newt_name',		type:'string'},
			{name:'newt_ac',		type:'string'},
			{name:'link_data_file',	type:'string'}
		]
	}),
	SetIntervalOrAPI(),
	loadGrid(),
	$Page.doLayout();
});              

function getExtraParams(){
	return {
		cType:	Ext.getDom("context_type").value,
		cId: Ext.getDom("context_id").value
	};
}

function CallBack(){
	var msg = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in PRC</b><br/>";
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

function renderExAccession(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://www.ebi.ac.uk/pride/directLink.do?experimentAccessionNumber={0}" target="_blank">{0}</a>', value);
}

function renderDataLink(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="Download" data-qclass="x-tip"';
	return Ext.String.format('<a href="{0}">Download</a>', value);
}

//]]>
</script>