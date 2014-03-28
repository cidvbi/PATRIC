<%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.ArrayList" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="org.json.simple.JSONObject" 
%><%

String gId = request.getParameter("context_id");
int genomeId = -1;

try {
	genomeId = Integer.parseInt(gId);
}
catch (Exception ex) {
	ex.printStackTrace();
}

if (genomeId > 0) {

	DBShared conn_shared = new DBShared();
	DBSummary conn_summary = new DBSummary();
	//ResultType context = conn_shared.getNamesFromGenomeInfoId(gId);
	SolrInterface solr = new SolrInterface();
	JSONObject context = solr.getGenome(gId);
	String tId = null;
	if (context.get("ncbi_tax_id") != null) {
		tId = context.get("ncbi_tax_id").toString();
	}
	int taxonId = Integer.parseInt(tId);
	
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(tId);
	ArrayList<ResultType> phylotree = conn_summary.getOrderInTaxonomy(taxonId);
	ResultType node = null;
	String flag = "";
	%>
	<nav class="breadcrumbs left">
		<ul class="inline no-decoration">
		<%
		for (int i=parents.size()-1; i>=0; i--) {
			node = parents.get(i);
			if (node.get("rank").equalsIgnoreCase("superkingdom") ||
					node.get("rank").equalsIgnoreCase("phylum") ||
					node.get("rank").equalsIgnoreCase("class") ||
					node.get("rank").equalsIgnoreCase("order") ||
					node.get("rank").equalsIgnoreCase("family") ||
					node.get("rank").equalsIgnoreCase("genus") ) {
				flag = "";
			} else {
				flag = "full";
			}
			%>
			<li class="<%=flag %>" style="<%=flag.equals("")?"":"display:none" %>">
				<a href="Taxon?cType=taxon&amp;cId=<%=node.get("ncbi_tax_id") %>" title="taxonomy rank:<%=node.get("rank")%>"><%=node.get("name")%></a>
			</li>
			<%
		}
		%>
			<li><%=context.get("genome_name")%> <img id="breadcrumb_btn" alt="expand or shrink bread crumb" src="/patric/images/spacer.gif" onclick="toggleBreadcrumb()" class="toggleButton toggleButtonHide" /></li>
		</ul>
	</nav>
	<div id="utilitybox" class="smallest right no-underline-links">
		<a class="double-arrow-link" href="ftp://ftp.patricbrc.org/patric2/genomes/<%=context.get("common_name") %>/" target="_blank">Download genome data</a>
	</div>
	<div class="clear"></div>

	<article class="tabs">
		<ul class="tab-headers no-decoration"> 	
			<li id="tabs_genomeoverview" class="first"><a href="Genome?cType=genome&amp;cId=<%=gId %>"><span>Overview</span></a></li>
			<% if (phylotree.size()>0) { %>
			<li id="tabs_phylogeny"><a href="Phylogeny?cType=genome&amp;cId=<%=gId %>"><span>Phylogeny</span></a></li>
			<% } %>
			<li id="tabs_genomebrowser"><a href="GenomeBrowser?cType=genome&amp;cId=<%=gId %>&amp;loc=0..10000&amp;tracks=DNA,PATRICGenes,RefSeqGenes"><span>Genome Browser</span></a></li>
			<li id="tabs_featuretable"><a href="FeatureTable?cType=genome&amp;cId=<%=gId %>&amp;featuretype=&amp;annotation=PATRIC&amp;filtertype=" 
				title="Feature Tables contain a summary list of all features (e.g., CDS, rRNA, tRNA, etc.) associated with a givenGenome."><span>Feature Table</span></a></li>
			<li id="tabs_pathways"><a href="CompPathwayTable?cType=genome&amp;cId=<%=gId %>&amp;algorithm=PATRIC&amp;ec_number="><span>Pathways</span></a></li>
			<li id="tabs_proteinfamilysorter"><a href="FIGfam?cType=genome&amp;cId=<%=gId %>&amp;dm=result&amp;bm="><span>Protein Families</span></a></li>
			<li id="tabs_explist"><a href="ExperimentList?cType=genome&amp;cId=<%=gId %>&amp;kw=" 
				title=""><span>Transcriptomics</span></a></li>
			<li id="tabs_proteomics"><a href="ProteomicsList?cType=genome&amp;cId=<%=gId %>&amp;kw=" title=""><span>Proteomics</span></a></li>
			<li id="tabs_disease"><a href="DiseaseOverview?cType=genome&amp;cId=<%=gId %>"><span>Diseases</span></a></li>
			<li id="tabs_literature"><a href="Literature?cType=genome&amp;cId=<%=gId %>&amp;time=a&amp;kw=none"><span>Literature</span></a></li>
		</ul>
	</article>
<% } %>
