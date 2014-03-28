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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.lob.SerializableClob;

/**
 * <p>
 * An interface class for database queries that can be shared across PATRIC projects.
 * </p>
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 */
public class DBShared {
	protected static SessionFactory factory;

	/**
	 * Sets SessionFactory to internal variable so that each method can use later.
	 * @param sf SessionFactory
	 */
	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	/**
	 * Reads SessionFactory from the internal variable.
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return factory;
	}

	/**
	 * Retrieves taxonomy info of ancestors from a given taxon.
	 * 
	 * @param id ncbi_taxon_id
	 * @return taxonomy lineage (ncbi_tax_id, name, rank, node_level)
	 */
	public ArrayList<ResultType> getTaxonParentTree(String id) {
		String sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
				+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
				+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name') lng, cas.ncbiclassification cls "
				+ " 	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "	connect by prior parent_id = taxon_id " + "	start with ncbi_tax_id = ? ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
		q.addScalar("node_level", Hibernate.INTEGER);
		q.setCacheable(true);

		q.setString(0, id);
		List<?> rset = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();
		try {
			rset = q.list();
		}
		catch (Exception ex) {
			session.getTransaction().rollback();
			return results;
		}
		session.getTransaction().commit();

		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("name", obj[1]);
			row.put("rank", obj[2]);
			row.put("node_level", obj[3]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves taxonomy info of genus within ancestors.
	 * 
	 * @param ncbi_taxon_id Integer type of NCBI Taxnomy ID
	 * @return taxonomy info of corresponding genus (ncbi_tax_id, name, rank, node_level)
	 */
	public ArrayList<ResultType> getGenusInAncestors(int ncbi_taxon_id) {
		String sql = "select lng.ncbi_tax_id, lng.name, cls.rank, cls.node_level " + "	from ( "
				+ "		select a.taxon_id, a.ncbi_tax_id, b.name, a.parent_id " + "		from sres.taxon a, sres.taxonname b "
				+ "		where a.taxon_id = b.taxon_id and b.name_class = 'scientific name') lng, cas.ncbiclassification cls "
				+ " 	where lng.ncbi_tax_id = cls.ncbi_taxon_id " + "		and rank = 'genus' " + "	connect by prior parent_id = taxon_id "
				+ "	start with ncbi_tax_id = :ncbi_taxon_id ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("name", Hibernate.STRING).addScalar("rank", Hibernate.STRING);
		q.addScalar("node_level", Hibernate.INTEGER);
		q.setCacheable(true);

		q.setInteger("ncbi_taxon_id", ncbi_taxon_id);

		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("name", obj[1]);
			row.put("rank", obj[2]);
			row.put("node_level", obj[3]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves taxonomy info of given taxon.
	 * 
	 * @param id ncbi_taxon_id
	 * @return taxonomy info (ncbi_tax_id, name, unique_name_variant, name_class, ncbi_genetic_code_id, ncbi_genetic_code_name)
	 */
	public ArrayList<ResultType> getTaxonNames(String id) {
		String sql = "select tx.ncbi_tax_id, tx.rank, txname.name, txname.unique_name_variant, txname.name_class, "
				+ " 		 gcd.ncbi_genetic_code_id, gcd.name as ncbi_genetic_code_name "
				+ "	from sres.taxon tx, sres.taxonname txname, sres.geneticcode gcd " + "	where tx.taxon_id = txname.taxon_id "
				+ "		and tx.genetic_code_id = gcd.genetic_code_id " + "		and tx.ncbi_tax_id = ? " + "	order by name_class";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("rank", Hibernate.STRING).addScalar("name", Hibernate.STRING);
		q.addScalar("unique_name_variant", Hibernate.STRING).addScalar("name_class", Hibernate.STRING)
				.addScalar("ncbi_genetic_code_id", Hibernate.INTEGER);
		q.addScalar("ncbi_genetic_code_name", Hibernate.STRING);
		q.setCacheable(true);

		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("rank", obj[1]);
			row.put("name", obj[2]);
			row.put("unique_name_variant", obj[3]);
			row.put("name_class", obj[4]);
			row.put("ncbi_genetic_code_id", obj[5]);
			row.put("ncbi_genetic_code_name", obj[6]);
			results.add(row);
		}
		return results;
	}

	public ArrayList<ResultType> getGenomesBelowTaxon(String ncbi_tax_id) {

		String sql = "select genome_info_id, genome_name " + " from app.genomesummary where ncbi_tax_id in ( " + " select ncbi_tax_id "
				+ " from sres.taxon " + " connect by prior taxon_id = parent_id start with ncbi_tax_id = ?) ";

		Object[] obj = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, ncbi_tax_id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();

			ResultType row = new ResultType();

			row.put("genome_info_id", obj[0]);

			results.add(row);
		}
		return results;

	}

	/**
	 * Retrieves taxonomy info of given taxon.
	 * 
	 * @param ncbi_taxon_id Integer NCBI Taxonomy ID
	 * @return taxonomy info (taxonomy id, rank, class name, etc)
	 */
	public ResultType getNamesFromTaxonId(int ncbi_taxon_id) {
		String sql = "select tx.ncbi_tax_id, tx.rank, txname.name, txname.unique_name_variant, txname.name_class "
				+ "	from sres.taxon tx, sres.taxonname txname " + "	where tx.taxon_id = txname.taxon_id " + "		and tx.ncbi_tax_id = :ncbi_taxon_id "
				+ "		and txname.name_class='scientific name' ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("ncbi_tax_id", Hibernate.INTEGER).addScalar("rank", Hibernate.STRING).addScalar("name", Hibernate.STRING);
		q.addScalar("unique_name_variant", Hibernate.STRING).addScalar("name_class", Hibernate.STRING);

		q.setCacheable(true);
		q.setInteger("ncbi_taxon_id", ncbi_taxon_id);

		List<?> rset = null;
		try {
			rset = q.list();
		}
		catch (Exception ex) {
			session.getTransaction().rollback();
			return new ResultType();
		}
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ncbi_tax_id", obj[0]);
			row.put("rank", obj[1]);
			row.put("name", obj[2]);
			row.put("unique_name_variant", obj[3]);
			row.put("name_class", obj[4]);
			results.add(row);
		}
		if (results.size() > 0) {
			return results.get(0);
		}
		else {
			return null;
		}
	}

	/**
	 * Retrieves attributes of a given genome
	 * 
	 * @param id genome_info_id
	 * @return genome attributes (genome_name, display_name, common_name and ncbi_taxon_id)
	 */
	public ResultType getNamesFromGenomeInfoId(String id) {

		String sql = "SELECT g.genome_name, g.display_name, g.ncbi_tax_id, g.common_name " + "FROM cas.genomeinfo g WHERE g.genome_info_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.addScalar("genome_name", Hibernate.STRING).addScalar("display_name", Hibernate.STRING).addScalar("ncbi_tax_id", Hibernate.INTEGER)
				.addScalar("common_name", Hibernate.STRING);
		q.setCacheable(true);

		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType names = new ResultType();
		Object[] obj = null;

		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			names.put("genome_name", obj[0]);
			names.put("display_name", obj[1]);
			names.put("ncbi_taxon_id", obj[2]);
			names.put("common_name", obj[3]);
		}

		return names;
	}

