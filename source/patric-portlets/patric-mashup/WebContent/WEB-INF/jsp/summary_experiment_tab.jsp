<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBPRC" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.*" 
%><%@ page import="org.json.simple.*" 
%><%

String tId = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String species_name = "";
String errorMsg = "Data is not available temporarily";
String psicquic_species_name = "";
String pride_species_name = "";


DBShared conn_shared = new DBShared();
DBSummary conn_summary = new DBSummary();
DBPRC conn_prc = new DBPRC();

if (cType.equals("taxon")) {
	tId = cId;
	ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
	if (parents.size() > 0) {
		species_name = parents.get(0).get("name");
	}
	psicquic_species_name = "species:"+cId;
	pride_species_name = conn_summary.getPRIDESpecies(cId);
		
} else if (cType.equals("genome")) {
	
	ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
	tId = names.get("ncbi_taxon_id");
	species_name = names.get("genome_name");
	psicquic_species_name = "species:"+names.get("ncbi_tax_id");
	pride_species_name = conn_summary.getPRIDESpecies(tId);
}
// Transcriptomics
	// GEO
		String strQueryTerm = "txid"+tId+"[Organism:exp]+NOT+gsm[ETYP]";
		EutilInterface eutil_api = new EutilInterface();
		HashMap<String,String> gds_taxon = null;
		try {
			gds_taxon = eutil_api.getCounts("gds", strQueryTerm, "");
		} catch (Exception ex) {
			
		}
		
	// ArrayExpress
		ArrayExpressInterface api = new ArrayExpressInterface();
		JSONObject arex_keyword = api.getResults(species_name,"");

// Proteomics
	PRIDEInterface pride_api = new PRIDEInterface();
	JSONObject proteomics_result = pride_api.getResults(pride_species_name);

// Structure
	strQueryTerm = "txid"+tId+"[Organism:exp]";
	HashMap<String,String> st = null;
	try {
		st = eutil_api.getCounts("structure", strQueryTerm, "");
	} catch (Exception ex) {
		
	}

// Protein Protein Interaction
	PSICQUICInterface psicquic_api = new PSICQUICInterface();
	String result = psicquic_api.getCounts("intact", psicquic_species_name);
	int result_pi = conn_prc.getPRCCount(tId, "PI");
	
%>
	<table class="basic far2x">
	<tbody>
	<tr>
		<th width="75%">Transcriptomics from GEO</th>
		<td class="right-align-text"><!-- GEO/keyword -->
			<% 
			if (gds_taxon == null || gds_taxon.get("hasData").equals("false")) {
				%><%=errorMsg%><%
			}
			else if (gds_taxon.get("Count").equalsIgnoreCase("0")) { 
				%>0<% 
			} else {
				%><a href="GEO?cType=<%=cType %>&amp;cId=<%=cId%>&amp;filter=&amp;keyword="><%=gds_taxon.get("Count") %></a><%
			} %>
		</td>
	</tr>
	<tr>
		<th>Transcriptomics from ArrayExpress</th>
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
	</tr>
	<tr>
		<th>Proteomics from PRIDE</th>
		<td class="right-align-text"><!-- PRIDE/Species -->
			<%
			if (proteomics_result.get("hasData").equals(false)) {
				%><%=errorMsg%><%
			}
			else if (proteomics_result.get("total").equals(0)) {
				%>0<%
			} else {
				%><a href="PRIDE?cType=<%=cType%>&amp;cId=<%=cId%>"><%=proteomics_result.get("total") %></a><%
			}
			%>
		</td>	
	</tr>
	<tr>
		<th>Structure from NCBI</th>
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
		<th>Protein Protein Interaction from IntAct</th>
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
	</tr>
	<tr>
		<td class="no-underline-links" colspan="2">
			<a class="double-arrow-link" href="ExperimentData?cType=taxon&cId=<%=cId %>&kw=Experiment%20Data">more</a>
		</td>
	</tr>
	</tbody>
	</table>