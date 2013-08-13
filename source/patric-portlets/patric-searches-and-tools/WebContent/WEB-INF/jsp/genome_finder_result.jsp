<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");

//HashMap<String,String> key = (HashMap<String,String>) portletSession.getAttribute("key"+pk);
ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);

String taxonId = "";
String genomeId = "";
String keyword = "";
String search_on = "", exact_search_term = "";

if(key != null && key.containsKey("taxonId")){
	taxonId = key.get("taxonId");
}

if(key != null && key.containsKey("genomeId")){
	genomeId = key.get("genomeId");
}

if(key != null && key.containsKey("keyword")){
	keyword = key.get("keyword");
}

if(key != null && key.containsKey("search_on")){
	search_on = key.get("search_on");
}

if(key != null && key.containsKey("exact_search_term")){
	exact_search_term = key.get("exact_search_term");
}

	
%>
<div style="display:none">
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="Genome" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword%>" />
<input type="hidden" name="search_on" value="<%=search_on %>" />
<input type="hidden" name="cType" id="cType" value="<%=cType %>" />
<input type="hidden" name="cId" id="cId" value="<%=cId %>" />
<input type="hidden" name="gId" id="gId" value="" />
<input type="hidden" id="aT" name="aT" value="" />
<input type="hidden" id="sort" name="sort" value="" />
<input type="hidden" id="dir" name="dir" value="" />

<!-- fasta download specific param -->
<input type="hidden" id="fileformat" name="fileformat" value=""/>
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="download_keyword" name="download_keyword" value="" />


</form>
</div>
<div id="copy-button"style="display:none;"></div>
<div style="padding:3px;">
<input type="button" class="button leftarrow" id="search_modify" value="Modify Search Criteria" onclick="returntoSearchPage();"/> 
<span style="color:#999;font-size: 13px;top: 23px;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;padding-left: 10px;padding-top: 6px;padding-bottom: 6px;">Showing results for: <b><%=exact_search_term %></b></span></div>

<div class="table-container">
<table><tr><td>
	To learn how to filter results based on metadata or perform an advanced search, 
		see <a href="http://enews.patricbrc.org/genome-finder-faqs/" target="_blank">Genome Finder FAQs</a>.
	To learn more about available metadata, see .<a href="http://enews.patricbrc.org/genome-metadata-faqs/" target="_blank">Genome Metadata FAQs</a>.
