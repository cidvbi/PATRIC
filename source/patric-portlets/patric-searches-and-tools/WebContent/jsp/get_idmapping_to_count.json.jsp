<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.*" 
%><%

	JSONObject json = new JSONObject();
	
	DBSearch conn_search = new DBSearch();
	
	HashMap<String,String> key = new HashMap<String,String>();
	String keyword = request.getParameter("keyword");
	String from = request.getParameter("from");
	String to = request.getParameter("to");
	String field = request.getParameter("field");
	
	key.put("keyword", keyword);
	key.put("from", from);
	key.put("to", to);
	key.put("field", field);
		
	int count = conn_search.getIDToCount(key);
	
	json.put("result",count);
		
	out.println(json.toString());
%>