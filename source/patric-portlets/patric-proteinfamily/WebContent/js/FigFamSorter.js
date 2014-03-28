var rows = [], cols = [], start, end;

function FigFamSorterStateObject(windowID, serveResource, getContextPath, keyword_search) {
	// save initial values
	this.windowID = windowID;
	this.serveURL = serveResource;
	this.contextPath = getContextPath;

	// set duration at 30 days or desired value
	this.duration = defaultDuration;

	this.famType = '';

	this.keyword_search = keyword_search;

	this.syntonyId = "";

	this.ClusterRowOrder = [];
	this.ClusterColumnOrder = [];

	this.heatmapAxis = '';
	this.colorScheme = 'rgb';

	this.genomeIds = [];
	// this.genomeNames = [];
	this.genomeVSfilter = null;
	//this.orders = [];
	//this.ncbitaxIds = [];

	this.cType = '';
	this.cId = '';

	this.filter = '';
	this.regex = '';

	this.perfectFamMatch = 'A';

	this.minnumber_of_members = '';
	this.maxnumber_of_members = '';

	this.minnumber_of_species = '';
	this.maxnumber_of_species = '';

	this.heatmapState = null;

	this.loadData = FigFamSorterLoadData;
	this.saveData = FigFamSorterSaveData;
}

var GenomeVSFilterPair = ( function() {
		function init(index, genome_name) {
			this.index = index, this.status = ' ', this.genome_name = genome_name;
		}

		function getIndex() {
			return this.index;
		}

		function getGenomeName() {
			return this.genome_name;
		}

		function getStatus() {
			return this.status;
		}

		function setStatus(status) {
			this.status = status;
		}

		return function() {
			this.init = init, this.setStatus = setStatus, this.getStatus = getStatus, this.getGenomeName = getGenomeName, this.getIndex = getIndex;
			return this;
		};
	}());

//create a method for the required saveData link
function FigFamSorterSaveData(windowID, namespace) {
}

function FigFamSorterLoadData(namespace) {
}

function FigFamSorterOnReady(windowID, resourceURL, contextPath, cType, cId, keyword_search, idText) {
	// register windowID to insure that state values gets stored to a cookie
	//   on page exits or refreshes
	addWindowID(windowID);
	// create a default state object with the critical values
	//   provided by the server
	var stateObject = new FigFamSorterStateObject(windowID, resourceURL, contextPath, keyword_search);

	//  try to get other state values that might have been saved in a cookie
	loadStateObject(windowID, stateObject);

	idForHeatmap = windowID;

	if ((idText != null) && (0 < idText.length)) {
		stateObject.genomeIds = idText.split(",");
		getGenomeNames(windowID, stateObject);
	} else if (cType == "genome") {
		stateObject.cType = cType;
		stateObject.cId = cId;
		stateObject.genomeIds = cId.split(",");
		getGenomeNames(windowID, stateObject);
	} else if (cType == "taxon") {
		stateObject.cType = cType;
		stateObject.cId = cId;
		Ext.Ajax.request({
			url : stateObject.serveURL,
			method : 'GET',
			timeout : 600000,
			params : {
				callType : "getTaxonIds",
				taxonId : cId
			},
			success : function(rs) {
				catchTaxonIds(windowID, stateObject, rs);
			}
		});
	}
}

function catchTaxonIds(windowID, stateObject, rs) {
	stateObject.genomeIds = (rs.responseText).split(",");
	getGenomeNames(windowID, stateObject);
}

function getGenomeNames(windowID, stateObject) {
	var genomeIds = stateObject.genomeIds, leftHtml, option1, option2, option3, option4;

	if (genomeIds.length > 500) {
		leftHtml = "<div style=\"margin-left:5px;\"><b>More than 500 genomes</b></div><br/>";
	} else {
		leftHtml = "<div style=\"margin-left:5px;\">" + genomeIds.length + " genomes (<a href=\"FIGfam?cType=taxon&cId=&dm=&bm=tool\">Change Genome Selection</a>)</div>" + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"2\"><tbody><tr width=\"100%\"><td style=\"line-height: 14px; padding-left: 4px;\" bgcolor=\"#f0f0f0\" colspan=\"4\"><b>Present</b> in all families</td></tr><tr><td style=\"line-height: 14px;\" bgcolor=\"#f0f0f0\"></td><td style=\"line-height: 14px; padding-left: 34px;\" width=\"100%\" bgcolor=\"#FFFFFF\" colspan=\"3\"><b>Absent</b> from all families</td></tr><tr><td style=\"line-height: 14px;\" bgcolor=\"#f0f0f0\"></td><td style=\"line-height: 14px;\" bgcolor=\"#FFFFFF\"></td><td style=\"line-height: 14px; padding-left: 58px;\" width=\"100%\" bgcolor=\"#f0f0f0\" colspan=\"2\"><b>Either/Mixed</b></td></tr></tbody></table>" + "<div id='" + windowID + "_4genome_grid'></div>";
	}

	option1 = "<div style='float: left;'>Number of Proteins per Family </div></br>" + "<FORM NAME='" + windowID + "_memberForm' " + "ONSUBMIT=\"updateOrthoListTable('" + windowID + "'); return false;\">" + "<div id='_minMembers' style='float:left'></div><div style='padding-left: 15px;padding-top: 5px;float: left;''>to</div> <div id='_maxMembers' style='float:left; padding-left:15px;'></div>" + "</FORM><br/>";

	option2 = "<br/><div style='float: left;'>Number of Genomes per Family </div></br>" + "<FORM NAME='" + windowID + "_speciesForm' " + "ONSUBMIT=\"updateOrthoListTable('" + windowID + "'); return false;\">" + "<div id='_minSpecies' style='float:left'></div><div style='padding-left: 15px;padding-top: 5px;float: left;''>to</div> <div id='_maxSpecies' style='float:left; padding-left:15px;'></div>" + "</FORM>";

	option3 = "<div style=\"padding-top:10px;\">" + "<INPUT title='Perfect Families (One protein per genome)' TYPE='radio' name='familySet' ID='" + windowID + "_perfectIn'\><label for='"+windowID+"_perfectIn'>Perfect Families (One protein per genome)</label><BR/><INPUT title='Non Perfect Families' TYPE='radio' name='familySet' ID='" + windowID + "_perfectOut'/><label for='"+windowID+"_perfectOut'>Non Perfect Families</label>" + "<BR/><INPUT title='All Families' TYPE='radio' name='familySet' ID='" + windowID + "_perfectAll' CHECKED/><label for='"+windowID+"_perfectAll'>All Families</label>" + "</div>";

	option4 = "<label for=\""+windowID+"_toSetRegex-inputEl\">Filter by one or more keywords </label><BR />" + "<div style=\"width: 98%; padding-top:10px;\" id=\"textbox_div\"></div>";

	leftHtml += "<div style=\"padding:10px;\">" + option4 + option3 + option1 + option2 + "<br/><div style=\"padding:10px; float:right;\"><input type='button' class='button' style='padding: 2px 8px;' value='Filter' onclick=\"updateOrthoListTable('" + windowID + "');\"/></div>";

	Ext.getDom("information_panel").innerHTML = "<p>For a description of what you can do from this Protein Families (FIGfam) page, please see our " + "<a href='http://enews.patricbrc.org/protein-family-sorter/'>Protein Family Sorter FAQs.</a></p>";

	Ext.create('Ext.panel.Panel', {
		id : 'tabLayout',
		border : true,
		autoScroll : false,
		width : $(window).width() - 245,
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
			height : 775,
			border : false,
			autoScroll : true
		}],
		renderTo : 'sample-layout'
	});

	Ext.create('Ext.panel.Panel', {
		title : 'Filter By',
		renderTo : 'tree-panel',
		width : 239,
		height : 825,
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
			width : 239,
			height : 825,
			split : true,
			boder : false
		}],
		listeners : {
			resize : function(cmp, width, height, oldWidth, oldHeight, eOpts) {
				var Page = $Page;
				if (Page.getGrid()) {
					Ext.getCmp("westPanel").setWidth(width);
					Page.doTabLayout();
					if(Ext.getCmp(windowID + '_genome_grid'))
						Ext.getCmp(windowID + '_genome_grid').setWidth(width);
				}
			}
		}
	});
	
	createLeftBottomItems(windowID);
	doAjaxNames(windowID, stateObject);
}

