var currentData;

function TranscriptomicsGrid(gridId, stateObject, instance) {

	this.geneCount = 0;
	this.stateObject = stateObject;
	this.gridId = gridId;
	this.grid = null;
	this.allowHashing = false;
	this.activeTab = 0;
	this.isHeatMapLoaded = false;

	TranscriptomicsGene = Ext.define('TranscriptomicsGene', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'exp_locus_tag',
			type : 'string'
		}, {
			name : 'locus_tag',
			type : 'string'
		}, {
			name : 'patric_product',
			type : 'string'
		}, {
			name : 'patric_accession',
			type : 'string'
		}, {
			name : 'start_max',
			type : 'int'
		}, {
			name : 'end_min',
			type : 'int'
		}, {
			name : 'strand',
			type : 'string'
		}, {
			name : 'na_feature_id',
			type : 'string'
		}, {
			name : 'genome_name',
			type : 'string'
		}, {
			name : 'gene',
			type : 'string'
		}, {
			name : 'sample_size',
			type : 'int'
		}, {
			name : 'up',
			type : 'int'
		}, {
			name : 'down',
			type : 'int'
		}, {
			name : 'sample_binary',
			type : 'string'
		}, {
			name : 'windowID',
			type : 'string'
		}]
	});

	this.headerReader = new Ext.data.reader.Array({
		model : 'TranscriptomicsGene'
	}, TranscriptomicsGene);

	this.cm = null;
	this.checkbox = createCheckBox(gridId);

	this.sortField = $Page.getPageProperties().sort[0].property;
	this.sortDir = $Page.getPageProperties().sort[0].direction;
	this.gridState = new TranscriptomicsGridState(this, stateObject, instance);

	this.significantComboStore = Ext.create('Ext.data.Store', {
		fields : ['id', 'value'],
		data : [{
			id : "All Genes",
			value : "All Genes"
		}, {
			id : "Significant Genes",
			value : "Significant Genes"
		}]
	});

	this.significantCombo = Ext.create('Ext.form.ComboBox', {
		queryMode : 'local',
		width : 160,
		triggerAction : 'all',
		stateful : true,
		store : this.significantComboStore,
		valueField : 'id',
		displayField : 'value',
		value : 'Significant Genes',
		typeAhead : false,
		listeners : {
			'select' : function(combo, records, options) {

				SignificantGenesFilter(gridId, this.getValue());

			}
		}
	});

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
			text : "Heatmap Color",
			menu : [{
				text : '<img src="/patric/images/expression_data_up.png" style="vertical-align: text-top;"></img>Red-Black-Green<img src="/patric/images/expression_data_down.png" style="vertical-align: text-top;"></img>',
				handler : function() {
					Ext.get("sample-layout").mask();
					DoColorChange(gridId, 'rgb');
				}
			}, {
				text : '<img src="/patric/images/expression_data_up.png" style="vertical-align: text-top;"></img>Red-White-Blue<img src="/patric/images/expression_data_down.png" style="vertical-align: text-top;"></img>',
				handler : function() {
					Ext.get("sample-layout").mask();
					DoColorChange(gridId, 'rbw');
				}
			}]
		}, '-', {
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
		}, '-', 'Show ', this.significantCombo]
	});

	this.heatMapPanel = Ext.create('Ext.Panel', {
		height : 740,
		border : false,
		layout : 'border',
		hidden : true,
		id : 'heatmap',
		tbar : this.heatMapPaneltoolbar,
		items : [{
			region : 'center',
			html : '<div id=\"flashTarget\"/></div>',
			border : false,
			split : true
		}],
		renderTo : gridId + '_4heatmap'
	});

	Ext.getDom('information').style.height = '40px';
	Ext.getDom("information").style.visibility = 'visible';

	this.showPanel = pickPanel;
	this.applyBackup = TranscriptomicsGridBackup;

	this.store = Ext.create('Ext.data.Store', {
		model : 'TranscriptomicsGene',
		storeId : 'ds',
		pageSize : this.gridState.pageSize
	});

	this.Pagingstore = Ext.create('Ext.data.Store', {
		model : 'TranscriptomicsGene',
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

	this.showPanel();
}

function TranscriptomicsGridBackup() {
	var backState = this.gridState;

	var stateObject = this.stateObject;
	stateObject.sampleFilter = backState.sampleFilter;
	stateObject.regex = backState.regex;
	stateObject.regexGN = backState.regexGN;
	stateObject.upFold = backState.upFold;
	stateObject.downFold = backState.downFold;
	stateObject.upZscore = backState.upZscore;
	stateObject.downZscore = backState.downZscore;
	stateObject.significantGenes = backState.significantGenes;
	stateObject.ClusterRowOrder = backState.ClusterRowOrder;
	stateObject.ClusterColumnOrder = backState.ClusterColumnOrder;
	stateObject.heatmapState = backState.heatmapState;
	stateObject.heatmapAxis = backState.heatmapAxis;
	stateObject.colorScheme = backState.colorScheme;
	stateObject.filterOffset = backState.filterOffset;
	updateFilter(this.gridId);
	updateTranscriptomics(this.gridId);
	this.allowHashing = true;
}

function pickPanel() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0;

	if (this.activeTab == 0) {

		this.heatMapPanel.setVisible(false);
		this.isHeatMapLoaded = false;

		if (this.grid == null) {

			this.cm = [this.checkbox, {
				header : 'Genome Name',
				sortable : true,
				flex : 2,
				dataIndex : 'genome_name',
				renderer : BasicRenderer
			}, {
				header : 'Locus Tag',
				sortable : true,
				flex : 1,
				dataIndex : 'locus_tag',
				renderer : renderGeneLevelPage
			}, {
				header : 'RefSeq Locus Tag',
				sortable : true,
				flex : 1,
				dataIndex : 'exp_locus_tag',
				renderer : BasicRenderer
			}, {
				header : 'Gene Symbol',
				sortable : true,
				flex : 1,
				dataIndex : 'gene',
				renderer : BasicRenderer
			}, {
				header : 'Product Description',
				sortable : true,
				flex : 2,
				dataIndex : 'patric_product',
				renderer : BasicRenderer
			}, {
				header : 'Start',
				sortable : true,
				flex : 1,
				hidden : true,
				align : 'right',
				dataIndex : 'start_max',
				renderer : BasicRenderer
			}, {
				header : 'End',
				sortable : true,
				flex : 1,
				hidden : true,
				align : 'right',
				dataIndex : 'end_min',
				renderer : BasicRenderer
			}, {
				header : 'Strand',
				sortable : true,
				flex : 1,
				hidden : true,
				dataIndex : 'strand',
				align : 'center',
				renderer : BasicRenderer
			}, {
				header : 'Comparisons',
				sortable : true,
				align : 'center',
				flex : 1,
				dataIndex : 'sample_size',
				renderer : renderSamples
			}, {
				header : 'Up',
				sortable : true,
				align : 'center',
				flex : 1,
				dataIndex : 'up',
				renderer : renderUpRegulate
			}, {
				header : 'Down',
				sortable : true,
				align : 'center',
				flex : 1,
				dataIndex : 'down',
				renderer : renderDownRegulate
			}];

			$Page.grid = this.grid = Ext.create('Ext.grid.PATRICGrid', {
				id : this.gridId,
				store : this.store,
				columns : this.cm,
				dockedItems : [{
					dock : 'bottom',
					xtype : 'patricpagingtoolbar',
					store : this.store,
					id : 'pagingtoolbar',
					displayMsg : property.pagingBarMsg ? property.pagingBarMsg[which] : 'Displaying record {0} - {1} of {2}'
				}],
				tbar : createToolbar("cart", "", "Feature"),
				plugins : this.checkbox,
				renderTo : this.gridId + "_4grid",
				stateful : true,
				stateEvents : ['hide', 'show', 'columnmove', 'columnresize', 'sortchange'],
				stateId : property.stateId ? property.stateId[which] : "NA",
				SortChange : function(ct, column, direction, options) {

					var gridObject = getScratchObject(this.id);
					var stateObject = getStateObject(this.id);

					if (gridObject) {
						var stateTracker = gridObject.gridState;
						if (stateTracker.sortField != column.dataIndex || stateTracker.sortDir != direction) {
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
							var startSet = parseInt(stateTracker.pageAt - 1) * parseInt(stateTracker.pageSize);
							var stopSet = parseInt(stateTracker.pageSize);
							var temp = new Array();

							for (var i = startSet; i < startSet + stopSet; i++) {
								if (gridObject.Pagingstore.data.items[i] != null)
									temp[i - startSet] = gridObject.Pagingstore.data.items[i].data;

							}

							Ext.getStore('ds').loadData(temp);

							var transcriptomicsRows = getScratchObject(this.id + "_groupRows");
							transcriptomicsRows.sort(sortRowsData(column.dataIndex, direction));
							cacheObject(this.id + "_groupRows", transcriptomicsRows);

						}
					}
				}
			});

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

				this.Pagingstore.sort();
				var temp = new Array();

				for (var i = startSet; i < stopSet; i++) {
					if (this.Pagingstore.data.items[i] != null)
						temp[i - startSet] = this.Pagingstore.data.items[i].data;
				}

				Ext.getStore('ds').loadData(temp);
				Ext.get("sample-layout").unmask();
				Ext.getDom('grid_result_summary').innerHTML = "<b>" + this.geneCount + " genes found</b><br/>";
				p.arrangeButtonBehaviors();
				pageData.fromRecord = startSet + 1;
				pageData.toRecord = stopSet;
				pageData.total = this.geneCount;
				pageData.pageCount = Math.ceil(this.geneCount / gridState.pageSize);
				p.SetandEnableTexts(pageData);
			} else {
				this.store.removeAll();
				Ext.getDom('grid_result_summary').innerHTML = "<b>No genes found</b><br/>";
				Ext.get("sample-layout").mask('No results found');
			}
		}
	} else {

		this.grid.setVisible(false);
		filterHeatmapData(this.gridId);
		if (0 < cols.length) {
			Ext.getDom('grid_result_summary').innerHTML = "<b>" + cols.length + " genes found</b><br/>";
			Ext.get("sample-layout").unmask();
			this.heatMapPanel.setVisible(true);
			
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
				Ext.getDom("TranscriptomicsGene_4heatmap").innerHTML = "Your Flash player is either disabled or not installed. Please install using this <a target=\"blank\" href=\"http://get.adobe.com/flashplayer\">link</a>.";
			}
			
		} else {

			this.heatMapPanel.setVisible(false);
			this.isHeatMapLoaded = false;
			Ext.getDom('grid_result_summary').innerHTML = "<b>No genes found</b><br/>";
			Ext.get("sample-layout").mask('No results found');

		}
	}
}
