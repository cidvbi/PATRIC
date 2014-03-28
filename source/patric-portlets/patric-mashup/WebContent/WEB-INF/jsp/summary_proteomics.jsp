<%@ page import="java.util.HashMap" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSummary" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPRC" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.EutilInterface" %>
<%@ page import="edu.vt.vbi.patric.common.PRIDEInterface" %>
<%@ page import="org.json.simple.*" %>
<%@ page import="java.util.*" %>
<%
String tId = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String species_name = "";
String errorMsg = "Data is not available temporarily";

DBSummary conn_summary = new DBSummary();
DBShared conn_shared = new DBShared();
DBPRC conn_prc = new DBPRC();

if (cType.equals("taxon")) {
	tId = cId;
	species_name = conn_summary.getPRIDESpecies(cId);
	
} else if (cType.equals("genome")) {
	
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	tId = names.get("ncbi_taxon_id");
	species_name = conn_summary.getPRIDESpecies(tId);
}

	//PRIDE
	PRIDEInterface api = new PRIDEInterface();
	JSONObject result = api.getResults(species_name);
	
	//PRC
	int result_ms = conn_prc.getPRCCount(tId, "MS");
	
%>
	<p>Experiment datasets of large-scale studies of proteins are retrieved from PRIDE and PRC post-genomic databases as listed below.</p>
	
	<table class="basic far2x">
	<thead>
		<tr>
			<th scope="row" width="25%">Data Source</th>
			<th scope="col" width="38%"><a href="http://www.ebi.ac.uk/pride/" target="_blank"><img src="/patric/images/logo_pride.png" alt="PRIDE" /></a><br/>
				<a href="http://www.ebi.ac.uk/pride/" target="_blank">PRoteomics IDEntification database (PRIDE)</a></th>
			<th scope="col" width="37%"><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank"><img src="/patric/images/logo_prc.png" alt="Proteomics Resource Center" /></a>
				<br/><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank">Proteomics Resource Center</a></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th scope="row">Taxonomy search</th>
			<td class="right-align-text"><!-- PRIDE/Taxonomy -->N/A</td>
			<td class="right-align-text"><!-- PRC/Taxonomy -->
				<% 
				if (result_ms < 0) {
					%><%=errorMsg%><%
				}
				else if (result_ms == 0) { 
					%>0<% 
				} else {
					%><a href="PRC?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=MS"><%=result_ms %></a><%
				} %>
			</td>
		</tr>
		<tr>
			<th scope="row">Species search</th>
			<td class="right-align-text"><!-- PRIDE/Species -->
				<%
				if (result.get("hasData").equals(false)) {
					%><%=errorMsg%><%
				}
				else if (result.get("total").equals(0)) {
					%>0<%
				} else {
					%><a href="PRIDE?cType=<%=cType%>&amp;cId=<%=cId%>"><%=result.get("total") %></a><%
				}
			%>
			</td>
			<td class="right-align-text"><!-- PRC/Species -->N/A</td>
		</tr>
	</tbody>
	</table>