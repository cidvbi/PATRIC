function createLayout() {

	var Page = $Page, property = Page.getPageProperties();

	Ext.create('Ext.panel.Panel', {
		id : 'tabLayout',
		border : true,
		autoScroll : false,
		width : $(window).width() - 250,
		items : [{
			region : 'center',
			id : 'centerPanel',
			html : '<div id="PATRICGrid"></div>',
			height : 588,
			border : false,
			autoScroll : true
		}],
		renderTo : 'sample-layout'
	});

	Ext.create('Ext.panel.Panel', {
		title : 'Filter By',
		renderTo : 'tree-panel',
		width : property.name == "Feature" ? 0 : 239,
		height : property.name == "Feature" ? 0 : 620,
		border : true,
		resizable : true,
		autoScroll : false,
		collapsible : true,
		collapseDirection : 'left',
		id : 'treePanel',
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
				value : '',
				emptyText : 'keyword',
				scope : this,
				listeners : {
					specialKey : function(field, e) {
						if (e.getKey() == e.ENTER) {
							if (field.value)
								Ext.getDom("keyword").value += "(" + field.value + ")";
							else
								Ext.getDom("keyword").value = getOriginalKeyword();
							refresh();
						}
					}
				}
			}, '->', {
				text : 'Clear All',
				handler : function() {
					Ext.getDom("keyword").value = getOriginalKeyword();
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

function getExtraParams() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	return {
		pk : hash.key,
		need : property.model[0].toLowerCase(),
		keyword : constructKeyword((property.tree) ? property.tree.getSelectedTerms() : {}, property.name),
		facet : JSON.stringify({
			"facet" : configuration[property.name].display_facets.join(","),
			"facet_text" : configuration[property.name].display_facets_texts.join(",")
		})
	};
}

function CallBack() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.aT ? hash.aT : 0, name = property.name, tree = property.tree, treeDS = property.treeDS;

	//Page.getGrid().setSortDirectionColumnHeader(hash.sort[which], hash.dir[which]);
	if (Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();

	Ext.getDom("grid_result_summary").innerHTML = '<b>' + Page.getStore(which).getTotalCount() + ' features found</b>';

	if (tree == null)
		createTree(518);
	else {

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
	}
}

function renderECNumber(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"http://expasy.org/enzyme/{0}\" target=\"_blank\">{0}</a>", value);
}

function renderKEGG(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="KEGG Pathway" data-qclass="x-tip"';
	return Ext.String.format('<a href=\"CompPathwayTable?cType=genome&amp;cId={0}&amp;algorithm={1}&amp;ec_number={2}\"><img src=\"/patric/images/patric-pathway-icon.png\" alt=\"KEGG Patwhays\" style=\"margin:-4px\" /></a>', record.data.gid, record.data.annotation, record.data.ec_number);
}

function renderGOID(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format('<a href="http://amigo.geneontology.org/cgi-bin/amigo/term_details?term={0}" target="_blank">{0}</a>', value);
}

function getSelectedFeatures() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), sl = Page.getCheckBox().getSelections(), i, fids = property.fids;

	for ( i = 0; i < sl.length; i++)
		fids.push(sl[i].data.na_feature_id);

}

function DownloadFile() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), tree = property.tree, form = Ext.getDom("fTableForm");

	if (tree.getSelectedTerms()["Keyword"] == null) {
		tree.selectedTerm["Keyword"] = Ext.getDom("keyword").value;
	}

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
	form.fileformat.value = arguments[0];
	form.download_keyword.value = constructKeyword(tree.getSelectedTerms(), property.name);
	form.target = "";
	getHashFieldsToDownload(form);

	form.submit();
}