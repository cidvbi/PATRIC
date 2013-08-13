<%@ page import="java.util.*"%><%@ page
	import="edu.vt.vbi.patric.common.SQLHelper"%><%@ page
	import="org.json.simple.*"%>
<%
	String featuretype = request.getParameter("featuretype");
	JSONObject json = new JSONObject();

	try {
		HashMap<String, String> hp = SQLHelper.getDisplayColumns(featuretype);
		json.put("header", hp.get("header"));
		json.put("field", hp.get("field"));
	}
	catch (NullPointerException nex) {
		json.put("header", "");
		json.put("field", "");
	}

	out.println(json.toString());
%>