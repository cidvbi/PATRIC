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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.dao.DBSearch;
import edu.vt.vbi.patric.dao.ResultType;

//NOTES:
//1. In allowing getter/setter methods to add new headers/fields, it opens up the
//   possibility of mismatching header and fields arrays (ie, one longer than the other)
//2. This class knows whether it's using HSSF or XSSF by which of wb or xwb are null

//OPTIONS INFORMATION
//-Boarder Options:
//  see createBorderStyle for options
//-Alternating row color
//  0 = not alternating
//  1 = alternating
//-Empty cells
//  0 = don't highlight
//  1 = highlight

//TABLE OF CONTENTS:
//1. Global variables
//2. Constructors
//3. Primary Methods
//4. Helper Methods
//5. Getters/setters
//6. Methods for testing
@SuppressWarnings("unchecked")
public class ExpressionDataFileReader {

	// 1. Global variables
	// -------------------------------------------------------------------

	// Excel Workbooks
	private Workbook wb;

	private XSSFWorkbook xwb;

	private XSSFSheet xsheet;

	private Sheet sheet;

	private String finalDataUrlString;

	private String finalSampleUrlString;

	private boolean samplefileThere;

	private String dataFormat;

	private String datafileType;

	private String separator;

	private String orientation;

	private String idType;

	private String samplefileType;

	private int countGeneIDs = 0;

	private int countSamples = 0;

	private JSONArray expression, gene, sample, snapshot_array;
	
	private ArrayList<String> sampleIDs = new ArrayList<String>();
	
	private JSONObject mapping;

	private JSONObject sample_order_list;

	private final String samplePreTag = "S";

	private final int snapshot_size = 30;

	public final static String CONTENT_EXPRESSION = "expression";

	public final static String CONTENT_SAMPLE = "sample";

	public final static String CONTENT_MAPPING = "mapping";

	private String collectionID;

	private final HashMap<String, String> idTypes = new HashMap<String, String>();

	// Constructor
	// -----------------------------------------------------------------------

	/**
	 * This constructor is used when you're reading in a file instead of generating one
	 */
	public ExpressionDataFileReader(JSONObject config) {

		samplefileThere = Boolean.parseBoolean(config.get("sampleFilePresent").toString());
		finalDataUrlString = (String) config.get("dataURL");
		finalSampleUrlString = (String) config.get("sampleURL");

		datafileType = (String) config.get("dataFileType"); // xls, xlsx or txt
		samplefileType = (String) config.get("sampleFileType"); // xls, xlsx or // txt
		dataFormat = (String) config.get("dataFileFormat"); // matrix or list
		orientation = (String) config.get("dataFileOrientation"); // gvs or svg (optional)
		idType = (String) config.get("idMappingType"); // refseq etc (optional)
		collectionID = config.get("collectionID").toString(); // mandatory

		sample = new JSONArray();
		gene = new JSONArray();
		expression = new JSONArray();
		snapshot_array = new JSONArray();
		mapping = new JSONObject();

		idTypes.put("refseq_source_id", "Refseq Locus Tag");
		idTypes.put("source_id", "PATRIC Locus Tag");
	}

	public boolean doRead() throws IOException {

		boolean sample_success = false;
		InputStream inp;
		InputStreamReader stream = null;
		BufferedReader reader = null;

		/*
		 * If sample file is provided
		 */

		if (samplefileThere) {

			inp = getInputStreamReader(finalSampleUrlString);

			if (samplefileType.equals("xls") || samplefileType.equals("xlsx")) {
				try {
					sample_success = readExcelFormat(inp, "sample");
				}
				catch (InvalidFormatException e) {
					e.printStackTrace();
				}
			}
			else if (samplefileType.equals("txt") || samplefileType.equals("csv")) {
				separator = (samplefileType.equals("txt")) ? "\t" : ",";
				stream = new InputStreamReader(inp);
				reader = new BufferedReader(stream);
				sample_success = readTXTSampleFile(reader);
			}
			if (this.sample.size() > 0) {
				countSamples = sample.size();
			}
		}
		else {
			sample_success = true;
		}

		boolean data_success = false;

		if (sample_success) {
			try {
				inp = getInputStreamReader(finalDataUrlString);
			}
			catch (MalformedURLException ex) {
				return false;
			}
			if (datafileType.equals("xls") || datafileType.equals("xlsx")) {
				try {
					data_success = readExcelFormat(inp, "data");
				}
				catch (InvalidFormatException e) {
					e.printStackTrace();
				}
			}
			else if (datafileType.equals("txt") || datafileType.equals("csv")) {
				separator = (datafileType.equals("txt")) ? "\t" : ",";

				stream = new InputStreamReader(inp);
				reader = new BufferedReader(stream);
				data_success = readTXTDataFile(reader);
			}
			if (this.expression.size() > 0) {
				countGeneIDs = gene.size();
			}
			if (this.sample.size() > 0) {
				countSamples = sample.size();
			}
		}

		return sample_success && data_success;
	}

