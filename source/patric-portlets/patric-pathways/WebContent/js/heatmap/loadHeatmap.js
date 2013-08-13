var heatmapid = "#CompPathwayHeatMap"; 

function loadHeatmap() {
	var swfVersionStr = "10.0.0";
	var xiSwfUrlStr = "/patric-common/js/heatmap/playerProductInstall.swf";
	var psfSwfUrlStr = "/patric-common/js/heatmap/ProteinFamilySorter.swf";
	var destDiv = "flashTarget";
	var heatMapWidth = "100%";
	var heatMapHeight = "596";
	
	var flashvars = {};
	flashvars.showLog = "false";
	flashvars.startColor = "0x6666ff";
	flashvars.endColor = "0x00ff00";
    var params = {};
	params.quality = "high";
    params.bgcolor = "#ffffff";
    params.allowscriptaccess = "sameDomain";
    params.allowfullscreen = "false";
	params.wmode="transparent";
    var attributes = {};
    attributes.id = "CompPathwayHeatMap";
    attributes.name = "CompPathwayHeatMap";
    swfobject.embedSWF(psfSwfUrlStr, destDiv,heatMapWidth,heatMapHeight,swfVersionStr,xiSwfUrlStr,flashvars, params, attributes);
}