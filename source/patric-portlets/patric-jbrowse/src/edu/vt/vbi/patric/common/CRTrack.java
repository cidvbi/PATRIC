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

import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CRTrack extends ArrayList<CRFeature> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7500241033679379068L;

	private int rowID;

	private String pin, genomeID, genomeName;

	private HashSet<String> featureIDs;

	public CRTrack(JSONObject jsonTrack) {
		rowID = Integer.parseInt(jsonTrack.get("row_id").toString());
		pin = jsonTrack.get("pin").toString();
		genomeID = jsonTrack.get("genome_id").toString();
		genomeName = jsonTrack.get("genome_name").toString();
		JSONArray jsonFeatures = (JSONArray) jsonTrack.get("features");
		featureIDs = new HashSet<String>();
		for (int i = 0; i < jsonFeatures.size(); i++) {
			CRFeature f = new CRFeature((JSONArray) jsonFeatures.get(i));
			super.add(f);
			featureIDs.add(f.getfeatureID());
		}
	}

	public CRFeature findFeature(String featureID) {
		CRFeature f = null;
		for (int i = 0; i < super.size(); i++) {
			if (super.get(i).getfeatureID().equals(featureID)) {
				f = super.get(i);
				break;
			}
		}
		return f;
	}

	public String getPSEEDIDs() {
		// return StringHelper.implode(featureIDs.toArray(),",");
		StringBuffer sb = new StringBuffer();
		for (String id : featureIDs) {
			if (sb.length() > 0) {
				sb.append(" OR ");
			}
			sb.append("\"" + id + "\"");
		}
		return sb.toString();
	}

	public void relocateFeatures(int window_size, String pin_strand) {
		CRFeature genome_pin = findFeature(this.getPin());
		int center = genome_pin.getCenterPosition();

		boolean isThisGenomeReversed = false;
		if (!genome_pin.getStrand().equals(pin_strand)) {
			isThisGenomeReversed = true;
		}

		// System.out.println("[debug:CRTrack.relocateFeatures]"+this.genomeName+","+center+","+window_size);
		for (int idx = 0; idx < super.size(); idx++) {
			CRFeature f = super.get(idx);
			// System.out.println("[debug:CRTrack.relocateFeatures]"+idx+","+f.getStartPosition()+":"+f.getEndPosition()+","+f.getStrand()+","+isThisGenomeReversed);
			int tS = (f.getStartPosition() - center) + window_size / 2;
			int tE = (f.getEndPosition() - center) + window_size / 2;

			// System.out.println("[debug:CRTrack.relocateFeatures]"+idx+",("+(f.getStartPosition()-center)+"):("+(f.getEndPosition()-center)+"),");

			if (isThisGenomeReversed) {
				int _tS = window_size - tS;
				int _tE = window_size - tE;
				tS = _tE;
				tE = _tS;

				if (f.getStrand().equals("+")) {
					f.setStrand("-");
				}
				else {
					f.setStrand("+");
				}
			}
			f.setStartPosition(tS);
			f.setEndPosition(tE);

			if (genome_pin.getfeatureID().equals(f.getfeatureID())) {
				f.setPhase(0);
			}
			super.set(idx, f);
			// System.out.println("[debug:CRTrack.relocateFeatures]"+idx+","+f.getStartPosition()+":"+f.getEndPosition()+","+f.getStrand());
		}

	}

	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getGenomeID() {
		return genomeID;
	}

	public void setGenomeID(String genomeID) {
		this.genomeID = genomeID;
	}

	public String getGenomeName() {
		return genomeName;
	}

	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}
}
