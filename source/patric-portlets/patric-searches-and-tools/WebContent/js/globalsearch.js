var metadataGenomeSummaryValue = [{
	name : 'Genome Status',
	text : 'genome_status'
}, {
	name : 'Strain',
	text : 'strain'
}, {
	name : 'Serovar',
	text : 'serovar'
}, {
	name : 'Biovar',
	text : 'biovar'
}, {
	name : 'Pathovar',
	text : 'pathovar'
}, {
	name : 'Culture Collection',
	text : 'culture_collection'
}, {
	name : 'Type Strain',
	text : 'type_strain'
}, {
	name : 'Project Status',
	text : 'project_status'
}, {
	name : 'Availability',
	text : 'availability'
}, {
	name : 'Completion Date',
	text : 'completion_date'
}, {
	name : 'Publication',
	text : 'publication'
}, {
	name : 'NCBI Project Id',
	text : 'ncbi_project_id'
}, {
	name : 'RefSeq Project Id',
	text : 'refseq_project_id'
}, {
	name : 'GenBank Accessions',
	text : 'genbank_accessions'
}, {
	name : 'RefSeq Accessions',
	text : 'refseq_accessions'
}, {
	name : 'Sequencing Status',
	text : 'sequencing_status'
}, {
	name : 'Sequencing Platform',
	text : 'sequencing_platform'
}, {
	name : 'Sequencing Depth',
	text : 'sequencing_depth'
}, {
	name : 'Assembly Method',
	text : 'assembly_method'
}, {
	name : 'Sequences',
	text : 'sequences',
	style : 'none'
}, {
	name : 'Genome Length',
	text : 'genome_length'
}, {
	name : 'GC Content',
	text : 'gc_content'
}, {
	name : 'RAST CDS',
	text : 'rast_cds'
}, {
	name : 'BRC CDS',
	text : 'brc_cds'
}, {
	name : 'RefSeq CDS',
	text : 'refseq_cds'
}, {
	name : 'Isolation Site',
	text : 'isolation_site'
}, {
	name : 'Isolation Source',
	text : 'isolation_source'
}, {
	name : 'Isolation Comments',
	text : 'isolation_comments'
}, {
	name : 'Collection Date',
	text : 'collection_date'
}, {
	name : 'Isolation Country',
	text : 'isolation_country'
}, {
	name : 'Geographic Location',
	text : 'geographic_location'
}, {
	name : 'Latitude',
	text : 'latitude'
}, {
	name : 'Longitude',
	text : 'longitude'
}, {
	name : 'Altitude',
	text : 'altitude'
}, {
	name : 'Depth',
	text : 'depth'
}, {
	name : 'Host Gender',
	text : 'host_gender'
}, {
	name : 'Host Age',
	text : 'host_age'
}, {
	name : 'Host Health',
	text : 'host_health'
}, {
	name : 'Body Sample State',
	text : 'body_sample_site'
}, {
	name : 'Body Sample Subsite',
	text : 'body_sample_subsite'
}, {
	name : 'Gram Stain',
	text : 'gram_stain'
}, {
	name : 'Cell Shape',
	text : 'cell_shape'
}, {
	name : 'Motility',
	text : 'motility'
}, {
	name : 'Sporulation',
	text : 'sporulation'
}, {
	name : 'Temperature Range',
	text : 'temperature_range'
}, {
	name : 'Optimal Temperature',
	text : 'optimal_temperature'
}, {
	name : 'Salinity',
	text : 'salinity'
}, {
	name : 'Oxygen Requirement',
	text : 'oxygen_requirement'
}, {
	name : 'Habitat',
	text : 'habitat'
}];

