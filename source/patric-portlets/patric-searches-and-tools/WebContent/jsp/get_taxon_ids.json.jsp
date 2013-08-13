<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.*" 
%><%
	String cId = request.getParameter("cId");
	String cType = request.getParameter("cType");
	String genomeId = request.getParameter("genomeId");
	String algorithm = request.getParameter("algorithm");
	String status = request.getParameter("status");
	
	JSONObject json = new JSONObject();
	DBSearch conn_search = new DBSearch();
	
	try {
		ArrayList<ResultType> items = conn_search.getTaxonIdList(cId, cType, genomeId, algorithm, status);
		json.put("ids",items);
	}
	catch (NullPointerException nex) {
	}
	
	out.println(json.toString());
%>