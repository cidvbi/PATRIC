<%@ page import="java.util.*" 
%><%@ page import="java.net.*"
%><%@ page import="java.io.*"
%><%
String taxonID = request.getParameter("taxonID");
//String baseURL = "http://"+request.getServerName()+request.getContextPath()+"/resources/";
String baseURL = "http://"+request.getServerName()+"/patric/static/phylogeny/";

URL url = new URL(baseURL+taxonID+".tree");
URLConnection conn = url.openConnection();
//System.out.println("reading pdb: "+url.toString());

try {
	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String line;
	StringBuffer sb = new StringBuffer();
	
	while ((line = rd.readLine())!=null) {
		sb.append(line);
	}
	rd.close();

	response.setContentType("application/octetstream");
	response.setHeader("Content-Disposition", "attachment; filename=\""+taxonID+".nwk\"");
	
	%><%=sb.toString()%><%
	sb = null;
} catch (Exception ex) {
	%>No data is available.(<%=url.toString()%>)<%
	//ex.printStackTrace();
}
%>