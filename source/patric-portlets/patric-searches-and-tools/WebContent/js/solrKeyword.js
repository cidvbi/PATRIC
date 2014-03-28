Ext.define('Feature', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'genome_info_id',
		type : 'int'
	}, {
		name : 'gid',
		type : 'int'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'genome_name_sort',
		type : 'string'
	}, {
		name : 'accession',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'locus_tag_sort',
		type : 'string'
	}, {
		name : 'na_feature_id',
		type : 'int'
	}, {
		name : 'annotation',
		type : 'string'
	}, {
		name : 'feature_type',
		type : 'string'
	}, {
		name : 'start_max',
		type : 'int'
	}, {
		name : 'end_min',
		type : 'int'
	}, {
		name : 'na_length',
		type : 'int'
	}, {
		name : 'strand',
		type : 'string'
	}, {
		name : 'refseq_protein_id',
		type : 'string'
	}, {
		name : 'aa_length',
		type : 'int'
	}, {
		name : 'gene',
		type : 'string'
	}, {
		name : 'bound_moiety',
		type : 'string'
	}, {
		name : 'anticodon',
		type : 'string'
	}, {
		name : 'product',
		type : 'string'
	}, {
		name : 'product_sort',
		type : 'string'
	}, {
		name : 'refseq_locus_tag',
		type : 'string'
	}, {
		name : 'refseq_locus_tag_sort',
		type : 'string'
	}, {
		name : 'pseed_id',
		type : 'string'
	}, {
		name : 'highlight'
	}, {
		name : 'figfam_id',
		type : 'string'
	}]
});

Ext.define('Genome', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'genome_info_id',
		type : 'string'
	}, {
		name : 'gid',
		type : 'string'
	}, {
		name : 'genome_status',
		type : 'string'
	}, {
		name : 'isolation_country',
		type : 'string'
	}, {
		name : 'host_name',
		type : 'string'
	}, {
		name : 'oxygen_requirement',
		type : 'string'
	}, {
		name : 'sporulation',
		type : 'string'
	}, {
		name : 'temperature_range',
		type : 'string'
	}, {
		name : 'disease',
		type : 'string'
	}, {
		name : 'habitat',
		type : 'string'
	}, {
		name : 'motility',
		type : 'string'
	}, {
		name : 'sequences',
		type : 'int'
	}, {
		name : 'collection_date',
		type : 'string'
	}, {
		name : 'mlst',
		type : 'string'
	}, {
		name : 'genome_length',
		type : 'int'
	}, {
		name : 'complete',
		type : 'string'
	}, {
		name : 'rast_cds',
		type : 'int'
	}, {
		name : 'brc_cds',
		type : 'int'
	}, {
		name : 'refseq_cds',
		type : 'int'
	}, {
		name : 'chromosomes',
		type : 'int'
	}, {
		name : 'plasmids',
		type : 'int'
	}, {
		name : 'contigs',
		type : 'int'
	}, {
		name : 'ncbi_tax_id',
		type : 'int'
	}, {
		name : 'organism_name',
		type : 'string'
	}, {
		name : 'strain',
		type : 'string'
	}, {
		name : 'serovar',
		type : 'string'
	}, {
		name : 'biovar',
		type : 'string'
	}, {
		name : 'pathovar',
		type : 'string'
	}, {
		name : 'culture_collection',
		type : 'string'
	}, {
		name : 'type_strain',
		type : 'string'
	}, {
		name : 'project_status',
		type : 'string'
	}, {
		name : 'availability',
		type : 'string'
	}, {
		name : 'sequencing_centers',
		type : 'string'
	}, {
		name : 'completion_date',
		type : 'string'
	}, {
		name : 'publication',
		type : 'string'
	}, {
		name : 'completion_date',
		type : 'string'
	}, {
		name : 'ncbi_project_id',
		type : 'int'
	}, {
		name : 'refseq_project_id',
		type : 'int'
	}, {
		name : 'genbank_accessions',
		type : 'string'
	}, {
		name : 'refseq_accessions',
		type : 'string'
	}, {
		name : 'sequencing_status',
		type : 'string'
	}, {
		name : 'sequencing_platform',
		type : 'string'
	}, {
		name : 'sequencing_depth',
		type : 'string'
	}, {
		name : 'assembly_method',
		type : 'string'
	}, {
		name : 'gc_content',
		type : 'string'
	}, {
		name : 'isolation_site',
		type : 'string'
	}, {
		name : 'isolation_source',
		type : 'string'
	}, {
		name : 'isolation_comments',
		type : 'string'
	}, {
		name : 'geographic_location',
		type : 'string'
	}, {
		name : 'latitude',
		type : 'string'
	}, {
		name : 'longitude',
		type : 'string'
	}, {
		name : 'altitude',
		type : 'string'
	}, {
		name : 'depth',
		type : 'string'
	}, {
		name : 'host_gender',
		type : 'string'
	}, {
		name : 'host_age',
		type : 'string'
	}, {
		name : 'host_health',
		type : 'string'
	}, {
		name : 'body_sample_site',
		type : 'string'
	}, {
		name : 'body_sample_subsite',
		type : 'string'
	}, {
		name : 'gram_stain',
		type : 'string'
	}, {
		name : 'cell_shape',
		type : 'string'
	}, {
		name : 'temperature_range',
		type : 'string'
	}, {
		name : 'optimal_temperature',
		type : 'string'
	}, {
		name : 'salinity',
		type : 'string'
	}, {
		name : 'disease',
		type : 'string'
	}, {
		name : 'comments',
		type : 'string'
	}, {
		name : 'highlight'
	}]
});