function getSummaryandCreateLayout() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	Ext.getDom("global_search_keyword").value = DecodeKeyword(Ext.getDom("keyword").value);

	if (!Ext.getDom("keyword").value.charAt(0) == "(" && !Ext.getDom("keyword").value.charAt(Ext.getDom("keyword").value.length - 1) == ")") {
		Ext.getDom("global_search_keyword").value = Ext.getDom("keyword").value.substring(1, Ext.getDom("keyword").value.length - 2);
		Ext.getDom("keyword").value = "(" + Ext.getDom("keyword").value + ")";
	}

	Ext.Ajax.request({
		url : "/patric-searches-and-tools/jsp/getGlobalSearchCounts.jsp",
		method : 'GET',
		timeout : 600000,
		params : {
			keyword : Ext.getDom('keyword').value,
			spellcheck : hash.spellcheck
		},
		success : function(response, opts) {
			var result = Ext.JSON.decode(response.responseText);
			var summary_data = result.data;

			for (var i = 0; i < property.name.length; i++) {
				if(result.suggestion && result.suggestion[i]){
					if(!property.alternativeKW){
						property.alternativeKW = [];
					}
					property.alternativeKW[i] = result.suggestion[i];
				}
				
				property.resultcount[i] = summary_data[i].response.numFound;
				for (var j = 0; j < 3; j++) {
					property.summary_data[i].push(summary_data[i].response.docs[j]);
				}
			}
			Ext.getDom("searching_span").style.visibility = "hidden";
			loadSearchResults(hash.cat);
		}
	});
}

function loadSearchResults(id) {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	if (hash.cat != id && id != "summary")
		property.reconfigure = true;

	hash.cat = id;
	hash.aP[0] = 1;
	arrangeCSS();
	if (id != "summary") {
		saveToPK();
	}else{
		setSummaryInnerHTMLs();
	}
}

function loadSearchResultsNoSpellCheck(keyword) {
	Ext.Ajax.request({
		url : '/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE',
		method : 'POST',
		params : {
			sraction : "save_params",
			keyword : EncodeKeyword(keyword)
		},
		success : function(rs) {
			document.location.href = "GlobalSearch?cType=taxon&cId=&dm=&pk=" + rs.responseText + "#aP0=1&aP1=1&aP2=1&aP3=1&pS0=10&pS1=10&pS2=10&pS3=10&cat=summary&key=" + rs.responseText + "&spellcheck=false";
		}
	});
}

function arrangeCSS() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	Ext.getDom('med').innerHTML = "<span style=\"font-size: 16px;padding: 0px 0px 0px 12px;font-weight: bold;\">Show Results in:</span>";

	if (hash.cat != "summary") {
		Ext.getDom("li__").innerHTML = "<a class=\"kl\" href=\"javascript:loadSearchResults('summary')\">Summary</a>";
		Ext.get("li__").removeCls("msel");
		Ext.getDom("PATRICGrid").style.visibility = "visible";
		Ext.getDom("summary_div").style.visibility = "hidden";
		Ext.getDom("summary_div").style.height = "0px";
		Ext.get("GenericSelector").dom.style.display = "";
	} else {
		Ext.getDom("PATRICGrid").style.visibility = "hidden";
		Ext.getDom("summary_div").style.visibility = "visible";
		Ext.getDom("summary_div").style.height = "1000px";
		Ext.getDom("PATRICGrid").style.height = "0px";
		Ext.get("li__").addCls("msel");
		Ext.getDom("li__").innerHTML = "Summary";
		Ext.get("GenericSelector").dom.style.display = "none";

		Ext.getDom('med').innerHTML += "<span style=\"font-size:16px;padding-left: 80px;\">Showing results for </span><b style=\"font-size:15px;\"><i>" + DecodeKeyword(Ext.getDom("keyword").value) + "</i></b><br/>";
		
		if(property.alternativeKW){
			Ext.getDom('med').innerHTML += "<span style=\"font-size:13px;text-decoration:none;padding-left: 228px;\">Did you mean: </span>";
			Ext.getDom('med').innerHTML += "<a style=\"font-size:13px; text-decoration:none;\" href=\"javascript:loadSearchResultsNoSpellCheck('" + EncodeKeyword(property.alternativeKW[0]) + "')\">" + property.alternativeKW[0] + "</a>";
			for(var z=1; z<property.alternativeKW.length; z++){
				if(property.alternativeKW[z]){
					Ext.getDom('med').innerHTML += ", <a style=\"font-size:13px; text-decoration:none;\" href=\"javascript:loadSearchResultsNoSpellCheck('" + EncodeKeyword(property.alternativeKW[z]) + "')\">" + property.alternativeKW[z] + "</a>";		
				}
			}
		}
	}

	for (var i = 0; i < property.text.length; i++) {
		if (property.resultcount[i] > 0) {
			if (hash.cat != i) {
				Ext.getDom("li__" + i).innerHTML = "<a class=\"kl\" href=\"javascript:loadSearchResults(" + i + ");\">" + property.text[i] + " (" + property.resultcount[i] + ")</a>";
				Ext.get("li__" + i).removeCls("msel");
			} else {
				Ext.get("li__" + i).addCls("msel");
				Ext.getDom("li__" + i).innerHTML = property.text[i] + " (" + property.resultcount[i] + ")";
			}
		} else {
			Ext.getDom("li__" + i).innerHTML = property.text[i] + " (" + property.resultcount[i] + ")";
		}
	}

}

