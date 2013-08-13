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
package edu.vt.vbi.patric.cache;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
//import java.text.SimpleDateFormat;
//import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.FacetParams;
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

	String baseURL = "http://patricenews-dev.vbi.vt.edu";

	// String baseURL = "http://patricenews-test.vbi.vt.edu";
	// String baseURL = "http://enews.patricbrc.org";

	public void setBaseURL(String url) {
		baseURL = url;
	}

	public boolean createCacheFileGenomes(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();

		// genomes
		JSONObject data = read(baseURL + "/tab/5161-genomes/?req=passphrase");
		if (data != null) {
			jsonData.put("genomes", data);
		}
		// topline
		data = read(baseURL + "/tab/genomes-data-topline/?req=passphrase");
		if (data != null) {
			jsonData.put("topline", data);
		}
		// tools
		data = read(baseURL + "/tab/genome-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/genome-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/generic-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}
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
			//jsonOut.println(jsonData.toJSONString());
			jsonData.writeJSONString(jsonOut);
			jsonOut.close();
			isSuccess = true;
		}
		catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return isSuccess;
	}

	public boolean createCacheFileFigfam(String filePath) {
		boolean isSuccess = false;
		JSONObject jsonData = new JSONObject();

		// protein families
		JSONObject data = read(baseURL + "/tab/12045-protein-families/?req=passphrase");
		if (data != null) {
			jsonData.put("proteinfamilies", data);
		}
		// tools
		data = read(baseURL + "/tab/genome-tools/?req=passphrase");
		if (data != null) {
			jsonData.put("tools", data);
		}
		// process
		data = read(baseURL + "/tab/genome-process/?req=passphrase");
		if (data != null) {
			jsonData.put("process", data);
		}
		// download
		data = read(baseURL + "/tab/generic-download/?req=passphrase");
		if (data != null) {
			jsonData.put("download", data);
		}
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

	private JSONObject getProteinFamilies() {
		JSONObject jsonData = null;
		JSONArray series = new JSONArray();
		ArrayList<Integer> listIDs = new ArrayList<Integer>();

		listIDs.addAll(Arrays.asList(new Integer[] { 1386, 773, 138, 234, 32008, 194, 83553, 1485, 776, 943, 561, 262,
				209, 1637, 1763, 780, 590, 620, 1279, 1301, 662, 629 }));
		// listIDs.addAll(Arrays.asList(new Integer[] {561,209}));

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

		for (Integer txId : listIDs) {
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
			finally {

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
		ArrayList<Integer> listIDs = new ArrayList<Integer>();
		DBSummary conn = new DBSummary();
		DBShared shared = new DBShared();
		ArrayList<ResultType> dist = null;

		listIDs.addAll(Arrays.asList(new Integer[] {561,209,1637,1763,780,590,620,1279,1301}));
		//listIDs.addAll(Arrays.asList(new Integer[] { 561, 209 }));

		for (Integer txId : listIDs) {
			dist = conn.getFIGFamConservDist(txId);
			ResultType name = shared.getNamesFromTaxonId(txId);
			JSONArray data = new JSONArray();
			for (ResultType pt : dist) {
				JSONObject o = new JSONObject();
				o.put("x", pt.get("grp") + "0%");
				o.put("y", pt.get("cnt"));
				data.add(o);
			}
			JSONObject item = new JSONObject();
			item.put("link", "/portal/portal/patric/FIGfamSorterB?cType=taxon&cId=" + txId + "&dm=result");
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
				// DecimalFormat df = new DecimalFormat("#.##");

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
		JSONObject jsonData = null;
		// http://macleod.vbi.vt.edu:8983/solr/genomesummary/select?q=*:*&wt=xml&rows=0&facet=true&facet.mincount=1&facet.range=completion_date&f.completion_date.facet.range.start=1990-01-01T00:00:00Z&f.completion_date.facet.range.end=2020-01-01T00:00:00Z&f.completion_date.facet.range.gap=%2B3YEAR&facet.sort=index

		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GenomeFinder");
			SolrQuery query = new SolrQuery();

			query.setQuery("*:*");
			query.setFacet(true);
			query.addDateRangeFacet("completion_date", solr.getRangeStartDate(), solr.getRangeEndDate(), "+3YEAR");
			// query.addDateRangeFacet("completion_date", new
			// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2006-01-01T00:00:00Z"),
			// solr.getRangeEndDate(), "+1YEAR");
			query.setFacetMinCount(1);
			query.setFacetSort(FacetParams.FACET_SORT_INDEX);
			query.setRows(0);

			JSONObject res = solr.ConverttoJSON(solr.getServer(), query, true, false);
			// System.out.println(res.toJSONString());

			JSONObject facets = (JSONObject) res.get("facets");
			JSONArray cDates = (JSONArray) ((JSONObject) facets.get("completion_date")).get("attributes");
			JSONArray series = new JSONArray();
			Collections.sort(cDates, new SeriesComparator());

			int total = 0;
			for (Object cDate : cDates) {
				JSONObject c = (JSONObject) cDate;
				JSONObject dot = new JSONObject();
				total = total + (int) c.get("count");
				dot.put("year", Integer.parseInt(c.get("value").toString()));
				dot.put("sequenced", c.get("count"));
				dot.put("total", total);
				series.add(dot);
			}

			jsonData = new JSONObject();
			jsonData.put("chart_title", "Number of Bacterial Genomes");
			jsonData.put("data", series);

		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

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
					case "Cattle, Bos taurus":
						icon = "/patric/images/hosts/";
						break;
					case "Bovine":
						icon = "/patric/images/hosts/";
						break;
					case "Cassava, Manihot esculenta":
						icon = "/patric/images/hosts/";
						break;
					case "American bison, Bison bison":
						icon = "/patric/images/hosts/";
						break;
/*					case "cow":
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
						break;*/
					case "USA":
						icon = "/patric/images/flags/United-States.png";
						break;
					default:
						icon = "/patric/images/flags/"+f.get("value").toString()+".png";
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
					jsonData.put("chart_desc", "Top 5 Bacterial Host");
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

	private JSONObject getPopularGenomes() {
		JSONObject jsonData = null;
		JSONArray list = new JSONArray();
		ArrayList<Integer> listGenomeIDs = new ArrayList<Integer>();
		String urlGenomeOverview = "Genome?cType=genome&cId=";
		String urlProteinFamily = "FIGfamSorterB?cType=genome&dm=result&cId=";
		String urlTranscriptomics = "ExperimentList?cType=genome&cId=";
		String urlPathway = "CompPathwayTable?cType=genome&algorithm=PATRIC&ec_number=&cId=";
		String urlFeatureTable = "FeatureTable?cType=genome&featuretype=&annotation=PATRIC&filtertype=&cId=";

		String urlIcon = "/images/icon-popular-genome-sample.png";

		listGenomeIDs.addAll(Arrays.asList(new Integer[] { 87468, 129921, 20916, 31049, 7843, 69206, 116463, 27904,
				92729, 33062 }));
		// listGenomeIDs.addAll(Arrays.asList(new Integer[] { 87468, 129921, 20916 }));

		DBPathways connPW = new DBPathways();
		DBTranscriptomics connTR = new DBTranscriptomics();
		SolrInterface solr = new SolrInterface();

		for (Integer gid : listGenomeIDs) {
			HashMap<String, String> key = new HashMap<String, String>();
			key.put("genomeId", gid.toString());
			key.put("algorithm", "RAST");

			ResultType skey = new ResultType();
			JSONObject res = null;
			JSONObject genomeInfo = null;

			// construct genome
			JSONObject genome = new JSONObject();
			genome.put("link", urlGenomeOverview + gid);

			// Genome Name
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
			//
			JSONArray data = new JSONArray();
			//
			// Protein Family
			JSONObject pf = new JSONObject();
			pf.put("description", "Protein Families");
			pf.put("link", urlProteinFamily + gid);
			pf.put("picture", urlIcon);
			//
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
			//
			// Experiment
			JSONObject tr = new JSONObject();
			tr.put("description", "Transcriptomic Experiments");
			tr.put("link", urlTranscriptomics + gid);
			ArrayList<String> listEId = connTR.getEIDsFromGenomeID(gid.toString());
			tr.put("picture", urlIcon);
			tr.put("data", listEId.size());
			data.add(tr);
			//
			// Pathways
			JSONObject pw = new JSONObject();
			pw.put("description", "Pathways");
			pw.put("link", urlPathway + gid);
			pw.put("picture", urlIcon);
			int cntPathway = connPW.getCompPathwayPathwayCount(key);
			pw.put("data", cntPathway);
			data.add(pw);
			//
			// Features
			JSONObject ft = new JSONObject();
			ft.put("description", "Features");
			ft.put("link", urlFeatureTable + gid);
			ft.put("picture", urlIcon);
			ft.put("data", genomeInfo.get("rast_cds"));
			data.add(ft);
			//
			genome.put("popularData", data);
			//
			list.add(genome);
		}
		if (list.size() > 0) {
			jsonData = new JSONObject();
			jsonData.put("popularList", list);
			jsonData.put("popularTitle", "Popular Genomes Box");
		}
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
