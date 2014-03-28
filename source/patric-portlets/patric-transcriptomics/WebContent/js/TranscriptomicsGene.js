var rows = [], cols = [], giMax;

function TranscriptomicsGeneStateObject(windowID, serveResource, getContextPath, cType, cId, keyword) {
	// save initial values
	this.windowID = windowID, this.serveURL = serveResource, this.contextPath = getContextPath, this.cType = cType, this.cId = cId, this.keyword = keyword, this.duration = defaultDuration, this.expId = '', this.sampleId = '', this.colId = '', this.colsampleId = '', this.sampleFilter = '', this.samplePIds = [], this.filterOffset = 0, this.regex = '', this.regexGN = '', this.significantGenes = 'Y', this.upFold = 0, this.downFold = 0, this.upZscore = 0, this.downZscore = 0, this.ClusterRowOrder = [], this.ClusterColumnOrder = [], this.heatmapState = null, this.heatmapAxis = 'Transpose', this.colorScheme = 'rgb', this.messageWindow = null, this.clusterWindow = null;
}

function TranscriptomicsGeneOnReady(windowID, resourceURL, contextPath, cType, cId, sampleId, expId, colId, log_ratio, zscore, keyword) {
	addWindowID(windowID);
	var stateObject = new TranscriptomicsGeneStateObject(windowID, resourceURL, contextPath, cType, cId, keyword);
	loadStateObject(windowID, stateObject);
	idForHeatmap = windowID;
	scanURL(stateObject, sampleId, expId, colId, log_ratio, zscore);
	createMainPanel(windowID, stateObject);
	createLeftBottomItems(windowID);
	createSampleGrid(windowID);
	$Page.doLayout();
}

function createMainPanel(windowID, stateObject) {"use strict";

	var leftHtml = "<div style=\"font-weight:bold;\"id=\"exp_sample_count_div\"></div><div id='" + windowID + "_4sample_grid'></div>" + "<div style=\"padding: 10px;\"><div style=\"float: left;\" id='gnchange'></div></br>" + "<div style='padding-top: 25px;'><label for=\"tb-inputEl\">Filter by one or more keywords or locus tags</label> <br />" + "<FORM NAME='" + windowID + "_regexForm'" + "ONSUBMIT=\"haveFilterClick('" + windowID + "'); return false;\">" + "<div style=\"width: 98%; padding-top:10px;\" id=\"textbox_div\"></div>" + "<span class=\"hint\" style=\"color:black\">e.g. VBIEscCol129921_0001, Transcription factor</span>" + "</FORM></div>" + "<div style=\"padding-top: 15px;\"><div style=\"float: left;padding-top: 4px;\"></div><div style=\"float: left;\" id='foldchange'></div></div>" + "<div style=\"padding-top: 30px;\"><div style=\"float: left;padding-top: 4px;\"></div><div style=\"float: left;\" id='zscore'></div></div>" + "<br/><div style=\"padding-top:15px; float:right;\"><input type='button' class='button' style='padding: 2px 8px;' value='Filter' onclick=\"haveFilterClick('" + windowID + "');\"/></div>";

	Ext.getDom("information_panel").innerHTML = "<p> The gene list below provides details about gene regulation across a given set of experiments and comparisons. The gene list can be filtered based on regulation within each comparison as well as by locus tag and keyword. To learn more, see our " + "<a href='http://enews.patricbrc.org/faqs/'>Transcriptomics Gene List FAQs.</a></p>";

	Ext.create('Ext.panel.Panel', {
		id : 'tabLayout',
		border : true,
		autoScroll : false,
		width : $(window).width() - 307,
		items : [{
			region : 'north',
			border : false,
			height : 22,
			xtype : 'tabpanel',
			id : 'tabPanel',
			items : [{
				title : "Table",
				id : "table-tab"
			}, {
				title : "Heatmap",
				id : "map-tab"
			}],
			listeners : {
				'tabchange' : function(tabPanel, tab) {
					var gridObject = getScratchObject(windowID);
					if (gridObject != null) {
						if (tab.getId() == "map-tab") {
							gridObject.activeTab = 1;
						} else {
							stateObject.ClusterRowOrder = [];
							stateObject.ClusterColumnOrder = [];
							gridObject.activeTab = 0;
							stateObject.heatmapState = displayStateOfFlash(jQuery(heatmapid)[0]);
							gridObject.gridState.storeInSession();
						}
						gridObject.showPanel(false);
					}
				}
			}
		}, {
			region : 'center',
			id : 'centerPanel',
			contentEl : 'information',
			height : 26,
			border : false,
			split : false
		}, {
			region : 'south',
			id : 'southPanel',
			html : "<div id='" + windowID + "_4grid'></div><div id='" + windowID + "_4heatmap'></div>",
			height : 740,
			border : false,
			autoScroll : true
		}],
		renderTo : 'sample-layout'
	});

	Ext.create('Ext.panel.Panel', {
		title : 'Filter By',
		renderTo : 'tree-panel',
		width : 300,
		height : 790,
		border : true,
		resizable : true,
		autoScroll : false,
		id : 'treePanel',
		collapsible : true,
		collapseDirection : 'left',
		items : [{
			id : 'westPanel',
			region : 'west',
			html : leftHtml,
			width : 300,
			height : 790,
			split : true,
			boder : false
		}],
		listeners : {
			resize : function(cmp, width, height, oldWidth, oldHeight, eOpts) {
				var Page = $Page;
				if (Page.getGrid()) {
					Ext.getCmp("westPanel").setWidth(width);
					Page.doTabLayout();
					Ext.getCmp(windowID + '_sample_grid').setWidth(width);
				}
			}
		}
	});
}

function scanURL(stateObject, sampleId, expId, colId, log_ratio, zscore) {"use strict";

	var colIds = colId.split(","), colsampleIds, subIds, i, j, collectionId = [], collectionSampleId = [], l = log_ratio, z = zscore;

	for ( i = 0; i < colIds.length; i++) {
		subIds = colIds[i].split(":");
		collectionId.push(subIds[0]);
		if (subIds[1]) {
			colsampleIds = subIds[1].split(" ");
			for ( j = 0; j < colsampleIds.length; j++) {
				collectionSampleId.push(subIds[0] + colsampleIds[j]);
			}
		}
	}

	stateObject.colId = collectionId.join(",");
	stateObject.colsampleId = collectionSampleId.join(",");
	stateObject.upZscore = z || 0;
	stateObject.downZscore = 0 - stateObject.upZscore;
	stateObject.upFold = l || 0;
	stateObject.downFold = 0 - stateObject.upFold;
	stateObject.sampleId = sampleId;
	stateObject.expId = expId;

}

function SetWindowLayout(windowID) {"use strict";
	var sample_grid = Ext.getCmp('TranscriptomicsGene_sample_grid'), panel = Ext.getCmp('TabTable_panel'), north = Ext.getCmp('TabTable_north'), center = Ext.getCmp('TabTable_center'), south = Ext.getCmp('TabTable_south'), cpwidth = Ext.getCmp('centerPanel').getWidth(), wpwidth = Ext.getCmp('westPanel').getWidth();

	if (sample_grid) {
		sample_grid.setWidth(wpwidth);
		panel.setWidth(cpwidth);
		north.setWidth(cpwidth);
		center.setWidth(cpwidth);
		south.setWidth(cpwidth);
		Ext.getCmp(windowID).setWidth(cpwidth);
		Ext.getCmp(windowID + '_heatmap').setWidth(cpwidth);
	}
}