Ext.define('Sequence', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'genome_info_id',
		type : 'string'
	}, {
		name : 'gid',
		type : 'string'
	}, {
		name : 'sid',
		type : 'string'
	}, {
		name : 'accession',
		type : 'string'
	}, {
		name : 'length',
		type : 'string'
	}, {
		name : 'sequence_type',
		type : 'string'
	}, {
		name : 'gc_content',
		type : 'float'
	}, {
		name : 'description',
		type : 'string'
	}, {
		name : 'highlight'
	}]
});

Ext.define('EC', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'gid',
		type : 'string'
	}, {
		name : 'genome_info_id',
		type : 'string'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'accession',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'na_feature_id',
		type : 'string'
	}, {
		name : 'annotation',
		type : 'string'
	}, {
		name : 'gene',
		type : 'string'
	}, {
		name : 'product',
		type : 'string'
	}, {
		name : 'ec_number',
		type : 'string'
	}, {
		name : 'ec_name',
		type : 'string'
	}, {
		name : 'start_max',
		type : 'int'
	}, {
		name : 'end_min',
		type : 'int'
	}, {
		name : 'highlight'
	}]
});

Ext.define('GO', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'gid',
		type : 'int'
	}, {
		name : 'genome_info_id',
		type : 'srring'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'accession',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'na_feature_id',
		type : 'int'
	}, {
		name : 'annotation',
		type : 'string'
	}, {
		name : 'gene',
		type : 'string'
	}, {
		name : 'product',
		type : 'string'
	}, {
		name : 'go_id',
		type : 'string'
	}, {
		name : 'go_term',
		type : 'string'
	}, {
		name : 'start_max',
		type : 'int'
	}, {
		name : 'end_min',
		type : 'int'
	}, {
		name : 'highlight'
	}]
});

Ext.define('Pathway', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'accession',
		type : 'string'
	}, {
		name : 'annotation',
		type : 'string'
	}, {
		name : 'ec_name',
		type : 'string'
	}, {
		name : 'ec_number',
		type : 'string'
	}, {
		name : 'feature_type',
		type : 'string'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'gid',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'na_feature_id',
		type : 'string'
	}, {
		name : 'ncbi_tax_id',
		type : 'string'
	}, {
		name : 'pathway_id',
		type : 'string'
	}, {
		name : 'pathway_name',
		type : 'string'
	}, {
		name : 'product',
		type : 'string'
	}, {
		name : 'start_max',
		type : 'int'
	}, {
		name : 'end_min',
		type : 'int'
	}, {
		name : 'highlight'
	}]
});

Ext.define('Taxonomy', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'genomes',
		type : 'string'
	}, {
		name : 'taxon_id',
		type : 'string'
	}, {
		name : 'taxon_name',
		type : 'string'
	}, {
		name : 'taxon_rank',
		type : 'string'
	}, {
		name : 'highlight'
	}]
});

