/*******************************************************************************
 * Copyright 2013 Virginia Polytechnic Institute and State University
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

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

public class PubMedHelper {
	public static String getTitleString(HashMap<String, String> key) {
		DBShared conn_shared = new DBShared();
		String title = null;

		if (key.get("context").equalsIgnoreCase("taxon")) {
			ArrayList<ResultType> taxon_names = conn_shared.getTaxonNames(key.get("ncbi_taxon_id"));
			ResultType name = null;
			ResultType scientific_name = null;

			for (Iterator<ResultType> iter = taxon_names.iterator(); iter.hasNext();) {
				name = iter.next();
				if (name.containsKey("name_class") && !name.get("name_class").equals("")
						&& name.get("name_class").equalsIgnoreCase("scientific name")) {
					scientific_name = name;
				}
			}
			if (scientific_name != null) {
				title = scientific_name.get("name");
			}
			else {
				title = "";
			}
		}
		else if (key.get("context").equalsIgnoreCase("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(key.get("genome_info_id"));
			String organism_name = names.get("organism_name");
			String genome_name = names.get("genome_name");
			String qScope = key.get("scope");

			if (qScope != null && qScope.equals("o")) {
				title = organism_name;
			}
			else {
				title = genome_name;
			}
		}
		else if (key.get("context").equalsIgnoreCase("feature")) {

			// getting feature info from Solr

			SolrInterface solr = new SolrInterface();
			try {
				solr.setCurrentInstance("GenomicFeature");
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			JSONArray res = solr.searchSolrRecords("na_feature_id:" + key.get("feature_id"));
			JSONObject feature = new JSONObject();

			if (!res.isEmpty()) {
				feature = (JSONObject) res.get(0);
			}

			String qScope = key.get("scope");

			if (qScope != null && qScope.equals("g")) {
				title = feature.get("genome_name").toString();
			}
			else {
				// default, feature level

				int offset1 = feature.get("genome_name").toString().indexOf(" ");
				int offset2 = feature.get("genome_name").toString().indexOf(" ", offset1 + 1);

				String org = feature.get("genome_name").toString().substring(0, offset2);

				// System.out.println("offset1: "+offset1+", offset2: "+offset2+", org: "+org);

				title = "(\"" + org.toLowerCase() + "\") AND (\"" + feature.get("locus_tag") + "\" OR \""
						+ feature.get("product").toString().toLowerCase() + "\"";

				if (feature.get("gene") != null) {
					title += " OR \"" + feature.get("gene") + "\"";
				}

				if (feature.get("refseq_locus_tag") != null) {
					title += " OR \"" + feature.get("refseq_locus_tag") + "\"";
				}

				if (feature.get("refseq_protein_id") != null) {
					title += " OR \"" + feature.get("refseq_protein_id") + "\"";
				}

				title += ")";

			}

			// System.out.println("qScore="+qScope+", title="+title);

			// end of Solr query

			/*
			 * ResultType names = conn_shared.getNamesFromNaFeatureId(key.get("feature_id")); String organism_name =
			 * names.get("organism_name"); String genome_name = names.get("genome_name"); String feature_name =
			 * names.get("feature_name"); String qScope = key.get("scope");
			 * 
			 * if (qScope!=null && qScope.equals("o")) { title = organism_name; } else if (qScope!=null &&
			 * qScope.equals("g")) { title = genome_name; } else { title = genome_name+" "+feature_name; }
			 */
		}
		return title;
	}

	public static String getPubmedQueryString(HashMap<String, String> key) throws NullPointerException {
		String title = getTitleString(key);
		if (title == null || title.equals("")) {
			throw new NullPointerException("title is not defined");
		}

		// String pubmedquery = "\""+title+"\"[ALL]";
		String pubmedquery = title;

		// keyword configuration
		String _str_kw = "";
		String qKeyword = key.get("keyword");

		if (qKeyword != null && !qKeyword.equals("none")) {
			Hashtable<String, Vector<String>> keywordhash = PubMedHelper.getKeywordHash();
			Vector<String> querykeyv = keywordhash.get(qKeyword);
			if (querykeyv != null) {
				for (int i = 0; i < querykeyv.size(); i++) {
					_str_kw = _str_kw + " or \"" + querykeyv.get(i) + "\"[ALL]";
				}
				pubmedquery = pubmedquery + " AND (\"" + qKeyword + "\"[ALL]" + _str_kw + ")";
			}
		}

		// date
		String qDate = key.get("date");
		if (qDate != null && !qDate.equals("")) {
			if (qDate.equals("w")) {
				pubmedquery = pubmedquery + " \"last 7 days\"[dp]";
			}
			else if (qDate.equals("m")) {
				pubmedquery = pubmedquery + " \"last 1 months\"[dp]";
			}
			else if (qDate.equals("y")) {
				pubmedquery = pubmedquery + " \"last 1 year\"[dp]";
			}
			else if (qDate.equals("f")) {
				Date todayDate = new Date();
				SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy/MM/dd");
				SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
				String strToday = sdfToday.format(todayDate).toString();
				String strYear = sdfYear.format(todayDate).toString();
				int intNextYear = Integer.parseInt(strYear) + 1;
				pubmedquery = pubmedquery + " " + strToday + ":" + intNextYear + " [dp]";
			}
		}
		try {
			pubmedquery = URLEncoder.encode(pubmedquery, "UTF8").toString();
		}
		catch (Exception ex) {
		}

		return pubmedquery;
	}

	public static Hashtable<String, Vector<String>> getKeywordHash() {
		Hashtable<String, Vector<String>> keywordshash = new Hashtable<String, Vector<String>>();
		Vector<String> keyword1 = new Vector<String>();
		keyword1.add("drug");
		keyword1.add("vaccine");
		keyword1.add("theraputics");
		keyword1.add("diagnostics");
		keyword1.add("target");
		keywordshash.put("Countermeasures", keyword1);

		Vector<String> keyword2 = new Vector<String>();
		keyword2.add("mass spectrometry");
		keyword2.add("2D-gels");
		keyword2.add("protein-protein interaction");
		keywordshash.put("Proteomics", keyword2);

		Vector<String> keyword3 = new Vector<String>();
		keyword3.add("microarray");
		keyword3.add("transcriptome");
		keyword3.add("expression profiling");
		keyword3.add("real time PCR");
		keyword3.add("immune response");
		keyword3.add("response to infection");
		keyword3.add("host response");
		keyword3.add("pathogenesis");
		keyword3.add("virulence");
		keyword3.add("disease response");
		keywordshash.put("Gene expression", keyword3);

		Vector<String> keyword4 = new Vector<String>();
		keyword4.add("culture");
		keyword4.add("microscopy");
		keyword4.add("haemagglutination");
		keyword4.add("complement fixation");
		keyword4.add("ELISA");
		keyword4.add("EIA");
		keyword4.add("immune double diffusion");
		keyword4.add("immunoelectrophoresis");
		keyword4.add("latex agglutination");
		keyword4.add("western blot");
		keyword4.add("antibody");
		keyword4.add("Polymerase chain reaction");
		keyword4.add("PCR");
		keyword4.add("PCR primer");
		keyword4.add("western blot");
		keywordshash.put("Diagnosis", keyword4);

		Vector<String> keyword6 = new Vector<String>();
		keyword6.add("symptom");
		keyword6.add("syndrome");
		keyword6.add("prognosis");
		keywordshash.put("Disease", keyword6);

		Vector<String> keyword7 = new Vector<String>();
		keywordshash.put("Pathogenesis", keyword7);

		Vector<String> keyword8 = new Vector<String>();
		keywordshash.put("Prevention", keyword8);

		Vector<String> keyword9 = new Vector<String>();
		keywordshash.put("Host", keyword9);

		Vector<String> keyword10 = new Vector<String>();
		keywordshash.put("Reservoir", keyword10);

		Vector<String> keyword11 = new Vector<String>();
		keywordshash.put("Transmission", keyword11);

		Vector<String> keyword12 = new Vector<String>();
		keywordshash.put("Genome", keyword12);

		Vector<String> keyword13 = new Vector<String>();
		keywordshash.put("Taxonomy", keyword13);

		Vector<String> keyword14 = new Vector<String>();
		keyword14.add("outbreak");
		keyword14.add("epidemic");
		keywordshash.put("Epidemiology", keyword14);

		Vector<String> keyword15 = new Vector<String>();
		keyword15.add("Microarray");
		keyword15.add("Expression array");
		keyword15.add("Gene expression");
		keyword15.add("Expression profil");
		keyword15.add("Genome variation profil");
		keyword15.add("RNA profil");
		keyword15.add("Tiling array");
		keyword15.add("ArrayCGH");
		keyword15.add("ChIP-chip");
		keyword15.add("SAGE");
		keyword15.add("RNA-Seq");
		keyword15.add("Protein microarray");
		keyword15.add("Protein array");
		keyword15.add("Mass spec");
		keyword15.add("Protein identification");
		keyword15.add("Peptide identification");
		keyword15.add("2D gel");
		keyword15.add("Proteomics");
		keyword15.add("Protein structure");
		keyword15.add("three-dimensional structure");
		keyword15.add("3D structure");
		keyword15.add("NMR");
		keyword15.add("X-ray diffraction");

		keywordshash.put("Experiment Data", keyword15);

		return keywordshash;
	}
}