function createLeftBottomItems(windowID) {"use strict";

	var stateObject = getStateObject(windowID);

	Ext.define('Fold', {
		extend : 'Ext.data.Model',
		fields : [{
			type : 'string',
			name : 'name'
		}]
	});

	Ext.create('Ext.form.ComboBox', {
		id : 'cb_gnchange',
		renderTo : 'gnchange',
		fieldLabel : 'Filter by Genome ',
		displayField : 'name',
		width : 285,
		labelWidth : 60,
		editable : true,
		store : Ext.create('Ext.data.Store', {
			model : 'Fold'
		}),
		queryMode : 'local',
		listeners : {
			'select' : function() {
				stateObject.regexGN = this.getValue();
			},
			'change' : function() {
				stateObject.regexGN = this.getValue();
			}
		}
	});

	Ext.create('Ext.form.ComboBox', {
		id : 'cb_foldchange',
		renderTo : 'foldchange',
		fieldLabel : 'Filter by |Log Ratio|',
		displayField : 'name',
		width : 195,
		labelWidth : 120,
		editable : true,
		store : Ext.create('Ext.data.Store', {
			model : 'Fold',
			data : [{
				"name" : "0"
			}, {
				"name" : "0.5"
			}, {
				"name" : "1"
			}, {
				"name" : "1.5"
			}, {
				"name" : "2"
			}, {
				"name" : "2.5"
			}, {
				"name" : "3"
			}]
		}),
		queryMode : 'local',
		listeners : {
			'select' : function() {
				var value = parseFloat(this.getValue());
				stateObject.downFold = 0 - value;
				stateObject.upFold = value;
			},
			'change' : function() {
				var upFold, downFold;
				var value = parseFloat(this.getValue());

				if (!IsItFloatFormat(value)) {
					upFold = 0;
					downFold = 0;
				} else if (value < 0) {
					upFold = -1 * value;
					downFold = value;
				} else {
					upFold = value;
					downFold = 0 - value;
				}

				stateObject.upFold = upFold;
				stateObject.downFold = downFold;
			}
		}
	});

	Ext.create('Ext.form.ComboBox', {
		id : 'cb_zscore',
		renderTo : 'zscore',
		fieldLabel : 'Filter by |Z-score|',
		displayField : 'name',
		width : 195,
		labelWidth : 120,
		editable : true,
		store : Ext.create('Ext.data.Store', {
			model : 'Fold',
			data : [{
				"name" : "0"
			}, {
				"name" : "0.5"
			}, {
				"name" : "1"
			}, {
				"name" : "1.5"
			}, {
				"name" : "2"
			}, {
				"name" : "2.5"
			}, {
				"name" : "3"
			}]
		}),
		queryMode : 'local',
		listeners : {
			'select' : function() {
				var value = parseFloat(this.getValue());
				stateObject.downZscore = 0 - value;
				stateObject.upZscore = value;
			},
			'change' : function() {
				var upZscore, downZscore;
				var value = parseFloat(this.getValue());

				if (!IsItFloatFormat(value)) {
					upZscore = 0;
					downZscore = 0;
				} else if (value < 0) {
					upZscore = -1 * value;
					downZscore = value;
				} else {
					upZscore = value;
					downZscore = 0 - value;
				}

				stateObject.upZscore = upZscore;
				stateObject.downZscore = downZscore;
			}
		}
	});

	Ext.create('Ext.form.field.TextArea', {
		id : 'tb',
		renderTo : 'textbox_div',
		width : 285,
		height : 75
	});

	Ext.getCmp("cb_foldchange").setValue(!IsItFloatFormat(stateObject.upFold) ? '' : stateObject.upFold);
	Ext.getCmp("cb_zscore").setValue(!IsItFloatFormat(stateObject.upZscore) ? '' : stateObject.upZscore);
}

function createSampleGrid(windowID) {"use strict";
	var gridObject, stateObject = getStateObject(windowID);

	Ext.define('Sample_Store', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'pid',
			type : 'string'
		}, {
			name : 'expname',
			type : 'string'
		}, {
			name : 'expmean',
			type : 'string'
		}, {
			name : 'timepoint',
			type : 'string'
		}, {
			name : 'mutant',
			type : 'string'
		}, {
			name : 'strain',
			type : 'string'
		}, {
			name : 'condition',
			type : 'string'
		}]
	});

	Ext.create('Ext.data.Store', {
		storeId : 'ds_sample',
		model : 'Sample_Store'
	});

	Ext.create('Ext.grid.Panel', {
		store : 'ds_sample',
		id : windowID + '_sample_grid',
		renderTo : windowID + "_4sample_grid",
		columns : [{
			header : '<img src="/patric/images/expression_data_up.png"></img>',
			hideable : false,
			menuDisabled : true,
			resizable : false,
			align : 'center',
			tooltip : 'Upregulate',
			width : 30,
			renderer : UpRRenderer
		}, {
			header : '<img src="/patric/images/expression_data_down.png"></img>',
			hideable : false,
			menuDisabled : true,
			resizable : false,
			align : 'center',
			tooltip : 'Downregulate',
			width : 40,
			renderer : DownRRenderer
		}, {
			header : '<img src="/patric/images/expression_data_up_down.png"></img>',
			hideable : false,
			menuDisabled : true,
			resizable : false,
			align : 'center',
			tooltip : "Don't care",
			width : 30,
			renderer : NcRenderer
		}, {
			header : 'Comparison ID',
			hidden : true,
			width : 70,
			sortable : true,
			dataIndex : 'samples',
			renderer : BasicTooltipRenderer
		}, {
			header : 'Source',
			width : 50,
			align : 'center',
			sortable : true,
			dataIndex : 'samples',
			renderer : OwnerRenderer
		}, {
			header : 'Title',
			width : 100,
			sortable : true,
			dataIndex : 'expname',
			renderer : BasicTooltipRenderer
		}, {
			header : 'Strain',
			width : 100,
			sortable : true,
			dataIndex : 'strain',
			renderer : BasicTooltipRenderer
		}, {
			header : 'Modification',
			width : 100,
			sortable : true,
			dataIndex : 'mutant',
			renderer : BasicTooltipRenderer
		}, {
			header : 'Condition',
			width : 100,
			sortable : true,
			dataIndex : 'condition',
			renderer : BasicTooltipRenderer
		}, {
			header : 'Time Point',
			width : 100,
			sortable : true,
			dataIndex : 'timepoint',
			renderer : BasicTooltipRenderer
		}],
		viewConfig : {
			forceFit : true
		},
		height : 470,
		border : false,
		listeners : {
			'sortchange' : function() {
				updateFilter(windowID);
				stateObject.ClusterRowOrder = [];
				stateObject.ClusterColumnOrder = [];
				gridObject = getScratchObject(windowID);
				if (gridObject.activeTab == 1)
					gridObject.showPanel();
			}
		}
	});
	
	// $("#"+windowID + "_4sample_grid").on('click', haveSampleCareClick);

	loadTables(windowID);
}

