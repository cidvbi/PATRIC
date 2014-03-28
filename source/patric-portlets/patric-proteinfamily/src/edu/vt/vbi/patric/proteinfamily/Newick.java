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
package edu.vt.vbi.patric.proteinfamily;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import edu.vt.vbi.patric.msa.SequenceData;

public class Newick {

	private String nexus;

	private final static int STEM_START = 0;

	private final static int IN_NAME = 1;

	private final static int IN_WIDTH = 2;

	private int tipCount = 0;

	private Stem[] tips = null;

	private TipFinder[] nameSearch;

	private Stem[] stems = null;

	private double maxDepth = 0.0;

	private double minWidth = Double.MAX_VALUE;

	private boolean genomeTips = false;

	private boolean flushTips = false;

	private int maxLevel = 0;

	private final static int distChop = 6;

	public Newick(String nexus) {
		this.nexus = nexus;
		ArrayList<Stem> tipList = new ArrayList<Stem>();
		ArrayList<Stem> stemList = new ArrayList<Stem>();
		char[] nexChars = nexus.toCharArray();
		int nextEnd = nexChars.length;
		int at = nexus.indexOf('(');
		Stem branchTop = new Stem();
		stemList.add(branchTop);
		++at;
		int state = STEM_START;
		// set level appropriate for first child of first root
		int level = 1;
		int start = at;
		Stem toFinish = null;

		while (at < nextEnd) {
			switch (nexChars[at]) {
			case '(':
				// descending to a deeper level
				// set branch to parent contents past (
				branchTop = new Stem(level, branchTop);
				stemList.add(branchTop);
				if (state == IN_WIDTH) {
					// set width of last member at old level
					toFinish.addWidth(start, at);
				}
				toFinish = null;
				// set to look for tip name in lower level
				state = STEM_START;
				// increase level for children of new branchTop
				++level;
				maxLevel = Math.max(level, maxLevel);
				// do not have anything to finish
				break;
			case ':':
				// about to get branch length
				if (state == IN_NAME) {
					// add tip
					toFinish = new Stem(level, branchTop, start, at);
					tipList.add(toFinish);
					stemList.add(toFinish);
				}
				// set start to get bounds of branch length
				start = at + 1;
				state = IN_WIDTH;
				break;
			case ',':
				if (state == IN_WIDTH) {
					toFinish.addWidth(start, at);
					toFinish = null;
				}
				// a name might follow a ,
				state = STEM_START;
				break;
			case ')':
				if (state == IN_WIDTH) {
					toFinish.addWidth(start, at);
				}
				// have concluded a level section
				--level;
				// next expected is : as in ():branch_length
				toFinish = branchTop;
				// adjust branchTop for new level
				branchTop = branchTop.above;
				break;
			default:
				if (state == STEM_START) {
					// have the start of a name
					start = at;
					state = IN_NAME;
				}
			}
			++at;
		}
		++maxLevel;

		tipCount = tipList.size();
		tips = new Stem[tipCount];
		Iterator<Stem> it = tipList.iterator();
		for (int i = tipCount - 1; 0 <= i; i--) {
			tips[i] = it.next();
		}
		tipList = null;

		stems = new Stem[stemList.size()];
		stemList.toArray(stems);
		stemList = null;
		Arrays.sort(stems);

		// depths at level 0 are known
		// loop is in increasing level size
		// deeper levels can compute their depth from their parent
		for (int i = 1; i < stems.length; i++) {
			(stems[i]).buildDepth();
		}

		// all depths should now be set

		// get maxDepth and set display position for tips
		double tipMid = 1.0;
		nameSearch = new TipFinder[tips.length];
		for (int i = 0; i < tipCount; i++) {
			Stem nextTip = tips[i];
			nameSearch[i] = new TipFinder(i, (tips[i]).tip);
			maxDepth = Math.max(maxDepth, nextTip.depth);
			if (0.0 < nextTip.width) {
				minWidth = Math.min(minWidth, nextTip.width);
			}
			nextTip.setDrop(tipMid);
			tipMid += 1.0;
		}
		Arrays.sort(nameSearch);

		// compute all display positions by using decreasing level
		// values to propigate bounds to upper levels
		for (int i = stems.length - 1; 0 <= i; i--) {
			(stems[i]).sendSpan();
		}
	}

	public void setTreeType(boolean genomes, boolean flush) {
		genomeTips = genomes;
		flushTips = flush;
	}

	public String getTipStates() {
		String result = "locus";
		if (genomeTips) {
			result = "genomes";
		}
		if (flushTips) {
			result += " flush";
		}
		else {
			result += " scaled";
		}
		return result;
	}

	public int getTipIndex(String name) {
		int at = Arrays.binarySearch(nameSearch, new TipFinder(0, name));
		if (0 <= at) {
			at = (nameSearch[at]).tipAt;
		}
		return at;
	}

	public void setGenomeNames(SequenceData[] full) {
		for (int i = 0; i < full.length; i++) {
			int at = Arrays.binarySearch(nameSearch, new TipFinder(0, (full[i]).getLocusTag()));
			if (0 <= at) {
				(tips[(nameSearch[at]).tipAt]).genome = (full[i]).getTaxonName();
			}
		}
	}

