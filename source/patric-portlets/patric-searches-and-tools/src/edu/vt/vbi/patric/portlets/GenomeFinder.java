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
package edu.vt.vbi.patric.portlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.ResultType;

public class GenomeFinder extends GenericPortlet {

	SolrInterface solr = new SolrInterface();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

		response.setContentType("text/html");
		String mode = request.getParameter("display_mode");
		// response.setTitle("Genome Finder");
		new SiteHelper().setHtmlMetaElements(request, response, "Genome Finder");

		PortletRequestDispatcher prd = null;
		if (mode != null && mode.equals("result")) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/genome_finder_result.jsp");
		}
		else {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/genome_finder.jsp");
		}
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		String sraction = request.getParameter("sraction");

		if (sraction != null && sraction.equals("save_params")) {

			ResultType key = new ResultType();

			String genomeId = request.getParameter("genomeId");
			String taxonId = "";
			String cType = request.getParameter("context_type");
			String cId = request.getParameter("context_id");
			if (cType != null && cId != null && cType.equals("taxon") && !cId.equals("")) {
				taxonId = cId;
			}
			String keyword = request.getParameter("keyword");
			String state = request.getParameter("state");
			String ncbi_taxon_id = request.getParameter("ncbi_taxon_id");
			String exact_search_term = request.getParameter("exact_search_term");
			String search_on = request.getParameter("search_on");

			if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
				key.put("genomeId", genomeId);
			}
			if (taxonId != null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId", taxonId);
			}
			if (keyword != null) {
				key.put("keyword", keyword.trim());
			}
			if (ncbi_taxon_id != null) {
				key.put("ncbi_taxon_id", ncbi_taxon_id);
			}
			if (state != null) {
				key.put("state", state);
			}
			if (exact_search_term != null) {
				key.put("exact_search_term", exact_search_term);
			}
			if (search_on != null) {
				key.put("search_on", search_on);
			}
			// random
			Random g = new Random();
			int random = g.nextInt();

			PortletSession sess = request.getPortletSession(true);
			sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

			PrintWriter writer = response.getWriter();
			writer.write("" + random);
			writer.close();

		}
		else if (sraction != null && sraction.equals("get_params")) {

			String ret = "";
			String pk = request.getParameter("pk");
			PortletSession sess = request.getPortletSession();

			if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) != null) {
				ResultType key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
				ret = key.get("keyword").toString();
			}

			PrintWriter writer = response.getWriter();
			writer.write("" + ret);
			writer.close();

		}
		else {

			String need = request.getParameter("need");

			String facet = "", keyword = "", pk = "", state = "";
			
			boolean hl = false;

			PortletSession sess = request.getPortletSession();

			ResultType key = new ResultType();

			JSONObject jsonResult = new JSONObject();

			if (need.equals("1")) {

				pk = request.getParameter("pk");

				keyword = request.getParameter("keyword");

				String genomeId = request.getParameter("genomeId");

				facet = request.getParameter("facet");

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					//System.out.print("1sequence");

					key.put("facet", facet);
					key.put("keyword", keyword);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {
					//System.out.print("2sequence");

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
				}

				String orig_keyword = key.get("keyword");

				if (genomeId != null && !genomeId.equals("")) {

					//System.out.print("genomeId - " + genomeId);

					key.put("keyword", solr.ConstructSequenceFinderKeyword(genomeId));

				}
				else if (genomeId != null && genomeId.equals("")) {

					String solrId = "";
					solr.setCurrentInstance("GenomeFinder");
					JSONObject solr_output = solr.getGenomeIDsfromSolr(key.get("keyword"), facet, true);

					if (!key.containsKey("solrId")) {
						solrId = solr.getGenomeIdsfromSolrOutput(solr_output);
						key.put("solrId", solrId);
					}
					else {
						solrId = key.get("solrId");
					}
					if (solrId.equals("")) {
						key.put("keyword", "");
					}
					else {
						key.put("keyword", solr.ConstructSequenceFinderKeyword(solrId));
					}

					JSONObject facets = (JSONObject) solr_output.get("facets");
					if (facets != null)
						key.put("facets", facets.toString());

				}

				String start_id = request.getParameter("start");
				String limit = request.getParameter("limit");
				int start = Integer.parseInt(start_id);
				int end = Integer.parseInt(limit);

				// sorting
				HashMap<String, String> sort = null;
				if (request.getParameter("sort") != null) {
					// sorting
					JSONParser a = new JSONParser();
					JSONArray sorter;
					String sort_field = "";
					String sort_dir = "";
					try {
						sorter = (JSONArray) a.parse(request.getParameter("sort").toString());
						sort_field += ((JSONObject) sorter.get(0)).get("property").toString();
						sort_dir += ((JSONObject) sorter.get(0)).get("direction").toString();
						for (int i = 1; i < sorter.size(); i++) {
							sort_field += "," + ((JSONObject) sorter.get(i)).get("property").toString();
						}
						//System.out.println(sort_field);
					}
					catch (ParseException e) {
						e.printStackTrace();
					}

					sort = new HashMap<String, String>();

					if (!sort_field.equals("") && !sort_dir.equals("")) {
						sort.put("field", sort_field);
						sort.put("direction", sort_dir);
					}

				}

				solr.setCurrentInstance("GenomeSequenceFinder");
				if (key.get("keyword").toString().equals("")) {
					jsonResult.put("results", new JSONArray());
					jsonResult.put("total", 0);
				}
				else {
					JSONObject object = solr.getData(key, sort, facet, start, end, false, false, false);

					JSONObject obj = (JSONObject) object.get("response");
					JSONArray obj1 = (JSONArray) obj.get("docs");

					if (!key.containsKey("facets")) {
						JSONObject facets = (JSONObject) object.get("facets");
						key.put("facets", facets.toString());
					}

					key.put("keyword", orig_keyword);

					jsonResult.put("results", obj1);
					jsonResult.put("total", obj.get("numFound"));
				}

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.toString());
				writer.close();

			}
			else if (need.equals("0")) {

				solr.setCurrentInstance("GenomeFinder");

				pk = request.getParameter("pk");
				keyword = request.getParameter("keyword");
				facet = request.getParameter("facet");
				
				String highlight = request.getParameter("highlight");
				
				hl = Boolean.parseBoolean(highlight);				
				
				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					//System.out.print("1genome");

					key.put("facet", facet);
					key.put("keyword", keyword);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {

					//System.out.print("2genome");

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
				}

				String start_id = request.getParameter("start");
				String limit = request.getParameter("limit");
				int start = Integer.parseInt(start_id);
				int end = Integer.parseInt(limit);

				HashMap<String, String> sort = null;
				if (request.getParameter("sort") != null) {
					// sorting
					JSONParser a = new JSONParser();
					JSONArray sorter;
					String sort_field = "";
					String sort_dir = "";
					try {
						sorter = (JSONArray) a.parse(request.getParameter("sort").toString());
						sort_field += ((JSONObject) sorter.get(0)).get("property").toString();
						sort_dir += ((JSONObject) sorter.get(0)).get("direction").toString();
						for (int i = 1; i < sorter.size(); i++) {
							sort_field += "," + ((JSONObject) sorter.get(i)).get("property").toString();
						}
						//System.out.println(sort_field);
					}
					catch (ParseException e) {
						e.printStackTrace();
					}

					sort = new HashMap<String, String>();

					if (!sort_field.equals("") && !sort_dir.equals("")) {
						sort.put("field", sort_field);
						sort.put("direction", sort_dir);
					}
				}

				JSONObject object = solr.getData(key, sort, facet, start, end, facet != null ? true : false, hl,
						false);

				JSONObject obj = (JSONObject) object.get("response");
				JSONArray obj1 = (JSONArray) obj.get("docs");

				if (!key.containsKey("facets")) {
					if (object.containsKey("facets")) {
						JSONObject facets = (JSONObject) object.get("facets");
						key.put("facets", facets.toString());
					}
				}

				if (!key.containsKey("solrId")) {
					key.put("solrId", solr.getGenomeIdsfromSolrOutput(solr.getGenomeIDsfromSolr(key.get("keyword"),
							facet, false)));
				}

				jsonResult.put("results", obj1);
				jsonResult.put("total", obj.get("numFound"));

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.toString());
				writer.close();

			}
			else if (need.equals("tree")) {

				solr.setCurrentInstance("GenomeFinder");

				pk = request.getParameter("pk");
				key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);

				if (key.containsKey("state"))
					state = key.get("state");
				else
					state = request.getParameter("state");

				key.put("state", state);

				sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				try {

					if (!key.containsKey("tree")) {

						JSONObject facet_fields = (JSONObject) new JSONParser().parse(key.get("facets"));
						JSONArray arr1 = solr.processStateAndTree(key, need, facet_fields, key.get("facet"), state, 4,
								false);
						jsonResult.put("results", arr1);
						key.put("tree", arr1);

					}
					else {
						jsonResult.put("results", key.get("tree"));
					}

				}
				catch (ParseException e) {
					e.printStackTrace();
				}

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.get("results").toString());
				writer.close();

			}
			else if (need.equals("tree_for_taxon")) {

				//long start_ms = System.currentTimeMillis();
				solr.setCurrentInstance("GenomeFinder");
				pk = request.getParameter("pk");
				facet = request.getParameter("facet");
				keyword = request.getParameter("keyword");
				state = request.getParameter("state");

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					key.put("facet", facet);
					key.put("keyword", keyword);
					key.put("state", state);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
				}

				HashMap<String, String> sort = null;

				JSONObject object = solr.getData(key, sort, facet, 0, -1, true, false, false);

				if (!key.containsKey("tree")) {

					JSONObject facet_fields = (JSONObject) object.get("facets");

					JSONArray arr1 = solr.processStateAndTree(key, need, facet_fields, facet, key.get("state"), 4,
							false);

					jsonResult.put("results", arr1);

					key.put("tree", arr1);

				}
				else {
					jsonResult.put("results", key.get("tree"));
				}

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.get("results").toString());
				writer.close();

				//long end_ms = System.currentTimeMillis();

				//System.out.print("time passed " + (end_ms - start_ms));

			}
			else if (need.equals("from_genome")) {

				solr.setCurrentInstance("GenomeFinder");

				try {
					JSONObject object = (JSONObject) solr.getGenomeTabJSON(request.getParameter("keyword"),
							"genome_finder");

					JSONObject obj = (JSONObject) object.get("response");
					JSONArray obj1 = (JSONArray) obj.get("docs");

					jsonResult.put("results", obj1.get(0));

				}
				catch (ParseException e) {
					System.err.print("error tree2");
					e.printStackTrace();
				}

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.get("results").toString());
				writer.close();
			}
			else if (need.equals("getIdsForCart")) {

				solr.setCurrentInstance("GenomeFinder");

				pk = request.getParameter("pk");
				int rows = Integer.parseInt(request.getParameter("limit").toString());
				String field = request.getParameter("field");

				key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);

				JSONObject object = solr.getIdsForCart(key, field, rows);

				JSONObject obj = (JSONObject) object.get("response");
				JSONArray obj1 = (JSONArray) obj.get("docs");

				jsonResult.put("results", obj1);
				jsonResult.put("total", obj.get("numFound"));

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.toString());
				writer.close();

			}

		}
	}

}