function createLeftBottomItems(windowID) {

	Ext.create('Ext.form.field.TextArea', {
		id : windowID + '_toSetRegex',
		renderTo : 'textbox_div',
		width : 215,
		height : 75
	});

	Ext.create('Ext.form.field.Text', {
		id : windowID + '_minMembers',
		renderTo : '_minMembers',
		width : 40
	});

	Ext.create('Ext.form.field.Text', {
		id : windowID + '_maxMembers',
		renderTo : '_maxMembers',
		width : 40
	});

	Ext.create('Ext.form.field.Text', {
		id : windowID + '_minSpecies',
		renderTo : '_minSpecies',
		width : 40
	});

	Ext.create('Ext.form.field.Text', {
		id : windowID + '_maxSpecies',
		renderTo : '_maxSpecies',
		width : 40
	});

}

function doAjaxNames(windowID, stateObject) {"use strict";

	var stateObject = getStateObject(windowID), i, items, tableObject, gridObject;
	start = (new Date()).getMilliseconds();
	tableObject = new GroupGrid(windowID, stateObject, 0);
	
	Ext.define('Genome_Store', {
		extend : 'Ext.data.Model',
		fields : [{
			name : 'gid',
			type : 'string'
		}, {
			name : 'genome_info_id',
			type : 'string'
		}, {
			name : 'genome_name',
			type : 'string'
		}, {
			name : 'genome_status',
			type : 'string'
		}, {
			name : 'isolation_country',
			type : 'string'
		}, {
			name : 'host_name',
			type : 'string'
		}, {
			name : 'disease',
			type : 'string'
		}, {
			name : 'collection_date',
			type : 'string'
		}, {
			name : 'completion_date',
			type : 'string'
		}]
	});

	Ext.create('Ext.data.Store', {
		storeId : 'ds_genome',
		model : 'Genome_Store',
		proxy : {
			type : 'ajax',
			url : stateObject.serveURL,
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
			extraParams : {
				keyword : 'gid:(' + stateObject.genomeIds.join(" OR ") + ")",
				fields: (stateObject.genomeIds.length > 500)?"genome_info_id,genome_name":"",
				callType:'getGenomeDetails'
			}
		},
		autoLoad : true,
		remoteSort : false,
		listeners : {
			datachanged : function() {
				if (!stateObject.genomeVSfilter) {
					items = this.data.items;
					stateObject.genomeVSfilter = {};
					for ( i = 0; i < items.length; i++) {
						stateObject.genomeVSfilter[items[i].data.genome_info_id] = new GenomeVSFilterPair();
						stateObject.genomeVSfilter[items[i].data.genome_info_id].init(i, items[i].data.genome_name);
						// stateObject.genomeNames[i] = items[i].data.genome_name;
						stateObject.genomeIds[i] = items[i].data.genome_info_id;
						stateObject.filter += " ";
					}

					tableObject.stateObject = stateObject;
					
					Ext.Ajax.request({
						url : stateObject.serveURL,
						method : 'POST',
						timeout : 600000,
						params : {
							callType : "getGroupStats",
							genomeIds : stateObject.genomeIds.join(","),
							keyword : stateObject.keyword_search
						},
						success : function(rs) {
							catchGroupStats(windowID, rs);
						}
					});
				}
			}
		}
	});

	if (stateObject.genomeIds.length <= 500) {
		Ext.create('Ext.grid.Panel', {
			store : 'ds_genome',
			id : windowID + '_genome_grid',
			renderTo : windowID + "_4genome_grid",
			columns : [{
				header : '<input title=\"Present in all families\" type=\"radio\" id=\"' + windowID + '_scALL_0\" onclick=\"haveOrthoCareClick(\'' + windowID + '\', \'ALL\', \'0\')\"/>',
				hideable : false,
				menuDisabled : true,
				resizable : false,
				align : 'center',
				tooltip : 'Present in all families',
				width : 30,
				renderer : GenomeColumnRenderer
			}, {
				header : '<input title=\"Absent from all families\" type=\"radio\" id=\"' + windowID + '_scALL_1\" onclick=\"haveOrthoCareClick(\'' + windowID + '\', \'ALL\', \'1\')\"/>',
				hideable : false,
				menuDisabled : true,
				resizable : false,
				align : 'center',
				tooltip : 'Absent from all families',
				width : 30,
				renderer : GenomeColumnRenderer
			}, {
				header : '<input title=\"Either/Mixed\" type=\"radio\" checked id=\"' + windowID + '_scALL_2\" onclick=\"haveOrthoCareClick(\'' + windowID + '\', \'ALL\', \'2\')\"/>',
				hideable : false,
				menuDisabled : true,
				resizable : false,
				align : 'center',
				tooltip : "Either/Mixed",
				width : 30,
				renderer : GenomeColumnRenderer
			}, {
				header : 'Genome Name',
				width : 100,
				sortable : true,
				dataIndex : 'genome_name',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Genome Status',
				width : 50,
				align : 'center',
				sortable : true,
				dataIndex : 'genome_status',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Isolation Country',
				width : 80,
				sortable : true,
				dataIndex : 'isolation_country',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Host Name',
				width : 80,
				sortable : true,
				dataIndex : 'host_name',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Disease',
				width : 80,
				sortable : true,
				dataIndex : 'disease',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Collection Date',
				width : 80,
				sortable : true,
				dataIndex : 'collection_date',
				renderer : BasicTooltipRenderer
			}, {
				header : 'Completion Date',
				width : 80,
				sortable : true,
				dataIndex : 'completion_date',
				renderer : BasicTooltipRenderer
			}],
			viewConfig : {
				forceFit : true
			},
			height : 370,
			border : false,
			listeners : {
				'sortchange' : function() {
					updateFilterHTML(stateObject);
					stateObject.ClusterRowOrder = [];
					stateObject.ClusterColumnOrder = [];
					gridObject = getScratchObject(windowID);
					if (gridObject.activeTab == 1)
						gridObject.showPanel();
				}
			}
		});
	}
	
	cacheObject(windowID, tableObject);
	$Page.doLayout();
}