Ext.define('Figfam', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'figfam_id',
		type : 'string'
	}, {
		name : 'figfam_product',
		type : 'string'
	}, {
		name : 'gid',
		type : 'string'
	}, {
		name : 'genome_info_id',
		type : 'srring'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'accession',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'na_feature_id',
		type : 'int'
	}, {
		name : 'start_max',
		type : 'int'
	}, {
		name : 'end_min',
		type : 'int'
	}, {
		name : 'refseq_locus_tag',
		type : 'string'
	}, {
		name : 'highlight'
	}]
});

Ext.define('Cart', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'na_feature_id',
		type : 'string'
	}, {
		name : 'genome_info_id',
		type : 'string'
	}, {
		name : 'gid',
		type : 'string'
	}]
});

Ext.define('Experiment', {
	extend : 'Ext.data.Model',
	idProperty : 'expid',
	fields : [{
		name : 'accession',
		type : 'string'
	}, {
		name : 'author',
		type : 'string'
	}, {
		name : 'condition',
		type : 'string'
	}, {
		name : 'description',
		type : 'string'
	}, {
		name : 'eid',
		type : 'int'
	}, {
		name : 'expid',
		type : 'int'
	}, {
		name : 'genes',
		type : 'string'
	}, {
		name : 'institution',
		type : 'string'
	}, {
		name : 'mutant',
		type : 'string'
	}, {
		name : 'organism',
		type : 'string'
	}, {
		name : 'pi',
		type : 'string'
	}, {
		name : 'platforms',
		type : 'int'
	}, {
		name : 'pmid',
		type : 'string'
	}, {
		name : 'release_date',
		type : 'string'
	}, {
		name : 'rownum',
		type : 'int'
	}, {
		name : 'samples',
		type : 'int'
	}, {
		name : 'strain',
		type : 'string'
	}, {
		name : 'timeseries',
		type : 'string'
	}, {
		name : 'title',
		type : 'string'
	}]
});

Ext.define('Sample', {
	extend : 'Ext.data.Model',
	idProperty : 'pid',
	fields : [{
		name : 'accession',
		type : 'string'
	}, {
		name : 'channels',
		type : 'int'
	}, {
		name : 'condition',
		type : 'string'
	}, {
		name : 'eid',
		type : 'int'
	}, {
		name : 'expid',
		type : 'int'
	}, {
		name : 'expmean',
		type : 'float'
	}, {
		name : 'expname',
		type : 'string'
	}, {
		name : 'expstddev',
		type : 'float'
	}, {
		name : 'genes',
		type : 'int'
	}, {
		name : 'mutant',
		type : 'string'
	}, {
		name : 'organism',
		type : 'string'
	}, {
		name : 'pi',
		type : 'string'
	}, {
		name : 'pid',
		type : 'int'
	}, {
		name : 'platform',
		type : 'string'
	}, {
		name : 'pmid',
		type : 'string'
	}, {
		name : 'release_date',
		type : 'string'
	}, {
		name : 'samples',
		type : 'string'
	}, {
		name : 'sig_log_ratio',
		type : 'float'
	}, {
		name : 'sig_z_score',
		type : 'float'
	}, {
		name : 'source',
		type : 'string'
	}, {
		name : 'strain',
		type : 'string'
	}, {
		name : 'timepoint',
		type : 'string'
	}]
});

Ext.define('Proteomics_Experiment', {
	extend : 'Ext.data.Model',
	idProperty : 'pid',
	fields : [{
		name : 'sample_name',
		type : 'string'
	}, {
		name : 'taxon_name',
		type : 'string'
	}, {
		name : 'contact_details',
		type : 'string'
	}, {
		name : 'accession',
		type : 'int'
	}, {
		name : 'contact_name',
		type : 'string'
	}, {
		name : 'proteins',
		type : 'int'
	}, {
		name : 'taxon_id',
		type : 'int'
	}, {
		name : 'project_name',
		type : 'string'
	}, {
		name : 'experiment_label',
		type : 'string'
	}, {
		name : 'experiment_title',
		type : 'string'
	}, {
		name : 'institution',
		type : 'string'
	}, {
		name : 'source',
		type : 'string'
	}, {
		name : 'experiment_type',
		type : 'string'
	}, {
		name : 'experiment_id',
		type : 'int'
	}]
});

