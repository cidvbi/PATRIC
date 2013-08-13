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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PRIDEInterface {

	private String baseURL = "http://www.ebi.ac.uk/pride/biomart/martservice";

	private StringBuffer xmlQueryString = null;

	public PRIDEInterface() {
		xmlQueryString = new StringBuffer();
	}

	private void setQueryString(String species) {
		xmlQueryString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlQueryString.append("<!DOCTYPE Query>");
		xmlQueryString
				.append("<Query  virtualSchemaName = \"default\" formatter = \"TSV\" header=\"0\" uniqueRows=\"0\" count=\"\" datasetConfigVersion=\"0.6\" >");
		xmlQueryString.append("<Dataset name=\"pride\" interface=\"default\" >");
		xmlQueryString.append("<Filter name=\"species_filter\" value=\"" + species + "\" />");
		xmlQueryString
				.append("	<Attribute name = \"project_id\" /><Attribute name = \"project_name\" /><Attribute name = \"experiment_ac\" />");
		xmlQueryString
				.append("	<Attribute name = \"experiment_title\" /><Attribute name = \"experiment_short_title\" />");
		xmlQueryString
				.append("	<Attribute name = \"pubmed_id\" /><Attribute name = \"newt_name\" /><Attribute name = \"newt_ac\" />");
		xmlQueryString.append("</Dataset>");
		xmlQueryString.append("</Query>");
	}

	@SuppressWarnings("unchecked")
	public JSONObject getResults(String species) throws java.rmi.RemoteException {
		JSONObject result = new JSONObject();
		result.put("hasData", false);
		if (species.equals("")) {
			System.out.println("PRIDE-query:No species name was given.");
			JSONArray subList = new JSONArray();
			result.put("results", subList);
			result.put("total", subList.size());
			result.put("hasData", true);
		}
		else {
			try {
				setQueryString(species);
				// setQueryString("Salmonella typhimurium");
				String param = "query=" + xmlQueryString.toString();
				System.out.println("PRIDE-query:" + xmlQueryString.toString());

				URL url = new URL(baseURL);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
				conn.setReadTimeout(EutilInterface.TIMEOUT_READ);
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(param);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				JSONArray subList = new JSONArray();
				while ((line = rd.readLine()) != null) {
					String columns[] = line.split("\t");
					JSONObject row = new JSONObject();
					try {
						row.put("project_id", columns[0]);
						row.put("project_name", columns[1]);
						row.put("experiment_ac", columns[2]);
						row.put("experiment_title", columns[3]);
						row.put("experiment_short_title", columns[4]);
						row.put("pubmed_id", columns[5]);
						row.put("newt_name", columns[6]);
						row.put("newt_ac", columns[7]);
						row.put("link_data_file", "ftp://ftp.ebi.ac.uk/pub/databases/pride/PRIDE_Exp_Complete_Ac_"
								+ columns[2] + ".xml.gz");
					}
					catch (ArrayIndexOutOfBoundsException ex) {
					}
					subList.add(row);
				}
				wr.close();
				rd.close();

				result.put("results", subList);
				result.put("total", subList.size());
				result.put("hasData", true);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
			}
		}
		return result;
	}
}