function updateFilterHTML(stateObject) {"use strict";

	var windowID = stateObject.windowID, toSet, i, which = 0, j, genomeIds, idx, key=null, genomeVSfilter, filter;

	toSet = Ext.getCmp(windowID + '_toSetRegex');
	toSet.setValue(stateObject.regex);
	if (stateObject.perfectFamMatch == 'A') {
		toSet = document.getElementById(windowID + "_perfectAll");
		toSet.checked = true;
	} else if (stateObject.perfectFamMatch == 'Y') {
		toSet = document.getElementById(windowID + "_perfectIn");
		toSet.checked = true;
	} else if (stateObject.perfectFamMatch == 'N') {
		toSet = document.getElementById(windowID + "_perfectOut");
		toSet.checked = true;
	}

	toSet = Ext.getCmp(windowID + '_minMembers');
	toSet.setValue(stateObject.minnumber_of_members);

	toSet = Ext.getCmp(windowID + '_maxMembers');
	toSet.setValue(stateObject.maxnumber_of_members);

	toSet = Ext.getCmp(windowID + '_minSpecies');
	toSet.setValue(stateObject.minnumber_of_species);

	toSet = Ext.getCmp(windowID + '_maxSpecies');
	toSet.setValue(stateObject.maxnumber_of_species);

	genomeVSfilter = stateObject.genomeVSfilter;

	filter = stateObject.filter.split("");

	for ( i = 0; i < filter.length; i++) {
		for (key in genomeVSfilter) {
			if (genomeVSfilter[key].getIndex() == i) {
				genomeVSfilter[key].setStatus(filter[i]);
			}
		}
	}

	genomeIds = stateObject.genomeIds;

	for ( i = 0; i < genomeIds.length; i++) {
		which = genomeVSfilter[genomeIds[i]].getStatus();
		idx = which == '1' ? '0' : which == '0' ? '1' : '2';
		for ( j = 0; j < 3; j++) {
			toSet = document.getElementById(windowID + '_sc' + genomeIds[i] + '_' + j);
			toSet && (toSet.checked = j == idx);
		}
	}

	checkForHeaderRow(windowID);
}

function checkForHeaderRow(windowID) {"use strict";

	var stateObject = getStateObject(windowID), id, toSet, filter, j;

	function AllSame(element, index, array) {
		return element === array[0];
	}

	filter = stateObject.filter.split("");
	id = filter.every(AllSame) ? filter[0] == " " ? 2 : filter[0] == "0" ? 1 : 0 : null;
	
	for ( j = 0; j < 3; j++) {
		toSet = document.getElementById(windowID + '_scALL_' + j);
		toSet.checked = !(id != j);
	}
}

function BasicTooltipRenderer(value, metadata, record, rowIndex, colIndex, store) {"use strict";
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return value ? value : "";
}

function padNum(num, digits){ 
    var str = num + ""; 
    return num.length >= digits? str: padNum("0" + str, digits);
}

function catchGroupStats(windowID, ajaxHttp) {"use strict";

	var rowData, orthoRows = [], distribution = "", genomeIds = getStateObject(windowID).genomeIds, i, t, j, ordered = [];
	
	if(ajaxHttp.responseText){
		rowData = Ext.JSON.decode(ajaxHttp.responseText);
		
		for(var key in rowData){
			ordered.push(parseInt(key.split("FIG")[1]));
		}
		
		ordered.sort(function(a, b) { return a - b;});
		
		for(j=0; j<ordered.length; j++){
			key = "FIG"+padNum(ordered[j], 8);
			
			if(genomeIds.length <= 500){
				distribution = "";
				for(i=0; i<genomeIds.length; i++){
					t = rowData[key].genomes[genomeIds[i]]?(rowData[key].genomes[genomeIds[i]]).toString(16):"0";
					distribution += t.length == 2?t:"0"+t;
				}
			}
			
			orthoRows.push(new orthoRow(key, 
					rowData[key].genomes, rowData[key].feature_count, 
					rowData[key].genome_count, 
					rowData[key].stats.min, rowData[key].stats.max, 
					rowData[key].stats.mean, rowData[key].stats.stddev, rowData[key].description));
		}
	}
	
	cacheObject(windowID + "_groupRows", orthoRows);
	drawOrthoGroupTable(windowID);
	defineButtons(windowID);
	//createCopyBox(windowID);
	cacheInitialGroup(windowID);
	beginHashChecking();
	end = (new Date()).getMilliseconds();
	console.log("Getting data from server and processing took "+ (end - start) +"ms");
	
}

function orthoRow(groupId, distribution, members, species, minLength, maxLength, mean, std, description, order) {
	this.groupId = groupId;
	this.intensity = distribution;
	this.members = members;
	this.species = species;
	this.minLength = minLength;
	this.maxLength = maxLength;
	this.mean = mean;
	this.std = std;
	this.description = description;
	this.order = order;
}

function sortNumber(a, b) {
	return a - b;
}

function setSyntenyOrder(windowID, genomeId) {
	currentData = null;
	rows = null;
	cols = null;
	var stateObject = getStateObject(windowID);
	var gridObject = getScratchObject(windowID);
	stateObject.syntonyId = genomeId;
	gridObject.gridState.syntonyId = genomeId;

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'GET',
		params : {
			callType : "getSyntonyOrder",
			// FAM_TYPE : stateObject.famType,
			syntonyId : genomeId
		},
		success : function(rs) {
			catchSyntenyOrder(windowID, rs);
		},
		failure: function(){
			Ext.MessageBox.alert("Error", "Data is not available now. Please try again later or contact PATRIC team (patric@vbi.vt.edu).");
			Ext.get("sample-layout").unmask();
		}
	});
}

