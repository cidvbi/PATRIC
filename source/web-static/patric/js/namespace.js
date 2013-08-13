// provides a list of windowID values present on the page
//   to support automatic caching portlet occurrences settings to a cookie
//   prior to a refresh
var windowIDs = [];
var stateStorage = [];
var scratchStorage = [];

// 30 days 
var defaultDuration = 30 * 86400000;

function addWindowID(windowID) {
	windowIDs[windowIDs.length] = windowID;
}

// used to populate 
function loadStateObject(windowID, stateObject) {
	if ((window.name == null) || (window.name.length == 0)) {
		// this is a new page so there is no chance of a 
		//  relevant cookie
		var seeTime = new Date();
		window.name = seeTime.getTime();
	} else {
		var namespace = windowID;
		namespace += '_';
		namespace += window.name;
		stateObject.loadData(namespace);
	}
	// put state settings in cache
	stateStorage[windowID] = stateObject;
	return stateObject;
}

function getNameSpace(windowID) {
	return (windowID + '_' + window.name);
}


function getStateObject(windowID) {
	return(stateStorage[windowID]);
}

function saveStateObject(stateObject) {
	var namespace = stateObject.windowID + '_' + window.name;
	// the object knows how to store its important settings
	//   in cookies
	stateObject.saveData(stateObject.windowID, namespace);
}

function cacheObject(cacheID, scratchObject) {
	// put dynamic object data in javascript ram
	scratchStorage[cacheID] = scratchObject;
}

function getScratchObject(cacheID) {
	// get dynamic object data from javascript ram
	return(scratchStorage[cacheID]);
}

function getMillisecondsInDays(days) {
	var duration = 86400000;
	duration *= days;
	return duration;
}

function getMillisecondsInHours(hours) {
	var duration = 3600000;
	duration *= hours;
	return duration;
}

function setCookie(c_name, value){
	var exdate = new Date();
	// set cookie to last 30 days
	exdate.setDate(exdate.getDate()+ defaultDuration);
	document.cookie =
		c_name + "=" + escape(value) + ";expires=" + exdate.toGMTString();
}


function setCookie(c_name, value, milliseconds){
	var exdate = new Date();
	// set cookie to last 30 days
	exdate.setTime(exdate.getTime()+ milliseconds);
	document.cookie =
		c_name + "=" + escape(value) + ";expires=" + exdate.toGMTString();
}

function getCookie(c_name, result){
	if (0 < document.cookie.length) {
		c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) {
			c_start += c_name.length + 1;
			c_end = document.cookie.indexOf(";", c_start);
			if (c_end < 0) {
				c_end=document.cookie.length;
			}
			result = unescape(document.cookie.substring(c_start, c_end));
		}
	}
	return result;
}

// support for setting the main portlet division with 
//   html from a server jsp file or raw writing
function stampHTML(division, ajaxHttp) {
	if (ajaxHttp.readyState == 4) {
		var toSet = document.getElementById(division);
		toSet.innerHTML = ajaxHttp.responseText;
	}
}


//  Use extjs method to insure that values from javascript ram are
//    updated in their cookies
Ext.EventManager.on(window, "unload",
		function(){
			for(i = 0; i < windowIDs.length; i++) {
				var stateObject = stateStorage[windowIDs[i]];
				saveStateObject(stateObject);
			}
		}
	); 