	public Graphics2D paint(BufferedImage image) {
		double maxWide = image.getWidth();
		double xExpand = Double.MAX_VALUE;
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(Color.BLACK);
		FontMetrics fm = g.getFontMetrics();
		int textGap = fm.getHeight() / 4;
		for (int i = 0; i < tipCount; i++) {
			Stem nextTip = tips[i];
			// get paint width available for
			double stemRoom = maxWide - nextTip.getTipRoom(fm) - textGap;
			if (flushTips) {
				stemRoom /= (1 + nextTip.level);
			}
			else {
				stemRoom /= (minWidth + nextTip.depth);
			}
			xExpand = Math.min(stemRoom, xExpand);
		}
		if (xExpand < 0) {
			xExpand = 10.0 / minWidth;
		}
		double yExpand = image.getHeight();
		yExpand /= (tipCount + 2);
		if (yExpand < fm.getHeight()) {
			yExpand = fm.getHeight();
		}
		int textDrop = fm.getHeight() / 2 - fm.getDescent();

		for (int i = 0; i < stems.length; i++) {
			(stems[i]).paint(xExpand, yExpand, textDrop, textGap, g);
		}
		return g;
	}

	public Dimension getPreferredSize(double minStem, Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		double adjust = minStem / minWidth;
		// double maxAcross = 0.0;
		for (int i = 0; i < tipCount; i++) {
			Stem nextTip = tips[i];
			double span = adjust * nextTip.depth;
			if (genomeTips) {
				span += fm.stringWidth(nextTip.genome);
			}
			else {
				span += fm.stringWidth(nextTip.tip);
			}
			span += 0.5 * fm.getHeight();
			maxDepth = Math.max(maxDepth, span);
		}
		int wideSet = (int) (maxDepth + minStem + 1.0);
		return (new Dimension(wideSet, fm.getHeight() * (tipCount + 2)));
	}

	private class Stem implements Comparable<Stem> {
		double width = 0.0;

		double depth = 0.0;

		// link to parent stem
		Stem above = null;

		// locus text for end of branch
		String tip = null;

		// genome text for end of branch
		String genome = null;

		// vertical position for top and bottom branches
		// for child branches
		double top = Double.MAX_VALUE;

		double base = 0.0;

		// ancesstory level
		// 0 corresponds to root
		// otherwise level = 1 + (above.level)
		int level = 0;

		Stem() {
		}

		Stem(int level, Stem above) {
			this.level = level;
			this.above = above;
		}

		Stem(int level, Stem above, int start, int end) {
			this.level = level;
			this.above = above;
			tip = nexus.substring(start, end);
		}

		void addWidth(int start, int end) {
			width = Double.parseDouble(nexus.substring(start, end));
			if (above == null) {
				depth = width;
			}
		}

		void adjustBounds(double value) {
			if (value < top) {
				top = value;
			}
			if (base < value) {
				base = value;
			}
		}

		void buildDepth() {
			if (above != null) {
				this.depth = above.depth + this.width;
			}
		}

		void setDrop(double value) {
			top = value;
			base = value;
			if (above != null) {
				above.adjustBounds(value);
			}
		}

		void sendSpan() {
			if ((tip == null) && (above != null)) {
				above.adjustBounds(0.5 * (top + base));
			}
		}

		int getTipRoom(FontMetrics fm) {
			int result = 0;
			if (tip != null) {
				if (genomeTips) {
					result = fm.stringWidth(genome);
				}
				else {
					result = fm.stringWidth(tip);
				}
			}
			return result;
		}

		void paint(double xExpand, double yExpand, int textDrop, int textGap, Graphics g) {
			int drop = (int) (0.5 + 0.5 * yExpand * (top + base));
			if (flushTips) {
				int leftStem = (int) (0.5 + xExpand * level);
				if (0.0 < depth) {
					String length = "" + width;
					if (distChop < length.length()) {
						length = length.substring(0, distChop);
					}
					g.drawString(length, leftStem + textGap, drop);
				}
				if (tip == null) {
					int rightStem = (int) (0.5 + xExpand * (1 + level));
					g.drawLine(leftStem, drop, rightStem, drop);
					int spanTop = (int) (0.5 + yExpand * top);
					int spanBase = (int) (0.5 + yExpand * base);
					g.drawLine(rightStem, spanTop, rightStem, spanBase);
				}
				else {
					int rightStem = (int) (0.5 + xExpand * maxLevel);
					g.drawLine(leftStem, drop, rightStem, drop);
					textDrop += drop;
					if (genomeTips) {
						g.drawString(genome, rightStem + textGap, textDrop);
					}
					else {
						g.drawString(tip, rightStem + textGap, textDrop);
					}
				}
			}
			else {
				double right = depth + minWidth;
				double left = depth;
				if (0.0 < depth) {
					left = right - width;
				}
				int leftStem = (int) (0.5 + xExpand * left);
				int rightStem = (int) (0.5 + xExpand * right);
				g.drawLine(leftStem, drop, rightStem, drop);
				if (tip == null) {
					int spanTop = (int) (0.5 + yExpand * top);
					int spanBase = (int) (0.5 + yExpand * base);
					g.drawLine(rightStem, spanTop, rightStem, spanBase);
				}
				else {
					textDrop += drop;
					if (genomeTips) {
						g.drawString(genome, rightStem + textGap, textDrop);
					}
					else {
						g.drawString(tip, rightStem + textGap, textDrop);
					}
				}
			}
		}

		public int compareTo(Stem arg0) {
			return (this.level - arg0.level);
		}
	}

	private class TipFinder implements Comparable<TipFinder> {
		String tipText;

		int tipAt;

		TipFinder(int index, String name) {
			tipAt = index;
			tipText = name;
		}

		public int compareTo(TipFinder arg0) {
			return ((this.tipText).compareTo(arg0.tipText));
		}
	}

}
