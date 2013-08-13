<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

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
<input type="hidden" id="tablesource" name="tablesource" value="IntAct" />
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
		name: "Intact",
		model: ["Intact"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Participants:</b>{participants}<br/><b>Experiments:</b> {experiments}<br/></p>']
    	}],
		scm:[[
				{header:"Accession",		dataIndex:'interaction_ac',		flex:1, renderer:renderInteractionAccession},
				{header:"Label",			dataIndex:'label',				flex:2, renderer:BasicRenderer},
				{header:"Interaction Type",	dataIndex:'interaction_type',	flex:2, renderer:BasicRenderer},
				{header:"Count Participants",	dataIndex:'count_participants',	flex:1, renderer:BasicRenderer},
				{header:"Count Experiments",	dataIndex:'count_exp_ref',		flex:1, hidden:true, renderer:BasicRenderer},
				{header:"Exp Name",			dataIndex:'exp_name',			flex:2, renderer:BasicRenderer},
				{header:"Method",			dataIndex:'exp_method',			flex:2, renderer:BasicRenderer},
				{header:"Organism",			dataIndex:'exp_org',			flex:1, renderer:BasicRenderer},
				{header:"Publication",		dataIndex:'exp_pubmed',			flex:1, renderer:renderPubMed},
				{header:"Count InteractionType",	dataIndex:'count_interaction_type',	flex:1, hidden:true, renderer:BasicRenderer}
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
		stateId: ['intact']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('Intact', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'interaction_ac',	type:'string'},
			{name:'interaction_type',	type:'string'},
			{name:'count_participants',	type:'string'},
			{name:'count_interaction_type',	type:'string'},
			{name:'count_exp_ref',	type:'string'},
			{name:'label',	type:'string'},
			{name:'participants',		type:'string'},
			{name:'experiments',	type:'string'},
			{name:'exp_name',	type:'string'},
			{name:'exp_method',	type:'string'},
			{name:'exp_org',		type:'string'},
			{name:'exp_pubmed',	type:'string'}
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
	var msg = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in IntAct</b><br/>";
	Ext.getDom('grid_result_summary').innerHTML = msg;
}

function renderInteractionAccession(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://www.ebi.ac.uk/intact/pages/details/details.xhtml?interactionAc={0}" target="_blank">{0}</a>', value);
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
//]]>
</script>