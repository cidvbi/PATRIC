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
public class GEOHandler extends DefaultHandler {

	private JSONArray list = null;

	private JSONObject docsum = null;

	private String currentElement = "";

	private boolean isReadingPubMedIds = false;

	private StringBuffer sbTitle = null;

	private StringBuffer sbTaxon = null;

	private StringBuffer sbType = null;

	private StringBuffer sbSummary = null;

	private StringBuffer sbPubMedID = null;

	private StringBuffer sbPDAT = null;

	public JSONArray getParsedJSON() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		list = new JSONArray();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase("DocSum")) {
			docsum = new JSONObject();
			sbTitle = new StringBuffer();
			sbTaxon = new StringBuffer();
			sbType = new StringBuffer();
			sbSummary = new StringBuffer();
			sbPubMedID = new StringBuffer();
			sbPDAT = new StringBuffer();
		}

		if (qName.equalsIgnoreCase("Item")) {
			if (atts.getValue("Name").equals("title") || atts.getValue("Name").equals("summary")
					|| atts.getValue("Name").equals("taxon") || atts.getValue("Name").equals("entryType")
					|| atts.getValue("Name").equals("GSE") || atts.getValue("Name").equals("GPL")
					|| atts.getValue("Name").equals("GDS") || atts.getValue("Name").equals("gdsType")
					|| atts.getValue("Name").equals("PDAT") || atts.getValue("Name").equals("n_samples")
					|| atts.getValue("Name").equals("ptechType") || atts.getValue("Name").equals("subsetInfo")
					|| atts.getValue("Name").equals("suppFile")) {
				currentElement = atts.getValue("Name");
				if (isReadingPubMedIds) {
					isReadingPubMedIds = false;
				}
			}
			else if (atts.getValue("Name").equals("PubMedIds")) {
				currentElement = "";
				isReadingPubMedIds = true;
			}
			else if (atts.getValue("Name").equals("int")) {
				currentElement = atts.getValue("Name");
			}
			else {
				currentElement = "";
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("DocSum")) {
			docsum.put("title", sbTitle.toString());
			docsum.put("taxon", sbTaxon.toString());
			docsum.put("summary", sbSummary.toString());
			// setting ID
			if (docsum.get("entryType").equals("GSE")) {
				docsum.put("ID", "GSE" + docsum.get("GSE"));
			}
			else if (docsum.get("entryType").equals("GPL")) {
				docsum.put("ID", "GPL" + docsum.get("GPL"));
			}
			else if (docsum.get("entryType").equals("GDS")) {
				docsum.put("ID", "GDS" + docsum.get("GDS"));
			}
			// setting Data Type, Exp Type, & download links
			if (docsum.get("entryType").equals("GPL")) {
				docsum.put("dataType", "Platform");
				docsum.put("expType", docsum.get("ptechType"));
				docsum.put(
						"link_soft_format",
						"ftp://ftp.ncbi.nih.gov/pub/geo/DATA/SOFT/by_platform/" + docsum.get("ID") + "/"
								+ docsum.get("ID") + "_family.soft.gz");
				docsum.put(
						"link_miniml_format",
						"ftp://ftp.ncbi.nih.gov/pub/geo/DATA/MINiML/by_platform/" + docsum.get("ID") + "/"
								+ docsum.get("ID") + "_family.xml.tgz");
				if (docsum.get("suppFile") != null && !docsum.get("suppFile").equals("")) {
					docsum.put("link_supplementary", "ftp://ftp.ncbi.nih.gov/pub/geo/DATA/supplementary/platforms/"
							+ docsum.get("ID") + "/");
				}
			}
			else if (docsum.get("entryType").equals("GSE")) {
				docsum.put("dataType", "Series");
				docsum.put("expType", sbType.toString());
				docsum.put("link_soft_format", "ftp://ftp.ncbi.nih.gov/pub/geo/DATA/SOFT/by_series/" + docsum.get("ID")
						+ "/" + docsum.get("ID") + "_family.soft.gz");
				docsum.put(
						"link_miniml_format",
						"ftp://ftp.ncbi.nih.gov/pub/geo/DATA/MINiML/by_series/" + docsum.get("ID") + "/"
								+ docsum.get("ID") + "_family.xml.tgz");
				docsum.put("link_seriesmatrix_format",
						"ftp://ftp.ncbi.nih.gov/pub/geo/DATA/SeriesMatrix/" + docsum.get("ID") + "/" + docsum.get("ID")
								+ "_series_matrix.txt.gz");
				if (docsum.get("suppFile") != null && !docsum.get("suppFile").equals("")) {
					docsum.put("link_supplementary", "ftp://ftp.ncbi.nih.gov/pub/geo/DATA/supplementary/series/"
							+ docsum.get("ID") + "/" + docsum.get("ID") + "_RAW.tar");
				}
			}
			else if (docsum.get("entryType").equals("GDS")) {
				docsum.put("dataType", "Datasets");
				docsum.put("expType", sbType.toString());
				docsum.put("link_soft_format", "ftp://ftp.ncbi.nih.gov/pub/geo/DATA/SOFT/GDS/" + docsum.get("ID")
						+ ".soft.gz");
			}
			if (docsum.get("GPL") != null && !docsum.get("GPL").equals("")) {
				if (docsum.get("GPL").toString().contains(";")) {
					docsum.put("platform", "GPL" + docsum.get("GPL").toString().replaceAll(";", ";GPL"));
				}
				else {
					docsum.put("platform", "GPL" + docsum.get("GPL"));
				}
			}
			docsum.put("pubmed_id", sbPubMedID.toString());
			docsum.put("PDAT", sbPDAT.toString());

			list.add(docsum);
			this.docsum = null;
			this.sbTitle = null;
			this.sbTaxon = null;
			this.sbType = null;
			this.sbSummary = null;
			this.sbPubMedID = null;
			this.sbPDAT = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);
		if (currentElement.equals("int")) {
			if (isReadingPubMedIds == true) {
				// docsum.put("pubmed_id", tmpVal);
				if (!tmpVal.trim().equalsIgnoreCase("")) {
					if (sbPubMedID.length() > 0) {
						sbPubMedID.append(",");
					}
					sbPubMedID.append(tmpVal);
				}
				// isReadingPubMedIds = false;
			}
		}
		else if (currentElement.equals("title")) {
			sbTitle.append(tmpVal);
		}
		else if (currentElement.equals("taxon")) {
			sbTaxon.append(tmpVal);
		}
		else if (currentElement.equals("gdsType")) {
			sbType.append(tmpVal);
		}
		else if (currentElement.equals("summary")) {
			sbSummary.append(tmpVal);
		}
		else if (currentElement.equals("PDAT")) {
			sbPDAT.append(tmpVal);
		}
		else if (!currentElement.equals("")) {
			docsum.put(currentElement, tmpVal);
		}
	}
}
