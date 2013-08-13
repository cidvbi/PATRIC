/**
 * @class Ext.ux.tree.tristate.Model
 * @extends Ext.data.Model
 * 
 * This class defines a data model for three state tree.
 */

Ext.define('Ext.ux.tree.tristate.Model', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
	fields: [{
		name: 'id', type: 'int'
	}, {
		name: 'name', type: 'string'
	}, {
		name: 'checked', type: 'boolean', defaultValue: false
	}, {
		name: 'partial', type: 'boolean', defaultValue: false
	}, {
		name: 'iconCls', type: 'string', defaultValue: 'x-tree-noicon'
	}, {
		name: 'leaf', type: 'boolean'
	}]
});


/**
 * @class Ext.ux.tree.tristate.Plugin
 * @extends Ext.AbstractPlugin
 * 
 * This class defines a plugin for tristate tree view
 */
 
Ext.define('Ext.ux.tree.tristate.Plugin', {
	extend: 'Ext.AbstractPlugin',
	alias: 'plugin.tristatetreeplugin',
	init: function(view) {
		view.on('checkchange', this.updateRecord, this);
	},
	updateRecord: function(record, value) {
		var me = this;
		//console.log("updteRecord is called");
		record.cascadeBy(function(child) {
			child.set('checked', value);
			child.set('partial', false);
		});
		if (record.parentNode != undefined) {
			me.updateAncestor(record.parentNode);
		}
	},
	updateAncestor: function(record) {
		//console.log("updateAncestor is called");
		var count,
			partial;
		record.bubble(function(parent) {
			count = 0,
			partial = false;
			parent.eachChild(function(sibling) {
				if (sibling.get('partial')) {
					partial = true;
				}
				if (sibling.get('checked')) {
					count++;
				}
			});
			if (partial) {
				parent.set('partial', true);
			} else {
				if (count == parent.childNodes.length) {
					parent.set('checked', true);
					parent.set('partial', false);
				}
				else if (count == 0) {
					parent.set('checked', false);
					parent.set('partial', false);
				}
				else {
					parent.set('partial', true);
				}
			}
		});
	}	
});


/**
 * @class Ext.ux.tree.tristate.Column
 * @extends Ext.grid.column.Column
 * 
 * This class overides a renderer function in order to display a partial check state. 
 * Actual modifications are commented //= modification from/to here =//
 *
 */
 
Ext.define('Ext.ux.tree.tristate.Column', {
	extend: 'Ext.grid.column.Column',
	alias: 'widget.tristatetreecolumn',
	renderer: function(value, metaData, record, rowIdx, colIdx, store, view) {
		var buf = [],
			format = Ext.String.format,
			depth = record.getDepth(),
			treePrefix = Ext.baseCSSPrefix + 'tree-',
			elbowPrefix = treePrefix + 'elbow-',
			expanderCls = treePrefix + 'expander',
			imgText = '<img src="{1}" class="{0}" />',
			checkboxText = '<input type="button" role="checkbox" class="{0}" {1} />',
			formattedValue = value,
			href = record.get('href'),
			target = record.get('hrefTarget'),
			cls = record.get('cls');
		
		while (record) {
			if (!record.isRoot() || (record.isRoot() && view.rootVisible)) {
				if (record.getDepth() === depth) {
					buf.unshift(format(
						imgText,
						treePrefix + 'icon ' + treePrefix + 'icon' + (record.get('icon') ? '-inline ': (record.isLeaf() ? '-leaf ': '-parent ')) + (record.get('iconCls') || ''),
						record.get('icon') || Ext.BLANK_IMAGE_URL
					));
					if (record.get('checked') !== null) {
						//= modification from here =//
						//console.log("  renderer called:"+record.get('name')+", partial="+record.get('partial')+", checked="+record.get('checked'));
						buf.unshift(format(
							checkboxText,
							(treePrefix + 'checkbox') + (record.get('partial') ? ' ' + treePrefix + 'checkbox-partial': record.get('checked') ? ' ' + treePrefix + 'checkbox-checked': ''),
							record.get('checked') ? 'aria-checked="true"': ''
						));
						if (record.get('checked')) {
							metaData.tdCls += (' ' + Ext.baseCSSPrefix + 'tree-checked');
						}
						//= modification to here =//
					}
					if (record.isLast()) {
						if (record.isExpandable()) {
							buf.unshift(format(imgText, (elbowPrefix + 'end-plus ' + expanderCls), Ext.BLANK_IMAGE_URL));
						} else {
							buf.unshift(format(imgText, (elbowPrefix + 'end'), Ext.BLANK_IMAGE_URL));
						}
					} else {
						if (record.isExpandable()) {
							buf.unshift(format(imgText, (elbowPrefix + 'plus ' + expanderCls), Ext.BLANK_IMAGE_URL));
						} else {
							buf.unshift(format(imgText, (treePrefix + 'elbow'), Ext.BLANK_IMAGE_URL));
						}
					}
				} else {
					if (record.isLast() || record.getDepth() === 0) {
						buf.unshift(format(imgText, (elbowPrefix + 'empty'), Ext.BLANK_IMAGE_URL));
					} else if (record.getDepth() !== 0) {
						buf.unshift(format(imgText, (elbowPrefix + 'line'), Ext.BLANK_IMAGE_URL));
					}
				}
			}
			record = record.parentNode;
		}
		// end of while
		if (href) {
			formattedValue = format('<a href="{0}" target="{1}">{2}</a>', href, target, formattedValue);
		}
		if (cls) {
			metaData.tdCls += ' ' + cls;
		}
		return buf.join("") + formattedValue;
	}
});

/*
store =  Ext.create('Ext.data.TreeStore', {
	model: 'Ext.ux.tree.tristate.Model',
	proxy: {...}
});

tree = Ext.create('Ext.tree.Panel', {
	renderTo:'tree-div',
	store: store,
	rootVisible: false,
	hideHeaders: true,
	viewConfig: {
		plugins: {
			ptype: 'tristatetreeplugin'
		}
	},
	columns: [{
		xtype	: 'tristatetreecolumn',
		flex	: 1,
		dataIndex: 'name'
	}]
});
*/
