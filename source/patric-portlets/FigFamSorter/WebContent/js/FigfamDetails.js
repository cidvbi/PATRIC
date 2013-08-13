function FigfamDetailsStateObject(windowID, serveResource, getContextPath) {
	// save initial values
	this.windowID = windowID;
	this.serveURL = serveResource;
	this.contextPath = getContextPath;
	this.famType = '';
	this.figfamNames = '';
	this.description = '';
	this.cId = '';
	this.cType = '';
	this.figfamCount = 0;
	this.figIdMap = null;
	// set duration at 30 days or desired value
	this.duration = defaultDuration;

	this.loadData = FigfamDetailsLoadData;
	this.saveData = FigfamDetailsSaveData;
	this.renderWindow = false;
}

// create a method for the required saveData link
function FigfamDetailsSaveData(windowID, namespace) {
}

function FigfamDetailsLoadData(namespace) {
}

function FigfamDetailsOnReady(windowID, resourceURL, contextPath, figfamNames, idText, cType, cId, famType) {
	// register windowID to insure that state values gets stored to a cookie
	//   on page exits or refreshes
	addWindowID(windowID);

	// create a default state object with the critical values
	//   provided by the server
	var stateObject = new FigfamDetailsStateObject(windowID, resourceURL, contextPath);

	//  try to get other state values that might have been saved in a cookie
	loadStateObject(windowID, stateObject);
	stateObject.renderWindow = ((figfamNames != null) && (2 <= figfamNames.length) && (figfamNames.charAt() == '0') && (figfamNames.charAt(1) == '.'));
	stateObject.cType = cType;
	stateObject.cId = cId;
	stateObject.famType = famType;

	var toSet = document.getElementById(windowID + '_detailsToFile');
	if ((idText != null) && (idText != "")) {
		stateObject.figfamNames = figfamNames;
		toSet.fileName.value = figfamNames;
	} else {
		toSet.fileName.value = 'f' + figfamNames;
	}

	if (idText == null) {
		idText = '';
	}

	//var initGrid = new DetailsGrid(windowID, '', 0);
	//initGrid.showTable(true, []);

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "expandFigfams",
			genomeIds : idText,
			FAM_TYPE : stateObject.famType,
			figfamIds : figfamNames,
			portlet_type : Ext.getDom("portlet_type").value
		},
		success : function(rs) {
			Ext.getDom(windowID + "_grid").innerHTML = "";
			catchTableDetails(windowID, rs, stateObject.renderWindow);
		}
	});

}

function refreshPageSize() {
	var ps = Ext.num(Ext.getDom("rpp").value, 20);
	if (ps <= 5000) {
		return ps;
	} else {
		Ext.MessageBox.alert('Notice', "You've requested to retrieve a table containing more than 5,000 records. We are currently upgrading our system to support large data retrieval, but at this time, display is limited to data sets smaller than 5,000 records. You can download in an Excel or plain-text format up to 100,000 records and FTP access to whole genomes (containing large data sets greater than 10,000 records) is available via the 'Downloads' top-level navigation menu.");
		return 20;
	}
}

