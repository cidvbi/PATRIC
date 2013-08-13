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
import edu.vt.vbi.patric.dao.DBTranscriptomics;
import edu.vt.vbi.patric.dao.ResultType;

public class TranscriptomicsGeneExp extends GenericPortlet {

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

		new SiteHelper().setHtmlMetaElements(request, response, "Transcriptomics Feature");

		PortletRequestDispatcher prd = null;
		prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/GeneExpression.jsp");
		prd.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {

		resp.setContentType("application/json");

		String paramFeatureId = req.getParameter("featureId");
		String paramStoreType = req.getParameter("storeType");
		String paramSampleId = req.getParameter("sampleId");
		String paramKeyword = req.getParameter("keyword");
		String paramLogRatio = req.getParameter("log_ratio");
		String paramZScore = req.getParameter("zscore");

		String paramStart = req.getParameter("start");
		String paramLimit = req.getParameter("limit");
		String paramSort = req.getParameter("sort");

		JSONParser a = new JSONParser();
		JSONArray sorter = null;
		String sort_field = "";
		String sort_dir = "";
		if (paramSort != null) {
			try {
				sorter = (JSONArray) a.parse(paramSort);
				sort_field += ((JSONObject) sorter.get(0)).get("property").toString();
				sort_dir += ((JSONObject) sorter.get(0)).get("direction").toString();
				for (int i = 1; i < sorter.size(); i++) {
					sort_field += "," + ((JSONObject) sorter.get(i)).get("property").toString();
				}

			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		System.out.println("paramFeatureId=" + paramFeatureId + ",paramSampleId=" + paramSampleId + ",paramStoreType="
				+ paramStoreType);

		HashMap<String, String> key = new HashMap<String, String>();
		HashMap<String, String> sort = new HashMap<String, String>();
		DBTranscriptomics conn_transcriptopics = new DBTranscriptomics();
		if (paramFeatureId != null && !paramFeatureId.equals("")) {
			key.put("na_feature_id", paramFeatureId);
		}
		if (paramSampleId != null && !paramSampleId.equals("")) {
			key.put("pid", paramSampleId);
		}
		if (paramKeyword != null && !paramKeyword.equals("")) {
			key.put("keyword", paramKeyword);
		}
		if (paramLogRatio != null && !paramLogRatio.equals("") && !paramLogRatio.equals("0")) {
			key.put("log_ratio", paramLogRatio);
		}
		if (paramZScore != null && !paramZScore.equals("") && !paramZScore.equals("0")) {
			key.put("zscore", paramZScore);
		}
		if (sorter != null) {
			sort.put("field", sort_field);
			sort.put("direction", sort_dir);
		}

		ArrayList<ResultType> items = null;
		JSONObject jsonResult = new JSONObject();

		if (paramStoreType.equals("features")) {

			items = conn_transcriptopics.getGeneLvlExpression(key);
			try {
				jsonResult.put("total", items.size());
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
		}
		else if (paramStoreType.equals("strain") || paramStoreType.equals("mutant")
				|| paramStoreType.equals("condition")) {
			// meta data fields
			items = conn_transcriptopics.getGeneLvlExpressionCounts(paramStoreType, key);
			try {
				JSONArray results = new JSONArray();

				for (int i = 0; i < items.size(); i++) {
					ResultType g = (ResultType) items.get(i);
					JSONObject obj = new JSONObject();
					obj.putAll(g);
					results.add(obj);
				}
				jsonResult.put("exp_stat", results);

			}
			catch (Exception ex) {
				System.out.println("***" + ex.toString());
			}
		}
		else if (paramStoreType.equals("log_ratio") || paramStoreType.equals("z_score")) {
			// bar-charts
			items = conn_transcriptopics.getGeneLvlExpressionHistogram(paramStoreType, key);
			try {
				JSONArray results = new JSONArray();

				for (int i = 0; i < items.size(); i++) {
					ResultType g = (ResultType) items.get(i);
					JSONObject obj = new JSONObject();
					obj.putAll(g);
					results.add(obj);
				}
				jsonResult.put("exp_stat", results);

			}
			catch (Exception ex) {
				System.out.println("***" + ex.toString());
			}
		}
		else if (paramStoreType.equals("correlation")) {

			String cutoffValue = req.getParameter("cutoffValue");
			String cutoffDir = req.getParameter("cutoffDir");

			key.put("na_feature_id", paramFeatureId);
			key.put("cutoff_value", cutoffValue);
			key.put("cutoff_dir", cutoffDir);
			if (paramKeyword != null && !paramKeyword.equals("")) {
				key.put("keyword", paramKeyword);
			}

			int start = Integer.parseInt(paramStart);
			int end = start + Integer.parseInt(paramLimit);
			int count_total = conn_transcriptopics.getCorrelatedGenesCount(key);
			items = conn_transcriptopics.getCorrelatedGenes(key, sort, start, end);

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

		}

		PrintWriter writer = resp.getWriter();
		writer.write(jsonResult.toString());
		writer.close();
	}
}