function saveToPK() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	Ext.Ajax.request({
		url : configuration[property.name[hash.cat]].url,
		method : 'POST',
		params : {
			sraction : "save_params",
			keyword : Ext.getDom("keyword").value
		},
		success : function(rs) {
			hash.key = rs.responseText;
			createURL();
		}
	});

}

function getExtraParams() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.cat, tree = property.tree;

	return {
		spellcheck : hash['spellcheck'],
		grouping : true,
		pk : hash.key,
		need : property.need[which],
		highlight: true,
		keyword : constructKeyword((tree) ? tree.getSelectedTerms() : {}, property.name[which]),
		facet : JSON.stringify({
			"facet" : configuration[property.name[which]].display_facets.join(","),
			"facet_text" : configuration[property.name[which]].display_facets_texts.join(",")
		})
	};
}

function loadTable() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.cat, plugin = property.plugin, plugintype = property.plugintype, checkbox = null;

	if (property.reconfigure) {
		if (which != "summary") {
			property.scm[which] = [];
			if (which != 2) {
				(plugin && plugintype == "checkbox") ? checkbox = Page.checkbox = createCheckBox(property.name[which]) : "";
				property.scm[which].push(checkbox);
			}
			property.scm[which].push({
				header : '<a href="javascript:hideToolbar(\'show\');"><span style="float:right">Show Toolbar</span></a> Select all',
				dataIndex : '',
				flex : 1,
				renderer : property.renderFunction[which],
				menuDisabled : true
			});
		}
	}
	if (which != "summary")
		loadGrid();
	else
		setSummaryInnerHTMLs();
}

