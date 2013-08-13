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

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.ResultType;

public class FunctionalPropertiesPortlet extends GenericPortlet {

	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

		response.setContentType("text/html");
		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");
		int validContextId = -1;

		if (cId != null) {
			try {
				validContextId = Integer.parseInt(cId);
			}
			catch (NumberFormatException ex) {
			}
		}

		PortletRequestDispatcher prd = null;

		DBShared conn_shared = new DBShared();
		DBSummary conn_summary = new DBSummary();

		JSONObject feature = new JSONObject();
		String fId = null;

		if (cType != null && cId != null && validContextId > 0 && cType.equals("feature")) {
			fId = cId;
			// getting feature info from Solr
			SolrInterface solr = new SolrInterface();
			feature = solr.getPATRICFeature(fId);
			// end of Solr query
		}

		if (feature.isEmpty() == false) {

			if (feature.get("feature_type").equals("CDS") || feature.get("feature_type").equals("mat_peptide")) {
				request.setAttribute("feature", feature);

				prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/funtional_properties/protein.jsp");
				prd.include(request, response);
			}
			else if (feature.get("feature_type").toString().contains("RNA")) {
				ResultType rnaInfo = null;
				rnaInfo = conn_summary.getRNAInfo(fId);

				if (rnaInfo.containsKey("comment_string")
						&& rnaInfo.get("comment_string").toString().contains("structure:")) {
					String[] tmp = rnaInfo.get("comment_string").toString().split("structure:");
					if (tmp[0] != null) {
						rnaInfo.put("comment", tmp[0]);
					}
					if (tmp[1] != null) {
						rnaInfo.put("structure", tmp[1]);
					}
				}
				else if (rnaInfo.containsKey("comment_string")) {
					rnaInfo.put("comment", rnaInfo.get("comment_string"));
				}

				request.setAttribute("rna", rnaInfo);
				prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/funtional_properties/rna.jsp");
				prd.include(request, response);
			}
			else if (feature.get("feature_type").equals("misc_feature")) {
				String comment = conn_shared.getNaFeatureComment(fId);
				request.setAttribute("comment", comment);
				prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/funtional_properties/misc_feature.jsp");
				prd.include(request, response);
			}
			else {
				PrintWriter writer = response.getWriter();
				writer.write("No information is available.");
				writer.close();
			}
		}
		else {
			PrintWriter writer = response.getWriter();
			writer.write("No information is available.");
			writer.close();
		}
	}
}
