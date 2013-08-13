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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.ExpressionDataFileReader;
import edu.vt.vbi.patric.common.PolyomicHandler;

public class TranscriptomicsUploader extends GenericPortlet {

	String ENDPOINT = "http://dayhoff.vbi.vt.edu:8888";

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		// do nothing
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		String mode = request.getParameter("mode");
		System.out.println("TranscriptomicsUploader mode:" + mode);

		if (request.getUserPrincipal() != null && mode != null) {
			String userName = request.getUserPrincipal().getName();
			PolyomicHandler polyomic = new PolyomicHandler();
			PortletSession p_session = request.getPortletSession(true);
			String token = (String) p_session.getAttribute("PolyomicAuthToken", PortletSession.APPLICATION_SCOPE);
			Long defaultWId = (Long) p_session.getAttribute("DefaultWorkspaceID", PortletSession.APPLICATION_SCOPE);

			if (token == null) {
				polyomic.authenticate(userName);
				p_session.setAttribute("PolyomicAuthToken", polyomic.getAuthenticationToken(),
						PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setAuthenticationToken(token);
			}
			if (defaultWId == null) {
				polyomic.retrieveDefaultWorkspace();
				defaultWId = polyomic.getDefaultWorkspaceID();
				p_session.setAttribute("DefaultWorkspaceID", defaultWId, PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setDefaultWorkspaceID(defaultWId);
			}

			// -- process actions --//
			if (mode.equals("create_collection")) {
				// get token, create a collection, make them ready to upload

				String collectionId = polyomic.createCollection("transcriptomics");

				JSONObject json = new JSONObject();
				json.put("success", true);
				json.put("token", token);
				json.put("collection", collectionId);
				json.put("url", polyomic.getEndpoint());

				System.out.println("create_collection is called:" + json.toJSONString());

				response.setContentType("application/json");
				response.getWriter().write(json.toJSONString());
				response.getWriter().close();
			}
			else if (mode.equals("parse_collection")) {
				// read raw files from polyomic, parse, and save in JSON format

				String collectionId = request.getParameter("collectionId");
				JSONObject snapshot = new JSONObject();

				System.out.println("in parse_collection. collectionId:" + collectionId + ",token="
						+ polyomic.getAuthenticationToken());

				if (collectionId != null) {

					JSONObject config = polyomic.getExpressionDataFileReaderConfig(collectionId);

					System.out.println("init reader: " + config.toJSONString());

					ExpressionDataFileReader reader = new ExpressionDataFileReader(config);

					if (reader.doRead()) {
						reader.calculateExpStats();
						// polyomic.saveJSONFilesToCollection(collectionId,
						// reader);
						polyomic.saveJSONtoCollection(collectionId, PolyomicHandler.CONTENT_SAMPLE + ".json",
								reader.get(PolyomicHandler.CONTENT_SAMPLE), PolyomicHandler.CONTENT_SAMPLE);
						polyomic.saveJSONtoCollection(collectionId, PolyomicHandler.CONTENT_EXPRESSION + ".json",
								reader.get(PolyomicHandler.CONTENT_EXPRESSION), PolyomicHandler.CONTENT_EXPRESSION);

						snapshot = reader.get("snapshot");
						snapshot.put("origFileName", config.get("dataFileName"));
						snapshot.put("countGeneIDs", reader.getCountGeneIDs());
						snapshot.put("countSamples", reader.getCountSamples());
						snapshot.put("success", true);
					}
					else {
						snapshot.put("success", false);
						snapshot.put("msg", "Currently we have a problem. Please try later or contact to PATRIC team.");
					}
				}

				response.setContentType("application/json");
				response.getWriter().write(snapshot.toJSONString());
				response.getWriter().close();
			}
			else if (mode.equals("map_genes")) {
				// read expression.json from polyomic
				// save the mapping result in IDMapping.json
				// return mapping results
				String collectionId = request.getParameter("collectionId");
				String geneIdType = request.getParameter("geneIdType");

				JSONObject mapping = null;

				if (collectionId != null) {
					JSONObject config = polyomic.getExpressionDataFileReaderConfig(collectionId);
					config.put("idMappingType", geneIdType);

					System.out.println("init reader: " + config.toJSONString());
					ExpressionDataFileReader reader = new ExpressionDataFileReader(config);

					if (reader.doRead()) {
						reader.calculateExpStats();
						reader.runIDMappingStatistics();

						// polyomic.saveJSONFilesToCollection(collectionId,
						// reader);
						polyomic.saveJSONtoCollection(collectionId, PolyomicHandler.CONTENT_EXPRESSION + ".json",
								reader.get(PolyomicHandler.CONTENT_EXPRESSION), PolyomicHandler.CONTENT_EXPRESSION);
						polyomic.saveJSONtoCollection(collectionId, PolyomicHandler.CONTENT_MAPPING + ".json",
								reader.get(PolyomicHandler.CONTENT_MAPPING), PolyomicHandler.CONTENT_MAPPING);
						mapping = reader.get(PolyomicHandler.CONTENT_MAPPING);
					}
				}

				// System.out.println(mapping.toJSONString());
				String cntMapped = ((JSONObject) mapping.get("mapping")).get("mapped_ids").toString();
				String cntUnMapped = ((JSONObject) mapping.get("mapping")).get("unmapped_ids").toString();
				int totalGenes = Integer.parseInt(cntMapped) + Integer.parseInt(cntUnMapped);
				String msg = "";

				if (cntUnMapped.equals("0")) {
					msg = "All " + cntMapped + " genes mapped to PATRIC";
				}
				else {
					msg = cntUnMapped + " of " + totalGenes + " genes did NOT map to PATRIC";
				}
				// msg = "All 1232 genes mapped to PATRIC";
				// msg = "14 of 1232 genes did NOT map to PATRIC";

				JSONObject json = new JSONObject();
				json.put("geneMapped", Integer.parseInt(cntMapped));
				json.put("geneMissed", Integer.parseInt(cntUnMapped));
				json.put("geneTotal", totalGenes);
				json.put("msg", msg);

				response.setContentType("application/json");
				response.getWriter().write(json.toJSONString());
				response.getWriter().close();
			}
			else if (mode.equals("save_experiment")) {
				// save experiment metadata in "experiment.json"
				// title, description, organism name, ncbi_taxon_id (or
				// genome_info_id), sample count,
				//

				String collectionId = request.getParameter("collectionId");
				String _title = request.getParameter("experiment_title");
				String _desc = request.getParameter("experiment_description");
				String _organismname = request.getParameter("organism_name");
				String _pubmed_id = request.getParameter("pubmed_id");
				String _extra = request.getParameter("extra");
				String _data_type = request.getParameter("data_type");
				JSONObject jsonExtra = null;
				JSONObject jsonParsed = null;
				// JSONObject jsonOrganism = null;
				JSONObject jsonMapping = null;

				JSONParser parser = new JSONParser();
				try {
					jsonExtra = (JSONObject) parser.parse(_extra);
					jsonParsed = (JSONObject) jsonExtra.get("parsed");
					// jsonOrganism = (JSONObject) jsonExtra.get("organism");
					jsonMapping = (JSONObject) jsonExtra.get("mapping");
				}
				catch (ParseException e) {
					e.printStackTrace();
				}

				JSONObject json = new JSONObject();
				if (_title != null) {
					json.put("title", _title);
				}
				if (_desc != null) {
					json.put("desc", _desc);
				}
				if (_organismname != null) {
					json.put("organism", _organismname);
				}
				if (_pubmed_id != null) {
					json.put("pmid", _pubmed_id);
				}
				else {
					json.put("pmid", "");
				}
				if (_data_type != null) {
					json.put("data_type", _data_type);
				}
				// sample count
				if (jsonParsed != null) {
					json.put("samples", jsonParsed.get("countSamples"));
					json.put("origFileName", jsonParsed.get("origFileName"));
				}
				// organism, ncbi_taxon_id
				/*
				 * if (jsonOrganism != null) { if
				 * (jsonOrganism.containsKey("display_name")) {
				 * json.put("organism", jsonOrganism.get("display_name")); } if
				 * (jsonOrganism.containsKey("ncbi_taxon_id")) {
				 * json.put("ncbi_taxon_id", jsonOrganism.get("ncbi_taxon_id"));
				 * } }
				 */
				// genes total, genes mapped
				if (jsonMapping != null) {
					json.put("geneTotal", jsonMapping.get("geneTotal"));
					json.put("geneMapped", jsonMapping.get("geneMapped"));
					json.put("genesMissed", jsonMapping.get("geneMissed"));
				}

				json.put("expid", collectionId);
				json.put("collectionType", "ExpressionExperiment");
				json.put("owner", userName);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timestamp = sdf.format(Calendar.getInstance().getTime());
				json.put("mdate", timestamp);
				json.put("cdate", timestamp);

				if (json != null) {
					polyomic.saveJSONtoCollection(collectionId, "experiment.json", json,
							PolyomicHandler.CONTENT_EXPERIMENT);

					// save to workspace as well for better retrieval
					// set status as "available" on the collection
					polyomic.setCollectionState(collectionId, "available");
					polyomic.addWorkspaceCollection(collectionId, json);
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(json.toJSONString());
				response.getWriter().close();

			}
			/*
			 * else if (mode.equals("test")) {
			 * 
			 * String requestOut = ""; String collectionId =
			 * "ecbe6c2d-7aee-4138-88a1-d1b56e8c020b";
			 * 
			 * JSONObject reqBody = new JSONObject();
			 * polyomic.saveJSONtoCollection(collectionId, "test2.json",
			 * reqBody, polyomic.CONTENT_SAMPLE);
			 * 
			 * System.out.println("expression raw: "+polyomic.findRawFileUrl(
			 * "expression", collectionId));
			 * System.out.println("sample raw: "+polyomic
			 * .findRawFileUrl("sample", collectionId));
			 * 
			 * System.out.println("expression json: "+polyomic.findJSONUrl(
			 * "expression", collectionId));
			 * System.out.println("sample json: "+polyomic.findJSONUrl("sample",
			 * collectionId));
			 * System.out.println("idmapping json: "+polyomic.findJSONUrl
			 * ("mapping", collectionId));
			 * 
			 * requestOut =
			 * "expression json: "+polyomic.findJSONUrl("expression",
			 * collectionId);
			 * 
			 * response.setContentType("text/plain");
			 * response.getWriter().write(requestOut);
			 * response.getWriter().close(); }
			 */
		}
		else {

			// System.out.println("user:"+request.getUserPrincipal()+",mode="+mode);
			JSONObject msg = new JSONObject();

			msg.put("success", false);
			if (request.getUserPrincipal() == null) {
				msg.put("msg", "Please login before you upload your transcriptomics data.");
			}
			if (mode != null) {
				msg.put("mode", mode);
			}
			response.setContentType("application/json");
			response.getWriter().write(msg.toJSONString());
			response.getWriter().close();
		}
	}
}
