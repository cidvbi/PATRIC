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

import org.json.simple.JSONArray;

public class CRFeature implements Comparable<CRFeature> {
	// [ "fig|511145.12.peg.1", "Thr operon leader peptide","FIG164298","83333.1:NC_000913",190,255,"+",0,1]
	private final String featureID, product, figfam, contig;

	private String strand;

	private int startPosition, endPosition, rownum, grpnum, phase;

	private String genome = null;

	public CRFeature(JSONArray jsonFeature) {

		if (jsonFeature.get(0) != null) {
			featureID = jsonFeature.get(0).toString();
		}
		else {
			featureID = "";
		}
		if (jsonFeature.get(1) != null) {
			product = jsonFeature.get(1).toString();
		}
		else {
			product = "";
		}
		if (jsonFeature.get(2) != null) {
			figfam = jsonFeature.get(2).toString();
		}
		else {
			figfam = "";
		}
		if (jsonFeature.get(3) != null) {
			contig = jsonFeature.get(3).toString();
		}
		else {
			contig = "";
		}
		if (jsonFeature.get(4) != null && jsonFeature.get(5) != null) {
			int osp = Integer.parseInt(jsonFeature.get(4).toString());
			int oep = Integer.parseInt(jsonFeature.get(5).toString());
			if (osp < oep) {
				startPosition = osp;
				endPosition = oep;
			}
			else {
				startPosition = oep;
				endPosition = osp;
			}
		}
		else {
			startPosition = 0;
			endPosition = 0;
		}
		if (jsonFeature.get(6) != null) {
			strand = jsonFeature.get(6).toString();
		}
		else {
			strand = "";
		}
		if (jsonFeature.get(7) != null) {
			rownum = Integer.parseInt(jsonFeature.get(7).toString());
		}
		else {
			rownum = 0;
		}
		if (jsonFeature.size() > 8 && jsonFeature.get(8) != null) {
			grpnum = Integer.parseInt(jsonFeature.get(8).toString());
			if (grpnum > 0) {
				this.phase = (grpnum - 1) % 7 + 1;
			}
			else {
				this.phase = grpnum;
			}
		}
		else {
			grpnum = 0;
			phase = 6;
		}
	}

	public Integer getCenterPosition() {
		return startPosition + (endPosition - startPosition) / 2;
	}

	public void setGenomeName(String name) {
		genome = name;
	}

	public String getGenomeName() {
		return genome;
	}

	public int compareTo(CRFeature f) {
		if (startPosition == f.startPosition) {
			return endPosition - f.endPosition;
		}
		else {
			return startPosition - f.startPosition;
		}
	}

	public String getfeatureID() {
		return featureID;
	}

	public String getProduct() {
		return product;
	}

	public String getFigfam() {
		return figfam;
	}

	public String getContig() {
		return contig;
	}

	public void setStartPosition(int p) {
		this.startPosition = p;
	}

	public int getStartPosition() {
		return startPosition - 1;
	}

	public int getStartString() {
		return startPosition;
	}

	public void setEndPosition(int p) {
		this.endPosition = p;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String s) {
		this.strand = s;
	}

	public int getRowNum() {
		return rownum;
	}

	public int getGrpNum() {
		return grpnum;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int p) {
		this.phase = p;
	}

}
