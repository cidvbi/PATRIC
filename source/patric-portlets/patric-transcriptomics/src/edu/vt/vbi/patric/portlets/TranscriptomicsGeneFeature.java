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
import java.util.*;

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

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.common.SolrInterface;

public class TranscriptomicsGeneFeature extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

		response.setContentType("text/html");

		response.setTitle("Transcriptomics Feature");

		PortletRequestDispatcher prd = null;

		new SiteHelper().setHtmlMetaElements(request, response, "Transcriptomics Feature");

		prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/TranscriptomicsFeature.jsp");

		prd.include(request, response);

	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		resp.setContentType("text/html");

		String callType = req.getParameter("callType");

		if (callType.equals("saveFeatureParams")) {

			HashMap<String, String> key = new HashMap<String, String>();

			key.put("feature_info_id", req.getParameter("feature_info_id"));

			Random g = new Random();
			int random = g.nextInt();

			PortletSession sess = req.getPortletSession(true);
			sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

			PrintWriter writer = resp.getWriter();
			writer.write("" + random);
			writer.close();

		}
		else if (callType.equals("getFeatureTable")) {

			PortletSession sess = req.getPortletSession();

			String pk = req.getParameter("pk");
			String start_id = req.getParameter("start");
			String limit = req.getParameter("limit");
			int start = Integer.parseInt(start_id);
			int end = Integer.parseInt(limit);

			// sorting
			/*
			 * JSONParser a = new JSONParser(); JSONArray sorter; String sort_field = ""; String sort_dir = ""; try {
			 * sorter = (JSONArray) a.parse(req.getParameter("sort").toString()); sort_field +=
			 * ((JSONObject)sorter.get(0)).get("property").toString(); sort_dir +=
			 * ((JSONObject)sorter.get(0)).get("direction").toString(); for(int i=1; i<sorter.size(); i++){ sort_field
			 * += ","+((JSONObject)sorter.get(i)).get("property").toString(); }
			 * 
			 * } catch (ParseException e) { e.printStackTrace(); }
			 */

			HashMap<String, String> key = (HashMap<String, String>) sess.getAttribute("key" + pk,
					PortletSession.APPLICATION_SCOPE);

			HashMap<String, String> condition = new HashMap<String, String>();
			condition.put("na_feature_ids", key.get("feature_info_id"));
			condition.put("sortParam", req.getParameter("sort").toString());
			condition.put("startParam", Integer.toString(start));
			condition.put("limitParam", Integer.toString(end));
			SolrInterface solr = new SolrInterface();
			JSONObject object = solr.getFeaturesByID(condition);
			JSONArray obj_array = (JSONArray) object.get("results");

			JSONObject jsonResult = new JSONObject();

			jsonResult.put("results", obj_array);
			jsonResult.put("total", object.get("total").toString());

			PrintWriter writer = resp.getWriter();
			writer.write(jsonResult.toString());
			writer.close();

		}

	}

}
