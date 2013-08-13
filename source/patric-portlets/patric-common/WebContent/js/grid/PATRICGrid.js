Ext.define('Ext.grid.PATRICGrid', {
	extend : 'Ext.grid.Panel',
	selModel : new Ext.grid.PATRICSelectionModel(),
	minHeight : 550,
	border : false,
	scroll : false,
	sortchangeOption : true,
	listeners : {
		itemmouseenter : function(view, record, item, index, e) {
			this.selModel.handleMouseOver(view, record, item, index, e);
		},
		itemmouseup : function(view, record, item, index, e) {
			this.selModel.handleMouseUp(view, record, item, index, e);
		},
		itemmousedown : function(view, record, item, index, e) {
			this.selModel.handleMouseDown(view, record, item, index, e);
		},
		render : function() {
			addKeyMap(this);
			if (Ext.getCmp('copy_popup') == null)
				createCopyBox();
		},
		afterlayout : function() {
			Ext.getCmp('grid_column_toolbar_sh') ? Ext.getCmp('grid_column_toolbar_sh').items.items[0].menu = this.headerCt.getMenu().items.items[3].menu : "";
			Ext.getCmp('grid_column_toolbar_sh_min') ? Ext.getCmp('grid_column_toolbar_sh_min').items.items[0].menu = this.headerCt.getMenu().items.items[3].menu : "";
		},
		itemcontextmenu : function(view, record, item, index, e) {
			e.stopEvent();
			var Page = $Page, property = Page.getPageProperties();
			if (property.name && property.name != "GlobalSearch") {
				this.selModel.handleContextMenu(view);

				if (!this.ctxMenu) {
					this.ctxMenu = Ext.create('Ext.menu.Menu', {
						items : [{
							text : 'Copy',
							listeners : {
								click : function() {
									onCopyItemClick(e);
								}
							}
						}]
					});
				}

				e.xy = [e.browserEvent.pageX, e.browserEvent.pageY];
				this.ctxMenu.showAt(e.xy);
			}
		},
		sortchange : function(ct, column, direction, eOpts) {
			var Page = $Page, property = Page.getPageProperties(), name = property.name;
			if (this.sortchangeOption) {
				if (!Page.exemptList.some(function(element, index, array) {
					return name == element;
				})) {
					this.setSortDirectionColumnHeader([{
						property : column.dataIndex,
						direction : direction
					}]);
					this.sortchangeOption = false;
				} else {
					this.SortChange(ct, column, direction, eOpts);
				}
			}
		}
	},
	setSortDirectionColumnHeader : function(incomingstate) {

		var columns = this.columns, id, i, j, sort = null, direction = null, property = $Page.getPageProperties(), hash = property.hash, which = hash.aT ? hash.aT : 0, state = incomingstate || (property.stateId && Ext.state.Manager.get(property.stateId[which]) ? Ext.state.Manager.get(property.stateId[which]).sort : property.sort[which]);

		if (state != undefined) {
			if (state.length == 1) {
				sort = "", direction = "";
				sort = state[0].property;
				direction = state[0].direction;
			} else {
				sort = [], direction = [];
				for ( i = 0; i < state.length; i++) {
					sort.push(state[i].property);
					direction.push(state[i].direction);
				}
			}
		}

		if (sort != undefined) {
			for ( i = 0; i < columns.length; i++) {
				id = Ext.get(columns[i].id);
				if (id) {
					id.removeCls('x-column-header-sort-ASC');
					id.removeCls('x-column-header-sort-DESC');
				}
				if (Object.prototype.toString.call(sort) === "[object Array]") {
					for ( j = 0; j < sort.length; j++) {
						sort[j] = sort[j].replace(/_sort/g, "");
						if (columns[i].dataIndex == sort[j])
							id.addCls('x-column-header-sort-' + direction[j]);
					}
				} else {
					sort = sort.replace(/_sort/g, "");
					if (columns[i].dataIndex == sort)
						id.addCls('x-column-header-sort-' + direction);
				}
			}
		}
	},
	collectGridRenderer : function(cr) {
		return this.columns[cr[1]].renderer.name;
	},
	collectGridData : function(cr) {
		var row1 = cr[0], col1 = cr[1], row2 = cr[2], col2 = cr[3], rowTsv = "", c, r, tsvData = "";

		for ( c = col1; c <= col2; c++) {
			if (tsvData)
				tsvData += "\t";
			tsvData += this.columns[c].text;
		}
		for ( r = row1; r <= row2; r++) {
			if (tsvData)
				tsvData += "\n";

			rowTsv = "";
			for ( c = col1; c <= col2; c++) {
				if (rowTsv != "")
					rowTsv += "\t";

				if (this.columns[c].dataIndex)
					rowTsv += this.store.getAt(r).get(this.columns[c].dataIndex);
				else
					rowTsv += "\t";
			}
			tsvData += rowTsv;
		}
		return tsvData;
	},
	getState : function() {
		var me = this, state = me.callParent(), sorter = me.store.sorters.first();

		state = me.addPropertyToState(state, 'columns', (me.headerCt || me).getColumnsState());

		if (sorter) {
			var sorters = [];
			sorters[0] = {
				property : sorter.property,
				direction : sorter.direction,
				root : sorter.root
			};
			if (me.store.sorters.length > 1) {
				sorter = me.store.sorters.last();
				sorters[1] = {
					property : sorter.property,
					direction : sorter.direction,
					root : sorter.root
				};
			}
			state = me.addPropertyToState(state, 'sort', sorters);
		}
		return state;
	},
	applyState : function(state) {
		var me = this, sorter = state.sort, store = me.store, columns = state.columns;
		delete state.columns;

		// Ensure superclass has applied *its* state.
		// AbstractComponent saves dimensions (and anchor/flex) plus collapsed state.
		me.callParent(arguments);

		if (columns) {
			(me.headerCt || me).applyColumnsState(columns);
		}

		if (sorter) {
			if (store.remoteSort) {
				// Pass false to prevent a sort from occurring
				// store.sort(sorter);
				store.sorters.removeAll(store.sorters.items);
				for (var i = 0; i < sorter.length; i++) {
					store.sorters.add(new Ext.util.Sorter({
						property : sorter[i].property,
						direction : sorter[i].direction
					}));
				}
			} else {
				//store.sort(sorter.property, sorter.direction);
				// store.sort(sorter[0].property, sorter[0].direction);
			}
		}
	}
});

