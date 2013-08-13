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
<input type="hidden" id="tablesource" name="tablesource" value="PRC" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />
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
		name: "PRC",
		model: ["PRC"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Description:</b>{description}<br/><b>Summary:</b>{summary}</p>']
    	}],
		scm:[[
			    {header:"ID",			dataIndex:'experiment_id',			flex:1, renderer:renderPRCLink},
			    {header:"Description",	dataIndex:'description',	flex:4, renderer:BasicRenderer},
			    {header:"Organism",		dataIndex:'speciesname',	flex:3, renderer:BasicRenderer},
			    {header:"Type",			dataIndex:'experimenttype',	flex:2, renderer:BasicRenderer},
			    {header:"Samples",		dataIndex:'samples',		flex:1, align:'right', renderer:BasicRenderer},
			    {header:"Publication",	dataIndex:'pubmed_id',		flex:1, align:'center', renderer:renderPubMed}
			]],
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort:[[{
			property:'experiment_id',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1]
		},
		remoteSort:true,
		gridType: "",
		border: true,
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['prc']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('PRC', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'experiment_id',			type:'string'},
			{name:'speciesname',	type:'string'},
			{name:'description',	type:'string'},
			{name:'summary',		type:'string'},
			{name:'samples',		type:'string'},
			{name:'experimenttype',	type:'string'},
			{name:'pubmed_id',		type:'string'}
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

function renderPRCLink(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/DataSetSummaryPage?windowstate=maximized&amp;action=a&amp;expid={0}" target="_blank">{0}</a>', value);
}

//]]>
</script>