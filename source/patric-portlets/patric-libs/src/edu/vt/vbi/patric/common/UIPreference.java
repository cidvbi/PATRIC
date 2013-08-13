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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UIPreference {

	public HashMap<String, String> state;

	public long timestamp;

	public UIPreference() {
		state = new HashMap<String, String>();
		setTimestamp();
	}

	/**
	 * Construct UIPreference with given UIPreference Object
	 * 
	 * @param ws JSONObject
	 */
	public UIPreference(JSONObject pref) {

		state = new HashMap<String, String>();

		JSONArray stateArray = (JSONArray) pref.get("state");
		if (stateArray != null && stateArray.isEmpty() == false) {
			this.setStateList(stateArray);
		}

		if (pref.get("timestamp") != null) {
			this.timestamp = Long.parseLong(pref.get("timestamp").toString());
		}
		else {
			setTimestamp();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUIPreference() {
		JSONObject ui = new JSONObject();

		ui.put("state", this.getStateList());
		// ui.put("timestamp", this.timestamp);

		return ui;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getStateList() {
		JSONArray stateArray = new JSONArray();
		if (state != null && state.size() > 0) {
			for (Map.Entry<String, String> entry : state.entrySet()) {
				JSONObject item = new JSONObject();
				item.put("name", entry.getKey());
				item.put("value", entry.getValue());

				stateArray.add(item);
			}
		}
		return stateArray;
	}

	public void setStateList(JSONArray stateArray) {
		for (Object obj : stateArray) {
			this.setState((JSONObject) obj);
		}
	}

	public String getState(String key) {
		if (state.containsKey(key)) {
			return state.get(key);
		}
		else {
			return null;
		}
	}

	public void setState(String key, String value) {
		state.put(key, value);
	}

	public void setState(JSONObject st) {
		if (st.containsKey("name") && st.get("name") != null && st.containsKey("value") && st.get("value") != null) {
			state.put((String) st.get("name"), (String) st.get("value"));
		}
	}

	public void resetState(String key) {
		state.remove(key);
	}

	public void reset() {
		state.clear();
	}

	public void setTimestamp() {
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
