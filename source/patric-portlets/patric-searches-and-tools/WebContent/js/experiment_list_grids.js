function createLayout() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	Ext.create('Ext.panel.Panel', {
		id : 'tabLayout',
		border : true,
		autoScroll : false,
		width : $(window).width() - 250,
		items : [{
			region : 'north',
			border : false,
			height : 22,
			xtype : 'tabpanel',
			id : 'tabPanel',
			items : [{
				title : "Experiments",
				id : "0"
			}, {
				title : "Comparisons",
				id : "1"
			}],
			ClickFromTab : true,
			listeners : {
				'tabchange' : function(tabPanel, tab) {
					if (tab.getId() == 0) {
						hash.cwG = false, hash.eId = "", hash.eName = "";
					} else {
						hash.cwG = (hash.cwG || hash.cwG == "true"), hash.eId = !hash.cwG ? "" : hash.eId, hash.eName = !hash.cwG ? "" : hash.eName;
					}

					if (!this.ClickFromTab) {
						loadGrid();
						this.ClickFromTab = true;
					} else {
						hash.aT = parseInt(tab.getId());
						hash.aP[tab.getId()] = 1, createURL();
					}
				}
			}
		}, {
			region : 'center',
			id : 'centerPanel',
			contentEl : 'information',
			border : false,
			split : false
		}, {
			region : 'south',
			id : 'southPanel',
			html : '<div id="PATRICGrid"></div>',
			height : 570,
			border : false,
			autoScroll : true
		}],
		renderTo : 'sample-layout'
	});

	Ext.create('Ext.panel.Panel', {
		title : 'Filter By',
		renderTo : 'tree-panel',
		width : 239,
		height : 620,
		border : true,
		resizable : true,
		autoScroll : false,
		id : 'treePanel',
		collapsible : true,
		collapseDirection : 'left',
		items : [{
			id : 'westPanel',
			region : 'west',
			html : '<div id="GenericSelector" style="padding-left: 3px;"></div>',
			width : 239,
			height : 596,
			split : true,
			tbar : [{
				xtype : 'textfield',
				itemId : 'keyword',
				width : 150,
				hideLabel : true,
				allowBlank : true,
				value : hash.kW,
				emptyText : 'keyword',
				listeners : {
					specialKey : function(field, e) {
						if (e.getKey() == e.ENTER) {
							hash.kW = field.value.trim();
							Ext.getDom("keyword").value = getOriginalKeyword(hash);
							refresh();
						}
					}
				}
			}, '->', {
				text : 'Clear All',
				handler : function() {
					hash.kW = "";
					Ext.getDom("keyword").value = getOriginalKeyword(hash);
					this.ownerCt.getComponent("keyword").setRawValue("");
					refresh("clear_all");
				}
			}]
		}],
		listeners : {
			resize : function(cmp, width, height, oldWidth, oldHeight, eOpts) {
				var Page = $Page;
				if (Page.getGrid()) {
					Ext.getCmp("westPanel").setWidth(width);
					Ext.getCmp("GenericSelectorTree").setWidth(width - 7);
					Page.doTabLayout();
				}
			}
		}
	});

}

function loadFBCD() {
	var tabs = Ext.getCmp("tabPanel"), id = tabs.getActiveTab().getId(), hash = $Page.getPageProperties().hash;

	SetLoadParameters();

	Ext.getCmp('westPanel').getDockedItems()[0].getComponent("keyword").setRawValue(hash.kW);

	if (hash.aT == parseInt(id)) {
		loadGrid();
	} else {
		tabs.ClickFromTab = false;
		tabs.setActiveTab(hash.aT);
	}
}

function getExtraParams() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.aT, name = property.name, tree = property.tree;

	return {
		pk : hash.key,
		need : which,
		eId : hash.eId,
		keyword : constructKeyword((tree) ? tree.getSelectedTerms() : {}, name),
		facet : JSON.stringify({
			"facet" : configuration[name].display_facets.join(","),
			"facet_text" : configuration[name].display_facets_texts.join(",")
		})
	};
}

function CallBack() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, totalcount = Page.getStore(hash.aT).totalCount, tree = property.tree, treeDS = property.treeDS, name = property.name;

	if (hash.aT == "1")
		Ext.getDom('grid_result_summary').innerHTML = "<b>" + totalcount + " comparisons found</b>";
	else
		Ext.getDom('grid_result_summary').innerHTML = "<b>" + totalcount + " experiments found</b>";

	if (tree) {
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
	} else
		createTree();

	//Page.getGrid().setSortDirectionColumnHeader(hash.sort[hash.aT], hash.dir[hash.aT]);
	if (Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();

	// add selected terms for information
	var k = displaySelectedFacetKeyword();
	Ext.getDom('grid_result_summary').innerHTML += "   " + k;
};

function renderPubMed(value, metadata, record, rowIndex, colIndex, store) {

	if (value != null & value != "") {
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', value);
	} else {
		return "";
	}
}

