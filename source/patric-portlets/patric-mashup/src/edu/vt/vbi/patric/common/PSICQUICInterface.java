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
package edu.vt.vbi.patric.common;

import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.xmlHandler.IntActHandler;

public class PSICQUICInterface {

	private String baseURL = "http://www.ebi.ac.uk/Tools/webservices/psicquic/";

	private String baseURLQuery = "/webservices/current/search/query/";

	// http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/species:356?format=count
	// http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/species:356?format=xml25

	public PSICQUICInterface() {
	}

	public String getCounts(String db, String term) throws java.rmi.RemoteException {
		String result = "-1";
		try {
			String url = baseURL + db + baseURLQuery + term + "?format=count";
			// System.out.println("psicquic-count-url:" + url);

			StringWriter writer = new StringWriter();
			IOUtils.copy((new URL(url)).openStream(), writer);
			result = writer.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getResults(String db, String term, int startAt, int count) throws java.rmi.RemoteException {
		JSONObject result = new JSONObject();
		try {
			String search_count = getCounts(db, term);
			String url = baseURL + db + baseURLQuery + term + "?format=xml25&firstResult=" + startAt + "&maxResults=" + count;

			// System.out.println("psicquic-fetch-url:" + url);

			JSONArray subList = null;

			if (db.equals("intact")) {
				IntActHandler psicquicHandler = new IntActHandler(url);
				subList = psicquicHandler.getParsedJSON();
			}

			result.put("results", subList);
			result.put("total", search_count);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
