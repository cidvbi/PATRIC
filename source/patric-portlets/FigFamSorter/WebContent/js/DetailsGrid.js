function createComboBoxes() {
	Ext.create('Ext.form.TextField', {
		id : 'tb_keyword',
		renderTo : 'f_keyword',
		width : 245,
		fieldLabel : 'Keyword',
		labelWidth : 60
	});
}

function filterTable() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;
	hash.aP[0] = 1, hash.kW = Ext.getCmp("tb_keyword").getValue(), createURL();
}

function getExtraParams() {

	var object = {}, Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	object["gid"] = getGID();
	;
	object["Keyword"] = (!hash.kW) ? '(*)' : "(" + hash.kW + ")";
	object["figfam_id"] = getFigFam();

	Ext.getDom("download_keyword").value = constructKeyword(object, property.name);

	return {
		callType : "getData",
		keyword : constructKeyword(object, property.name)
	};
}

function CallBack() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.aT ? hash.aT : 0, temp_string = "";

	if (Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();

	Ext.getDom("grid_result_summary").innerHTML = Page.getStore(which).getTotalCount() + ' features found in ';

	if (getFigFam().split("##").length == 1)
		Ext.getDom("grid_result_summary").innerHTML += getFigFam();
	else
		Ext.getDom("grid_result_summary").innerHTML += getFigFam().split("##").length + " protein families";

	temp_string += "<b>";
	temp_string += Ext.getDom("grid_result_summary").innerHTML + "</b>";
	Ext.getDom("grid_result_summary").innerHTML = temp_string;
}

function getSelectedFeatures() {"use strict";

	var Page = $Page, property = Page.getPageProperties(), sl = Page.getCheckBox().getSelections(), i, fids = property.fids;

	for ( i = 0; i < sl.length; i++)
		fids.push(sl[i].data.na_feature_id);
}

function DownloadFile() {"use strict";

	var form = Ext.getDom("fTableForm");

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp", form.target = "", form.fileformat.value = arguments[0];
	getHashFieldsToDownload(form);
	form.submit();
}
