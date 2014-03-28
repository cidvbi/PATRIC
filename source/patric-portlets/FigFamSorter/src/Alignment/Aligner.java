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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;

import edu.vt.vbi.ci.util.CommandResults;
import edu.vt.vbi.ci.util.ExecUtilities;
import figfamGroups.edu.vt.vbi.image.Newick;

//import edu.vt.vbi.ci.nongraphic.alignment.AlignmentFormatConverter;

public class Aligner {
	private final static String[] gBlocksDrops = { "</head>", "<h2>Gblocks", "<pre>", "<title>", "<body bgcolor",
			"Processed file:" };

	/*
	 * private final static int HEAD_WADE = 0; private final static int PRE_PRE = 1; private final static int IN_PRE =
	 * 2; private final static int POST_PRE = 3; private final static int TAIL_WADE = 4;
	 */
	private String prefix;

	private char getHtml;

	private SequenceData[] sequences = null;

	private String groupID = null;

	private File trimAligned = null;

	private File rawAligned = null;

	private String[] treeLines = null;

	private int[] aaRange = { Integer.MAX_VALUE, 0 };

	private int genomeCount = 1;

	public Aligner(String newickText, String locusNames, String genomeNames) {
		treeLines = new String[1];
		treeLines[0] = newickText;
		String[] locusList = locusNames.split("\t");
		String[] genomeList = genomeNames.split("\t");
		int minLength = Math.min(locusList.length, genomeList.length);
		sequences = new SequenceData[minLength];
		for (int i = 0; i < sequences.length; i++) {
			sequences[i] = new SequenceData(locusList[i], genomeList[i], null);
			(sequences[i]).fastaOrder = i;
		}
		File toDrop = setPrefix();
		toDrop.delete();
	}

	public Aligner(String groupId, String locusNames, String genomeNames, String alignedSequences) {
		this.groupID = groupId;
		String[] locusList = locusNames.split("\t");
		String[] genomeList = genomeNames.split("\t");
		String[] seqList = alignedSequences.split("\t");
		int minLength = Math.min(locusList.length, genomeList.length);
		minLength = Math.min(seqList.length, minLength);
		sequences = new SequenceData[minLength];
		for (int i = 0; i < sequences.length; i++) {
			sequences[i] = new SequenceData(locusList[i], genomeList[i], seqList[i]);
			(sequences[i]).fastaOrder = i;
		}
		File toDrop = setPrefix();
		toDrop.delete();
	}

