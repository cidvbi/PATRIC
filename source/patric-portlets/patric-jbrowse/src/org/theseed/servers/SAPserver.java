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
/**
 * 
 */
package org.theseed.servers;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.theseed.json.JSONArray;
import org.theseed.json.JSONException;

import org.theseed.serverConnections.ServerConnectJson;
//import serverConnections.ServerConnectYaml;
import org.theseed.serverConnections.ServerConnection;

/**
 * @author Rob Edwards
 * @author Daniel Cuevas
 * @author Josh Hoffman
 * @version 0.5
 */

public class SAPserver {
	/*
	 * Instantiate and connect to the server.
	 */
	public ServerConnection server = null;

	public SAPserver() {
		try {
			init_server(null);

		}
		catch (MalformedURLException e) {
			System.err.println("Oops! For some reason our URL is not right!");
			e.printStackTrace();
		}

	}

	public SAPserver(String url) {
		try {
			init_server(url);

		}
		catch (MalformedURLException e) {
			System.err.println("Oops! For some reason our URL is not right!");
			e.printStackTrace();
		}
	}

	void init_server(String url) throws MalformedURLException {
		if (url == null) {
			url = "http://servers.nmpdr.org/sapling/server.cgi";
		}
		// server = new ServerConnectYaml("http://servers.nmpdr.org/sapling/server.cgi");
		// server = new ServerConnectJson("http://bioseed.mcs.anl.gov/~olson/FIG/sap_server.cgi");
		// server = new ServerConnectJson("http://edwards.sdsu.edu/~redwards/cgi-bin/cgi.cgi"); // this is used for
		// debugging and just reports the cgi params
		// server = new ServerConnectJson("http://servers.nmpdr.org/sapling/server.cgi");

		server = new ServerConnectJson(url);
		server.setSource("Rob's java testing");
		server.setEmail("redwards@mcs.anl.gov");

	}

	/**
	 * <h1>Sapling Server Function Object</h1>
	 */

	// <h3>equiv_precise_assertions</h3> DONE
	// <h3>equiv_sequence_assertions</h3> DONE
	// <h3>feature_assignments</h3> DONE
	// <h3>ids_to_assertions</h3> DONE
	// <h3>ids_to_annotations</h3> DONE
	// <h3>ids_to_functions</h3> DONE
	// <h3>occ_of_role</h3> DONE
	// <h2>DNA and Protein Sequence Methods</h2>
	// <h3>equiv_ids_for_sequences</h3> DONE
	// <h3>ids_to_sequences</h3> DONE
	// <h3>locs_to_dna</h3> DONE
	// <h3>upstream</h3> DONE
	// <h2>Feature (Gene) Data Methods</h2>
	// <h3>equiv_sequence_ids</h3> DONE
	// <h3>fid_locations</h3> DONE
	// <h3>fid_possibly_truncated</h3>
	// <h3>fids_to_ids</h3>
	// <h3>fids_with_evidence_codes</h3>
	// <h3>genes_in_region</h3> DONE
	// <h3>ids_to_data</h3> DONE
	// <h3>ids_to_fids</h3>
	// <h3>ids_to_genomes</h3>
	// <h3>make_runs</h3>
	// <h2>FIGfam Data Methods</h2>
	// <h3>all_figfams</h3>
	// <h3>figfam_fids</h3>
	// <h3>figfam_function</h3>
	// <h3>ids_to_figfams</h3>
	// <h3>related_figfams</h3>
	// <h2>Functional Coupling Data Methods</h2>
	// <h3>clusters_containing</h3>
	// <h3>co_occurrence_evidence</h3>
	// <h3>conserved_in_neighborhood</h3>
	// <h3>pairsets</h3>
	// <h3>related_clusters</h3>
	// <h2>Genome Data Methods</h2>
	// <h3>all_features</h3>
	// <h3>all_genomes</h3> DONE
	// <h3>contig_lengths</h3>
	// <h3>genome_contigs</h3>
	// <h3>genome_metrics</h3>
	// <h3>genome_names</h3> DONE
	// <h3>is_prokaryotic</h3>
	// <h3>representative</h3>
	// <h3>representative_genomes</h3> DONE
	// <h3>taxonomy_of</h3>
	// <h2>Subsystem Data Methods</h2>
	// <h3>all_subsystems</h3>
	// <h3>classification_of</h3>
	// <h3>genomes_to_subsystems</h3>
	// <h3>ids_in_subsystems</h3>
	// <h3>ids_to_subsystems</h3> DONE
	// <h3>is_in_subsystem</h3>
	// <h3>is_in_subsystem_with</h3>
	// <h3>pegs_implementing_roles</h3>
	// <h3>pegs_in_subsystems</h3>
	// <h3>roles_exist_in_subsystem</h3>
	// <h3>subsystem_data</h3>
	// <h3>subsystem_names</h3>
	// <h3>subsystem_roles</h3>
	// <h3>subsystem_spreadsheet</h3>
	// <h3>subsystem_type</h3>

