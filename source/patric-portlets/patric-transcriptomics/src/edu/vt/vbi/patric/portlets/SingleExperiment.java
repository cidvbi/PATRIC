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
import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.ResultType;

public class SingleExperiment extends GenericPortlet {

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {

		new SiteHelper().setHtmlMetaElements(request, response, "Single Experiment");

		response.setContentType("text/html");
		response.setTitle("Single Experiment");

		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/SingleExperiment.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {

		resp.setContentType("text/html");

		JSONObject jsonResult = new JSONObject();
		SolrInterface solr = new SolrInterface();
		String eid = req.getParameter("eid");
		String callType = req.getParameter("callType");

		if (callType.equals("getTable")) {
			String start_id = req.getParameter("start");
			String limit = req.getParameter("limit");
			int start = Integer.parseInt(start_id);
			int end = Integer.parseInt(limit);

			HashMap<String, String> sort = null;

			if (req.getParameter("sort") != null) {
				// sorting
				JSONParser a = new JSONParser();
				JSONArray sorter;
				String sort_field = "";
				String sort_dir = "";
				try {
					sorter = (JSONArray) a.parse(req.getParameter("sort").toString());
					sort_field += ((JSONObject) sorter.get(0)).get("property").toString();
					sort_dir += ((JSONObject) sorter.get(0)).get("direction").toString();
					for (int i = 1; i < sorter.size(); i++) {
						sort_field += "," + ((JSONObject) sorter.get(i)).get("property").toString();
					}
					// System.out.println(sort_field);
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

			JSONObject jsonObject = solr.getTranscriptomicsSamples(null, eid, "", start, end, sort);

			jsonResult.put("results", jsonObject.get("data"));
			jsonResult.put("total", jsonObject.get("total"));

		}
		else if (callType.equals("getSummary")) {

			ResultType key = new ResultType();
			key.put("keyword", "eid:(" + eid + ")");
			key.put("fields", "description,condition,pi,title,institution,release_date,accession,organism,strain,timeseries");
			solr.setCurrentInstance("GENEXP_Experiment");
			JSONObject object = solr.getData(key, null, null, 0, 1, false, false, false);

			JSONObject obj = (JSONObject) object.get("response");
			JSONArray obj1 = (JSONArray) obj.get("docs");

			jsonResult.put("summary", obj1.get(0));
		}

		PrintWriter writer = resp.getWriter();
		jsonResult.writeJSONString(writer);
		writer.close();
	}
}
