var idForHeatmap = null;

/**
 * Flash calls this function when it has completed its loading and
 * initialization.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns void
 */
function flashReady(flashObjectID) {
	(jQuery(heatmapid)[0]).refreshData();
}

/**
 * Flash calls this function when column order has changed.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns void
 */
function flashChangedColumnOrder(flashObjectID) {
	/*
	 * var gridObject = getScratchObject(idForHeatmap); var stateObject =
	 * getStateObject(idForHeatmap);
	 *
	 * stateObject.columnOrder = (stateObject.heatmapAxis ==
	 * "Transpose")?this.rowOrderInFlash(jQuery(heatmapid)[0]):this.columnOrderInFlash(jQuery(heatmapid)[0]);
	 *
	 * gridObject.gridState.columnOrder = stateObject.columnOrder;
	 *
	 * gridObject.gridState.storeInSession();
	 *
	 */

}

/**
 * Flash calls this function when row order has changed.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns void
 */
function flashChangedRowOrder(flashObjectID) {
	/*
	 * var gridObject = getScratchObject(idForHeatmap); var stateObject =
	 * getStateObject(idForHeatmap);
	 *
	 * stateObject.rowOrder = (stateObject.heatmapAxis ==
	 * "Transpose")?this.columnOrderInFlash(jQuery(heatmapid)[0]):this.rowOrderInFlash(jQuery(heatmapid)[0]);
	 *
	 * gridObject.gridState.rowOrder = stateObject.rowOrder;
	 *
	 * gridObject.gridState.storeInSession();
	 */
}

/**
 * Flash calls this function when columns have been hidden.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns void
 */
function flashColumnsHidden(flashObjectID) {
	alert("Flash " + flashObjectID + " hid some columns.");
}

/**
 * Flash calls this function when rows have been hidden.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns void
 */
function flashRowsHidden(flashObjectID) {
	alert("Flash " + flashObjectID + " hid some rows.");
}

/**
 * Flash calls this function when requesting detail for a specific heatmap cell.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @param colID
 *            the colID that was clicked.
 * @param rowID
 *            the rowID that was clicked.
 * @returns void
 */
function flashCellClicked(flashObjectID, colID, rowID) {

	var temp = checkAxis(colID, rowID), colID = temp.cols, rowID = temp.rows, buttonList = [], showDetails = null, putInCart = null, downloadF = null, download = null, discard = null, text = "", clickPop = null, i;

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
			text : 'Excel file (.xlsx)',
			icon : '/patric/images/toolbar_excel.png',
			handler : function() {
				MediatorDownload(rowID, colID, 'xlsx');
				clickPop.hide();
			}
		}]
	});

	downloadF = new Ext.Button({
		text : 'Download Genes',
		ctCls : 'x-btn-over',
		xtype : 'splitbutton',
		menu : [{
			text : 'Text File (.txt)',
			icon : '/patric/images/toolbar_text.png',
			handler : function() {
				MediatorDownloadF(colID, 'txt');
				clickPop.hide();

			}
		}, {
			text : 'Excel file (.xlsx)',
			icon : '/patric/images/toolbar_excel.png',
			handler : function() {
				MediatorDownloadF(colID, 'xlsx');
				clickPop.hide();
			}
		}]
	});

	showDetails = new Ext.Button({
		text : 'Show Genes',
		ctCls : 'x-btn-over',
		handler : function() {
			clickPop.hide();
			submittoFeatureTable(idForHeatmap, colID);
		}
	});

	putInCart = new Ext.Button({
		text : 'Add Genes To Group',
		ctCls : 'x-btn-over',
		handler : function() {
			clickPop.hide();
			Ext.getDom("fids").value = colID;
			addSelectedItems("Feature");
		}
	});

	discard = new Ext.Button({
		text : 'Cancel',
		ctCls : 'x-btn-over',
		handler : function() {
			clickPop.hide();
		}
	});

	for ( i = 0; i < rows.length; i++) {
		if (rows[i].rowID == rowID)
			text += '<b>Sample ID : </b> ' + rows[i].meta.samples + " - " + rows[i].rowLabel + " - " + rows[i].meta.timepoint + " - " + rows[i].meta.mutant + " - " + rows[i].meta.strain + " - " + rows[i].meta.condition + "<br/>";
	}

	for ( i = 0; i < cols.length; i++) {
		if (cols[i].colID == colID)
			text += '<b>Product : </b> ' + cols[i].colLabel;
	}

	buttonList.push(download);
	buttonList.push(downloadF);
	buttonList.push(showDetails);
	buttonList.push(putInCart);
	buttonList.push(discard);

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
 * Flash calls this function when a set of cells has been selected.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @param affectedRows
 *            {array} the rowIDs of the selected rows
 * @param affectedColumns
 *            {array} the colIDs of the selected columns
 * @returns void
 */
