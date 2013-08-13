var idForHeatmap = null;

/**
 Flash calls this function when it has completed its loading and initialization.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns void
 */
function flashReady(flashObjectID) {
	(jQuery(heatmapid)[0]).refreshData();
}

/**
 Flash calls this function when column order has changed.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns void
 */
function flashChangedColumnOrder(flashObjectID) {
	/*
	 var gridObject = getScratchObject(idForHeatmap),
	 stateObject = getStateObject(idForHeatmap);

	 stateObject.ClusterColumnOrder = this.columnOrderInFlash(jQuery(heatmapid)[0]);
	 gridObject.gridState.storeInSession();
	 */
}

/**
 Flash calls this function when row order has changed.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns void
 */
function flashChangedRowOrder(flashObjectID) {
	/*
	 var gridObject = getScratchObject(idForHeatmap),
	 stateObject = getStateObject(idForHeatmap);

	 stateObject.ClusterRowOrder = this.rowOrderInFlash(jQuery(heatmapid)[0]);
	 gridObject.gridState.storeInSession();
	 */
}

/**
 Flash calls this function when columns have been hidden.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns void
 */
function flashColumnsHidden(flashObjectID) {
	// alert("Flash "+ flashObjectID +" hid some columns.");
}

/**
 Flash calls this function when rows have been hidden.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns void
 */
function flashRowsHidden(flashObjectID) {
	// alert("Flash "+ flashObjectID +" hid some rows.");
}

/**
 Flash calls this function when requesting detail for a specific heatmap cell.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @param colID the colID that was clicked.
 @param rowID the rowID that was clicked.
 @returns void
 */
function flashCellClicked(flashObjectID, colID, rowID) {

	var stateObject = getStateObject(idForHeatmap), temp = checkAxis(colID, rowID), colID = temp.cols, rowID = temp.rows;

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getLocusTags",
			figfamIds : colID,
			genomeIds : rowID,
			portlet_type : Ext.getDom("portlet_type").value
		},
		success : function(rs) {
			catchLocusTags(colID, rowID, rs);
		}
	});

}

function catchLocusTags(colID, rowID, ajaxHttp) {

	var locusList = (ajaxHttp.responseText).split("\t"), parts = getCellParts(idForHeatmap, colID, rowID), buttonList = [], showDetails = null, putInCart = null, downloadF = null, download = null, discard = null, text = "", i = 0, clickPop = null, memberCount = parts[2];

	if (0 < memberCount) {
		download = new Ext.Button({
			text : 'Download Heatmap Data',
			ctCls : 'x-btn-over',
			xtype : 'splitbutton',
			menu : [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					MediatorDownload(rowID, colID, 'txt');
					clickPop.hide();
				}
			}, {
				text : 'Excel file (.xls)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					MediatorDownload(rowID, colID, 'xls');
					clickPop.hide();
				}
			}]
		});
		downloadF = new Ext.Button({
			text : 'Download Proteins',
			ctCls : 'x-btn-over',
			xtype : 'splitbutton',
			menu : [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					MediatorDownloadF(rowID, colID, 'txt');
					clickPop.hide();
				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					MediatorDownloadF(rowID, colID, 'xlsx');
					clickPop.hide();
				}
			}]
		});
		showDetails = new Ext.Button({
			text : 'Show Proteins',
			ctCls : 'x-btn-over',
			handler : function() {
				clickPop.hide();
				submitDetails(idForHeatmap, colID, rowID);
			}
		});
		putInCart = new Ext.Button({
			text : 'Add Proteins To Group',
			ctCls : 'x-btn-over',
			handler : function() {
				clickPop.hide();
				addRectangleToGroup(idForHeatmap, colID, rowID);
			}
		});
		buttonList.push(download);
		buttonList.push(downloadF);
		buttonList.push(showDetails);
		buttonList.push(putInCart);
	}

	discard = new Ext.Button({
		text : 'Cancel',
		ctCls : 'x-btn-over',
		handler : function() {
			clickPop.hide();
		}
	});

	buttonList.push(discard);

	text = '<b>Genome: </b> ' + parts[0] + '<br />' + '<b>Product: </b> ' + parts[1] + '<br />' + '<b>Figfam ID: </b> ' + colID + '<br />' + '<b>Members: </b> ' + parts[2];

	for ( i = 0; i < locusList.length; i++) {
		text += "<br />" + locusList[i];
	}

	clickPop = new Ext.Window({
		html : text,
		layout : 'fit',
		width : 780,
		height : 300,
		closeAction : 'hide',
		plain : true,
		modal : true,
		shim : false,
		title : 'Selected Cell from Heatmap',
		buttons : buttonList
	});

	clickPop.show();
}