	/**
	 * <h1>Primary Methods</h1> <h2>Server Utility Methods</h2>
	 * 
	 * You will not use the methods in this section very often. Some are used by the server framework for maintenance and control purposes
	 * ("methods"), while others ("query" and "get") provide access to data in the database in case you need data not available from one of the
	 * standard methods.
	 */

	/**
	 * @return Return a reference to a list of the methods allowed on this object.
	 */
	public String[] methods() {
		server.reset();
		server.setMethod("methods");
		return server.resultsAsArray();
	}

	/**
	 * <h2>Annotation and Assertion Data Methods</h2>
	 */

	/**
	 * Return the assertions for all genes in the database that match the identified gene. The gene can be specified by any prefixed gene identifier
	 * (e.g. uni|AYQ44, gi|85841784, or fig|360108.3.peg.1041).
	 * 
	 * @param ids an array of IDs
	 * 
	 * @return Returns a reference to a hash that maps each incoming ID to a list of 4-tuples. Each 4-tuple contains (0) an identifier that is for the
	 * same gene as the input identifier, (1) the asserted function of that identifier, (2) the source of the assertion, and (3) a flag that is TRUE
	 * if the assertion is by a human expert.
	 */
	public HashMap<String, ArrayList<String>> equivPreciseAssertions(String[] ids) {
		server.reset();
		server.setMethod("equiv_precise_assertions");
		server.setData("-ids", ids);

		return server.resultsAsHashMap();
	}

	/**
	 * Return the assertions for all genes in the database that match the identified protein sequences. A protein sequence can be identified by a
	 * prefixed MD5 code or any prefixed gene identifier (e.g. uni|AYQ44, gi|85841784, or fig|360108.3.peg.1041).
	 * 
	 * @param ids Reference to a list of protein identifiers. Each identifier should be a prefixed gene identifier or the md5|-prefixed MD5 of a
	 * protein sequence.
	 * 
	 * @return Returns a hash mapping each incoming protein identifier to an ArrayList of 5-tuples, consisting of (0) an identifier that is
	 * sequence-equivalent to the input identifier, (1) the asserted function of that identifier, (2) the source of the assertion, (3) a flag that is
	 * TRUE if the assertion is by an expert, and (4) the name of the genome relevant to the identifer (if any).
	 */
	public HashMap<String, ArrayList<String>> equivSequenceAssertions(String[] ids) {
		server.reset();
		server.setMethod("equiv_sequence_assertions");
		server.setData("-ids", ids);

		return server.resultsAsHashMap();
	}

	/**
	 * @see SAPserver#featureAssignments(String, String, boolean)
	 */
	public HashMap<String, String> featureAssignments(String genome) {
		return this.featureAssignments(genome, null, false);
	}

	/**
	 * @see #featureAssignments(String, String, boolean)
	 */
	public HashMap<String, String> featureAssignments(String genome, String type) {
		return this.featureAssignments(genome, type, false);
	}

	/**
	 * @see #featureAssignments(String, String, boolean)
	 */
	public HashMap<String, String> featureAssignments(String genome, boolean hypothetical) {
		return this.featureAssignments(genome, null, hypothetical);
	}

	/**
	 * Return all features of the specified type for the specified genome along with their assignments.
	 * 
	 * @param genome ID of the genome whose features are desired.
	 * @param type If specified, the type of feature desired (peg, rna, etc.). If omitted, all features will be returned.
	 * @param hypothetical If true only hypothetical genes will be returned If undefined or not specified, all genes will be returned.
	 * @return Returns a hash mapping the ID of each feature in the specified genome to its assignment.
	 * 
	 * <em>Variation</em> varies from the API because API specifies that 0 will return only non-hypothetical genes.
	 */
	public HashMap<String, String> featureAssignments(String genome, String type, boolean hypothetical) {
		server.reset();
		server.setMethod("feature_assignments");
		server.setData("-genome", genome);
		if (type != null)
			server.setData("-type", type);
		if (hypothetical)
			server.setData("-hypothetical", "1");

		return server.resultsAsHashMapString();
	}

