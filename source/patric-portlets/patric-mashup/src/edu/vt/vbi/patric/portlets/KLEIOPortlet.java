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

import edu.vt.vbi.patric.common.KLEIOInterface;

public class KLEIOPortlet extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = null;

		String cType = request.getParameter("display_mode");

		if (cType != null && !cType.equals("")) {

			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/kleio.jsp");

		}
		else {

			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/kleio_iframe.jsp");
		}

		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {

		response.setContentType("application/json");
		String qKeyword = "";
		String type = request.getParameter("type");

		System.out.print(type);

		KLEIOInterface api = new KLEIOInterface();
		JSONObject temp = null;
		String jsonResult = "";

		try {

			if (type != null && type.equals("grid")) {

				qKeyword = request.getParameter("keyword");

				String start = request.getParameter("start");
				String end = request.getParameter("limit");

				temp = api.getDocumentList(qKeyword, null, false, Integer.parseInt(start), Integer.parseInt(end));

				jsonResult = temp.toString();
			}

			if (type != null && type.equals("tree")) {

				qKeyword = request.getParameter("keyword");
				temp = api.getFacets(qKeyword);
				jsonResult = temp.get("result").toString();
			}

			// System.out.println("RESULT : \n"+jsonResult.toString());

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		PrintWriter writer = response.getWriter();
		writer.write(jsonResult);
		writer.close();
	}

}
