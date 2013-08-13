var heatmapid = "#ProteinFamilySorter";

var xiSwfUrlStr;
var psfSwfUrlStr;

function loadHeatmap() {
	xiSwfUrlStr = "/patric-common/js/heatmap/playerProductInstall.swf";
	psfSwfUrlStr = "/patric-common/js/heatmap/ProteinFamilySorter.swf";

	// Set some SWFObject configuration options.
	var swfVersionStr = "10.0.0";
	var destDiv = "flashTarget";
	var heatMapWidth = "100%";
	var heatMapHeight = "747";

	// Set some basic flashvars, but most configuration
	// options are handled by Javascript later on in the game.
	var flashvars = {};
	flashvars.showLog = "false";
	flashvars.startColor = "0x6666ff";
	flashvars.endColor = "0x00ff00";

	// Standard Flash plugin options. If you're having
	// trouble with layering in older versions of IE,
	// you might try setting the wmode to "opaque."
	var params = {};
	params.quality = "high";
	params.bgcolor = "#ffffff";
	params.allowscriptaccess = "sameDomain";
	params.allowfullscreen = "false";
	params.wmode = "transparent";

	// Fire off the SWFObject process with the above
	// parameters and flashvars.
	var attributes = {};
	attributes.id = "ProteinFamilySorter";
	attributes.name = "ProteinFamilySorter";
	swfobject.embedSWF(psfSwfUrlStr, destDiv, heatMapWidth, heatMapHeight, swfVersionStr, xiSwfUrlStr, flashvars, params, attributes);
}