</td></tr>
</table>
</div>
<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<div id="information" class="table-container"  style="background-color:#DDE8F4;">
<table><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
	<div id="grid_result_summary"><b>Loading...</b><br/>
	</div>
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
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/createtree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/genome_finder_grids.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function()
{
	var checkbox = createCheckBox("Genome");
	
	var pageProperties = {
		name: "Genome",
		items: 2,
		cart: true,
		cartType:'',
		scm: [[checkbox,
			{header:'Organism Name',	dataIndex:'genome_name',		flex:2, renderer:renderGenomeName}, 
			{header:'NCBI Taxon Id',	dataIndex:'ncbi_tax_id',		flex:1, hidden:true, align:'right'},
			{header:'Genome Status',	dataIndex:'genome_status',		flex:1, align:'center'/*, renderer:BasicRenderer*/}, 
			{header:'Genome Browser',	dataIndex:'genome_browser', 	flex:1,	hidden:true, sortable:false, align: 'center', renderer:renderGenomeBrowserByGenome},
			{header:'Size',				dataIndex:'genome_length',		flex:1, hidden:true, align:'right'/*, renderer:BasicRenderer*/},
			{header:'Chromosome',		dataIndex:'chromosomes',		flex:1, hidden:true, align:'center'/*, renderer:BasicRenderer*/},
			{header:'Plasmids',			dataIndex:'plasmids',			flex:1, hidden:true, align:'center'/*, renderer:BasicRenderer*/},
			{header:'Contigs',			dataIndex:'contigs',			flex:1, hidden:true, align:'center'/*, renderer:BasicRenderer*/},
			{header:'Sequences',		dataIndex:'sequences',			flex:1, align:'center', renderer:renderTotal},
			{header:'PATRIC CDS',		dataIndex:'rast_cds',			flex:1, align:'center', renderer:renderCDS_Count_RAST},
			{header:'Legacy BRC CDS',	dataIndex:'brc_cds',			flex:1, hidden:true, align:'center', renderer:renderCDS_Count_BRC},
			{header:'RefSeq CDS',		dataIndex:'refseq_cds',			flex:1, hidden:true, align:'center', renderer:renderCDS_Count_RefSeq},
			{header:'Isolation Country',dataIndex:'isolation_country', 	flex:1, align:'center'/*, renderer:BasicRenderer*/}, 
			{header:'Host Name',		dataIndex:'host_name',			flex:1, align:'center'/*, renderer:BasicRenderer*/}, 
			{header:'Disease', 			dataIndex:'disease',			flex:1, align:'center'/*, renderer:BasicRenderer*/}, 
			{header:'Collection Date', 	dataIndex:'collection_date', 	flex:1, align:'center'/*, renderer:BasicRenderer*/},
			{header:'Completion Date', 	dataIndex:'completion_date', 	flex:1, align:'center', renderer:renderCompletionDate},
			{header:'MLST', 		dataIndex:'mlst', 	flex:1, align:'center', renderer:BasicRenderer, hidden:true},
			{header:'Strain',			dataIndex:'strain', 			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},      
			{header:'Serovar',			dataIndex:'serovar',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Biovar',			dataIndex:'biovar',				flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Pathovar',			dataIndex:'pathovar',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Culture Collection',	dataIndex:'culture_collection', flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Type Strain',			dataIndex:'type_strain',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Project Status', 		dataIndex:'project_status', 	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Availability', 		dataIndex:'availability', 		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Sequencing Center',	dataIndex:'sequencing_centers', flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Publication', 			dataIndex:'publication',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'NCBI Project Id', 		dataIndex:'ncbi_project_id',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'RefSeq Project Id',	dataIndex:'refseq_project_id',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Genbank Accessions',	dataIndex:'genbank_accessions',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'RefSeq Accessions',	dataIndex:'refseq_accessions',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Sequencing Platform',	dataIndex:'sequencing_platform',flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Sequencing Depth',		dataIndex:'sequencing_depth',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Assembly Method',		dataIndex:'assembly_method',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'GC Content',			dataIndex:'gc_content',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Isolation Site', 		dataIndex:'isolation_site',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Isolation Source', 	dataIndex:'isolation_source',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Isolation Comments',	dataIndex:'isolation_comments',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Geographic Location',	dataIndex:'geographic_location',flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Latitude',				dataIndex:'latitude',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Longitude',			dataIndex:'longitude',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Altitude', 			dataIndex:'altitude',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Depth', 				dataIndex:'depth', 				flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Host Gender',			dataIndex:'host_gender',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Host Age', 			dataIndex:'host_age',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Host Health',			dataIndex:'host_health',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Body Sample Site',		dataIndex:'body_sample_site',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Body Sample Subsite',	dataIndex:'body_sample_subsite',flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Gram Stain',			dataIndex:'gram_stain',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Cell Shape',			dataIndex:'cell_shape',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Motility',				dataIndex:'motility',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Sporulation',			dataIndex:'sporulation',		flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Temperature Range',	dataIndex:'temperature_range',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Optimal Temperature',	dataIndex:'optimal_temperature',flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Salinity',				dataIndex:'salinity', 			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Oxygen Requirement',	dataIndex:'oxygen_requirement',	flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Habitat',				dataIndex:'habitat',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/},
			{header:'Others',				dataIndex:'comments',			flex:1, align:'center', hidden:true/*, renderer:BasicRenderer*/}],
			[checkbox,
			{header:"Genome Name",		dataIndex:'genome_name', 		flex:3, renderer:renderGenomeName},
			{header:"Accession",		dataIndex:'accession',			flex:1, renderer:renderAccession},
			{header:"Genome Browser",	dataIndex:'genome_browser',		flex:1, sortable:false, align:'center', renderer:renderGenomeBrowserBySequence},
			{header:"Length (bp)",		dataIndex:'length', 			flex:1, align:'right', renderer:BasicRenderer},
			{header:"Sequence Type",	dataIndex:'sequence_type',		flex:1, align:'center', renderer:BasicRenderer},
			{header:"GC Content (%)",	dataIndex:'gc_content',			flex:1, align:'center', renderer:BasicRenderer},
			{header:"Description",		dataIndex:'description',		flex:2, renderer:BasicRenderer}]],
		plugin:true,
		plugintype:"checkbox",	
		extraParams:getExtraParams,
		callBackFn:CallBack,
		sort: [[{
				property:"genome_name",
				direction: "ASC"
			}],[{
				property:"genome_name",
				direction: "ASC"
			}]
		],
		hash:{
			aP: [1, 1],
			aT: 0,
			key: "<%=pk%>",
			cwG: false,
			gId: "",
			gName: ""
		},
		remoteSort:true,
		model:["Genome", "Sequence"],
		tree: null,
		treeDS: null,
		fids: [],
		gridType: "Genome",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['/portal/portal/patric/GenomeFinder/GenomeFinderWindow?action=b&cacheability=PAGE'],
		loaderFunction: function(){loadFBCD();},
		stateId: ['genomelist', 'sequencelist'],
		pagingBarMsg: ['Displaying genomes {0} - {1} of {2}','Displaying sequences {0} - {1} of {2}']
	};

	SetPageProperties(pageProperties),
	$Page.checkbox = checkbox,
	createLayout(),
	loadFBCD(),
	$Page.doLayout(),
	// SetIntervalOrAPI(),
	Ext.QuickTips.init(),
	overrideButtonActions();
});

function getOriginalKeyword(){
	return "<%=keyword%>";
}

function returntoSearchPage(){
	var key = DecodeKeyword('<%=exact_search_term%>');
	document.location.href = "GenomeFinder?cType=<%=cType%>&cId=<%=cId%>&dm=#search_on=<%=search_on%>&keyword="+key;
}

//]]>
</script>
