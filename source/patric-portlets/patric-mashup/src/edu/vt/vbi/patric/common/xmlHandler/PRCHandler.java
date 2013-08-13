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
package edu.vt.vbi.patric.common.xmlHandler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unchecked")
public class PRCHandler extends DefaultHandler {

	private JSONArray list = null;

	private JSONObject exp = null;

	private boolean isReadingSpecies = false;

	private int expCount = 0;

	private int sampleCount = 0;

	public JSONArray getParsedJSON() {
		return list;
	}

	public int getCount() {
		return expCount;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new JSONArray();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase("experiment")) {
			exp = new JSONObject();
			sampleCount = 0;
		}

		if (qName.equalsIgnoreCase("experiment")) {
			exp.put("expid", atts.getValue("EXPERIMENT_ID"));
			exp.put("summary", atts.getValue("SUMMARY"));
			exp.put("description", atts.getValue("DESCRIPTION"));
			exp.put("experimenttype", atts.getValue("EXPERIMENT_TYPE"));
		}
		else if (qName.equalsIgnoreCase("publication")) {
			exp.put("pubmed_id", atts.getValue("PUBMED_ID"));
		}
		else if (qName.equalsIgnoreCase("biomaterial")) {
			isReadingSpecies = true;
		}
		else if (qName.equalsIgnoreCase("expid")) {
			expCount++;
		}
		else if (qName.equalsIgnoreCase("sample")) {
			sampleCount++;
		}

		if (isReadingSpecies == true) {
			exp.put("species", atts.getValue("SPECIES_NAME") + " " + atts.getValue("STRAIN_NAME"));
			isReadingSpecies = false;
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("experiment")) {
			exp.put("samples", sampleCount);
			list.add(exp);
			this.exp = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}
}