function loadTables(windowID) {"use strict";

	var stateObject = getStateObject(windowID), tableObject = new TranscriptomicsGrid(windowID, stateObject, 0), decoded, samplecount, infotext, totalcount, ids, i, sample_grid = Ext.getCmp(windowID + '_sample_grid');

	cacheObject(windowID, tableObject);

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getTables",
			sampleId : stateObject.sampleId,
			expId : stateObject.expId,
			colId : stateObject.colId,
			colsampleId : stateObject.colsampleId,
			windowID : windowID,
			keyword: stateObject.keyword?stateObject.keyword:""
		},
		success : function(rs) {
			decoded = Ext.JSON.decode(rs.responseText);
			samplecount = decoded.sampleTotal;
			infotext = samplecount + " comparison(s)";
			if (stateObject.expId || stateObject.colId) {
				totalcount = (stateObject.expId) ? stateObject.expId.split(",").length : 0;
				totalcount += (stateObject.colId) ? stateObject.colId.split(",").length : 0;

				infotext += " in " + totalcount + " experiment(s) ";
			}

			Ext.getDom("exp_sample_count_div").innerHTML = infotext;
			Ext.getStore('ds_sample').loadData(decoded.sample);
			ids = Ext.getStore('ds_sample').data.items[0].id.split("-");
			stateObject.filterOffset = ids[ids.length - 1];
			
			if(decoded.sample.length > 500){
				Ext.getDom(windowID + "_4sample_grid").innerHTML = "<br/><br/><div>Our current resources cannot handle more than 500 comparisons.<br/><br/><br/></div>";
			}

			if (samplecount <= 5)
				sample_grid.setHeight(170);
			else if (samplecount > 5 && samplecount <= 10)
				sample_grid.setHeight(270);
			else if (samplecount > 10 && samplecount <= 15)
				sample_grid.setHeight(370);

			for ( i = 0; i < samplecount; i++) {
				stateObject.sampleFilter += " ";
				stateObject.samplePIds[i] = decoded.sample[i].pid;
			}

			catchExperimentStats(windowID, decoded.expression);
		}
	});
}

function updateFilter(windowID) {"use strict";

	var stateObject = getStateObject(windowID), checkSetter = stateObject.sampleFilter, offset = stateObject.filterOffset, getById = document.getElementById.bind(document), i, ids = [], id, items = Ext.getStore('ds_sample').data.items;

	if (checkSetter == "") {
		for ( i = 0; i < items.length; i++) {
			checkSetter += " ";
		}
		stateObject.sampleFilter = checkSetter;
	}

	for ( i = 0; i < items.length; i++) {

		ids = items[i].id.split("-");
		id = ids[ids.length - 1];

		if (offset == "0") {
			offset = id;
			stateObject.filterOffset = offset;
		}

		if (checkSetter[id - offset] == '1') {
			getById(windowID + '_sc1' + items[i].id).checked = true;
			getById(windowID + '_sc0' + items[i].id).checked = false;
			getById(windowID + '_sc_' + items[i].id).checked = false;
		} else if (checkSetter[id - offset] == '0') {
			getById(windowID + '_sc1' + items[i].id).checked = false;
			getById(windowID + '_sc0' + items[i].id).checked = true;
			getById(windowID + '_sc_' + items[i].id).checked = false;
		} else {
			getById(windowID + '_sc1' + items[i].id).checked = false;
			getById(windowID + '_sc0' + items[i].id).checked = false;
			getById(windowID + '_sc_' + items[i].id).checked = true;
		}
	}

	Ext.getCmp('tb').setValue(stateObject.regex);
	Ext.getCmp("cb_foldchange").setValue(!IsItFloatFormat(stateObject.upFold) ? '' : stateObject.upFold);
	Ext.getCmp("cb_zscore").setValue(!IsItFloatFormat(stateObject.upZscore) ? '' : stateObject.upZscore);
	Ext.getCmp("cb_gnchange").setValue(stateObject.regexGN);
}

function SignificantGenesFilter(windowID, value) {"use strict";

	var stateObject = getStateObject(windowID);
	if (value == "All Genes")
		stateObject.significantGenes = 'N';
	else
		stateObject.significantGenes = 'Y';

	stateObject.ClusterRowOrder = [];
	stateObject.ClusterColumnOrder = [];
	updateFilterState(windowID, stateObject);
	updateTranscriptomics(windowID);
}

function catchExperimentStats(windowID, rowData) {

	var gridObject = getScratchObject(windowID);

	createGenomeFilter(rowData);

	rowData.sort(sortRowsData(gridObject.gridState.sortField, gridObject.gridState.sortDir));

	cacheObject(windowID + "_groupRows", rowData);
	updateTranscriptomics(windowID);
	defineButtons(windowID);
	cacheInitialTranscriptomics(windowID);
	beginHashChecking();

}

function createGenomeFilter(rowData) {"use strict";

	var hash = {}, arr = [], store = Ext.getCmp('cb_gnchange').getStore(), text = "", genome_name = "", rdgn = "";

	for (var obj in rowData) {
		rdgn = rowData[obj].genome_name;
		if (rdgn) {
			genome_name = rdgn;
			if (hash[genome_name] != null)
				hash[genome_name]++;
			else
				hash[genome_name] = 1;
		}
	}
	for (var obj in hash) {
		text = obj + " (" + hash[obj] + ")";
		arr.push({
			name : text
		});
	}
	store.loadData(arr);
}

function sortRowsData(property, direction) {
	return function(a, b) {
		if (a[property] < b[property])
			return (direction == "ASC") ? -1 : 1;
		if (a[property] > b[property])
			return (direction == "ASC") ? 1 : -1;
		return 0;
	};
}

function haveSampleCareClick(windowID, row, column){
	"use strict";
	
	var stateObject = getStateObject(windowID),
		sampleFilter = stateObject.sampleFilter,
		items =  Ext.getStore('ds_sample').data.items,
		offset = stateObject.filterOffset,
		getById = document.getElementById.bind(document),
		ids = [],
		newFilter = [],
		id,
		i,
		index;

	for (i = 0; i < items.length; i++) {
		
		ids = items[i].id.split("-"),
		id = ids[ids.length-1],
		index = id - offset;
		
		if(id - offset < 0)
			index = offset - id;
		
		if(id == row){
			if(column == "1"){
				getById(windowID + '_sc1'+ items[i].id).checked = true;
				getById(windowID + '_sc0'+ items[i].id).checked = false;
				getById(windowID + '_sc_'+ items[i].id).checked = false;
				newFilter[index] = "1";
			}else if(column == "0"){
				getById(windowID + '_sc1'+ items[i].id).checked = false;
				getById(windowID + '_sc0'+ items[i].id).checked = true;
				getById(windowID + '_sc_'+ items[i].id).checked = false;
				newFilter[index] = "0";
			}else{
				getById(windowID + '_sc1'+ items[i].id).checked = false;
				getById(windowID + '_sc0'+ items[i].id).checked = false;
				getById(windowID + '_sc_'+ items[i].id).checked = true;
				newFilter[index] = " ";
			}			
		}else{
			newFilter[index] = sampleFilter[index];
		}
	}
	stateObject.regex = Ext.getCmp('tb').getValue();
	stateObject.ClusterColumnOrder = [];
	stateObject.ClusterRowOrder = [];
	stateObject.filterOffset = offset;
	stateObject.sampleFilter = newFilter.join("");
	updateFilterState(windowID, stateObject);
	updateTranscriptomics(windowID);
}

function haveFilterClick(windowID) {"use strict";

	var stateObject = getStateObject(windowID);
	stateObject.regex = Ext.getCmp('tb').getValue();
	stateObject.ClusterRowOrder = [];
	stateObject.ClusterColumnOrder = [];
	stateObject.heatmapState = null;
	updateFilterState(windowID, stateObject);
	updateTranscriptomics(windowID);
}

