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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.lob.SerializableClob;

import edu.vt.vbi.patric.beans.DNAFeature;
import edu.vt.vbi.patric.common.SolrInterface;

/**
 * <p>
 * An interface class for database queries. DBSummary includes queries that are used for patric-overview, patric-jbrowse, patric-phylogeny, and
 * patric-common. This class needs to be initialized (set SessionFactory) prior to use.
 * </p>
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 */
public class DBSummary {
	protected static SessionFactory factory;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public static SessionFactory getSessionFactory() {
		return factory;
	}

	/**
	 * Retrieves sequences that are associated to a given taxon node. This is used by GenomeList tab.
	 * 
	 * @param key filtering conditions such as data_source ( <code>Legacy BRC|RefSeq|PATRIC</code>) and ncbi_taxon_id
	 * @param sort sorting condition
	 * @param start starting pointer
	 * @param end stopping pointer. If this is <code>-1</code>, returns all the results.
	 * @return a list of sequence info
	 */
	public ArrayList<ResultType> getSequenceListByTaxon(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = "select distinct gi.genome_info_id, gi.genome_name, gi.ncbi_tax_id, si.sequence_info_id, si.accession, "
				+ " 		si.gi, si.length, si.chromosome, si.strain, si.isolate, "
				+ "		si.molecule_type, si.segment, si.localization, si.sequence_type, si.collection_date, "
				+ " 		si.country, round(((nvl(ns.c_count,0)+nvl(ns.g_count,0))/ns.length*100),2) base_composition, ns.description "
				+ "	FROM app.genomesummary gi, cas.sequenceinfo si, dots.nasequence ns " + "	WHERE gi.genome_info_id = si.genome_info_id "
				+ "		and si.na_sequence_id = ns.na_sequence_id " + "		and si.sequence_info_id in (" + getSequenceIdsInTaxonSQL(":ncbi_taxon_id")
				+ ") ";

		if (key.get("data_source").equalsIgnoreCase("Legacy BRC")) {
			sql += "	and gi.brc = '1' ";
		}
		else if (key.get("data_source").equalsIgnoreCase("RefSeq")) {
			sql += "	and gi.refseq='1' ";
		}
		else if (key.get("data_source").equalsIgnoreCase("PATRIC")) {
			sql += "	and gi.rast='1' ";
		}

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " Order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " Order by genome_name, accession";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setString("ncbi_taxon_id", key.get("ncbi_taxon_id"));

		ScrollableResults scr = sqlQuery.scroll();
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
			row.put("ncbi_tax_id", obj[2]);
			row.put("sequence_info_id", obj[3]);
			row.put("accession", obj[4]);
			row.put("gi", obj[5]);
			row.put("length", obj[6]);
			row.put("chromosome", obj[7]);
			row.put("strain", obj[8]);
			row.put("isolate", obj[9]);
			row.put("molecular_type", obj[10]);
			row.put("segment", obj[11]);
			row.put("localization", obj[12]);
			row.put("sequence_type", obj[13]);
			row.put("collection_date", obj[14]);
			row.put("country", obj[15]);
			row.put("base_composition", obj[16]);
			row.put("description", obj[17]);
			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}

	/**
	 * Retrieves taxonomy info of a given taxon node. This is used by GenomeSelector
	 * 
	 * @param key filtering condition such as ncbi_taxon_id
	 * @return taxonomy info (ncbi_taxon_id, class_name, node_count)
	 */
	public ResultType getTaxonomyNodeForGenomeSelector(HashMap<String, String> key) {
		String sql = "SELECT ncbi_taxon_id, class_name, node_count from cas.ncbiclassification where ncbi_taxon_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, key.get("ncbi_taxon_id"));
		List<?> nodeList = q.list();
		Object[] obj = null;
		if (nodeList.iterator().hasNext()) {
			obj = (Object[]) nodeList.iterator().next();
		}

		ResultType result = new ResultType();
		result.put("ncbi_taxon_id", obj[0]);
		result.put("class_name", obj[1]);
		result.put("node_count", obj[2]);

		return result;
	}

