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
public class StructureHandler extends DefaultHandler {

	private JSONArray list = null;

	private JSONObject docsum = null;

	private String currentElement = "";

	private boolean isReadingOrganism = false;

	private StringBuffer sbMethod = null;

	private StringBuffer sbClass = null;

	private StringBuffer sbDesc = null;

	private StringBuffer sbDate = null;

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
			sbMethod = new StringBuffer();
			sbClass = new StringBuffer();
			sbDesc = new StringBuffer();
			sbDate = new StringBuffer();
		}
		if (qName.equalsIgnoreCase("Id")) {
			currentElement = "Id";
		}
		else if (qName.equalsIgnoreCase("Item")) {
			if (atts.getValue("Name").equals("OrganismList")) {
				currentElement = "";
				isReadingOrganism = true;
			}
			else {
				currentElement = atts.getValue("Name");
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("DocSum")) {
			docsum.put("ExpMethod", sbMethod.toString());
			docsum.put("PdbClass", sbClass.toString());
			docsum.put("PdbDescr", sbDesc.toString());
			docsum.put("PdbDepositDate", sbDate.toString());
			docsum.put("link_pdb", "http://www.pdb.org/pdb/explore/explore.do?structureId=" + docsum.get("PdbAcc"));
			docsum.put("link_ncbi", "http://www.ncbi.nlm.nih.gov/sites/entrez?db=structure&cmd=DetailsSearch&term=" + docsum.get("PdbAcc"));
			docsum.put("link_jmol", "http://www.pdb.org/pdb/explore/jmol.do?structureId=" + docsum.get("PdbAcc") + "&bionumber=1");
			list.add(docsum);
			this.docsum = null;
			this.sbMethod = null;
			this.sbClass = null;
			this.sbDesc = null;
			this.sbDate = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);
		if (currentElement.equals("string")) {
			if (isReadingOrganism == true) {
				docsum.put("Organism", tmpVal);
				isReadingOrganism = false;
			}
		}
		else if (currentElement.equals("ExpMethod")) {
			sbMethod.append(tmpVal);
		}
		else if (currentElement.equals("PdbClass")) {
			sbClass.append(tmpVal);
		}
		else if (currentElement.equals("PdbDescr")) {
			sbDesc.append(tmpVal);
		}
		else if (currentElement.equals("PdbDepositDate")) {
			sbDate.append(tmpVal);
		}
		else if (!currentElement.equals("")) {
			docsum.put(currentElement, tmpVal);
		}
	}
}
