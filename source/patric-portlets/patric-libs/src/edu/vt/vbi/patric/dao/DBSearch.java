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

import edu.vt.vbi.patric.common.SQLHelper;
import edu.vt.vbi.patric.common.StringHelper;
import edu.vt.vbi.patric.dao.ResultType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * An interface class for database queries that is used for search tools.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * @author Oral Dalay (orald@vbi.vt.edu)
 * 
 */
public class DBSearch {
	protected static SessionFactory factory;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public static SessionFactory getSessionFactory() {
		return factory;
	}

	protected final int SQL_TIMEOUT = 5 * 60;

	// ID Mapping

	public int getIDSearchCount(HashMap<String, String> key) {
		String sql = this.getIDSearchSQL(key, null, "count");
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public int getIDToCount(HashMap<String, String> key) {

		String sql = this.getIDSearchSQL(key, null, "tocount");
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<ResultType> getTranscriptomicsIDSearchResult(HashMap<String, String> key, int start, int end) {
		String sql = "";

		sql = this.getIDSearchSQL(key, null, "shortversion");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		if (end > 0) {
			q.setMaxResults(end);
		}
		q.setTimeout(240);

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

			row.put("na_feature_id", obj[0]);

			if (key.get("from").equalsIgnoreCase("RefSeq Locus Tag"))
				row.put("refseq_source_id", obj[1]);
			else if (key.get("from").equalsIgnoreCase("PATRIC Locus Tag")) {
				row.put("source_id", obj[1]);
				if (obj[2] == null) {
					obj[2] = "";
				}
				row.put("refseq_source_id", obj[2]);
			}

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	public ArrayList<ResultType> getIDSearchResult(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = "";

		sql = this.getIDSearchSQL(key, sort, "function");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		if (end > 0) {
			q.setMaxResults(end);
		}
		q.setTimeout(300);

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
			row.put("pseed_id", obj[21]);

			if (key.get("to").equalsIgnoreCase("UniProtKB-ID")) {
				row.put("uniprotkb_accession", obj[22]);
				row.put("uniprot_id", obj[23]);
			}
			else if (key.get("to").equalsIgnoreCase("RefSeq Locus Tag")) {
				row.put("refseq_source_id", obj[22]);
			}
			else if (key.get("to").equalsIgnoreCase("Gene ID")) {
				row.put("gene_id", obj[22]);
			}
			else if (key.get("to").equalsIgnoreCase("GI")) {
				row.put("gi_number", obj[22]);
			}
			else if (key.get("to").equalsIgnoreCase("RefSeq")) {
				row.put("protein_id", obj[22]);
			}
			else if (key.get("to").equalsIgnoreCase("PATRIC Locus Tag")) {

				if (key.get("from").equalsIgnoreCase("UniProtKB-ID")) {
					row.put("uniprotkb_accession", obj[22]);
					row.put("uniprot_id", obj[23]);
				}
				else if (key.get("from").equalsIgnoreCase("RefSeq Locus Tag")) {
					row.put("refseq_source_id", obj[22]);
				}
				else if (key.get("from").equalsIgnoreCase("RefSeq")) {
					row.put("protein_id", obj[22]);
				}
				else if (key.get("from").equalsIgnoreCase("Gene ID")) {
					row.put("gene_id", obj[22]);
				}
				else if (key.get("from").equalsIgnoreCase("GI")) {
					row.put("gi_number", obj[22]);
				}
				else if (!key.get("from").equalsIgnoreCase("PATRIC ID") && !key.get("from").equalsIgnoreCase("PSEED ID")) {
					row.put("requested_data", obj[22]);
				}
			}
			else if (!key.get("to").equalsIgnoreCase("PATRIC ID") && !key.get("to").equalsIgnoreCase("PATRIC Locus Tag")
					&& !key.get("to").equalsIgnoreCase("PSEED ID")) {
				row.put("requested_data", obj[22]);
			}

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	private String getIDSearchSQL(HashMap<String, String> key, HashMap<String, String> sort, String where) {

		String sql = "";

		if (where.equals("tocount")) {
			sql += "select count(distinct " + key.get("field") + ") as cnt ";

		}
		else if (where.equals("count")) {
			sql += "select count(*) as cnt ";

		}
		else if (where.equals("shortversion")) {
			sql += "select nf.na_feature_id ";

		}
		else {
			sql += "select nf.genome_info_id, nf.genome_name, nf.accession, nf.na_feature_id, nf.na_sequence_id, "
					+ "			nf.name, nf.source_id as locus_tag, "
					+ "				decode(nf.algorithm,'Curation','Legacy BRC','RAST','PATRIC','RefSeq') as algorithm, "
					+ "				decode(nf.is_reversed,1,'-','+') as strand, nf.debug_field, "
					+ "			nf.start_min, nf.start_max, nf.end_min, nf.end_max, nf.na_length, "
					+ "			nf.product, nf.gene, nf.aa_length, nf.is_pseudo, nf.bound_moiety, " + "			nf.anticodon"
					+ /* ,nf.protein_id */", nf.pseed_id ";
		}

		if (where.equals("shortversion") || where.equals("function")) {

			if (key.get("to").equalsIgnoreCase("PATRIC ID") || key.get("to").equalsIgnoreCase("PATRIC Locus Tag")
					|| key.get("to").equalsIgnoreCase("PSEED ID")) {

				if (key.get("from").equalsIgnoreCase("UniProtKB-ID")) {
					sql += ", pum.uniprotkb_accession, pum.uniprot_id ";
				}
				else if (key.get("from").equalsIgnoreCase("RefSeq Locus Tag")) {
					sql += ", rm.refseq_source_id ";
				}
				else if (key.get("from").equalsIgnoreCase("RefSeq")) {
					sql += ", rm.protein_id ";
				}
				else if (key.get("from").equalsIgnoreCase("Gene ID")) {
					sql += ", rm.gene_id ";
				}
				else if (key.get("from").equalsIgnoreCase("GI")) {
					sql += ", rm.gi_number ";
				}
				else if (!key.get("from").equalsIgnoreCase("PATRIC ID") && !key.get("from").equalsIgnoreCase("PATRIC Locus Tag")
						&& !key.get("from").equalsIgnoreCase("PSEED ID")) {
					sql += ", im.id requested_data ";
				}
				else if (key.get("from").equalsIgnoreCase("PATRIC Locus Tag")) {
					sql += ", nf.source_id ";
					if (key.get("to").equalsIgnoreCase("PATRIC Locus Tag")) {
						sql += ", prm.refseq_source_id ";
					}
				}
			}
			else if (key.get("to").equalsIgnoreCase("RefSeq") || key.get("to").equalsIgnoreCase("RefSeq Locus Tag")
					|| key.get("to").equalsIgnoreCase("Gene ID") || key.get("to").equalsIgnoreCase("GI")) {
				if (key.get("to").equalsIgnoreCase("RefSeq Locus Tag"))
					sql += ", rm.refseq_source_id ";
				if (key.get("to").equalsIgnoreCase("RefSeq"))
					sql += ", rm.protein_id ";
				if (key.get("to").equalsIgnoreCase("Gene ID"))
					sql += ", rm.gene_id ";
				if (key.get("to").equalsIgnoreCase("GI"))
					sql += ", rm.gi_number ";
			}
			else {
				if (key.get("to").equalsIgnoreCase("UniProtKB-ID")) {
					sql += ", pum.uniprotkb_accession, pum.uniprot_id ";
				}
				else {
					sql += ", im.id requested_data ";
				}
			}
		}

		if ((key.get("to").equalsIgnoreCase("RefSeq") || key.get("to").equalsIgnoreCase("RefSeq Locus Tag")
				|| key.get("to").equalsIgnoreCase("Gene ID") || key.get("to").equalsIgnoreCase("GI"))
				|| (key.get("from").equalsIgnoreCase("RefSeq") || key.get("from").equalsIgnoreCase("Gene ID") || key.get("from").equalsIgnoreCase(
						"GI"))) {

			sql += " from app.dnafeature nf, app.patricrefseqmapping rm " + "	where "/* nf.name='CDS' */+ " nf.na_feature_id=rm.patric_na_feature_id ";

		}
		else if (key.get("to").equalsIgnoreCase("UniProtKB-ID") || key.get("from").equalsIgnoreCase("UniProtKB-ID")) {

			sql += " from app.dnafeature nf, app.patricuniprotmapping pum " + "	where "/* nf.name='CDS' */+ " nf.na_feature_id=pum.na_feature_id ";

		}
		else if ((key.get("to").equalsIgnoreCase("PATRIC ID") || key.get("to").equalsIgnoreCase("PATRIC Locus Tag") || key.get("to")
				.equalsIgnoreCase("PSEED ID"))
				|| (key.get("from").equalsIgnoreCase("PATRIC ID") || key.get("from").equalsIgnoreCase("PATRIC Locus Tag") || key.get("from")
						.equalsIgnoreCase("PSEED ID"))) {

			if (key.get("from").equalsIgnoreCase("UniProtKB-ID")) {

				sql += " from app.dnafeature nf, app.patricuniprotmapping pum " + "	where "/* nf.name='CDS' */
						+ " nf.na_feature_id=pum.na_feature_id ";

			}
			else if (key.get("from").equalsIgnoreCase("RefSeq") || key.get("from").equalsIgnoreCase("RefSeq Locus Tag")
					|| key.get("from").equalsIgnoreCase("Gene ID") || key.get("from").equalsIgnoreCase("GI")) {

				sql += " from app.dnafeature nf, app.patricrefseqmapping rm " + "	where "/* nf.name='CDS' */
						+ " nf.na_feature_id=rm.patric_na_feature_id ";

			}
			else if ((key.get("from").equalsIgnoreCase("PATRIC ID") || key.get("from").equalsIgnoreCase("PSEED ID") || key.get("from")
					.equalsIgnoreCase("PATRIC Locus Tag"))
					&& (key.get("to").equalsIgnoreCase("PATRIC ID") || key.get("to").equalsIgnoreCase("PATRIC Locus Tag") || key.get("to")
							.equalsIgnoreCase("PSEED ID"))) {

				sql += " from app.dnafeature nf ";

				if (key.get("to").equalsIgnoreCase("PATRIC Locus Tag")) {
					sql += ", app.patricrefseqmapping prm ";
				}

				sql += " where 1=1 ";// nf.name='CDS' ";

				if (key.get("to").equalsIgnoreCase("PATRIC Locus Tag")) {
					sql += " and nf.na_feature_id = prm.patric_na_feature_id (+) ";
				}

			}
			else {

				if (key.get("from").equalsIgnoreCase("PATRIC ID") || key.get("from").equalsIgnoreCase("PSEED ID")
						|| key.get("from").equalsIgnoreCase("PATRIC Locus Tag")) {
					sql += " from app.dnafeature nf, app.idmapping im, app.patricuniprotmapping pum " + "	where "/* nf.name='CDS' */
							+ "nf.na_feature_id = pum.na_feature_id " + "	and im.uniprotkb_accession = pum.uniprotkb_accession "
							+ "	and im.id_type = '" + key.get("to") + "'";
				}
				else {
					sql += " from app.dnafeature nf, app.idmapping im, app.patricuniprotmapping pum " + "	where "/* nf.name='CDS' */
							+ "nf.na_feature_id = pum.na_feature_id " + "	and im.uniprotkb_accession = pum.uniprotkb_accession "
							+ "	and im.id_type = '" + key.get("from") + "'";
				}
			}

		}
		else {

			sql += " from app.dnafeature nf, app.idmapping im, app.patricuniprotmapping pum " + "	where "/* nf.name='CDS' */
					+ " nf.na_feature_id=pum.na_feature_id " + "	and im.uniprotkb_accession = pum.uniprotkb_accession " + "	and im.id_type = '"
					+ key.get("to") + "'";

		}

		if (key.containsKey("keyword") && key.get("keyword") != null) {

			// parse keyword
			String[] tmp = key.get("keyword").split("[,\\s]+");
			String keywords = "";

			if (tmp.length > 500) {
				keywords += " ('" + tmp[0].trim() + "',";
				for (int i = 1; i < tmp.length; i++) {
					if (i % 500 == 0) {
						keywords = keywords.substring(0, keywords.length() - 1);
						keywords += ") or REPLACE_ME in ('" + tmp[i].trim() + "',";
					}
					else {
						keywords += "'" + tmp[i].trim() + "',";
					}
				}
				keywords = keywords.substring(0, keywords.length() - 1) + ")";
			}
			else {
				keywords += " ('" + tmp[0].trim() + "'";
				for (int i = 1; i < tmp.length; i++) {
					keywords += ",'" + tmp[i].trim() + "'";
				}
				keywords += ")";
			}

			if (key.get("from").equalsIgnoreCase("PATRIC ID")) {
				keywords = keywords.replaceAll("REPLACE_ME", "nf.na_feature_id");
				sql += "	and (nf.na_feature_id in " + keywords + ") ";
			}
			else if (key.get("from").equalsIgnoreCase("PATRIC Locus Tag")) {
				keywords = keywords.replaceAll("REPLACE_ME", "nf.source_id");
				sql += "	and (nf.source_id in " + keywords + ") ";
			}
			else if (key.get("from").equalsIgnoreCase("PSEED ID")) {
				keywords = keywords.replaceAll("REPLACE_ME", "nf.pseed_id");
				sql += "	and (nf.pseed_id in " + keywords + ") ";
			}
			else if (key.get("from").equalsIgnoreCase("RefSeq Locus Tag") || key.get("from").equalsIgnoreCase("RefSeq")
					|| key.get("from").equalsIgnoreCase("Gene ID") || key.get("from").equalsIgnoreCase("GI")) {
				if (key.get("from").equalsIgnoreCase("RefSeq Locus Tag")) {
					keywords = keywords.replaceAll("REPLACE_ME", "rm.refseq_source_id");
					sql += "and (rm.refseq_source_id in " + keywords + ") ";
				}
				else if (key.get("from").equalsIgnoreCase("RefSeq")) {
					keywords = keywords.replaceAll("REPLACE_ME", "rm.protein_id");
					sql += "and (rm.protein_id in " + keywords + ") ";
				}
				else if (key.get("from").equalsIgnoreCase("Gene ID")) {
					keywords = keywords.replaceAll("REPLACE_ME", "rm.gene_id");
					sql += "and (rm.gene_id in " + keywords + ") ";
				}
				else if (key.get("from").equalsIgnoreCase("GI")) {
					keywords = keywords.replaceAll("REPLACE_ME", "rm.gi_number");
					sql += "and (rm.gi_number in " + keywords + ") ";
				}
			}
			else if (key.get("from").equalsIgnoreCase("UniProtKB-ID")) {
				String keywords_1 = keywords.replaceAll("REPLACE_ME", "pum.uniprotkb_accession");
				String keywords_2 = keywords.replaceAll("REPLACE_ME", "pum.uniprot_id");
				sql += "	and (pum.uniprotkb_accession in " + keywords_1 + ") OR  (pum.uniprot_id in " + keywords_2 + ")";
			}
			else {
				keywords = keywords.replaceAll("REPLACE_ME", "im.id");
				sql += "	and (im.id in " + keywords + ")";
			}
		}

		if (!where.equals("count") && !where.equals("tocount") && !where.equals("shortversion")) {
			if (sort != null) {
				sql += " order by " + sort.get("field") + " " + sort.get("direction");
				if (sort.get("field").equals("genome_name")) {
					sql += " order by " + sort.get("field") + " " + sort.get("direction") + ", locus_tag ASC ";
				}
			}
		}

		return sql;
	}

	/**
	 * Count the result of GenomeFinder in sequence list mode.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition [why we need a sorting condition for count query?]
	 * @return count
	 */
	public int getSequenceFinderSearchCount(HashMap<String, String> key, HashMap<String, String> sort, String genomeId) {
		String sql = this.getSequenceFinderSearchSQL(key, sort, genomeId, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGenomeFinderSearchValues(q, key, genomeId);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Count the result of GenomeFinder in genome list mode.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition [why we need a sorting condition for count query?]
	 * @return count
	 */
	public int getGenomeFinderSearchCount(HashMap<String, String> key, HashMap<String, String> sort) {
		String sql = this.getGenomeFinderSearchSQL(key, sort, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGenomeFinderSearchValues(q, key, "");

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Retrieves the result of GenomeFinder in sequence list mode.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting point of cursor in the result-set
	 * @param end stopping point of cursor in the result-set. If <code>-1</code> , returns all the results.
	 * @return sequence
	 */
	public ArrayList<ResultType> getSequenceFinderSearchResult(HashMap<String, String> key, HashMap<String, String> sort, String genomeId, int start,
			int end) {

		String sql = this.getSequenceFinderSearchSQL(key, sort, genomeId, "function");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindGenomeFinderSearchValues(q, key, genomeId);

		if (end > 0) {
			q.setMaxResults(end);
		}
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
			row.put("genome_name", obj[0]);
			row.put("display_name", obj[1]);
			row.put("genome_info_id", obj[2]);
			row.put("length", obj[3]);
			row.put("accession", obj[4]);
			row.put("sequence_info_id", obj[5]);
			row.put("na_sequence_id", obj[6]);
			row.put("molecule_type", obj[7]);
			row.put("sequence_type", obj[8]);
			row.put("topology", obj[9]);
			row.put("base_composition", obj[10]);
			row.put("description", obj[11]);
			row.put("sequence_status_name", obj[12]);
			results.add(row);
		}

		session.getTransaction().commit();
		return results;
	}

	/**
	 * Retrieves the result of GenomeFinder in genome list mode.
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting point of cursor in the result-set
	 * @param end stopping point of cursor in the result-set. If <code>-1</code> , returns all the results.
	 * @return genome
	 */
	public ArrayList<ResultType> getGenomeFinderSearchResult(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = this.getGenomeFinderSearchSQL(key, sort, "function");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q = bindGenomeFinderSearchValues(q, key, "");
		if (end > 0) {
			q.setMaxResults(end);
		}
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
			row.put("genome_info_id", obj[0]);
			row.put("genome_name", obj[1]);
			row.put("length", obj[2]);
			row.put("chromosome", obj[3]);
			row.put("plasmid", obj[4]);
			row.put("contig", obj[5]);
			row.put("rast_cds", obj[6]);
			row.put("brc_cds", obj[7]);
			row.put("refseq_cds", obj[8]);
			row.put("complete", obj[9]);

			int sum = 0;

			if (obj[3] != null)
				sum += Integer.parseInt(obj[3].toString());

			if (obj[4] != null)
				sum += Integer.parseInt(obj[4].toString());

			if (obj[5] != null)
				sum += Integer.parseInt(obj[5].toString());

			row.put("total", (Integer.toString(sum)));
			results.add(row);
		}

		session.getTransaction().commit();
		return results;
	}

	private String getSequenceFinderSearchSQL(HashMap<String, String> key, HashMap<String, String> sort, String genomeId, String where) {
		String sql = "";

		if (where.equals("count")) {
			sql += "select count(*) as cnt ";
		}
		else {
			sql += "select distinct gi.genome_name, gi.display_name, gi.genome_info_id, si.length, si.accession, "
					+ "		si.sequence_info_id,  si.na_sequence_id, si.molecule_type, "
					+ "		si.sequence_type, si.topology, round(((nvl(ns.c_count,0)+nvl(ns.g_count,0))/ns.length*100),2) base_composition, ns.description, si.sequence_status_name_id  ";
		}

		sql += " from " + "	cas.genomeinfo gi, " + "	cas.sequenceinfo si, " + "	dots.nasequence ns, " + "	app.genomesummary gs "
				+ "	where gi.genome_info_id = si.genome_info_id " + "	and gi.genome_info_id = gs.genome_info_id"
				+ "	and si.na_sequence_id = ns.na_sequence_id";

		if (key.containsKey("solrId") && !key.get("solrId").equalsIgnoreCase("")) {
			List<?> lstGId = Arrays.asList(key.get("solrId").split(","));
			if (lstGId.size() < 333) {
				sql += " and gi.genome_info_id in (" + key.get("solrId") + ")";
			}
			else {
				String innerSQL = "";
				ArrayList<ArrayList<String>> arrGId = SQLHelper.splitIDStringtoArray(key.get("solrId"), ",");
				for (int i = 0; i < arrGId.size(); i++) {
					innerSQL += " gi.genome_info_id in (" + StringHelper.implode(arrGId.get(i).toArray(), ",") + ")";
					if (i < (arrGId.size() - 1)) {
						innerSQL += " or ";
					}
				}
				sql += " and ( " + innerSQL + " )";
			}
		}

		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			sql += " and gi.genome_info_id = :genomeId";
		}

		if (!where.equals("count")) {
			if (sort != null && sort.containsKey("field") && !sort.get("field").equalsIgnoreCase("")) {
				sql += " order by " + sort.get("field") + " " + sort.get("direction");
			}
			else {
				sql += " order by gi.genome_name ";
			}
		}
		return sql;
	}

	private String getGenomeFinderSearchSQL(HashMap<String, String> key, HashMap<String, String> sort, String where) {
		String sql = "";
		if (where.equals("count")) {
			sql += "select count(distinct gi.genome_info_id) as cnt ";
		}
		else {
			sql += "select distinct gi.genome_info_id, gi.genome_name, gs.length, "
					+ "	gs.chromosome, gs.plasmid, gs.contig, gs.rast_cds, gs.brc_cds, " + "	gs.refseq_cds, gs.complete";
		}
		sql += "	from " + "		cas.genomeinfo gi, " + "		cas.sequenceinfo si, " + "		app.genomesummary gs "
				+ "	where gi.genome_info_id = gs.genome_info_id " + "		and gi.genome_info_id = si.genome_info_id ";

		if (key.containsKey("solrId") && !key.get("solrId").equalsIgnoreCase("")) {

			// System.out.print(key.get("solrId"));
			List<?> lstGId = Arrays.asList(key.get("solrId").split(","));
			if (lstGId.size() < 333) {
				sql += " and gi.genome_info_id in (" + key.get("solrId") + ")";
			}
			else {
				String innerSQL = "";
				ArrayList<ArrayList<String>> arrGId = SQLHelper.splitIDStringtoArray(key.get("solrId"), ",");
				for (int i = 0; i < arrGId.size(); i++) {
					innerSQL += " gi.genome_info_id in (" + StringHelper.implode(arrGId.get(i).toArray(), ",") + ")";
					if (i < (arrGId.size() - 1)) {
						innerSQL += " or ";
					}
				}
				sql += " and ( " + innerSQL + " )";
			}
		}

		if (!where.equals("count")) {
			if (sort != null && sort.containsKey("field") && !sort.get("field").equalsIgnoreCase("")) {
				sql += " order by " + sort.get("field") + " " + sort.get("direction");
			}
			else {
				sql += " order by gi.genome_name ";
			}
		}
		return sql;
	}

	private SQLQuery bindGenomeFinderSearchValues(SQLQuery q, HashMap<String, String> key, String genomeId) {
		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			q.setString("genomeId", genomeId);
		}
		return q;
	}

	// PROTEOMICS

	@SuppressWarnings("unchecked")
	public JSONArray getProteomicsPeptides(String experiment_id, String na_feature_id) {

		String sql = " SELECT pp.peptide_sequence " + " FROM proteomics.peptide pp, app.dnafeature df, app.patricuniprotmapping pum "
				+ " WHERE df.na_feature_id = ?" + " AND pp.experiment_id = ?" + " AND pum.na_feature_id = df.na_feature_id "
				+ " AND pum.uniprotkb_accession = pp.protein_id";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, na_feature_id);
		q.setString(1, experiment_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		JSONArray results = new JSONArray();
		Object obj = null;

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = it.next();
			JSONObject row = new JSONObject();
			row.put("peptide", obj);
			results.add(row);
		}

		return results;
	}

	// GO SEARCH

	private String getGOSearchListSQL(HashMap<String, String> key, String where) {
		String sql = "";
		if (where.equals("count")) {
			sql += "select count(distinct (gs.go_id || gs.algorithm)) as cnt ";

		}
		else if (where.equals("breadcrumb")) {
			sql += "select count(distinct (gs.go_id)) as cnt ";
		}
		else {
			sql += "select distinct(gs.go_id), gs.go_term, gs.algorithm, count(distinct(gs.na_feature_id)) gene_count ";
		}

		sql += "	from app.gosummary gs where 1=1 ";

		if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Term")) {
			String keyword = key.get("keyword");
			if (!keyword.equals("") && keyword != null) {
				sql += " and gs.go_id = :go_term";
			}
		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Name")) {
			String keyword = key.get("keyword");
			if (!keyword.equals("") && keyword != null) {
				sql += " and lower(gs.go_term) like lower(:go_name) ";
			}
		} /*
		 * else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("Keyword") ) {
		 * 
		 * String keyword = (String)key.get("keyword");
		 * 
		 * if(!keyword.equals("") && keyword !=null){ sql += " and contains(gs.keyword, lower(:keyword)) > 0"; }
		 * 
		 * }
		 */

		if (key.containsKey("algorithm")) {
			String algorithm = key.get("algorithm");
			if (!algorithm.equals("") && algorithm != null) {
				if (!algorithm.equals("ALL")) {
					sql += "	AND gs.algorithm = :algorithm";
				}
			}
		}

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

			List<?> lstGId = Arrays.asList(key.get("genomeId").split(","));
			sql += " AND gs.genome_info_id in (";
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
				sql += key.get("genomeId");
			}
			sql += ") ";
			sql += "	AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";
		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").equalsIgnoreCase("")) {
			sql += " AND gs.genome_info_id = :genomeId ";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";
		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("")) {
			sql += " and gs.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";
		}
		return sql;
	}

