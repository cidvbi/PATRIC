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

import edu.vt.vbi.patric.dao.DBPathways;
import edu.vt.vbi.patric.dao.ResultType;

public class PathwayTableSingle extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = null;
		response.setTitle("Pathway Table");
		prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/single_pathway_table.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");

		String callType = request.getParameter("callType");

		if (callType != null && callType.equals("savetopk")) {

			String cId = request.getParameter("cId");
			String cType = request.getParameter("cType");
			String map = request.getParameter("map");
			String algorithm = request.getParameter("algorithm");
			String ec_number = request.getParameter("ec_number");

			ResultType key = new ResultType();

			if (cId != null && !cId.equals("")) {
				key.put("genomeId", cId);
			}
			if (cType != null && !cType.equals("")) {
				key.put("cType", cType);
			}

			if (map != null && !map.equals("")) {
				key.put("map", map);
			}

			if (algorithm != null && !algorithm.equals("")) {
				key.put("algorithm", algorithm);
			}

			if (ec_number != null && !ec_number.equals("")) {
				key.put("ec_number", ec_number);
			}

			key.put("which", "download_from_heatmap_feature");

			Random g = new Random();
			int random = g.nextInt();

			PortletSession sess = request.getPortletSession(true);
			sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

			PrintWriter writer = response.getWriter();
			writer.write("" + random);
			writer.close();

		}
		else if (callType.equals("show")) {

			JSONObject jsonResult = new JSONObject();
			JSONArray results = new JSONArray();
			String pk = request.getParameter("pk");
			PortletSession sess = request.getPortletSession();
			ResultType key = (ResultType) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
			ResultType key_clone = (ResultType) key.clone();
			sess.setAttribute("key" + pk, key_clone, PortletSession.APPLICATION_SCOPE);

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
					System.out.println(sort_field);
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

			System.out.println("EC:" + key.get("ec_number"));
			System.out.println("ALGO:" + key.get("algorithm"));
			System.out.println("MAP:" + key.get("map"));

			DBPathways conn_pathways = new DBPathways();
			int start = Integer.parseInt(request.getParameter("start"));
			int end = start + Integer.parseInt(request.getParameter("limit"));
			int count_total = conn_pathways.getCompPathwayFeatureCount(key.toHashMap());

			ArrayList<ResultType> items = new ArrayList<ResultType>();

			if (count_total > 0)
				items = conn_pathways.getCompPathwayFeatureList(key_clone.toHashMap(), sort, start, end);

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
			writer.write(jsonResult.toString());
			writer.close();
		}
	}
}
