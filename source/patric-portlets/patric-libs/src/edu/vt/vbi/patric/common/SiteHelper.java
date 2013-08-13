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
package edu.vt.vbi.patric.common;

import javax.portlet.MimeResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.w3c.dom.Element;

import edu.vt.vbi.patric.dao.DBShared;
import edu.vt.vbi.patric.dao.ResultType;

public class SiteHelper {

	public static String getLinks(String target, String id) {
		String link = "";
		if (target.equals("taxon_overview")) {
			link = "<a href=\"Taxon?cType=taxon&amp;cId="
					+ id
					+ "\"><img src=\"/patric/images/icon_taxon.gif\" alt=\"Taxonomy Overview\" title=\"Taxonomy Overview\" /></a>";
		}
		else if (target.equals("genome_list")) {
			link = "<a href=\"GenomeList?cType=taxon&amp;cId="
					+ id
					+ "&amp;dataSource=All&amp;displayMode=genome\"><img src=\"/patric/images/icon_sequence_list.gif\" alt=\"Genome List\" title=\"Genome List\" /></a>";
		}
		else if (target.equals("feature_table")) {
			link = "<a href=\"FeatureTable?cType=taxon&amp;cId="
					+ id
					+ "&amp;featuretype=CDS&amp;annotation=All&amp;filtertype=\"><img src=\"/patric/images/icon_table.gif\" alt=\"Feature Table\" title=\"Feature Table\"/></a>";
		}
		return link;
	}

	public static String getExternalLinks(String target) {
		String link = "";

		if (target.equalsIgnoreCase("ncbi_gene")) {
			link = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=full_report&list_uids=";
		}
		else if (target.equalsIgnoreCase("ncbi_accession")) {
			link = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nucleotide&val=";
		}
		else if (target.equalsIgnoreCase("ncbi_protein") || target.equalsIgnoreCase("RefSeq")
				|| target.equalsIgnoreCase("GI")) {
			link = "http://www.ncbi.nlm.nih.gov/protein/";
		}
		else if (target.equalsIgnoreCase("RefSeq_NT")) {
			link = "http://www.ncbi.nlm.nih.gov/nuccore/"; // NC_010067.1 -
															// nucleotide db
		}
		else if (target.equalsIgnoreCase("go_term")) {
			link = "http://amigo.geneontology.org/cgi-bin/amigo/term_details?term="; // GO:0004747
		}
		else if (target.equalsIgnoreCase("ec_number")) {
			//link = "http://www.brenda-enzymes.org/php/result_flat.php4?ecno="; // 2.7.1.15
			link = "http://enzyme.expasy.org/EC/";
		}
		else if (target.equalsIgnoreCase("kegg_pathwaymap") || target.equalsIgnoreCase("KEGG")) {
			link = "http://www.genome.jp/dbget-bin/www_bget?"; // pathway+map00010
		}
		else if (target.equalsIgnoreCase("UniProtKB-Accession") || target.equalsIgnoreCase("UniProtKB-ID")) {
			link = "http://www.uniprot.org/uniprot/"; // A9MFG0 or ASTD_SALAR
		}
		else if (target.equalsIgnoreCase("UniRef100") || target.equalsIgnoreCase("UniRef90")
				|| target.equalsIgnoreCase("UniRef50")) {
			link = "http://www.uniprot.org/uniref/"; // UniRef100_A9MFG0,
														// UniRef90_B5F7J0, or
														// UniRef50_Q1C8A9
		}
		else if (target.equalsIgnoreCase("UniParc")) {
			link = "http://www.uniprot.org/uniparc/"; // UPI0001603B3F
		}
		else if (target.equalsIgnoreCase("EMBL") || target.equalsIgnoreCase("EMBL-CDS")) {
			link = "http://www.ebi.ac.uk/ena/data/view/"; // CP000880, ABX21565
		}
		else if (target.equalsIgnoreCase("GeneID")) {
			link = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&term="; // 5763416;
		}
		else if (target.equalsIgnoreCase("GenomeReviews")) {
			link = "http://www.genomereviews.ebi.ac.uk/GR/contigview?chr="; // CP000880_GR
		}
		else if (target.equalsIgnoreCase("eggNOG")) {
			link = "http://eggnog.embl.de/cgi_bin/display_multi_clusters.pl?linksource=uniprot&level=0&1="; // Q2YII1
																											// --
																											// uniprot
																											// accession
		}
		else if (target.equalsIgnoreCase("HOGENOM")) {
			link = "http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?db=HOGENOM&query="; // A9MFG0
																						// --
																						// uniprot
																						// accession
		}
		else if (target.equalsIgnoreCase("OMA")) {
			link = "http://omabrowser.org/cgi-bin/gateway.pl?f=DisplayGroup&p1="; // A9MFG0
																					// --
																					// uniprot
																					// accession
		}
		else if (target.equalsIgnoreCase("ProtClustDB")) {
			link = "http://www.ncbi.nlm.nih.gov/sites/entrez?Db=proteinclusters&Cmd=DetailsSearch&Term="; // A9MFG0
																											// --
																											// uniprot
																											// accession
		}
		else if (target.equalsIgnoreCase("BioCyc")) {
			link = "http://biocyc.org/getid?id="; // BMEL359391:BAB2_0179-MONOMER
		}
		else if (target.equalsIgnoreCase("NMPDR")) {
			link = "http://www.nmpdr.org/linkin.cgi?id="; // fig|382638.8.peg.1669"
		}
		else if (target.equalsIgnoreCase("EnsemblGenome") || target.equalsIgnoreCase("EnsemblGenome_TRS")
				|| target.equalsIgnoreCase("EnsemblGenome_PRO")) {
			link = "http://www.ensemblgenomes.org/id/"; // EBMYCT00000005579
		}
		else if (target.equalsIgnoreCase("BEIR")) {
			link = "http://www.beiresources.org/Catalog/ItemDetails/tabid/522/Default.aspx?Template=Clones&BEINum=";
		}
		else if (target.equalsIgnoreCase("PDB")) {
			link = "Jmol?structureID=";
		}
		return link;
	}

