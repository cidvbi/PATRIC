function CallBack() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, store = Page.getStore(0), grid = Page.getGrid();

	grid.setSortDirectionColumnHeader(hash.sort, hash.dir);
	Ext.getDom("grid_result_summary").innerHTML = ' <b>Out of ' + Ext.getDom("featureList").value.split(",").length + ' genes selected, ' + getFoundCount() + ' genes found in ' + store.totalCount + ' pathways</b>';
}

function getExtraParams() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash;

	return {
		pk : hash.key,
		callType : 'getFeatureTable'
	};
}

function getSelectedFeatures() {

	var Page = $Page, property = Page.getPageProperties(), checkbox = Page.getCheckBox(), sl = checkbox.getSelections(), pid = [sl[0].data.pathway_id], i, na_features;

	for ( i = 0; i < sl.length; i++)
		pid.push(sl[i].data.pathway_id);

	Ext.Ajax.request({
		url : "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		method : 'POST',
		params : {
			map : pid.join(','),
			featureList : Ext.getDom("featureList").value,
			algorithm : "'RAST'"
		},
		success : function(response, opts) {
			na_features = Ext.JSON.decode(response.responseText);
			for ( i = 0; i < na_features.genes.length; i++)
				property.fids.push(na_features.genes[i].genes);
		}
	});
}

function DownloadFile() {"use strict";

	var form = Ext.getDom("fTableForm");

	form.action = "/patric-pathways/jsp/grid_download_handler.jsp", form.target = "", form.fileformat.value = arguments[0];
	getHashFieldsToDownload(form);
	form.submit();
}

function renderPathwayEnrichment(value, metadata, record, rowIndex, colIndex, store) {
	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"gotoPathwayMap('" + record.data.pathway_id + "');\"/>" + record.data.pathway_name + "</a>");
}

function gotoPathwayMap(map) {

	Ext.Ajax.request({
		url : "/portal/portal/patric/TranscriptomicsEnrichment/TranscriptomicsEnrichmentWindow?action=b&cacheability=PAGE",
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getGenomeIds",
			feature_info_id : Ext.getDom("featureList").value,
			map : map
		},
		success : function(rs) {

			Ext.Ajax.request({
				url : "/portal/portal/patric/PathwayFinder/PathwayFinderWindow?action=b&cacheability=PAGE",
				method : 'POST',
				timeout : 600000,
				params : {
					feature_info_id : Ext.getDom("featureList").value,
					sraction : "save_params",
					genomeId : rs.responseText,
					algorithm : "ALL"
				},
				success : function(rs2) {
					document.location.href = "CompPathwayMap?cType=&cId=&dm=featurelist&map=" + map + "&pk=" + rs2.responseText + "&algorithm=PATRIC&feature_info_id=&ec_number=";
				}
			});

		}
	});
}