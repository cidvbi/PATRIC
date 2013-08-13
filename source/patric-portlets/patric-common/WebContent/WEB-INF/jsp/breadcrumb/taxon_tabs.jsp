<%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.ArrayList" 
%><%
String tId = request.getParameter("context_id");
int taxonId = -1;

try {
	taxonId = Integer.parseInt(tId);
} catch (Exception ex) {	
}

if (taxonId > 0)
{	
	DBShared conn_shared = new DBShared();
	DBSummary conn_summary = new DBSummary();
	
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(tId);
	ArrayList<ResultType> genus = conn_shared.getGenusInAncestors(taxonId);
	ArrayList<ResultType> phylotree = conn_summary.getOrderInTaxonomy(taxonId);
	ResultType node = null;
	String flag = "";
	boolean expandable = true;
	if (parents.size() <= 6) {
		expandable = false;
	}
	%>
	<nav class="breadcrumbs left">
		<ul id="breadcrumbs" class="inline no-decoration">
		<%
		for (int i=parents.size()-1; i>=0; i--) {
			node = parents.get(i);
			if (i==0) { 
				%>
				<li><%=node.get("name")%>
					<% if (expandable) { %>
					<img id="breadcrumb_btn" alt="expand or shrink bread crumb" src="/patric/images/spacer.gif" onclick="toggleBreadcrumb()" class="toggleButton toggleButtonHide" />
					<% } %>
				</li>
				<%
			} else {
				if (!expandable || (node.get("rank").equalsIgnoreCase("superkingdom") ||
						node.get("rank").equalsIgnoreCase("phylum") ||
						node.get("rank").equalsIgnoreCase("class") ||
						node.get("rank").equalsIgnoreCase("order") ||
						node.get("rank").equalsIgnoreCase("family") ||
						node.get("rank").equalsIgnoreCase("genus") )) {
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
		}
		%>
		</ul>
	</nav>
	<div id="utilitybox" class="smallest right no-underline-links">
		<a class="double-arrow-link" href="http://enews.patricbrc.org/patric-data-organization-overview/" target="_blank">Data Overview Tutorial</a>
		<br/><a class="double-arrow-link" href="Downloads?cType=taxon&amp;cId=<%=tId %>" target="_blank">Download genome data</a>
	</div>
	<div class="clear"></div>
	
	<article class="tabs">
		<ul class="tab-headers no-decoration"> 
			<li id="tabs_taxonoverview"><a href="Taxon?cType=taxon&amp;cId=<%=tId %>"><span>Overview</span></a></li>
			<li id="tabs_taxontree"><a href="TaxonomyTree?cType=taxon&amp;cId=<%=tId %>"><span>Taxonomy</span></a></li>
			<% if (phylotree.size()>0) { %>
			<li id="tabs_phylogeny"><a href="Phylogeny?cType=taxon&amp;cId=<%=tId %>"><span>Phylogeny</span></a></li>
			<% } %>
			<li id="tabs_genomelist"><a href="GenomeList?cType=taxon&amp;cId=<%=tId %>&amp;dataSource=&amp;displayMode=&amp;pk=&amp;kw=" 
				title="Genome Lists contain a summary list of all genomes associated with a given Phylum, Class, Order, Family, Genus or Species."><span>Genome List</span></a></li>
			<li id="tabs_featuretable"><a href="FeatureTable?cType=taxon&amp;cId=<%=tId %>&amp;featuretype=&amp;annotation=PATRIC&amp;filtertype="
				title="Feature Tables contain a summary list of all features (e.g., CDS, rRNA, tRNA, etc.) associated with a given Phylum, Class, Order, Family, Genus, Species or Genome."><span>Feature Table</span></a></li>
			<% if (genus.size()>0) { %>
			<li id="tabs_proteinfamilysorter"><a href="FIGfamSorterB?cType=taxon&amp;cId=<%=tId %>&amp;dm=result"><span>Protein Families</span></a></li>
			<% } %>
			<li id="tabs_pathways"><a href="CompPathwayTable?cType=taxon&amp;cId=<%=tId %>&amp;algorithm=PATRIC&amp;ec_number="><span>Pathways</span></a></li>
			<li id="tabs_explist"><a href="ExperimentList?cType=taxon&amp;cId=<%=tId %>&amp;kw=" 
				title=""><span>Transcriptomics</span></a></li>
			<li id="tabs_proteomics"><a href="ProteomicsList?cType=taxon&amp;cId=<%=tId %>&amp;kw=" 
				title=""><span>Proteomics</span></a></li>
			<li id="tabs_disease"><a href="DiseaseOverview?cType=taxon&amp;cId=<%=tId %>"><span>Diseases</span></a></li>
			<li id="tabs_literature"><a href="Literature?cType=taxon&amp;cId=<%=tId %>&amp;time=a&amp;kw=none"><span>Literature</span></a></li>
		</ul>
	</article>
<% } %>
