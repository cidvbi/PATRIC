function BlastOnReady(windowID, resourceURL, contextPath, pk, dm) {
	// register windowID to insure that state values gets stored to a cookie
	//   on page exits or refreshes
	addWindowID(windowID);

	// create a default state object with the critical values
	//   provided by the server
	var stateObject = new BlastStateObject(windowID, resourceURL, contextPath, pk, dm);

	//  try to get other state values that might have been saved in a cookie
	loadStateObject(windowID, stateObject);
	BlastFormJsp(windowID, stateObject);
}

function BlastFormJsp(windowID, stateObject) {
	Ext.Ajax.request({
		url: stateObject.serveURL,
		method: 'GET',
		params: {callType: "getJsp",
				 JSP_NAME: "settings"
		},
		success : function(rs) {
			stampHTML(windowID, rs);
			fixFormValues(windowID);
			if (stateObject.dm == 'submit') {
				RunBlastCGI(windowID);
			}
		}
	});
}

function fixFormValues(windowID) {
	// after reload of form restore form settings using
	//   default or cookie values
	var formSet = document.getElementById(windowID + "_MainBlastForm");
	var values = document.getElementById(windowID + "_keyStore");
	formSet.PROGRAM.selectedIndex = values.programIndex.value;
	formSet.DATALIB.selectedIndex = values.dbIndex.value;
	formSet.SEQUENCE.value = values.sequence.value;
	formSet.QUERY_FROM.value = values.queryFrom.value;
	formSet.QUERY_TO.value = values.queryTo.value;

	formSet.EXPECT.selectedIndex = values.expectIndex.value;
	formSet.MAT_PARAM.selectedIndex = values.matParamIndex.value;
	if (values.alignment.value == 'on') {
		formSet.UNGAPPED_ALIGNMENT.checked = true;
	}

	formSet.GENETIC_CODE.selectedIndex = values.geneCodeIndex.value;
	formSet.DB_GENETIC_CODE.selectedIndex = values.dbCodeIndex.value;
	formSet.OOF_ALIGN.selectedIndex = values.oofAlignIndex.value;

	formSet.OTHER_ADVANCED.value = values.advanced.value;

	if (values.overview.value != 'on') {
		formSet.OVERVIEW.checked = false;
	}

	formSet.ALIGNMENT_VIEW.selectedIndex = values.alignViewIndex.value;
	formSet.DESCRIPTIONS.selectedIndex = values.descriptionIndex.value;

	formSet.ALIGNMENTS.selectedIndex = values.alignmentsIndex.value;

	formSet.COLOR_SCHEMA.selectedIndex = values.schemaIndex.value;

	if (values.lowFilter.value != 'L') {
		formSet.FILTER[0].checked = false;
	}

	if (values.midFilter.value == 'm') {
		formSet.FILTER[1].checked = true;
	}
}

//begin with constructor of object that will maintain
//state values for each portlet occurrence
function BlastStateObject(windowID, serveResource, getContextPath, pk, dm) {
	// save initial values
	this.windowID = windowID;
	this.serveURL = serveResource;
	this.contextPath = getContextPath;
	this.pk = pk;
	this.dm = dm;

	this.fileName = '';
	// set duration at 30 days or desired value
	this.duration = defaultDuration;

	// set links for required methods that allow namespace.js
	//   to operate properly
	this.loadData = BlastLoadData;
	this.saveData = BlastSaveData;
}

//create a method for the required saveData link
function BlastSaveData(windowID, namespace) {
	var formSee = document.getElementById(windowID + "_MainBlastForm");
	if (formSee != null) {
		this.fileName = formSee.SEQFILE.value;
		setCookie(namespace + '_fileName', this.fileName, this.duration);
		saveBlastSettings(windowID, formSee);
	}
}

