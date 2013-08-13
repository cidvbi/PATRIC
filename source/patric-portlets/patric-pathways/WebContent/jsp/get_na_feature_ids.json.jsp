<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="org.json.simple.*" %>
<%

	String cId = request.getParameter("cId");
	String cType = request.getParameter("cType");
	String map = request.getParameter("map");
	String algorithm = request.getParameter("algorithm");	
	String ec_number = request.getParameter("ec_number");
	String featureList = request.getParameter("featureList");
	
	//System.out.print(cId);
	//System.out.print(cType);
	//System.out.print(map);
	//System.out.print(ec_number);
	//System.out.print(algorithm);
	
	JSONObject json = new JSONObject();
	
	DBPathways conn_pathways = new DBPathways();
	
		try {
		
		ArrayList<ResultType> items = conn_pathways.getGenomeNaFeatureIdList(cId, cType, map, algorithm, ec_number, featureList);
		json.put("genes",items);
		
	}
	catch (NullPointerException nex) {
	}
	
	out.println(json.toString());



%>