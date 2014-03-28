var oldHash = null;
var backoffSteps = -1;
var hashStates = new Array();

function findFirstTracker(base) {
	var result = null;
	var oldHash = window.location.hash;
	if ((oldHash == null) || (oldHash == "") || (oldHash == "#")) {
		oldHash = '#' + base + '=0';
		window.location.hash = oldHash; --backoffSteps;
		result = '0';
	} else {
		hashSplit = (oldHash.substring(1)).split('&');
		for (var i = 0; i < hashSplit.length; i++) {
			var pair = (hashSplit[i]).split('=');
			if ((pair.length == 2) && (pair[0] == base)) {
				result = pair[1];
				i = hashSplit.length;
			}
		}
		if (result == null) {
			oldHash += '&' + base + '=' + '0';
			window.location.hash = oldHash; --backoffSteps;
			result = '0';
		}
	}
	return result;
}

function beginHashChecking() {
	oldHash = window.location.hash;
	setInterval("checkForBackClick()", 100);
}

function checkForBackClick() {
	// check for change in hash
	if (window.location.hash != oldHash) {
		var allZero = true;
		var haveZero = false;
		// set oldHash to prevent reprocessing
		oldHash = window.location.hash;
		// search for part change
		var hashParts = (oldHash.substring(1)).split('&');
		for (var i = 0; i < hashParts.length; i++) {
			var setPair = (hashParts[i]).split('=');
			if (setPair.length == 2) {
				var toCheck = hashStates[setPair[0]];
				if (toCheck != null) {
					if (setPair[1] == 0) {
						haveZero = true;
					} else {
						allZero = false;
						toCheck.checkKey(setPair[1]);
					}
				}
			}
		}
		if ((haveZero) && (allZero)) {
			history.go(-1);
		}
	}
}

function backoffPage() {
	history.go(backoffSteps);
}

function getStateKey(gridStateId) {
	oldHash = window.location.hash;
	var result = null;
	if ((oldHash != null) && (oldHash != "") && (oldHash != "#")) {
		var toSearch = (oldHash.substring(1)).split('&');
		for (var i = 0; i < toSearch.length; i++) {
			var pairSet = (toSearch[i]).split('=');
			if ((pairSet.length == 2) && (pairSet[0] == gridStateId)) {
				result = pairSet[1];
				i = toSearch.length;
			}
		}
	}
	return result;
}

function setStateChange(gridStateId, random) {
	var foundId = false;
	if ((oldHash != null) && (oldHash != "") && (oldHash != "#")) {
		var toSearch = (oldHash.substring(1)).split('&');
		for (var i = 0; i < toSearch.length; i++) {
			var pairSet = (toSearch[i]).split('=');
			if ((pairSet.length == 2) && (pairSet[0] == gridStateId)) {
				toSearch[i] = gridStateId + '=' + random;
				i = toSearch.length;
				foundId = true;
			}
		}
		if (foundId) {--backoffSteps;
			oldHash = toSearch.join('&');
			oldHash = '#' + oldHash;
			window.location.hash = oldHash;
		}
	}
	return foundId;
}