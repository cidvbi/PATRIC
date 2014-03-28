<%@ page import="java.io.*" %><%
	

	String text = request.getParameter("data");
	String fileformat = request.getParameter("fileformat");
    
	if(fileformat.equals("newick")){
    	
  		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\"tree.nwk\"");
  
		out.println(text.toString());
		
    }else{
    	
    	response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\"aligner.aga\"");
  
		out.println(text.toString());
    }
    
 %>
