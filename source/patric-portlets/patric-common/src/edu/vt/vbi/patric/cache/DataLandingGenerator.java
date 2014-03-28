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
package edu.vt.vbi.patric.cache;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.DBPathways;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.DBTranscriptomics;
import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class DataLandingGenerator {

	String baseURL = "http://enews.patricbrc.org";

	final Integer[] REFERENCE_GENOME_IDS = { 87468, 129921, 20916, 31049, 7843, 69206, 116463, 27904, 92729, 33062 };

	final Integer[] GENUS_TAXON_IDS = { 1386, 773, 138, 234, 32008, 194, 83553, 1485, 776, 943, 561, 262, 209, 1637, 1763, 780, 590, 620, 1279, 1301,
			662, 629 };

	final String URL_GENOMEOVERVIEW_TAB = "Genome?cType={cType}&cId={cId}";

	final String URL_FEATURETABLE_TAB = "FeatureTable?cType={cType}&cId={cId}&featuretype={featureType}&annotation=PATRIC&filtertype={filterType}";

	final String URL_PROTEINFAMILY_TAB = "FIGfam?cType={cType}&cId={cId}&dm=result&bm=";

	final String URL_PATHWAY_TAB = "CompPathwayTable?cType={cType}&cId={cId}&algorithm=PATRIC&ec_number=#aP0=1&aP1=1&aP2=1&aT=0&alg=RAST&cwEC=false&cwP=true&pId={pId}&pClass=&ecN=";

	final String URL_TRANSCRIPTOMICS_TAB = "ExperimentList?cType={cType}&cId={cId}&kw={kw}";

	final String URL_SINGLE_EXP = "SingleExperiment?cType=taxon&cId=2&eid={eid}";

	final String URL_GENOMEBROWSER = "GenomeBrowser?cType={cType}&cId={cId}&loc=0..10000&tracks=";

	final String URL_PATHWAY_EC_TAB = "CompPathwayTable?cType=genome&cId={cId}&algorithm=PATRIC&ec_number=#aP0=1&aP1=1&aP2=1&aT=1&alg=RAST&cwEC=false&cwP=true&pId={pId}&pClass=&ecN=";

	final String URL_PATHWAY_GENE_TAB = "CompPathwayTable?cType=genome&cId={cId}&algorithm=PATRIC&ec_number=#aP0=1&aP1=1&aP2=1&aT=2&alg=RAST&cwEC=false&cwP=true&pId={pId}&pClass=&ecN=";

	public void setBaseURL(String url) {
		baseURL = url;
	}

	public boolean createCacheFileGenomes(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;
		// from WP
		// data
		data = read(baseURL + "/tab/dlp-genomes-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-genomes-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-genomes-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-genomes-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}
		// from solr or database
		// add popularGenomes
		data = getPopularGenomes();
		if (data != null) {
			jsonData.put("popularGenomes", data);
		}
		// add top5_1
		data = getTop5List("host_name_f");
		if (data != null) {
			jsonData.put("top5_1", data);
		}
		// add top5_2
		data = getTop5List("isolation_country_f");
		if (data != null) {
			jsonData.put("top5_2", data);
		}
		// add numberGenomes
		data = getGenomeCounts();
		if (data != null) {
			jsonData.put("numberGenomes", data);
		}

		// add genomeStatus
		data = getGenomeStatus();
		if (data != null) {
			jsonData.put("genomeStatus", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFileProteinFamilies(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-proteinfamilies-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-proteinfamilies-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-proteinfamilies-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-proteinfamilies-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}
		//
		// add popularGenra
		data = getPopularGeneraFigfam();
		if (data != null) {
			jsonData.put("popularGenomes", data);
		}
		// add figfam graph data
		data = getProteinFamilies();
		if (data != null) {
			jsonData.put("FIGfams", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFileGenomicFeatures(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-genomicfeatures-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// popular genomes
		data = getPopularGenomesForGenomicFeature();
		if (data != null) {
			jsonData.put("popularGenomes", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-genomicfeatures-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-genomicfeatures-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-genomicfeatures-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFileTranscriptomics(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-transcriptomics-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-transcriptomics-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-transcriptomics-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-transcriptomics-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}
		// topSpecies
		data = getTopSpeciesForTranscriptomics();
		if (data != null) {
			jsonData.put("topSpecies", data);
		}
		// featuredExperiment
		data = getFeaturedExperimentForTranscriptomics();
		if (data != null) {
			jsonData.put("featuredExperiment", data);
		}
		// popularGenomes
		data = getPopularGenomesForTranscriptomics();
		if (data != null) {
			jsonData.put("popularGenomes", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFileProteomics(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-proteomics-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-proteomics-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-proteomics-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-proteomics-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFilePPInteractions(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-ppinteractions-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-ppinteractions-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-ppinteractions-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-ppinteractions-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFilePathways(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();
		JSONObject data = null;

		// from WP
		// data
		data = read(baseURL + "/tab/dlp-pathways-data/?req=passphrase");
		if (data != null) {
			jsonData.put("data", data);
		}
		// conservation
		data = getPathwayECDist();
		if (data != null) {
			jsonData.put("conservation", data);
		}
		// populargenomes
		data = getPopularGenomesForPathways();
		if (data != null) {
			jsonData.put("popularGenomes", data);
		}
		// tools
		data = read(baseURL + "/tab/dlp-pathways-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/dlp-pathways-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/dlp-pathways-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}

		// save jsonData to file
		try {
			PrintWriter jsonOut = new PrintWriter(new FileWriter(filePath));
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	private JSONObject getProteinFamilies() {
		JSONObject jsonData = null;
		JSONArray series = new JSONArray();

		DBSummary conn = new DBSummary();
		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GlobalTaxonomy");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ResultType key = new ResultType();
		JSONObject res = null;
		JSONObject tx = null;

		for (Integer txId : GENUS_TAXON_IDS) {
			ResultType stat = conn.getFIGFamStat(txId);
			try {
				key.put("keyword", "taxon_id:" + txId);
				res = solr.getData(key, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				tx = (JSONObject) docs.get(0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject item = new JSONObject();
			item.put("pathogen", tx.get("taxon_name"));
			item.put("genomes", tx.get("genomes"));
			item.put("total", Integer.parseInt(stat.get("total")));
			item.put("functional", Integer.parseInt(stat.get("functional")));
			item.put("hypotheticals", Integer.parseInt(stat.get("hypotheticals")));
			item.put("core", Integer.parseInt(stat.get("core")));
			item.put("accessory", Integer.parseInt(stat.get("accessory")));

			series.add(item);
		}

		jsonData = new JSONObject();
		jsonData.put("chart_title", "FIGfams");
		jsonData.put(
				"chart_desc",
				"FIGfams are protein families generated by the Fellowship for Interpretation of Genomes (FIG), which are based on a collection of functional subsystems, as well as correspondences between genes in closely related strains.  FIGfams are sets of Protein Sequences that are similar along their full length.");
		jsonData.put("data", series);

		return jsonData;
	}

	private JSONObject getPopularGeneraFigfam() {
		JSONObject jsonData = null;
		JSONArray list = new JSONArray();
		DBSummary conn = new DBSummary();
		DBShared shared = new DBShared();
		ArrayList<ResultType> dist = null;

		for (Integer txId : GENUS_TAXON_IDS) {
			dist = conn.getFIGFamConservDist(txId);
			ResultType name = shared.getNamesFromTaxonId(txId);
			JSONArray data = new JSONArray();

			// initialize map
			HashMap<Integer, Integer> distMap = new HashMap<Integer, Integer>();
			for (int i = 1; i <= 10; i++) {
				distMap.put(i, 0);
			}
			// update map
			for (ResultType pt : dist) {
				distMap.put(Integer.parseInt(pt.get("grp")), Integer.parseInt(pt.get("cnt")));
			}
			// convert map to JSONObject;
			for (int i = 1; i <= 10; i++) {
				JSONObject o = new JSONObject();
				o.put("x", i + "0%");
				o.put("y", distMap.get(i));
				data.add(o);
			}

			JSONObject item = new JSONObject();
			item.put("link", "/portal/portal/patric/FIGfam?cType=taxon&cId=" + txId + "&dm=result&bm=");
			item.put("popularName", name.get("name"));
			item.put("popularData", data);

			list.add(item);
		}
		jsonData = new JSONObject();
		jsonData.put("popularTitle", "Popular Genera");
		jsonData.put("popularList", list);

		return jsonData;
	}

	private JSONObject getGenomeStatus() {
		JSONObject jsonData = null;

		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GenomeFinder");
			JSONObject status = solr.queryFacet("*:*", "genome_status_f");

			if (status != null) {
				long total = (long) status.get("total");
				JSONArray facet = (JSONArray) status.get("facet");

				JSONArray data = new JSONArray();
				for (Object _f : facet) {
					JSONObject f = (JSONObject) _f;
					JSONObject item = new JSONObject();
					if (f.get("value").equals("WGS")) {
						item.put("label", "Whole Genome Shotgun");
						item.put("m_label", "gsc_shotgun_sequence");
					}
					else if (f.get("value").equals("Complete") || f.get("value").equals("Plasmid")) {
						item.put("label", f.get("value"));
						item.put("m_label", "gsc_" + f.get("value").toString().toLowerCase());
					}
					float percentage = ((long) f.get("count")) * 100.00f / total;
					item.put("value", Math.round(percentage));
					item.put("reported", Math.round(percentage) + "%");

					data.add(item);
				}
				jsonData = new JSONObject();
				jsonData.put("chart_title", "Genome Status");
				jsonData.put("data", data);
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	private JSONObject getGenomeCounts() {
		SolrInterface solr = new SolrInterface();
		String solrServer = solr.getSeverUrl("GenomeFinder");
		String cUrl = solrServer + "/select?q=genome_status_f%3AComplete&facet=true&facet.range=completion_date&f.completion_date.facet.range.start=2009-01-01T05%3A00%3A00.000Z&f.completion_date.facet.range.end=2015-01-01T05%3A00%3A00.000Z&f.completion_date.facet.range.gap=%2B1YEAR&facet.sort=index&facet.range.other=before&rows=0&wt=json";
		String wUrl = solrServer + "/select?q=genome_status_f%3AWGS&facet=true&facet.range=completion_date&f.completion_date.facet.range.start=2009-01-01T05%3A00%3A00.000Z&f.completion_date.facet.range.end=2015-01-01T05%3A00%3A00.000Z&f.completion_date.facet.range.gap=%2B1YEAR&facet.sort=index&facet.range.other=before&rows=0&wt=json";

		JSONObject cRet = read(cUrl);
		JSONObject cFacetCounts = (JSONObject) cRet.get("facet_counts");
		JSONObject cFacetRanges = (JSONObject) cFacetCounts.get("facet_ranges");
		JSONObject cCompletionDates = (JSONObject) cFacetRanges.get("completion_date");

		JSONObject wRet = read(wUrl);
		JSONObject wFacetCounts = (JSONObject) wRet.get("facet_counts");
		JSONObject wFacetRanges = (JSONObject) wFacetCounts.get("facet_ranges");
		JSONObject wCompletionDates = (JSONObject) wFacetRanges.get("completion_date");

		long cBefore = (long) cCompletionDates.get("before");
		long wBefore = (long) wCompletionDates.get("before");
		JSONArray cCounts = (JSONArray) cCompletionDates.get("counts");
		JSONArray wCounts = (JSONArray) wCompletionDates.get("counts");
		
		JSONArray series = new JSONArray();
		
		for (int i = 0; i < cCounts.size(); i=i+2 ) {
			String year = cCounts.get(i).toString().substring(0, 4);

			long cCount = (long) cCounts.get(i+1);
			long wCount = (long) wCounts.get(i+1);

			JSONObject item = new JSONObject();
			item.put("year", Integer.parseInt(year));
			item.put("complete", cBefore + cCount);
			item.put("wgs", wBefore + wCount);
			series.add(item);

			cBefore += cCount;
			wBefore += wCount;
		}
		
		JSONObject jsonData = new JSONObject();
		jsonData.put("chart_title", "Number of Bacterial Genomes");
		jsonData.put("data", series);

		return jsonData;
	}

	private JSONObject getTop5List(String type) {
		JSONObject jsonData = null;
		// http://macleod.vbi.vt.edu:8983/solr/genomesummary/select?q=*%3A*&rows=0&wt=xml&facet=true&facet.field=host_name_f
		// http://macleod.vbi.vt.edu:8983/solr/genomesummary/select?q=*%3A*&rows=0&wt=xml&facet=true&facet.field=genome_status_f
		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GenomeFinder");
			JSONObject status = solr.queryFacet("*:*", type);

			if (status != null) {
				JSONArray facet = (JSONArray) status.get("facet");
				JSONArray data = new JSONArray();
				long cntTop = 0, cntSecond = 0;

				for (int i = 0; i < 5; i++) {
					JSONObject f = (JSONObject) facet.get(i);
					JSONObject item = new JSONObject();

					String icon = ""; // default
					switch (f.get("value").toString()) {
					case "Human, Homo sapiens":
						icon = "/patric/images/hosts/human.png";
						break;
					case "Cattle, Bos sp.":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "Cattle, Bos taurus":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "Bovine":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "Pig, Sus scrofa domesticus":
						icon = "/patric/images/hosts/pig.png";
						break;
					case "Cassava, Manihot esculenta":
						icon = "/patric/images/hosts/cassava.png";
						break;
					case "Bovine, Bovinae":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "Cow, Bos Taurus":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "American bison, Bison bison":
						icon = "/patric/images/hosts/bison.png";
						break;
					case "Mouse, Mus musculus":
						icon = "/patric/images/hosts/mouse.png";
						break;
					case "cow":
						icon = "/patric/images/hosts/cow.png";
						break;
					case "pig":
						icon = "/patric/images/hosts/pig.png";
						break;
					case "tick":
						icon = "/patric/images/hosts/tick.png";
						break;
					case "sheep":
						icon = "/patric/images/hosts/sheep.png";
						break;
					case "USA":
						icon = "/patric/images/flags/United-States.png";
						break;
					default:
						icon = "/patric/images/flags/" + f.get("value").toString().replaceAll(" ", "-") + ".png";
						break;
					}

					item.put("icon", icon);
					item.put("label", f.get("value"));
					item.put("m_label", f.get("value").toString().replaceAll(" ", "_").toLowerCase());
					item.put("value", f.get("count"));

					data.add(item);
					//
					if (i == 0) {
						cntTop = (long) f.get("count");
					}
					else if (i == 1) {
						cntSecond = (long) f.get("count");
					}
				}
				// reported?
				if (cntTop > 2 * cntSecond) {
					JSONObject item = (JSONObject) data.get(0);
					item.put("reported", Math.round(cntSecond * 1.5));
					data.set(0, item);
				}
				jsonData = new JSONObject();
				if (type.equals("host_name_f")) {
					jsonData.put("chart_title", "Bacterial Host");
					jsonData.put("chart_desc", "Top 5 Bacterial Hosts");
					jsonData.put("tab_title", "Host");
				}
				else if (type.equals("isolation_country_f")) {
					jsonData.put("chart_title", "Isolation Country");
					jsonData.put("chart_desc", "Top 5 Isolation Countries");
					jsonData.put("tab_title", "Isolation Country");
				}
				jsonData.put("data", data);
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	private JSONObject getTopSpeciesForTranscriptomics() {

		SolrInterface solr = new SolrInterface();
		ResultType key = new ResultType();
		JSONObject res = null;

		JSONObject jsonData = new JSONObject();
		jsonData.put("title", "TOP 5 Species with Transcriptomics Data");

		JSONArray data = new JSONArray();
		try {
			solr.setCurrentInstance("GENEXP_Experiment");

			key.put("keyword", "*");
			res = solr.getData(key, null, "{\"facet\":\"organism\"}", 0, 0, true, false, false);

			JSONObject facets = (JSONObject) res.get("facets");
			JSONObject annotations = (JSONObject) facets.get("organism");
			JSONArray attrs = (JSONArray) annotations.get("attributes");

			int i = 0;
			for (Object attr : attrs) {
				JSONObject j = (JSONObject) attr;

				JSONObject organism = new JSONObject();
				organism.put("label", j.get("value"));
				organism.put("value", j.get("count"));
				data.add(organism);
				i++;
				if (i > 4) {
					break;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		jsonData.put("data", data);
		return jsonData;
	}

	private JSONObject getFeaturedExperimentForTranscriptomics() {
		JSONObject jsonData = new JSONObject();
		JSONArray data = new JSONArray();

		SolrInterface solr = new SolrInterface();
		ResultType key = new ResultType();
		HashMap<String, String> sort = new HashMap<String, String>();
		try {
			key.put("keyword", "*");
			sort.put("field", "release_date");
			sort.put("direction", "desc");

			solr.setCurrentInstance("GENEXP_Experiment");
			JSONObject res = solr.getData(key, sort, null, 0, 3, false, false, false);
			JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");

			for (Object obj : docs) {
				JSONObject row = (JSONObject) obj;
				JSONObject exp = new JSONObject();

				exp.put("title", row.get("title"));
				exp.put("pmid", row.get("pmid"));
				exp.put("accession", row.get("accession"));
				ArrayList<String> organisms = (ArrayList<String>) row.get("organism");
				exp.put("organism", organisms.get(0));
				exp.put("link", URL_SINGLE_EXP.replace("{eid}", row.get("eid").toString()));

				data.add(exp);
			}
			jsonData.put("data", data);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return jsonData;
	}

	private JSONObject getPopularGenomes() {
		JSONObject jsonData = null;
		JSONArray list = new JSONArray();

		DBPathways connPW = new DBPathways();
		DBTranscriptomics connTR = new DBTranscriptomics();
		SolrInterface solr = new SolrInterface();

		for (Integer gid : REFERENCE_GENOME_IDS) {
			HashMap<String, String> key = new HashMap<String, String>();
			key.put("genomeId", gid.toString());
			key.put("algorithm", "RAST");

			ResultType skey = new ResultType();
			JSONObject res = null;
			JSONObject genomeInfo = null;

			// construct genome
			JSONObject genome = new JSONObject();
			genome.put("link", URL_GENOMEOVERVIEW_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));

			// Genome Name
			try {
				skey.put("keyword", "genome_info_id:" + gid);
				solr.setCurrentInstance("GenomeFinder");
				res = solr.getData(skey, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				genomeInfo = (JSONObject) docs.get(0);
				// System.out.println(genomeInfo.toJSONString());
				genome.put("popularName", genomeInfo.get("genome_name"));
				genome.put("gb_link", URL_GENOMEBROWSER.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
				// meta data
				JSONObject meta = new JSONObject();
				meta.put("genome_status", genomeInfo.get("genome_status"));
				meta.put("completion_date", (genomeInfo.get("completion_date") != null)? genomeInfo.get("completion_date"): "");
				meta.put("collection_date", (genomeInfo.get("collection_date") != null)? genomeInfo.get("collection_date"): "");
				meta.put("isolation_country", (genomeInfo.get("isolation_country") != null) ? genomeInfo.get("isolation_country") : "");
				meta.put("host_name", (genomeInfo.get("host_name") != null) ? genomeInfo.get("host_name") : "");
				meta.put("disease", (genomeInfo.get("disease") != null) ? genomeInfo.get("disease") : "");
				meta.put("chromosomes", genomeInfo.get("chromosomes"));
				meta.put("plasmids", genomeInfo.get("plasmids"));
				meta.put("contigs", genomeInfo.get("contigs"));
				meta.put("genome_length", genomeInfo.get("genome_length"));

				genome.put("metadata", meta);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			JSONArray data = new JSONArray();

			// Features
			JSONObject ft = new JSONObject();
			ft.put("description", "Features");
			ft.put("link", URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)).replace("{featureType}", "")
					.replace("{filterType}", ""));
			ft.put("picture", "/patric/images/icon-popular-feature.png");
			ft.put("data", genomeInfo.get("rast_cds"));
			data.add(ft);

			// Pathways
			JSONObject pw = new JSONObject();
			pw.put("description", "Pathways");
			pw.put("link", URL_PATHWAY_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
			pw.put("picture", "/patric/images/icon-popular-pathway.png");
			int cntPathway = connPW.getCompPathwayPathwayCount(key);
			pw.put("data", cntPathway);
			data.add(pw);

			// Protein Family
			JSONObject pf = new JSONObject();
			pf.put("description", "Protein Families");
			pf.put("link", URL_PROTEINFAMILY_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
			pf.put("picture", "/patric/images/icon-popular-proteinfamily.png");

			// Experiment
			JSONObject tr = new JSONObject();
			tr.put("description", "Transcriptomic Experiments");
			tr.put("link", URL_TRANSCRIPTOMICS_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid).replace("{kw}", "")));
			ArrayList<String> listEId = connTR.getEIDsFromGenomeID(gid.toString());
			tr.put("picture", "/patric/images/icon-popular-experiment.png");
			tr.put("data", listEId.size());
			data.add(tr);

			skey.put("keyword", "figfam_id:[*%20TO%20*]");
			skey.put("filter", "gid:" + gid + " AND annotation:PATRIC");
			try {
				solr.setCurrentInstance("GenomicFeature");
				res = solr.getData(skey, null, null, 0, 0, false, false, false);
				pf.put("data", ((JSONObject) res.get("response")).get("numFound"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			data.add(pf);

			genome.put("popularData", data);

			list.add(genome);
		}
		if (list.size() > 0) {
			jsonData = new JSONObject();
			jsonData.put("popularList", list);
			jsonData.put("popularTitle", "Select Genomes");
		}
		return jsonData;
	}

	private JSONObject getPopularGenomesForGenomicFeature() {
		JSONObject jsonData = null;
		JSONArray list = new JSONArray();

		SolrInterface solr = new SolrInterface();

		for (Integer gid : REFERENCE_GENOME_IDS) {

			ResultType key = new ResultType();
			ResultType skey = new ResultType();
			JSONObject res = null;
			JSONObject genomeInfo = null;

			JSONObject hypotheticalProteins = new JSONObject();
			JSONObject functionalProteins = new JSONObject();
			JSONObject ecAssignedProteins = new JSONObject();
			JSONObject goAssignedProteins = new JSONObject();
			JSONObject pathwayAssignedProteins = new JSONObject();
			JSONObject figfamAssignedProteins = new JSONObject();

			// construct genome
			JSONObject genome = new JSONObject();
			genome.put("link", URL_GENOMEOVERVIEW_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));

			// Genome Info
			try {
				skey.put("keyword", "genome_info_id:" + gid);
				solr.setCurrentInstance("GenomeFinder");
				res = solr.getData(skey, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				genomeInfo = (JSONObject) docs.get(0);
				genome.put("popularName", genomeInfo.get("genome_name"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			JSONArray featureTypes = new JSONArray();
			JSONArray proteinSummary = new JSONArray();
			try {
				solr.setCurrentInstance("GenomicFeature");
				key.put("filter", String.format("gid:(%d) AND annotation_f:PATRIC", gid));

				// top 5 feature type
				key.put("keyword", "*");
				res = solr.getData(key, null, "{\"facet\":\"feature_type_f\"}", 0, 0, true, false, false);

				JSONObject facets = (JSONObject) res.get("facets");
				JSONObject annotations = (JSONObject) facets.get("feature_type_f");
				JSONArray attrs = (JSONArray) annotations.get("attributes");

				int i = 0;
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;

					JSONObject fTypes = new JSONObject();
					fTypes.put("description", j.get("value"));
					fTypes.put(
							"link",
							URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid))
									.replace("{featureType}", j.get("value").toString()).replace("{filterType}", ""));
					fTypes.put("data", j.get("count"));
					featureTypes.add(fTypes);
					i++;
					if (i > 4) {
						break;
					}
				}

				// Protein Summary
				// hypothetical
				key.put("keyword", "product:(hypothetical AND protein) AND feature_type_f:CDS");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				hypotheticalProteins.put("description", "Unknwon functions");
				hypotheticalProteins.put("link", URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid))
						.replace("{featureType}", "CDS").replace("{filterType}", "hypothetical_proteins"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					hypotheticalProteins.put("data", j.get("count"));
				}
				proteinSummary.add(hypotheticalProteins);

				// funtional assigned
				key.put("keyword", "!product:(hypothetical AND protein) AND feature_type_f:CDS");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				functionalProteins.put("description", "Functional assignments");
				functionalProteins.put("link",
						URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)).replace("{featureType}", "CDS")
								.replace("{filterType}", "functional_proteins"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					functionalProteins.put("data", j.get("count"));
				}
				proteinSummary.add(functionalProteins);

				// ec assigned
				key.put("keyword", "ec:[*%20TO%20*]");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				ecAssignedProteins.put("description", "EC assignments");
				ecAssignedProteins.put("link",
						URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)).replace("{featureType}", "CDS")
								.replace("{filterType}", "ec"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					ecAssignedProteins.put("data", j.get("count"));
				}
				proteinSummary.add(ecAssignedProteins);

				// go assigned
				key.put("keyword", "go:[*%20TO%20*]");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				goAssignedProteins.put("description", "GO assignments");
				goAssignedProteins.put("link",
						URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)).replace("{featureType}", "CDS")
								.replace("{filterType}", "go"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					goAssignedProteins.put("data", j.get("count"));
				}
				proteinSummary.add(goAssignedProteins);

				// pathway assigned
				key.put("keyword", "pathway:[*%20TO%20*]");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				pathwayAssignedProteins.put("description", "Pathways assignments");
				pathwayAssignedProteins.put("link", URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid))
						.replace("{featureType}", "CDS").replace("{filterType}", "pathway"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					pathwayAssignedProteins.put("data", j.get("count"));
				}
				proteinSummary.add(pathwayAssignedProteins);

				// figfam assigned
				key.put("keyword", "figfam_id:[*%20TO%20*]");
				res = solr.getData(key, null, "{\"facet\":\"annotation_f\"}", 0, 0, true, false, false);

				facets = (JSONObject) res.get("facets");
				annotations = (JSONObject) facets.get("annotation_f");
				attrs = (JSONArray) annotations.get("attributes");

				figfamAssignedProteins.put("description", "FIGfam assignments");
				figfamAssignedProteins.put("link", URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid))
						.replace("{featureType}", "CDS").replace("{filterType}", "figfam_id"));
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;
					figfamAssignedProteins.put("data", j.get("count"));
				}
				proteinSummary.add(figfamAssignedProteins);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			//
			genome.put("featureTypes", featureTypes);
			genome.put("proteinSummary", proteinSummary);

			// link outs
			JSONArray links = new JSONArray();
			// Genome Browser
			JSONObject link = new JSONObject();
			link.put("name", "Genome Browser");
			link.put("link", URL_GENOMEBROWSER.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
			links.add(link);

			// Feature Table
			link = new JSONObject();
			link.put("name", "Feature Table");
			link.put("link", URL_FEATURETABLE_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid))
					.replace("{featureType}", "").replace("{filterType}", ""));
			links.add(link);

			// Protein Family
			link = new JSONObject();
			link.put("name", "Protein Family Sorter");
			link.put("link", URL_PROTEINFAMILY_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
			links.add(link);

			// Pathway
			link = new JSONObject();
			link.put("name", "Pathway");
			link.put("link", URL_PATHWAY_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)));
			links.add(link);

			// Transcriptomics
			link = new JSONObject();
			link.put("name", "Transcriptomics");
			link.put("link", URL_TRANSCRIPTOMICS_TAB.replace("{cType}", "genome").replace("{cId}", String.format("%d", gid)).replace("{kw}", ""));
			links.add(link);

			genome.put("links", links);
			list.add(genome);
		}
		if (list.size() > 0) {
			jsonData = new JSONObject();
			jsonData.put("popularList", list);
			jsonData.put("popularTitle", "Select Genomes");
		}
		return jsonData;
	}

	private JSONObject getPopularGenomesForPathways() {

		JSONObject jsonData = null;
		JSONArray list = new JSONArray();
		DBPathways connPW = new DBPathways();
		SolrInterface solr = new SolrInterface();

		HashMap<String, String> sort = new HashMap<String, String>();
		sort.put("field", "ec_count");
		sort.put("direction", "DESC");

		for (Integer gid : REFERENCE_GENOME_IDS) {
			HashMap<String, String> key = new HashMap<String, String>();
			key.put("genomeId", gid.toString());
			key.put("algorithm", "RAST");

			ResultType skey = new ResultType();
			JSONObject res = null;
			JSONObject genomeInfo = null;

			// construct genome
			JSONObject genome = new JSONObject();

			// Genome Name
			try {
				skey.put("keyword", "genome_info_id:" + gid);
				solr.setCurrentInstance("GenomeFinder");
				res = solr.getData(skey, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				genomeInfo = (JSONObject) docs.get(0);
				genome.put("popularName", genomeInfo.get("genome_name"));
				genome.put("link", URL_PATHWAY_TAB.replace("{cType}", "genome").replace("{cId}", gid.toString()).replace("{pId}", ""));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			JSONArray data = new JSONArray();

			ArrayList<ResultType> items = connPW.getCompPathwayPathwayList(key, sort, 0, 5);
			for (ResultType item : items) {
				JSONObject pathway = new JSONObject();

				pathway.put("name", item.get("pathway_name"));
				pathway.put("name_link",
						URL_PATHWAY_TAB.replace("{cType}", "genome").replace("{cId}", gid.toString()).replace("{pId}", item.get("pathway_id")));
				pathway.put("class", item.get("pathway_class"));
				pathway.put("gene_count", item.get("gene_count"));
				pathway.put("gene_link", URL_PATHWAY_GENE_TAB.replace("{cId}", gid.toString()).replace("{pId}", item.get("pathway_id")));
				pathway.put("ec_count", item.get("ec_count"));
				pathway.put("ec_link", URL_PATHWAY_EC_TAB.replace("{cId}", gid.toString()).replace("{pId}", item.get("pathway_id")));

				data.add(pathway);
			}
			genome.put("popularData", data);
			list.add(genome);
		}
		if (list.size() > 0) {
			jsonData = new JSONObject();
			jsonData.put("popularList", list);
			jsonData.put("popularTitle", "Select Genomes");
		}
		return jsonData;
	}

	private JSONObject getPopularGenomesForTranscriptomics() {
		JSONObject jsonData = null;
		JSONArray list = new JSONArray();
		SolrInterface solr = new SolrInterface();
		DBTranscriptomics conn_transcriptopics = new DBTranscriptomics();

		for (Integer gid : REFERENCE_GENOME_IDS) {
			ResultType key = new ResultType();
			JSONObject res = null;

			// construct genome
			JSONObject genome = new JSONObject();

			// Genome Name
			try {
				key.put("keyword", "genome_info_id:" + gid);
				solr.setCurrentInstance("GenomeFinder");
				res = solr.getData(key, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				JSONObject genomeInfo = (JSONObject) docs.get(0);
				genome.put("popularName", genomeInfo.get("genome_name"));
				genome.put("link", URL_TRANSCRIPTOMICS_TAB.replace("{cType}", "genome").replace("{cId}", gid.toString()).replace("{kw}", ""));
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			// Retrieve EId associated a given genome
			ArrayList<String> eids = conn_transcriptopics.getEIDsFromGenomeID(gid.toString());

			JSONObject gm = new JSONObject();
			JSONObject ec = new JSONObject();
			gm.put("title", "Gene Modifications");
			ec.put("title", "Experiment Conditions");
			JSONArray data = new JSONArray();
			try {
				solr.setCurrentInstance("GENEXP_Experiment");
				key.put("filter", String.format("eid:(%s)", StringUtils.join(eids.toArray(), " OR ")));

				// top 5 gene modifications
				key.put("keyword", "*");
				res = solr.getData(key, null, "{\"facet\":\"mutant,condition\"}", 0, 0, true, false, false);

				JSONObject facets = (JSONObject) res.get("facets");
				JSONObject mutants = (JSONObject) facets.get("mutant");
				JSONObject conditions = (JSONObject) facets.get("condition");

				JSONArray attrs = (JSONArray) mutants.get("attributes");

				int i = 0;
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;

					JSONObject mutant = new JSONObject();
					mutant.put("label", j.get("value"));
					mutant.put("value", j.get("count"));
					data.add(mutant);
					i++;
					if (i > 4) {
						break;
					}
				}
				gm.put("data", data);

				// top 5 experiment conditoins
				data = new JSONArray();
				attrs = (JSONArray) conditions.get("attributes");
				i = 0;
				for (Object attr : attrs) {
					JSONObject j = (JSONObject) attr;

					JSONObject mutant = new JSONObject();
					mutant.put("label", j.get("value"));
					mutant.put("value", j.get("count"));
					data.add(mutant);
					i++;
					if (i > 4) {
						break;
					}
				}
				ec.put("data", data);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			genome.put("GeneModifications", gm);
			genome.put("ExperimentConditions", ec);

			list.add(genome);
		}
		if (list.size() > 0) {
			jsonData = new JSONObject();
			jsonData.put("popularList", list);
			jsonData.put("popularTitle", "Select Genomes");
		}
		return jsonData;
	}

	private JSONObject getPathwayECDist() {
		JSONObject jsonData = null;
		JSONArray series = new JSONArray();

		DBPathways conn = new DBPathways();
		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GlobalTaxonomy");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ResultType key = new ResultType();
		JSONObject res = null;
		JSONObject tx = null;

		for (Integer txId : GENUS_TAXON_IDS) {

			ArrayList<Integer> dist = conn.getDistECConservation("taxon", txId.toString());

			try {
				key.put("keyword", "taxon_id:" + txId);
				res = solr.getData(key, null, null, 0, 1, false, false, false);
				JSONArray docs = (JSONArray) ((JSONObject) res.get("response")).get("docs");
				tx = (JSONObject) docs.get(0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject item = new JSONObject();
			item.put("pathogen", tx.get("taxon_name"));

			int total = 0;
			for (Integer v : dist) {
				total += v;
			}
			item.put("total", total);
			// Integer[] d = (Integer[]) dist.toArray();
			JSONArray jDist = new JSONArray();
			jDist.addAll(dist);
			item.put("dist", jDist);

			series.add(item);
		}

		jsonData = new JSONObject();
		jsonData.put("chart_title", "Pathway Conservation in Pathogenic Bacteria");
		jsonData.put("chart_desc", "Across 22 Genera");
		jsonData.put("data", series);

		return jsonData;
	}

	private JSONObject read(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(url);
		JSONObject jsonData = null;

		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httpRequest, responseHandler);

			JSONParser parser = new JSONParser();
			jsonData = (JSONObject) parser.parse(response);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		finally {
			httpclient.getConnectionManager().shutdown();
		}
		return jsonData;
	}
}
