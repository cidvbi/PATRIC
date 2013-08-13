<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%

	JSONParser a = new JSONParser();
	JSONObject val = (JSONObject) a.parse(request.getParameter("val").toString());
	
	JSONObject json = new JSONObject();
	JSONArray items = null;

	String algorithm = val.get("alg") != null?val.get("alg").toString():"";
	String pid = val.get("pId") != null?val.get("pId").toString():"";
	String pathway_class = val.get("pClass") != null?val.get("pClass").toString():"";
	String ec_number = val.get("ecN") != null?val.get("ecN").toString():"";
	String cType = val.get("cType") != null?val.get("cType").toString():"";
	String cId = val.get("cId") != null?val.get("cId").toString():"";
	String need = val.get("need") != null?val.get("need").toString():"";
	
	HashMap<String, String> key = new HashMap<String, String>();
	
	if(cType.equals("genome"))
		key.put("genomeId",cId);
	else
		key.put("taxonId",cId);
	key.put("algorithm",algorithm);
	key.put("map",pid);
	key.put("pathway_class",pathway_class);
	key.put("ec_number",ec_number);
	
	DBPathways conn_pathways = new DBPathways();
	
	Iterator<?> itr = null;
	
	Object obj = null;
	
	if(need.equals("pathway")){
		items = conn_pathways.getListOfPathwayNameList(key);
		json.put(need, items);
	}
	if(need.equals("ec")){
		items = conn_pathways.getListOfEc_NumberList(key);
		json.put(need, items);
	}
	if(need.equals("parent")){
		items = conn_pathways.getListOfPathwayParentList(key);
		json.put(need, items);
	}
	if(need.equals("algorithm")){
		items = conn_pathways.getListOfAlgorithmList(key);
		json.put(need, items);
	}
	
	out.println(json.toString());
%>