function saveBlastSettings(windowID, formSee) {
	var stateObject = getStateObject(windowID);
	var alignCheck = '';
	if (formSee.UNGAPPED_ALIGNMENT.checked) {
		alignCheck = 'on';
	}

	var overCheck = '';
	if (formSee.OVERVIEW.checked) {
		overCheck = 'on';
	}

	var lowCheck = '';
	if (formSee.FILTER[0].checked) {
		lowCheck = 'L';
	}

	var midCheck = '';
	if (formSee.FILTER[1].checked) {
		overCheck = 'm';
	}

	Ext.Ajax.request({
		url : stateObject.serveURL,
		method : 'GET',
		params : {
			callType : "formStore",
			programIndex : formSee.PROGRAM.selectedIndex,
			dbIndex : formSee.DATALIB.selectedIndex,
			sequence : formSee.SEQUENCE.value,
			fileName : formSee.SEQFILE.value,
			queryFrom : formSee.QUERY_FROM.value,
			queryTo : formSee.QUERY_TO.value,
			expectIndex : formSee.EXPECT.selectedIndex,
			matParamIndex : formSee.MAT_PARAM.selectedIndex,
			alignment : alignCheck,
			geneCodeIndex : formSee.GENETIC_CODE.selectedIndex,
			dbCodeIndex : formSee.DB_GENETIC_CODE.selectedIndex,
			oofAlignIndex : formSee.OOF_ALIGN.selectedIndex,
			advanced : formSee.OTHER_ADVANCED.value,
			overview : overCheck,
			alignViewIndex : formSee.ALIGNMENT_VIEW.selectedIndex,
			descriptionIndex : formSee.DESCRIPTIONS.selectedIndex,
			alignmentsIndex : formSee.ALIGNMENTS.selectedIndex,
			schemaIndex : formSee.COLOR_SCHEMA.selectedIndex,
			lowFilter : lowCheck,
			midFilter : midCheck,
			pk : stateObject.pk
		},
		success : function(rs) {
		}
	});
}

function BlastLoadData(namespace) {
	this.fileName = getCookie(namespace + "_fileName", this.fileName);
}

function stampBlastHelp(helpHtml, ajaxHttp) {
	var nextPop = window.open(helpHtml, helpHtml, "height=500, width=1000, toolbar=1,resizable=1,scrollbars=1");
	(nextPop.document).write(ajaxHttp.responseText);
	(nextPop.document).close();
}

function showBlastHelp(windowID, helpHtml) {
	var nextPop = window.open(helpHtml, helpHtml, "height=500, width=1000, toolbar=1,resizable=1,scrollbars=1");
	var stateObject = getStateObject(windowID);
	Ext.Ajax.request({
		url: stateObject.serveURL,
		method: 'GET',
		params: {callType: "blastHelp",
				 helpFile: helpHtml
		},
		success : function(rs) {
			(nextPop.document).write(rs.responseText);
			(nextPop.document).close();
		}
	});

}

function scrollBlastHelp(windowID, helpHtml, anchor) {
	var nextPop = window.open(helpHtml, helpHtml, "height=500, width=1000, toolbar=1,resizable=1,scrollbars=1");
	var stateObject = getStateObject(windowID);
	Ext.Ajax.request({
		url: stateObject.serveURL,
		method: 'GET',
		params: {callType: "blastHelp",
				 helpFile: helpHtml,
				 scrollTo: anchor
		},
		success : function(rs) {
			(nextPop.document).write(rs.responseText);
			(nextPop.document).close();
		}
	});
}

function ClearBlastFasta(windowID) {
	var toClear = document.getElementById(windowID + "_MainBlastForm");
	var stateObject = getStateObject(windowID);
	if (toClear != null) {
		toClear.SEQUENCE.value = '';
		toClear.QUERY_FROM.value = '';
		toClear.QUERY_TO.value = '';
		toClear.SEQUENCE.focus();

		toClear = document.getElementById(windowID + "_fastaFile");
		toClear.innerHTML = toClear.innerHTML;
		var namespace = getNameSpace(windowID);

		setCookie(namespace + "_fileName", '', stateObject.duration);
		stateObject.fileName = '';
	}
}

function hideBlastSettings(windowID, formSet) {
	// after reload of form restore form settings using
	//   default or cookie values
	var values = document.getElementById(windowID + "_keyStore");
	values.programIndex.value = formSet.PROGRAM.selectedIndex;
	values.dbIndex.value = formSet.DATALIB.selectedIndex;
	values.sequence.value = formSet.SEQUENCE.value;
	values.queryFrom.value = formSet.QUERY_FROM.value;
	values.queryTo.value = formSet.QUERY_TO.value;

	values.expectIndex.value = formSet.EXPECT.selectedIndex;
	values.matParamIndex.value = formSet.MAT_PARAM.selectedIndex;
	if (formSet.UNGAPPED_ALIGNMENT.checked) {
		values.alignment.value = 'on';
	} else {
		values.alignment.value == '';
	}

	values.geneCodeIndex.value = formSet.GENETIC_CODE.selectedIndex;
	values.dbCodeIndex.value = formSet.DB_GENETIC_CODE.selectedIndex;
	values.oofAlignIndex.value = formSet.OOF_ALIGN.selectedIndex;

	values.advanced.value = formSet.OTHER_ADVANCED.value;

	if (formSet.OVERVIEW.checked) {
		values.overview.value = 'on';
	} else {
		values.overview.value = '';
	}

	values.alignViewIndex.value = formSet.ALIGNMENT_VIEW.selectedIndex;
	values.descriptionIndex.value = formSet.DESCRIPTIONS.selectedIndex;

	values.alignmentsIndex.value = formSet.ALIGNMENTS.selectedIndex;

	values.schemaIndex.value = formSet.COLOR_SCHEMA.selectedIndex;

	if (formSet.FILTER[0].checked) {
		values.lowFilter.value = 'L';
	} else {
		values.lowFilter.value = '';
	}

	if (formSet.FILTER[1].checked) {
		values.midFilter.value = 'm';
	} else {
		values.midFilter.value = '';
	}
}