function renderID(value) {
	if (value != null && value != "") {
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc={0}" target="_blank">{0}</a>', value);
	} else {
		return "";
	}
}

function linkToGeneList(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=taxon&cId={5}&dm=result&pk=&expId={1}&sampleId={2}&colId=&log_ratio={3}&zscore={4}">{0}</a>', value, record.data.eid, (record.get("pid") == undefined) ? "" : record.data.pid, 0, 0, Ext.getDom("cId").value);
	} else {
		return 0;
	}
}

function linkToGeneListFold(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=taxon&cId={5}&dm=result&pk=&expId={1}&sampleId={2}&colId=&log_ratio={3}&zscore={4}">{0}</a>', value, record.data.eid, record.data.pid, 1, 0, Ext.getDom("cId").value);
	} else {
		return 0;
	}
}

function linkToGeneListZScore(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=taxon&cId={5}&dm=result&pk=&expId={1}&sampleId={2}&colId=&log_ratio={3}&zscore={4}">{0}</a>', value, record.data.eid, record.data.pid, 0, 2, Ext.getDom("cId").value);
	} else {
		return 0;
	}
}

function renderExpTitle(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"SingleExperiment?cType=taxon&cId={2}&eid={1}\">{0}</a>", value, record.data.eid, Ext.getDom("cId").value);

}

function renderComparisons(value, metadata, record, rowIndex, colIndex, store) {
	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"ShowComparisonsTab('" + record.data.eid + "','" + record.data.title.replace(/'/gi, '`') + "');\"/>" + value + "</a>");

}

function ShowComparisonsTab(eId, title) {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	hash.aT = 1, hash.aP[1] = 1, hash.eName = title, hash.eId = eId, hash.cwG = true;

	createURL();
}

function displaySelectedFacetKeyword() {
	var k = new Array(), Page = $Page, property = Page.getPageProperties(), name = property.name;

	var terms = property.tree.getSelectedTerms();
	Ext.Array.each(configuration[name].all_facets, function(facet) {
		if (facet != "Keyword" && terms[facet] != null) {
			//console.log(terms[facet]);
			if (terms[facet] != "(*)") {
				k.push(terms[facet].split("##"));
			}
		}
	});
	if (k.length > 0) {
		return "(" + k.join(", ") + ")";
	} else {
		return "";
	}
}

function getSelectedFeatures() {

	var Page = $Page, property = Page.getPageProperties(), sl = Page.getCheckBox().getSelections(), arr = [];

	for (var i = 0; i < sl.length; i++) {
		property.fids.push(sl[i].data.expid);
	}
	for (var i = 0; i < property.fids.length; i++) {
		(arr.length > 0 && arr.indexOf(property.fids[i]) == -1) ? arr.push(property.fids[i]) : arr.length == 0 ? arr.push(property.fids[i]) : "";
	}
	property.fids = arr;
}

function DownloadFile() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), form = Ext.getDom("fTableForm"), name = property.name, tree = property.tree;

	if (tree.getSelectedTerms()["Keyword"] == null) {
		tree.selectedTerm["Keyword"] = Ext.getDom("keyword").value;
	}

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
	form.download_keyword.value = constructKeyword(tree.getSelectedTerms(), name);
	form.fileformat.value = arguments[0];
	form.target = "";
	getHashFieldsToDownload(form);
	form.submit();

}

function showExpDetail() {
	var cType = Ext.getDom("cType") ? Ext.getDom("cType").value : "", cId = Ext.getDom("cId") ? Ext.getDom("cId").value : "", field, expId = "", sampleId = "", countComparisons = 0, maxComparisions = 100, arrExpID = new Array(), param, Page = $Page, property = Page.getPageProperties(), hash = property.hash, checkbox = Page.getCheckBox();

	if (hash.aT == 1) {
		field = "pid";
	} else {
		field = "eid";
	}

	if (checkbox.getCount() > 0) {

		if (field == "pid") {
			countComparisons = checkbox.getCount();
		} else {
			Ext.Array.each(checkbox.getSelections(), function(obj) {
				countComparisons += obj.get("samples");
			});
		}

		//console.log("# of comparisons:"+countComparisons);

		if (countComparisons >= maxComparisions) {
			alert("You have exceeded the limit of comparisons. Please lower than " + maxComparisions);
			return false;
		}

		Ext.Array.each(checkbox.getSelections(), function(obj) {
			arrExpID.push(obj.get(field));
		});
		arrExpID = Ext.Array.unique(arrExpID);
		param = arrExpID.join(",");

		if (hash.aT == 1) {
			sampleId = param;
		} else {
			expId = param;
		}

		document.location.href = "TranscriptomicsGene?cType=" + cType + "&cId=" + cId + "&dm=result&pk=&expId=" + expId + "&sampleId=" + sampleId + "&colId=&log_ratio=&zscore=";

	} else {
		alert("No experiment(s) are selected. To see associated gene(s), at least one experiment must be selected");
	}
}

//Add to Cart
//END
