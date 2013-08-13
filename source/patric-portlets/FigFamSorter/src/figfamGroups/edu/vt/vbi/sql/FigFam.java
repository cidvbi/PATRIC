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
package figfamGroups.edu.vt.vbi.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.lob.SerializableClob;
import org.json.simple.JSONObject;

import Alignment.Aligner;
import Alignment.SequenceData;
import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.dao.ResultType;

public class FigFam {
	private final static int GENOMES_AT = 0;

	private final static int FIGFAMS_AT = 1;

	private final static int PAIR_ROOM = 2;

	public final static int FIGFAM_COUNT = 0;

	public final static int SPECIES_COUNT = 1;

	public final static int MIN_AA = 2;

	public final static int MAX_AA = 3;

	public final static int FEATURE_COUNT = 4;

	public final static int DETAILS_STATS_ROOM = 5;

	public final static String FIGFAM_ID = "figfamId";

	private final static String[] hexDigits = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B",
			"0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D",
			"1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41",
			"42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53",
			"54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77",
			"78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B",
			"9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD",
			"AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
			"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1",
			"D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3",
			"E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5",
			"F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

	private final static String TAXON_TO_GENOMES_START = "select genome_info_id from app.genomesummary where ncbi_tax_id in"
			+ " (select ncbi_tax_id from sres.taxon"
			+ " connect by prior taxon_id = parent_id"
			+ " start with ncbi_tax_id = ";

	private final static String GENOMES_TO_GENOMES_START = "select genome_info_id from app.genomesummary where ";

	private final static String algorithmRestrict = " and algorithm ='RAST'";;

	protected static SessionFactory factory = null;

	public static void setSessionFactory(SessionFactory sf) {
		factory = sf;
	}

	private final static String TAXON_ID_TO_NAME = "select tn.name from sres.taxon t, sres.taxonname tn"
			+ " where t.taxon_id = tn.taxon_id" + " and name_class = 'scientific name'" + " and ncbi_tax_id = ";

	private final static String emptyText = "";

	private final static String ORTHO_ENV_NAME = "ORTHO_WORK_HOME";

	private final static int IN_MAX = 333;

	private final static String GET_GROUP_TAIL = " order by fi.name, gi.genome_name";

	private final static String GET_DETAILS_TAIL = "order by genome_name";

	// Harry modified
	private final static String GET_FASTA_START = "select genome_name, accession, source_id, product, translation"
			+ " from app.dnafeature where name = 'CDS' ";

	private final static String GET_ALIGN_START = "select gi.common_name, df.source_id, df.translation"
			+ " from app.dnafeature df, cas.genomeinfo gi where" + " gi.genome_info_id = df.genome_info_id";

	private final static String START_BLAST_FASTA = "select translation from app.dnafeature where na_feature_id = ";

	private final static String GET_HTML_START = "select fi.name, nf.genome_name, nf.source_id, nf.aa_length, nf.product "
			+ "from sres.figfaminfo fi, "
			+ "dots.figfamassociation fa, "
			+ "app.dnafeature nf "
			+ "where nf.na_feature_id = fa.na_feature_id "
			+ "and fa.figfam_info_id = fi.figfam_info_id"
			+ "and nf.name = 'CDS' ";

	private Object[] replaceNulls(Object[] objs) {
		int count = objs.length;
		Object[] result = new Object[count];
		System.arraycopy(objs, 0, result, 0, count);
		for (int i = 0; i < count; i++) {
			if (result[i] == null) {
				result[i] = emptyText;
			}
		}
		return result;
	}

