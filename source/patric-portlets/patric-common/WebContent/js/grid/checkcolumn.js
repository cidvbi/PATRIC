Ext.grid.CheckColumn = function(config) {
	Ext.apply(this, config);
	if (!this.id) {
		this.id = Ext.id();
	}
	Ext.bind(this.renderer, this);
};

Ext.grid.CheckColumn.prototype = {
	init : function(grid) {
		this.grid = grid;
		this.checkAllState = false;
	},
	getGrid : function() {
		this.grid = (!this.grid) ? $Page.getGrid() : this.grid;
		return this.grid;
	},
	onMouseDown : function(e, t) {
		if (t.className && t.className.indexOf('x-grid3-cc-' + this.id) != -1) {
			var index = this.getGrid().getView().findRowIndex(t), record = this.getGrid().store.getAt(index);
			e.stopEvent();
			record.set(this.dataIndex, !record.data[this.dataIndex]);
			adjustCheckBoxes('');
		}
	},
	getCount : function() {
		var page_size = this.getGrid().getDockedItems('toolbar')[1].getPageSize(), count = 0, i, record;

		for ( i = 0; i < page_size; i++) {
			record = this.getGrid().store.getAt(i);
			if (record && record.get(this.dataIndex))
				count = count + 1;
		}
		return count;
	},
	getSelections : function() {
		var page_size = this.getGrid().getDockedItems('toolbar')[1].getPageSize(), sel_size = this.getCount(), sl = new Array(sel_size), count = 0, j, cur_record;

		for ( j = 0; j < page_size; j++) {
			cur_record = this.getGrid().store.getAt(j);
			if (cur_record && cur_record.get(this.dataIndex)) {
				sl[count] = this.getGrid().store.getAt(j), count++;
			}
		}
		return sl;
	},
	clearSelections : function() {
		var page_size = this.getGrid().getDockedItems('toolbar')[1].getPageSize(), j, cur_record;

		for ( j = 0; j < page_size; j++) {
			cur_record = this.getGrid().store.getAt(j);
			if (cur_record)
				cur_record.set(this.dataIndex, false);
		}
	},
	setCheckAll : function(state) {
		this.checkAllState = state;
	},
	getCheckAll : function() {
		return false;
	},
	updateCheckAllIcon : function() {
		Ext.getDom("checkbox_headerBox").src = "/patric/images/unchecked.gif";
	},
	showMessage : function(count, type) {
		var mesaj = {}, m, msgCt = null;

		mesaj['Feature'] = ' feature(s)', mesaj['Genome'] = ' genome(s)', mesaj['ExpressionExperiment'] = ' experiment(s)', msgCt = (!msgCt) ? Ext.core.DomHelper.insertFirst("workspace_toolbar", {
			id : 'atc-msg-div'
		}, true) : Ext.getDom('atc-msg-div'), m = Ext.core.DomHelper.append(msgCt, '<div class="msg"><p>' + count + mesaj[type] + ' added to workspace</p></div>', true), m.hide(), m.slideIn('l').ghost("l", {
			delay : 1250,
			remove : true
		});

	},
	renderer : function(v, p, record) {
		var cssPrefix = Ext.baseCSSPrefix, cls = [cssPrefix + 'grid-checkheader'];
		if (v)
			cls.push(cssPrefix + 'grid-checkheader-checked');
		return '<div class="' + cls.join(' ') + '">&#160;</div>';
	}
}; 