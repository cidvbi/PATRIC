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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Oral Dalay
 * @author Harry Yoo
 * 
 */
@SuppressWarnings("unchecked")
public class DBTranscriptomics {
	protected static SessionFactory factory;

	protected final int SQL_TIMEOUT = 5 * 60;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public JSONArray getSamples(String sampleId, String expId) {
		long start = System.currentTimeMillis();
		String sql = "select g.pid, g.expname, g.timepoint, g.strain, g.mutant, g.condition from app.genexp_sample g where 1 = 1";

		if (expId != null && expId != "") {
			sql += " and g.eid in (:expId)";
		}

		if (sampleId != null && sampleId != "") {
			sql += " and g.pid in (:sampleId)";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q = bindTranscriptomicsValues(q, expId, sampleId);

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		JSONArray results = new JSONArray();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			JSONObject row = new JSONObject();
			row.put("pid", obj[0].toString());
			row.put("expname", obj[1].toString());

			if (obj[2] != null) {
				row.put("timepoint", obj[2].toString());
			}
			else {
				row.put("timepoint", "");
			}
			if (obj[3] != null) {
				row.put("strain", obj[3].toString());
			}
			else {
				row.put("strain", "");
			}
			if (obj[4] != null) {
				row.put("mutant", obj[4].toString());
			}
			else {
				row.put("mutant", "");
			}
			if (obj[5] != null) {
				row.put("condition", obj[5].toString());
			}
			else {
				row.put("condition", "");
			}
			results.add(row);
		}
		long end = System.currentTimeMillis();
		System.out.println("Total query and Processing time for 1st query is - " + (end - start));
		return results;
	}

	public JSONArray getGenes(String sampleId, String expId) {

		long start = System.currentTimeMillis();

		String sql = "select g.pid, g.locustag, g.log_ratio, g.z_score, " + "p.patric_na_feature_id "
				+ " from app.genexp_gene g, app.genexp_genemapping p " + " where 1 = 1 ";

		if (expId != null && expId != "") {
			sql += " and g.eid in (:expId)";
		}

		if (sampleId != null && sampleId != "") {
			sql += " and g.pid in (:sampleId)";
		}

		sql += " and g.locustag = p.exp_locus_tag";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);
		q = bindTranscriptomicsValues(q, expId, sampleId);

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		JSONArray results = new JSONArray();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {

			obj = (Object[]) it.next();
			JSONObject row = new JSONObject();

			if (obj[4] != null) {

				row.put("pid", obj[0].toString());
				row.put("exp_locus_tag", obj[1].toString());
				if (obj[2] != null)
					row.put("log_ratio", obj[2].toString());
				else
					row.put("log_ratio", "");

				if (obj[3] != null) {
					row.put("z_score", obj[3].toString());
				}
				else {
					row.put("z_score", "");
				}
				row.put("na_feature_id", obj[4].toString());

				results.add(row);
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("Total query and Processing time for 2nd query is - " + (end - start));
		return results;
	}

	private SQLQuery bindTranscriptomicsValues(SQLQuery q, String expId, String sampleId) {

		if (expId != null && expId != "") {
			q.setParameterList("expId", expId.split(","));
		}
		if (sampleId != null && sampleId != "") {
			q.setParameterList("sampleId", sampleId.split(","));
		}

		return q;
	}

	// / from here, temporary SQLs
	public ArrayList<ResultType> getGeneLvlExpression(HashMap<String, String> key) {

		boolean hasParam = false;
		int pidCnt = 0;
		int pidLoopCnt = 0;
		int pidChunk = 900;

		String sql = "select * from ( "
				+ "	select g.eid, g.accession, g.platform, g.samples, g.pid, g.locustag, g.avg_intensity, g.log_ratio, g.z_score, "
				+ "		s.expname, s.channels, s.timepoint, s.organism, s.strain, s.mutant, s.condition, s.pmid,"
				+ "		pm.patric_na_feature_id, pm.patric_locus_tag, pm.figfam_id "
				+ "	from app.genexp_gene g, app.genexp_sample s, app.genexp_genemapping pm " + "	where "
				+ "		g.locustag = pm.exp_locus_tag and g.pid = s.pid ";

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			sql += " and pm.patric_na_feature_id = :na_feature_id ";
			hasParam = true;
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			pidCnt = key.get("pid").split(",").length;
			pidLoopCnt = (int) Math.ceil((double) pidCnt / pidChunk);

			if (pidLoopCnt == 1) {
				sql += " and g.pid in (:pid) ";
			}
			else {
				sql += " and ( ";
				for (int i = 1; i <= pidLoopCnt; i++) {
					if (i > 1) {
						sql += " OR ";
					}
					sql += " g.pid in (:pid" + i + " ) ";
				}
				sql += " ) ";
			}
			hasParam = true;
		}
		if (key.containsKey("log_ratio") && !key.get("log_ratio").equals("")) {
			sql += "	and (log_ratio <= :logratio_lowerbound or log_ratio >= :logratio_upperbound) ";
			hasParam = true;
		}
		if (key.containsKey("zscore") && !key.get("zscore").equals("")) {
			sql += "	and (z_score <= :zscore_lowerbound or z_score >= :zscore_upperbound) ";
			hasParam = true;
		}

		sql += ") ";

		if (hasParam == false) {
			return null;
		}
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			q.setInteger("na_feature_id", Integer.parseInt(key.get("na_feature_id")));
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			if (pidLoopCnt == 1) {
				q.setParameterList("pid", key.get("pid").split(","));
			}
			else {
				String[] pids = key.get("pid").split(",");
				for (int i = 1; i <= pidLoopCnt; i++) {
					if (i == pidLoopCnt) {
						q.setParameterList("pid" + i, Arrays.copyOfRange(pids, (i - 1) * pidChunk, pidCnt));
					}
					else {
						q.setParameterList("pid" + i, Arrays.copyOfRange(pids, (i - 1) * pidChunk, i * pidChunk));
					}
				}
			}
		}
		if (key.containsKey("log_ratio")) {
			float threshold = Float.parseFloat(key.get("log_ratio"));
			q.setFloat("logratio_lowerbound", (-1) * threshold);
			q.setFloat("logratio_upperbound", threshold);
		}
		if (key.containsKey("zscore")) {
			float threshold = Float.parseFloat(key.get("zscore"));
			q.setFloat("zscore_lowerbound", (-1) * threshold);
			q.setFloat("zscore_upperbound", threshold);
		}

