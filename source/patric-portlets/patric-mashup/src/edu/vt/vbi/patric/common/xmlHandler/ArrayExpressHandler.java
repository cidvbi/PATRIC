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

public class ArrayExpressHandler extends DefaultHandler {

	private JSONArray list = null;

	private JSONObject exp = null;

	private String currentElement = "";

	private boolean isReadingBibliography = false;

	private StringBuffer sbDescription = null;

	private StringBuffer sbTitle = null;

	private StringBuffer sbSpecies = null;

	private StringBuffer sbExpType = null;

	private StringBuffer sbExpDesign = null;

	private StringBuffer sbAccession = null;

	private StringBuffer sbPubMedID = null;

	private boolean isReadingID = false;

	private boolean isReadingExpAccession = false;

	private boolean isReadingExpName = false;

	public JSONArray getParsedJSON() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new JSONArray();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase("experiment")) {
			exp = new JSONObject();
			isReadingID = true;
			isReadingExpAccession = true;
			isReadingExpName = true;
			isReadingBibliography = false;
			sbAccession = new StringBuffer();
			sbDescription = new StringBuffer();
			sbTitle = new StringBuffer();
			sbSpecies = new StringBuffer();
			sbExpType = new StringBuffer();
			sbExpDesign = new StringBuffer();
			sbPubMedID = new StringBuffer();
		}

		if (qName.equalsIgnoreCase("accession") || qName.equalsIgnoreCase("name")
				|| qName.equalsIgnoreCase("releasedate") || qName.equalsIgnoreCase("species")
				|| qName.equalsIgnoreCase("assays") || qName.equalsIgnoreCase("samples")
				|| qName.equalsIgnoreCase("experimenttype") || qName.equalsIgnoreCase("experimentdesign")
				|| qName.equalsIgnoreCase("text") || qName.equalsIgnoreCase("secondaryaccession")
				|| qName.equalsIgnoreCase("id")) {
			currentElement = qName;
		}
		else if (qName.equalsIgnoreCase("bibliography")) {
			currentElement = "";
			isReadingBibliography = true;
			isReadingExpAccession = false;
		}
		else if (qName.equalsIgnoreCase("arraydesign")) {
			isReadingExpAccession = false;
		}
		else {
			currentElement = "";
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("experiment")) {
			exp.put("accession", sbAccession.toString());
			exp.put("description", sbDescription.toString());
			exp.put("name", sbTitle.toString());
			exp.put("species", sbSpecies.toString());
			exp.put("experimenttype", sbExpType.toString());
			exp.put("experimentdesign", sbExpDesign.toString());
			exp.put("pubmed_id", sbPubMedID.toString());
			// download link
			exp.put("link_data", "http://www.ebi.ac.uk/microarray-as/ae/files/" + exp.get("accession"));

			list.add(exp);
			this.exp = null;
			this.sbAccession = null;
			this.sbDescription = null;
			this.sbTitle = null;
			this.sbSpecies = null;
			this.sbExpType = null;
			this.sbExpDesign = null;
			this.sbPubMedID = null;
		}
		else if (qName.equalsIgnoreCase("bibliography")) {
			isReadingBibliography = false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);

		if (currentElement.equals("text")) {
			sbDescription.append(tmpVal);
		}
		else if (currentElement.equals("species")) {
			sbSpecies.append(tmpVal);
		}
		else if (currentElement.equals("experimenttype")) {
			sbExpType.append(tmpVal);
		}
		else if (currentElement.equals("experimentdesign")) {
			if (sbExpDesign.length() > 0) {
				sbExpDesign.append(", ");
			}
			sbExpDesign.append(tmpVal);
		}
		else if (currentElement.equals("name") && isReadingExpName == true) {
			sbTitle.append(tmpVal);
			isReadingExpName = false;
		}
		else if (currentElement.equals("accession") && isReadingExpAccession == true) {
			sbAccession.append(tmpVal);
		}
		else if (currentElement.equals("accession") && isReadingBibliography == true) {
			if (!tmpVal.trim().equalsIgnoreCase("")) {
				if (sbPubMedID.length() > 0) {
					sbPubMedID.append(",");
				}
				sbPubMedID.append(tmpVal);
			}
		}
		else if (currentElement.equals("id") && isReadingID == true) {
			exp.put("id", tmpVal);
			isReadingID = false;
		}
		else if (currentElement.equals("name") || currentElement.equals("accession") || currentElement.equals("id")) {
			// skip
			// System.out.println(currentElement+":"+tmpVal);
		}
		else if (!currentElement.equals("")) {
			exp.put(currentElement, tmpVal);
		}
	}
}
