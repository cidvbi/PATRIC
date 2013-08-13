function TreeAlignerStateObject(windowID, serveResource, getContextPath, figfamIds, product) {
	// save initial values
	this.windowID = windowID;
	this.serveURL = serveResource;
	this.contextPath = getContextPath;
	// set duration at 30 days or desired value
	this.duration = defaultDuration;
	this.figfamNames = figfamIds;
	this.description = product;

	this.loadData = TreeAlignerLoadData;
	this.saveData = TreeAlignerSaveData;
}

//create a method for the required saveData link
function TreeAlignerSaveData(windowID, namespace) {
}

function TreeAlignerLoadData(namespace) {
}

function TreeAlignerOnReady(windowID, resourceURL, contextPath, featureIds, figfamId, product) {
	/*var waitMask =
	 new Ext.LoadMask(windowID + "_forApplet",
	 {msg:"Computing Tree and Alignment ..."});
	 waitMask.show();*/

	Ext.get(windowID).mask('Computing Tree and Alignment ...');

	// register windowID to insure that state values gets stored to a cookie
	//   on page exits or refreshes
	addWindowID(windowID);

	// create a default state object with the critical values
	//   provided by the server
	var stateObject = new TreeAlignerStateObject(windowID, resourceURL, contextPath, figfamId, product);

	//  try to get other state values that might have been saved in a cookie
	loadStateObject(windowID, stateObject);

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 6000000,
		params : {
			callType : "alignFromFeatures",
			featureIds : featureIds
		},
		success : function(rs) {
			insertTreeApplet(windowID, rs);
		}
	});
}

function insertTreeApplet(windowID, ajaxHttp) {
	var stateObject = getStateObject(windowID);
	var data = (ajaxHttp.responseText).split("\f");
	path = data[1];
	var nexusData = data[0].split("\t");

	var sumText = "<table width='100%' border='0' cellpadding='10'>";
	sumText += "<tr>";
	if (0 < (stateObject.figfamNames).length) {
		sumText += "<td width='40%' valign='top'  style='line-height:180%'>";
		sumText += "<b>Protein Family ID: </b>" + stateObject.figfamNames + "<br />";
		sumText += "<b>Product: </b>" + stateObject.description + "</td>";
	}
	var rowCount = nexusData[0];
	sumText += "<td width='20%' valign=top style='line-height:150%'>";
	sumText += "<b>No. of Members: </b>" + rowCount + "<br />";
	sumText += "<b>No. of Species: </b>" + nexusData[1];
	sumText += "</td><td width='20%' valign=top style='line-height:150%'>";
	sumText += "<b>Min AA Length: </b>" + nexusData[2] + "<br />";
	sumText += "<b>Max AA Length: </b>" + nexusData[3];
	sumText += "</td><td width='20%' valign=top style='line-height:150%'>";
	sumText += "<b>Printable Alignment </b>";
	sumText += "<a href=\"javascript:getGblocks('" + windowID + "')\" >For all " + rowCount + " members</a>";
	//&nbsp; (";
	//sumText += "<a href=\"javascript:getClustalW('"+ windowID + "')\" >ClustalW</a>)";
	sumText += "<br /><b>Printable Tree </b>";
	sumText += "<a href=\"javascript:getPrintableTree('" + windowID + "')\" >For all " + rowCount + " members</a>&nbsp; (";
	sumText += "<a href=\"javascript:getNewickTree('" + windowID + "')\" >Newick File</a>)";

	sumText += "</td>";

	sumText += "</tr></table>";

	var toSet = document.getElementById(windowID + "_summary");
	toSet.innerHTML = sumText;

	var jarUrl = stateObject.contextPath + '/TreeViewer.jar';
	toSet = document.getElementById(windowID + "_forApplet");

	var setter = "<applet archive='" + jarUrl + "'" + " codebase='.'" + " code='edu/vt/vbi/patric/applets/treealign/TreeAlignment.class'" + " id='" + windowID + "_applet'" + " width='100%' height='100%'>" + " <param name='NEWICK'" + " value='" + nexusData[4] + "'>";
	var alignNum = 0;
	for (var i = 5; i < nexusData.length; i++) {++alignNum;
		var pName = "ALIGN" + alignNum;
		pName = " <param name='" + pName + "'" + " value='" + nexusData[i] + "'>";
		setter += pName;
	}
	setter += " </applet>";

	toSet.innerHTML = setter;
	Ext.get(windowID).unmask();
}

