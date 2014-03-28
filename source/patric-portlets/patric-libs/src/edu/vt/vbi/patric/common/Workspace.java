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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
/**
 * 
 */
public class Workspace {

	public final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	public JSONArray tracks;

	public JSONArray tags;

	public JSONArray mapping;

	public long timestamp;

	/**
	 * Construct Workspace Object
	 */
	public Workspace() {
		tracks = new JSONArray();
		tags = new JSONArray();
		mapping = new JSONArray();
		setTimestamp();
	}

	/**
	 * Construct Workspace with parameters
	 * 
	 * @param tracks
	 * @param tags
	 * @param mapping
	 */
	public Workspace(JSONArray tracks, JSONArray tags, JSONArray mapping) {
		this.tracks = tracks;
		this.tags = tags;
		this.mapping = mapping;
		setTimestamp();
	}

	/**
	 * Construct Workspace with given Workspace Object
	 * 
	 * @param ws JSONObject
	 */
	public Workspace(JSONObject ws) {
		this.tracks = (JSONArray) ws.get("tracks");
		this.tags = (JSONArray) ws.get("tags");
		this.mapping = (JSONArray) ws.get("mapping");

		if (ws.get("timestamp") != null) {
			this.timestamp = Long.parseLong(ws.get("timestamp").toString());
		}
		else {
			setTimestamp();
		}
	}

	/**
	 * Generate JSON object to feed Library (StationsList) View
	 * 
	 * @return JSONArray
	 */
	public JSONArray getLibraryByDataType() {
		int cntFeature = 0, cntGenome = 0, cntExpression = 0;
		if (tracks != null && tracks.size() > 0) {
			for (Object track : tracks) {
				JSONObject jsonTrack = (JSONObject) track;
				if (jsonTrack.get("trackType").toString().equals("Feature")) {
					cntFeature++;
				}
				else if (jsonTrack.get("trackType").toString().equals("Genome")) {
					cntGenome++;
				}
				else if (jsonTrack.get("trackType").toString().equals("ExpressionExperiment")) {
					cntExpression++;
				}
			}
		}

		JSONArray rtn = new JSONArray();
		JSONObject rtnGenome = new JSONObject();
		JSONObject rtnFeature = new JSONObject();
		JSONObject rtnExperiment = new JSONObject();

		rtnFeature.put("name", "Features (" + cntFeature + ")");
		rtnFeature.put("type", "Feature");
		rtnFeature.put("leaf", true);
		rtnFeature.put("id", 2);

		rtnGenome.put("name", "Genomes (" + cntGenome + ")");
		rtnGenome.put("type", "Genome");
		rtnGenome.put("leaf", true);
		rtnGenome.put("id", 3);

		rtnExperiment.put("name", "Experiments (" + cntExpression + ")");
		rtnExperiment.put("type", "ExpressionExperiment");
		rtnExperiment.put("leaf", true);
		rtnExperiment.put("id", 4);

		rtn.add(rtnGenome);
		rtn.add(rtnFeature);
		rtn.add(rtnExperiment);
		return rtn;
	}

	/**
	 * Get maximum Track ID
	 * 
	 * @return int Track ID
	 */
	public int getMaxTrackId() {
		int id = 0;
		int tmp = 0;

		for (Object track : tracks) {
			JSONObject jTrack = (JSONObject) track;
			tmp = Integer.parseInt(jTrack.get("trackId").toString());
			if (tmp > id) {
				id = tmp;
			}
		}
		return id;
	}

	/**
	 * Get maximum Tag ID
	 * 
	 * @return int Tag ID
	 */
	public int getMaxTagId() {
		int id = 0;
		int tmp = 0;

		for (Object tag : tags) {
			JSONObject jTrack = (JSONObject) tag;
			tmp = Integer.parseInt(jTrack.get("tagId").toString());
			if (tmp > id) {
				id = tmp;
			}
		}
		return id;
	}

	/**
	 * Create a Track
	 * 
	 * @param trackId
	 * @param type
	 * @param internalId
	 * @return JSONObject track
	 */
	public JSONObject createTrack(int trackId, String trackType, Object internalId) {
		JSONObject track = new JSONObject();
		track.put("trackId", trackId);
		track.put("trackType", trackType);
		track.put("internalId", internalId);

		return track;
	}

	/**
	 * Create a Tag. Group type.
	 * @param tagId
	 * @param name
	 * @param type
	 * @param desc
	 * @param count
	 * @param cdate
	 * @return
	 */
	public JSONObject createGroupTag(int tagId, String name, String type, String desc, int count, String cdate) {
		JSONObject tag = new JSONObject();
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		tag.put("tagId", tagId);
		tag.put("tagType", "Group");
		tag.put("name", name);
		tag.put("type", type);
		tag.put("desc", desc);
		tag.put("members", count);
		if (cdate == null) {
			tag.put("cdate", timestamp);
			tag.put("mdate", timestamp);
		}
		else {
			tag.put("cdate", cdate);
			tag.put("mdate", cdate);
		}
		return tag;
	}

