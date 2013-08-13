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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.theseed.servers.SAPserver;

import edu.vt.vbi.patric.common.CRFeature;
import edu.vt.vbi.patric.common.CRResultSet;
import edu.vt.vbi.patric.common.CRTrack;
import edu.vt.vbi.patric.common.ExcelHelper;
import edu.vt.vbi.patric.common.SiteHelper;
import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.ResultType;

public class CompareRegionViewer extends GenericPortlet {

	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {

		new SiteHelper().setHtmlMetaElements(request, response, "Compare Region Viewer");

		response.setContentType("text/html");
		PortletRequestDispatcher prd = null;
		prd = getPortletContext().getRequestDispatcher("/WEB-INF/CRViewer.jsp");
		prd.include(request, response);
	}

	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		String mode = request.getParameter("mode");

		if (mode.equals("getRefSeqs")) {
			printRefSeqInfo(request, response);
		}
		else if (mode.equals("getTrackList")) {
			printTrackList(request, response);
		}
		else if (mode.equals("getTrackInfo")) {
			printTrackInfo(request, response);
		}
		else if (mode.equals("downloadInExcel")) {
			exportInExcelFormat(request, response);
		}
		else {
			response.getWriter().write("wrong param");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void printRefSeqInfo(ResourceRequest request, ResourceResponse response) throws IOException {

		String _cType = request.getParameter("cType");
		String _cId = request.getParameter("cId");
		String _feature = request.getParameter("feature"); // pin feature
		String _window = request.getParameter("window"); // window size

		DBSummary conn_summary = new DBSummary();

		// if pin feature is not given, retrieve from the database based on na_feature_id
		if (_feature == null && (_cType != null && _cType.equals("feature") && _cId != null)) {
			HashMap<String, ResultType> pseedMap = conn_summary.getPSeedMapping("PATRIC", _cId);
			_feature = pseedMap.get(_cId).get("pseed_id");
		}

		if (_feature != null && _feature.equals("") == false && _window != null) {
			
			JSONObject seq = new JSONObject();
			seq.put("length", (Integer.parseInt(_window)));
			seq.put("name", _feature);
			seq.put("seqDir", "");
			seq.put("start", 1);
			seq.put("end", (Integer.parseInt(_window)));
			seq.put("seqChunkSize", 20000);
			
			JSONArray json = new JSONArray();
			json.add(seq);
			
			response.setContentType("application/json");
			json.writeJSONString(response.getWriter());
			response.getWriter().close();
		}
		else {
			response.getWriter().write("[]");
		}
	}

	@SuppressWarnings("unchecked")
	private void printTrackList(ResourceRequest request, ResourceResponse response) throws IOException {

		String _cType = request.getParameter("cType");
		String _cId = request.getParameter("cId");
		String _feature = request.getParameter("feature"); // pin feature
		String _window = request.getParameter("window"); // window size
		int _numRegion = Integer.parseInt(request.getParameter("regions")); // number of genomes to compare
		int _numRegion_buffer = 10; // number of genomes to use as a buffer in case that PATRIC has no genome data,
									// which was retrieved from API
		String _key = "";

		DBSummary conn_summary = new DBSummary();
		PortletSession session = request.getPortletSession(true);

		// if pin feature is not given, retrieve from the database based on na_feature_id
		if (_feature == null && (_cType != null && _cType.equals("feature") && _cId != null)) {
			HashMap<String, ResultType> pseedMap = conn_summary.getPSeedMapping("PATRIC", _cId);
			_feature = pseedMap.get(_cId).get("pseed_id");
		}
		
		if (_feature != null && _feature.equals("") == false && _window != null) {
			CRResultSet crRS = null;

			try {
				SAPserver sapling = new SAPserver("http://servers.nmpdr.org/pseed/sapling/server.cgi");
				crRS = new CRResultSet(_feature, sapling.compared_regions(_feature, _numRegion + _numRegion_buffer,
						Integer.parseInt(_window) / 2));

				Random g = new Random();
				int random = g.nextInt();
				_key = "key" + random;
				session.setAttribute(_key, crRS);
				session.setAttribute("window_size", _window);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			
			JSONObject trackList = new JSONObject();
			JSONArray tracks = new JSONArray();
			JSONObject trStyle = new JSONObject();
			trStyle.put("className", "feature5");
			trStyle.put("showLabels", false);
			trStyle.put("label", "function( feature ) { return feature.get('locus_tag'); }");
			JSONObject trHooks = new JSONObject();
			trHooks.put("modify", "function(track, feature, div) { div.style.backgroundColor = ['red','green','blue','orange','purple','brown','gray'][feature.get('phase')];}"); 
			
			//query genome metadata
			HashMap<String, ResultType> gMetaData = conn_summary.getGenomeMetadata(crRS.getGenomeNames());
			
			int count_genomes = 1;
			if (crRS != null && crRS.getGenomeNames().size() > 0) {
				for (Integer idx : crRS.keySet()) {
					if (count_genomes > _numRegion) {
						break;
					}
					CRTrack crTrack = crRS.get(idx);
					int chk = conn_summary.getPSeedGenomeCount(crTrack.getGenomeID());
					if (chk > 0) {
						count_genomes++;
						crRS.addToDefaultTracks(crTrack);
						JSONObject tr = new JSONObject();
						tr.put("style", trStyle);
						tr.put("hooks", trHooks);
						tr.put("type", "FeatureTrack");
						tr.put("tooltip", "<div style='line-height:1.7em'><b>{locus_tag}</b> | {refseq} | {gene}<br>{product}<br>{type}:{start}...{end} ({strand_str})<br> <i>Click for detail information</i></div>");
						tr.put("urlTemplate", "/portal/portal/patric/CompareRegionViewer/CRWindow?action=b&cacheability=PAGE&mode=getTrackInfo&key="
								+ _key + "&rowId=" + crTrack.getRowID() + "&format=.json");
						tr.put("key", crTrack.getGenomeName());
						tr.put("label", "CR" + idx);
						tr.put("dataKey", _key);
						JSONObject metaData = new JSONObject();
						ResultType g = gMetaData.get(crTrack.getGenomeName());
						if (g != null && g.containsKey("isolation_country")) {
							metaData.put("Isolation Country", g.get("isolation_country"));
						}
						if (g != null && g.containsKey("host_name")) {
							metaData.put("Host Name", g.get("host_name"));
						}
						if (g != null && g.containsKey("disease")) {
							metaData.put("Disease", g.get("disease"));
						}
						if (g != null && g.containsKey("collection_date")) {
							metaData.put("Collection Date", g.get("collection_date"));
						}
						if (g != null && g.containsKey("completion_date")) {
							metaData.put("Completion Date", g.get("completion_date"));
						}
						tr.put("metadata", metaData);
						tracks.add(tr);
					}
				}
			}
			trackList.put("tracks", tracks);
			
			JSONObject facetedTL = new JSONObject();
			JSONArray dpColumns = new JSONArray();
			dpColumns.addAll(Arrays.asList(new String[] {"key", "Isolation Country", "Host Name", "Disease", "Collection Date", "Completion Date"}));
			facetedTL.put("displayColumns", dpColumns);
			facetedTL.put("type", "Faceted");
			facetedTL.put("escapeHTMLInData", false);
			trackList.put("trackSelector", facetedTL);
			trackList.put("defaultTracks", crRS.getDefaultTracks());

			response.setContentType("application/json");
			trackList.writeJSONString(response.getWriter());
			response.getWriter().close();
		}
		else {
			response.getWriter().write("{}");
		}
	}

	@SuppressWarnings("unchecked")
	private void printTrackInfo(ResourceRequest request, ResourceResponse response) throws IOException {
		String _rowID = request.getParameter("rowId");
		String _key = request.getParameter("key");

		PortletSession session = request.getPortletSession(true);
		CRResultSet crRS = (CRResultSet) session.getAttribute(_key);
		int _window_size = Integer.parseInt(session.getAttribute("window_size").toString());

		String pin_strand = crRS.getPinStrand();
		CRTrack crTrack = crRS.get(Integer.parseInt(_rowID));
		String pseed_ids = crTrack.getPSEEDIDs();

		int features_count = 0;
		try {
			crTrack.relocateFeatures(_window_size, pin_strand);
			Collections.sort(crTrack);
			features_count = crTrack.size();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		DBSummary conn_summary = new DBSummary();
		HashMap<String, ResultType> pseedMap = conn_summary.getPSeedMapping("pseed", pseed_ids);
		// System.out.println("pseed_map:"+pseedMap.toString());

		// formatting
		JSONArray nclist = new JSONArray();
		CRFeature feature = null;
		ResultType feature_patric = null;
		for (int i = 0; i < features_count; i++) {

			feature = crTrack.get(i);
			feature_patric = pseedMap.get(feature.getfeatureID());
			// System.out.println(feature.getfeatureID());

			if (feature_patric != null) {
				
				JSONArray f = new JSONArray();
				f.add(0);
				f.add(feature.getStartPosition());
				f.add(feature.getStartString());
				f.add(feature.getEndPosition());
				f.add((feature.getStrand().equalsIgnoreCase("-")) ? -1 : 1);
				f.add(feature.getStrand());
				f.add(feature_patric.get("na_feature_id"));
				f.add(feature_patric.get("locus_tag"));
				f.add("PATRIC");
				f.add(feature_patric.get("feature_type"));
				f.add(feature_patric.get("product").replace("\"", "\\\""));
				f.add(feature_patric.get("gene"));
				f.add(feature_patric.get("refseq_locus_tag"));
				f.add(feature_patric.get("genome_name"));
				f.add(feature_patric.get("accession"));
				f.add(feature.getPhase());
				nclist.add(f);
			}
		}
		//formatter.close();

		JSONObject track = new JSONObject();
		track.put("featureCount", features_count);
		track.put("formatVersion", 1);
		track.put("histograms", new JSONObject());
		
		JSONObject intervals = new JSONObject();
		JSONArray _clses = new JSONArray();
		JSONObject _cls = new JSONObject();
		_cls.put("attributes", Arrays.asList(new String[] {"Start", "Start_str", "End", "Strand", "strand_str", "id", "locus_tag", "source", "type", "product", "gene", "refseq", "genome_name", "accession", "phase"}));
		_cls.put("isArrayAttr", new JSONObject());
		_clses.add(_cls);
		intervals.put("classes",_clses);
		intervals.put("lazyClass", 5);
		intervals.put("minStart", 1);
		intervals.put("maxEnd", 20000);
		intervals.put("urlTemplate", "lf-{Chunk}.json");
		intervals.put("nclist", nclist);
		track.put("intervals", intervals);
		
		response.setContentType("application/json");
		track.writeJSONString(response.getWriter());
		response.getWriter().close();
	}

	private void exportInExcelFormat(ResourceRequest request, ResourceResponse response) throws IOException {
		PortletSession session = request.getPortletSession(true);
		String _key = request.getParameter("key");
		CRResultSet crRS = (CRResultSet) session.getAttribute(_key);

		CRTrack crTrack = null;
		CRFeature crFeature = null;
		String genome_name = null;

		ArrayList<String> _tbl_header = new ArrayList<String>();
		ArrayList<String> _tbl_field = new ArrayList<String>();
		// JSONArray _tbl_source = new JSONArray();
		// ArrayList<HashMap<String, Object>> _tbl_source = new ArrayList<HashMap<String, Object>>();
		ArrayList<ResultType> _tbl_source = new ArrayList<ResultType>();

		_tbl_header.addAll(Arrays.asList(new String[] { "Genome Name", "Feature", "Start", "End", "Strand", "FigFam",
				"Product", "Group" }));
		_tbl_field.addAll(Arrays.asList(new String[] { "genome_name", "feature_id", "start", "end", "strand",
				"figfam_id", "product", "group_id" }));

		if (crRS != null && crRS.getGenomeNames().size() > 0) {
			for (Integer idx : crRS.keySet()) {
				crTrack = crRS.get(idx);
				genome_name = crTrack.getGenomeName();
				for (int i = 0; i < crTrack.size(); i++) {
					crFeature = crTrack.get(i);
					ResultType f = new ResultType();
					f.put("genome_name", genome_name);
					f.put("feature_id", crFeature.getfeatureID());
					f.put("start", crFeature.getStartPosition());
					f.put("end", crFeature.getEndPosition());
					f.put("strand", crFeature.getStrand());
					f.put("figfam_id", crFeature.getFigfam());
					f.put("product", crFeature.getProduct());
					f.put("group_id", crFeature.getGrpNum());
					_tbl_source.add(f);
				}
			}
		}
		//print out to xlsx file
		response.setContentType("application/octetstream");
		response.setProperty("Content-Disposition", "attachment; filename=\"CompareRegionView.xlsx\"");

		OutputStream outs = response.getPortletOutputStream();// .getOutputStream();
		ExcelHelper excel = new ExcelHelper("xssf", _tbl_header, _tbl_field, _tbl_source);
		excel.buildSpreadsheet();
		excel.writeSpreadsheettoBrowser(outs);
	}
}