	public InputStream getInputStreamReader(String path) throws MalformedURLException {
		InputStream inp = null;

		System.out.println(path);

		try {
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			inp = connection.getInputStream();
		}
		catch (MalformedURLException mfuex) {
			throw mfuex;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return inp;
	}

	/**
	 * Reads in an Excel file via inputstream
	 * 
	 * @param inp InputStream
	 * @param input - reading whether sample or gene file
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public boolean readExcelFormat(InputStream inp, String input) throws InvalidFormatException, IOException {

		Iterator<Row> rowIter = null;

		if (samplefileThere && input.equals("sample")) {
			// sample file
			if (samplefileType.equals("xlsx")) {
				xwb = (XSSFWorkbook) WorkbookFactory.create(inp);
				xsheet = xwb.getSheetAt(0);
				rowIter = xsheet.rowIterator();
			}
			else if (samplefileType.equals("xls")) {
				wb = WorkbookFactory.create(inp);
				sheet = wb.getSheetAt(0);
				rowIter = sheet.rowIterator();
			}
			return readXLSSampleFile(rowIter);
		}
		else {
			// data file
			if (datafileType.equals("xlsx")) {
				xwb = (XSSFWorkbook) WorkbookFactory.create(inp);
				xsheet = xwb.getSheetAt(0);
				rowIter = xsheet.rowIterator();
			}
			else if (datafileType.equals("xls")) {
				wb = WorkbookFactory.create(inp);
				sheet = wb.getSheetAt(0);
				rowIter = sheet.rowIterator();
			}
			return readXLSDataFile(rowIter);
		}
	}

	/**
	 * Reads in a file via BufferedReader Reads only Data Text File
	 * 
	 * @param in BufferedReader input
	 * @return true/false for success/failure
	 */
	public boolean readTXTDataFile(BufferedReader in) {
		try {
			String strLine = null;
			int rowCount = 0;

			while ((strLine = in.readLine()) != null && strLine != "") {
				String snapshot = "";
				String[] separated = strLine.split(separator);

				if (separated != null && separated[0] != null && separated[0].length() > 0
						&& (separated[0].charAt(0) == '!' || separated[0].charAt(0) == '#')) {
					continue;
				}

				if (dataFormat.equals("list")) {
					JSONObject a = new JSONObject();

					a.put("exp_locus_tag", separated[0].trim());
					if (getGene(separated[0]) == null) {
						gene.add(a);
					}

					/*
					 * If the data file is in list format correctly, First column is gene separated[0], Second column is sample user given id
					 * separated[1], and Third column is expression value separated[2]
					 */
					String pid = AddToSampleJSONArray(separated[1].trim());

					a.put("pid", pid);
					a.put("log_ratio", getFloatValue(separated[2].trim()));

					expression.add(a);
					snapshot = strLine;
				}
				else {
					if (orientation.equals("svg")) {
						if (separated[0] == null || separated[0].trim().equals("")) {
							continue;
						}
						for (int i = 1; i < separated.length; i++) {
							JSONObject a;

							if (rowCount == 0) {
								// process header
								a = new JSONObject();
								a.put("sampleUserGivenId", separated[i].trim());

								// If the sample file is not provided
								if (!samplefileThere) {
									a.put("pid", collectionID + samplePreTag + (i - 1));
									a.put("expname", separated[i].trim());
									sample.add(a);
								}
								sampleIDs.add(a.get("sampleUserGivenId").toString());
								snapshot += "\t" + separated[i].trim();
							}
							else {
								// process data cells
								if (separated[i] != null && !separated[i].trim().equals("")) {
									if (i == 1) {
										a = new JSONObject();
										a.put("exp_locus_tag", separated[0].trim());
										gene.add(a);
										snapshot = separated[0].trim();
									}
									
									JSONObject s = getSample(sampleIDs.get(i-1));
									String myCell = getFloatValue(separated[i].trim());
									
									a = new JSONObject();
									a.put("exp_locus_tag", separated[0].trim());
									a.put("pid", s.get("pid").toString());
									a.put("log_ratio", myCell);
									expression.add(a);
									snapshot += "\t" + myCell;
								}
								else {
									if (i == 1) {
										a = new JSONObject();
										a.put("exp_locus_tag", separated[0].trim());
										gene.add(a);
										snapshot = separated[0].trim() + "\t";
									} else {
										snapshot += "\t";
									}
								}
							}
						}
					}
					else if (orientation.equals("gvs")) {

						// For gvs matrix format, the first line is always genes count is used to track line numbers
						if (rowCount == 0) {
							for (int i = 1; i < separated.length; i++) {
								JSONObject a = new JSONObject();
								a.put("exp_locus_tag", separated[i].trim());
								gene.add(a);
							}
						}
						else {
							for (int i = 1; i < separated.length; i++) {
								JSONObject a = new JSONObject();
								a.put("log_ratio", getFloatValue(separated[i].trim()));

								// If sample file is not provided The first column is always sample names
								JSONObject b;
								String pid = "";

								if (i - 1 == 0) {
									b = new JSONObject();
									pid = collectionID + samplePreTag + rowCount;
									b.put("pid", pid);
									a.put("expname", separated[i]);
									b.put("sampleUserGivenId", separated[i].trim());

									if (!samplefileThere) {
										sample.add(b);
									}
								}

								a.put("pid", pid);
								b = (JSONObject) gene.get(i - 1);
								a.put("exp_locus_tag", b.get("exp_locus_tag"));
								expression.add(a);
								snapshot += "\t" + separated[i].trim();
							}
						}
					}
					
				}
				// System.out.println(snapshot);
				// To provide user a snapshot of uploaded file.
				if (snapshot_array.size() < snapshot_size && snapshot.length() >= 1) {
					JSONObject snapshot_obj = new JSONObject();
					snapshot_obj.put("line", snapshot);
					snapshot_array.add(snapshot_obj);
				}
				rowCount++;
			}
		}
		catch (IOException e) {
			System.out.println("File read Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
			return false;
		}

		return true;
	}

	/*
	 * processData
	 * 
	 * @param Iterator<Row>
	 */
	public boolean readXLSDataFile(Iterator<Row> rowIter) {

		Row myRow;
		Cell myCell;
		Iterator<Cell> cellIter;
		int rowCount = 0;
		int cellCount = 0;
		
		while (rowIter.hasNext()) {
			myRow = rowIter.next();
			cellIter = myRow.cellIterator();
			String snapshot = "";

			if (dataFormat.equals("list")) {
				cellCount = 0;
				JSONObject geneA = new JSONObject();

				while (cellIter.hasNext()) {
					myCell = cellIter.next();

					if (IsCellNotNull(myCell)) {
						String strMyCell = myCell.toString().trim();
						if (cellCount == 0) {
							geneA.put("exp_locus_tag", strMyCell);
							if (getGene(strMyCell) == null)
								gene.add(geneA);
						}
						else if (cellCount == 1) {
							String pid = AddToSampleJSONArray(strMyCell);
							geneA.put("pid", pid);
						}
						else if (cellCount == 2) {
							geneA.put("log_ratio", getFloatValue(strMyCell));
						}

						// add to a snapshot
						if (cellCount == 0) {
							snapshot += strMyCell;
						}
						else if (cellCount == 2) {
							snapshot += "\t" + getFloatValue(strMyCell);
						}
						else {
							snapshot += "\t" + strMyCell;
						}
					}
					cellCount++;
				}
				expression.add(geneA);
			}
			else if (orientation.equals("svg")) {

				if (rowCount == 0) {
					// process header
					while (cellIter.hasNext()) {
						myCell = cellIter.next();
						JSONObject th = new JSONObject();
						if (cellCount > 0) {
							if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								th.put("sampleUserGivenId", String.format("%.0f", myCell.getNumericCellValue()));
							}
							else {
								th.put("sampleUserGivenId", myCell.toString().trim());
							}

							if (!samplefileThere) {
								th.put("pid", collectionID + samplePreTag + cellCount);

								if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									th.put("expname", String.format("%.0f", myCell.getNumericCellValue()));
								}
								else {
									th.put("expname", myCell.toString().trim());
								}
								sample.add(th);
							}
							sampleIDs.add(th.get("sampleUserGivenId").toString());
						}
						// snapshot
						if (IsCellNotNull(myCell)) {
							if (cellCount == 0) {
								snapshot += myCell.toString();
							}
							else {
								snapshot += "\t" + myCell.toString();
							}
						}
						else {
							snapshot += "\t";
						}
						cellCount++;
					}
					//System.out.println(sample.toJSONString());
				}
				else {
					// process data cells
					JSONObject geneID = new JSONObject();
					for (int i = 0; i < cellCount; i++) {
						JSONObject td = new JSONObject();
						myCell = myRow.getCell(i);
						if (IsCellNotNull(myCell)) {
							String strMyCell = myCell.toString().trim();
							if (i == 0) {
								geneID.put("exp_locus_tag", strMyCell);
								gene.add(geneID);
								snapshot += strMyCell;
							}
							else {
								JSONObject s = getSample(sampleIDs.get(i-1));
								//System.out.println("i=" + i + ", " + s.get("sampleUserGivenId"));

								td.put("exp_locus_tag", geneID.get("exp_locus_tag"));
								td.put("pid", s.get("pid"));
								if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									td.put("log_ratio", myCell.getNumericCellValue());
									strMyCell = String.valueOf(myCell.getNumericCellValue());
								}
								else {
									td.put("log_ratio", 0.0d);
									strMyCell = "0.0";
								}
								expression.add(td);
								snapshot += "\t" + strMyCell;
							}
						}
						else {
							if (i == 0) {
								break;
							}
							else {
								// convert to zero
								JSONObject s = getSample(sampleIDs.get(i-1));
								//System.out.println("i=" + i + ", " + s.get("sampleUserGivenId"));
								
								td.put("exp_locus_tag", geneID.get("exp_locus_tag"));
								td.put("pid", s.get("pid"));
								td.put("log_ratio", "0.0");
								expression.add(td);

								snapshot += "\t0.0";
							}
						}
						// System.out.println(td.toJSONString());
					}
					// System.out.println(snapshot);
				}
			}
			/*
			 * else if (orientation.equals("svg")) { int cellNullcounter = 0; while (cellIter.hasNext()) { myCell = cellIter.next(); if (rowCount == 0
			 * && cellCount > fcn) { // process header JSONObject b = new JSONObject(); if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			 * b.put("sampleUserGivenId", String.format("%.0f", myCell.getNumericCellValue())); } else { b.put("sampleUserGivenId",
			 * myCell.toString().trim()); }
			 * 
			 * if (!samplefileThere) { b.put("pid", collectionID + samplePreTag + cellCount);
			 * 
			 * if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) { b.put("expname", String.format("%.0f", myCell.getNumericCellValue())); } else {
			 * b.put("expname", myCell.toString().trim()); } sample.add(b); } samples_temp.add(b); } else if (rowCount > 0) { // process data
			 * JSONObject geneA = new JSONObject();
			 * 
			 * if (cellCount == 0) { if (IsCellNotNull(myCell)) { geneA.put("exp_locus_tag", myCell.toString().trim()); gene.add(geneA); } else {
			 * break; } } else { if (IsCellNotNull(myCell)) { JSONObject a = (JSONObject) gene.get(rowCount - 1); geneA.put("exp_locus_tag",
			 * a.get("exp_locus_tag"));
			 * 
			 * a = (JSONObject) samples_temp.get(cellCount - 1); a = getSample(a.get("sampleUserGivenId").toString().trim());
			 * 
			 * geneA.put("pid", a.get("pid")); geneA.put("log_ratio", getFloatValue(myCell.toString().trim())); expression.add(geneA); } else {
			 * cellNullcounter += 1; if (cellNullcounter == samples_temp.size()) { gene.remove(gene.size() - 1); rowCount--; } } } } // add to
			 * snapshot if (IsCellNotNull(myCell)) { if (cellCount == 0 && rowCount > 0 || cellCount > fcn && rowCount == 0) { if (cellCount > fcn)
			 * snapshot += "\t"; snapshot += myCell.toString(); } else if (cellCount == 0 && rowCount == 0) snapshot += "Gene"; else if (cellCount > 0
			 * && rowCount > 0) snapshot += "\t" + getFloatValue(myCell.toString()); } else { snapshot += "\t"; }
			 * 
			 * cellCount++; } }
			 */
			else if (orientation.equals("gvs")) {
				String sampleId = "";
				cellCount = 0;
				while (cellIter.hasNext()) {
					JSONObject geneA = new JSONObject();
					myCell = cellIter.next();
					if (rowCount == 0) {
						JSONObject a = new JSONObject();
						a.put("exp_locus_tag", myCell.toString().trim());
						gene.add(a);
					}
					else if (rowCount > 0) {
						if (cellCount == 0) {
							String pid = "";
							JSONObject b;
							if (!samplefileThere) {
								b = new JSONObject();
								pid = collectionID + samplePreTag + (rowCount - 1);
								b.put("pid", pid);
								b.put("expname", myCell.toString().trim());
								b.put("sampleUserGivenId", myCell.toString().trim());
								sample.add(b);
							}
							else {
								b = getSample(myCell.toString().trim());
								pid = b.get("pid").toString();
							}
							sampleId = pid;
						}
						else {
							JSONObject a = (JSONObject) gene.get(cellCount - 1);
							geneA.put("exp_locus_tag", a.get("exp_locus_tag"));
							geneA.put("pid", sampleId);
							geneA.put("log_ratio", getFloatValue(myCell.toString().trim()));
							expression.add(geneA);
						}
					}

					snapshot += "\t" + myCell.toString().trim();
					cellCount++;
				}
			}

			if (snapshot_array.size() < snapshot_size && snapshot.length() >= 1) {
				JSONObject snapshot_obj = new JSONObject();
				snapshot_obj.put("line", snapshot);
				snapshot_array.add(snapshot_obj);
			}
			rowCount++;
		}

