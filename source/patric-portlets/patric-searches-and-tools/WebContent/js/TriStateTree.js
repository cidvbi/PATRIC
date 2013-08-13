/**
 * data model
 */
Ext.define('Ext.tree.tristate.Model', {
	extend : 'Ext.data.Model',
	fields : [{
		name : 'id',
		type : 'int'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'checked',
		type : 'boolean',
		defaultValue : false
	}, {
		name : 'partial',
		type : 'boolean',
		defaultValue : false
	}, {
		name : 'iconCls',
		type : 'string',
		defaultValue : 'x-tree-noicon'
	}, {
		name : 'leaf',
		type : 'boolean'
	}]
});

/**
 * plugin for tristate tree view
 */
Ext.define('Ext.tree.tristate.Plugin', {
	init : function(view) {
		view.onCheckboxChange = function(e, t) {
			var item = e.getTarget(this.getItemSelector(), this.getTargetEl()), record, value;
			if (item) {
				record = this.getRecord(item);
				value = !record.get('checked');

				//
				record.set('checked', value);
				record.set('partial', false);
				if (!record.get('leaf')) {
					this.bubbleDown(value, record);
				}
				this.bubbleUp(value, record.parentNode);
				//

				this.fireEvent('checkchange', record, value);
			}
		};
		view.bubbleUp = function(checked, record) {
			record.bubble(function(parent) {
				count = 0, partial = false;
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
					} else if (count == 0) {
						parent.set('checked', false);
						parent.set('partial', false);
					} else {
						parent.set('partial', true);
					}
				}
			});
		};
		view.bubbleDown = function(checked, record) {
			record.cascadeBy(function(child) {
				child.set('checked', checked);
				child.set('partial', false);
			});
		};
	}
});
/**
 * tree column
 */
Ext.define('Ext.tree.tristate.Column', {
	extend : 'Ext.grid.column.Column',
	alias : 'widget.tristatetreecolumn',
	initComponent : function() {
		var origRenderer = this.renderer || this.defaultRenderer, origScope = this.scope || window;

		this.renderer = function(value, metaData, record, rowIdx, colIdx, store, view) {
			var buf = [], format = Ext.String.format, depth = record.getDepth(), treePrefix = Ext.baseCSSPrefix + 'tree-', elbowPrefix = treePrefix + 'elbow-',
			// expanderCls = treePrefix + 'expander',
			imgText = '<img src="{1}" class="{0}" />', checkboxText = '<input type="button" role="checkbox" class="{0}" {1} />', formattedValue = origRenderer.apply(origScope, arguments), href = record.get('href'), target = record.get('hrefTarget'), cls = record.get('cls');

			while (record) {
				if (!record.isRoot() || (record.isRoot() && view.rootVisible)) {
					if (record.getDepth() === depth) {
						buf.unshift(format(imgText, treePrefix + 'icon ' + treePrefix + 'icon' + (record.get('icon') ? '-inline ' : (record.isLeaf() ? '-leaf ' : '-parent ')) + (record.get('iconCls') || ''), record.get('icon') || Ext.BLANK_IMAGE_URL));

						if (record.raw.renderstep == "2") {
							if (record.get('checked') !== null) {
								// = modification from here =//

								buf.unshift(format(checkboxText, (treePrefix + 'checkbox') + (record.get('partial') ? ' ' + treePrefix + 'checkbox-partial' : record.get('checked') ? ' ' + treePrefix + 'checkbox-checked' : ''), record.get('checked') ? 'aria-checked="true"' : ''));
								if (record.get('checked')) {
									metaData.tdCls += (' ' + Ext.baseCSSPrefix + 'tree-checked');
								}
								// = modification to here =//
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
			}// end of while

			if (href) {
				formattedValue = format('<a href="{0}" target="{1}">{2}</a>', href, target, formattedValue);
			}
			if (cls) {
				metaData.tdCls += ' ' + cls;
			}
			return buf.join("") + formattedValue;

		};
		// end of renderer()

		this.callParent(arguments);
	},
	defaultRenderer : function(value) {
		return value;
	}
});
