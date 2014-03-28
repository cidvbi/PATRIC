<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%

DBPathways conn_pathways = new DBPathways();

HashMap<String,String> key = new HashMap<String,String>();

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String algorithm = request.getParameter("algorithm");
String ec_number = request.getParameter("ec_number");

key.put("cId", cId);
	
key.put("cType", cType);

if(algorithm != null && !algorithm.equals(""))
	key.put("algorithm", algorithm);

if(ec_number == null)
	ec_number="";

if(algorithm != null && !algorithm.equals("")){
	if(algorithm.equals("BRC") || algorithm.equals("Legacy BRC"))
		algorithm = "Curation";
	else if(algorithm.equals("PATRIC"))
		algorithm = "RAST";
	else if(algorithm.equals("ALL"))
		algorithm = "";
}
%>

	<form id="fTableForm" action="#" method="post">
	<input type="hidden" id="tablesource" name="tablesource" value="CompPathwayTable" />
	<input type="hidden" id="cType" name="cType" value="<%=(cType!=null)?cType:"" %>" />	
	<input type="hidden" id="cId" name="cId" value="<%=(cId!=null)?cId:"" %>"/>
	<input type="hidden" id="alg" name="alg" value="<%=algorithm%>"/>
	<input type="hidden" id="ecN" name="ecN" value="<%=ec_number %>" />
	<input type="hidden" id="pId" name="pId" value="" />
	<input type="hidden" id="pClass" name="pClass" value="" />
	<input type="hidden" id="sort" name="sort" value="" />
	<input type="hidden" id="dir" name="dir" value="" />
	
	<!-- fasta download specific param -->
	
	<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
	<input type="hidden" id="fids" name="fids" value="" />
	<input type="hidden" id="aT" name="aT" value="" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />
	</form>
<div id="copy-button" style="display:none"></div>
<p>
	For a description of what you can do from this Comparative Pathways page, please see
	<a href="http://enews.patricbrc.org/comparative-pathway-tool-faqs/" target="_blank">Comparative Pathway Tool FAQs</a>.
</p> 

<div id="panelFilter" style="display:block;">
	<div id="f_pathway_class" style="float:left; padding:7px;"></div>
	<div id="f_pathway_name" style="float:left; padding:7px;"></div>
	<div id="f_ec_number" style="float:left; padding:7px;"></div>
	<div id="f_algorithm" style="float:left; padding:7px;"></div>
	<img src="/patric/images/filter_table.png" alt="Filter Table" onclick="filter()" style="cursor:pointer;vertical-align:middle; float:left; padding:7px;" />
