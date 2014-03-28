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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class StringHelper {

	public static String implode(String[] array, String delim) {
		String out = "";
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				out += delim;
			}
			out += array[i];
		}
		return out;
	}

	public static String implode(Object[] array, String delim) {
		String out = "";
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				out += delim;
			}
			out += array[i].toString();
		}
		return out;
	}

	public static boolean in_array(List<?> haystack, String needle) {
		for (int i = 0; i < haystack.size(); i++) {
			if (haystack.get(i).toString().equals(needle)) {
				return true;
			}
		}
		return false;
	}

	public static String strip_html_tag(String html) {
		return html.replaceAll("\\<.*?>", "");
	}

	public static String chunk_split(String str, int length, String end) {
		StringBuilder sb = new StringBuilder();
		if (str.length() <= length) {
			sb.append(str);
			sb.append(end);
		}
		else {
			int steps = (int) java.lang.Math.floor(str.length() / length);
			String sub;
			for (int i = 0; i < steps; i++) {
				sub = str.substring(i * length, (i + 1) * length);
				sb.append(sub);
				sb.append(end);
			}
			if (str.length() > steps * length) {
				sub = str.substring(steps * length, str.length());
				sb.append(sub);
				sb.append(end);
			}
		}
		return sb.toString();
	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null which
		 * means there's no more data to read. Each line will appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String parseSolrKeywordOperator(String keyword) {
		// keyword.replaceAll("\b[a-zA-Z0-9_]\b", "XX");
		// String return_keyword = "";
		keyword = keyword.replaceAll("\\s+or\\s+|\\s+Or\\s+|\\s+oR\\s+", " OR ").replaceAll("( )+", " ");
		/*
		 * List<String> matchList = new ArrayList<String>(); Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'"); Matcher regexMatcher =
		 * regex.matcher(keyword); while (regexMatcher.find()) { matchList.add(regexMatcher.group()); } for(int i=0; i< matchList.size(); i++){
		 * System.out.println(matchList.get(i)); if(matchList .get(i).toLowerCase().equals(((String)"AND").toLowerCase())){ return_keyword += " AND ";
		 * }else if(matchList.get(i).toLowerCase().equals (((String)"OR").toLowerCase())){ return_keyword += " OR "; }else
		 * if(matchList.get(i).substring(0,1).equals("(")){ return_keyword += matchList.get(i).trim(); int j = i+1; for(; j< matchList.size(); j++){
		 * System.out.println(matchList.get(j)); if(!matchList.get(j).substring(0,1).equals(")")){ if(matchList.get(j).
		 * toLowerCase().equals(((String)"OR").toLowerCase())) return_keyword += " OR "; else
		 * if(matchList.get(j).toLowerCase().equals(((String)"TO").toLowerCase ())) return_keyword += " TO "; else if(matchList.get(j).toLowerCase().
		 * equals(((String)"AND").toLowerCase())) return_keyword += " AND "; else return_keyword += matchList.get(j).trim(); }else{ return_keyword +=
		 * matchList.get(j).trim(); break; } } i = j; }else{ return_keyword += "\""+matchList.get(i).trim()+"\""; } }
		 * 
		 * return return_keyword;
		 */
		return keyword;

	}
}
