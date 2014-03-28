<%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="java.util.*" 
%><%
String fId = request.getParameter("context_id");
int featureId = -1;

try {
	featureId = Integer.parseInt(fId);
} catch (Exception ex) {
	fId = null;
}

JSONObject feature = new JSONObject();

if (fId!=null && !fId.equals("") && featureId > 0) 
{
	// getting feature info from Solr 
	SolrInterface solr = new SolrInterface();
	feature = solr.getPATRICFeature(fId);
	// end of Solr query
}

if (feature.isEmpty() == false) {
	
	DBShared conn_shared = new DBShared();
	String tId = feature.get("ncbi_tax_id").toString();
	
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(tId);
	ResultType node = null;
	String flag = "";
	
	String tracks = "DNA,PATRICGenes,RefSeqGenes";
	
	int window_start = Integer.parseInt(feature.get("start_max").toString());
	if (window_start-1000 > 0) {
		window_start = window_start - 1000;
	}
	int window_end = Integer.parseInt(feature.get("end_min").toString())+1000;
	String gb_link = "GenomeBrowser?cType=feature&amp;cId="+fId+"&amp;loc="+window_start+".."+window_end+"&amp;tracks="+tracks;
	String crv_link = "CompareRegionViewer?cType=feature&amp;cId="+feature.get("na_feature_id")+"&amp;tracks=&amp;regions=5&amp;window=10000&amp;loc=1..10000";
	%>
	<nav class="breadcrumbs" style="width=100%">
		<ul class="inline no-decoration">
		<%
		for (int i=parents.size()-1; i>=0; i--) {
			node = parents.get(i);
			if (node.get("rank").equalsIgnoreCase("superkingdom") ||
					node.get("rank").equalsIgnoreCase("phylum") ||
					node.get("rank").equalsIgnoreCase("class") ||
					node.get("rank").equalsIgnoreCase("order") ||
					node.get("rank").equalsIgnoreCase("family") ||
					node.get("rank").equalsIgnoreCase("genus") ) 
			{
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
			<li><a title="genome" href="Genome?cType=genome&amp;cId=<%=feature.get("gid")%>"><%=feature.get("genome_name")%></a></li>
			<li id="feature_breadcrumb">
				<% if (feature.get("annotation").equals("PATRIC")) { %>
					
					<%=feature.get("locus_tag") %>
					<%=(feature.get("refseq_locus_tag")!= null)?" <span class='pipe'>|</span> "+feature.get("refseq_locus_tag"):"" %>
					<%=(feature.get("gene")!=null)?" <span class='pipe'>|</span> "+feature.get("gene"):"" %>
				<% } else { %>
					<%=(feature.get("locus_tag")!=null)?feature.get("locus_tag"):"" %>
				<% } %>
				
				<%=(feature.get("product")!=null)?" <span class='pipe'>|</span> "+feature.get("product"):"" %>
				<img id="breadcrumb_btn" alt="expand or shrink bread crumb" src="/patric/images/spacer.gif" onclick="toggleBreadcrumb()" class="toggleButton toggleButtonHide" />
			</li>
		</ul>
	</nav>
	
	<div class="clear"></div>
	<article class="tabs">
		<ul class="tab-headers no-decoration">
			<li id="tabs_featureoverview" class="first"><a href="Feature?cType=feature&amp;cId=<%=fId %>"><span>Overview</span></a></li>
			<li id="tabs_genomebrowser"><a href="<%=gb_link%>"><span>Genome Browser</span></a></li>
			
		 	<% if (feature.get("annotation").equals("PATRIC")) { %>
			<li id="tabs_crviewer"><a href="<%=crv_link%>"><span>Compare Region Viewer</span></a></li>
			<li id="tabs_pathways"><a href="PathwayTable?cType=feature&amp;cId=<%=feature.get("na_feature_id") %>"><span>Pathways</span></a></li>
			<li id="tabs_expression"><a href="TranscriptomicsGeneExp?cType=feature&amp;cId=<%=feature.get("na_feature_id") %>&amp;sampleId=&amp;colId=&amp;log_ratio=&amp;zscore=" title=""><span>Transcriptomics</span></a></li>
			<li id="tabs_proteomics"><a href="ProteomicsList?cType=feature&amp;cId=<%=feature.get("na_feature_id") %>&amp;kw="><span>Proteomics</span></a></li> 
			<li id="tabs_correlated"><a href="TranscriptomicsGeneCorrelated?cType=feature&amp;cId=<%=feature.get("na_feature_id") %>" title=""><span>Correlated Genes</span></a></li>
			<% } %>
			
			<li id="tabs_literature"><a href="Literature?cType=feature&amp;cId=<%=fId %>&amp;time=a&amp;kw=none"><span>Literature</span></a></li>
		</ul>
	</article>
<% } else { %>
	&nbsp;
<% } %>
