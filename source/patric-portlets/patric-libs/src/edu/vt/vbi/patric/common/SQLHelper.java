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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SQLHelper {

	/*
	 * @ called by DBSummary
	 */
	public static HashMap<String, Object> getProcessColumns(String featuretype) {
		HashMap<String, Object> pr = new HashMap<String, Object>();
		HashMap<?, ?> fieldHash = getFieldHash();

		ArrayList<String> _field = new ArrayList<String>();
		ArrayList<String> _header = new ArrayList<String>();
		Iterator<?> itr = null;
		HashMap<?, ?> _f;
		String _col;

		if (getFeaturetypeFieldHash(featuretype) != null) {
			itr = ((List<?>) getFeaturetypeFieldHash(featuretype).get("column")).iterator();
			while (itr.hasNext()) {
				_col = (String) itr.next();
				_f = (HashMap<?, ?>) fieldHash.get(_col);
				_field.add(_f.get("sql_field").toString());
				_header.add(_f.get("display_name").toString());
			}
		}

		pr.put("field", _field);
		pr.put("header", _header);
		return pr;
	}

	public static HashMap<String, String> getDisplayColumns(String featuretype) {
		HashMap<String, String> dp = new HashMap<String, String>();
		HashMap<?, ?> fieldHash = getFieldHash();

		String dp_field = "";
		StringBuilder dp_header = new StringBuilder();
		String _col;
		Iterator<?> itr = null;
		if (getFeaturetypeFieldHash(featuretype) != null) {
			itr = ((List<?>) getFeaturetypeFieldHash(featuretype).get("column")).iterator();
		}
		HashMap<?, ?> _field;
		while (itr.hasNext()) {
			_col = (String) itr.next();
			_field = (HashMap<?, ?>) fieldHash.get(_col);

			// dp_field += ",'"+_col+"'";
			dp_field += ",'" + _field.get("sql_field") + "'";
			dp_header.append(", {");
			dp_header.append("header:'" + _field.get("display_name") + "'");
			// dp_header.append(", dataIndex:'"+_col+"'");
			dp_header.append(", dataIndex:'" + _field.get("sql_field") + "'");

			if (!_field.get("display_width").equals("")) {
				// dp_header.append(", maxWidth:"+_field.get("display_width")+" ");
			}
			if (!_field.get("renderer").equals("")) {
				dp_header.append(", renderer:" + _field.get("renderer") + " ");
			}
			if (!_field.get("display_alignment").equals("")) {
				dp_header.append(", align:'" + _field.get("display_alignment") + "' ");
			}
			if (!_field.get("display_flex").equals("0")) {
				dp_header.append(", flex:" + _field.get("display_flex") + " ");
			}
			if (!_field.get("renderer").equals("")) {
				dp_header.append(", renderer:" + _field.get("renderer") + " ");
			}
			dp_header.append("} ");
		}
		dp.put("field", dp_field);
		dp.put("header", dp_header.toString());

		return dp;
	}

	private static HashMap<String, Object> getDefaultField() {
		HashMap<String, Object> _field = new HashMap<String, Object>();
		_field.put("sql_field", "");
		_field.put("sql_query", "");
		_field.put("searchable", true);
		_field.put("sortable", true);
		_field.put("display_name", "");
		_field.put("display_alignment", "center");
		_field.put("renderer", "");
		_field.put("display_width", "");
		_field.put("display_flex", "1");
		return _field;
	}

	private static HashMap<String, HashMap<String, Object>> getFieldHash() {
		// width, alignment?

		HashMap<String, Object> _gene_symbol = getDefaultField();
		_gene_symbol.put("sql_field", "gene");
		_gene_symbol.put("sql_query", "gene");
		_gene_symbol.put("display_name", "Gene Symbol");
		_gene_symbol.put("display_width", "70");
		_gene_symbol.put("renderer", "BasicRenderer");
		_gene_symbol.put("display_flex", "1");

		HashMap<String, Object> _protein_id = getDefaultField();
		_protein_id.put("sql_field", "protein_id");
		_protein_id.put("sql_query", "protein_id");
		_protein_id.put("display_name", "Protein ID");
		_protein_id.put("renderer", "renderProteinID");
		_protein_id.put("display_width", "80");
		_protein_id.put("display_flex", "1");

		HashMap<String, Object> _ec_num = getDefaultField();
		_ec_num.put("sql_field", "ec_number");
		_ec_num.put("sql_query", "ec_number");
		_ec_num.put("display_name", "EC Number");
		_ec_num.put("renderer", "BasicRenderer");
		_ec_num.put("display_flex", "1");

		HashMap<String, Object> _aa_length = getDefaultField();
		_aa_length.put("sql_field", "aa_length");
		_aa_length.put("sql_query", "aa_length");
		_aa_length.put("searchable", false);
		_aa_length.put("display_name", "Length (AA)");
		_aa_length.put("display_alignment", "right");
		_aa_length.put("display_width", "60");
		_aa_length.put("renderer", "BasicRenderer");
		_aa_length.put("display_flex", "1");

		HashMap<String, Object> _product = getDefaultField();
		_product.put("sql_field", "product");
		_product.put("sql_query", "product");
		_product.put("display_name", "Product Description");
		_product.put("display_alignment", "left");
		_product.put("renderer", "BasicRenderer");
		_product.put("display_flex", "3");

		HashMap<String, Object> _pseudo_gene = getDefaultField();
		_pseudo_gene.put("sql_field", "is_pseudo");
		_pseudo_gene.put("sql_query", "is_pseudo");
		_pseudo_gene.put("searchable", false);
		_pseudo_gene.put("display_name", "Pseudo Gene");
		_pseudo_gene.put("display_alignment", "center");
		_pseudo_gene.put("renderer", "displayPseudoGene");
		_pseudo_gene.put("display_flex", "1");

		HashMap<String, Object> _bound_moiety = getDefaultField();
		_bound_moiety.put("sql_field", "bound_moiety");
		_bound_moiety.put("sql_query", "bound_moiety");
		_bound_moiety.put("display_name", "Bound Moiety");
		_bound_moiety.put("display_width", "70");
		_bound_moiety.put("renderer", "BasicRenderer");
		_bound_moiety.put("display_flex", "1");

		HashMap<String, Object> _anti_codon = getDefaultField();
		_anti_codon.put("sql_field", "anticodon");
		_anti_codon.put("sql_query", "anticodon");
		_anti_codon.put("display_name", "Anticodon");
		_anti_codon.put("display_width", "70");
		_anti_codon.put("renderer", "BasicRenderer");
		_anti_codon.put("display_flex", "1");

		HashMap<String, HashMap<String, Object>> _hash = new HashMap<String, HashMap<String, Object>>();
		_hash.put("gene_symbol", _gene_symbol);
		_hash.put("protein_id", _protein_id);
		_hash.put("ec_num", _ec_num);
		_hash.put("aa_length", _aa_length);
		_hash.put("product", _product);
		_hash.put("pseudo_gene", _pseudo_gene);
		_hash.put("bound_moiety", _bound_moiety);
		_hash.put("anti_codon", _anti_codon);

		return _hash;
	}

	public static HashMap<?, ?> getFeaturetypeFieldHash(String featuretype) {
		HashMap<String, HashMap<?, ?>> _hash = new HashMap<String, HashMap<?, ?>>();

		HashMap<String, Object> _cds = new HashMap<String, Object>();
		_cds.put("column", Arrays.asList(new String[] { "protein_id", "aa_length", "gene_symbol", "product" }));
		// _cds.put("table", "dots.transcript");

		HashMap<String, Object> _gene = new HashMap<String, Object>();
		_gene.put("column", Arrays.asList(new String[] { "gene_symbol", "pseudo_gene" }));
		// _gene.put("table", "dots.genefeature");

		HashMap<String, Object> _mat_peptide = new HashMap<String, Object>();
		_mat_peptide.put("column", Arrays.asList(new String[] { "aa_length", "gene_symbol", "product" }));
		// _mat_peptide.put("table", "dots.proteinfeature");

		HashMap<String, Object> _misc_feature = new HashMap<String, Object>();
		_misc_feature.put("column", Arrays.asList(new String[] { "product" }));
		// _misc_feature.put("table", "dots.miscellaneous");

		HashMap<String, Object> _misc_binding = new HashMap<String, Object>();
		_misc_binding.put("column", Arrays.asList(new String[] { "bound_moiety", "product" }));
		// _misc_binding.put("table", "dots.miscellaneous");

		HashMap<String, Object> _misc_signal = new HashMap<String, Object>();
		_misc_signal.put("column", Arrays.asList(new String[] { "product" }));
		// _misc_signal.put("table", "dots.miscellaneous");

		HashMap<String, Object> _misc_structure = new HashMap<String, Object>();
		_misc_structure.put("column", Arrays.asList(new String[] { "product" }));
		// _misc_structure.put("table", "dots.miscellaneous");

		HashMap<String, Object> _mRNA = new HashMap<String, Object>();
		_mRNA.put("column", Arrays.asList(new String[] { "anti_codon", "product" }));
		// _mRNA.put("table", "dots.rnatype");

		HashMap<String, Object> _misc_RNA = new HashMap<String, Object>();
		_misc_RNA.put("column", Arrays.asList(new String[] { "anti_codon", "product" }));
		// _misc_RNA.put("table", "dots.rnatype");

		HashMap<String, Object> _ncRNA = new HashMap<String, Object>();
		_ncRNA.put("column", Arrays.asList(new String[] { "gene_symbol", "product" }));
		// _ncRNA.put("table", "dots.rnatype");

		HashMap<String, Object> _rRNA = new HashMap<String, Object>();
		_rRNA.put("column", Arrays.asList(new String[] { "gene_symbol", "product" }));
		// _rRNA.put("table", "dots.rnatype");

		HashMap<String, Object> _tRNA = new HashMap<String, Object>();
		_tRNA.put("column", Arrays.asList(new String[] { "anti_codon", "product" }));
		// _tRNA.put("table", "dots.rnatype");

		HashMap<String, Object> _all = new HashMap<String, Object>();
		_all.put("column", Arrays.asList(new String[] { "gene_symbol", "product" }));
		// _all.put("table", "app.dnafeature");

		_hash.put("CDS", _cds);
		_hash.put("gene", _gene);
		_hash.put("mat_peptide", _mat_peptide);
		_hash.put("misc_feature", _misc_feature);
		_hash.put("misc_binding", _misc_binding);
		_hash.put("misc_signal", _misc_signal);
		_hash.put("misc_structure", _misc_structure);
		_hash.put("mRNA", _mRNA);
		_hash.put("misc_RNA", _misc_RNA);
		_hash.put("ncRNA", _ncRNA);
		_hash.put("rRNA", _rRNA);
		_hash.put("tRNA", _tRNA);
		_hash.put("all", _all);

		if (featuretype == null) {
			return _hash;
		}
		else if (!featuretype.equalsIgnoreCase("") && _hash.containsKey(featuretype)) {
			return _hash.get(featuretype);
		}
		else {
			return null;
		}
	}

	/*
	 * @ called by DBSearch
	 */
	public static ArrayList<ArrayList<String>> splitIDStringtoArray(String ids, String delimiter) {
		List<String> lstIDS = Arrays.asList(ids.split(delimiter));

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int countIds = lstIDS.size();
		int countGroups = (int) java.lang.Math.floor(countIds / 333);
		int i = 1;
		for (i = 1; i <= countGroups; i++) {
			ArrayList<String> grp = new ArrayList<String>(lstIDS.subList((i - 1) * 333, i * 333));
			result.add(grp);
		}
		ArrayList<String> grp = new ArrayList<String>(lstIDS.subList((i - 1) * 333, countIds));
		result.add(grp);

		return result;
	}
}