function CallBack() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = parseInt(hash.cat), which_cat = "", store = Page.getStore(which), name = property.name[which], treeDS = property.treeDS, tree = property.tree, grid = Page.getGrid(), hide_show = "Show", docked_items, setId = 0, model = property.model[which];

	if (which < 1)
		which_cat = " feature(s)";
	else if (which == 1)
		which_cat = " genome(s)";
	else if (which == 2)
		which_cat = " taxa";
	else if (which == 3)
		which_cat = " experiment(s)";
	
	Ext.getDom('med').innerHTML = "<span style=\"font-size: 16px;padding: 0px 0px 0px 12px;font-weight: bold;\">Show Results in:</span>";

	Ext.getDom('med').innerHTML += "<span id=\"resultStats\" style=\"padding-left: 80px;\">Showing " + store.getTotalCount() + which_cat + " for: <b><i>" + Ext.getDom("keyword").value + "</i></b></span>";

	Ext.getDom('med').innerHTML += "</span>";

	if (treeDS && treeDS.proxy.url.indexOf(model) != -1) {
		treeDS.proxy.extraParams = {
			need : "tree",
			pk : hash.key,
			facet : JSON.stringify({
				"facet" : configuration[name].display_facets.join(","),
				"facet_text" : configuration[name].display_facets_texts.join(",")
			}),
			state : JSON.stringify(tree.getState())
		};
		treeDS.load();
	} else {
		Ext.getDom("GenericSelector").innerHTML = "";
		createTree();
	}

	if (!Ext.get(Ext.get('GenericSelector').dom.childNodes[0].childNodes[1].id).hasCls('x-docked-noborder-top')) {
		Ext.get(Ext.get('GenericSelector').dom.childNodes[0].childNodes[1].id).addCls('x-docked-noborder-top');
	}

	if (which == 0)
		Ext.get("GenericSelectorTree") ? Ext.get("GenericSelectorTree").setHeight(0) : "";
	else
		Ext.get("GenericSelectorTree").setHeight(565);

	if (parseInt(which) > 0) {
		if (grid.getDockedItems('toolbar').length > 1) {
			hide_show = "Hide";
			docked_items = grid.getDockedItems()[0].items.items;
			docked_items[2].disable();
			docked_items[4].items.items[1].disable();
			docked_items[6].disable();
			if (which == 2) {
				docked_items[0].disable();
			} else if(which == 1){
				docked_items[0].enable();
				docked_items[0].items.items[0].el.dom.childNodes[0].childNodes[0].childNodes[0].innerHTML = "Add Genome(s)";
			}else {
				docked_items[0].enable();
				docked_items[0].items.items[0].el.dom.childNodes[0].childNodes[0].childNodes[0].innerHTML = "Add Experiment(s)";
			}
		}
		if (which == 2)
			grid.columns[0].setText("<a href=\"javascript:hideToolbar('" + hide_show + "');\"><span style=\"float:right\">" + hide_show + " Toolbar</span></a>");
		else if(which == 1)
			grid.columns[1].setText("<a href=\"javascript:hideToolbar('" + hide_show + "');\"><span style=\"float:right\">" + hide_show + " Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed genome(s)");
		else if(which == 3)
			grid.columns[1].setText("<a href=\"javascript:hideToolbar('" + hide_show + "');\"><span style=\"float:right\">" + hide_show + " Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed experiment(s)");
	} else {
		if (grid.getDockedItems('toolbar').length > 1) {
			hide_show = "Hide";
			docked_items = grid.getDockedItems()[0].items.items;
			docked_items[0].enable();
			docked_items[2].enable();
			docked_items[4].items.items[1].disable();
			docked_items[6].enable();
			docked_items[0].items.items[0].el.dom.childNodes[0].childNodes[0].childNodes[0].innerHTML = "Add Feature(s)";
		}
		grid.columns[1].setText("<a href=\"javascript:hideToolbar('" + hide_show + "');\"><span style=\"float:right\">" + hide_show + " Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed feature(s)");
	}

	setId = setTimeout(function() {
		sHeight();
		clearTimeout(setId);
	}, 200);

}

function updateCountAtColumnHeader() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, store = Page.getStore(hash.cat), pageSize = store.pageSize || (Ext.state.Manager.get('pagesize') ? Ext.state.Manager.get('pagesize').value : 20),
		cP = parseInt(store.currentPage), tC = store.totalCount, tP = tC/pageSize;

	
	return (tC < pageSize) ? tC : (cP > tP?tC - (pageSize * (cP - 1)):pageSize);
}