Ext.define('Proteomics_Protein', {
	extend : 'Ext.data.Model',
	idProperty : 'pid',
	fields : [{
		name : 'accession',
		type : 'string'
	}, {
		name : 'taxon_id',
		type : 'int'
	}, {
		name : 'na_feature_id',
		type : 'int'
	}, {
		name : 'experiment_label',
		type : 'string'
	}, {
		name : 'experiment_title',
		type : 'string'
	}, {
		name : 'source',
		type : 'string'
	}, {
		name : 'experiment_id',
		type : 'int'
	}, {
		name : 'taxon_name',
		type : 'string'
	}, {
		name : 'genome_name',
		type : 'string'
	}, {
		name : 'refseq_locus_tag',
		type : 'string'
	}, {
		name : 'locus_tag',
		type : 'string'
	}, {
		name : 'product',
		type : 'string'
	}, {
		name : 'refseq_gene',
		type : 'string'
	}, {
		name : 'pubmed_id',
		type : 'string'
	}]
});

var configuration = {};
configuration['Genome'] = {
	display_facets : ['genome_status_f', 'isolation_country_f', 'host_name_f', 'disease_f', 'collection_date_f', 'completion_date'],
	display_facets_texts : ['Genome Status', 'Isolation Country', 'Host Name', 'Disease', 'Collection Date', 'Completion Date'],
	all_facets : ['Keyword', 'genome_status_f', 'isolation_country_f', 'host_name_f', 'disease_f', 'collection_date_f', 'completion_date', 'genome_status', 'isolation_country', 'host_name', 'disease', 'collection_date', 'gid', 'annotation'],
	search_fields : [{
		value : 'Keyword',
		text : 'Keyword'
	}, {
		value : 'genome_status',
		text : 'Genome Status'
	}, {
		value : 'isolation_country',
		text : 'Isolation Country'
	}, {
		value : 'host_name',
		text : 'Host Name'
	}, {
		value : 'disease',
		text : 'Disease'
	}, {
		value : 'collection_date',
		text : 'Collection Date'
	}, {
		value : 'completion_date',
		text : 'Completion Date'
	}],
	sort_fields : ["isolation_country", "host_name", "disease"],
	url : "/portal/portal/patric/GenomeFinder/GenomeFinderWindow?action=b&cacheability=PAGE"
};

configuration['Sequence'] = {
	sort_fields : ["genome_name", "accession", "description"]
};

configuration['Feature'] = {
	display_facets : ['annotation_f', 'feature_type_f'],
	display_facets_texts : ['Annotation', 'Feature Type'],
	all_facets : ['Keyword', 'feature_type', 'annotation', 'feature_type_f', 'annotation_f', 'gid', 'go', 'ec', 'pathway', 'figfam_id'],
	search_fields : [{
		value : 'Keyword',
		text : 'Keyword'
	}, {
		value : 'feature_type',
		text : 'Feature Type'
	}, {
		value : 'annotation',
		text : 'Annotation'
	}, {
		value : 'sequence_status',
		text : 'Sequence Status'
	}],
	sort_fields : ["genome_name", "locus_tag", "refseq_locus_tag", "product"],
	url : "/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE"
};

configuration['GO'] = {
	display_facets : ['annotation_f', 'go_id_f', 'go_term_f'],
	display_facets_texts : ['Annotation', 'GO Term ID', 'GO Term Description'],
	all_facets : ['Keyword', 'go_id_f', 'go_id', 'annotation_f', 'annotation', 'go_term', 'go_term_f', 'gid'],
	search_fields : [{
		value : 'Keyword',
		text : 'Keyword'
	}, {
		value : 'go_id',
		text : 'GO Term ID'
	}, {
		value : 'go_term',
		text : 'GO Term Description'
	}, {
		value : 'annotation',
		text : 'Annotation'
	}],
	sort_fields : ["genome_name", "locus_tag", "go_term", "go_id", "product"],
	url : "/portal/portal/patric/GOSearch/GOSearchWindow?action=b&cacheability=PAGE"
};

