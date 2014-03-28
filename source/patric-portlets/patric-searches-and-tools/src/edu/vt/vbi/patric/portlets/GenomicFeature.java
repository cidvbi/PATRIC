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

public class GenomicFeature extends GenericPortlet {

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
		new SiteHelper().setHtmlMetaElements(request, response, "Feature Finder");

		PortletRequestDispatcher prd = null;
		if (mode != null && mode.equals("result")) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/feature_finder_result.jsp");
		}
		else {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/feature_finder.jsp");
		}
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		String sraction = request.getParameter("sraction");

		if (sraction != null && sraction.equals("save_params")) {

			// HashMap<String,String> key = new HashMap<String,String>();
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

			// random
			Random g = new Random();
			int random = g.nextInt();

			PortletSession sess = request.getPortletSession(true);
			sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

			PrintWriter writer = response.getWriter();
			writer.write("" + random);
			writer.close();

		}
		else {

			String need = request.getParameter("need");

			String facet = "", keyword = "", pk = "", state = "";
			boolean grouping = false;
			boolean hl = false;

			PortletSession sess = request.getPortletSession();

			ResultType key = new ResultType();

			JSONObject jsonResult = new JSONObject();

			if (need.equals("feature")) {

				solr.setCurrentInstance("GenomicFeature");

				pk = request.getParameter("pk");
				keyword = request.getParameter("keyword");
				facet = request.getParameter("facet");
				String highlight = request.getParameter("highlight");
				
				hl = Boolean.parseBoolean(highlight);
				
				if (request.getParameter("grouping") != null)
					grouping = Boolean.parseBoolean(request.getParameter("grouping").toString());

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					//System.out.print("1feature");

					key.put("facet", facet);
					key.put("keyword", keyword);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {

					//System.out.print("2feature");

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
				}

				// To support grouping option
				key.put("grouping", true);

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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					sort = new HashMap<String, String>();

					if (!sort_field.equals("") && !sort_dir.equals("")) {
						sort.put("field", sort_field);
						sort.put("direction", sort_dir);
					}

				}

				JSONObject object = solr.getData(key, sort, facet, start, end, true, hl, grouping);

				JSONObject obj = (JSONObject) object.get("response");
				JSONArray obj1 = (JSONArray) obj.get("docs");

				if (!key.containsKey("facets")) {
					JSONObject facets = (JSONObject) object.get("facets");
					key.put("facets", facets.toString());
				}

				jsonResult.put("results", obj1);
				jsonResult.put("total", obj.get("numFound"));

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.toString());
				writer.close();

			}
			else if (need.equals("featurewofacet")) {

				solr.setCurrentInstance("GenomicFeature");

				pk = request.getParameter("pk");
				keyword = request.getParameter("keyword");
				facet = request.getParameter("facet");

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					//System.out.print("1feature");

					key.put("facet", facet);
					key.put("keyword", keyword);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {

					//System.out.print("2feature");

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					sort = new HashMap<String, String>();

					if (!sort_field.equals("") && !sort_dir.equals("")) {
						sort.put("field", sort_field);
						sort.put("direction", sort_dir);
					}

				}

				// DBSearch conn_search = new DBSearch();
				// ArrayList<ResultType> items = null;

				JSONObject object = solr.getData(key, sort, facet, start, end, false, false, grouping);

				JSONObject obj = (JSONObject) object.get("response");
				JSONArray obj1 = (JSONArray) obj.get("docs");

				jsonResult.put("results", obj1);
				jsonResult.put("total", obj.get("numFound"));

				response.setContentType("application/json");
				PrintWriter writer = response.getWriter();
				writer.write(jsonResult.toString());
				writer.close();

			}
			else if (need.equals("tree")) {

				pk = request.getParameter("pk");

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) != null) {
					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
				}

				if (key != null && key.containsKey("state"))
					state = key.get("state");
				else
					state = request.getParameter("state");

				sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				try {

					if (!key.containsKey("tree")) {

						JSONObject facet_fields = (JSONObject) new JSONParser().parse(key.get("facets"));

						JSONArray arr1 = solr.processStateAndTree(key, need, facet_fields, key.get("facet"), state, 4,
								true);

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
			else if (need.equals("getIdsForCart")) {

				solr.setCurrentInstance("GenomicFeature");

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
