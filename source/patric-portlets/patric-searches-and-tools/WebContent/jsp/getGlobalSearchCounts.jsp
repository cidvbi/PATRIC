<%@ page session="true" 
%><%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.common.SQLHelper" 
%><%@ page import="edu.vt.vbi.patric.common.StringHelper" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.dao.*" 
%><%@ page import="javax.portlet.PortletSession" 
%><%@ page import="java.io.OutputStream" 
%><%

	SolrInterface solr = new SolrInterface();
	String keyword = request.getParameter("keyword");
	String spellcheck = request.getParameter("spellcheck");
	JSONObject result = new JSONObject();
	
	if(Boolean.parseBoolean(spellcheck)){
		JSONObject a = solr.getSpellCheckerResult(keyword);
		if(a.containsKey("suggestion")){
			keyword = a.get("suggestion").toString();
			result.put("suggestion", keyword);
		}
	}
	
	JSONArray data = new JSONArray();
	solr.setCurrentInstance("GenomicFeature");
	JSONObject obj = solr.getSummaryforGlobalSearch(keyword);
	data.add(obj);
	solr.setCurrentInstance("GenomeFinder");
	obj = solr.getSummaryforGlobalSearch(keyword);
	data.add(obj);
	solr.setCurrentInstance("GlobalTaxonomy");
	obj = solr.getSummaryforGlobalSearch(keyword);
	data.add(obj);
	solr.setCurrentInstance("GENEXP_Experiment");
	obj = solr.getSummaryforGlobalSearch(keyword);
	data.add(obj);
	
	result.put("data", data);
	out.print(result.toString());

%>
