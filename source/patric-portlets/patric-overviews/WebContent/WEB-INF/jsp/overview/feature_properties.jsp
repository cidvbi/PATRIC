<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.common.SiteHelper" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="org.json.simple.JSONArray" 
%><%

String fId = request.getParameter("context_id");
ResultType refseqInfo = null;
String refseqLink = null;
String refseqLocusTag = null;
JSONObject feature = new JSONObject();
JSONArray relatedFeatures = new JSONArray();
SolrInterface solr = new SolrInterface();

if (fId!=null) {	
	// getting feature info from Solr 	
	feature = solr.getPATRICFeature(fId);
}

if (feature.isEmpty() == false) {
	
	// get related annotations
	JSONArray sortParam = new JSONArray();
	JSONObject sort1 = new JSONObject();
	sort1.put("property", "annotation_sort");
	sort1.put("direction", "asc");
	sortParam.add(sort1);
	
	HashMap<String, String> opt = new HashMap<String, String>();
	opt.put("sort", sortParam.toJSONString());
	
	relatedFeatures = solr.searchSolrRecords("pos_group:\""+feature.get("pos_group").toString()+"\"", opt);
	
	//TODO: need to migrate to solr query
	DBSummary conn_summary = new DBSummary();
	
	String uniprot_link = "";
	String uniprot_accession = "";
	ArrayList<ResultType> uniprot = null;
	
	if (feature.get("annotation").equals("PATRIC")) {
		//ArrayList<ResultType> uniprot = conn_summary.getUniprotAccession(fId);
		uniprot = conn_summary.getUniprotAccession(feature.get("na_feature_id").toString());
		
		if (uniprot != null && uniprot.size() > 0) {
			uniprot_accession = uniprot.get(0).get("uniprotkb_accession");
		}
	}
	
	if (feature.get("annotation").equals("PATRIC")) {
		refseqInfo = conn_summary.getRefSeqInfo("PATRIC", feature.get("na_feature_id").toString());
		//System.out.println("refseqInfo:"+refseqInfo.toString());
		if (refseqInfo.get("gene_id")!=null && refseqInfo.get("gene_id").equals("") == false) {
			refseqLink = SiteHelper.getExternalLinks("ncbi_gene").replace("&","&amp;")+refseqInfo.get("gene_id");
		}
		refseqLocusTag = (String) feature.get("refseq_locus_tag");
	} 
	else if (feature.get("annotation").equals("RefSeq")) {
		//TODO: this is not working. Refseq has no corresponding PATRIC
		//refseqInfo = conn_summary.getRefSeqInfo(feature.get("na_feature_id").toString());
		refseqLocusTag = (String) feature.get("locus_tag");
	}
%>
	<% if (feature.get("annotation").equals("RefSeq")) { %>
	<div class="close2x" id="note_refseq_only_feature"><b>NOTE:</b> There is no corresponding PATRIC feature. Comparative tools are not available at this time.</div>
	<% } %>
	<table class="basic stripe far2x left" style="width:600px">
	<tbody>
		<tr>
			<th scope="row" style="width:20%">Gene ID</th>
			<td>
				<% if (feature.get("annotation").equals("PATRIC") && feature.get("locus_tag") != null) { %>
					<span><b>PATRIC</b></span>: 
					<span><%=feature.get("locus_tag") %> </span>
					&nbsp;&nbsp;&nbsp;&nbsp;
				<% } %>
				
				<% if (refseqLocusTag != null) { %>
					<span><b>RefSeq</b></span>: 
					<% if (refseqLink != null) { %>
						<a href="<%=refseqLink %> " target="_blank"><%=refseqLocusTag%></a>
					<% } else { %>
						<%=refseqLocusTag%>
					<% } %>
					&nbsp;&nbsp;
				<% } %>
				&nbsp;
			</td>
		</tr>
		<tr>
			<th scope="row">Protein ID</th>
			<td>
				<% if (feature.get("refseq_protein_id") != null) { %>
					<span><b>RefSeq</b></span>:
					<% if (refseqInfo != null) { %>
						<span><a href="<%=SiteHelper.getExternalLinks("ncbi_protein")+refseqInfo.get("gi_number")%>" target="_blank"><%=feature.get("refseq_protein_id") %></a></span>
					<% } else { %>
						<span><%=feature.get("refseq_protein_id") %></span>
					<% } %>
					&nbsp;&nbsp;&nbsp;&nbsp;
				<% } %>
				<% if (uniprot != null && uniprot.size() > 0) { %>
				<span><b>UnitProt</b></span>: 
				<span><a href="<%=SiteHelper.getExternalLinks("UniProtKB-Accession") %><%=uniprot_accession %>" target="_blank"><%=uniprot_accession %></a></span>
				&nbsp;&nbsp;
				<span> <a href="#" onclick="toggleLayer('uniprot_detail');return false;"><%=uniprot.size() %> IDs are mapped</a></span>
				<div id="uniprot_detail" class="table-container" style="display:none">
					<table class="basic">
					<% for (int u=0; u<uniprot.size(); u++) { %>
					<tr>
						<th scope="row" style="width:20%"><%=uniprot.get(u).get("id_type") %></th>
						<td><%
							uniprot_link = SiteHelper.getExternalLinks(uniprot.get(u).get("id_type").trim()).replace("&","&amp;");
							if (uniprot_link != "" && uniprot.get(u).get("id_type").matches("HOGENOM|OMA|ProtClustDB|eggNOG")) {
								%><a href="<%=uniprot_link%><%=uniprot_accession %>" target="_blank"><%=uniprot.get(u).get("id") %></a><%
							} else if (uniprot_link != "") {
								%><a href="<%=uniprot_link%><%=uniprot.get(u).get("id") %>" target="_blank"><%=uniprot.get(u).get("id") %></a><%
							} else {
								%><%=uniprot.get(u).get("id") %><%
							}
						%></td>
					</tr>
					<% } %>
					</table>
				</div>
				<% } %>
				&nbsp;
			</td>
		</tr>
	</tbody>
	</table>
	
	<div id="feature_box" class="far2x right">
		<div id="gene_symbol"><%=(feature.get("gene")!=null)?feature.get("gene"):"" %></div>
		<% if (feature.get("strand").equals("+")) { %>
		<img id="strand" alt="forward strand" src="/patric/images/forward.png"/>
		<% } else { %>
		<img id="strand" alt="reverse strand" src="/patric/images/reverse.png"/>
		<% } %>
		<div id="feature_type"><%=feature.get("feature_type") %></div>
	</div>
	
	<div class="clear"></div>

	<table class="basic stripe far2x">
	<thead>
		<tr>
			<th scope="col">Annotation</th>
			<th scope="col">Locus Tag</th>
			<th scope="col">Start</th>
			<th scope="col">End</th>
			<th scope="col">NT Length</th>
			<th scope="col">AA Length</th>
			<th scope="col">Product</th>
		</tr>
	</thead>
	<tbody>
	<% for (Object obj: relatedFeatures) { JSONObject an = (JSONObject) obj; %>
		<tr>
			<td><%=an.get("annotation") %></td>
			<td><%=(an.get("locus_tag")!=null)?an.get("locus_tag"):"-" %></td>
			<td class="right-align-text"><%=an.get("start_max") %></td>
			<td class="right-align-text"><%=an.get("end_min") %></td>
			<td class="right-align-text"><%=an.get("na_length") %></td>
			<td class="right-align-text"><%=(an.get("aa_length")!=null)?an.get("aa_length"):"-" %></td>
			<td><% if (an.get("product")!=null) { %>
					<%=an.get("product")%>
				<% } else if (an.get("feature_type").equals("CDS") == false) { %>
					(feature type: <%=an.get("feature_type") %>)
				<% } else { %>
					-
				<% } %>
			</td>
		</tr>
	<% } %>
	</tbody>
	</table>
	
<% } %>

<script type="text/javascript">
//<![CDATA[
Ext.onReady(function () {
	if (Ext.get("tabs_featureoverview")!=null) {
		Ext.get("tabs_featureoverview").addCls("sel");
	}
});
//]]>
</script>