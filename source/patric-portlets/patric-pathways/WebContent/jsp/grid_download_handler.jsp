<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.common.SQLHelper"
%><%@ page import="edu.vt.vbi.patric.common.StringHelper"
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%@ page import="edu.vt.vbi.patric.dao.*"
%><%@ page import="javax.portlet.PortletSession"
%><%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%@ page import="java.io.OutputStream" 
%><%
	DBPathways conn_pathways = new DBPathways();
	String _filename = "";
	ArrayList<String> _tbl_header = new ArrayList<String>();
	ArrayList<String> _tbl_field = new ArrayList<String>();
	ArrayList<?> _tbl_source = null;

	// getting common params
	String _fileformat = request.getParameter("fileformat");
	String _tablesource = request.getParameter("tablesource");

	HashMap<String, String> key = new HashMap<String, String>();
	HashMap<String, String> sort = null;
	String sort_field;
	String sort_dir;
	
	if (_tablesource == null || _fileformat == null) {
		System.out.println("Error");
		_fileformat = null;
	}
	
	ExcelHelper excel= null;
	
	if (_tablesource.equalsIgnoreCase("PathwayTable")) 
	{
		if (request.getParameter("cType")!=null && !request.getParameter("cType").equals("")) {
			key.put("cType", request.getParameter("cType"));
		}
		if (request.getParameter("cId")!=null && !request.getParameter("cId").equals("")) {
			key.put("cId", request.getParameter("cId"));
		}
			
			if (request.getParameter("ec_number")!=null) {
				key.put("ec_number", request.getParameter("ec_number"));
			}
			
			if (request.getParameter("pathway_name")!=null) {
				key.put("pathway_name", request.getParameter("pathway_name"));
			}
			
			if (request.getParameter("pathway_class")!=null) {
				key.put("pathway_class", request.getParameter("pathway_class"));
			}
			
			if (request.getParameter("keyword")!=null) {
				key.put("keyword", request.getParameter("keyword"));
			}
			
			_filename = "PathwayTable";
			_tbl_source = (ArrayList<?>) conn_pathways.getFeaturePathwayList(key,sort,0,-1);
		
	}else if (_tablesource.equalsIgnoreCase("CompPathwayFinder")) {

		String search_on = request.getParameter("search_on");
		String keyword = request.getParameter("keyword");
		String algorithm = request.getParameter("alg");		
		String genomeId = request.getParameter("genomeId");
		String taxonId = request.getParameter("taxonId");
		String ec_number = request.getParameter("ecN");	
		String map = request.getParameter("pId");

		if(genomeId == null || genomeId.equals(""))
			key.put("taxonId", taxonId);
		else
			key.put("genomeId", genomeId);
		
		if (search_on.equalsIgnoreCase("Map_ID"))	
			key.put("map", keyword.trim());
		else if (search_on.equalsIgnoreCase("Ec_Number"))
			key.put("ec_number", keyword.trim());			
		else if (search_on.equalsIgnoreCase("Keyword"))
			key.put("keyword", keyword.trim());
		
		if (search_on!=null)
			key.put("search_on",search_on.trim());
		
		if (algorithm!=null)
			key.put("algorithm",algorithm);
		
		if (map != null)
			key.put("map", map);
		
		if (ec_number != null)
			key.put("ec_number", ec_number);
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		if (request.getParameter("aT").equals("0")) {
			_tbl_source = (ArrayList<?>) conn_pathways.getCompPathwayPathwayList(key, sort, 0, -1);
			_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "Genome Count","Unique Gene Count", "Unique EC Count", "EC Conservation %", "Gene Conservation"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id", "pathway_name", "pathway_class", "algorithm", "genome_count", "gene_count", "ec_count", "ec_cons", "gene_cons"}));
		}else if (request.getParameter("aT").equals("1")) {
			_tbl_source = (ArrayList<?>) conn_pathways.getCompPathwayECList(key, sort, 0, -1);
			_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "Ec Number", "EC Description", "Genome Count", "Unique Gene Count"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id", "pathway_name", "pathway_class", "algorithm", "ec_number", "ec_name", "genome_count", "gene_count"}));
			
		}else if (request.getParameter("aT").equals("2")) {
			_tbl_source = (ArrayList<?>) conn_pathways.getCompPathwayFeatureList(key, sort, 0, -1);
			_tbl_header.addAll(Arrays.asList(new String[] {"Feature ID", "Genome Name", "Accession", "Locus Tag", "Gene Symbol", "Product Name", "Annotation", "Pathway ID", "Pathway Name", "Ec Number", "EC Description"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"na_deature_id", "genome_name","accession","locus_tag", "gene", "product", "algorithm", "pathway_id", "pathway_name", "ec_number", "ec_name"}));
		}
	
		_filename = "CompPathwayFinder_"+request.getParameter("pk");

	}else if (_tablesource.equalsIgnoreCase("CompPathwayTable")) {
	
		String cId = request.getParameter("cId");
		String cType = request.getParameter("cType");
		String alg = request.getParameter("alg");
		String pClass = request.getParameter("pClass");
		String map = request.getParameter("pId");
		String ecN = request.getParameter("ecN");
	
		if(pClass != null && !pClass.equals(""))
			key.put("pathway_class", pClass);
		
		if(map != null && !map.equals("") )
			key.put("map", map);
		
		if(ecN != null && !ecN.equals(""))
			key.put("ec_number", ecN);
		
		if(alg != null && !alg.equals(""))
			key.put("algorithm", alg);
				
		if(cType.equals("genome"))
			key.put("genomeId", cId);
		else if(cType.equals("taxon"))
			key.put("taxonId", cId);

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		if (request.getParameter("aT").equals("0")) {
			_tbl_source = (ArrayList<?>) conn_pathways.getCompPathwayPathwayList(key, sort, 0, -1);
			if(cType.equals("taxon")){
				_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "Genome Count", "Unique Gene Count", "Unique EC Count", "Ec Conservation %", "Gene Conservation"}));
				_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id","pathway_name","pathway_class", "algorithm", "genome_count", "gene_count", "ec_count", "ec_cons", "gene_cons"}));
			}else if(cType.equals("genome")){
				_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "Unique Gene Count", "Unique EC Count", "Ec Conservation %", "Gene Conservation"}));
				_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id","pathway_name","pathway_class", "algorithm", "gene_count", "ec_count", "ec_cons", "gene_cons"}));
			}
		}else if (request.getParameter("aT").equals("1")) {
			_tbl_source = (ArrayList<?>)conn_pathways.getCompPathwayECList((HashMap<String,String>)key, sort, 0, -1);
			if(cType.equals("taxon")){
				_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "EC Number", "EC Description", "Genome Count", "Unique Gene Count"}));
				_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id","pathway_name","pathway_class", "algorithm", "ec_number", "ec_name", "genome_count", "gene_count"}));
			}else if(cType.equals("genome")){
				_tbl_header.addAll(Arrays.asList(new String[] {"Pathway ID", "Pathway Name", "Pathway Class", "Annotation", "EC Number", "EC Description", "Unique Gene Count"}));
				_tbl_field.addAll(Arrays.asList(new String[] {"pathway_id","pathway_name","pathway_class", "algorithm", "ec_number", "ec_name", "gene_count"}));
			}
		}else if (request.getParameter("aT").equals("2")) {
			_tbl_source = (ArrayList<?>)conn_pathways.getCompPathwayFeatureList((HashMap<String,String>)key, sort, 0, -1);
			_tbl_header.addAll(Arrays.asList(new String[] {"Feature ID", "Genome Name", "Accession", "Locus Tag", "Gene Symbol", "Product Name", "Annotation", "Pathway ID", "Pathway Name", "Ec Number", "EC Description"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"na_feature_id", "genome_name","accession","locus_tag", "gene", "product", "algorithm", "pathway_id", "pathway_name", "ec_number", "ec_name"}));
		}

		_filename = "CompPathwayTable";

	}else if (_tablesource.equalsIgnoreCase("MapFeatureTable")) {
		
		String cId = request.getParameter("genomeId");
		String algorithm = request.getParameter("algorithm");
		String ec_number = request.getParameter("ec_number");
		String map = request.getParameter("map");
		
		if(algorithm.equals("PATRIC") || algorithm.equals("RAST"))
			algorithm = "'RAST'";
		else if(algorithm.equals("Legacy BRC") || algorithm.equals("BRC"))
			algorithm = "'Curation'";
		else if(algorithm.equals("RefSeq"))
			algorithm = "'RefSeq'";
		
		key.put("genomeId", cId);
		key.put("algorithm", algorithm);
		key.put("ec_number", ec_number);
		key.put("map", map);
		key.put("which", "download_from_heatmap_feature");		
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		_tbl_source = (ArrayList<?>)conn_pathways.getCompPathwayFeatureList((HashMap<String,String>)key, sort, 0, -1);
				
		_tbl_header.addAll(Arrays.asList(new String[] {"Genome", "Accession", "Locus Tag", "Annotation", "Feature Type", "Start", "End", "Length(NT)", "Strand", "Pathway ID", "Pathway Name", "EC Number", "EC Name"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag","algorithm", "name","start_max","end_min","na_length","strand", "pathway_id", "pathway_name", "ec_number", "ec_name"}));
				
		_filename = "MapFeatureTable";
		
	}else if(_tablesource.equalsIgnoreCase("MapFeatureTable_Cell")){
		
		_filename = "MapFeatureTable_Cell";
		
		if (_fileformat.equalsIgnoreCase("xls")){
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
		}else if(_fileformat.equalsIgnoreCase("txt")){
			
			response.setContentType("application/octetstream");
			response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
			
		}
		_fileformat = "";

	}else if(_tablesource.equalsIgnoreCase("TranscriptomicsEnrichment")){
		
		_filename = "PathwaySummary";
		
		key.put("feature_info_id",request.getParameter("featureList"));

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		DBTranscriptomics conn_transcriptomics = new DBTranscriptomics();
		_tbl_source = conn_transcriptomics.getPathwayEnrichmentList(key, sort, 0, -1);
		
		_tbl_header.addAll(Arrays.asList(new String[] {"Pathway Name", "# of Genes Selected	", "# of Genes Annotated", "% Coverage"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"pathway_name", "ocnt", "ecnt", "percentage"}));

	}
		
	excel = new ExcelHelper("xssf",_tbl_header, _tbl_field, _tbl_source);
	excel.buildSpreadsheet();
	
	if (_fileformat.equalsIgnoreCase("xlsx")) {
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		
		OutputStream outs = response.getOutputStream();

		excel.writeSpreadsheettoBrowser(outs);

		outs.flush();
		
	}else if(_fileformat.equalsIgnoreCase("txt")){
		
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");

		out.println(excel.writeToTextFile());

	}else{
		String output = request.getParameter("data");
		out.println(output);
	}
%>