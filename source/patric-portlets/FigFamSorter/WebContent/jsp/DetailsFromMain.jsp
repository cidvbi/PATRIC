<%@ page import="java.util.*" %><%@ page import="java.io.*" %><%@ page import="figfamGroups.edu.vt.vbi.sql.FigFam" %><%@ page import="java.sql.SQLException" %><%
	String parameter = request.getParameter("detailsName");
	String fileType = request.getParameter("detailsType");
	String genomeIds = request.getParameter("detailsGenomes");
	String figfamIds = request.getParameter("detailsFigfams");
	String type = request.getParameter("portlet_type");
	FigFam figger = new FigFam();
	int[] stats = new int[FigFam.DETAILS_STATS_ROOM];
	
	String[] tableData = figger.getDetails(genomeIds, figfamIds, stats, type);
	
	String[] headers = 
		{"Group Id", "Genome Name", "Accession", "Locus Tag", "Feature Type",
		  "Start", "End", "Length(NT)", "Strand", "Length(AA)", "Gene Symbol",
		  "Product Description"};
	int columnCount = headers.length - 1;
	
    StringBuilder output = new StringBuilder();
    if (fileType.equals("xls") || fileType.equals("xlsx")) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ parameter +"." + fileType + "\"");
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
							+ parameter + "." + fileType +"\"");

    	output.append("<table width='100%' cellspacing='0' border='1'>");
    	output.append("<tr>");
    	for (int i = 0; i < headers.length; i++) {
    		output.append("<th>" + headers[i] + "</th>");    		
    	}
    	output.append("</tr>");
    
    	int dataAt = 0;
    	while (dataAt < tableData.length) {
    		int count = Integer.parseInt(tableData[dataAt]);
    		String figfamName = tableData[dataAt + 1];
    		dataAt += 3;
    		while (0 < count) {
        		output.append("<tr>");
  		   		output.append("<td>" + figfamName + "</td>");
    			for (int i = 0; i < columnCount; i++) {
       		   		output.append("<td>" + tableData[dataAt] + "</td>");
       		   		++dataAt;
    			}
           		output.append("</tr>");
           		--count;
    		}
    	}
   	    output.append("</table>");
   	    out.println(output.toString());
 	} else if (fileType.equals("txt")) {
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""
					+ parameter +"." + fileType +"\"");
		output.append(headers[0]);
		for (int i = 1; i < headers.length; i++) {
    		output.append("\t" + headers[i]);			
		}
    	output.append("\r\n");

    	int dataAt = 0;
    	while (dataAt < tableData.length) {
    		int count = Integer.parseInt(tableData[dataAt]);
    		String figfamName = tableData[dataAt + 1];
    		dataAt += 3;
    		while (0 < count) {
        		output.append(figfamName);
    			for (int i = 0; i < columnCount; i++) {
       		   		output.append("\t" + tableData[dataAt]);
       		   		++dataAt;
    			}
          		output.append("\r\n");
          		--count;
    		}
    	}
		out.println(output.toString());
    }
 %>