		return true;
	}

	public boolean IsCellNotNull(Cell cell) {
		return cell != null && (cell.toString().trim().equals("") == false);
	}

	public String getFloatValue(String number) {
		String op;
		String[] n;

		if (number.contains("e")) {
			n = number.split("e");
			op = n[1].substring(0, 1);
			number = n[0];

			if (op.equals("+")) {
				number = String.valueOf(1000 * Double.parseDouble(number));
			}
			else {
				number = String.valueOf(0.0001 * Double.parseDouble(number));
			}
		}
		String strFloatValue = "";
		try {
			double a = Double.parseDouble(number);
			a = Math.round(a * 1000) / (double) 1000;
			strFloatValue = String.valueOf(a);
		}
		catch (NumberFormatException e) {
			strFloatValue = String.valueOf(0.0d);
		}
		return strFloatValue;
	}

	/*
	 * If sample file is provided and it is in xls/xlsx format. processSample
	 * 
	 * @param Iterator<Row>
	 */
	public boolean readXLSSampleFile(Iterator<Row> rowIter) {
		Row myRow;
		Cell myCell;
		Iterator<Cell> cellIter;
		int rowCount = 0;
		int columnCount = 0;
		sample_order_list = new JSONObject();

		while (rowIter.hasNext()) {
			myRow = rowIter.next();
			if (rowCount == 0) {
				// process header line
				cellIter = myRow.cellIterator();
				while (cellIter.hasNext()) {
					myCell = cellIter.next();
					String lcase = "";
					if (IsCellNotNull(myCell) && myCell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
						lcase = myCell.toString().trim().toLowerCase();
					}

					if (lcase.equals("pid") || lcase.equals("comparison id")) {
						sample_order_list.put(columnCount, "pid");
					}
					else if (lcase.equals("accession")) {
						sample_order_list.put(columnCount, "accession");
					}
					else if (lcase.equals("title")) {
						sample_order_list.put(columnCount, "expname");
					}
					else if (lcase.equals("pubmed")) {
						sample_order_list.put(columnCount, "pubmed");
					}
					else if (lcase.equals("organism")) {
						sample_order_list.put(columnCount, "organism");
					}
					else if (lcase.equals("strain")) {
						sample_order_list.put(columnCount, "strain");
					}
					else if (lcase.equals("gene modification") || lcase.equals("mutant")) {
						sample_order_list.put(columnCount, "mutant");
					}
					else if (lcase.equals("experiment condition")) {
						sample_order_list.put(columnCount, "condition");
					}
					else if (lcase.equals("time point") || lcase.equals("timepoint")) {
						sample_order_list.put(columnCount, "timepoint");
					}
					columnCount++;
				}
			}
			else {
				// process data line
				JSONObject a = new JSONObject();
				for (int i = 0; i < columnCount; i++) {
					myCell = myRow.getCell(i);

					if (IsCellNotNull(myCell)) {
						if (sample_order_list.get(i) != null) {
							if (sample_order_list.get(i).equals("comparison id") || sample_order_list.get(i).equals("pid")) {
								if (myCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									a.put("sampleUserGivenId", String.format("%.0f", myCell.getNumericCellValue()));
								}
								else {
									a.put("sampleUserGivenId", myCell.toString().trim());
								}
								a.put("pid", collectionID + samplePreTag + (rowCount - 1));
							}
							else {
								a.put(sample_order_list.get(i), myCell.toString().trim());
							}
						}
					}
					else {
						if (i == 0) {
							break;
						}
						if (sample_order_list.get(i) != null) {
							a.put(sample_order_list.get(i), "");
						}
					}
				}
				if (a.isEmpty() == false) {
					sample.add(a);
				}
			}
			rowCount++;
		}
		// System.out.println("readingXSLSample: " + sample.toJSONString());
		return true;
	}

	/*
	 * If sample file is provided and it is in txt/csv format. processSample
	 * 
	 * @param Iterator<Row>
	 */
	public boolean readTXTSampleFile(BufferedReader in) {

		boolean success = true;
		sample_order_list = new JSONObject();
		try {
			String strLine = "";
			int rowCount = 0;
			int columnCount = 0;
			while ((strLine = in.readLine()) != null && strLine.trim().equals("") == false) {
				String[] separated = strLine.split(separator);
				JSONObject a = new JSONObject();
				if (rowCount == 0) {
					// process header
					for (int i = 0; i < separated.length; i++) {
						String lcase = (separated[i] != null && separated[i].trim().equals("") == false) ? separated[i].trim().toLowerCase() : "";

						if (lcase.equals("pid") || lcase.equals("comparison id")) {
							sample_order_list.put(i, "pid");
						}
						else if (lcase.equals("accession")) {
							sample_order_list.put(i, "accession");
						}
						else if (lcase.equals("title")) {
							sample_order_list.put(i, "expname");
						}
						else if (lcase.equals("pubmed")) {
							sample_order_list.put(i, "pubmed");
						}
						else if (lcase.equals("organism")) {
							sample_order_list.put(i, "organism");
						}
						else if (lcase.equals("strain")) {
							sample_order_list.put(i, "strain");
						}
						else if (lcase.equals("gene modification") || lcase.equals("mutant")) {
							sample_order_list.put(i, "mutant");
						}
						else if (lcase.equals("experiment condition")) {
							sample_order_list.put(i, "condition");
						}
						else if (lcase.equals("time point") || lcase.equals("timepoint")) {
							sample_order_list.put(i, "timepoint");
						}
						columnCount++;
					}
				}
				else if (rowCount > 0) {
					// process data
					// System.out.println("strLine-> "+strLine);
					// for (int i = 0; i < separated.length; i++) {
					for (int i = 0; i < columnCount; i++) {
						if (separated[i] != null && separated[i].trim().equals("") == false) {
							if (sample_order_list.get(i) != null) {
								if (sample_order_list.get(i).equals("pid") || sample_order_list.get(i).equals("comparison id")) {
									a.put("pid", collectionID + samplePreTag + (rowCount - 1));
									a.put("sampleUserGivenId", separated[i]);
								}
								else {
									a.put(sample_order_list.get(i), separated[i].trim());
								}
							}
						}
						else {
							if (i == 0) {
								break;
							}
							if (sample_order_list.get(i) != null) {
								a.put(sample_order_list.get(i), "");
							}
						}
					}
					sample.add(a);
				}
				rowCount++;
			}
		}
		catch (IOException e) {
			System.out.println("File read Exception thrown. Uncomment in readSampleFile(String fileName) to see stack trace.");
			return false;
		}
		System.out.println("readSampleFile: " + sample.toJSONString());
		return success;
	}

	/*
	 * Calls DBSearch.getIDSearchResult function
	 */
	public boolean runIDMappingStatistics() {

		DBSearch db = new DBSearch();
		HashMap<String, String> key = new HashMap<String, String>();

		String idList = ((JSONObject) (gene.get(0))).get("exp_locus_tag").toString();

		for (int i = 1; i < gene.size(); i++) {
			idList += "," + ((JSONObject) (gene.get(i))).get("exp_locus_tag").toString();
		}

		key.put("keyword", idList);
		key.put("to", "PATRIC Locus Tag");
		key.put("from", idTypes.get(idType));

		ArrayList<ResultType> items = db.getTranscriptomicsIDSearchResult(key, 0, -1);

		mapping.put("mapped_ids", items.size());
		mapping.put("unmapped_ids", gene.size() - items.size());

		JSONObject results = new JSONObject();

		for (int i = 0; i < items.size(); i++) {
			ResultType g = items.get(i);

			JSONObject obj = new JSONObject();
			obj.putAll(g);

			results.put(obj.get(idType), obj);
		}

		JSONArray mapped_list = new JSONArray();
		JSONArray unmapped_list = new JSONArray();

		/*
		 * This for loop is to create mapping.json The output is written to idmapping_stat JSONObject mapped_list -> JSONArray of JSONObjects
		 * (exp_locus_tag & patric_locus_tag) unmapped_list -> JSONArray of JSONObjects(exp_locus_tag)
		 */

		for (int j = 0; j < gene.size(); j++) {

			JSONObject b = (JSONObject) gene.get(j);
			JSONObject a = (JSONObject) results.get(b.get("exp_locus_tag"));
			JSONObject c = new JSONObject();

			if (a != null) {
				c.put("exp_locus_tag", a.get(idType));
				c.put("na_feature_id", a.get("na_feature_id"));
				mapped_list.add(c);
			}
			else {
				c.put("exp_locus_tag", b.get("exp_locus_tag"));
				unmapped_list.add(c);
			}
		}

		mapping.put("mapped_list", mapped_list);
		mapping.put("unmapped_list", unmapped_list);

		/*
		 * This for loop is to update expression.json The output is in gene_sample_list pid - exp_locus_tag - refseq_locus_tag (if from == PATRIC
		 * Locus Tag) - log_ratio - na_fature_id
		 */
		JSONArray temp_list = new JSONArray();

		for (int j = 0; j < expression.size(); j++) {

			JSONObject b = (JSONObject) expression.get(j);
			JSONObject a = (JSONObject) results.get(b.get("exp_locus_tag"));

			if (a != null) {
				JSONObject c = new JSONObject();
				c.put("na_feature_id", a.get("na_feature_id"));
				c.put("exp_locus_tag", b.get("exp_locus_tag"));
				if (a.get("refseq_source_id") != null) {
					c.put("refseq_locus_tag", a.get("refseq_source_id"));
				}
				c.put("pid", b.get("pid"));
				c.put("log_ratio", b.get("log_ratio"));
				c.put("z_score", b.get("z_score"));
				temp_list.add(c);
			}
		}
		expression = temp_list;
		return true;
	}

	/*
	 * This function calculated expmean, expstddev and z_score for each gene
	 */
	public void calculateExpStats() {

		HashMap<String, String> sample_values = new HashMap<String, String>();
		StdStats stats = new StdStats();
		JSONArray temp_sample = new JSONArray();
		JSONArray temp_gene_sample_list = new JSONArray();
		JSONObject temp_stat = new JSONObject();

		for (int i = 0; i < expression.size(); i++) {
			JSONObject a = (JSONObject) expression.get(i);
			String temp = "";
			String pid = a.get("pid").toString();
			String log_ratio = a.get("log_ratio").toString();

			if (sample_values.containsKey(pid)) {
				temp = sample_values.get(pid) + "," + log_ratio;
			}
			else {
				temp = log_ratio;
			}

			sample_values.put(pid, temp);
		}

		for (int i = 0; i < sample.size(); i++) {
			JSONObject z = (JSONObject) sample.get(i);
			String pid = z.get("pid").toString();

			String[] a = sample_values.get(pid).toString().split(",");
			double[] b = new double[a.length];

			int count = 0;
			for (int j = 0; j < a.length; j++) {
				b[j] = Double.parseDouble(a[j]);
				if (Math.abs(b[j]) >= 1.0) {
					count++;
				}
			}

			String mean = getFloatValue(Double.toString(stats.mean(b)));
			String stddev = getFloatValue(Double.toString(stats.stddev(b)));

			z.put("expmean", mean);
			z.put("expstddev", stddev);
			z.put("sig_log_ratio", count);
			z.put("sig_z_score", 0);
			z.put("genes", a.length);

			temp_stat.put(pid, z);
		}

		for (int i = 0; i < expression.size(); i++) {
			JSONObject a = (JSONObject) expression.get(i);
			String pid = a.get("pid").toString();
			String log_ratio = a.get("log_ratio").toString();
			JSONObject b = (JSONObject) temp_stat.get(pid);
			String expmean = b.get("expmean").toString();
			String expstddev = b.get("expstddev").toString();

			String z_score = Double.toString((Double.parseDouble(log_ratio) - Double.parseDouble(expmean)) / Double.parseDouble(expstddev));

			a.put("z_score", z_score);

			int count = Integer.parseInt(b.get("sig_z_score").toString());

			if (Double.parseDouble(z_score) >= 2) {
				b.put("sig_z_score", ++count);
			}
			else {
				b.put("sig_z_score", count);
			}

			temp_stat.put(pid, b);
			temp_gene_sample_list.add(a);
		}

		for (int i = 0; i < sample.size(); i++) {
			JSONObject a = (JSONObject) sample.get(i);
			String pid = a.get("pid").toString();
			JSONObject b = (JSONObject) temp_stat.get(pid);

			temp_sample.add(b);
		}

		expression = temp_gene_sample_list;
		sample = temp_sample;
	}

	public void writeData(String type) {
		String temp_url = "/tmp/";
		String id = "", content = "";
		try {

			FileWriter fwrite;
			if (type.equals(CONTENT_SAMPLE)) {
				id = "sample.json";
				content = CONTENT_SAMPLE;
			}
			else if (type.equals(CONTENT_EXPRESSION)) {
				id = "expression.json";
				content = CONTENT_EXPRESSION;
			}
			else if (type.equals(CONTENT_MAPPING)) {
				id = "mapping.json";
				content = CONTENT_MAPPING;
			}

			File file = new File(temp_url + id);

			if (file.createNewFile()) {
				fwrite = new FileWriter(file);
				fwrite.write(this.get(content).toString());
				fwrite.flush();
				fwrite.close();
			}
			else {
				System.err.println("Error creating file");
			}
		}
		catch (Exception e) {
			System.err.println("Error: Unable to Write " + e.getMessage());
		}
	}

	/*
	 * public void writeDataAsList() { try { FileWriter fwrite; File file = new File("/tmp/test_from_patric_list.txt");
	 * 
	 * if (file.createNewFile()) { System.out.println("File is created!"); fwrite = new FileWriter(file); String as = "";
	 * 
	 * for (int i = 0; i < expression.size(); i++) { JSONObject ao = (JSONObject) expression.get(i); as += ao.get("exp_locus_tag").toString() + "\t";
	 * as += getSampleReverse(ao.get("pid").toString()).get("sampleUserGivenId").toString() + "\t"; as += ao.get("log_ratio").toString(); as += "\n";
	 * }
	 * 
	 * fwrite.write(as); fwrite.flush(); fwrite.close(); } else { System.out.print("Error creating file"); } } catch (Exception e) {// Catch exception
	 * if any System.err.println("Error: Unable to Write " + e.getMessage()); } }
	 */
	public JSONObject get(String type) {
		JSONObject temp = new JSONObject();

		if (type.equals(CONTENT_SAMPLE)) {
			temp.put(CONTENT_SAMPLE, sample);
		}
		else if (type.equals(CONTENT_EXPRESSION)) {
			temp.put(CONTENT_EXPRESSION, expression);
		}
		else if (type.equals(CONTENT_MAPPING)) {
			temp.put(CONTENT_MAPPING, mapping);
		}
		else {
			temp.put("snapshot", snapshot_array);
		}

		return temp;
	}

	public JSONObject getSample(String sampleUserGivenId) {

		JSONObject a;
		for (int i = 0; i < sample.size(); i++) {
			a = (JSONObject) sample.get(i);
			if (!sampleUserGivenId.equals("") && a.get("sampleUserGivenId").equals(sampleUserGivenId)) {
				return a;
			}
		}
		return null;
	}

	public JSONObject getSampleReverse(String pid) {

		JSONObject a;
		for (int i = 0; i < sample.size(); i++) {
			a = (JSONObject) sample.get(i);
			if (!pid.equals("") && a.get("pid").equals(pid)) {
				return a;
			}
		}
		return null;
	}

	public JSONObject getGene(String exp_locus_tag) {

		JSONObject a;
		for (int i = 0; i < gene.size(); i++) {
			a = (JSONObject) gene.get(i);
			if (!exp_locus_tag.equals("") && a.get("exp_locus_tag").equals(exp_locus_tag)) {
				return a;
			}
		}
		return null;
	}

	public String AddToSampleJSONArray(String data) {
		JSONObject b = getSample(data);
		String pid;

		if (!samplefileThere && b == null) {
			// Add samples to jSONArray
			b = new JSONObject();
			pid = collectionID + samplePreTag + sample.size();
			b.put("pid", pid);
			b.put("expname", data);
			b.put("sampleUserGivenId", data);
			sample.add(b);
		}
		else {
			// Get sample object from samples JSONArray
			pid = b.get("pid").toString();
		}
		return pid;
	}

	public int getCountGeneIDs() {
		return countGeneIDs;
	}

	public int getCountSamples() {
		return countSamples;
	}
}
