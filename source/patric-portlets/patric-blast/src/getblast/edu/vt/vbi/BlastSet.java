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
package getblast.edu.vt.vbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

public class BlastSet extends GenericPortlet {
	public final static String SRC_FIND = "SRC=";

	public final static int SRC_STEP = SRC_FIND.length();

	public final static String SEQ_DESCRIBE = "SEQ_DESCRIBE";

	public final static String JSP_NAME = "JSP_NAME";

	public void init(PortletConfig portletConfig) throws UnavailableException, PortletException {
		super.init(portletConfig);
	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");
		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/router.jsp");
		prd.include(request, response);
	}

	// async requests and responses are processed here
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		String callType = req.getParameter("callType");
		if (callType.equals("getJsp")) {
			String jspName = req.getParameter(JSP_NAME);
			jspName = "/WEB-INF/jsp/" + jspName + ".jsp";
			req.setAttribute(SEQ_DESCRIBE, req.getParameter(SEQ_DESCRIBE));
			resp.setContentType("text/html");
			PortletContext context = this.getPortletContext();
			PortletRequestDispatcher reqDispatcher = context.getRequestDispatcher(jspName);
			reqDispatcher.include(req, resp);
		}
		else if (callType.equals("blastHelp")) {
			String helpHtml = req.getParameter("helpFile");
			resp.setContentType("text/html");
			PortletContext context = this.getPortletContext();
			PortletRequestDispatcher reqDispatcher = context.getRequestDispatcher("/WEB-INF/jsp/popup_header.jsp");
			reqDispatcher.include(req, resp);
			reqDispatcher = context.getRequestDispatcher("/WEB-INF/jsp/" + helpHtml + ".jsp");
			reqDispatcher.include(req, resp);
			reqDispatcher = context.getRequestDispatcher("/WEB-INF/jsp/popup_footer.jsp");
			reqDispatcher.include(req, resp);
		}
		else if (callType.equals("formStore")) {
			String random = req.getParameter("pk");
			if ((random == null) || (random.length() == 0)) {
				Random g = new Random();
				random = "" + g.nextInt();
			}
			HashMap<String, String> key = new HashMap<String, String>();
			String[] toPass = { "programIndex", "dbIndex", "sequence", "queryFrom", "queryTo", "expectIndex",
					"matParamIndex", "alignment", "lowFilter", "midFilter", "geneCodeIndex", "dbCodeIndex",
					"oofAlignIndex", "advanced", "overview", "alignViewIndex", "alignmentsIndex", "descriptionIndex",
					"schemaIndex", "fileName" };
			for (int i = 0; i < toPass.length; i++) {
				String result = req.getParameter(toPass[i]);
				if (result == null) {
					result = "";
				}
				key.put(toPass[i], result);
			}
			PortletSession sess = req.getPortletSession(true);
			sess.setAttribute("key" + random, key);
			PrintWriter writer = resp.getWriter();
			writer.write("" + random);
			writer.close();
		}
		else if (callType.equals("eBug")) {
			PrintWriter eWrite = resp.getWriter();
			Process p = null;
			Runtime r = Runtime.getRuntime();
			p = r.exec("env");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			int lineAt = 0;
			while ((line = br.readLine()) != null) {
				eWrite.write(lineAt + " " + line + "<br />");
				++lineAt;
				int eAt = line.indexOf('=');
				if (eAt < 0) {
					eWrite.write("^^^^^^^^^^^^^^^^^^^" + "<br />");
				}
			}
			br.close();
			eWrite.close();
		}
	}

}
