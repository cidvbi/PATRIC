<%@ page import="java.util.*" 
%><%@ page import="java.net.*"
%><%@ page import="java.io.*"
%><%
String pdbID = request.getParameter("pdbID");
String baseURL = "http://www.rcsb.org/pdb/files/";

URL url = new URL(baseURL+pdbID+".pdb");
URLConnection conn = url.openConnection();
//System.out.println("reading pdb: "+url.toString());

try {
	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String line;
	StringBuffer pdb = new StringBuffer();
	
	while ((line = rd.readLine())!=null) {
		pdb.append(line);
		pdb.append("\n");
	}
	rd.close();

	%><%=pdb.toString()%><%
} catch (Exception ex) {
	%>No PDB data is available.(<%=url.toString()%>)<%
	//ex.printStackTrace();
}
%>