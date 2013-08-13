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

import edu.vt.vbi.patric.common.ArrayExpressInterface;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class ArrayExpressPortlet extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/arrayexpress_list.jsp");
		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");
		String keyword = request.getParameter("keyword");
		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");
		String start_id = request.getParameter("start");
		String limit = request.getParameter("limit");
		int start = Integer.parseInt(start_id);
		int end = Integer.parseInt(limit);

		DBShared conn_shared = new DBShared();
		String species_name = "";

		if (cType.equals("taxon")) {
			ArrayList<ResultType> parents = conn_shared.getTaxonParentTree(cId);
			if (parents.size() > 0) {
				species_name = parents.get(0).get("name");
			}
		}
		else if (cType.equals("genome")) {
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			species_name = names.get("genome_name");
		}

		ArrayExpressInterface api = new ArrayExpressInterface();

		JSONObject jsonAll = api.getResults(keyword, species_name);
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		jsonResult.put("total", jsonAll.get("total"));
		jsonResult.put("hasData", jsonAll.get("hasData"));
		for (int i = start; i < start + end; i++) {
			if (i < ((JSONArray) jsonAll.get("results")).size()) {
				JSONObject j = (JSONObject) ((JSONArray) jsonAll.get("results")).get(i);
				jsonArr.add(i - start, j);
			}
		}
		jsonResult.put("results", jsonArr);
		PrintWriter writer = response.getWriter();
		writer.write(jsonResult.toString());
		writer.close();
	}
}
