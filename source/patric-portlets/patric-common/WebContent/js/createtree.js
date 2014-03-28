Ext.define('VBI.data.TreeStore', {
	extend : 'Ext.data.TreeStore',
	load : function(options) {
		options = options || {};
		options.params = options.params || {};

		var me = this, node = options.node || me.tree.getRootNode();

		// If there is not a node it means the user hasnt defined a rootnode yet. In this case lets just
		// create one for them.
		if (!node) {
			node = me.setRootNode({
				expanded : true
			});
		}

		if (me.clearOnLoad) {
			node.removeAll(false);
		}

		Ext.applyIf(options, {
			node : node
		});
		options.params[me.nodeParam] = node ? node.getId() : 'root';

		if (node) {
			node.set('loading', true);
		}

		return me.callParent([options]);
	}
});

function createTree(height) {

	Ext.define('VBI.treeNode', {
		extend : 'Ext.tree.tristate.Model',
		fields : [{
			name : 'text',
			type : 'string'
		}, {
			name : 'count',
			type : 'int'
		}, {
			name : 'parentId',
			type : 'int'
		}, {
			name : 'id',
			type : 'string'
		}]
	});

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : 0, name = which == 0 && !hash.hasOwnProperty('cat') ? property.name : property.name[which], url = configuration[name].url;

	var pluginView = Ext.create('Ext.tree.tristate.Plugin', {});

	property.treeDS = Ext.create('VBI.data.TreeStore', {
		model : 'VBI.treeNode',
		autoLoad : true,
		proxy : {
			type : 'ajax',
			actionMethods : {
				create : 'POST',
				read : 'POST'
			},
			url : url,
			noCache : false,
			extraParams : {
				need : "tree",
				pk : hash.key,
				keyword : constructKeyword({}, name),
				facet : JSON.stringify({
					"facet" : configuration[name].display_facets.join(","),
					"facet_text" : configuration[name].display_facets_texts.join(",")
				}),
				state : JSON.stringify({})
			}
		}
	});

	property.tree = Ext.create('Ext.tree.Panel', {
		id : "GenericSelectorTree",
		height : height || 565,
		store : property.treeDS,
		rootVisible : false,
		hideHeaders : true,
		renderTo : 'GenericSelector',
		border : false,
		selectedTerm : {},
		hiddenNodes : {},
		autoScroll : true,
		viewConfig : {
			plugins : [pluginView],
			style : {
				overflow : 'auto',
				overflowX : 'hidden'
			}
		},
		columns : [{
			xtype : 'tristatetreecolumn',
			flex : 1,
			dataIndex : 'text'
		}],
		listeners : {
			checkchange : function() {
				var root_children = this.getRootNode().childNodes, i, parentID, display_facets, flag = false, temp = {};

				this.selectedTerm = {};
				for ( i = 0; i < root_children.length; i++)
					root_children[i].raw.checked = false;

				if (this.view.getChecked().length > 0) {
					Ext.each(this.view.getChecked(), function(node) {
						flag = false;
						parentID = node.raw.parentID;
						display_facets = configuration[name].display_facets.join(",");

						if (display_facets.indexOf(node.internalId) == -1) {
							flag = true;
							if (parentID.indexOf("_more") != -1) {
								parentID = parentID.split("_more")[0];
							}
						}

						if (flag == true) {
							this.store.getNodeById(parentID).raw.checked = true;
							(!temp[parentID]) ? temp[parentID] = [] : "";
							temp[parentID].push(node.internalId.split("##")[0]);
							this.selectedTerm[parentID] = temp[parentID].join("##");
						}

					}, this);
				} else {
					root_children = this.getRootNode().childNodes;
					for ( i = 0; i < root_children.length; i++)
						root_children[i].raw.checked = false;
				}
				refresh();
			},
			itemclick : function(view, record, item, index, e, options) {
				var node = this.store.getNodeById(record.raw.id), parentNode = node.parentNode, n = [], i, end, start = 4;
				if (node.id.indexOf("_more") != -1) {

					for ( i = 0; i < node.childNodes.length; i++)
						n.push(node.childNodes[i]);

					for ( i = 0; i < n.length; i++)
						parentNode.appendChild(n[i]);

					this.hiddenNodes[parentNode.raw.id] = node;
					parentNode.removeChild(node);

				} else if (node.id.indexOf("_less") != -1) {

					end = parentNode.childNodes.length;
					start = 4;
					if (parentNode.childNodes[0].id == parentNode.id + '_clear')
						start = start + 1;

					if (parentNode.childNodes[end - 1].id == parentNode.id + '_more')
						end = end - 1;

					for ( i = start; i < end; i++) {
						n.push(parentNode.childNodes[i]);
					}

					this.hiddenNodes[parentNode.raw.id].data.expanded = false;
					this.hiddenNodes[parentNode.raw.id].appendChild(n);
					parentNode.appendChild(this.hiddenNodes[parentNode.raw.id]);

				} else if (node.id.indexOf("_clear") != -1) {

					parentNode.cascadeBy(function(child) {
						child.set("checked", "false");
					});
					parentNode.raw.checked = false;
					this.selectedTerm[parentNode.raw.id] = "";
					refresh();
				}
			}
		},
		getSelectedTerms : function() {
			return this.selectedTerm;
		},
		getState : function() {

			var state = {}, rc = this.getRootNode().childNodes, i, j, k, obj, oc, snc, co = {}, so = {}, sss = [], ssnc = {}, sso = {};

			for ( i = 0; i < rc.length; i++) {

				if (rc[i].raw.checked == true) {

					obj = {}, oc = [], obj.id = rc[i].data.id, obj.text = rc[i].data.text, obj.checked = rc[i].data.checked, obj.leaf = rc[i].data.leaf, obj.expanded = true, snc = rc[i].childNodes;

					if (this.getSelectedTerms()[rc[i].data.id] != null && snc[0].data.id != rc[i].data.id + "_clear") {
						co = {}, co.leaf = true, co.parentID = rc[i].data.id, co.checked = false, co.id = rc[i].data.id + "_clear", co.text = "<b>clear</b>", oc.push(co);
					}

					for ( j = 0; j < snc.length; j++) {

						if (this.getSelectedTerms()[rc[i].data.id] == null && snc[j].data.id == snc[j].data.parentID + "_clear") {
						} else if (snc[j].data.leaf == true) {

							so = {};
							so.id = snc[j].data.id;
							so.text = snc[j].data.text;
							so.checked = snc[j].data.checked;
							so.leaf = snc[j].data.leaf;
							so.parentID = snc[j].data.parentID;
							oc.push(so);
						} else {
							ssnc = snc[j].childNodes, sss = [], so = {};

							for ( k = 0; k < ssnc.length; k++) {
								sso = {}, sso.id = ssnc[k].data.id, sso.checked = ssnc[k].data.checked, sso.leaf = ssnc[k].data.leaf, sso.parentID = ssnc[k].data.parentID, sss.push(sso);
							}

							so.children = sss;
							so.expanded = snc[j].expanded;
							so.checked = snc[j].data.checked;
							so.id = snc[j].data.id;
							so.text = snc[j].data.text;
							so.parentID = snc[j].data.parentID;
							oc.push(so);
						}
					}
					obj.children = oc;
					state[rc[i].data.id] = obj;
				}
			}
			return state;
		}
	});
}

function refresh(action) {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, tree = property.tree, rc = tree.getRootNode().childNodes, i;

	hash.key = +Date.now();
	hash.aP[(hash.aT != null) ? hash.aT : 0] = 1;
	(hash.gId && hash.aT) ? (hash.gId = "", hash.gName = "", hash.cwG = false) : "";
	(hash.eId && hash.aT) ? (hash.eId = "", hash.eName = "", hash.cwG = false) : "";

	if (action == "clear_all") {
		for ( i = 0; i < rc.length; i++)
			rc[i].raw.checked = false;
		tree.selectedTerm = {};
	}

	tree.selectedTerm["Keyword"] = Ext.getDom("keyword").value;

	createURL();
}