	/**
	 * Retrieves genomes that are associated to a given taxon node. This is used by GenomeSelector to feed the Genome List tab and "Jump to"
	 * functionality.
	 * 
	 * @param key filtering condition such as ncbi_taxon_id and keyword
	 * @return a list of genome info (ncbi_taxon_id, class_name, node_count, genome_info_id, genome_name)
	 */
	public ArrayList<ResultType> getGenomeListForGenomeSelector(HashMap<String, String> key) {
		String sql = "SELECT node_level, node_left, node_right from cas.ncbiclassification where ncbi_taxon_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, key.get("ncbi_taxon_id"));
		List<?> nodeList = q.list();
		Object[] node = null;
		if (nodeList.iterator().hasNext()) {
			node = (Object[]) nodeList.iterator().next();
		}

		sql = "SELECT cls.ncbi_taxon_id, cls.class_name, cls.node_count, gi.genome_info_id, gi.genome_name "
				+ "	from cas.ncbiclassification cls, app.genomesummary gi " + "	where cls.node_left between ? and ? "
				+ "	and (cls.node_count>0 or cls.genome_count>0) " + "	and cls.ncbi_taxon_id = gi.ncbi_tax_id ";
		if (key.containsKey("keyword")) {
			sql += "	and lower(gi.genome_name) like :genome_name ";
		}
		sql += "	order by gi.genome_name ";

		q = session.createSQLQuery(sql);
		q.setString(0, node[1].toString());
		q.setString(1, node[2].toString());

		if (key.containsKey("keyword")) {
			String keyword = key.get("keyword").toLowerCase().trim().replace("'", "''").replace("_", "\\_").replace("-", "\\-");
			q.setString("genome_name", "%" + keyword + "%");
		}
		q.addScalar("ncbi_taxon_id", Hibernate.INTEGER).addScalar("class_name", Hibernate.STRING).addScalar("node_count", Hibernate.INTEGER);
		q.addScalar("genome_info_id", Hibernate.INTEGER).addScalar("genome_name", Hibernate.STRING).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("ncbi_taxon_id", obj[0]);
			row.put("class_name", obj[1]);
			row.put("node_count", obj[2]);
			row.put("genome_info_id", obj[3]);
			row.put("genome_name", obj[4]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves taxonomy tree data that are associated to a given taxon node. This is used by GenomeSelector to feed the Taxonomy Tree tab and
	 * "Jump to" functionality.
	 * 
	 * @param key filtering condition such as ncbi_taxon_id and keyword
	 * @return a list of tree info (node_level, node_left, rank, class_name, node_count, is_leaf, parent_ncbi_taxon_id, genome_count, genome_below)
	 */
	public ArrayList<ResultType> getTaxonomyTreeForGenomeSelector(HashMap<String, String> key) {
		String sql = "SELECT node_level, node_left, node_right from cas.ncbiclassification where ncbi_taxon_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, key.get("ncbi_taxon_id"));
		List<?> nodeList = q.list();
		Object[] node = null;
		if (nodeList.iterator().hasNext()) {
			node = (Object[]) nodeList.iterator().next();
		}

		sql = "SELECT distinct cls.node_level, cls.node_left, cls.rank, " + "		cls.class_name, cls.node_count, "
				+ "		cls.ncbi_taxon_id, decode(cls.node_right-cls.node_left,1,1,0) is_leaf, cls.parent_ncbi_taxon_id, "
				+ "		cls.genome_count, cls.node_count-cls.genome_count genome_below "
				+ "	from cas.ncbiclassification cls, cas.genomeclassrelationship gr " + "	where node_left between ? and ? "
				+ "		and (cls.node_count>0 or cls.genome_count>0) " + "		and cls.genome_classification_id = gr.genome_classification_id(+) ";

		if (key.containsKey("keyword")) {
			sql += "	and lower(cls.class_name) like :keyword ";
		}
		sql += "	order by node_left";

		q = session.createSQLQuery(sql);
		q.setString(0, node[1].toString());
		q.setString(1, node[2].toString());
		if (key.containsKey("keyword")) {
			String keyword = key.get("keyword").toLowerCase().trim().replace("'", "''").replace("_", "\\_").replace("-", "\\-");
			q.setString("keyword", "%" + keyword + "%");
		}
		q.addScalar("node_level", Hibernate.INTEGER).addScalar("node_left", Hibernate.INTEGER).addScalar("rank", Hibernate.STRING);
		q.addScalar("class_name", Hibernate.STRING).addScalar("node_count", Hibernate.INTEGER);
		q.addScalar("ncbi_taxon_id", Hibernate.INTEGER).addScalar("is_leaf", Hibernate.INTEGER).addScalar("parent_ncbi_taxon_id", Hibernate.INTEGER);
		q.addScalar("genome_count", Hibernate.INTEGER).addScalar("genome_below", Hibernate.INTEGER).setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		Object[] obj = null;

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			ResultType row = new ResultType();
			row.put("node_level", obj[0]);
			row.put("node_left", obj[1]);
			row.put("rank", obj[2]);
			row.put("class_name", obj[3]);
			row.put("node_count", obj[4]);
			row.put("ncbi_taxon_id", obj[5]);
			row.put("is_leaf", obj[6]);
			row.put("parent_ncbi_taxon_id", obj[7]);
			row.put("genome_count", obj[8]);
			row.put("genome_below", obj[9]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves genome summary that are associated to a given taxon node.
	 * 
	 * @param key filtering condition such as data_source( <code>Legacy BRC|RefSeq|PATRIC</code>), filter( <code>complete|wgs|plasmid</code>), and
	 * ncbi_taxon_id
	 * @param sort sorting condition
	 * @param start starting pointer
	 * @param end stopping pointer. If <code>-1</code>, returns all the results.
	 * @return genome level summary of features and sequences. Each row (ResultType) has
	 * <dl>
	 * <dd><code>genome_info_id</code></dd>
	 * <dd><code>genome_name</code></dd>
	 * <dd><code>length</code></dd>
	 * <dd><code>chromosome</code> - number of chromosomes</dd>
	 * <dd><code>plasmid</code> - number of plasmid</dd>
	 * <dd><code>contig</code> - number of contigs</dd>
	 * <dd><code>rast_cds</code> - number of CDS in RAST annotation</dd>
	 * <dd><code>brc_cds</code> - number of CDS in Legacy BRC annotation</dd>
	 * <dd><code>refseq_cds</code> - number of CDS in RefSeq annotation</dd>
	 * <dd><code>complete</code> - completeness of annotation. <code>Complete | WGS | Plasmid</code></dd>
	 * <dd><code>ncbi_tax_id</code></dd>
	 * </dl>
	 */
	public ArrayList<ResultType> getGenomeListByTaxon(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = "SELECT gs.genome_info_id, gs.genome_name, gs.length, " + "		nvl(gs.chromosome,0), nvl(gs.plasmid,0), nvl(gs.contig,0), "
				+ "		nvl(gs.rast_cds,0), nvl(gs.brc_cds,0), nvl(gs.refseq_cds,0), " + "		complete, gs.ncbi_tax_id " + "	FROM app.genomesummary gs "
				+ "	WHERE gs.ncbi_tax_id in ( " + getTaxonIdsInTaxonSQL("?") + ") ";

		if (key.get("data_source").equalsIgnoreCase("Legacy BRC")) {
			sql += "	and gs.brc = 1 ";
		}
		else if (key.get("data_source").equalsIgnoreCase("RefSeq")) {
			sql += "	and gs.refseq = 1 ";
		}
		else if (key.get("data_source").equalsIgnoreCase("PATRIC")) {
			sql += "	and gs.rast = 1 ";
		}

		if (key.containsKey("filter") && key.get("filter") != null) {
			if (key.get("filter").equals("complete")) {
				sql += "	and complete = 'Complete'";
			}
			else if (key.get("filter").equals("wgs")) {
				sql += "	and complete = 'WGS'";
			}
			else if (key.get("filter").equals("plasmid")) {
				sql += "	and complete = 'Plasmid'";
			}
		}

		sql += "	Group by gs.genome_info_id, gs.genome_name, gs.length, gs.chromosome, gs.plasmid, gs.contig, gs.rast_cds, gs.brc_cds, gs.refseq_cds, complete, ncbi_tax_id ";

		if (sort != null && sort.containsKey("field") && sort.get("field") != null && sort.containsKey("direction") && sort.get("direction") != null) {
			sql += " Order by " + sort.get("field") + " " + sort.get("direction");
		}
		else {
			sql += " Order by genome_name";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setString(0, key.get("ncbi_taxon_id").toString());

		if (end > 0) {
			sqlQuery.setMaxResults(end);
		}

		ScrollableResults scr = sqlQuery.scroll();
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
			row.put("ncbi_tax_id", obj[10]);
			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}

	/**
	 * Retrieves genome summary of a given genome.
	 * 
	 * @param id genome_info_id
	 * @return genome level summary (see the return type of {@link #getGenomeListByTaxon(HashMap, HashMap, int, int)})
	 */
	public ResultType getGenomeSummary(String id) {
		String sql = "SELECT gs.genome_info_id, gs.genome_name, gs.length, " + "		nvl(gs.chromosome,0), nvl(gs.plasmid,0), nvl(gs.contig,0), "
				+ "		nvl(gs.rast_cds,0), nvl(gs.brc_cds,0), nvl(gs.refseq_cds,0), " + "		complete, gs.ncbi_tax_id " + "	FROM app.genomesummary gs "
				+ "	WHERE gs.genome_info_id = :genome_info_id "
				+ "	Group by gs.genome_info_id, gs.genome_name, gs.length, gs.chromosome, gs.plasmid, gs.contig, "
				+ "		gs.rast_cds, gs.brc_cds, gs.refseq_cds, complete, ncbi_tax_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setString("genome_info_id", id);

		List<?> rset = sqlQuery.list();
		session.getTransaction().commit();

		Object[] obj = null;
		ResultType results = new ResultType();
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			results.put("genome_info_id", obj[0]);
			results.put("genome_name", obj[1]);
			results.put("length", obj[2]);
			results.put("chromosome", obj[3]);
			results.put("plasmid", obj[4]);
			results.put("contig", obj[5]);
			results.put("rast_cds", obj[6]);
			results.put("brc_cds", obj[7]);
			results.put("refseq_cds", obj[8]);
			results.put("complete", obj[9]);
			results.put("ncbi_tax_id", obj[10]);
		}
		return results;
	}

	/**
	 * Counts genomes that are associated to a given taxon node.
	 * 
	 * @param key filtering condition such as data_source and ncbi_taxon_id
	 * @return count of complete genomes (cnt_complete), wgs (cnt_wgs), plasmid (cnt_plasmid), and all genomes (cnt_all).
	 */
	public ResultType getGenomeCount(HashMap<String, String> key) {

		String sql = "select nvl(sum(decode(gs.complete,'Complete',1,0)),0) complete_cnt, " + "		nvl(sum(decode(gs.complete,'WGS',1,0)),0) wgs_cnt, "
				+ "		nvl(sum(decode(gs.complete,'Plasmid',1,0)),0) plasmid_cnt, " + "		count(*) all_cnt " + "	from app.genomesummary gs, ("
				+ getTaxonIdsInTaxonSQL("?") + ") tx " + "	where gs.ncbi_tax_id = tx.ncbi_tax_id ";

		if (key.get("data_source").equalsIgnoreCase("RefSeq")) {
			sql += "	and gs.refseq = '1' ";
		}
		else if (key.get("data_source").equalsIgnoreCase("Legacy BRC")) {
			sql += "	and gs.brc = '1' ";
		}
		else if (key.get("data_source").equalsIgnoreCase("PATRIC")) {
			sql += "	and gs.rast = '1' ";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.addScalar("complete_cnt", Hibernate.INTEGER).addScalar("wgs_cnt", Hibernate.INTEGER).addScalar("plasmid_cnt", Hibernate.INTEGER)
				.addScalar("all_cnt", Hibernate.INTEGER);
		sqlQuery.setCacheable(true);

		sqlQuery.setString(0, key.get("ncbi_taxon_id"));
		List<?> rset = sqlQuery.list();
		session.getTransaction().commit();

		Object[] obj = null;
		ResultType results = new ResultType();
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			results.put("cnt_complete", obj[0]);
			results.put("cnt_wgs", obj[1]);
			results.put("cnt_plasmid", obj[2]);
			results.put("cnt_all", obj[3]);
		}
		return results;
	}

	/**
	 * Counts genomic features in each type for a given genome or taxon. This feeds "Genomic Feature Summary" portlet.
	 * 
	 * @param key filtering condition such as genome_info_id, ncbi_taxon_id, and view (<code>full|abbreviated</code>).
	 * @return genomic feature counts in RAST (patric), Legacy BRC (brc), and RefSeq (refseq) annotation.
	 */
	public ArrayList<ResultType> getNAFeatureSummary(HashMap<String, String> key) {
		String sql = "select name, sum(rast) patric, sum(brc) brc, sum(refseq) refseq from app.featuresummary " + "	where ";
		if (key.containsKey("genome_info_id")) {
			sql += "	genome_info_id = :genome_info_id ";
		}
		else if (key.containsKey("ncbi_taxon_id")) {
			sql += "	ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":ncbi_taxon_id") + ") ";
		}

		if (key.containsKey("view") && !key.get("view").equalsIgnoreCase("full")) {
			sql += " 	and name in ('CDS','mRNA','ncRNA','rRNA','tRNA','tmRNA','misc_RNA') ";
		}
		sql += "	group by name order by name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();

		SQLQuery sqlQuery = session.createSQLQuery(sql);

		if (key.containsKey("ncbi_taxon_id")) {
			sqlQuery.setString("ncbi_taxon_id", key.get("ncbi_taxon_id"));
		}
		else if (key.containsKey("genome_info_id")) {
			sqlQuery.setString("genome_info_id", key.get("genome_info_id"));
		}

		sqlQuery.addScalar("name", Hibernate.STRING);
		sqlQuery.addScalar("patric", Hibernate.INTEGER).addScalar("brc", Hibernate.INTEGER).addScalar("refseq", Hibernate.INTEGER);
		sqlQuery.setCacheable(true);

		List<?> rset = sqlQuery.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("name", obj[0]);
			row.put("patric", obj[1]);
			row.put("brc", obj[2]);
			row.put("refseq", obj[3]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves unique sequence_status_name for a given condition. This is used to feed filtering options.
	 * 
	 * @param key filtering condition such as genome_info_id or ncbi_taxon_id
	 * @return list of sequence status name
	 */
	public ArrayList<String> getListOfUniqueSequenceStatusNames(HashMap<String, String> key) {
		String sql = "select unique ssn.sequence_status_name " + "	from cas.genomeinfo gi, cas.sequenceinfo si, cas.sequencestatusname ssn "
				+ "	where gi.genome_info_id = si.genome_info_id " + "		and si.sequence_status_name_id = ssn.sequence_status_name_id ";

		if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			sql += " and gi.genome_info_id = :genome_info_id ";
		}
		else if (key.containsKey("ncbi_taxon_id") && key.get("ncbi_taxon_id") != null) {
			sql += " and gi.ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":ncbi_taxon_id") + ")";
		}
		sql += " order by sequence_status_name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("sequence_status_name", Hibernate.STRING).setCacheable(true);

		if (key.containsKey("genome_info_id")) {
			q.setString("genome_info_id", key.get("genome_info_id"));
		}
		else if (key.containsKey("ncbi_taxon_id")) {
			q.setString("ncbi_taxon_id", key.get("ncbi_taxon_id"));
		}

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<String> results = new ArrayList<String>();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			results.add((String) iter.next());
		}
		return results;
	}

	/**
	 * Retrieves unique feature_type given condition. This is used to feed filtering options.
	 * 
	 * @param key filtering condition such as ncbi_taxon_id or gneome_info_id
	 * @return list of feature type
	 */
	public ArrayList<String> getListOfFeatureTypes(HashMap<String, String> key) {
		String sql = "SELECT unique name from app.featuresummary ";

		if (key.containsKey("ncbi_taxon_id") && key.get("ncbi_taxon_id") != null) {
			sql += "	where ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":ncbi_taxon_id") + ")";
		}
		else if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			sql += "	where genome_info_id = :genome_info_id ";
		}

		sql += " order by name";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("name", Hibernate.STRING).setCacheable(true);

		if (key.containsKey("ncbi_taxon_id")) {
			q.setString("ncbi_taxon_id", key.get("ncbi_taxon_id"));
		}
		else if (key.containsKey("genome_info_id")) {
			q.setString("genome_info_id", key.get("genome_info_id"));
		}

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<String> results = new ArrayList<String>();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			results.add((String) iter.next());
		}
		return results;
	}

	/**
	 * Returns sub-SQL based on sequence_info_id to build a hierarchical query for a given taxon.
	 * 
	 * @param id ncbi_taxon_id
	 * @return sub-SQL string
	 */
	public static String getSequenceIdsInTaxonSQL(String id) {
		String sql = "select distinct si.sequence_info_id " + "	from ( " + "		select ncbi_tax_id from sres.taxon "
				+ "			connect by prior taxon_id = parent_id " + "			start with ncbi_tax_id = " + id + ") lng, "
				+ "	 cas.genomeinfo gi, cas.sequenceinfo si " + "	where lng.ncbi_tax_id = gi.ncbi_tax_id "
				+ "		and gi.genome_info_id = si.genome_info_id";
		return sql;
	}

	/**
	 * Return sub-SQL based on taxon_id to build a hierarchical query for a given taxon.
	 * 
	 * @param id ncbi_taxon_id
	 * @return sub-SQL string
	 */
	public static String getTaxonIdsInTaxonSQL(String id) {
		String sql = "select ncbi_tax_id from sres.taxon " + "connect by prior taxon_id = parent_id " + "start with ncbi_tax_id = " + id;
		return sql;
	}

	// This is private function for getGeneTable & getCountGeneTable
	private String getFeaturetableSQL(HashMap<String, String> key, HashMap<String, String> sort, boolean isCount) {
		String sqlstr = "";

		if (isCount) {
			sqlstr += "select count(*) as cnt ";
		}
		else {

			sqlstr += "select /*+ FIRST_ROWS */ nf.genome_info_id, nf.genome_name, nf.accession, nf.na_feature_id, nf.na_sequence_id, "
					+ "			nf.name, nf.source_id as locus_tag, "
					+ "				decode(nf.algorithm,'Curation','Legacy BRC','RAST','PATRIC','RefSeq') as algorithm, "
					+ "				decode(nf.is_reversed,1,'-','+') as strand, nf.debug_field, "
					+ "			nf.start_min, nf.start_max, nf.end_min, nf.end_max, nf.na_length, "
					+ "			nf.product, nf.gene, nf.aa_length, nf.is_pseudo, nf.bound_moiety, " + "			nf.anticodon, nf.protein_id ";

		}
		sqlstr += "	from app.dnafeature nf where 1=1 ";
		// scope
		if (key.containsKey("feature_info_id") && key.get("feature_info_id") != null) {
			sqlstr += "	and nf.na_feature_id in (" + key.get("feature_info_id") + ") ";
		}
		else if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			sqlstr += "	and nf.genome_info_id = :scope";
		}
		else if (key.containsKey("ncbi_taxon_id") && key.get("ncbi_taxon_id") != null) {
			if (key.get("ncbi_taxon_id").equals("2") && key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
				// skip
			}
			else {
				sqlstr += "	and nf.ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":scope") + ") ";
			}
		}
		else {
			sqlstr += " and nf.na_feature_id = 1";
		}
		// feature type
		if (key.containsKey("featuretype") && key.get("featuretype") != null) {
			if (key.get("featuretype").equalsIgnoreCase("all")) {
				// no condition
			}
			else {
				sqlstr += "	and nf.name = :featuretype ";
			}
		}
		// annotation
		if (key.containsKey("annotation") && key.get("annotation") != null && !key.get("annotation").equals("")
				&& !key.get("annotation").equals("ALL")) {
			sqlstr += "	and nf.algorithm = :annotation ";
		}

		// sequence status
		if (key.containsKey("sequencestatus") && key.get("sequencestatus") != null && !key.get("sequencestatus").equals("All")) {
			sqlstr += "	and nf.sequence_status = :sequencestatus ";
		}

		if (!isCount) {
			// sorting
			if (sort != null && sort.containsKey("field") && sort.get("field") != null) {
				sqlstr += " order by " + sort.get("field") + " " + sort.get("direction");
			}
			else {
				sqlstr += " order by nf.genome_name asc, nf.na_sequence_id, nf.start_max asc";
			}
		}
		return sqlstr;
	}

	// This is private function for getGeneTable & getCountGeneTable
	private SQLQuery bindFeaturetableValues(SQLQuery q, HashMap<String, String> key) {
		boolean debug = false;
		// scope
		if (key.containsKey("feature_info_id") && key.get("feature_info_id") != null) {
			// q.setString("scope", key.get("feature_info_id"));
			// there is no easy way to bind multiple values
			if (debug) {
				System.out.println("scope: feature_info_id = " + key.get("feature_info_id"));
			}
		}
		else if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			q.setString("scope", key.get("genome_info_id"));
			if (debug) {
				System.out.println("scope: genome_info_id = " + key.get("genome_info_id"));
			}
		}
		else if (key.containsKey("ncbi_taxon_id") && key.get("ncbi_taxon_id") != null) {
			if (key.get("ncbi_taxon_id").equals("2") && key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
				// skip
			}
			else {
				q.setString("scope", key.get("ncbi_taxon_id"));
			}
			if (debug) {
				System.out.println("scope: ncbi_taxon_id = " + key.get("ncbi_taxon_id"));
			}
		}

		// feature type
		if (key.containsKey("featuretype") && key.get("featuretype") != null) {
			if (key.get("featuretype").equalsIgnoreCase("all")) {
				// no condition for this case
				if (debug) {
					System.out.println("feature type: all ");
				}
			}
			else if (key.get("featuretype") != null) {
				q.setString("featuretype", key.get("featuretype"));
				if (debug) {
					System.out.println("feature type: " + key.get("featuretype"));
				}
			}
			else {
				q.setString("featuretype", "CDS");
				if (debug) {
					System.out.println("feature type: CDS (default)");
				}
			}
		}

		// annotation
		if (key.containsKey("annotation") && key.get("annotation") != null) {
			if (key.get("annotation").equalsIgnoreCase("RefSeq")) {
				q.setString("annotation", "RefSeq");
			}
			else if (key.get("annotation").equalsIgnoreCase("Legacy BRC")) {
				q.setString("annotation", "Curation");
			}
			else if (key.get("annotation").equalsIgnoreCase("PATRIC")) {
				q.setString("annotation", "RAST");
			}
			if (debug) {
				System.out.println("annotation: " + key.get("annotation"));
			}
		}

		// sequence status
		if (key.containsKey("sequencestatus") && key.get("sequencestatus") != null && !key.get("sequencestatus").equals("All")) {
			q.setString("sequencestatus", key.get("sequencestatus"));
			if (debug) {
				System.out.println("sequence status:" + key.get("sequence_status"));
			}
		}

		return q;
	}

	/**
	 * Retrieves features for a given condition.
	 * 
	 * @param key filtering condition
	 * @param sort sorting condition
	 * @param start starting pointer
	 * @param end stopping pointer. If <code>-1</code>, returns all the results.
	 * @return list of features
	 */
	public ArrayList<ResultType> getGenetable(HashMap<String, String> key, HashMap<String, String> sort, int start, int end) {
		String sql = getFeaturetableSQL(key, sort, false);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q = bindFeaturetableValues(q, key);
		if (end > 0) {
			q.setMaxResults(end);
		}
		q.setTimeout(300);
		ScrollableResults scr = null;
		if (key.containsKey("keyword") && key.get("keyword") != null && !key.get("keyword").equals("")) {
			// to collect keywords that users are interested
			System.out.println("[Keyword Collection(featuretable):" + key.toString() + "]");
		}
		try {
			scr = q.scroll();
		}
		catch (Exception ex) {
			System.out.println("[SQL error]" + key.toString());
			ex.printStackTrace();
			return null;
		}
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
			row.put("protein_id", obj[21]);

			results.add(row);
		}
		// scr.close();
		session.getTransaction().commit();

		return results;
	}

	/**
	 * Counts features for a given condition.
	 * 
	 * @param key filtering condition
	 * @return feature count
	 */
	public int getCountGenetable(HashMap<String, String> key) {
		String sql = getFeaturetableSQL(key, null, true);

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q = bindFeaturetableValues(q, key);
		q.setTimeout(300);
		q.setCacheable(true);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Counts CDS as a protein level summary for a given genome or taxon. This feeds "Protein Feature Summary" portlet.
	 * 
	 * @param key filtering condition such as genome_info_id or ncbi_taxon_id
	 * @return protein level summary.
	 * @deprecated
	 */
	public ArrayList<ResultType> getProteinFeatureSummary(HashMap<String, String> key) {
		String sql = "";
		if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			sql = " select attribute, order_by_att, rast, brc, refseq " + "		from app.proteinsummary " + "		where genome_info_id = :genome_info_id "
					+ "		order by order_by_att";
		}
		else if (key.containsKey("ncbi_taxon_id") && key.get("ncbi_taxon_id") != null) {
			sql = "	select attribute, order_by_att, nvl(sum(rast),0) rast, nvl(sum(brc),0) brc, nvl(sum(refseq),0) refseq "
					+ "		from app.proteinsummary " + "		where ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":ncbi_taxon_id") + ") "
					+ "		group by attribute, order_by_att " + "		order by order_by_att";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("attribute", Hibernate.STRING).addScalar("order_by_att", Hibernate.STRING);
		q.addScalar("rast", Hibernate.INTEGER).addScalar("brc", Hibernate.INTEGER).addScalar("refseq", Hibernate.INTEGER);
		q.setCacheable(true);

		if (key.containsKey("genome_info_id")) {
			q.setString("genome_info_id", key.get("genome_info_id"));
		}
		else if (key.containsKey("ncbi_taxon_id")) {
			q.setString("ncbi_taxon_id", key.get("ncbi_taxon_id"));
		}

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("filtertype", obj[0]);
			row.put("order", obj[1]);
			row.put("rast", obj[2]);
			row.put("brc", obj[3]);
			row.put("refseq", obj[4]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves RNA detail info. This query on app.dnafeature and dots.nafeaturecomment tables.
	 * 
	 * @param id na_feature_id
	 * @return RNA info (na_feature_id, gene, label, anticodon, product, comment_string)
	 */
	public ResultType getRNAInfo(String id) {
		String sql = "select nf.na_feature_id, nf.gene, nf.label, nf.anticodon, nf.product, nfc.comment_string "
				+ "	from app.dnafeature nf, dots.nafeaturecomment nfc " + "	where nf.na_feature_id = ? "
				+ "		and nf.na_feature_id = nfc.na_feature_id(+)";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType result = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			result.put("na_feature_id", obj[0]);
			result.put("gene", obj[1]);
			result.put("label", obj[2]);
			result.put("anticodon", obj[3]);
			result.put("product", obj[4]);

			try {
				SerializableClob clobComment = (SerializableClob) obj[5];
				String strComment = IOUtils.toString(clobComment.getAsciiStream(), "UTF-8");
				result.put("comment_string", strComment);
			}
			catch (NullPointerException ex) {
				// this can be null
			}
			catch (Exception ex) {
				System.out.println("Problem in retrieving comments for RNA: " + ex.toString());
			}
		}
		return result;
	}

	/**
	 * Retrieves Gene detail info. This query on app.dnafeature table.
	 * 
	 * @param id na_feature_id
	 * @return Gene info (na_feature_id, gene)
	 */
	public ResultType getGeneInfo(String id) {
		String sql = "select nf.na_feature_id, nf.gene from app.dnafeature nf " + "		where nf.na_feature_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType result = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			result.put("na_feature_id", obj[0]);
			result.put("gene", obj[1]);
		}
		return result;
	}

	/**
	 * Retrieves PATRIC-RefSeq mapping info. This query on app.patricrefseqmapping table.
	 * 
	 * @param annotation annotation source. <code>PATRIC|RefSeq</code>
	 * @param id na_feature_id. This can be either patric_na_feature_id or refseq_na_feature_id.
	 * @return patric_refseq mapping info
	 */
	public ResultType getRefSeqInfo(String annotation, String id) {
		String sql = "SELECT prm.patric_na_feature_id, prm.patric_source_id, prm.feature, prm.patric_start, prm.patric_end, prm.patric_strand, "
				+ "		prm.refseq_na_feature_id, prm.refseq_source_id, prm.refseq_start, prm.refseq_end, prm.refseq_strand, "
				+ "		prm.protein_id, prm.gi_number, prm.gene_id " + "	FROM app.patricrefseqmapping prm ";
		if (annotation.equals("PATRIC")) {
			sql += "	WHERE prm.patric_na_feature_id = ?";
		}
		else {
			sql += "	WHERE prm.refseq_na_feature_id = ?";
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType result = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			result.put("patric_na_feature_id", obj[0]);
			result.put("patric_locus_tag", obj[1]);
			result.put("feature_type", obj[2]);
			result.put("patric_start", obj[3]);
			result.put("patric_end", obj[4]);
			result.put("patric_strand", obj[5]);
			result.put("refseq_na_feature_id", obj[6]);
			result.put("refseq_locus_tag", obj[7]);
			result.put("refseq_start", obj[8]);
			result.put("refseq_end", obj[9]);
			result.put("refseq_strand", obj[10]);
			result.put("protein_id", obj[11]);
			result.put("gi_number", obj[12]);
			result.put("gene_id", obj[13]);
		}
		return result;
	}

	public ResultType getRefSeqInfo(String id) {
		String sql = "select gi.gene_id, gn.gi gi_number " + "		from app.dnafeature df, dots.gene_id gi, dots.gi_number gn "
				+ "		where df.na_feature_id = gi.na_feature_id (+) " + "			and df.na_feature_id = gn.na_feature_id (+) "
				+ "			and df.na_feature_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType result = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			result.put("gene_id", obj[0]);
			result.put("gi_number", obj[1]);
		}
		return result;
	}

	// Genome Browser SQLs
	/**
	 * Retrieves sequences for a given condition This is used for Genome Browser
	 * 
	 * @param key filtering condition such as genoem_info_id or feature_info_id
	 * @return sequence info (sequence_info_id, na_sequence_id, accession, length)
	 */
	public ArrayList<ResultType> getRefSeqs(HashMap<String, String> key) {
		String sql = "";
		if (key.containsKey("genome_info_id") && key.get("genome_info_id") != null) {
			sql = "	select sequence_info_id, na_sequence_id, accession, length " + "	from cas.sequenceinfo " + "	where genome_info_id = "
					+ key.get("genome_info_id") + " order by accession ";
		}
		else if (key.containsKey("feature_info_id") && key.get("feature_info_id") != null) {
			sql = "	select si.sequence_info_id, si.na_sequence_id, si.accession, si.length " + "	from app.dnafeature nf, cas.sequenceinfo si "
					+ "	where nf.sequence_info_id = si.sequence_info_id and na_feature_id = " + key.get("feature_info_id");
		}

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("sequence_info_id", obj[0]);
			row.put("na_sequence_id", obj[1]);
			row.put("accession", obj[2]);
			row.put("length", obj[3]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves feature info for a given condition. This is used to feed Genome Browser track.
	 * 
	 * @param key filtering condition such as accession, feature type, and annotation
	 * @return list of feature level data (na_feature_id, locus_tag, p_start, p_end, is_reversed, name, debug, product, gene)
	 */
	public ArrayList<ResultType> getFeatures(HashMap<String, String> key) {

		// query by sequence_info_id (if available) or accession
		String q = "";
		if (key.containsKey("sid") && key.get("sid") != null) {
			q += "sequence_info_id:" + key.get("sid");
		}
		else {
			q += "accession:\"" + key.get("accession") + "\"";
		}
		// filter by annotation and feature type (if provided)
		String fq = "annotation:" + key.get("algorithm");
		if (key.containsKey("type")) {
			fq += " AND feature_type:" + key.get("type");
		}
		fq += " AND !(feature_type:source)";

		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFilterQueries(fq);

		return getGenomicFeaturesFromSolr(query);
		// return getGenomicFeaturesFromSolrStream(query);
		// return getGenomicFeaturesFromSolrBatch(query);
	}

	public List<DNAFeature> getDNAFeatures(HashMap<String, String> key) {

		// query by sequence_info_id (if available) or accession
		String q = "";
		if (key.containsKey("sid") && key.get("sid") != null) {
			q += "sequence_info_id:" + key.get("sid");
		}
		else {
			q += "accession:\"" + key.get("accession") + "\"";
		}
		// filter by annotation and feature type (if provided)
		String fq = "annotation:" + key.get("algorithm");
		if (key.containsKey("type")) {
			fq += " AND feature_type:" + key.get("type");
		}
		fq += " AND !(feature_type:source)";

		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFilterQueries(fq);

		return getGenomicFeaturesFromSolrBean(query);
		// return getGenomicFeaturesFromSolrBeanBatch(query);
	}

	public ArrayList<ResultType> streamResponse(SolrQuery query) {
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		SolrInterface solr = new SolrInterface();
		final BlockingQueue<SolrDocument> tmpQueue = new LinkedBlockingQueue<SolrDocument>();
		try {
			solr.setCurrentInstance("GenomicFeature");

			long tp1 = System.currentTimeMillis();
			solr.getServer().queryAndStreamResponse(query, new StreamCallbackHandler(tmpQueue));
			long tp2 = System.currentTimeMillis();
			System.out.println("first query: " + (tp2 - tp1) + " ms");

			SolrDocument tmpDoc;
			do {
				tmpDoc = tmpQueue.take();
				ResultType row = new ResultType();
				row.putAll(tmpDoc);
				if (row.isEmpty() == false) {
					results.add(row);
				}
			} while (!tmpDoc.isEmpty());

			long tp3 = System.currentTimeMillis();
			System.out.println("fetching all: " + (tp3 - tp2) + " ms, totalRows=" + results.size());
		}
		catch (SolrServerException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return results;
	}

	public ArrayList<ResultType> getGenomicFeaturesFromSolrBatch(SolrQuery query) {
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		SolrInterface solr = new SolrInterface();
		int fetchSize = 5000;
		long offset = 0;
		long totalResults = 0;

		query.setFields("na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag");

		try {
			solr.setCurrentInstance("GenomicFeature");

			// first query to get totalRows
			long tp1 = System.currentTimeMillis();
			query.setRows(1);
			totalResults = solr.getServer().query(query).getResults().getNumFound();
			long tp2 = System.currentTimeMillis();
			System.out.println("first query: " + (tp2 - tp1) + " ms, totalRows=" + totalResults);

			// fetching
			query.setSort("start_max", SolrQuery.ORDER.asc);
			long tp3 = System.currentTimeMillis();
			while (offset < totalResults) {

				tp1 = System.currentTimeMillis();
				// //
				query.setStart((int) offset);
				query.setRows(fetchSize);
				for (SolrDocument doc : solr.getServer().query(query).getResults()) {
					ResultType row = new ResultType();
					row.putAll(doc);
					results.add(row);
				}
				// //
				tp2 = System.currentTimeMillis();
				System.out.println("offset=" + offset + ": " + (tp2 - tp1) + " ms");

				offset += fetchSize;
			}

			long tp4 = System.currentTimeMillis();
			System.out.println("fetching all: " + (tp4 - tp3) + " ms");
		}
		catch (MalformedURLException | SolrServerException e) {
			e.printStackTrace();
		}
		return results;
	}

	public List<DNAFeature> getGenomicFeaturesFromSolrBeanBatch(SolrQuery query) {
		List<DNAFeature> beans = new ArrayList<DNAFeature>();
		SolrInterface solr = new SolrInterface();
		int fetchSize = 800;
		long offset = 0;
		long totalResults = 0;

		query.setFields("na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag");

		try {
			solr.setCurrentInstance("GenomicFeature");

			// first query to get totalRows
			long tp1 = System.currentTimeMillis();
			query.setRows(1);
			totalResults = solr.getServer().query(query).getResults().getNumFound();
			long tp2 = System.currentTimeMillis();
			System.out.println("first query: " + (tp2 - tp1) + " ms, totalRows=" + totalResults);

			// fetching
			query.setSort("start_max", SolrQuery.ORDER.asc);
			long tp3 = System.currentTimeMillis();
			while (offset < totalResults) {

				tp1 = System.currentTimeMillis();

				query.setStart((int) offset);
				query.setRows(fetchSize);
				QueryResponse rsp = solr.getServer().query(query);
				beans.addAll(rsp.getBeans(DNAFeature.class));

				tp2 = System.currentTimeMillis();
				System.out.println("offset=" + offset + ": " + (tp2 - tp1) + " ms");

				offset += fetchSize;
			}

			long tp4 = System.currentTimeMillis();
			System.out.println("fetching all: " + (tp4 - tp3) + " ms");
		}
		catch (MalformedURLException | SolrServerException e) {
			e.printStackTrace();
		}
		return beans;
	}

	public ArrayList<ResultType> getGenomicFeaturesFromSolrStream(SolrQuery query) {
		query.setRows(10000);
		query.setSort("start_max", SolrQuery.ORDER.asc);
		query.setFields("na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag");

		return streamResponse(query);
	}

	public List<DNAFeature> getGenomicFeaturesFromSolrBean(SolrQuery query) {
		SolrInterface solr = new SolrInterface();
		List<DNAFeature> beans = null;

		try {
			solr.setCurrentInstance("GenomicFeature");
			query.setSort("start_max", SolrQuery.ORDER.asc);
			query.setFields("na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag");

			// 1st q. get total rows
			long tp1 = System.currentTimeMillis();

			query.setRows(1);
			long totalResults = solr.getServer().query(query).getResults().getNumFound();

			long tp2 = System.currentTimeMillis();
			System.out.println("first query: " + (tp2 - tp1) + " ms");

			// long totalResults = 10000;
			// 2nd Q. fetch
			long tp3 = System.currentTimeMillis();

			query.setRows((int) totalResults);
			QueryResponse rsp = solr.getServer().query(query);

			long tp4 = System.currentTimeMillis();
			System.out.println("2nd query: " + (tp4 - tp3) + " ms");

			// fetch
			long tp5 = System.currentTimeMillis();
			beans = rsp.getBeans(DNAFeature.class);
			long tp6 = System.currentTimeMillis();
			System.out.println("fetching all: " + (tp6 - tp5) + " ms, totalRows=" + beans.size());
		}
		catch (MalformedURLException | SolrServerException e) {
			e.printStackTrace();
		}

		return beans;
	}

	public ArrayList<ResultType> getGenomicFeaturesFromSolr(SolrQuery query) {
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		SolrInterface solr = new SolrInterface();

		// common settings
		query.setRows(10000);
		query.setSort("start_max", SolrQuery.ORDER.asc);
		query.setFields("na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag");

		QueryResponse rsp = null;

		try {
			solr.setCurrentInstance("GenomicFeature");
			long tp1 = System.currentTimeMillis();
			rsp = solr.getServer().query(query);
			long tp2 = System.currentTimeMillis();
			System.out.println("first query: " + (tp2 - tp1) + " ms");
		}
		catch (MalformedURLException | SolrServerException e) {
			e.printStackTrace();
		}
		long tp3 = System.currentTimeMillis();
		SolrDocumentList docs = rsp.getResults();
		for (Iterator<SolrDocument> iter = docs.iterator(); iter.hasNext();) {
			SolrDocument obj = iter.next();
			ResultType row = new ResultType();
			row.putAll(obj);
			results.add(row);
		}
		long tp4 = System.currentTimeMillis();
		System.out.println("fetching all: " + (tp4 - tp3) + " ms, totalRows=" + results.size());

		return results;
	}

	public ArrayList<Integer> getHistogram(HashMap<String, String> key) {

		String q = "accession:" + key.get("accession") + " AND sequence_info_id:" + key.get("sid");
		String fq = "annotation:" + key.get("algorithm");
		if (key.containsKey("type")) {
			fq += " AND feature_type:" + key.get("type");
		}
		fq += " AND !(feature_type:source)";

		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFilterQueries(fq);
		query.setRows(0);
		query.setFacet(true);
		query.setFacetMinCount(1);
		query.addNumericRangeFacet("start_max", 0, 10000000, 10000);
		// System.out.println("query:"+query.toString());
		ArrayList<Integer> results = new ArrayList<Integer>();
		SolrInterface solr = new SolrInterface();
		QueryResponse qr = null;
		try {
			solr.setCurrentInstance("GenomicFeature");
			qr = solr.getServer().query(query);

			for (RangeFacet<?, ?> range : qr.getFacetRanges()) {
				List<RangeFacet.Count> rangeEntries = range.getCounts();
				if (rangeEntries != null) {
					for (RangeFacet.Count fcount : rangeEntries) {
						results.add(fcount.getCount());
					}
				}
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Counts features that match the given genomeID. This is used for CompareReginoViewer to decide whether there are features matching to a given
	 * PSEED genome ID.
	 * 
	 * @param id PSEED genome ID
	 * @return feature count
	 */
	public int getPSeedGenomeCount(String id) {
		String sql = "SELECT count(*) FROM app.dnafeature WHERE pseed_id like 'fig|" + id + ".peg.%' ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	/**
	 * Retrieves features that can be mapped by PATRIC feature ID (na_feature_id) or PSEED peg ID. This is used for CompareRegionViewer to map
	 * features each other.
	 * 
	 * @param src ID source. <code>PATRIC|PSEED</code>
	 * @param IDs IDs
	 * @return list of features (na_feature_id, pseed_id, source_id, start, end, strand, na_length, aa_length, product, genome_name, accession)
	 */
	public HashMap<String, ResultType> getPSeedMapping(String src, String IDs) {

		HashMap<String, ResultType> result = new HashMap<String, ResultType>();
		SolrInterface solr = new SolrInterface();

		String q = null;
		if (src.equalsIgnoreCase("PATRIC")) {
			q = "na_feature_id:(" + IDs + ")";
		}
		else {
			q = "pseed_id:(" + IDs + ")";
		}

		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFields("na_feature_id,pseed_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag,genome_name,accession");
		query.setRows(1000);
		QueryResponse rsp = null;

		// System.out.println(query.toString());

		try {
			solr.setCurrentInstance("GenomicFeature");
			rsp = solr.getServer().query(query);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}

		SolrDocumentList docs = rsp.getResults();
		for (Iterator<SolrDocument> iter = docs.iterator(); iter.hasNext();) {
			SolrDocument obj = iter.next();

			ResultType row = new ResultType();
			row.putAll(obj);
			if (src.equalsIgnoreCase("PATRIC")) {
				result.put(row.get("na_feature_id"), row);
			}
			else {
				result.put(row.get("pseed_id"), row);
			}
		}
		return result;
	}

	public HashMap<String, ResultType> getGenomeMetadata(HashSet<String> genomeNames) {

		HashMap<String, ResultType> result = new HashMap<String, ResultType>();
		SolrInterface solr = new SolrInterface();

		StringBuilder sb = new StringBuilder();
		for (String name : genomeNames) {
			if (sb.length() > 0) {
				sb.append(" OR ");
			}
			sb.append("\"" + name + "\"");
		}
		if (sb.length() == 0) {
			return null;
		}

		String q = "genome_name:(" + sb.toString() + ")";

		SolrQuery query = new SolrQuery();
		query.setQuery(q);
		query.setFields("genome_info_id,genome_name,isolation_country,host_name,disease,collection_date,completion_date");
		QueryResponse rsp = null;
		// System.out.println(query.toString());

		try {
			solr.setCurrentInstance("GenomeFinder");
			rsp = solr.getServer().query(query);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}

		SolrDocumentList docs = rsp.getResults();
		for (Iterator<SolrDocument> iter = docs.iterator(); iter.hasNext();) {
			SolrDocument obj = iter.next();
			ResultType row = new ResultType();
			row.putAll(obj);
			if (obj.get("completion_date") != null) {
				row.put("completion_date", solr.transformDate((Date) obj.get("completion_date")));
			}
			else {
				row.put("completion_date", "");
			}
			result.put(row.get("genome_name"), row);
		}
		return result;
	}

	// / End of Genome Browser SQLs

	/**
	 * Finds taxonomy rank "Order" either below the given taxon or within the ancestor of the given taxon.
	 * 
	 * @param ncbi_taxon_id integer type of NCBI Taxonomy ID
	 * @return list of Order level taxonomy info
	 */
	public ArrayList<ResultType> getOrderInTaxonomy(int ncbi_taxon_id) {
		if (ncbi_taxon_id <= 0)
			return new ArrayList<ResultType>();

		String sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
				+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
				+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name' " + "	) lng, cas.ncbiclassification cls "
				+ "	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and cls.rank = 'order' "
				+ "		and cls.ncbi_taxon_id in (2037,1385,80840,213849,51291,186802,91347,186826,118969,356,766,136,72273,135623) "
				+ "	connect by prior parent_id = taxon_id " + "	start with ncbi_tax_id = :ncbi_taxon_id";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setInteger("ncbi_taxon_id", ncbi_taxon_id);

		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
		q.addScalar("node_level", Hibernate.INTEGER);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		if (rset.size() == 0) {
			// TODO: work on this sql. Need some performance improvement.
			sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
					+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
					+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name' " + "	) lng, cas.ncbiclassification cls "
					+ "	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and cls.rank = 'order' "
					+ "		and ncbi_tax_id in (2037,1385,80840,213849,51291,186802,91347,186826,118969,356,766,136,72273,135623) "
					+ "	connect by prior taxon_id = parent_id " + "	start with ncbi_tax_id = :ncbi_taxon_id " + "	order by name";
			session = factory.getCurrentSession();
			session.beginTransaction();
			q = session.createSQLQuery(sql);
			q.setInteger("ncbi_taxon_id", ncbi_taxon_id);

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
			// row.put("node_level", obj[3]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Identifies species that match PRIDE database for a given taxon. This is used for Experiment data API call.
	 * 
	 * @param id ncbi_tax_id
	 * @return list of species name
	 */
	public String getPRIDESpecies(String id) {
		String sql = "select pr.species, pr.ncbi_tax_id " + "	from app.pride pr, (" + getTaxonIdsInTaxonSQL(":ncbi_taxon_id") + ") tx "
				+ "	where pr.ncbi_tax_id = tx.ncbi_tax_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("ncbi_taxon_id", id);

		List<?> rset = q.list();
		session.getTransaction().commit();

		StringBuilder results = new StringBuilder();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			if (results.length() > 0) {
				results.append("," + obj[0].toString());
			}
			else {
				results.append(obj[0].toString());
			}
		}

		return results.toString();
	}

	/**
	 * Retrieves UniprotAccession for a given feature. This is used for link out.
	 * 
	 * @param id na_feature_id
	 * @return Uniprot info (uniprotkb_accession, id_type, id)
	 */
	public ArrayList<ResultType> getUniprotAccession(String id) {
		String sql = "select uniprotkb_accession, id_type, id " + "	from app.idmapping " + "	where uniprotkb_accession in ( "
				+ "		select uniprotkb_accession " + "		from app.idmapping idm, app.patricrefseqmapping prm " + "		where idm.id_type = 'GI' "
				+ "			and idm.id = to_char(prm.gi_number) " + "			and prm.patric_na_feature_id = ? )";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setString(0, id);

		sqlQuery.addScalar("uniprotkb_accession", Hibernate.STRING).addScalar("id_type", Hibernate.STRING).addScalar("id", Hibernate.STRING);
		sqlQuery.setCacheable(true);

		List<?> rset = sqlQuery.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("uniprotkb_accession", obj[0]);
			row.put("id_type", obj[1]);
			row.put("id", obj[2]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves BEIR Clone info for a given feature. This is used for link out.
	 * 
	 * @param id na_feature_id
	 * @return clone info (beir_clone_id, clone_name)
	 */
	public ArrayList<ResultType> getBEIRClones(String id) {
		String sql = "select sc.beirclone_id, sc.clone_name " + "	from structure.beir_clone sc, structure.gene sg, app.patricrefseqmapping mp "
				+ "	where sc.beirclone_id = sg.beirclone_id " + "		and ( (lower(id_type) like '%geneid%' and geneidentifier = mp.gene_id) "
				+ "			or (id_type = 'RefSeq' and geneidentifier = protein_id) ) " + "		and mp.feature = 'CDS' "
				+ "		and mp.patric_na_feature_id = :na_feature_id " + "	union " + "	select sc.beirclone_id, sc.clone_name "
				+ "	from structure.beir_clone sc, structure.gene sg, app.idmapping im, app.patricrefseqmapping mp "
				+ "	where sc.beirclone_id = sg.beirclone_id " + "		and sg.id_type = 'UniProt' " + "		and sg.geneidentifier = im.uniprotkb_accession "
				+ "		and im.id_type = 'GI' " + "		and im.id = to_char(mp.gi_number) " + "		and mp.patric_na_feature_id = :na_feature_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("na_feature_id", id);

		q.addScalar("beirclone_id", Hibernate.STRING).addScalar("clone_name", Hibernate.STRING);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("beir_clone_id", obj[0]);
			row.put("clone_name", obj[1]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves BEIR Clone info for a given PDB. This is used for link out.
	 * 
	 * @param id PDB ID
	 * @return clone info (beir_clone_id, clone_name)
	 */
	public ArrayList<ResultType> getBEIRClonesByPDB(String id) {
		String sql = "select sc.beirclone_id, sc.clone_name " + "		from structure.beir_clone sc, structure.gene sg "
				+ "		where sc.beirclone_id = sg.beirclone_id " + "			and lower(id_type) like '%geneid%' " + "			and geneidentifier in ( "
				+ "				select id from app.idmapping where id_type = 'GeneID' and uniprotkb_accession in ( "
				+ "					select uniprotkb_accession from app.idmapping where id_type='PDB' and id = :pdb_id " + "				) " + "			) " + "	union "
				+ "	select sc.beirclone_id, sc.clone_name " + "		from structure.beir_clone sc, structure.gene sg, app.idmapping im "
				+ "		where sc.beirclone_id = sg.beirclone_id " + "			and sg.id_type = 'UniProt' "
				+ "			and sg.geneidentifier = im.uniprotkb_accession " + "			and im.id_type = 'PDB' " + "			and im.id = :pdb_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("pdb_id", id);

		q.addScalar("beirclone_id", Hibernate.STRING).addScalar("clone_name", Hibernate.STRING);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("beir_clone_id", obj[0]);
			row.put("clone_name", obj[1]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Finds taxonomy rank "Genus" for a given taxon node.
	 * 
	 * @param id ncbi_taxon_id
	 * @return taxonomy info of genus
	 */
	public ResultType getGenusInTaxonomy(String id) {
		String sql = "select lng.ncbi_tax_id, lng.name, cls.rank from ( " + "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id "
				+ "		from sres.taxon a, sres.taxonname b " + "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name' "
				+ "	) lng, cas.ncbiclassification cls " + "	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and cls.rank = 'genus' "
				+ "	connect by prior parent_id = taxon_id start with ncbi_tax_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);

		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType result = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			result.put("ncbi_tax_id", obj[0]);
			result.put("name", obj[1]);
			result.put("rank", obj[2]);
		}
		return result;
	}

	/**
	 * Finds taxonomy rank "Genus" for a given taxon node.
	 * 
	 * @param id RefSeq Locus Tag
	 * @return comments
	 */
	public ArrayList<ResultType> getTBAnnotation(String id) {
		String sql = "select distinct locus_tag, property, value, evidence_code, comments, source" + "	from app.tbcap_annotation "
				+ "	where locus_tag = :refseq_locus_tag and property != 'Interaction'" + "	order by property asc, evidence_code asc ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString("refseq_locus_tag", id);

		q.addScalar("locus_tag", Hibernate.STRING).addScalar("property", Hibernate.STRING).addScalar("value", Hibernate.STRING);
		q.addScalar("evidence_code", Hibernate.STRING).addScalar("comments", Hibernate.STRING).addScalar("source", Hibernate.STRING);
		// q.setCacheable(true);

		List<?> rset = q.list();
		// session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("locus", obj[0]);
			row.put("property", obj[1]);
			row.put("value", obj[2]);
			row.put("evidencecode", obj[3]);
			row.put("comment", obj[4]);
			row.put("source", obj[5]);
			results.add(row);
		}

		// get Interactions
		sql = "select distinct locus_tag, property, value, evidence_code, comments, source" + "	from app.tbcap_annotation "
				+ "	where locus_tag = :refseq_locus_tag and property = 'Interaction' " + "	order by value asc, evidence_code asc ";

		// session = factory.getCurrentSession();
		// session.beginTransaction();
		q = session.createSQLQuery(sql);
		q.setString("refseq_locus_tag", id);

		q.addScalar("locus_tag", Hibernate.STRING).addScalar("property", Hibernate.STRING).addScalar("value", Hibernate.STRING);
		q.addScalar("evidence_code", Hibernate.STRING).addScalar("comments", Hibernate.STRING).addScalar("source", Hibernate.STRING);

		rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("locus", obj[0]);
			row.put("property", obj[1]);
			row.put("value", obj[2]);
			row.put("evidencecode", obj[3]);
			row.put("comment", obj[4]);
			row.put("source", obj[5]);
			results.add(row);
		}

		return results;
	}

	// Protein Family Landing page

	public ArrayList<ResultType> getFIGFamConservDist(int taxonId) {
		String sql = "select grp, count(*) cnt from " + "	(select a.name, ceil(a.gcnt/b.gcnt*10) grp from "
				+ "		(select name, count(distinct(genome_info_id)) gcnt " + "			from app.figfamsummary " + "			where ncbi_tax_id in ("
				+ getTaxonIdsInTaxonSQL(":taxonId") + ") " + "			group by name) a, " + "		(select count(genome_info_id) gcnt "
				+ "			from app.genomesummary " + "			where rast=1 and ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":taxonId") + ") " + "		) b " + "	) "
				+ "group by grp " + "order by grp";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setInteger("taxonId", taxonId);

		q.addScalar("grp", Hibernate.INTEGER).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("grp", obj[0]);
			row.put("cnt", obj[1]);
			results.add(row);
		}

		return results;
	}

	public ResultType getFIGFamStat(int taxonId) {
		ResultType result = new ResultType();
		String sql = "select count(distinct name) cnt from app.figfamsummary ffs where ncbi_tax_id in ( " + getTaxonIdsInTaxonSQL(":taxonId") + ")";

		// get total, hypothetical, and functional
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setInteger("taxonId", taxonId);
		q.addScalar("cnt", Hibernate.INTEGER).setCacheable(true);

		Object cnt = q.uniqueResult();
		int total = Integer.parseInt(cnt.toString());
		result.put("total", total);

		sql = "select count(distinct name) cnt from app.figfamsummary ffs "
				+ "		where lower(ffs.description) like '%hypothetical%' and ncbi_tax_id in ( " + getTaxonIdsInTaxonSQL(":taxonId") + ")";
		q = session.createSQLQuery(sql);
		q.setInteger("taxonId", taxonId);
		q.addScalar("cnt", Hibernate.INTEGER).setCacheable(true);

		cnt = q.uniqueResult();
		int hypothetical = Integer.parseInt(cnt.toString());
		result.put("hypotheticals", hypothetical);
		result.put("functional", (total - hypothetical));

		// get core vs accessory
		sql = "select count(*) cnt from (select a.name, ceil(a.gcnt/b.gcnt*10) grp from "
				+ "		(select name, count(distinct(genome_info_id)) gcnt from app.figfamsummary " + "			where ncbi_tax_id in ("
				+ getTaxonIdsInTaxonSQL(":taxonId") + ") group by name) a, " + "		(select count(genome_info_id) gcnt from app.genomesummary "
				+ "			where rast=1 and ncbi_tax_id in (" + getTaxonIdsInTaxonSQL(":taxonId") + ") ) b " + "	where a.gcnt = b.gcnt ) group by grp";
		q = session.createSQLQuery(sql);
		q.setInteger("taxonId", taxonId);
		q.addScalar("cnt", Hibernate.INTEGER).setCacheable(true);
		cnt = q.uniqueResult();
		int core = Integer.parseInt(cnt.toString());
		result.put("core", core);
		result.put("accessory", (total - core));

		session.getTransaction().commit();

		return result;
	}

	private class StreamCallbackHandler extends StreamingResponseCallback {
		private BlockingQueue<SolrDocument> queue;

		private long currentPosition;

		private long numFound;

		public StreamCallbackHandler(BlockingQueue<SolrDocument> aQueue) {
			queue = aQueue;
		}

		@Override
		public void streamDocListInfo(long aNumFound, long aStart, Float aMaxScore) {
			currentPosition = aStart;
			numFound = aNumFound;

			if (numFound == 0) {
				queue.add(new SolrDocument());
			}
		}

		@Override
		public void streamSolrDocument(SolrDocument aDoc) {
			currentPosition++;
			// System.out.println("adding doc " + currentPosition + " of " + numFound);
			queue.add(aDoc);
			if (currentPosition == numFound) {
				queue.add(new SolrDocument());
			}
		}
	}
}
