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
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

public class GenomicFeatureSummaryPortlet extends GenericPortlet {

	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {

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

		if (cType != null && cId != null && validContextId > 0 && (cType.equals("genome") || cType.equals("taxon"))) {
			PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/overview/genomic_feature_summary_init.jsp");
			prd.include(request, response);
		}
		else {
			PrintWriter writer = response.getWriter();
			writer.write("<p>Invalid Parameter - missing context information</p>");
			writer.close();
		}
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/overview/genomic_feature_summary.jsp");
		prd.include(request, response);
	}
}
