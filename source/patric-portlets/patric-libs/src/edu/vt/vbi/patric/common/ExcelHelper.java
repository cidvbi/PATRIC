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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

public class ExcelHelper {

	// 1. Global variables
	// -------------------------------------------------------------------

	// Variables for the headers, field names, and source data
	private ArrayList<String> headers;

	private ArrayList<String> fields;

	private ArrayList<?> source;

	// Map of premade cell styles
	private HashMap<String, CellStyle> styles;

	private HashMap<String, XSSFCellStyle> xstyles;

	// Excel Workbooks
	private Workbook wb;

	private XSSFWorkbook xwb;

	// Option selection Variables
	private int borderOpt;

	private int alternatingOpt;

	private int emptyOpt;

	// Booleans
	// private boolean isJSON;

	// 2. Constructors
	// -----------------------------------------------------------------------

	/**
	 * This constructor is used when you're reading in a file instead of
	 * generating one
	 * 
	 * @param type = type of workbook. "hssf" or "xssf", defaults to XSSF
	 */
	public ExcelHelper(String type) {
		if (type.equalsIgnoreCase("hssf")) {
			wb = new HSSFWorkbook();
			styles = new HashMap<String, CellStyle>();
			styles = createStyles();
		}
		else {
			xwb = new XSSFWorkbook();
			xstyles = new HashMap<String, XSSFCellStyle>();
			xstyles = createXStyles();
		}

		borderOpt = 0;
		alternatingOpt = 0;
		emptyOpt = 0;

		headers = new ArrayList<String>();
		fields = new ArrayList<String>();
		source = new JSONArray();

	}

	/**
	 * Full constructor
	 * 
	 * @param type = type of workbook. "hssf" or "xssf", defaults to HSSF
	 * @param h = list of column headers
	 * @param f = list of field names for data retrieval
	 * @param s = list of JSON objects for data retrieval
	 */
	public ExcelHelper(String type, ArrayList<String> h, ArrayList<String> f, ArrayList<?> s) {
		if (type.equalsIgnoreCase("hssf")) {
			wb = new HSSFWorkbook();
			styles = new HashMap<String, CellStyle>();
			styles = createStyles();
		}
		else {
			xwb = new XSSFWorkbook();
			xstyles = new HashMap<String, XSSFCellStyle>();
			xstyles = createXStyles();
		}

		borderOpt = 0;
		alternatingOpt = 0;
		emptyOpt = 0;

		headers = h;
		fields = f;
		source = s;
	}

	// 3. Primary Methods
	// --------------------------------------------------------------------

