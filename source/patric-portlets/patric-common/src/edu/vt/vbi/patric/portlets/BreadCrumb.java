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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import org.hibernate.jmx.StatisticsService;
import org.json.simple.JSONArray;

import edu.vt.vbi.patric.cache.DataLandingGenerator;
import edu.vt.vbi.patric.cache.ENewsGenerator;
import edu.vt.vbi.patric.common.OrganismTreeBuilder;
import edu.vt.vbi.patric.dao.DBDisease;
import edu.vt.vbi.patric.dao.DBPIG;
import edu.vt.vbi.patric.dao.DBPRC;
import edu.vt.vbi.patric.dao.DBPathways;
import edu.vt.vbi.patric.dao.DBSearch;
import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.DBSummary;
import edu.vt.vbi.patric.dao.DBTranscriptomics;
import edu.vt.vbi.patric.dao.HibernateHelper;

public class BreadCrumb extends GenericPortlet {

	private final boolean initCache = true;

	/**
	 * Initialize Database connections, build genome selector caches, and
	 * generate static data feed for eNews and Watchlist
	 * 
	 */
	@Override
	public void init() throws PortletException {
		super.init();
		// now it should refer to [jboss-instance]/conf/PATRIC_DB.cfg.xml
		//
		String k = "PATRIC_DB.cfg.xml";
		HibernateHelper.buildSessionFactory(k, k);
		DBShared.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBSummary.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBSearch.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBPathways.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBDisease.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBPIG.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBPRC.setSessionFactory(HibernateHelper.getSessionFactory(k));
		DBTranscriptomics.setSessionFactory(HibernateHelper.getSessionFactory(k));
		try {
			ArrayList<?> list = MBeanServerFactory.findMBeanServer(null);
			MBeanServer server = (MBeanServer) list.get(0);
			ObjectName on = new ObjectName("Hibernate:type=statistics,application=PATRIC2");
			StatisticsService mBean = new StatisticsService();
			mBean.setSessionFactory(DBShared.getSessionFactory());
			server.registerMBean(mBean, on);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
/*
		String resourceRoot = System.getProperty("web.resource", "/");
		// File folder = new File(getPortletContext().getRealPath("/"));
		// folder.mkdirs();
		Path newLink = new File(getPortletContext().getRealPath(resourceRoot)).toPath();
		Path target = new File(getPortletContext().getRealPath("/")).toPath();

		// System.out.println("create link from ["+newLink.toString()+"] to ["+target.toString()+"]");
		try {
			Files.createSymbolicLink(newLink, target);
		}
		catch (IOException x) {
			System.out.println(x);
		}
		catch (UnsupportedOperationException x) {
			System.out.println(x);
		}
*/
		// create cache for genome selector (all bacteria level)
		try {
			if (initCache) {
				HashMap<String, String> key = new HashMap<String, String>();
				key.put("ncbi_taxon_id", "2");
				JSONArray list = OrganismTreeBuilder.buildGenomeTree(key);

				PrintWriter out = new PrintWriter(new FileWriter(getPortletContext().getRealPath("txtree-bacteria.js")));
				out.println(list.toString());
				out.close();

				list = OrganismTreeBuilder.buildGenomeList(key);
				out = new PrintWriter(new FileWriter(getPortletContext().getRealPath("azlist-bacteria.js")));
				out.println(list.toString());
				out.close();

				list = OrganismTreeBuilder.buildTaxonGenomeMapping(key);
				out = new PrintWriter(new FileWriter(getPortletContext().getRealPath("tgm-bacteria.js")));
				out.println(list.toString());
				out.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// create cache for enews
		try {
			if (initCache) {
				ENewsGenerator cacheGen = new ENewsGenerator();
				if (cacheGen.createCacheFile(getPortletContext().getRealPath("/js/enews_data.js"))) {
					System.out.println("eNews cache is generated");
				}
				else {
					System.out.println("problem in generating eNews cache");
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		// create cache for data-landing pages
		try {
			if (initCache) {
				DataLandingGenerator cacheGen = new DataLandingGenerator();
				boolean isSuccess = cacheGen.createCacheFileGenomes(getPortletContext().getRealPath(
						"/data/genomeData.json"));
				if (isSuccess) {
					System.out.println("Genome Landing data is generated");
				}
				else {
					System.out.println("failed");
				}
				isSuccess = false;
				isSuccess = cacheGen.createCacheFileFigfam(getPortletContext().getRealPath("/data/figfamData.json"));
				if (isSuccess) {
					System.out.println("Figfam Landing data is generated");
				}
				else {
					System.out.println("failed");
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void destroy() {
		try {
			ArrayList<?> list = MBeanServerFactory.findMBeanServer(null);
			MBeanServer server = (MBeanServer) list.get(0);
			ObjectName on = new ObjectName("Hibernate:type=statistics,application=PATRIC2");
			server.unregisterMBean(on);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");

		String cType = request.getParameter("context_type");

		String windowID = request.getWindowID();

		if (windowID.indexOf("ECSearch") >= 1 || windowID.indexOf("GOSearch") >= 1
				|| windowID.indexOf("GenomicFeature") >= 1 || windowID.indexOf("GenomeFinder") >= 1
				|| windowID.indexOf("PathwayFinder") >= 1 || windowID.indexOf("Downloads") >= 1
				|| windowID.indexOf("IDMapping") >= 1 || windowID.indexOf("HPITool") >= 1
				|| (windowID.indexOf("FIGfamSorter") >= 1 && windowID.indexOf("FIGfamSorterB") < 1)
				|| (windowID.indexOf("FIGfamViewer") >= 1 && windowID.indexOf("FIGfamViewerB") < 1)
				|| windowID.indexOf("ExperimentData") >= 1 || windowID.indexOf("GEO") >= 1
				|| windowID.indexOf("ArrayExpress") >= 1 || windowID.indexOf("PRC") >= 1
				|| windowID.indexOf("PRIDE") >= 1 || windowID.indexOf("Structure") >= 1
				|| windowID.indexOf("IntAct") >= 1 || windowID.indexOf("RAST") >= 1 || windowID.indexOf("MGRAST") >= 1
				|| windowID.indexOf("TranscriptomicsEnrichment") >= 1) {

			request.setAttribute("WindowID", windowID);

			PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(
					"/WEB-INF/jsp/breadcrumb/other_tabs.jsp");
			prd.include(request, response);

		}
		else {

			if (cType == null || cType.equals("")) {
				// show nothing
				PrintWriter writer = response.getWriter();
				writer.write(" ");
				writer.close();
			}
			else if (cType.equals("feature")) {
				PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(
						"/WEB-INF/jsp/breadcrumb/feature_tabs.jsp");
				prd.include(request, response);
			}
			else if (cType.equals("genome")) {
				PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(
						"/WEB-INF/jsp/breadcrumb/genome_tabs.jsp");
				prd.include(request, response);
			}
			else if (cType.equals("taxon")) {
				PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher(
						"/WEB-INF/jsp/breadcrumb/taxon_tabs.jsp");
				prd.include(request, response);
			}

		}
	}
}