		q.addScalar("eid", Hibernate.INTEGER).addScalar("accession", Hibernate.STRING);
		q.addScalar("platform", Hibernate.STRING).addScalar("samples", Hibernate.STRING);
		q.addScalar("pid", Hibernate.INTEGER).addScalar("locustag", Hibernate.STRING);
		q.addScalar("avg_intensity", Hibernate.FLOAT).addScalar("log_ratio", Hibernate.FLOAT);
		q.addScalar("z_score", Hibernate.FLOAT);
		q.addScalar("expname", Hibernate.STRING).addScalar("channels", Hibernate.INTEGER);
		q.addScalar("timepoint", Hibernate.STRING);
		q.addScalar("organism", Hibernate.STRING).addScalar("strain", Hibernate.STRING);
		q.addScalar("mutant", Hibernate.STRING);
		q.addScalar("condition", Hibernate.STRING).addScalar("pmid", Hibernate.STRING);
		q.addScalar("patric_na_feature_id", Hibernate.INTEGER).addScalar("patric_locus_tag", Hibernate.STRING);
		q.addScalar("figfam_id", Hibernate.STRING);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {

			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("exp_id", obj[0]);
			row.put("exp_accession", obj[1]);
			row.put("exp_platform", obj[2]);
			row.put("exp_samples", obj[3]);
			row.put("pid", obj[4]);
			row.put("exp_locustag", obj[5]);
			row.put("exp_pavg", obj[6]);
			row.put("exp_pratio", obj[7]);
			row.put("exp_zscore", obj[8]);
			row.put("exp_name", obj[9]);
			row.put("exp_channels", obj[10]);
			row.put("exp_timepoint", obj[11]);
			row.put("exp_organism", obj[12]);
			row.put("exp_strain", obj[13]);
			row.put("exp_mutant", obj[14]);
			row.put("exp_condition", obj[15]);
			row.put("pmid", obj[16]);
			row.put("na_feature_id", obj[17]);
			row.put("locus_tag", obj[18]);
			row.put("figfam_id", obj[19]);

			results.add(row);
		}
		return results;
	}

	public ArrayList<ResultType> getGeneLvlExpressionCounts(String field, HashMap<String, String> key) {
		boolean hasParam = false;
		String sql = "select rownum, A.* from ( " + "	select nvl(" + field + ", 'N/A') name, count(*) cnt from ( "
				+ "		select distinct pm.patric_na_feature_id, g.pid, " + field + "		from " + "			app.genexp_gene g, " + "			app.genexp_sample s, "
				+ "			app.genexp_genemapping pm, " + "			app.dnafeature nf " + "		where " + "			g.locustag = pm.exp_locus_tag "
				+ "			and g.pid = s.pid " + "			and pm.patric_na_feature_id = nf.na_feature_id ";

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			sql += " 		and pm.patric_na_feature_id = :na_feature_id ";
			hasParam = true;
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			sql += " 		and g.pid in (:pid) ";
			hasParam = true;
		}
		if (key.containsKey("keyword") && !key.get("keyword").equals("")) {
			sql += "		and lower(s.expname) like :keyword";
			hasParam = true;
		}
		if (key.containsKey("log_ratio") && !key.get("log_ratio").equals("")) {
			sql += "		and (log_ratio <= :logratio_lowerbound or log_ratio >= :logratio_upperbound) ";
			hasParam = true;
		}
		if (key.containsKey("zscore") && !key.get("zscore").equals("")) {
			sql += "		and (z_score <= :zscore_lowerbound or z_score >= :zscore_upperbound) ";
			hasParam = true;
		}
		// meta data fields
		if (key.containsKey("strain") && !key.get("strain").equals("")) {
			sql += "		and strain = :strain ";
			hasParam = true;
		}
		if (key.containsKey("mutant") && !key.get("mutant").equals("")) {
			sql += "		and mutant = :mutant ";
			hasParam = true;
		}
		if (key.containsKey("condition") && !key.get("condition").equals("")) {
			sql += "		and condition = :condition ";
			hasParam = true;
		}
		sql += "	) ";
		sql += "	group by " + field;
		sql += "	order by cnt desc ) A ";

		if (hasParam == false) {
			return null;
		}
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			q.setInteger("na_feature_id", Integer.parseInt(key.get("na_feature_id")));
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			q.setParameterList("pid", key.get("pid").split(","));
		}
		if (key.containsKey("keyword")) {
			q.setString("keyword", "%" + key.get("keyword").toLowerCase() + "%");
		}
		if (key.containsKey("log_ratio")) {
			float threshold = Float.parseFloat(key.get("log_ratio"));
			q.setFloat("logratio_lowerbound", (-1) * threshold);
			q.setFloat("logratio_upperbound", threshold);
		}
		if (key.containsKey("zscore")) {
			float threshold = Float.parseFloat(key.get("zscore"));
			q.setFloat("zscore_lowerbound", (-1) * threshold);
			q.setFloat("zscore_upperbound", threshold);
		}
		// meta data fields
		if (key.containsKey("strain")) {
			q.setString("strain", key.get("strain"));
		}
		if (key.containsKey("mutant")) {
			q.setString("mutant", key.get("mutant"));
		}
		if (key.containsKey("condition")) {
			q.setString("condition", key.get("condition"));
		}

		q.addScalar("rownum", Hibernate.INTEGER);
		q.addScalar("name", Hibernate.STRING);
		q.addScalar("cnt", Hibernate.INTEGER).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("rownum", obj[0]);
			row.put("category", obj[1]);
			row.put("count", obj[2]);
			results.add(row);
		}
		return results;
	}

	public ArrayList<ResultType> getGeneLvlExpressionHistogram(String field, HashMap<String, String> key) {
		boolean hasParam = false;
		String sql = "select rangee, count(*) cnt from ( " + "	select distinct pm.patric_na_feature_id, g.pid, (case " + "			when " + field
				+ " < -2 then 1 " + "			when " + field + " between -2.0 and -1.5 then 2 " + "			when " + field + " between -1.5 and -1 then 3 "
				+ "			when " + field + " between -1.0 and -0.5 then 4 " + "			when " + field + " between -0.5 and 0.0 then 5 " + "			when " + field
				+ " between 0.0 and 0.5 then 6 " + "			when " + field + " between 0.5 and 1.0 then 7 " + "			when " + field
				+ " between 1.0 and 1.5 then 8 " + "			when " + field + " between 1.5 and 2.0 then 9 " + "			when " + field + " > 2 then 10 "
				+ "		end) rangee " + "	from " + "		app.genexp_gene g, " + "		app.genexp_sample s, " + "		app.genexp_genemapping pm " + "	where "
				+ "		g.locustag = pm.exp_locus_tag " + "		and g.pid = s.pid ";

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			sql += "	and pm.patric_na_feature_id = :na_feature_id ";
			hasParam = true;
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			sql += "	and g.pid in (:pid) ";
			hasParam = true;
		}
		if (key.containsKey("keyword") && !key.get("keyword").equals("")) {
			sql += "	and lower(s.expname) like :keyword";
			hasParam = true;
		}
		if (key.containsKey("log_ratio") && !key.get("log_ratio").equals("")) {
			sql += "	and (log_ratio <= :logratio_lowerbound or log_ratio >= :logratio_upperbound) ";
			hasParam = true;
		}
		if (key.containsKey("zscore") && !key.get("zscore").equals("")) {
			sql += "	and (z_score <= :zscore_lowerbound or z_score >= :zscore_upperbound) ";
			hasParam = true;
		}
		if (key.containsKey("accession") && !key.get("accession").equals("")) {
			sql += "	and g.accession = :accession";
			hasParam = true;
		}
		sql += "	and " + field + " is not null " + "	) group by rangee " + "	order by rangee";

		if (hasParam == false) {
			return null;
		}
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		if (key.containsKey("na_feature_id")) {
			q.setInteger("na_feature_id", Integer.parseInt(key.get("na_feature_id")));
		}
		if (key.containsKey("pid") && key.get("pid") != null) {
			q.setParameterList("pid", key.get("pid").split(","));
		}
		if (key.containsKey("keyword")) {
			q.setString("keyword", "%" + key.get("keyword").toLowerCase() + "%");
		}
		if (key.containsKey("log_ratio")) {
			float threshold = Float.parseFloat(key.get("log_ratio"));
			q.setFloat("logratio_lowerbound", (-1) * threshold);
			q.setFloat("logratio_upperbound", threshold);
		}
		if (key.containsKey("zscore")) {
			float threshold = Float.parseFloat(key.get("zscore"));
			q.setFloat("zscore_lowerbound", (-1) * threshold);
			q.setFloat("zscore_upperbound", threshold);
		}
		if (key.containsKey("accession")) {
			q.setString("accession", key.get("accession"));
		}
		q.addScalar("rangee", Hibernate.STRING).addScalar("cnt", Hibernate.INTEGER).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		HashMap<String, String> range = new HashMap<String, String>();
		range.put("1", "<-2");
		range.put("2", "-2~-1.5");
		range.put("3", "-1.5~-1");
		range.put("4", "-1~-.5");
		range.put("5", "-.5~0");
		range.put("6", "0~.5");
		range.put("7", ".5~1");
		range.put("8", "1~1.5");
		range.put("9", "1.5~2");
		range.put("10", "2<");

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("rownum", obj[0]);
			row.put("category", range.get(obj[0].toString()));
			row.put("count", obj[1]);
			results.add(row);
		}

		return results;
	}

	public ArrayList<ResultType> getCorrelatedGenes(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = "	select co.locustag1, co.locustag2 as refseq_locus_tag, co.correlation, co.cnt, "
				+ "			df.genome_info_id, df.genome_name, df.accession, df.source_id locus_tag, df.na_feature_id, "
				+ "			df.start_max, df.end_min, df.na_length, df.is_reversed, df.product, "
				+ "			decode(df.algorithm,'Curation','Legacy BRC','RAST','PATRIC','RefSeq') as annotation, "
				+ "			df.name as feature_type, df.gene, df.protein_id, df.aa_length " + "		from (select " + "				mp1.refseq_locus_tag locustag1, "
				+ "				mp2.refseq_locus_tag locustag2, " + "				mp2.patric_na_feature_id, "
				+ "				round(corr(e1.log_ratio, e2.log_ratio), 3) correlation, " + "				count(distinct(e1.pid)) cnt " + "			from "
				+ "				app.genexp_gene e1, app.genexp_genemapping mp1, " + "				app.genexp_gene e2, app.genexp_genemapping mp2 "
				+ "			where e1.log_ratio is not null"
				+ "				and e1.pid = e2.pid and e1.locustag = mp1.exp_locus_tag and e2.locustag = mp2.exp_locus_tag "
				+ "				and mp1.patric_na_feature_id = :na_feature_id " + "				and mp1.genome_info_id = mp2.genome_info_id "
				+ "			group by mp1.refseq_locus_tag, mp2.refseq_locus_tag, mp2.patric_na_feature_id " + "			having ";
		if (key.containsKey("cutoff_value") && key.containsKey("cutoff_dir")) {
			if (key.get("cutoff_dir").equals("positive")) {
				sql += "		corr(e1.log_ratio, e2.log_ratio) > :cutoff_value";
			}
			else {
				sql += "		corr(e1.log_ratio, e2.log_ratio) < :cutoff_value";
			}
		}
		sql += "				and count(distinct(e1.pid)) > (select 0.8*count(distinct(gene.pid)) "
				+ "					from app.genexp_gene gene, app.genexp_genemapping mp " + "					where gene.log_ratio is not null "
				+ "						and gene.locustag = mp.exp_locus_tag " + "						and mp.patric_na_feature_id = :na_feature_id) " + "		) co, "
				+ "		app.dnafeature df " + "		where	" + "			co.patric_na_feature_id = df.na_feature_id ";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += "	order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += "	order by correlation desc, cnt desc";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);

		// binding value
		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			q.setInteger("na_feature_id", Integer.parseInt(key.get("na_feature_id")));
		}
		if (key.containsKey("cutoff_value") && key.containsKey("cutoff_dir")) {
			q.setFloat("cutoff_value", Float.parseFloat(key.get("cutoff_value")));
		}

		ScrollableResults scr = q.scroll();
		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {

			obj = scr.get();
			ResultType row = new ResultType();
			row.put("locustag1", obj[0]);
			row.put("locustag2", obj[1]);
			row.put("correlation", obj[2]);
			row.put("count", obj[3]);
			row.put("genome_info_id", obj[4]);
			row.put("genome_name", obj[5]);
			row.put("accession", obj[6]);
			row.put("locus_tag", obj[7]);
			row.put("na_feature_id", obj[8]);
			row.put("start_max", obj[9]);
			row.put("end_min", obj[10]);
			row.put("na_length", obj[11]);
			row.put("strand", obj[12]);
			row.put("product", obj[13]);

			row.put("annotation", obj[14]);
			row.put("feature_type", obj[15]);
			row.put("refseq_locus_tag", obj[1]);
			row.put("gene", obj[16]);
			row.put("protein_id", obj[17]);
			row.put("aa_length", obj[18]);

			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}

	public int getCorrelatedGenesCount(HashMap<String, String> key) {

		String sql = "select count(*) cnt " + "		from (select " + "				mp1.refseq_locus_tag locustag1, " + "				mp2.refseq_locus_tag locustag2, "
				+ "				mp2.patric_na_feature_id " + "			from " + "				app.genexp_gene e1, app.genexp_genemapping mp1, "
				+ "				app.genexp_gene e2, app.genexp_genemapping mp2" + "			where e1.log_ratio is not null "
				+ "				and e1.pid = e2.pid and e1.locustag = mp1.exp_locus_tag and e2.locustag = mp2.exp_locus_tag "
				+ "				and mp1.patric_na_feature_id = :na_feature_id " + "				and mp1.genome_info_id = mp2.genome_info_id " + "			group by "
				+ "				mp1.refseq_locus_tag, mp2.refseq_locus_tag, mp2.patric_na_feature_id " + "			having ";
		if (key.containsKey("cutoff_value") && key.containsKey("cutoff_dir")) {
			if (key.get("cutoff_dir").equals("positive")) {
				sql += "		corr(e1.log_ratio, e2.log_ratio) > :cutoff_value";
			}
			else {
				sql += "		corr(e1.log_ratio, e2.log_ratio) < :cutoff_value";
			}
		}
		sql += "				and count(distinct(e1.pid)) > (select 0.8*count(distinct(gene.pid)) "
				+ "					from app.genexp_gene gene, app.genexp_genemapping mp " + "					where gene.log_ratio is not null "
				+ "						and gene.locustag = mp.exp_locus_tag " + "						and mp.patric_na_feature_id = :na_feature_id) " + "			) co, "
				+ "			app.dnafeature df " + "		where " + "			co.patric_na_feature_id = df.na_feature_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);

		if (key.containsKey("na_feature_id") && key.get("na_feature_id") != null) {
			q.setInteger("na_feature_id", Integer.parseInt(key.get("na_feature_id").toString()));
		}
		if (key.containsKey("cutoff_value") && key.containsKey("cutoff_dir")) {
			q.setFloat("cutoff_value", Float.parseFloat(key.get("cutoff_value")));
		}

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<String> getEIDs(String taxon_id) {

		String sql = "select distinct mp.eid " + "	from app.genexp_genomemapping mp, "
				+ "	(select ncbi_tax_id from sres.taxon connect by prior taxon_id = parent_id start with ncbi_tax_id = :taxon_id) tx "
				+ "	where mp.ncbi_tax_id = tx.ncbi_tax_id";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("taxon_id", taxon_id);
		q.addScalar("eid", Hibernate.STRING).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<String> results = new ArrayList<String>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			results.add(it.next().toString());
		}
		return results;
	}

	public ArrayList<String> getEIDsFromGenomeID(String gid) {

		String sql = "select distinct mp.eid from app.genexp_genomemapping mp " + "	where mp.genome_info_id = :gid";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("gid", gid);
		q.addScalar("eid", Hibernate.STRING).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<String> results = new ArrayList<String>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			results.add(it.next().toString());
		}

		return results;
	}

	/* end of gene-level-expression */

	/* Pathway Enrichment */

	public int getPathwayEnrichmentNoofGenesSQL(HashMap<String, String> key) {

		String sql = "select count(distinct(na_feature_id)) cnt from app.pathwaysummary where " + getParsedFeatureIds(key);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	public String getPathwayEnrichmentSQL(HashMap<String, String> key, String where) {

		String sql = "";

		if (where.equals("count")) {
			sql += "select count(*) cnt from ";
		}
		else if (where.equals("function")) {
			sql += "select obs.opname pathway_name, obs.opid pathway_id, obs.ocnt ocnt, exp.ecnt ecnt, trunc(obs.ocnt*100/exp.ecnt) percentage from ";
		}

		sql += " ( select pathway_name opname, pathway_id opid, count(distinct(na_feature_id)) ocnt " + " from app.pathwaysummary " + " where "
				+ getParsedFeatureIds(key) + " group by pathway_name, pathway_id) obs, "
				+ " (select pathway_name epname, pathway_id epid, count(distinct(na_feature_id)) ecnt " + " from app.pathwaysummary "
				+ " where genome_info_id in (select distinct genome_info_id from app.pathwaysummary " + " where " + getParsedFeatureIds(key)
				+ " ) and algorithm = 'RAST' group by pathway_name, pathway_id) exp where obs.opid = exp.epid ";

		return sql;
	}

	public String getParsedFeatureIds(HashMap<String, String> key) {

		String sql = " (na_feature_id in (";

		List<?> lstGId = Arrays.asList(key.get("feature_info_id").split(","));

		if (lstGId.size() > 500) {
			sql += lstGId.get(0) + ",";
			for (int i = 1; i < lstGId.size(); i++) {
				if (i % 500 == 0) {
					sql = sql.substring(0, sql.length() - 1);
					sql += ") or na_feature_id in (" + lstGId.get(i) + ",";
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

		sql += "))";

		return sql;
	}

	public int getPathwayEnrichmentCount(HashMap<String, String> key) {
		String sql = getPathwayEnrichmentSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<ResultType> getPathwayEnrichmentList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {

		String sql = getPathwayEnrichmentSQL(key, "function");

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " ORDER BY " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " ORDER BY ps.pathway_id";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
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
			row.put("pathway_name", obj[0]);
			row.put("pathway_id", obj[1]);
			row.put("ocnt", obj[2]);
			row.put("ecnt", obj[3]);
			row.put("percentage", obj[4]);
			results.add(row);
		}
		return results;
	}

	public String getGenomeListFromFeatureIds(HashMap<String, String> key, int start, int end) {
		String sql = "", genomeIds = "";
		sql += "select distinct genome_info_id from app.pathwaysummary " + " where " + getParsedFeatureIds(key) + " and pathway_id = '"
				+ key.get("map").toString() + "'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setTimeout(SQL_TIMEOUT);

		if (end > 0) {
			q.setMaxResults(end);
		}

		ScrollableResults scr = q.scroll();
		Object[] obj = null;

		if (start > 1) {
			scr.setRowNumber(start - 1);
		}
		else {
			scr.beforeFirst();
		}

		for (int i = start; (end > 0 && i < end && scr.next() == true) || (end == -1 && scr.next() == true); i++) {
			obj = scr.get();
			if (i == 0) {
				genomeIds += obj[0].toString();
			}
			else {
				genomeIds += "," + obj[0].toString();
			}
		}
		return genomeIds;
	}
}
