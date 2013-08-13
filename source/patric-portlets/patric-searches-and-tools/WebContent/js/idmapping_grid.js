function loadFBCD() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, checkbox = Page.getCheckBox() || null, plugin = property.plugin, plugintype = property.plugintype, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0, header, requested_data;

	(plugin && plugintype == "checkbox" && !checkbox) ? checkbox = Page.checkbox = createCheckBox(property.name) : checkbox.updateCheckAllIcon();

	if (hash.to == "RefSeq Locus Tag") {
		header = "RefSeq Locus Tag";
		requested_data = "refseq_source_id";
	} else if (hash.to == "RefSeq") {
		header = "Protein ID";
		requested_data = "protein_id";
	} else if (hash.to == "Gene ID") {
		header = "GeneID";
		requested_data = "gene_id";
	} else if (hash.to == "GI") {
		header = "GI";
		requested_data = "gi_number";
	} else if (hash.to == "PATRIC ID") {
		header = "PATRIC ID";
		requested_data = "na_feature_id";
	} else if (hash.to == "PATRIC Locus Tag") {
		if (hash.from == "RefSeq Locus Tag") {
			requested_data = "refseq_source_id";
		} else if (hash.from == "RefSeq") {
			requested_data = "protein_id";
		} else if (hash.from == "Gene ID") {
			requested_data = "gene_id";
		} else if (hash.from == "GI") {
			requested_data = "gi_number";
		} else if (hash.from == "PATRIC ID") {
			requested_data = "na_feature_id";
		} else if (hash.from == "PSEED ID") {
			requested_data = "pseed_id";
		} else {
			requested_data = "requested_data";
		}
		header = hash.from;
	} else if (hash.to == "PSEED ID") {
		requested_data = "pseed_id";
		header = "PSEED ID";
	} else {
		header = hash.to;
		requested_data = "requested_data";
	}

	if (!property.scm[which]) {
		if (hash.to == "UniProtKB-ID" || hash.from == "UniProtKB-ID")
			property.scm[which] = eval("[checkbox," + "{header:'Genome Name', flex:2, dataIndex: 'genome_name', renderer:renderGenomeName}," + "{header:'Accession', flex:1, align:'center', hidden: true, dataIndex: 'accession', renderer:renderAccession}," + "{header:'Locus Tag', flex:1, align:'center', dataIndex: 'locus_tag', renderer:renderLocusTag}, " +
			//	"{header:'Gene Symbol', flex:1, align:'center', dataIndex: 'gene', renderer:BasicRenderer}, "+
			"{header:'UniProtKB-ID', flex:1, dataIndex:'uniprotkb_accession', renderer:renderURL}, " + "{header:'UniProtKB-Accession', flex:1, dataIndex:'uniprot_id', renderer:renderURL}, " + "{header:'Genome Browser', flex:1, hidden: true, align:'center', dataIndex:'',renderer:renderGenomeBrowserByFeatureIDMapping}," +
			//	"{header:'Annotation', flex:1, align:'center',dataIndex: 'algorithm', renderer:BasicRenderer}, "+
			"{header:'Start', flex:1, hidden: true, dataIndex:'start_max', align:'center', renderer:BasicRenderer}, " + "{header:'End', flex:1, hidden: true, dataIndex:'end_min', align:'center', renderer:BasicRenderer}, " + "{header:'Length (NT)', flex:1, hidden: true, dataIndex:'na_length', align:'center', renderer:BasicRenderer}," + "{header:'Strand', flex:1, hidden: true, dataIndex:'strand',align:'center', renderer:BasicRenderer}," + "{header:'Product Description', flex:2, dataIndex:'product', renderer:BasicRenderer}]");
		else
			property.scm[which] = eval("[checkbox, " + "{header:'Genome Name', flex:2, dataIndex: 'genome_name', renderer:renderGenomeName}," + "{header:'Accession', flex:1, align:'center', hidden: true, dataIndex: 'accession', renderer:renderAccession}," + "{header:'Locus Tag', flex:1, align:'center', dataIndex: 'locus_tag', renderer:renderLocusTag}," + "{header:'" + header + "', flex:1, align:'center', dataIndex:'" + requested_data + "', renderer:renderURL}, " +
			//	"{header:'Gene Symbol', flex:1, align:'center', dataIndex: 'gene', renderer:BasicRenderer}, "+
			"{header:'Genome Browser', flex:1, hidden: true, dataIndex:'', align:'center', renderer:renderGenomeBrowserByFeatureIDMapping}," +
			//	"{header:'Annotation', flex:1, align:'center', dataIndex: 'algorithm', renderer:BasicRenderer}, "+
			"{header:'Start', flex:1, hidden: true, dataIndex:'start_max', align:'center', renderer:BasicRenderer}, " + "{header:'End', flex:1, hidden: true, dataIndex:'end_min', align:'center', renderer:BasicRenderer}, " + "{header:'Length (NT)', flex:1, hidden: true, dataIndex:'na_length', align:'center', renderer:BasicRenderer}, " + "{header:'Strand', flex:1, hidden: true, dataIndex:'strand',align:'center', renderer:BasicRenderer}," + "{header:'Product Description', flex:2, dataIndex:'product', renderer:BasicRenderer}]");
	}
	loadGrid();
}

