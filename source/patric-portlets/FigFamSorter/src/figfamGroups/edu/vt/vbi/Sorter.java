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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

import edu.vt.vbi.ci.util.CommandResults;
import edu.vt.vbi.ci.util.ExecUtilities;
import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.dao.HibernateHelper;
import edu.vt.vbi.patric.dao.ResultType;
import figfamGroups.edu.vt.vbi.sql.FigFam;

public class Sorter extends GenericPortlet {

	public void init(PortletConfig portletConfig) throws UnavailableException, PortletException {
		super.init(portletConfig);
		String k = "PATRIC_DB.cfg.xml";
		HibernateHelper.buildSessionFactory(k, k);
		FigFam.setSessionFactory(HibernateHelper.getSessionFactory(k));
	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");

		String mode = request.getParameter("display_mode");

		new SiteHelper().setHtmlMetaElements(request, response, "Protein Families");

		PortletRequestDispatcher prd = null;
		if ((mode != null) && (mode.equals("result"))) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/main.jsp");
		}
		else if ((mode != null) && (mode.equals("treeSee"))) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/aligner.jsp");
		}
		else {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/tree1.jsp");
		}
		prd.include(request, response);
	}

	private void getGenomeIds(ResourceRequest req, ResultType key) {
		String result = req.getParameter("genomeIds");
		FigFam access = new FigFam();
		if (result != null && !result.equals("")) {
			key.put("genomeIds", result);
		}
		else {
			String cType = req.getParameter("cType");
			if ((cType != null) && (cType.equals("taxon"))) {
				String cId = req.getParameter("cId");
				if (cId == null || cId.equals("")) {
					cId = "2";
				}
				result = access.getGenomeIdsForTaxon(cId);
				key.put("genera", access.getTaxonName(cId));
			}
			key.put("genomeIds", result);
		}
	}

	private void getTaxonIds(ResourceRequest req, PrintWriter writer) {
		String cId = req.getParameter("taxonId");
		if ((cId != null) && (0 < cId.length())) {
			// String keyword = req.getParameter("keyword");
			FigFam access = new FigFam();
			writer.write(access.getGenomeIdsForTaxon(cId));
		}
		
	}

	private void setKeyValues(String name, ResourceRequest req, ResultType key) {
		String result = req.getParameter(name);
		if (result == null) {
			result = "";
		}
		key.put(name, result);
	}

	private String getKeyValue(String name, ResultType key) {
		String result = key.get(name);
		if (result == null) {
			result = "";
		}
		return result;
	}

	// async requests and responses are processed here
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		resp.setContentType("text/html");
		String callType = req.getParameter("callType");

		//System.out.println(callType);
		if (callType != null) {
			if (callType.equals("toSorter")) {
				ResultType key = new ResultType();
				// Added by OralDALAY

				if (req.getParameter("keyword") != null && !req.getParameter("keyword").equals(""))
					key.put("keyword", req.getParameter("keyword"));

				getGenomeIds(req, key);
				Random g = new Random();
				int random = g.nextInt();
				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute("key" + random, key);
				PrintWriter writer = resp.getWriter();
				writer.write("" + random);
				writer.close();
			}
			else if (callType.equals("getGenomeDetails")) {
				resp.setContentType("application/json");
				PrintWriter writer = resp.getWriter();
				FigFam access = new FigFam();
				access.getGenomeDetails(req, writer);
				writer.close();
			}
			else if (callType.equals("getTaxonIds")) {
				PrintWriter writer = resp.getWriter();
				getTaxonIds(req, writer);
				writer.close();
			}
			else if (callType.equals("toAligner")) {
				ResultType key = new ResultType();
				setKeyValues("featureIds", req, key);
				setKeyValues("figfamId", req, key);
				setKeyValues("product", req, key);
				Random g = new Random();
				int random = g.nextInt();
				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);
				PrintWriter writer = resp.getWriter();
				writer.write("" + random);
				writer.close();
			}
			else if (callType.equals("toDetails")) {
				ResultType key = new ResultType();
				setKeyValues("genomeIds", req, key);
				setKeyValues("figfamIds", req, key);
				Random g = new Random();
				int random = g.nextInt();
				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);
				PrintWriter writer = resp.getWriter();
				writer.write("" + random);
				writer.close();
			}
			else if (callType.equals("getJsp")) {
				String jspName = req.getParameter("JSP_NAME");
				jspName = "/WEB-INF/jsp/" + jspName + ".jsp";
				resp.setContentType("text/html");
				PortletContext context = this.getPortletContext();
				PortletRequestDispatcher reqDispatcher = context.getRequestDispatcher(jspName);
				reqDispatcher.include(req, resp);
			}
			else if (callType.equals("getFeatureIds")) {
				PrintWriter writer = resp.getWriter();
				FigFam access = new FigFam();
				access.getFeatureIds(req, writer, req.getParameter("keyword"));
				writer.close();
			}
			else if (callType.equals("getGroupStats")) {
				resp.setContentType("application/json");
				PrintWriter writer = resp.getWriter();
				FigFam access = new FigFam();
				access.getGroupStats(req, writer);
				writer.close();
			}
			else if (callType.equals("getLocusTags")) {
				PrintWriter writer = resp.getWriter();
				FigFam access = new FigFam();
				access.getLocusTags(req, writer);
				writer.close();
			}else if (callType.equals("getSessionId")) {
				PrintWriter writer = resp.getWriter();
				PortletSession sess = req.getPortletSession(true);
				writer.write(sess.getId());
				writer.close();
			}
			else if (callType.equals("saveState")) {
				String keyType = req.getParameter("keyType");
				ResultType key = new ResultType();
				setKeyValues("pageAt", req, key);
				setKeyValues("syntonyId", req, key);
				setKeyValues("regex", req, key);
				setKeyValues("filter", req, key);
				setKeyValues("perfectFamMatch", req, key);
				setKeyValues("minnumber_of_members", req, key);
				setKeyValues("maxnumber_of_members", req, key);
				setKeyValues("minnumber_of_species", req, key);
				setKeyValues("maxnumber_of_species", req, key);
				setKeyValues("ClusterRowOrder", req, key);
				setKeyValues("ClusterColumnOrder", req, key);
				setKeyValues("heatmapAxis", req, key);
				setKeyValues("colorScheme", req, key);
				setKeyValues("heatmapState", req, key);
				setKeyValues("steps", req, key);
				Random g = new Random();
				int random = 0;
				while (random == 0) {
					random = g.nextInt();
				}
				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute(keyType + random, key);
				PrintWriter writer = resp.getWriter();
				writer.write("" + random);
				writer.close();
			}
			else if (callType.equals("getState")) {
				PrintWriter writer = resp.getWriter();
				PortletSession sess = req.getPortletSession(true);
				String keyType = req.getParameter("keyType");
				String random = req.getParameter("random");
				if ((random != null) && (keyType != null)) {
					ResultType key = (ResultType) (sess.getAttribute(keyType + random));
					writer.write(getKeyValue("pageAt", key));
					writer.write("\t" + getKeyValue("syntonyId", key));
					writer.write("\t" + getKeyValue("regex", key));
					writer.write("\t" + getKeyValue("filter", key));
					writer.write("\t" + getKeyValue("perfectFamMatch", key));
					writer.write("\t" + getKeyValue("minnumber_of_members", key));
					writer.write("\t" + getKeyValue("maxnumber_of_members", key));
					writer.write("\t" + getKeyValue("minnumber_of_species", key));
					writer.write("\t" + getKeyValue("maxnumber_of_species", key));
					writer.write("\t" + getKeyValue("steps", key));
					writer.write("\t" + getKeyValue("ClusterRowOrder", key));
					writer.write("\t" + getKeyValue("ClusterColumnOrder", key));
					writer.write("\t" + getKeyValue("heatmapAxis", key));
					writer.write("\t" + getKeyValue("colorScheme", key));
					writer.write("\t" + getKeyValue("heatmapState", key));
					writer.close();
				}
			}
			else if (callType.equals("doClustering")) {
				PrintWriter writer = resp.getWriter();
				String data = req.getParameter("data");
				String g = req.getParameter("g");
				String e = req.getParameter("e");
				String m = req.getParameter("m");
				String ge = req.getParameter("ge");
				String pk = req.getParameter("pk");
				String action = req.getParameter("action");

				String folder = "/tmp/";
				String filename = folder + "tmp_" + pk + ".txt";
				String output_filename = folder + "cluster_tmp_" + pk;
				try {

					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
					out.write(data);
					out.close();

				}
				catch (Exception es) {// Catch exception if any
					System.err.println("Error: " + es.getMessage());
				}

				if (action.equals("Run"))
					writer.write(doCLustering(filename, output_filename, g, e, m, ge).toString());

				writer.close();

			}
			else if (callType.equals("getSyntonyOrder")) {
				PrintWriter writer = resp.getWriter();
				FigFam access = new FigFam();
				JSONArray json = access.getSyntonyOrder(req);
				long start_ms = System.currentTimeMillis();
				json.writeJSONString(writer);
				long end_ms = System.currentTimeMillis();
				System.out.println("Writing response time - "+ (end_ms - start_ms));
			}
			else {
				PrintWriter writer = resp.getWriter();
				writer.write(callType);
				writer.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject doCLustering(String filename, String outputfilename, String g, String e, String m, String ge)
			throws IOException {

		boolean remove = true;
		JSONObject output = new JSONObject();

		String exec = "sh /opt/jboss-patric/runMicroArrayClustering.sh " + filename + " " + outputfilename + " "
				+ ((g.equals("1")) ? ge : "0") + " " + ((e.equals("1")) ? ge : "0") + " " + m;

		System.out.print(exec);

		CommandResults callClustering = ExecUtilities.exec(exec);

		if (callClustering.getStdout()[0].toString().equals("done")) {

			BufferedReader in = new BufferedReader(new FileReader(outputfilename + ".cdt"));
			String strLine = "";
			int count = 0;
			JSONArray rows = new JSONArray();
			while ((strLine = in.readLine()) != null) {
				String[] tabs = strLine.split("\t");
				if (count == 0) {
					JSONArray columns = new JSONArray();
					for (int i = 4; i < tabs.length; i++) {
						columns.add(tabs[i].split("-")[0]);
					}
					output.put("columns", columns);
				}
				if (count >= 3) {
					rows.add(tabs[1].split("-")[0]);
				}
				count++;
			}
			in.close();
			output.put("rows", rows);
		}

		if (remove) {
			exec = "rm " + filename + " " + outputfilename;
			callClustering = ExecUtilities.exec(exec);
		}

		return output;
	}
}