	/*
	 * @ called by patric-overview/WebContents/WEB-INF/jsp/feature_summary.jsp
	 */
	public static String getGenusByStructuralGenomicsCenter(String name) {
		// 'Mycobacterium', 'Bartonella', 'Brucella', 'Ehrlichia', 'Rickettsia',
		// 'Burkholderia', 'Borrelia'

		// 1763,138,780,773,234,943,32008
		String ssgcid = "Mycobacterium|Bartonella|Brucella|Ehrlichia|Rickettsia|Burkholderia|Borrelia";

		// 'Bacillus', 'Listeria', 'Staphylococcus', 'Streptococcus',
		// 'Clostridium',
		// 'Coxiella', 'Escherichia', 'Francisella', 'Salmonella', 'Shigella',
		// 'Vibrio', 'Yersinia', 'Campylobacter', 'Helicobacter'

		// 1485,1279,1386,1301,1637,194,662,209,776,620,262,561,629,590

		String csgid = "Bacillus|Listeria|Staphylococcus|Streptococcus|Clostridium|Coxiella|Escherichia|Francisella|Salmonella|Shigella|Vibrio|Yersinia|Campylobacter|Helicobacter";

		if (name.equals("ssgcid")) {
			return ssgcid;
		}
		else if (name.equals("csgid")) {
			return csgid;
		}
		else {
			return "";
		}
	}

	public static void addHtmlMetaElements(RenderRequest req, RenderResponse res, String key, Element el) {
		res.addProperty(key, el);
	}

	public void setHtmlMetaElements(RenderRequest req, RenderResponse res, String context) {
		String strTitle = "PATRIC::";
		String strKeywords = "";
		String contextType = req.getParameter("context_type");
		String contextId = req.getParameter("context_id");
		int validContextId = -1;

		try {
			validContextId = Integer.parseInt(contextId);
		}
		catch (NumberFormatException ex) {
		}

		if (contextType != null && contextId != null && validContextId > 0) {
			// Get taxon/genome/feature info
			DBShared db_shared = new DBShared();
			ResultType org = new ResultType();

			if (contextType.equals("taxon")) {
				org = db_shared.getNamesFromTaxonId(validContextId);
				if (org != null) {
					strTitle += org.get("name") + "::" + context;
					strKeywords = context + ", " + org.get("name") + ", PATRIC";
				}
				else {
					strTitle += context;
				}
			}
			else if (contextType.equals("genome")) {
				org = db_shared.getNamesFromGenomeInfoId(contextId);
				if (org != null) {
					strTitle += org.get("genome_name") + "::" + context;
					strKeywords = context + ", " + org.get("genome_name") + ", PATRIC";
				}
			}
			else if (contextType.equals("feature")) {
				org = db_shared.getNamesFromNaFeatureId(contextId);
				if (org != null) {
					strTitle += org.get("source_id") + ":" + org.get("feature_name") + "::" + context;
					strKeywords = context + ", " + org.get("source_id") + ":" + org.get("feature_name") + ", PATRIC";
				}
			}
		}
		else {
			strTitle += context;
			strKeywords = context + ", PATRIC";
		}

		// Setup elements
		Element elTitle = res.createElement("title");
		Element elKeywords = res.createElement("meta");

		elTitle.setTextContent(strTitle);
		elKeywords.setAttribute("name", "Keywords");
		elKeywords.setAttribute("content", strKeywords);

		// Set to headerContents
		res.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elTitle);
		res.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elKeywords);
	}
}