function hideToolbar(action) {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, grid = Page.getGrid(), which = parseInt(hash.cat);

	if (action.toLowerCase() == "hide") {
		grid.removeDocked(grid.getDockedItems()[0]);

		if (which == 2)
			grid.columns[0].setText("<a href=\"javascript:hideToolbar('show');\"><span style=\"float:right\">Show Toolbar</span></a>");
		else {
			if (which == 1)
				grid.columns[1].setText("<a href=\"javascript:hideToolbar('show');\"><span style=\"float:right\">Show Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed genome(s)");
			else if(which == 0)
				grid.columns[1].setText("<a href=\"javascript:hideToolbar('show');\"><span style=\"float:right\">Show Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed feature(s)");
			else 
				grid.columns[1].setText("<a href=\"javascript:hideToolbar('show');\"><span style=\"float:right\">Show Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed experiment(s)");
		}
	} else {
		if (which > 0) {
			grid.addDocked(createToolbar("cart", "", "Genome"));
			docked_items = grid.getDockedItems()[0].items.items;
			docked_items[0].disable();
			docked_items[2].disable();
			docked_items[4].items.items[1].disable();
			docked_items[6].disable();
			if (which == 1) {
				docked_items[0].enable();
				grid.columns[1].setText("<a href=\"javascript:hideToolbar('hide');\"><span style=\"float:right\">Hide Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed genome(s)");
			} else
				grid.columns[0].setText("<a href=\"javascript:hideToolbar('hide');\"><span style=\"float:right\">Hide Toolbar</span></a>");
		} else if(which == 0){
			grid.addDocked(createToolbar("cart", "", "Feature"));
			grid.columns[1].setText("<a href=\"javascript:hideToolbar('hide');\"><span style=\"float:right\">Hide Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed feature(s)");
		} else if(which == 3){
			grid.addDocked(createToolbar("cart", "", "Experiment"));
			grid.columns[1].setText("<a href=\"javascript:hideToolbar('hide');\"><span style=\"float:right\">Hide Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed experiment(s)");
		}
	}

	sHeight();
}

function sHeight() {
	var Page = $Page, grid = Page.getGrid();

	Ext.get('mw').setHeight(810 > grid.getHeight() + 40 ? 810 : grid.getHeight() + 40);

}

function setSummaryInnerHTMLs() {"use strict";

	var span, span1, Page = $Page, property = Page.getPageProperties();

	for (var i = 0; i < property.name.length; i++) {
		Ext.getDom("summary_div_" + property.divID[i] + "_span_1").innerHTML = property.summaryHeaderText[i];
		// +" ("+property.resultcount[i]+")",
		span1 = Ext.getDom("summary_div_" + property.divID[i] + "_span_2"), span1.innerHTML = "";

		if (property.resultcount[i] > 0)
			span1.innerHTML = " <a style=\"font-size: 16px; text-decoration:none;\" href=\"javascript:loadSearchResults('" + i + "')\">(" + property.resultcount[i] + ")</a>";

		for (var j = 0; j < 3; j++) {
			span = Ext.getDom("summary_div_" + property.divID[i]);
			if(span.childNodes.length < 12){
				span.innerHTML += "<div class=\"clear\"></div>";
				span.innerHTML += property.renderFunction[i].call(null, "", "", {
					data : property.summary_data[i][j]
				});
			}
		}
	}

	document.getElementById("searching_span").style.height = "0px";
	
	Ext.get('mw').setHeight(1050);

}

function renderListExperiment(value, p, record) {

	var data = {}, text = "", organism;
	if (record && record.data) {
		data = record.data;
		text = "<div style=\"line-height:1.8; white-space: normal !important;\"><div><img src=\"/patric/images/global_experiment.png\"style=\"float: left; padding: 3px 3px 0px 3px;\"/>";
		if(data.highlight && data.highlight.title){
			text += Ext.String.format('<a href="SingleExperiment?cType=taxon&cId=2&eid={0}">{1}</a>', data.eid, data.highlight.title);
		}else{
			text += Ext.String.format('<a href="SingleExperiment?cType=taxon&cId=2&eid={0}">{1}</a>', data.eid, data.title);	
		}
		
		text += "<br/><span style=\"color: #C60;\">";

		if (data.highlight && data.highlight.organism)
			organism = data.highlight.organism.join(",");
		else
			organism = ({}).toString.call(data.organism) === "[object Array]"?data.organism.join(","):data.organism;

		text += organism + "</span>";
		if (parseInt(data.eid) > 0){
			text += "<br/> Accession : ";
			if (data.highlight && data.highlight.accession)
				text += data.highlight.accession;
			else
				text += data.accession;
		}
		text += "</div>";
	}
	return text;
}

