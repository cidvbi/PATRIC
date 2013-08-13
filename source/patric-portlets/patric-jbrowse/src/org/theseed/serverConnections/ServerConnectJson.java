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
package org.theseed.serverConnections;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.theseed.json.JSONArray;
import org.theseed.json.JSONException;
import org.theseed.json.JSONObject;
import org.theseed.json.JSONStringer;
import org.theseed.json.JSONTokener;

/**
 * @author Rob Edwards
 * @version 0.1 The SeverConnectJson class uses JSON, the <a href="http://www.json.org">Javascript Object Notation</a>
 * to transmit data to and from the server. This is another lightweight markup language similar to YAML (used by the
 * original ServerConnect class). However, JSON is tightly integrated into Javascript, and so is receiving wide support.
 * 
 * To use this class, you will also need to download the JSON classes from <a href="http://www.json.org/java/">JSON
 * Java</a>
 */

public class ServerConnectJson extends SEEDServer implements ServerConnection {

	// private JSONObject json;
	private JSONStringer jStringer;

	// default constructor
	public ServerConnectJson(String s) throws MalformedURLException {
		super(s);
		encoding = "json";
	}

	/**
	 * Encode a String[] array into a yaml object. You can then add this to data
	 * @param array a string array to encode
	 * @return the string representation of the array
	 */
	public String encode(String[] array) {
		jStringer = new JSONStringer();
		try {
			jStringer.array();
			for (String s : array)
				jStringer.value(s);
			jStringer.endArray();
		}
		catch (JSONException e) {
			System.err.println("There was a JSON exception. Sorry:");
			e.printStackTrace();
		}
		return jStringer.toString();
	}

	/**
	 * Encode a HashMap<String, Object> into a yaml object. You can then add this to the args
	 * @param hashmap a HashMap to encode
	 * @return the string representation of the array
	 */
	public String encode(HashMap<String, Object> hashmap) {
		jStringer = new JSONStringer();
		try {
			jStringer.object();

			for (String key : hashmap.keySet()) {
				jStringer.key(key);
				jStringer.value(hashmap.get(key));
			}
			jStringer.endObject();
		}
		catch (JSONException e) {
			System.err.println("There was a JSON exception. Sorry:");
			e.printStackTrace();
		}
		return jStringer.toString();
	}

	/**
	 * This method is provided as a convenience. If you have a call that simply returns an array, then use this and it
	 * will return the items as an array of strings.
	 * 
	 * @return the results as a simple String[] array
	 */
	public String[] resultsAsArray() {
		ArrayList<Object> results = this.resultsAsArrayList();
		// convert the arrayList to a new array of strings and return that.
		return results.toArray(new String[0]);
	}

	/**
	 * Return the results as a HashMap<String, Object>. This will allow you to retrieve the objects at will.
	 * 
	 * @return the results from the server.
	 */
	public HashMap<Object, Object> resultsAsHashMapObject() {

		HashMap<Object, Object> results = new HashMap<Object, Object>();

		try {
			JSONObject jsonO = new JSONObject(new JSONTokener(new InputStreamReader(this.query())));
			if (debug)
				System.err.println("JSON object returned from server: " + jsonO.toString());
			Iterator<?> iter = jsonO.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				results.put(key, jsonO.get(key));
			}
		}
		catch (JSONException e) {
			System.err.println("There was an error loading the JSON stream");
			e.printStackTrace();
		}
		catch (NoSuchElementException e) {
			System.err.println("We ran out of elements!");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.err.println("There was an error getting the results stream");
			e.printStackTrace();
		}
		return results;

	}

	/**
	 * Return the results as a HashMap<String, String>. A convenience to accomodate the casting.
	 * 
	 * @return the results from the server.
	 */
	public HashMap<String, String> resultsAsHashMapString() {
		HashMap<Object, Object> results = this.resultsAsHashMapObject();
		if (results == null)
			return null;
		HashMap<String, String> toReturn = new HashMap<String, String>();
		for (Object o : results.keySet()) {
			String s = o.toString();
			String a = (String) results.get(o);
			toReturn.put(s, a);
		}
		return toReturn;
	}

	/**
	 * Return the results as a HashMap<String, ArrayList<String>>. This is probably the most common method for getting
	 * the results back from the server
	 * 
	 * @return the results from the server.
	 */
	public HashMap<String, ArrayList<String>> resultsAsHashMap() {
		HashMap<Object, Object> results = this.resultsAsHashMapObject();
		HashMap<String, ArrayList<String>> toReturn = new HashMap<String, ArrayList<String>>();
		try {
			for (Object o : results.keySet()) {
				String s = (String) o;
				JSONArray jsonA = (JSONArray) results.get(o);
				ArrayList<String> a = new ArrayList<String>();
				for (int i = 0; i < jsonA.length(); i++)
					a.add(jsonA.getString(i));
				toReturn.put(s, a);
			}
		}
		catch (JSONException e) {
			System.err.println("Sorry, we got an error parsing the JSON stream");
			e.printStackTrace();
		}

		return toReturn;
	}

	/**
	 * Return the results as a HashMap<String, ArrayList<HashMap<String, Object>>>.
	 * 
	 * @return the results from the server.
	 */
	public HashMap<String, ArrayList<HashMap<String, Object>>> resultsAsHashMapArrayHashMap() {
		HashMap<String, ArrayList<HashMap<String, Object>>> toReturn = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		try {
			HashMap<Object, Object> results = this.resultsAsHashMapObject();
			for (Object o : results.keySet()) {
				String s = (String) o;
				JSONArray jsonA = (JSONArray) results.get(o);
				ArrayList<HashMap<String, Object>> a = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < jsonA.length(); i++) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					JSONObject jo = jsonA.getJSONObject(i);
					Iterator<?> iter = jo.keys();
					while (iter.hasNext()) {
						String key = (String) iter.next();
						hm.put(key, jo.get(key));
					}
					a.add(hm);
				}
				toReturn.put(s, a);
			}
		}
		catch (JSONException e) {
			System.err.println("Sorry, we got an error parsing the JSON stream");
			e.printStackTrace();
		}

		return toReturn;
	}

	/**
	 * Get an array of results. This is not often used, since most results are returned as hashes.
	 */
	public ArrayList<Object> resultsAsArrayList() {
		// we use an array list here since we don't know how large it is.
		ArrayList<Object> results = null;

		try {
			InputStream res = this.query();
			JSONTokener tok = new JSONTokener(new InputStreamReader(res));
			JSONObject jsonO = new JSONObject(tok);

			// if (debug)
			// System.err.println("Result is" +res;)
			JSONArray jsonA = new JSONArray(jsonO);
			if (debug)
				System.err.println("JSON Array is " + jsonA.toString());
			results = new ArrayList<Object>(jsonA.length());
			for (int i = 0; i < jsonA.length(); i++)
				results.add(jsonA.get(i));
		}
		catch (JSONException e) {
			System.err.println("There was an error loading the JSON stream");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.err.println("There was an error parsing stream");
			e.printStackTrace();
		}
		return results;
	}
}
