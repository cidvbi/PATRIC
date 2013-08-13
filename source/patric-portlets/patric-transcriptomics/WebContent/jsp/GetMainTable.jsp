<%@ page import="java.util.*" %><%@ page import="java.io.*" %><%@ page import="java.sql.SQLException" %><%
	String parameter = request.getParameter("GeneFileName");
	String fileType = request.getParameter("GeneFileType");
	String data = request.getParameter("data");
    StringBuilder output = new StringBuilder();
    
    if (fileType.equals("xls") || fileType.equals("xlsx")) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ parameter +"." + fileType + "\"");
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
							+ parameter + "." + fileType +"\"");
		String a[] = data.split("\n");
		output.append("<table>");
    	
		for(int i=0; i<a.length; i++){
			output.append("<tr>");
			String b[] = a[i].split("\t");
			for(int j=0; j<b.length; j++){
				output.append("<td>" + b[j] + "</td>");
			}
			output.append("</tr>");
		}
   	    output.append("</table>");
 	} else if (fileType.equals("txt")) {
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ parameter +"." + fileType +"\"");
		output.append(data);	
    }
    out.println(output.toString());
 %>
