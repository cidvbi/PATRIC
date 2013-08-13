<%@ page import="java.util.*"%>
<%@ page import="edu.vt.vbi.patric.common.SolrInterface"%>
<%@ page import="edu.vt.vbi.patric.dao.ResultType"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%
	String keyword = request.getParameter("keyword");
	String facet = request.getParameter("facet");

	SolrInterface solr = new SolrInterface();
	solr.setCurrentInstance("GenomicFeature");

	ResultType key = new ResultType();
	key.put("facet", facet);
	key.put("keyword", keyword);

	JSONObject object = solr.getData(key, null, facet, 0, 1, true, false, false);

	out.println(object.toString());
%>