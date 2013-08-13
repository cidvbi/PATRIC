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
package edu.vt.vbi.patric.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class KLEIOInterface {
	private final String kleioKey = "578e2b24fcf9c04e5464384a1ee7836e5e2327596c586189f76e2da0";

	private final String endpointUrl = "http://nactem4.mc.man.ac.uk:18080/Kleio/services/KleioPort";

	ServiceClient client = null;

	private OMFactory omFactory = null;

	private OMNamespace nsWeb = null;

	private OMNamespace nsXsi = null;

	private final String facets = "GENE,PROTEIN,MESHHEADING,METABOLITE,DRUG,BACTERIA,SYMPTOM,DISEASE,ORGAN,DIAG_PROC,THERAPEUTIC_PROC,INDICATOR";

	private Map<String, String> hash = new HashMap<String, String>();

	public KLEIOInterface() {
		try {

			client = new ServiceClient();
			Options opts = new Options();
			opts.setTo(new EndpointReference(endpointUrl));
			opts.setTimeOutInMilliSeconds(300000);
			client.setOptions(opts);

			omFactory = OMAbstractFactory.getOMFactory();
			nsWeb = omFactory.createOMNamespace("http://webservice.kleio.nactem.ac.uk/", "web");
			nsXsi = omFactory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");

		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
	}

	private OMElement getKLEIOKey() {
		OMElement e = omFactory.createOMElement("key", null);
		e.setText(kleioKey);
		return e;
	}

	private OMElement getOptionalCriteria(String criteria, String value) {
		OMElement e = omFactory.createOMElement(criteria, null);
		if (value == null) {
			if (criteria.equalsIgnoreCase("sortby")) {
				value = "RELEVANCE";
			}
			else if (criteria.equalsIgnoreCase("nonullabstract")) {
				value = "false";
			}
			else if (criteria.equalsIgnoreCase("startat")) {
				value = "0";
			}
			else if (criteria.equalsIgnoreCase("hitsperpage")) {
				value = "10";
			}
		}
		e.addChild(omFactory.createOMText(value));
		return e;
	}

	public JSONObject getDocumentList(String query, String sortby, boolean nullabstract, int startat, int hitsperpage) {
		JSONObject r = new JSONObject();
		JSONArray articles = new JSONArray();

		OMElement res = null;
		OMElement req = omFactory.createOMElement("getDocumentList", nsWeb);
		req.addChild(getKLEIOKey());
		req.addChild(getQueryNodes(query));

		// optional
		req.addChild(getOptionalCriteria("sortBy", sortby));
		req.addChild(getOptionalCriteria("noNullAbstract", "" + nullabstract));
		req.addChild(getOptionalCriteria("startAt", "" + startat));
		req.addChild(getOptionalCriteria("hitsPerPage", "" + hitsperpage));

		QName qnTotal = new QName("", "totalHits");
		QName qnCitations = new QName("", "citations");
		QName qnDate = new QName("", "dateString");
		QName qnJournal = new QName("", "journalCite");
		QName qnSnippet = new QName("", "docSnippet");
		QName qnPMID = new QName("", "pmid");
		QName qnTitle = new QName("", "title");
		QName qnAuthors = new QName("", "authors");

		try {
			res = client.sendReceive(req);
			OMElement rtrn = res.getFirstElement();

			OMElement totalHits = rtrn.getFirstChildWithName(qnTotal);
			System.out.println(totalHits.getText());
			r.put("total", totalHits.getText());

			if (rtrn.getFirstChildWithName(qnCitations) != null) {
				Iterator<OMElement> itr = rtrn.getFirstChildWithName(qnCitations).getChildElements();
				OMElement citation = null;

				while (itr.hasNext()) {
					citation = itr.next();
					JSONObject article = new JSONObject();
					article.put("pmid", citation.getFirstChildWithName(qnPMID).getText());

					String title = citation.getFirstChildWithName(qnTitle).getText();
					StringBuffer z = new StringBuffer();
					for (int i = 0; i < title.length(); i++) {
						char c = title.charAt(i);
						if (c > 127 || c == '"' || c == '<' || c == '>') {
							z.append("&#" + (int) c + ";");
						}
						else {
							z.append(c);
						}
					}

					title = z.toString();

					article.put("title", title);

					article.put("date", citation.getFirstChildWithName(qnDate).getText());
					article.put("journal", citation.getFirstChildWithName(qnJournal).getText());
					article.put("snippet", citation.getFirstChildWithName(qnSnippet).getText());
					// authors
					JSONArray authors = new JSONArray();
					Iterator<OMElement> itrAuthors = citation.getFirstChildWithName(qnAuthors).getChildElements();
					OMElement a = null;
					while (itrAuthors.hasNext()) {
						a = itrAuthors.next();
						authors.add(a.getText());
					}
					article.put("authors", authors);
					articles.add(article);
				}
			}
			r.put("result", articles);
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}

		return r;
	}

	public JSONObject getFacets(String query) {

		JSONObject r = new JSONObject();
		JSONArray facets = new JSONArray();
		boolean flag = false;

		OMElement res = null;
		OMElement req = omFactory.createOMElement("getFacets", nsWeb);
		req.addChild(getKLEIOKey());
		req.addChild(getQueryNodes(query));

		QName qnFacetName = new QName("", "facetName");
		QName qnFacetFreq = new QName("", "facetFreq");
		QName qnFacetValues = new QName("", "facetValues");
		QName qnName = new QName("", "name");
		QName qnFreq = new QName("", "freq");

		try {
			res = client.sendReceive(req);

			// Iterator<OMElement> itr = res.getChildrenWithLocalName("getFacetResponse");
			Iterator<OMElement> itr = res.getChildrenWithNamespaceURI(nsWeb.getNamespaceURI());

			OMElement e = null;
			OMElement v = null;

			while (itr.hasNext()) {
				e = itr.next(); // <return>

				if (!e.getFirstChildWithName(qnFacetName).getText().toString().equals("HUMAN_PHENOM")
						&& !e.getFirstChildWithName(qnFacetName).getText().toString().equals("GENERAL_PHENOM")
						&& !e.getFirstChildWithName(qnFacetName).getText().toString().equals("NATURAL_PHENOM")
						&& !e.getFirstChildWithName(qnFacetName).getText().toString().equals("PUBLICATIONTYPE")) {

					flag = false;
					JSONObject facet = new JSONObject();
					if (e.getFirstChildWithName(qnFacetFreq).getText().toString().equals("0")) {
					}
					else {
						if (e.getFirstChildWithName(qnFacetName).getText().toString().equals("DIAG_PROC")) {
							facet.put(
									"text",
									"<span style=\"color: #CC6600; margin: 0; padding: 0 0 2px; font-weight: bold;\">Diagnostic Procedure</span><span style=\"color: #888;\"> ("
											+ e.getFirstChildWithName(qnFacetFreq).getText() + "+) </span>");
						}
						else if (e.getFirstChildWithName(qnFacetName).getText().toString().equals("THERAPEUTIC_PROC")) {
							facet.put(
									"text",
									"<span style=\"color: #CC6600; margin: 0; padding: 0 0 2px; font-weight: bold;\">Therapeutic Procedure</span><span style=\"color: #888;\"> ("
											+ e.getFirstChildWithName(qnFacetFreq).getText() + "+) </span>");
						}
						else {
							facet.put(
									"text",
									"<span style=\"color: #CC6600; margin: 0; padding: 0 0 2px; font-weight: bold;\">"
											+ e.getFirstChildWithName(qnFacetName).getText().substring(0, 1)
													.toUpperCase()
											+ e.getFirstChildWithName(qnFacetName).getText().substring(1).toLowerCase()
											+ "</span><span style=\"color: #888;\"> ("
											+ e.getFirstChildWithName(qnFacetFreq).getText() + "+)</span>");
						}
						facet.put("id", e.getFirstChildWithName(qnFacetName).getText());
						facet.put("leaf", false);
						facet.put("expanded", true);
						facet.put("renderstep", "1");
					}

					if (Integer.parseInt(e.getFirstChildWithName(qnFacetFreq).getText()) > 0) {
						Iterator<OMElement> itrValues = e.getFirstChildWithName(qnFacetValues).getChildElements();
						JSONArray jsonValues = new JSONArray();
						int more_count = 0;
						JSONArray morechildren = new JSONArray();
						while (itrValues.hasNext()) {
							v = itrValues.next(); // <facetValue>
							if (v.getFirstChildWithName(qnFreq).getText().toString().equals("0")) {
							}
							else {
								more_count++;
								JSONObject jsonValue = null;
								JSONObject moreValue = null;
								if (more_count == 4) {
									jsonValue = new JSONObject();
									jsonValue.put("parentID", e.getFirstChildWithName(qnFacetName).getText() + "_more");
									jsonValue.put(
											"id",
											v.getFirstChildWithName(qnName).getText() + "##"
													+ e.getFirstChildWithName(qnFacetName));
									jsonValue.put("leaf", true);
									jsonValue.put("text", v.getFirstChildWithName(qnName).getText().substring(0, 1)
											.toUpperCase()
											+ v.getFirstChildWithName(qnName).getText().substring(1).toLowerCase()
											+ "<span style=\"color: #888;\"> ("
											+ v.getFirstChildWithName(qnFreq).getText() + "+)</span>");
									jsonValue.put(
											"checked",
											keepState(v.getFirstChildWithName(qnName).getText(), e
													.getFirstChildWithName(qnFacetName).getText()));
									jsonValue.put("renderstep", "2");
									morechildren.add(jsonValue);

									while (itrValues.hasNext()) {
										v = itrValues.next();
										jsonValue = new JSONObject();
										jsonValue.put("parentID", e.getFirstChildWithName(qnFacetName).getText()
												+ "_more");
										jsonValue.put(
												"id",
												v.getFirstChildWithName(qnName).getText() + "##"
														+ e.getFirstChildWithName(qnFacetName));
										jsonValue.put("leaf", true);
										jsonValue.put("text", v.getFirstChildWithName(qnName).getText().substring(0, 1)
												.toUpperCase()
												+ v.getFirstChildWithName(qnName).getText().substring(1).toLowerCase()
												+ "<span style=\"color: #888;\"> ("
												+ v.getFirstChildWithName(qnFreq).getText() + "+)</span>");
										jsonValue.put("renderstep", "2");
										jsonValue.put(
												"checked",
												keepState(v.getFirstChildWithName(qnName).getText(), e
														.getFirstChildWithName(qnFacetName).getText()));
										morechildren.add(jsonValue);

									}

									moreValue = new JSONObject();

									moreValue.put("parentID", e.getFirstChildWithName(qnFacetName).getText());
									moreValue.put("id", e.getFirstChildWithName(qnFacetName).getText() + "_more");
									moreValue.put("leaf", false);
									moreValue.put("text", "<b>more</b>");
									moreValue.put("renderstep", "3");

									JSONObject lessValue = new JSONObject();

									lessValue.put("parentID", e.getFirstChildWithName(qnFacetName).getText());
									lessValue.put("id", e.getFirstChildWithName(qnFacetName).getText() + "_less");
									lessValue.put("leaf", true);
									lessValue.put("text", "<b>less</b>");
									lessValue.put("renderstep", "3");
									morechildren.add(lessValue);

									moreValue.put("children", morechildren);
									jsonValues.add(moreValue);
								}
								else {
									jsonValue = new JSONObject();
									jsonValue.put("parentID", e.getFirstChildWithName(qnFacetName).getText());
									jsonValue.put(
											"id",
											v.getFirstChildWithName(qnName).getText() + "##"
													+ e.getFirstChildWithName(qnFacetName));
									jsonValue.put("leaf", true);
									jsonValue.put("text", v.getFirstChildWithName(qnName).getText().substring(0, 1)
											.toUpperCase()
											+ v.getFirstChildWithName(qnName).getText().substring(1).toLowerCase()
											+ "<span style=\"color: #888;\"> ("
											+ v.getFirstChildWithName(qnFreq).getText() + "+)</span>");
									jsonValue.put(
											"checked",
											keepState(v.getFirstChildWithName(qnName).getText(), e
													.getFirstChildWithName(qnFacetName).getText()));
									jsonValues.add(jsonValue);
									jsonValue.put("renderstep", "2");
								}
							}
						}

						if (!v.getFirstChildWithName(qnFreq).getText().toString().equals("0")) {
							facet.put("children", jsonValues);
							if (flag == true) {
								facet.put("expanded", true);
							}
						}
					}
					if (!e.getFirstChildWithName(qnFacetFreq).getText().toString().equals("0")) {
						facets.add(facet);
					}
				}
				r.put("result", facets);
			}
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		return r;
	}

	public boolean keepState(String text, String facet) {
		boolean flag = false;
		Iterator<?> it = hash.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();

			if (!facet.equals("CONTENT") || !facet.equals("content")) {
				String[] pairs_array = pairs.getValue().toString().split(";;");
				if (pairs_array.length > 1) {
					for (int i = 0; i < pairs_array.length; i++) {
						if (text.equals(pairs_array[i]) && facet.equals(pairs.getKey().toString())) {
							flag = true;
						}
					}
				}
				else {
					if (text.equals(pairs.getValue().toString()) && facet.equals(pairs.getKey().toString())) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	private OMElement getQueryNodes(String query) {

		System.out.print("KLEIO query" + query);
		String[] splitted = query.split(" AND | OR ");
		if (splitted.length > 1) {
			for (int i = 0; i < splitted.length; i++) {
				String[] row = splitted[i].split(":");
				if (row[0] != null && facets.indexOf(row[0]) < 0) {
					if (hash.containsKey("content")) {
						String exists = hash.get("content") + ";;" + row[0].trim();
						hash.put("content", exists);
					}
					else {
						hash.put("content", row[0].trim());
					}
				}
				else {
					if (hash.containsKey(row[0].trim())) {
						String exists = hash.get(row[0].trim()) + ";;" + row[1].trim();
						hash.put(row[0].trim(), exists);
					}
					else {
						hash.put(row[0].trim(), row[1].trim());
					}
				}
			}
		}
		else {

			splitted = query.split(":");

			if (splitted[0] != null && facets.indexOf(splitted[0]) < 0)
				hash.put("content", splitted[0].trim());
			else
				hash.put(splitted[0].trim(), splitted[1].trim());

		}

		Iterator<?> it = hash.entrySet().iterator();
		OMElement e = omFactory.createOMElement("query", null);
		OMElement eall = omFactory.createOMElement("node", null);

		if (hash.size() > 1) {
			eall.addAttribute("type", "web:andNode", nsXsi);
		}

		while (it.hasNext()) {

			Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();
			OMElement etemp = omFactory.createOMElement("node", null);

			if (pairs.getKey().equals("content") || pairs.getKey().equals("CONTENT")) {
				String[] pairs_array = pairs.getValue().toString().split(";;");
				if (pairs_array.length > 1) {
					etemp.addAttribute("type", "web:andNode", nsXsi);
					for (int i = 0; i < pairs_array.length; i++) {
						OMElement eNode = omFactory.createOMElement("node", null);
						eNode.addAttribute("type", "web:stringNode", nsXsi);
						OMElement eValue = omFactory.createOMElement("value", null);
						eValue.setText(pairs_array[i]);
						eNode.addChild(eValue);
						etemp.addChild(eNode);
					}
				}
				else {
					etemp.addAttribute("type", "web:stringNode", nsXsi);
					OMElement eValue = omFactory.createOMElement("value", null);
					eValue.setText(pairs.getValue().toString());
					etemp.addChild(eValue);
				}
			}
			else {

				String[] pairs_array = pairs.getValue().toString().split(";;");

				if (pairs_array.length > 1) {
					etemp.addAttribute("type", "web:orNode", nsXsi);
					for (int i = 0; i < pairs_array.length; i++) {
						OMElement eNode = omFactory.createOMElement("node", null);
						eNode.addAttribute("type", "web:facetNode", nsXsi);
						OMElement eValue1 = omFactory.createOMElement("facet", null);
						OMElement eValue2 = omFactory.createOMElement("value", null);
						eValue1.setText(pairs.getKey().toString());
						eValue2.setText(pairs_array[i]);
						eNode.addChild(eValue1);
						eNode.addChild(eValue2);
						etemp.addChild(eNode);
					}
				}
				else {
					etemp.addAttribute("type", "web:facetNode", nsXsi);
					OMElement eValue1 = omFactory.createOMElement("facet", null);
					OMElement eValue2 = omFactory.createOMElement("value", null);
					eValue1.setText(pairs.getKey().toString());
					eValue2.setText(pairs.getValue().toString());
					etemp.addChild(eValue1);
					etemp.addChild(eValue2);
				}
			}

			if (hash.size() > 1) {
				eall.addChild(etemp);
			}
			else {
				eall = etemp;
			}
		}

		e.addChild(eall);
		// System.out.print(e.toString());
		return e;
	}

	public JSONObject getDocument(String pubmedID) {
		JSONObject r = new JSONObject();
		OMElement req = omFactory.createOMElement("getDocument", nsWeb);
		OMElement res = null;

		QName qnAuthors = new QName("", "authors");
		QName qnAbstract = new QName("", "abstract");
		QName qnTitle = new QName("", "title");
		QName qnJournal = new QName("", "journalCite");
		QName qnPMID = new QName("", "pmid");
		QName qnMesh = new QName("", "meshHeadings");

		req.addChild(getKLEIOKey());
		OMElement documentID = omFactory.createOMElement("documentId", null);
		documentID.setText(pubmedID);
		req.addChild(documentID);
		try {
			res = client.sendReceive(req);
			OMElement article = res.getFirstElement();

			// authors
			JSONArray authors = new JSONArray();
			Iterator<OMElement> itrAuthors = article.getFirstChildWithName(qnAuthors).getChildElements();
			OMElement a = null;
			while (itrAuthors.hasNext()) {
				a = itrAuthors.next();
				authors.add(a.getText());
			}
			r.put("authors", authors);
			r.put("abstract", article.getFirstChildWithName(qnAbstract).getText());

			String title = article.getFirstChildWithName(qnTitle).getText();
			// System.out.print("title"+title);
			StringBuffer z = new StringBuffer();

			for (int i = 0; i < title.length(); i++) {
				char c = title.charAt(i);
				if (c > 127 || c == '"' || c == '<' || c == '>') {
					z.append("&#" + (int) c + ";");
				}
				else {
					z.append(c);
				}
			}

			title = z.toString();

			r.put("title", title);
			r.put("journal", article.getFirstChildWithName(qnJournal).getText());
			r.put("pmid", article.getFirstChildWithName(qnPMID).getText());

			// mesh
			JSONArray mesh = new JSONArray();
			Iterator<OMElement> itrMesh = article.getFirstChildWithName(qnMesh).getChildElements();
			OMElement m = null;
			while (itrMesh.hasNext()) {
				m = itrMesh.next();
				mesh.add(m.getText());
			}
			r.put("mesh", mesh);
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		return r;
	}

	public JSONArray getFacetNames() {
		JSONArray r = new JSONArray();
		OMElement res = null;
		OMElement req = omFactory.createOMElement("getFacetNames", nsWeb);
		req.addChild(getKLEIOKey());

		try {
			res = client.sendReceive(req);
			Iterator<OMElement> itr = res.getChildrenWithLocalName("getFacetNamesResponse");
			while (itr.hasNext()) {
				OMElement e = itr.next();
				r.add(e.getText());
			}
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		return r;
	}

	public JSONObject getNamedEntities(String pubmedID) {

		JSONObject r = new JSONObject();
		JSONArray results = new JSONArray();

		OMElement req = omFactory.createOMElement("getNamedEntities", nsWeb);
		OMElement res = null;

		// QName qnannotation = new QName("","annotation");

		QName qnexternalReferences = new QName("", "externalReferences");
		QName qnid = new QName("", "id");
		QName qnnamespace = new QName("", "namespace");

		QName qnbegin = new QName("", "begin");
		QName qnend = new QName("", "end");
		QName qnexpandedForm = new QName("", "expandedForm");
		QName qnlocation = new QName("", "location");
		QName qnnamedEntity = new QName("", "namedEntity");
		QName qnnormalisedForm = new QName("", "normalisedForm");
		QName qnshortForm = new QName("", "shortForm");
		QName qnsurfaceForm = new QName("", "surfaceForm");

		req.addChild(getKLEIOKey());
		OMElement documentID = omFactory.createOMElement("documentId", null);
		documentID.setText(pubmedID);
		req.addChild(documentID);

		try {
			res = client.sendReceive(req);
			Iterator<OMElement> itr = res.getChildrenWithLocalName("getNamedEntitiesResponse");
			OMElement entities = null;
			while (itr.hasNext()) {
				entities = itr.next();
				JSONObject entity = new JSONObject();
				entity.put("begin", entities.getFirstChildWithName(qnbegin).getText());
				entity.put("end", entities.getFirstChildWithName(qnend).getText());
				entity.put("expandedForm", entities.getFirstChildWithName(qnexpandedForm).getText());
				entity.put("location", entities.getFirstChildWithName(qnlocation).getText());

				if (entities.getFirstChildWithName(qnnamedEntity) != null) {
					entity.put("namedEntity", entities.getFirstChildWithName(qnnamedEntity).getText());
				}

				entity.put("normalisedForm", entities.getFirstChildWithName(qnnormalisedForm).getText());
				entity.put("shortForm", entities.getFirstChildWithName(qnshortForm).getText());
				entity.put("surfaceForm", entities.getFirstChildWithName(qnsurfaceForm).getText());

				Iterator<OMElement> itrreferences = entities.getFirstChildWithName(qnexternalReferences)
						.getChildElements();

				JSONObject extreferences = new JSONObject();

				OMElement references = null;
				while (itrreferences.hasNext()) {
					references = itrreferences.next();

					JSONObject reference = new JSONObject();
					reference.put("namespace", references.getFirstChildWithName(qnnamespace).getText());
					reference.put("id", references.getFirstChildWithName(qnid).getText());
					extreferences.put("reference", reference);
				}
				entity.put("externalReferences", extreferences);
				results.add(entity);
			}
			r.put("result", results);
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}
		return r;
	}

	public void clean() {
		try {
			client.cleanup();
			client.cleanupTransport();
		}
		catch (AxisFault e) {
			e.printStackTrace();
		}

	}
}
