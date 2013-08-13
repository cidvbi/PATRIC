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
	text += presearchtext + "<li>EC Search</li> ";
}else if (windowID.indexOf("GOSearch") >=1) {
	text += presearchtext + "<li>GO Search</li> ";
}else if (windowID.indexOf("GenomeFinder") >=1) {
	text += presearchtext + "<li>Genome Finder</li> ";
}else if (windowID.indexOf("GenomicFeature") >=1) {
	text += presearchtext + "<li>Feature Finder</li> ";
}else if (windowID.indexOf("PathwayFinder") >=1) {
	text += presearchtext + "<li>Comparative Pathway Tool</li> ";
}else if (windowID.indexOf("IDMapping") >=1) {
	text += presearchtext + "<li>ID Mapping</li> ";
}else if (windowID.indexOf("HPITool") >=1) {
	text += presearchtext + "<li>Host Pathogen Interactions</li> ";
}else if (windowID.indexOf("FIGfamSorter") >=1 || windowID.indexOf("FIGfamViewer") >=1) {
	text += presearchtext + "<li>Protein Family Sorter</li> ";
}else if (windowID.indexOf("MGRAST") >=1) {
	text += presearchtext + "<li>MG-RAST</li> ";
}else if (windowID.indexOf("RAST") >=1) {
	text += presearchtext + "<li>RAST</li> ";
}else if (windowID.indexOf("TranscriptomicsEnrichment") >=1) {
	text += presearchtext + "<li>Pathway Summary</li> ";
}else if (windowID.indexOf("Downloads") >=1) {
	text += "<li><a href=\"#\">Downloads</a></li> <li>Download Tool<li> ";
}

DBShared conn_shared = new DBShared();
ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(tId);

if (parents != null && parents.size() > 0) {
	//ResultType node = parents.get(0);	
%>
	<nav class="breadcrumbs left">
		<ul class="inline no-decoration">
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