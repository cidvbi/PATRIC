/**
	Flash calls this function when it has completed its loading and initialization.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@returns void
 */
function flashReady(flashObjectID) {
	flashShouldRefreshData(jQuery(heatmapid)[0]);
}

/**
	Flash calls this function when column order has changed.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@returns void
 */
function flashChangedColumnOrder(flashObjectID) {
	//alert("Flash "+ flashObjectID +"changed column order.");
}

/**
	Flash calls this function when row order has changed.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@returns void
 */
function flashChangedRowOrder(flashObjectID) {
	//alert("Flash "+ flashObjectID +"changed row order.");
}

/**
	Flash calls this function when columns have been hidden.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@returns void
 */
function flashColumnsHidden(flashObjectID) {
	//alert("Flash "+ flashObjectID +" hid some columns.");
}

/**
	Flash calls this function when rows have been hidden.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@returns void
 */
function flashRowsHidden(flashObjectID) {
	//alert("Flash "+ flashObjectID +" hid some rows.");
}

/**
	Flash calls this function when requesting detail for a specific heatmap cell.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@param colID the colID that was clicked.
	@param rowID the rowID that was clicked.
	@returns void
 */
function flashCellClicked(flashObjectID, colID, rowID) {
	var algorithm = Ext.getDom("algorithm").value,
		temp;	
			
	if(algorithm == "PATRIC")
		algorithm = "RAST";
	else if(algorithm == "Legacy BRC" || algorithm == "BRC")
		algorithm = "Curation";

	if(axis == "Transpose"){
		temp = colID;
		colID = rowID;
		rowID = temp;
	}
	
	Ext.Ajax.request({
		url: "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		method: 'GET',
		params: {cId:colID, cType:'genome', map:"'"+Ext.getDom("map").value+"'", algorithm:"'"+algorithm+"'", ec_number: "'"+rowID+"'"},
		success: function(rs) {
  			catchLocusTags(colID, rowID, eval(Ext.JSON.decode(rs.responseText)));
		}
	});
	
}
	
function catchLocusTags(colID, rowID, responseText) {
	
	var locusList =[],
		na_idList =[],
		i,
		parts = getCellData_ECGenome(colID, rowID),
		algorithm = Ext.getDom("algorithm").value,
		memberCount = responseText.genes.length,
		buttonList = [],
		showDetails = null,
		download = null,
		putInCart = null,
		downloadF = null,
		discard = null, 
		text = "",
		colID = colID,
		rowID = rowID;
		
	for(i=0;i<responseText.genes.length; i++){
		locusList.push(responseText.genes[i].locustags);
		na_idList.push(responseText.genes[i].genes);
	}
			
	if(algorithm == "PATRIC")
		algorithm = "RAST";
	else if(algorithm == "Legacy BRC" || algorithm == "BRC")
		algorithm = "Curation";
	
	if (0 < memberCount) {
		putInCart =
			new Ext.Button({
				text: 'Add Proteins To Group',
				ctCls:'x-btn-over'
			});
		
		download = new Ext.Button({
			text: 'Download Heatmap Data',
			ctCls:'x-btn-over',
			xtype:'splitbutton',
    		menu: [{text: 'Text File (.txt)',
			    icon: '/patric/images/toolbar_text.png',
			 	handler: function(){
			 		MediatorDownload(original_ec, colID, 'txt');
			 		Ext.getCmp('clickPop').hide();
	 				}
		 		}, 
		 		{text: 'Excel file (.xls)',
		 		icon: '/patric/images/toolbar_excel.png',
		 		handler: function(){
		 			MediatorDownload(original_ec, colID, 'xls');
		 			Ext.getCmp('clickPop').hide();
		 			}
		 		}
		 	]
		});
		
		showDetails = new Ext.Button({
			text: 'Show Proteins',
			ctCls:'x-btn-over'
		});
		
		downloadF = new Ext.Button({
			text: 'Download Proteins',
			ctCls:'x-btn-over',
			xtype:'splitbutton',
			menu: [{text: 'Text File (.txt)',
			    icon: '/patric/images/toolbar_text.png',
			 	handler: function(){
			 		MediatorDownloadF("'"+rowID +"'", colID, 'txt');
			 		Ext.getCmp('clickPop').hide();
	 				}
		 		}, 
		 		{text: 'Excel file (.xls)',
		 		icon: '/patric/images/toolbar_excel.png',
		 		handler: function(){
		 			MediatorDownloadF("'"+rowID +"'", colID, 'xlsx');
		 			Ext.getCmp('clickPop').hide();
		 			}
		 		}
		 	]
		});
		
		buttonList.push(download);
		buttonList.push(downloadF);
		buttonList.push(showDetails);
		buttonList.push(putInCart);
	}
	
	discard = new Ext.Button({
		text: 'Cancel',
		ctCls:'x-btn-over'
	});
	
	buttonList.push(discard);
	
	text += '<b>Genome: </b> ' + parts[1] + '<br />'+
		'<b>Product: </b> ' + parts[0] + '<br />' +
		'<b>EC Number: </b> ' + rowID + '<br />' +
		'<b>Members: </b> ' + responseText.genes.length;
			
	for(i = 0; i < locusList.length; i++) {
		text += "<br />" + locusList[i];
	}
	
	if(Ext.getCmp('clickPop'))
		Ext.getDom('clickPop-body').innerHTML = text;
	else
		Ext.create('Ext.Window', {
			id: 'clickPop',
			html: text,
			layout:'fit',
			width:640,
			height:300,
			closeAction:'hide',
			plain: true,
			modal:true,
			shim: false, 
			autoScroll: true,
			title: 'Selected Cell from Heatmap',
			buttons: buttonList
		});
	
	if (0 < memberCount) {
		putInCart.on('click', function() {
			Ext.getCmp('clickPop').hide();
			addSelectedItems("Feature");
			Ext.getDom("fids").value = na_idList.join(",");
         });
		
		showDetails.on('click',	function() {
			Ext.getCmp('clickPop').hide();
			Ext.Ajax.request({
				url: "/portal/portal/patric/PathwayTableSingle/PathwayTableSingleWindow?action=b&cacheability=PAGE",
				method: 'POST',
				params: {cId:colID, cType:'genome', map:Ext.getDom("map").value, algorithm:"'"+algorithm+"'", ec_number: "'"+rowID +"'", callType:'savetopk'},
				success: function(rs) {
					document.location.href = "/portal/portal/patric/CompPathwayTable?cType=genome&cId="+colID+"&algorithm="+algorithm+"&ec_number="+rowID;
				}
			});	
		});
	}
	
	discard.on('click', function() {
		Ext.getCmp('clickPop').hide();
	});
	
	Ext.getCmp('clickPop').show();
}

