var loadGrid = function() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0, grid = Page.getGrid() || null, store = Page.getStore(which) || null, checkbox = Page.getCheckBox() || null, hash = property.hash, scm = property.scm[which], i, s, plugin = property.plugin, plugintype = property.plugintype, state = property.stateId ? Ext.state.Manager.get(property.stateId[which]) : null, pageSize = 20, isExceededMaxPageSize = false, global;

	// compute maxPageSize
	if (Ext.state.Manager.get('pagesize') != undefined && Ext.state.Manager.get('pagesize').value != undefined) {
		global = Ext.state.Manager.get('pagesize').value;
		if (property.maxPageSize != undefined && property.maxPageSize < global) {
			pageSize = property.maxPageSize;
			isExceededMaxPageSize = true;
		} else {
			pageSize = global;
		}
	}

	if (!store) {
		scm.forEach(function(element, index, array) {
			if (!element.id) {
				element.id = property.model[which] + '_' + element.dataIndex;
			}
		});

		store = Page.store[which] = Ext.create('Ext.data.Store', {
			storeId : 'ds',
			model : property.model[which],
			proxy : {
				type : 'ajax',
				url : hash.hasOwnProperty('cat') ? configuration[property.name[which]].url : (property.url[which] ? property.url[which] : property.url[0]),
				noCache : false,
				timeout : 600000, //10*60*1000
				actionMethods : {
					create : 'POST',
					read : 'POST'
				},
				reader : {
					type : 'json',
					root : 'results',
					totalProperty : 'total'
				},
				afterRequest : function(request, success) {
					if (success == false) {
						Ext.Msg.alert('Failure', 'Sorry, this request is timed out. Please try again.');
					}
				}
			},
			autoLoad : false,
			remoteSort : property.remoteSort ? true : false,
			pageSize : pageSize,
			listeners : {
				datachanged : function(st, options) {
					MaskGrid(st.getTotalCount(), property.callBackFn);
				},
				beforeload : function(st, operation, options) {

					// remove no results mask if it is still there
					$('#PATRICGrid > .x-mask-msg').remove();
					$('#PATRICGrid > .x-mask').remove();

					var sorter, conf;
					if ( typeof configuration != "undefined" && configuration[property.model[which]]) {
						conf = configuration[property.model[which]];
						if (conf) {
							sorter = st.getSorters();
							for (var i = 0; i < sorter.length; i++) {
								sorter[i].property.trim().replace(/_sort/g, '');
								if (conf.sort_fields && conf.sort_fields.indexOf(sorter[i].property) >= 0) {
									sorter[i].property += "_sort";
								}
							}
						}
					}
				}
			}
		});
	}

	/*
	 * Read from state, if not available read from property object for default
	 * The getSorters function is in PATRICGrid.js
	 */

	store.sorters.clear();
	if (property.remoteSort && ((state && state.sort) || property.sort)) {
		s = (state && state.sort) ? state.sort : property.sort[which];

		for ( i = 0; i < s.length; i++) {
			store.sorters.add(new Ext.util.Sorter({
				property : s[i].property,
				direction : s[i].direction
			}));
		}
	}

	store.getProxy().extraParams = property.extraParams();
	store.loadPage(hash.aP[which]);

	if (!grid) {
		grid = Page.grid = Ext.create('Ext.grid.PATRICGrid', {
			store : store,
			columns : {
				items : scm
			},
			tbar : property.cart ? createToolbar(property.cartType, property.WoWorkspace ? "table_wo_workspace" : "", property.gridType) : "",
			plugins : (plugin && plugintype == "checkbox") ? checkbox : property.pluginConfig,
			dockedItems : [{
				dock : 'bottom',
				xtype : 'patricpagingtoolbar',
				store : store,
				id : 'pagingtoolbar',
				displayMsg : property.pagingBarMsg ? property.pagingBarMsg[which] : 'Displaying record {0} - {1} of {2}',
				maxPageSize : property.maxPageSize ? property.maxPageSize : 5000
			}],
			renderTo : 'PATRICGrid',
			border : property.border ? property.border : false,
			stateful : true,
			stateEvents : ['hide', 'show', 'columnmove', 'columnresize', 'sortchange'],
			stateId : property.stateId ? property.stateId[which] : "NA",
	        viewConfig: {
	            stripeRows: true
	        }
		});

		if (property.hideToolbar)
			grid.removeDocked(grid.getDockedItems()[0]);

	} else {
		if (property.stateId)
			grid.stateId = property.stateId[which];

		// update display message
		if (property.pagingBarMsg != undefined) {
			grid.getDockedItems('pagingtoolbar')[0].displayMsg = property.pagingBarMsg[which];
		}

		grid.sortchangeOption = false;

		if (plugin && plugintype == 'rowexpander') {
			grid.reconfigure(store);
		} else {
			grid.reconfigure(store, scm);
		}

		grid.getDockedItems("pagingtoolbar")[0].bindStore(store);
		grid.getDockedItems('pagingtoolbar')[0].updateInfo();
		grid.getDockedItems('pagingtoolbar')[0].setPageSize(store.pageSize);
		grid.sortchangeOption = true;
	}
	if (property.stateId) {
		grid.stateId = property.stateId[which];
	}
	if (state) {
		ApplyState(state, grid);
	}

	if (Modernizr && !Modernizr.history) {
		if (property.current_hash != window.location.href.split("#")[1]) {
			property.current_hash = window.location.href.split("#")[1];
		}
	}
	if (isExceededMaxPageSize == true && store.totalCount > property.maxPageSize) {
		//grid.getDockedItems("pagingtoolbar")[0].showMessageTip("Message", "At this time, we can only show " + property.maxPageSize + " records at a time for these type of tables. We will remember and apply your table seetings to other tables when possible");
		grid.getDockedItems("pagingtoolbar")[0].showMessageTip("Message", "At this time, we can only show " + property.maxPageSize + " records at a time for these type of tables.");
	}
}; 