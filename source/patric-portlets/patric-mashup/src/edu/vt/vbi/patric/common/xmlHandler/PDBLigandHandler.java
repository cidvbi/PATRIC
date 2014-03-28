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

public class PDBLigandHandler extends DefaultHandler {

	private ArrayList<HashMap<String, String>> result;

	private String currentElement = "";

	private HashMap<String, String> ligand;

	private StringBuffer sbChemicalName = null;

	private StringBuffer sbFormula = null;

	private StringBuffer sbSmiles = null;

	public PDBLigandHandler() {
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

		if (qName.equalsIgnoreCase("ligand")) {
			ligand = new HashMap<String, String>();
			sbChemicalName = new StringBuffer();
			sbFormula = new StringBuffer();
			sbSmiles = new StringBuffer();

			ligand.put("chemicalID", atts.getValue("chemicalID"));
		}
		else if (qName.equalsIgnoreCase("chemicalName") || qName.equalsIgnoreCase("formula") || qName.equalsIgnoreCase("smiles")) {
			currentElement = qName;
		}
		else {
			currentElement = "";
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("ligand")) {

			ligand.put("chemicalName", sbChemicalName.toString().trim());
			ligand.put("formula", sbFormula.toString().trim());
			ligand.put("smiles", sbSmiles.toString().trim());

			result.add(ligand);
			ligand = null;
			sbChemicalName = null;
			sbFormula = null;
			sbSmiles = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);

		if (!currentElement.equals("")) {
			if (currentElement.equals("chemicalName") && sbChemicalName != null) {
				sbChemicalName.append(tmpVal);
			}
			else if (currentElement.equals("formula") && sbFormula != null) {
				sbFormula.append(tmpVal);
			}
			else if (currentElement.equals("smiles") && sbSmiles != null) {
				sbSmiles.append(tmpVal);
			}
		}
	}
}