/**
 Flash calls this function when a set of cells has been selected.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @param affectedRows {array} the rowIDs of the selected rows
 @param affectedColumns {array} the colIDs of the selected columns
 @returns void
 */
function flashCellsSelected(flashObjectID, columns, rows) {

	if ((0 < rows.length) && (0 < columns.length)) {

		var temp = checkAxis(columns, rows), columns = temp.cols, rows = temp.rows, membersCount = getRectangleCount(idForHeatmap, columns, rows), selectFigs = columns[0], selectGenomes = rows[0], i = 0, buttonList = [], showDetails = null, putInCart = null, setSynteny = null, download = null, downloadF = null, discard = null, selectPop = null;

		for ( i = 1; i < columns.length; i++) {
			selectFigs += ',' + columns[i];
		}
		for ( i = 1; i < rows.length; i++) {
			selectGenomes += ',' + rows[i];
		}

		if (0 < membersCount) {

			download = new Ext.Button({
				text : 'Download Heatmap Data',
				ctCls : 'x-btn-over',
				xtype : 'splitbutton',
				menu : [{
					text : 'Text File (.txt)',
					icon : '/patric/images/toolbar_text.png',
					handler : function() {
						MediatorDownloadMultiple(rows, columns, 'txt');
						selectPop.hide();

					}
				}, {
					text : 'Excel file (.xls)',
					icon : '/patric/images/toolbar_excel.png',
					handler : function() {
						MediatorDownloadMultiple(rows, columns, 'xls');
						selectPop.hide();
					}
				}]
			});

			downloadF = new Ext.Button({
				text : 'Download Proteins',
				ctCls : 'x-btn-over',
				xtype : 'splitbutton',
				menu : [{
					text : 'Text File (.txt)',
					icon : '/patric/images/toolbar_text.png',
					handler : function() {
						MediatorDownloadF(selectGenomes, selectFigs, 'txt');
						selectPop.hide();

					}
				}, {
					text : 'Excel file (.xlsx)',
					icon : '/patric/images/toolbar_excel.png',
					handler : function() {
						MediatorDownloadF(selectGenomes, selectFigs, 'xlsx');
						selectPop.hide();
					}
				}]
			});

			showDetails = new Ext.Button({
				text : 'Show Proteins',
				ctCls : 'x-btn-over',
				handler : function() {
					selectPop.hide();
					submitDetails(idForHeatmap, selectFigs, selectGenomes);
				}
			});

			putInCart = new Ext.Button({
				text : 'Add Proteins To Group',
				ctCls : 'x-btn-over',
				handler : function() {
					selectPop.hide();
					addRectangleToGroup(idForHeatmap, selectFigs, selectGenomes);
				}
			});

			if (rows && rows.length == 1) {
				setSynteny = new Ext.Button({
					text : 'Sort Protein Families',
					ctCls : 'x-btn-over',
					handler : function() {
						selectPop.hide();
						setSyntenyOrder(idForHeatmap, parseInt(selectGenomes));
						Ext.get("sample-layout").mask('Loading...', 'x-mask-loading');
					}
				});
			}

			buttonList.push(download);
			buttonList.push(downloadF);
			buttonList.push(showDetails);
			if (rows && rows.length == 1) {
				buttonList.push(setSynteny);
			}
			buttonList.push(putInCart);
		}

		discard = new Ext.Button({
			text : 'Cancel',
			ctCls : 'x-btn-over',
			handler : function() {
				selectPop.hide();
			}
		});

		buttonList.push(discard);

		text = '<b>Genomes selected: </b> ' + rows.length + '<br />' + '<b>Figfams selected: </b> ' + columns.length + '<br />' + '<b>Members: </b> ' + membersCount;

		selectPop = new Ext.Window({
			html : text,
			layout : 'fit',
			width : ((rows) && (rows.length == 1)) ? 780 : 680,
			height : 300,
			closeAction : 'hide',
			plain : true,
			modal : true,
			shim : false,
			title : 'Selected Area from Heatmap',
			buttons : buttonList
		});

		selectPop.show();
	}
}

