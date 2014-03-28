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

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import edu.vt.vbi.patric.cache.DataLandingGenerator;
import edu.vt.vbi.patric.cache.ENewsGenerator;
import edu.vt.vbi.patric.dao.DBShared;

public class P2Management extends GenericPortlet {

	private String mode = null;

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		mode = request.getParameter("mode");

		if (mode == null || mode.equals("")) {
			out.println("<h3>Usage</h3>");
			out.println("?mode=");
			out.println("<ul>");
			out.println("<li>updateENews: update fontpage eNews feed</li>");
			out.println("<li>updateENewsDev: update fontpage eNews feed from Dev-version WordPress</li>");
			out.println("<li>checkCurrentDB: show current database name</li>");
			out.println("<li>updateDataLandingGenomicFeatures: update local cache for feature landing pages</li>");
			out.println("<li>updateDataLandingGenomes: update local cache for genome landing pages</li>");
			out.println("<li>updateDataLandingProteinFamilies: update local cache for protein families landing pages</li>");
			out.println("<li>updateDataLandingTranscriptomics: update local cache for transcriptomics landing pages</li>");
			// out.println("<li>updateDataLandingProteomics: update local cache for proteomics landing pages</li>");
			// out.println("<li>updateDataLandingPPInteractions: update local cache for protein protein interaction landing pages</li>");
			out.println("<li>updateDataLandingPathways: update local cache for pathway landing pages</li>");
			out.println("</ul>");
		}
		else if (mode.equals("checkCurrentDB")) {
			String db_name = DBShared.getUniqueDBName();
			out.println("<ul>");
			out.println("<li>Your current db is " + db_name + " .</li>");
			out.println("</ul>");
		}
		else if (mode.equals("updateENews") || mode.equals("updateENewsDev")) {
			String prodENewsURL = "http://enews.patricbrc.org/php/rssAdapter.php";
			String devENewsURL = "http://patricenews-dev.vbi.vt.edu/php/rssAdapter.php";
			String eNewsURL = prodENewsURL;
			if (mode.equals("updateENewsDev")) {
				eNewsURL = devENewsURL;
			}
			ENewsGenerator cacheGen = new ENewsGenerator();
			cacheGen.setSourceURL(eNewsURL);
			boolean isSuccess = cacheGen.createCacheFile(getPortletContext().getRealPath("/js/enews_data.js"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>eNews has been updated.(" + mode + ")</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingGenomicFeatures")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFileGenomicFeatures(getPortletContext().getRealPath("/data/features.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Feature Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingGenomes")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFileGenomes(getPortletContext().getRealPath("/data/genomes.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Genome Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingProteinFamilies")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFileProteinFamilies(getPortletContext().getRealPath("/data/proteinfamilies.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>ProteinFamilies Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingTranscriptomics")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFileTranscriptomics(getPortletContext().getRealPath("/data/transcriptomics.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Transcriptomics Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingProteomics")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFileProteomics(getPortletContext().getRealPath("/data/proteomics.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Proteomics Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingPPInteractions")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFilePPInteractions(getPortletContext().getRealPath("/data/ppinteractions.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Protein Protein Interactions Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else if (mode.equals("updateDataLandingPathways")) {
			DataLandingGenerator cacheGen = new DataLandingGenerator();
			boolean isSuccess = cacheGen.createCacheFilePathways(getPortletContext().getRealPath("/data/pathways.json"));

			out.println("<ul>");
			if (isSuccess) {
				out.println("<li>Pathways Landing data is generated</li>");
			}
			else {
				out.println("<li>failed</li>");
			}
			out.println("</ul>");
		}
		else {
			out.println("wrong param:" + mode);
		}
	}
}