function addKeyMap(grid) {
	new Ext.util.KeyMap(grid.getEl(), [{
		key : 67,
		ctrl : true,
		fn : function() {
			onCopyItemClick();
		}
	}]);
}

function onCopyItemClick(e) {
	var Page = $Page, grid = Page.getGrid(), selectionModel = grid.getSelectionModel();

	Ext.getCmp('copy_popup').show();
	Ext.getCmp('copy_text').focus();
	Ext.getCmp('copy_text').setRawValue(grid.collectGridData(selectionModel.getSelectedCellRange()));
}

function createCopyBox() {
	var btnCopy = Ext.create('Ext.CopyButton', {
		id : 'btnCopy',
		text : 'Copy to Clipboard',
		renderTo : 'copy-button',
		margins : {
			bottom : 0,
			top : 0,
			left : 0,
			right : 5
		},
		getValue : function() {
			var grid = $Page.getGrid();
			Ext.getCmp('copy_popup').hide();
			grid.getSelectionModel().clearSelections(grid.getView());
			return Ext.getCmp('copy_text').getValue();
		}
	});

	var copy_form = Ext.create('Ext.form.Panel', {
		url : "/patric-common/jsp/export_copied_handler.jsp",
		standardSubmit : true,
		width : 388,
		height : 258,
		items : [{
			xtype : 'textarea',
			id : 'copy_text',
			value : "",
			width : 388,
			height : 258,
			inputId : 'copy_text_to_file',
			autoScroll : true,
			wordWrap : false,
			listeners : {
				render : function() {
					this.rows = "18";
					this.cols = "50";
				}
			}
		}],
		buttons : [{
			xtype : 'button',
			id : 'btnExport',
			text : 'Export to File',
			handler : function() {

				var form = this.up('form').getForm();
				form.submit();

				Ext.getCmp('copy_popup').hide();

			}
		}, btnCopy, {
			xtype : 'button',
			id : 'btnCopyCancel',
			text : 'Cancel',
			handler : function() {
				Ext.getCmp('copy_popup').hide();
			}
		}]

	});

	Ext.create('Ext.window.Window', {
		title : 'Export/Copy Selected Data',
		height : 320,
		width : 400,
		layout : 'fit',
		id : 'copy_popup',
		closeAction : 'hide',
		items : [copy_form]

	});

	Ext.getCmp('copy_popup').hide();
}

function getSortersInText(store) {
	var sorter = store.sorters.items, obj = {}, i;

	obj.property = sorter[0].property;
	obj.direction = sorter[0].direction;

	for ( i = 1; i < sorter.length; i++) {
		obj.property += "," + sorter[i].property;
	}

	return obj;
}
