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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CRResultSet extends HashMap<Integer, CRTrack> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4687109777629511114L;

	JSONParser parser = new JSONParser();

	private String pinStrand;

	private String pinGenome;

	private HashSet<String> genomeNames;

	private ArrayList<String> defaultTracks;

	public CRResultSet(String pin, BufferedReader br) {
		pinGenome = pin.replace("fig|", "").split(".peg.[0-9]*")[0];

		try {
			if (br != null) {
				JSONObject res = (JSONObject) parser.parse(br);
				JSONArray tracks = (JSONArray) res.get(pin);
				genomeNames = new HashSet<String>();
				defaultTracks = new ArrayList<String>();

				for (int i = 0; i < tracks.size(); i++) {
					JSONObject tr = (JSONObject) tracks.get(i);

					CRTrack crTrk = new CRTrack(tr);
					super.put(crTrk.getRowID(), crTrk);
					genomeNames.add(crTrk.getGenomeName());
					if (pinGenome.equals(crTrk.getGenomeID())) {
						pinStrand = crTrk.findFeature(pin).getStrand();
					}
				}
			}
			else {
				System.out.println("BufferedReader is null");
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPinStrand() {
		return pinStrand;
	}

	public HashSet<String> getGenomeNames() {
		return genomeNames;
	}

	public void addToDefaultTracks(CRTrack crTrk) {
		if (pinGenome.equals(crTrk.getGenomeID())) {
			ArrayList<String> newTrack = new ArrayList<String>();
			newTrack.add("CR" + crTrk.getRowID());
			newTrack.addAll(defaultTracks);
			defaultTracks = newTrack;
		}
		else {
			defaultTracks.add("CR" + crTrk.getRowID());
		}
	}

	public String getDefaultTracks() {
		return StringHelper.implode(defaultTracks.toArray(), ",");
	}
}
