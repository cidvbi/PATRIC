<%@ page import="java.util.*" 
%><%@ page import="org.json.simple.*" 
%><%@ page import="edu.vt.vbi.patric.common.*" 
%><%@ page import="edu.vt.vbi.patric.dao.*" 
%><%@ page import="java.io.OutputStream" 
%><%
	String _filename = "";
	ArrayList<String> _tbl_header = new ArrayList<String>();
	ArrayList<String> _tbl_field = new ArrayList<String>();
	JSONArray _tbl_source = null;
	
	// getting common params
	String _fileformat = request.getParameter("fileformat");
	String _tablesource = request.getParameter("tablesource");
	
	String cType = "";
	String cId = "";
	String keyword = null;
	
	ExcelHelper excel= null;
	
	if (_tablesource == null || _fileformat == null) {
		System.out.println("Error");
		_fileformat = null;
	}
	
	if (_tablesource.equalsIgnoreCase("GEO")) {
		String filter = "";
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		if (request.getParameter("filter")!=null) {
			filter = request.getParameter("filter");
		}
		if (request.getParameter("keyword")!=null) {
			keyword = request.getParameter("keyword");
		}
		
		_filename = "GEO_Data";
		
		String strQueryTerm = "txid"+cId+"[Organism:exp]";
		if (filter!=null && !filter.equals("")) {
			strQueryTerm = strQueryTerm + "+AND+" + filter+"[ETYP]";
		} else {
			strQueryTerm = strQueryTerm + "+NOT+gsm[ETYP]";
		}
		if (keyword!=null && !keyword.equals("")) {
			strQueryTerm = keyword;
		}
		
		EutilInterface eutil_api = new EutilInterface();
		
		JSONObject jsonResult = eutil_api.getResults("gds", strQueryTerm, "", "", 0, -1);
		
		_tbl_source = (JSONArray)jsonResult.get("results");
		_tbl_header.addAll(Arrays.asList(new String[] {"Data Type", "ID", "Title", "Organism", "Experiment Type", "Samples", "Publication", "Date", "Summary", "Platform", "Subset Info", "Download(SOFT)", "Download(MINiML)", "Download(SeriesMatrix)", "Download(Supplementary)"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"dataType", "ID", "title", "taxon", "expType", "n_samples", "pubmed_id", "PDAT", "summary", "platform", "subsetInfo", "link_soft_format", "link_miniml_format", "link_seriesmatrix", "link_supplementary"}));
		
		_filename = "PATRIC_GEO";
		
	}
	else if (_tablesource.equalsIgnoreCase("ArrayExpress")) {
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		if (request.getParameter("keyword")!=null) {
			keyword = request.getParameter("keyword");
		}
		
		_filename = "ArrayExpress_Data";
		
		DBShared conn_shared = new DBShared();
		String species_name = "";
		
		if (cType.equals("taxon")) {
			ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
			species_name = parents.get(0).get("name");
		} 
		else if (cType.equals("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			species_name = names.get("genome_name");	
		}

		ArrayExpressInterface api = new ArrayExpressInterface();
		
		JSONObject jsonResult = api.getResults(keyword, species_name);
		
		_tbl_source = (JSONArray)jsonResult.get("results");		
		_tbl_header.addAll(Arrays.asList(new String[] {"ID","Title", "Organism", "Type", "Assays", "Samples", "Publication", "Data Download", "Date", "Descriptoin", "Experiment Design"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"accession", "name", "species", "experimenttype", "assays", "samples", "pubmed_id", "link_data", "releasedate", "description", "experimentdesign"}));
				
		_filename = "PATRIC_ArrayExpress";
	}
	else if (_tablesource.equalsIgnoreCase("PRC")) {
		String filter = "", sort = "", dir = "";
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		if (request.getParameter("filter")!=null) {
			filter = request.getParameter("filter");
		}
		if (request.getParameter("sort")!=null) {
			sort = request.getParameter("sort");
		}
		if (request.getParameter("dir")!=null) {
			dir = request.getParameter("dir");
		}
		
		_filename = "ProteomicsResourceCenter_Data";
		
		DBShared conn_shared = new DBShared();
		String taxonid = "";
		
		if (cType.equals("taxon")) {
			taxonid = cId;
		} 
		else if (cType.equals("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			taxonid = names.get("ncbi_tax_id");	
		}
		
		DBPRC conn_prc = new DBPRC();
		ArrayList<ResultType> _tbl_source_ = conn_prc.getPRCData(taxonid, filter, 0, -1, sort, dir);

		_tbl_source = new JSONArray();
		JSONObject object = null;
		for (ResultType obj: _tbl_source_) {
			object = new JSONObject();
			object.putAll(obj);
			_tbl_source.add(object);
		}
		
		_tbl_header.addAll(Arrays.asList(new String[] {"ID", "Description", "Organism", "Type", "Samples", "Publication", "Summary"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"expid", "description", "speciesname", "experimenttype", "samples", "pubmed_id", "summary"}));
				
		_filename = "PATRIC_PRC";
	}
	else if (_tablesource.equalsIgnoreCase("Peptidome")) {
		String filter = "";
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		if (request.getParameter("filter")!=null) {
			filter = request.getParameter("filter");
		}
		String tId = "";
		
		_filename = "Peptidome_Data";
		
		if (cType.equals("taxon")) {
			tId = cId;
		} 
		else if (cType.equals("genome")) {
			DBShared conn_shared = new DBShared();
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			tId = names.get("ncbi_taxon_id");	
		} 
		
		String strQueryTerm = "txid"+tId+"[Organism:exp]";
		if (filter!=null) {
			strQueryTerm = strQueryTerm + "+AND+" + filter+"[ETYP]";
		}
		
		EutilInterface eutil_api = new EutilInterface();
		
		JSONObject jsonResult = eutil_api.getResults("pepdome", strQueryTerm, "", "", 0, -1);
		
		_tbl_source = (JSONArray)jsonResult.get("results");
		_tbl_header.addAll(Arrays.asList(new String[] {"Accession", "Title", "Organism", "Samples", "Proteins", "Peptides", "Spectra", "Publication", "Summary", "Download"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"Accession", "title", "TaxName", "SampleCount", "ProteinCount", "PeptideCount", "SpectraCount", "pubmed_id", "summary", "link_data_file"}));
				
		_filename = "PATRIC_Peptidome";
	}
	else if (_tablesource.equalsIgnoreCase("PRIDE")) {
		
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		
		_filename = "PRIDE_Data";
		
		DBSummary conn_summary = new DBSummary();
		String species_name = "";
		
		if (cType.equals("taxon")) {
			species_name = conn_summary.getPRIDESpecies(cId);
		} else {
			species_name = "";
		}

		PRIDEInterface api = new PRIDEInterface();
		
		JSONObject jsonResult = api.getResults(species_name);
		
		_tbl_source = (JSONArray)jsonResult.get("results");
		_tbl_header.addAll(Arrays.asList(new String[] {"Accession", "Title", "Short Label", "Organism", "Publication", "Download"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"experiment_ac", "experiment_title", "experiment_short_title", "newt_name", "pubmed_id", "link_data_file"}));
		
		_filename = "PATRIC_PRIDE";
	}
	else if (_tablesource.equalsIgnoreCase("Structure")) {
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		String tId = "";
		
		_filename = "Structure_Data";
		
		if (cType.equals("taxon")) {
			tId = cId;
		} 
		else if (cType.equals("genome")) {
			//need to query ncbi_tax_id from DB
			DBShared conn_shared = new DBShared();
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			tId = names.get("ncbi_taxon_id");	
		}
		
		String strQueryTerm = "txid"+tId+"[Organism:exp]";
		EutilInterface eutil_api = new EutilInterface();
		JSONObject jsonResult = eutil_api.getResults("structure", strQueryTerm, "", "", 0, -1);
		
		_tbl_source = (JSONArray)jsonResult.get("results");
		_tbl_header.addAll(Arrays.asList(new String[] {"Accession", "Title", "EC No", "Class", "Experiment Method", "Resolution", "Date", "Organism", "LigCode"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"PdbAcc", "PdbDescr", "EC", "PdbClass", "ExpMethod", "Resolution", "PdbDepositDate", "Organism", "LigCode"}));
			
		_filename = "PATRIC_Structure";
		
	}
	else if (_tablesource.equalsIgnoreCase("IntAct")) {
		if (request.getParameter("context_type")!=null) {
			cType = request.getParameter("context_type");
		}
		if (request.getParameter("context_id")!=null && !request.getParameter("context_id").equals("")) {
			cId = request.getParameter("context_id");
		}
		
		_filename = "IntAct_Data";
		
		DBShared conn_shared = new DBShared();
		String species_name = "";
		
		if (cType.equals("taxon")) {
			species_name = "species:"+cId;
		}
		else if (cType.equals("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			species_name = "species:"+names.get("ncbi_tax_id");
		}

		PSICQUICInterface api = new PSICQUICInterface();
		
		String count = api.getCounts("intact", species_name);
		JSONObject jsonResult = api.getResults("intact", species_name, 0, Integer.parseInt(count));
		
		_tbl_source = (JSONArray)jsonResult.get("results");
		_tbl_header.addAll(Arrays.asList(new String[] {"Interaction Accession", "Label", "Interaction Type", "Count Participants", "Count Experiments", "Exp Name", "Method", "Organism", "Publication", "Participatns", "Experiments"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"interaction_ac", "label", "interaction_type", "count_participants", "count_exp_ref", "exp_name", "exp_method", "exp_org", "exp_pubmed", "participants", "experiments"}));

		_filename = "PATRIC_IntAct";
	}

	if (_fileformat == null) {
	}
	else if (_fileformat.equalsIgnoreCase("xlsx")) {
		excel = new ExcelHelper("xssf", _tbl_header, _tbl_field, _tbl_source);
		excel.buildSpreadsheet();
		
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		
		OutputStream outs = response.getOutputStream();
		excel.writeSpreadsheettoBrowser(outs);
		outs.flush();
	} 
	else if (_fileformat.equalsIgnoreCase("txt")) {
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"."+_fileformat+"\"");
		StringBuilder output = new StringBuilder();
		int i=0;
		for (i=0; i<_tbl_header.size()-1;i++) {
			output.append(_tbl_header.get(i).toString()+"\t");
		}
		output.append(_tbl_header.get(i).toString()+"\r\n");
		Iterator<JSONObject> itr = _tbl_source.iterator();
		JSONObject row = null;
		String _f = "";
		while (itr.hasNext()) {
			row = itr.next();
			for (i=0;i<_tbl_field.size()-1;i++) {
				_f = _tbl_field.get(i).toString();
				if (row.get(_f)!=null) {
					output.append(StringHelper.strip_html_tag(row.get(_f).toString())+"\t");
				}
				else {
					output.append("\t");
				}
			}
			_f = _tbl_field.get(i).toString();
			output.append(row.get(_f)+"\r\n");
		}
		out.println(output.toString());
	}
%>