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
/**
 * 
 */
package org.theseed.serverConnections;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * @author redwards
 * 
 */
public interface Server {

	/**
	 * The query reader is an output stream from the network instantiated as a buffered reader. It allows you to read the results of your query
	 * @return a buffered reader
	 * @throws Exception if there is an error reading the stream
	 */
	public BufferedReader queryReader() throws Exception;

	/**
	 * The query input stream is the network connection to which data is written.
	 * @return an InputStream connection
	 * @throws Exception
	 */
	public InputStream query() throws Exception;

	/**
	 * Getter/Setter methods for the three variables, url (the server URL), method (the function that is to be called), and data (the key/value pairs
	 * of any data to be sent to the server.
	 * 
	 * You have to set the URL and the method before you can make a valid call!
	 * 
	 * @return the current url
	 */
	public String getUrl();

	/**
	 * @param url the new url
	 * @throws MalformedURLException
	 */
	public void setUrl(String url) throws MalformedURLException;

	/**
	 * @return the function call to be made on the server
	 */
	public String getMethod();

	/**
	 * @param query function call to be made on the server
	 */
	public void setMethod(String query);

	/**
	 * The encoding is the method that is used to transfer data. Currently, there are two formats that we support: YAML (default) and JSON.
	 * @return encoding the type of encoding you would like to retrieve
	 */

	public String getEncoding();

	/**
	 * @param encoding The type of encoding you would like to use
	 */
	public void setEncoding(String encoding);

	/**
	 * The source is a string that identifies the resource that is accessing our servers
	 * @return the source
	 */
	public String getSource();

	/**
	 * @param source the source to set
	 */
	public void setSource(String source);

	/**
	 * @return the username
	 */
	public String getUsername();

	/**
	 * The username for the RAST server
	 * @param username the username to set
	 */
	public void setUsername(String username);

	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * The password for your username. PLEASE NOTE: right now this is sent as cleartext!!
	 * @param password the password to set
	 */
	public void setPassword(String password);

	/**
	 * The email address of the person to contact if the code goes awry
	 * @return the email
	 */
	public String getEmail();

	/**
	 * @param email the contact email to set
	 */
	public void setEmail(String email);

	/**
	 * Get the data associated with an argument
	 * @param key the name of the key for the parameter
	 * @return object the object for the key
	 */
	public Object getData(String key);

	/**
	 * set a key/value pair to be passed to the server
	 * @param arg the argument name
	 * @param obj the object to add
	 */
	public void setData(String arg, Object obj);

	public void setDebug(boolean debug);

	public boolean isDebug();

	/**
	 * Encode a String[] array into a data object that can be exchanged (e.g. yaml or json data object). You can then add this to data.
	 * @param array a string array to encode
	 * @return the string representation of the array
	 * @throws Exception
	 */
	public String encode(String[] array) throws Exception;

	/**
	 * Encode a HashMap<String, Object> into a data object. You can then add this to the args
	 * @param hashmap a HashMap to encode
	 * @return the string representation of the array
	 * @throws Exception
	 */
	public String encode(HashMap<String, Object> hashmap) throws Exception;

	/**
	 * Reset the server connection. This resets the method and arguments passed to the server, but does not affect the URL, email, source, or encoding
	 * strings, so you can call this method between calls to clear previous data.
	 */
	public void reset();

}