function filterTranscriptomicsTableData(windowID) {"use strict";

	var tableData = null, transcriptomicsRows = getScratchObject(windowID + "_groupRows"), stateObject = getStateObject(windowID), gridObject = getScratchObject(windowID), sampleFilter, kept, nextRow, sample_flag, up_r, down_r, total_samples, i, k;

	var start = new Date().getTime();
		
	if (transcriptomicsRows != null) {
		tableData = new Array();
		sampleFilter = stateObject.sampleFilter;
		kept = 0;
		for ( i = 0; i < transcriptomicsRows.length; i++) {
			nextRow = transcriptomicsRows[i];
			if (TranscriptomicsAdvancedFilter(nextRow, windowID)) {
				sample_flag = false;
				up_r = 0;
				down_r = 0;
				total_samples = 0;
				for ( k = 0; k < sampleFilter.length; k++) {
					if (nextRow.sample_binary[k] == '1') {
						if (sampleFilter[k] != ' ') {
							if (TranscriptomicsThresholdFilter(nextRow, k, sampleFilter[k], windowID))
								sample_flag = true;
							else {
								sample_flag = false;
								break;
							}
						} else {
							if (sample_flag != true) {
								if (TranscriptomicsThresholdFilter(nextRow, k, sampleFilter[k], windowID))
									sample_flag = true;
								else
									sample_flag = false;
							}
						}
					} else {
						if (sampleFilter[k] != ' ') {
							sample_flag = false;
							break;
						}
					}

					if (TranscriptomicsSampleUp(nextRow, k, windowID))
						up_r++;
					if (TranscriptomicsSampleDown(nextRow, k, windowID))
						down_r++;
					if (TranscriptomicsSamplePresent(nextRow, k, windowID))
						total_samples++;
				}

				if (sample_flag) {
					tableData[kept] = [nextRow.refseq_locus_tag, nextRow.locus_tag, nextRow.patric_product, nextRow.patric_accession, nextRow.start_max, nextRow.end_min, nextRow.strand, nextRow.na_feature_id, nextRow.genome_name, nextRow.gene, total_samples, up_r, down_r, nextRow.sample_binary, windowID];
					kept++;
				}
			}
		}

		gridObject.Pagingstore.loadData(tableData);
		gridObject.store.totalCount = tableData.length;
	}
	var end = new Date().getTime();
	
	console.log("Total client side processing is "+(end-start));
	return tableData;
}

function filterHeatmapData(windowID) {"use strict";

	rows = [], cols = [];
	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, sampleFilter = stateObject.sampleFilter, gridObject = getScratchObject(windowID), transcriptomicsRows = getScratchObject(windowID + "_groupRows"), Pagingstore_items = gridObject.Pagingstore.data.items, ids_order = [], items = Ext.getStore('ds_sample').data.items, clusterColumn = (stateObject.heatmapAxis == "Transpose") ? stateObject.ClusterColumnOrder : stateObject.ClusterRowOrder, clusterRow = (stateObject.heatmapAxis == "Transpose") ? stateObject.ClusterRowOrder : stateObject.ClusterColumnOrder, i, j, k, idss, nextRow, iMax = 0;

	if (clusterRow != null && clusterRow.length > 0 && clusterRow[0] != "") {
		for ( j = 0; j < clusterRow.length; j++) {
			for ( i = 0; i < items.length; i++) {
				if (items[i].data.pid == clusterRow[j]) {
					idss = items[i].id.split("-");
					ids_order.push(idss[idss.length - 1] - stateObject.filterOffset);
					break;
				}
			}
		}
	} else {
		for ( i = 0; i < items.length; i++) {
			idss = items[i].id.split("-");
			ids_order.push(idss[idss.length - 1] - stateObject.filterOffset);
		}
	}

	if (clusterColumn != null && clusterColumn.length > 0 && clusterColumn[0] != "") {
		for ( j = 0; j < clusterColumn.length; j++) {
			for ( i = 0; i < transcriptomicsRows.length; i++) {
				nextRow = transcriptomicsRows[i];
				if (nextRow.na_feature_id == clusterColumn[j]) {
					iMax = createColumn(nextRow, windowID, sampleFilter, samplePIds, ids_order, iMax);
					break;
				}
			}
		}
	} else {
		if (stateObject.significantGenes == 'N') {
			for ( i = 0; i < transcriptomicsRows.length; i++) {
				iMax = createColumn(transcriptomicsRows[i], windowID, sampleFilter, samplePIds, ids_order, iMax);
			}
		} else {
			for ( k = 0; k < Pagingstore_items.length; k++) {
				for ( i = 0; i < transcriptomicsRows.length; i++) {
					if (transcriptomicsRows[i].locus_tag == Pagingstore_items[k].data.locus_tag) {
						iMax = createColumn(transcriptomicsRows[i], windowID, sampleFilter, samplePIds, ids_order, iMax);
						break;
					}
				}
			}
		}
	}

	if (stateObject.significantGenes == 'Y')
		gridObject.significantCombo.setValue("Significant Genes");
	else
		gridObject.significantCombo.setValue("All Genes");

	if (clusterRow != null && clusterRow.length > 0 && clusterRow[0] != "") {
		for ( j = 0; j < clusterRow.length; j++)
			for ( i = 0; i < items.length; i++)
				if (items[i].data.pid == clusterRow[j]) {
					pushRow(items[i]);
					break;
				}
	} else {
		for ( i = 0; i < items.length; i++)
			pushRow(items[i]);
	}

	giMax = iMax;

	currentData = {
		'rows' : rows,
		'columns' : cols,
		'colorStops' : getColorStop(windowID, iMax),
		'rowLabel' : 'Comparison',
		'colLabel' : 'Gene',
		'rowTrunc' : 'end',
		'colTrunc' : 'end',
		'offset' : 1,
		'digits' : 2,
		'countLabel' : 'Log ratio',
		'negativeBit' : false,
		'cellLabelField' : 'labels',
		'cellLabelsOverrideCount' : true,
		'beforeCellLabel' : '',
		'afterCellLabel' : ''
	};

	if (stateObject.heatmapAxis == "Transpose") {
		flipAxises(windowID);
	}
}

