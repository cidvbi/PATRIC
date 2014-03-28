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

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.ResultType;

/**
 * Class to support Genome Selector. You just need to call buildOrganismTreeListView() method to create an instance of genome selector.
 * 
 * @author Harry Yoo
 * 
 */
@SuppressWarnings("unchecked")
public class OrganismTreeBuilder {

	/**
	 * Build html to construct a genome selector.
	 * 
	 * @return html for genome selector
	 */
	public static String buildOrganismTreeListView() {
		StringBuilder view = new StringBuilder();
		view.append("<script type=\"text/javascript\" src=\"/patric-common/js/parameters.js\"></script>\n");
		view.append("<script type=\"text/javascript\" src=\"/patric/js/vbi/TriStateTree.min.js\"></script>\n");
		view.append("<script type=\"text/javascript\" src=\"/patric/js/vbi/GenomeSelector.min.js\"></script>\n");
		view.append("<div id=\"GenomeSelector\"></div>\n");
		return view.toString();
	}

	/**
	 * Create a json node for taxonomy tree
	 * 
	 * @param taxon_id NCBI Taxonomy ID
	 * @param name NCBI Taxonomy class name
	 * @param rank Taxonomy rank
	 * @param node_count number of genomes contained in current node
	 * @param isLeaf
	 * @param parent_id NCBI Taxonomy ID of parent node
	 * @return json object of tree node
	 */
	public static JSONObject createOrganismTreeNode(String taxon_id, String name, String rank, String node_count, boolean isLeaf, String parent_id) {
		JSONObject n = new JSONObject();
		n.put("id", Integer.parseInt(taxon_id));
		n.put("name", name);
		n.put("rank", rank);
		n.put("node_count", Integer.parseInt(node_count));
		n.put("leaf", isLeaf);
		if (parent_id != null && !parent_id.equals("")) {
			n.put("parentId", Integer.parseInt(parent_id));
		}
		return n;
	}

	/**
	 * Create a json node for genome list
	 * 
	 * @param genome_info_id Genome identifier
	 * @param name Genome name
	 * @param taxon_id NCBI Taxonomy ID of genome
	 * @return json object of genome-list node
	 */
	public static JSONObject createGenomeListNode(String genome_info_id, String name, String taxon_id, String parent_id) {
		JSONObject n = new JSONObject();
		n.put("id", Integer.parseInt(genome_info_id));
		n.put("genome_info_id", Integer.parseInt(genome_info_id));
		n.put("name", name);
		n.put("ncbi_taxon_id", Integer.parseInt(taxon_id));
		if (parent_id != null && !parent_id.equals("")) {
			n.put("parentId", Integer.parseInt(parent_id));
		}
		if (genome_info_id.equals("0")) {
			n.put("leaf", false);
		}
		else {
			n.put("leaf", true);
		}
		return n;
	}

	/**
	 * Builds an array of nodes for Genome List view
	 * 
	 * @param key HashMap of configuration parameters
	 * @return json array of feed for Genome List
	 */
	public static JSONArray buildGenomeList(HashMap<String, String> key) {

		DBSummary db_summary = new DBSummary();
		ResultType genome = db_summary.getTaxonomyNodeForGenomeSelector(key);
		ArrayList<ResultType> list = db_summary.getGenomeListForGenomeSelector(key);

		JSONArray azlist = new JSONArray();
		JSONArray children = new JSONArray();

		JSONObject root = createGenomeListNode("0", genome.get("class_name") + " (" + genome.get("node_count") + ")", genome.get("ncbi_taxon_id"),
				null);

		for (int i = 0; i < list.size(); i++) {
			genome = list.get(i);
			children.add(createGenomeListNode(genome.get("genome_info_id"), genome.get("genome_name"), genome.get("ncbi_taxon_id"), "0"));
		}

		root.put("children", children);
		azlist.add(root);

		return azlist;
	}

	/**
	 * Find a parent node of given id
	 */
	private static JSONArray nodeFinder(JSONArray tx, int id) {
		int idx = 0;
		JSONObject target = null;
		JSONArray result = null;

		for (idx = tx.size() - 1; idx >= 0; idx--) {
			target = (JSONObject) tx.get(idx);
			if (Integer.parseInt(target.get("id").toString()) == id) {
				result = (JSONArray) target.get("children");
				break;
			}
			if (result == null && target.containsKey("children")) {
				result = nodeFinder((JSONArray) target.get("children"), id);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Build an array of nodes for Taxonomy Tree view
	 * 
	 * @param key HashMap of configuration parameters
	 * @return json array of feed for Taxonomy Tree
	 */
	public static JSONArray buildGenomeTree(HashMap<String, String> key) {
		DBSummary db_summary = new DBSummary();
		JSONArray txtree = new JSONArray();

		// if taxonId is given, start from there, otherwise get the first node
		// (probably 2:bacteria) and start from there
		if (key.containsKey("ncbi_taxon_id") == false || key.get("ncbi_taxon_id") == null) {
			key.put("ncbi_taxon_id", "2");
		}

		ArrayList<ResultType> list = db_summary.getTaxonomyTreeForGenomeSelector(key);
		ResultType taxon = null;

		for (int idx = 0; idx < list.size(); idx++) {

			taxon = list.get(idx);

			if (taxon.get("is_leaf").equals("0") && !taxon.get("genome_below").equals("0")) {
				// has children
				JSONObject node = createOrganismTreeNode(taxon.get("ncbi_taxon_id"), taxon.get("class_name"), taxon.get("rank"),
						taxon.get("node_count"), false, taxon.get("parent_ncbi_taxon_id"));
				JSONArray children = new JSONArray();

				node.put("children", children);

				JSONArray addTo = nodeFinder(txtree, Integer.parseInt(taxon.get("parent_ncbi_taxon_id")));
				if (addTo != null) {
					addTo.add(node);
				}
				else {
					txtree.add(node);
				}
			}
			else {
				// this is a leaf node
				JSONObject node = createOrganismTreeNode(taxon.get("ncbi_taxon_id"), taxon.get("class_name"), taxon.get("rank"),
						taxon.get("node_count"), true, taxon.get("parent_ncbi_taxon_id"));
				if (nodeFinder(txtree, Integer.parseInt(taxon.get("parent_ncbi_taxon_id"))) == null) {
					txtree.add(node);
				}
				else {
					nodeFinder(txtree, Integer.parseInt(taxon.get("parent_ncbi_taxon_id"))).add(node);
				}
			}
		}
		return txtree;
	}

	/**
	 * Build a taxonomy-genome map.
	 * @param key HashMap of configuration parameters
	 * @return json array of mapping {ncbi_taxon_id,genome_info_id}
	 */
	public static JSONArray buildTaxonGenomeMapping(HashMap<String, String> key) {

		DBSummary db_summary = new DBSummary();
		ArrayList<ResultType> list = db_summary.getGenomeListForGenomeSelector(key);

		JSONArray tgm = new JSONArray();
		JSONObject child = null;
		for (int i = 0; i < list.size(); i++) {
			child = new JSONObject();
			child.put("ncbi_taxon_id", Integer.parseInt(list.get(i).get("ncbi_taxon_id")));
			child.put("genome_info_id", Integer.parseInt(list.get(i).get("genome_info_id")));
			tgm.add(child);
		}

		return tgm;
	}
}
