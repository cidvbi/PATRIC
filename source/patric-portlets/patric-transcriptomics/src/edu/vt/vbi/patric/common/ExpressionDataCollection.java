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
package edu.vt.vbi.patric.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.PolyomicHandler;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class ExpressionDataCollection {

	private String[] collectionIds;

	private ArrayList<String> expressionFileName;

	private ArrayList<String> sampleFileName;

	private ArrayList<String> mappingFileName;

	private JSONArray sample, expression;

	private InputStream inp;

	public final static String CONTENT_EXPRESSION = "expression";

	public final static String CONTENT_SAMPLE = "sample";

	public final static String CONTENT_MAPPING = "mapping";

	public ExpressionDataCollection(String id, String token) {

		PolyomicHandler polyomic = new PolyomicHandler();
		polyomic.setAuthenticationToken(token);
		collectionIds = id.split(",");

		sample = new JSONArray();
		expression = new JSONArray();

		JSONObject collection = null;
		expressionFileName = new ArrayList<String>();
		sampleFileName = new ArrayList<String>();
		mappingFileName = new ArrayList<String>();

		for (int i = 0; i < collectionIds.length; i++) {
			collection = polyomic.getCollection(collectionIds[i], null);

			expressionFileName.add(polyomic.findJSONUrl(collection, CONTENT_EXPRESSION));
			sampleFileName.add(polyomic.findJSONUrl(collection, CONTENT_SAMPLE));
			mappingFileName.add(polyomic.findJSONUrl(collection, CONTENT_MAPPING));
		}
	}

	public InputStream getInputStreamReader(String path) {

		try {
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			inp = connection.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return inp;
	}

	public void read(String input) throws FileNotFoundException {

		ArrayList<String> temp = null;

		if (input.equals(CONTENT_SAMPLE)) {
			temp = sampleFileName;
		}
		else if (input.equals(CONTENT_EXPRESSION)) {
			temp = expressionFileName;
		}

		InputStreamReader stream = null;
		BufferedReader reader = null;

		String strLine = "";
		for (int i = 0; i < temp.size(); i++) {

			inp = getInputStreamReader(temp.get(i));
			stream = new InputStreamReader(inp);
			reader = new BufferedReader(stream);

			try {
				while ((strLine = reader.readLine()) != null) {
					try {
						JSONObject tmp = (JSONObject) new JSONParser().parse(strLine);
						AddToCurrentSet((JSONArray) tmp.get(input), input);
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
				}
				inp.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void filter(String item, String input) throws FileNotFoundException {

		JSONArray temp = null;
		String[] items = item.split(",");

		if (input.equals(CONTENT_SAMPLE)) {
			temp = sample;
		}
		else if (input.equals(CONTENT_EXPRESSION)) {
			temp = expression;
		}

		JSONArray ret = new JSONArray();

		for (int i = 0; i < temp.size(); i++) {
			JSONObject a = (JSONObject) temp.get(i);

			for (int j = 0; j < items.length; j++) {
				if (a.get("pid").toString().equals(items[j])) {
					ret.add(a);
					break;
				}
			}
		}

		if (input.equals(CONTENT_SAMPLE)) {
			sample = ret;
		}
		else if (input.equals(CONTENT_EXPRESSION)) {
			expression = ret;
		}
	}

	public void AddToCurrentSet(JSONArray b, String type) {
		for (int i = 0; i < b.size(); i++) {
			JSONObject c = (JSONObject) b.get(i);
			if (type.equals(CONTENT_SAMPLE)) {
				this.sample.add(c);
			}
			else if (type.equals(CONTENT_EXPRESSION)) {
				this.expression.add(c);
			}
		}
	}

	public JSONArray append(JSONArray a, String input) {
		JSONArray b = null;

		if (input.equals(CONTENT_SAMPLE)) {
			b = this.sample;
		}
		else if (input.equals(CONTENT_EXPRESSION)) {
			b = this.expression;
		}

		for (int i = 0; i < b.size(); i++) {
			JSONObject c = (JSONObject) b.get(i);
			a.add(c);
		}
		return a;
	}

	public JSONArray get(String type) {
		if (type.equals(CONTENT_EXPRESSION)) {
			return this.expression;
		}
		else if (type.equals(CONTENT_SAMPLE)) {
			return this.sample;
		}
		return null;
	}
}
