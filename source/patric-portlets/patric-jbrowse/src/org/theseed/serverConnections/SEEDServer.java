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
/**
 * 
 */
package org.theseed.serverConnections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author Rob Edwards The parent class of all the server connections. This class handles user information, etc.
 */
public class SEEDServer implements Server {

	protected URL url;

	protected String method;

	protected String source;

	protected String email;

	protected HashMap<String, Object> data = new HashMap<String, Object>();

	protected String username;

	protected String password;

	protected String encoding;

	/**
	 * 
	 */
	public boolean debug = false;

	// default constructor
	public SEEDServer(String s) throws MalformedURLException {
		url = new URL(s);

		source = "Rob's Java code";
		email = "noone@java";

		username = null;
		password = null;
	}

	public BufferedReader queryReader() throws Exception {
		InputStreamReader is = new InputStreamReader(this.query());
		BufferedReader reader = new BufferedReader(is);
		return reader;
	}

	public InputStream query() throws Exception {

		if (method == null)
			throw new Exception("No function to be called on the server has been defined");

		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			String args = null;
			writer.write("function=" + method);
			writer.write("&source=" + source);
			writer.write("&email=" + email);
			writer.write("&encoding=" + encoding);
			if (!data.isEmpty()) {
				args = java.net.URLEncoder.encode(this.encode(data), "UTF-8");
				writer.write("&args=" + args);
			}

			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return (connection.getInputStream());
			}
			else {
				System.err.println("Sorry dude. The server at " + this.getUrl() + " did not understand " + method);
				System.err.println("The server's response was :" + connection.getResponseCode() + " which means "
						+ connection.getResponseMessage());
				System.err.println("We wrote these options to the server: ");
				System.err.println("function=" + method + "\n" + "&source=" + source + "\n" + "&email=" + email);
				System.err.println("&encoding=" + encoding);
				System.err.println("DATA: &args=" + args);
				// System.exit(-1);
			}
		}
		catch (MalformedURLException e) {
			System.err.println("There was a Malformed URL exception error for " + this.getUrl() + ". The error was:");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.err.println("There was an IO exception error for " + this.getUrl() + ". The error was:");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Getter/Setter methods for the variables, url (the server URL), method (the function that is to be called),
	 * encoding (json or yaml) and data (the key/value pairs of any data to be sent to the server.
	 * 
	 * You have to set the URL and the method before you can make a valid call!
	 * 
	 * @return the current url
	 */
	public String getUrl() {
		return url.toString();
	}

	/**
	 * @param url the new url
	 * @throws MalformedURLException
	 */
	public void setUrl(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	/**
	 * @return the function call to be made on the server
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param query function call to be made on the server
	 */
	public void setMethod(String query) {
		try {
			query = URLEncoder.encode(query, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			System.err.println("Oops! We got an unsupported encoding exception which is not supported");
			e.printStackTrace();
		}
		this.method = query;
	}

	/**
	 * @see serverConnections.Server#getEncoding()
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * @see serverConnections.Server#setEncoding(java.lang.String)
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @see serverConnections.Server#getSource()
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The username for the RAST server
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * The password for your username. PLEASE NOTE: right now this is sent as cleartext!! Also, right now we don't use
	 * usernames/passwords for very much (anything?)
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * The email address of the person to contact if the code goes awry
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the contact email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Reset the server connection. This resets the method and arguments passed to the server, but does not affect the
	 * URL, email, or Source strings.
	 */
	public void reset() {
		data = new HashMap<String, Object>();
		method = null;
	}

	/**
	 * Get the data associated with an argument
	 * @param key the name of the key for the parameter
	 * @return object the object for the key
	 */
	public Object getData(String key) {
		return data.get(key);
	}

	/**
	 * set a key/value pair to be passed to the server
	 * @param arg the argument name
	 * @param obj the object to add
	 */
	public void setData(String arg, Object obj) {
		data.put(arg, obj);
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @throws Exception
	 * @see serverConnections.Server#encode(java.lang.String[])
	 */
	public String encode(String[] array) throws Exception {
		throw new Exception("This should be overridden");
	}

	/**
	 * @throws Exception
	 * @see serverConnections.Server#encode(java.util.HashMap)
	 */
	public String encode(HashMap<String, Object> hashmap) throws Exception {
		throw new Exception("This should be overridden");
	}

}