function getExtraParams() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	return {
		pk : hash.key
	};
}

function getSelectedFeatures() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), sl = Page.getCheckBox().getSelections(), i, fids = property.fids;

	for ( i = 0; i < sl.length; i++)
		fids.push(sl[i].data.na_feature_id);

}

function DownloadFile() {"use strict";

	var form = Ext.getDom("fTableForm");

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
	form.fileformat.value = arguments[0];
	form.target = "";
	getHashFieldsToDownload(form);
	form.submit();
}

function renderGenomeBrowserByFeatureIDMapping(value, p, record) {

	var tracks = "DNA,", Page = $Page, property = Page.getPageProperties(), hash = property.hash, window_start = Math.max(0, (record.data.start_max - 1000)), window_end = parseInt(record.data.end_min) + 1000;
	;

	if (record.data.feature_type != null && (record.data.feature_type == "CDS" || record.data.feature_type == "gene")) {
		tracks += record.data.feature_type;
	} else if (record.data.name != null && (record.data.name == "CDS" || record.data.name == "gene")) {
		tracks += record.data.name;
	} else if (record.data.feature_type != null && record.data.feature_type.indexOf(/.*RNA/) != -1) {
		tracks += "RNA";
	} else if (record.data.name != null && record.data.name.indexOf(/.*RNA/) != -1) {
		tracks += "RNA";
	} else {
		tracks += "Misc";
	}

	if (record.data.annotation == "PATRIC") {
		tracks += "(PATRIC)";
	} else if (record.data.algorithm == "PATRIC") {
		tracks += "(PATRIC)";
	} else if (record.data.annotation == "Legacy BRC") {
		tracks += "(BRC)";
	} else if (record.data.algorithm == "Legacy BRC") {
		tracks += "(BRC)";
	} else {
		tracks += "(RefSeq)";
	}

	if (hash.to == "RefSeq" || hash.to == "RefSeq LocusTag") {
		tracks += ",";
		if (record.data.feature_type != null && (record.data.feature_type == "CDS" || record.data.feature_type == "gene")) {
			tracks += record.data.feature_type;
		} else if (record.data.name != null && (record.data.name == "CDS" || record.data.name == "gene")) {
			tracks += record.data.name;
		} else if (record.data.feature_type != null && record.data.feature_type.indexOf(/.*RNA/) != -1) {
			tracks += "RNA";
		} else if (record.data.name != null && record.data.name.indexOf(/.*RNA/) != -1) {
			tracks += "RNA";
		} else {
			tracks += "Misc";
		}

		tracks += "(RefSeq)";
	}

	return Ext.String.format('<a href="GenomeBrowser?cType=genome&cId={0}&loc={1}:{2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif"  alt="Genome Browser" style="margin:-4px" /></a>', (!record.data.genome_info_id) ? record.data.gid : record.data.genome_info_id, record.data.accession, window_start, window_end, tracks);
}

function CallBack() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0, store = Page.getStore(which), requested_data = "id";

	if (hash.to == "UniProtKB-ID")
		requested_data = "uniprotkb_accession";
	else if (hash.to == "RefSeq Locus Tag")
		requested_data = "refseq_source_id";
	else if (hash.to == "RefSeq")
		requested_data = "rm.protein_id";
	else if (hash.to == "Gene ID")
		requested_data = "gene_id";
	else if (hash.to == "GI")
		requested_data = "gi_number";
	else if (hash.to == "PATRIC Locus Tag")
		requested_data = "source_id";
	else if (hash.to == "PATRIC ID")
		requested_data = "na_feature_id";
	else if (hash.to == "PSEED ID")
		requested_data = "pseed_id";

	Ext.Ajax.request({
		url : "/patric-searches-and-tools/jsp/get_idmapping_to_count.json.jsp",
		method : 'POST',
		params : {
			field : requested_data,
			from : hash.from,
			to : hash.to,
			keyword : Ext.getDom("keyword").value
		},
		success : function(response, opts) {
			if (store.getTotalCount() > property.keyword_size)
				Ext.getDom('grid_result_summary').innerHTML = "<b>" + property.keyword_size + " out of " + property.keyword_size + " " + hash.from + "s mapped to " + Ext.JSON.decode(response.responseText).result + " " + hash.to + "s</b><br/>";
			else
				Ext.getDom('grid_result_summary').innerHTML = "<b>" + store.totalCount + " out of " + property.keyword_size + " " + hash.from + "s mapped to " + Ext.JSON.decode(response.responseText).result + " " + hash.to + "s</b><br/>";
		}
	});

}

function renderURL(value, p, record) {
	var Page = $Page, property = Page.getPageProperties();

	if (property.renderURL)
		return Ext.String.format("<a href=\"" + property.renderURL + "{0}\" target=\"_blank\">{0}</a>", value);
	else
		return value;
}