function catchTableDetails(windowID, ajaxHttp, renderWindow) {
	cacheObject(windowID + "_ajaxText", ajaxHttp.responseText);
	var ajaxData = (ajaxHttp.responseText).split("\t");
	//  figfam count
	//  species count
	///  member count
	//    maximum aa length
	//    minimum aa length
	if (ajaxData.length < 21) {
		for (var i = 0; i < ajaxData.length; i++) {
			alert(ajaxData[i]);
		}
	} else {
		var at = 0;
		var figfamCount = ajaxData[at];
		at++;
		var speciesCount = ajaxData[at];
		at++;
		var rowCount = ajaxData[at];
		at++;
		var minFind = ajaxData[at];
		at++;
		var maxFind = ajaxData[at];
		at++;
		var stateObject = getStateObject(windowID);
		this.figfamCount = figfamCount;
		var tableData = [];
		if (1 < figfamCount) {
			var groupIndex = 0;
			var groupCounter = new Array();
			while (at != ajaxData.length) {
				var count = ajaxData[at];
				at++;
				var figId = ajaxData[at];
				at++;
				// description for figId
				at++;
				for (var i = 0; i < count; i++) {
					var row = [];
					row.push(windowID);
					row.push(figId);
					for (var j = 0; j < 13; j++) {
						row.push(ajaxData[at]);
						if (j == 3) {
							// set map to go from feature id
							//   to an index for its figId
							groupCounter[ajaxData[at]] = groupIndex;
						}
						at++;
					}
					tableData.push(row);
				}++groupIndex;
			}
			stateObject.figIdMap = groupCounter;
		} else {
			var count = ajaxData[at];
			at++;
			stateObject.figfamNames = ajaxData[at];
			at++;
			stateObject.description = ajaxData[at];
			at++;
			for (var i = 0; i < count; i++) {
				var row = [];
				row.push(windowID);
				for (var j = 0; j < 13; j++) {
					row.push(ajaxData[at]);
					at++;
				}
				tableData.push(row);
			}
		}

		var sumText = "<a href=\"javascript:backoffPage()\" >" + "<img src='/patric/images/button_return_to_list_of_protein_families.png'" + " alt='Return to Families Table' /></a>";
		sumText += "<table width='100%' border='0' cellpadding='10'>";
		sumText += "<tr>";
		sumText += "<td width='40%' valign='top'  style='line-height:180%'>";

		if (figfamCount == 1) {
			sumText += "<b>Protein Family ID: </b>" + stateObject.figfamNames + "<br />";
			sumText += "<b>Product: </b>" + stateObject.description;
		} else {
			sumText += "<br /><b>Number of Protein Families: </b>" + figfamCount + "<br />";
		}
		sumText += "</td><td width='20%' valign=top style='line-height:150%'>";
		sumText += "<b>No. of Members: </b>" + rowCount + "<br />";
		sumText += "<b>No. of Species: </b>" + speciesCount;
		sumText += "</td><td width='20%' valign=top style='line-height:150%'>";
		sumText += "<b>Min AA Length: </b>" + minFind + "<br />";
		sumText += "<b>Max AA Length: </b>" + maxFind;
		if (1 < rowCount) {
			sumText += "</td><td width='20%' valign=top style='line-height:150%'>";
			sumText += "<b>Integrated Protein Tree and Alignment</b>";
			sumText += "<br />";
			sumText += "<a href=\"javascript:getAlignViewer('" + windowID + "')\" >For all " + rowCount + " members</a></td>";
		}
		sumText += "</tr></table>";

		toSet = document.getElementById(windowID + "_summary");
		toSet.innerHTML = sumText;

		cacheObject(windowID + "_tableData", tableData);

		var gridObject = new DetailsGrid(windowID, figfamCount, 0, renderWindow);

		gridObject.Pagingstore.loadData(tableData);
		gridObject.store.totalCount = tableData.length;
		var gridState = gridObject.gridState;
		gridState.serveURL = stateObject.serveURL;
		gridObject.showTable(false, tableData);
		cacheObject(windowID, gridObject);
		defineButtons(windowID);
		//createCopyBox(windowID);
		cacheInitialState(windowID);
		beginHashChecking();
	}
}

