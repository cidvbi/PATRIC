/*******************************************************************************
 * Copyright 2014 Virginia Polytechnic Institute and State University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.vt.vbi.patric.common;

import java.util.List;
import java.util.Arrays;

public class DownloadHelper {

	public static List<String> getHeaderForFeatures() {
		List<String> header = Arrays.asList(new String[] {
			"Genome",
			"Accession",
			"Locus Tag",
			"RefSeq Locus Tag",
			"Annotation",
			"Feature Type",
			"Start",
			"End",
			"Length",
			"Strand",
			"Protein ID",
			"AA Length",
			"Gene Symbol",
			"Product",
			"Bound Moiety",
			"AntiCodon"
		});
		return header;
	}

	public static List<String> getFieldsForFeatures() {
		List<String> fields = Arrays.asList(new String[] {
			"genome_name",
			"accession",
			"locus_tag",
			"refseq_locus_tag",
			"annotation",
			"feature_type",
			"start_max",
			"end_min",
			"na_length",
			"strand",
			"protein_id",
			"aa_length",
			"gene",
			"product",
			"bound_moiety",
			"anticodon"
		});
		return fields;
	}

	public static List<String> getHeaderForGenomes() {
		List<String> header = Arrays.asList(new String[] {
			"Genome Info ID",
			"Genome Name",
			"NCBI Taxon ID",
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
			"NCBI Project ID",
			"RefSeq Project ID",
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
		});
		return header;
	}

	public static List<String> getFieldsForGenomes() {
		List<String> fields = Arrays.asList(new String[] {
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
		});
		return fields;
	}
}