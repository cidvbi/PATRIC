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
package edu.vt.vbi.patric.common.xmlHandler;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PDBAnnotationsHandler extends DefaultHandler {

	private ArrayList<HashMap<String, String>> result;

	private HashMap<String, String> feature;

	private String currentElement = "";

	private StringBuffer sbType;

	private StringBuffer sbNote;

	private StringBuffer sbLink;

	public PDBAnnotationsHandler() {
	}

	public ArrayList<HashMap<String, String>> getParsedData() {
		return result;
	}

	@Override
	public void startDocument() throws SAXException {
		result = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (qName.equalsIgnoreCase("feature")) {
			feature = new HashMap<String, String>();
			sbType = new StringBuffer();
			sbNote = new StringBuffer();
			sbLink = new StringBuffer();

			feature.put("id", atts.getValue("id"));
		}
		else if (qName.equalsIgnoreCase("type") || qName.equalsIgnoreCase("method") || qName.equalsIgnoreCase("start")
				|| qName.equalsIgnoreCase("end") || qName.equalsIgnoreCase("note") || qName.equalsIgnoreCase("link")) {
			currentElement = qName;
		}
		else {
			currentElement = "";
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("feature")) {

			feature.put("type", sbType.toString());
			feature.put("note", sbNote.toString());
			feature.put("link", sbLink.toString());

			if (feature.get("METHOD").toLowerCase().matches("scop|pfam|cath|interpro")) {
				if (feature.get("METHOD").toLowerCase().matches("cath|pfam|interpro")) {
					String[] p = feature.get("note").split(" ");
					if (p.length > 0) {
						feature.put("ext_id", p[0]);
					}
				}
				else {
					// scop
					String[] p = feature.get("note").split(" | ");
					if (p.length > 0) {
						feature.put("ext_id", p[0]);
					}
				}
				result.add(feature);
			}
			feature = null;
			sbType = null;
			sbNote = null;
			sbLink = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);
		if (!currentElement.equals("")) {
			if (currentElement.equalsIgnoreCase("type")) {
				sbType.append(tmpVal);
			}
			else if (currentElement.equalsIgnoreCase("note")) {
				sbNote.append(tmpVal);
			}
			else if (currentElement.equalsIgnoreCase("link")) {
				sbLink.append(tmpVal);
			}
			else {
				feature.put(currentElement, tmpVal);
			}
		}
	}
}
