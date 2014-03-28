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
public class DBPRC {
	protected static SessionFactory factory;

	protected final int SQL_TIMEOUT = 5 * 60;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	public int getPRCCount(String taxonId, String filter) {

		String sql = "select count(*) cnt from (select distinct experiment_id, description, speciesname, processing_type, summary, pubmed_id, count(distinct sample_id) from app.post_genomic";
		if (filter.equals("MS"))
			sql += " where processing_type = 'Mass spectrometry'";
		else if (filter.equals("MA"))
			sql += " where processing_type = 'Microarray'";
		else
			sql += " where processing_type = 'Protein interaction'";
		sql += " and taxon_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
		sql += " group by experiment_id, description, speciesname, processing_type, summary, pubmed_id)";

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql).addScalar("cnt", Hibernate.INTEGER);
		q.setCacheable(true);
		q.setTimeout(SQL_TIMEOUT);
		q.setString("taxonId", taxonId);

		Object obj = q.uniqueResult();
		session.getTransaction().commit();

		return Integer.parseInt(obj.toString());
	}

	public ArrayList<ResultType> getPRCData(String taxonId, String filter, int start, int end, String sort, String dir) {

		String sql = "select distinct experiment_id, description, speciesname, processing_type, summary, pubmed_id, count(distinct sample_id) from app.post_genomic";
		if (filter.equals("MS"))
			sql += " where processing_type = 'Mass spectrometry'";
		else if (filter.equals("MA"))
			sql += " where processing_type = 'Microarray'";
		else
			sql += " where processing_type = 'Protein interaction'";
		sql += " and taxon_id in (" + DBSummary.getTaxonIdsInTaxonSQL(":taxonId") + ")";
		sql += " group by experiment_id, description, speciesname, processing_type, summary, pubmed_id";
		sql += " order by " + sort + " " + dir;

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(sql);

		q.setTimeout(SQL_TIMEOUT);
		q.setString("taxonId", taxonId);

		ScrollableResults scr = null;

		try {
			scr = q.scroll();
		}
		catch (Exception ex) {
			System.out.println("[SQL error]" + taxonId);
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

			row.put("experiment_id", obj[0]);
			row.put("description", obj[1]);
			row.put("speciesname", obj[2]);
			row.put("experimenttype", obj[3]);
			row.put("summary", obj[4]);
			row.put("pubmed_id", obj[5]);
			row.put("samples", obj[6]);
			results.add(row);
		}
		session.getTransaction().commit();
		return results;
	}
}
