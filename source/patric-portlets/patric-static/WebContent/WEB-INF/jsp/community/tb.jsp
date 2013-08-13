<%@ page import="edu.vt.vbi.patric.common.SolrInterface" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.dao.DBTranscriptomics" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%
int mtbTaxon = 77643;//1773;
SolrInterface solr = new SolrInterface();
HashMap<Object,Object> genomes = new HashMap<Object,Object>();
int cntExperiments = -1;
JSONObject res = null;
ResultType key = new ResultType();
JSONArray docs = null;

// getting genome count
solr.setCurrentInstance("GlobalTaxonomy");
key.put("keyword", "taxon_id:(1773 OR 77643)");
res = solr.getData(key, null, null, 0, 3, false, false, false);
docs = (JSONArray)((JSONObject)res.get("response")).get("docs");

for (Object tx:docs) {
	JSONObject taxonomy = (JSONObject)tx;
	genomes.put(taxonomy.get("taxon_id"),taxonomy.get("genomes"));
}

// getting expression data count
DBTranscriptomics conn_transcriptopics = new DBTranscriptomics();
//ArrayList<String> items = conn_transcriptopics.getEIDs(""+mtbTaxon);
ArrayList<String> items = conn_transcriptopics.getEIDs("1763");
cntExperiments = items.size();

%>
<style type="text/css" media="screen" scoped>
div#tbGenomeList {
	background:#fff;
	padding: 5px;
	font-size: 12px;
}
div#tbGenomeList table {
	padding: 5px 0px;
}
div#tbGenomeList table td {
	padding: 3px 0px;
}
div#tbFindGenesProteins {
	background:#fff;
	padding: 15px;
}
div#tbBrowseExpressionData {
	background: #fff;
	width: 350px;
	height: 200px;
	padding: 10px;
	margin-right: 9px;
	font-size: 11px;
	line-height: 1.3em;
}
div#tbBrowseExpressionData hr {
	margin: 3px;
}
div#tbUploadExpressionData {
	background: #fff; 
	width: 260px;
	height: 200px;
	padding: 10px;
	font-size: 12px;
}
</style>
<script type="text/javascript">
//<![CDATA[
var name,url,cId,cType,genomeId,keyword;

function searchGenome() {
	var taxonId = <%=mtbTaxon %>; //Mtb genomes
	
	name = "Genome";
	url = "/portal/portal/patric/GenomeFinder/GenomeFinderWindow?action=b&cacheability=PAGE";
	cId = taxonId;
	cType = "taxon";
	genomeId = "";
	keyword = Ext.getDom("keyword_genome").value;
	//searchbykeyword(taxonId, 'taxon');

	var object = {};
	if (keyword == "" || keyword == "*") { 
		object["Keyword"] = "(*)";	
	} else {
		object["Keyword"] = "("+EncodeKeyword(keyword)+")";
	}

	Ext.Ajax.request({
		url: "/patric-searches-and-tools/jsp/get_taxon_ids.json.jsp",
		method: 'GET',
		params: {cType: cType, cId: cId, genomeId:genomeId, algorithm:"", status:""},
		success: function(response, opts) {
			genomes = Ext.JSON.decode(response.responseText);
			var ids="";
			if(genomes.ids.length >= 1){
				ids += genomes.ids[0].id;
			}
			for(var i=1; i< genomes.ids.length; i++) {
				ids += "##"+genomes.ids[i].id;
			}
			ids +="##59918##75906"; //add two outgroup genomes 
			object["gid"] = ids;
			search__(constructKeyword(object, name), cId, cType);
		}
	});
}

function searchFeature() {
	var need_genomes = true;
	
	name = "Feature";
	url = "/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE";
	cId = getScope();
	cType = "taxon";
	genomeId = "";
	keyword = Ext.getDom("keyword_feature").value;
	var outgroup = getOutgroupGenomes();
	
	if (cId == 83332) {
		need_genomes = false;
		cType = "genome";
		cId = 87468;
		genomeId = "87468";
		if (outgroup.length > 0) {
			genomeId += "##"+outgroup.join("##");
		}
	}
	
	var object = {};
	object["annotation"] = "";
	object["feature_type"] = "";
	if (keyword == "" || keyword == "*") { 
		object["Keyword"] = "(*)";	
	} else {
		object["Keyword"] = "("+EncodeKeyword(keyword)+")";
	}
	
	if (need_genomes) {
		Ext.Ajax.request({
			url: "/patric-searches-and-tools/jsp/get_taxon_ids.json.jsp",
			method: 'GET',
			params: {cType: cType, cId: cId, genomeId:genomeId, algorithm:"", status:""},
			success: function(response, opts) {
				genomes = Ext.JSON.decode(response.responseText);
				var ids="";
				if(genomes.ids.length >= 1){
					ids += genomes.ids[0].id;
				}
				for(var i=1; i< genomes.ids.length; i++) {
					ids += "##"+genomes.ids[i].id;
				}
				if (outgroup.length > 0) {
					ids += "##"+outgroup.join("##");
				}
				object["gid"] = ids;
				search__(constructKeyword(object, name), cId, cType);
			}
		});
	} else {
		object["gid"] = genomeId;
		search__(constructKeyword(object, name), cId, cType);
	}
}

