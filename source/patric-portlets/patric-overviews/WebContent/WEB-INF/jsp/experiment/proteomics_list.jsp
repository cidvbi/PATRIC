<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBTranscriptomics" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface"  
%><%

	String cType = request.getParameter("context_type");
	String cId = request.getParameter("context_id");
	String kw = (request.getParameter("keyword") != null)?request.getParameter("keyword"):"";
	String taxonId = "", experimentId = "";
	
	if(kw != null && (kw.startsWith("/") || kw.startsWith("#"))){
		kw = "";
	}
	DBShared db_shared = new DBShared();
	SolrInterface solr = new SolrInterface();
	
	ArrayList<ResultType> items = null;
	if(cType.equals("taxon")){
		items = db_shared.getTaxonIdsBelowTaxonIdForProteomics(cId);
		if(items.size() > 0){
			taxonId = items.get(0).get("id");
			for (int i = 1; i < items.size(); i++) {
				taxonId += "##" + items.get(i).get("id");
			}
			
		}
	}else if(cType.equals("genome")){
		taxonId = db_shared.getTaxonIdOfGenomeId(cId);
	}else if(cType.equals("feature")){
		solr.setCurrentInstance("Proteomics_Protein");
		experimentId = solr.getProteomicsTaxonIdFromFeatureId(cId);		
	}
	
	String keyword = "(*)";

%>
<div style="display:none">
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="Proteomics_Experiment" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword %>" />
<input type="hidden" name="cType" id="cType" value="<%=cType %>" />
<input type="hidden" name="cId" id="cId" value="<%=cId %>" />
<input type="hidden" name="taxonId" id="taxonId" value="<%=taxonId %>" />
<input type="hidden" name="experiment_id" id="experiment_id" value="" />

<input type="hidden" id="aT" name="aT" value="" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fileformat" name="fileformat" value=""/>
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="download_keyword" name="download_keyword" value="" />
</form>
</div>
<div id="copy-button" class="x-hidden"></div>
<p>
	The list below provides all of the available proteomics experiments and associated metadata 
	at this taxonomic level. The list of experiments can be filtered by metadata or keyword. 
	To learn more about PATRIC's proteomics data and associated  metadata, 
	see <a href="http://enews.patricbrc.org/faqs/transcriptomics-faqs/transcriptomics-experiment-and-comparison-list-faqs" target=_blank>Transcriptomics FAQs</a>.
</p>

<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<div id="information" class="table-container"  style="background-color:#DDE8F4;">
<table style="width:100%"><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
	<div id="grid_result_summary"><b>Loading...</b></div>
</td></tr></table>
</div>

<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/checkcolumn.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/toolbar.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/gridoptions.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/grid/table_checkboxes.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/createtree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/proteomics_list_grids.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/metadata.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
           
