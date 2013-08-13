<%@ page import="edu.vt.vbi.patric.common.SiteHelper"
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface"
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary"
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%@ page import="java.util.ArrayList"
%><%@ page import="org.json.simple.JSONObject"
%><%@ page import="org.json.simple.JSONArray"
%><%

JSONObject feature = (JSONObject) request.getAttribute("feature");
String[] item = null;
String urlBEIR = SiteHelper.getExternalLinks("BEIR");

// get PDB ID
	DBSummary conn_summary = new DBSummary();
	ArrayList<ResultType> uniprot = conn_summary.getUniprotAccession(feature.get("na_feature_id").toString());
	ArrayList<String> uniprot_pdb_id = new ArrayList<String>();
	String uniprot_accession = "";
	if (uniprot != null && uniprot.size() > 0) {
		uniprot_accession = uniprot.get(0).get("uniprotkb_accession");
	}
	for (int u=0; u<uniprot.size(); u++) { 
		if (uniprot.get(u).get("id_type").equals("PDB")) {
			uniprot_pdb_id.add(uniprot.get(u).get("id"));
		}
	}
// End of getting PDB ID

// Structure Center related 
	ArrayList<ResultType> beir = conn_summary.getBEIRClones(feature.get("na_feature_id").toString());
	String beir_clone_id = "";
	
// End of Structure Center related
%>
<table class="basic stripe far2x">
<tbody>
	<tr>
		<th style="width:20%">GO Assignments</th> 
		<td class="last"><%
			if (feature.get("go")!=null) {
				ArrayList<String> goArr = (ArrayList<String>) feature.get("go");
				for (String go: goArr) {
					item = go.split("\\|");
					%><a href="<%=SiteHelper.getExternalLinks("go_term")+item[0] %>" target="_blank"><%=item[0] %></a> <%=item[1] %><br /><%
				}
			} else {
				%>-<%
			}
		%></td> 
	</tr>			
	<tr> 
		<th>EC Assignments</th> 
		<td><%
			if (feature.get("ec")!=null) {
				ArrayList<String> ecArr = (ArrayList<String>) feature.get("ec");
				for (String ec : ecArr) {
					item = ec.split("\\|");
					%><a href="<%=SiteHelper.getExternalLinks("ec_number")+item[0] %>" target="_blank"><%="EC:"+item[0] %></a> <%=item[1] %><br /><%
				}
			} else {
				%>-<%
			}
		%></td> 
	</tr>
	<tr> 
		<th>FIGfam Assignments</th> 
		<td><%
			if (feature.get("figfam_id")!=null) {
				%><a href="javascript:submitFigfam('<%=feature.get("figfam_id") %>')"><%=feature.get("figfam_id") %></a><%
			} else {
				%>-<%
			}
		%></td> 
	</tr>
	<tr> 
		<th>Pathway Assignments</th> 
		<td><%
			if (feature.get("pathway")!=null) {
				ArrayList<String> pwArr = (ArrayList<String>) feature.get("pathway");
				for (String pw : pwArr) {
					item = pw.split("\\|");
					%><a href="CompPathwayMap?cType=genome&amp;cId=<%=feature.get("gid") %>&amp;dm=feature&amp;feature_info_id=<%=feature.get("na_feature_id") %>&amp;map=<%=item[0] %>&amp;algorithm=<%=feature.get("annotation") %>&amp;ec_number="><%="KEGG:"+item[0] %></a> <%=item[1] %><br /><%
				}
			} else {
				%>-<%
			}
		%></td>
	</tr>
	
	<!-- PDB Info -->
	<% if (uniprot_pdb_id.size()>0) { %>
	<tr>
		<th>Structure</th>
		<td>
			<% for (String _pdb_id:uniprot_pdb_id) { %>
			<a href="Jmol?structureID=<%=_pdb_id %>"><%=_pdb_id %></a>&nbsp;
			<% } %>
		</td>
	</tr>
	<% } %>
	<!-- End of PDB Info -->
	
	<!-- structure center related -->
	<% if (beir.size() > 0) { %>
	<tr>
		<th>BEIR Clones</th>
		<td> <a href="#" onclick="toggleLayer('beir_detail');return false;"><%=beir.size() %> clones are available</a>
			<div id="beir_detail" class="table-container" style="display:none">
				<table>
				<% for (int u=0; u<beir.size(); u++) { 
					beir_clone_id = beir.get(u).get("beir_clone_id");
					beir_clone_id = beir_clone_id.replace("NRS","NR"); //temporary solution for NRS
				%>
				<tr>
					<td style="width:25%"><a href="<%=urlBEIR%><%=beir_clone_id %>" target="_blank"><%=beir_clone_id %></a></td>
					<td><%=beir.get(u).get("clone_name") %></td>
				</tr>
				<% } %>
				</table>
			</div>
		</td>
	</tr>
	<% } %>
	<%
	// submit to CSGID/SSGCID
	if (beir.size() == 0 && uniprot_pdb_id.size() == 0 && feature.get("feature_type").equals("CDS")) {
		String regSSGCID = SiteHelper.getGenusByStructuralGenomicsCenter("ssgcid");
		String regCSGID = SiteHelper.getGenusByStructuralGenomicsCenter("csgid");
		
		ResultType genus = conn_summary.getGenusInTaxonomy(feature.get("ncbi_tax_id").toString());
		
		if (genus.get("name").matches(regSSGCID)) {
			%>
			<tr><th>&nbsp;</th><td><a href="#" onclick="SubmitToStructuralGenomicsCenter('ssgcid');return false;">Submit a request for structure determination to SSGCID</a></td></tr>
			<%
		} else if (genus.get("name").matches(regCSGID)) {
			%>
			<tr><th>&nbsp;</th><td><a href="#" onclick="SubmitToStructuralGenomicsCenter('csgid');return false;">Submit a request for structure determination to CSGID</a></td></tr>
			<%
		} else {
			%>
			<tr><th>&nbsp;</th><td>Not supported by SSGCID/CSGID</td></tr>
			<%
		}
	}
	%>
	<!-- end of structure center related -->
