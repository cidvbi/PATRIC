<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch"
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%
	String cType = request.getParameter("context_type");
	String cId = request.getParameter("context_id");
	String algorithm = request.getParameter("data_source");
	String status = request.getParameter("display_mode");
	String kw = (request.getParameter("keyword") != null)?request.getParameter("keyword"):"";
	if(kw != null && (kw.startsWith("/") || kw.startsWith("#"))){
		kw = "";
	}
	String pk = request.getParameter("param_key");
	if (pk == null)
		pk = "";
	if (status == null)
		status = "";
	if (algorithm == null)
		algorithm = "";
	DBSearch db_search = new DBSearch();	
	
	String keyword = "(*)";
	String gid = "NA";	
	if(cId.equals("2") && algorithm.equals("")){
		
		keyword= "(*)";
		gid = "";
		
	}else{
				
		ArrayList<ResultType> items = db_search.getTaxonIdList(cId, cType, "", "", "");

		if(items.size() > 0){
		
			gid = items.get(0).get("id");
		
			for (int i = 1; i < items.size(); i++) {
				gid += "##" + items.get(i).get("id");
			}
			
		}
		
	}

%>
<div style="display:none">
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="Genome" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword%>" />
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
<div id="copy-button" class="x-hidden"></div>
<p>
	The Genome List provides all of the available genomes and associated metadata at this taxonomic level. 
	To learn more about available metadata, see <a href="http://enews.patricbrc.org/genome-metadata-faqs/" target="_blank">Genome Metadata FAQs</a>.
</p>
<div id="tree-panel" style="float:left"></div>
<div id="sample-layout" style="float:left"></div>
<div class="clear"></div>
<div id="information" class="table-container"  style="background-color:#DDE8F4;">
<table style="width:100%"><tr><td style="background-color:#DDE8F4; font-size:1em; line-height:1.2em; padding:6px 8px;text-align:left; border-bottom:0px; border-right:0px;">
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
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/createtree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/genome_finder_grids.js"></script> 
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>

