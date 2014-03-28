<%@ page session="true" 
%><%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.*" 
%><%@ page import="edu.vt.vbi.patric.common.*" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject"
%><%@ page import="java.io.OutputStream" %><%

	DBSummary conn_summary = new DBSummary();
	ExcelHelper excel= null;
	JSONArray _tbl_source = null;
	ArrayList<ResultType> _tbl_source_ = null;
	
	ArrayList<String> _tbl_header = new ArrayList<String>();
	
	ArrayList<String> _tbl_field = new ArrayList<String>();
    
	if(request.getParameter("_tablesource")!= null && request.getParameter("_tablesource").equals("Table_Cell")){
    	
    	String _fileformat = request.getParameter("_fileformat");
    	String _filename = "MapTable_Cell";
    	
		if (_fileformat.equalsIgnoreCase("xls") || _fileformat.equalsIgnoreCase("xlsx")){
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
		}else if(_fileformat.equalsIgnoreCase("txt")){
			
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
		}
		
		out.println(request.getParameter("_data").toString());
    	
    }else{
    	
    	String _fileformat = request.getParameter("_fileformat");
    	String _filename = "Table_Gene";
    	HashMap<String, String> key = new HashMap<String, String>();
    	JSONArray sort = new JSONArray();
    	HashMap<String, String> condition = new HashMap<String, String>();
			
   		String sort_field = request.getParameter("sort");
		String sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			JSONObject x = new JSONObject();
			x.put("property", sort_field);
			x.put("direction", sort_dir);
			sort.add(x);
			condition.put("sortParam", sort.toString());
		}
		condition.put("na_feature_ids", request.getParameter("featureIds"));
		SolrInterface solr = new SolrInterface();
	    JSONObject object = solr.getFeaturesByID(condition);
	    _tbl_source = (JSONArray)object.get("results");
    	
    	_tbl_header.addAll(Arrays.asList(new String[] {"Genome", "Accession", "Locus Tag", "RefSeq Locus Tag", "Gene Symbol", "Annotation", "Feature Type", "Start", "End", "Length", "Strand", "AA Length", "Gene Symbol", "Product",}));
		_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag", "refseq_locus_tag", "gene", "annotation", "feature_type", "start_max", "end_min", "na_length", "strand", "aa_length", "gene", "product"}));
		
	    
    	excel = new ExcelHelper("xssf",_tbl_header, _tbl_field, _tbl_source);
    	excel.buildSpreadsheet();
    	
    	if (_fileformat.equalsIgnoreCase("xlsx")) {
			
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
			OutputStream outs = response.getOutputStream();

			excel.writeSpreadsheettoBrowser(outs);

			outs.flush();
			
			
		}else{
			
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");

			out.println(excel.writeToTextFile());

		}
		
    }
 %>