/**
	Flash calls this function when a set of cells has been selected.
	@param flashObjectID {String} the HTML ID of the Flash item making the request
	@param affectedRows {array} the rowIDs of the selected rows
	@param affectedColumns {array} the colIDs of the selected columns
	@returns void
 */
function flashCellsSelected(flashObjectID, affectedColumns, affectedRows) {
		
	var algorithm = Ext.getDom("algorithm").value,
		selectEcs = [],
		selectGenomes = [],
		i,
		temp = null;
	
	if(axis == "Transpose"){
		temp = affectedColumns.splice(0);
		affectedColumns = affectedRows.splice(0);
		affectedRows = temp;
	}
	
	if(algorithm == "PATRIC")
		algorithm = "RAST";
	else if(algorithm == "Legacy BRC")
		algorithm = "Curation";
	
	for (i=0; i < affectedRows.length; i++) {
		selectEcs.push("'"+affectedRows[i]+"'");
	}
	
	for (i=0; i < affectedColumns.length; i++) {
		selectGenomes.push(affectedColumns[i]);
	}
	
	Ext.Ajax.request({
		url: "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		method: 'GET',
		params: {cId:selectGenomes.join(","), cType:'genome', map:"'"+Ext.getDom("map").value+"'", algorithm:"'"+algorithm+"'", ec_number: selectEcs.join(",")},
		success: function(rs) {
			catchLocusTagsMultiple(affectedColumns, affectedRows, selectGenomes, selectEcs, eval(Ext.JSON.decode(rs.responseText)));  			
		}
	});	
}
	
