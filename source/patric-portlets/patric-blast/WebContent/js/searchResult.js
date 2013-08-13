var $Page, pageProperties = {
	cart : true
};
SetPageProperties(pageProperties);

//<!-- selected box operation -->
function toggleAllCheckBoxes() {
	var i;
	CheckValue = document.searchResult.selectall.checked;
	oElement = document.searchResult.elements['fids[]'];
	if (oElement.length) {
		for ( i = 0; i < oElement.length; i++)
			oElement[i].checked = CheckValue;
	}
}

function getSelectedFeatures() {
	var sl = document.searchResult.elements['fids[]'];
	if (sl.length == 0) {
		return false;
	}

	var fid = "", i;
	for ( i = 0; i < sl.length; i++) {
		if (sl[i].checked == true) {
			if (fid.length == 0) {
				fid = sl[i].value;
			} else {
				fid += "," + sl[i].value;
			}
		}
	}
	if (fid == "") {
		return false;
	}

	Ext.getDom("fids").value = fid;
}

function checkFastaOptions() {
	return true;
}

function show_fasta_files() {
	Ext.getDom("searchResult").action = "/patric-common/jsp/fasta_download_handler.jsp";
	Ext.getDom("fastaaction").value = "display";
	if (getSelectedFeatures() == false) {
		return false;
	}
	window.open("", "disp", "width=920,height=400,scrollbars,resizable");
	Ext.getDom("searchResult").target = "disp";
	Ext.getDom("searchResult").submit();
}

function download_fasta_files() {
	Ext.getDom("searchResult").action = "/patric-common/jsp/fasta_download_handler.jsp";
	Ext.getDom("fastaaction").value = "download";
	if (getSelectedFeatures() == false) {
		return false;
	}
	Ext.getDom("searchResult").target = "";
	Ext.getDom("searchResult").submit();
}

function add_to_cart() {
	if (getSelectedFeatures() == false) {
		Ext.Msg.alert("Alert", "No feature was selected");
		return false;
	} else {
		addSelectedItems("Feature");
	}
}

Ext.onReady(function() {

	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();

	if (btnGroupPopupSave != null) {
		btnGroupPopupSave.on('click', function() {
			saveToGroup(Ext.getDom("fids").value, "Feature");
		});
	}

});
