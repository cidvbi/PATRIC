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
package edu.vt.vbi.patric.common;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import edu.vt.vbi.patric.common.xmlHandler.ESearchHandler;
import edu.vt.vbi.patric.common.xmlHandler.GEOHandler;
import edu.vt.vbi.patric.common.xmlHandler.PeptidomeHandler;
import edu.vt.vbi.patric.common.xmlHandler.PubMedHandler;
import edu.vt.vbi.patric.common.xmlHandler.StructureHandler;

public class EutilInterface {

	private String baseURLESearch = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi";

	private String baseURLESummary = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi";

	public static final int TIMEOUT_CONN = 3000;

	public static final int TIMEOUT_READ = 5000;

	private XMLReader xmlReader = null;

	public EutilInterface() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			xmlReader = spf.newSAXParser().getXMLReader();
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
	}

	public HashMap<String, String> getCounts(String db, String term, String search_opt)
			throws java.rmi.RemoteException, SocketTimeoutException, IOException, Exception {
		// defer exception handling to getResult()

		ESearchHandler esearchHandler = new ESearchHandler();
		xmlReader.setContentHandler(esearchHandler);

		String url = baseURLESearch + "?usehistory=y&db=" + db + "&term=" + term + search_opt;
		URLConnection c = new URL(url).openConnection();
		c.setConnectTimeout(TIMEOUT_CONN);
		c.setReadTimeout(TIMEOUT_READ);
		c.setUseCaches(true);
		xmlReader.parse(new InputSource(c.getInputStream()));

		return esearchHandler.getParsedData();
	}

	@SuppressWarnings("unchecked")
	public JSONObject getResults(String db, String term, String search_opt, String summary_opt, int startAt, int count)
			throws java.rmi.RemoteException {
		JSONObject result = new JSONObject();
		try {
			HashMap<String, String> esearch_result = getCounts(db, term, search_opt);

			if (count < 0) {
				count = Integer.parseInt(esearch_result.get("Count"));
			}

			URL url = new URL(baseURLESummary + "?db=" + db + "&query_key=" + esearch_result.get("QueryKey")
					+ "&WebEnv=" + esearch_result.get("WebEnv") + "&retstart=" + startAt + "&retmax=" + count
					+ summary_opt);
			URLConnection c = url.openConnection();
			c.setConnectTimeout(TIMEOUT_CONN);
			c.setReadTimeout(TIMEOUT_READ);
			c.setUseCaches(true);

			System.out.println("esummary-url:" + url.toString());

			JSONArray subList = null;
			if (db.equals("pubmed") || db.equals("pmc")) {
				PubMedHandler eutilHandler = new PubMedHandler();
				xmlReader.setContentHandler(eutilHandler);
				xmlReader.parse(new InputSource(c.getInputStream()));
				subList = eutilHandler.getParsedJSON();
			}
			else if (db.equals("gds")) {
				GEOHandler eutilHandler = new GEOHandler();
				xmlReader.setContentHandler(eutilHandler);
				xmlReader.parse(new InputSource(c.getInputStream()));
				subList = eutilHandler.getParsedJSON();
			}
			else if (db.equals("pepdome")) {
				PeptidomeHandler eutilHandler = new PeptidomeHandler();
				xmlReader.setContentHandler(eutilHandler);
				xmlReader.parse(new InputSource(c.getInputStream()));
				subList = eutilHandler.getParsedJSON();
			}
			else if (db.equals("structure")) {
				StructureHandler eutilHandler = new StructureHandler();
				xmlReader.setContentHandler(eutilHandler);
				xmlReader.parse(new InputSource(c.getInputStream()));
				subList = eutilHandler.getParsedJSON();
			}
			result.put("results", subList);
			result.put("total", esearch_result.get("Count"));
		}
		catch (SocketTimeoutException toex) {
			System.out.println("[EUTIL-ERROR]:SocketTimeoutException");
			return null;
		}
		catch (IOException ioex) {
			System.out.println("[EUTIL-ERROR]:IOException");
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}
}