function catchLocusTagsMultiple(affectedColumns, affectedRows, selectGenomes, selectEcs, members){
	
	var membersCount = members.genes.length,
		locusList =[],
		na_idList =[],
		i,
		algorithm = Ext.getDom("algorithm").value,
		buttonList = [],
		showDetails = null,
		putInCart = null,
		download = null,
		downloadF = null,
		discard,
		text = "";
		
	for(i=0;i<members.genes.length; i++){
		locusList.push(members.genes[i].locustags);
		na_idList.push(members.genes[i].genes);
	}
	
	
	if (0 < membersCount) {
		putInCart = new Ext.Button({
			text: 'Add Proteins To Group',
			ctCls:'x-btn-over'
		});
		
		showDetails = new Ext.Button({
			text: 'Show Proteins',
			ctCls:'x-btn-over'
		});
		
		download = new Ext.Button({
			text: 'Download Heatmap Data',
			ctCls:'x-btn-over',
			xtype:'splitbutton',
    		menu: [{text: 'Text File (.txt)',
			    icon: '/patric/images/toolbar_text.png',
			 	handler: function(){
			 		MediatorDownloadMultiple(affectedRows, affectedColumns, 'txt');
			 		Ext.getCmp('selectPop').hide();
	 			}
		 		},{text: 'Excel file (.xls)',
		 		icon: '/patric/images/toolbar_excel.png',
		 		handler: function(){
		 			MediatorDownloadMultiple(affectedRows, affectedColumns, 'xls');
		 			Ext.getCmp('selectPop').hide();
		 		}
		 	}]
		});
		
		downloadF = new Ext.Button({
			text: 'Download Proteins',
			ctCls:'x-btn-over',
			xtype:'splitbutton',
    		menu: [{text: 'Text File (.txt)',
			    icon: '/patric/images/toolbar_text.png',
			 	handler: function(){
			 		MediatorDownloadF(selectEcs, selectGenomes, 'txt');
			 		Ext.getCmp('selectPop').hide();	   		 		
	 			}
		 		},{text: 'Excel file (.xlsx)',
		 		icon: '/patric/images/toolbar_excel.png',
		 		handler: function(){
		 			MediatorDownloadF(selectEcs, selectGenomes, 'xlsx');
		 			Ext.getCmp('selectPop').hide();	    		 		
		 		}
		 	}]
		});
		
		buttonList.push(download);
		buttonList.push(downloadF);
		buttonList.push(showDetails);
		buttonList.push(putInCart);
	}

	discard = new Ext.Button({
		text: 'Cancel',
		ctCls:'x-btn-over'
	});
	buttonList.push(discard);
		
	text += '<b>Genomes selected: </b> ' + affectedColumns.length + '<br />'+
			'<b>EC Numbers selected: </b> ' + affectedRows.length + '<br />'+
			'<b>Members: </b> ' + membersCount;

	if(Ext.getCmp('selectPop'))
		Ext.getDom('selectPop-body').innerHTML = text;
	else
		Ext.create('Ext.Window', {
			id:'selectPop',
			html: text,
			layout:'fit',
			width:640,
			height:300,
			closeAction:'hide',
			plain: true,
			modal:true,
			shim: false, 
			autoScroll: true,
			title: 'Selected Area from Heatmap',
			buttons: buttonList
		});
	
	if (0 < membersCount) {
		
		putInCart.on('click', function() {
			Ext.getCmp('selectPop').hide();
			Ext.getDom("fids").value = na_idList;
			addSelectedItems("Feature");
        });
		
		showDetails.on('click', function() {
			Ext.getCmp('selectPop').hide();
			Ext.Ajax.request({
				url: "/portal/portal/patric/PathwayTableSingle/PathwayTableSingleWindow?action=b&cacheability=PAGE",
				method: 'POST',
				params: {cId:selectGenomes.join(","), cType:'genome', map:Ext.getDom("map").value, algorithm:"'"+algorithm+"'", ec_number: selectEcs.join(","), callType:'savetopk'},
				success: function(rs) {
					document.location.href = "/portal/portal/patric/PathwayTableSingleB?cType="+Ext.getDom("cType").value+"&cId="+Ext.getDom("cId").value+"&pk="+rs.responseText;		
				}
			});	
			
         });
	}
	
	discard.on('click', function() {
		Ext.getCmp('selectPop').hide();
	});
	
	Ext.getCmp('selectPop').show();

}

function MediatorDownloadF(selectEcs, selectGenomes, type){
	
	Ext.getDom("tablesource").value = "MapFeatureTable";	
	Ext.getDom("fMapForm").action = "/patric-pathways/jsp/grid_download_handler.jsp";
	Ext.getDom("ec_number").value = typeof selectEcs == "object"?selectEcs.join(","):selectEcs;
	Ext.getDom("genomeId").value = typeof selectGenomes == "object"?selectGenomes.join(","):selectGenomes;
	Ext.getDom("fileformat").value = type;
	Ext.getDom("fMapForm").target = "";
	Ext.getDom("fMapForm").submit();
	
}

