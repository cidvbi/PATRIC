<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String _window = request.getParameter("window");
String _regions = request.getParameter("regions");

String params = "";
if (cType!=null && cId!=null) {
	params = "cType="+cType+"&cId="+cId;
}
if (_window==null || _window.equals("")) {
	_window = "10000";
}
if (_regions == null || _regions.equals("")) {
	_regions = "5";
}
String param_crv = params + "&window="+_window+"&regions="+_regions;
String urlRoot = "/patric-jbrowse/jbrowse/";
%>
<link rel="stylesheet" type="text/css" href="<%=urlRoot%>genome.css">
<script type="text/javascript" src="<%=urlRoot%>src/dojo/dojo.js" data-dojo-config="async: 1, baseUrl: '/patric-jbrowse/jbrowse/src'"></script>
<script type="text/javascript" src="<%=urlRoot%>src/JBrowse/init.js"></script>
<script type="text/javascript">
	window.onerror=function(msg){
	if( document.body )
		document.body.setAttribute("JSError",msg);
	}

	var JBrowse;
	require(['JBrowse/Browser', 'dojo/io-query', 'dojo/json' ],
		function (Browser,ioQuery,JSON) {
			var queryParams = ioQuery.queryToObject( window.location.search.slice(1) );
			var dataRoot = queryParams.data || '/patric-jbrowse/data';
			var config = {
				containerID: "GenomeBrowser",
				refSeqs: "/portal/portal/patric/CompareRegionViewer/CRWindow?action=b&cacheability=PAGE&mode=getRefSeqs&<%=param_crv%>",
				baseUrl: dataRoot + '/',
				browserRoot: '<%=urlRoot%>',
				include: [
					'<%=urlRoot%>jbrowse_conf.json',
					'/portal/portal/patric/CompareRegionViewer/CRWindow?action=b&cacheability=PAGE&mode=getTrackList&<%=param_crv%>'
				],
				nameUrl: dataRoot + "/name.jsp",
				/*defaultTracks: "",*/
				queryParams: queryParams,
				location: queryParams.loc,
				/*forceTracks: queryParams.tracks,*/
				initialHighlight: queryParams.highlight,
				show_nav: queryParams.nav,
				show_tracklist: queryParams.tracklist,
				show_overview: queryParams.overview,
				stores: { url: { type: "JBrowse/Store/SeqFeature/FromConfig", features: [] } },
				makeFullViewURL: function( browser ) {
					return browser.makeCurrentViewURL({ nav: 1, tracklist: 1, overview: 1 });
				},
				updateBrowserURL: true
			};
			//if there is ?addFeatures in the query params,
			//define a store for data from the URL
			if( queryParams.addFeatures ) {
				config.stores.url.features = JSON.parse( queryParams.addFeatures );
			}
			
			// if there is ?addTracks in the query params, add
			// those track configurations to our initial
			// configuration
			if( queryParams.addTracks ) {
				config.tracks = JSON.parse( queryParams.addTracks );
			}
			
			// if there is ?addStores in the query params, add
			// those store configurations to our initial
			// configuration
			if( queryParams.addStores ) {
				config.stores = JSON.parse( queryParams.addStores );
			}
			
			// create a JBrowse global variable holding the JBrowse instance
			JBrowse = new Browser( config );
		}
	);
	
	Ext.onReady(function(){
		//window resize
		Ext.get('GenomeBrowser').setHeight(Math.max(600, Ext.getBody().getViewSize().height-500));
			
		// tab configuraion
		if (Ext.get("tabs_crviewer")!=null) {
			Ext.get("tabs_crviewer").addCls("sel");
		}
	});
	
	function updateCRV() {
		var w = Ext.getDom("window_size").value;
		var r = Ext.getDom("num_regions").value;
		var p = "<%=params%>&window="+w+"&regions="+r+"&tracks=&loc=1.."+w;
		window.location.href = "CompareRegionViewer?"+p;
	}
	
	function downloadCRV() {
		var dataKey = JBrowse.config.tracks[0].dataKey;
		window.location.href= "/portal/portal/patric/CompareRegionViewer/CRWindow?action=b&cacheability=PAGE&mode=downloadInExcel&key="+dataKey;
	}
</script>
	<div id="control" class="center-text">
		<form action="#" id="crv_control">
			<label for="window_size">Window Size</label>
			<select id="window_size" name="window_size">
				<option value="5000" <%=(_window.equals("5000"))?"selected=\"selected\"":"" %>>5,000 bp</option>
				<option value="10000" <%=(_window.equals("10000"))?"selected=\"selected\"":"" %>>10,000 bp</option>
				<option value="15000" <%=(_window.equals("15000"))?"selected=\"selected\"":"" %>>15,000 bp</option>
				<option value="20000" <%=(_window.equals("20000"))?"selected=\"selected\"":"" %>>20,000 bp</option>
			</select>
			<label for="num_regions">Number of Genomes</label>
			<select id="num_regions" name="num_regions">
				<option value="5" <%=(_regions.equals("5"))?"selected=\"selected\"":"" %>>5</option>
				<option value="10" <%=(_regions.equals("10"))?"selected=\"selected\"":"" %>>10</option>
				<option value="15" <%=(_regions.equals("15"))?"selected=\"selected\"":"" %>>15</option>
				<option value="20" <%=(_regions.equals("20"))?"selected=\"selected\"":"" %>>20</option>
				<option value="30" <%=(_regions.equals("30"))?"selected=\"selected\"":"" %>>30</option>
				<option value="40" <%=(_regions.equals("40"))?"selected=\"selected\"":"" %>>40</option>
			</select>
			<input type="button" onclick="updateCRV()" value="update" />
			<input type="button" onclick="downloadCRV()" value="Download in Excel" />
		</form>
	</div>
	<div id="GenomeBrowser" style="height:600px"></div>