function flashCellsSelected(flashObjectID, columns, rows) {

	if (0 < rows.length && 0 < columns.length) {

		var temp = checkAxis(columns, rows), columns = temp.cols, rows = temp.rows, buttonList = [], showDetails = null, putInCart = null, download = null, downloadF = null, selectPop = null, discard = null, text = "";

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
			text : 'Download Genes',
			ctCls : 'x-btn-over',
			xtype : 'splitbutton',
			menu : [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					MediatorDownloadF(columns.join(","), 'txt');
					selectPop.hide();

				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					MediatorDownloadF(columns.join(","), 'xlsx');
					selectPop.hide();
				}
			}]
		});

		showDetails = new Ext.Button({
			text : 'Show Genes',
			ctCls : 'x-btn-over',
			handler : function() {
				selectPop.hide();
				submittoFeatureTable(idForHeatmap, columns.join(","));
			}
		});

		putInCart = new Ext.Button({
			text : 'Add Genes To Group',
			ctCls : 'x-btn-over',
			handler : function() {
				selectPop.hide();
				Ext.getDom("fids").value = columns.join(",");
				addSelectedItems("Feature");
			}
		});

		discard = new Ext.Button({
			text : 'Cancel',
			ctCls : 'x-btn-over',
			handler : function() {
				selectPop.hide();
			}
		});

		buttonList.push(download);
		buttonList.push(downloadF);
		buttonList.push(showDetails);
		buttonList.push(putInCart);
		buttonList.push(discard);

		text = '<b>Number of comparisons selected: </b> ' + rows.length + '<br />' + '<b>Number of features selected: </b> ' + columns.length;

		selectPop = new Ext.Window({
			html : text,
			layout : 'fit',
			width : 680,
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

function MediatorDownloadF(columns, type) {

	var toSubmit = document.getElementById("fTableForm_Feature");
	toSubmit.featureIds.value = columns;
	toSubmit._fileformat.value = type;
	toSubmit.action = "/patric-transcriptomics/jsp/GetDetailTable.jsp";
	toSubmit.target = "";
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
							distribution += cols[j].meta['labels'].split("|")[l] + "\t";
						}
					}
				}
				if (i == 0)
					data += "Comparisons/Genes\t" + column + "\n" + cols[j].colLabel + "\t" + distribution + "\n";
				else
					data += cols[j].colLabel + "\t" + distribution + "\n";
			}
		}

	}

	Ext.getDom("fTableForm_Cell").action = "/patric-transcriptomics/jsp/GetDetailTable.jsp";
	Ext.getDom("_data").value = data;
	Ext.getDom("_fileformat").value = type;
	Ext.getDom("fTableForm_Cell").target = "";
	Ext.getDom("fTableForm_Cell").submit();

}

function MediatorDownload(rowID, colID, type) {

	var data = "", distribution, column, j, l;

	for ( j = 0; j < cols.length; j++) {
		if (colID == cols[j].colID) {
			distribution = "", column = "";
			for ( l = 0; l < rows.length; l++) {
				if (rowID == rows[l].rowID) {
					column += rows[l].rowLabel + "\t";
					distribution += parseInt(cols[j].distribution.charAt(l * 2) + cols[j].distribution.charAt(l * 2 + 1), 16) + "\t";
				}
			}
			data = "Comparisons/Genes\t" + column + "\n" + cols[j].colLabel + "\t" + distribution;
		}
	}

	Ext.getDom("fTableForm_Cell").action = "/patric-transcriptomics/jsp/GetDetailTable.jsp";
	Ext.getDom("_data").value = data;
	Ext.getDom("_fileformat").value = type;
	Ext.getDom("fTableForm_Cell").target = "";
	Ext.getDom("fTableForm_Cell").submit();

}

