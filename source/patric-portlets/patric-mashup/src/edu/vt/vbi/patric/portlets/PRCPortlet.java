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

import edu.vt.vbi.patric.dao.DBPRC;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class PRCPortlet extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/prc_list.jsp");
		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");

		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");
		String filter = request.getParameter("filter");
		String start_id = request.getParameter("start");
		String limit = request.getParameter("limit");

		int start = Integer.parseInt(start_id);
		int end = start + Integer.parseInt(limit);

		DBShared conn_shared = new DBShared();
		String taxonid = "";

		if (cType.equals("taxon")) {
			taxonid = cId;
		}
		else if (cType.equals("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			taxonid = names.get("ncbi_tax_id");
		}

		if (filter == null) {
			filter = "";
		}

		String sort_field = "";
		String sort_dir = "";

		if (request.getParameter("sort") != null) {
			// sorting
			JSONParser a = new JSONParser();
			JSONArray sorter;

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
				e.printStackTrace();
			}
		}

		DBPRC conn_prc = new DBPRC();
		ArrayList<ResultType> items = null;
		JSONObject jsonResult = new JSONObject();
		JSONArray results = new JSONArray();

		int count_total = conn_prc.getPRCCount(taxonid, filter);

		if (count_total > 0) {
			items = conn_prc.getPRCData(taxonid, filter, start, end, sort_field, sort_dir);
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