function MediatorDownloadMultiple(affectedRows, affectedColumns,type){
	
	var data = "",
		distribution,
		column,
		i,
		j,
		k,
		l;
	
	for (i = 0; i < affectedRows.length; i++) {
		for(j=0; j < rows.length; j++){
			if(affectedRows[i] == rows[j].rowID){
				distribution = "";
				column = "";
				for (k = 0; k < affectedColumns.length; k++) {
					for (l = 0; l< cols.length; l++) {
						if(affectedColumns[k] == cols[l].colID){
							column += cols[l].colLabel+"\t";
							distribution += parseInt(cols[l].distribution.charAt(j*2)+cols[l].distribution.charAt(j*2+1), 16)+"\t";
						}
					}
				}
				if(i == 0)
					data += "EC Numbers/Genomes\t"+column+"\n"+rows[j].rowLabel+"\t"+distribution+"\n";
				else
					data += rows[j].rowLabel+"\t"+distribution+"\n";
			}
		}
	}	
	
	Ext.getDom("tablesource").value = "MapFeatureTable_Cell";
	Ext.getDom("fMapForm").action = "/patric-pathways/jsp/grid_download_handler.jsp";
	Ext.getDom("data").value = data;
	Ext.getDom("fileformat").value = type;
	Ext.getDom("fMapForm").target = "";
	Ext.getDom("fMapForm").submit();
	
}

function MediatorDownload(rowID, colID,type){
	
	var data = "",
		l,
		j,
		distribution,
		column;
	
	for(j=0; j < rows.length; j++){
		
		if(rowID == rows[j].rowID){
			
			distribution = "";
			column = "";
			
			for (l = 0; l< cols.length; l++) {
				if(colID == cols[l].colID){
					column += cols[l].colLabel+"\t";
					distribution += parseInt(cols[l].distribution.charAt(j*2)+cols[l].distribution.charAt(j*2+1), 16)+"\t";					
				}
			}		
			data = "EC Numbers/Genomes\t"+column+"\n"+rows[j].rowLabel+"\t"+distribution;
		}
	}
	
	Ext.getDom("tablesource").value = "MapFeatureTable_Cell";
	Ext.getDom("fMapForm").action = "/patric-pathways/jsp/grid_download_handler.jsp";
	Ext.getDom("data").value = data;
	Ext.getDom("fileformat").value = type;
	Ext.getDom("fMapForm").target = "";
	Ext.getDom("fMapForm").submit();
	
}

function flipAxises() {

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

function flashColSelected(flashObjectID,colID) {
	var algorithm = Ext.getDom("algorithm").value;	
	
	if(algorithm == "PATRIC")
		algorithm = "RAST";
	else if(algorithm == "Legacy BRC" || algorithm == "BRC")
		algorithm = "Curation";
		
		
	Ext.Ajax.request({
		url: "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		method: 'GET',
		params: {cId:colID, cType:'genome', map:"'"+Ext.getDom("map").value+"'", algorithm:"'"+algorithm+"'", ec_number: ""},
		success: function(rs) {
			catchLocusTagsMultiple("1", rows.length, colID, "", eval(Ext.util.JSON.decode(rs.responseText)));
		}
	});
}
	  
/**
  	Flash calls this function when a row has been selected.
  	@param flashObjectID {String} the HTML ID of the Flash item making the request
  	@param rowID {int} the rowID of the selected row.
*/
function flashRowSelected(flashObjectID,rowID) {
  	var algorithm = Ext.getDom("algorithm").value;	
	
	if(algorithm == "PATRIC")
		algorithm = "RAST";
	else if(algorithm == "Legacy BRC" || algorithm == "BRC")
		algorithm = "Curation";
		
		
	Ext.Ajax.request({
		url: "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		method: 'GET',
		params: {cType:'genome', map:"'"+Ext.getDom("map").value+"'", algorithm:"'"+algorithm+"'", ec_number:"'"+rowID+"'"},
		success: function(rs) {
			catchLocusTagsMultiple(cols.length, 1, rowID, "", eval(Ext.util.JSON.decode(rs.responseText)));  			
		}
	});
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


//Use this to retrieve the current label visibility status in Flash (in case you haven't been keeping up with notifications).  
//*retrieves from Flash:* `labelStatus`	an object that the status of heatmap labels (isRowLabelVisible | isColLabelVisible) . "True" means "visible."
function labelVisibilityInFlash(whichObject) {
	return whichObject.labelVisibilty();
}

//Flash calls this function when label view states change -- both row and column.  
//`labelStatus`	an object that the status of heatmap labels (isRowLabelVisible | isColLabelVisible) . "True" means "visible."  
function flashLabelStateChanged(flashObjectID,labelStatus) {
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
function updateHighlightedCellsInFlash(whichObject,newHighlights) {
	return whichObject.updateFlaggedCells(newHighlights);
}

function updateDisplayStateInFlash(whichObject,stateObject) {
	return whichObject.updateDisplayState(stateObject);
}