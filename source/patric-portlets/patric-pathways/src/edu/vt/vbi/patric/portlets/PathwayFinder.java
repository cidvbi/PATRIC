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
import java.util.ArrayList;
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
import edu.vt.vbi.patric.dao.DBPathways;
import edu.vt.vbi.patric.dao.ResultType;

public class PathwayFinder extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {

		response.setContentType("text/html");
		PortletRequestDispatcher prd = null;
		response.setTitle("Comparative Pathway Tool");
		new SiteHelper().setHtmlMetaElements(request, response, "Comparative Pathway Tool");

		String mode = request.getParameter("display_mode");

		if (mode != null && mode.equals("result")) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/comp_pathway_finder_result.jsp");
		}
		else {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/comp_pathway_finder.jsp");
		}
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		String sraction = request.getParameter("sraction");

		if (sraction != null && sraction.equals("save_params")) {

			String search_on = request.getParameter("search_on");
			String keyword = request.getParameter("keyword");
			String taxonId = request.getParameter("taxonId");
			String algorithm = request.getParameter("algorithm");
			String genomeId = request.getParameter("genomeId");
			String feature_info_id = request.getParameter("feature_info_id");

			ResultType key = new ResultType();

			if (search_on != null) {

				key.put("search_on", search_on.trim());

				if (search_on.equalsIgnoreCase("Map_ID")) {
					key.put("map", keyword.trim());
				}
				else if (search_on.equalsIgnoreCase("Ec_Number")) {
					key.put("ec_number", keyword.trim());
				}
				else if (search_on.equalsIgnoreCase("Keyword")) {
					key.put("keyword", keyword.trim());
				}
			}
			if (taxonId != null && !taxonId.equalsIgnoreCase(""))
				key.put("taxonId", taxonId);

			if (genomeId != null && !genomeId.equalsIgnoreCase(""))
				key.put("genomeId", genomeId);

			if (algorithm != null && !algorithm.equals(""))
				key.put("algorithm", algorithm);

			if (feature_info_id != null && !feature_info_id.equalsIgnoreCase(""))
				key.put("feature_info_id", feature_info_id);

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
			JSONObject jsonResult = new JSONObject();
			JSONArray results = new JSONArray();

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
			}
			catch (ParseException e) {
				e.printStackTrace();
			}

			HashMap<String, String> sort = new HashMap<String, String>();

			if (!sort_field.equals("") && !sort_dir.equals("")) {
				sort.put("field", sort_field);
				sort.put("direction", sort_dir);
			}
			response.setContentType("application/json");

			int start = Integer.parseInt(request.getParameter("start"));
			int end = start + Integer.parseInt(request.getParameter("limit"));
			ArrayList<ResultType> items = new ArrayList<ResultType>();
			int count_total = 0;
			DBPathways conn_summary = new DBPathways();

			response.setContentType("application/json");

			if (need.equals("0")) {
				String pk = request.getParameter("pk");
				PortletSession sess = request.getPortletSession();
				ResultType key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);

				ResultType key_clone = (ResultType) key.clone();

				key_clone.put("ec_number", request.getParameter("ec_number"));
				key_clone.put("algorithm", request.getParameter("algorithm"));

				count_total = conn_summary.getCompPathwayPathwayCount(key_clone.toHashMap());

				if (count_total > 0)
					items = conn_summary.getCompPathwayPathwayList(key_clone.toHashMap(), sort, start, end);

			}
			else if (need.equals("1")) {

				String pk = request.getParameter("pk");
				String map = request.getParameter("pathway_id");
				String algorithm = request.getParameter("algorithm");
				String ec_number = request.getParameter("ec_number");

				PortletSession sess = request.getPortletSession();
				ResultType key_original = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);

				ResultType key = (ResultType) key_original.clone();

				if (map != null && !map.equals(""))
					key.put("map", map);
				else if (key.containsKey("map"))
					key.put("map", key.get("map"));

				if (algorithm != null && !algorithm.equals(""))
					key.put("algorithm", algorithm);
				else if (key.containsKey("algorithm"))
					key.put("algorithm", key.get("algorithm"));

				if (ec_number != null && !ec_number.equals(""))
					key.put("ec_number", ec_number);
				else if (key.containsKey("ec_number"))
					key.put("ec_number", key.get("ec_number"));

				count_total = conn_summary.getCompPathwayECCount(key.toHashMap());

				if (count_total > 0)
					items = conn_summary.getCompPathwayECList(key.toHashMap(), sort, start, end);

			}
			else if (need.equals("2")) {

				String pk = request.getParameter("pk");
				String map = request.getParameter("pathway_id");
				String ec_number = request.getParameter("ec_number");
				String algorithm = request.getParameter("algorithm");

				PortletSession sess = request.getPortletSession();
				ResultType key_original = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
				ResultType key = new ResultType();

				if (key_original != null)
					key = (ResultType) key_original.clone();

				if (ec_number != null && !ec_number.equals(""))
					key.put("ec_number", ec_number);
				else if (key.containsKey("ec_number"))
					key.put("ec_number", key.get("ec_number"));

				if (algorithm != null && !algorithm.equals(""))
					key.put("algorithm", algorithm);
				else if (key.containsKey("algorithm"))
					key.put("algorithm", key.get("algorithm"));

				if (map != null && !map.equals(""))
					key.put("map", map);
				else if (key.containsKey("map"))
					key.put("map", key.get("map"));

				count_total = conn_summary.getCompPathwayFeatureCount(key.toHashMap());
				if (count_total > 0)
					items = conn_summary.getCompPathwayFeatureList(key.toHashMap(), sort, start, end);
			}

			try {
				jsonResult.put("total", count_total);
				for (int i = 0; i < items.size(); i++) {
					ResultType g = (ResultType) items.get(i);
					JSONObject obj = new JSONObject();
					obj.putAll(g);
					results.add(obj);
				}
				jsonResult.put("results", results);
			}
			catch (Exception ex) {
				System.out.println("***" + ex.toString());
			}

			PrintWriter writer = response.getWriter();
			jsonResult.writeJSONString(writer);
			writer.close();
		}

	}
}