	// This function is called to get an ordering of the Figfams based on order of occurrence only for the ref. genome
	// It has nothing to do with the other genomes in a display.
	// This function returns an ordering of the Figfam ID's for the reference genome with paralogs removed
	// The counting for the number of paralogs occurs in the javascript code (I think)
	public void getSyntonyOrder(ResourceRequest req, PrintWriter writer) {
		String idText = req.getParameter("syntonyId");
		
		if (idText != null) {
			int genomeId = Integer.parseInt(idText);
			
			SolrInterface solr = new SolrInterface();
			try {
				solr.setCurrentInstance("FigFamSorter");
			}
			catch (MalformedURLException e1) {
				e1.printStackTrace();
			}

			LBHttpSolrServer server = solr.getServer();
			
			SolrQuery solr_query = new SolrQuery("gid:"+genomeId);
			solr_query.setRows(15000);
			solr_query.addField("figfam_id");
			solr_query.addSort("locus_tag", SolrQuery.ORDER.asc);
			
			System.out.println(genomeId);
			System.out.println(solr_query.toString());
			
			QueryResponse qr;
			SolrDocumentList sdl;
			int orderSet = 0;
			ArrayList<SyntonyOrder> collect = new ArrayList<SyntonyOrder>();
			try {
				qr = server.query(solr_query, SolrRequest.METHOD.POST);
				sdl = qr.getResults();
				
				for (SolrDocument d : sdl) {
					for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
						Map.Entry<String, Object> el = i.next();
						if(el.getKey().toString().equals("figfam_id")){
							collect.add(new SyntonyOrder(el.getValue().toString(), orderSet));
							++orderSet;
						}
					}
				}
				
			}
			catch (SolrServerException e) {
				e.printStackTrace();
			}
			
			/*StringBuilder query = new StringBuilder(0x1000);
			query.append("select ff.name");
			query.append(" from app.figfamsummary ff, app.dnafeature df");
			query.append(" where ff.genome_info_id = " + genomeId);
			query.append(" and ff.na_feature_id = df.na_feature_id");
			query.append(" and ff.genome_info_id = df.genome_info_id");
			query.append(" and df.name = 'CDS'");
			query.append(" and df.algorithm = 'RAST'");
			query.append(" order by df.accession, df.start_min");//accessions are figfam ids
			Iterator<?> iter = getSqlIterator(query);
			int orderSet = 0;
			ArrayList<SyntonyOrder> collect =
				new ArrayList<SyntonyOrder>();
			while (iter.hasNext()) {
				collect.add(
					new SyntonyOrder((String)iter.next(), orderSet));
				++orderSet;
			}
			*/
			if (0 < collect.size()) {
				SyntonyOrder[] orderSave = new SyntonyOrder[collect.size()];
				collect.toArray(orderSave);// orderSave is array in order of Figfam ID
				SyntonyOrder[] toSort = new SyntonyOrder[collect.size()];
				System.arraycopy(orderSave, 0, toSort, 0, toSort.length);// copy the array so it can be sorted based on
																			// position in the genome
				Arrays.sort(toSort); // sort based on figfamIDs
				SyntonyOrder start = toSort[0];
				for (int i = 1; i < toSort.length; i++) {
					start = start.mergeSameId(toSort[i]);// set syntonyAt -1 to those objects which occur multiple times
				}
				orderSet = 0;
				for (int i = 0; i < orderSave.length; i++) {
					orderSet = (orderSave[i]).compressAt(orderSet); // adjusts the syntonyAt number to get the correct
																	// column based on replicon with -1's removed
				}
				String prefix = "";
				for (int i = 0; i < toSort.length; i++) {// writes all those that don't have -1's
					prefix = (toSort[i]).write(prefix, writer);
				}
			}
		}
	}

	private String[] getFigfamPair(ResourceRequest req) {
		String[] result = null;
		String genomeIds = req.getParameter("genomeIds");
		String figfamIds = req.getParameter("figfamIds");
		if ((genomeIds == null) || ((genomeIds.trim()).length() == 0)) {
			if (figfamIds != null) {
				PortletSession sess = req.getPortletSession(true);
				ResultType key = (ResultType) (sess.getAttribute("key" + figfamIds, PortletSession.APPLICATION_SCOPE));
				if (key != null) {
					genomeIds = key.get("genomeIds");
					figfamIds = key.get("figfamIds");
				}
			}
		}
		if ((figfamIds != null) && (0 < figfamIds.length()) && (genomeIds != null) && (0 < genomeIds.length())) {
			result = new String[PAIR_ROOM];
			result[GENOMES_AT] = genomeIds;
			result[FIGFAMS_AT] = figfamIds;
		}
		return result;
	}

	private String getFigfamNameRestrict(String fieldName, String figfamIds) {
		String[] figList = figfamIds.split(",");
		StringBuilder build = new StringBuilder(0x200);
		restrictByList(fieldName, figList, build);
		return (build.toString());
	}

	public void getLocusTags(ResourceRequest req, PrintWriter writer, String type) {
		String[] figfamPair = getFigfamPair(req);
		if (figfamPair == null) {
			writer.write("");
		}
		else {
			String figRestrict = "";

			if (type.equals("figfam")) {

				figRestrict = getFigfamNameRestrict("fs.name", figfamPair[FIGFAMS_AT]);
			}
			else {
				figRestrict = getFigfamNameRestrict("es.ec_number", figfamPair[FIGFAMS_AT]);
			}

			int[] genomeIds = splitOutGenomeIds(figfamPair[GENOMES_AT]);
			StringBuilder query = new StringBuilder(0x1000);
			query.append("select df.source_id from app.dnafeature df,");
			if (type.equals("figfam")) {
				query.append(" app.figfamsummary fs");
			}
			else {
				query.append(" app.ecsummary es");
			}
			query.append(" where ");
			if (type.equals("figfam"))
				restrictByNumbers("fs.genome_info_id ", genomeIds, query);
			else
				restrictByNumbers("es.genome_info_id ", genomeIds, query);
			query.append(" and " + figRestrict);
			query.append(" and df.algorithm = 'RAST'");
			query.append(" and df.name = 'CDS'");
			if (type.equals("figfam")) {
				query.append(" and fs.na_feature_id = df.na_feature_id");
			}
			else {
				query.append(" and es.na_feature_id = df.na_feature_id");
			}
			Iterator<?> iter = getSqlIterator(query);
			if (iter.hasNext()) {
				writer.write("" + iter.next());
				while (iter.hasNext()) {
					writer.write("\t" + iter.next());
				}
			}
		}
	}

	private FigfamFeature[] getFigfamFeatures(String[] figfamPair, String type) {
		StringBuilder query = new StringBuilder(0x1000);

		if (type.equals("figfam")) {

			query.append("select na_feature_id,  name, description");
			query.append(" from app.figfamsummary");
			query.append(" where ");

		}
		else if (type.equals("ec")) {

			query.append("select na_feature_id,  ec_number, ec_name");
			query.append(" from app.ecsummary");
			query.append(" where ");

		}

		int[] genomeIds = splitOutGenomeIds(figfamPair[GENOMES_AT]);

		restrictByNumbers("genome_info_id ", genomeIds, query);
		query.append(" and ");
		String[] figList = (figfamPair[FIGFAMS_AT]).split(",");

		if (type.equals("figfam")) {
			restrictByList("name", figList, query);
		}
		else if (type.equals("ec")) {
			restrictByList("ec_number", figList, query);
		}
		query.append(" order by na_feature_id");
		ArrayList<FigfamFeature> list = new ArrayList<FigfamFeature>();
		Iterator<?> iter = getSqlIterator(query);
		while (iter.hasNext()) {
			Object[] line = replaceNulls((Object[]) iter.next());
			list.add(new FigfamFeature(line));
		}
		FigfamFeature[] result = new FigfamFeature[list.size()];
		list.toArray(result);
		return result;
	}

	public String[] getDetails(String genomeIds, String figfamIds, int[] stats, String type) {
		String[] result = null;
		String[] figfamPair = new String[PAIR_ROOM];
		figfamPair[GENOMES_AT] = genomeIds;
		figfamPair[FIGFAMS_AT] = figfamIds;
		FigfamFeature[] features = getDetailsArray(figfamPair, stats, type);
		int start = 0;
		ArrayList<String> collect = new ArrayList<String>();
		for (int i = 1; i < features.length; i++) {
			if (!(features[i]).inSameGroup(features[start])) {
				(features[start]).stampHeader(i - start, collect);
				while (start < i) {
					(features[start]).stampDetails(collect);
					++start;
				}
			}
		}
		(features[start]).stampHeader(features.length - start, collect);
		while (start < features.length) {
			(features[start]).stampDetails(collect);
			++start;
		}
		result = new String[collect.size()];
		collect.toArray(result);
		return result;
	}

	public ArrayList<ResultType> getDetailsArray_HeatMapDownload(String[] figfamPair, int start, int end, String type) {
		/**
		 * System.out.print("getFigfamFeatures0"+figfamPair[0]); System.out.print("getFigfamFeatures1"+figfamPair[1]);
		 */

		FigfamFeature[] features = getFigfamFeatures(figfamPair, type);
		StringBuilder query = new StringBuilder(0x1000);
		query.append("select" + "	distinct df.genome_info_id, " + "	df.genome_name, " + "	df.accession, "
				+ "	df.na_feature_id, " + "	df.na_sequence_id, " + "	df.name, " + "	df.source_id as locus_tag, "
				+ "	decode(df.algorithm,'Curation','Legacy BRC','RAST','PATRIC','RefSeq') as algorithm, "
				+ "	decode(df.is_reversed,1,'-','+') as strand, " + "	df.debug_field, " + "	df.start_min, "
				+ "	df.start_max, " + "	df.end_min, " + "	df.end_max, " + "	df.na_length, " + "	df.product, "
				+ "	df.gene, " + "	df.aa_length, " + "	df.bound_moiety, " + "	df.anticodon," + " 	df.protein_id ");
		query.append(" from app.dnafeature df");
		query.append(" where ");
		int[] featureIds = new int[features.length];
		for (int i = 0; i < features.length; i++) {
			featureIds[i] = (features[i]).featureId;
		}
		restrictByNumbers("na_feature_id", featureIds, query);
		query.append(" and name = 'CDS' and algorithm = 'RAST'");
		query.append(" order by na_feature_id");

		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(query.toString());
		q.setCacheable(true);

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

			for (int j = 0; j < features.length; j++) {

				if (obj[3].toString().equals(Integer.toString(features[j].featureId))) {
					// System.out.println(obj[3] + "-" + features[j].featureId + '-' + features[j].figfamName);
					row.put("groupId", (Object) features[j].figfamName);
				}
			}
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
			row.put("bound_moiety", obj[18]);
			row.put("anticodon", obj[19]);
			row.put("protein_id", obj[20]);

			results.add(row);
		}

		session.getTransaction().commit();

		return results;

	}

	private FigfamFeature[] getDetailsArray(String[] figfamPair, int[] stats, String type) {
		FigfamFeature[] features = getFigfamFeatures(figfamPair, type);
		StringBuilder query = new StringBuilder(0x1000);
		query.append("select na_feature_id, genome_name, genome_info_id, accession"
				+ ", source_id, name, start_max, end_min" + ", (end_min-start_max+1), decode(is_reversed,1,'-','+')"
				+ ", aa_length, gene, product");
		query.append(" from app.dnafeature");
		query.append(" where ");
		int[] featureIds = new int[features.length];
		for (int i = 0; i < features.length; i++) {
			featureIds[i] = (features[i]).featureId;
		}
		restrictByNumbers("na_feature_id", featureIds, query);
		query.append(" and name = 'CDS' and algorithm = 'RAST'");
		query.append(" order by na_feature_id");
		Iterator<?> iter = getSqlIterator(query);
		if (iter.hasNext()) {
			// set keepers to discard features members without details
			ArrayList<FigfamFeature> keepers = new ArrayList<FigfamFeature>();
			Object[] line = replaceNulls((Object[]) iter.next());
			int featureAt = 0;
			while ((line != null) && (featureAt < features.length)) {
				// check to see if line provides details for featureAt
				int differ = (features[featureAt]).addDetails(line, keepers);
				if (differ <= 0) {
					// either featureAt got its details
					// or line progress has passed it
					++featureAt;
				}
				// do not replace line if its featureIds was too large
				// to match featureAt
				if (0 <= differ) {
					if (iter.hasNext()) {
						line = replaceNulls((Object[]) iter.next());
					}
					else {
						line = null;
					}
				}
			}
			if (keepers.size() < features.length) {
				features = new FigfamFeature[keepers.size()];
				keepers.toArray(features);
			}
			if (0 < features.length) {
				// change order of features so same figfam groups
				// stay in the same block
				Arrays.sort(features);
				// prepare to collect statistics for group block
				int minAa = (features[0]).aaLength;
				int maxAa = minAa;
				int[] genomeCounter = new int[features.length];
				genomeCounter[0] = (features[0]).genomeId;
				int start = 0;
				int figfamCount = 1;
				for (int i = 1; i < features.length; i++) {
					int nextAa = (features[i]).aaLength;
					if (nextAa < minAa) {
						minAa = nextAa;
					}
					else if (maxAa < nextAa) {
						maxAa = nextAa;
					}
					genomeCounter[i] = (features[i]).genomeId;
					if (!(features[i]).inSameGroup(features[start])) {
						++figfamCount;
						start = i;
					}
				}
				int speciesCount = 1;
				Arrays.sort(genomeCounter);
				for (int i = 1; i < genomeCounter.length; i++) {
					if (genomeCounter[i] != genomeCounter[i - 1]) {
						++speciesCount;
					}
				}
				// stat values
				stats[FIGFAM_COUNT] = figfamCount;
				stats[SPECIES_COUNT] = speciesCount;
				stats[MIN_AA] = minAa;
				stats[MAX_AA] = maxAa;
				stats[FEATURE_COUNT] = features.length;
			}
		}
		return features;
	}

	public void getDetails(ResourceRequest req, PrintWriter writer, String type) {
		String[] figfamPair = getFigfamPair(req);
		if (figfamPair == null) {
			String genomeIds = req.getParameter("genomeIds");
			String figfamIds = req.getParameter("figfamIds");
			if ((genomeIds == null)) {
				writer.write("genomeIds are null");
			}
			else {
				writer.write("genomeIds = " + genomeIds);
			}
			if ((figfamIds == null)) {
				writer.write("\tfigfams are null");
			}
			else {
				writer.write("\tfigfams = " + figfamIds);
			}
		}
		else {
			int[] stats = new int[DETAILS_STATS_ROOM];
			FigfamFeature[] features = getDetailsArray(figfamPair, stats, type);
			if (0 < features.length) {
				writer.write("" + stats[FIGFAM_COUNT]);
				writer.write("\t" + stats[SPECIES_COUNT]);
				writer.write("\t" + stats[FEATURE_COUNT]);
				writer.write("\t" + stats[MIN_AA]);
				writer.write("\t" + stats[MAX_AA] + "\t");
				int start = 0;
				for (int i = 1; i < features.length; i++) {
					if (!(features[i]).inSameGroup(features[start])) {
						(features[start]).stampHeader(i - start, writer);
						while (start < i) {
							(features[start]).stampDetails(writer);
							++start;
						}
						writer.write("\t");
					}
				}
				(features[start]).stampHeader(features.length - start, writer);
				while (start < features.length) {
					(features[start]).stampDetails(writer);
					++start;
				}
			}
		}
	}

	public Aligner getAlignment(char needHtml, ResourceRequest req) {
		Aligner result = null;
		String groupID = req.getParameter(FIGFAM_ID);
		try {
			SequenceData[] sequences = getProteins(req);
			result = new Aligner(needHtml, groupID, sequences);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * private void markProgress(String fileName, String text) { try { BufferedWriter writer = new BufferedWriter(new
	 * FileWriter("/tmp/dbg_" + fileName)); writer.write(text); writer.newLine(); writer.close(); } catch (IOException
	 * e) { e.printStackTrace(); } }
	 */

	public Aligner getFeatureAlignment(char needHtml, ResourceRequest req) {
		Aligner result = null;
		try {
			SequenceData[] sequences = getFeatureProteins(req);
			result = new Aligner(needHtml, req.getParameter(FIGFAM_ID), sequences);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * private void addPopupHeader(PrintWriter writer) { writer.write(
	 * "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
	 * "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
	 * "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" +
	 * "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />\n" + "<title></title>\n" +
	 * "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" />\n" +
	 * "<link rel=\"stylesheet\" href=\"/patric/css/reset.css\" type=\"text/css\" />" + "\n" +
	 * "<link rel=\"stylesheet\" href=\"/patric/css/popup.css\" type=\"text/css\" />" + "</head>\n" + "<body>\n" +
	 * "<div id=\"header\">\n" + "<div id=\"masthead\">" + "<a href=\"/\">PATRIC <span class=\"sub\">" +
	 * "Pathosystems Resouce Integration Center</span></a></div>" + "</div>\n" + "<div id=\"toppage\">\n" +
	 * "<div class=\"content\">\n"); }
	 * 
	 * private void addPopupFooter(PrintWriter writer) { writer.write( "</div></div>\n" + "<div id=\"footer\">\n" +
	 * "&nbsp;\n" + "</div>" + "</body>" + "</html>"); }
	 */

	public static File getOrthoPath(String subDir) {
		File result = null;
		Map<?, ?> check = System.getenv();
		String homeDir = (String) (check.get(ORTHO_ENV_NAME));
		if (homeDir == null) {
			homeDir = "/tmp";
		}
		if (homeDir != null) {
			result = new File(homeDir + File.separator + subDir);
		}
		return result;
	}

	public int[] getIntArray(String text) {

		String[] b = text.split(",");
		int[] ret = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			ret[i] = Integer.parseInt(b[i]);
		}

		return ret;

	}

	public String getGenomeIdsForTaxon(String taxon) {
		StringBuilder query = new StringBuilder(0x1000);
		query.append(TAXON_TO_GENOMES_START);
		query.append(taxon);
		query.append(") and rast = 1");

		StringBuilder builder = new StringBuilder(0x1000);
		Iterator<?> iter = getSqlIterator(query);
		if (iter.hasNext()) {
			builder.append((String) ("" + iter.next()));
			while (iter.hasNext()) {
				builder.append("," + (String) ("" + iter.next()));
				// DBSummary db = new DBSummary();
			}
		}
		return builder.toString();
	}

	public String getGenomeIdsFromGenome(String genome) {
		StringBuilder query = new StringBuilder(0x1000);
		query.append(GENOMES_TO_GENOMES_START);
		restrictByNumbers("genome_info_id", getIntArray(genome), query);
		query.append("and rast = 1");

		StringBuilder builder = new StringBuilder(0x1000);
		Iterator<?> iter = getSqlIterator(query);
		if (iter.hasNext()) {
			builder.append((String) ("" + iter.next()));
			while (iter.hasNext()) {
				builder.append("," + (String) ("" + iter.next()));
			}
		}
		return builder.toString();
	}

	public String getTaxonName(String taxon) {
		StringBuilder query = new StringBuilder(0x1000);
		query.append(TAXON_ID_TO_NAME);
		query.append(taxon);
		String result = null;
		Iterator<?> iter = getSqlIterator(query);
		if (iter.hasNext()) {
			result = (String) (iter.next());
		}
		return result;
	}

	private SequenceData[] getProteins(ResourceRequest req) throws SQLException {
		SequenceData[] result = null;
		String featureList = req.getParameter("featureIds");
		if (featureList != null) {
			String[] featuresText = featureList.split(",");
			int[] featureIds = new int[featuresText.length];
			for (int i = 0; i < featureIds.length; i++) {
				featureIds[i] = Integer.parseInt(featuresText[i]);
			}
			result = getFeatureSequences(featureIds);
		}
		return result;
	}

	private SequenceData[] getFeatureSequences(int[] featureIds) {
		ArrayList<SequenceData> collect = new ArrayList<SequenceData>();
		StringBuilder query = new StringBuilder(0x1000);
		query.append(GET_ALIGN_START);
		query.append(" and ");
		restrictByNumbers("df.na_feature_id", featureIds, query);
		query.append(" order by gi.common_name");
		Iterator<?> iter = getSqlIterator(query);
		while (iter.hasNext()) {
			Object[] line = replaceNulls((Object[]) iter.next());
			SequenceData toAdd = new SequenceData(line[0], line[1], line[2]);
			collect.add(toAdd);
		}
		SequenceData[] result = new SequenceData[collect.size()];
		collect.toArray(result);
		return result;
	}

	private SequenceData[] getFeatureProteins(ResourceRequest req) throws SQLException {
		SequenceData[] result = null;
		String featuresString = req.getParameter("featureIds");
		if (featuresString != null) {
			String[] idList = featuresString.split(",");
			int[] featureIds = new int[idList.length];
			for (int i = 0; i < featureIds.length; i++) {
				featureIds[i] = Integer.parseInt(idList[i]);
			}
			result = getFeatureSequences(featureIds);
		}
		return result;
	}

	public String[] getFastaLines(ResourceRequest req) {
		String featureCount = req.getParameter("featureCount");
		ArrayList<String> lines = new ArrayList<String>();
		if (featureCount != null) {
			int[] featureIds = new int[Integer.parseInt(featureCount)];
			for (int i = 0; i < featureIds.length; i++) {
				featureIds[i] = Integer.parseInt(req.getParameter("feature" + i));
			}
			StringBuilder query = new StringBuilder(0x1000);
			query.append(GET_FASTA_START);
			query.append(algorithmRestrict);
			query.append(" and ");
			restrictByNumbers("na_feature_id", featureIds, query);
			query.append(GET_DETAILS_TAIL);

			Iterator<?> iter = getSqlIterator(query);
			while (iter.hasNext()) {
				Object[] line = replaceNulls((Object[]) iter.next());
				String titleLine = ">locus|";
				titleLine += line[2] + " ";
				titleLine += line[3] + " [";
				titleLine += line[0] + " | ";
				titleLine += line[1] + "]";
				lines.add(titleLine);
				try {
					SerializableClob clobSeq = (SerializableClob) (line[4]);
					String strSeq = IOUtils.toString(clobSeq.getAsciiStream(), "UTF-8");
					lines.add(strSeq);
				}
				catch (Exception ex) {
					System.out.println("*******************");
					System.out.println(ex.toString());
					System.out.println("*******************");
				}

			}
		}
		String[] result = new String[lines.size()];
		lines.toArray(result);
		return result;
	}

	public void setHtmlDetailRows(ArrayList<?> genomesList, ArrayList<?> figfamIds, StringBuilder output) {
		output.append("<tr>");
		output.append("<th>Id</th>");
		output.append("<th>Genome</th>");
		output.append("<th>Source Id</th>");
		output.append("<th>AA Length</th>");
		output.append("<th>Protein Product</th>");
		output.append("</tr>");
		StringBuilder query = new StringBuilder(0x1000);
		query.append(GET_HTML_START);
		query.append(algorithmRestrict);
		query.append(" and ");
		restrictByNumbers("nf.genome_info_id", genomesList, query);
		query.append(" and ");
		restrictByList("fi.name", figfamIds, query);
		query.append(GET_GROUP_TAIL);

		Iterator<?> iter = getSqlIterator(query);
		while (iter.hasNext()) {
			Object[] line = replaceNulls((Object[]) iter.next());
			output.append("<tr>");
			output.append("<td>" + line[0] + "</td>");
			output.append("<td>" + line[1] + "</td>");
			output.append("<td>" + line[2] + "</td>");
			output.append("<td>" + line[3] + "</td>");
			output.append("<td>" + line[4] + "</td>");
			output.append("</tr>");
		}
	}

	private int[] splitOutGenomeIds(String idText) {
		int[] result = null;
		if (idText != null) {
			String[] textIds = idText.split(",");
			result = new int[textIds.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = Integer.parseInt(textIds[i]);
			}
		}
		return result;
	}

	private GenomeBitSetter[] getGenomeBitSetters(ResourceRequest req) {
		GenomeBitSetter[] result = null;
		String listText = req.getParameter("genomeIds");
		if (listText != null) {
			String[] textIds = listText.split(",");
			result = new GenomeBitSetter[textIds.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new GenomeBitSetter(i, Integer.parseInt(textIds[i]));
			}
			Arrays.sort(result);
		}

		return result;
	}

	private void restrictByNumbers(String restricted, ArrayList<?> textNumbers, StringBuilder toBuild) {
		int count = textNumbers.size();
		if (0 < count) {
			Iterator<?> it = textNumbers.iterator();
			String value = (String) (it.next());
			if (count == 1) {
				toBuild.append(restricted + " = " + value);
			}
			else if (count <= IN_MAX) {
				toBuild.append(restricted + " in (" + value);
				while (it.hasNext()) {
					value = (String) (it.next());
					toBuild.append(", " + value);
				}
				toBuild.append(")");
			}
			else {
				toBuild.append("((" + restricted + " in (" + value);
				int at = 1;
				int nextBreak = IN_MAX;
				while (it.hasNext()) {
					value = (String) (it.next());
					if (at <= nextBreak) {
						toBuild.append(", " + value);
					}
					else {
						toBuild.append(")) or (" + restricted + " in (" + value);
						nextBreak += IN_MAX;
					}
					++at;
				}
				toBuild.append(")))");
			}
		}
	}

	private void restrictByNumbers(String restricted, int[] idList, StringBuilder toBuild) {
		int count = idList.length;
		if (0 < count) {
			if (count == 1) {
				toBuild.append(restricted + " = " + idList[0]);
			}
			else if (count <= IN_MAX) {
				toBuild.append(restricted + " in (" + idList[0]);
				for (int i = 1; i < count; i++) {
					toBuild.append(", " + idList[i]);
				}
				toBuild.append(")");
			}
			else {
				toBuild.append("((" + restricted + " in (" + idList[0]);
				int nextBreak = IN_MAX;
				for (int at = 1; at < count; at++) {
					if (at < nextBreak) {
						toBuild.append(", " + idList[at]);
					}
					else {
						toBuild.append(")) or (" + restricted + " in (" + idList[at]);
						nextBreak += IN_MAX;
					}
				}
				toBuild.append(")))");
			}
		}
	}

	private void restrictByNumbers(String restricted, GenomeBitSetter[] idList, StringBuilder toBuild) {
		int count = idList.length;
		if (0 < count) {
			if (count == 1) {
				toBuild.append(restricted + " = " + (idList[0]).genomeId);
			}
			else if (count <= IN_MAX) {
				toBuild.append(restricted + " in (" + (idList[0]).genomeId);
				for (int i = 1; i < count; i++) {
					toBuild.append(", " + (idList[i]).genomeId);
				}
				toBuild.append(")");
			}
			else {
				toBuild.append("((" + restricted + " in (" + (idList[0]).genomeId);
				int nextBreak = IN_MAX;
				for (int at = 1; at < count; at++) {
					if (at <= nextBreak) {
						toBuild.append(", " + (idList[at]).genomeId);
					}
					else {
						toBuild.append(")) or (" + restricted + " in (" + (idList[at]).genomeId);
						nextBreak += IN_MAX;
					}
					++at;
				}
				toBuild.append(")))");
			}
		}
	}

	private void restrictByList(String restricted, ArrayList<?> list, StringBuilder toBuild) {
		int count = list.size();
		if (0 < count) {
			Iterator<?> it = list.iterator();
			String value = (String) (it.next());
			if (count == 1) {
				toBuild.append(restricted + " = '" + value + "'");
			}
			else if (count <= IN_MAX) {
				toBuild.append(restricted + " in ('" + value + "'");
				while (it.hasNext()) {
					value = (String) (it.next());
					toBuild.append(", '" + value + "'");
				}
				toBuild.append(")");
			}
			else {
				toBuild.append("((" + restricted + " in ('" + value + "'");
				int at = 1;
				int nextBreak = IN_MAX;
				while (it.hasNext()) {
					value = (String) (it.next());
					if (at <= nextBreak) {
						toBuild.append(", '" + value + "'");
					}
					else {
						toBuild.append(")) or (" + restricted + " in ('" + value + "'");
						nextBreak += IN_MAX;
					}
					++at;
				}
				toBuild.append(")))");
			}
		}
	}

	private void restrictByList(String restricted, String[] textValues, StringBuilder toBuild) {
		int count = textValues.length;
		if (0 < count) {
			if (count == 1) {
				toBuild.append(restricted + " = '" + textValues[0] + "'");
			}
			else if (count <= IN_MAX) {
				toBuild.append(restricted + " in ('" + textValues[0] + "'");
				for (int i = 1; i < count; i++) {
					toBuild.append(", '" + textValues[i] + "'");
				}
				toBuild.append(")");
			}
			else {
				toBuild.append("((" + restricted + " in ('" + textValues[0] + "'");
				int at = 1;
				int nextBreak = IN_MAX;
				while (at < count) {
					String value = textValues[at];
					if (at <= nextBreak) {
						toBuild.append(", '" + value + "'");
					}
					else {
						toBuild.append(")) or (" + restricted + " in ('" + value + "'");
						nextBreak += IN_MAX;
					}
					++at;
				}
				toBuild.append(")))");
			}
		}
	}

	public void getFeatureIds(ResourceRequest req, PrintWriter writer, String keyword) {

		SolrInterface solr = new SolrInterface();
		try {
			solr.setCurrentInstance("GenomicFeature");
		}
		catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		LBHttpSolrServer server = solr.getServer();

		SolrQuery solr_query = new SolrQuery();
		solr_query.setQuery(keyword);
		solr_query.setRows(1);
		solr_query.addField("na_feature_id");

		QueryResponse qr;
		SolrDocumentList sdl;
		long rows = 0;
		String ret = "";
		try {
			qr = server.query(solr_query, SolrRequest.METHOD.POST);
			sdl = qr.getResults();
			rows = sdl.getNumFound();
			solr_query.setRows((int) rows);
			qr = server.query(solr_query, SolrRequest.METHOD.POST);
			sdl = qr.getResults();

			for (SolrDocument d : sdl) {
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();
					if (el.getKey().equals("na_feature_id")) {
						ret += el.getValue().toString() + ",";
					}
				}
			}

			writer.write(ret.substring(0, ret.length() - 1));

		}
		catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getGroupStats(ResourceRequest req, PrintWriter writer, String type) throws IOException {
		SolrInterface solr = new SolrInterface();
		solr.setCurrentInstance("FigFamSorter");

		LBHttpSolrServer server = solr.getServer();

		long start_ms, end_ms;
		JSONObject figfams = new JSONObject();
		String figfam_ids = "";
		String[] genomeIds = req.getParameter("genomeIds").toString().split(",");
		String[] genomeIdsStr = new String[genomeIds.length];
		List<String> genomeIdsArr = Arrays.asList(genomeIds);
		
		/* getting genome counts per figfamID (figfam) */
		SolrQuery solr_query = new SolrQuery("*:*");
		solr_query.addFilterQuery(getSolrQuery(req));
		solr_query.setRows(0);
		solr_query.setFacet(true);
		solr_query.addFacetPivotField("figfam_id,gid");
		solr_query.setFacetMinCount(1);
		solr_query.setFacetLimit(-1);
		solr_query.setSort("figfam_id", SolrQuery.ORDER.asc);
		
		System.out.println(solr_query.toString());
		
		try {
			start_ms = System.currentTimeMillis();
			QueryResponse qr = server.query(solr_query, SolrRequest.METHOD.POST);
			end_ms = System.currentTimeMillis();
			System.out.println("Query time - 1st - "+ (end_ms - start_ms));
			start_ms = System.currentTimeMillis();
			NamedList<List<PivotField>> pivots = qr.getFacetPivot();
			
			for (Map.Entry<String, List<PivotField>> pivot : pivots) {
				List<PivotField> pivotEntries = pivot.getValue();
				if (pivotEntries != null) {
					for (PivotField pivotEntry : pivotEntries) {
						//JSONObject obj = new JSONObject();
						JSONObject obj2 = new JSONObject();

						List<PivotField> pivotGenomes = pivotEntry.getPivot();
						int count = 0, index;
						String hex;
						
						Arrays.fill(genomeIdsStr, "00");
						/*for(int i=0; i<genomeIds.length; i++){
							genomeIdsStr[i] = "00";
						}*/
						
						for (PivotField pivotGenome : pivotGenomes) {
							//index = Arrays.asList(genomeIds).indexOf(pivotGenome.getValue().toString());
							index = genomeIdsArr.indexOf(pivotGenome.getValue().toString());
							hex = Integer.toHexString(pivotGenome.getCount());
							genomeIdsStr[index] = hex.length()<2?"0"+hex:hex;
							count += pivotGenome.getCount();
						}
						
						if(figfam_ids.length() == 0)
							figfam_ids += pivotEntry.getValue();
						else
							figfam_ids += " OR " + pivotEntry.getValue();
						
						obj2.put("genomes", StringUtils.join(genomeIdsStr, ""));
						obj2.put("genome_count", pivotGenomes.size());
						obj2.put("feature_count", count);	
						
						figfams.put(pivotEntry.getValue(), obj2);
					}
				}
			}
			end_ms = System.currentTimeMillis();
			
			System.out.println("Procesing time - 1st - "+ (end_ms - start_ms));
			
		//	System.out.println(figfams.toJSONString());
		}catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		/* getting distribution of aa length in each proteim family (dnafeature: need to modify the type of figfam_id column) */
		/* *:*&fq=gid:(252168 OR 25663 OR 244652 OR 223303)&rows=0&stats=true&stats.field=aa_length&stats.facet=figfam_id */
		
		solr_query = new SolrQuery("*:*");
		solr_query.addFilterQuery(getSolrQuery(req));
		solr_query.setRows(0);
		solr_query.set("stats", "true");
		solr_query.set("stats.field", "aa_length");
		solr_query.set("stats.facet", "figfam_id");
		
		//System.out.println(solr_query);
		
		try {
			start_ms = System.currentTimeMillis();
			QueryResponse qr = server.query(solr_query, SolrRequest.METHOD.POST);
			end_ms = System.currentTimeMillis();
			System.out.println("Query time - 2nd - "+ (end_ms - start_ms));
			start_ms = System.currentTimeMillis();
			FieldStatsInfo stats = qr.getFieldStatsInfo().get("aa_length");
			List<FieldStatsInfo> fieldStats = stats.getFacets().get("figfam_id");
			for (FieldStatsInfo fieldStat : fieldStats) {
				JSONObject obj = (JSONObject)figfams.get(fieldStat.getName());
				JSONObject obj2 = new JSONObject();
				obj2.put("max", fieldStat.getMax());
				obj2.put("min", fieldStat.getMin());
				obj2.put("mean", fieldStat.getMean());
				obj2.put("stddev", Math.round(fieldStat.getStddev() * 1000) / (double) 1000);
				
				if(obj == null)
					obj = new JSONObject();
				
				obj.put("stats", obj2);
				figfams.put(fieldStat.getName(), obj);	
			}
			end_ms = System.currentTimeMillis();
			
			System.out.println("Processing time - 2nd - " + (end_ms-start_ms));

			//System.out.println(figfams.toJSONString());
		}catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		/* # getting distinct figfam_product */
		/* *:*&fq=gid:(252168 OR 25663 OR 244652 OR 223303) AND figfam_id:(FIG00002864 OR FIG00450233)&group=true&group.field=figfam_id&fl=figfam_id,figfam_product*/

		solr.setCurrentInstance("FigFamDictionary");
		server = solr.getServer();
		
		solr_query = new SolrQuery();
		solr_query.setQuery("*:*");
		solr_query.addFilterQuery("figfam_id:(" + figfam_ids + ")");
		solr_query.addField("figfam_id, figfam_product");
		solr_query.setRows(figfams.size());
		//System.out.println(solr_query);
		
		try {
			start_ms = System.currentTimeMillis();
			QueryResponse qr = server.query(solr_query, SolrRequest.METHOD.POST);
			end_ms = System.currentTimeMillis();
			System.out.println("Query time - 3rd - "+ (end_ms - start_ms));
			start_ms = System.currentTimeMillis();
			SolrDocumentList sdl = qr.getResults();
			
			for (SolrDocument d : sdl) {
				JSONObject obj = null;
				String k = "", description = "";
				for (Iterator<Map.Entry<String, Object>> i = d.iterator(); i.hasNext();) {
					Map.Entry<String, Object> el = i.next();
					if (el.getKey().equals("figfam_id")){
						k = el.getValue().toString();
						if(!description.equals("")){
							obj = (JSONObject)figfams.get(k);
							obj.put("description", description);
							figfams.put(k, obj);
						}
					}
					// System.out.println(el.getValue().toString());
					
					if (el.getKey().equals("figfam_product")){
						if(!k.equals("")){
							obj = (JSONObject)figfams.get(k);
							obj.put("description", el.getValue().toString());
							figfams.put(k, obj);
						}else{
							description = el.getValue().toString();
						}
					}
				}
			}
			end_ms = System.currentTimeMillis();
			
			System.out.println("Processing time - 3rd - " + (end_ms-start_ms));

			//System.out.println(figfams.toJSONString());
			
		}catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		
		start_ms = System.currentTimeMillis();
		//writer.write(figfams.toString());
		figfams.writeJSONString(writer);
		end_ms = System.currentTimeMillis();
		System.out.println("Writing to response - "+ (end_ms - start_ms));
		
	}
	

	public void getGroupStats(ResourceRequest req, BufferedWriter writer, String type) throws IOException {

		GenomeBitSetter[] idFinder = getGenomeBitSetters(req);
		if ((idFinder != null) && (0 < idFinder.length)) {

			StringBuilder query = new StringBuilder(0x1000);

			if (type.equals("figfam")) {

				query.append("select ff.name figfam_id, ff.description,");
				query.append(" ff.genome_info_id,");
				query.append(" count(distinct(ff.na_feature_id)) members,");
				query.append(" min(nf.aa_length) min_len,");
				query.append(" max(nf.aa_length) max_len");
				query.append(" from app.figfamsummary ff, app.dnafeature nf");
				query.append(" where ff.na_feature_id = nf.na_feature_id");
				query.append(" and ff.genome_info_id = nf.genome_info_id");
				restrictByNumbers(" and ff.genome_info_id ", idFinder, query);
				query.append(" and nf.name = 'CDS'");
				query.append(" and nf.algorithm = 'RAST'");

				System.out.print("keyword_2" + req.getParameter("keyword"));

				if (req.getParameter("keyword") != null && !req.getParameter("keyword").equals(""))

					query.append(" and lower(ff.description) like '%" + req.getParameter("keyword") + "%'");

				query.append(" group by ff.name, ff.description, ff.genome_info_id");
				query.append(" order by ff.name");

			}
			else if (type.equals("ec")) {

				query.append("select es.ec_number ec_number, es.ec_name ec_name,");
				query.append(" es.genome_info_id,");
				query.append(" count(distinct(es.na_feature_id)) members,");
				query.append(" min(nf.aa_length) min_len,");
				query.append(" max(nf.aa_length) max_len");
				query.append(" from app.ecsummary es, app.dnafeature nf");
				query.append(" where es.na_feature_id = nf.na_feature_id");
				query.append(" and es.genome_info_id = nf.genome_info_id");
				restrictByNumbers(" and es.genome_info_id ", idFinder, query);
				query.append(" and nf.name = 'CDS'");
				query.append(" and nf.algorithm = 'RAST'");

				System.out.print("keyword_2" + req.getParameter("keyword"));

				if (req.getParameter("keyword") != null && !req.getParameter("keyword").equals(""))

					query.append(" and lower(es.ec_name) like '%" + req.getParameter("keyword") + "%'");

				query.append(" group by es.ec_number, es.ec_name, es.genome_info_id");
				query.append(" order by es.ec_number");
			}

			Iterator<?> iter = getSqlIterator(query);
			if (iter.hasNext()) {
				boolean pastStart = false;
				Object[] line = replaceNulls((Object[]) iter.next());
				String figfamName = (String) (line[0]);
				String description = (String) (line[1]);
				int[] members = new int[idFinder.length];
				int genomeId = getObjectInt(line[2]);
				int setAt = Arrays.binarySearch(idFinder, new GenomeBitSetter(0, genomeId));
				int proteinCount = getObjectInt(line[3]);
				(idFinder[setAt]).addParalogs(proteinCount, members);
				int minAminos = getObjectInt(line[4]);
				int maxAminos = getObjectInt(line[5]);
				while (iter.hasNext()) {
					line = replaceNulls((Object[]) iter.next());
					String nextName = (String) (line[0]);
					;
					if (figfamName.equals(nextName)) {
						genomeId = getObjectInt(line[2]);
						setAt = Arrays.binarySearch(idFinder, new GenomeBitSetter(0, genomeId));
						int paraCount = getObjectInt(line[3]);
						(idFinder[setAt]).addParalogs(paraCount, members);
						proteinCount += paraCount;
						minAminos = Math.min(minAminos, getObjectInt(line[4]));
						maxAminos = Math.max(maxAminos, getObjectInt(line[5]));
					}
					else {
						if (pastStart) {
							writer.write("\t");
						}
						pastStart = true;
						setFigFamStat(figfamName, proteinCount, idFinder.length, members, minAminos, maxAminos,
								description, writer);
						figfamName = nextName;
						description = (String) (line[1]);
						Arrays.fill(members, 0);
						genomeId = getObjectInt(line[2]);
						setAt = Arrays.binarySearch(idFinder, new GenomeBitSetter(0, genomeId));
						proteinCount = getObjectInt(line[3]);
						(idFinder[setAt]).addParalogs(proteinCount, members);
						minAminos = getObjectInt(line[4]);
						maxAminos = getObjectInt(line[5]);
					}
				}
				if (pastStart) {
					writer.write("\t");
				}
				setFigFamStat(figfamName, proteinCount, idFinder.length, members, minAminos, maxAminos, description,
						writer);
			}
		}
	}

	private int getObjectInt(Object toConvert) {
		String text = "" + toConvert;
		return (Integer.parseInt(text));
	}

	/*
	private static void setFigFamStat(String figfamName, int proteinCount, int genomeCount, int[] members,
			int minAminos, int maxAminos, int median, double std, String description, PrintWriter writer) {
		writer.write(figfamName + "\t");
		int speciesCount = 0;
		for (int i = 0; i < genomeCount; i++) {
			int value = members[i];
			if (0 < value) {
				++speciesCount;
				value = Math.min(value, 255);
			}
			writer.write(hexDigits[value]);
		}

		writer.write("\t" + proteinCount + "\t" + speciesCount + "\t" + minAminos + "\t" + maxAminos + "\t" + median
				+ "\t" + std + "\t" + description);

	}*/

	private static void setFigFamStat(String figfamName, int proteinCount, int genomeCount, int[] members,
			int minAminos, int maxAminos, String description, BufferedWriter writer) throws IOException {
		writer.write(figfamName + "\t");
		int speciesCount = 0;
		for (int i = 0; i < genomeCount; i++) {
			int value = members[i];
			if (0 < value) {
				++speciesCount;
				value = Math.min(value, 255);
			}
			writer.write(hexDigits[value]);
		}
		writer.write("\t" + proteinCount + "\t" + speciesCount + "\t" + minAminos + "\t" + maxAminos + "\t"
				+ description);
	}

	public String getSingleFasta(String featureId) {
		String result = null;
		StringBuilder query = new StringBuilder(0x1000);
		query.append(START_BLAST_FASTA);
		query.append(featureId);
		query.append(algorithmRestrict);

		Iterator<?> iter = getSqlIterator(query);
		if (iter.hasNext()) {
			SerializableClob clobSeq = (SerializableClob) (iter.next());
			try {
				result = IOUtils.toString(clobSeq.getAsciiStream(), "UTF-8");
			}
			catch (Exception ex) {
				System.out.println("*******************");
				System.out.println(ex.toString());
				System.out.println("*******************");
			}
		}
		return result;
	}

	private Iterator<?> getSqlIterator(StringBuilder query) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		SQLQuery q = session.createSQLQuery(query.toString());

		List<?> rset = q.list();
		session.getTransaction().commit();

		return (rset.iterator());
	}

	/*
	 * private int getSqlInt(StringBuilder query) { Session session = factory.getCurrentSession();
	 * session.beginTransaction(); SQLQuery q = session.createSQLQuery(query.toString());
	 * 
	 * //List<?> rset = q.list(); session.getTransaction().commit();
	 * 
	 * 
	 * Object results = q.uniqueResult(); session.getTransaction().commit();
	 * 
	 * int result = 0; if (results != null) { return Integer.parseInt(results.toString()); } return result; }
	 * 
	 * private String getSqlString(StringBuilder query) { Session session = factory.getCurrentSession();
	 * session.beginTransaction(); SQLQuery q = session.createSQLQuery(query.toString());
	 * 
	 * Object results = q.uniqueResult(); session.getTransaction().commit();
	 * 
	 * String result = ""; if (results != null) { result = results.toString(); } return result; }
	 */

	private class SyntonyOrder implements Comparable<SyntonyOrder> {
		String groupId;

		int syntonyAt;

		SyntonyOrder(String id, int at) {
			groupId = id;
			syntonyAt = at;
			
			// System.out.println(at+" - "+id);
			
		}

		int compressAt(int orderSet) {
			if (0 <= syntonyAt) {
				syntonyAt = orderSet;
				++orderSet;
			}
			return orderSet;
		}

		String write(String prefix, PrintWriter writer) {
			if (0 <= syntonyAt) {
				writer.write(prefix + groupId + "," + syntonyAt);
				prefix = ",";
			}
			return prefix;
		}

		SyntonyOrder mergeSameId(SyntonyOrder other) {
			SyntonyOrder result = other;
			if ((this.groupId).equals(other.groupId)) {
				other.syntonyAt = -1;
				result = this;
			}
			return result;
		}

		public int compareTo(SyntonyOrder o) {
			int result = (this.groupId).compareTo(o.groupId);
			if (result == 0) {
				result = this.syntonyAt - o.syntonyAt;
			}
			return result;
		}

	}

	private class GenomeBitSetter implements Comparable<Object> {
		int alphaOrder;

		int genomeId;

		GenomeBitSetter(int order, int id) {
			alphaOrder = order;
			genomeId = id;
		}

		void addParalogs(int count, int[] toSet) {
			toSet[alphaOrder] = count;
		}

		public int compareTo(Object o) {
			return (this.genomeId - ((GenomeBitSetter) (o)).genomeId);
		}
	}

	private class FigfamFeature implements Comparable<Object> {
		int featureId;

		String figfamName;

		String figfamProduct;

		String genomeName = null;

		int genomeId = 0;

		String accession = null;

		String sourceId = null;

		String name = null;

		String startMax = null;

		String endMin = null;

		String span = null;

		String isReversed = null;

		int aaLength = 0;

		String gene = null;

		String product = null;

		FigfamFeature(Object[] selects) {
			featureId = getObjectInt(selects[0]);
			figfamName = (String) selects[1];
			figfamProduct = (String) selects[2];
		}

		int addDetails(Object[] selects, ArrayList<FigfamFeature> keepers) {
			int result = (featureId - getObjectInt(selects[0]));
			if (result == 0) {
				genomeName = "" + selects[1];
				genomeId = getObjectInt(selects[2]);
				accession = "" + selects[3];
				sourceId = "" + selects[4];
				name = "" + selects[5];
				startMax = "" + selects[6];
				endMin = "" + selects[7];
				span = "" + selects[8];
				isReversed = "" + selects[9];
				aaLength = getObjectInt(selects[10]);
				gene = "" + selects[11];
				product = "" + selects[12];
				keepers.add(this);
			}
			return result;
		}

		void stampHeader(int count, PrintWriter writer) {
			writer.write(count + "\t" + figfamName + "\t" + figfamProduct);
		}

		void stampHeader(int count, ArrayList<String> collect) {
			collect.add("" + count);
			collect.add(figfamName);
			collect.add(figfamProduct);
		}

		void stampDetails(ArrayList<String> collect) {
			collect.add(genomeName);
			collect.add(accession);
			collect.add(sourceId);
			collect.add(name);
			collect.add(startMax);
			collect.add(endMin);
			collect.add(span);
			collect.add(isReversed);
			collect.add("" + aaLength);
			collect.add(gene);
			collect.add(product);
		}

		void stampDetails(PrintWriter writer) {
			writer.write("\t" + genomeName);
			writer.write("\t" + genomeId);
			writer.write("\t" + accession);
			writer.write("\t" + featureId);
			writer.write("\t" + sourceId);
			writer.write("\t" + name);
			writer.write("\t" + startMax);
			writer.write("\t" + endMin);
			writer.write("\t" + span);
			writer.write("\t" + isReversed);
			writer.write("\t" + aaLength);
			writer.write("\t" + gene);
			writer.write("\t" + product);
		}

		boolean inSameGroup(FigfamFeature o) {
			return (this.figfamName).equals(o.figfamName);
		}

		public int compareTo(Object other) {
			FigfamFeature o = (FigfamFeature) other;
			int result = (this.figfamName).compareTo(o.figfamName);
			if (result == 0) {
				result = (this.genomeName).compareTo(o.genomeName);
			}
			return result;
		}
	}
	/*
	private class SolrFamily {

		String figfam_id = null;

		String figfam_product = null;

		String na_feature_id = "";

		String gid = "";

		int features = 0;

		int min_length = 0;

		int max_length = 0;

		ArrayList<Integer> lengths = new ArrayList<Integer>();

		SolrFamily(JSONObject selects) {
			this.figfam_id = selects.get("figfam_id").toString();
			this.figfam_product = selects.get("figfam_product").toString();
			this.na_feature_id = selects.get("na_feature_id").toString();
			this.gid = selects.get("gid").toString();
			this.min_length = Integer.parseInt(selects.get("aa_length").toString());
			this.max_length = Integer.parseInt(selects.get("aa_length").toString());
			this.features = 1;
			this.lengths.add(Integer.parseInt(selects.get("aa_length").toString()));
		}

		public int getTheMedian() {

			Collections.sort(this.lengths);
			int data = this.lengths.get(this.lengths.size() / 2);
			return data;

		}

		public double getTheStd(ArrayList<Integer> list) {

			double max = list.get(0).doubleValue();
			double mean = 0.0;
			double brackets = 0.0;

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).doubleValue() > max)
					max = list.get(i).doubleValue();
				mean += list.get(i).doubleValue();
			}
			mean = mean / list.size();

			for (int j = 0; j < list.size(); j++) {
				brackets += (Math.pow((list.get(j).doubleValue() - mean), 2) / (list.size() - 1));
			}
			brackets = Math.sqrt(brackets);
			// System.out.print(brackets);
			if (brackets > 0) {
				DecimalFormat twoDForm = new DecimalFormat("#.#");
				return Double.valueOf(twoDForm.format(brackets));
			}
			else {
				return 0.0;
			}
		}
	}
	*/
	public String getSolrQuery(ResourceRequest req) {
		String keyword = "";
		SolrInterface solr = new SolrInterface();

		if (req.getParameter("keyword") != null && !req.getParameter("keyword").equals(""))
			keyword += "(" + solr.KeywordReplace(req.getParameter("keyword")) + ")";

		String listText = req.getParameter("genomeIds");

		if (listText != null) {
			if (req.getParameter("keyword") != null && !req.getParameter("keyword").equals(""))
				keyword += " AND ";
			keyword += "(gid:(" + listText.replaceAll(",", " OR ") + "))";
		}
		// System.out.println("keyword ->" + keyword);
		return keyword;
	}
}
