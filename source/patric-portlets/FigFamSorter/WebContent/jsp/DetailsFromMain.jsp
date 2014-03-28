<%@ page import="java.util.*" 
%><%@ page import="java.io.*" 
%><%@ page import="figfamGroups.edu.vt.vbi.sql.FigFam" 
%><%@ page import="java.sql.SQLException"
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%
	String genomeIds = request.getParameter("detailsGenomes");
	String figfamIds = request.getParameter("detailsFigfams");
	FigFam figger = new FigFam();

	String _fileformat = request.getParameter("detailsType");

	ArrayList<String> _tbl_header = new ArrayList<String>();
	ArrayList<String> _tbl_field = new ArrayList<String>();
	
	
	JSONArray _tbl_source = figger.getDetails(genomeIds, figfamIds);
	_tbl_header.addAll(Arrays.asList(new String[] {"Group Id", "Genome Name", "Accession", "Locus Tag", "Start", "End", "Length(NT)", "Strand", "Length(AA)", "Gene Symbol", "Product Description"}));
	_tbl_field.addAll(Arrays.asList(new String[] {"figfam_id", "genome_name", "accession", "locus_tag", "start_max",  "end_min", "na_length", "strand", "aa_length", "gene", "figfam_product"}));
	
	ExcelHelper excel = new ExcelHelper("xssf", _tbl_header, _tbl_field, _tbl_source);
	excel.buildSpreadsheet();
	
	String _filename = "ProteinFamilyFeatures"; 
	
	response.setContentType("application/octetstream");
	if (_fileformat.equalsIgnoreCase("xlsx")) {

		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		
		OutputStream outs = response.getOutputStream();
		excel.writeSpreadsheettoBrowser(outs);
		outs.flush();

	}else if(_fileformat.equalsIgnoreCase("txt")){
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");

		out.println(excel.writeToTextFile());
	}
 %>