function getAddPopupPanel() {
	return (
		Ext.create('Ext.form.FormPanel', {
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

function getSelectedFeatures(windowID, actiontype, showdownload, fastatype, to) {
	var gridObject = getScratchObject(windowID);
	var rs = {};
	if (gridObject.checkbox.getCheckAll() == false) {
		var sl = gridObject.checkbox.getSelections();
		rs.responseText = (sl[0]).get('na_feature_id');
		if (sl.length > 1) {
			for (var i = 1; i < sl.length; i++) {
				rs.responseText += "," + (sl[i]).get('na_feature_id');
			}
		}
	} else {
		var items = Ext.getStore('ds_details_paging').data.items;
		rs.responseText = items[0].data.na_feature_id;
		for (var i = 1; i < items.length; i++) {
			rs.responseText += "," + items[i].data.na_feature_id;
		}
	}

	processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, rs);

}

function processFigfamSelectedItems(windowID, actiontype, showdownload, fastatype, to, rs) {
	var stateObject = getStateObject(windowID);
	if (actiontype == "cart") {
		Ext.getDom("fids").value = rs.responseText;
		setGroupPopup(windowID);
	} else if (actiontype == "fasta") {
		catchFastaIds(windowID, showdownload, fastatype, rs);
		adjustCheckBoxes("unchecked");
	} else if (actiontype == "msa") {
		var hrefBase = 'TreeAlignerB?cType=' + stateObject.cType + "&cId=" + stateObject.cId + "&";
		if (windowID.indexOf('FIGfamSorterB') < 0) {
			hrefBase = 'TreeAligner?';
		}
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

function checkAddClick(windowID) {

	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		addSelectedItems("Feature");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function submitFASTA(windowID, actionType, fastaType) {

	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "fasta", actionType, fastaType, "");
	} else {
		if (actionType == "display") {
			alert("No item(s) are selected. To show Fasta sequence, at least one item must be selected.");
		} else {
			alert("No item(s) are selected. To download Fasta sequence, at least one item must be selected.");
		}
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
		window.open("", "disp", "width=920,height=400,scrollbars,resizable");
		toSubmit.target = "disp";
	} else {
		toSubmit.target = "";
	}
	toSubmit.submit();
}

function getHeaderEntry(place, name) {
	var result = "<input name ='Head" + place + "' type='hidden' value='" + name + "' />";
	return result;
}

function setTopOrtho(windowID, fileType) {
	var tablePass = "Genome Name\tLocus Tag\tStart\tEnd\tLength(NT)\tStrand\tLength(AA)\tGene Symbol\tProduct Description";
	var tableData = getScratchObject(windowID + "_tableData");
	var column_order = [1, 5, 7, 8, 9, 10, 11, 12, 13];

	for (var i = 0; i < tableData.length; i++) {
		var rowData = tableData[i];
		tablePass += rowData[column_order[0]];

		for (var j = 1; j < column_order.length; j++) {
			tablePass += "\t" + rowData[column_order[j]];
		}

		tablePass += "\n";
	}

	var toSet = document.getElementById(windowID + '_detailsToFile');
	toSet.fileName.value = 'genes';
	toSet.fileType.value = fileType;
	toSet.data.value = tablePass;

	toSet.submit();
}

function submitIDMapping(windowID, to) {

	var gridObject = getScratchObject(windowID);
	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "idmap", "", "", to);
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

function submitEnrichment(windowID) {
	var gridObject = getScratchObject(windowID);

	if (gridObject.checkbox.getCheckAll() == true || gridObject.checkbox.getCount() > 0) {
		getSelectedFeatures(windowID, "pathway_enrichment", "", "", "");
	} else {
		alert("No item(s) are selected. At least one Figfam Group must be selected.");
	}
}

function getAlignViewer(windowID) {
	var stateObject = getStateObject(windowID);
	var hrefBase = 'TreeAlignerB?cType=' + stateObject.cType + "&cId=" + stateObject.cId + "&";
	if (windowID.indexOf('FIGfamSorterB') < 0) {
		hrefBase = 'TreeAligner?';
	}
	var rs = {};
	var items = Ext.getStore('ds_details_paging').data.items;
	rs.responseText = items[0].data.na_feature_id;
	for (var i = 1; i < items.length; i++) {
		rs.responseText += "," + items[i].data.na_feature_id;
	}
	catchAlignIds(rs.responseText.split(",").length, stateObject.serveURL, hrefBase, rs);
}

function popAlignHtml(ajaxHttp, showIn) {
	var toSet = (showIn.document).getElementById("forGblocks");
	toSet.innerHTML = ajaxHttp.responseText;
}

function viewNexus(ajaxHttp, showIn) {
	var toSet = (showIn.document).getElementById("forAlignView");
	toSet.innerHTML = "<pre>" + ajaxHttp.responseText + "</pre>";
}

function popRawTree(ajaxHttp, contextPath, ajaxURL, showIn) {
	var treeSplit = (ajaxHttp.responseText).split("\t");
	var url = ajaxURL + '&' + "callType=retrieveTreePng";
	url += '&' + "TREE_PNG=" + treeSplit[0];
	var toSet = (showIn.document).getElementById("forTreeView");
	var setter = "<IMG src='" + url + "' /><br />";
	for (var i = 1; i < treeSplit.length; i++) {
		setter += "<br />" + treeSplit[i];
	}
	toSet.innerHTML = setter;
}

function popOrthoHtml(ajaxHttp, showIn) {
	showIn.document.write(ajaxHttp.responseText);
	showIn.document.close();
}

function doOrthoBlast(windowID, featureId) {
	var stateObject = getStateObject(windowID);
	var showIn = window.open("", "", "height=320,width=800,scrollbars");

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getBlastPop",
			featureId : featureId
		},
		success : function(rs) {
			popOrthoHtml(rs, showIn);
		}
	});

}

function renderWindowGenome(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format('<a href="../Genome?cType=genome&amp;cId={0}">{1}</a>', record.data.genome_info_id, value);
}

function renderReplaceGenome(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return Ext.String.format('<a href="Genome?cType=genome&amp;cId={0}">{1}</a>', record.data.genome_info_id, value);
}

function renderWindowLocus(value, metadata, record, rowIndex, colIndex, store) {

	if (value != null && value != "") {
		metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&amp;cId={0}">{1}</a>', record.data.na_feature_id, value);
	} else if (record.data.na_feature_id != null) {
		metadata.tdAttr = 'data-qtip="' + record.data.na_feature_id + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&amp;cId={0}">fid:{0}</a>', record.data.na_feature_id);
	} else {
		return "";
	}
}

function renderReplaceLocus(value, metadata, record, rowIndex, colIndex, store) {

	if (value != null && value != "") {
		metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&amp;cId={0}">{1}</a>', record.data.na_feature_id, value);
	} else if (record.data.na_feature_id != null) {
		metadata.tdAttr = 'data-qtip="' + record.data.na_feature_id + '" data-qclass="x-tip"';
		return Ext.String.format('<a href="Feature?cType=feature&amp;cId={0}">fid:{0}</a>', record.data.na_feature_id);
	} else {
		return "";
	}
}

function defineButtons(windowID) {
	var gridObject = getScratchObject(windowID), btn = $Page.getCartSaveButton();
	gridObject.allowHashing = true;
	btn.on('click', function() {
		getSelectedFeatures(windowID, "cart", "", "", "");
	});
}

function setGroupPopup(windowID) {
	var gridObject = getScratchObject(windowID);
	if (saveToGroup(Ext.getDom("fids").value, "Feature")) {
		adjustCheckBoxes("unchecked");
		gridObject.checkbox.showMessage(Ext.getDom("fids").value.split(",").length, "Feature");
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
		Ext.Msg.alert(featureList.length + ' proteins selected!', 'Current resources can not handle this many proteins in ' + figfamCount + ' families.');
	}
}
