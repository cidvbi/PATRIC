<%@ page import="java.util.HashMap" 
%><%@ page import="java.util.ArrayList" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch" 
%><%
	String cType = request.getParameter("context_type");
	String cId = request.getParameter("context_id");
	String filterQuery = "";
	ResultType key = new ResultType();
	JSONObject hypotheticalProteins = new JSONObject();
	JSONObject functionalProteins = new JSONObject();
	JSONObject ecAssignedProteins = new JSONObject();
	JSONObject goAssignedProteins = new JSONObject();
	JSONObject pathwayAssignedProteins = new JSONObject();
	JSONObject figfamAssignedProteins = new JSONObject();
	
	if (cType.equals("taxon")) {
		if (cId.equals("2") == false) {
			DBSearch db_search = new DBSearch();
			ArrayList<ResultType> items = db_search.getTaxonIdList(cId, cType, "", "", "");
	
			if (items.size() > 0) {
				filterQuery = "gid:("+items.get(0).get("id");
				for (int i = 1; i < items.size(); i++) {
					filterQuery += " OR " + items.get(i).get("id");
				}
				filterQuery += ")";
			} else {
				filterQuery = "gid:0"; // no associated genomes, so query should return null
			}
		}
		/// gid:(xxx OR xxx OR xxx)	
	} else {
		filterQuery = "gid:"+cId;
	}
	
	SolrInterface solr = new SolrInterface();
	solr.setCurrentInstance("GenomicFeature");
	
	JSONObject res = null;
	key.put("filter", filterQuery);
	
	// hypothetical
	key.put("keyword", "product:(hypothetical AND protein) AND feature_type_f:CDS");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	JSONObject facets = (JSONObject)res.get("facets");
	JSONObject annotations = (JSONObject)facets.get("annotation_f");
	JSONArray attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		hypotheticalProteins.put(j.get("value"), j.get("count"));
	}

	// funtional assigned
	key.put("keyword", "!product:(hypothetical AND protein) AND feature_type_f:CDS");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	facets = (JSONObject)res.get("facets");
	annotations = (JSONObject)facets.get("annotation_f");
	attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		functionalProteins.put(j.get("value"), j.get("count"));
	}
	
	// ec assigned
	key.put("keyword", "ec:[*%20TO%20*]");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	facets = (JSONObject)res.get("facets");
	annotations = (JSONObject)facets.get("annotation_f");
	attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		ecAssignedProteins.put(j.get("value"), j.get("count"));
	}
	
	// go assigned
	key.put("keyword", "go:[*%20TO%20*]");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	facets = (JSONObject)res.get("facets");
	annotations = (JSONObject)facets.get("annotation_f");
	attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		goAssignedProteins.put(j.get("value"), j.get("count"));
	}
	
	// pathway assigned
	key.put("keyword", "pathway:[*%20TO%20*]");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	facets = (JSONObject)res.get("facets");
	annotations = (JSONObject)facets.get("annotation_f");
	attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		pathwayAssignedProteins.put(j.get("value"), j.get("count"));
	}
	
	// figfam assigned
	key.put("keyword", "figfam_id:[*%20TO%20*]");
	res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);
	
	facets = (JSONObject)res.get("facets");
	annotations = (JSONObject)facets.get("annotation_f");
	attrs = (JSONArray) annotations.get("attributes");
	
	for (Object attr: attrs) {
		JSONObject j = (JSONObject) attr;
		figfamAssignedProteins.put(j.get("value"), j.get("count"));
	}

%>
<table class="basic stripe far2x">
<thead>
	<tr>
		<th width="40%"></th>
		<th width="20%">PATRIC</th>
		<th width="20%">Legacy BRC</th>
		<th width="20%">RefSeq</th>
	</tr>
</thead>
<tbody>
	<tr class="alt">
		<th>Hypothetical proteins</th>
		<td class="right-align-text">
		<% if (hypotheticalProteins.isEmpty() == false
				&& hypotheticalProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=hypothetical_proteins"><%=hypotheticalProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (hypotheticalProteins.isEmpty() == false
				&& hypotheticalProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=hypothetical_proteins"><%=hypotheticalProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (hypotheticalProteins.isEmpty() == false
				&& hypotheticalProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=hypothetical_proteins"><%=hypotheticalProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
	<tr>
		<th>Proteins with functional assignments</th>
		<td class="right-align-text">
		<% if (functionalProteins.isEmpty() == false
				&& functionalProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=functional_proteins"><%=functionalProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (functionalProteins.isEmpty() == false
				&& functionalProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=functional_proteins"><%=functionalProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (functionalProteins.isEmpty() == false
				&& functionalProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=functional_proteins"><%=functionalProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
	<tr class="alt">
		<th>Proteins with EC number assignments</th>
		<td class="right-align-text">
		<% if (ecAssignedProteins.isEmpty() == false
				&& ecAssignedProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=ec"><%=ecAssignedProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (ecAssignedProteins.isEmpty() == false
				&& ecAssignedProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=ec"><%=ecAssignedProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (ecAssignedProteins.isEmpty() == false
				&& ecAssignedProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=ec"><%=ecAssignedProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
	<tr>
		<th>Proteins with GO assignments</th>
		<td class="right-align-text">
		<% if (goAssignedProteins.isEmpty() == false
				&& goAssignedProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=go"><%=goAssignedProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (goAssignedProteins.isEmpty() == false
				&& goAssignedProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=go"><%=goAssignedProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (goAssignedProteins.isEmpty() == false
				&& goAssignedProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=go"><%=goAssignedProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
	<tr class="alt">
		<th>Proteins with Pathway assignments</th>
		<td class="right-align-text">
		<% if (pathwayAssignedProteins.isEmpty() == false
				&& pathwayAssignedProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=pathway"><%=pathwayAssignedProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (pathwayAssignedProteins.isEmpty() == false
				&& pathwayAssignedProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=pathway"><%=pathwayAssignedProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (pathwayAssignedProteins.isEmpty() == false
				&& pathwayAssignedProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=pathway"><%=pathwayAssignedProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
	<tr>
		<th>Proteins with FigFam assignments</th>
		<td class="right-align-text">
		<% if (figfamAssignedProteins.isEmpty() == false
				&& figfamAssignedProteins.get("PATRIC") != null) 
		{
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=figfam_id"><%=figfamAssignedProteins.get("PATRIC") %></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text">
		<% if (figfamAssignedProteins.isEmpty() == false
				&& figfamAssignedProteins.get("BRC") != null)
		{ 
		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=figfam_id"><%=figfamAssignedProteins.get("BRC")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
		<td class="right-align-text last">
		<% if (figfamAssignedProteins.isEmpty() == false
				&& figfamAssignedProteins.get("RefSeq") != null) 
		{ 
        		%>
			<a href="FeatureTable?cType=<%=cType%>&amp;cId=<%=cId%>&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=figfam_id"><%=figfamAssignedProteins.get("RefSeq")%></a>
		<% } else { %>
			0
		<% } %>
		</td>
	</tr>
</tbody>
</table>