function pushRow(item) {

	var meta = {
		'samples' : item.data.samples,
		'timepoint' : item.data.timepoint,
		'mutant' : item.data.mutant,
		'strain' : item.data.strain,
		'condition' : item.data.condition
	};

	var labelColor = ((rows.length % 2) == 0) ? 0x000066 : null;
	var rowColor = ((rows.length % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;

	rows.push(new Row(rows.length, item.data.pid, item.data.expname, labelColor, rowColor, meta));

}

function createColumn(nextRow, windowID, sampleFilter, samplePIds, ids, iMax) {"use strict";

	var meta = {
		'labels' : ""
	}, keeps = "", push_flag = false, all_genes_flag = false, stateObject = getStateObject(windowID), i, labelColor, columnColor, obj, l;

	for ( i = 0; i < ids.length; i++) {
		all_genes_flag = false;
		if (stateObject.significantGenes == "N") {
			if (nextRow.sample_binary[ids[i]] == '1') {
				if (TranscriptomicsThresholdFilter(nextRow, ids[i], sampleFilter[ids[i]], windowID)) {
					push_flag = true;
					all_genes_flag = false;
				} else {
					if (stateObject.significantGenes == "N") {
						push_flag = true;
						all_genes_flag = true;
					}
				}
			} else {
				if (sampleFilter[ids[i]] != ' ') {
					push_flag = false;
					break;
				} else {
					if (stateObject.significantGenes == "N")
						all_genes_flag = true;
				}
			}
		} else {
			all_genes_flag = false;
			push_flag = true;
		}
		obj = nextRow.samples[samplePIds[ids[i]]];
		if (obj) {
			l = IsItFloatFormat(obj["log_ratio"]) ? parseFloat(obj["log_ratio"]) : 0;
			if (all_genes_flag) {
				keeps += "0B";
			} else {
				if (!TranscriptomicsThresholdFilter(nextRow, ids[i], sampleFilter[ids[i]], windowID)) {
					keeps += "0B";
				} else {
					if (l < 0 && l >= -1) {
						keeps += "01";
					} else if (l < -1 && l >= -2) {
						keeps += "02";
					} else if (l < -2 && l >= -3) {
						keeps += "03";
					} else if (l < -3 && l >= -4) {
						keeps += "04";
					} else if (l < -4) {
						keeps += "05";
					} else if (l > 0 && l <= 1) {
						keeps += "06";
					} else if (l > 1 && l <= 2) {
						keeps += "07";
					} else if (l > 2 && l <= 3) {
						keeps += "08";
					} else if (l > 3 && l <= 4) {
						keeps += "09";
					} else if (l > 4) {
						keeps += "0A";
					} else {
						keeps += "0B";
					}
				}
			}
			if (iMax < parseInt(keeps.substr(keeps.length - 2, 2), 16))
				iMax = parseInt(keeps.substr(keeps.length - 2, 2), 16);
			meta.labels += l + "|";
		} else {
			keeps += "0B";
			meta.labels += "0|";
		}
	}

	if (push_flag) {
		labelColor = 0x000066;
		columnColor = ((cols.length % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;
		meta.labels = meta.labels.substring(0, meta.labels.length - 1);
		cols.push(new Column(cols.length, nextRow.na_feature_id, nextRow.locus_tag + " - " + nextRow.patric_product, keeps, labelColor, columnColor, meta));
	}

	return iMax;
}

function getColorStop(windowID, iMax) {"use strict";

	var stateObject = getStateObject(windowID), colorup = '255,0,0', colordown = '0,255,0', colorzero = '0x000000', colorperct = [], colorScheme = stateObject.colorScheme, colorsignificantup = "0xFF0000", colorsignificantdown = "0x00FF00", cStop = [];

	if (stateObject.colorScheme == 'rgb') {
		colorzero = '0x000000';
		colordown = '0,255,0', colorup = '255,0,0', colorzero = '0x000000';
		colorperct.push('20', '40', '60', '80');
		colorsignificantup = "0xFF0000";
		colorsignificantdown = "0x00FF00";
	} else if (stateObject.colorScheme == 'rbw') {
		colorzero = '0xFFFFFF';
		colordown = '255,255,255', colorup = '255,255,255', colorzero = '0xFFFFFF';
		colorperct.push('80', '60', '40', '20');
		colorsignificantup = "0xFF0000";
		colorsignificantdown = "0x0000FF";
	}

	//	cStop.push(new ColorStop(0, colorzero));

	for (var i = 1; i <= iMax; i++) {
		if (i < 5)
			cStop.push(new ColorStop(i / iMax, getColor(colorperct[i % 5 - 1], colordown, colorScheme, 'down')));
		if (i > 5 && i < 10)
			cStop.push(new ColorStop(i / iMax, getColor(colorperct[i % 5 - 1], colorup, colorScheme, 'up')));
		if (i == 5)
			cStop.push(new ColorStop(i / iMax, colorsignificantdown));
		if (i == 10)
			cStop.push(new ColorStop(i / iMax, colorsignificantup));
		if (i == 11)
			cStop.push(new ColorStop(i / iMax, colorzero));
	}
	return cStop;
}

function getColor(light, color, colorScheme, value) {"use strict";

	var color_arr = color.split(","), rgb = {
		r : color_arr[0],
		g : color_arr[1],
		b : color_arr[2]
	}, hsv = {};

	rgbTohsv(rgb, hsv);
	hsv.v = parseInt(light);
	hsvTorgb(hsv, rgb);

	if (colorScheme == 'rbw')
		if (value == "up")
			return rgbTohex(255, rgb.g, rgb.b);
		else
			return rgbTohex(rgb.r, rgb.g, 255);
	else
		return rgbTohex(rgb.r, rgb.g, rgb.b);

}

function updateTranscriptomics(windowID) {"use strict";

	var gridObject = getScratchObject(windowID);
	filterTranscriptomicsTableData(windowID);
	gridObject.showPanel();
}

function IsItFloatFormat(value) {
	return !isNaN(parseFloat(value));
}

function TranscriptomicsThresholdFilter(nextRow, index, type, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]], z = (obj && IsItFloatFormat(obj["z_score"])) ? parseFloat(obj["z_score"]) : 0, l = (obj && IsItFloatFormat(obj["log_ratio"])) ? parseFloat(obj["log_ratio"]) : 0, uf = stateObject["upFold"], df = stateObject["downFold"], uz = stateObject["upZscore"], dz = stateObject["downZscore"];

	if (type == " ") {
		if (obj) {
			if (dz == uz && df == uf) {
				return true;
			} else if ((z >= uz || z <= dz) && (l >= uf || l <= df)) {
				return true;
			} else {
				return false;
			}
		}
	} else if (type == "1") {
		if (obj && (uz != 0?z >= uz:true) && l >= uf) {
			return true;
		} else {
			return false;
		}

	} else if (type == "0") {
		if (obj && (dz != 0?z <= dz:true) && l <= df) {
			return true;
		} else {
			return false;
		}
	}
}

function TranscriptomicsSampleUp(nextRow, index, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]], uf, l;

	if (obj) {
		uf = parseFloat(stateObject["upFold"]);
		l = (obj && IsItFloatFormat(obj["log_ratio"])) ? parseFloat(obj["log_ratio"]) : 0;
		if (l > uf)
			return true; // TranscriptomicsSampleZScoreUp(nextRow, index, windowID) && true;
		else
			return false;
	} else
		return false;
}

function TranscriptomicsSampleDown(nextRow, index, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]], df, l;

	if (obj) {
		df = parseFloat(stateObject["downFold"]);
		l = IsItFloatFormat(obj["log_ratio"]) ? parseFloat(obj["log_ratio"]) : 0;

		if (l < df)
			return true;
		else
			return false;
	} else
		return false;

}

function TranscriptomicsSampleZScoreUp(nextRow, index, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]], uz, z;

	if (obj) {
		uz = parseFloat(stateObject["upZscore"]);
		z = IsItFloatFormat(obj["z_score"]) ? parseFloat(obj["z_score"]) : 0;

		if (z > uz)
			return true;
		else
			return false;
	} else
		return false;

}

function TranscriptomicsSampleZScoreDown(nextRow, index, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]], dz, z;

	if (obj) {
		dz = parseFloat(stateObject["downZscore"]);
		z = IsItFloatFormat(obj["z_score"]) ? parseFloat(obj["z_score"]) : 0;

		if (z < dz)
			return true;
		else
			return false;
	} else
		return false;

}

function TranscriptomicsSamplePresent(nextRow, index, windowID) {"use strict";

	var stateObject = getStateObject(windowID), samplePIds = stateObject.samplePIds, obj = nextRow.samples[samplePIds[index]];

	if (obj)
		if (IsItFloatFormat(obj["log_ratio"]))
			return true;

	return false;

}

