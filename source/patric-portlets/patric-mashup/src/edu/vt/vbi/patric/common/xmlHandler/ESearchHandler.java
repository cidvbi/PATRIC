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

public class ESearchHandler extends DefaultHandler {

	private HashMap<String, String> result;

	private String currentElement = "";

	private boolean isReadingResult = false;

	private StringBuffer sbCount = null;

	private StringBuffer sbQueryKey = null;

	private StringBuffer sbWebEnv = null;

	public ESearchHandler() {
		result = new HashMap<String, String>();
		result.put("hasData", "false");
	}

	public HashMap<String, String> getParsedData() {
		return result;
	}

	@Override
	public void startDocument() throws SAXException {
		isReadingResult = true;
		result.put("hasData", "true");
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (qName.equalsIgnoreCase("eSearchResult")) {
			sbCount = new StringBuffer();
			sbQueryKey = new StringBuffer();
			sbWebEnv = new StringBuffer();
		}
		else if (qName.equalsIgnoreCase("Count") || qName.equalsIgnoreCase("QueryKey") || qName.equalsIgnoreCase("WebEnv")) {
			currentElement = qName;
		}
		else if (qName.equalsIgnoreCase("TranslationStack")) {
			isReadingResult = false;
			currentElement = "";
		}
		else {
			currentElement = "";
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("eSearchResult")) {
			result.put("Count", sbCount.toString());
			result.put("QueryKey", sbQueryKey.toString());
			result.put("WebEnv", sbWebEnv.toString());

			sbCount = null;
			sbQueryKey = null;
			sbWebEnv = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tmpVal = new String(ch, start, length);

		if (!currentElement.equals("") && isReadingResult == true) {
			// result.put(currentElement, tmpVal);
			if (currentElement.equals("Count")) {
				sbCount.append(tmpVal);
			}
			else if (currentElement.equals("QueryKey")) {
				sbQueryKey.append(tmpVal);
			}
			else if (currentElement.equals("WebEnv")) {
				sbWebEnv.append(tmpVal);
			}
		}
	}
}