var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("Proteomics_Experiment");
	
	var pageProperties = {
		name: "Proteomics_Experiment",
		items: 2,
		cart: true,
		cartType: 'cart',
		scm: [[checkbox,
				{header:'Sample Name',				flex:2, dataIndex: 'sample_name',			renderer:BasicRenderer},
				{header:'Taxon Name',			flex:2, dataIndex: 'taxon_name',		renderer:BasicRenderer},
				{header:'Proteins',				flex:1, dataIndex: 'proteins',			align: 'center', renderer:renderProteins},
				{header:'Project Name',				flex:1, dataIndex: 'project_name',			renderer:BasicRenderer},
				{header:'Experiment Label',				flex:1, dataIndex: 'experiment_label',		renderer:BasicRenderer}, 
				{header:'Experiment Title',				flex:2, dataIndex: 'experiment_title',		renderer:BasicRenderer}, 
				{header:'Experiment Type',				flex:2, dataIndex: 'experiment_type',		renderer:BasicRenderer}, 
				{header:'Source',				flex:1, dataIndex: 'source',		renderer:BasicRenderer},
				{header:'Contact Name',	flex:2, dataIndex: 'contact_name',		renderer:BasicRenderer},
				{header:'Institution',	flex:2, dataIndex: 'institution',		renderer:BasicRenderer},
			],
			[checkbox,
				{header: "Experiment Title",	flex:3, dataIndex: 'experiment_title',	renderer:BasicRenderer},
				{header: "Experiment Label",	flex:3, dataIndex: 'experiment_label',	renderer:BasicRenderer},
				{header: "Source",	flex:1, dataIndex: 'source',		renderer: BasicRenderer},
				{header:'Genome Name',		flex:2, dataIndex:'genome_name',	renderer: BasicRenderer},
				{header:'Accession',			dataIndex:'accession',	hidden: true,	flex:1, renderer:BasicRenderer},
				{header:'Peptides',			dataIndex:'',	hidden: false,	flex:1, renderer:renderPeptide},
				{header:'Locus Tag',			dataIndex:'locus_tag', flex:2, renderer: renderLocusTag},
				{header:'RefSeq Locus Tag',			dataIndex:'refseq_locus_tag', flex:2, renderer: BasicRenderer},
				{header:'Gene Symbol',		dataIndex:'refseq_gene', flex:1, renderer:BasicRenderer},
				{header:'Description',			dataIndex:'product', flex:3, renderer: BasicRenderer}
				//{header:'Annotation',			dataIndex:'annotation',	hidden: true,	flex:1, renderer:BasicRenderer}
				//{header:'Feature Type',		dataIndex:'feature_type',	hidden: true,	flex:1, renderer:BasicRenderer}, 
				//{header:'Start',				dataIndex:'start_max',	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
				//{header:'End', 				dataIndex:'end_min',	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
				//{header:'Length (NT)',		dataIndex:'na_length',	hidden: true,	flex:1, align:'right', renderer:BasicRenderer},
				//{header:'Strand',				dataIndex:'strand',	hidden: true,	flex:1, align:'right', renderer:BasicRenderer}
			]],
		plugin:true,
		plugintype:"checkbox",	
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{
				property:"sample_name",
				direction: "ASC"
			}],[{
				property:"experiment_title",
				direction: "ASC"
			}]
		],
		hash:{
			aP: [1, 1],
			aT: 0,
			key:'',
			cwE: false,
			experiment_id: "",
			experiment_title: "",
			kW:"<%=kw%>"
		},
		remoteSort:true,
		model:["Proteomics_Experiment", "Proteomics_Protein"],
		tree: null,
		treeDS: null,
		fids: [],
		gridType: "Feature",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/ProteomicsList/ProteomicsListWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){loadFBCD();},
		stateId: ['proteomicsexperimentlist','proteomicsproteinlist'],
		pagingBarMsg: ['Displaying experiments {0} - {1} of {2}','Displaying proteins {0} - {1} of {2}']
	};

		
	Ext.getDom("keyword").value = getOriginalKeyword(pageProperties.hash);
	
	Ext.Ajax.request({
		url: pageProperties.url[0],
		method: 'POST',
		params: {
			cType: "<%=cType%>",
			cId: "<%=cId%>",
			sraction: "save_params",
			keyword: Ext.getDom("keyword").value.trim() + (pageProperties.hash.kW?" AND " + pageProperties.hash.kW:"")
		},
		success: function(rs) {	
			pageProperties.hash.key = rs.responseText,
			SetPageProperties(pageProperties),
			$Page.checkbox = checkbox,
			createLayout(),
			loadFBCD(),
			$Page.doLayout(),
			Ext.QuickTips.init(),
			overrideButtonActions();
		}
	});
	
	if (Ext.get("tabs_proteomics")) {
		Ext.get("tabs_proteomics").addCls("sel");
	}
	
});

function getOriginalKeyword(hash){
	
	var list_object ={};
		
	if(hash && hash.kW != '')
		list_object["Keyword"] =  hash.kW;
	
	list_object['taxon_id'] = '<%=taxonId%>';	
	
	
	return constructKeyword(list_object, "Proteomics_Experiment");
}
// ]]>
</script>
