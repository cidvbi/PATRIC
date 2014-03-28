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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import edu.vt.vbi.patric.msa.Aligner;
import edu.vt.vbi.patric.proteinfamily.PngWriter;

import edu.vt.vbi.patric.proteinfamily.FIGfamData;

public class MultipleSequenceAlignment extends GenericPortlet {
	public void init(PortletConfig portletConfig) throws UnavailableException, PortletException {
		super.init(portletConfig);
	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// set return content type
		String value = request.getParameter("figfam_name");
		request.setAttribute("figfamName", value);
		value = request.getParameter("feature_ids");
		request.setAttribute("featureIds", value);

		response.setContentType("text/html");
		PortletContext context = this.getPortletContext();
		PortletRequestDispatcher reqDispatcher = context.getRequestDispatcher("/WEB-INF/jsp/msa.jsp");
		reqDispatcher.include(request, response);
	}

	private boolean getBooleanState(String ynText) {
		return ((ynText != null) && (ynText.equals("Y")));
	}

	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		resp.setContentType("text/html");
		String callType = req.getParameter("callType");
		if (callType.equals("alignFromFeatures")) {
			FIGfamData access = new FIGfamData();
			Aligner alignment = access.getFeatureAlignment('n', req);
			alignment.runFastTree();
			PrintWriter writer = resp.getWriter();
			alignment.setAlignTree(writer);
			writer.close();
		}
		else if (callType.equals("dataForTree")) {
			Aligner alignment = new Aligner(req.getParameter("NEWICK"), req.getParameter("locusNames"), req.getParameter("genomeNames"));

			PrintWriter writer = resp.getWriter();

			alignment.setTreePng(getBooleanState(req.getParameter("genomeTips")), getBooleanState(req.getParameter("flushTips")), writer);

			writer.close();

		}
		else if (callType.equals("dataForGblocks")) {
			Aligner alignment = new Aligner(req.getParameter(FIGfamData.FIGFAM_ID), req.getParameter("locusNames"), req.getParameter("genomeNames"),
					req.getParameter("sequences"));
			PrintWriter writer = resp.getWriter();
			alignment.getGblocksPrintable(getBooleanState(req.getParameter("genomeTips")), req.getParameter("conserveChop"),
					req.getParameter("description"), writer);
			writer.close();
		}
		else if (callType.equals("retrieveTreePng")) {

			String pngPath = req.getParameter("TREE_PNG");
			if (pngPath != null) {
				PngWriter.returnPng(pngPath, resp);
			}

		}
		else if (callType.equals("clustalW")) {

			String path = req.getParameter("path");

			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String nextLine = "";
			String lineSep = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer();
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine);
				sb.append(lineSep);
			}
			br.close();

			PrintWriter writer = resp.getWriter();
			writer.close();
		}
	}
}
