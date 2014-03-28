function MaskGrid(total, fn) {

	if (total > 0) {
		fn();
	} else {
		if (Ext.getDom("grid_result_summary")) {
			Ext.getDom("grid_result_summary").innerHTML = "<b>0 results found</b>";
			Ext.get("PATRICGrid").mask('No results found');
			$('#PATRICGrid').find('.x-mask-msg > div').css('background-image', 'none').css("padding", "5px 10px 5px 5px");
		}
	}
}

function open_in_new_tab(url) {
	window.open(url, '_new', "menubar=1,resizable=1,scrollbars=1, fullscreen=1, toolbar=1,titlebar=1,status=1");
}

function setPageSize(config) {
	var count = 0;

	if (config.stateId) {
		for (sId in config.stateId) {
			config.hash.pS[count] = Ext.state.Manager.get(sId).pS;
			count++;
		}
	}
}

function ApplyState(state, grid) {
	var columns = state.columns;

	columns.forEach(function(element, index, array) {
		if (element.hasOwnProperty("hidden")) {
			Ext.getCmp(element.id).setVisible(!element.hidden);
		}
	});

	grid.headerCt.applyColumnsState(columns);
}

function getCheckBoxImage(checkState) {
	return "/patric/images/" + checkState + ".gif";
}

function createCheckBox(name) {

	return Ext.create('Ext.grid.CheckColumn', {
		id : 'checkBOX',
		width : 30,
		header : "<div style=\"padding:3px 1px 1px 1px;\"><img id=\"checkbox_headerBox\" onclick=\"javascript:flipHeaderCheckbox(this.src)\" src=\"" + getCheckBoxImage("unchecked") + "\"></div>",
		dataIndex : name,
		menuDisabled : true,
		hideable : false,
		sortable : false,
		draggable : false,
		resizable : false
	});
}

//defining renderer
function renderGenomeName(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	if (record.data.genome_info_id == null || record.data.genome_info_id == "0" || record.data.genome_info_id == "")
		return Ext.String.format('<a href="Genome?cType=genome&cId={0}">{1}</a>', record.data.gid, value);
	else
		return Ext.String.format('<a href="Genome?cType=genome&cId={0}">{1}</a>', record.data.genome_info_id, value);
}

function BasicRenderer(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return value;

}

function renderAccession(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	if (value.match(/^NC.*/) || value.match(/^NZ.*/)) {
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nucleotide&val={0}" target="_blank">{0}</a>', value);
	} else {
		return value;
	}
}

function renderGenomeBrowserByFeature(value, metadata, record, rowIndex, colIndex, store) {
	//metadata.tdAttr = 'data-qtip="Genome Browser" data-qclass="x-tip"';
	var tracks = "DNA,PATRICGenes,RefSeqGenes", window_start = Math.max(0, (record.data.start_max - 1000)), window_end = parseInt(record.data.end_min) + 1000;

	return Ext.String.format('<a href="GenomeBrowser?cType=feature&cId={0}&loc={2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif"  alt="Genome Browser" style="margin:-4px" /></a>', value, record.data.accession, window_start, window_end, tracks);
}

function renderGenomeBrowserBySequence(value, metadata, record, rowIndex, colIndex, store) {
	//metadata.tdAttr = 'data-qtip="Genome Browser" data-qclass="x-tip"';
	var tracks = "DNA,PATRICGenes,RefSeqGenes";
	return Ext.String.format('<a href="GenomeBrowser?cType=genome&cId={0}&loc={1}:{2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif" alt="Genome Browser" style="margin:-4px" /></a>', (record.data.genome_info_id == 0) ? record.data.gid : record.data.genome_info_id, "sid|"+value+"|accn|"+record.data.accession, 0, 10000, tracks);
}