function catchSyntenyOrder(windowID, rs) {"use strict";
	var orthoRows = getScratchObject(windowID + "_groupRows"),
		orderPairs = Ext.JSON.decode(rs.responseText),
		colOrder = [],
		adjust,
		endAid,
		orderAt,
		i,
		nextRow,
		bumper,
		emptySpot,
		j,
		gridObject = getScratchObject(windowID),
		stateObject = getStateObject(windowID);

	
	if (0 < orderPairs.length) {
		adjust = -(orderPairs.length * 2);
		adjust /= 2;
		orderAt = 0;
		endAid = [];
		for (i = 0; i < orthoRows.length; i++) {
			nextRow = orthoRows[i];
			if (orderPairs[orderAt] && nextRow.groupId == orderPairs[orderAt].groupId) {
				nextRow.order = orderPairs[orderAt].syntonyAt; 
				++orderAt;
			} else {
				nextRow.order -= adjust;
				endAid.push(nextRow.order);
			}
		}
		if (0 < endAid.length) {
			adjust = -adjust;
			endAid.sort(sortNumber);
			bumper = [];
			for (i = 0; i < endAid.length; i++) {
				bumper[endAid[i]] = adjust; 
				++adjust;
			}
			for (i = 0; i < orthoRows.length; i++) {
				nextRow = orthoRows[i];
				if (bumper[nextRow.order] != null) {
					nextRow.order = bumper[nextRow.order];
				}
			}
		}
		emptySpot = 0;
		for (j = 0; j < orthoRows.length; j++) {
			if (colOrder[parseInt(orthoRows[j].order)] != null) {
				for (i = 0; i < colOrder.length; i++) {
					if (colOrder[i] == null) {
						emptySpot = i;
					}
				}
				colOrder[emptySpot] = orthoRows[j].groupId;
			} else {
				colOrder[parseInt(orthoRows[j].order)] = orthoRows[j].groupId;
			}
		}
		filterOrthoTableData(windowID);

		if (stateObject.heatmapAxis != "Transpose") {
			stateObject.ClusterRowOrder = [];
			stateObject.ClusterColumnOrder = colOrder;
		} else {
			stateObject.ClusterRowOrder = colOrder;
			stateObject.ClusterColumnOrder = [];
		}
		gridObject.store.sorters.clear();
		gridObject.store.sorters.add(new Ext.util.Sorter({
			property : 'order',
			direction : 'ASC'
		}));
		gridObject.Pagingstore.sorters.clear();
		gridObject.Pagingstore.sorters.add(new Ext.util.Sorter({
			property : 'order',
			direction : 'ASC'
		}));
		gridObject.Pagingstore.sort([{
			'property' : 'order',
			'direction' : 'ASC'
		}]);
		gridObject.gridState.storeInSession();
		gridObject.showPanel();
	}
	Ext.get("sample-layout").unmask();
}

function GenomeColumnRenderer(value, metadata, record, rowIndex, colIndex, store) {
	var Page = $Page, property = Page.getPageProperties(), windowID = property.name, stateObject = getStateObject(windowID), gid = record.data.genome_info_id, checked, genomeVSfilter;

	genomeVSfilter = stateObject.genomeVSfilter;
	checked = genomeVSfilter[gid].getStatus();

	return '<input title=\"'+((colIndex == 0)?'Present in all families':(colIndex == 1)?'Absent from all families':'Either/Mixed')+'\" type=\"radio\" id=\"' + property.name + '_sc' + record.data.genome_info_id + '_' + colIndex + '\" ' + (colIndex == 0 && checked == '1' ? 'checked' : colIndex == 1 && checked == '0' ? 'checked' : colIndex == 2 && checked == ' ' ? 'checked' : '') + ' onclick=\"haveOrthoCareClick(\'' + property.name + '\', \'' + record.data.genome_info_id + '\', \'' + colIndex + '\')\"/>';
}

function haveOrthoCareClick(windowID, record_id, column) {"use strict";

	var stateObject = getStateObject(windowID), toSet, id = windowID, i, j, newFilter = [], genomeIds, genomeVSfilter, index;

	genomeIds = stateObject.genomeIds;
	genomeVSfilter = stateObject.genomeVSfilter;
	newFilter.length = genomeIds.length;

	if (record_id == "ALL") {

		for ( i = 0; i < 3; i++) {
			if (i != parseInt(column)) {
				id = windowID + '_sc' + record_id + '_' + i;
				toSet = document.getElementById(id);
				toSet.checked = false;
			}
		}

		for ( i = 0; i < genomeIds.length; i++) {
			newFilter[i] = column == 0 ? "1" : column == 1 ? "0" : " ";
			for ( j = 0; j < 3; j++) {
				id = windowID + '_sc' + genomeIds[i] + '_' + j;
				toSet = document.getElementById(id);
				toSet.checked = !(j != parseInt(column));
			}
		}

	} else {

		for ( i = 0; i < 3; i++) {
			if (i != parseInt(column)) {
				id = windowID + '_sc' + record_id + '_' + i;
				toSet = document.getElementById(id);
				toSet.checked = false;
			}
			id = windowID + '_scALL_' + i;
			toSet = document.getElementById(id);
			toSet.checked = false;
		}

		for ( i = 0; i < genomeIds.length; i++) {
			toSet = document.getElementById(windowID + '_sc' + genomeIds[i] + "_0");
			index = genomeVSfilter[genomeIds[i]].getIndex();
			if (toSet.checked) {
				newFilter[index] = '1';
			} else {
				toSet = document.getElementById(windowID + '_sc' + genomeIds[i] + "_1");
				newFilter[index] = toSet.checked ? '0' : ' ';
			}
			genomeVSfilter[genomeIds[i]].setStatus(newFilter[index]);
		}
	}
	stateObject.filter = newFilter.join('');
	checkForHeaderRow(windowID);
	updateOrthoListTable(windowID);
}

