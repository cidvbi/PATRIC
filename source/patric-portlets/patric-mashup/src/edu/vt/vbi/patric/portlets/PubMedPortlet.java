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

import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.EutilInterface;
import edu.vt.vbi.patric.common.PubMedHelper;
import edu.vt.vbi.patric.common.SiteHelper;

public class PubMedPortlet extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {
		new SiteHelper().setHtmlMetaElements(request, response, "Literature");
		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/pubmed_list.jsp");
		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");

		String qScope = request.getParameter("scope");
		String qDate = request.getParameter("date");
		String qKeyword = request.getParameter("keyword");

		int start = 0;
		int limit = 0;

		if (request.getParameter("start") != null) {
			start = Integer.parseInt(request.getParameter("start"));
		}
		if (request.getParameter("limit") != null) {
			limit = Integer.parseInt(request.getParameter("limit"));
		}
		String tId = null;
		String gId = null;
		String fId = null;

		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");
		JSONObject jsonResult = null;

		if (cType != null) {
			if (cType.equals("taxon")) {
				tId = cId;
			}
			else if (cType.equals("genome")) {
				gId = cId;
			}
			else if (cType.equals("feature")) {
				fId = cId;
			}

			HashMap<String, String> key = new HashMap<String, String>();
			key.put("scope", qScope);
			key.put("date", qDate);
			key.put("keyword", qKeyword);
			key.put("ncbi_taxon_id", tId);
			key.put("genome_info_id", gId);
			key.put("feature_id", fId);
			key.put("context", cType);

			String strPubmedQuery = PubMedHelper.getPubmedQueryString(key);
			// System.out.println("pubmedQuery: " + strPubmedQuery);

			// PubMedInterface pubmed_api = new PubMedInterface();
			EutilInterface eutil_api = new EutilInterface();

			// JSONObject jsonResult = pubmed_api.getResults(strPubmedQuery, start, limit);
			jsonResult = eutil_api.getResults("pubmed", strPubmedQuery, "&sort=pub+date", "&sort=pub+date&retmode=xml", start, limit);
		}
		else {
			jsonResult = new JSONObject();
		}

		PrintWriter writer = response.getWriter();
		writer.write(jsonResult.toString());
		writer.close();
	}
}