	/**
	 * Return the assertions associated with each prefixed ID.
	 * 
	 * @param ids An array of of prefixed feature IDs (e.g. gi|17017961, NP_625335.1, fig|360108.3.peg.1041). The assertions associated with each
	 * particular identifier will be returned. In this case, there will be no processing for equivalent IDs. For that, you should use
	 * equiv_sequence_assertions or equiv_precise_assertions.
	 * 
	 * @return Returns a hash mapping every incoming ID to an ArrayList of 3-tuples, each consisting of (0) an asserted function, (1) the source of
	 * the assertion, and (2) a flag that is TRUE if the assertion was made by an expert.
	 */
	public HashMap<String, ArrayList<String>> idsToAssertions(String[] ids) {
		server.reset();
		server.setMethod("ids_to_assertions");
		server.setData("-ids", ids);
		return server.resultsAsHashMap();
	}

	/**
	 * @see #idsToFunctions(String[], String)
	 */
	public HashMap<String, String> idsToFunctions(String[] ids) {
		return this.idsToFunctions(ids, null);
	}

	/**
	 * Return the functional assignment for each feature in the incoming list.
	 * @param ids A list of feature IDs.
	 * @param source Database source of the IDs specified-- SEED for FIG IDs, GENE for standard gene identifiers, or LocusTag for locus tags. In
	 * addition, you may specify RefSeq, CMR, NCBI, Trembl, or UniProt for IDs from those databases. Use mixed to allow mixed ID types (though this
	 * may cause problems when the same ID has different meanings in different databases). Use prefixed to allow IDs with prefixing indicating the ID
	 * type (e.g. uni|P00934 for a UniProt ID, gi|135813 for an NCBI identifier, and so forth). The default is SEED.
	 * 
	 * @return Returns a reference to a hash mapping each feature ID to the feature's current functional assignment. Features that do not exist in the
	 * database will not be present in the results. For IDs that correspond to multiple features, only one functional assignment will be returned.
	 */
	public HashMap<String, String> idsToFunctions(String[] ids, String source) {
		server.reset();
		server.setMethod("ids_to_functions");
		server.setData("-ids", ids);
		if (source != null)
			server.setData("-source", source);

		return server.resultsAsHashMapString();

	}

	/**
	 * Occurence of Roles and Functions
	 * 
	 * Search for features in a specified genome with the indicated roles or functions.
	 * 
	 * @param roles A list of the roles to search for.
	 * @param functions A list of the functional assignments to search for.
	 * @param genome ID of the genome whose genes are to be searched for the specified roles and assignments.
	 * @return A hash that maps each specified role ID or functional assignment to a list of the FIG IDs of genes that have that role or assignment.
	 */
	public HashMap<String, ArrayList<String>> occOfRole(String[] roles, String[] functions, String genome) {
		server.reset();
		server.setMethod("occ_to_role");
		server.setData("-genome", genome);
		server.setData("-roles", roles);
		server.setData("-functions", functions);
		return server.resultsAsHashMap();
	}

	public HashMap<String, ArrayList<HashMap<String, Object>>> compared_regions(String focus, int count, String[] genomes, String[] pins, int extent) {
		server.reset();
		server.setMethod("compared_regions");
		if (count == 0)
			count = 4;
		if (extent == 0)
			extent = 16000;
		server.setData("-focus", focus);
		server.setData("-count", count);
		if (genomes != null)
			server.setData("-genomes", genomes);
		if (pins != null)
			server.setData("-pins", pins);

		server.setData("-extent", extent);
		return server.resultsAsHashMapArrayHashMap();
	}

