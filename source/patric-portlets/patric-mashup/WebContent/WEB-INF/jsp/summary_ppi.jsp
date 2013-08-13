<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPRC" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.PSICQUICInterface" %>
<%@ page import="org.json.simple.*" %>
<%
String psicquic_species_name = "";
String taxonid = "";
String species_name = "";
String errorMsg = "Data is not available temporarily";

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

DBShared conn_shared = new DBShared();
DBPRC conn_prc = new DBPRC();

if (cType.equals("taxon") && cId != null && !cId.equals("")) {
	psicquic_species_name = "species:"+cId;
	taxonid = cId;
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
	if (parents.size() > 0) {
		species_name = parents.get(0).get("name");
	}
}
else if (cType.equals("genome") && cId != null && !cId.equals("")) {
	
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	psicquic_species_name = "species:"+names.get("ncbi_tax_id");
	taxonid = names.get("ncbi_taxon_id");	
	species_name = names.get("genome_name");
}
else {
	psicquic_species_name = "";
}

if (psicquic_species_name.equals("")) {
	%><p>Internal Error due to wrong paramter: cType=<%=cType %>,cId=<%=cId %></p><%
}
else {
	PSICQUICInterface api = new PSICQUICInterface();
	String result = api.getCounts("intact", psicquic_species_name);
	int result_pi = conn_prc.getPRCCount(taxonid, "PI");
%>
	<p>Interaction data of <%=species_name %> are retrieved from some prominent databases, e.g.,  InAct and PRC and displayed below.
		 Interaction experiment data covers protein-protein, protein-DNA, protein-carbohydrate and antibody-antigen. 
	</p>
	
	<table class="basic far2x">
	<thead>
		<tr>
			<th width="25%">Data Source</th>
			<th width="38%"><a href="http://www.ebi.ac.uk/intact/" target="_blank"><img src="/patric/images/logo_intact.png" alt="IntAct" /></a>
				<br/><a href="http://www.ebi.ac.uk/intact/" target="_blank">IntAct</a></th>
			<th width="37%"><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank"><img src="/patric/images/logo_prc.png" alt="Proteomics Resource Center" /></a>
				<br/><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank">Proteomics Resource Center</a></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th>Taxonomy search</th>
			<td class="right-align-text"><%
				if (result.equals("-1")) {
					%><%=errorMsg %><%
				}
				else if (result.equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="IntAct?cType=<%=cType%>&amp;cId=<%=cId%>"><%=result %></a><%
				}%>
			</td>
			<td class="right-align-text"><%
				if (result_pi < 0) {
					%><%=errorMsg %><%
				}
				else if (result_pi == 0) { 
					%>0<% 
				} else {
					%><a href="PRC?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=PI"><%=result_pi %></a><%
				}%>
			</td>
		</tr>
	</tbody>
	</table>
<% } %>