function updateOrthoListTable(windowID) {
	var stateObject = getStateObject(windowID);
	var toRead = Ext.getCmp(windowID + "_toSetRegex");
	stateObject.regex = toRead.getValue();

	toRead = document.getElementById(windowID + "_perfectIn");
	if (toRead.checked) {
		stateObject.perfectFamMatch = 'Y';
	}

	toRead = document.getElementById(windowID + "_perfectOut");
	if (toRead.checked) {
		stateObject.perfectFamMatch = 'N';
	}

	toRead = document.getElementById(windowID + "_perfectAll");
	if (toRead.checked) {
		stateObject.perfectFamMatch = 'A';
	}

	toRead = Ext.getCmp(windowID + "_minMembers");
	stateObject.minnumber_of_members = toRead.getValue();

	toRead = Ext.getCmp(windowID + "_maxMembers");
	stateObject.maxnumber_of_members = toRead.getValue();

	toRead = Ext.getCmp(windowID + "_minSpecies");
	stateObject.minnumber_of_species = toRead.getValue();

	toRead = Ext.getCmp(windowID + "_maxSpecies");
	stateObject.maxnumber_of_species = toRead.getValue();

	stateObject.syntonyId = "";
	stateObject.ClusterRowOrder = [];
	stateObject.ClusterColumnOrder = [];
	stateObject.heatmapState = null;
	updateFilterState(windowID, stateObject);
	var gridObject = getScratchObject(windowID);
	gridObject.gridState.syntonyId = "";
	drawOrthoGroupTable(windowID);
}

function getAddPopupPanel() {
	return (new Ext.form.FormPanel({
		formId : 'ATGform',
		baseCls : 'x-plain',
		labelWidth : 70,
		defaultType : 'textfield',
		items : [{
			fieldLabel : 'Name',
			name : 'group_name',
			id : 'group_name',
			anchor : '100%'
		}, {
			xtype : 'textarea',
			fieldLabel : 'Description',
			name : 'group_desc',
			id : 'group_desc',
			anchor : '100% -10'
		}, new Ext.form.Hidden({
			name : 'action_type',
			value : 'groupAction'
		}), new Ext.form.Hidden({
			name : 'action',
			value : 'add'
		}), new Ext.form.Hidden({
			id : 'group_members',
			name : 'fid'
		})]
	})
	);
}

function checkAddClick(windowID) {

	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		addSelectedItems("Feature");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}

}

function addRectangleToGroup(windowID, figfamNames, genomeList) {

	var stateObject = getStateObject(windowID), object = {};

	object["gid"] = genomeList.replace(/,/g, '##');
	object["figfam_id"] = figfamNames.replace(/,/g, '##');

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getFeatureIds",
			keyword : constructKeyword(object, "Feature")
		},
		success : function(rs) {
			if (rs.responseText.length > 0) {
				addSelectedItems("Feature");
				Ext.getDom("fids").value = rs.responseText;
			}
		}
	});
}

function drawOrthoGroupTable(windowID) {
	var gridObject = getScratchObject(windowID);
	filterOrthoTableData(windowID);
	gridObject.showPanel();
}

