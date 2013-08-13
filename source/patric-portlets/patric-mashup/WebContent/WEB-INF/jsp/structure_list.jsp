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
 
<form action="#" id="fTableForm">
<input type="hidden" id="context_type" name="context_type" value="<%=cType %>" />
<input type="hidden" id="context_id" name="context_id" value="<%=cId %>" />
<input type="hidden" id="filter" name="filter" value="<%=filter%>" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="tablesource" name="tablesource" value="Structure" />
</form>
<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	var pageProperties = {
		name: "Structure",
		model: ["Structure"],
		items: 1,
		cart: true,
		cartType: "",
		plugin:true,
		plugintype:"rowexpander",
		WoWorkspace: true,
		pluginConfig:[{
            ptype: 'rowexpander',
            rowBodyTpl : ['<p><b>Organism:</b>{Organism}<br/><b>Title:</b>{PdbDescr}<br/><b>LigCode:</b> {LigCode}</p>']
    	}],
		scm:[[
	            {header:"Accession",		dataIndex:'PdbAcc',			flex:1,	renderer:renderAccessionLink},
			    {header:"Title",			dataIndex:'PdbDescr',		flex:4, renderer:BasicRenderer},
			    {header:"EC No",			dataIndex:'EC',				flex:1, renderer:BasicRenderer},
			    {header:"Class",			dataIndex:'PdbClass',		flex:2, renderer:BasicRenderer},
			    {header:"Experiment Method",dataIndex:'ExpMethod',		flex:2, renderer:BasicRenderer},
			    {header:"Resolution",		dataIndex:'Resolution', 	flex:1, renderer:BasicRenderer},
			    {header:"Date",				dataIndex:'PdbDepositDate', flex:1, renderer:BasicRenderer },
			    {header:"Link",				dataIndex:'',			flex:1, renderer:renderStructureLink},
			    {header:"Organism",			dataIndex:'Organism',		flex:1, hidden:true, renderer:BasicRenderer}
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
		stateId: ['structure']
	};
	
	SetPageProperties(pageProperties),
	Ext.QuickTips.init(),
	Ext.define('Structure', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'Id',	type:'string'},
			{name:'PdbAcc',	type:'string'},
			{name:'PdbDescr',	type:'string'},
			{name:'EC',	type:'string'},
			{name:'Resolution',	type:'string'},
			{name:'ExpMethod',	type:'string'},
			{name:'PdbClass',		type:'string'},
			{name:'PdbDepositDate',	type:'string'},
			{name:'Organism',	type:'string'},
			{name:'LigCode',	type:'string'},
			{name:'LigCount',		type:'string'},
			{name:'ModProteinResCount',	type:'string'},
			{name:'ModDNAResCount',	type:'string'},
			{name:'ModRNAResCount',	type:'string'},
			{name:'ProteinChainCount',	type:'string'},
			{name:'DNAChainCount',	type:'string'},
			{name:'RNAChainCount',	type:'string'},
			{name:'link_pdb',	type:'string'},
			{name:'link_ncbi',	type:'string'},
			{name:'link_jmol',	type:'string'}
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
	var msg = "<b>"+Ext.getStore('ds').getTotalCount()+" records found in Structure</b><br/>";
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

function renderAccessionLink(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="{0}" target="_blank">{1}</a>', record.data.link_ncbi, value);
}

function renderStructureLink(value, p, record) {
	if (record.data.PdbAcc != null) {
		var pdb_link = Ext.String.format('<a href="{0}" target="_blank">PDB</a>', record.data.link_pdb);
		var jmol_link = Ext.String.format('<a href="Jmol?structureID={0}" target="_blank">Jmol</a>', record.data.PdbAcc);

		return pdb_link+", "+jmol_link;
	} else {
		return "";
	}
}

//]]>
</script>