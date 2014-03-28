<%@ page import="java.util.*" 
%><%@ page import="java.io.*" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.StringHelper"
%><%@ page import="figfamGroups.edu.vt.vbi.sql.FigFam" 
%><%

	String parameter = request.getParameter("fileName");
	String fileType = request.getParameter("fileType");
    StringBuilder output = new StringBuilder();
    	
   	String _fileformat = request.getParameter("_fileformat");
   	String _filename = "MapFeatureTable_Cell";
   	
	if (_fileformat.equalsIgnoreCase("xls") || _fileformat.equalsIgnoreCase("xlsx")){
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		
	}else if(_fileformat.equalsIgnoreCase("txt")){
		
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		
	}
	
	out.println(request.getParameter("_data").toString());
%>
