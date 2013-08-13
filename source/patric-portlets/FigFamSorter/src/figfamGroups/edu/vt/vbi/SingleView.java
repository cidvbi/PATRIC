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
package figfamGroups.edu.vt.vbi;

import java.io.IOException;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.ResultType;

public class SingleView extends GenericPortlet {

	SolrInterface solr = new SolrInterface();

	public void init(PortletConfig portletConfig) throws UnavailableException, PortletException {
		super.init(portletConfig);
	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		new SiteHelper().setHtmlMetaElements(request, response, "Protein Family");
		response.setContentType("text/html");
		PortletContext context = this.getPortletContext();
		PortletRequestDispatcher reqDispatcher = context.getRequestDispatcher("/WEB-INF/jsp/single.jsp");
		reqDispatcher.include(request, response);
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		String callType = req.getParameter("callType");
		if (callType != null) {
			ResultType key = new ResultType();
			if (callType.equals("saveState")) {
				String gid = req.getParameter("gid");
				String figfam = req.getParameter("figfam");

				key.put("gid", gid);
				key.put("figfam", figfam);

				Random g = new Random();
				int random = g.nextInt();

				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

				PrintWriter writer = resp.getWriter();
				writer.write("" + random);
				writer.close();

			}
			else if (callType.equals("getData")) {
				String keyword = req.getParameter("keyword");
				JSONObject jsonResult = new JSONObject();
				key.put("keyword", keyword);

				solr.setCurrentInstance("GenomicFeature");
				String start_id = req.getParameter("start");
				String limit = req.getParameter("limit");
				int start = Integer.parseInt(start_id);
				int end = Integer.parseInt(limit);

				// sorting
				HashMap<String, String> sort = null;
				if (req.getParameter("sort") != null) {
					// sorting
					JSONParser a = new JSONParser();
					JSONArray sorter;
					String sort_field = "";
					String sort_dir = "";
					try {
						sorter = (JSONArray) a.parse(req.getParameter("sort").toString());
						sort_field += ((JSONObject) sorter.get(0)).get("property").toString();
						sort_dir += ((JSONObject) sorter.get(0)).get("direction").toString();
						for (int i = 1; i < sorter.size(); i++) {
							sort_field += "," + ((JSONObject) sorter.get(i)).get("property").toString();
						}
						System.out.println(sort_field);
					}
					catch (ParseException e) {
						e.printStackTrace();
					}

					sort = new HashMap<String, String>();

					if (!sort_field.equals("") && !sort_dir.equals("")) {
						sort.put("field", sort_field);
						sort.put("direction", sort_dir);
					}
				}

				JSONObject object = solr.getData(key, sort, null, start, end, false, false, false);

				JSONObject obj = (JSONObject) object.get("response");
				JSONArray obj1 = (JSONArray) obj.get("docs");

				jsonResult.put("results", obj1);
				jsonResult.put("total", obj.get("numFound"));

				resp.setContentType("application/json");
				PrintWriter writer = resp.getWriter();
				writer.write(jsonResult.toString());
				writer.close();
			}
		}
	}
}
