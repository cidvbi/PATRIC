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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.portlet.GenericPortlet;
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
import edu.vt.vbi.patric.common.ExpressionDataCollection;
import edu.vt.vbi.patric.common.ExpressionDataGene;
import edu.vt.vbi.patric.common.PolyomicHandler;
import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.common.SolrInterface;
//import edu.vt.vbi.patric.dao.DBTranscriptomics;
import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class TranscriptomicsGene extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {

		new SiteHelper().setHtmlMetaElements(request, response, "Transcriptomics Gene");

		response.setContentType("text/html");

		String mode = request.getParameter("display_mode");
		// String cType = request.getParameter("context_type");
		// String cId = request.getParameter("context_id");
		PortletRequestDispatcher prd = null;

		if (mode != null && mode.equals("result")) {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/TranscriptomicsGene.jsp");
		}
		else {
			prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/tree.jsp");
		}

		prd.include(request, response);

	}

	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		resp.setContentType("text/html");
		String callType = req.getParameter("callType");
		PrintWriter writer = resp.getWriter();
		JSONObject jsonResult = new JSONObject();

		// need to handle polyomic token
		String token = null;
		if (req.getUserPrincipal() != null) {
			String userName = req.getUserPrincipal().getName();
			PolyomicHandler polyomic = new PolyomicHandler();
			PortletSession p_session = req.getPortletSession(true);
			token = (String) p_session.getAttribute("PolyomicAuthToken", PortletSession.APPLICATION_SCOPE);

			if (token == null) {
				polyomic.authenticate(userName);
				token = polyomic.getAuthenticationToken();
				p_session.setAttribute("PolyomicAuthToken", token, PortletSession.APPLICATION_SCOPE);
			}
		}

		if (token == null) {
			token = "";
		}

		// end of polyomic token handling. Added by Harry

		if (callType != null) {
			if (callType.equals("saveParams")) {

				ResultType key = new ResultType();
				String keyword = req.getParameter("keyword");
				SolrInterface solr = new SolrInterface();
				String sId = solr.getTranscriptomicsSamplePIds(keyword);

				if (!keyword.equals("")) {
					key.put("keyword", keyword);
				}

				if (!sId.equals("")) {
					key.put("sampleId", sId);
					Random g = new Random();
					int random = g.nextInt();

					PortletSession sess = req.getPortletSession(true);
					sess.setAttribute("key" + random, key, PortletSession.APPLICATION_SCOPE);

					writer.write("" + random);
				}
				else {
					writer.write("");
				}
				writer.close();

			}
			else if (callType.equals("getTables")) {

				String expId = req.getParameter("expId");
				String sampleId = req.getParameter("sampleId");
				String colId = req.getParameter("colId");
				String colsampleId = req.getParameter("colsampleId");
				String keyword = req.getParameter("keyword");
				SolrInterface solr = new SolrInterface();

				JSONObject sample_obj = new JSONObject();
				JSONArray sample = new JSONArray();

				if ((sampleId != null && !sampleId.equals("")) || (expId != null && !expId.equals(""))) {
					sample_obj = solr
							.getTranscriptomicsSamples(sampleId, expId, "pid,expname,expmean,timepoint,mutant,strain,condition", 0, -1, null);
					sample = (JSONArray) sample_obj.get("data");
					/*
					 * if(experimental != null && experimental.equals("true") || Boolean.parseBoolean(experimental) == true){ sample =
					 * solr.getTranscriptomicsSamples(sampleId, expId, "pid,expname,expmean,timepoint,mutant,strain,condition"); }else{ sample =
					 * dbomics.getSamples(sampleId, expId); }
					 */
				}

				// System.out.println(sample.toString());

				// Read from JSON if collection parameter is there
				ExpressionDataCollection parser = null;
				if (colId != null && !colId.equals("") && token != null) {

					parser = new ExpressionDataCollection(colId, token);
					parser.read(ExpressionDataCollection.CONTENT_SAMPLE);
					if (colsampleId != null && !colsampleId.equals("")) {
						parser.filter(colsampleId, ExpressionDataCollection.CONTENT_SAMPLE);
					}
					// Append samples from collection to samples from DB
					sample = parser.append(sample, ExpressionDataCollection.CONTENT_SAMPLE);
				}

				// System.out.println(sample.toJSONString());

				String sampleList = "";
				sampleList += ((JSONObject) sample.get(0)).get("pid");

				for (int i = 1; i < sample.size(); i++) {
					sampleList += "," + ((JSONObject) sample.get(i)).get("pid");
				}

				jsonResult.put(ExpressionDataCollection.CONTENT_SAMPLE + "Total", sample.size());
				jsonResult.put(ExpressionDataCollection.CONTENT_SAMPLE, sample);
				JSONArray expression = new JSONArray();

				if ((sampleId != null && !sampleId.equals("")) || (expId != null && !expId.equals(""))) {
					expression = solr.getTranscriptomicsGenes(sampleId, expId, keyword);
					/*
					 * if(experimental != null && experimental.equals("true") || Boolean.parseBoolean(experimental) == true){ expression =
					 * solr.getTranscriptomicsGenes(sampleId, expId); }else{ expression = dbomics.getGenes(sampleId, expId); }
					 */
				}

				if (colId != null && !colId.equals("") && token != null) {

					parser.read(ExpressionDataCollection.CONTENT_EXPRESSION);
					if (colsampleId != null && !colsampleId.equals(""))
						parser.filter(colsampleId, ExpressionDataCollection.CONTENT_EXPRESSION);

					// Append expression from collection to expression from DB
					expression = parser.append(expression, ExpressionDataCollection.CONTENT_EXPRESSION);
				}

				JSONArray stats = getExperimentStats(expression, sampleList, sample);
				jsonResult.put(ExpressionDataCollection.CONTENT_EXPRESSION + "Total", stats.size());
				jsonResult.put(ExpressionDataCollection.CONTENT_EXPRESSION, stats);

				// writer.write(jsonResult.toString());
				resp.setContentType("application/json");
				jsonResult.writeJSONString(writer);
				writer.close();

			}
			else if (callType.equals("doClustering")) {

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
			else if (callType.equals("saveState")) {

				String keyType = req.getParameter("keyType");
				String pageAt = req.getParameter("pageAt");
				String sampleFilter = req.getParameter("sampleFilter");
				String regex = req.getParameter("regex");
				String regexGN = req.getParameter("regexGN");
				String upFold = req.getParameter("upFold");
				String downFold = req.getParameter("downFold");
				String upZscore = req.getParameter("upZscore");
				String downZscore = req.getParameter("downZscore");
				String significantGenes = req.getParameter("significantGenes");
				String ClusterColumnOrder = req.getParameter("ClusterColumnOrder");
				String ClusterRowOrder = req.getParameter("ClusterRowOrder");
				String heatmapState = req.getParameter("heatmapState");
				String heatmapAxis = req.getParameter("heatmapAxis");
				String colorScheme = req.getParameter("colorScheme");
				String filterOffset = req.getParameter("filterOffset");

				ResultType key = new ResultType();
				key.put("sampleFilter", sampleFilter);
				key.put("pageAt", pageAt);
				key.put("regex", regex);
				key.put("regexGN", regexGN);
				key.put("upFold", upFold);
				key.put("downFold", downFold);
				key.put("upZscore", upZscore);
				key.put("downZscore", downZscore);
				key.put("significantGenes", significantGenes);
				key.put("ClusterRowOrder", ClusterRowOrder);
				key.put("ClusterColumnOrder", ClusterColumnOrder);
				key.put("heatmapState", heatmapState);
				key.put("heatmapAxis", heatmapAxis);
				key.put("colorScheme", colorScheme);
				key.put("filterOffset", filterOffset);

				Random g = new Random();
				int random = 0;
				while (random == 0) {
					random = g.nextInt();
				}
				PortletSession sess = req.getPortletSession(true);
				sess.setAttribute(keyType + random, key);

				writer.write("" + random);
				writer.close();

			}
			else if (callType.equals("getState")) {

				PortletSession sess = req.getPortletSession(true);
				String keyType = req.getParameter("keyType");
				String random = req.getParameter("random");

				if ((random != null) && (keyType != null)) {
					JSONArray results = new JSONArray();
					JSONObject a = new JSONObject();
					ResultType key = (ResultType) (sess.getAttribute(keyType + random));
					if (key != null) {
						a.put("sampleFilter", key.get("sampleFilter"));
						a.put("pageAt", key.get("pageAt"));
						a.put("regex", key.get("regex"));
						a.put("regexGN", key.get("regexGN"));
						a.put("upFold", key.get("upFold"));
						a.put("downFold", key.get("downFold"));
						a.put("upZscore", key.get("upZscore"));
						a.put("downZscore", key.get("downZscore"));
						a.put("significantGenes", key.get("significantGenes"));
						a.put("ClusterRowOrder", key.get("ClusterRowOrder"));
						a.put("ClusterColumnOrder", key.get("ClusterColumnOrder"));
						a.put("heatmapState", key.get("heatmapState"));
						a.put("heatmapAxis", key.get("heatmapAxis"));
						a.put("colorScheme", key.get("colorScheme"));
						a.put("filterOffset", key.get("filterOffset"));
					}
					results.add(a);
					// writer.write(results.toString());
					resp.setContentType("application/json");
					results.writeJSONString(writer);
					writer.close();
				}
			}
		}
	}

	public JSONObject doCLustering(String filename, String outputfilename, String g, String e, String m, String ge) throws IOException {

		boolean remove = true;
		JSONObject output = new JSONObject();

		String exec = "sh /opt/jboss-patric/runMicroArrayClustering.sh " + filename + " " + outputfilename + " " + ((g.equals("1")) ? ge : "0") + " "
				+ ((e.equals("1")) ? ge : "0") + " " + m;
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
						columns.add(tabs[i]);
					}
					output.put("columns", columns);
				}
				if (count >= 3) {
					rows.add(tabs[1]);
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

	public JSONArray getExperimentStats(JSONArray data, String samples, JSONArray sample_data) throws IOException {

		JSONArray results = new JSONArray();

		HashMap<String, ExpressionDataGene> genes = new HashMap<String, ExpressionDataGene>();
		HashMap<String, String> sample = new HashMap<String, String>();

		for (int i = 0; i < sample_data.size(); i++) {
			JSONObject a = (JSONObject) sample_data.get(i);
			sample.put(a.get("pid").toString(), a.get("expname").toString());
		}

		// System.out.println(data.toJSONString());

		for (int i = 0; i < data.size(); i++) {

			JSONObject a = (JSONObject) data.get(i);
			String id = a.get("na_feature_id").toString();
			ExpressionDataGene b = null;

			if (genes.containsKey(id)) {
				b = genes.get(id);
			}
			else {
				b = new ExpressionDataGene(a);
			}

			b.addSamplestoGene(a, sample); // Sample HashMap is used to create absence/presence string
			genes.put(id, b);
		}

		Iterator<?> it = genes.entrySet().iterator();
		String idList = "";
		JSONObject temp = new JSONObject();

		while (it.hasNext()) {

			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();

			ExpressionDataGene value = (ExpressionDataGene) entry.getValue();

			JSONObject a = new JSONObject();

			a.put("refseq_locus_tag", value.getRefSeqLocusTag());
			a.put("na_feature_id", value.getNAFeatureID());
			value.setSampleBinary(samples);
			a.put("sample_binary", value.getSampleBinary());
			a.put("sample_size", value.getSampleCounts());
			a.put("samples", value.getSamples());

			idList += value.getNAFeatureID() + ",";

			temp.put(value.getNAFeatureID(), a);
		}

		// System.out.println(idList);

		/*
		 * Solr Call to get Feature attributes-----------------------------------
		 */
		HashMap<String, String> condition = new HashMap<String, String>();
		condition.put("na_feature_ids", idList.substring(0, idList.length() - 1));
		SolrInterface solr = new SolrInterface();
		JSONObject object = solr.getFeaturesByID(condition);
		JSONArray obj_array = (JSONArray) object.get("results");
		/**/

		JSONObject a, b;
		// long start, end;

		// System.out.print(obj_array.size());
		// start = System.currentTimeMillis();
		for (int i = 0; i < obj_array.size(); i++) {
			a = (JSONObject) obj_array.get(i);
			b = (JSONObject) temp.get(a.get("na_feature_id").toString());
			b.put("strand", a.get("strand"));
			b.put("patric_product", a.get("product"));
			b.put("patric_accession", a.get("accession"));
			b.put("start_max", a.get("start_max"));
			b.put("end_min", a.get("end_min"));
			b.put("locus_tag", a.get("locus_tag"));
			b.put("genome_name", a.get("genome_name"));
			b.put("gene", a.get("gene"));
			results.add(b);
		}
		// end = System.currentTimeMillis();
		// System.out.println("Processing time for getting feature attributes - "+(end-start));

		return results;
	}
}
