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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.EutilInterface;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

public class PeptidomePortlet extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/peptidome_list.jsp");
		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");
		String filter = request.getParameter("filter");
		int start = 0;
		int limit = 0;

		if (request.getParameter("start") != null) {
			start = Integer.parseInt(request.getParameter("start"));
		}
		if (request.getParameter("limit") != null) {
			limit = Integer.parseInt(request.getParameter("limit"));
		}
		String tId = null;
		String cType = request.getParameter("context_type");
		String cId = request.getParameter("context_id");

		if (cType.equals("taxon")) {
			tId = cId;
		}
		else if (cType.equals("genome")) {
			DBShared conn_shared = new DBShared();
			ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
			tId = names.get("ncbi_taxon_id");
		}

		String strQueryTerm = "txid" + tId + "[Organism:exp]";
		if (filter != null) {
			strQueryTerm = strQueryTerm + "+AND+" + filter + "[ETYP]";
		}

		EutilInterface eutil_api = new EutilInterface();

		JSONObject jsonResult = eutil_api.getResults("pepdome", strQueryTerm, "", "", start, limit);

		PrintWriter writer = response.getWriter();
		writer.write(jsonResult.toString());
		writer.close();
	}
}