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
package edu.vt.vbi.patric.cache;

import java.util.Comparator;
import org.json.simple.JSONObject;

public class SeriesComparator implements Comparator<JSONObject> {
	public int compare(JSONObject s1, JSONObject s2) {
		return (Integer.parseInt(s1.get("value").toString()) - (Integer.parseInt(s2.get("value").toString())));
	}
}