	/**
	 * Create a Tag. String type.
	 * @param tagId
	 * @param name
	 * @return
	 */
	public JSONObject createStringTag(int tagId, String name) {
		JSONObject tag = new JSONObject();
		tag.put("tagId", tagId);
		tag.put("tagType", "String");
		tag.put("name", name);
		return tag;
	}

	/**
	 * Create a Map.
	 * @param tagId
	 * @param trackId
	 * @return
	 */
	public JSONObject createMapping(int tagId, int trackId) {
		JSONObject m = new JSONObject();
		m.put("tagId", tagId);
		m.put("trackId", trackId);

		return m;
	}

	/**
	 * Add a Track
	 * @param type
	 * @param internalId
	 * @return
	 */

	public int addTrack(String type, Object internalId) {
		int trackId = findTrack(type, internalId);
		if (trackId > 0) {
			return trackId;
		}
		else {
			trackId = getMaxTrackId() + 1;
			JSONObject track = createTrack(trackId, type, internalId);
			this.tracks.add(track);
			return trackId;
		}
	}

	public HashSet<Integer> addTracks(String type, HashSet<?> internalIds) {
		HashSet<Integer> trackIds = new HashSet<Integer>();
		int maxId = getMaxTrackId();
		int trackId = -1;

		for (Object internalId : internalIds) {
			trackId = findTrack(type, internalId);

			if (trackId > 0) {
				trackIds.add(trackId);
			}
			else {
				trackId = maxId + 1;
				maxId++;
				JSONObject track = createTrack(trackId, type, internalId);
				this.tracks.add(track);
				trackIds.add(trackId);
			}
		}
		return trackIds;
	}

	public HashSet<Integer> addTracks(String type, String internalIds) {

		HashSet<Object> hsInternalIds = new HashSet<Object>();

		for (String internalId : internalIds.split(",")) {
			try {
				hsInternalIds.add(Long.parseLong(internalId));
			}
			catch (NumberFormatException nfe) {
				hsInternalIds.add(internalId);
			}
		}

		return addTracks(type, hsInternalIds);
	}

	/**
	 * Find a Track matches a given type and internal ID. Return -1 if no track matches.
	 * @param type
	 * @param internalId
	 * @return
	 */
	public int findTrack(String type, Object internalId) {
		for (Iterator<JSONObject> iter = tracks.iterator(); iter.hasNext();) {
			JSONObject track = iter.next();
			// System.out.println("tracking type of internalID::"+track.get("internalId").getClass().getName());
			if (track.get("trackType").equals(type) && track.get("internalId").equals(internalId)) {
				return Integer.parseInt(track.get("trackId").toString());
			}
		}
		return -1;
	}

	/**
	 * Find Tracks matches a given type and a set of internal ID.
	 * @param type group
	 * @param internalIds either String or Long
	 * @return
	 */
	public HashSet<Integer> findTracks(String type, HashSet<?> internalIds) {
		HashSet<Integer> trackIds = new HashSet<Integer>();

		for (Object track : tracks) {
			JSONObject tr = (JSONObject) track;

			if (tr.get("trackType").equals(type) && internalIds.contains(tr.get("internalId"))) {
				trackIds.add(Integer.parseInt(tr.get("trackId").toString()));
			}

		}

		return trackIds;
	}

	public int findTag(String type, String name) {

		for (Iterator<JSONObject> iter = tags.iterator(); iter.hasNext();) {
			JSONObject tag = iter.next();
			if (tag.get("tagType").toString().equals(type) && tag.get("name").toString().equals(name)) {
				return Integer.parseInt(tag.get("tagId").toString());
			}
		}
		return -1;
	}

	public int findTag(String type, String name, String contentType) {

		for (Iterator<JSONObject> iter = tags.iterator(); iter.hasNext();) {
			JSONObject tag = iter.next();
			if (tag.get("tagType").toString().equals(type) && tag.get("name").toString().equals(name)
					&& tag.get("type").toString().equals(contentType)) {
				return Integer.parseInt(tag.get("tagId").toString());
			}
		}
		return -1;
	}

	public JSONObject findTagByTagId(int tagId) {
		for (Iterator<JSONObject> iter = tags.iterator(); iter.hasNext();) {
			JSONObject tag = iter.next();
			if (Integer.parseInt(tag.get("tagId").toString()) == tagId) {
				return tag;
			}
		}
		return null;
	}

	public int findIndexByTagId(int tagId) {

		for (int i = 0; i < tags.size(); i++) {
			JSONObject tag = (JSONObject) tags.get(i);
			if (Integer.parseInt(tag.get("tagId").toString()) == tagId) {
				return i;
			}
		}
		return -1;
	}

