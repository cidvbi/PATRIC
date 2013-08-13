<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%
String _accession = request.getParameter("accession");
String _algorithm = request.getParameter("algorithm");

if (_accession!=null && _algorithm!=null) {
	DBSummary db = new DBSummary();
	HashMap<String,String> key = new HashMap<String,String>();
	key.put("accession", _accession);
	key.put("algorithm", _algorithm);

	ArrayList<Integer> hist = db.getHistogram(key);

	response.setContentType("application/json");
	out.println(hist.toString());
}
else {
	%>wrong parameters!<%
}
%>
