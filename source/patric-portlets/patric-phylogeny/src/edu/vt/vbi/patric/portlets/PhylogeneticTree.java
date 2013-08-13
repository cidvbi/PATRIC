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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.ResultType;

public class PhylogeneticTree extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	public void init() throws PortletException {
		super.init();

		// update genome-id mapping cache
		try {
			HashMap<String, String> key = new HashMap<String, String>();
			key.put("ncbi_taxon_id", "2");
			key.put("data_source", "");

			DBSummary conn = new DBSummary();
			ArrayList<ResultType> list = conn.getGenomeListByTaxon(key, null, 0, -1);
			StringBuilder sb = new StringBuilder();
			sb.append("var genomeMap = new Array();\n");
			for (int i = 0; i < list.size(); i++) {
				sb.append("genomeMap[\"" + list.get(i).get("genome_name").replaceAll("[\\s\\(\\)\\:\\[\\],]+", "_")
						+ "\"] = " + list.get(i).get("genome_info_id") + ";\n");
			}

			PrintWriter out = new PrintWriter(new FileWriter(getPortletContext().getRealPath("/js/genomeMaps.js")));
			out.println(sb.toString());
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");

		new SiteHelper().setHtmlMetaElements(request, response, "Phylogeny");

		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/index.jsp");
		prd.include(request, response);
	}
}