function TranscriptomicsAdvancedFilter(nextRow, windowID) {"use strict";

	var stateObject = getStateObject(windowID),
	nlocus_tag = nextRow.locus_tag?nextRow.locus_tag.toLowerCase():"",
	nelocus_tag = nextRow.refseq_locus_tag?nextRow.refseq_locus_tag.toLowerCase():"",
	patric_product = nextRow.patric_product?nextRow.patric_product.toLowerCase():"",
	gene_sym = nextRow.gene?nextRow.gene.toLowerCase():"",
	genome_name = nextRow.genome_name?nextRow.genome_name.toLowerCase():"",
	regex = stateObject.regex && stateObject.regex.toLowerCase().replace(/,/g, "~").replace(/\n/g, "~").replace(/ /g, "~").split("~"),
	regexGN = stateObject.regexGN && stateObject.regexGN.split(" (")[0].toLowerCase(),
	regexState = false,
	i, rgxi;

	if (!regex && !regexGN) {
		return true;
	} else if (regexGN || regex.length >= 0) {
		for ( i = 0; i < regex.length; i++) {
			rgxi = regex[i];
			if (rgxi && (nlocus_tag.indexOf(rgxi) >= 0 || nelocus_tag.indexOf(rgxi) >= 0 || patric_product.indexOf(rgxi) >= 0 || genome_name.indexOf(rgxi) >= 0 || gene_sym.indexOf(rgxi) >= 0)) {
				regexState = true;
				break;
			}
		}

		return regexGN ? regex ? genome_name.indexOf(regexGN) >= 0 && regexState : genome_name.indexOf(regexGN) >= 0 : regex ? regexState : false;

	} else
		return true;
}

function defineButtons(windowID) {
	var gridObject = getScratchObject(windowID), btn = $Page.getCartSaveButton();
	gridObject.allowHashing = true;

	if (btn) {
		btn.on('click', function() {
			if (gridObject.activeTab == 0) {
				getSelectedFeatures(windowID, "cart", "", "", "");
			} else {
				processFigfamSelectedItems(windowID, "heatmap_cart", "", "", "", "");
				Ext.getDom("fids").value = "";
			}
		});
	}
}

function DownloadTable(windowID, fileType) {"use strict";

	var tP = "Genome Name\tLocus Tag\tRefSeq Locus Tag\tGene Symbol\tProduct Description\tSamples\tUpregulated\tDownregulated\n", gridObject = getScratchObject(windowID), Pi = gridObject.Pagingstore.data.items, d, tS;

	for (var k = 0; k < Pi.length; k++) {
		d = Pi[k].data;
		tP += d.genome_name + "\t";
		tP += d.locus_tag + "\t";
		tP += d.exp_locus_tag + "\t";
		tP += d.gene + "\t";
		tP += d.patric_product + "\t";
		tP += d.sample_size + "\t";
		tP += d.up + "\t";
		tP += d.down + "\n";
	}

	tS = document.getElementById(windowID + '_geneToFile');
	tS.GeneFileName.value = 'TranscriptomicsGenes';
	tS.GeneFileType.value = fileType;
	tS.data.value = tP;
	tS.submit();
}

function DownloadAllData(windowID) {"use strict";

	var gridObject = getScratchObject(windowID), tR = getScratchObject(windowID + "_groupRows"), items = Ext.getStore('ds_sample').data.items, Pi = gridObject.Pagingstore.data.items, tP = "", i, j, k, d, ik, tS, m;

	for ( i = 0; i < items.length; i++) {
		tP += "\t" + items[i].data.pid;
	}
	tP += "\n";

	for ( j = 0; j < Pi.length; j++) {
		for ( m = 0; m < tR.length; m++) {
			if (tR[m].refseq_locus_tag == Pi[j].data.refseq_locus_tag) {
				d = tR[m];
				tP += d.refseq_locus_tag;
				for ( k = 0; k < items.length; k++) {
					ik = items[k].data;
					if (d.samples[ik.pid]) {
						if (d.samples[ik.pid].log_ratio == "")
							tP += "\t0";
						else
							tP += "\t" + d.samples[ik.pid].log_ratio;
					} else
						tP += "\t0";
				}
			}
		}
		tP += "\n";
	}

	tS = document.getElementById(windowID + '_geneToFile');
	tS.GeneFileName.value = 'SamplesGenes';
	tS.GeneFileType.value = 'txt';
	tS.data.value = tP;
	tS.submit();

}

function prepareDataForCluster(windowID) {
	var stateObject = getStateObject(windowID), tP = "", c, r, id, datalabel, i, j;

	if (stateObject.heatmapAxis != "Transpose") {
		c = currentData.rows;
		r = currentData.columns;
		id = "rowID";
		datalabel = "colID";
	} else {
		c = currentData.columns;
		r = currentData.rows;
		id = "colID";
		datalabel = "rowID";
	}

	for ( i = 0; i < c.length; i++) {
		tP += "\t" + c[i][id];
	}

	tP += "\n";

	for ( i = 0; i < r.length; i++) {
		tP += r[i][datalabel];
		for ( j = 0; j < c.length; j++) {
			spl = r[i].meta.labels.split("|");
			if (spl[j] == null)
				tP += "\t0";
			else
				tP += "\t" + spl[j];
		}
		tP += "\n";
	}
	return tP;
}

function DoColorChange(windowID, colorScheme) {"use strict";

	var stateObject = getStateObject(windowID);

	if (stateObject.colorScheme != colorScheme) {
		stateObject.colorScheme = colorScheme;
		currentData.colorStops = getColorStop(windowID, giMax);
		(jQuery(heatmapid)[0]).refreshData();
	}
	Ext.get("sample-layout").unmask();
}

function DoCluster(windowID) {"use strict";

	var params = {};
	params.e = '1';
	params.g = '1';
	params.m = 'a';
	params.ge = '2';
	submitCluster(prepareDataForCluster(windowID), params, windowID);

}

