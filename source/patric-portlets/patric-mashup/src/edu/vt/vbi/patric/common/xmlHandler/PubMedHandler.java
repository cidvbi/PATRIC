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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unchecked")
public class PubMedHandler extends DefaultHandler {

	private JSONArray articles = null;

	private JSONObject article = null;

	private String currentElement = "";

	private ArrayList<String> authors = null;

	private boolean isReadingArticleIds = false;

	private StringBuffer sbTitle = null;

	private StringBuffer sbSource = null;

	public JSONArray getParsedJSON() {
		return articles;
	}

	@Override
	public void startDocument() throws SAXException {
		this.articles = new JSONArray();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (qName.equalsIgnoreCase("DocSum")) {
			this.article = new JSONObject();
			this.sbTitle = new StringBuffer();
			this.sbSource = new StringBuffer();
		}
		if (qName.equalsIgnoreCase("Item")) {
			if (atts.getValue("Name").equals("Title") || atts.getValue("Name").equals("Author") || atts.getValue("Name").equals("PubDate")
					|| atts.getValue("Name").equals("FullJournalName") || atts.getValue("Name").equals("Source")
					|| atts.getValue("Name").equals("pubmed") || atts.getValue("Name").equals("pmid")) {
				currentElement = atts.getValue("Name");
			}
			else if (atts.getValue("Name").equals("AuthorList")) {
				authors = new ArrayList<String>();
			}
			else if (atts.getValue("Name").equals("ArticleIds")) {
				isReadingArticleIds = true;
			}
			else {
				currentElement = "";
			}
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("DocSum")) {
			String abbrAuthorList, fullAuthorList = null;

			if (authors.size() == 1) {
				abbrAuthorList = fullAuthorList = authors.get(0);
			}
			else if (authors.size() == 2) {
				abbrAuthorList = fullAuthorList = authors.get(0) + " and " + authors.get(1);
			}
			else if (authors.size() >= 3) {
				abbrAuthorList = authors.get(0) + " et al";
				fullAuthorList = authors.get(0);
				for (int i = 1; i < authors.size(); i++) {
					fullAuthorList += ", " + authors.get(i);
				}
			}
			else {
				abbrAuthorList = fullAuthorList = "N/A";
			}
			article.put("abbrAuthorList", abbrAuthorList);
			article.put("fullAuthorList", fullAuthorList);
			article.put("Title", sbTitle.toString());
			article.put("Source", sbSource.toString());
			this.articles.add(this.article);
			this.article = null;
			this.authors = null;
			this.sbTitle = null;
			this.sbSource = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		String tmpVal = new String(ch, start, length);

		if (currentElement.equals("Author") && !tmpVal.trim().equals("")) {
			authors.add(tmpVal);
		}
		else if (currentElement.equals("pubmed") && isReadingArticleIds == true) {
			if (tmpVal.trim().equals("")) {
				isReadingArticleIds = false;
			}
			else {
				if (article.get("pubmed_id") != null) {
					tmpVal = article.get("pubmed_id") + tmpVal;
				}
				article.put("pubmed_id", tmpVal);
			}
		}
		else if (currentElement.equals("pmid") && isReadingArticleIds == true) {
			if (tmpVal.trim().equals("")) {
				isReadingArticleIds = false;
			}
			else {
				if (article.get("pubmed_id") != null) {
					tmpVal = article.get("pubmed_id") + tmpVal;
				}
				article.put("pubmed_id", tmpVal);
			}
		}
		else if (currentElement.equals("Source") && !tmpVal.trim().equals("")) {
			sbSource.append(tmpVal);
		}
		else if (currentElement.equals("AuthorList") || currentElement.equals("ArticleIds") || currentElement.equals("pubmed")
				|| currentElement.equals("Author") || currentElement.equals("Source")) {
			// skip
		}
		else if (currentElement.equals("Title")) {
			sbTitle.append(tmpVal);
		}
		else if (!currentElement.equals("")) {
			article.put(currentElement, tmpVal.trim());
		}
	}
}
