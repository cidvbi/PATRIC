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
package edu.vt.vbi.patric.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.SQLQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.dao.ResultType;

@SuppressWarnings("unchecked")
public class DBPathways {
	private final static String[] hexDigits = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E",
			"3F", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53", "54", "55",
			"56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C",
			"6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83",
			"84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A",
			"9B", "9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF", "B0", "B1",
			"B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8",
			"C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
			"E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

	protected final int SQL_TIMEOUT = 5 * 60;

	protected static SessionFactory factory;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public ArrayList<ResultType> getPathwayList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		return getFeaturePathwayList(key, sort, start, end);
	}

	public String getPathwaySQL(HashMap<String, String> key) {

		String sql = "";

		if (key.containsKey("na_feature_id") && !key.get("na_feature_id").toString().equals("")) {
			sql += " AND ps.na_feature_id = :na_feature_id";
		}

		if (key.containsKey("map") && !key.get("map").toString().equals("")) {
			sql += " AND ps.pathway_id = :map ";
		}

		if (key.containsKey("ec_number") && !key.get("ec_number").toString().equals("")) {
			sql += " AND ps.ec_number in ( :ec_number )";
		}

		if (key.containsKey("algorithm") && !key.get("algorithm").toString().equals("")) {
			sql += " AND ps.algorithm = :algorithm";
		}

		if (key.containsKey("pathway_class") && !key.get("pathway_class").toString().equals("")) {
			sql += " AND lower(ps.pathway_class) like lower(:pathway_class)";
		}

		return sql;
	}

	public String getFeaturePathwayCount(HashMap<String, String> key) {

		String sql = "SELECT count(distinct ps.pathway_id) " + "	FROM app.pathwaysummary ps WHERE 1=1 " + getPathwaySQL(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return obj.toString();

	}

	public ArrayList<ResultType> getFeaturePathwayList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "SELECT" + "	distinct(ps.pathway_id), " + "	ps.na_feature_id, " + "	ps.pathway_name, " + "	ps.pathway_class, "
				+ "	ps.algorithm, " + "	ps.ec_number, " + "	ps.occurrence, " + "	ps.ec_name, " + "	ps.ncbi_tax_id," + "	ps.genome_info_id "
				+ "	FROM app.pathwaysummary ps " + "	WHERE 1=1 " + getPathwaySQL(key);

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " ORDER BY ps.pathway_id";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		if (end > 0) {
			q.setMaxResults(end);
		}

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();

			row.put("pathway_id", obj[0]);
			row.put("na_feature_id", obj[1]);
			row.put("pathway_name", obj[2]);

			String[] temp = null;
			temp = obj[3].toString().split("; ");

			if (temp.length == 2)
				row.put("pathway_class", temp[1]);
			else
				row.put("pathway_class", temp[0]);

			if (obj[4].toString().equals("Curation"))
				row.put("algorithm", "Legacy BRC");
			else if (obj[4].toString().equals("RefSeq"))
				row.put("algorithm", "RefSeq");
			else if (obj[4].toString().equals("RAST"))

				row.put("algorithm", "PATRIC");

			row.put("ec_number", obj[5]);
			row.put("occurrence", obj[6]);
			row.put("ec_name", obj[7]);
			row.put("taxon_id", obj[8]);
			row.put("genome_info_id", obj[9]);

			results.add(row);
		}
		return results;

	}

	public JSONArray getListOfPathwayNameList(HashMap<String, String> key) {

		String sql = "SELECT distinct ps.pathway_id pid, ps.pathway_name pname" + "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id " + getPathwaySQL(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("pid", Hibernate.STRING).addScalar("pname", Hibernate.STRING);
		q = bindCompSQLValues(q, key);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;
		JSONArray arr = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("name", "ALL");
		o.put("value", "ALL");
		arr.add(o);

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			o = new JSONObject();
			o.put("value", obj[0].toString());
			o.put("name", obj[1].toString());

			arr.add(o);
		}
		return arr;

	}

	public JSONArray getListOfPathwayParentList(HashMap<String, String> key) {

		String sql = "SELECT distinct(ps.pathway_class) pclass" + "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id " + getPathwaySQL(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("pclass", Hibernate.STRING);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		q.setCacheable(true);
		List<?> rset = q.list();
		session.getTransaction().commit();

		String[] obj = null;
		JSONArray arr = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("name", "ALL");
		o.put("value", "ALL");
		arr.add(o);

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next().toString().split("; ");
			o = new JSONObject();
			o.put("name", obj.length == 2 ? obj[1] : obj[0]);
			o.put("value", obj.length == 2 ? obj[1] : obj[0]);

			arr.add(o);
		}
		return arr;

	}

	public JSONArray getListOfEc_NumberList(HashMap<String, String> key) {

		String sql = "SELECT distinct(ps.ec_number) ecnum" + "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id " + getPathwaySQL(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("ecnum", Hibernate.STRING);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object obj = null;
		JSONArray arr = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("name", "ALL");
		o.put("value", "ALL");
		arr.add(o);

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next().toString();
			o = new JSONObject();
			o.put("name", obj);
			o.put("value", obj);

			arr.add(o);
		}
		return arr;

	}

	public JSONArray getListOfAlgorithmList(HashMap<String, String> key) {

		HashMap<String, String> key_clone = (HashMap<String, String>) key.clone();
		key_clone.remove("algorithm");
		String sql = "SELECT distinct(ps.algorithm) alg" + "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key_clone)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id " + getPathwaySQL(key_clone);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("alg", Hibernate.STRING);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key_clone);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object obj = null;
		JSONArray arr = new JSONArray();
		JSONObject o = new JSONObject();
		o.put("name", "ALL");
		o.put("value", "ALL");
		arr.add(o);
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next().toString();
			o = new JSONObject();

			if (obj.equals("Curation"))
				obj = "Legacy BRC";
			else if (obj.equals("RAST"))
				obj = "PATRIC";

			o.put("name", obj);
			o.put("value", obj);

			arr.add(o);
		}
		return arr;

	}

	public String getPathwayAttributes(String map) {
		Object[] obj = null;

		String sql = "SELECT name, description, class FROM sres.ecpathway where source_id = '" + map + "'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		String description = "", klass = "", name = "";

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {

			obj = (Object[]) iter.next();

			if (obj[0] != null && !obj[0].equals(""))
				name = obj[0].toString();
			else
				name = " ";

			if (obj[1] != null && !obj[1].equals(""))
				description = obj[1].toString();
			else
				description = " ";

			if (obj[2] != null && !obj[2].equals(""))
				if (obj[2].toString().indexOf(";") < 0)
					klass = obj[2].toString();
				else
					klass = obj[2].toString().split(";")[1];
			else
				klass = " ";
		}

		return name + ";" + klass + ";" + description;
	}

	public ArrayList<ResultType> aaSequence2ECAssignments(String na_feature_id, String map) {

		// Validation Started
		int featureId = -1;
		if (na_feature_id != null) {
			try {
				featureId = Integer.parseInt(na_feature_id);
			}
			catch (NumberFormatException ex) {
				//
			}
		}

		if (featureId <= 0) {
			return new ArrayList<ResultType>();
		}
		// End of validating na_feature_id

		String sql = "SELECT distinct ps.ec_number, ps.ec_name, ps.occurrence " + "	FROM app.pathwaysummary ps " + "	WHERE  ps.na_feature_id = '"
				+ na_feature_id + "'" + "	AND  ps.pathway_id = '" + map + "'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ec_number", obj[0]);
			row.put("description", obj[1]);
			row.put("occurrence", obj[2]);
			if (obj[0] != null) {
				results.add(row);
			}
		}
		return results;
	}

	public String getMapSQL(HashMap<String, String> key) {

		String sql = "	FROM app.pathwaysummary ps, sres.ecpathwayenzymeclass d" + "	WHERE d.ec_pathway_id = ps.ec_pathway_id "
				+ "	AND ps.ec_number = d.map_name ";

		if (key.get("cType").equals("feature")) {
			sql = sql + "	AND ps.na_feature_id = " + key.get("cId");
			sql = sql + "	AND ps.genome_info_id =   " + key.get("genome");
		}
		else if (key.get("cType").equals("genome")) {
			sql = sql + "	AND ps.genome_info_id =   " + key.get("cId");
		}

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL"))

					if (algorithm.equals("BRC"))

						sql = sql + "	AND ps.algorithm = 'Curation'";

					else if (algorithm.equals("PATRIC"))
						sql = sql + "	AND ps.algorithm = 'RAST'";
					else if (algorithm.equals("RefSeq"))
						sql = sql + "	AND ps.algorithm = 'RefSeq'";
			}
		}

		if (key.containsKey("map")) {

			String map = key.get("map");

			if (!map.equals("") && map != null) {

				sql = sql + " AND ps.pathway_id = '" + key.get("map") + "'";
			}
		}

		return sql;

	}

	public ArrayList<ResultType> getCoordinates(HashMap<String, String> key) {

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		String sql = "SELECT distinct d.coordinate_x, d.coordinate_y, ps.ec_number, ps.ec_name, ps.algorithm " + getMapSQL(key)
				+ "	AND d.map_type = 'enzyme'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {

			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("x", obj[0]);
			row.put("y", obj[1]);
			row.put("ec", obj[2]);
			row.put("description", obj[3]);
			row.put("algorithm", obj[4]);

			results.add(row);
		}

		return results;

	}

	public ArrayList<ResultType> getMapIdsInMap(String map) {

		String sql = "SELECT d.map_name, d.coordinate_x, d.coordinate_y, d.map_width, d.map_height"
				+ "	FROM sres.ecpathwayenzymeclass d, sres.ecpathway c" + "	WHERE d.map_type = 'path'" + "	AND d.ec_pathway_id = c.ec_pathway_id "
				+ " 	AND c.source_id= '" + map + "'";

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {

			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("source_id", obj[0]);
			row.put("x", obj[1]);
			row.put("y", obj[2]);
			row.put("width", obj[3]);
			row.put("height", obj[4]);

			results.add(row);
		}

		return results;

	}

	public ArrayList<ResultType> getAllCoordinatesInMap(String map) {

		String sql = "SELECT d.coordinate_x, d.coordinate_y, e.ec_number, e.description"
				+ "	FROM sres.ecpathwayenzymeclass d, sres.enzymeclass e, sres.ecpathway c" + "	WHERE d.map_type = 'enzyme'"
				+ "	AND e.enzyme_class_id=d.enzyme_class_id	" + "	AND c.source_id = '" + map + "'" + "	AND c.ec_pathway_id = d.ec_pathway_id";

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {

			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("x", obj[0]);
			row.put("y", obj[1]);
			row.put("ec", obj[2]);
			row.put("description", obj[3]);

			results.add(row);
		}

		return results;

	}

	public ArrayList<ResultType> getGenomeNaFeatureIdList(String cId, String cType, String map, String algorithm, String ec_number, String featureList) {

		String sql = "	select	distinct(ps.na_feature_id) genes, df.source_id locustags"
				+ "	FROM app.pathwaysummary ps, app.dnafeature  df WHERE ps.na_feature_id = df.na_feature_id ";

		if (cType != null && cType.equals("taxon")) {

			HashMap<String, String> key = new HashMap<String, String>();
			key.put("taxonId", cId);
			key.put("algorithm", algorithm);

			sql += " AND ps.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(key.get("taxonId")) + ") ";
			sql += " AND (select count(*) from app.genomesummary gs where gs.genome_info_id = ps.genome_info_id and (gs.complete='Complete'  or gs.complete = 'WGS')) > 0";

		}
		else if (cType != null && cType.equals("genome") && cId != null && cId != "") {

			sql += " AND	ps.genome_info_id in (" + cId + ") ";
		}

		sql += "	AND ps.pathway_id in (" + map + ")" + "	AND ps.algorithm in (" + algorithm + ")";

		if (ec_number != null && !ec_number.equals("") && !ec_number.equals("'ALL'"))
			sql += " AND ps.ec_number in (" + ec_number + ")";

		if (featureList != null && !featureList.equals("")) {
			sql += " AND (ps.na_feature_id in (";

			if (featureList.split(",").length > 500) {

				String[] featureListArray = featureList.split(",");

				for (int i = 0; i < featureListArray.length; i++) {
					if (i % 500 == 0)
						sql = sql.substring(0, sql.length() - 1) + ") OR ps.na_feature_id in (";

					sql += featureListArray[i] + ",";
				}
				sql = sql.substring(0, sql.length() - 1);

			}
			else {
				sql += featureList;
			}

			sql += "))";
		}

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("genes", obj[0]);
			row.put("locustags", obj[1]);

			results.add(row);
		}
		return results;
	}

	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...
	// Comparative Pathway Analysis SQLs start here...

	public String CompleteGenomeSQL(HashMap<?, ?> key) {
		String sql = "	(SELECT genome_info_id from app.genomesummary" + "	WHERE complete in ('Complete', 'WGS') ";

		if (key.containsKey("genomeId") && key.get("genomeId").toString().contains(",")) {
			List<?> lstGId = Arrays.asList(key.get("genomeId").toString().split(","));
			sql += " AND genome_info_id in (";

			if (lstGId.size() > 500) {
				sql += lstGId.get(0) + ",";
				for (int i = 1; i < lstGId.size(); i++) {
					if (i % 500 == 0) {
						sql = sql.substring(0, sql.length() - 1);
						sql += ") or genome_info_id in (" + lstGId.get(i) + ",";
					}
					else {
						sql += lstGId.get(i) + ",";
					}
				}
				sql = sql.substring(0, sql.length() - 1);
			}
			else {
				sql += key.get("genomeId").toString();
			}
			sql += ")";

		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").toString().equalsIgnoreCase("")) {
			sql += " AND genome_info_id = :genomeId ";
		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").toString().equalsIgnoreCase("")) {
			sql += " AND ncbi_tax_id in (select ncbi_tax_id from sres.taxon connect by prior taxon_id = parent_id start with ncbi_tax_id = :taxonId)";
		}

		sql += ") gs";

		return sql;

	}

	public SQLQuery bindCompSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("na_feature_id") && !key.get("na_feature_id").toString().equals("")) {
			q.setString("na_feature_id", key.get("na_feature_id").toString());
		}

		if (key.containsKey("map") && !key.get("map").toString().equals("")) {
			q.setString("map", key.get("map").toString());
		}

		if (key.containsKey("ec_number") && !key.get("ec_number").toString().equals("")) {
			q.setString("ec_number", key.get("ec_number").toString());
		}

		if (key.containsKey("algorithm") && !key.get("algorithm").toString().equals("")) {
			q.setString("algorithm", key.get("algorithm").toString());
		}

		if (key.containsKey("pathway_name") && !key.get("pathway_name").toString().equals("")) {
			q.setString("pathway_name", "%" + key.get("pathway_name").toString() + "%");
		}

		if (key.containsKey("pathway_class") && !key.get("pathway_class").toString().equals("")) {
			q.setString("pathway_class", "%" + key.get("pathway_class").toString() + "%");
		}

		// add keyword search....
		if (key.containsKey("keyword") && !key.get("keyword").toString().equalsIgnoreCase("")) {
			q.setString("keyword", "%" + key.get("keyword") + "%");

		}

		if (key.containsKey("genomeId") && key.get("genomeId").toString().contains(",")) {

		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").toString().equalsIgnoreCase("")) {

			q.setString("genomeId", key.get("genomeId").toString());

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").toString().equalsIgnoreCase("")) {

			q.setString("taxonId", key.get("taxonId").toString());

		}

		return q;
	}

	public String CompSQLConditions(HashMap<?, ?> key) {

		String sql = "";

		if (key.containsKey("map") && !key.get("map").toString().equals("")) {
			sql += " AND ps.pathway_id = :map ";
		}

		if (key.containsKey("ec_number") && !key.get("ec_number").toString().equals("")) {
			sql += " AND ps.ec_number in ( :ec_number )";
		}

		if (key.containsKey("algorithm") && !key.get("algorithm").toString().equals("")) {
			sql += " AND ps.algorithm = :algorithm";
		}

		if (key.containsKey("pathway_name")) {
			sql += " AND lower(ps.pathway_name) like lower(:pathway_name)";
		}

		if (key.containsKey("pathway_class")) {
			sql += " AND lower(ps.pathway_class) like lower(:pathway_class)";
		}

		// add keyword search....
		if (key.containsKey("keyword") && !key.get("keyword").toString().equalsIgnoreCase("")) {
			sql += " AND contains(ps.keyword, :keyword) > 0";
		}

		return sql;
	}

	// BEGIN PATHWAY TAB

	public int getTaxonCountSQL(String cId, String cType) {

		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Object obj = null;
		SQLQuery q = null;

		String sql = " SELECT count(distinct(genome_info_id)) cnt from app.genomesummary " + "  WHERE complete in ('Complete', 'WGS') " + "  AND ";
		if (cType.equals("taxon") || cType.equals("genome")) {

			if (cType.equals("taxon")) {
				sql += " ncbi_tax_id in (select ncbi_tax_id  from sres.taxon connect  by prior taxon_id = parent_id start with ncbi_tax_id = ?)";
			}
			else if (cType.equals("genome")) {
				sql += " genome_info_id = ? ";
			}

			q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
			q.setString(0, cId);
			q.setCacheable(true);
		}
		else if (cType.equals("genomelist")) {

			List<?> lstGId = Arrays.asList(cId.split(","));

			sql += " genome_info_id in (";

			if (lstGId.size() > 500) {
				sql += lstGId.get(0) + ",";
				for (int i = 1; i < lstGId.size(); i++) {
					if (i % 500 == 0) {
						sql = sql.substring(0, sql.length() - 1);
						sql += ") or genome_info_id in (" + lstGId.get(i) + ",";
					}
					else {
						sql += lstGId.get(i) + ",";
					}
				}
				sql = sql.substring(0, sql.length() - 1);
			}
			else {
				sql += cId;
			}
			sql += ") ";
		}
		q.setTimeout(SQL_TIMEOUT);
		obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public String getCompPathwayPathwaySQL(HashMap<String, String> key, String where) {

		String sql = "";
		String count = "(";

		if (where.equals("count")) {

			sql += "SELECT count(distinct(ps.pathway_id || ps.algorithm)) cnt ";

		}
		else if (where.equals("function")) {

			if (key.containsKey("genomeId") && key.get("genomeId").toString().contains(",")) {

				count += Integer.toString(getCompleteGenomeCount(key.get("genomeId")));

			}
			else if (key.containsKey("genomeId") && !key.get("genomeId").toString().equalsIgnoreCase("")) {

				count += "1";

			}
			else if (key.containsKey("taxonId") && !key.get("taxonId").toString().equalsIgnoreCase("")) {

				count += getTaxonCountSQL(key.get("taxonId"), "taxon");

			}
			else {
				count += getTaxonCountSQL("2", "taxon");

			}

			count += ")";

			sql += "select"
					+ "	distinct ps.pathway_id, "
					+ "	ps.pathway_name, "
					+ "	ps.pathway_class, "
					+ "	ps.algorithm, "
					+ "	count(distinct(ps.genome_info_id)) genome_count, "
					+ "	count(distinct(ps.na_feature_id)) gene_count, "
					+ "	count(distinct(ps.ec_number)) ec_count, "
					+ "	trunc((count(distinct(ps.genome_info_id || ps.ec_number))*100/"
					+ count
					+ ")/count(distinct(ps.ec_number)), 2) ec_cons,  "
					+ "	trunc((count(distinct ps.na_feature_id) / (count(distinct(ps.ec_number)) * count(distinct(ps.genome_info_id)))),2) gene_cons ";
		}

		sql += "	FROM app.pathwaysummary ps," + CompleteGenomeSQL(key);
		sql += "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		return sql;
	}

	public ArrayList<ResultType> getCompPathwayPathwayList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getCompPathwayPathwaySQL(key, "function");

		sql += "	GROUP BY pathway_id, pathway_name, pathway_class, algorithm";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else if (!key.containsKey("map")) {

			sql += " ORDER BY ps.pathway_id";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		if (end > 0) {
			q.setMaxResults(end);
		}

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("pathway_id", obj[0]);
			row.put("pathway_name", obj[1]);

			String[] temp = null;

			temp = obj[2].toString().split("; ");

			if (temp.length == 2) {

				row.put("pathway_class", temp[1]);

			}
			else {

				row.put("pathway_class", temp[0]);

			}

			if (obj[3].toString().equals("Curation"))

				row.put("algorithm", "Legacy BRC");

			else if (obj[3].toString().equals("RefSeq"))

				row.put("algorithm", "RefSeq");

			else if (obj[3].toString().equals("RAST"))

				row.put("algorithm", "PATRIC");

			row.put("genome_count", obj[4].toString());
			row.put("gene_count", obj[5].toString());

			row.put("ec_count", obj[6].toString());

			row.put("ec_cons", obj[7].toString());

			row.put("gene_cons", obj[8].toString());

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public int getCompPathwayPathwayCount(HashMap<String, String> key) {

		String sql = "";

		sql = getCompPathwayPathwaySQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	// END PATHWAY TAB
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// BEGIN EC TAB

	public String getCompPathwayECSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {
			sql += "SELECT count(distinct(ps.pathway_id || ps.algorithm || ps.ec_number)) cnt ";
		}
		else if (where.equals("function")) {
			sql += "select" + "	distinct ps.pathway_id, " + "	ps.pathway_name, " + "	ps.pathway_class, " + "	ps.algorithm, " + "	ps.ec_number, "
					+ "	ps.ec_name," + "	count(distinct(ps.genome_info_id)) genome_count, " + "	count(distinct(ps.na_feature_id)) gene_count ";
		}

		sql += "	FROM app.pathwaysummary ps," + CompleteGenomeSQL(key);
		sql += "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		return sql;
	}

	public ArrayList<ResultType> getCompPathwayECList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = "";

		sql += getCompPathwayECSQL(key, "function");

		sql += "	GROUP BY pathway_id, pathway_name, pathway_class, algorithm, ec_number, ec_name";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else if (!key.containsKey("map")) {

			sql += " ORDER BY ps.pathway_id";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		if (end > 0) {
			q.setMaxResults(end);
		}

		ScrollableResults scr = q.scroll();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("pathway_id", obj[0]);
			row.put("pathway_name", obj[1]);

			String[] temp = null;
			temp = obj[2].toString().split("; ");

			if (temp.length == 2) {
				row.put("pathway_class", temp[1]);
			}
			else {
				row.put("pathway_class", temp[0]);
			}

			if (obj[3].toString().equals("Curation"))
				row.put("algorithm", "Legacy BRC");
			else if (obj[3].toString().equals("RefSeq"))
				row.put("algorithm", "RefSeq");
			else if (obj[3].toString().equals("RAST"))
				row.put("algorithm", "PATRIC");

			row.put("ec_number", obj[4]);
			row.put("ec_name", obj[5]);
			row.put("genome_count", obj[6]);
			row.put("gene_count", obj[7]);
			results.add(row);
		}

		session.getTransaction().commit();
		return results;

	}

	public int getCompPathwayECCount(HashMap<String, String> key) {

		String sql = "";

		sql = getCompPathwayECSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	// END EC TAB
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// BEGIN FEATURE TAB

	public String getCompPathwayFeatureSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {

			sql += "SELECT count(distinct(ps.pathway_id || ps.ec_number || ps.algorithm || ps.na_feature_id)) cnt ";
			sql += "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key);
			sql += "	WHERE ps.genome_info_id = gs.genome_info_id ";
			sql += CompSQLConditions(key);

		}
		else if (where.equals("download_from_heatmap_count")) {

			sql += "	SELECT count(distinct(ps.pathway_id || ps.ec_number || ps.algorithm || ps.na_feature_id)) cnt ";
			sql += "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key);
			sql += "	WHERE ps.genome_info_id = gs.genome_info_id ";

		}
		else if (where.equals("function")) {

			sql += "select" + "	distinct ps.genome_info_id, " + "	df.genome_name, " + "	df.accession, " + "	ps.na_feature_id, "
					+ "	df.source_id as locus_tag, " + "	df.gene, " + "	df.product, " + "	ps.pathway_id, " + "	ps.pathway_name, "
					+ "	ps.pathway_class, " + "	ps.algorithm, " + "	ps.ec_number, " + "	ps.ec_name ";

			sql += "	FROM app.dnafeature df, app.pathwaysummary ps, " + CompleteGenomeSQL(key);
			sql += "	WHERE df.na_feature_id = ps.na_feature_id";
			sql += "	AND ps.genome_info_id = gs.genome_info_id ";
			sql += CompSQLConditions(key);

		}
		else if (where.equals("download_from_heatmap_feature")) {

			sql += "select" + "	distinct df.genome_info_id, " + "	df.genome_name, " + "	df.accession, " + "	df.na_feature_id, "
					+ "	df.na_sequence_id, " + "	df.name, " + "	df.source_id as locus_tag, "
					+ "	decode(df.algorithm,'Curation','Legacy BRC','RAST','PATRIC','RefSeq') as algorithm, "
					+ "	decode(df.is_reversed,1,'-','+') as strand, " + "	df.debug_field, " + "	df.start_min, " + "	df.start_max, " + "	df.end_min, "
					+ "	df.end_max, " + "	df.na_length, " + "	df.product, " + "	df.gene, " + "	df.aa_length, " + "	df.is_pseudo, "
					+ "	df.bound_moiety, " + "	df.anticodon," + " 	df.protein_id, " + "	ps.pathway_id, " + "	ps.pathway_name, " + "	ps.ec_number, "
					+ "	ps.ec_name ";

			sql += "	FROM app.dnafeature df, app.pathwaysummary ps, " + CompleteGenomeSQL(key);
			sql += "	WHERE ps.genome_info_id = gs.genome_info_id ";
			sql += "	AND df.na_feature_id = ps.na_feature_id ";
		}
		return sql;
	}

	public ArrayList<ResultType> getCompPathwayFeatureList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";
		HashMap<?, ?> key_clone = null;
		if (key.containsKey("which") && key.get("which").equals("download_from_heatmap_feature")) {

			sql += getCompPathwayFeatureSQL(key, "download_from_heatmap_feature");
			sql += "	AND ps.pathway_id in (" + key.get("map") + ")";
			sql += "	AND ps.algorithm in (" + key.get("algorithm") + ")";

			if (key.get("ec_number") != null && !key.get("ec_number").equals(""))
				sql += " AND ps.ec_number in (" + key.get("ec_number") + ")";
			key_clone = (HashMap<String, String>) key.clone();
			key_clone.remove("ec_number");
			key_clone.remove("algorithm");
			key_clone.remove("map");
			sql += CompSQLConditions(key_clone);

		}
		else {
			sql += getCompPathwayFeatureSQL(key, "function");
		}

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " ORDER BY genome_info_id, source_id ASC";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		if (key_clone != null)
			q = bindCompSQLValues(q, key_clone);
		else
			q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		if (end > 0) {
			q.setMaxResults(end);
		}

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();

			if (key.containsKey("which") && key.get("which").equals("download_from_heatmap_feature")) {

				row.put("genome_info_id", obj[0]);
				row.put("genome_name", obj[1]);
				row.put("accession", obj[2]);
				row.put("na_feature_id", obj[3]);
				row.put("na_sequence_id", obj[4]);
				row.put("name", obj[5]);
				row.put("locus_tag", obj[6]);
				row.put("algorithm", obj[7]);
				row.put("strand", obj[8]);
				row.put("debug_field", obj[9]);
				row.put("start_min", obj[10]);
				row.put("start_max", obj[11]);
				row.put("end_min", obj[12]);
				row.put("end_max", obj[13]);
				row.put("na_length", obj[14]);
				row.put("product", obj[15]);
				row.put("gene", obj[16]);
				row.put("aa_length", obj[17]);
				row.put("is_pseudo", obj[18]);
				row.put("bound_moiety", obj[19]);
				row.put("anticodon", obj[20]);
				row.put("protein_id", obj[21]);
				row.put("pathway_id", obj[22]);
				row.put("pathway_name", obj[23]);
				row.put("ec_number", obj[24]);
				row.put("ec_name", obj[25]);

			}
			else {

				row.put("genome_info_id", obj[0]);
				row.put("genome_name", obj[1]);
				row.put("accession", obj[2]);
				row.put("na_feature_id", obj[3]);
				row.put("locus_tag", obj[4]);
				row.put("gene", obj[5]);
				row.put("product", obj[6]);
				row.put("pathway_id", obj[7]);
				row.put("pathway_name", obj[8]);

				String[] temp = null;
				temp = obj[9].toString().split("; ");

				if (temp.length == 2)
					row.put("pathway_class", temp[1]);
				else
					row.put("pathway_class", temp[0]);

				if (obj[10].toString().equals("Curation"))
					row.put("algorithm", "Legacy BRC");
				else if (obj[10].toString().equals("RefSeq"))
					row.put("algorithm", "RefSeq");
				else if (obj[10].toString().equals("RAST"))
					row.put("algorithm", "PATRIC");
				row.put("ec_number", obj[11]);
				row.put("ec_name", obj[12]);
			}
			results.add(row);
		}

		session.getTransaction().commit();
		return results;

	}

	public int getCompPathwayFeatureCount(HashMap<String, String> key) {

		String sql = "";

		HashMap<String, String> key_clone = null;
		if (key.containsKey("which") && key.get("which").equals("download_from_heatmap_feature")) {

			sql += getCompPathwayFeatureSQL(key, "download_from_heatmap_count");

			sql += "	AND ps.pathway_id in (" + key.get("map") + ")";
			sql += "	AND ps.algorithm in (" + key.get("algorithm") + ")";
			sql += "	AND ps.ec_number in (" + key.get("ec_number") + ")";

			key_clone = (HashMap<String, String>) key.clone();
			key_clone.remove("ec_number");
			key_clone.remove("algorithm");
			key_clone.remove("map");
			sql += CompSQLConditions(key_clone);
		}
		else {
			sql += getCompPathwayFeatureSQL(key, "count");
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);

		if (key_clone != null)
			q = bindCompSQLValues(q, key_clone);
		else
			q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	// END FEATURE TAB
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------

	public ArrayList<ResultType> getCompPathwayFeatureCoordinates(HashMap<String, String> key, int start, int end) {

		String sql = "SELECT distinct d.map_name, d.coordinate_x, d.coordinate_y " + "	FROM sres.ecpathwayenzymeclass d, app.pathwaysummary ps ";

		List<?> lstGId = Arrays.asList(key.get("feature_info_id").toString().split(","));
		sql += "	WHERE ps.na_feature_id in (";

		if (lstGId.size() > 500) {
			sql += lstGId.get(0) + ",";
			for (int i = 1; i < lstGId.size(); i++) {
				if (i % 500 == 0) {
					sql = sql.substring(0, sql.length() - 1);
					sql += ") or ps.na_feature_id in (" + lstGId.get(i) + ",";
				}
				else {
					sql += lstGId.get(i) + ",";
				}
			}
			sql = sql.substring(0, sql.length() - 1);
		}
		else {
			sql += key.get("feature_info_id");
		}
		sql += ") ";

		sql += " AND ps.ec_number = d.map_name" + "	AND d.ec_pathway_id = ps.ec_pathway_id" + "	AND ps.pathway_id = :map ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q.setString("map", key.get("map"));

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("ec_number", obj[0]);
			row.put("x", obj[1]);
			row.put("y", obj[2]);
			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public ArrayList<ResultType> getCompPathwayEcCoordinates(HashMap<String, String> key, int start, int end) {

		String sql = "SELECT distinct d.map_name, d.coordinate_x, d.coordinate_y " + "	FROM sres.ecpathwayenzymeclass d, app.pathwaysummary ps "
				+ "	WHERE ps.ec_number = d.map_name " + "	AND d.ec_pathway_id = ps.ec_pathway_id" + "	AND d.map_name = :ec_number"
				+ "	AND	ps.pathway_id = :map";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("ec_number", key.get("ec_number"));
		q.setString("map", key.get("map"));
		q.setTimeout(SQL_TIMEOUT);
		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("ec_number", obj[0]);
			row.put("x", obj[1]);
			row.put("y", obj[2]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	public ArrayList<ResultType> getCompPathwayCoordinates(HashMap<String, String> key, int start, int end) {

		String sql = "SELECT distinct d.map_name, d.coordinate_x, d.coordinate_y, ps.algorithm, ps.ec_name, "
				+ "	count(distinct(ps.genome_info_id)) genome_count " + "	FROM sres.ecpathwayenzymeclass d, app.pathwaysummary ps, "
				+ CompleteGenomeSQL(key) + "	WHERE ps.genome_info_id = gs.genome_info_id " + "	AND ps.ec_number = d.map_name "
				+ "   AND ps.ec_pathway_id = d.ec_pathway_id " + "	AND d.map_type = 'enzyme' ";

		sql += CompSQLConditions(key);

		sql += "GROUP BY d.map_name, d.coordinate_x, d.coordinate_y, ps.algorithm, ps.ec_name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("ec_number", obj[0]);
			row.put("x", obj[1]);
			row.put("y", obj[2]);
			row.put("algorithm", obj[3]);
			row.put("description", obj[4]);
			row.put("genome_count", obj[5]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	public String getCompPathwayPathwayIdsSQL(HashMap<?, ?> key) {

		String sql = "";

		sql += "SELECT " + "	distinct ps.pathway_id, " + "	ps.pathway_name, " + "	ps.algorithm " + "	FROM app.pathwaysummary ps, "
				+ CompleteGenomeSQL(key) + "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		return sql;
	}

	public ArrayList<ResultType> getCompPathwayPathwayIds(HashMap<String, String> key, int start, int end) {

		String sql = "SELECT " + "	distinct ps.pathway_id, " + "	ps.pathway_name, " + "	ps.algorithm " + "	FROM app.pathwaysummary ps, "
				+ CompleteGenomeSQL(key) + "	WHERE ps.genome_info_id = gs.genome_info_id ";

		sql += CompSQLConditions(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("map_id", obj[0]);
			row.put("map_name", obj[1]);
			row.put("algorithm", obj[2]);
			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public int getDistinctCompPathwayPathwayBreadCrumb(HashMap<String, String> key) {

		String sql = "	SELECT count(distinct(ps.pathway_id)) cnt FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getDistinctCompPathwayECBreadCrumb(HashMap<String, String> key) {

		String sql = "	SELECT count(distinct(ps.ec_number)) cnt FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getDistinctCompPathwayFeatureBreadCrumb(HashMap<String, String> key) {

		String sql = "select count(distinct(ps.na_feature_id)) cnt FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id ";
		sql += CompSQLConditions(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q = bindCompSQLValues(q, key);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();

		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<ResultType> EC2ECProperties(String ec_number, String map) {

		String sql = "SELECT distinct ps.ec_name, ps.occurrence " + "	FROM app.pathwaysummary ps " + "	WHERE  ps.ec_number = ?"
				+ "	AND  ps.pathway_id = ?" + "	GROUP BY ps.ec_name, ps.occurrence";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		// q.setCacheable(true);
		q.setString(0, ec_number);
		q.setString(1, map);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("description", obj[0]);
			row.put("occurrence", obj[1]);
			if (obj[0] != null) {
				results.add(row);
			}
		}
		return results;

	}

	public ArrayList<ResultType> getTaxonGenomeCount(String cId, String cType) {

		String sql = "	SELECT genome_info_id, rast, brc, refseq from app.genomesummary" + "	WHERE complete in ('Complete', 'WGS') ";

		if (cType.equals("genomelist")) {
			List<?> lstGId = Arrays.asList(cId.split(","));
			sql += " AND genome_info_id in (";
			if (lstGId.size() > 500) {

				sql += lstGId.get(0) + ",";

				for (int i = 1; i < lstGId.size(); i++) {

					if (i % 500 == 0) {

						sql = sql.substring(0, sql.length() - 1);

						sql += ") or genome_info_id in (" + lstGId.get(i) + ",";

					}
					else {
						sql += lstGId.get(i) + ",";
					}
				}
				sql = sql.substring(0, sql.length() - 1);
			}
			else {
				sql += cId;
			}
			sql += ") ";
		}
		else if (cType.equals("genome")) {
			sql += " AND genome_info_id = " + cId;
		}
		else if (cType.equals("taxon")) {
			sql += " AND ncbi_tax_id in (select ncbi_tax_id from sres.taxon connect by prior taxon_id = parent_id start with ncbi_tax_id = " + cId
					+ ")";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		int curation = 0, refseq = 0, rast = 0;
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {

			obj = (Object[]) iter.next();

			if (obj[1].toString().equals("1"))
				rast += 1;
			if (obj[2].toString().equals("1"))
				curation += 1;
			if (obj[3].toString().equals("1"))
				refseq += 1;
		}

		ResultType row = new ResultType();
		row.put("algorithm", "RAST");
		row.put("count", rast);
		results.add(row);

		ResultType row1 = new ResultType();
		row1.put("algorithm", "RefSeq");
		row1.put("count", refseq);
		results.add(row1);

		ResultType row2 = new ResultType();
		row2.put("algorithm", "Curation");
		row2.put("count", curation);
		results.add(row2);

		return results;

	}

	public int getCompleteGenomeCount(String genomeId) {
		Object obj = null;

		String sql = "select count(distinct(genome_info_id)) cnt " + "	from app.genomesummary gs" + "	where gs.genome_info_id in (";

		List<?> lstGId = Arrays.asList(genomeId.split(","));

		if (lstGId.size() > 500) {

			sql += lstGId.get(0) + ",";

			for (int i = 1; i < lstGId.size(); i++) {

				if (i % 500 == 0) {
					sql = sql.substring(0, sql.length() - 1);

					sql += ") or gs.genome_info_id in (" + lstGId.get(i) + ",";

				}
				else {

					sql += lstGId.get(i) + ",";

				}

			}
			sql = sql.substring(0, sql.length() - 1);

		}
		else {

			sql += genomeId;

		}

		sql += ") and (gs.complete = 'Complete' or gs.complete = 'WGS')";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setTimeout(SQL_TIMEOUT);
		q.setCacheable(true);
		obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	// HEATMAP

	public ArrayList<ResultType> getHeatMapData(HashMap<String, String> key, int start, int end) {

		String sql = "";

		sql += " SELECT distinct ps.genome_info_id, ps.algorithm, ps.ec_number, ps.ec_name, count(distinct(ps.na_feature_id)) gene_count "
				+ "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key) + "	WHERE ps.genome_info_id = gs.genome_info_id " + CompSQLConditions(key)
				+ " GROUP BY ps.genome_info_id, ps.algorithm, ps.ec_number, ps.ec_name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q = bindCompSQLValues(q, key);

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();

			row.put("genome_info_id", obj[0]);
			row.put("algorithm", obj[1]);
			row.put("ec_number", obj[2]);
			row.put("ec_name", obj[3]);
			row.put("gene_count", hexDigits[Integer.parseInt(obj[4].toString())]);

			results.add(row);

		}

		session.getTransaction().commit();

		return results;

	}

	public ArrayList<ResultType> getGenomeNames(HashMap<String, String> key, int start, int end) {

		String sql = "";

		sql += "	SELECT distinct ps.genome_info_id, ps.genome_name" + "	FROM app.genomesummary ps, " + CompleteGenomeSQL(key)
				+ "	WHERE ps.genome_info_id = gs.genome_info_id ";

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm").toString();

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL")) {

					if (algorithm.equals("BRC") || algorithm.equals("Legacy BRC"))

						sql += "	AND ps.brc = 1";

					else if (algorithm.equals("PATRIC") || algorithm.equals("RAST"))

						sql += "	AND ps.rast = 1";

					else if (algorithm.equals("RefSeq"))

						sql += "	AND ps.refseq = 1";
				}

				key.remove("algorithm");
			}
		}

		sql += "	ORDER BY ps.genome_name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q = bindCompSQLValues(q, key);

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();
			row.put("genome_info_id", obj[0]);
			row.put("genome_name", obj[1]);

			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}

	public String getCompPathwayMapGridSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {
			sql += "SELECT count(distinct ps.ec_number) cnt ";
		}
		else if (where.equals("function")) {

			sql += "select" + "	distinct ps.ec_number, ps.algorithm, ps.occurrence, ps.ec_name,"
					+ "	count(distinct(ps.genome_info_id)) genome_count, " + "	count(distinct(ps.na_feature_id)) gene_count ";

		}

		sql += "	FROM app.pathwaysummary ps, " + CompleteGenomeSQL(key) + "	WHERE ps.genome_info_id = gs.genome_info_id ";

		sql += CompSQLConditions(key);

		return sql;
	}

	public ArrayList<ResultType> getCompPathwayMapGridList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = getCompPathwayMapGridSQL(key, "function");

		sql += " GROUP BY ps.ec_number, ps.algorithm, ps.occurrence, ps.ec_name";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " ORDER BY ec_number";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q = bindCompSQLValues(q, key);

		ScrollableResults scr = q.scroll();
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}
		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			ResultType row = new ResultType();

			row.put("ec_number", obj[0]);
			row.put("algorithm", obj[1]);
			row.put("occurrence", obj[2]);
			row.put("ec_name", obj[3]);
			row.put("genome_count", obj[4]);
			row.put("gene_count", obj[5]);
			results.add(row);

		}

		session.getTransaction().commit();

		return results;

	}

	public int getCompPathwayMapGridCount(HashMap<String, String> key) {

		String sql = getCompPathwayMapGridSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindCompSQLValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	// DLP
	public ArrayList<Integer> getDistECConservation(String cType, String cId) {

		HashMap<String, String> key = new HashMap<String, String>();
		if (cType.equals("genome")) {
			key.put("genomeId", cId);
		}
		else if (cType.equals("taxon")) {
			key.put("taxonId", cId);
		}

		int uniqECCnt = 0;
		if (cType.equals("taxon")) {
			uniqECCnt = getTaxonCountSQL(cId, cType);
		}
		else if (cType.equals("genome")) {
			uniqECCnt = getCompleteGenomeCount(cId);
		}
		else {
			return null;
		}

		String sql = "select bin, count(*) cnt from ( "
				+ "	select  (case when ec_cons < 20 then 5 when ec_cons between 20 and 40 then 4 when ec_cons between 40 and 60 then 3 "
				+ "	when ec_cons between 60 and 80 then 2 when ec_cons > 80 then 1 end) bin " + "	from ( "
				+ "		select trunc((count(distinct(ps.genome_info_id || ps.ec_number))*100/(:EC_COUNT))/count(distinct(ps.ec_number)), 2) ec_cons "
				+ "		from app.pathwaysummary ps, " + CompleteGenomeSQL(key);
		sql += "		where ps.genome_info_id = gs.genome_info_id AND ps.algorithm = 'RAST' "
				+ "		group by pathway_id, pathway_name, pathway_class, algorithm " + "	) " + ") " + "group by bin " + "order by bin";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("bin", Hibernate.INTEGER).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);

		q.setInteger("EC_COUNT", uniqECCnt);
		if (cType.equals("taxon")) {
			q.setString("taxonId", cId);
		}
		else if (cType.equals("genome")) {
			q.setString("genomeId", cId);
		}

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		ArrayList<Integer> results = new ArrayList<Integer>();
		results.addAll(Arrays.asList(new Integer[] { 0, 0, 0, 0, 0 }));

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();

			int j = Integer.parseInt(obj[0].toString());
			results.set(j - 1, Integer.parseInt(obj[1].toString()));
		}

		return results;
	}

}
