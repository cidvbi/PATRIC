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

import java.util.*;

import org.hibernate.Hibernate;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.SQLQuery;

/**
 * @author oral
 * 
 */
public class DBDisease {
	protected static SessionFactory factory;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public ArrayList<ResultType> getMeshHierarchy(String ncbi_taxon_id, String tree_node) {
		String sql = "select c.disease_name, c.disease_id, c.pathogen, c.taxon_id, c.tree_node, length(tree_node) lvl, "
				+ "	(select decode(count (*), 0, 1,0)  from  diseasedb.pathogen_disease where parent_node = c.tree_node and disease_db='MESH') is_leaf, "
				+ "   c.vfg, c.gad_genes, c.ctd_genes " + " from diseasedb.disease_summary c  where 1=1";

		if (tree_node != null && !tree_node.equals("-1") && !tree_node.equals("") && !tree_node.equals("root"))
			sql += " and (c.tree_node like '" + tree_node + ".%')";
		else
			sql += " and (c.tree_node like 'C01.252.400%' or tree_node like 'C01.252.410%')";

		sql += " and c.disease_db='MESH' and c.taxon_id in  (select gi.ncbi_tax_id from cas.genomeinfo gi "
				+ " where gi.ncbi_tax_id in ( "
				+ " select "
				+ " ncbi_tax_id "
				+ " from "
				+ "     sres.taxon connect "
				+ " by "
				+ "     prior taxon_id = parent_id    start with ncbi_tax_id = ?)"
				+ " ) group by  c.disease_name, c.disease_id, c.pathogen, c.taxon_id, c.tree_node, c.vfg, c.gad_genes, c.ctd_genes  order by lvl, is_leaf, c.disease_name ASC";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("disease_name", obj[0]);
			row.put("disease_id", obj[1]);
			row.put("pathogen", obj[2]);
			row.put("taxon_id", obj[3]);
			row.put("tree_node", obj[4]);
			row.put("lvl", obj[5]);
			row.put("leaf", obj[6]);
			row.put("vfdb", obj[7]);
			row.put("gad", obj[8]);
			row.put("ctd", obj[9]);

			results.add(row);
		}
		return results;
	}

	public SQLQuery bindGraphGADSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("name")) {

			String name = ((String) key.get("name")).toLowerCase();

			if (!name.equals("") && name != null) {

				q.setString("name", name);

			}
		}

		return q;
	}

	public SQLQuery bindGraphCTDSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("name")) {

			String name = (String) key.get("name");

			if (!name.equals("") && name != null) {

				q.setString("name", name);

			}
		}

		return q;
	}

	public SQLQuery bindSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("name")) {

			String name = ((String) key.get("name")).toLowerCase();

			if (!name.equals("") && name != null) {

				q.setString("name", name + "%");

			}
		}

		return q;
	}

	public String getVFDBSQL(HashMap<String, String> key, String where) {
		String sql = "";

		if (where.equals("breadcrumb")) {

			sql = " SELECT count(distinct b.vfg_id || b.gene_name) cnt ";

		}
		else {

			if (where.equals("count")) {
				sql += " SELECT count(*) cnt from (";
			}
			sql += " SELECT distinct b.vfg_id, b.gene_name, b.gene_product, b.vf_id, b.vf_name, b.vf_fullname, b.function,"
					+ " (select count(distinct na_feature_id) from diseasedb.VF_summary c  where c.vfg_id = b.vfg_id "
					+ "AND c.ncbi_tax_id in ( select ncbi_tax_id from sres.taxon  connect  by prior taxon_id = parent_id  start with ncbi_tax_id = :id )"
					+ ") feature_count";

		}

		sql += " from diseasedb.vf_summary b WHERE 1 = 1";

		if (key.containsKey("cId")) {

			String id = key.get("cId");

			if (!id.equals("") && id != null) {

				sql += " AND b.ncbi_tax_id in (select ncbi_tax_id " + "	from sres.taxon " + "	connect by prior taxon_id = parent_id "
						+ "	start with ncbi_tax_id = :id )";

			}
		}

		return sql;
	}

	public String getVFDBFeatureSQL(HashMap<String, String> key, String where) {
		String sql = "";

		if (where.equals("breadcrumb")) {

			sql = " SELECT count(distinct b.na_feature_id) cnt ";

		}
		else {

			if (where.equals("count")) {

				sql = " SELECT count(*) cnt from (";
			}

			sql += " SELECT distinct b.VF_ID, b.VF_NAME, b.vfg_id, b.gene_name, b.na_feature_id, df.genome_info_id, df.genome_name, df.accession, df.source_id as locus_tag, df.product ";

		}

		sql += " from diseasedb.vf_summary b, app.dnafeature df WHERE b.na_feature_id = df.na_feature_id ";

		if (key.containsKey("cId")) {

			String id = key.get("cId");

			if (id != null && !id.equals("")) {

				sql += " AND b.ncbi_tax_id in (select ncbi_tax_id " + "	from sres.taxon " + "	connect by prior taxon_id = parent_id "
						+ "	start with ncbi_tax_id = " + id + " )";

			}
		}

		if (key.containsKey("vfgId")) {
			String vfgId = key.get("vfgId");

			if (vfgId != null && !vfgId.equals("")) {

				sql += " AND b.vfg_id=:vfgId ";

			}
			else {

				sql += " AND vfg_id in (";
				sql += " SELECT distinct b.vfg_id from diseasedb.vf_summary b WHERE " + "	b.ncbi_tax_id in ( "
						+ " select ncbi_tax_id  from sres.taxon  connect  by " + " prior taxon_id = parent_id  start with ncbi_tax_id = "
						+ key.get("cId") + ") ";

				sql += ")";

			}
		}

		return sql;
	}

	public SQLQuery bindVFDBSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("cId")) {

			String cId = (String) key.get("cId");

			if (!cId.equals("") && cId != null) {

				q.setString("id", cId);

			}
		}

		if (key.containsKey("vfgId")) {

			String vfgId = (String) key.get("vfgId");

			if (!vfgId.equals("") && vfgId != null) {

				q.setString("vfgId", vfgId);

			}
		}

		return q;
	}

	public SQLQuery bindVFDBFeatureSQLValues(SQLQuery q, HashMap<?, ?> key) {

		if (key.containsKey("vfgId")) {
			String vfgId = (String) key.get("vfgId");
			if (vfgId != null && !vfgId.equals("")) {
				q.setString("vfgId", vfgId);
			}
		}

		return q;
	}

	public int getVFDBBreadCrumbCount(HashMap<String, String> key) {

		String sql = getVFDBSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindVFDBSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getVFDBFeatureBreadCrumbCount(HashMap<String, String> key) {

		String sql = getVFDBFeatureSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindVFDBFeatureSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getVFDBCount(HashMap<String, String> key) {

		String sql = getVFDBSQL(key, "count");

		sql += " )";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindVFDBSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getVFDBFeatureCount(HashMap<String, String> key) {

		String sql = getVFDBFeatureSQL(key, "count");

		sql += " )";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindVFDBFeatureSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public ArrayList<ResultType> getVFDBList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getVFDBSQL(key, "function");

		sql += " GROUP BY vfg_id, gene_name, gene_product, vf_id, vf_name, vf_fullname, function ";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY b.vfg_id, b.gene_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindVFDBSQLValues(q, key);

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
			row.put("vfg_id", obj[0]);
			row.put("gene_name", obj[1]);
			row.put("gene_product", obj[2]);
			row.put("vf_id", obj[3]);
			row.put("vf_name", obj[4]);
			row.put("vf_fullname", obj[5]);
			row.put("function", obj[6]);
			row.put("feature_count", obj[7]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public ArrayList<ResultType> getVFDBFeatureList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getVFDBFeatureSQL(key, "function");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY na_feature_id";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindVFDBFeatureSQLValues(q, key);

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
			row.put("vf_id", obj[0]);
			row.put("vf_name", obj[1]);
			row.put("vfg_id", obj[2]);
			row.put("gene_name", obj[3]);
			row.put("na_feature_id", obj[4]);
			row.put("genome_info_id", obj[5]);
			row.put("genome_name", obj[6]);
			row.put("accession", obj[7]);
			row.put("locus_tag", obj[8]);
			row.put("product", obj[9]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public ArrayList<ResultType> getVDFBNaFeatureIdList(HashMap<String, String> key) {

		String sql = "SELECT distinct b.na_feature_id from diseasedb.vf_summary b WHERE 1 = 1 ";

		if (key.containsKey("cId")) {

			String cId = key.get("cId");

			if (!cId.equals("") && cId != null) {

				sql += "AND b.ncbi_tax_id in ( select ncbi_tax_id from sres.taxon  connect by "
						+ "	prior taxon_id = parent_id  start with ncbi_tax_id = " + cId + ")";

			}
		}

		if (key.containsKey("vfgId")) {

			String vfgId = key.get("vfgId");

			if (!vfgId.equals("") && vfgId != null) {

				sql += " AND vfg_id in (" + vfgId + ")";

			}
			else {

				sql += " AND vfg_id in (";
				sql += " SELECT distinct b.vfg_id from diseasedb.vf_summary b WHERE " + "	b.ncbi_tax_id in ( "
						+ " select ncbi_tax_id  from sres.taxon  connect  by " + " prior taxon_id = parent_id  start with ncbi_tax_id = "
						+ key.get("cId") + ") ";

				sql += ")";

			}
		}

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		List<?> rset = sqlQuery.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next();
			// HashMap<String,Object> row = new HashMap<String,Object>();
			ResultType row = new ResultType();
			row.put("genes", obj);

			results.add(row);
		}
		return results;

	}

	public String getCTDSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("breadcrumb") || where.equals("graphbreadcrumb")) {

			sql += " SELECT count(distinct cs.gene_sym) cnt ";

		}
		else {

			if (where.equals("count") || where.equals("graphcount")) {

				sql += " SELECT count(*) cnt from (";
			}

			sql += " select distinct cs.gene_sym gene_sym, cs.gene_id gene_id,  cs.gene_disease_rel gene_disease_rel, cs.disease_name disease_name, cs.pubmed_ids pubmed_id, "
					+ " cs.disease_id disease_id, cs.gd_app_name gd_app_name";

		}

		if (where.equals("function") || where.equals("count") || where.equals("breadcrumb")) {

			sql += " from diseasedb.ctd_summary cs, (select distinct disease_name from diseasedb.pathogen_disease d ";

			if (key.containsKey("name")) {

				String name = key.get("name");

				if (!name.equals("") && name != null) {

					sql += " start with lower(disease_name) like lower(:name)" + " connect by prior tree_node=parent_node) c ";

				}
			}

			sql += " where cs.disease_name = c.disease_name";

		}
		else if (where.equals("graphlist") || where.equals("graphcount") || where.equals("graphbreadcrumb")) {

			sql += " from diseasedb.ctd_summary cs ";

			if (key.containsKey("name")) {

				String name = key.get("name");

				if (!name.equals("") && name != null) {

					sql += " where cs.disease_id = :name";

				}
			}

		}

		if (where.equals("count") || where.equals("graphcount")) {

			sql += " )";
		}

		return sql;
	}

	public int getCTDBreadCrumbCount(HashMap<String, String> key) {

		String sql = getCTDSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getCTDCount(HashMap<String, String> key) {

		String sql = getCTDSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getCTDGraphBreadCrumbCount(HashMap<String, String> key) {

		String sql = getCTDSQL(key, "graphbreadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindGraphCTDSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getCTDGraphCount(HashMap<String, String> key) {

		String sql = getCTDSQL(key, "graphcount");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindGraphCTDSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public ArrayList<ResultType> getCTDList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getCTDSQL(key, "function");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY cs.gene_sym, cs.disease_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindSQLValues(q, key);

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
			row.put("gene_sym", obj[0]);
			row.put("gene_id", obj[1]);
			row.put("gene_disease_rel", obj[2]);
			row.put("disease_name", obj[3]);
			row.put("pubmed_id", obj[4]);
			row.put("disease_id", obj[5]);
			row.put("gd_app_name", obj[6]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public ArrayList<ResultType> getCTDGraphList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getCTDSQL(key, "graphlist");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY cs.gene_sym, cs.disease_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindGraphCTDSQLValues(q, key);

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
			row.put("gene_sym", obj[0]);
			row.put("gene_id", obj[1]);
			row.put("gene_disease_rel", obj[2]);
			row.put("disease_name", obj[3]);
			row.put("pubmed_id", obj[4]);
			row.put("disease_id", obj[5]);
			row.put("gd_app_name", obj[6]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	public String getGADSQL(HashMap<String, String> key, String where) {
		String sql = "";

		if (where.equals("breadcrumb") || where.equals("graphbreadcrumb")) {

			sql = " SELECT count(distinct gs.gene_sym) cnt ";

		}
		else {

			if (where.equals("count") || where.equals("graphcount")) {

				sql += " SELECT count(*) cnt from ( ";

			}
			sql += " select distinct gs.gene_sym gene_sym,  gs.gene_id gene_id, gs.association association, gs.mesh_disease_terms mesh_disease_terms, "
					+ " gs.broad_phenotype broad_phenotype, gs.pubmed_id pubmed_id, gs.conclusion conclusion, gs.gd_app_name gd_app_name";

		}

		if (where.equals("function") || where.equals("count") || where.equals("breadcrumb")) {

			sql += " from diseasedb.gad_summary gs, " + " (select distinct disease_name " + " from diseasedb.PATHOGEN_DISEASE d ";

			if (key.containsKey("name")) {

				String name = key.get("name");

				if (!name.equals("") && name != null) {

					sql += " start with lower(disease_name) like lower(:name)" + " connect by prior tree_node=parent_node) c ";

				}
			}

			sql += " where (gs.association is NULL or gs.association='Y') and gs.disease_name = c.disease_name";

		}
		else if (where.equals("graphlist") || where.equals("graphbreadcrumb") || where.equals("graphcount")) {

			sql += " from diseasedb.gad_summary gs ";

			if (key.containsKey("name")) {

				String name = key.get("name");

				if (!name.equals("") && name != null) {

					sql += " where lower(gs.disease_name) like lower(:name)";

				}
			}

			sql += " and (gs.association is NULL or gs.association='Y')";
		}
		if (where.equals("count") || where.equals("graphcount")) {
			sql += ")";
		}

		return sql;
	}

	public int getGADBreadCrumbCount(HashMap<String, String> key) {

		String sql = getGADSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getGADGraphBreadCrumbCount(HashMap<String, String> key) {

		String sql = getGADSQL(key, "graphbreadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindGraphGADSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getGADGraphCount(HashMap<String, String> key) {

		String sql = getGADSQL(key, "graphcount");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindGraphGADSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public int getGADCount(HashMap<String, String> key) {

		String sql = getGADSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindSQLValues(q, key);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public ArrayList<ResultType> getGADGraphList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getGADSQL(key, "graphlist");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY gs.gene_sym";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindGraphGADSQLValues(q, key);

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
			row.put("gene_sym", obj[0]);
			row.put("gene_id", obj[1]);
			row.put("association", obj[2]);
			row.put("mesh_disease_terms", obj[3]);
			row.put("broad_phenotype", obj[4]);
			row.put("pubmed_id", obj[5]);
			row.put("conclusion", obj[6]);
			row.put("gd_app_name", obj[7]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	public ArrayList<ResultType> getGADList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "";

		sql += getGADSQL(key, "function");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {

			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");

		}
		else {

			sql += " ORDER BY gs.gene_sym";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindSQLValues(q, key);

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
			row.put("gene_sym", obj[0]);
			row.put("gene_id", obj[1]);
			row.put("association", obj[2]);
			row.put("mesh_disease_terms", obj[3]);
			row.put("broad_phenotype", obj[4]);
			row.put("pubmed_id", obj[5]);
			row.put("conclusion", obj[6]);
			row.put("gd_app_name", obj[7]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	public ArrayList<ResultType> getMeshTermGraphData(String ncbi_taxon_id) {

		String sql = "";

		sql = "select distinct t.ncbi_tax_id taxon_id, replace(replace(tn.name, '['), ']') organism_name, t.rank organism_rank"
				+ "	from sres.taxon t, sres.taxonname tn" + "	where t.taxon_id = tn.taxon_id" + "	and t.ncbi_tax_id = ? "
				+ "	and tn.name_class = 'scientific name'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("taxon_id", obj[0]);
			row.put("organism_name", obj[1]);
			row.put("organism_rank", obj[2]);
			row.put("parent_id", "");
			row.put("mesh_disease_id", "");
			row.put("mesh_disease_name", "");
			row.put("mesh_tree_node", "");
			row.put("parent_tree_node", "");
			row.put("description", "");

			results.add(row);
		}

		sql = "select distinct t.ncbi_tax_id taxon_id, tn.name organism_name, t.rank organism_rank, tp.ncbi_tax_id parent_id, pd.disease_id mesh_disease_id, pd.disease_name mesh_disease_name, pd.tree_node mesh_tree_node, pd.parent_node parent_tree_node, pd.description description "
				+ "	from sres.taxon t, sres.taxonname tn, sres.taxon tp, diseasedb.disease_summary pd "
				+ "	where t.taxon_id = tn.taxon_id "
				+ "	and t.parent_id = tp.taxon_id "
				+ "	and t.ncbi_tax_id = pd.taxon_id "
				+ "	and t.taxon_id in "
				+ "	(select distinct taxon_id from sres.taxon  connect by prior taxon_id = parent_id start with ncbi_tax_id = ?) "
				+ "	and tn.name_class = 'scientific name' "
				+ "	and (pd.tree_node like 'C01.252.400%' OR pd.tree_node like 'C01.252.410%') "
				+ "	and pd.disease_db = 'MESH'";

		session = factory.getCurrentSession();
		session.beginTransaction();
		q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("taxon_id", obj[0]);
			row.put("organism_name", obj[1]);
			row.put("organism_rank", obj[2]);
			row.put("parent_id", obj[3]);
			row.put("mesh_disease_id", obj[4]);
			row.put("mesh_disease_name", obj[5]);
			row.put("mesh_tree_node", obj[6]);
			row.put("parent_tree_node", obj[7]);
			row.put("description", obj[8]);

			results.add(row);
		}
		return results;

	}

	public ArrayList<ResultType> getCTDGADGraphData(String ncbi_taxon_id) {

		String sql = "";

		sql += "select distinct z.gene_sym gene_sym, z.gene_name gene_name,  z.disease_id disease_id, z.gene_disease_rel evidence, z.pubmed_ids pubmed "
				+ " 	from (select distinct pd.disease_id disease_id, pd.disease_name disease_name "
				+ "	from sres.taxon t, diseasedb.pathogen_disease pd "
				+ "	where t.ncbi_tax_id = pd.taxon_id "
				+ "	and t.taxon_id in  "
				+ "	(select distinct taxon_id from sres.taxon  connect by prior taxon_id = parent_id start with ncbi_tax_id = ?) "
				+ "	) x, diseasedb.ctd_summary z " + "	where x.disease_name = z.disease_name ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("gene_sym", obj[0]);
			row.put("gene_name", obj[1]);
			row.put("disease_id", obj[2]);
			row.put("evidence", obj[3]);
			row.put("pubmed", obj[4]);

			results.add(row);
		}

		sql = "select distinct z.gene_sym gene_sym, z.gene_name gene_name,  x.disease_id disease_id, z.pubmed_id pubmed "
				+ " 	from (select distinct pd.disease_id disease_id, pd.disease_name disease_name "
				+ "	from sres.taxon t, diseasedb.pathogen_disease pd " + "	where t.ncbi_tax_id = pd.taxon_id " + "	and t.taxon_id in  "
				+ "	(select distinct taxon_id from sres.taxon  connect by prior taxon_id = parent_id start with ncbi_tax_id = ?) "
				+ "	) x, diseasedb.gad_summary z " + "	where x.disease_name = z.disease_name";

		session = factory.getCurrentSession();
		session.beginTransaction();
		q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("gene_sym", obj[0]);
			row.put("gene_name", obj[1]);
			row.put("disease_id", obj[2]);
			row.put("evidence", "gad");
			row.put("pubmed", obj[3]);

			results.add(row);
		}

		return results;

	}

	public ArrayList<ResultType> getVFDBGraphData(String ncbi_taxon_id) {

		String sql = "";

		sql += "	select vf.ncbi_tax_id, vf.rank, vf.parent_id, df.genome_name, df.accession, vf.na_feature_id, df.source_id, df.product, vf.vfg_id, vf.vf_id, vf.gene_name "
				+ "	from diseasedb.vf_summary vf, app.dnafeature df "
				+ "	where vf.ncbi_tax_id in ( "
				+ "	select ncbi_tax_id "
				+ "	from sres.taxon "
				+ "	connect by prior taxon_id = parent_id "
				+ "	start with ncbi_tax_id = ? "
				+ "	)and vf.na_feature_id = df.na_feature_id"
				+ "	and vf.algorithm = 'ID Mapping'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, ncbi_taxon_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("rank", obj[1]);
			row.put("parent_id", obj[2]);
			row.put("genome_name", obj[3]);
			row.put("accession", obj[4]);
			row.put("na_feature_id", obj[5]);
			row.put("source_id", obj[6]);
			row.put("product", obj[7]);
			row.put("vfg_id", obj[8]);
			row.put("vf_id", obj[9]);
			row.put("gene_name", obj[10]);

			results.add(row);
		}

		return results;
	}

	public ArrayList<ResultType> getGenusInTaxonomy(String tid) {

		String sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
				+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
				+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name' " + "	) lng, cas.ncbiclassification cls "
				+ "	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and cls.rank = 'genus' "
				+ "		and cls.ncbi_taxon_id in (1386,773,138,234,32008,194,83553,1485,776,943,561,262,209,1637,1763,780,590,620,1279,1301,662,629) "
				+ "	connect by prior parent_id = taxon_id " + "	start with ncbi_tax_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, tid);

		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
		q.addScalar("node_level", Hibernate.INTEGER);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		if (rset.size() == 0) {
			sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
					+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
					+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name' " + "	) lng, cas.ncbiclassification cls "
					+ "	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and cls.rank = 'genus' "
					+ "		and ncbi_tax_id in (1386,773,138,234,32008,194,83553,1485,776,943,561,262,209,1637,1763,780,590,620,1279,1301,662,629) "
					+ "	connect by prior taxon_id = parent_id " + "	start with ncbi_tax_id = ? " + "	order by name";
			session = factory.getCurrentSession();
			session.beginTransaction();
			q = session.createSQLQuery(sql);
			q.setString(0, tid);

			q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
			q.addScalar("node_level", Hibernate.INTEGER);
			q.setCacheable(true);

			rset = q.list();
			session.getTransaction().commit();
		}

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("name", obj[1]);
			row.put("rank", obj[2]);
			results.add(row);
		}
		return results;
	}

}