<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function () {

	var checkbox = createCheckBox("Genome");
	var pageProperties = {
		name: "Genome",
		items: 2,
		cart: true,
		cartType: '',
		scm: [[checkbox,
			{header:'Organism Name',		dataIndex:'genome_name',		flex:2, renderer:renderGenomeName}, 
			{header:'NCBI Taxon Id',	dataIndex:'ncbi_tax_id',		flex:1, hidden:true, align:'right'},
			{header:'Genome Status',		dataIndex:'genome_status',		flex:1, align:'center'}, 
			{header:'Genome Browser',		dataIndex:'genome_browser', 	flex:1,	align:'center', hidden:true, sortable:false, renderer:renderGenomeBrowserByGenome},
			{header:'Size',					dataIndex:'genome_length',		flex:1, align:'right',  hidden:true},
			{header:'Chromosome',			dataIndex:'chromosomes',		flex:1, align:'center', hidden:true},
			{header:'Plasmids',				dataIndex:'plasmids',			flex:1, align:'center', hidden:true},
			{header:'Contigs',				dataIndex:'contigs',			flex:1, align:'center', hidden:true},
			{header:'Sequences',			dataIndex:'sequences',			flex:1, align:'center', hidden:true, renderer:renderTotal},
			{header:'PATRIC CDS',			dataIndex:'rast_cds',			flex:1, align:'center', renderer:renderCDS_Count_RAST},
			{header:'Legacy BRC CDS',		dataIndex:'brc_cds',			flex:1, align:'center', hidden:true, renderer:renderCDS_Count_BRC},
			{header:'RefSeq CDS',			dataIndex:'refseq_cds',			flex:1, align:'center', hidden:true, renderer:renderCDS_Count_RefSeq},
			{header:'Isolation Country',	dataIndex:'isolation_country', 	flex:1, align:'center'}, 
			{header:'Host Name',			dataIndex:'host_name',			flex:1, align:'center'}, 
			{header:'Disease', 				dataIndex:'disease',			flex:1, align:'center'}, 
			{header:'Collection Date', 		dataIndex:'collection_date', 	flex:1, align:'center'},
			{header:'Completion Date', 		dataIndex:'completion_date', 	flex:1, align:'center', renderer:renderCompletionDate},
			{header:'MLST', 				dataIndex:'mlst', 				flex:1, align:'center', renderer:BasicRenderer, hidden:true},
			{header:'Strain',				dataIndex:'strain', 			flex:1, align:'center', hidden:true},      
			{header:'Serovar',				dataIndex:'serovar',			flex:1, align:'center', hidden:true},
			{header:'Biovar',				dataIndex:'biovar',				flex:1, align:'center', hidden:true},
			{header:'Pathovar',				dataIndex:'pathovar',			flex:1, align:'center', hidden:true},
			{header:'Culture Collection',	dataIndex:'culture_collection', flex:1, align:'center', hidden:true},
			{header:'Type Strain',			dataIndex:'type_strain',		flex:1, align:'center', hidden:true},
			{header:'Project Status', 		dataIndex:'project_status', 	flex:1, align:'center', hidden:true},
			{header:'Availability', 		dataIndex:'availability', 		flex:1, align:'center', hidden:true},
			{header:'Sequencing Center',	dataIndex:'sequencing_centers', flex:1, align:'center', hidden:true},
			{header:'Publication', 			dataIndex:'publication',		flex:1, align:'center', hidden:true},
			{header:'NCBI Project Id', 		dataIndex:'ncbi_project_id',	flex:1, align:'center', hidden:true},
			{header:'RefSeq Project Id',	dataIndex:'refseq_project_id',	flex:1, align:'center', hidden:true},
			{header:'Genbank Accessions',	dataIndex:'genbank_accessions',	flex:1, align:'center', hidden:true},
			{header:'RefSeq Accessions',	dataIndex:'refseq_accessions',	flex:1, align:'center', hidden:true},
			{header:'Sequencing Platform',	dataIndex:'sequencing_platform',flex:1, align:'center', hidden:true},
			{header:'Sequencing Depth',		dataIndex:'sequencing_depth',	flex:1, align:'center', hidden:true},
			{header:'Assembly Method',		dataIndex:'assembly_method',	flex:1, align:'center', hidden:true},
			{header:'GC Content',			dataIndex:'gc_content',			flex:1, align:'center', hidden:true},
			{header:'Isolation Site', 		dataIndex:'isolation_site',		flex:1, align:'center', hidden:true},
			{header:'Isolation Source', 	dataIndex:'isolation_source',	flex:1, align:'center', hidden:true},
			{header:'Isolation Comments',	dataIndex:'isolation_comments',	flex:1, align:'center', hidden:true},
			{header:'Geographic Location',	dataIndex:'geographic_location',flex:1, align:'center', hidden:true},
			{header:'Latitude',				dataIndex:'latitude',			flex:1, align:'center', hidden:true},
			{header:'Longitude',			dataIndex:'longitude',			flex:1, align:'center', hidden:true},
			{header:'Altitude', 			dataIndex:'altitude',			flex:1, align:'center', hidden:true},
			{header:'Depth', 				dataIndex:'depth', 				flex:1, align:'center', hidden:true},
			{header:'Host Gender',			dataIndex:'host_gender',		flex:1, align:'center', hidden:true},
			{header:'Host Age', 			dataIndex:'host_age',			flex:1, align:'center', hidden:true},
			{header:'Host Health',			dataIndex:'host_health',		flex:1, align:'center', hidden:true},
			{header:'Body Sample Site',		dataIndex:'body_sample_site',	flex:1, align:'center', hidden:true},
			{header:'Body Sample Subsite',	dataIndex:'body_sample_subsite',flex:1, align:'center', hidden:true},
			{header:'Gram Stain',			dataIndex:'gram_stain',			flex:1, align:'center', hidden:true},
			{header:'Cell Shape',			dataIndex:'cell_shape',			flex:1, align:'center', hidden:true},
			{header:'Motility',				dataIndex:'motility',			flex:1, align:'center', hidden:true},
			{header:'Sporulation',			dataIndex:'sporulation',		flex:1, align:'center', hidden:true},
			{header:'Temperature Range',	dataIndex:'temperature_range',	flex:1, align:'center', hidden:true},
			{header:'Optimal Temperature',	dataIndex:'optimal_temperature',flex:1, align:'center', hidden:true},
			{header:'Salinity',				dataIndex:'salinity', 			flex:1, align:'center', hidden:true},
			{header:'Oxygen Requirement',	dataIndex:'oxygen_requirement',	flex:1, align:'center', hidden:true},
			{header:'Habitat',				dataIndex:'habitat',			flex:1, align:'center', hidden:true},
			{header:'Others',				dataIndex:'comments',			flex:1, align:'center', hidden:true}],
			[checkbox,
			{text:"Genome Name",		dataIndex:'genome_name', 		flex:3, renderer:renderGenomeName},
			{text:"Accession",			dataIndex:'accession',			flex:1, renderer:renderAccession},
			{text:"Genome Browser",		dataIndex:'genome_browser', 	flex:1, align:'center', sortable:false, renderer:renderGenomeBrowserBySequence},
			{text:"Length (bp)",		dataIndex:'length', 			flex:1, align:'right', renderer:BasicRenderer},
			{text:"Sequence Type",		dataIndex:'sequence_type',		flex:1, align:'center', renderer:BasicRenderer},
			{text:"GC Content (%)",		dataIndex:'gc_content',			flex:1, align:'center', renderer:BasicRenderer},
			{text:"Description",		dataIndex:'description',		flex:2, renderer:BasicRenderer}]],
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
			gName: "",
			kW:"<%=kw%>"
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
		stateId: ['genomelist','sequencelist'],
		pagingBarMsg: ['Displaying genomes {0} - {1} of {2}','Displaying sequences {0} - {1} of {2}'],
		maxPageSize: 500
	};
	
	if('<%=gid%>' == 'NA'){		
		Ext.getDom("grid_result_summary").innerHTML = "<b>No genomes found.</b>";
	}else{
		
		Ext.getDom("keyword").value = getOriginalKeyword(pageProperties.hash);
		if(pageProperties.hash.key){
			SetPageProperties(pageProperties),
			$Page.checkbox = checkbox,
			createLayout(),
			loadFBCD(),
			$Page.doLayout(),
			// SetIntervalOrAPI(),
			Ext.QuickTips.init(),
			overrideButtonActions();
		}else{
			Ext.Ajax.request({
				url: pageProperties.url[0],
				method: 'POST',
				params: {cType: '<%=cType%>',
					cId: '<%=cId%>',
					sraction: "save_params",
					keyword: Ext.getDom("keyword").value.trim() + (pageProperties.hash.kW?" AND "+pageProperties.hash.kW:""),
					exact_search_term: Ext.getDom("keyword").value.trim(),
					search_on: 'Keyword'
				},
				success: function(rs) {	
					pageProperties.hash.key = rs.responseText,
					SetPageProperties(pageProperties),
					$Page.checkbox = checkbox,
					createLayout(),
					loadFBCD(),
					$Page.doLayout(),
					// SetIntervalOrAPI(),
					Ext.QuickTips.init(),
					overrideButtonActions();
				}
			});
		}
		if (Ext.get("tabs_genomelist"))
			Ext.get("tabs_genomelist").addCls("sel");
	}
	
});

function getOriginalKeyword(hash){
	var genome_list_object = {};
	
	if('<%=gid%>' != 'NA' && '<%=gid%>' != '')
		genome_list_object["gid"] =  '<%=gid%>';
	if('<%=status%>' != '')	
		genome_list_object["genome_status_f"] =  '<%=status%>';
	if('<%=algorithm%>' != '')	
		genome_list_object["annotation"] =  '<%=algorithm%>';
	if(hash && hash.kW != '')
		genome_list_object["Keyword"] = hash.kW;
		
	return constructKeyword(genome_list_object, "Genome");
}

// ]]>
</script>
