<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
DBPathways conn_summary = new DBPathways();
String cId = request.getParameter("context_id");
%>

<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="PathwayTable" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
</form>

<div class="table-container">
<table>
<tr><td>
	<div id="grid_result_summary"></div>
	To learn how to filer, sort, manipulate, refine, and save data within PATRIC feature tables, 
	please see <a href="http://enews.patricbrc.org/feature-table-faqs/" target="_blank">Feature Table FAQs</a>.  
	Click on a pathway name to view a pathway map. 

</td></tr></table>
</div>
<div id="copy-button" style="display:none;"></div>	
<div id='PATRICGrid'></div>
<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function () {
	
	Ext.define('SinglePathway', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'pathway_id',	type:'string'},
			{name:'pathway_name',	type:'string'},
			{name:'pathway_class',	type:'string'},
			{name:'algorithm',	type:'string'},
			{name:'ec_number',	type:'string'},
			{name:'occurrence',	type:'string'},
			{name:'ec_name',		type:'string'},
			{name:'taxon_id',	type:'string'},
			{name:'na_feature_id',	type:'string'},
			{name:'genome_info_id',	type:'string'}
		]
	});
	
	var pageProperties = {
		name: "SinglePathway",
		model: ['SinglePathway'],
		items: 1,
		cart: false,
		cartType:'cart',
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{
			property: 'pathway_id',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1]
		},
		remoteSort:true,
		scm: [[{header:'Pathway ID', flex:1, dataIndex:'pathway_id', width: 100, sortable: true, renderer:BasicRenderer},
			  	{header:'Pathway Name', flex:1, dataIndex:'pathway_name', width: 260, sortable: true, renderer:renderPathwayName},
		   	    {header:'Pathway Class', flex:1, dataIndex:'pathway_class', width: 260, sortable: true, renderer:BasicRenderer}, 
		   	    {header:'Annotation', flex:1, dataIndex:'algorithm', sortable: true, align: 'center', renderer:BasicRenderer}, 
		   	    {header:'EC Number', flex:1, dataIndex:'ec_number', align:'center', width: 120, sortable: true, renderer:BasicRenderer},
		   	    {header:'Occurrence', flex:1, dataIndex:'occurrence', align:'center', width:120, renderer:BasicRenderer},
		   	    {header:'Description', flex:1, dataIndex:'ec_name', width: 220, renderer:BasicRenderer}]],
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();loadGrid();},
		stateId: ['singlepathway']
	};
				
	SetPageProperties(pageProperties),
	// SetIntervalOrAPI(),
	Ext.QuickTips.init();
	Ext.get("tabs_pathways") && Ext.get("tabs_pathways").addCls("sel");
	overrideButtonActions();	
	loadGrid(),
	$Page.doLayout();

});

function getExtraParams(){
	return  {id: Ext.getDom("cId").value	};
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
	Ext.getDom("grid_result_summary").innerHTML = '<b>'+store.totalCount+' pathways found</b>';
		
}

function renderPathwayName(value, p, record){
	
	var render_algorithm = "";

	if(record.data.algorithm == "Legacy BRC" )
		render_algorithm = "BRC";
	else
		render_algorithm = record.data.algorithm;

	return Ext.String.format("<a href=\"CompPathwayMap?cType=genome&cId={0}&dm=feature&feature_info_id={1}&map={2}&algorithm={3}&ec_number=\">{4}</a>", record.data.genome_info_id, record.data.na_feature_id, record.data.pathway_id, render_algorithm, record.data.pathway_name);

}

function DownloadFile(type){

	Ext.getDom("fTableForm").action = "/patric-pathways/jsp/grid_download_handler.jsp";
	Ext.getDom("fileformat").value = type;
	Ext.getDom("fTableForm").submit();

}
//]]>
</script>
