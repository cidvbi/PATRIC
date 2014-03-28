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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.dao.DBTranscriptomics;
import edu.vt.vbi.patric.dao.ResultType;

public class TranscriptomicsEnrichment extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {

		new SiteHelper().setHtmlMetaElements(request, response, "Pathway Summary");

		response.setContentType("text/html");
		response.setTitle("Pathway Summary");

		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/TranscriptomicsEnrichment.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {

		resp.setContentType("text/html");
		String callType = req.getParameter("callType");

		if (callType.equals("saveParams")) {

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

		if (callType.equals("getGenomeIds")) {

			HashMap<String, String> key = new HashMap<String, String>();

			key.put("feature_info_id", req.getParameter("feature_info_id"));
			key.put("map", req.getParameter("map"));
			DBTranscriptomics conn_transcriptomics = new DBTranscriptomics();
			String genomeId = conn_transcriptomics.getGenomeListFromFeatureIds(key, 0, -1);

			PrintWriter writer = resp.getWriter();
			writer.write(genomeId);
			writer.close();
		}
		else if (callType.equals("getFeatureTable")) {

			PortletSession sess = req.getPortletSession();

			String pk = req.getParameter("pk");
			String start_id = req.getParameter("start");
			String limit = req.getParameter("limit");
			int start = Integer.parseInt(start_id);
			int end = start + Integer.parseInt(limit);

			HashMap<String, String> key = (HashMap<String, String>) sess.getAttribute("key" + pk, PortletSession.APPLICATION_SCOPE);
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
			}
			catch (ParseException e) {
				e.printStackTrace();
			}

			HashMap<String, String> sort = new HashMap<String, String>();

			if (!sort_field.equals("") && !sort_dir.equals("")) {
				sort.put("field", sort_field);
				sort.put("direction", sort_dir);
			}

			DBTranscriptomics conn_transcriptomics = new DBTranscriptomics();
			int count_total = conn_transcriptomics.getPathwayEnrichmentCount(key);
			ArrayList<ResultType> items = conn_transcriptomics.getPathwayEnrichmentList(key, sort, start, end);

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
				ex.printStackTrace();
			}

			PrintWriter writer = resp.getWriter();
			writer.write(jsonResult.toString());
			writer.close();
		}
	}
}