function getClustalW(windowID) {

	var stateObject = getStateObject(windowID);
	var insides = "<!DOCTYPE html PUBLIC " + "\"-//W3C//DTD XHTML 1.0 Transitional//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" >";
	insides += "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>";
	insides += "<meta http-equiv=\"Content-Type\" " + "content=\"text/html;charset=utf-8\">";

	insides += "<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"/patric/css/popup.css\" />";

	insides += "</head>\n";
	insides += "<body id='popup'>\n";
	insides += "<div id='header'>" + "<div id=\"masthead\">" + "<a href=\"/\">PATRIC" + "<span class=\"sub\">Pathosystems Resouce Integration Center" + "</span></a></div>" + "</div>\n";
	insides += "<div id=\"toppage\"><div class=\"content\">";
	insides += "<div>";
	insides += "<br />";
	insides += "<div id='forTreeView'>";
	insides += "</div></div></div></div>";
	insides += "<div id=\"footer\"> &nbsp</div></body></html>";
	var showIn = window.open("", "", "width = 1000, height=760, scrollbars = 1");
	showIn.document.write(insides);
	showIn.document.close();

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 6000000,
		params : {
			"path" : path,
			callType : "clustalW"
		},
		success : function(rs) {
			var toSet = (showIn.document).getElementById("forTreeView");
			toSet.innerHTML = rs.responseText;
		}
	});

}

function getNewickTree(windowID) {

	var appFinder = document.getElementById(windowID + "_applet");
	var treeBase = appFinder.getTreePrintData();
	var text = treeBase.split("&")[1].split("=")[1];
	text = text.substring(0, text.length - 1);

	Ext.getDom(windowID + "_form").action = "/FigFamSorter/jsp/GetAlignerFiles.jsp";
	Ext.getDom("data").value = text;
	Ext.getDom("fileformat").value = "newick";
	Ext.getDom(windowID + "_form").target = "";
	Ext.getDom(windowID + "_form").submit();

}

function getPrintableTree(windowID) {
	var stateObject = getStateObject(windowID);
	var insides = "<!DOCTYPE html PUBLIC " + "\"-//W3C//DTD XHTML 1.0 Transitional//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" >";
	insides += "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>";
	insides += "<meta http-equiv=\"Content-Type\" " + "content=\"text/html;charset=utf-8\">";
	insides += "<title>" + stateObject.figfamNames + ": " + stateObject.description + "</title>";
	insides += "<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"/patric/css/popup.css\" />";

	insides += "</head>\n";
	insides += "<body id='popup'>\n";
	insides += "<div id='header'>" + "<div id=\"masthead\">" + "<a href=\"/\">PATRIC" + "<span class=\"sub\">Pathosystems Resouce Integration Center" + "</span></a></div>" + "</div>\n";
	insides += "<div id=\"toppage\"><div class=\"content\">";
	insides += "<div>";
	if (0 < (stateObject.figfamNames).length) {
		insides += "<b>";
		insides += stateObject.figfamNames + ": " + stateObject.description;
		insides += "</b><br />";
	}
	insides += "<br />";
	insides += "<div id='forTreeView'>";
	insides += "</div></div></div></div>";
	insides += "<div id=\"footer\"> &nbsp</div></body></html>";
	var showIn = window.open("", "", "width = 1000, height=760, scrollbars = 1");
	showIn.document.write(insides);
	showIn.document.close();

	var appFinder = document.getElementById(windowID + "_applet");
	var treeBase = appFinder.getTreePrintData();
	var paramList = packParameters(treeBase, stateObject);
	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 6000000,
		params : paramList,
		success : function(rs) {
			popRawTree(rs, stateObject.contextPath, stateObject.serveURL, showIn);
		}
	});

}