function MediatorDownloadF(selectGenomes, selectFigs, type) {

	var object = {}, toSubmit = document.getElementById("fTableForm_Feature");

	object["gid"] = selectGenomes.replace(/,/g, '##');
	object["figfam_id"] = selectFigs.replace(/,/g, '##');
	toSubmit.download_keyword.value = constructKeyword(object, "Feature");
	toSubmit.fileformat.value = type;
	toSubmit.submit();
}

function MediatorDownloadMultiple(affectedRows, affectedColumns, type) {

	var data = "", i, j, k, l, distribution, column;

	for ( i = 0; i < affectedColumns.length; i++) {
		for ( j = 0; j < cols.length; j++) {
			if (affectedColumns[i] == cols[j].colID) {
				distribution = "", column = "";
				for ( k = 0; k < affectedRows.length; k++) {
					for ( l = 0; l < rows.length; l++) {
						if (affectedRows[k] == rows[l].rowID) {
							column += rows[l].rowLabel + "\t";
							distribution += parseInt(cols[j].distribution.charAt(l * 2) + cols[j].distribution.charAt(l * 2 + 1), 16) + "\t";
						}
					}
				}
				if (i == 0)
					data += "Genomes/Protein Families\t" + column + "\n" + cols[j].colLabel + "\t" + distribution + "\n";
				else
					data += cols[j].colLabel + "\t" + distribution + "\n";
			}
		}
	}

	Ext.getDom("fTableForm_Cell").action = "/FigFamSorter/jsp/GetDetailTable.jsp";
	Ext.getDom("_data").value = data;
	Ext.getDom("_fileformat").value = type;
	Ext.getDom("fTableForm_Cell").target = "";
	Ext.getDom("fTableForm_Cell").submit();

}

function MediatorDownload(rowID, colID, type) {

	var data = "", j, l, distribution, column;

	for ( j = 0; j < cols.length; j++) {
		if (colID == cols[j].colID) {
			distribution = "", column = "";
			for ( l = 0; l < rows.length; l++) {
				if (rowID == rows[l].rowID) {
					column += rows[l].rowLabel + "\t";
					distribution += parseInt(cols[j].distribution.charAt(l * 2) + cols[j].distribution.charAt(l * 2 + 1), 16) + "\t";
				}
			}
			data = "Protein Families/Genomes\t" + column + "\n" + cols[j].colLabel + "\t" + distribution;
		}
	}

	Ext.getDom("fTableForm_Cell").action = "/FigFamSorter/jsp/GetDetailTable.jsp";
	Ext.getDom("_data").value = data;
	Ext.getDom("_fileformat").value = type;
	Ext.getDom("fTableForm_Cell").target = "";
	Ext.getDom("fTableForm_Cell").submit();
}