function renderGenomeBrowserByGenome(alue, metadata, record, rowIndex, colIndex, store) {
	//metadata.tdAttr = 'data-qtip="Genome Browser" data-qclass="x-tip"';
	var tracks = "DNA,PATRICGenes,RefSeqGenes";
	return Ext.String.format('<a href="GenomeBrowser?cType=genome&cId={0}&loc={2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif" alt="Genome Browser" style="margin:-4px" /></a>', record.data.genome_info_id, '', 0, 10000, tracks);
}

function renderLocusTag(value, metadata, record, rowIndex, colIndex, store) {

	if (value != null && value != "") {
		if (value[0].indexOf("<em ") >= 0) {
			metadata.tdAttr = 'data-qtip="' + record.data.locus_tag + '" data-qclass="x-tip"';
		} else
			metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&cId={0}">{1}</a>', record.data.na_feature_id, value);
	} else if (record.data.na_feature_id != null) {
		metadata.tdAttr = 'data-qtip="fid:' + record.data.na_feature_id + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&cId={0}">fid:{0}</a>', record.data.na_feature_id);
	} else {
		return "";
	}
}

function renderRefSeqID(value, metadata, record, rowIndex, colIndex, store) {

	if (value != null && record.data.gene_id != null && record.data.gene_id != "") {
		metadata.tdAttr = 'data-qtip="' + record.data.gene_id + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=full_report&list_uids={1}" target="_blank">{0}</a>', value, record.data.gene_id);
	} else {
		return "";
	}
}

function renderProteinID(value, metadata, record, rowIndex, colIndex, store) {

	if (value == null || value.match("^vbi\:")) {
		return "";
	} else {
		metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?val={0}" target="_blank">{0}</a>', value);
	}
}

function displayPseudoGene(value, metadata, record, rowIndex, colIndex, store) {

	if (value == null) {
		return "";
	} else if (value == 1) {
		metadata.tdAttr = 'data-qtip="Yes" data-qclass="x-tip"';
		return "Yes";
	} else {
		metadata.tdAttr = 'data-qtip="No" data-qclass="x-tip"';
		return "No";
	}
}

function renderInterProID(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://www.ebi.ac.uk/interpro/IEntry?ac={0}" target="_blank">{0}</a>', value);
}

function renderPvalue(value, metadata, record, rowIndex, colIndex, store) {

	if (value == 0) {
		return 0;
	} else {
		metadata.tdAttr = 'data-qtip="' + record.data.pvalue_exp + '" data-qclass="x-tip"';
		return Ext.String.format('{0}e<sup>{1}</sup>', value, record.data.pvalue_exp);
	}
}

function renderPubMed(value, metadata, record, rowIndex, colIndex, store) {
	var pubmed_link = "";

	if (value != null && value != "") {
		metadata.tdAttr = 'data-qtip="PubMed" data-qclass="x-tip"';
		pubmed_link = Ext.String.format('<a href="http://view.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">PubMed</a>', value);
	}
	return pubmed_link;
}

//Toolbar operations
//BEGIN
function callOperation() {
	var Page = $Page, property = Page.getPageProperties(), checkbox = Page.getCheckBox(), args = arguments, timeoutId = null;

	function PropertyCheck() {
		if (property.fids.length > 0) {
			this[args[0]].apply(this, Array.prototype.slice.call(args));
			(timeoutId) ? clearInterval(timeoutId) : "";
		}
	}

	if (checkbox.getCount() > 0) {
		property.fids = [];
		getSelectedFeatures();
		if (property.fids.length == 0)
			timeoutId = setInterval(PropertyCheck, 1000);
		else
			this[args[0]].apply(this, Array.prototype.slice.call(args));
	} else
		alert(args[arguments.length - 1]);
}

function DoPathwayEnrichment() {"use strict";

	var Page = $Page, property = Page.getPageProperties();

	Ext.Ajax.request({
		url : "/portal/portal/patric/TranscriptomicsEnrichment/TranscriptomicsEnrichmentWindow?action=b&cacheability=PAGE",
		method : 'POST',
		timeout : 600000,
		params : {
			feature_info_id : property.fids.join(","),
			callType : 'saveParams'
		},
		success : function(response, opts) {
			document.location.href = "TranscriptomicsEnrichment?cType=taxon&cId=&pk=" + response.responseText;
		}
	});

}