function renderListTaxonomy(value, p, record) {

	var data = {}, text = "";

	if (record && record.data) {
		data = record.data;
		text = "<div style=\"line-height:1.8; white-space: normal !important;\"><div><img src=\"/patric/images/global_taxa.png\"style=\"float: left; padding: 3px 3px 0px 3px;\"/>";
		if (data.highlight && data.highlight.taxon_name) {

			text += Ext.String.format('<a style=\"font-size:14px;\" href="Taxon?cType=taxon&cId={0}">{1}</a> ({2})', data.taxon_id, data.highlight.taxon_name, data.taxon_rank);
		} else {
			text += Ext.String.format('<a style=\"font-size:14px;\" href="Taxon?cType=taxon&cId={0}">{1}</a> ({2})', data.taxon_id, data.taxon_name, data.taxon_rank);
		}
		if (parseInt(data.genomes) > 0)
			text += "<br/>" + Ext.String.format('<a href="GenomeList?cType=taxon&amp;cId={0}&amp;dataSource=&amp;displayMode=&amp;pk=">{1} Genome(s)</a>', data.taxon_id, data.genomes);
		else
			text += "<br/> 0 Genome(s)";
		text += "</div>";
	}
	return text;
}

function renderListGenome(value, p, record) {

	var data = {}, text = "", brflag = false, subtext = "", i, span = "<span style=\"font-size: 14px;\"> | </span>";

	if (record && record.data) {
		data = record.data;
		text = "";
		if (data.plasmids || data.contigs || data.chromosomes)
			text += "<div style=\"float: left;\"><img src=\"/patric/images/global_genome.png\"style=\"padding: 8px 3px 0px 3px;\"/></div>";

		text += "<div style=\"line-height:1.5; white-space: normal !important; float:left; width:700px;\">";

		if (data.highlight && data.highlight.genome_name)
			text += Ext.String.format('<a style=\"font-size:14px;\" href="Genome?cType=genome&cId={0}">{1}</a>', data.genome_info_id, data.highlight.genome_name);
		else
			text += Ext.String.format('<a style=\"font-size:14px;\" href="Genome?cType=genome&cId={0}">{1}</a>', data.genome_info_id, data.genome_name);

		text += "<br/><span style=\"color: #C60;\">";

		if (parseInt(data.chromosomes) > 0) {
			if (data.highlight && data.highlight.chromosomes)
				text += data.highlight.chromosomes + ((parseInt(data.highlight.chromosomes) > 1) ? " Chromosomes" : " Chromosome");
			else
				text += data.chromosomes + ((parseInt(data.chromosomes) > 1) ? " Chromosomes" : " Chromosome");
		}

		if (parseInt(data.plasmids) > 0) {

			if (parseInt(data.chromosomes) > 0)
				text += span;

			if (data.highlight && data.highlight.plasmids)
				text += data.highlight.plasmids + ((parseInt(data.highlight.plasmids) > 1) ? " Plasmids" : " Plasmid");
			else
				text += data.plasmids + ((parseInt(data.plasmids) > 1) ? " Plasmids" : " Plasmid");
		}

		if (parseInt(data.contigs) > 0) {

			if (parseInt(data.plasmids) > 0)
				text += span;

			if (data.highlight && data.highlight.contigs)
				text += data.highlight.contigs + ((parseInt(data.highlight.contigs) > 1) ? " Contigs" : " Contig");
			else
				text += data.contigs + ((parseInt(data.contigs) > 1) ? " Contigs" : " Contig");
		}

		text += "</span>";

		if (data.completion_date) {
			text += "<br/>";

			if (data.highlight && data.highlight.completion_date)
				text += "SEQUENCED: " + data.highlight.completion_date;
			else if (data.completion_date && data.completion_date)
				text += "SEQUENCED: " + data.completion_date;

			if (data.highlight && data.highlight.sequencing_centers)
				text += " (" + data.highlight.sequencing_centers + ")";
			else if (data.sequencing_centers && data.sequencing_centers)
				text += " (" + data.sequencing_centers + ")";
		}

		if (data.collection_date || data.host_name || data.disease)
			text += "<br/>";

		if (data.collection_date) {

			if (data.highlight && data.highlight.collection_date)
				text += "COLLECTED: " + data.highlight.collection_date;
			else if (data.collection_date && data.collection_date)
				text += "COLLECTED: " + data.collection_date;

			if (data.highlight && data.highlight.isolation_country)
				text += " (" + data.highlight.isolation_country + ")";
			else if (data.isolation_country)
				text += " (" + data.isolation_country + ")";

			if (data.host_name || data.disease)
				text += span;

		}
		if (data.host_name) {
			if (data.highlight && data.highlight.host_name)
				text += "HOST: " + data.highlight.host_name;
			else if (data.host_name)
				text += "HOST: " + data.host_name;

			if (data.disease)
				text += span;
		}

		if (data.disease) {
			if (data.highlight && data.highlight.disease)
				text += "DISEASE: " + data.highlight.disease;
			else if (data.disease)
				text += "DISEASE: " + data.disease;
		}

		for ( i = 0; i < metadataGenomeSummaryValue.length; i++) {
			if (data.highlight[metadataGenomeSummaryValue[i].text]) {
				if (brflag == false) {
					text += "<br/>";
					brflag = true;
				}
				if (subtext.length > 0)
					subtext += span;

				subtext += metadataGenomeSummaryValue[i].name.toUpperCase() + ": " + data.highlight[metadataGenomeSummaryValue[i].text];
			}

		}

		text += subtext;
		if (data.comments) {
			text += "<br/>";
			if (data.highlight && data.highlight.comments)
				text += "COMMENT: " + data.highlight.comments[0];
			else
				text += "COMMENT: " + data.comments;
		}
		text += "</div>";
	}
	return text;
}