	/**
	 * Added by Harry Yoo (hyun@vbi.vt.edu)
	 * @param focus
	 * @param count
	 * @param genomes
	 * @param pins
	 * @param extent
	 * @return
	 * @throws Exception
	 */
	public BufferedReader compared_regions(String focus, int count, int extent) {
		server.reset();
		server.setMethod("compared_regions");
		if (count == 0)
			count = 4;
		if (extent == 0)
			extent = 16000;
		server.setData("-focus", focus);
		server.setData("-count", count);

		server.setData("-extent", extent);
		BufferedReader br = null;
		try {
			br = server.queryReader();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return br;
	}

	/**
	 * <h2>DNA and Protein Sequence Methods</h2>
	 * 
	 */

	/**
	 * Find all the identifiers in the database that produce the specified proteins.
	 * 
	 * @param sequences Reference to a list of protein specifications. A protein specification can be a FASTA string, a 3-tuple consisting of (0) a
	 * label, (1) a comment, and (2) a protein sequence, OR a 2-tuple consisting of (0) a label and (1) a protein sequence. In other words, each
	 * specification can be a raw FASTA string, a parsed FASTA string, or a simple [id, sequence] pair. In every case, the protein sequence will be
	 * used to find identifiers and the label will be used to identify the results.
	 * 
	 * @return A hash mapping each incoming label to a list of identifiers from the database that name the protein or a feature that produces the
	 * protein.
	 */
	public HashMap<String, ArrayList<String>> equivIdsForSequences(ArrayList<?>[] sequences) {
		server.reset();
		server.setMethod("equiv_ids_for_sequences");
		server.setData("-seqs", sequences);
		return server.resultsAsHashMap();
	}

	/**
	 * @see #idsToSequences(String[], String, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> idsToSequences(String[] ids) {
		return this.idsToSequences(ids, null);
	}

	/**
	 * @see #idsToSequences(String[], String, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> idsToSequences(String[] ids, String source) {
		return this.idsToSequences(ids, source, false);
	}

	/**
	 * @see #idsToSequences(String[], String, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> idsToSequences(String[] ids, String source, boolean protein) {
		return this.idsToSequences(ids, source, protein, false);
	}

	/**
	 * @see #idsToSequences(String[], String, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> idsToSequences(String[] ids, String source, boolean protein, boolean fasta) {
		HashMap<String, String> comments = new HashMap<String, String>();
		return this.idsToSequences(ids, source, protein, fasta, comments);
	}

	/**
	 * Compute a DNA or protein string for each incoming feature ID.
	 * @param ids Reference to a list of feature IDs.
	 * @param source Database source of the IDs specified-- SEED for FIG IDs, GENE for standard gene identifiers, or LocusTag for locus tags. In
	 * addition, you may specify RefSeq, CMR, NCBI, Trembl, or UniProt for IDs from those databases. Use mixed to allow mixed ID types (though this
	 * may cause problems when the same ID has different meanings in different databases). Use prefixed to allow IDs with prefixing indicating the ID
	 * type (e.g. uni|P00934 for a UniProt ID, gi|135813 for an NCBI identifier, and so forth). The default is SEED.
	 * 
	 * @param protein if set, the output FASTA sequences will be protein sequences; otherwise, they will be DNA sequences
	 * 
	 * @param fasta if set, the output sequences will be multi-line FASTA strings instead of sequences.
	 * 
	 * @param comments Allows the user to add a label or description to each FASTA formatted sequence. The HashMap has keys that are the ids, and the
	 * values are the desired labels. This parameter is only used when the -fasta option is specified.
	 * 
	 * @return a hash mapping the incoming IDs to sequence strings. IDs that are not found in the database will not appear in the hash.
	 */
	public HashMap<String, String> idsToSequences(String[] ids, String source, boolean protein, boolean fasta, HashMap<String, String> comments) {
		server.reset();
		server.setMethod("ids_to_sequences");
		server.setData("-ids", ids);
		if (source != null)
			server.setData("-source", source);
		if (protein)
			server.setData("-protein", true);
		if (fasta)
			server.setData("-fasta", true);
		if (comments.size() > 0)
			server.setData("-comments", comments);
		return server.resultsAsHashMapString();

	}

	/**
	 * @see #locsToDna(HashMap, boolean)
	 */
	public HashMap<String, String> locsToDna(HashMap<String, String> locations) {
		return this.locsToDna(locations, false);
	}

	/**
	 * Return the DNA sequences for the specified locations.
	 * 
	 * @param locations A a hash that maps IDs to a location. A location can be in the form of a "Location String"
	 * 
	 * @param fasta if set, the output sequences will be multi-line FASTA strings instead of sequences
	 * 
	 * @return A hash that maps the incoming IDs to FASTA sequences for the specified DNA locations. The FASTA ID will be the ID specified in the
	 * incoming hash.
	 */
	public HashMap<String, String> locsToDna(HashMap<String, String> locations, boolean fasta) {
		server.reset();
		server.setMethod("locs_to_dna");
		server.setData("-locations", locations);
		if (fasta)
			server.setData("-fasta", true);
		return server.resultsAsHashMapString();

	}

	/**
	 * @see #upstream(String[], int, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> upstream(String[] ids) {
		return this.upstream(ids, 0);
	}

	/**
	 * @see #upstream(String[], int, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> upstream(String[] ids, int size) {
		return this.upstream(ids, size, false);
	}

	/**
	 * @see #upstream(String[], int, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> upstream(String[] ids, int size, boolean skipGene) {
		return this.upstream(ids, size, skipGene, false);
	}

	/**
	 * @see #upstream(String[], int, boolean, boolean, HashMap)
	 */
	public HashMap<String, String> upstream(String[] ids, int size, boolean skipGene, boolean fasta) {
		HashMap<String, String> comments = new HashMap<String, String>();
		return this.upstream(ids, size, skipGene, fasta, comments);
	}

	/**
	 * Return the DNA sequences for the upstream regions of the specified features. The nucleotides inside coding regions are displayed in upper case;
	 * others are displayed in lower case.
	 * @param ids a list of FIG feature IDs of interest.
	 * @param size of upstream nucleotides to include in the output. The default is 200.
	 * @param skipGene If TRUE, only the upstream region is included. Otherwise, the content of the feature is included in the output.
	 * @param fasta If TRUE, the output sequences will be multi-line FASTA strings instead of sequences.
	 * @param comments Allows the user to add a label or description to each FASTA formatted sequence. The values is a reference to a hash whose keys
	 * are the ids, and the values are the desired labels. This parameter is only used when the -fasta option is specified.
	 * @return A hash mapping each incoming feature ID to the DNA sequence of its upstream region.
	 */
	public HashMap<String, String> upstream(String[] ids, int size, boolean skipGene, boolean fasta, HashMap<String, String> comments) {
		server.reset();
		server.setMethod("upstream");
		server.setData("-ids", ids);
		if (size > 0)
			server.setData("-size", size);
		if (skipGene)
			server.setData("-skipGene", true);
		if (fasta)
			server.setData("-fasta", true);
		if (comments.size() > 0)
			server.setData("-comments", comments);

		return server.resultsAsHashMapString();
	}

	/**
	 * <h2>Feature (Gene) Data Methods</h2>
	 */

	/**
	 * @see #equivalentSequenceIDs(String[], boolean)
	 */
	public HashMap<String, ArrayList<String>> equivalentSequenceIDs(String[] ids) {
		return this.equivalentSequenceIDs(ids, false);
	}

	/**
	 * Return all identifiers for genes in the database that are protein-sequence-equivalent to the specified identifiers. In this case, the
	 * identifiers are assumed to be in their natural form (without prefixes). For each identifier, the identified protein sequences will be found and
	 * then for each protein sequence, all identifiers for that protein sequence or for genes that produce that protein sequence will be returned.
	 * 
	 * Optionally, you can ask for identifiers that are precisely equivalent, that is, that identify the same location on the same genome.
	 * 
	 * @param ids a list of identifiers to ask for
	 * @param precise boolean whether to specify that they are on the same genome
	 * @return a hash that maps each incoming identifier to a list of sequence-equivalent identifie
	 */
	public HashMap<String, ArrayList<String>> equivalentSequenceIDs(String[] ids, boolean precise) {
		server.reset();
		server.setMethod("equiv_sequence_ids");
		server.setData("-ids", ids);
		if (precise)
			server.setData("-precise", true);
		return server.resultsAsHashMap();
	}

	/**
	 * 
	 * @see #fidLocations(String[], boolean)
	 */
	public HashMap<String, String> fidLocations(String[] ids) {
		return this.fidLocations(ids, false);
	}

	/**
	 * Return the DNA locations for the specified features
	 * @param ids The features to get the locations for
	 * @param boundaries If true, for any multi-location feature, a single location encompassing all the location segments will be returned instead of
	 * a list of all the segments. If the segments cross between contigs, then the behavior in this mode is undefined (something will come back, but
	 * it may not be what you're expecting). If this is false (or ommitted) the locations for each feature will be presented in a list.
	 * @return Returns a reference to a hash mapping each feature ID to a list of location strings representing the feature locations in sequence
	 * order
	 */
	public HashMap<String, String> fidLocations(String[] ids, boolean boundaries) {
		server.reset();
		server.setMethod("fid_locations");
		server.setData("-ids", ids);
		if (boundaries)
			server.setData("-boundaries", true);
		return server.resultsAsHashMapString();
	}

	// <h3>fid_possibly_truncated</h3>
	// <h3>fids_to_ids</h3>
	// <h3>fids_with_evidence_codes</h3>
	// <h3>genes_in_region</h3>

	/**
	 * @see #genesInRegion(String[], boolean)
	 */
	public HashMap<String, ArrayList<String>> genesInRegion(String[] locations) {
		return this.genesInRegion(locations, false);
	}

	/**
	 * Return a list of the IDs for the features that overlap the specified regions on a contig.
	 * @param locations Reference to a list of location strings (e.g. 360108.3:NZ_AANK01000002_264528_264007 or 100226.1:NC_003888_3766170+612). A
	 * location string consists of a contig ID (which includes the genome ID), an underscore, a begin offset, and either an underscore followed by an
	 * end offset or a direction (+ or -) followed by a length.
	 * @param includeLocation If true, then instead of mapping each location to a list of IDs, the hash will map each location to a hash reference
	 * that maps the IDs to their locations.
	 * @return Returns a reference to a hash mapping each incoming location string to a list of the IDs for the features that overlap that location.
	 */

	public HashMap<String, ArrayList<String>> genesInRegion(String[] locations, boolean includeLocation) {
		server.reset();
		server.setMethod("genes_in_region");
		server.setData("-locations", locations);
		if (includeLocation)
			server.setData("-includeLocation", "1");
		return server.resultsAsHashMap();
	}

	public HashMap<String, String[]> idsToData(String[] ids, String[] data) {
		return this.idsToData(ids, data, null);
	}

	public HashMap<String, String[]> idsToData(String[] ids, String[] data, String source) {
		return this.idsToData(ids, data, source, null);
	}

	public HashMap<String, String[]> idsToData(String[] ids, String[] data, String source, String genome) {
		server.reset();
		server.setMethod("ids_to_data");
		server.setData("-ids", ids);
		server.setData("-data", data);
		if (source != null)
			server.setData("-source", source);
		if (genome != null)
			server.setData("-genome", genome);
		HashMap<Object, Object> results = server.resultsAsHashMapObject();
		HashMap<String, String[]> toReturn = new HashMap<String, String[]>();
		try {
			for (Object o : results.keySet()) {
				String s = (String) o;
				JSONArray outerArray = (JSONArray) results.get(o);
				JSONArray ja = (JSONArray) outerArray.get(0);
				String[] match = new String[ja.length()];

				for (int i = 0; i < ja.length(); i++)
					match[i] = (String) ja.get(i);

				toReturn.put(s, match);
			}
		}
		catch (JSONException e) {
			System.err.println("Sorry, there was an error parsing the array");
			e.printStackTrace();
		}
		return toReturn;

	}

	/**
	 * get a list of all the genomes and their ids
	 * @return a hashmap of genome ids and their names
	 */
	public HashMap<String, String> organisms() {
		server.reset();
		server.setMethod("all_genomes");
		return server.resultsAsHashMapString();

	}

	/**
	 * <h1>Genome Data Methods</h1>
	 */

	/**
	 * @see #allFeatures(String[], String)
	 */
	public HashMap<String, ArrayList<String>> allFeatures(String[] ids) {
		return this.allFeatures(ids, null);
	}

	/**
	 * Get a list of features for a list of genomes. Takes an array of genome ids and a feature type (peg, rna, pp, etc), and returns a hashMap with
	 * the keys being genomes and the values being ids of those features. If the type is ommitted all features will be returned.
	 * 
	 * @param ids an array of genome IDs.
	 * @param type a string of the function type (e.g. rna, pp, peg)
	 * @return A hashMap of Genomes and feature IDs
	 * 
	 */
	public HashMap<String, ArrayList<String>> allFeatures(String[] ids, String type) {
		server.reset();
		server.setMethod("all_features");
		if (type != null)
			server.setData("-type", type);
		server.setData("-ids", ids);
		return server.resultsAsHashMap();
	}

	public HashMap<String, String> allGenomes() {
		server.reset();
		server.setMethod("all_genomes");
		return server.resultsAsHashMapString();
	}

	/**
	 * Return the name of the genome containing each specified feature or genome.
	 * @param ids A list of identifiers. Each identifier can be a prefixed feature ID (e.g. fig|100226.1.peg.3361, uni|P0AC98) or a genome ID
	 * (83333.1, 360108.3).
	 * @param numbers (optional) If true, the genome ID number will be returned instead of the name. Note that this facility is only useful when the
	 * incoming identifiers are feature IDs, as genome IDs would be mapped to themselves
	 * @return Returns a reference to a hash mapping each incoming feature ID to the scientific name of its parent genome. If an ID refers to more
	 * than one real feature, only the first feature's genome is returned.
	 */
	public HashMap<String, String> genomeNames(String[] ids) {
		return this.genomeNames(ids, false);
	}

	public HashMap<String, String> genomeNames(String[] ids, boolean numbers) {
		server.reset();
		server.setMethod("genome_names");
		server.setData("-ids", ids);
		if (numbers)
			server.setData("-numbers", numbers);
		return server.resultsAsHashMapString();
	}

	/**
	 * Return the representative genome for each specified incoming genome ID. Genomes with the same representative are considered closely related,
	 * while genomes with a different representative would be considered different enough that similarities between them have evolutionary
	 * significance.
	 * @param ids Reference to a list of the IDs for the genomes of interest
	 * @return turns a reference to a hash mapping each incoming genome ID to the ID of its representative genome.
	 */
	public HashMap<String, String> representative(String[] ids) {
		server.reset();
		server.setMethod("representative");
		server.setData("-ids", ids);
		return server.resultsAsHashMapString();
	}

	/**
	 * Compute mappings for the genome sets (OTUs) in the database. This method will return a mapping from each genome to its genome set ID and from
	 * each genome set ID to a list of the genomes in the set. For the second mapping, the first genome in the set will be the representative.
	 * @return Returns a reference to a 2-tuple. The first element is a reference to a hash mapping genome IDs to genome set IDs; the second element
	 * is a reference to a hash mapping each genome set ID to a list of the genomes in the set. The first genome in each of these lists will be the
	 * set's representative.
	 */
	public HashMap<Object, Object> representativeGenomes() {
		server.reset();
		server.setMethod("representative_genomes");
		return server.resultsAsHashMapObject();
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids) {
		return this.idsToSubsystems(ids, false);
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids, boolean usable) {
		return this.idsToSubsystems(ids, usable, null);
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids, boolean usable, String[] exclude) {
		return this.idsToSubsystems(ids, usable, exclude, null);
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids, boolean usable, String[] exclude, String source) {
		return this.idsToSubsystems(ids, usable, exclude, source, null);
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids, boolean usable, String[] exclude, String source, String genome) {
		return this.idsToSubsystems(ids, usable, exclude, source, genome, false);
	}

	public HashMap<String, ArrayList<String[]>> idsToSubsystems(String[] ids, boolean usable, String[] exclude, String source, String genome,
			boolean subsOnly) {
		server.reset();
		server.setMethod("ids_to_subsystems");
		server.setData("-ids", ids);
		if (usable)
			server.setData("-usable", usable);
		if (exclude != null)
			server.setData("-exclude", exclude);
		if (source != null)
			server.setData("-source", source);
		if (genome != null)
			server.setData("-genome", genome);
		if (subsOnly)
			server.setData("-subsOnly", "true");

		HashMap<Object, Object> results = server.resultsAsHashMapObject();
		HashMap<String, ArrayList<String[]>> toReturn = new HashMap<String, ArrayList<String[]>>();

		// convert the results to string/object
		for (Object obj : results.keySet()) {
			JSONArray ja = (JSONArray) results.get(obj);
			ArrayList<String[]> al = new ArrayList<String[]>(ja.length());
			try {
				for (int i = 0; i < ja.length(); i++) {
					JSONArray inner = (JSONArray) ja.get(i);
					String[] temp = new String[inner.length()];
					for (int j = 0; j < inner.length(); j++)
						temp[j] = inner.getString(j);
					al.add(temp);
				}
			}
			catch (JSONException e) {
				System.err.println("Sorry, there was an error parsing the JSON code");
				e.printStackTrace();
			}
			toReturn.put((String) obj, al);

		}
		return toReturn;
	}

}