</tbody>
</table>


	<form id="sgc_form" method="POST" action="#">
		<input type="hidden" name="patric_feature_id" value="<%=feature.get("na_feature_id") %>" />
		<input type="hidden" name="patric_callback_url" value="/portal/portal/patric/Feature?cType=feature&amp;cId=<%=feature.get("na_feature_id") %>" />
		<input type="hidden" name="genome_name" value="<%=feature.get("genome_name") %>" />
		<input type="hidden" name="product" value="<%=feature.get("product") %>" />
		<input type="hidden" id="dna_sequence" name="dna_sequence" value="" />
		<input type="hidden" id="protein_sequence" name="protein_sequence" value="" />
		<input type="hidden" name="refseq_locus_tag" value="<%=(feature.get("refseq_locus_tag")!=null)?feature.get("refseq_locus_tag"):"" %>" />
		<input type="hidden" name="refseq_protein_id" value="<%=(feature.get("refseq_protein_id")!=null)?feature.get("refseq_protein_id"):"" %>" />
		<input type="hidden" name="refseq_gi_number" value="<%--=(refseqInfo!=null)?refseqInfo.get("gi_number"):"" --%>" />
		<input type="hidden" name="uniprot_accession" value="<%=uniprot_accession %>" />
	</form>
	
<script type="text/javascript">
//<![CDATA[
var featureID = <%=feature.get("na_feature_id") %>;
function getDNASequence() {
	Ext.Ajax.request({
		url: '/patric-common/jsp/fasta_download_handler.jsp?fastaaction=ajax&fastatype=FASTA DNA Sequence(s)&fastascope=Selected&fids='+featureID,
		success: function(rs, opts) {
			Ext.getDom("dna_sequence").value = rs.responseText;
		},
		callback: getProteinSequence
	});	
}
function getProteinSequence() {
	Ext.Ajax.request({
		url: '/patric-common/jsp/fasta_download_handler.jsp?fastaaction=ajax&fastatype=FASTA Protein Sequence(s)&fastascope=Selected&fids='+featureID,
		success: function(rs, opts) {
			Ext.getDom("protein_sequence").value = rs.responseText;
		},
		callback: function() {
			Ext.getDom("sgc_form").submit();
		}
	});
}
function SubmitToStructuralGenomicsCenter(center) {
	if (center=="ssgcid") {
		Ext.getDom("sgc_form").action = "http://apps.sbri.org/SSGCIDCommTargReq/Default.aspx";
	} else {
		Ext.getDom("sgc_form").action = "http://www.biochem.ucl.ac.uk/cgi-bin/phil/csgid/submit_CSGID_targets/submit_PATRIC_protein.pl";
	}
	getDNASequence();
}
function submitFigfam(id) {

	Ext.Ajax.request({
		url: '/portal/portal/patric/FIGfamViewer/FigFamViewerWindow?action=b&cacheability=PAGE',
		method: 'POST',
		timeout: 600000,
		params: {callType: "saveState",
			gid: '',
			figfam: id
		},
		success: function(rs) {
			document.location.href = "FIGfamViewerB?"+"&cType=taxon&cId=2&pk="+rs.responseText;
		}
	});	
}
//]]>
</script>