	public ArrayList<JSONObject> findMappingByTrackId(int trackId) {
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
			JSONObject m = iter.next();
			if (Integer.parseInt(m.get("trackId").toString()) == trackId) {
				result.add(m);
			}
		}
		return result;
	}

	public ArrayList<JSONObject> findMappingByTagId(int tagId) {
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
			JSONObject m = iter.next();
			if (Integer.parseInt(m.get("tagId").toString()) == tagId) {
				result.add(m);
			}
		}
		return result;
	}

	public boolean isMappingExist(int tagId, int trackId) {

		for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
			JSONObject m = iter.next();
			if (Integer.parseInt(m.get("tagId").toString()) == tagId && Integer.parseInt(m.get("trackId").toString()) == trackId) {
				return true;
			}
		}
		return false;
	}

	public int addGroup(String name, String type, String desc, int count, String date) {
		int tagId = getMaxTagId() + 1;
		JSONObject tag = createGroupTag(tagId, name, type, desc, count, date);
		this.tags.add(tag);
		return tagId;
	}

	public void addTagging(String tags, HashSet<Integer> trackId) {

		int tagId = -1;
		if (tags.contains(",")) {
			for (String tag : tags.split(",")) {
				tag = tag.trim();

				tagId = findTag("String", tag);
				if (tagId > 0) {
					for (int tId : trackId) {
						if (isMappingExist(tagId, tId) == false) {
							addMapping(tagId, tId);
						}
					}
				}
				else {
					// create
					tagId = getMaxTagId() + 1;
					JSONObject t = createStringTag(tagId, tag);
					this.tags.add(t);
					addMapping(tagId, trackId);
				}
			}
		}
		else if (!tags.equals("")) {
			tags = tags.trim();
			tagId = findTag("String", tags);
			if (tagId > 0) {
				for (int tId : trackId) {
					if (isMappingExist(tagId, tId) == false) {
						addMapping(tagId, tId);
					}
				}
			}
			else {
				// create
				tagId = getMaxTagId() + 1;
				JSONObject t = createStringTag(tagId, tags);
				this.tags.add(t);
				addMapping(tagId, trackId);
			}
		}
	}

	public void addMapping(int tagId, int trackId) {
		JSONObject m = createMapping(tagId, trackId);
		this.mapping.add(m);
	}

	public void addMapping(int tagId, HashSet<Integer> trackIds) {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();

		for (Integer trackId : trackIds) {
			JSONObject m = createMapping(tagId, trackId);
			list.add(m);
		}
		this.mapping.addAll(list);
	}

	public void updateGroupTag(int tagId, String name, String desc) {
		int new_count = countAssociation(tagId);
		int idx = findIndexByTagId(tagId);

		if (idx > -1) {
			JSONObject group = (JSONObject) tags.get(idx);

			if (name != null) {
				group.put("name", name);
			}
			if (desc != null) {
				group.put("desc", desc);
			}

			group.put("members", new_count);
			group.put("mdate", sdf.format(Calendar.getInstance().getTime()));
		}
	}

	public void removeTracks(HashSet<Integer> trackIds) {

		for (Iterator<JSONObject> iter = tracks.iterator(); iter.hasNext();) {
			JSONObject jsonTrack = iter.next();

			if (trackIds.contains(Integer.parseInt(jsonTrack.get("trackId").toString()))) {
				iter.remove();
			}
		}
	}

	public void removeTags(HashSet<Integer> tagIds) {

		for (Iterator<JSONObject> iter = tags.iterator(); iter.hasNext();) {
			JSONObject jsonTag = iter.next();

			if (tagIds.contains(Integer.parseInt(jsonTag.get("tagId").toString()))) {
				iter.remove();
			}
		}
	}

	public void removeTag(int tagId) {
		for (Iterator<JSONObject> iter = tags.iterator(); iter.hasNext();) {
			JSONObject jsonTag = iter.next();
			if (Integer.parseInt(jsonTag.get("tagId").toString()) == tagId) {
				iter.remove();
			}
		}
	}

	public void removeMapping(HashSet<Integer> tagIds, HashSet<Integer> trackIds) {

		if (tagIds != null && trackIds != null) {

			for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
				JSONObject jsonM = iter.next();

				if (trackIds.contains(Integer.parseInt(jsonM.get("trackId").toString()))
						&& tagIds.contains(Integer.parseInt(jsonM.get("tagId").toString()))) {
					iter.remove();
				}
			}
		}
		else if (tagIds == null && trackIds != null) {

			for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
				JSONObject jsonM = iter.next();

				if (trackIds.contains(Integer.parseInt(jsonM.get("trackId").toString()))) {
					iter.remove();
				}
			}
		}
		else if (tagIds != null && trackIds == null) {

			for (Iterator<JSONObject> iter = mapping.iterator(); iter.hasNext();) {
				JSONObject jsonM = iter.next();

				if (tagIds.contains(Integer.parseInt(jsonM.get("tagId").toString()))) {
					iter.remove();
				}
			}
		}
	}

	public JSONArray getTracks(HashSet<Integer> trackIds) {
		JSONArray rtn = new JSONArray();

		for (Object track : tracks) {
			JSONObject jsonTrack = (JSONObject) track;
			if (trackIds.contains(Integer.parseInt(jsonTrack.get("trackId").toString()))) {
				rtn.add(jsonTrack);
			}
		}

		return rtn;
	}

	public JSONArray getTracks(JSONObject filters) {
		JSONArray rtn = new JSONArray();
		String k = "", v = "";

		if (tracks != null && tracks.size() > 0) {
			if (filters.containsKey("key") && filters.containsKey("value")) {
				k = filters.get("key").toString();
				v = filters.get("value").toString();
				HashSet<String> hash = new HashSet<String>();
				hash.addAll(Arrays.asList(v.split(",")));

				for (Object track : tracks) {
					JSONObject jsonTrack = (JSONObject) track;
					if (jsonTrack.containsKey(k) && hash.contains(jsonTrack.get(k).toString())) { // pass
																									// filters
						rtn.add(jsonTrack);
					}
				}
			}
		}
		return rtn;
	}

	public JSONArray getGroups(JSONObject filters) {
		JSONArray rtn = new JSONArray();
		String k = "", v = "";

		if (tags != null && tags.size() > 0) {
			if (filters.containsKey("key") && filters.containsKey("value")) {
				k = filters.get("key").toString();
				v = filters.get("value").toString();

				HashSet<String> hash = new HashSet<String>();
				hash.addAll(Arrays.asList(v.split(",")));

				for (Object tag : tags) {
					JSONObject jsonTag = (JSONObject) tag;
					if (jsonTag.get("tagType").equals("Group") && hash.contains(jsonTag.get(k).toString())) {
						rtn.add(jsonTag);
					}
				}
			}
		}
		return rtn;
	}

	public JSONArray getTags(JSONObject filters) {
		JSONArray rtn = new JSONArray();
		String k = "", v = "";

		if (tags != null && tags.size() > 0) {

			if (filters.containsKey("key") && filters.containsKey("value")) {
				k = filters.get("key").toString();
				v = filters.get("value").toString();
				HashSet<String> hash = new HashSet<String>();
				hash.addAll(Arrays.asList(v.split(",")));

				for (Object tag : tags) {
					JSONObject jsonTag = (JSONObject) tag;
					if (jsonTag.containsKey(k) && hash.contains(jsonTag.get(k).toString())) {
						rtn.add(jsonTag);
					}
				}
			}
		}
		return rtn;
	}

	public int countAssociation(int tagId) {
		int count = 0;

		if (mapping != null && mapping.size() > 0) {
			for (Object map : mapping) {
				JSONObject jsonMap = (JSONObject) map;
				if (Integer.parseInt(jsonMap.get("tagId").toString()) == tagId) {
					count++;
				}
			}
		}

		return count;
	}

	public boolean hasAssociation(String type, int trackId) {
		boolean test = false;
		ArrayList<JSONObject> associations = this.findMappingByTrackId(trackId);
		for (JSONObject m : associations) {
			JSONObject tag = findTagByTagId(Integer.parseInt(m.get("tagId").toString()));
			if (tag != null && tag.get("tagType").toString().equals(type)) {
				test = true;
			}
		}
		return test;
	}

	public JSONObject getWorkspace() {
		JSONObject ws = new JSONObject();

		ws.put("tracks", tracks);
		ws.put("tags", tags);
		ws.put("mapping", mapping);

		return ws;
	}

	public JSONArray getTracks() {
		return tracks;
	}

	public int getTrackCount() {
		if (this.tracks != null) {
			return tracks.size();
		}
		return -1;
	}

	public int getGroupCount() {
		if (this.tags != null) {
			return getGroups().size();
		}
		return -1;
	}

	public void setTracks(JSONArray tracks) {
		this.tracks = tracks;
	}

	public JSONArray getTags() {
		return tags;
	}

	public JSONArray getGroups() {
		JSONArray rtn = new JSONArray();
		for (Object tag : tags) {
			JSONObject jsonTag = (JSONObject) tag;
			if (jsonTag.get("tagType").equals("Group")) {
				rtn.add(jsonTag);
			}
		}
		return rtn;
	}

	public void setTags(JSONArray tags) {
		this.tags = tags;
	}

	public JSONArray getMapping() {
		return mapping;
	}

	public void setMapping(JSONArray mapping) {
		this.mapping = mapping;
	}

	public void setTimestamp() {
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
