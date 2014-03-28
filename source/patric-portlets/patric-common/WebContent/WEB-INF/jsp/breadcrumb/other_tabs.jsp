<%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.*" 
%><%
String tId = request.getParameter("context_id");
String windowID = request.getAttribute("WindowID").toString();

String presearchtext = "<li><a href=\"Tools\">Searches & Tools</a></li> ";

String text = "";
String pages = "";

if (windowID.indexOf("ECSearch") >=1) {
	text += presearchtext + "<li>EC Search</li>";
} else if (windowID.indexOf("GOSearch") >=1) {
	text += presearchtext + "<li>GO Search</li>";
} else if (windowID.indexOf("GenomeFinder") >=1) {
	text += presearchtext + "<li>Genome Finder</li>";
} else if (windowID.indexOf("GenomicFeature") >=1) {
	text += presearchtext + "<li>Feature Finder</li>";
} else if (windowID.indexOf("PathwayFinder") >=1) {
	text += presearchtext + "<li>Comparative Pathway Tool</li>";
} else if (windowID.indexOf("IDMapping") >=1) {
	text += presearchtext + "<li>ID Mapping</li>";
} else if (windowID.indexOf("HPITool") >=1) {
	text += presearchtext + "<li>Protein Protein Interactions</li>";
/*} else if (windowID.indexOf("FIGfamSorter") >=1 || windowID.indexOf("FIGfamViewer") >=1) {
	text += presearchtext + "<li>Protein Family Sorter</li>";*/
} else if (windowID.indexOf("FIGfam") >=1 || windowID.indexOf("SingleFIGfam") >=1) {
	text += presearchtext + "<li>Protein Family Sorter</li>";
} else if (windowID.indexOf("MGRAST") >=1) {
	text += presearchtext + "<li>MG-RAST</li>";
} else if (windowID.indexOf("RAST") >=1) {
	text += presearchtext + "<li>RAST</li>";
} else if (windowID.indexOf("TranscriptomicsEnrichment") >=1) {
	text += presearchtext + "<li>Pathway Summary</li>";
} else if (windowID.indexOf("Downloads") >=1) {
	text += "<li><a href=\"#\">Downloads</a></li> <li>Download Tool<li>";
} else if (windowID.indexOf("ExperimentData") >=1) {
	text = "<li></li>";
}

DBShared conn_shared = new DBShared();
ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(tId);

if (parents != null && parents.size() > 0) {
	
%>
	<nav class="breadcrumbs left">
		<ul class="inline no-decoration">
		<%
		ResultType node = null;
		String flag = "";
		boolean expandable = true;
		if (parents.size() <= 6) {
			expandable = false;
		}
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
		<ul class="inline no-decoration" style="margin-top:-20px">
			<%=text %>
		</ul>
	</nav>
	<div id="utilitybox" class="smallest right no-underline-links"><a class="double-arrow-link" href="Downloads?cType=taxon&amp;cId=<%=tId %>" target="_blank">Download genome data</a></div>
	<div class="clear"></div>
<% } else { %>
	<nav class="breadcrumbs">
		<ul class="inline no-decoration">
			<%=text %>
		</ul>
	</nav>
<% } %>