<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String filter = request.getParameter("filter");

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
<input type="hidden" id="filter" name="filter" value="<%=filter %>" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="tablesource" name="tablesource" value="Peptidome" />
</form>
<div id="copy-button"></div>
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
		name: "Peptidome",
		model: ["Peptidome"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Title:</b>{title}<br/>',
                '<b>Summary:</b>{summary}</p>']
    	}],
		scm:[[
	            {header: "Accession",	dataIndex: 'Accession',		flex:1,		renderer:renderPeptidomeAccession, sortable:false},
	            {header: "Title",		dataIndex: 'title',			flex:3,		renderer:BasicRenderer, sortable:false},
	            {header: "Organism",	dataIndex: 'TaxName',		flex:2,		renderer:BasicRenderer, sortable:false},
	            {header: "Samples",		dataIndex: 'SampleCount',	flex:1,		align:'right',	renderer:BasicRenderer, sortable:false},
	            {header: "Proteins",	dataIndex: 'ProteinCount',	flex:1,		align:'right',	renderer:BasicRenderer, sortable:false},
	            {header: "Peptides",	dataIndex: 'PeptideCount',	flex:1,		align:'right',	renderer:BasicRenderer, sortable:false},
	            {header: "Spectra",		dataIndex: 'SpectraCount',	flex:1,		align:'right',	renderer:BasicRenderer, sortable:false},
	            {header: "Publication",	dataIndex: 'pubmed_id',		flex:1,		renderer:renderPubMed,	sortable:false},
	            {header: "Data File", 	dataIndex: 'link_data_file',flex:1,		renderer:renderDataLink,	sortable:false}
        	]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		hash:{
			aP: [1]
		},
		remoteSort:false,
		gridType: "",
		border:true,
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();}
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('Peptidome', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'Accession',	type:'string'},
			{name:'title',	type:'string'},
			{name:'TaxName',	type:'string'},
			{name:'SampleCount',	type:'string'},
			{name:'ProteinCount',	type:'string'},
			{name:'PeptideCount',	type:'string'},
			{name:'SpectraCount',		type:'string'},
			{name:'pubmed_id',	type:'string'},
			{name:'link_data_file',	type:'string'},
			{name:'summary',	type:'string'},
			{name:'int',		type:'string'},
			{name:'entryType',	type:'string'}
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
    	filter:	Ext.getDom("filter").value
	};
}

function CallBack(){
	var msg = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in Peptidome</b><br/>";
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
           
function renderPeptidomeAccession(value, metadata, record, rowIndex, colIndex, store) {

	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	var link = "";
	
	if (value != null && value != "") {
		link = Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/peptidome/repository/{0}" target="_blank">{0}</a>',value);
	}
	return link;
}

function renderDataLink(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="Link" data-qclass="x-tip"';
	return Ext.String.format('<a href="{0}" target="_blank">Link</a>', value);
}
//]]>
</script>