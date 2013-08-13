<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.*" 
%><%
	String cId = request.getParameter("cId");
	String cType = request.getParameter("cType");
	String input = request.getParameter("input");
	String algorithm = request.getParameter("algorithm");
	String type = request.getParameter("type");
	
	JSONObject json = new JSONObject();
	DBSearch conn_search = new DBSearch();
	
	if(type.equals("go")) {
		try {
			
			ArrayList<ResultType> items = conn_search.getGONaFeatureIdList(cId, cType, input, algorithm);
			json.put("genes",items);
		}
		catch (NullPointerException nex) {
		}
	} else if(type.equals("ec")) {
		try {
			ArrayList<ResultType> items = conn_search.getECNaFeatureIdList(cId, cType, input, algorithm);
			json.put("genes",items);
		}
		catch (NullPointerException nex) {
		}
	}
	out.println(json.toString());
%>