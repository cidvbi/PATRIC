var currentData;

function GroupGrid(gridId, stateObject, instance) {
	this.familyCount = 0;
	this.stateObject = stateObject;
	this.gridId = gridId;
	this.grid = null;
	this.allowHashing = false;
	this.activeTab = 0;
	this.isHeatMapLoaded = false;

	this.syntenyComboStore = Ext.create('Ext.data.Store', {
		fields : ['id', 'value']
	});

	this.syntenyCombo = Ext.create('Ext.form.ComboBox', {
		queryMode : 'local',
		width : 260,
		triggerAction : 'all',
		stateful : true,
		store : this.syntenyComboStore,
		valueField : 'id',
		displayField : 'value',
		typeAhead : false,
		label : 'Sort Protein Families by',
		listeners : {
			'select' : function(combo, records, options) {
				Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');
				setSyntenyOrder(gridId, records[0].data.id);
			}
		}
	});

	Figfam = Ext.define('Figfam', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'members',
			type : 'int'
		}, {
			name : 'species',
			type : 'int'
		}, {
			name : 'description'
		}, {
			name : 'min',
			type : 'int'
		}, {
			name : 'max',
			type : 'int'
		}, {
			name : 'mean',
			type : 'int'
		}, {
			name : 'std'
		}, {
			name : 'groupId'
		}, {
			name : 'intensity'
		}, {
			name : 'order',
			type : 'int'
		}, {
			name : 'window'
		}]
	});

	this.headerReader = new Ext.data.reader.Array({
		model : 'Figfam'
	}, Figfam);

	this.checkbox = createCheckBox(gridId);
	this.store = null;

	this.cm = null;

	this.sortField = $Page.getPageProperties().sort[0].property;
	this.sortDir = $Page.getPageProperties().sort[0].direction;

	this.heatMapPaneltoolbar = new Ext.create('Ext.toolbar.Toolbar', {
		items : [{
			text : "Flip Axis",
			handler : function() {
				Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');
				if (stateObject.heatmapAxis == "Transpose")
					stateObject.heatmapAxis = "";
				else
					stateObject.heatmapAxis = "Transpose";

				flipAxises(gridId);
				updateFilterState(gridId, stateObject);
				(jQuery(heatmapid)[0]).refreshData();
				Ext.get("sample-layout").unmask();
			}
		}, {
			text : "Cluster",
			handler : function() {
				Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');
				DoCluster(gridId);
			}
		}, {
			text : "Advanced Clustering",
			handler : function() {
				DoAdvancedCluster(gridId);
			}
		}, this.syntenyCombo, {
			xtype : 'tbspacer',
			width : 10
		}/*,'->', {text: "Download Heatmap", handler:function(){DownloadHeatmap(gridId);}}*/]

	});

	this.heatMapPanel = Ext.create('Ext.Panel', {
		id : 'heatmap',
		height : 775,
		border : false,
		layout : 'border',
		tbar : this.heatMapPaneltoolbar,
		items : [{
			region : 'west',
			id : 'heatMapPanel_westPanel',
			stateful : true,
			stateEvents : ['collapse', 'expand', 'resize'],
			stateId : 'PFhmLengendPanel',
			split : true,
			collapsible : true,
			width : 150,
			bodyStyle : 'padding:2px; background-color:white;',
			title : 'Legend',
			contentEl : 'legend',
			collapsed : false,
			border : false,
			autoScroll : false,
			listeners : {
				render : function() {
					Ext.getDom("legend").style.visibility = "visible";
				}
			}
		}, {
			region : 'center',
			id : 'heatMapPanel_centerPanel',
			html : '<div id=\"flashTarget\"/></div>',
			border : false,
			split : true,
			autoScroll : false
		}],
		renderTo : gridId + '_4heatmap'
	});

	Ext.getDom('information').style.height = '25px';
	Ext.getDom("information").style.visibility = 'visible';

	this.showPanel = pickPanel;
	this.applyBackup = GroupGridBackup;
	this.gridState = new GroupGridState(this, stateObject, instance);

	this.store = Ext.create('Ext.data.Store', {
		model : 'Figfam',
		storeId : 'ds',
		pageSize : this.gridState.pageSize
	});

	this.Pagingstore = Ext.create('Ext.data.Store', {
		model : 'Figfam',
		storeId : 'ds_paging',
		pageSize : this.gridState.pageSize
	});

	var s = $Page.getPageProperties().sort;
	for (var i = 0; i < s.length; i++) {
		this.store.sorters.add(new Ext.util.Sorter({
			property : s[i].property,
			direction : s[i].direction
		}));
		this.Pagingstore.sorters.add(new Ext.util.Sorter({
			property : s[i].property,
			direction : s[i].direction
		}));
	}

	this.showPanel(true);
}