configuration['EC'] = {
	display_facets : ['annotation_f', 'ec_number_f'],
	display_facets_texts : ['Annotation', 'EC Number'],
	all_facets : ['Keyword', 'ec_number_f', 'ec_number', 'ec_name', 'annotation_f', 'annotation', 'gid'],
	search_fields : [{
		value : 'Keyword',
		text : 'Keyword'
	}, {
		value : 'ec_number',
		text : 'EC Number'
	}, {
		value : 'ec_name',
		text : 'EC Name'
	}, {
		value : 'annotation',
		text : 'Annotation'
	}],
	sort_fields : ["genome_name", "locus_tag", "ec_name", "ec_number", "product"],
	url : "/portal/portal/patric/ECSearch/ECSearchWindow?action=b&cacheability=PAGE"
};

configuration['GlobalTaxonomy'] = {
	display_facets : ['taxon_rank_f', 'genomes_f'],
	display_facets_texts : ['Taxon Rank', 'Genomes'],
	all_facets : ['Keyword', 'taxon_rank_f', 'genomes_f'],
	search_fields : ['taxon_name'],
	url : "/portal/portal/patric/GlobalTaxonomy/GlobalTaxonomyWindow?action=b&cacheability=PAGE"
};

configuration['GENEXP_Experiment'] = {
	display_facets : ['organism', 'strain', 'mutant', 'condition', 'timeseries', 'release_date'],
	display_facets_texts : ['Organism', 'Strain', 'Gene Modification', 'Experimental Condition', 'Time Series', 'Release Date'],
	all_facets : ['Keyword', 'organism', 'strain', 'mutant', 'condition', 'timeseries', 'release_date', 'eid'],
	search_fields : [{
		value : 'Keyword',
		text : 'Keyword'
	}, 'organism', 'strain', 'mutant', 'condition', 'timeseries', 'release_date'],
	url : "/portal/portal/patric/ExperimentList/ExperimentListWindow?action=b&cacheability=PAGE"
};

configuration['Proteomics_Experiment'] = {
		display_facets : ['experiment_type'],
		display_facets_texts : ['Experiment Type'],
		all_facets : ['experiment_type', 'taxon_id', 'experiment_id'],
		url : "/portal/portal/patric/ProteomicsList/ProteomicsListWindow?action=b&cacheability=PAGE"
	};

function constructKeyword(object, name) {

	var all_facets = configuration[name].all_facets, keyword = "", entries;

	keyword += (object["Keyword"] == null) ? "(*)" : keyword += object["Keyword"].split("&facet=true")[0];

	for (var i = 1; i < all_facets.length; i++) {

		var facet = all_facets[i];

		if (object[facet] != null && keyword.indexOf(facet + ":") != -1)
			keyword = removeThisField(keyword, facet);

		if (facet == "annotation" && name == "Genome") {

			if (object[facet] != null && (object[facet] == "PATRIC" || object[facet] == "RAST"))
				keyword += " AND (rast_cds:[1 TO *])";
			else if (object[facet] != null && (object[facet] == "Legacy BRC" || object[facet] == "BRC"))
				keyword += " AND (brc_cds:[1 TO *])";
			else if (object[facet] != null && object[facet] == "RefSeq")
				keyword += " AND (refseq_cds:[1 TO *])";

		} else if (object[facet] != null && object[facet] != "") {
			entries = object[facet].split("##");
			DateFormat(entries, facet, name);
			keyword += " AND (" + facet + ":(" + entries.join(" OR ") + "))";
		}
	}
	return keyword.replace("(*) AND ", "");
}

function DateFormat(data, facet, name) {

	for (var i = 0; i < data.length; i++) {

		if (facet == "completion_date" || facet == "release_date") {

			if (data[i] == "*")
				data[i] = "[* TO *]";
			else
				data[i] = "[" + data[i] + "-01-01T00:00:00Z TO " + (parseInt(data[i].split("-")[0]) + parseInt("1")) + "-01-01T00:00:00Z]";

		} else if (configuration[name].display_facets.indexOf(facet) >= 0) {
			if (data[i] != "*")
				data[i] = "\"" + data[i] + "\"";
		}
	}
}

function removeThisField(keyword, facet) {

	var ind1 = keyword.indexOf(" AND (" + facet + ":");
	var ind2 = keyword.indexOf(")", ind1);

	keyword = keyword.replace(keyword.substring(ind1, ind2 + 6), "");

	return keyword;
}
