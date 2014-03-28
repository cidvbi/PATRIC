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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unchecked")
public class PeptidomeHandler extends DefaultHandler {

	private JSONArray list = null;

	private JSONObject item = null;

	private String currentElement = "";

	private StringBuffer sbSummary = null;

	private StringBuffer sbTitle = null;

	private boolean isReadingPubMed = false;

	private boolean isReadingTitle = false;

	private boolean isReadingAccession = true;

	private StringBuffer sbAccession = null;

	private StringBuffer sbTaxName = null;

	private StringBuffer sbSampleCount = null;

	private StringBuffer sbProteinCount = null;

	private StringBuffer sbPeptideCount = null;

	private StringBuffer sbSpectraCount = null;

	public JSONArray getParsedJSON() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new JSONArray();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase("DocumentSummary")) {
			item = new JSONObject();
			sbSummary = new StringBuffer();
			sbTitle = new StringBuffer();
			isReadingTitle = true;
			isReadingAccession = true;
			sbAccession = new StringBuffer();
			sbTaxName = new StringBuffer();
			sbSampleCount = new StringBuffer();
			sbProteinCount = new StringBuffer();
			sbPeptideCount = new StringBuffer();
			sbSpectraCount = new StringBuffer();
		}

		if (qName.equalsIgnoreCase("accession") || qName.equalsIgnoreCase("entryType") || qName.equalsIgnoreCase("title")
				|| qName.equalsIgnoreCase("summary") || qName.equalsIgnoreCase("int") || qName.equalsIgnoreCase("taxname")
				|| qName.equalsIgnoreCase("proteincount") || qName.equalsIgnoreCase("peptidecount") || qName.equalsIgnoreCase("spectracount")
				|| qName.equalsIgnoreCase("samplecount")) {
			currentElement = qName;
		}
		else if (qName.equalsIgnoreCase("pubmedids")) {
			currentElement = "";
			isReadingPubMed = true;
		}
		else {
			currentElement = "";
		}

	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("DocumentSummary")) {
			item.put("summary", sbSummary.toString());
			item.put("title", sbTitle.toString());
			item.put("Accession", sbAccession.toString());
			item.put("TaxName", sbTaxName.toString());
			item.put("SampleCount", sbSampleCount.toString());
			item.put("ProteinCount", sbProteinCount.toString());
			item.put("PeptideCount", sbPeptideCount.toString());
			item.put("SpectraCount", sbSpectraCount.toString());
			// link
			item.put("link_data_file", "ftp://ftp.ncbi.nih.gov/pub/peptidome/studies/PSEnnn/" + item.get("Accession") + "/");
			list.add(item);
			this.item = null;
			this.sbSummary = null;
			this.sbTitle = null;
			this.sbAccession = null;
			this.sbTaxName = null;
			this.sbSampleCount = null;
			this.sbProteinCount = null;
			this.sbPeptideCount = null;
			this.sbSpectraCount = null;
		}
		else if (qName.equalsIgnoreCase("pubmedids")) {
			isReadingPubMed = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);

		if (currentElement.equals("Summary")) {
			sbSummary.append(tmpVal);
		}
		else if (currentElement.equals("Title") && isReadingTitle == true) {
			sbTitle.append(tmpVal);
			isReadingTitle = false;
		}
		else if (currentElement.equals("Accession") && isReadingAccession == true) {
			sbAccession.append(tmpVal);
			isReadingAccession = false;
		}
		else if (currentElement.equals("TaxName")) {
			sbTaxName.append(tmpVal);
		}
		else if (currentElement.equals("SampleCount")) {
			sbSampleCount.append(tmpVal);
		}
		else if (currentElement.equals("ProteinCount")) {
			sbProteinCount.append(tmpVal);
		}
		else if (currentElement.equals("PeptideCount")) {
			sbPeptideCount.append(tmpVal);
		}
		else if (currentElement.equals("SpectraCount")) {
			sbSpectraCount.append(tmpVal);
		}
		else if (currentElement.equals("int") && isReadingPubMed == true) {
			item.put("pubmed_id", tmpVal);
			isReadingPubMed = false;
		}
		else if (currentElement.equals("int") || currentElement.equals("Title")) {
			// skip
			// System.out.println(currentElement+":"+tmpVal);
		}
		else if (!currentElement.equals("")) {
			item.put(currentElement, tmpVal);
		}
	}
}