	private SQLQuery bindGOSearchValues(SQLQuery q, HashMap<String, String> key) {

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

		}
		else if (key.containsKey("genomeId") && key.get("genomeId") != null && !key.get("genomeId").equalsIgnoreCase("")) {

			q.setString("genomeId", key.get("genomeId"));

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("") && !key.get("taxonId").equalsIgnoreCase("")) {

			q.setString("taxonId", key.get("taxonId"));

		}

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				// System.out.print("algorithm"+algorithm);

				if (!algorithm.equals("ALL")) {

					if (algorithm.equals("BRC") || algorithm.equals("Legacy BRC"))

						q.setString("algorithm", "Curation");

					else if (algorithm.equals("PATRIC") || algorithm.equals("RAST"))

						q.setString("algorithm", "RAST");

					else if (algorithm.equals("RefSeq"))

						q.setString("algorithm", "RefSeq");
				}
			}
		}

		/*
		 * if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("Keyword") ) {
		 * 
		 * String keyword = ((String)key.get("keyword"));
		 * 
		 * if(!keyword.equals("") && keyword !=null){
		 * 
		 * q.setString("keyword", "%"+keyword+"%"); }
		 * 
		 * }else
		 */if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Term")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				q.setString("go_term", keyword);
			}

		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Name")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				q.setString("go_name", "%" + keyword + "%");
			}

		}

		if (key.containsKey("go_id")) {
			String go_id = (key.get("go_id"));
			if (!go_id.equals("") && go_id != null) {

				q.setString("go_id", go_id);
			}

		}

		return q;

	}

	/**
	 * Counts the result of GO Search in the go-term list mode.
	 * @param key filtering condition
	 * @param sort sorting condition [why we need a sorting condition for count query?]
	 * @return count
	 * @deprecated
	 */
	@Deprecated
	public int getGOSearchCount(HashMap<String, String> key, HashMap<String, String> sort) {
		String sql = this.getGOSearchListSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGOSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Retrieves the result of GO Search in the go-term list mode.
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting point of cursor in the result-set
	 * @param end stopping point of cursor in the result-set. If <code>-1</code> , returns all the results.
	 * @return go-term
	 * @deprecated
	 */
	@Deprecated
	public ArrayList<ResultType> getGOSearchList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql;

		sql = this.getGOSearchListSQL(key, "function");

		sql += "	GROUP BY gs.go_id, gs.go_term, gs.algorithm ";

		if (sort != null && sort.containsKey("field") && !sort.get("field").equalsIgnoreCase("")) {
			sql += " order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " order by gs.go_id,algorithm ";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q = bindGOSearchValues(q, key);

		if (key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
			// to collect keywords that users are interested
			System.out.println("[Keyword Collection(GOSearch):" + key.toString() + "]");
		}

		if (end > 0) {
			q.setMaxResults(end);
		}
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
			row.put("go_id", obj[0]);
			row.put("go_term", obj[1]);

			if (obj[2].toString().equals("Curation"))

				row.put("algorithm", "Legacy BRC");

			else if (obj[2].toString().equals("RefSeq"))

				row.put("algorithm", "RefSeq");

			else if (obj[2].toString().equals("RAST"))

				row.put("algorithm", "PATRIC");

			row.put("gene_count", obj[3]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	private String getGOFeatureSearchListSQL(HashMap<String, String> key, String where) {
		String sql = "";
		if (where.equals("count") || where.equals("breadcrumb")) {

			sql += "SELECT count(distinct (gs.go_id || gs.na_feature_id)) as cnt ";
			sql += "	FROM app.gosummary gs WHERE 1=1 ";

		}
		else {

			sql += "SELECT distinct gs.go_id, gs.go_term, gs.algorithm, gs.genome_info_id, df.genome_name, df.accession, df.source_id as locus_tag, df.na_feature_id, df.gene, df.product ";
			sql += "	FROM app.gosummary gs, app.dnafeature df ";
			sql += "WHERE df.na_feature_id = gs.na_feature_id ";
		}

		if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Term")) {

			String keyword = key.get("keyword").toLowerCase();

			if (!keyword.equals("") && keyword != null) {

				sql += " and gs.go_id = :go_term";
			}

		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("GO_Name")) {

			String keyword = key.get("keyword").toLowerCase();

			if (!keyword.equals("") && keyword != null) {

				sql += " and lower(gs.go_term) like lower(:go_name) ";
			}

		} /*
		 * else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("Keyword") ) {
		 * 
		 * String keyword = ((String)key.get("keyword")).toLowerCase();
		 * 
		 * if(!keyword.equals("") && keyword !=null){ sql += " and contains(gs.keyword, lower(:keyword)) > 0"; }
		 * 
		 * }
		 */

		if (key.containsKey("go_id")) {

			String go_id = (key.get("go_id"));

			if (!go_id.equals("") && go_id != null) {

				sql += " and gs.go_id = :go_id";
			}

		}

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL")) {

					sql += "	AND gs.algorithm = :algorithm";
				}
			}
		}

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

			List<?> lstGId = Arrays.asList(key.get("genomeId").split(","));

			sql += " AND gs.genome_info_id in (";

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

				sql += key.get("genomeId");

			}

			sql += ") ";

			sql += "	AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").equalsIgnoreCase("")) {

			sql += " AND gs.genome_info_id = :genomeId ";

			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("")) {

			sql += " and gs.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}

		return sql;
	}

	/**
	 * Count the result of GO Search in the feature list mode.
	 * @param key filtering condition
	 * @param sort sorting condition [why we need a sorting condition for count query?]
	 * @return count
	 * @deprecated
	 */
	@Deprecated
	public int getGOFeatureSearchCount(HashMap<String, String> key, HashMap<String, String> sort) {
		String sql = this.getGOFeatureSearchListSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGOSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Retrieves the result of GO Search in the feature list mode.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting point of cursor in the result-set
	 * @param end stopping point of cursor in the result-set. If <code>-1</code> , returns all the results.
	 * @return feature
	 * @deprecated
	 */
	@Deprecated
	public ArrayList<ResultType> getGOFeatureSearchList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql;

		sql = this.getGOFeatureSearchListSQL(key, "function");

		// sql
		// +="	GROUP BY gs.go_id, gs.go_term, gs.algorithm, gs.genome_info_id, df.genome_name, df.accession, df.source_id, df.na_feature_id, df.gene, df.product ";

		if (sort != null && sort.containsKey("field") && !sort.get("field").equalsIgnoreCase("")) {
			sql += " order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " order by gs.go_id,locus_tag ";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q = bindGOSearchValues(q, key);
		if (key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
			// to collect keywords that users are interested
			System.out.println("[Keyword Collection(GOFeatureSearch):" + key.toString() + "]");
		}
		if (end > 0) {
			q.setMaxResults(end);
		}
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
			row.put("go_id", obj[0]);
			row.put("go_term", obj[1]);
			if (obj[2].toString().equals("Curation"))

				row.put("algorithm", "Legacy BRC");

			else if (obj[2].toString().equals("RefSeq"))

				row.put("algorithm", "RefSeq");

			else if (obj[2].toString().equals("RAST"))

				row.put("algorithm", "PATRIC");

			row.put("genome_info_id", obj[3]);
			row.put("genome_name", obj[4]);
			row.put("accession", obj[5]);
			row.put("locus_tag", obj[6]);
			row.put("na_feature_id", obj[7]);
			row.put("gene", obj[8]);
			row.put("product", obj[9]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	/**
	 * [what is this for?]
	 * @param cId
	 * @param cType
	 * @param input
	 * @param algorithm
	 * @return ?
	 */
	public ArrayList<ResultType> getGONaFeatureIdList(String cId, String cType, String input, String algorithm) {

		String sql = "	select	distinct(gs.na_feature_id) genes" + "	FROM app.gosummary gs WHERE 1=1 ";

		if (cType.equals("taxon")) {

			HashMap<String, String> key = new HashMap<String, String>();
			key.put("taxonId", cId);
			key.put("algorithm", algorithm);

			// sql += getGenomeListByTaxon(key.get("taxonId"),
			// key.get("algorithm"), 0, -1);

			sql += " AND gs.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(key.get("taxonId")) + ") ";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (cType.equals("genome")) {

			sql += " AND	gs.genome_info_id in ("
					+ cId
					+ ") AND ( select count(*) from app.genomesummary gsu where gsu.genome_info_id = gs.genome_info_id  and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0 ";
		}

		sql += "	AND gs.go_id in (" + input + ")" + "	AND gs.algorithm in (" + algorithm + ")";

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("genes", Hibernate.STRING);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
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

	// EC Search

	private String getECSearchListSQL(HashMap<String, String> key, String where) {

		String sql = "";
		if (where.equals("count")) {
			sql += "select count(distinct (es.ec_number || es.algorithm)) as cnt ";

		}
		else if (where.equals("breadcrumb")) {

			sql += "select count(distinct (es.ec_number)) as cnt ";

		}
		else {

			sql += "select distinct(es.ec_number), es.ec_name, es.algorithm, count(distinct(es.na_feature_id)) gene_count ";
		}

		sql += "	from app.ecsummary es where 1=1 ";

		if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Number")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				sql += " and es.ec_number = :ec_number";
			}

		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Name")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				sql += " and lower(es.ec_name) like lower(:ec_name) ";
			}

		} /*
		 * else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("Keyword") ) {
		 * 
		 * String keyword = (String)key.get("keyword");
		 * 
		 * if(!keyword.equals("") && keyword !=null){ sql += " and contains(es.keyword, lower(:keyword)) > 0"; }
		 * 
		 * }
		 */

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL")) {

					sql += "	AND es.algorithm = :algorithm";
				}
			}
		}

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

			List<?> lstGId = Arrays.asList(key.get("genomeId").split(","));

			sql += " AND es.genome_info_id in (";

			if (lstGId.size() > 500) {

				sql += lstGId.get(0) + ",";

				for (int i = 1; i < lstGId.size(); i++) {

					if (i % 500 == 0) {

						sql = sql.substring(0, sql.length() - 1);

						sql += ") or es.genome_info_id in (" + lstGId.get(i) + ",";

					}
					else {

						sql += lstGId.get(i) + ",";

					}

				}

				sql = sql.substring(0, sql.length() - 1);

			}
			else {

				sql += key.get("genomeId");

			}

			sql += ") ";

			sql += "	AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").equalsIgnoreCase("")) {

			sql += " AND es.genome_info_id = :genomeId ";

			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("")) {

			sql += " and es.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}

		return sql;
	}

	private SQLQuery bindECSearchValues(SQLQuery q, HashMap<String, String> key) {

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

		}
		else if (key.containsKey("genomeId") && key.get("genomeId") != null && !key.get("genomeId").equalsIgnoreCase("")) {

			q.setString("genomeId", key.get("genomeId"));

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("") && !key.get("taxonId").equalsIgnoreCase("")) {

			q.setString("taxonId", key.get("taxonId"));

		}

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL")) {

					if (algorithm.equals("BRC") || algorithm.equals("Legacy BRC"))

						q.setString("algorithm", "Curation");

					else if (algorithm.equals("PATRIC") || algorithm.equals("RAST"))

						q.setString("algorithm", "RAST");

					else if (algorithm.equals("RefSeq"))

						q.setString("algorithm", "RefSeq");
				}
			}
		}

		/*
		 * if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("Keyword") ) {
		 * 
		 * String keyword = ((String)key.get("keyword"));
		 * 
		 * if(!keyword.equals("") && keyword !=null){
		 * 
		 * q.setString("keyword", "%"+keyword+"%"); }
		 * 
		 * }else
		 */if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Number")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				q.setString("ec_number", keyword);
			}

		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Name")) {

			String keyword = key.get("keyword");

			if (!keyword.equals("") && keyword != null) {

				q.setString("ec_name", "%" + keyword + "%");
			}

		}

		if (key.containsKey("ec_number")) {

			String ec_number = (key.get("ec_number"));

			if (!ec_number.equals("") && ec_number != null) {

				q.setString("ec_number", ec_number);
			}

		}

		return q;

	}

	/**
	 * Count the result of EC Search in the EC list mode.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition [why we need a sorting condition for count query?]
	 * @return count
	 * @deprecated
	 */
	@Deprecated
	public int getECSearchCount(HashMap<String, String> key, HashMap<String, String> sort) {
		String sql = this.getECSearchListSQL(key, "count");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindECSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Retrieve the result of EC Search in the EC list mode.
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting point of cursor in the result-set
	 * @param end stopping point of cursor in the result-set. If <code>-1</code> , returns all the results.
	 * @return EC
	 * @deprecated
	 */
	@Deprecated
	public ArrayList<ResultType> getECSearchList(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql;

		sql = this.getECSearchListSQL(key, "function");

		sql += "	GROUP BY es.ec_number, es.ec_name, es.algorithm ";

		if (sort != null && sort.containsKey("field") && !sort.get("field").equalsIgnoreCase("")) {
			sql += " order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " order by es.ec_number,algorithm ";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindECSearchValues(q, key);

		if (key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
			// to collect keywords that users are interested
			System.out.println("[Keyword Collection(ECSearch):" + key.toString() + "]");
		}

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
			row.put("ec_number", obj[0]);
			row.put("ec_name", obj[1]);

			if (obj[2].toString().equals("Curation"))

				row.put("algorithm", "Legacy BRC");

			else if (obj[2].toString().equals("RefSeq"))

				row.put("algorithm", "RefSeq");

			else if (obj[2].toString().equals("RAST"))

				row.put("algorithm", "PATRIC");

			row.put("gene_count", obj[3]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;
	}

	private String getECFeatureSearchListSQL(HashMap<String, String> key, String where) {
		String sql = "";
		if (where.equals("count") || where.equals("breadcrumb")) {
			sql += "SELECT count(distinct (es.ec_number || es.na_feature_id)) as cnt ";
			sql += "	FROM app.ecsummary es WHERE 1=1 ";
		}
		else {
			sql += "SELECT distinct es.ec_number, es.ec_name, es.algorithm, es.genome_info_id, df.genome_name, df.accession, df.source_id as locus_tag, df.na_feature_id, df.gene, df.product ";
			sql += "	FROM app.ecsummary es, app.dnafeature df ";
			sql += "WHERE df.na_feature_id = es.na_feature_id ";
		}

		if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Number")) {

			String keyword = key.get("keyword").toLowerCase();

			if (!keyword.equals("") && keyword != null) {
				sql += " and es.ec_number = :ec_number";
			}
		}
		else if (key.containsKey("search_on") && key.get("search_on").equalsIgnoreCase("EC_Name")) {

			String keyword = key.get("keyword").toLowerCase();

			if (!keyword.equals("") && keyword != null) {
				sql += " and lower(es.ec_name) like lower(:ec_name) ";
			}

		}

		if (key.containsKey("ec_number")) {

			String ec_number = (key.get("ec_number"));

			if (!ec_number.equals("") && ec_number != null) {

				sql += " and es.ec_number = :ec_number";
			}

		}

		if (key.containsKey("algorithm")) {

			String algorithm = key.get("algorithm");

			if (!algorithm.equals("") && algorithm != null) {

				if (!algorithm.equals("ALL")) {

					sql += "	AND es.algorithm = :algorithm";
				}
			}
		}

		if (key.containsKey("genomeId") && key.get("genomeId").contains(",")) {

			List<?> lstGId = Arrays.asList(key.get("genomeId").split(","));

			sql += " AND es.genome_info_id in (";

			if (lstGId.size() > 500) {

				sql += lstGId.get(0) + ",";

				for (int i = 1; i < lstGId.size(); i++) {

					if (i % 500 == 0) {

						sql = sql.substring(0, sql.length() - 1);

						sql += ") or es.genome_info_id in (" + lstGId.get(i) + ",";

					}
					else {

						sql += lstGId.get(i) + ",";

					}

				}

				sql = sql.substring(0, sql.length() - 1);

			}
			else {

				sql += key.get("genomeId");

			}

			sql += ") ";

			sql += "	AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("genomeId") && !key.get("genomeId").equalsIgnoreCase("")) {

			sql += " AND es.genome_info_id = :genomeId ";

			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (key.containsKey("taxonId") && !key.get("taxonId").equalsIgnoreCase("")) {

			sql += " and es.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}

		return sql;
	}

	/**
	 * [what is this for?]
	 * @param cId
	 * @param cType
	 * @param input
	 * @param algorithm
	 * @return ?
	 */
	public ArrayList<ResultType> getECNaFeatureIdList(String cId, String cType, String input, String algorithm) {

		String sql = "	select	distinct(es.na_feature_id) genes" + "	FROM app.ecsummary es WHERE 1=1 ";

		if (cType.equals("taxon")) {

			HashMap<String, String> key = new HashMap<String, String>();
			key.put("taxonId", cId);
			key.put("algorithm", algorithm);

			sql += " AND es.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(key.get("taxonId")) + ") ";
			sql += " AND (select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";

		}
		else if (cType.equals("genome")) {

			sql += " AND	es.genome_info_id in ("
					+ cId
					+ ") AND ( select count(*) from app.genomesummary gsu where gsu.genome_info_id = es.genome_info_id  and (gsu.complete='Complete' or gsu.complete ='WGS') ) > 0";
		}

		sql += "	AND es.ec_number in (" + input + ")" + "	AND es.algorithm in (" + algorithm + ")";

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("genes", Hibernate.STRING);
		q.setTimeout(SQL_TIMEOUT);
		List<?> rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next();

			ResultType row = new ResultType();

			row.put("genes", obj);

			results.add(row);
		}
		return results;

	}

	// for download?

	/**
	 * [what is this for?]
	 * @param key
	 * @return ?
	 */
	public ArrayList<ResultType> getGenomeNames(HashMap<String, String> key) {
		String sql = "select distinct gi.common_name names from cas.genomeinfo gi where 1=1 ";

		if (key.containsKey("genomeId") && key.get("genomeId") != null && !key.get("genomeId").equals("")) {

			List<?> lstGId = Arrays.asList(key.get("genomeId").split(","));

			sql += " AND (gi.genome_info_id in (";

			if (lstGId.size() > 500) {

				sql += lstGId.get(0) + ",";

				for (int i = 1; i < lstGId.size(); i++) {

					if (i % 500 == 0) {

						sql = sql.substring(0, sql.length() - 1);

						sql += ") or gi.genome_info_id in (" + lstGId.get(i) + ",";

					}
					else {

						sql += lstGId.get(i) + ",";

					}

				}

				sql = sql.substring(0, sql.length() - 1);

			}
			else {

				sql += key.get("genomeId");

			}

			sql += "))";

			// sql += " and gi.genome_info_id in (" + key.get("genomeId") + ")";

		}
		else if (key.containsKey("taxonId") && key.get("taxonId") != null && !key.get("taxonId").equals("")) {

			sql += " and gi.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(key.get("taxonId")) + ")";

		}

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("names", Hibernate.STRING);
		q.setCacheable(true);
		List<?> rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next();

			ResultType row = new ResultType();
			row.put("genomeNames", obj);

			results.add(row);
		}
		return results;

	}

	// for breadcrumb?

	/**
	 * [what is this for?]
	 * @param key
	 * @return count?
	 */
	public int getDistinctECFinderBreadCrumb(HashMap<String, String> key) {

		String sql = this.getECSearchListSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindECSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());

	}

	/**
	 * [what is this for?]
	 * @param key
	 * @return count?
	 */
	public int getDistinctECFeatureFinderBreadCrumb(HashMap<String, String> key) {

		String sql = this.getECFeatureSearchListSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindECSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * [what is this for?]
	 * @param key
	 * @return count?
	 */
	public int getDistinctGOFinderBreadCrumb(HashMap<String, String> key) {

		String sql = this.getGOSearchListSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGOSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * [what is this for?]
	 * @param key
	 * @return count?
	 */
	public int getDistinctGOFeatureFinderBreadCrumb(HashMap<String, String> key) {

		String sql = this.getGOFeatureSearchListSQL(key, "breadcrumb");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q = bindGOSearchValues(q, key);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * [what is this for?]
	 * @return ?
	 */
	public ArrayList<ResultType> getIDTypes() {

		// String sql = " select distinct id_type ids from app.idmapping order by ids ";
		String sql = " select id_type ids from app.idtype order by ids ";

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("ids", Hibernate.STRING);
		q.setCacheable(true);
		List<?> rset = q.list();

		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next();

			ResultType row = new ResultType();
			if (!obj.toString().equals("RefSeq") && !obj.toString().equals("Gene ID") && !obj.toString().equals("GI")) {
				row.put("id", obj);
				row.put("value", obj);
				row.put("group", "Other Identifiers");
				results.add(row);
			}
			/*
			 * if (obj.toString().equals("UniProtKB-ID")){ row.put("id", (Object)"UniProtKB AC/ID"); row.put("value", (Object)"UniProtKB AC/ID");
			 * row.put("group", "Other Identifiers"); results.add(row); }
			 */
		}
		return results;
	}

	// To be removed

	public ArrayList<ResultType> getTaxonIdList(String cId, String cType, String genomeId, String algorithm, String status) {

		String sql = " select gi.genome_info_id as ids from app.genomesummary gi ";

		if (cType.equals("taxon") && genomeId.equals("")) {

			if (!algorithm.equals("")) {

				if (algorithm.equals("RAST"))

					sql += " where gi.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(cId) + ") and gi.RAST = 1";

				else if (algorithm.equals("RefSeq"))

					sql += " where gi.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(cId) + ") and gi.RefSeq = 1";

				else if (algorithm.equals("BRC"))

					sql += " where gi.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(cId) + ") and gi.BRC = 1";

				if (!status.equals(""))
					sql += "and gi.complete = '" + status + "'";
			}
			else

				sql += " where gi.ncbi_tax_id in (" + DBSummary.getTaxonIdsInTaxonSQL(cId) + ")";

		}
		else {
			sql += " where gi.genome_info_id in (" + genomeId + " ) ";
		}

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("ids", Hibernate.STRING);
		q.setCacheable(true);
		List<?> rset = q.list();

		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = iter.next();

			ResultType row = new ResultType();
			row.put("id", obj);
			results.add(row);

		}
		return results;

	}

	protected String getRgExpPatOfIds() {
		return "TIGR[0-9]{1,5}|PF[0-9]{1,5}|COG[0-9]{1,4}|IPB[0-9]{1,6}|IPR[0-9]{1,6}|PS[0-9]{1,5}|SSF[0-9]{1,5}|PR[0-9]{1,5}|SM[0-9]{1,5}|PD[0-9]{1,6}|GO\\:[0-9]{1,7}";
	}

}
