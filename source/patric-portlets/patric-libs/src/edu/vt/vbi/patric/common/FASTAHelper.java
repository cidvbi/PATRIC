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

import org.apache.commons.lang.StringUtils;

import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

public class FASTAHelper {

	static DBShared conn_shared = new DBShared();

	public static String getFASTANASequence(String fid) {
		ArrayList<ResultType> sequences = conn_shared.getFastaNASequence(fid);
		String na_sequence = "";

		if (sequences.size() > 1) {
			for (int i = 0; i < sequences.size(); i++) {
				na_sequence += sequences.get(i).get("na_sequence");
			}
		}
		else if (sequences.size() == 1) {
			na_sequence = sequences.get(0).get("na_sequence");
		}

		if (sequences.get(0).get("is_reversed").equalsIgnoreCase("1")) {
			na_sequence = getComplement(na_sequence);
		}
		if (na_sequence.length() > 0) {
			na_sequence = StringHelper.chunk_split(na_sequence, 60, "\n");
			return getFASTAIdentifier(fid) + "\n" + na_sequence;
		}
		else {
			return "";
		}
	}

	public static String getFASTAAASequence(String fid) {
		String aa_sequence = conn_shared.getFastaAASequence(fid);
		if (aa_sequence != null && aa_sequence.length() > 0) {
			aa_sequence = StringHelper.chunk_split(aa_sequence, 60, "\n");
			return getFASTAIdentifier(fid) + "\n" + aa_sequence;
		}
		else {
			return "";
		}
	}

	/*
	 * public static String getFASTAAASequence(List fids) { String aa_sequence =
	 * conn_shared.getFastaAASequence(fid);; if (aa_sequence.length()>0) {
	 * aa_sequence = StringHelper.chunk_split(aa_sequence, 60, "\n"); return
	 * getFASTAIdentifier(fid)+"\n"+aa_sequence; } else { return ""; } }
	 */
	public static String getFASTAIdentifier(String fid) {
		ResultType hashID = conn_shared.getFastaIdentifiers(fid);
		String id = "";
		/*
		 * if (hashID.get("protein_id").equals("")) { id =
		 * ">fid|"+hashID.get("na_feature_id"
		 * )+"|locus|"+hashID.get("source_id")+
		 * "| "+hashID.get("product")+" ["+hashID.get("genome_name")+"]"; } else
		 * { id =
		 * ">fid|"+hashID.get("na_feature_id")+"|locus|"+hashID.get("source_id"
		 * )+
		 * "|accn|"+hashID.get("protein_id")+"| "+hashID.get("product")+" ["+hashID
		 * .get("genome_name")+"]"; }
		 */
		id = ">fid|" + hashID.get("na_feature_id") + "|locus|" + hashID.get("source_id") + "|   "
				+ hashID.get("product") + "   [" + hashID.get("genome_name") + "]";
		return id;
	}

	public static String getComplement(String sequence) {
		String reversed = StringUtils.reverse(sequence.toLowerCase());
		reversed = reversed.replace('a', 'x');
		reversed = reversed.replace('t', 'a');
		reversed = reversed.replace('x', 't');
		reversed = reversed.replace('c', 'x');
		reversed = reversed.replace('g', 'c');
		reversed = reversed.replace('x', 'g');

		return reversed;
	}
}
