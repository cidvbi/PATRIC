<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPRC" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.*" %>
<%@ page import="org.json.simple.*" %>
<%
String tId = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String species_name = "";
String errorMsg = "Data is not available temporarily";

DBShared conn_shared = new DBShared();
DBPRC conn_prc = new DBPRC();

if (cType.equals("taxon")) {
	tId = cId;
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
	if (parents.size() > 0) {
		species_name = parents.get(0).get("name");
	}
}
else if (cType.equals("genome")) {
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	tId = names.get("ncbi_taxon_id");
	species_name = names.get("genome_name");
}

// GEO
	String strQueryTerm = "txid"+tId+"[Organism:exp]+NOT+gsm[ETYP]";
	EutilInterface eutil_api = new EutilInterface();

	HashMap<String,String> gds_taxon = null;
	HashMap<String,String> gds_keyword = null;
	try {
		gds_taxon = eutil_api.getCounts("gds", strQueryTerm, "");
		gds_keyword = eutil_api.getCounts("gds", species_name.replaceAll(" ","+")+"+NOT+gsm[ETYP]", "");
	} catch (Exception ex) {
		
	}

// ArrayExpress
	ArrayExpressInterface api = new ArrayExpressInterface();
	JSONObject arex_species = api.getResults("", species_name);
	JSONObject arex_keyword = api.getResults(species_name,"");
	
// PRC
	int prc_ma = conn_prc.getPRCCount(tId, "MA");
	
%>
	<p>Genome-wide gene expression profiling datasets are retrieved from ArrayExpress, GEO and  PRC post-genomic databases as listed below. 
	They include cDNA microarrays and oligo-microarrays, cDNA-AFLP , SAGE and RNA-Seq.</p>
	
	<table class="basic far2x">
	<thead>
		<tr>
			<th scope="row" width="25%">Data Source</th>
			<th scope="col" width="25%"><a href="http://www.ncbi.nlm.nih.gov/geo/" target="_blank"><img src="/patric/images/logo_geo.png" alt="GEO" /></a>
				<br/><a href="http://www.ncbi.nlm.nih.gov/geo/" target="_blank">Gene Expression Omnibus</a>
			</th>
			<th scope="col" width="25%"><a href="http://www.ebi.ac.uk/microarray-as/ae/" target="_blank"><img src="/patric/images/logo_arrayexpress.png" alt="ArrayExpress" /></a>
				<br/><a href="http://www.ebi.ac.uk/microarray-as/ae/" target="_blank">ArrayExpress</a></th>
			<th scope="col" width="25%"><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank"><img src="/patric/images/logo_prc.png" alt="Proteomics Resource Center" /></a>
				<br/><a href="http://pathogenportal.net/portal/portal/PathPort/Data+Set+Summary/prc" target="_blank">Proteomics Resource Center</a></th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th scope="row">Taxonomy search</th>
			<td class="right-align-text"><!-- GEO/taxonomy -->
				<% 
				if (gds_taxon == null || gds_taxon.get("hasData").equals("false")) { 
					%><%=errorMsg%><%
				} else if (gds_taxon.get("Count").equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="GEO?cType=<%=cType %>&amp;cId=<%=cId%>&amp;filter=&amp;keyword="><%=gds_taxon.get("Count") %></a><%
				} %>
			</td>
			<td class="right-align-text"><!-- ArrayExpress/Taxonomy -->N/A</td>
			<td class="right-align-text last"><!-- PRC/Taxonomy -->
				<% 
				if (prc_ma < 0) {
					%><%=errorMsg%><%
				}
				else if (prc_ma == 0) { 
					%>0<% 
				} else {
					%><a href="PRC?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=MA"><%=prc_ma %></a><%
				}%>
			</td>
		</tr>
		<tr>
			<th scope="row">Keyword search</th>
			<td class="right-align-text"><!-- GEO/keyword -->
				<% 
				if (gds_keyword == null || gds_keyword.get("hasData").equals("false")) {
					%><%=errorMsg%><%
				}
				else if (gds_keyword.get("Count").equalsIgnoreCase("0")) { 
					%>0<% 
				} else {
					%><a href="GEO?cType=<%=cType%>&amp;cId=<%=cId%>&amp;filter=&amp;keyword=<%=species_name %>"><%=gds_keyword.get("Count") %></a><%
				} %>
			</td>
			<td class="right-align-text"><!-- ArrayExpress/Keyword -->
				<% 
				if (arex_keyword.get("hasData").equals(false)) {
					%><%=errorMsg%><%
				}
				else if (arex_keyword.get("total").equals(0)) { 
					%>0<% 
				} else {
					%><a href="ArrayExpress?cType=<%=cType%>&amp;cId=<%=cId%>&amp;kw=<%=species_name%>"><%=arex_keyword.get("total") %></a><%
				}%>
			</td>
			<td class="right-align-text last"><!-- PRC/Keyword -->N/A</td>
		</tr>
		<tr>
			<th scope="row">Species search</th>
			<td class="right-align-text"><!-- GEO/Species -->N/A</td>
			<td class="right-align-text"><!-- ArrayExpress/Species -->
				<% 
				if (arex_species.get("hasData").equals(false)) {
					%><%=errorMsg%><%
				} else if (arex_species.get("total").equals(0)) { 
					%>0<% 
				} else {
					%><a href="ArrayExpress?cType=<%=cType%>&amp;cId=<%=cId%>&amp;kw="><%=arex_species.get("total") %></a><%
				} %>
			</td>
			<td class="right-align-text last"><!-- PRC/Species -->N/A</td>
		</tr>
	</tbody>
	</table>