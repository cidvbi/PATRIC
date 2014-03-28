<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.EutilInterface" %>
<%
String tId = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String species_name = "";
String errorMsg = "Data is not available temporarily";
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

	String strQueryTerm = "txid"+tId+"[Organism:exp]";
	EutilInterface eutil_api = new EutilInterface();
	
	HashMap<String,String> st = null;
	HashMap<String,String> st_ssgcid = null;
	HashMap<String,String> st_csgid = null;
	try {
		st = eutil_api.getCounts("structure", strQueryTerm, "");
		st_ssgcid = eutil_api.getCounts("structure", strQueryTerm+"%20AND%20\"ssgcid\"", "");
		st_csgid = eutil_api.getCounts("structure", strQueryTerm+"%20AND%20\"csgid\"", "");
	} catch (Exception ex) {
		
	}
%>
		<p>Protein structure data of <%=species_name %> are retrieved from Protein Data Bank.</p>
		
		<table class="basic far2x">
		<thead>
			<tr>
				<th scope="row" width="75%">Data Source</th>
				<th scope="col"><a href="http://www.ncbi.nlm.nih.gov/Structure/" target="_blank"><img src="/patric/images/logo_ncbi.png" alt="NCBI Structure" /></a>
					<br/><a href="http://www.ncbi.nlm.nih.gov/Structure/" target="_blank">NCBI Structure</a></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<th scope="row">Taxonomy search</th>
				<td class="right-align-text"><% 
				if (st == null || st.get("hasData").equals("false")) {
					%><%=errorMsg%><%
				}
				else if (st.get("Count").equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="Structure?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter="><%=st.get("Count") %></a><%
				} %>
				</td>
			</tr>
			<tr>
				<th scope="row">Taxonomy search - Seattle Structural Genomics Center for Infectious Disease (SSGCID)</th>
				<td class="right-align-text"><% 
				if (st_ssgcid == null || st_ssgcid.get("hasData").equals("false")) {
					%><%=errorMsg%><%
				}
				else if (st_ssgcid.get("Count").equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="Structure?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=ssgcid"><%=st_ssgcid.get("Count") %></a><%
				} %>
				</td>
			</tr>
			<tr>
				<th scope="row">Taxonomy search - Center for Structural Genomics of Infectious Diseases (CSGID)</th>
				<td class="right-align-text"><% 
				if (st_csgid == null || st_csgid.get("hasData").equals("false")) {
					%><%=errorMsg%><%
				}
				else if (st_csgid.get("Count").equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="Structure?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=csgid"><%=st_csgid.get("Count") %></a><%
				} %>
				</td>
			</tr>
		</tbody>
		</table>