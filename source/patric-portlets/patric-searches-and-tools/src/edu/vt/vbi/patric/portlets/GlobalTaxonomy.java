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

import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.ResultType;

public class GlobalTaxonomy extends GenericPortlet {

	SolrInterface solr = new SolrInterface();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		String sraction = request.getParameter("sraction");

		if (sraction != null && sraction.equals("save_params")) {

			String search_on = request.getParameter("search_on");
			String keyword = request.getParameter("keyword");
			String taxonId = request.getParameter("taxonId");
			String genomeId = request.getParameter("genomeId");
			String algorithm = request.getParameter("algorithm");
			String exact_search_term = request.getParameter("exact_search_term");

			ResultType key = new ResultType();

			if (search_on != null) {
				key.put("search_on", search_on.trim());
			}

			key.put("keyword", keyword.trim());

			if (taxonId != null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId", taxonId);
			}

			if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
				key.put("genomeId", genomeId);
			}

			if (algorithm != null && !algorithm.equalsIgnoreCase("")) {
				key.put("algorithm", algorithm);
			}

			if (exact_search_term != null) {
				key.put("exact_search_term", exact_search_term);
			}

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

			boolean hl = false;
			
			PortletSession sess = request.getPortletSession();

			ResultType key = new ResultType();

			JSONObject jsonResult = new JSONObject();

			if (need.equals("taxonomy")) {

				solr.setCurrentInstance("GlobalTaxonomy");

				pk = request.getParameter("pk");
				keyword = request.getParameter("keyword");
				facet = request.getParameter("facet");
				String highlight = request.getParameter("highlight");
				
				hl = Boolean.parseBoolean(highlight);

				if (sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE) == null) {

					//System.out.print("1taxonomy");

					key.put("facet", facet);
					key.put("keyword", keyword);

					sess.setAttribute("key" + pk, key, PortletSession.APPLICATION_SCOPE);

				}
				else {

					//System.out.print("2taxonomy");

					key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
					key.put("facet", facet);
				}

				String start_id = request.getParameter("start");
				String limit = request.getParameter("limit");
				int start = Integer.parseInt(start_id);
				int end = Integer.parseInt(limit);

				// sorting
				String sort_field = request.getParameter("sort");
				String sort_dir = request.getParameter("dir");

				HashMap<String, String> sort = null;

				if (sort_field != null && sort_dir != null && !sort_field.equals("") && !sort_dir.equals("")) {
					sort = new HashMap<String, String>();
					sort.put("field", sort_field);
					sort.put("direction", sort_dir);
				}

				JSONObject object = solr.getData(key, sort, facet, start, end, true, hl, false);

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
			else if (need.equals("tree")) {

				pk = request.getParameter("pk");
				key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
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
		}
	}
}