function search__(_keyword, cId, cType){
	Ext.Ajax.request({
		url: url,
		method: 'POST',
		params: {
			cType: cType,
			cId: cId,
			sraction: "save_params",
			keyword: _keyword.replace(/\"/g, "%22").replace(/'/g, "%27").trim(),
			exact_search_term: keyword.trim(),
			search_on: "Keyword"
		},
		success: function(rs) {		
			if(name == "Genome") {
				document.location.href="GenomeFinder?cType="+cType+"&cId="+cId+"&dm=result&pk="+rs.responseText;
			} else if(name == "Feature") {
				document.location.href="GenomicFeature?cType="+cType+"&cId="+cId+"&dm=result&pk="+rs.responseText;
			}
		}
	});
}

function getScope() {
	var elements = document.getElementsByName("scope");
	for (var i=0; i<elements.length; ++i) {
		if (elements[i].checked) {
			return elements[i].value;
		}
	} 
}

function getOutgroupGenomes() {
	var elements = document.getElementsByName("outgroup"),
		outgroup = new Array();
	
	for (var i=0; i<elements.length; ++i) {
		if (elements[i].checked) {
			outgroup.push(elements[i].value);
		}
	}
	return outgroup;
}

function launchCPT() {
	Ext.Ajax.request({
		url: "/patric-searches-and-tools/jsp/get_taxon_ids.json.jsp",
		method: 'GET',
		params: {cType: "taxon", cId:<%=mtbTaxon%> , genomeId:genomeId, algorithm:"", status:""},
		success: function(response, opts) {
			genomes = Ext.JSON.decode(response.responseText);
			var ids="59918,75906";
			for(var i=0; i< genomes.ids.length; i++)
				ids += ","+genomes.ids[i].id;
			_launchCPT(ids);
		}
	});
}

function _launchCPT(idList) {
	Ext.Ajax.request({
		url: '/portal/portal/patric/PathwayFinder/PathwayFinderWindow?action=b&cacheability=PAGE',
		method: 'POST',
		params: {cType: "taxon"
			,cId: ""
			,sraction: "save_params"
			,genomeId: idList
			,search_on: "Keyword"
			,taxonId: ""
			,keyword: ""
		},
		success: function(rs) {
			document.location.href="PathwayFinder?cType=taxon&cId=&dm=result&map=&ec_number=&algorithm=PATRIC&pk="+rs.responseText;
		}
	});
}

function launchPFS() {
	Ext.Ajax.request({
		url: "/patric-searches-and-tools/jsp/get_taxon_ids.json.jsp",
		method: 'GET',
		params: {cType: "taxon", cId:<%=mtbTaxon%> , genomeId:genomeId, algorithm:"", status:""},
		success: function(response, opts) {
			genomes = Ext.JSON.decode(response.responseText);
			var ids="59918,75906";
			for(var i=0; i< genomes.ids.length; i++)
				ids += ","+genomes.ids[i].id;
			_launchPFS(ids);
		}
	});
}

function _launchPFS(idList) {
	Ext.Ajax.request({
		url: '/portal/portal/patric/FIGfamSorter/FigFamSorterWindow?action=b&cacheability=PAGE',
		method: 'POST',
		timeout: 600000,
		params: {callType: "toSorter", genomeIds: idList, keyword:""},
		success: function(rs) {
			document.location.href = "FIGfamSorter?dm=result&pk=" + rs.responseText + "#gs_0=0";
		}
	});
}

function launchTranscriptomicsUploader() {
	Ext.Ajax.request({
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
		params: {
			action_type: "WSSupport",
			action: "getToken"
		},
		success: function(response) {
			var res = response.responseText;
			
			if (res != undefined && res != "") {
				uploader = Ext.create('TranscriptomicsUploader.view.Viewport',{
					callback: function() {
						// customize ending behavior either to refresh workspace or add message to the launch button
						if(!msgCt){
							msgCt = Ext.DomHelper.insertFirst("uploader_div", {id:'atc-msg-div'}, true);
						}
						
						var m = Ext.DomHelper.append(msgCt, '<div class="msg"><p> Your experiment data is added</p></div>', true).hide();
						m.slideIn('l').ghost("l", {delay: 2000, remove: true});	
						
						updateCartInfo();
					}
				}).show();
			}
			else {
				getLoginUpPopupWindow('Upload Transcriptomics Data to Workspace','Upload Transcriptomics Data<br/> to Workspace','Register @ PATRIC To Upload Your Transcriptomics Data');
				PopupModalLoading = false;
			}
		}
	});
}
//]]>
</script>
<script type="text/javascript" src="/patric-searches-and-tools/js/search_common.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric/js/vbi/TranscriptomicsUpload.min.js"></script>

<div class="container tabs-above">
	<div id="utilitybox" class="smallest right no-underline-links">
		<a class="double-arrow-link" href="http://enews.patricbrc.org/what-is-tb-patric/" target="_blank">About TB @ PATRIC</a>
		<br><a class="double-arrow-link" href="Downloads?cType=taxon&amp;cId=<%=mtbTaxon %>" target="_blank">Download all TB @ PATRIC genomes</a>
	</div>
	<h1 class="bold">Welcome to TB @ PATRIC</h1>
	<div class="clear far2x"></div>
</div>
<%--<div class='container main-container'>--%>
<div class='container'>
	<section class='main'>
		<section class='two-thirds-col'>
			<div class='column' style='width:331px'>
				<h3 class="section-title normal-case close2x">
					<span class="wrap">TB Reference Genomes</span>
				</h3>
				<div class="far2x has-border">
					<div id="tbGenomeList" class="no-underline-links">
						<table class="far">
							<tr>
								<td style="width:280px"><a href="Genome?cType=genome&amp;cId=87468">Mycobacterium tuberculosis H37Rv</a></td>
								<td>
									<a href="FeatureTable?cType=genome&amp;cId=87468&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table"></a>
								</td>
							</tr>
							<tr>
								<td><a href="Genome?cType=genome&amp;cId=22151">Mycobacterium tuberculosis CDC1551</a></td>
								<td>
									<a href="FeatureTable?cType=genome&amp;cId=22151&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table"></a>
								</td>
							</tr>
							<tr>
								<td><a href="Genome?cType=genome&amp;cId=80988">Mycobacterium bovis BCG str. Pasteur 1173P2</a></td>
								<td>
									<a href="FeatureTable?cType=genome&amp;cId=80988&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table"></a>
								</td>
							</tr>
							<tr>
								<td><a href="Genome?cType=genome&amp;cId=59918">Mycobacterium smegmatis str. MC2 155</a></td>
								<td>
									<a href="FeatureTable?cType=genome&amp;cId=59918&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table"></a>
								</td>
							</tr>
							<tr>
								<td><a href="Genome?cType=genome&amp;cId=75906">Mycobacterium marinum M</a></td>
								<td>
									<a href="FeatureTable?cType=genome&amp;cId=75906&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table"></a>
								</td>
							</tr>
						</table>
						<h4>Find other Mtb genomes...</h4>
						<form action="#" onsubmit="return false;">
							<input type="text" id="keyword_genome" placeholder="Search using genome name or metadata" style="width:250px">
							<input type="submit" class="button" value="Search" onclick="searchGenome()"/>
						</form>
						<label>Examples:</label>
						<span class="hint">Mycobacterium Erdman</span>
						<br/>
						<span class="hint" style="padding-left:60px">South Africa 2009</span>
						<br/>
						<span class="hint" style="padding-left:60px">Homo sapiens</span>
					</div>
				</div>
				<!--  -->
				<h3 class="section-title normal-case close2x">
					<span class="wrap">Comparative TB Tools</span>
				</h3>
				<div class="far2x">
					<h4 class="bold">Protein Family Sorter</h4>
					<div>Compare protein families across groups of Mtb genomes via visualization and multiple sequence alignments.</div>
					<div class="no-underline-links right">
						<a class="double-arrow-link" href="http://enews.patricbrc.org/faqs/protein-family-sorter/" target="_blank">Learn more</a>
						&nbsp;
					</div>
					<div class="clear"></div>
					<div class="right">
						<button class="button no-radius" onclick="launchPFS()">Launch Protein Family Sorter</button>
					</div>
					<div class="clear"></div>
					<hr/>
					
					<h4 class="bold">Comparative Pathway Tool</h4>
					<div>Compare consistently annotated metabolic pathways across closely related or diverse groups of Mtb genomes.</div>
					<div class="no-underline-links right">
						<a class="double-arrow-link" href="http://enews.patricbrc.org/faqs/comparative-pathway-tool-faqs/" target="_blank">Learn more</a>
						&nbsp;
					</div>
					<div class="clear"></div>
					<div class="right">
						<button class="button no-radius" onclick="launchCPT()">Launch Comparative Pathway Tool</button>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			<div class='column' style='width:619px'>
				<h3 class="section-title normal-case close2x">
					<span class="wrap">Search TB @ PATRIC</span>
				</h3>
				<div class="far2x has-border" id="tbFindGenesProteins">
					<h4 class="bold">Find Genes/Proteins in</h4>
					<div style='padding-left:70px; width:580px;'>
						<form action="#" onsubmit="return false;">
							<div class="left">
								<input type="radio" name="scope" value="83332" checked="checked"> H37Rv Reference Genome <br/>
								<input type="radio" name="scope" value="1773"> Mtb genomes (<%=genomes.get("1773") %> genomes) <br/>
								<input type="radio" name="scope" value="77643"> Mtb complex genomes (<%=genomes.get("77643") %> genomes) <br/>
							</div>
							<div class="left" style="margin:0px 0px 10px 30px;padding-left:5px;border-left:1px dashed #DFDFEF">
								<span class="bold">Include:</span><br/>
								<input type="checkbox" name="outgroup" value="59918"> M.smegmatis str. MC2 155 <br/>
								<input type="checkbox" name="outgroup" value="75906"> M.marinum M
							</div>
							<div class="clear"></div>
							<input type="text" id="keyword_feature" style="width:510px"> <br/>
							<label>Examples:</label>
								<span class="hint">Rv0002</span><br/>
								<span class="hint" style="padding-left:63px">dnaN</span><br/>
								<span class="hint" style="padding-left:63px">DNA polymerase III beta subunit</span><br/>
							<input type="submit" class="button right" value="Search" onclick="searchFeature()">
						</form>
						<div class="clear"></div>
					</div>
				</div>
				<h3 class="section-title normal-case close2x">
					<span class="wrap">TB Omics Data</span>
				</h3>
				<div class="far2x">
					<div class="left has-border" id="tbBrowseExpressionData">
						<h4 class="left"><b>Browse</b> Expression Data&nbsp;&nbsp;</h4>
						<div style="line-height:16px">(<%=cntExperiments %> experiments)</div>
						<div class="clear"></div>
						<div class="left" style="width:230px;padding:10px 0px;">
							GSE13978: Cholesterol's effect on M. tuberculosis
							<hr/>
							GSE11096: Role of M.tuberculsis dosS and dosT in CO sensing
							<hr/>
							GSE7539: A PhoP point mutation discriminates between the virulent H37Rv and avirulent H37Ra strains of Mycobacterium tuberculosis
						</div>
						<img src="/patric/images/heatmap.png" alt="heatmap" class="right" style="padding-top:25px">
						<div class="clear"></div>
						<button class="button" onclick="location.href='ExperimentList?cType=taxon&amp;cId=1763';">Browse All Expression Data</button>
						<div class="clear"></div>
					</div>
					<div class="left has-border" id="tbUploadExpressionData">
						<h4><b>Upload</b> your Expression Data into Workspace</h4>
						<br/>
						<img src="/patric/images/transcriptomics_uploader_ad.png" alt="transcriptomics uploader" width=90 height=90 class="right" onclick="launchTranscriptomicsUploader()" style="cursor: pointer;">
						<div style="width:140px">
							Leverage PATRIC's private &amp; secure workspace to analyze your Mtb data and 
							compare with other published datasets.
							<br/><br/>
						</div>
						<div class="no-underline-links">
							<a class="double-arrow-link" href="http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs/" target="_blank">Learn more</a>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<a href="javascript:void(0)" onclick="launchTranscriptomicsUploader()">Upload now</a>
						</div>
						
						<div class="clear"></div>
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</section>
	</section>
</div>