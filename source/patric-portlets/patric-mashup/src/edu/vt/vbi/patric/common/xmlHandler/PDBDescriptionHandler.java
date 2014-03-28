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

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PDBDescriptionHandler extends DefaultHandler {

	private HashMap<String, String> result;

	public PDBDescriptionHandler() {
	}

	public HashMap<String, String> getParsedData() {
		return result;
	}

	@Override
	public void startDocument() throws SAXException {
		result = new HashMap<String, String>();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (qName.equalsIgnoreCase("pdb")) {

			result.put("title", atts.getValue("title"));
			result.put("pubmedId", atts.getValue("pubmedId"));
			result.put("expMethod", atts.getValue("expMethod"));
			result.put("organism", atts.getValue("organism"));
			result.put("keywords", atts.getValue("keywords"));
			result.put("citation_authors", atts.getValue("citation_authors"));
			// nr_entities, nr_residues, nr_atoms, publish_date, revision_date
			// result.put("", atts.getValue(""));
		}
	}
}