</div>
<div class="clear"></div>
<div id="sample-layout"></div>
<div id="PATRICGrid"></div>


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
<script type="text/javascript" src="/patric-pathways/js/comp_pathway_grids.js"></script>
<script type="text/javascript" src="/patric-pathways/js/pathway_breadcrumb.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("CompPathwayTable");
	
	var pageProperties = {
		name: "CompPathwayTable",
		model:["Pathway", "Ec", "Feature"],
		items: 3,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		extraParams:getExtraParams,
		callBackFn:CallBack,
		remoteSort:true,
		border: true, 
		scm :[[checkbox, {header:'Pathway ID',			dataIndex:'pathway_id',		flex:1, renderer:BasicRenderer},
						{header:'Pathway Name',			dataIndex:'pathway_name',	flex:2, renderer:renderPathwayName},
						{header:'Pathway Class',		dataIndex:'pathway_class',	flex:2, renderer:BasicRenderer},
						{header:'Annotation',			dataIndex:'algorithm',		flex:1, align:'center', renderer:BasicRenderer},
						{header:'Unique Genome Count',	dataIndex:'genome_count',	flex:1, align:'center', renderer:BasicRenderer},
						{header:'Unique Gene Count',	dataIndex:'gene_count',		flex:1, align:'center', renderer:renderGeneCountPathway},
						{header:'Unique EC Count',		dataIndex:'ec_count',		flex:1, align:'center', renderer:renderAvgECCount},
						{header:'EC Conservation',		dataIndex:'ec_cons',		flex:1, align:'center', renderer:BasicRenderer},
						{header:'Gene Conservation',	dataIndex:'gene_cons',		flex:1, align:'center', renderer:BasicRenderer}
				],[checkbox, {header:'Pathway ID',		dataIndex:'pathway_id',		flex:1, renderer:BasicRenderer},
						{header:'Pathway Name',		dataIndex:'pathway_name',	flex:2, renderer:renderPathwayEc},
						{header:'Pathway Class',	dataIndex:'pathway_class',	flex:2, renderer:BasicRenderer},
						{header:'Annotation',		dataIndex:'algorithm',		flex:1, align:'center', renderer:BasicRenderer},
						{header:'EC Number',		dataIndex:'ec_number',		flex:1, align:'center', renderer:BasicRenderer},
						{header:'Description',		dataIndex:'ec_name',		flex:2, renderer:BasicRenderer},
						{header:'Genome Count',		dataIndex:'genome_count',	flex:1, align:'center', renderer:BasicRenderer},
						{header:'Unique GeneCount',	dataIndex:'gene_count',		flex:1, align:'center', renderer:renderGeneCountEc}
				],[checkbox, {header:'Feature ID',		dataIndex:'na_feature_id', 	flex:1, hidden:true, renderer:BasicRenderer},  
			          {header:'Genome Name',	dataIndex:'genome_name', 	flex:1, renderer:renderGenomeName},
			    	  {header:'Accession',		dataIndex:'accession', 		flex:1, hidden: true, renderer:renderAccession},
			    	  {header:'Locus Tag',		dataIndex:'locus_tag', 		flex:2, renderer:renderLocusTag},
			    	  {header:'Gene Symbol',	dataIndex:'gene',			flex:1, align:'center', renderer:BasicRenderer},
			    	  {header:'Product Name',	dataIndex:'product',		flex:2, renderer:BasicRenderer},
			    	  {header:'Annotation',		dataIndex:'algorithm', 		flex:1, align:'center', renderer:BasicRenderer},  	  
			    	  {header:'Pathway ID',		dataIndex:'pathway_id', 	flex:1, align:'center', renderer:BasicRenderer},
			    	  {header:'Pathway Name',	dataIndex:'pathway_name', 	flex:2, renderer:renderPathwayFeature},
			    	  {header:'EC Number',		dataIndex:'ec_number', 		flex:1, align:'center', renderer:BasicRenderer},
			          {header:'EC Description',	dataIndex:'ec_name', 		flex:2, renderer:BasicRenderer}
		]],
		sort:[[{
			property: 'pathway_id',
			direction: 'ASC'
		}],[{
			property: 'pathway_id',
			direction: 'ASC'
		},{
			property: 'ec_number',
			direction: 'ASC'
		}],[{
			property: 'genome_name',
			direction: 'ASC'
		},{
			property: 'locus_tag',
			direction: 'ASC'
		}]],
		hash:{
			aP: [1, 1, 1],
			aT: 0,
			alg: "<%=algorithm%>",
			cwEC: false,
			cwP: false,
			pId: "",
			pClass: "",
			ecN: "<%=ec_number%>"
		},
		fids: [],
		pageType: "Table",
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url:[ '<portlet:resourceURL />'],
		loaderFunction: function(){loadFBCD();},
		stateId: ['pathwaylist','pathwayeclist','PWfeaturelist'],
		pagingBarMsg: ['Displaying pathways {0} - {1} of {2}','Displaying records {0} - {1} of {2}','Displaying records {0} - {1} of {2}']
	};
	
	SetPageProperties(pageProperties);
	$Page.checkbox = checkbox;
	createLayout();	
	createLoadComboBoxes();
	loadFBCD(),
	$Page.doLayout(),
	Ext.get("tabs_pathways") && Ext.get("tabs_pathways").addCls("sel");
	// SetIntervalOrAPI(),
	Ext.QuickTips.init(),
	overrideButtonActions();
});
//]]>
</script>