function renderListFeature(value, p, record) {

	var data = {}, text = "", locus_tag, refseq_locus_tag, highlights_checked = {}, span = "<span style=\"font-size: 14px;\"> | </span>";

	if (record && record.data) {
		data = record.data;
		if (data.annotation) {

			if (data.annotation == "PATRIC" || data.annotation == "RAST") {
				text += "<div style=\"float:left\"><img src=\"/patric/images/global_feature_patric.png\"style=\"float: left; padding: 5px 6px 2px 2px;\"/></div>";
			} else if (data.annotation == "Legacy BRC" || data.annotation == "BRC" || data.annotation == "Curation") {
				text += "<div style=\"float:left\"><img src=\"/patric/images/global_feature_legacy.png\"style=\"float: left; padding: 5px 6px 2px 2px;\"/></div>";
			} else if (data.annotation == "RefSeq") {
				text += "<div style=\"float:left\"><img src=\"/patric/images/global_feature_refseq.png\"style=\"float: left; padding: 5px 6px 2px 2px;\"/></div>";
			}

		} else {
			text += "<div style=\"float:left\"><img src=\"/patric/images/global_feature_patric.png\"style=\"float: left; padding: 8px 6px 2px 2px;\"/></div>";
		}
		text += "<div style=\"line-height:1.5; white-space: normal !important; float:left; width:700px;\">";

		if (data.product) {
			text += "<span style=\"font-size: 14px;\">";
			if (data.highlight && data.highlight.product)
				text += renderLocusTag(data.highlight.product, "", record);
			else if (data.product && data.product)
				text += renderLocusTag(data.product, "", record);
			text += "</span>";
			highlights_checked.product = true;
		}

		text += renderSubGeneLocusTagGenome(record, highlights_checked);

		if (data.feature_type) {
			text += " <br/> ";
			text += data.feature_type;
		}

		if (data.locus_tag) {
			text += span;
			// + "PATRIC: ";
			if (data.highlight && data.highlight.locus_tag)
				locus_tag = data.highlight.locus_tag;
			else
				locus_tag = data.locus_tag;

			text += locus_tag;
			highlights_checked.locus_tag = true;
		}

		if (data.refseq_locus_tag) {
			text += span;
			// + "RefSeq: ";
			if (data.highlight && data.highlight.refseq_locus_tag)
				refseq_locus_tag = data.highlight.refseq_locus_tag;
			else
				refseq_locus_tag = data.refseq_locus_tag;

			text += refseq_locus_tag;
			highlights_checked.refseq_locus_tag = true;
		}
		var brflag = false;
		if (data.highlight) {
			for (var j in data.highlight) {
				if (!highlights_checked.hasOwnProperty(j)) {
					if (!brflag) {
						text += "<br/>";
						brflag = true;
					}
					if (j == "ids") {
						for (var z = 0; z < data.highlight[j].length; z++) {
							text += data.highlight[j][z].replace("|",":").split(":")[0].toUpperCase().replace("GLOBAL_EM", "global_em");
							text += ": " + data.highlight[j][z].split("|")[1];
							text += span;
						}
					} else if (j == "pathway" || j == "go") {
						text += ((j == "pathway") ? j.toUpperCase() + ": " : "");
						for (var z = 0; z < data.highlight[j].length; z++) {
							if (j == "pathway")
								text += data.highlight[j][z].split("|")[1] + ",";
							else
								text += data.highlight[j][z] + ",";
						}
						text = text.substring(0, text.length - 1);
						text += span;
					} else {
						text += j.replace("_", " ").toUpperCase() + ": " + data.highlight[j].join(", ");
						text += span;
					}
				}
			}
			if (brflag)
				text = text.substring(0, text.length - span.length);
		}
		text += "</div></div>";

	}
	return text;

}

