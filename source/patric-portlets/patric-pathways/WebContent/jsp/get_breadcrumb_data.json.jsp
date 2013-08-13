<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%

	HashMap<String, String> key = new HashMap<String, String>();

	JSONParser a = new JSONParser();
	JSONObject val = (JSONObject) a.parse(request.getParameter("val").toString());
	
	DBPathways conn_pathways = new DBPathways();
	int distinct_items = 0;
	
	if(val != null && val.get("need") != null){
		if(val.get("search_on") != null){
			key.put("search_on",val.get("search_on").toString().trim());
			if (val.get("search_on").toString().equalsIgnoreCase("Keyword")) {
				key.put("keyword", val.get("keyword").toString().trim());
			}
		}
		
		if(val.get("pClass") != null && !val.get("pClass").toString().equals(""))
			key.put("pathway_class", val.get("pClass").toString().trim());	
			
		if(val.get("ecN") != null && !val.get("ecN").toString().equals(""))
			key.put("ec_number", val.get("ecN").toString().trim());
		
		if(val.get("map") != null && !val.get("map").toString().equals(""))
			key.put("map", val.get("map").toString().trim());
		
		if(val.get("alg") != null && !val.get("alg").toString().equals(""))
			key.put("algorithm", val.get("alg").toString().trim());
		
		if (val.get("genomeId") != null && !val.get("genomeId").toString().equalsIgnoreCase(""))
			key.put("genomeId", val.get("genomeId").toString());
		else if (val.get("taxonId") != null && !val.get("taxonId").toString().equalsIgnoreCase("")) 
			key.put("taxonId", val.get("taxonId").toString());
			try {
				if(val.get("need").toString().equals("0"))
					distinct_items = conn_pathways.getDistinctCompPathwayPathwayBreadCrumb(key);
				else if(val.get("need").toString().equals("1"))
					distinct_items = conn_pathways.getDistinctCompPathwayECBreadCrumb(key);
				else if(val.get("need").toString().equals("2"))
					distinct_items = conn_pathways.getDistinctCompPathwayFeatureBreadCrumb(key);
					
				out.println(distinct_items);
			} catch (NullPointerException nex) {
			}
	}
%>