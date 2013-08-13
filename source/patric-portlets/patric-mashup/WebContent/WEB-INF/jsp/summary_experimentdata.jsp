<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String tId = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String species_name = "";

DBShared conn_shared = new DBShared();

if (cType.equals("taxon")) {
	tId = cId;
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
	if (parents.size() > 0) {
		species_name = parents.get(0).get("name");
	}
} else if (cType.equals("genome")) {
	
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	tId = names.get("ncbi_taxon_id");
	species_name = names.get("genome_name");
}

%>
<p>Below we provide general post-genomic data awareness for <%=species_name %> including aggregation of post-genomic meta-data 
	from multiple sources and consolidated access to specific experimental datasets, details, and results. 
	At this time, PATRIC retrieves post-geomic meta-data in real-time from world prominent databases by using taxonomy ID or name as the search term.
	Actual experimental data and results can be accessed via linkouts to respective databases. 			
	We categorize the post-genomic metadata into transcriptomics, proteomics, structure and interaction data types. 
	For further explanation, please see <a href="http://enews.patricbrc.org/post-genomics-faqs/" target="_blank">Experiment Data FAQs</a>.
</p>

<script type="text/javascript">
Ext.onReady(function () {
	if (Ext.get("tabs_experimentdata")!=null) {
		Ext.get("tabs_experimentdata").addCls("sel");
	}
});
</script>