function RunBlastCGI(windowID) {
	//var nameSpace = getNameSpace(windowID);

	var formSee = document.getElementById(windowID + "_MainBlastForm");
	if (formSee != null) {
		hideBlastSettings(windowID, formSee);
		saveBlastSettings(windowID, formSee);
		formSee.submit();
		// replace input form jsp with one that indicates blast processing
		var stateObject = getStateObject(windowID);
		saveStateObject(stateObject);
		Ext.Ajax.request({
			url: stateObject.serveURL,
			method: 'GET',
			params: {callType: "getJsp",
					 JSP_NAME: "blastWait"
			},
			success : function(rs) {
				showWaiting(windowID, rs);
			}
		});
	}
}

function showWaiting(windowID, ajaxHttp) {
	var stateObject = getStateObject(windowID);
	if (stateObject.viewBlast != 'Y') {
		var toSet = document.getElementById(windowID);
		toSet.innerHTML = ajaxHttp.responseText;
		// create object that handles waiting animation
		var waitSet = new Object();
		waitSet.renderTo = windowID + '_waitBar';
		waitSet.text = windowID;
		var waitObject = new Ext.ProgressBar(waitSet);
		waitObject.wait({
			interval : 200, // update progress bar every 200 milleseconds
			increment : 15 // number of updates before bar loops back
		});
	}
}

function returnToBlastSetting(windowID) {
	var stateObject = getStateObject(windowID);
	var namespace = getNameSpace(windowID);
	setCookie(namespace + "_fileName", '', stateObject.duration);
	stateObject.blastView = 'N';
	stateObject.fileSequence = '';
	stateObject.fileName = '';
	stateObject.dm = 'result';
	BlastFormJsp(windowID, stateObject);
}

function haveBlast(iFrameName) {
	// get windowID by removing _catch from iFrameName
	var windowID = iFrameName.substring(0, iFrameName.length - 6);
	var stateObject = getStateObject(windowID);
	var toSet = document.getElementById(windowID);
	//  division will start with button for return for form values
	var divSet = "<input type=\"image\" src=\"/patric/images/btn_modify_search.gif\"" + " onclick=\"returnToBlastSetting('" + windowID + "');\" />";
	// remainder of division will use return from form submission
	var atFrame = document.getElementById(iFrameName);
	var toSee = atFrame.contentDocument;
	if (toSee == null) {
		toSee = atFrame.contentWindow.document;
	}
	toSee = toSee.body;
	divSet += toSee.innerHTML;

	// nph-viewgif.cgi would not work in JBoss
	//   so replace it with showGif.cgi
	var nphAt = divSet.indexOf("nph-viewgif");
	if (0 <= nphAt) {
		var preNph = divSet.substring(0, nphAt);
		nphAt = divSet.indexOf('?', nphAt + 1);
		if (0 <= nphAt) {
			var nphStart = preNph.lastIndexOf('=');
			if (0 <= nphStart) {
				divSet = preNph.substring(0, nphStart + 1) + '"' + stateObject.contextPath + "/cgi/showGif.cgi?fileName=" + divSet.substring(nphAt + 1);
			}
		}
	}
	divSet = divSet.replace("/JBOSS_CONTEXTPATH/g", stateObject.contextPath);
	toSet.innerHTML = divSet;

	// set cookies to return result viewing after
	//    an update
	var namespace = getNameSpace(windowID);
	stateObject.blastView = 'Y';
	setCookie(namespace + "_blastView", 'Y', stateObject.duration);
}

