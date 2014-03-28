<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%
	HashMap<String, String> key = new HashMap<String, String>();
	DBPathways conn_pathways = new DBPathways();
	JSONObject ret = new JSONObject();	
	JSONParser a = new JSONParser();
	JSONObject val = (JSONObject) a.parse(request.getParameter("val").toString());
	
	
	ArrayList<ResultType> items = null;
	
	String need = val.get("need").toString();
	
	//System.out.println(need);
	
	
	if(need.equals("all")){
		if(val.get("genomeId") != null)
			key.put("genomeId", val.get("genomeId").toString());
		if(val.get("taxonId") != null)
			key.put("taxonId", val.get("taxonId").toString());
		key.put("map", val.get("map").toString());
		
		try {
			items = conn_pathways.getCompPathwayCoordinates(key,  0, -1);
			ret.put("genome_x_y", items);
			key.remove("map");
			items = conn_pathways.getCompPathwayPathwayIds(key, 0, -1);
			ret.put("genome_pathway_x_y", items);				
			items = conn_pathways.getMapIdsInMap(val.get("map").toString());
			ret.put("map_ids_in_map", items);
			items = conn_pathways.getAllCoordinatesInMap(val.get("map").toString());
			ret.put("all_coordinates", items);
		} catch (NullPointerException nex) {
		}
		
	}else{
		
		//System.out.println(val.get("value").toString());
		
		key.put("map", val.get("map").toString());
		key.put(need, val.get("value").toString());
		
		
		try {
			if(need.equals("ec_number"))
				items = conn_pathways.getCompPathwayEcCoordinates(key, 0, -1);
			else
				items = conn_pathways.getCompPathwayFeatureCoordinates(key, 0, -1);
			
			ret.put("coordinates", items);
		} catch (NullPointerException nex) {
		}		
	}
		
	out.println(ret.toString());
%>