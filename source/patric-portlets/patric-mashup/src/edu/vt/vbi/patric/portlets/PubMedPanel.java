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

import edu.vt.vbi.patric.common.EutilInterface;
import edu.vt.vbi.patric.common.PubMedHelper;

public class PubMedPanel extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");

		String cId = request.getParameter("context_id");
		int validContextId = -1;

		if (cId != null) {
			try {
				validContextId = Integer.parseInt(cId);
			}
			catch (NumberFormatException ex) {
			}
		}
		if (validContextId > 0) {
			PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/pubmed_panel.jsp");
			prd.include(request, response);
		}
		else {
			PrintWriter writer = response.getWriter();
			writer.write("<p>Invalid Parameter - missing context information</p>");
			writer.close();
		}
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		// response.setContentType("application/json");
		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");
		String qKeyword = request.getParameter("keyword");
		String db = request.getParameter("db");

		String contextLink = "";
		HashMap<String, String> key = new HashMap<String, String>();

		if (cType != null && cType.equals("taxon")) {
			key.put("ncbi_taxon_id", cId);
			key.put("context", "taxon");
			contextLink = "cType=taxon&amp;cId=" + cId;

		}
		else if (cType != null && cType.equals("genome")) {
			key.put("genome_info_id", cId);
			key.put("context", "genome");
			contextLink = "cType=genome&amp;cId=" + cId;

		}
		else if (cType != null && cType.equals("feature")) {
			key.put("feature_id", cId);
			key.put("context", "feature");
			contextLink = "cType=feature&amp;cId=" + cId;

		}

		if (qKeyword != null) {
			key.put("keyword", qKeyword);
			contextLink += "&amp;kw=" + qKeyword;
		}
		else {
			contextLink += "&amp;time=a&amp;kw=";
		}

		StringBuilder sb = new StringBuilder();

		try {
			String strPubmedQuery = PubMedHelper.getPubmedQueryString(key);
			System.out.println("pubmedQuery: " + strPubmedQuery);

			EutilInterface eutil_api = new EutilInterface();

			JSONObject jsonResult = new JSONObject();
			if (db != null && db.equals("pmc")) {
				jsonResult = eutil_api.getResults("pmc", strPubmedQuery, "&sort=pub+date",
						"&sort=pub+date&retmode=xml", 0, 5);
			}
			else {
				jsonResult = eutil_api.getResults("pubmed", strPubmedQuery, "&sort=pub+date",
						"&sort=pub+date&retmode=xml", 0, 5);
			}

			JSONArray results = (JSONArray) jsonResult.get("results");
			JSONObject row = null;

			sb.append("<ul class=\"no-decoration small\">");
			for (int i = 0; i < results.size(); i++) {
				row = (JSONObject) results.get(i);

				sb.append("<li>");
				if (row.containsKey("PubDate")) {
					sb.append("<div>" + row.get("PubDate") + "</div>");
				}
				if (row.containsKey("pubmed_id") == true && !row.get("pubmed_id").equals("")) {
					sb.append("<div><a href=\"http://view.ncbi.nlm.nih.gov/pubmed/" + row.get("pubmed_id")
							+ "\" target=\"_blank\">" + row.get("Title") + "</a></div>");
				}
				else {
					sb.append("<div>" + row.get("Title") + "</div>");
				}
				sb.append("<div>" + row.get("abbrAuthorList") + "</div>");
				sb.append("<div>" + row.get("Source") + "</div>");
				sb.append("</li>");
			}

			if (results.size() == 0) {
				// sb.append("<div> No pubmed record is available.</div>");
				sb.append("<div class=\"far\"> No pubmed record is available.</div>");
				sb.append("<div> Please try ");
				sb.append(" <a href=\"http://www.ncbi.nlm.nih.gov/pmc/?term=" + strPubmedQuery
						+ "\" target=_blank>PMC</a>");
				sb.append(" or <a href=\"http://scholar.google.com/scholar?q=" + strPubmedQuery
						+ "\" target=_blank>Google Scholar</a>");
				sb.append("</div>");
			}
			else {
				sb.append("<div class=\"left\"><a class=\"double-arrow-link\" href=\"Literature?" + contextLink
						+ "\">more</a></div>");
			}
			sb.append("</ul>");

		}
		catch (NullPointerException npex) {
			sb.append("<div> No pubmed record is available.</div>");

		}
		catch (Exception ex) {
			sb.append("<div> No pubmed record is available.</div>");
		}

		PrintWriter writer = response.getWriter();
		writer.write(sb.toString());
		writer.close();
	}
}