function popRawTree(ajaxHttp, contextPath, ajaxURL, showIn) {
	var treeSplit = (ajaxHttp.responseText).split("\t");
	var url = ajaxURL + '&' + "callType=retrieveTreePng";
	url += '&' + "TREE_PNG=" + treeSplit[0];
	var toSet = (showIn.document).getElementById("forTreeView");
	toSet.innerHTML = "";
	var setter = "<IMG src='" + url + "' /><br />";
	for (var i = 1; i < treeSplit.length; i++) {
		setter += "<br />" + treeSplit[i];
	}
	toSet.innerHTML = setter;
}

function getGblocks(windowID) {
	var stateObject = getStateObject(windowID);
	var insides = "<!DOCTYPE html PUBLIC " + "\"-//W3C//DTD XHTML 1.0 Transitional//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" >";
	insides += "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>";
	insides += "<meta http-equiv=\"Content-Type\" " + "content=\"text/html;charset=utf-8\">";

	insides += "<style media=screen type=text/css><!--\n" + ".BL {background-color:navy;color:navy}\n" + ".A1 {background-color:black;color:lime}\n" + ".G1 {background-color:black;color:lime}\n" + ".S1 {background-color:black;color:lime}\n" + ".T1 {background-color:black;color:lime}\n" + ".C1 {background-color:black;color:orange}\n" + ".P1 {background-color:black;color:aqua}" + ".D1 {background-color:black;color:white}\n" + ".E1 {background-color:black;color:white}\n" + ".Q1 {background-color:black;color:white}\n" + ".N1 {background-color:black;color:white}\n" + ".F1 {background-color:black;color:yellow}\n" + ".W1 {background-color:black;color:yellow}\n" + ".Y1 {background-color:black;color:yellow}\n" + ".H1 {background-color:black;color:red}\n" + ".K1 {background-color:black;color:red}\n" + ".R1 {background-color:black;color:red}\n" + ".I1 {background-color:black;color:fuchsia}\n" + ".L1 {background-color:black;color:fuchsia}\n" + ".M1 {background-color:black;color:fuchsia}\n" + ".V1 {background-color:black;color:fuchsia}\n" + "--></style>\n";
	insides += "<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"/patric/css/popup.css\" />";
	insides += "</head>\n";
	insides += "<body id='popup'>\n";
	insides += "<div id='header'>" + "<div id=\"masthead\">" + "<a href=\"/\">PATRIC" + "<span class=\"sub\">Pathosystems Resouce Integration Center" + "</span></a></div>" + "</div>\n";
	insides += "<div id=\"toppage\"><div class=\"content\">";
	insides += "<div id='forGblocks'>";
	insides += "</div></div></div>";
	insides += "<div id=\"footer\"> &nbsp</div></body></html>";
	var showIn = window.open("", "", "width = 1000, height=760, scrollbars = 1");
	showIn.document.write(insides);
	showIn.document.close();

	var appFinder = document.getElementById(windowID + "_applet");
	var gblocksBase = appFinder.getGblocksData();
	var paramList = packParameters(gblocksBase, stateObject);
	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'POST',
		timeout : 6000000,
		params : paramList,
		success : function(rs) {
			popGblocks(rs, showIn);
		}
	});

}

function packParameters(fromApplet, stateObject) {
	fromApplet += '&figfamId=' + stateObject.figfamNames;
	fromApplet += '&description=' + stateObject.description;
	return fromApplet;
}

function popGblocks(ajaxHttp, showIn) {
	var toSet = (showIn.document).getElementById("forGblocks");
	toSet.innerHTML = "";
	toSet.innerHTML = ajaxHttp.responseText;
}