	/**
	 * Retrieves attributes of a given feature and corresponding genome
	 * 
	 * @param id na_feature_id
	 * @return genome and feature attributes
	 * <dl>
	 * <dd><code>genome_name</code> - name of the genome that this feature belongs to</dd>
	 * <dd><code>common_name</code> - common name is used for unix file name</dd>
	 * <dd><code>display_name</code> - name for display on the web, which may include html tags</dd>
	 * <dd><code>genome_info_id</code> - internal id of genome that this feature belongs to</dd>
	 * <dd><code>ncbi_taxon_id</code> - ncbi_taxon_id of the genome that this feature belongs to</dd>
	 * <dd><code>source_id</code> - locus tag</dd>
	 * <dd><code>feature_type</code> - genomic feature type such as CDS, gene, or rRNA</dd>
	 * <dd><code>feature_name</code> - product</dd>
	 * <dd><code>annotation</code> - algorithm/annotation source such as RAST, RefSeq, or Curation</dd>
	 * </dl>
	 */
	/*
	 * public ResultType getNamesFromNaFeatureId(String id) { String sql =
	 * "SELECT gi.genome_name, gi.common_name, gi.display_name, gi.genome_info_id, gi.ncbi_tax_id, " +
	 * "			 nf.source_id, nf.name, nf.product, nf.algorithm " + "	FROM cas.genomeinfo gi, app.dnafeature nf " +
	 * "	WHERE gi.genome_info_id = nf.genome_info_id " + "		and nf.na_feature_id = ? ";
	 * 
	 * Session session = factory.getCurrentSession(); session.beginTransaction(); SQLQuery q = session.createSQLQuery(sql); q.setString(0, id);
	 * List<?> rset = q.list(); session.getTransaction().commit();
	 * 
	 * ResultType names = new ResultType(); Object[] obj = null;
	 * 
	 * for (Iterator<?> iter = rset.iterator(); iter.hasNext();) { obj = (Object[]) iter.next(); names.put("genome_name", obj[0]);
	 * names.put("common_name", obj[1]); names.put("display_name", obj[2]); names.put("genome_info_id", obj[3]); names.put("ncbi_taxon_id", obj[4]);
	 * names.put("source_id", obj[5]); names.put("feature_type", obj[6]); names.put("feature_name", obj[7]); names.put("annotation", obj[8]); } return
	 * names; }
	 */

