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
package edu.vt.vbi.patric.dao;

import java.util.*;

import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.SQLQuery;
import edu.vt.vbi.patric.dao.ResultType;

/**
 * @author oral
 * 
 */
public class DBPIG {
	protected static SessionFactory factory;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public static SessionFactory getSessionFactory() {
		return factory;
	}

	public ArrayList<ResultType> getGenomeInteractionTree() {

		String sql = "select distinct  ps.taxon_id_a, ps.taxon_name_a, count(distinct ps.pig_id) "
				+ "	from  pig.pig_summary ps " + "	group by  ps.taxon_id_a, ps.taxon_name_a "
				+ "	order by ps.taxon_name_a asc";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("id", obj[0]);
			row.put("text", (obj[1]));
			row.put("genome", (obj[1]));
			row.put("count", obj[2]);
			row.put("leaf", "true");
			row.put("parentID", "");

			results.add(row);
		}
		return results;
	}

	public String getNodesSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {
			sql += "SELECT count(*) cnt from pig.pig_summary ps where 1=1 ";

			if (key.containsKey("genomeIds")) {
				String genomeIds = (key.get("genomeIds"));
				if (!genomeIds.equals("") && genomeIds != null) {
					sql += " and ps.taxon_id_a in (" + key.get("genomeIds") + ")";
				}
			}

			if (key.containsKey("type")) {
				String types_ = (key.get("type"));
				if (!types_.equals("") && types_ != null) {
					sql += " and ps.type_id in (" + key.get("type") + ")";
				}
			}

			if (key.containsKey("source")) {
				String sources_ = (key.get("source"));
				if (!sources_.equals("") && sources_ != null) {
					sql += " and ps.source_id in (" + key.get("source") + ")";
				}
			}

			if (key.containsKey("method")) {
				String methods_ = (key.get("method"));
				if (!methods_.equals("") && methods_ != null) {
					sql += " and ps.method_id in (" + key.get("method") + ")";
				}
			}
		}
		else if (where.equals("function1")) {

			sql += " SELECT distinct ps.source_mol_id interactor_id, ps.taxon_id_a tax_id, ps.taxon_name_a tax_name, ps.label_a label, ps.patric_source_id locus_tag, ps.na_feature_id na_feature_id "
					+ "	from pig.pig_summary ps where 1=1 ";

			if (key.containsKey("genomeIds")) {
				String genomeIds = (key.get("genomeIds"));
				if (!genomeIds.equals("") && genomeIds != null) {
					sql += " and ps.taxon_id_a in (" + key.get("genomeIds") + ")";
				}
			}

			if (key.containsKey("type")) {
				String types_ = (key.get("type"));
				if (!types_.equals("") && types_ != null) {
					sql += " and ps.type_id in (" + key.get("type") + ")";
				}
			}

			if (key.containsKey("source")) {
				String sources_ = (key.get("source"));
				if (!sources_.equals("") && sources_ != null) {
					sql += " and ps.source_id in (" + key.get("source") + ")";
				}
			}

			if (key.containsKey("method")) {
				String methods_ = (key.get("method"));
				if (!methods_.equals("") && methods_ != null) {
					sql += " and ps.method_id in (" + key.get("method") + ")";
				}
			}
		}
		else if (where.equals("function2")) {

			sql += "SELECT distinct ps.target_mol_id interactor_id, ps.taxon_id_b tax_id, ps.taxon_name_b tax_name, ps.label_b label "
					+ "	from pig.pig_summary ps where 1=1 ";

			if (key.containsKey("genomeIds")) {
				String genomeIds = (key.get("genomeIds"));
				if (!genomeIds.equals("") && genomeIds != null) {
					sql += " and ps.taxon_id_a in (" + key.get("genomeIds") + ")";
				}
			}

			if (key.containsKey("type")) {
				String types_ = (key.get("type"));
				if (!types_.equals("") && types_ != null) {
					sql += " and ps.type_id in (" + key.get("type") + ")";
				}
			}

			if (key.containsKey("source")) {
				String sources_ = (key.get("source"));
				if (!sources_.equals("") && sources_ != null) {
					sql += " and ps.source_id in (" + key.get("source") + ")";
				}
			}

			if (key.containsKey("method")) {
				String methods_ = (key.get("method"));
				if (!methods_.equals("") && methods_ != null) {
					sql += " and ps.method_id in (" + key.get("method") + ")";
				}
			}
		}
		return sql;
	}

	public int getNodesCount(HashMap<String, String> key) {

		String sql = getNodesSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<ResultType> getNodes(HashMap<String, String> key) {

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		// for pathogen nodes
		String sql = getNodesSQL(key, "function1");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		List<?> rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {

			obj = (Object[]) it.next();
			ResultType row = new ResultType();

			row.put("interactor_id", obj[0]);
			row.put("tax_id", obj[1]);
			row.put("name", obj[2]);
			row.put("label", obj[3]);
			row.put("locus_tag", obj[4]);
			row.put("na_feature_id", obj[5]);

			results.add(row);
		}

		// for host nodes
		sql = getNodesSQL(key, "function2");

		session = factory.getCurrentSession();
		session.beginTransaction();
		q = session.createSQLQuery(sql);
		rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {

			obj = (Object[]) it.next();
			ResultType row = new ResultType();

			row.put("interactor_id", obj[0]);
			row.put("tax_id", obj[1]);
			row.put("name", obj[2]);
			row.put("label", obj[3]);
			row.put("locus_tag", obj[3]);
			row.put("na_feature_id", "");

			results.add(row);
		}

		return results;
	}

	public String getInteractionsSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {
			sql += "SELECT count(*) cnt ";
		}
		else if (where.equals("function")) {
			sql += " SELECT ps.pig_id AS pig_id, " + " ps.source_mol_id AS source_mol_id,"
					+ " ps.target_mol_id AS target_mol_id," + " ps.taxon_name_a AS taxon_name_a,"
					+ " ps.taxon_name_b AS taxon_name_b," + " ps.label_a AS label_a," + " ps.label_b AS label_b,"
					+ " ps.description_a AS description_a," + " ps.patric_source_id AS locus_tag,"
					+ " ps.na_feature_id AS na_feature_id," + " ps.method_id," + " ps.method_name,"
					+ " ps.method_source," + " ps.method_source_id," + " ps.source_id," + " ps.source_name,"
					+ " ps.source_dbid," + " ps.type_id," + " ps.type_name," + " ps.type_source,"
					+ " ps.type_source_id," + " ps.reference_id," + " ps.reference_source,"
					+ " ps.reference_source_id," + " ps.interaction_score, " + " ps.taxon_id_a AS ncbi_tax_id_a,"
					+ " ps.taxon_id_b AS ncbi_tax_id_b," + " ps.description_b AS description_b";
		}

		String notin = null;
		if (key.containsKey("notin")) {
			notin = key.get("notin");
		}

		sql += " from pig.pig_summary ps where 1=1 ";

		if (key.containsKey("source")) {

			String source = (key.get("source"));

			if (!source.equals("") && source != null) {
				if (notin != null && notin.equals("true"))
					sql += " and ps.source_id not in (" + key.get("source") + ") ";
				else
					sql += " and ps.source_id in (" + key.get("source") + ") ";
			}

		}

		if (key.containsKey("type")) {
			String type = (key.get("type"));

			if (!type.equals("") && type != null) {
				if (notin != null && notin.equals("true"))
					sql += " and ps.type_id not in (" + key.get("type") + ") ";
				else
					sql += " and ps.type_id in (" + key.get("type") + ") ";
			}
		}

		if (key.containsKey("method")) {
			String method = (key.get("method"));
			if (!method.equals("") && method != null) {
				if (notin != null && notin.equals("true"))
					sql += " and ps.method_id not in (" + key.get("method") + ") ";
				else
					sql += " and ps.method_id in (" + key.get("method") + ") ";
			}
		}

		sql += "	AND ps.taxon_id_b = 9606";

		if (key.containsKey("genomeIds")) {
			String genomeIds = (key.get("genomeIds"));
			if (!genomeIds.equals("") && genomeIds != null) {
				if (notin != null && notin.equals("true") && key.containsKey("orig_genomeIds"))
					sql += " and ps.taxon_id_a not in (" + key.get("orig_genomeIds") + ")";
				sql += " and ps.taxon_id_a in (" + key.get("genomeIds") + ")";
			}
		}
		return sql;
	}

	public int getInteractionsCount(HashMap<String, String> key) {

		String sql = getInteractionsSQL(key, "count");
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public ArrayList<ResultType> getInteractions(HashMap<String, String> key, int start, int end) {

		String sql = getInteractionsSQL(key, "function");
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

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

			row.put("pig_id", obj[0]);
			row.put("source_mol_id", obj[1]);
			row.put("target_mol_id", obj[2]);
			row.put("taxon_name_a", obj[3]);
			row.put("taxon_name_b", obj[4]);
			row.put("label_a", obj[5]);
			row.put("label_b", obj[6]);
			row.put("description_a", obj[7]);
			row.put("locus_tag", obj[8]);
			row.put("na_feature_id", obj[9]);
			row.put("method_id", obj[10]);
			row.put("method_name", obj[11]);
			row.put("method_source", obj[12]);
			row.put("method_source_id", obj[13]);
			row.put("source_id", obj[14]);
			row.put("source_name", obj[15]);
			row.put("source_dbid", obj[16]);
			row.put("type_id", obj[17]);
			row.put("type_name", obj[18]);
			row.put("type_source", obj[19]);
			row.put("type_source_id", obj[20]);
			row.put("reference_id", obj[21]);
			row.put("reference_source", obj[22]);
			row.put("reference_source_id", obj[23]);
			row.put("interaction_score", obj[24]);
			row.put("ncbi_tax_id_a", obj[25]);
			row.put("ncbi_tax_id_b", obj[26]);
			row.put("description_b", obj[27]);

			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}

	public ArrayList<ResultType> getPIGTypes(String taxids) {

		String sql = "";
		if (!taxids.equals("")) {
			sql += "select distinct type_id, type_name from pig.pig_summary where taxon_id_a in (" + taxids
					+ ") order by type_name";
		}
		else {
			sql += "select distinct type_id, type_name from pig.pig_types order by type_name";
		}

		// String sql =
		// "select distinct type_id, type_name from pig.pig_types order by type_name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("id", obj[0]);
			row.put("value", obj[1]);
			row.put("count", 0);
			// row.put("count", obj[2]);

			results.add(row);
		}

		return results;

	}

	public ArrayList<ResultType> getPIGMethods(String taxids) {

		String sql = "";
		if (!taxids.equals("")) {
			sql += "select distinct method_id, method_name from pig.pig_summary where taxon_id_a in (" + taxids
					+ ") order by method_name";
		}
		else {
			sql += "select distinct method_id, method_name from pig.pig_methods order by method_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("id", obj[0]);
			row.put("value", obj[1]);
			row.put("count", 0);
			// row.put("count", obj[2]);

			results.add(row);
		}

		return results;
	}

	public ArrayList<ResultType> getPIGSources(String taxids) {

		String sql = "";
		if (!taxids.equals("")) {
			sql += "select distinct source_id, source_name from pig.pig_summary where taxon_id_a in (" + taxids
					+ ") order by source_name";
		}
		else {
			sql += "select distinct source_id, interaction_source_name from pig.pig_sources order by interaction_source_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("id", obj[0]);
			row.put("value", obj[1]);
			row.put("count", 0);
			results.add(row);
		}
		return results;
	}
}
