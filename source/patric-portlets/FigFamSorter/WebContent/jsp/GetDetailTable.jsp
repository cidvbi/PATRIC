<%@ page import="java.util.*" 
%><%@ page import="java.io.*" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.StringHelper"
%><%@ page import="figfamGroups.edu.vt.vbi.sql.FigFam" 
%><%

	String parameter = request.getParameter("fileName");
	String fileType = request.getParameter("fileType");
    StringBuilder output = new StringBuilder();
    
    if(request.getParameter("_tablesource")!= null && request.getParameter("_tablesource").equals("FeatureTable_Cell")){
    	
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
    	
    }else if(request.getParameter("_tablesource") != null && request.getParameter("_tablesource").equals("FeatureTable_Feature")){
    	
    	ArrayList<ResultType> _tbl_source = null;
    	
    	FigFam access = new FigFam();
    	
		String[] result = new String[2];
		result[0] = request.getParameter("genomeIds");
		result[1] = request.getParameter("figfamNames");
		
		String type = request.getParameter("portlet_type");
    	
    	_tbl_source = access.getDetailsArray_HeatMapDownload(result, 0, -1, type);
    	
    	ArrayList<String> _tbl_header = new ArrayList<String>();
    	ArrayList<String> _tbl_field = new ArrayList<String>();
    	
    	_tbl_header.addAll(Arrays.asList(new String[] {"Group ID", "Genome", "Accession", "Locus Tag", "Annotation", "Feature Type", "Start", "End", "Length(NT)", "Strand", "Product Description"}));
    	_tbl_field.addAll(Arrays.asList(new String[] {"groupId", "genome_name","accession","locus_tag","algorithm","name","start_max","end_min","na_length","strand", "product"}));
				
		String _filename = "MapFeatureTable";
		String _fileformat = request.getParameter("_fileformat");
		
		if (_fileformat == null) {
		}
		else if (_fileformat.equalsIgnoreCase("xlsx")) 
		{
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
			output.append("<table width=\"100%\" cellspacing=\"0\" border=\"1\">");
			output.append("<tr>");
			
			// print header
			for (int i=0; i<_tbl_header.size(); i++) {
				output.append("<th>"+_tbl_header.get(i).toString()+"</th>");
			}
			output.append("</tr>");
			
			// print contents
			Iterator<?> itr = _tbl_source.iterator();
			HashMap<?,?> row;
			String _f = "";
			while (itr.hasNext()) {
			row = (HashMap<?,?>) itr.next();
			output.append("<tr>");
			for (int i=0;i<_tbl_field.size();i++) {
				_f = _tbl_field.get(i).toString();
				if (row.get(_f)!=null) {
					output.append("<td>"+row.get(_f)+"</td>");
				} else {
					output.append("<td></td>");
				}
			}
			output.append("</tr>");
			}
			output.append("</table>");
			out.println(output.toString());
			
		} else if (_fileformat.equalsIgnoreCase("txt")) 
		{
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			int i=0;
			for (i=0; i<_tbl_header.size()-1;i++) {
				output.append(_tbl_header.get(i).toString()+"\t");
			}
			output.append(_tbl_header.get(i).toString()+"\r\n");
			Iterator<?> itr = _tbl_source.iterator();
			HashMap<?,?> row;
			String _f = "";
			while (itr.hasNext()) {
				row = (HashMap<?,?>) itr.next();
				for (i=0;i<_tbl_field.size()-1;i++) {
					_f = _tbl_field.get(i).toString();
					if (row.get(_f)!=null) {
						output.append(StringHelper.strip_html_tag(row.get(_f).toString())+"\t");
					} else {
						output.append("\t");
					}
				}
				_f = _tbl_field.get(i).toString();
				output.append(row.get(_f)+"\r\n");
			}
			out.println(output.toString());
		}
    	
    }else{
    	String data = request.getParameter("data");
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
    }
 %>