function DoAdvancedCluster(windowID) {"use strict";

	var stateObject = getStateObject(windowID);

	var s = {
		"No clustering" : "0",
		"Uncentered correlation" : "1",
		"Pearson correlation" : "2",
		"Uncentered correlation, absolute value" : "3",
		"Pearson correlation, absolute value" : "4",
		"Spearman rank correlation" : "5",
		"Kendall tau" : "6",
		"Euclidean distance" : "7",
		"City-block distance" : "8",
		"Pairwise complete-linkage" : "m",
		"Pairwise single-linkage" : "s",
		"Pairwise centroid-linkage" : "c",
		"Pairwise average-linkage" : "a"
	};

	if (stateObject.clusterWindow == null) {

		var clusterStore = Ext.create('Ext.data.Store', {
			fields : ['id', 'value'],
			data : [{
				id : "0",
				value : "No clustering"
			}, {
				id : "1",
				value : "Uncentered correlation"
			}, {
				id : "2",
				value : "Pearson correlation"
			}, {
				id : "3",
				value : "Uncentered correlation, absolute value"
			}, {
				id : "4",
				value : "Pearson correlation, absolute value"
			}, {
				id : "5",
				value : "Spearman rank correlation"
			}, {
				id : "6",
				value : "Kendall tau"
			}, {
				id : "7",
				value : "Euclidean distance"
			}, {
				id : "8",
				value : "City-block distance"
			}]
		});

		var distanceStore = Ext.create('Ext.data.Store', {
			fields : ['id', 'value'],
			data : [{
				id : "m",
				value : "Pairwise complete-linkage"
			}, {
				id : "s",
				value : "Pairwise single-linkage"
			}, {
				id : "c",
				value : "Pairwise centroid-linkage"
			}, {
				id : "a",
				value : "Pairwise average-linkage"
			}]
		});

		var distance = {
			xtype : 'fieldset',
			title : 'Clustering type',
			layout : 'anchor',
			defaults : {
				anchor : '100%',
				labelStyle : 'padding-left:4px;'
			},
			items : [{
				id : 'cluster_distance',
				xtype : 'combobox',
				store : distanceStore,
				valueField : 'id',
				displayField : 'value',
				value : 'Pairwise average-linkage',
				typeAhead : false
			}]
		};

		var algorithm = {
			xtype : 'fieldset',
			title : 'Clustering algorithm',
			layout : 'anchor',
			defaults : {
				anchor : '100%',
				labelStyle : 'padding-left:4px;'
			},
			items : [{
				id : 'cluster_algorithm',
				xtype : 'combobox',
				store : clusterStore,
				valueField : 'id',
				displayField : 'value',
				value : 'Pearson correlation',
				typeAhead : false
			}]
		};

		var radioGroup = {
			xtype : 'fieldset',
			title : 'Cluster by',
			layout : 'anchor',
			defaults : {
				anchor : '100%',
				labelStyle : 'padding-left:4px;',
				style : 'padding-top:4px'
			},
			items : [{
				id : 'cluster_radio',
				xtype : 'radiogroup',
				columns : 1,
				items : [{
					boxLabel : 'Genes',
					name : 'val',
					inputValue : 1
				}, {
					boxLabel : 'Comparisons',
					name : 'val',
					inputValue : 2
				}, {
					boxLabel : 'Both',
					name : 'val',
					inputValue : 3,
					checked : true
				}]
			}]
		};

		var fp = Ext.create('Ext.FormPanel', {
			items : [radioGroup, algorithm, distance],
			width : 300,
			buttons : [{
				text : 'Submit',
				handler : function() {
					Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');
					var params = {}, crvalue = Ext.getCmp('cluster_radio').getValue();

					params.g = (crvalue['val'] == 3 || crvalue['val'] == 1) ? '1' : '0';
					params.e = (crvalue['val'] == 3 || crvalue['val'] == 2) ? '1' : '0';
					params.m = s[Ext.getCmp('cluster_distance').getValue()] || Ext.getCmp('cluster_distance').getValue();
					params.ge = s[Ext.getCmp('cluster_algorithm').getValue()] || Ext.getCmp('cluster_algorithm').getValue();

					submitCluster(prepareDataForCluster(windowID), params, windowID);
					stateObject.clusterWindow.hide();
				}
			}, {
				text : 'Cancel',
				handler : function() {
					stateObject.clusterWindow.hide();
				}
			}]
		});

		stateObject.clusterWindow = new Ext.create('Ext.Window', {
			layout : 'fit',
			closeAction : 'hide',
			plain : true,
			modal : true,
			shim : false,
			title : 'Advanced Clustering',
			autoScroll : true,
			items : fp
		});
		stateObject.clusterWindow.show();
	} else {
		stateObject.clusterWindow.show();
	}
}

function submitCluster(data, params, windowID) {"use strict";

	var pk = +Date.now(), threshold = 1000000, place, sendData = [], end, i;

	if (data.length > threshold) {
		place = 0;
		sendData = [];

		for ( i = 0; i < data.length; i += threshold) {
			end = i + threshold;

			if (end > data.length)
				end = i + data.length % threshold;

			sendData[place] = data.substring(i, end);
			place++;
		}

		for ( i = 0; i < place; i++) {
			if (i == place - 1)
				setTimeout(sendDataChunks(sendData[i], params, pk, 'Run', windowID), 3000);
			else if (i == 0)
				sendDataChunks(sendData[i], params, pk, 'Store', windowID);
			else
				setTimeout(sendDataChunks(sendData[i], params, pk, 'Store', windowID), 3000);
		}
	} else {
		sendDataChunks(data, params, pk, 'Run', windowID);
	}
}

function sendDataChunks(data, params, pk, action, windowID) {"use strict";

	var stateObject = getStateObject(windowID), decoded;
	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 6000000,
		params : {
			callType : "doClustering",
			data : data,
			m : params.m,
			g : params.g,
			e : params.e,
			ge : params.ge,
			pk : pk,
			action : action
		},
		success : function(rs) {

			if (action == 'Run') {
				decoded = Ext.JSON.decode(rs.responseText);
				if (stateObject.heatmapAxis == "") {
					stateObject.ClusterRowOrder = decoded.rows;
					stateObject.ClusterColumnOrder = decoded.columns;
				} else {
					stateObject.ClusterRowOrder = decoded.columns;
					stateObject.ClusterColumnOrder = decoded.rows;
				}
				updateFilterState(windowID, stateObject);
				updateTranscriptomics(windowID);
				Ext.get("sample-layout").unmask();
			}
		}
	});

}

function checkAddClick(windowID) {

	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		addSelectedItems("Feature");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}

}

function setGroupPopup(windowID) {
	var gridObject = getScratchObject(windowID);
	if (saveToGroup(Ext.getDom("fids").value, "Feature")) {

		gridObject.checkbox.clearSelections();
		gridObject.checkbox.updateCheckAllIcon();
		gridObject.checkbox.setCheckAll(false);

		adjustCheckBoxes("unchecked");
		gridObject.checkbox.showMessage(Ext.getDom("fids").value.split(",").length, "Feature");
	}
}

function getSelectedFeatures(windowID, actiontype, showdownload, fastatype, to) {"use strict";

	var gridObject = getScratchObject(windowID), locusList = "", items, i, sl;

	if (gridObject.checkbox.getCheckAll() == true) {
		items = Ext.getStore('ds_paging').data.items;
		locusList = items[0].data.na_feature_id;
		for ( i = 1; i < items.length; i++) {
			locusList += "," + items[i].data.na_feature_id;
		}
	} else {
		sl = gridObject.checkbox.getSelections();
		locusList = (sl[0]).get('na_feature_id');
		for ( i = 1; i < sl.length; i++) {
			locusList += "," + (sl[i]).get('na_feature_id');
		}
	}
	processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, locusList);
}

function processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, rs) {
	var stateObject = getStateObject(windowID), hrefBase;
	if (actiontype == "cart") {
		Ext.getDom("fids").value = rs;
		setGroupPopup(windowID);
	} else if (actiontype == "fasta") {
		catchFastaIds(windowID, showdownload, fastatype, rs);
	} else if (actiontype == "msa") {
		hrefBase = 'TreeAlignerB?cType=' + stateObject.cType + "&cId=" + stateObject.cId + "&";
		if (windowID.indexOf('FIGfamSorterB') < 0) {
			hrefBase = 'TreeAligner?';
		}
		catchAlignIds(rs.split(",").length, stateObject.serveURL, hrefBase, rs);
	} else if (actiontype == "idmap") {
		Ext.Ajax.request({
			url : "/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE",
			method : 'POST',
			timeout : 600000,
			params : {
				keyword : rs,
				from : 'PATRIC ID',
				to : to,
				sraction : 'save_params'
			},
			success : function(response, opts) {
				document.location.href = "IDMapping?cType=&cId=&dm=result&pk=" + response.responseText;
			}
		});
	} else if (actiontype == "heatmap_cart") {
		setGroupPopup(windowID);
	} else if (actiontype == "pathway_enrichment") {
		Ext.Ajax.request({
			url : "/portal/portal/patric/TranscriptomicsEnrichment/TranscriptomicsEnrichmentWindow?action=b&cacheability=PAGE",
			method : 'POST',
			timeout : 600000,
			params : {
				feature_info_id : rs,
				callType : 'saveParams'
			},
			success : function(response, opts) {
				document.location.href = "TranscriptomicsEnrichment?cType=&cId=&pk=" + response.responseText;
			}
		});
	}
}

