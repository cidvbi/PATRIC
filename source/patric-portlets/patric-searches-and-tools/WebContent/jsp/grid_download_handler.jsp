<%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%@ page import="edu.vt.vbi.patric.common.PolyomicHandler" 
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="edu.vt.vbi.patric.common.SQLHelper" 
%><%@ page import="edu.vt.vbi.patric.common.StringHelper" 
%><%@ page import="edu.vt.vbi.patric.dao.*" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="org.json.simple.parser.JSONParser" 
%><%@ page import="org.json.simple.parser.ParseException" 
%><%@ page import="java.util.*" 
%><%@ page import="java.io.OutputStream" 
%><%

	DBSearch conn_search = new DBSearch();

	String _filename = "";
	
	ArrayList<String> _tbl_header = new ArrayList<String>();
	ArrayList<String> _tbl_field = new ArrayList<String>();
	JSONArray _tbl_source = null;

	// getting common params
	String _fileformat = request.getParameter("fileformat");
	String _tablesource = request.getParameter("tablesource");
	ResultType key = new ResultType();
	
	System.out.print("tablesource:"+_tablesource);
	
	String sort_field;
	String sort_dir;
	HashMap<String, String> sort = null;

	if (_tablesource == null || _fileformat == null) {
		System.out.println("Error");
		_fileformat = null;
	}
	
	ExcelHelper excel= null;
	
	if(_tablesource.equalsIgnoreCase("Genome")){
		
		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");
		String genomeId = request.getParameter("gId");
		
		if (keyword!=null) {
			key.put("keyword",keyword.trim());
		}
		
		if (request.getParameter("aT")!=null && request.getParameter("aT").equals("0")) {
			
			sort_field = request.getParameter("gsort");
			sort_dir = request.getParameter("gdir");
			
			if (sort_field!=null && sort_dir!=null) {
				sort = new HashMap<String, String>();
				sort.put("field", sort_field);
				sort.put("direction", sort_dir);
			}
			
			solr.setCurrentInstance("GenomeFinder");
			JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
			
			JSONObject obj = (JSONObject)object.get("response");
			_tbl_source = (JSONArray)obj.get("docs");

			_tbl_header.addAll(Arrays.asList(new String[] {
				"Genome Info Id",
				"Genome Name",
				"NCBI Taxon Id",
				"Genome Status",
				"Organism Name",
				"Strain",
				"Serovar",
				"Biovar",
				"Pathovar",
				"Culture Collection",
				"Type Strain",
				"Project Status",
				"Availability",
				"Sequencing Center",
				"Completion Date",
				"MLST",
				"Publication",
				"NCBI Project Id",
				"RefSeq Project Id",
				"GenBank Accessions",
				"RefSeq Accessions",
				"Sequencing Status",
				"Sequencing Platform",
				"Sequencing Depth",
				"Assembly Method",
				"Chromosomes",
				"Plasmids",
				"Contigs",
				"Sequences",
				"Genome Length",
				"GC Content",
				"RAST CDS",
				"BRC CDS",
				"RefSeq CDS",
				"Isolation Site",
				"Isolation Source",
				"Isolation Comments",
				"Collection Date",
				"Isolation Country",
				"Geographic Location",
				"Latitude",
				"Longitude",
				"Altitude",
				"Depth",
				"Host Name",
				"Host Gender",
				"Host Age",
				"Host Health",
				"Body Sample Site",
				"Body Sample Subsite",
				"Gram Stain",
				"Cell Shape",
				"Motility",
				"Sporulation",
				"Temperature Range",
				"Optimal Temperature",
				"Salinity",
				"Oxygen Requirement",
				"Habitat",
				"Disease",
				"Others"
			}));

			_tbl_field.addAll(Arrays.asList(new String[] {
				"genome_info_id",
				"genome_name",
				"ncbi_tax_id",
				"genome_status",
				"organism_name",
				"strain",
				"serovar",
				"biovar",
				"pathovar",
				"culture_collection",
				"type_strain",
				"project_status",
				"availability",
				"sequencing_centers",
				"completion_date",
				"mlst",
				"publication",
				"ncbi_project_id",
				"refseq_project_id",
				"genbank_accessions",
				"refseq_accessions",
				"sequencing_status",
				"sequencing_platform",
				"sequencing_depth",
				"assembly_method",
				"chromosomes",
				"plasmids",
				"contigs",
				"sequences",
				"genome_length",
				"gc_content",
				"rast_cds",
				"brc_cds",
				"refseq_cds",
				"isolation_site",
				"isolation_source",
				"isolation_comments",
				"collection_date",
				"isolation_country",
				"geographic_location",
				"latitude",
				"longitude",
				"altitude",
				"depth",
				"host_name",
				"host_gender",
				"host_age",
				"host_health",
				"body_sample_site",
				"body_sample_subsite",
				"gram_stain",
				"cell_shape",
				"motility",
				"sporulation",
				"temperature_range",
				"optimal_temperature",
				"salinity",
				"oxygen_requirement",
				"habitat",
				"disease",
				"comments"
			}));

		} else if (request.getParameter("aT")!=null && request.getParameter("aT").equals("1")) {
		
			if(genomeId != null && !genomeId.equals("")){
				key.put("keyword", solr.ConstructSequenceFinderKeyword(genomeId));
			} else {
				solr.setCurrentInstance("GenomeFinder");
				JSONObject solr_output = solr.getGenomeIDsfromSolr(keyword, null, false);
				String solrId = solr.getGenomeIdsfromSolrOutput(solr_output);
				key.put("keyword",solr.ConstructSequenceFinderKeyword(solrId));
			}

			solr.setCurrentInstance("GenomeSequenceFinder");

			JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
			
			JSONObject obj = (JSONObject)object.get("response");
			_tbl_source = (JSONArray)obj.get("docs");
			
			sort_field = request.getParameter("sort");
			sort_dir = request.getParameter("dir");
			
			if (sort_field!=null && sort_dir!=null) {
				sort = new HashMap<String, String>();
				sort.put("field", sort_field);
				sort.put("direction", sort_dir);
			}

			_tbl_header.addAll(Arrays.asList(new String[] {"Genome Name", "Isolate", "Accession", "Length (bp)", "Sequence Type", "Topology", "GC Content", "Description"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"genome_name", "isolate", "accession", "length",  "sequence_type", "topology", "base_composition", "description"}));
		}
		
		_filename = "GenomeFinder";
		
	} else if (_tablesource.equalsIgnoreCase("GO")) {

		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");

		key.put("keyword", keyword);

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");

		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		solr.setCurrentInstance("GOSearch");
		JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
		
		JSONObject obj = (JSONObject)object.get("response");
		_tbl_source = (JSONArray)obj.get("docs");
		
		_tbl_header.addAll(Arrays.asList(new String[] {"Feature ID", "Genome Name", "Accession", "Locus Tag", /*"RefSeq Locus Tag",*/ "Gene Symbol", "Product Name", "Annotation", "GO Term", "GO Description"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"na_feature_id", "genome_name", "accession", "locus_tag", /*"refseq_locus_tag",*/ "gene", "product", "annotation", "go_id", "go_term"}));

		_filename = "GOTermSearch";

	} else if (_tablesource.equalsIgnoreCase("EC")) {
		
		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");

		key.put("keyword", keyword);

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");

		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		solr.setCurrentInstance("ECSearch");
		JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);

		JSONObject obj = (JSONObject)object.get("response");
		_tbl_source = (JSONArray)obj.get("docs");

		_tbl_header.addAll(Arrays.asList(new String[] {"Feature ID", "Genome Name", "Accession", "Locus Tag", /*"RefSeq Locus Tag",*/ "Gene Symbol", "Product Name", "Annotation", "EC Number", "EC Description"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"na_feature_id", "genome_name", "accession", "locus_tag", /*"refseq_locus_tag",*/ "gene", "product", "annotation", "ec_number", "ec_name"}));

		_filename = "ECTermSearch";

	} else if (_tablesource.equalsIgnoreCase("Feature")) {

		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");

		key.put("keyword", keyword);

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");

		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		solr.setCurrentInstance("GenomicFeature");
		JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);

		JSONObject obj = (JSONObject)object.get("response");
		_tbl_source = (JSONArray)obj.get("docs");

		if(request.getParameter("subtablesource") != null && request.getParameter("subtablesource").toString().equals("FigFam")){
			_tbl_header.addAll(Arrays.asList(new String[] {"ID"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"figfam_id"}));
		}

		_tbl_header.addAll(Arrays.asList(new String[] {"Genome", "Accession", "Locus Tag", "RefSeq Locus Tag", "Annotation", "Feature Type", "Start", "End", "Length", "Strand", "Protein ID", "AA Length", "Gene Symbol", "Product", "Bound Moiety", "AntiCodon"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag", "refseq_locus_tag", "annotation", "feature_type", "start_max", "end_min", "na_length", "strand", "protein_id", "aa_length", "gene", "product", "bound_moiety", "anticodon"}));

		_filename = "FeatureTable";

	} else if (_tablesource.equalsIgnoreCase("GlobalSearch")){

		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");
		String cat = request.getParameter("cat");

		key.put("keyword", keyword);

		if(cat.equals("2")){
			solr.setCurrentInstance("GlobalTaxonomy");
			_tbl_header.addAll(Arrays.asList(new String[] {"Taxon ID", "Taxon Name", "# of Genomes"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"taxon_id", "taxon_name", "genomes"}));
		}else if(cat.equals("3")){
			solr.setCurrentInstance("GENEXP_Experiment");
			_tbl_header.addAll(Arrays.asList(new String[] {"Experiment ID", "Title", "Comparisons", "Genes", "PubMed", "Accession", "Organism", "Strain", "Gene Modification", "Experimental Condition", "Time Series", "Release Date", "Author", "PI", "Institution" }));
			_tbl_field.addAll(Arrays.asList(new String[] {"eid", "title", "samples", "genes", "pmid", "accession", "organism", "strain", "mutant", "condition", "timeseries", "release_date", "author", "pi", "institution"}));
		}else if(cat.equals("1")){
			solr.setCurrentInstance("GenomeFinder");
			_tbl_header.addAll(Arrays.asList(new String[] {
				"Genome Info Id",
				"Genome Name",
				"NCBI Taxon Id", 
				"Genome Status", 
				"Organism Name", 
				"Strain", 
				"Serovar",
				"Biovar",
				"Pathovar",
				"Culture Collection",
				"Type Strain",
				"Project Status",
				"Availability",
				"Sequencing Center",
				"Completion Date",
				"MLST",
				"Publication",
				"NCBI Project Id",
				"RefSeq Project Id",
				"GenBank Accessions",
				"RefSeq Accessions",
				"Sequencing Status",
				"Sequencing Platform",
				"Sequencing Depth",
				"Assembly Method",
				"Chromosomes",
				"Plasmids",
				"Contigs",
				"Sequences",
				"Genome Length",
				"GC Content",
				"RAST CDS",
				"BRC CDS",
				"RefSeq CDS",
				"Isolation Site",
				"Isolation Source",
				"Isolation Comments",
				"Collection Date",
				"Isolation Country",
				"Geographic Location",
				"Latitude",
				"Longitude",
				"Altitude",
				"Depth",
				"Host Name",
				"Host Gender",
				"Host Age",
				"Host Health",
				"Body Sample Site",
				"Body Sample Subsite",
				"Gram Stain",
				"Cell Shape",
				"Motility",
				"Sporulation",
				"Temperature Range",
				"Optimal Temperature",
				"Salinity",
				"Oxygen Requirement",
				"Habitat",
				"Disease",
				"Others"
			}));

			_tbl_field.addAll(Arrays.asList(new String[] {
				"genome_info_id",
				"genome_name",
				"ncbi_tax_id",
				"genome_status",
				"organism_name",
				"strain",
				"serovar",
				"biovar",
				"pathovar",
				"culture_collection",
				"type_strain",
				"project_status",
				"availability",
				"sequencing_centers",
				"completion_date",
				"mlst",
				"publication",
				"ncbi_project_id",
				"refseq_project_id",
				"genbank_accessions",
				"refseq_accessions",
				"sequencing_status",
				"sequencing_platform",
				"sequencing_depth",
				"assembly_method",
				"chromosomes",
				"plasmids",
				"contigs",
				"sequences",
				"genome_length",
				"gc_content",
				"rast_cds",
				"brc_cds",
				"refseq_cds",
				"isolation_site",
				"isolation_source",
				"isolation_comments",
				"collection_date",
				"isolation_country",
				"geographic_location",
				"latitude",
				"longitude",
				"altitude",
				"depth",
				"host_name",
				"host_gender",
				"host_age",
				"host_health",
				"body_sample_site",
				"body_sample_subsite",
				"gram_stain",
				"cell_shape",
				"motility",
				"sporulation",
				"temperature_range",
				"optimal_temperature",
				"salinity",
				"oxygen_requirement",
				"habitat",
				"disease",
				"comments"
			}));

		} else if(cat.equals("0")) {
			solr.setCurrentInstance("GenomicFeature");
			_tbl_header.addAll(Arrays.asList(new String[] {"Genome", "Accession", "Locus Tag", "RefSeq Locus Tag", "Annotation", "Feature Type", "Start", "End", "Length", "Strand", "Protein ID", "AA Length", "Gene Symbol", "Product", "Bound Moiety", "AntiCodon"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag", "refseq_locus_tag", "annotation", "feature_type", "start_max", "end_min", "na_length", "strand", "protein_id", "aa_length", "gene", "product", "bound_moiety", "anticodon"}));
		}
		
		JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
		JSONObject obj = (JSONObject)object.get("response");
		_tbl_source = (JSONArray)obj.get("docs");
		_tbl_header.addAll(Arrays.asList(new String[] {}));
		_tbl_field.addAll(Arrays.asList(new String[] {}));
		
		_filename = "GlobalSearch";
			
	} else if (_tablesource.equalsIgnoreCase("IDMapping")) {

		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String keyword = request.getParameter("keyword");

		key.put("from", from);
		key.put("to", to);
		key.put("keyword", keyword);

		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");

		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		String header = "", id = "";
		
		if(to.equals("UniProtKB-ID")){
			header = "UniProtKB-ID";
			id = "uniprotkb_accession";
		}else if(to.equals("RefSeq Locus Tag")){
			header = "RefSeq Locus Tag";
			id = "refseq_source_id";
		}else if(to.equals("RefSeq")){
			header = "RefSeq";
			id = "protein_id";
		}else if(to.equals("Gene ID")){
			header = "GeneID";
			id = "gene_id";
		}else if(to.equals("GI")){
			header = "GI";
			id = "gi_number";
		}else if(to.equals("PATRIC ID")){
			header = "PATRIC ID";
			id = "na_feature_id";
		}else if(to.equals("PATRIC Locus Tag")){
			header = from;
			
			if(from.equals("RefSeq Locus Tag")){
				id = "refseq_source_id";
			}else if(from.equals("RefSeq")){
				id = "protein_id";
			}else if(from.equals("UniProtKB-ID")){
				id = "uniprotkb_accession";
			}else if(from.equals("Gene ID")){
				id = "gene_id";
			}else if(from.equals("GI")){
				id = "gi_number";
			}else if(from.equals("PATRIC ID")){
				id = "na_feature_id";
			}else if(from.equals("PSEED ID")){
				id = "pseed_id";
			}else{
				id = "requested_data";
			}
			
		}else if(to.equals("PSEED ID")){
			header = "PSEED ID";
			id = "pseed_id";
		}else{
			header = to;
			id = "requested_data";
		}
		
		ArrayList<ResultType> _tbl_source_ = conn_search.getIDSearchResult(key.toHashMap(), sort, 0, -1);
		_tbl_source = new JSONArray();
		JSONObject object = null;
		for (ResultType obj: _tbl_source_) {
			object = new JSONObject();
			object.putAll(obj);
			_tbl_source.add(object);
		}
		_tbl_header.addAll(Arrays.asList(new String[] {"Genome", "Accession", "Locus Tag", "PATRIC ID", header, "Annotation", "Feature Type", "Start", "End", "Length(NT)", "Strand", "Length (AA)", "Product Description"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag", "na_feature_id", id, "algorithm","name","start_max","end_min","na_length","strand", "aa_length", "product"}));
		
		_filename = "IDMapping";
	}
	else if (_tablesource.equalsIgnoreCase("Workspace")){
		String idType = request.getParameter("idType");
		String idList = request.getParameter("fids");

		SolrInterface solr = new SolrInterface();
		JSONObject object = null;
		HashMap<String, String> condition = new HashMap<String, String>();
		
		if (idType.equals("Feature")) {
			if (idList.contains(",")) {
				condition.put("na_feature_ids", idList);
			} else {
				condition.put("na_feature_id", idList);
			}
			object = solr.getFeaturesByID(condition);
			_tbl_header.addAll(Arrays.asList(new String[] {"Genome Name", "Accession", "Locus Tag", "Refseq Locus Tag", "Annotation", "Feature Type", "Start", "End", "Length", "Strand", "Protein ID", "AA Length", "Gene Symbol", "Product", "Bound Moiety", "AntiCodon"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"genome_name","accession","locus_tag", "refseq_locus_tag", "annotation", "feature_type", "start_max", "end_min", "na_length", "strand", "refseq_protein_id", "aa_length", "gene", "product", "bound_moiety", "anticodon"}));

			_tbl_source = (JSONArray)object.get("results");
			_filename = "Workspace_Features";
			
		} else if (idType.equals("Genome")) {
			
			if (idList.contains(",")) {
				condition.put("genome_info_ids", idList);
			} else {
				condition.put("genome_info_id", idList);
			}
			object = solr.getGenomesByID(condition);
			
			_tbl_header.addAll(Arrays.asList(new String[] {"Genome Name", "Genome Status", "PATRIC CDS", "Isolation Country", "Host Name", "Disease", "Collection Date", "Completion Date"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"genome_name", "genome_status", "rast_cds", "isolation_country", "host_name", "disease", "collection_date", "completion_date"}));
			
			_tbl_source = (JSONArray)object.get("results");
			_filename = "Workspace_Genomes";
			
		} else if (idType.equals("ExpressionExperiment")) {
			
			ArrayList<String> collectionIds = new ArrayList<String>();
			JSONArray PATRICExperiments = new JSONArray();
			JSONArray results = new JSONArray();
			JSONObject resPATRIC = null;
			JSONObject resUser = null;
			
			if (idList != null && idList != "{}") {
			
				JSONParser parser = new JSONParser();
				JSONObject fids = (JSONObject) parser.parse(idList);
				String token = fids.get("token").toString();
				
				if (fids.containsKey("PATRICExperiments")) {
					PATRICExperiments = (JSONArray) parser.parse(fids.get("PATRICExperiments").toString());
					
					// reading PATRIC Experiments
					HashMap<String, Object> _key = new HashMap<String, Object>();
					_key.put("tracks", PATRICExperiments);			
					resPATRIC = solr.getExperimentsByID(_key);
				}
				
				if (fids.containsKey("USERExperiments")) {
					collectionIds.addAll( (JSONArray)fids.get("USERExperiments") );
					
					// reading USER Experiments from collection
					PolyomicHandler polyomic = new PolyomicHandler();
					polyomic.setAuthenticationToken(token);
					resUser = polyomic.getExperiments(collectionIds);
				}
				
				// merging
				if (resPATRIC.containsKey("results")) {
					for (Object exp: (JSONArray)resPATRIC.get("results")) {
						JSONObject jsonExp = (JSONObject) exp;
						jsonExp.put("source", "PATRIC");
						results.add(jsonExp);
					}
				}
				
				if (resUser.containsKey("results")) {
					for (Object exp: (JSONArray)resUser.get("results")) {
						JSONObject jsonExp = (JSONObject) exp;
						jsonExp.put("source", "me");
						results.add(jsonExp);
					}
				}
			}
			
			_tbl_header.addAll(Arrays.asList(new String[] {"Source", "Experiment Title", "PubMed", "Comparisons", "Organism"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"source", "title", "pmid", "samples", "organism"}));
			
			_tbl_source = results;	
			_filename = "Workspace_Experiments";
			
		} else if (idType.equals("ExpressionSample")) {
			
			JSONArray results = new JSONArray();
			
			if (idList != null && idList != "{}") {
				
				JSONParser parser = new JSONParser();
				JSONObject fids = (JSONObject) parser.parse(idList);
				String token = fids.get("token").toString();
				
				if (fids.get("source").equals("PATRIC")) {

					ResultType _key = new ResultType();
					_key.put("keyword", "eid:"+fids.get("eid"));
					solr.setCurrentInstance("GENEXP_Sample");
					JSONObject samples = solr.getData(_key, null, null, 0, 10000, false, false, false);
					JSONObject obj = (JSONObject) samples.get("response");
					
					results = (JSONArray) obj.get("docs");
					
				} else {
					
					PolyomicHandler polyomic = new PolyomicHandler();
					polyomic.setAuthenticationToken(token);
					
					results = polyomic.getSamples(fids.get("expId").toString(), null);
				}
			}

			_tbl_header.addAll(Arrays.asList(new String[] {"Accession", "Title", "Genes", "Significant genes(Log Ratio)", "Significant genes(Z score)", "Organism", "Strain", "Gene Modification", "Experimental Condition", "Time Point"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"accession", "expname", "genes", "sig_log_ratio", "sig_z_score", "organism", "strain", "mutant", "condition", "timepoint"}));
			
			_tbl_source = results;	
			_filename = "Workspace_Comparisons";
		}
		
	}
	else if (_tablesource.equalsIgnoreCase("GENEXP_Experiment")){
		
		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		if (keyword!=null) {
			key.put("keyword",keyword.trim());
		}
		
		if (request.getParameter("aT").equals("0")) {
			
			solr.setCurrentInstance("GENEXP_Experiment");
			JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
			JSONObject obj = (JSONObject)object.get("response");
			_tbl_source = (JSONArray)obj.get("docs");
			_tbl_header.addAll(Arrays.asList(new String[] {
				"Experiment ID", "Title", "Comparisons", "Genes", "PubMed", "Accession", "Organism", "Strain", "Gene Modification",
				"Experimental Condition", "Time Series", "Release Date", "Author", "PI", "Institution" 
			}));
		
			_tbl_field.addAll(Arrays.asList(new String[] {
				"eid", "title", "samples", "genes", "pmid", "accession", "organism", "strain", "mutant",
				"condition", "timeseries", "release_date", "author", "pi", "institution"
			}));

		} else if (request.getParameter("aT").equals("1")) {

			String solrId = "";
			solr.setCurrentInstance("GENEXP_Experiment");
			
			JSONObject object_t = solr.getData(key, null, null, 0, 10000, false, false, false);
			JSONObject obj_t = (JSONObject)object_t.get("response");
			JSONArray obj1 = (JSONArray)obj_t.get("docs");
			
			for (Object ob: obj1) {
				JSONObject doc = (JSONObject) ob;
				if (solrId.length() == 0) {
					solrId += doc.get("accession").toString();
				} else {
					solrId += ","+doc.get("accession").toString();
				}
			}
			
			key.put("keyword", solr.ConstructKeyword("accession", solrId));
			
			////////////////////////
			
			solr.setCurrentInstance("GENEXP_Sample");
			
			JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
			JSONObject obj = (JSONObject)object.get("response");
			_tbl_source = (JSONArray)obj.get("docs");
			_tbl_header.addAll(Arrays.asList(new String[] {
					"Experiment ID", "Comparison ID", "Title", "Genes", "Significant genes(Log Ratio)", "Significant genes(Z Score)",
					"PubMed", "Accession", "Organism", "Strain", "Gene Modification", "Experiment Condition", "Time Point", "Release Date"
			}));
			
			_tbl_field.addAll(Arrays.asList(new String[] {
					"eid", "pid", "expname", "genes", "sig_log_ratio", "sig_z_score", 
					"pmid", "accession", "organism", "strain", "mutant", "condition", "timepoint", "release_date"
			}));
		}
		
		_filename = "Transcriptomics";
	}
	else if (_tablesource.equalsIgnoreCase("Proteomics_Experiment")){
		
		SolrInterface solr = new SolrInterface();
		String keyword = request.getParameter("download_keyword");
		String experiment_id = request.getParameter("experiment_id");
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		if (keyword!=null) {
			key.put("keyword",keyword.trim());
		}
		
		if (request.getParameter("aT").equals("0")) {
			
			solr.setCurrentInstance("Proteomics_Experiment");
			JSONObject object = solr.getData(key, sort, null, 0, -1, false, false, false);
			JSONObject obj = (JSONObject)object.get("response");
			_tbl_source = (JSONArray)obj.get("docs");
			_tbl_header.addAll(Arrays.asList(new String[] {"Sample Name", "Taxon Name", "Proteins", "Project Name", "Experiment Label", "Experiment Title", "Experiment Type", "Source", "Contact Name", "Institution"}));
		
			_tbl_field.addAll(Arrays.asList(new String[] {"sample_name", "taxon_name", "proteins", "project_name", "experiment_label", "experiment_title", "experiment_type", "source", "contact_name","institution"}));

		} else if (request.getParameter("aT").equals("1")) {

			String solrId = "";
			solr.setCurrentInstance("Proteomics_Protein");
			
			if(experiment_id != null && !experiment_id.equals("")){
				keyword += " AND experiment_id:("+experiment_id+")";
			}
			
			key.put("keyword",keyword.trim());
			
			JSONObject object_t = solr.getData(key, null, null, 0, -1, false, false, false);
			JSONObject obj_t = (JSONObject)object_t.get("response");
			_tbl_source = (JSONArray)obj_t.get("docs");
			
			_tbl_header.addAll(Arrays.asList(new String[] {"Experiment Title", "Experiment Label", "Source", "Genome Name", "Accession", "Locus Tag", "RefSeq Locus Tag", "Gene Symbol", "Description"}));
			_tbl_field.addAll(Arrays.asList(new String[] {"experiment_title", "experiment_label", "source", "genome_name", "accession", "locus_tag",  "refseq_locus_tag", "refseq_gene", "product"}));
		}
		
		_filename = "Proteomics";
	}else if (_tablesource.equalsIgnoreCase("GeneExpression")) {
		
		DBTranscriptomics conn_transcriptomics = new DBTranscriptomics();
		
		String idList = request.getParameter("fids");
		JSONParser parser = new JSONParser();
		JSONObject fids = (JSONObject) parser.parse(idList);
		
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("na_feature_id", fids.get("na_feature_id").toString());
		param.put("pid", fids.get("pid").toString());
		
		ArrayList<ResultType> _tbl_source_ = conn_transcriptomics.getGeneLvlExpression(param);

		_tbl_source = new JSONArray();
		JSONObject object = null;
		for (ResultType obj: _tbl_source_) {
			object = new JSONObject();
			object.putAll(obj);
			_tbl_source.add(object);
		}
		_tbl_header.addAll(Arrays.asList(new String[] {"Platform", "Samples", "Locus Tag", "Title", "PubMed", "Accession", "Strain", "Gene Modification", "Experimental Condition", "Time Point", "Avg Intensity", "Log Ratio", "Z-score"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"exp_platform", "exp_samples", "exp_locustag", "exp_name", "pmid", "exp_accession", "exp_strain", "exp_mutant", "exp_condition", "exp_timepoint", "exp_pavg", "exp_pratio", "exp_zscore"}));
		
		_filename = "GeneExpression";
	}else if(_tablesource.equalsIgnoreCase("Correlation")){
		
		DBTranscriptomics conn_transcriptomics = new DBTranscriptomics();
		HashMap<String, String> k = new HashMap<String, String>();
		
		String cutoffValue = request.getParameter("cutoffValue");
		String cutoffDir = request.getParameter("cutoffDir");
		String cId = request.getParameter("cId");
		
		k.put("na_feature_id", cId);
		k.put("cutoff_value", cutoffValue);
		k.put("cutoff_dir", cutoffDir);
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		ArrayList<ResultType> _tbl_source_ = conn_transcriptomics.getCorrelatedGenes(k, sort, 0, -1);
		
		_tbl_source = new JSONArray();
		JSONObject object = null;
		for (ResultType obj: _tbl_source_) {
			object = new JSONObject();
			object.putAll(obj);
			_tbl_source.add(object);
		}
		
		_tbl_header.addAll(Arrays.asList(new String[] {"Genome Name", "Accession", "Locus Tag", "RefSeq Locus Tag", "Gene Symbol", "Annotation", "Feature Type", "Start", "End", "Length(NT)", "Strand", "Protein ID", "Length(AA)", "Product Description", "Correlations", "Comparisons"}));
		_tbl_field.addAll(Arrays.asList(new String[] {"genome_name", "accession", "locus_tag", "refseq_locus_tag", "gene", "annotation", "feature_type", "start_max", "end_min", "na_length", "strand", "protein_id", "aa_length", "product", "correlation", "count"}));
		
		_filename = "Correlated Genes";
	}else if(_tablesource.equalsIgnoreCase("SingleExperiment")){
		
		HashMap<String, String> k = new HashMap<String, String>();
		
		String cId = request.getParameter("cId");
		String cType = request.getParameter("cType");
		String eid = request.getParameter("eid");
		
		k.put("cId", cId);
		k.put("cType", cType);
		k.put("eid", eid);
		
		sort_field = request.getParameter("sort");
		sort_dir = request.getParameter("dir");
		
		if (sort_field!=null && sort_dir!=null) {
			sort = new HashMap<String, String>();
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}
		
		SolrInterface solr = new SolrInterface();
		_tbl_source = solr.getTranscriptomicsSamples(null, eid, "");
		
		_tbl_header.addAll(Arrays.asList(new String[] {
				"Experiment ID", "Comparison ID", "Title", "Genes", "Significant genes(Log Ratio)", "Significant genes(Z Score)",
				"PubMed", "Accession", "Organism", "Strain", "Gene Modification", "Experiment Condition", "Time Point", "Release Date"
		}));
		
		_tbl_field.addAll(Arrays.asList(new String[] {
				"eid", "pid", "expname", "genes", "sig_log_ratio", "sig_z_score", 
				"pmid", "accession", "organism", "strain", "mutant", "condition", "timepoint", "release_date"
		}));
		
		_filename = "SingleExperiment";
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
	}
%>