function filterHeatmapData(stateObject, ds, windowID) {"use strict";

	var keeps = [], items = Ext.getStore('ds_genome').data.items, stateObject = getStateObject(windowID);
	var gridObject = getScratchObject(windowID), syntenyOrderStore = [], rowCount = ds.getTotalCount(), iMax = 0, clusterColumn = (stateObject.heatmapAxis == "Transpose") ? stateObject.ClusterRowOrder : stateObject.ClusterColumnOrder, clusterRow = (stateObject.heatmapAxis == "Transpose") ? stateObject.ClusterColumnOrder : stateObject.ClusterRowOrder, rowColor, labelColor, place, i, j, genome_info_id, genome_name, index, z, record, groupId, meta, colorStop = [],
			orthoFilter = stateObject.filter;
	rows = [], cols = [];

	//	var Tree = {}, TI = {};
	//var orders = stateObject.orders;
	
	if (clusterRow != null && clusterRow.length > 0 && clusterRow[0] != "") {
		place = 0;
		for ( j = 0; j < clusterRow.length; j++) {
			for ( i = 0; i < items.length; i++) {
				genome_info_id = items[i].data.genome_info_id, index = stateObject.genomeVSfilter[genome_info_id].getIndex(), genome_name = stateObject.genomeVSfilter[genome_info_id].getGenomeName();
				if (orthoFilter.charAt(index) != '0') {
					if (genome_info_id == clusterRow[j]) {
						keeps.push(2 * index);
						labelColor = ((i % 2) == 0) ? 0x000066 : null;
						rowColor = ((i % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;
						rows.push(new Row(place, genome_info_id, genome_name, labelColor, rowColor));

						/*TI = {index: place, children: null, rowID: genomeIds[i], leaf: true};
						 if(!Tree[orders[i]])
						 Tree[orders[i]] = [];
						 Tree[orders[i]].push(TI);*/

						syntenyOrderStore.push([genome_info_id, genome_name]); ++place;
					}
				}
			}
		}

	} else {

		for ( i = 0; i < items.length; i++) {
			genome_info_id = items[i].data.genome_info_id, index = stateObject.genomeVSfilter[genome_info_id].getIndex(), genome_name = stateObject.genomeVSfilter[genome_info_id].getGenomeName();

			if (orthoFilter.charAt(index) != '0') {
				keeps.push(2 * index);
				labelColor = ((i % 2) == 0) ? 0x000066 : null;
				rowColor = ((i % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;

				rows.push(new Row(i, genome_info_id, genome_name, labelColor, rowColor));
				/*	TI = {index: i, children: null, rowID: genomeIds[i], leaf: true};
				 if(!Tree[orders[i]])
				 Tree[orders[i]] = [];
				 Tree[orders[i]].push(TI);*/
				syntenyOrderStore.push([genome_info_id, genome_name]);

			}
		}

	}

	gridObject.syntenyComboStore.loadData(syntenyOrderStore);

	if (gridObject.gridState.syntonyId != "") {
		gridObject.syntenyCombo.setValue(gridObject.gridState.syntonyId);
		gridObject.Pagingstore.sort([{
			'property' : gridObject.gridState.sortField,
			'direction' : gridObject.gridState.sortDir
		}]);
	} else {
		gridObject.syntenyCombo.setValue("");
	}

	if (clusterColumn != null && clusterColumn.length > 0 && clusterColumn[0] != "") {

		for ( z = 0; z < clusterColumn.length; z++) {
			for ( i = 0; i < rowCount; i++) {
				record = gridObject.Pagingstore.data.items[i], groupId = record.data['groupId'];
				if (groupId == clusterColumn[z]) {
					meta = {
						'instances' : record.data['members'],
						'families' : record.data['species'],
						'min' : record.data['min'],
						'max' : record.data['max']
					}, iMax = createColumn(z, record, meta, groupId, keeps, iMax);
				}
			}
		}

	} else {

		for ( i = 0; i < rowCount; i++) {

			record = gridObject.Pagingstore.data.items[i], meta = {
				'instances' : record.data['members'],
				'families' : record.data['species'],
				'min' : record.data['min'],
				'max' : record.data['max']
			}, groupId = record.data['groupId'];
			iMax = createColumn(i, record, meta, groupId, keeps, iMax);
		}
	}

	if (iMax == 1) {
		colorStop = [new ColorStop(1, 0xfadb4e)];
	} else if (iMax == 2) {
		colorStop = [new ColorStop(1 / 2, 0xfadb4e), new ColorStop(2 / 2, 0xf6b437)];
	} else if (iMax >= 3) {
		colorStop = [new ColorStop(1 / iMax, 0xfadb4e), new ColorStop(2 / iMax, 0xf6b437), new ColorStop(3 / iMax, 0xff6633), new ColorStop(iMax / iMax, 0xff6633)];
	}

	currentData = {
		'rows' : rows,
		'columns' : cols,
		'colorStops' : colorStop,
		'rowLabel' : 'Genomes',
		'colLabel' : 'Protein Families',
		'rowTrunc' : 'mid',
		'colTrunc' : 'end',
		'offset' : 1,
		'digits' : 2,
		'countLabel' : 'Members',
		'negativeBit' : false,
		'cellLabelField' : '',
		'cellLabelsOverrideCount' : false,
		'beforeCellLabel' : '',
		'afterCellLabel' : ''
		//'rowTree': createCladogram(Tree),
	};

	if (stateObject.heatmapAxis == "Transpose") {
		flipAxises(windowID);
	}

	return rowCount;
}

function createColumn(i, record, meta, groupId, keeps, iMax) {

	var iSend = "", intensity = record.data['intensity'], j, pick, iSendDecimal, labelColor, columnColor;

	for ( j = 0; j < keeps.length; j++) {
		pick = keeps[j];
		iSend += intensity.charAt(pick); ++pick;
		iSend += intensity.charAt(pick);

		iSendDecimal = parseInt(intensity.charAt(pick - 1) + intensity.charAt(pick), 16);

		if (iMax <= iSendDecimal) {
			iMax = iSendDecimal;
		}
	}

	labelColor = ((i % 2) == 0) ? 0x000066 : null;
	columnColor = ((i % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;

	cols[i] = new Column(i, groupId, record.data['description'], iSend, labelColor, columnColor, meta);

	return iMax;
}

function createCladogram(Tree) {
	var rowTree = [{
		c : [],
		l : null
	}];

	function getCladogramChildren(parent) {
		var children = [];
		for (var j = 0; j < parent.length; j++) {
			if (parent[j].leaf) {
				var obj = {
					c : null,
					l : parent[j].index
				};
				children.push(rowTree.length);
				rowTree.push(obj);
			}
		}
		return children;
	}

	for (var i in Tree) {
		rowTree.push({});
		rowTree[0].c.push(rowTree.length - 1);
		rowTree[rowTree.length - 1] = {
			c : getCladogramChildren(Tree[i]),
			l : null
		};
	}

	return rowTree;
}

function getCellParts(windowID, groupId, genomeId) {
	var stateObject = getStateObject(windowID);

	var genomeVSfilter = stateObject.genomeVSfilter;
	var result = [];
	var depth = 2 * genomeVSfilter[genomeId].getIndex();
	result.push(genomeVSfilter[genomeId].getGenomeName());
	
	var orthoRows = getScratchObject(windowID + "_groupRows");
	for (var i = 0; i < orthoRows.length; i++) {
		var nextRow = orthoRows[i];
		if (groupId == nextRow.groupId) {
			result.push(nextRow.description);
			var density = nextRow.intensity;
			if (density.charAt(depth) != '0') {
				result.push(density.substr(depth, 2));
			} else {++depth;
				result.push(density.charAt(depth));
			}
			i = orthoRows.length;
		}
	}
	return result;
}

function getRectangleCount(windowID, figfamIds, ids) {
	var depths = [];
	var stateObject = getStateObject(windowID);
	var genomeIds = stateObject.genomeIds;
	var found = ids.length;
	for (var i = 0; i < genomeIds.length; i++) {
		var iId = genomeIds[i];
		for (var j = 0; j < ids.length; j++) {
			if (iId == ids[j]) {
				j = ids.length;
				depths.push(2 * i); --found;
				if (found == 0) {
					i = genomeIds.length;
				}
			}
		}
	}
	var orthoRows = getScratchObject(windowID + "_groupRows");
	var sorted = figfamIds.slice(0);
	sorted.sort();
	var sortAt = 1;
	var toFind = sorted[0];
	var result = 0;
	//  logic depends on current state where server sorts data by
	//    groupId
	for (var i = 0; i < orthoRows.length; i++) {
		var nextRow = orthoRows[i];
		if (nextRow.groupId == toFind) {
			var counts = nextRow.intensity;
			for (var j = 0; j < depths.length; j++) {
				var bump = counts.substr(depths[j], 2);
				bump = parseInt(bump, 16);
				result += bump;
			}
			if (sortAt < sorted.length) {
				toFind = sorted[sortAt]; ++sortAt;
			} else {
				i = orthoRows.length;
			}
		}
	}
	return result;
}

function filterOrthoTableData(windowID) {"use strict";

	var stateObject = getStateObject(windowID), orthoRows = getScratchObject(windowID + "_groupRows"), genomeIds = stateObject.genomeIds, genomeVSfilter = stateObject.genomeVSfilter, orthoFilter = stateObject.filter, nextRow = null, i, j, filterFlag, tableData = new Array(), kept = 0, gridObject = getScratchObject(windowID), index;

	if (orthoRows != null) {
		for ( i = 0; i < orthoRows.length; i++) {
			nextRow = orthoRows[i];
			if (AdvancedFilter(nextRow, stateObject)) {
				filterFlag = true;
				//if(genomeIds.length <= 500){
					for ( j = 0; j < genomeIds.length; j++) {
						index = genomeVSfilter[genomeIds[j]].getIndex();
						if (orthoFilter[index] != ' ') {
							if (orthoFilter[index] == '1' && parseInt(nextRow.intensity.charAt(index * 2) + nextRow.intensity.charAt(index * 2 + 1), 16) > 0 || orthoFilter[index] == '0' && parseInt(nextRow.intensity.charAt(index * 2) + nextRow.intensity.charAt(index * 2 + 1), 16) == 0) {
								filterFlag = true;
							} else {
								filterFlag = false;
								break;
							}
						}
					}
				//}
				if (filterFlag) {
					tableData[kept] = [nextRow.members, nextRow.species, nextRow.description, nextRow.minLength, nextRow.maxLength, nextRow.mean, nextRow.std, nextRow.groupId, nextRow.intensity, nextRow.order, windowID];
					kept++;
				}
			}
		}
	}

	gridObject.Pagingstore.loadData(tableData);
	// gridObject.Pagingstore.sort();
	gridObject.store.totalCount = tableData.length;

	return tableData;
}

function AdvancedFilter(nextRow, stateObject) {

	var minmem = null, maxmem = null, minspe = null, maxspe = null, regex = stateObject.regex.trim() && stateObject.regex.trim().toLowerCase().replace(/,/g, "~").replace(/\n/g, "~").replace(/ /g, "~").split("~"), perfectFamFilter = true, regexFilter = regex == '' ? true : false, memFilter = false, speFilter = false, a = false, b = false, i, rgxi;

	if (!regexFilter) {
		for ( i = 0; i < regex.length; i++) {
			rgxi = regex[i];
			if (rgxi && (nextRow.description.toLowerCase().indexOf(rgxi) >= 0 || nextRow.groupId.toLowerCase().indexOf(rgxi) >= 0)) {
				regexFilter = true;
				break;
			}
		}
	}

	minmem = stateObject.minnumber_of_members;
	maxmem = stateObject.maxnumber_of_members;

	if (minmem != '' || maxmem != '') {
		if (minmem == '' || (minmem && nextRow.members >= parseInt(minmem)))
			a = true;

		if (maxmem == '' || (maxmem && nextRow.members <= parseInt(maxmem)))
			b = true;

		if (a && b)
			memFilter = true;
	} else if (minmem == '' && maxmem == '') {
		memFilter = true;
	}

	minspe = stateObject.minnumber_of_species;
	maxspe = stateObject.maxnumber_of_species;

	if (minspe != '' || maxspe != '') {
		a = false, b = false;

		if (minspe == '' || (minspe && nextRow.species >= parseInt(minspe)))
			a = true;

		if (maxspe == '' || (maxspe && nextRow.species <= parseInt(maxspe)))
			b = true;

		if (a && b)
			speFilter = true;
	} else if (minspe == '' && maxspe == '') {
		speFilter = true;
	}

	if (stateObject.perfectFamMatch == 'Y') {
		if (nextRow.species == nextRow.members)
			perfectFamFilter = true;
		else
			perfectFamFilter = false;
	} else if (stateObject.perfectFamMatch == 'N') {
		if (nextRow.species == nextRow.members)
			perfectFamFilter = false;
		else
			perfectFamFilter = true;
	} else
		perfectFamFilter = true;

	return regexFilter && memFilter && speFilter && perfectFamFilter;
}

function prepareDataForCluster(windowID) {
	var stateObject = getStateObject(windowID);
	var tablePass = "";

	var c, r, id, datalabel;
	if (stateObject.heatmapAxis == "Transpose") {
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

	for (var i = 0; i < c.length; i++) {
		tablePass += "\t" + c[i][id];
	}

	tablePass += "\n";

	for (var i = 0; i < r.length; i++) {
		tablePass += r[i][datalabel];
		for (var j = 0; j < c.length; j++) {
			if (stateObject.heatmapAxis == "Transpose")
				tablePass += "\t" + parseInt(parseInt(r[i].distribution[j * 2 + 1], 16) + parseInt(r[i].distribution[j * 2 + 1], 16), 16);
			else
				tablePass += "\t" + parseInt(parseInt(c[j].distribution[i], 16) + parseInt(c[j].distribution[i + 1], 16), 16);
		}
		tablePass += "\n";
	}
	return tablePass;
}

function DoCluster(windowID) {

	var params = {};
	params.e = '1';
	params.g = '1';
	params.m = 'a';
	params.ge = '2';
	submitCluster(prepareDataForCluster(windowID), params, windowID);

}

function DoAdvancedCluster(windowID) {
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
				style : 'padding-top:4px;'
			},
			items : [{
				id : 'cluster_radio',
				xtype : 'radiogroup',
				columns : 1,
				items : [{
					boxLabel : 'Protein Families',
					name : 'val',
					inputValue : 2
				}, {
					boxLabel : 'Genomes',
					name : 'val',
					inputValue : 1
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
					var params = {};
					params.g = (Ext.getCmp('cluster_radio').getValue()['val'] == 3 || Ext.getCmp('cluster_radio').getValue()['val'] == 1) ? '1' : '0';
					params.e = (Ext.getCmp('cluster_radio').getValue()['val'] == 3 || Ext.getCmp('cluster_radio').getValue()['val'] == 2) ? '1' : '0';
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

function submitCluster(data, params, windowID) {

	var pk = +Date.now();
	var threshold = 1000000;
	if (data.length > threshold) {
		var place = 0;
		var sendData = [];

		for (var i = 0; i < data.length; i += threshold) {
			var end = i + threshold;

			if (end > data.length)
				end = i + data.length % threshold;

			sendData[place] = data.substring(i, end);
			place++;
		}

		for (var i = 0; i < place; i++) {
			if (i == place - 1)
				setTimeout(sendDataChunks(sendData[i], params, pk, 'Run', windowID), 1000);
			else if (i == 0)
				sendDataChunks(sendData[i], params, pk, 'Store', windowID);
			else
				setTimeout(sendDataChunks(sendData[i], params, pk, 'Store', windowID), 1000);
		}
	} else {
		sendDataChunks(data, params, pk, 'Run', windowID);
	}
}

function sendDataChunks(data, params, pk, action, windowID) {
	var stateObject = getStateObject(windowID);
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
				var decoded = Ext.JSON.decode(rs.responseText);
				if (stateObject.heatmapAxis == "") {
					stateObject.ClusterRowOrder = decoded.rows;
					stateObject.ClusterColumnOrder = decoded.columns;
				} else {
					stateObject.ClusterRowOrder = decoded.columns;
					stateObject.ClusterColumnOrder = decoded.rows;
				}
				updateFilterState(windowID, stateObject);
				drawOrthoGroupTable(windowID);
				Ext.get("sample-layout").unmask();
			}
		}
	});

}

function submitFigfam(windowID, groupId) {
	var stateObject = getStateObject(windowID);

	Ext.Ajax.request({
		url : '/portal/portal/patric/SingleFIGfam/SingleFIGfamWindow?action=b&cacheability=PAGE',
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "saveState",
			gid : stateObject.genomeIds.join('##'),
			figfam : groupId
		},
		success : function(rs) {
			//document.location.href = "FIGfamViewer" + ((!stateObject.cType) ? "" : "B") + "?cType=" + stateObject.cType + "&cId=" + stateObject.cId + "&pk=" + rs.responseText;
			document.location.href = "SingleFIGfam?cType=" + stateObject.cType + "&cId=" + stateObject.cId + "&pk=" + rs.responseText;
		}
	});
}

function submitDetails(windowID, groupIds, genomeIds) {
	var stateObject = getStateObject(windowID);
	Ext.Ajax.request({
		url : '/portal/portal/patric/SingleFIGfam/SingleFIGfamWindow?action=b&cacheability=PAGE',
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "saveState",
			gid : genomeIds.replace(/,/g, '##'),
			figfam : groupIds.replace(/,/g, '##')
		},
		success : function(rs) {
			//open_in_new_tab("FIGfamViewer" + ((!stateObject.cType) ? "" : "B") + "?cType=" + stateObject.cType + "&cId=" + stateObject.cId + "&pk=" + rs.responseText);
			open_in_new_tab("SingleFIGfam?cType=" + stateObject.cType + "&cId=" + stateObject.cId + "&pk=" + rs.responseText);
		}
	});
}

function launchDetails(submitId, groupIds, genomeIds) {
	var toSubmit = document.getElementById(submitId);
	toSubmit.figfam_names.value = groupIds;
	toSubmit.genome_ids.value = genomeIds;
	toSubmit.submit();
}

function OrthoDetails(genomeName, locus, length, description) {
	this.genomeName = genomeName;
	this.length = length;
	this.description = description;
	this.locus = locus;
}

function getOrthoAlignment(windowID, groupID) {
	var stateObject = getStateObject(windowID);
	var showIn = window.open("", "", "height=320,width=800,scrollbars");
	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "projectName",
			figfamId : groupID,
			genomeIds : idText
		},
		success : function(rs) {
			popOrthoHtml(rs, showIn);
		}
	});

}

function setAllDetails(windowID, fileType) {
	var tableData = filterOrthoTableData(windowID);
	if (0 < tableData.length) {
		var toSet = document.getElementById('detailsToFile');
		toSet.detailsType.value = fileType;
		var idText = getStateObject(windowID).genomeIds.join(" OR ");
		toSet.detailsGenomes.value = idText;
		var figList = tableData[0][7];

		for (var i = 1; i < tableData.length; i++) {
			figList += ' OR ' + tableData[i][7];
		}
		toSet.detailsFigfams.value = figList;
		toSet.submit();
	}
}

function setTopOrtho(windowID, fileType) {
	// get the data for the table
	var tableData = filterOrthoTableData(windowID);

	var tablePass = "Id\tMembers\tSpecies\tProduct Description\tMin\tMax\tMean\tStd\n";

	var column_order = [7, 0, 1, 2, 3, 4, 5, 6];

	for (var i = 0; i < tableData.length; i++) {
		var rowData = tableData[i];
		tablePass += rowData[column_order[0]];

		for (var j = 1; j < column_order.length; j++) {
			tablePass += "\t" + rowData[column_order[j]];
		}

		tablePass += "\n";
	}

	var toSet = document.getElementById('orthoToFile');
	toSet.OrthoFileName.value = 'groups';
	toSet.OrthoFileType.value = fileType;
	toSet.data.value = tablePass;

	toSet.submit();
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

function getSelectedFeatures(windowID, actiontype, showdownload, fastatype, to) {
	var gridObject = getScratchObject(windowID), stateObject = getStateObject(windowID), sl = gridObject.checkbox.getSelections(), object = {
		'figfam_id' : []
	};

	for (var i = 0; i < sl.length; i++) {
		object["figfam_id"].push((sl[i]).get('groupId'));
	}

	object["gid"] = stateObject.genomeIds.join('##');
	object["figfam_id"] = object["figfam_id"].join("##");

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getFeatureIds",
			keyword : constructKeyword(object, "Feature")
		},
		success : function(rs) {
			processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, rs);
		}
	});
}

function processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, rs) {
	var stateObject = getStateObject(windowID);
	if (actiontype == "cart") {
		Ext.getDom("fids").value = rs.responseText;
		setGroupPopup(windowID);
	} else if (actiontype == "fasta") {
		catchFastaIds(windowID, showdownload, fastatype, rs);
	} else if (actiontype == "msa") {
		/*var hrefBase = 'TreeAlignerB?cType=' + stateObject.cType + "&cId=" + stateObject.cId + "&";
		if (windowID.indexOf('FIGfamSorterB') < 0) {
			hrefBase = 'TreeAligner?';
		}*/
		var hrefBase = 'MSA?cType=' + stateObject.cType + "&cId=" + stateObject.cId + "&";
		catchAlignIds(rs.responseText.split(",").length, stateObject.serveURL, hrefBase, rs);
	} else if (actiontype == "idmap") {
		Ext.Ajax.request({
			url : "/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE",
			method : 'POST',
			timeout : 600000,
			params : {
				keyword : rs.responseText,
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
				feature_info_id : rs.responseText,
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

function catchFastaIds(windowID, actionType, fastaType, ajaxHttp) {
	var toSubmit = document.getElementById(windowID + "_fTableForm");
	toSubmit.action = "/patric-common/jsp/fasta_download_handler.jsp";
	toSubmit.fastaaction.value = actionType;
	toSubmit.fastascope.value = "Selected";
	toSubmit.fastatype.value = fastaType;
	toSubmit.fids.value = ajaxHttp.responseText;
	if (actionType == "display") {
		toSubmit.target = "disp";
	} else {
		toSubmit.target = "";
	}
	toSubmit.submit();
	adjustCheckBoxes("unchecked");
}

function submitIDMapping(windowID, to) {

	var gridObject = getScratchObject(windowID);
	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "idmap", "", "", to);
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}

}

function submitEnrichment(windowID) {
	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "pathway_enrichment", "", "", "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function submitAlign(windowID) {
	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "msa", "", "", "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function catchAlignIds(figfamCount, serveURL, hrefBase, ajaxHttp) {
	var featureList = (ajaxHttp.responseText).split(',');
	if (featureList.length <= 262) {
		Ext.Ajax.request({
			url : serveURL,
			method : 'POST',
			timeout : 600000,
			params : {
				callType : "toAligner",
				featureIds : ajaxHttp.responseText
			},
			success : function(rs) {
				//relocate to result page
				document.location.href = hrefBase + "pk=" + rs.responseText;
			}
		});
	} else {
		Ext.Msg.alert(featureList.length + ' proteins selected!', 'Current resources can not handle this many proteins in ' + featureList.length + ' proteins.');
	}
}

function DownloadHeatmap(windowID) {
	document.getElementById(windowID + '_HeatmapData_form').data.value = JSON.stringify(currentData);
	document.getElementById(windowID + '_HeatmapData_form').submit();
}
