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
				title : "Proteins",
				id : "1"
			}],
			ClickFromTab : true,
			listeners : {
				'tabchange' : function(tabPanel, tab) {
					if (tab.getId() == 0) {
						hash.cwE = false, hash.experiment_id = "", hash.experiment_title = "";
					} else {
						hash.cwE = (hash.cwE || hash.cwE == "true"), hash.experiment_id = !hash.cwE ? "" : hash.experiment_id, hash.experiment_title = !hash.cwE ? "" : hash.experiment_title;
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
		hidden:true, // remove it later
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
		experiment_id : hash.experiment_id,
		keyword : constructKeyword((tree) ? tree.getSelectedTerms() : {}, name),
		facet : JSON.stringify({
			"facet" : configuration[name].display_facets.join(","),
			"facet_text" : configuration[name].display_facets_texts.join(",")
		})
	};
}

function CallBack() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, totalcount = Page.getStore(hash.aT).totalCount, tree = property.tree, treeDS = property.treeDS, name = property.name;

	if (hash.aT == "1"){
		if(hash.experiment_title)
			Ext.getDom('grid_result_summary').innerHTML = "<b>" + totalcount + " proteins found in " + hash.experiment_title + "</b>";
		else
			Ext.getDom('grid_result_summary').innerHTML = "<b>" + totalcount + " proteins found</b>";
	}else
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

	if (Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();

	// add selected terms for information
	var k = displaySelectedFacetKeyword();
	Ext.getDom('grid_result_summary').innerHTML += "   " + k;
};

function renderPubMed(value, metadata, record, rowIndex, colIndex, store) {
	return (value != null & value != "")?Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', value):"";
}

function renderProteins(value, metadata, record, rowIndex, colIndex, store) {
	return value > 0?Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"ShowProteinsTab('" + record.data.experiment_id + "','" + record.data.experiment_title.replace(/'/gi, '`') + "');\"/>" + value + "</a>"):0;
}

function renderPeptide(value, metadata, record, rowIndex, colIndex, store){

	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"displayPeptide('" + record.data.experiment_id + "','" + (Ext.getDom("cType").value == "feature"?Ext.getDom("cId").value:record.data.na_feature_id) + "');\"/>Peptides</a>");

}

function displayPeptide(experiment_id, na_feature_id){
	
	if(!this.peptideWindow){
		
		var peptide_form = Ext.create('Ext.form.Panel', {
			width : 448,
			height : 258,
			items : [{
				xtype: 'textareafield',
	            grow: true,
	            id: 'peptide_text_area',
				width : 448,
				height : 258
			}],
			buttons : [{
				xtype : 'button',
				text : 'Cancel',
				handler : function() {
					window.peptideWindow.hide();
				}
			}]
		});

		this.peptideWindow = Ext.create('Ext.window.Window', {
			title : 'Peptide Sequence',
			height : 320,
			width : 460,
			layout : 'fit',
			closeAction : 'hide',
			items : [peptide_form]
		});
		
		if(!('contains' in String.prototype))
			  String.prototype.contains = function(str, startIndex) { return -1 !== String.prototype.indexOf.call(this, str, startIndex); };
	}
	this.peptideWindow.show();
	Ext.get('peptide_text_area').mask('Loading');
	Ext.Ajax.request({
		url : '/portal/portal/patric/ProteomicsList/ProteomicsListWindow?action=b&cacheability=PAGE',
		method : 'POST',
		params : {
			need : "getPeptides",
			experiment_id : experiment_id,
			na_feature_id : na_feature_id
		},
		success : function(rs) {
			var decoded = Ext.JSON.decode(rs.responseText);
			
			var peptides = decoded.docs,
				aa = decoded.aa;
			
			for(var i=0; i<peptides.length; i++){
				if(aa.indexOf(peptides[i].peptide_sequence)){
					aa = aa.replace(peptides[i].peptide_sequence, '<span style="color:red;">' + peptides[i].peptide_sequence + '</span>');
				}
			}
			Ext.get('peptide_text_area').unmask();
			Ext.getDom('peptide_text_area').innerHTML = aa;
		}
	});
	
}

function ShowProteinsTab(experiment_id, experiment_title) {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	hash.aT = 1, hash.aP[1] = 1, hash.experiment_title = experiment_title, hash.experiment_id = experiment_id, hash.cwE = true;

	createURL();
}

function displaySelectedFacetKeyword() {
	var k = new Array(), Page = $Page, property = Page.getPageProperties(), name = property.name;

	var terms = property.tree.getSelectedTerms();
	Ext.Array.each(configuration[name].all_facets, function(facet) {
		if (facet != "Keyword" && terms[facet] != null) {
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

	var Page = $Page, property = Page.getPageProperties(), sl = Page.getCheckBox().getSelections(), hash = property.hash, idList = [], arr = [], i, na_features;

	if(hash.aT == 1){
		for (i = 0; i < sl.length; i++) {
			arr.push(sl[i].data.na_feature_id);
		}
		property.fids = arr;
	}else{
		for (i = 0; i < sl.length; i++) {
			idList.push(sl[i].data.experiment_id);
		}
		Ext.Ajax.request({
			url : "/portal/portal/patric/ProteomicsList/ProteomicsListWindow?action=b&cacheability=PAGE",
			method : 'POST',
			timeout : 600000,
			params : {
				keyword : "experiment_id: ("+idList.join(",")+")",
				need : 'getFeatureIds'
			},
			success : function(response, opts) {
				na_features = (Ext.JSON.decode(response.responseText)).docs;			
		    	for(i = 0; i < na_features.length; i++){
		    		arr.push(na_features[i].na_feature_id);
		    	}
		    	property.fids = arr;
			}
		});
	}	
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
