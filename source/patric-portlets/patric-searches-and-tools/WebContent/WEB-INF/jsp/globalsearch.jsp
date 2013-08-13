<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<portlet:defineObjects/>
<%
String pk = request.getParameter("param_key");

ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);
String keyword = "";
if(key != null && key.get("keyword") != null)
	keyword = key.get("keyword");
%>
<div style="display:none">
<form id="fTableForm" action="#" method="post">
<input type="hidden" id="tablesource" name="tablesource" value="GlobalSearch" />
<input type="hidden" id="fileformat" name="fileformat" value="" />
<input type="hidden" id="fastaaction" name="fastaaction" value="" />
<input type="hidden" id="fastatype" name="fastatype" value="" />
<input type="hidden" id="fastascope" name="fastascope" value="" />
<input type="hidden" id="fids" name="fids" value="" />
<input type="hidden" id="cat" name="cat" value="" />
<input type="hidden" name="download_keyword" id="download_keyword" value="" />
<input type="hidden" name="keyword" id="keyword" value="<%=keyword%>" />
</form>
</div>
<div id="copy-button" style="display:none;"></div>
<div id="mw" class="mw">
	<div>
		<div id="med">
		</div>
	</div>
	<div id="searching_span" style="color: rgb(153, 153, 153); font-size: 13px; top: 23px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; padding-left: 230px; padding-top: 6px; padding-bottom: 6px;"><img src="/patric/images/loading.gif" alt="Loading Icon"> Loading...</div>
	<div style="position:relative; padding-top:3px;" id="rcnt">  
		<div id="leftnavc" style="">
			<div id="leftnav" style="position:absolute;top:1px;width:132px">
				<div id="ms">
					<ul>
						<li class="mitem msel" id="li__" style="background-image: url('/patric/images/patric_search_group_icon2.png');background-repeat: no-repeat;padding: 0px 0px 0px 25px; background-position: left;">Summary</li>
						<li class="mitem" id="li__0" style="background-image: url('/patric/images/workspace_feature_group_20x20.png');background-repeat: no-repeat;padding: 0px 0px 0px 25px; background-position: left;"><a class="kl" href="javascript:loadSearchResults('0');">Features</a></li>
						<li class="mitem" id="li__1" style="background-image: url('/patric/images/workspace_genome_group_20x20.png');background-repeat: no-repeat;padding: 0px 0px 0px 25px;background-position: left;"><a class="kl" href="javascript:loadSearchResults('1');">Genomes</a></li>
						<li class="mitem" id="li__2" style="background-image: url('/patric/images/workspace_taxa_20x20.png');background-repeat: no-repeat;padding: 0px 0px 0px 25px;background-position: left;"><a class="kl" href="javascript:loadSearchResults('2');">Taxa</a></li>
						<li class="mitem" id="li__3" style="background-image: url('/patric/images/workspace_taxa_20x20.png');background-repeat: no-repeat;padding: 0px 0px 0px 25px;background-position: left;"><a class="kl" href="javascript:loadSearchResults('3');">Experiments</a></li>
					</ul>
					<div class="lnsep"></div>
					<div id="GenericSelector"></div>
				</div>
			</div>
		</div>
		<div id="center_col" style="visibility: visible; ">
			<div id="summary_div" style="height:1000px; overflow:auto; visibility:hidden;">
				<div id="summary_div_feature" class="x-grid-cell-inner" style="background-color: whiteSmoke;">
					<div style="line-height: 28px;">
						<span id="summary_div_feature_span_1" style="font-size: 16px;font-weight: bold;"></span><span id="summary_div_feature_span_2" style="font-weight:bold;"></span>
					</div>
				</div>
				<div id="summary_div_genome" class="x-grid-cell-inner" style="background-color: whiteSmoke;">
					<div style="line-height: 28px;">
						<span id="summary_div_genome_span_1" style="font-size: 16px;font-weight: bold;"></span><span id="summary_div_genome_span_2" style="font-weight:bold;"></span>
					</div>
				</div>
				<div id="summary_div_taxa" class="x-grid-cell-inner">
					<div style="line-height: 28px;">
						<span id="summary_div_taxa_span_1" style="font-size: 16px;font-weight: bold;"></span><span id="summary_div_taxa_span_2" style="font-weight:bold;"></span>
					</div>
				</div>
				<div id="summary_div_experiment" class="x-grid-cell-inner">
					<div style="line-height: 28px;">
						<span id="summary_div_experiment_span_1" style="font-size: 16px;font-weight: bold;"></span><span id="summary_div_experiment_span_2" style="font-weight:bold;"></span>
					</div>
				</div>
			</div>
			<div id='PATRICGrid' style="visibility:hidden"></div>
		</div>
	</div>
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
<script type="text/javascript" src="/patric-common/js/createtree.js"></script> 
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/loadgrid.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/globalsearch.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/TriStateTree.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript">
//<![CDATA[
var $Page;

Ext.onReady(function() {
	
	var pageProperties = {
		name: ["Feature", /* "GOSearch", "ECSearch", "GlobalProteinFamilies", "GlobalPathwaySearch",*/ "Genome", "GlobalTaxonomy", "GENEXP_Experiment"],
		text: ["Features", /* "by GO Terms", "by EC Numbers", "by Protein Families", "by Pathways", */ "Genomes", "Taxa", "Experiments"],
		resultcount: [0, /*0, 0, 0, 0,*/ 0, 0, 0],
		need: ["feature", /*"go", "ec", "figfam", "pathway",*/ "0", "taxonomy", "0"],
		divID: ["feature", /*"feature_by_go", "feature_by_ec", "feature_by_protein", "feature_by_pathway",*/ "genome", "taxa", "experiment"],
		summaryHeaderText: ["Features", /*"Features by Go Terms", "Features by EC Numbers", "Features by Protein Families", "Features by Pathways",*/ "Genomes", "Taxonomy", "Experiments"],
		summaryPostText :["features", /*"features", "features", "features", "features",*/ "genomes", "taxas", "experiments"],
		summary_data: [[], [], [], []],
		renderFunction: [renderListFeature, /*renderListGO, renderListEC, renderListFigFam, renderListPathway,*/ renderListGenome, renderListTaxonomy, renderListExperiment],
		alternativeKW: null,
		tree: null,
		treeDS: null,
		items:3,
		cart: true,
		cartType:'cart',
		plugin:true,
		plugintype:"checkbox",
		scm:[],
		hideToolbar:true,
		extraParams:getExtraParams,
		callBackFn:CallBack,
		remoteSort:false,
		hash:{
			aP: [1, 1, 1, 1],
			cat: "summary",
			key: "<%=pk%>",
			spellcheck: true
		},
		reconfigure: true,
		model:["Feature", /*"GO", "EC", "Figfam", "Pathway",*/ "Genome", "Taxonomy", "Experiment"],
		fids: [],
		gridType: "",
		current_hash: window.location.hash?window.location.hash.substring(1):"",
		url: ['<portlet:resourceURL />'],
		loaderFunction: function(){SetLoadParameters();arrangeCSS();loadTable();},
		stateId: ['globalfeaturelist', 'globalgenomelist', 'globaltaxalist'],
		pagingBarMsg: ['Displaying features {0} - {1} of {2}', 'Displaying genomes {0} - {1} of {2}', 'Displaying taxonomies {0} - {1} of {2}', 'Displaying experiments {0} - {1} of {2}']
	};
	SetPageProperties(pageProperties);
	SetLoadParameters();
	// SetIntervalOrAPI();
	getSummaryandCreateLayout();
	overrideButtonActions();
});
//]]>
</script>