	/**
	 * This method builds the spreadsheet based on flagged options.
	 */
	public void buildSpreadsheet() {
		// row counter
		int rowCount = 0;

		// Alternating background helper
		boolean bg = false;

		// Empty Cell flag
		boolean emptyCell = false;

		// This logical structure builds the spreadsheet based on whether it is
		// XSSF or HSSF
		if (xwb == null) {
			Sheet sheet1 = wb.createSheet("Results");

			// Create the header row
			Row headerRow = sheet1.createRow(rowCount);
			rowCount++;

			// create the header cells based off the values of headers array
			Cell headerCell;
			for (int i = 0; i < headers.size(); i++) {
				headerCell = headerRow.createCell(i);
				headerCell.setCellValue(headers.get(i));
				headerCell.setCellStyle(styles.get("header"));
			}

			if (source instanceof JSONArray) {

				// print body
				Iterator<?> itr = source.iterator();
				while (itr.hasNext()) {
					Row row = sheet1.createRow(rowCount);
					rowCount++;
					JSONObject jObj = (JSONObject) itr.next();
					for (int i = 0; i < fields.size(); i++) {
						String _f = fields.get(i);
						Cell cell = row.createCell(i);

						if (jObj.get(_f) != null) {
							cell.setCellValue(jObj.get(_f).toString());
						}
						else {
							cell.setCellValue("");
							emptyCell = true;
						}

						if (alternatingOpt == 1 && !bg) {
							cell.setCellStyle(styles.get("bg2"));
						}
						else {
							cell.setCellStyle(styles.get("bg1"));
						}
						if (emptyOpt == 1 && emptyCell) {
							cell.setCellStyle(styles.get("empty"));
							emptyCell = false;
						}
					}
					bg = !bg;
				}

			}
			else if (source instanceof ArrayList<?>) {

				// print body
				Iterator<?> itr = source.iterator();
				while (itr.hasNext()) {
					Row row = sheet1.createRow(rowCount);
					rowCount++;
					ResultType rObj = (ResultType) itr.next();
					for (int i = 0; i < fields.size(); i++) {
						String _f = fields.get(i);
						Cell cell = row.createCell(i);

						if (rObj.get(_f) != null) {
							cell.setCellValue(rObj.get(_f).toString());
						}
						else {
							cell.setCellValue("");
							emptyCell = true;
						}

						if (alternatingOpt == 1 && !bg) {
							cell.setCellStyle(styles.get("bg2"));
						}
						else {
							cell.setCellStyle(styles.get("bg1"));
						}
						if (emptyOpt == 1 && emptyCell) {
							cell.setCellStyle(styles.get("empty"));
							emptyCell = false;
						}
					}
					bg = !bg;
				}

			}

			setColWidths();
		}
		else {

			XSSFSheet sheet1 = xwb.createSheet("Results");

			// Create the header row
			XSSFRow headerRow = sheet1.createRow(rowCount);
			rowCount++;

			// create the header cells based off the values of titles array
			XSSFCell headerCell;
			for (int i = 0; i < headers.size(); i++) {
				headerCell = headerRow.createCell(i);
				headerCell.setCellValue(headers.get(i));
				headerCell.setCellStyle(xstyles.get("header"));
			}

			if (source instanceof JSONArray) {

				// print body
				Iterator<?> itr = source.iterator();
				bg = true;
				while (itr.hasNext()) {
					XSSFRow row = sheet1.createRow(rowCount);
					rowCount++;
					JSONObject jObj = (JSONObject) itr.next();
					for (int i = 0; i < fields.size(); i++) {
						String _f = fields.get(i);
						XSSFCell cell = row.createCell(i);

						if (jObj.get(_f) != null) {
							cell.setCellValue(jObj.get(_f).toString());
						}
						else {
							cell.setCellValue("");
							emptyCell = true;
						}

						if (alternatingOpt == 1 && !bg) {
							cell.setCellStyle(xstyles.get("bg2"));
						}
						else {
							cell.setCellStyle(xstyles.get("bg1"));
						}
						if (emptyOpt == 1 && emptyCell) {
							cell.setCellStyle(xstyles.get("empty"));
							emptyCell = false;
						}
					}
					bg = !bg;
				}

			}
			else if (source instanceof ArrayList<?>) {

				// print body
				Iterator<?> itr = source.iterator();
				bg = true;
				while (itr.hasNext()) {
					XSSFRow row = sheet1.createRow(rowCount);
					rowCount++;
					ResultType rObj = (ResultType) itr.next();
					for (int i = 0; i < fields.size(); i++) {
						String _f = fields.get(i);
						XSSFCell cell = row.createCell(i);

						if (rObj.get(_f) != null) {
							cell.setCellValue(rObj.get(_f).toString());
						}
						else {
							cell.setCellValue("");
							emptyCell = true;
						}

						if (alternatingOpt == 1 && !bg) {
							cell.setCellStyle(xstyles.get("bg2"));
						}
						else {
							cell.setCellStyle(xstyles.get("bg1"));
						}
						if (emptyOpt == 1 && emptyCell) {
							cell.setCellStyle(xstyles.get("empty"));
							emptyCell = false;
						}
					}
					bg = !bg;
				}

			}
			setColWidths();
		}

	}