function DoFastaOperation() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), fids = property.fids;

	Ext.getDom("fTableForm").action = "/patric-common/jsp/fasta_download_handler.jsp";
	Ext.getDom("fastaaction").value = arguments[1];
	Ext.getDom("fastascope").value = "Selected";
	Ext.getDom("fastatype").value = arguments[2];
	Ext.getDom("fids").value = fids;

	if (arguments[1] == "display") {
		window.open("", "disp", "width=920,height=400,scrollbars,resizable");
		Ext.getDom("fTableForm").target = "disp";
	} else
		Ext.getDom("fTableForm").target = "";

	Ext.getDom("fTableForm").submit();

	cleanCheckBoxSelections();
}

function DoIDMapping() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), fids = property.fids;

	Ext.Ajax.request({
		url : "/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE",
		method : 'POST',
		params : {
			keyword : fids.join(","),
			from : 'PATRIC ID',
			to : arguments[1],
			sraction : 'save_params'
		},
		success : function(response, opts) {
			document.location.href = "IDMapping?cType=taxon&cId=&dm=result&pk=" + response.responseText;
		}
	});

}

function DoMsa() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), fids = property.fids;

	if (fids.length <= 262) {
		Ext.Ajax.request({
			//url : "/portal/portal/patric/FIGfamSorter/FigFamSorterWindow?action=b&cacheability=PAGE",
			url : "/portal/portal/patric/FIGfam/FIGfamWindow?action=b&cacheability=PAGE",
			method : 'POST',
			params : {
				featureIds : fids.join(","),
				callType : "toAligner"
			},
			success : function(response, opts) {

				if (Ext.getDom("pk") != null)
					//document.location.href = "TreeAligner?pk=" + response.responseText;
					document.location.href = "MSA?pk=" + response.responseText;
				else {
					if (Ext.getDom("cType") && Ext.getDom("cId")) {
						//document.location.href = "TreeAlignerB?cType=" + Ext.getDom("cType").value + "&cId=" + Ext.getDom("cId").value + "&pk=" + response.responseText;
						document.location.href = "MSA?cType=" + Ext.getDom("cType").value + "&cId=" + Ext.getDom("cId").value + "&pk=" + response.responseText;
					} else {
						//document.location.href = "TreeAligner?pk=" + response.responseText;
						document.location.href = "MSA?pk=" + response.responseText;
					}
				}

			}
		});
	} else {
		alert("The maximum number of sequences allowed to run MSA is 262.");
	}
}

function DoCart() {
	var Page = $Page, property = Page.getPageProperties();

	addSelectedItems(property.gridType);
}

function overrideButtonActions() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), btn = Page.getCartSaveButton(), fids;

	if (btn) {
		btn.on('click', function() {
			var c = Page.getCheckBox();
			fids = property.fids;
			if (saveToGroup(fids.join(","), property.gridType))
				c.showMessage(fids.length, property.gridType);
			cleanCheckBoxSelections();
		});
	}
}

function cleanCheckBoxSelections() {"use strict";

	var Page = $Page, c = Page.getCheckBox();
	c.clearSelections(), c.updateCheckAllIcon(), c.setCheckAll(false);
}

function getHashFieldsToDownload(form) {"use strict";

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, tmp, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0, state = property.stateId ? Ext.state.Manager.get(property.stateId[which]) : null, s = null;

	for (var i in form) {
		tmp = form[i] || null;
		if (tmp && tmp.id && (tmp.id == "sort" || tmp.id == "dir")) {
			s = (state && state.sort) ? state.sort : property.sort[which];
			if (tmp.id == "sort") {
				tmp.value = s[0].property;
			} else {
				tmp.value = s[0].direction;
			}
		} else if (tmp && hash[tmp.id] != null)
			tmp.value = hash[tmp.id];
	}
}
