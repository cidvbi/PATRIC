<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%
String _accession = request.getParameter("accession");
String _algorithm = request.getParameter("algorithm");

String _accn = null;
String _sid = null;

if (_accession!=null && _algorithm!=null) {
	
	String[] names = _accession.split("\\|");
	if (names[3] != null) {
		_accn = names[3];
	}
	if (names[1] != null) {
		_sid = names[1];
	}
	
	DBSummary db = new DBSummary();
	HashMap<String,String> key = new HashMap<String,String>();
	key.put("accession", _accn);
	key.put("algorithm", _algorithm);
	key.put("sid", _sid);

	//System.out.println(key.toString());

	ArrayList<Integer> hist = db.getHistogram(key);

	response.setContentType("application/json");
	out.println(hist.toString());
}
else {
	%>wrong parameters!<%
}
%>