	public Aligner(char needHtml, String groupId, SequenceData[] sequences) {
		// save flag for Gblocks run
		getHtml = needHtml;
		if ((groupId != null) && (groupId.length() == 0)) {
			groupId = null;
		}
		this.groupID = groupId;
		this.sequences = sequences;
		if (sequences != null) {
			try {
				File tmpFaa = setPrefix();
				BufferedWriter faaWrite = new BufferedWriter(new FileWriter(tmpFaa));
				String[] genomeNames = new String[sequences.length];
				for (int i = 0; i < sequences.length; i++) {
					(sequences[i]).fastaOrder = i;
					genomeNames[i] = (sequences[i]).setFasta(50, faaWrite, aaRange);
				}
				faaWrite.close();
				Arrays.sort(genomeNames);
				String checkName = genomeNames[0];
				for (int i = 1; i < genomeNames.length; i++) {
					if (!checkName.equals(genomeNames[i])) {
						++genomeCount;
						checkName = genomeNames[i];
					}
				}

				Arrays.sort(sequences);
				ExecUtilities.exec("muscle -fasta -stable -in " + tmpFaa.getAbsolutePath() + " -out " + prefix + "aga");

				ExecUtilities.exec("Gblocks " + prefix + "aga -b5=h -p=" + getHtml);
				rawAligned = new File(prefix + "aga");
				trimAligned = new File(prefix + "aga-gb");
				if (!trimAligned.exists()) {
					trimAligned = null;
				}
				else {
					BufferedReader checker = new BufferedReader(new FileReader(trimAligned));
					boolean empty = true;
					String line = checker.readLine();
					ArrayList<String> locusList = new ArrayList<String>();
					while (line != null) {
						line = line.trim();
						if (0 == line.length()) {
							line = checker.readLine();
						}
						else if (line.startsWith(">")) {
							locusList.add(line.substring(1));
							line = checker.readLine();
						}
						else {
							empty = false;
							line = null;
						}
					}
					checker.close();
					if (empty) {
						trimAligned.delete();
						checker = new BufferedReader(new FileReader(rawAligned));
						BufferedWriter writer = new BufferedWriter(new FileWriter(trimAligned));
						line = checker.readLine();
						while (line != null) {
							writer.write(line);
							writer.newLine();
							line = checker.readLine();
						}
						writer.close();
						checker.close();
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File setPrefix() {
		File tmpFaa = null;
		try {
			tmpFaa = File.createTempFile("msa", ".faa");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		prefix = tmpFaa.getAbsolutePath();
		int pAt = prefix.lastIndexOf('.');
		if (0 <= pAt) {
			prefix = prefix.substring(0, pAt + 1);
		}
		return tmpFaa;
	}
	
	public void runFastTree() {
		if (trimAligned != null) {
			CommandResults treeHold = ExecUtilities.exec("FastTree_LG -gamma -nosupport "
					+ trimAligned.getAbsolutePath());

			if (treeHold != null) {
				treeLines = treeHold.getStdout();
			}

			//System.out.print(treeLines.length);
		}
	}

	private SequenceData getDataForLocus(String toCheck) {
		SequenceData result = null;
		if (toCheck.startsWith(">")) {
			result = new SequenceData(toCheck.substring(1));
			int at = Arrays.binarySearch(sequences, result);
			if (0 <= at) {
				result = sequences[at];
			}
		}
		return result;
	}

	public void setAlignTree(PrintWriter writer) throws IOException {
		writer.write("" + sequences.length);
		writer.write("\t" + genomeCount);
		writer.write("\t" + aaRange[0] + "\t" + aaRange[1] + "\t");
		for (int i = 0; i < treeLines.length; i++) {
			writer.write(treeLines[i]);
			// System.out.print(treeLines[i]);
		}

		BufferedReader msaRead = new BufferedReader(new FileReader(rawAligned));
		String msaLine = msaRead.readLine();
		while ((msaLine != null) && (!msaLine.startsWith(">"))) {
			msaLine = msaRead.readLine();
		}
		if (msaLine != null) {
			SequenceData lastData = getDataForLocus(msaLine);
			StringBuffer sequence = new StringBuffer();
			msaLine = msaRead.readLine();
			while (msaLine != null) {
				SequenceData nextData = getDataForLocus(msaLine);
				if (nextData != null) {
					if (0 < sequence.length()) {
						writer.write("\t" + lastData.locus + "\t" + lastData.taxonName + "\t" + sequence.toString());
						sequence = new StringBuffer();
						lastData = nextData;
					}
				}
				else {
					String[] parts = msaLine.split("\\s");
					for (int i = 0; i < parts.length; i++) {
						sequence.append(parts[i]);
					}
				}
				msaLine = msaRead.readLine();
			}
			if (0 < sequence.length()) {
				writer.write("\t" + lastData.locus + "\t" + lastData.taxonName + "\t" + sequence.toString());

				writer.write("\f" + rawAligned.getAbsolutePath());
			}
		}
		msaRead.close();
		// trimAligned.delete();
		trimAligned = null;
		// rawAligned.delete();
		rawAligned = null;
	}

	public void setTreePng(boolean genomeTips, boolean flushTips, PrintWriter writer) throws IOException {
		if (rawAligned != null) {
			rawAligned.delete();
			rawAligned = null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < treeLines.length; i++) {
			sb.append(treeLines[i]);
		}
		Newick treeForm = new Newick(sb.toString());
		treeForm.setGenomeNames(sequences);
		treeForm.setTreeType(genomeTips, flushTips);
		BufferedImage gGetter = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

		// Create a graphics contents on the buffered image
		Graphics2D g2d = gGetter.createGraphics();

		Dimension preferred = treeForm.getPreferredSize(1, g2d);
		preferred.width = 666;

		// Graphics context no longer needed so dispose it
		// g2d.dispose();

		gGetter = new BufferedImage(preferred.width, preferred.height, BufferedImage.TYPE_INT_RGB);

		treeForm.paint(gGetter);

		File pngFile = new File(prefix + "png");
		try {
			ImageIO.write(gGetter, "PNG", pngFile);
		}
		catch (IOException err) {
			err.printStackTrace();
		}

		g2d.dispose();
		writer.write(prefix + "png");
		String[] nameOrder = new String[sequences.length];

		for (int i = 0; i < sequences.length; i++) {
			int at = treeForm.getTipIndex((sequences[i]).locus);
			nameOrder[at] = (sequences[i]).getLongName();
		}

		for (int i = 0; i < nameOrder.length; i++) {
			writer.write("\t" + nameOrder[i]);
		}
		if (trimAligned != null) {
			trimAligned.delete();
			trimAligned = null;
		}
	}

	public void getGblocksPrintable(boolean genomeTags, String conserveChop, String description, PrintWriter writer)
			throws IOException {
		if (trimAligned != null) {
			if (rawAligned != null) {
				rawAligned.delete();
				rawAligned = null;
			}
		}
		else {
			rawAligned = new File(prefix + "aga");
			if (!rawAligned.exists()) {
				BufferedWriter aWrite = new BufferedWriter(new FileWriter(rawAligned));
				for (int i = 0; i < sequences.length; i++) {
					(sequences[i]).writeToFasta(aWrite);
				}
				aWrite.close();
			}
			int chop = Integer.parseInt(conserveChop);
			int minCheck = sequences.length;
			minCheck >>= 1;
			++minCheck;
			if (minCheck == chop) {
				ExecUtilities.exec("Gblocks " + prefix + "aga -b5=h -p=y");
			}
			else {
				if (chop < sequences.length) {
					++chop;
				}
				ExecUtilities.exec("Gblocks " + prefix + "aga -p=y -b5=h -b2=" + chop + " -b1=" + conserveChop);
			}
			rawAligned.delete();
			rawAligned = null;
			trimAligned = new File(prefix + "aga-gb");
		}
		File alignmentFile = new File(prefix + "aga-gb.htm");
		adjustAlignHtml(genomeTags, description, alignmentFile, writer);
		// trimAligned.delete();
		trimAligned = null;
		alignmentFile.delete();
	}

	int checkForSpecial(String line) {
		int result = -1;
		for (int i = 0; i < gBlocksDrops.length; i++) {
			if (line.startsWith(gBlocksDrops[i])) {
				result = i;
				i = gBlocksDrops.length;
			}
		}
		return result;
	}

	private void adjustAlignHtml(boolean genomeTags, String description, File alignmentFile, PrintWriter writer)
			throws IOException {
		SequenceData[] sortSave = sequences;
		sequences = new SequenceData[sequences.length];
		for (int i = 0; i < sortSave.length; i++) {
			sequences[(sortSave[i]).fastaOrder] = sortSave[i];
		}
		String[] expander = new String[sequences.length];
		int maxLeft = 0;
		String line = null;
		for (int i = 0; i < sequences.length; i++) {
			if (genomeTags) {
				line = (sequences[i]).taxonName;
			}
			else {
				line = (sequences[i]).locus;
			}
			int nextLength = line.length();
			if (22 < nextLength) {
				line = line.substring(0, 22);
				maxLeft = 22;
			}
			else {
				maxLeft = Math.max(maxLeft, nextLength);
			}
			expander[i] = line;
		}
		char[] emptyForm = new char[maxLeft];
		Arrays.fill(emptyForm, ' ');
		String emptyLeft = new String(emptyForm);

		BufferedReader reader = new BufferedReader(new FileReader(alignmentFile));
		line = reader.readLine();
		boolean prePre = true;
		boolean postPre = false;
		ArrayList<String> leftSide = new ArrayList<String>();
		String equalSkip = null;
		int skipCount = 0;
		ArrayList<String> rightSide = new ArrayList<String>();
		int expCheck = 0;
		while (line != null) {
			if (prePre) {
				int preAt = checkForSpecial(line);
				if (preAt < 0) {
					writer.write(line + "\n");
				}
				else if (preAt == 0) {
					writer.write("</head>\n");
					writer.write("<body id='popup'>\n");
					writer.write("<div id='page-wrapper-content-bg'>\n");
					writer.write("<div id='page-wrapper'>\n");
					writer.write("<div id='page-area>\n");
					/*
					 * writer.write("<div id='header'>" + "<img src=\"/patric/images/logo_popup.gif\" " +
					 * "width='219' height='84' " + "alt='Patric - PathoSystems Resource Integration Center' />" +
					 * "</div>\n");
					 */
					writer.write("<div id='content-area'>\n");
				}
				else if (preAt == 1) {
					preAt = line.indexOf("Results");
					line = line.substring(0, preAt);
					if ((groupID != null) && (0 < groupID.length())) {
						line += "for " + groupID + ":";
						if (description != null) {
							line += description;
						}
					}
					writer.write(line + "</h2>\n");
				}
				else if (preAt == 2) {
					prePre = false;
				}
			}
			else if (postPre) {
				for (int i = 0; i < sequences.length; i++)
					if (line.startsWith("New number of positions")) {
						int keepAt = line.indexOf("<b>");
						line = line.substring(keepAt);
						line = "New number of positions " + line;
					}
					else if (line.startsWith("</body>")) {
						line = "</div></div></div></div>" + line;
					}
				writer.write(line + "\n");
			}
			else {
				if (line.startsWith("</pre>")) {
					writer.write("<pre>");
					Iterator<String> itLeft = leftSide.iterator();
					Iterator<String> itRight = rightSide.iterator();
					while (itLeft.hasNext()) {
						String next = itLeft.next();
						int indent = maxLeft - next.length();
						if (0 < indent) {
							next += emptyLeft.substring(0, indent);
						}
						writer.write(next + " ");
						next = itRight.next();
						if (next.startsWith(equalSkip)) {
							next = next.substring(skipCount);
						}
						writer.write(next + "\n");
					}
					postPre = true;
					for (int i = 0; i < sequences.length; i++) {
						(sequences[i]).writeLongName(writer);
					}
				}
				else {
					String eCheck = line.trim();
					if (eCheck.length() == 0) {
						leftSide.add(emptyLeft);
						rightSide.add("");
					}
					else if (line.charAt(0) != ' ') {
						leftSide.add(expander[expCheck]);
						++expCheck;
						if (expander.length <= expCheck) {
							expCheck = 0;
						}
						rightSide.add(line.substring(skipCount));
					}
					else {
						leftSide.add(emptyLeft);
						int equalAt = line.indexOf("===");
						if (equalAt < 0) {
							rightSide.add(line);
						}
						else {
							if (equalSkip == null) {
								equalSkip = line.substring(0, equalAt);
								skipCount = equalAt;
							}
							rightSide.add(line.substring(equalAt));
						}
					}
				}
			}
			line = reader.readLine();
		}
		reader.close();
		sequences = sortSave;
	}
}
