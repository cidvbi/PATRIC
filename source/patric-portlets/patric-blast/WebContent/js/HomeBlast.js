function launchBlast(windowID, serveURL) {
	var formSee = document.getElementById(windowID + "_MainBlastForm");
	formSee.submit();
	var callParams = 'callType=getJsp&JSP_NAME=blastWait';
	callParams += "&SEQ_DESCRIBE=";
	new Ajax.Request(serveURL, {
		method : 'POST',
		onSuccess : function(ajaxHttp) {
			showWaiting(windowID, ajaxHttp);
		},
		asynchronous : true,
		parameters : callParams
	});
}

function showWaiting(windowID, ajaxHttp) {
	if (ajaxHttp.readyState == 4) {
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

function haveBlast(iFrameName) {
	// get windowID by removing _catch from iFrameName
	var windowID = iFrameName.substring(0, iFrameName.length - 6);
	var stateObject = getStateObject(windowID);
	var toSet = document.getElementById(windowID);
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
