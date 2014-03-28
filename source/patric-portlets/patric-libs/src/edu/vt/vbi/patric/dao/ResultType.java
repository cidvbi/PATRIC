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
package edu.vt.vbi.patric.dao;

import java.util.HashMap;

/**
 * <p>
 * A key-value map to contain database query result. This is an extension of java.util.HashMap class in order to handle null value. A ResultType
 * represents a record (tuple) in the DB query result.
 * </p>
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 */
public class ResultType extends HashMap<Object, Object> {

	private static final long serialVersionUID = -2922819637750273384L;

	/**
	 * Add key-value pair in this map. If the value is null, stores empty string.
	 * 
	 * @param k key
	 * @param v value in java.lang.String format
	 * @see java.util.HashMap#put(Object, Object)
	 */
	@Override
	public Object put(Object k, Object v) {
		if (v == null) {
			return super.put(k, "");
		}
		else {
			return super.put(k, v.toString());
		}
	}

	/**
	 * Read from this map with a given key. If the corresponding value is not available, returns empty string.
	 * 
	 * @param k key
	 * @return corresponding value in string.
	 * @see java.util.HashMap#get(Object)
	 */
	@Override
	public String get(Object k) {
		if (super.get(k) != null) {
			return super.get(k).toString();
		}
		else {
			return "";
		}
	}

	public HashMap<String, String> toHashMap() {
		HashMap<String, String> rtn = new HashMap<String, String>();
		for (Object k : this.keySet()) {
			rtn.put(k.toString(), this.get(k).toString());
		}
		return rtn;
	}
}