function renderSubGeneLocusTagGenome(record, highlights_checked) {

	var data = record.data, text = "", gene, genome_name, span = "<span style=\"font-size: 14px;\"> | </span>";

	if (data.gene) {

		if (data.product)
			text += span;

		if (data.highlight && data.highlight.gene)
			gene = data.highlight.gene;
		else
			gene = data.gene;
		text += gene;
		highlights_checked.gene = true;

	}

	text += "<br/><span style=\"color: #C60;\">";

	if (data.highlight && data.highlight.genome_name)
		genome_name = data.highlight.genome_name;
	else
		genome_name = data.genome_name;

	text += genome_name + "</span>";
	highlights_checked.genome_name = true;

	//text +="&nbsp;&nbsp;&nbsp;"+renderGenomeBrowserByFeature(data.na_feature_id, "", record);
	text += "</span>";

	return text;
}

function getSelectedFeatures() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = parseInt(hash.cat), sl = Page.getCheckBox().getSelections(), i, fids = property.fids;

	if (which == 1) {
		for ( i = 0; i < sl.length; i++) {
			fids.push(sl[i].data.genome_info_id);
		}
		property.gridType = "Genome";
	} else if (which == 3) {
		for ( i = 0; i < sl.length; i++) {
			fids.push(sl[i].data.expid);
		}
		property.gridType = "ExpressionExperiment";
	}else {
		for ( i = 0; i < sl.length; i++) {
			fids.push(sl[i].data.na_feature_id);
		}
		property.gridType = "Feature";
	}
}

function DownloadFile() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = parseInt(hash.cat), name = property.name[which], tree = property.tree, form = Ext.getDom("fTableForm");

	if (tree.getSelectedTerms()["Keyword"] == null) {
		tree.selectedTerm["Keyword"] = Ext.getDom("keyword").value;
	}

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
	form.target = "";
	form.fileformat.value = arguments[0];
	form.download_keyword.value = constructKeyword(tree.getSelectedTerms(), name);
	getHashFieldsToDownload(form);
	form.submit();

}