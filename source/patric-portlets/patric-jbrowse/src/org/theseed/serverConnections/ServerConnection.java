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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author redwards
 * 
 */
public interface ServerConnection extends Server {

	public String encode(HashMap<String, Object> hashmap);

	public String encode(String[] array);

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

	public String[] resultsAsArray();

	public HashMap<Object, Object> resultsAsHashMapObject();

	public HashMap<String, String> resultsAsHashMapString();

	public HashMap<String, ArrayList<String>> resultsAsHashMap();

	public HashMap<String, ArrayList<HashMap<String, Object>>> resultsAsHashMapArrayHashMap();

	public ArrayList<Object> resultsAsArrayList();

}
