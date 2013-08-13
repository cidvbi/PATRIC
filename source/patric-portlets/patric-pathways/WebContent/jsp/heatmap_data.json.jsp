<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSummary" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="org.json.simple.*" %>
<%

	HashMap<String, String> key = new HashMap<String, String>();
		
	if (request.getParameter("map")!=null) {
		key.put("map",request.getParameter("map"));
	}	
	
	String genomeId, taxonId, algorithm;
	
	genomeId = request.getParameter("genomeId");
	taxonId = request.getParameter("taxonId");
	algorithm = request.getParameter("algorithm");
	
	if (taxonId !=null && !taxonId.equals(""))  {
	
		key.put("taxonId", taxonId);
		
	}else if (genomeId !=null && !genomeId.equals(""))  {
		
		key.put("genomeId", genomeId);
		
	}

	key.put("algorithm", algorithm);
	
	DBPathways conn_pathways = new DBPathways();
	JSONObject json = new JSONObject();

	ArrayList<ResultType> items;
	
	
	System.out.print("hm"+genomeId);
	System.out.print("hm"+taxonId);
	System.out.print("hm"+algorithm);

	try {
		
		items = conn_pathways.getHeatMapData(key, 0, -1);
		
		json.put("data", items);
		
		key.remove("map");
		
		items = conn_pathways.getGenomeNames(key, 0, -1);

		json.put("genomes", items);
		
	} catch (NullPointerException nex) {
	}

	out.println(json.toString());	
	
%>