	/**
	 * Retrieves associated EC assignments for a given feature
	 * @param id na_feature_id
	 * @return EC assignments (ec_number, description)
	 */
	public ArrayList<ResultType> aaSequence2ECAssignments(String id) {
		String sql = "select ec_number, ec_name " + "	from app.ecsummary " + "	where na_feature_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("ec_number", obj[0]);
			row.put("description", obj[1]);
			if (obj[0] != null) {
				results.add(row);
			}
		}
		return results;
	}

	/**
	 * Retrieves feature attributes to construct FASTA identifiers. This is used in {@link edu.vt.vbi.patric.common.FASTAHelper}
	 * 
	 * @param id na_feature_id
	 * @return feature attributes (na_feature_id, source_id, protein_id, product, genome_name, accession)
	 */
	public ResultType getFastaIdentifiers(String id) {
		String sql = "select na_feature_id, source_id, protein_id, product, genome_name, accession " + "	from app.dnafeature "
				+ "	where na_feature_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ResultType results = new ResultType();
		Object[] obj = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			results.put("na_feature_id", obj[0]);
			results.put("source_id", obj[1]);
			results.put("protein_id", obj[2]);
			results.put("product", obj[3]);
			results.put("genome_name", obj[4]);
			results.put("accession", obj[5]);
		}
		return results;
	}

	/**
	 * Retrieves nucleic acid (NA) sequence of a given feature.
	 * 
	 * @param id na_feature_id
	 * @return sequence string
	 */
	public ArrayList<ResultType> getFastaNASequence(String id) {

		String sql = "SELECT df.na_feature_id, df.start_max, df.end_min, df.is_reversed, "
				+ "		substr(ns.sequence,df.start_max,df.na_length) as na_sequence " + "	from app.dnafeature df, dots.nasequence ns "
				+ "	where df.na_sequence_id = ns.na_sequence_id " + "		and df.na_feature_id = ? " + "	order by df.na_feature_id, start_max";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object[] obj = null;
		SerializableClob clobSequence = null;
		String strSequence = null;
		for (Iterator<?> iter = rset.iterator(); iter.hasNext();) {
			obj = (Object[]) iter.next();
			ResultType row = new ResultType();
			row.put("na_feature_id", obj[0]);
			row.put("start_max", obj[1]);
			row.put("end_min", obj[2]);
			row.put("is_reversed", obj[3]);

			try {
				clobSequence = (SerializableClob) obj[4];
				strSequence = IOUtils.toString(clobSequence.getAsciiStream(), "UTF-8");
				row.put("na_sequence", strSequence);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves amino acid (AA) sequence of a given feature.
	 * 
	 * @param id na_feature_id
	 * @return protein sequence string
	 */
	public String getFastaAASequence(String id) {
		String strSequence = null;
		SerializableClob clobSequence = null;
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		String sql = "select translation as aa_sequence from app.dnafeature where na_feature_id = ?";

		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);

		try {
			Object obj = q.uniqueResult();
			session.getTransaction().commit();

			clobSequence = (SerializableClob) obj;
			strSequence = IOUtils.toString(clobSequence.getAsciiStream(), "UTF-8");
		}
		catch (NullPointerException exNP) {
			System.out.println("Error in Retrieving AASequence. na_feature_id: " + id);
		}
		catch (NonUniqueResultException exNU) {
			List<?> rset = q.list();
			session.getTransaction().commit();
			Iterator<?> iter = rset.iterator();
			if (iter.hasNext()) {
				try {
					clobSequence = (SerializableClob) iter.next();
					strSequence = IOUtils.toString(clobSequence.getAsciiStream(), "UTF-8");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return strSequence;
	}

	public String getFastaNTSequence(String id) {

		String sql = "SELECT  substr(ns.sequence,df.start_max,df.na_length) as na_sequence " + "	from app.dnafeature df, dots.nasequence ns "
				+ "	where df.na_sequence_id = ns.na_sequence_id " + "		and df.na_feature_id = ? ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		SerializableClob clobSequence = null;
		String strSequence = null;

		try {
			clobSequence = (SerializableClob) obj;
			strSequence = IOUtils.toString(clobSequence.getAsciiStream(), "UTF-8");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return strSequence;
	}

	/**
	 * Retrieves substring of nucleic acid (NA) sequence of a given sequence. This is used by Genome Browser.
	 * 
	 * @param accession
	 * @param start start position
	 * @param length total length of sequence to retrieve
	 * @return sequence string
	 */
	public String getNASequence(String sid, String accession, int start, int length) {

		String sql = "";
		if (length > 0) {
			sql += "select substr(ns.sequence, " + start + ", " + length + ") ";
		}
		else {
			sql += "select ns.sequence ";
		}
		sql += "	from cas.sequenceinfo si, dots.nasequence ns " + "	where si.na_sequence_id = ns.na_sequence_id "
				+ "		and si.accession = ? and si.sequence_info_id = ? ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, accession);
		q.setString(1, sid);
		Object rset = q.uniqueResult();
		session.getTransaction().commit();

		SerializableClob clobSequence = null;
		String strSequence = null;
		try {
			clobSequence = (SerializableClob) rset;
			strSequence = IOUtils.toString(clobSequence.getAsciiStream(), "UTF-8");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return strSequence;
	}

	/**
	 * Retrieves sequence for a given accession
	 * @param accession
	 * @return sequence info (genome_name, sequence_info_id, sequence_description)
	 */
	public ResultType getSequenceInfoByAccession(String sid, String accession) {
		String sql = "select gi.genome_name, si.sequence_info_id, ns.description "
				+ "	from cas.genomeinfo gi, cas.sequenceinfo si, dots.nasequence ns " + "	where gi.genome_info_id = si.genome_info_id "
				+ "		and si.na_sequence_id = ns.na_sequence_id " + "		and si.accession = ? and si.sequence_info_id = ? ";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, accession);
		q.setString(1, sid);
		List<?> rset = q.list();
		session.getTransaction().commit();
		Object[] obj = null;
		ResultType result = new ResultType();
		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = (Object[]) it.next();
			result.put("genome_name", obj[0]);
			result.put("sequence_info_id", obj[1]);
			result.put("sequence_description", obj[2]);
		}
		return result;
	}

	/**
	 * Retrieves comment of a given feature
	 * @param id na_feature_id
	 * @return comment
	 */
	public String getNaFeatureComment(String id) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		String sql = "SELECT comment_string FROM dots.nafeaturecomment WHERE na_feature_id = ?";

		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		SerializableClob clobComment = null;
		String strComment = null;

		try {
			clobComment = (SerializableClob) obj;
			strComment = IOUtils.toString(clobComment.getAsciiStream(), "UTF-8");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return strComment;
	}

	/**
	 * Retrieves direct children of a given taxon. This used to feed Taxonomy tab
	 * 
	 * @param id ncbi_taxon_id
	 * @return taxonomy info of children (node_level, node_left, rank, class_name, node_count, ncbi_taxon_id, is_leaf, genome_below)
	 */
	public ArrayList<ResultType> getTaxonomyChildren(String id) {
		String sql = "SELECT node_level, node_left, node_right from cas.ncbiclassification where ncbi_taxon_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> nodeList = null;
		ArrayList<ResultType> results = new ArrayList<ResultType>();

		try {
			nodeList = q.list();
		}
		catch (Exception ex) {
			session.getTransaction().rollback();
			return results;
		}
		Object[] node = null;
		if (nodeList.iterator().hasNext()) {
			node = (Object[]) nodeList.iterator().next();
		}

		sql = "SELECT distinct cls.node_level, cls.node_left, cls.rank, cls.class_name, cls.node_count, " + "		cls.ncbi_taxon_id, "
				+ "		decode(cls.node_right-cls.node_left,1,1,0) is_leaf, cls.node_count-cls.genome_count genome_below "
				+ "	from cas.ncbiclassification cls, cas.genomeclassrelationship gr "
				+ "	where node_left between ? and ? and (cls.node_count>0 or cls.genome_count>0) "
				+ "		and cls.genome_classification_id = gr.genome_classification_id(+) " + "		and node_level = ? " + "	order by node_left";

		q = session.createSQLQuery(sql);
		q.setString(0, node[1].toString());
		q.setString(1, node[2].toString());
		q.setInteger(2, Integer.parseInt(node[0].toString()) + 1);

		List<?> rset = null;
		try {
			rset = q.list();
		}
		catch (Exception ex) {
			session.getTransaction().rollback();
			return results;
		}
		session.getTransaction().commit();

		Object[] obj = null;

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
			row.put("genome_below", obj[7]);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves unique database name that this class is connecting to. This is useful to identify database under the failover (dataguard)
	 * configuration. This is used for system management purpose.
	 * 
	 * @return database name
	 */
	public static String getUniqueDBName() {
		String sql = "select db_unique_name from v$database";
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		Object obj = session.createSQLQuery(sql).uniqueResult();
		session.getTransaction().commit();

		return obj.toString();
	}

	/**
	 * Retrieves organism name (taxon class name in this case) of a given taxon. This is written specially for KLEIO
	 * 
	 * @param id ncbi_taxon_id
	 * @return taxon class name
	 */
	public String getOrganismName(String id) {

		String sql = "select txname.name from sres.taxon tx, sres.taxonname txname "
				+ "where tx.taxon_id = txname.taxon_id and tx.ncbi_tax_id = ? and name_class='scientific name'";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		if (obj == null)
			return "";
		else
			return obj.toString();
	}

	/**
	 * Retrieves feature product of a given feature. This is written specially for KLEIO
	 * 
	 * @param id na_feature_id
	 * @return feature product
	 */
	public String getFeatureName(String id) {

		String sql = "SELECT nf.product FROM app.dnafeature nf WHERE nf.na_feature_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		if (obj == null) {
			return "";
		}
		else {
			return obj.toString();
		}
	}

	/**
	 * Retrieves name of a given genome This is written specially for KLEIO
	 * 
	 * @param id genome_info_id
	 * @return genome name
	 */
	public String getGenomeName(String id) {

		String sql = "SELECT g.genome_name " + "FROM cas.genomeinfo g WHERE g.genome_info_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		if (obj == null) {
			return "";
		}
		else {
			return obj.toString();
		}
	}

	/*
	 * Retrieves taxon ids below certain taxonomy id
	 * 
	 * @param id taxon_id
	 * 
	 * @return ArrayList of taxonids
	 */

	public ArrayList<ResultType> getTaxonIdsBelowTaxonIdForProteomics(String id) {

		String sql = " select tx.ncbi_tax_id "
				+ " from (select ncbi_tax_id from sres.taxon connect by prior taxon_id = parent_id start with ncbi_tax_id = ?) tx, "
				+ " proteomics.experiment exp where tx.ncbi_tax_id = exp.taxon_id";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		List<?> rset = q.list();
		session.getTransaction().commit();

		ArrayList<ResultType> results = new ArrayList<ResultType>();
		Object obj = null;

		for (Iterator<?> it = rset.iterator(); it.hasNext();) {
			obj = it.next();
			ResultType row = new ResultType();
			row.put("id", obj);
			results.add(row);
		}
		return results;
	}

	/**
	 * Retrieves ncbi taxon id of a given genome id
	 * 
	 * @param id genome_info_id
	 * @return ncbi_tax_id
	 */
	public String getTaxonIdOfGenomeId(String id) {

		String sql = "SELECT g.ncbi_tax_id " + "FROM cas.genomeinfo g WHERE g.genome_info_id = ?";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);
		q.setString(0, id);
		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		if (obj == null) {
			return "";
		}
		else {
			return obj.toString();
		}

	}
}