function GroupGridBackup(oldSyntony) {
	var backState = this.gridState;

	var stateObject = this.stateObject;
	stateObject.filter = backState.filter;
	stateObject.regex = backState.regex;
	stateObject.perfectFamMatch = backState.perfectFamMatch;

	stateObject.minnumber_of_members = backState.minnumber_of_members;
	stateObject.maxnumber_of_members = backState.maxnumber_of_members;

	stateObject.minnumber_of_species = backState.minnumber_of_species;
	stateObject.maxnumber_of_species = backState.maxnumber_of_species;

	stateObject.ClusterRowOrder = backState.ClusterRowOrder;
	stateObject.ClusterColumnOrder = backState.ClusterColumnOrder;

	stateObject.heatmapAxis = backState.heatmapAxis;
	stateObject.colorScheme = backState.colorScheme;

	stateObject.heatmapState = backState.heatmapState;

	updateFilterHTML(stateObject);
	drawOrthoGroupTable(this.gridId);
	this.allowHashing = true;
}

function renderDescription(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + record.data.description + '" data-qclass="x-tip"';
	return "<span onclick=\"submitFigfam('" + record.data.window + "', '" + record.data.groupId + "');\"><u>" + record.data.description + "</u></span>";
}

function pickPanel() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0;

	if (this.activeTab == 0) {

		this.heatMapPanel.setVisible(false);
		this.isHeatMapLoaded = false;

		if (this.grid == null) {
			Page.grid = this.grid = Ext.create('Ext.grid.PATRICGrid', {
				id : this.gridId,
				store : this.store,
				columns : [this.checkbox, {
					header : 'ID',
					dataIndex : 'groupId',
					flex : 1,
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Proteins',
					dataIndex : 'members',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Genomes',
					dataIndex : 'species',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Product Description',
					dataIndex : 'description',
					flex : 3,
					sortable : true,
					renderer : renderDescription
				}, {
					header : 'Min AA Length',
					dataIndex : 'min',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Max AA length',
					dataIndex : 'max',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Mean',
					dataIndex : 'mean',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}, {
					header : 'Std',
					dataIndex : 'std',
					flex : 1,
					align : 'center',
					sortable : true,
					renderer : BasicRenderer
				}],
				tbar : createToolbar("cart", "", "Feature"),
				plugins : [this.checkbox],
				dockedItems : [{
					dock : 'bottom',
					xtype : 'patricpagingtoolbar',
					store : this.store,
					id : 'pagingtoolbar',
					displayMsg : property.pagingBarMsg ? property.pagingBarMsg[which] : 'Displaying record {0} - {1} of {2}'
				}],
				stateful : true,
				stateEvents : ['hide', 'show', 'columnmove', 'columnresize', 'sortchange'],
				stateId : property.stateId ? property.stateId[which] : "NA",
				SortChange : function(ct, column, direction, options) {
					var gridObject = getScratchObject(this.id);
					var stateObject = getStateObject(this.id);
					if (gridObject) {
						var stateTracker = gridObject.gridState;
						if ((stateTracker.sortField != column.dataIndex) || (stateTracker.sortDir != direction)) {
							stateTracker.sortField = column.dataIndex;
							stateTracker.sortDir = direction;
							stateObject.ClusterRowOrder = [];
							stateObject.ClusterColumnOrder = [];

							this.store.sorters.clear();
							this.store.sorters.add(new Ext.util.Sorter({
								property : column.dataIndex,
								direction : direction
							}));

							gridObject.Pagingstore.sort([{
								'property' : column.dataIndex,
								'direction' : direction
							}]);
							//stateTracker.storeInSession();

							var startSet = parseInt(stateTracker.pageAt - 1) * parseInt(stateTracker.pageSize);
							var stopSet = parseInt(stateTracker.pageSize);
							var temp = new Array();

							for (var i = startSet; i < startSet + stopSet; i++) {
								if (gridObject.Pagingstore.data.items[i] != null)
									temp[i - startSet] = gridObject.Pagingstore.data.items[i].data;
							}

							Ext.getStore('ds').loadData(temp);
							stateTracker.syntonyId = "";
						}
					}
				}
			});
			this.grid.render(this.gridId + "_4grid");
			this.grid.setVisible(true);
			Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');

		} else {

			this.grid.setVisible(true);
			this.geneCount = this.store.getTotalCount();
			var p = this.grid.getDockedItems('patricpagingtoolbar')[0];

			var gridState = this.gridState;
			var startSet = 0, stopSet = 0;
			var pageData = {};

			if (0 < this.geneCount) {
				p.setPageSize(parseInt(gridState.pageSize));
				p.setInputItemValue(gridState.pageAt);
				startSet = parseInt(gridState.pageAt - 1) * parseInt(gridState.pageSize);
				stopSet = parseInt(gridState.pageSize);

				stopSet += startSet;

				if (this.geneCount < stopSet)
					stopSet = this.geneCount + startSet;

				this.Pagingstore.sort([{
					property : this.Pagingstore.sorters.items[0].property,
					direction : this.Pagingstore.sorters.items[0].direction
				}]);
				var temp = new Array();

				for (var i = startSet; i < stopSet; i++) {
					if (this.Pagingstore.data.items[i] != null)
						temp[i - startSet] = this.Pagingstore.data.items[i].data;
				}

				Ext.getStore('ds').loadData(temp);
				Ext.get("sample-layout").unmask();
				Ext.getDom('grid_result_summary').innerHTML = "<b>" + this.geneCount + " families found</b><br/>";
				p.arrangeButtonBehaviors();
				pageData.fromRecord = startSet + 1;
				pageData.toRecord = stopSet;
				pageData.total = this.geneCount;
				pageData.pageCount = Math.ceil(this.geneCount / gridState.pageSize);
				p.SetandEnableTexts(pageData);
			} else {
				this.store.removeAll();
				Ext.getDom('grid_result_summary').innerHTML = "<b>0 families found</b>";
				Ext.get("sample-layout").unmask();
			}
		}
	} else {
		
		this.heatMapPanel.setVisible(true);
		this.grid.setVisible(false);
		
		this.familyCount = filterHeatmapData(this.stateObject, this.store, this.gridId);

		if (0 < this.familyCount) {
			Ext.getDom('grid_result_summary').innerHTML = "<b>" + this.familyCount + " families found</b><br/>";
			Ext.get("sample-layout").unmask();
			
			if(navigator.mimeTypes ["application/x-shockwave-flash"]){

				if (this.isHeatMapLoaded == false) {
					loadHeatmap();
					this.isHeatMapLoaded = true;
				} else {
					flashShouldRefreshData(jQuery(heatmapid)[0]);
				}
	
				var stateObject = this.stateObject;
				function callHeatmapStateRestore(state) {
					updateDisplayStateInFlash(jQuery(heatmapid)[0], stateObject.heatmapState);
				}
	
				if (stateObject.heatmapState) {
					setTimeout(function() {
						callHeatmapStateRestore();
					}, 3000);
				}
			}else{
				Ext.getDom("FIGfamSorter_4heatmap").innerHTML = "Your Flash player is either disabled or not installed. Please install using this <a target=\"blank\" href=\"http://get.adobe.com/flashplayer\">link</a>.";
			}

		} else {
			this.isHeatMapLoaded = false;
			Ext.getDom('grid_result_summary').innerHTML = "<b>0 families found</b>";
			Ext.get("sample-layout").unmask();
		}
	}
}