	/**
	 * This method will write a simple text file using the header and field
	 * arrays, with an array as the source
	 * 
	 * @return = string text file
	 */
	public String writeToTextFile() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append("\t");
		}
		sb.append("\n");

		if (source instanceof JSONArray) {
			Iterator<?> itr = source.iterator();
			while (itr.hasNext()) {
				JSONObject jObj = (JSONObject) itr.next();
				for (int i = 0; i < fields.size(); i++) {
					String _f = fields.get(i);

					if (jObj.get(_f) != null) {
						sb.append(jObj.get(_f));
						sb.append("\t");
					}
					else {
						sb.append("\t");
					}
				}
				sb.append("\n");
			}
		}
		else if (source instanceof ArrayList<?>) {
			Iterator<?> itr = source.iterator();
			while (itr.hasNext()) {
				ResultType rObj = (ResultType) itr.next();
				for (int i = 0; i < fields.size(); i++) {
					String _f = fields.get(i);

					if (rObj.get(_f) != null) {
						sb.append(rObj.get(_f));
						sb.append("\t");
					}
					else {
						sb.append("\t");
					}
				}
				sb.append("\n");
			}

		}

		return sb.toString();

	}

	/**
	 * This method takes the completed spreadsheet and prints it to the browser
	 * 
	 * @param out = output stream to use
	 */
	public void writeSpreadsheettoBrowser(OutputStream out) {

		try {
			if (wb == null) {
				xwb.write(out);
			}
			else {
				wb.write(out);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Writes the workbook to an Excel file
	 * 
	 * @param fN = the desired filename (NOTE: DO NOT include the file
	 * extension)
	 */
	public void writeSpreadsheet(String fN) {
		String fileName = "";
		if (fN == null || fN.equals("")) {
			fileName = "temp.xls";
		}
		else {
			fileName = fN + ".xls";
		}

		try {
			if (wb == null) {
				fileName += "x";
				FileOutputStream out = new FileOutputStream(fileName);
				xwb.write(out);
				out.close();
			}
			else {
				FileOutputStream out = new FileOutputStream(fileName);
				wb.write(out);
				out.close();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Reads in an Excel file via a filename
	 * 
	 * @param fileName = name of the file to read in
	 * @return true/false for success/failure
	 */
	public boolean readFile(String fileName) {

		InputStream inp;
		try {
			inp = new FileInputStream(fileName);
		}
		catch (FileNotFoundException e) {
			inp = null;
			e.printStackTrace();
		}

		if (fileName.substring(fileName.length() - 1, fileName.length()).equals("x")) {
			try {
				xwb = (XSSFWorkbook) WorkbookFactory.create(inp);
				return true;
			}
			catch (FileNotFoundException e) {
				System.out
						.println("File Not Found Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (InvalidFormatException e) {
				System.out
						.println("Invalid Format Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (IOException e) {
				System.out.println("I/O Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (Exception e) {
				return false;
			}
		}
		else {
			try {
				wb = WorkbookFactory.create(inp);
				return true;
			}
			catch (FileNotFoundException e) {
				System.out
						.println("File Not Found Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (InvalidFormatException e) {
				System.out
						.println("Invalid Format Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (IOException e) {
				System.out.println("I/O Exception thrown. Uncomment in readFile(String fileName) to see stack trace.");
				// e.printStackTrace();
				return false;
			}
			catch (Exception e) {
				return false;
			}
		}
	}

	/**
	 * Returns the text in the cell at (row, col)
	 * 
	 * @param row
	 * @param col
	 * @return text of this cell, or "" otherwise
	 */
	public String getCellText(int row, int col) {

		if (xwb == null) {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = sheet.getRow(row).getCell(col);
			String s = cell.getStringCellValue();
			if (s != null) {
				return s;
			}
			else {
				return "";
			}
		}
		else {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = sheet.getRow(row).getCell(col);
			String s = cell.getStringCellValue();
			if (s != null) {
				return s;
			}
			else {
				return "";
			}
		}

	}

	/**
	 * Changes the text of the cell at (row, col) to newText
	 * 
	 * @param row
	 * @param col
	 * @param newText
	 */
	public void setCellText(int row, int col, String newText) {

		if (xwb == null) {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = sheet.getRow(row).getCell(col);
			cell.setCellValue(newText);

		}
		else {
			Sheet sheet = wb.getSheetAt(0);
			Cell cell = sheet.getRow(row).getCell(col);
			cell.setCellValue(newText);

		}

	}

	// 4. Helper Methods
	// ---------------------------------------------------------------------

	/**
	 * This method creates a map of Cellstyle objects for page building. Note:
	 * this method used for HSSF pages
	 * 
	 * @return hashmap of styles
	 */
	private HashMap<String, CellStyle> createStyles() {

		// create custom colors
		HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();

		// This replaces various shades of grey with custom colors
		palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte) 0, // RGB
																			// red
																			// (0-255)
				(byte) 52, // RGB green
				(byte) 94 // RGB blue
		);
		palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte) 230, (byte) 240, (byte) 248);
		palette.setColorAtIndex(HSSFColor.GREY_50_PERCENT.index, (byte) 255, (byte) 193, (byte) 193);

		// Create header style

		CellStyle style = createBorderStyle();

		Font headerFont = wb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setFont(headerFont);
		styles.put("header", style);

		// Create alternating-color body styles

		Font bodyFont = wb.createFont();
		bodyFont.setFontHeightInPoints((short) 8);

		style = createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(bodyFont);
		style.setWrapText(true);
		styles.put("bg1", style);

		style = createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setFont(bodyFont);
		styles.put("bg2", style);

		// create style for empty cell
		style = createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(bodyFont);
		style.setWrapText(true);
		styles.put("empty", style);

		return styles;
	}

	/**
	 * This method creates a map of Cellstyle objects for page building. Note:
	 * this method used for XSSF pages
	 * 
	 * @return hashmap of styles
	 */
	private HashMap<String, XSSFCellStyle> createXStyles() {

		XSSFCellStyle style = (XSSFCellStyle) createBorderStyle();

		// create style for cells in header row
		Font headerFont = xwb.createFont();
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.getIndex());

		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 52, 94)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setFont(headerFont);
		xstyles.put("header", style);

		// create styles for alternating background color cells
		Font bodyFont = xwb.createFont();
		bodyFont.setFontHeightInPoints((short) 8);

		style = (XSSFCellStyle) createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(230, 240, 248)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(bodyFont);
		style.setWrapText(true);
		xstyles.put("bg2", style);

		style = (XSSFCellStyle) createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setFont(bodyFont);
		xstyles.put("bg1", style);

		style = (XSSFCellStyle) createBorderStyle();
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 193, 193)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(bodyFont);
		style.setWrapText(true);
		xstyles.put("empty", style);

		return xstyles;
	}

	/**
	 * Returns a CellStyle with a thin black boarder around all edges Boarder
	 * Options: 0 = no boarder 1 = all thin black boarder 2 = top+bot thin black
	 * boarder
	 * 
	 * @param wb = workbook used to create style
	 * @return CellStyle
	 */
	private CellStyle createBorderStyle() {
		CellStyle style = null;
		if (wb == null) {
			style = xwb.createCellStyle();
		}
		else {
			style = wb.createCellStyle();
		}
		int opt = borderOpt;

		switch (opt) {
		case 0: // no border
			break;
		case 1: // all thin black border
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			break;
		case 2: // thin, only top+bot
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			break;
		default:
			// nothing
			break;

		}
		return style;
	}

	/**
	 * This method automatically sets the column widths How: Measures the
	 * character length of the text in header cell of a column. Max column
	 * length is either the title length or the title length *4
	 */
	public void setColWidths() {
		if (wb == null) {
			int margin = 4;
			XSSFSheet sheet = xwb.getSheetAt(0);

			XSSFRow row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				sheet.setColumnWidth(i, (decideXColumnWidth(sheet, i) + margin) * 256);
			}

		}
		else {
			int margin = 4;
			Sheet sheet = wb.getSheetAt(0);

			Row row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				sheet.setColumnWidth(i, (decideColumnWidth(sheet, i) + margin) * 256);
			}

		}

	}

	/**
	 * Returns the width the Column should be (XSSF version)
	 * @param sheet - sheet of workbook
	 * @param col - the column to work with
	 * @return length (in characters) of that column
	 */
	private int decideXColumnWidth(XSSFSheet sheet, int col) {
		int titleLength = sheet.getRow(0).getCell(col).getStringCellValue().length();
		int longestString = titleLength;

		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(col);
			int temp = cell.getStringCellValue().length();
			if (temp > titleLength * 2) {
				longestString = temp;
			}
		}

		if (longestString > titleLength * 4) {
			longestString = titleLength * 4;
		}

		return longestString;
	}

	/**
	 * Returns the width the Column should be (HSSF version)
	 * @param sheet - sheet of workbook
	 * @param col - the column to work with
	 * @return length (in characters) of that column
	 */
	private int decideColumnWidth(Sheet sheet, int col) {
		int titleLength = sheet.getRow(0).getCell(col).getStringCellValue().length();
		int longestString = titleLength;

		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			Cell cell = row.getCell(col);
			int temp = cell.getStringCellValue().length();
			if (temp > titleLength * 2) {
				longestString = temp;
			}
		}

		if (longestString > titleLength * 4) {
			longestString = titleLength * 4;
		}

		return longestString;
	}

	// 5. Getters/Setters
	// --------------------------------------------------------------------

	/**
	 * Sets header array
	 * 
	 * @param h = new list of headers
	 */
	public void setHeaders(ArrayList<String> h) {
		headers = h;
	}

	/**
	 * Returns the header array
	 * 
	 * @return header
	 */
	public ArrayList<String> getHeaders() {
		return headers;
	}

	/**
	 * Sets fields array
	 * 
	 * @param f = new list of fields
	 */
	public void setFields(ArrayList<String> f) {
		fields = f;
	}

	/**
	 * Returns the fields array
	 * 
	 * @return fields
	 */
	public ArrayList<String> getFields() {
		return fields;
	}

	/**
	 * Sets predetermined boarder option. Styles must be remade if border option
	 * is changed see createBorderStyle for options
	 * 
	 * @param opt = option to change to
	 */
	public void setBorderOption(int opt) {
		borderOpt = opt;
		if (wb == null) {
			xstyles = createXStyles();
		}
		else {
			styles = createStyles();
		}
	}

	/**
	 * Sets alternating border color option
	 * 
	 * @param opt = option to change to
	 */
	public void setAltOption(int opt) {
		alternatingOpt = opt;
	}

	/**
	 * Sets empty cell highlighting option
	 * 
	 * @param opt = option to change to
	 */
	public void setEmptyOption(int opt) {
		emptyOpt = opt;
	}

}
