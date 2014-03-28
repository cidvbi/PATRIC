//Modernizr.history = false;
function createLoadComboBoxes() {
	var Page = $Page, property = Page.getPageProperties(), object = {};

	Ext.create('Ext.form.ComboBox', {
		id : 'cb_feature_type_f',
		renderTo : 'f_feature_type',
		fieldLabel : 'Feature Type',
		displayField : 'name',
		valueField : 'value',
		width : 235,
		labelWidth : 90,
		editable : false,
		typeHead : true,
		store : Ext.create('Ext.data.Store', {
			fields : ['name', 'value']
		}),
		queryMode : 'local'
	});

	Ext.create('Ext.form.ComboBox', {
		id : 'cb_annotation_f',
		renderTo : 'f_annotation',
		fieldLabel : 'Annotation',
		displayField : 'name',
		valueField : 'value',
		width : 175,
		labelWidth : 60,
		editable : false,
		typeHead : true,
		store : Ext.create('Ext.data.Store', {
			fields : ['name', 'value']
		}),
		queryMode : 'local'
	});

	Ext.create('Ext.form.TextField', {
		id : 'tb_keyword',
		renderTo : 'f_keyword',
		width : 245,
		fieldLabel : 'Keyword',
		labelWidth : 60
	});

	object["gid"] = getGID();

	Ext.Ajax.request({
		url : '/patric-common/jsp/featuretable_filter_populate.jsp',
		method : 'POST',
		params : {
			keyword : constructKeyword(object, property.name),
			facet : JSON.stringify({
				"facet" : configuration[property.name].display_facets.join(","),
				"facet_text" : configuration[property.name].display_facets_texts.join(",")
			})
		},
		success : function(response, opts) {
			FillComboBoxes(Ext.JSON.decode(response.responseText).facets);
		}
	});

}

function FillComboBoxes(data) {

	var d = {}, ds = [];
	for (var i in data) {
		ds = [];
		for (var j = 0; j < data[i].attributes.length; j++) {
			d = {};
			d["name"] = data[i].attributes[j].value == "BRC" ? "Legacy BRC" : data[i].attributes[j].value;
			d["value"] = data[i].attributes[j].value;
			ds.push(d);
		}

		ds.sort(sortRowsData("name"));
		d = {};
		d["name"] = (i == "annotation_f") ? "ALL" : "ALL Feature Types";
		d["value"] = "ALL";
		ds.push(d);

		Ext.getCmp("cb_" + i).getStore().loadData(ds);
	}

}

function sortRowsData(value) {
	return function(a, b) {
		if (a[value] == "ALL")
			return -1;
		if (a[value] < b[value])
			return -1;
		if (a[value] > b[value])
			return 1;
		return 0;
	};
}

function filterFeatureTable() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	hash.aP[0] = 1, hash.fT = Ext.getCmp("cb_feature_type_f").getValue(), hash.alg = Ext.getCmp("cb_annotation_f").getValue(), hash.kW = Ext.getCmp("tb_keyword").getValue(),
	//hash.key = Math.floor(Math.random()*1001);
	hash.key = +Date.now();
	property.reconfigure = true;

	createURL();
}

function loadFBCD() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.hasOwnProperty('cat') ? hash.cat : hash.aT ? hash.aT : 0, timeoutId = 0, hiddenCols = property.featureHiddenCols[hash.fT] ? property.featureHiddenCols[hash.fT] : property.featureHiddenCols["ALL"], scm = property.scm;

	SetLoadParameters();

	function setInputs() {
		if (Ext.getCmp("cb_feature_type_f") && Ext.getCmp("cb_feature_type_f").getStore().data.items.length > 0) {
			Ext.getCmp("cb_feature_type_f").setValue(hash.fT);
			Ext.getCmp("cb_annotation_f").setValue(hash.alg);
			Ext.getCmp("tb_keyword").setValue(hash.kW); clearTimeout(timeoutId);
		}
	}

	if (!Ext.getCmp("cb_feature_type_f") && !Ext.getCmp("cb_annotation_f")) {
		timeoutId = setInterval(setInputs, 1000);
	} else {
		setInputs();
	}

	for (var i = 1; i < scm[which].length; i++) {
		scm[which][i]["hidden"] = scm[which][i]["orig_hidden_value"];
		if (hiddenCols.indexOf(scm[which][i]["dataIndex"]) >= 0) {
			scm[which][i]["hidden"] = true;
		}
	}

	for (var i = 1; i < scm[which].length; i++) {
		if (scm[which][i]["dataIndex"] == "annotation") {
			scm[which][i]["hidden"] = (hash.alg != "ALL");
		} else if (scm[which][i]["dataIndex"] == "feature_type") {
			scm[which][i]["hidden"] = (hash.fT != "ALL");
		}
	}

	loadGrid();
}

function getExtraParams() {

	var object = {}, filter = {}, filter2 = {}, Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	filter["gid"] = getGID();

	if (hash.alg && hash.alg.toLowerCase() != "all")
		filter2["annotation"] = hash.alg;

	if (hash.fT && hash.fT.toLowerCase().indexOf("all") < 0)
		filter2["feature_type"] = hash.fT;

	object["Keyword"] = (!hash.kW) ? '(*)' : "(" + hash.kW + ")";

	for (var i in filter)
	object[i] = filter[i];

	for (var i in filter2)
	object[i] = filter2[i];

	if (hash.filter != "hypothetical_proteins" && hash.filter != "functional_proteins")
		object[hash.filter] = "[* TO *]";
	else if (hash.filter == "hypothetical_proteins")
		object["Keyword"] = !object["Keyword"] ? "product:(hypothetical AND protein)" : object["Keyword"] + " AND product:(hypothetical AND protein)";
	else
		object["Keyword"] = !object["Keyword"] ? "!product:(hypothetical AND protein)" : object["Keyword"] + " AND !product:(hypothetical AND protein)";

	Ext.getDom("download_keyword").value = constructKeyword(object, property.name);

	return {
		pk : hash.key,
		need : "featurewofacet",
		keyword : constructKeyword(object, property.name),
		facet : JSON.stringify({
			"facet" : configuration[property.name].display_facets.join(","),
			"facet_text" : configuration[property.name].display_facets_texts.join(",")
		})
	};
}

function CallBack() {
	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, which = hash.aT ? hash.aT : 0;

	if (Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();

	Ext.getDom("grid_result_summary").innerHTML = '<b>' + Page.getStore(which).getTotalCount() + ' features found</b>';

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
