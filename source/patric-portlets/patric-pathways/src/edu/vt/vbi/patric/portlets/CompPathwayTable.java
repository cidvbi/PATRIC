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

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.dao.DBPathways;
import edu.vt.vbi.patric.dao.ResultType;

public class CompPathwayTable extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

		new SiteHelper().setHtmlMetaElements(request, response, "Pathways");
		response.setContentType("text/html");
		PortletRequestDispatcher prd = null;
		response.setTitle("Pathways");
		prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/comp_pathway_table.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		String need = request.getParameter("need");
		JSONObject jsonResult = new JSONObject();
		JSONArray results = new JSONArray();
		HashMap<String, String> key = new HashMap<String, String>();

		String pathway_class = request.getParameter("pathway_class");
		if (pathway_class != null && !pathway_class.equals(""))
			key.put("pathway_class", pathway_class);

		String map = request.getParameter("pathway_id");
		if (map != null && !map.equals(""))
			key.put("map", map);

		String ec_number = request.getParameter("ec_number");
		if (ec_number != null && !ec_number.equals(""))
			key.put("ec_number", ec_number);

		String algorithm = request.getParameter("algorithm");
		if (algorithm != null && !algorithm.equals(""))
			key.put("algorithm", algorithm);

		String cId = request.getParameter("cId");
		String cType = request.getParameter("cType");
		if (cType.equals("genome"))
			key.put("genomeId", cId);
		else if (cType.equals("taxon"))
			key.put("taxonId", cId);

		// paging
		int start = Integer.parseInt(request.getParameter("start"));
		int end = start + Integer.parseInt(request.getParameter("limit"));

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

		DBPathways conn_summary = new DBPathways();
		ArrayList<ResultType> items = new ArrayList<ResultType>();
		int count_total = 0;

		response.setContentType("application/json");

		if (need.equals("0")) {

			count_total = conn_summary.getCompPathwayPathwayCount(key);
			if (count_total > 0)
				items = conn_summary.getCompPathwayPathwayList(key, sort, start, end);

		}
		else if (need.equals("1")) {

			count_total = conn_summary.getCompPathwayECCount(key);
			if (count_total > 0)
				items = conn_summary.getCompPathwayECList(key, sort, start, end);

		}
		else if (need.equals("2")) {

			count_total = conn_summary.getCompPathwayFeatureCount(key);
			if (count_total > 0)
				items = conn_summary.getCompPathwayFeatureList(key, sort, start, end);

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
		writer.write(jsonResult.toString());
		writer.close();
	}

}