function submitFASTA(windowID, actionType, fastaType) {
	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "fasta", actionType, fastaType, "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function catchFastaIds(windowID, actionType, fastaType, ajaxHttp) {"use strict";

	var toS = document.getElementById(windowID + "_fTableForm");
	toS.action = "/patric-common/jsp/fasta_download_handler.jsp";
	toS.fastaaction.value = actionType;
	toS.fastascope.value = "Selected";
	toS.fastatype.value = fastaType;
	toS.fids.value = ajaxHttp;
	if (actionType == "display") {
		toS.target = "disp";
	} else {
		toS.target = "";
	}
	toS.submit();
	adjustCheckBoxes("unchecked");
}

function submitIDMapping(windowID, to) {"use strict";

	var gridObject = getScratchObject(windowID);
	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "idmap", "", "", to);
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}

}

function submitAlign(windowID) {"use strict";

	var gridObject = getScratchObject(windowID);
	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "msa", "", "", "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function submitEnrichment(windowID) {"use strict";

	var gridObject = getScratchObject(windowID);
	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "pathway_enrichment", "", "", "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function catchAlignIds(figfamCount, serveURL, hrefBase, ajaxHttp) {"use strict";

	var featureList = ajaxHttp.split(',');
	if (featureList.length <= 256) {
		Ext.Ajax.request({
			url : serveURL,
			method : 'POST',
			timeout : 600000,
			params : {
				callType : "toAligner",
				featureIds : ajaxHttp
			},
			success : function(rs) {
				document.location.href = hrefBase + "pk=" + rs.responseText;
			}
		});
	} else {
		Ext.Msg.alert(featureList.length + ' genes selected!', 'Current resources can not handle this many proteins');
	}
}

function submittoFeatureTable(windowID, featureIds) {"use strict";

	var stateObject = getStateObject(windowID);
	Ext.Ajax.request({
		url : "/portal/portal/patric/TranscriptomicsGeneFeature/TranscriptomicsGeneFeatureWindow?action=b&cacheability=PAGE",
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "saveFeatureParams",
			feature_info_id : featureIds
		},
		success : function(rs) {
			open_in_new_tab("TranscriptomicsGeneFeature?cType=" + stateObject.cType + "&cId=" + stateObject.cId + "&pk=" + rs.responseText);
		}
	});
}

function renderGeneLevelPage(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"Feature?cType=feature&cId=" + record.data.na_feature_id + "\"/>" + value + "</a>");

}

function BasicTooltipRenderer(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return value ? value : "";
}

function renderDownRegulate(value, metadata, record, rowIndex, colIndex, store) {

	var stateObject = getStateObject(record.data.windowID);
	metadata.tdAttr = 'data-qtip="' + record.data.down + '" data-qclass="x-tip"';
	if (stateObject.colId == '') {
		if (+record.data.down > 0)
			return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"showSamples('" + record.data.windowID + "','down','" + record.data.na_feature_id + "');\"/>" + record.data.down + "</a>");
		else
			return record.data.down;
	} else
		return record.data.down;
}

function renderUpRegulate(value, metadata, record, rowIndex, colIndex, store) {
	var stateObject = getStateObject(record.data.windowID);
	metadata.tdAttr = 'data-qtip="' + record.data.up + '" data-qclass="x-tip"';
	if (stateObject.colId == '') {
		if (+record.data.up > 0)
			return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"showSamples('" + record.data.windowID + "','up','" + record.data.na_feature_id + "');\"/>" + record.data.up + "</a>");
		else
			return record.data.up;
	} else
		return record.data.up;
}

function renderSamples(value, metadata, record, rowIndex, colIndex, store) {
	var stateObject = getStateObject(record.data.windowID);
	metadata.tdAttr = 'data-qtip="' + record.data.sample_size + '" data-qclass="x-tip"';

	if (stateObject.colId == '')
		if (+record.data.sample_size > 0)
			return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"showSamples('" + record.data.windowID + "','all','" + record.data.na_feature_id + "');\"/>" + record.data.sample_size + "</a>");
		else
			return record.data.sample_size;
	else
		return record.data.sample_size;
}

function UpRRenderer(value, metadata, record, rowIndex, colIndex, store){
	var ids = record.id.split("-"),
		id = ids[ids.length -1],
		Page = $Page,
		property = Page.getPageProperties();
	return '<input title=\"Up regulated\" type=\"radio\" id=\"'+property.name+'_sc1'+record.id+'\" onclick=\"haveSampleCareClick(\''+property.name+'\', \''+id+'\', \'1\')\"/>';
}

function DownRRenderer(value, metadata, record, rowIndex, colIndex, store){
	var ids = record.id.split("-"),
		id = ids[ids.length -1],
		Page = $Page,
		property = Page.getPageProperties();
	return '<input title=\"Down regulated\" type=\"radio\" id=\"'+property.name+'_sc0'+record.id+'\" onclick=\"haveSampleCareClick(\''+property.name+'\', \''+id+'\', \'0\')\"/>';
}

function NcRenderer(value, metadata, record, rowIndex, colIndex, store){
	var ids = record.id.split("-"),
	 	id = ids[ids.length -1],
		Page = $Page,
		property = Page.getPageProperties();
	return '<input title=\"Up or down regulated\" type=\"radio\" id=\"'+property.name+'_sc_'+record.id+'\" checked onclick=\"haveSampleCareClick(\''+property.name+'\', \''+id+'\', \'_\')\"/>';
}


function OwnerRenderer(value, metadata, record, rowIndex, colIndex, store) {"use strict";

	var owner;
	if (record.data.pid.indexOf("-") >= 0) {
		metadata.tdAttr = 'data-qtip="me" data-qclass="x-tip"';
		owner = 'me';
	} else {
		metadata.tdAttr = 'data-qtip="PATRIC" data-qclass="x-tip"';
		owner = 'PATRIC';
	}
	return owner;
}

function showSamples(windowID, which, data) {"use strict";

	var stateObject = getStateObject(windowID), tR = getScratchObject(windowID + "_groupRows"), sIds = stateObject.samplePIds, output = "", nextRow, i, j, obj, flag, z, l, s, uf = stateObject["upFold"], df = stateObject["downFold"], uz = stateObject["upZscore"], dz = stateObject["downZscore"];

	if (tR != null) {
		for ( j = 0; j < tR.length; j++) {
			nextRow = tR[j];
			if (nextRow.na_feature_id == data) {
				s = nextRow.samples;
				for ( i = 0; i < sIds.length; i++) {
					obj = s[sIds[i]];
					if (obj != null) {
						flag = false;
						z = IsItFloatFormat(obj["z_score"]) ? parseFloat(obj["z_score"]) : 0;
						l = IsItFloatFormat(obj["log_ratio"]) ? parseFloat(obj["log_ratio"]) : 0;
						if (which == "down") {
							if (z < dz && l < df)
								flag = true;
						} else if (which == "up") {
							if (z > uz && l > uf)
								flag = true;
						} else
							flag = true;

						if (flag) {
							if (output == "")
								output += sIds[i];
							else
								output += "," + sIds[i];
						}
					}
				}
			}
		}
	}

	if (which != "up" && which != "down")
		window.location.href = "TranscriptomicsGeneExp?cType=feature&cId=" + data + "&sampleId=" + output + "&log_ratio=&zscore=";
	else
		window.location.href = "TranscriptomicsGeneExp?cType=feature&cId=" + data + "&sampleId=" + output + "&log_ratio=" + uf + "&zscore=" + uz;
}