function flipAxises(windowID) {

	var r = currentData.rows, c = currentData.columns, which_meta_field = "labels", temp_rows = new Array(), temp_columns = new Array(), temp_dists = new Array(), meta = new Array(), distribution, first, split, metafield, i, j, temp;

	for ( j = 0; j < r.length; j++) {
		temp_dists[j] = "";
		meta[j] = "";

		for ( i = 0; i < c.length; i++) {
			distribution = c[i].distribution;
			first = distribution[j * 2] ? distribution[j * 2] : "0", second = distribution[j * 2 + 1] ? distribution[j * 2 + 1] : "0";
			split = c[i].meta[which_meta_field].split("|");
			metafield = split[j] ? split[j] : null;
			temp_dists[j] += first + second;
			meta[j] += "|";
			if (metafield)
				meta[j] += metafield;
		}
	}

	for ( i = 0; i < c.length; i++)
		temp_rows.push(new Row(i, c[i].colID, c[i].colLabel, c[i].labelColor, c[i].bgColor, c[i].meta));

	for ( i = 0; i < r.length; i++) {
		if (meta[i])
			r[i].meta[which_meta_field] = meta[i].substring(1, meta[i].length);
		temp_columns.push(new Column(i, r[i].rowID, r[i].rowLabel, temp_dists[i], r[i].labelColor, r[i].bgColor, r[i].meta));
	}

	currentData.rows = temp_rows, currentData.columns = temp_columns, temp = currentData.colTrunc, currentData.colTrunc = currentData.rowTrunc, currentData.rowTrunc = temp, temp = currentData.colLabel, currentData.colLabel = currentData.rowLabel, currentData.rowLabel = temp, temp_rows = [], temp_columns = [], temp_dists = [];
}

function checkAxis(columns, rows) {
	var temp = {};

	temp.rows = (getStateObject(idForHeatmap).heatmapAxis == "") ? rows : columns;
	temp.cols = (getStateObject(idForHeatmap).heatmapAxis == "") ? columns : rows;

	return temp;

}

/**
 * Flash calls this function to retrieve updated data
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @returns {DataSet} A dataset representing the current state of the heatmap
 */
function flashRequestsData(flashObjectID) {

	return currentData;
}

/**
 * Flash calls this function when a row has been selected.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @param colID
 *            {int} the colID of the selected row.
 */
function flashColSelected(flashObjectID, colID) {

}

/**
 * Flash calls this function when a row has been selected.
 *
 * @param flashObjectID
 *            {String} the HTML ID of the Flash item making the request
 * @param rowID
 *            {int} the rowID of the selected row.
 */
function flashRowSelected(flashObjectID, rowID) {

}

/**
 * Use this to notify the heatmap that it should refresh the data at the next
 * convienient moment.
 *
 * @param whichObject
 *            {Element} the DOM object representing the target Flash component
 * @returns void
 */
function flashShouldRefreshData(whichObject) {
	// console.log(whichObject);
	(jQuery(heatmapid)[0]).refreshData();
}

/**
 * Use this to retrieve Flash's current row order.
 *
 * @param whichObject
 *            {Element} the DOM object representing the target Flash component
 * @returns {array} a list of rowIDs in the order currently displayed in Flash.
 * @see flashChangedRowOrder
 */
function rowOrderInFlash(whichObject) {
	return whichObject.rowOrder();
}

/**
 * Use this to retrieve Flash's current column order.
 *
 * @param whichObject
 *            {Element} the DOM object representing the target Flash component
 * @returns {array} a list of colIDs in the order currently displayed in Flash.
 * @see flashChangedColumnOrder
 */
function columnOrderInFlash(whichObject) {
	return whichObject.columnOrder();
}

// Use this to retrieve the current label visibility status in Flash (in case
// you haven't been keeping up with notifications).
// *retrieves from Flash:* `labelStatus` an object that the status of heatmap
// labels (isRowLabelVisible | isColLabelVisible) . "True" means "visible."
function labelVisibilityInFlash(whichObject) {
	return whichObject.labelVisibilty();
}

// Flash calls this function when label view states change -- both row and
// column.
// `labelStatus` an object that the status of heatmap labels (isRowLabelVisible
// | isColLabelVisible) . "True" means "visible."
function flashLabelStateChanged(flashObjectID, labelStatus) {
	// jQuery("#rowLabelVisReport").text((labelStatus.isRowLabelVisible) ?
	// "visible" : "hidden");
	// jQuery("#colLabelVisReport").text((labelStatus.isColLabelVisible) ?
	// "visible" : "hidden");
}

// Use this to retrieve the current display state of the flash component,
// including zoom levels and
// scroll positions.
function displayStateOfFlash(whichObject) {
	return whichObject.getDisplayState();
}

// ### Functions that send brief data to the flash component

// Change which cells are highlighted. Send an empty array to clear existing
// highlights.
function updateHighlightedCellsInFlash(whichObject, newHighlights) {
	return whichObject.updateFlaggedCells(newHighlights);
}

function updateDisplayStateInFlash(whichObject, stateObject) {
	return whichObject.updateDisplayState(stateObject);
}
