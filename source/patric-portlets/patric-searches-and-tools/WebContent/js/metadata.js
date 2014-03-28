var metadataName = ['genome_status_f', 'isolation_country_f', 'host_name_f', 'disease_f', 'collection_date_f', 'completion_date'];
var metadataTaxonSummary = ['genome_status_f', 'isolation_country_f', 'host_name_f'/*,  'oxygen_requirement', 'sporulation', 'temperature_range', 'motility', 'habitat',*/, 'disease_f', 'collection_date_f'];
var metadataGenomeSummaryID = ['Organism_Info', 'Isolate_Info', 'Host_Info', 'Sequence_Info', 'Phenotype_Info', 'Project_Info', 'Others'];
var metadataGenomeSummaryValue = {};
metadataGenomeSummaryValue['Organism_Info'] = [{
	name : 'Genome Info ID',
	text : 'genome_info_id',
	style : 'none'
}, {
	name : 'Genome Name',
	text : 'genome_name',
	style : 'none'
}, {
	name : 'NCBI Taxon ID',
	text : 'ncbi_tax_id',
	style : 'none'
}, {
	name : 'Genome Status',
	text : 'genome_status',
	style : 'visible'
}, {
	name : 'Organism Name',
	text : 'organism_name',
	style : 'none'
}, {
	name : 'Strain',
	text : 'strain',
	style : 'none'
}, {
	name : 'Serovar',
	text : 'serovar',
	style : 'none'
}, {
	name : 'Biovar',
	text : 'biovar',
	style : 'none'
}, {
	name : 'Pathovar',
	text : 'pathovar',
	style : 'none'
}, {
	name : 'MLST',
	text : 'mlst',
	style : 'none'
}, {
	name : 'Culture Collection',
	text : 'culture_collection',
	style : 'none'
}, {
	name : 'Type Strain',
	text : 'type_strain',
	style : 'none'
}];
metadataGenomeSummaryValue['Project_Info'] = [{
	name : 'Project Status',
	text : 'project_status',
	style : 'none'
}, {
	name : 'Availability',
	text : 'availability',
	style : 'none'
}, {
	name : 'Sequencing Center',
	text : 'sequencing_centers',
	style : 'none'
}, {
	name : 'Completion Date',
	text : 'completion_date',
	style : 'visible'
}, {
	name : 'Publication',
	text : 'publication',
	style : 'none',
	link : 'http://www.ncbi.nlm.nih.gov/pubmed/{0}',
	value: 'publication', 
	linkClass: 'double-arrow-link',
	cellClass: 'no-underline-links '
}, {
	name : 'NCBI Project ID',
	text : 'ncbi_project_id',
	style : 'none',
	link : 'http://www.ncbi.nlm.nih.gov/bioproject/{0}',
	value: 'ncbi_project_id',
	linkClass: 'double-arrow-link',
	cellClass: 'no-underline-links'
}, {
	name : 'RefSeq Project ID',
	text : 'refseq_project_id',
	style : 'none',
	link : 'http://www.ncbi.nlm.nih.gov/bioproject/{0}',
	value: 'refseq_project_id',
	linkClass: 'double-arrow-link',
	cellClass: 'no-underline-links'
}, {
	name : 'GenBank Accessions',
	text : 'genbank_accessions',
	style : 'none',
	link : 'http://www.ncbi.nlm.nih.gov/nuccore/?term={0}',
	value: 'genbank_accessions',
	linkClass: 'double-arrow-link',
	cellClass: 'no-underline-links'
}, {
	name : 'RefSeq Accessions',
	text : 'refseq_accessions',
	style : 'none',
	link : 'http://www.ncbi.nlm.nih.gov/nuccore/?term={0}',
	value: 'refseq_accessions',
	linkClass: 'double-arrow-link',
	cellClass: 'no-underline-links'

}];
metadataGenomeSummaryValue['Sequence_Info'] = [{
	name : 'Sequencing Status',
	text : 'sequencing_status',
	style : 'visible'
}, {
	name : 'Sequencing Platform',
	text : 'sequencing_platform',
	style : 'none'
}, {
	name : 'Sequencing Depth',
	text : 'sequencing_depth',
	style : 'none'
}, {
	name : 'Assembly Method',
	text : 'assembly_method',
	style : 'none'
}, {
	name : 'Chromosomes',
	text : 'chromosomes',
	style : 'none'
}, {
	name : 'Plasmids',
	text : 'plasmids',
	style : 'none'
}, {
	name : 'Contigs',
	text : 'contigs',
	style : 'none'
}, {
	name : 'Sequences',
	text : 'sequences',
	style : 'none'
}, {
	name : 'Genome Length',
	text : 'genome_length',
	style : 'none'
}, {
	name : 'GC Content',
	text : 'gc_content',
	style : 'none'
}, {
	name : 'RAST CDS',
	text : 'rast_cds',
	style : 'none',
	link : 'FeatureTable?cType=taxon&cId={0}&featuretype=CDS&annotation=PATRIC&filtertype=',
	value: 'ncbi_tax_id'
}, {
	name : 'BRC CDS',
	text : 'brc_cds',
	style : 'none',
	link : 'FeatureTable?cType=taxon&cId={0}&featuretype=CDS&annotation=BRC&filtertype=',
	value: 'ncbi_tax_id'
}, {
	name : 'RefSeq CDS',
	text : 'refseq_cds',
	style : 'none',
	link : 'FeatureTable?cType=taxon&cId={0}&featuretype=CDS&annotation=RefSeq&filtertype=',
	value: 'ncbi_tax_id'
}];
metadataGenomeSummaryValue['Isolate_Info'] = [{
	name : 'Isolation Site',
	text : 'isolation_site',
	style : 'none'
}, {
	name : 'Isolation Source',
	text : 'isolation_source',
	style : 'none'
}, {
	name : 'Isolation Comments',
	text : 'isolation_comments',
	style : 'none'
}, {
	name : 'Collection Date',
	text : 'collection_date',
	style : 'visible'
}, {
	name : 'Isolation Country',
	text : 'isolation_country',
	style : 'visible'
}, {
	name : 'Geographic Location',
	text : 'geographic_location',
	style : 'none'
}, {
	name : 'Latitude',
	text : 'latitude',
	style : 'none'
}, {
	name : 'Longitude',
	text : 'longitude',
	style : 'none'
}, {
	name : 'Altitude',
	text : 'altitude',
	style : 'none'
}, {
	name : 'Depth',
	text : 'depth',
	style : 'none'
}];
metadataGenomeSummaryValue['Host_Info'] = [{
	name : 'Host Name',
	text : 'host_name',
	style : 'visible'
}, {
	name : 'Host Gender',
	text : 'host_gender',
	style : 'none'
}, {
	name : 'Host Age',
	text : 'host_age',
	style : 'none'
}, {
	name : 'Host Health',
	text : 'host_health',
	style : 'none'
}, {
	name : 'Body Sample Site',
	text : 'body_sample_site',
	style : 'none'
}, {
	name : 'Body Sample Subsite',
	text : 'body_sample_subsite',
	style : 'none'
}];
metadataGenomeSummaryValue['Phenotype_Info'] = [{
	name : 'Gram Stain',
	text : 'gram_stain',
	style : 'none'
}, {
	name : 'Cell Shape',
	text : 'cell_shape',
	style : 'none'
}, {
	name : 'Motility',
	text : 'motility',
	style : 'none'
}, {
	name : 'Sporulation',
	text : 'sporulation',
	style : 'none'
}, {
	name : 'Temperature Range',
	text : 'temperature_range',
	style : 'none'
}, {
	name : 'Optimal Temperature',
	text : 'optimal_temperature',
	style : 'none'
}, {
	name : 'Salinity',
	text : 'salinity',
	style : 'none'
}, {
	name : 'Oxygen Requirement',
	text : 'oxygen_requirement',
	style : 'none'
}, {
	name : 'Habitat',
	text : 'habitat',
	style : 'none'
}, {
	name : 'Disease',
	text : 'disease',
	style : 'visible'
}];
metadataGenomeSummaryValue['Others'] = [{
	name : '&nbsp;&nbsp;',
	text : '',
	style : 'visible'
}, {
	name : 'Comments',
	text : 'comments',
	style : 'none'
}];
