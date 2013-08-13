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

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
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

public class PathwayTable extends GenericPortlet {

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

		response.setTitle("Pathways");

		prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/pathway_table.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");

		response.setContentType("application/json");

		int start = Integer.parseInt(request.getParameter("start"));
		int end = start + Integer.parseInt(request.getParameter("limit"));

		HashMap<String, String> key = new HashMap<String, String>();

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

		if (request.getParameter("id") != null) {
			key.put("na_feature_id", request.getParameter("id"));
		}
		DBPathways conn_pathways = new DBPathways();
		int count_total = Integer.parseInt(conn_pathways.getFeaturePathwayCount(key));
		ArrayList<ResultType> items = conn_pathways.getPathwayList(key, sort, start, end);

		JSONObject jsonResult = new JSONObject();

		try {

			jsonResult.put("total", count_total);
			JSONArray results = new JSONArray();
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