function flipAxises(windowID) {

	var r = currentData.rows, c = currentData.columns, temp_rows = new Array(), temp_columns = new Array(), temp_dists = new Array(), i, j, distribution, first, second, temp;

	for ( j = 0; j < r.length; j++) {
		temp_dists[j] = "";
		for ( i = 0; i < c.length; i++) {
			distribution = c[i].distribution;
			first = distribution[j * 2] ? distribution[j * 2] : "0";
			second = distribution[j * 2 + 1] ? distribution[j * 2 + 1] : "0";
			temp_dists[j] += first + second;
		}
	}

	for ( i = 0; i < c.length; i++)
		temp_rows.push(new Row(i, c[i].colID, c[i].colLabel, c[i].labelColor, c[i].bgColor, c[i].meta));

	for ( i = 0; i < r.length; i++) {
		temp_columns.push(new Column(i, r[i].rowID, r[i].rowLabel, temp_dists[i], r[i].labelColor, r[i].bgColor, r[i].meta));
	}

	currentData.rows = temp_rows, currentData.columns = temp_columns, temp = currentData.colTrunc, currentData.colTrunc = currentData.rowTrunc, currentData.rowTrunc = temp, temp = currentData.colLabel, currentData.colLabel = currentData.rowLabel, currentData.rowLabel = temp;
}

function checkAxis(columns, rows) {
	var temp = {};

	temp.rows = (getStateObject(idForHeatmap).heatmapAxis == "") ? rows : columns;
	temp.cols = (getStateObject(idForHeatmap).heatmapAxis == "") ? columns : rows;

	return temp;

}

/**
 Flash calls this function to retrieve updated data
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @returns {DataSet} A dataset representing the current state of the heatmap
 */
function flashRequestsData(flashObjectID) {
	return currentData;
}

/**
 Flash calls this function when a row has been selected.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @param colID {int} the colID of the selected row.
 */
function flashColSelected(flashObjectID, colID) {
}

/**
 Flash calls this function when a row has been selected.
 @param flashObjectID {String} the HTML ID of the Flash item making the request
 @param rowID {int} the rowID of the selected row.
 */
function flashRowSelected(flashObjectID, rowID) {
}

/**
 Use this to notify the heatmap that it should refresh the data at the
 next convienient moment.
 @param whichObject {Element} the DOM object representing the target Flash component
 @returns void
 */
function flashShouldRefreshData(whichObject) {
	whichObject.refreshData();
}

/**
 Use this to retrieve Flash's current row order.
 @param whichObject {Element} the DOM object representing the target Flash component
 @returns {array} a list of rowIDs in the order currently displayed in Flash.
 @see flashChangedRowOrder
 */
function rowOrderInFlash(whichObject) {
	return whichObject.rowOrder();
}

/**
 Use this to retrieve Flash's current column order.
 @param whichObject {Element} the DOM object representing the target Flash component
 @returns {array} a list of colIDs in the order currently displayed in Flash.
 @see flashChangedColumnOrder
 */
function columnOrderInFlash(whichObject) {
	return whichObject.columnOrder();
}

// Use this to retrieve the current label visibility status in Flash (in case you haven't been keeping up with notifications).
// *retrieves from Flash:* `labelStatus`	an object that the status of heatmap labels (isRowLabelVisible | isColLabelVisible) . "True" means "visible."
function labelVisibilityInFlash(whichObject) {
	return whichObject.labelVisibilty();
}

// Flash calls this function when label view states change -- both row and column.
// `labelStatus`	an object that the status of heatmap labels (isRowLabelVisible | isColLabelVisible) . "True" means "visible."
function flashLabelStateChanged(flashObjectID, labelStatus) {
	//jQuery("#rowLabelVisReport").text((labelStatus.isRowLabelVisible) ? "visible" : "hidden");
	//jQuery("#colLabelVisReport").text((labelStatus.isColLabelVisible) ? "visible" : "hidden");
}

//Use this to retrieve the current display state of the flash component, including zoom levels and
//scroll positions.
function displayStateOfFlash(whichObject) {
	return whichObject.getDisplayState();
}

//### Functions that send brief data to the flash component

//Change which cells are highlighted. Send an empty array to clear existing highlights.
function updateHighlightedCellsInFlash(whichObject, newHighlights) {
	return whichObject.updateFlaggedCells(newHighlights);
}

function updateDisplayStateInFlash(whichObject, stateObject) {
	return whichObject.updateDisplayState(stateObject);
}
