<%@ page import="java.util.*"%><%@ page
	import="edu.vt.vbi.patric.common.SQLHelper"%><%@ page
	import="edu.vt.vbi.patric.common.StringHelper"%><%@ page
	import="edu.vt.vbi.patric.dao.*"%>
<%
	DBSearch conn_search = new DBSearch();
	DBSummary conn_summary = new DBSummary();

	// getting common params
	String _filetext = request.getParameter("copy_text_to_file");

	response.setContentType("application/octetstream");
	response.setHeader("Content-Disposition", "attachment; filename=\"PATRICCopy.txt\"");

	out.println(_filetext);
%>