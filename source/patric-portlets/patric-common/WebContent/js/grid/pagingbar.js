Ext.define('PATRICPagingToolbar', {
	extend : 'Ext.toolbar.Paging',
	alias : 'widget.patricpagingtoolbar',
	beforePageSizeText : 'Show',
	afterPageSizeText : 'per page',
	displayMsg : 'Displaying record {0} - {1} of {2}',
	displayInfo : true,
	maxPageSize : 5000,
	maskOnDisable : true,
	getPagingItems : function() {
		var me = this;
		return [{
			itemId : 'first',
			tooltip : me.firstText,
			overflowText : me.firstText,
			iconCls : Ext.baseCSSPrefix + 'tbar-page-first',
			disabled : true,
			handler : me.moveFirst,
			scope : me
		}, {
			itemId : 'prev',
			tooltip : me.prevText,
			overflowText : me.prevText,
			iconCls : Ext.baseCSSPrefix + 'tbar-page-prev',
			disabled : true,
			handler : me.movePrevious,
			scope : me
		}, '-', me.beforePageText, {
			xtype : 'numberfield',
			itemId : 'inputItem',
			name : 'inputItem',
			cls : Ext.baseCSSPrefix + 'tbar-page-number',
			allowDecimals : false,
			minValue : 1,
			hideTrigger : true,
			enableKeyEvents : true,
			keyNavEnabled : false,
			selectOnFocus : true,
			submitValue : false,
			width : me.inputItemWidth,
			margins : '-1 2 3 2',
			listeners : {
				scope : me,
				keydown : me.onPagingKeyDown,
				blur : me.onPagingBlur
			}
		}, {
			xtype : 'tbtext',
			itemId : 'afterTextItem',
			text : Ext.String.format(me.afterPageText, 1)
		}, '-', {
			itemId : 'next',
			tooltip : me.nextText,
			overflowText : me.nextText,
			iconCls : Ext.baseCSSPrefix + 'tbar-page-next',
			disabled : true,
			handler : me.moveNext,
			scope : me
		}, {
			itemId : 'last',
			tooltip : me.lastText,
			overflowText : me.lastText,
			iconCls : Ext.baseCSSPrefix + 'tbar-page-last',
			disabled : true,
			handler : me.moveLast,
			scope : me
		}, '->', '-',
		/* modification start */
		me.beforePageSizeText, {
			xtype : 'numberfield',
			itemId : 'pagesize',
			cls : Ext.baseCSSPrefix + 'tbar-page-number',
			allowDecimals : false,
			minValue : 1,
			maxValue : 64000,
			hideTrigger : true,
			enableKeyEvents : true,
			selectOnFocus : true,
			submitValue : false,
			width : 50,
			margins : '-1 2 3 2',
			value : me.pageSize,
			stateful : true,
			stateId : 'pagesize',
			stateEvents : ['savePageSize'],
			applyState : function(state) {
				if (state != undefined && state.value != undefined) {
					if (me.maxPageSize < state.value) {
						this.setValue(me.maxPageSize);
					} else {
						me.store.pageSize = state.value;
						this.setValue(state.value);
					}
				}
			},
			initComponent : function() {
				var me = this;
				if (me.allowOnlyWhitespace === false) {
					me.allowBlank = false;
				}
				me.callParent();
				me.addEvents('autosize', 'keydown', 'keyup', 'keypress', 'change');
				//me.addStateEvents('change');
				me.setGrowSizePolicy();
				if (Ext.state.Manager.get("pagesize") == undefined || Ext.state.Manager.get("pagesize").value == undefined) {
					me.setValue(20);
				}
			},
			listeners : {
				scope : me,
				specialKey : function(field, e) {
					if (e.getKey() == e.ENTER) {
						var value = me.getPageSize(),
							valueIsNull = value === null;

						if (valueIsNull == false) {
							me.updateStore();
						}
					}
				}
			}
		}, me.afterPageSizeText, {
			itemId : 'refresh',
			text : 'Apply',
			tooltip : 'Apply to This Table Only',
			style : {
				'border-color' : '#81a4d0',
				'background-color' : '#dbeeff',
				'background-image' : '-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler : function() {
				me.updateStore();
			},
			scope : me
		}, '-', {
			itemId : 'saveState',
			text : 'Apply to ALL tables',
			tooltip : 'Apply to ALL tables',
			style : {
				'border-color' : '#81a4d0',
				'background-color' : '#dbeeff',
				'background-image' : '-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler : function() {
				if (me.updateStore() != false) {
					me.child('#pagesize').fireEvent('savePageSize');
				}
			},
			scope : me
		}, '-'];
	},
	getPageSize : function() {
		return this.child('#pagesize').getValue();
	},
	setPageSize : function(value) {
		if ( typeof value == 'string') {
			this.child('#pagesize').setValue(parseInt(value));
		} else if ( typeof value == 'number') {
			this.child('#pagesize').setValue(value);
		}
	},
	getInputItemValue : function() {
		return this.child('#inputItem').getValue();
	},
	setInputItemValue : function(value) {
		if ( typeof value == 'string') {
			this.child('#inputItem').setValue(parseInt(value));
		} else if ( typeof value == 'number') {
			this.child('#inputItem').setValue(value);
		}
	},
	moveFirst : function() {
		var me = this;

		if (me.store.currentPage != 1) {
			me.store.currentPage = 1;
			if (this.checkStoreIsLocal()) {
				me.doPagingLoad();
			} else {
				me.updateHash();
			}
		}
	},
	movePrevious : function() {
		var me = this, prev = me.store.currentPage - 1;

		if (prev > 0) {
			me.store.currentPage = prev;
			if (this.checkStoreIsLocal()) {
				me.doPagingLoad();
			} else {
				me.updateHash();
			}
		}
	},
	moveNext : function() {
		var me = this, next = parseInt(me.store.currentPage) + 1, total;

		if (this.checkStoreIsLocal()) {
			total = Math.ceil(me.store.getTotalCount() / me.store.pageSize);
			if (next <= total) {
				me.store.currentPage = next;
				me.doPagingLoad();
			}
		} else {
			total = me.getPageData().pageCount;
			if (next <= total) {
				me.store.currentPage = next;
				me.updateHash();
			}
		}

	},
	moveLast : function() {
		var me = this, last = 0;

		if (this.checkStoreIsLocal()) {
			last = Math.ceil(me.store.getTotalCount() / me.store.pageSize);
			me.store.currentPage = last;
			me.doPagingLoad();
		} else {
			last = me.getPageData().pageCount;
			me.store.currentPage = last;
			me.updateHash();
		}
	},
	updateHash : function() {
		var me = this, Page = $Page, property = Page.getPageProperties(), hash = property.hash, id = 0;

		if (property.items > 1) {
			id = hash.hasOwnProperty('cat') ? hash.cat : Page.getCurrentItemId();
		}
		hash.aP[id] = me.store.currentPage;
		createURL();
	},
	showMessageTip : function(title, msg) {
		var me = this;
		Ext.create('Ext.tip.QuickTip', {
			target : me.child("#pagesize").el,
			closable : true,
			autoHide : false,
			anchor : 'bottom',
			bodyPadding : 10,
			title : title,
			html : msg
		}).show();
	},
	updateStore : function() {
		var me = this,
			store = me.getStore(),
			pagesize = me.child("#pagesize").getValue();

		if (pagesize > me.maxPageSize) {

			this.showMessageTip('Warning', 'Maximum number of visible rows must be smaller than ' + me.maxPageSize + '.');
			return false;

		} else {

			if (pagesize != store.pageSize) {
				store.currentPage = 1;
				store.pageSize = pagesize;
				if (this.checkStoreIsLocal()) {
					this.doPagingLoad();
				} else {
					store.load();
				}
			}
		}
	},
	checkStoreIsLocal : function() {
		var Page = $Page, id = this.container.id;

		if (Page.exemptList.some(function(element, index, array) {
			return id == element;
		})) {
			return true;
		} else {
			return false;
		}
	},
	doPagingLoad : function() {

		var Page = $Page, property = Page.getPageProperties(), dataStore = this.getStore(), gridObject = getScratchObject(property.name), pageData = {}, startSet, stopSet;

		startSet = parseInt(dataStore.currentPage - 1) * parseInt(dataStore.pageSize);
		stopSet = parseInt(dataStore.pageSize);

		stopSet += startSet;
		var temp = new Array();

		for (var i = startSet; i < stopSet; i++) {
			if (gridObject.Pagingstore.data.items[i] != null)
				temp[i - startSet] = gridObject.Pagingstore.data.items[i].data;

		}
		dataStore.loadData(temp);
		updatePaging(this.container.id, dataStore.currentPage);
		gridObject.gridState.pageSize = dataStore.pageSize;
		pageData.fromRecord = startSet + 1;
		pageData.toRecord = stopSet;
		pageData.total = dataStore.getTotalCount();
		pageData.pageCount = Math.ceil(pageData.total / dataStore.pageSize);
		this.setInputItemValue(dataStore.currentPage);
		this.arrangeButtonBehaviors();
		this.SetandEnableTexts(pageData);
	},
	SetandEnableTexts : function(pageData) {// Added to handle for local paging
		this.child('#inputItem').setDisabled();
		this.child('#afterTextItem').setText("of " + pageData.pageCount);
		var msg = Ext.String.format(this.displayMsg, pageData.fromRecord, pageData.toRecord, pageData.total);
		this.child('#displayItem').setText(msg);
	},
	arrangeButtonBehaviors : function() {// Added to handle for local paging
		var ceil = Math.ceil(this.getStore().getTotalCount() / this.getPageSize());

		if (this.getInputItemValue() == 1) {
			this.child('#prev').setDisabled(true);
			this.child('#first').setDisabled(true);
			if (this.getInputItemValue() == ceil) {
				this.child('#next').setDisabled(true);
				this.child('#last').setDisabled(true);
			} else {
				this.child('#next').setDisabled(false);
				this.child('#last').setDisabled(false);
			}

		} else if (this.getInputItemValue() != 1) {
			this.child('#prev').setDisabled(false);
			this.child('#first').setDisabled(false);
		}

		if (this.getInputItemValue() == ceil) {
			this.child('#next').setDisabled(true);
			this.child('#last').setDisabled(true);

		} else if (this.getInputItemValue() != ceil) {
			this.child('#next').setDisabled(false);
			this.child('#last').setDisabled(false);
		}
	}
});
