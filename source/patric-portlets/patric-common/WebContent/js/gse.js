function launchGSE() {
	//var grIdxString = _getSelectedGroupIds();
	getSelectedFeatures();
	var grIdxString = Ext.getDom("fids").value;

	if (grIdxString == "") {
		alert("No Group(s) are selected.");
	} else {
		document.location.href = "GroupManagement?mode=gse&groupId=" + grIdxString;
	}
}
