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
package Alignment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.hibernate.lob.SerializableClob;

public class SequenceData implements Comparable<SequenceData> {
	String taxonName = null;;

	String locus;

	String aminos;

	int fastaOrder = 0;

	SequenceData(String locus) {
		this.locus = locus;
		taxonName = null;
	}

	public SequenceData(String locusText, String genomeText, String sequence) {
		locus = locusText;
		taxonName = genomeText;
		aminos = sequence;
	}

	public SequenceData(Object taxon, Object sourceId, Object sequence) {
		taxonName = "" + taxon;
		locus = "" + sourceId;
		SerializableClob clobSeq = (SerializableClob) (sequence);
		try {
			aminos = IOUtils.toString(clobSeq.getAsciiStream(), "UTF-8");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean doTaxonsDiffer(SequenceData other) {
		return (!(this.taxonName).equals(other.taxonName));
	}

	public void writeLongName(PrintWriter writer) {
		writer.write(locus + "  " + taxonName + "\n");
	}

	public String getLocusTag() {
		return locus;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public String getLongName() {
		return (taxonName + " " + locus);
	}

	public String setFasta(int maxName, BufferedWriter writer, int[] aaRange) throws IOException {
		if (aaRange != null) {
			int set = aminos.length();
			if (set < aaRange[0]) {
				aaRange[0] = set;
			}
			if (aaRange[1] < set) {
				aaRange[1] = set;
			}
		}

		writer.write(">");
		if (locus.length() < maxName) {
			writer.write(locus);
		}
		else {
			writer.write(locus.substring(0, maxName - 1));
		}
		writer.write("\n" + aminos + "\n");
		return (taxonName);
	}

	public void writeTaxon(BufferedWriter writer) throws IOException {
		writer.write(taxonName);
	}

	public void writeToFasta(BufferedWriter writer) throws IOException {
		writer.write(">" + locus);
		writer.newLine();
		writer.write(aminos);
		writer.newLine();
	}

	public int compareTo(SequenceData arg0) {
		return ((this.locus).compareTo(arg0.locus));
	}
}
