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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.portlet.PortletException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class SolrInterface {

	String type = "";

	String server_staging = "http://macleod.vbi.vt.edu:8983";

	String server_production = "http://macleod.vbi.vt.edu:8080";

	String server_production2 = "http://macleod.vbi.vt.edu:9090";

	String startDate = "1990-01-01T00:00:00";

	String endDate = "2020-01-01T00:00:00";

	String rangeDate = "+1YEAR";

	Date startDateFormat, endDateFormat;

	SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	boolean isProduction = true; // change this to true when deploy to dayhoff

	LBHttpSolrServer server = null;

	public SolrInterface() {
		isProduction = System.getProperty("solr.isProduction", "false").equals("true");
		try {
			startDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(startDate);
			endDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(endDate);

			// reset timezone for short date format
			shortDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		}
		catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}

	public Date getRangeStartDate() {
		return startDateFormat;
	}

	public Date getRangeEndDate() {
		return endDateFormat;
	}

	public String getInstanceURL(String type) {
		if (type.equals("GenomeFinder")) {
			return "/solr/genomesummary";
		}
		else if (type.equals("GenomeSequenceFinder")) {
			return "/solr/sequenceinfo";
		}
		else if (type.equals("GenomicFeature")) {
			return "/solr/dnafeature";
		}
		else if (type.equals("GOSearch")) {
			return "/solr/goterm";
		}
		else if (type.equals("ECSearch")) {
			return "/solr/ecnumber";
		}
		else if (type.equals("GlobalProteinFamilies") || type.equals("FigFamSorter")) {
			return "/solr/figfam";
		}
		else if(type.equals("FigFamDictionary")){
			return "/solr/figfam-dic";
		}
		else if (type.equals("GlobalTaxonomy")) {
			return "/solr/taxonomy";
		}
		else if (type.equals("GENEXP_Experiment")) {
			return "/solr/genexp-experiment";
		}
		else if (type.equals("GENEXP_Sample")) {
			return "/solr/genexp-sample";
		}
		else if (type.equals("GENEXP_Gene")) {
			return "/solr/genexp-gene";
		}
		else if (type.equals("Proteomics_Experiment")) {
			return "/solr/proteomics-experiment";
		}
		else if (type.equals("Proteomics_Protein")) {
			return "/solr/proteomics-protein";
		}
		else if(type.equals("Proteomics_Peptide")){
			return "/solr/proteomics-peptide";
		}
		else {
			return "";
		}
	}

	public LBHttpSolrServer getServer() {
		return server;
	}

	public void setCurrentInstance(String index_type) throws MalformedURLException {

		type = index_type;
		String core = getInstanceURL(index_type);

		if (isProduction) {
			server = new LBHttpSolrServer(server_production + core, server_production2 + core);
		}
		else {
			server = new LBHttpSolrServer(server_staging + core);
		}
	}

	public JSONObject getData(ResultType key, HashMap<String, String> sort, String facets, int start, int end,
			boolean facet, boolean highlight, boolean grouping) throws IOException {

		if (end == -1)
			end = 500000;

		SolrQuery query = new SolrQuery();
		query.setQuery(KeywordReplace(key.get("keyword")));
		if (key.containsKey("filter") && key.get("filter") != null) {
			query.setFilterQueries(key.get("filter"));
		}
		if (key.containsKey("filter2") && key.get("filter2") != null) {
			query.addFilterQuery(key.get("filter2"));
		}
		
		query.setStart(start); // setting starting index
		
		if(end != -1){
			query.setRows(end);
		}

		if (grouping) {
			query.set("group", true);
			query.set("group.field", "pos_group");
			query.set("group.sort", "annotation_sort asc");
			query.set("group.ngroups", "true");
			if (facet)
				query.set("group.truncate", "true");
		}

		if (facet) {
			query.setFacet(true);
			query.setFacetMinCount(1);
			query.setFacetLimit(-1);
			query.setFacetSort("count");

			if (!type.equals("FigFamSorter")) {

				JSONObject facet_data = null;

				try {
					facet_data = (JSONObject) new JSONParser().parse(facets);
				}
				catch (ParseException e2) {
					e2.printStackTrace();
				}

				String[] ff = facet_data.get("facet").toString().split(",");

				for (int i = 0; i < ff.length; i++)
					if (!ff[i].equals("completion_date") && !ff[i].equals("release_date"))
						query.addFacetField(ff[i]);
					else
						query.addDateRangeFacet(ff[i], startDateFormat, endDateFormat, rangeDate);
			}
		}

		if (sort != null && sort.get("field") != null && sort.get("field") != "") {

			String[] sort_field = sort.get("field").split(",");
			String s = "";
			s += sort_field[0] + ((sort.get("direction").equalsIgnoreCase("asc")) ? " asc" : " desc");

			for (int i = 1; i < sort_field.length; i++) {
				s += ", " + sort_field[i] + ((sort.get("direction").equalsIgnoreCase("asc")) ? " asc" : " desc");
			}
			query.set("sort", s);
		}

		if (highlight) {
			query.set("hl", "on");
			query.set("hl.fl", "*");
		}
		
		if(key.containsKey("fields") && !key.get("fields").toString().equals("")){
			query.addField(key.get("fields").toString());
		}

		System.out.println("Request: " + query.toString());

		return ConverttoJSON(server, query, facet, highlight);

	}

	public JSONObject getIdsForCart(ResultType key, String field, int rows) {

		SolrQuery query = new SolrQuery();
		query.setQuery(KeywordReplace(key.get("keyword")));

		query.setStart(0); // setting starting index
		query.setRows(rows);
		query.setFields(field);

		return ConverttoJSON(server, query, false, false);

	}

	@SuppressWarnings("rawtypes")
	public JSONObject ConverttoJSON(SolrServer server, SolrQuery query, boolean faceted, boolean highlighted) {

		JSONObject result = new JSONObject();
		long start = 0, end = 0;

		try {
			start = System.currentTimeMillis();
			QueryResponse qr = server.query(query, SolrRequest.METHOD.POST);
			end = System.currentTimeMillis();
			System.out.println("Query time - "+(end-start)+"ms");
			// System.out.println(query.toString()); // un-comment if you want
			// to see the result string
			
			start = System.currentTimeMillis();
			
			SolrDocumentList sdl = new SolrDocumentList();
			GroupResponse groupResponse = qr.getGroupResponse();
			JSONObject response = new JSONObject();
			JSONArray docs = new JSONArray();

			if (type.equals("GenomicFeature") && groupResponse != null) {
				// Read the group results per command
				for (GroupCommand command : groupResponse.getValues()) {
					response.put("numFound", command.getNGroups().intValue());
					for (Group group : command.getValues()) {
						SolrDocumentList docList = group.getResult();
						for (SolrDocument doc : docList) {
							sdl.add(doc);
						}
					}
				}
			}
			else {
				sdl = qr.getResults();
				response.put("numFound", sdl.getNumFound());
			}

			Map<String, Map<String, List<String>>> highlight_id = null;

			if (highlighted) {
				highlight_id = qr.getHighlighting();
			}
			// System.out.println("Search result: "+sdl.getNumFound());
			for (SolrDocument d : sdl) {
				// use Java native data type or JSON
				Map<String, List<String>> highlight_fields = null;

				JSONObject values = new JSONObject();
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();
					if (el.getKey().equals("completion_date") || el.getKey().equals("release_date")) {
						// System.out.println(el.getKey()+":"+el.getValue().toString()+" ("+el.getValue().getClass()+")");
						values.put(el.getKey(), transformDate((Date) el.getValue()));
					}
					else
						values.put(el.getKey(), el.getValue());

					if (el.getKey().equals("rownum")) {
						if (highlighted) {
							// System.out.print(el.getValue());
							if (highlight_id.containsKey(el.getValue().toString())) {
								highlight_fields = highlight_id.get(el.getValue().toString());
								// System.out.print("highlight_fields "+highlight_fields);
							}
						}
					}
				}

				if (highlight_fields != null) {

					Set<?> entries = highlight_fields.entrySet();
					Iterator<?> it = entries.iterator();
					JSONObject highlight_values_json = new JSONObject();

					while (it.hasNext()) {
						Entry<?, ?> entry = (Entry<?, ?>) it.next();
						List<?> highlight_values = (List<String>) entry.getValue();
						String highlight_key = entry.getKey().toString();
						JSONArray highlight_values_array = new JSONArray();

						for (int i = 0; i < highlight_values.size(); i++) {
							highlight_values_array.add(highlight_values.get(i).toString()
									.replace("<em>", "<em class=\"global_em\">"));
						}
						highlight_values_json.put(highlight_key, highlight_values_array);
					}
					values.put("highlight", highlight_values_json);
				}
				docs.add(values);
			}

			// System.out.println(docs.toJSONString());
			response.put("docs", docs);
			result.put("response", response);

			JSONObject facets_json = new JSONObject();

			if (faceted) {
				List<FacetField> facets = qr.getFacetFields();
				for (FacetField facet : facets) {
					JSONObject facet_json = new JSONObject();
					int count = 0;
					JSONArray attributes_json = new JSONArray();
					List<FacetField.Count> facetEntries = facet.getValues();

					if (facet.getValues() != null) {
						for (FacetField.Count fcount : facetEntries) {
							JSONObject attribute_json = new JSONObject();
							attribute_json.put("text",
									fcount.getName() + " <span style=\"color: #888;\"> (" + fcount.getCount()
											+ ") </span>");
							attribute_json.put("value", fcount.getName());
							attribute_json.put("count", fcount.getCount());
							attributes_json.add(attribute_json);

							count += Integer.parseInt(String.valueOf(fcount.getCount()));
						}
					}

					facet_json.put("value", facet.getName());
					facet_json.put("count", count);
					facet_json.put("text", facet.getName() + " <span style=\"color: #888;\"> (" + count + ") </span>");
					facet_json.put("attributes", attributes_json);
					facets_json.put(facet.getName(), facet_json);
				}

				List<RangeFacet> ranges = qr.getFacetRanges();
				for (RangeFacet range : ranges) {
					JSONObject facet_json = new JSONObject();
					int count = 0;
					JSONArray attributes_json = new JSONArray();
					List<RangeFacet.Count> rangeEntries = range.getCounts();

					if (rangeEntries != null) {
						for (RangeFacet.Count fcount : rangeEntries) {
							if (fcount.getCount() > 0) {
								JSONObject attribute_json = new JSONObject();
								attribute_json.put("text", fcount.getValue().split("-")[0]
										+ " <span style=\"color: #888;\"> (" + fcount.getCount() + ") </span>");
								attribute_json.put("value", fcount.getValue().split("-")[0]);
								attribute_json.put("count", fcount.getCount());
								attributes_json.add(attribute_json);
								count += Integer.parseInt(String.valueOf(fcount.getCount()));
							}
						}
					}
					JSONArray tmp = attributes_json;
					for (int i = 0; i < attributes_json.size(); i++) {
						for (int j = 0; j < attributes_json.size(); j++) {
							JSONObject a = (JSONObject) attributes_json.get(i);
							JSONObject b = (JSONObject) attributes_json.get(j);
							if (Integer.parseInt(a.get("count").toString()) > Integer.parseInt(b.get("count")
									.toString())) {
								tmp.set(i, b);
								tmp.set(j, a);
							}
						}
					}

					facet_json.put("value", range.getName());
					facet_json.put("count", count);
					facet_json.put("text", range.getName() + " <span style=\"color: #888;\"> (" + count + ") </span>");
					facet_json.put("attributes", tmp);
					facets_json.put(range.getName(), facet_json);
				}
				result.put("facets", facets_json);
			}
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("Processing time - "+(end-start)+"ms");
		return result;
	}

	public String KeywordReplace(String keyword) {

		keyword = keyword.replaceAll("%20", " ");
		keyword = keyword.replaceAll("%22", "\"");
		keyword = keyword.replaceAll("%27", "'");
		keyword = keyword.replaceAll("%2F", "\\\\/");

		//System.out.println(keyword);

		keyword = StringHelper.parseSolrKeywordOperator(keyword);
		return keyword;
	}

	public JSONObject getSingleFacetsData(String keyword, String single_facet, String[] facets, boolean grouping)
			throws IOException, ParseException {
		return getSingleFacetsData(keyword, single_facet, facets, null, grouping);
	}

	public JSONObject getSingleFacetsData(String keyword, String single_facet, String[] facets, String fq,
			boolean grouping) throws IOException, ParseException {
		keyword = KeywordReplace(keyword);

		// System.out.print("Patric Libs keyword - "+keyword+","+fq);
		// String url = createGenomeFinderFacetURL(key, single_facet);
		int beginindex = keyword.indexOf(" AND (" + single_facet);
		int endindex = 0;

		StringBuffer s = new StringBuffer(keyword);

		// System.out.print("single_facet: "+single_facet);
		// System.out.print("beginindex: "+beginindex);

		if (beginindex < 0) {
			beginindex = keyword.indexOf("(" + single_facet);
			endindex = keyword.indexOf(") AND ", beginindex);
			if (endindex < 0) {
				endindex = keyword.indexOf("))", beginindex);
				System.out.print("endindex: " + endindex);
				// TODO: this cause java.lang.StringIndexOutOfBoundsException:
				// String index out of range: -1
				// when Patric Libs keyword - (*)
				// and endindex: 2
				s.delete(beginindex, endindex + 2);
			}
			else {
				s.delete(beginindex, endindex + 6);
			}
		}
		else {
			endindex = keyword.indexOf("))", beginindex);
			if (endindex == -1) {
				endindex = keyword.indexOf("])", beginindex);
			}
			s.delete(beginindex, endindex + 2);
		}
		System.out.print(s);
		System.out.print("s.length: " + s.length());
		if (s.length() == 0)
			s.append("(*)");

		/*
		 * System.out.print("substring : "+keyword.substring(beginindex,
		 * endindex+1)); System.out.print("new query : "+s.toString());
		 */

		SolrQuery query = new SolrQuery();
		query.setQuery(s.toString());
		if (fq != null) {
			query.setFilterQueries(fq);
		}

		query.setStart(0); // setting starting index
		query.setRows(1);
		query.setFacet(true);
		query.setFacetMinCount(1);

		if (grouping) {
			query.set("group", true);
			query.set("group.field", "pos_group");
			query.set("group.sort", "annotation_sort asc");
			query.set("group.ngroups", "true");
			query.set("group.truncate", "true");
		}

		for (int i = 0; i < facets.length; i++)
			if (!facets[i].equals("completion_date") && !facets[i].equals("release_date"))
				query.addFacetField(facets[i]);
			else
				query.addDateRangeFacet(facets[i], startDateFormat, endDateFormat, rangeDate);

		return ConverttoJSON(server, query, true, false);

	}

	public JSONObject getGenomeTabJSON(String genome_info_id, String solr_instance) throws IOException, ParseException {

		SolrQuery query = new SolrQuery();
		query.setQuery("gid:" + genome_info_id);

		return ConverttoJSON(server, query, false, false);

	}

	public JSONObject getGenomeIDsfromSolr(String keyword, String facets, boolean faceted) throws MalformedURLException {

		SolrQuery query = new SolrQuery();
		query.setQuery(KeywordReplace(keyword));

		if (faceted) {
			if (type.equals("GenomeFinder")) {

				JSONObject facet_data = null;

				try {
					facet_data = (JSONObject) new JSONParser().parse(facets);
				}
				catch (ParseException e2) {
					e2.printStackTrace();
				}

				query.setFacet(true);
				query.setFacetMinCount(1);
				String[] ff = facet_data.get("facet").toString().split(",");

				for (int i = 0; i < ff.length; i++)
					if (!ff[i].equals("completion_date") && !ff[i].equals("release_date"))
						query.addFacetField(ff[i]);
					else
						query.addDateRangeFacet(ff[i], startDateFormat, endDateFormat, rangeDate);
			}
		}

		query.setRows(100000);

		return ConverttoJSON(server, query, faceted, false);
	}

	public JSONObject getSummaryforGlobalSearch(String keyword) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery(KeywordReplace(keyword));
		query.setRows(3);
		query.set("hl", "on");
		query.set("hl.fl", "*");

		if (type.equals("GenomicFeature")) {
			query.set("group", true);
			query.set("group.field", "pos_group");
			query.set("group.sort", "annotation_sort asc");
			query.set("group.ngroups", "true");
		}

		// System.out.println("Global Summary Search : "+query.toString());
		return ConverttoJSON(server, query, false, true);

	}

	public JSONObject getSpellCheckerResult(String keyword) throws SolrServerException, MalformedURLException {

		SolrQuery query = new SolrQuery();
		query.setQuery(KeywordReplace(keyword));
		query.setRows(0);
		query.set("spellcheck.q", KeywordReplace(keyword));
		query.set("spellcheck", "true");
		query.set("spellcheck.collate", "true");
		query.set("spellcheck.onlyMorePopular", "true");
		query.set("spellcheck.extendedResults", "true");

		JSONObject result = new JSONObject();
		boolean flag = true;
		boolean spellflag = false;

		for (int i = 0; i < 2 && flag; i++) {

			if (i == 0) {
				this.setCurrentInstance("GenomicFeature");
			}
			else if (i == 1) {
				this.setCurrentInstance("GenomeFinder");
			}else if (i == 2) {
				this.setCurrentInstance("GlobalTaxonomy");
			}else if (i == 3) {
				this.setCurrentInstance("GENEXP_Experiment");
			}
			System.out.println(query);
			QueryResponse qr = server.query(query, SolrRequest.METHOD.POST);
			SpellCheckResponse spellCheckRes = qr.getSpellCheckResponse();
			SolrDocumentList sdl = qr.getResults();
			spellflag = spellCheckRes.getCollatedResult() != null ? true : false;

			if (spellflag) {
				result.put("suggestion", spellCheckRes.getCollatedResult());
				flag = false;
			}
			else {
				if (sdl.getNumFound() > 0) {
					if (result.containsKey("suggestion")) {
						result.remove("suggestion");
					}
					flag = false;
				}
			}
		}

		// hypotetical - suggestion + numFound > 0 + get alternativKW
		// south koree - suggestion + numFound = 0 + continue

		return result;
	}

	/**
	 * @param need
	 * @param facet_fields
	 * @param facet
	 * @param tree_state
	 * @param completion_counts
	 * @return
	 */
	public JSONArray processStateAndTree(ResultType key, String need, JSONObject facet_fields, String facet,
			String state, int limit, boolean grouping) throws PortletException, IOException {

		JSONObject facet_data = null;

		try {
			facet_data = (JSONObject) new JSONParser().parse(facet);
		}
		catch (ParseException e2) {
			e2.printStackTrace();
		}

		/*
		 * System.out.print(facet_data.toString());
		 * System.out.print(facet_data.get("facet").toString());
		 */
		String[] a = facet_data.get("facet").toString().split(",");
		String[] a_text = facet_data.get("facet_text").toString().split(",");

		JSONObject state_object = null;

		try {
			if (state != "")
				state_object = (JSONObject) new JSONParser().parse(state);
		}
		catch (ParseException e1) {
			e1.printStackTrace();
		}

		JSONArray x = new JSONArray();

		for (int i = 0; i < a.length; i++) {

			JSONObject f = null;
			JSONObject sent = null;

			if (state == "" || state_object.get(a[i]) == null) {
				sent = (JSONObject) facet_fields.get(a[i]);
				f = createNode(sent, i, need, false, "", limit, a_text[i]);
			}
			else {
				if (state_object != null && state_object.get(a[i]) != null) {
					JSONObject object = null;

					try {
						object = getSingleFacetsData(key.get("keyword"), a[i], a, grouping);
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					System.out.print("ai " + a[i]);
					JSONObject obj = (JSONObject) object.get("facets");
					sent = (JSONObject) obj.get(a[i]);
					f = createNode(sent, i, "tree", true, key.get("keyword"), limit, a_text[i]);
				}
			}
			x.add(f);
		}
		return x;
	}

	public JSONObject createNode(JSONObject sent, int i, String need, boolean clear, String keyword, int limit,
			String facet_text) {

		JSONArray arrsub = (JSONArray) sent.get("attributes");

		// int count = 0;
		JSONArray arr_more_children = new JSONArray();
		JSONArray arr_return = new JSONArray();

		JSONObject f = new JSONObject();

		String ai = sent.get("value").toString();
		f.put("id", ai);
		f.put("renderstep", "1");
		f.put("leaf", false);

		if (clear) {

			f.put("checked", true);
			JSONObject clear_object = new JSONObject();

			clear_object.put("id", ai + "_clear");
			clear_object.put("parentID", ai);
			clear_object.put("leaf", true);
			clear_object.put("text", "<b>clear</b>");
			clear_object.put("checked", false);
			clear_object.put("renderstep", "3");

			arr_return.add(clear_object);
		}

		for (int j = 0; j < arrsub.size(); j++) {

			JSONObject object = (JSONObject) arrsub.get(j);
			JSONObject temp = new JSONObject();

			temp.put("renderstep", "2");
			temp.put("text", object.get("text").toString());
			temp.put("count", object.get("count").toString());
			temp.put("id", object.get("value") + "##" + ai);

			if (j < limit) {
				temp.put("parentID", ai);
				arr_return.add(temp);
			}
			else if (j >= limit) {
				temp.put("parentID", ai + "_more");
				arr_more_children.add(temp);
			}
			if (clear) {

				String[] split = keyword.split(ai + ":");

				for (int sp = 1; sp < split.length; sp++) {
					int endindex = split[sp].indexOf(")");
					String lookup = "";

					if (ai.equals("completion_date") || ai.equals("release_date"))
						lookup = split[sp].substring(2, endindex);
					else
						lookup = split[sp].substring(1, endindex);

					// System.out.print("lookup : "+ lookup);
					// System.out.print("lookup : "+ lookup.indexOf(" OR "));

					if (lookup.indexOf(" OR ") > 0) {
						String[] lookup_arr = lookup.split(" OR ");
						for (int k = 0; k < lookup_arr.length; k++) {
							if (ai.equals("completion_date") || ai.equals("release_date")) {
								lookup_arr[k] = lookup_arr[k].split("-")[0];
								if (k > 0)
									lookup_arr[k] = lookup_arr[k].split("\\[")[1];
							}
							// System.out.print("lookup_arr[k] : "+
							// lookup_arr[k]);
							if (lookup_arr[k].equals(object.get("value").toString())
									|| lookup_arr[k].equals("\"" + object.get("value").toString() + "\"")) {
								temp.put("checked", true);
								break;
							}
						}
					}
					else {
						if (ai.equals("completion_date") || ai.equals("release_date"))
							lookup = lookup.split("-")[0];

						if (lookup.equals(object.get("value").toString())
								|| lookup.equals("\"" + object.get("value").toString() + "\"") || lookup.equals("*")
								|| lookup.equals("* TO *]")) {
							temp.put("checked", true);
							break;
						}
					}
				}
			}
		}

		if (arrsub.size() > limit) {

			JSONObject temp = new JSONObject();
			temp.put("parentID", ai + "_more");
			temp.put("id", ai + "_less");
			temp.put("text", " <b>less</b>");
			temp.put("leaf", true);
			temp.put("renderstep", "3");
			arr_more_children.add(temp);

			JSONObject more = new JSONObject();
			more.put("id", ai + "_more");
			more.put("leaf", false);
			more.put("renderstep", "3");
			more.put("children", arr_more_children);
			more.put("text", "<b>more</b>");
			arr_return.add(more);
		}

		f.put("expanded", true);
		f.put("children", arr_return);
		f.put("count", sent.get("count"));

		if (need.equals("tree")) {
			f.put("text", "<span style=\"color: #CC6600; margin: 0; padding: 0 0 2px; font-weight: bold;\">"
					+ facet_text + "</span><span style=\"color: #888;\"> (" + sent.get("count").toString() + ")</span>");
		}
		else {
			f.put("text", facet_text + " <b>(" + sent.get("count").toString() + ")</b>");
		}
		return f;
	}

	public String ConstructSequenceFinderKeyword(String gid) {

		String keyword = "";

		if (gid.contains(",")) {
			String[] gids = gid.split(",");
			if (gids.length > 0) {
				keyword += "(gid:(" + gids[0];
				for (int i = 1; i < gids.length; i++) {
					keyword += " OR " + gids[i];
				}
				keyword += "))";
			}
		}
		else {
			keyword = "(gid:" + gid + ")";
		}
		return keyword;
	}

	public String ConstructKeyword(String field_name, String id) {
		String keyword = "";
		if (id.contains(",")) {
			String[] ids = id.split(",");
			keyword += "(" + field_name + ":" + ids[0];
			for (int i = 1; i < ids.length; i++) {
				keyword += " OR " + field_name + ":" + ids[i];
			}
			keyword += ")";
		}
		else {
			keyword = "(" + field_name + ":" + id + ")";
		}
		return keyword;
	}

	public String getGenomeIdsfromSolrOutput(JSONObject solr_output) {

		String solrId = "";
		try {
			JSONObject obj = (JSONObject) solr_output.get("response");
			JSONArray obj1 = (JSONArray) obj.get("docs");
			JSONObject a = (JSONObject) obj1.get(0);

			if (a.containsKey("genome_info_id"))
				solrId = a.get("genome_info_id").toString();

			for (int i = 1; i < obj1.size(); i++) {
				a = (JSONObject) obj1.get(i);
				if (a.containsKey("genome_info_id")) {
					solrId += "," + a.get("genome_info_id").toString();
				}
			}
		}
		catch (Exception ex) {
			System.out.println("error getSolrIds" + ex.toString());
		}

		return solrId;
	}
	
	/**
	 * Retrieve transcriptomics comparison table from Solr with given experiment id(s) and sample id(s)
	 * @author Oral Dalay
	 * @param experiment Ids or/and Comparison Ids
	 * @return JSONObject
	 * @throws MalformedURLException
	 */
	
	public JSONArray getTranscriptomicsSamples(String sampleId, String expId, String fields) throws MalformedURLException {
		JSONObject res = new JSONObject();
		String query = "";
		
		System.out.println(expId);
		System.out.println(sampleId);
		
		if(expId != null && !expId.equals("")){
			query += "eid:("+expId.replaceAll(",", " OR ")+")";
		}
		
		if(sampleId != null && !sampleId.equals("")){
			if(query.length() > 0){
				query += " AND ";
			}
			query += "pid:("+sampleId.replaceAll(",", " OR ")+")";
		}
		
		ResultType key = new ResultType();
		key.put("keyword", query);
		if(!fields.equals(""))
			key.put("fields", fields);
		
		try {
			this.setCurrentInstance("GENEXP_Sample");
			res = this.getData(key, null, null, 0, -1, false, false, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println((JSONObject)res.get("response"));
		return (JSONArray)((JSONObject)res.get("response")).get("docs");
	}
	
	
	/**
	 * Retrieve transcriptomics genes from Solr with given experiment id(s) and sample id(s)
	 * @author Oral Dalay
	 * @param experiment Ids or/and Comparison Ids
	 * @return JSONObject
	 * @throws MalformedURLException
	 */
	
	public JSONArray getTranscriptomicsGenes(String sampleId, String expId) throws MalformedURLException {
		JSONObject res = new JSONObject();
		String query = "";
		
		System.out.println(expId);
		System.out.println(sampleId);

		
		if(expId != null && !expId.equals("")){
			query += "eid:("+expId.replaceAll(",", " OR ")+")";
		}
		
		if(sampleId != null && !sampleId.equals("")){
			if(query.length() > 0){
				query += " AND ";
			}
			query += "pid:("+sampleId.replaceAll(",", " OR ")+")";
		}
		
		ResultType key = new ResultType();
		key.put("keyword", query);
		key.put("fields", "pid,refseq_locus_tag,na_feature_id,log_ratio,z_score");
		
		try {
			this.setCurrentInstance("GENEXP_Gene");
			res = this.getData(key, null, null, 0, -1, false, false, false);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println((JSONObject)res.get("response"));
		return (JSONArray)((JSONObject)res.get("response")).get("docs");
	}
	

	/**
	 * Retrieve genomic features from Solr with given na_feature_id(s)
	 * @author Harry Yoo
	 * @param key HashMap of search keys.
	 * @return JSONObject
	 * @throws MalformedURLException
	 */
	public JSONObject getFeaturesByID(HashMap<?, ?> key) throws MalformedURLException {

		JSONObject res = new JSONObject();
		int queryCount = 0;
		long start, end;

		StringBuffer queryParam = new StringBuffer();
		String startParam = null;
		if (key.containsKey("startParam") && key.get("startParam") != null) {
			startParam = key.get("startParam").toString();
		}
		String limitParam = null;
		if (key.containsKey("limitParam") && key.get("limitParam") != null) {
			limitParam = key.get("limitParam").toString();
		}
		String sortParam = null;
		if (key.containsKey("sortParam") && key.get("sortParam") != null) {
			sortParam = key.get("sortParam").toString();
		}
		else {
			sortParam = "[{\"property\":\"locus_tag\",\"direction\":\"ASC\"}]";
		}

		// building query parameter string
		queryParam.append("(");

		JSONArray tracks = null;
		if (key.containsKey("tracks")) {
			tracks = (JSONArray) key.get("tracks");
			if (tracks.size() > 0) {
				queryParam.append("na_feature_id:(");
				for (int i = 0; i < tracks.size(); i++) {
					JSONObject tr = (JSONObject) tracks.get(i);
					if (tr.get("trackType").toString().equals("Feature")
							&& tr.get("internalId").toString().equals("") == false) {
						if (i > 0) {
							queryParam.append(" OR ");
						}
						queryParam.append(tr.get("internalId").toString());
						queryCount++;
					}
				}
				queryParam.append(")");
			}
		}
		else if (key.containsKey("na_feature_id")) {
			queryParam.append("na_feature_id:" + key.get("na_feature_id").toString());
			queryCount++;
		}
		else if (key.containsKey("na_feature_ids")) {
			queryParam.append("na_feature_id:(");
			boolean isFirst = true;
			for (String fid : key.get("na_feature_ids").toString().split(",")) {
				if (fid.equals("") == false) {
					if (isFirst) {
						isFirst = false;
					}
					else {
						queryParam.append(" OR ");
					}
					queryParam.append(fid);
					queryCount++;
				}
			}
			queryParam.append(")");
		}
		queryParam.append(")");

		// query to Solr
		if (queryCount > 0) {
			start = System.currentTimeMillis();
			this.setCurrentInstance("GenomicFeature");
			res = this.querySolr(queryParam.toString(), startParam, limitParam, sortParam, queryCount);
			end = System.currentTimeMillis();
			System.out.println("Query time for getting feature attributes - "+(end-start));
		}
		return res;
	}

	/**
	 * Retrieve genome info from Solr with given genome_info_id(s) or tracks
	 * info
	 * @author Harry Yoo
	 * @param key HashMap of search keys
	 * @return
	 * @throws MalformedURLException
	 */
	public JSONObject getGenomesByID(HashMap<?, ?> key) throws MalformedURLException {
		JSONObject res = new JSONObject();
		int queryCount = 0;

		StringBuffer queryParam = new StringBuffer();
		String startParam = null;
		if (key.containsKey("startParam") && key.get("startParam") != null) {
			startParam = key.get("startParam").toString();
		}
		String limitParam = null;
		if (key.containsKey("limitParam") && key.get("limitParam") != null) {
			limitParam = key.get("limitParam").toString();
		}
		String sortParam = null;
		if (key.containsKey("sortParam") && key.get("sortParam") != null) {
			sortParam = key.get("sortParam").toString();
		}

		// build query parameter string
		queryParam.append("(");
		JSONArray tracks = null;
		if (key.containsKey("tracks")) {
			tracks = (JSONArray) key.get("tracks");
			if (tracks.size() > 0) {
				queryParam.append("gid:(");
				for (int i = 0; i < tracks.size(); i++) {
					JSONObject tr = (JSONObject) tracks.get(i);
					if (tr.get("trackType").toString().equals("Genome")
							&& tr.get("internalId").toString().equals("") == false) {
						if (i > 0) {
							queryParam.append(" OR ");
						}
						queryParam.append(tr.get("internalId").toString());
						queryCount++;
					}
				}
				queryParam.append(")");
			}
		}
		else if (key.containsKey("genome_info_id")) {
			queryParam.append("gid:" + key.get("genome_info_id").toString());
			queryCount++;
		}
		else if (key.containsKey("genome_info_ids")) {

			if (key.get("genome_info_ids") != null && key.get("genome_info_ids").equals("") == false) {
				queryParam.append("gid:(");
				boolean isFirst = true;
				for (String gid : key.get("genome_info_ids").toString().split(",")) {
					if (gid.equals("") == false) {
						if (isFirst) {
							isFirst = false;
						}
						else {
							queryParam.append(" OR ");
						}
						queryParam.append(gid);
						queryCount++;
					}
				}
			}
		}
		queryParam.append(")");

		// query to Solr
		if (queryCount > 0) {
			this.setCurrentInstance("GenomeFinder");
			res = this.querySolr(queryParam.toString(), startParam, limitParam, sortParam, queryCount);
		}

		return res;
	}

	public JSONObject getExperimentsByID(HashMap<?, ?> key) throws MalformedURLException {
		JSONObject res = new JSONObject();
		int queryCount = 0;

		StringBuffer queryParam = new StringBuffer();
		String startParam = null;
		if (key.containsKey("startParam") && key.get("startParam") != null) {
			startParam = key.get("startParam").toString();
		}
		String limitParam = null;
		if (key.containsKey("limitParam") && key.get("limitParam") != null) {
			limitParam = key.get("limitParam").toString();
		}
		String sortParam = null;
		if (key.containsKey("sortParam") && key.get("sortParam") != null) {
			sortParam = key.get("sortParam").toString();
		}

		// build query parameter string
		queryParam.append("(");
		JSONArray tracks = null;
		if (key.containsKey("tracks")) {
			tracks = (JSONArray) key.get("tracks");

			if (tracks.size() > 0) {
				queryParam.append("expid:(");
				for (int i = 0; i < tracks.size(); i++) {
					JSONObject tr = (JSONObject) tracks.get(i);
					if (tr.get("trackType").toString().equals("ExpressionExperiment")
							&& tr.get("internalId").toString().equals("") == false) {
						if (i > 0) {
							queryParam.append(" OR ");
						}
						queryParam.append(tr.get("internalId").toString());
						queryCount++;
					}
				}
				queryParam.append(")");
			}
		}
		queryParam.append(")");

		// query to Solr
		if (queryCount > 0) {
			this.setCurrentInstance("GENEXP_Experiment");
			res = this.querySolr(queryParam.toString(), startParam, limitParam, sortParam, queryCount);
		}

		return res;
	}

	/**
	 * Submit query to Solr and get result back
	 * @author Harry Yoo
	 * @param queryParam
	 * @param startParam
	 * @param limitParam
	 * @param sortParam
	 * @param queryCount JSONObject
	 * <dl>
	 * <dd><code>total</code> - total number of rows found</dd>
	 * <dd><code>results</code> - JSONArray of rows in JSONObject format</dd>
	 * </dl>
	 * @return
	 */
	public JSONObject querySolr(String queryParam, String startParam, String limitParam, String sortParam,
			int queryCount) {
		JSONObject res = new JSONObject();
		SolrQuery query = new SolrQuery();

		query.setQuery(queryParam);
		if (startParam != null) {
			query.setStart(Integer.parseInt(startParam));
		}
		if (limitParam != null) {
			query.setRows(Integer.parseInt(limitParam));
		}
		else {
			query.setRows(queryCount);
		}

		if (sortParam != null) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonSort = (JSONObject) ((JSONArray) parser.parse(sortParam)).get(0);
				query.setSort(jsonSort.get("property").toString(),
						SolrQuery.ORDER.valueOf(jsonSort.get("direction").toString().toLowerCase()));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		try {
			QueryResponse qr = server.query(query, SolrRequest.METHOD.POST);
			SolrDocumentList sdl = qr.getResults();
			JSONArray docs = new JSONArray();

			for (SolrDocument d : sdl) {
				JSONObject values = new JSONObject();
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();

					if (el.getKey().equals("release_date") || el.getKey().equals("completion_date")) {
						values.put(el.getKey(), transformDate((Date) el.getValue()));
					}
					else {
						values.put(el.getKey(), el.getValue());
					}
				}
				docs.add(values);
			}
			res.put("total", sdl.getNumFound());
			res.put("results", docs);
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}

		return res;
	}

	public JSONObject queryFacet(String queryParam, String facet) {
		JSONObject res = new JSONObject();
		SolrQuery query = new SolrQuery();

		query.setQuery(queryParam);
		query.setFacet(true);
		query.addFacetField(facet);
		query.setFacetLimit(-1);
		query.setFacetSort("count");
		try {
			QueryResponse qr = server.query(query);
			// skip passing records
			SolrDocumentList sdl = qr.getResults();

			// get facet list and counts
			FacetField ff = qr.getFacetField(facet);
			// System.out.println(ff.toString());

			JSONArray facetfield = new JSONArray();
			List<FacetField.Count> facetEntries = ff.getValues();

			if (ff.getValues() != null) {

				for (FacetField.Count fcount : facetEntries) {
					JSONObject attr = new JSONObject();
					attr.put("value", fcount.getName());
					attr.put("count", fcount.getCount());
					facetfield.add(attr);
				}
			}
			// System.out.println(attributes_json.toJSONString());

			res.put("facet", facetfield);

			//
			res.put("total", sdl.getNumFound());

		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}

		return res;
	}

	public JSONArray searchSolrRecords(String queryParam) {
		return searchSolrRecords(queryParam, null);
	}

	public JSONArray searchSolrRecords(String queryParam, HashMap<?, ?> options) {

		JSONArray docs = new JSONArray();
		SolrQuery query = new SolrQuery();
		query.setQuery(queryParam);

		// options
		if (options != null && options.containsKey("sort")) {
			String sortParam = options.get("sort").toString();
			try {
				JSONParser parser = new JSONParser();
				JSONObject jsonSort = (JSONObject) ((JSONArray) parser.parse(sortParam)).get(0);
				query.setSort(jsonSort.get("property").toString(),
						SolrQuery.ORDER.valueOf(jsonSort.get("direction").toString().toLowerCase()));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		try {
			QueryResponse qr = server.query(query);
			SolrDocumentList sdl = qr.getResults();

			for (SolrDocument d : sdl) {
				JSONObject r = new JSONObject();
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();

					if (el.getKey().equals("release_date")) {
						r.put(el.getKey(), transformDate((Date) el.getValue()));
					}
					else {
						r.put(el.getKey(), el.getValue());
					}
				}
				docs.add(r);
			}
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}

		return docs;
	}

	public String transformDate(Date solrDate) {
		String transformedDate = null;
		transformedDate = shortDateFormat.format(solrDate).toString();

		return transformedDate;
	}

	public JSONObject getPATRICFeature(String na_feature_id) {
		JSONObject pf = new JSONObject();

		try {
			this.setCurrentInstance("GenomicFeature");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		JSONArray res = this.searchSolrRecords("na_feature_id:" + na_feature_id);
		JSONObject f = new JSONObject();

		if (!res.isEmpty()) {
			f = (JSONObject) res.get(0);

			if (f.get("annotation").equals("PATRIC")) {
				// if this is PATRIC
				pf = f;
			}
			else {
				// get corresponding PARIC
				JSONArray res2 = this.searchSolrRecords("pos_group:\"" + f.get("pos_group").toString()
						+ "\"+feature_type:\"" + f.get("feature_type").toString() + "\"+annotation:PATRIC");
				if (!res2.isEmpty()) {
					// found PATRIC
					pf = (JSONObject) res2.get(0);
				}
				else {
					pf = f;
				}
			}
		}
		return pf;
	}
	
	public String getProteomicsTaxonIdFromFeatureId(String id){
		SolrQuery query = new SolrQuery();
		query.setQuery("na_feature_id:"+id);
		query.setRows(1000000);
		String experiment_id = "";
		
		System.out.println(query.toString());
		
		try {
			QueryResponse qr = server.query(query);
			SolrDocumentList sdl = qr.getResults();

			for (SolrDocument d : sdl) {
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();
					if (el.getKey().equals("experiment_id")) {
						if(experiment_id.length() == 0)
							experiment_id = el.getValue().toString();
						else
							experiment_id += "##"+el.getValue().toString();
					}
				}
			}
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		return experiment_id;
	}
}
