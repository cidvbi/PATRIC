/*
	Define some mouse handlers for the charts. Rather than define the handlers in the
	charts themselves, I've added the ability to pass a handler function into the charts.
	I did this mostly because click behavior has to be defined by the VBI team, but it
	turns out to be a pretty useful pattern. 

	Mouse events return d, the data element; i, the data index; and meta, an object with
	other useful information. meta.clickTarget is always the element that fired the mouse
	event.
*/

(function() {
	//var genericClickHandler, genericMouseoutHandler, genericMouseoverHandler;
	var genomeStatusClickHandler, genomeStatusMouseoverHandler, genomeStatusMouseoutHandler;
	var numberOfBacterialGenomesClickHandler, numberOfBacterialGenomesMouseoutHandler, numberOfBacterialGenomesMouseoverHandler;
	var top5HostClickHandler, top5IsolationCountryClickHandler, top5MouseoutHandler, top5MouseoverHandler;

	// General Click/MouseOver/MouseOut Handlers
	/*
	genericClickHandler = function(d, i, meta) {
		if (typeof console !== "undefined" && console !== null) {
			console.log(d, i, meta);
		}
		return;
	};
	genericMouseoverHandler = function(d, i, meta) {
		return typeof console !== "undefined" && console !== null ? console.log("mouseOver " + i) : void 0;
	};
	genericMouseoutHandler = function(d, i, meta) {
		return typeof console !== "undefined" && console !== null ? console.log("mouseOut " + i) : void 0;
	};
*/

	// Genome Status Chart
	genomeStatusClickHandler = function(d, i, meta) {
		if (d.label == "Whole Genome Shotgun") {
			window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw=genome_status:WGS','GenomeList');
		} else if (d.label == "Complete") {
			window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw=genome_status:Complete','GenomeList');
		} else {
			window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw=genome_status:Plasmid','GenomeList');
		}
	};
	
	genomeStatusMouseoverHandler = function(d, i, meta) {
		var $clickTarget, $ct, barWidth, str, tipWidth;
		$ct = $(meta.chartTarget);
		$ct.find(".data-tooltip").remove();
		str = "<div class='data-tooltip'>" + d.label + "<br><br><i>Click for list of genomes</i></div>";
		$clickTarget = $(meta.clickTarget);

		/*
			We need to manually get the width since SVG doesn't have width
			the way jQuery expects it.
		*/
		barWidth = $clickTarget.attr("width");
		tipWidth = barWidth - 4;

		$(meta.chartTarget).append(str).find(".data-tooltip").css("width", tipWidth).addClass("status");
		$(document).mousemove(function(ev){
			$(meta.chartTarget).find(".data-tooltip").position({
				my: "left bottom",
				of: ev,
				offset: "3 -3",
				collision: "fit"
			});
		});
		return;
	};

	genomeStatusMouseoutHandler = function(d, i, meta) {
		return $(meta.chartTarget).find(".data-tooltip").remove();
	};

	// Number of Bacterial Genomes
	numberOfBacterialGenomesClickHandler = function(d, i, meta) {
		var dataset = $(meta.clickTarget).attr("class");
		var kw = "";// "completion_date:[2011-01-01T00:00:00Z%20TO%202014-01-01T00:00:00Z]";
		if (dataset === "point-total") {
			//accumulated
			kw = "genome_status_f:WGS+AND+completion_date:[*%20TO%20" + (d.year+1) +"-01-01T00:00:00Z]";
		} else {
			//point-sequenced
			kw = "genome_status_f:Complete+AND+completion_date:[*%20TO%20" + (d.year+1) + "-01-01T00:00:00Z]";
		}
		window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw='+kw,'GenomeList');
	};
	
	numberOfBacterialGenomesMouseoverHandler = function(d, i, meta) {
		var content, dataset, str;
		$(meta.chartTarget).find(".data-tooltip").remove();
		dataset = $(meta.clickTarget).attr("class");
		if (dataset === "point-total") {
			content = "WGS: " + d.wgs;
		} else if (dataset === "point-sequenced") {
			content = "Complete: " + d.complete;
		} else {
			content = "Unknown data set";
		}
		str = "<div class='data-tooltip'>" + content + "</div>";

		/*
			Position the tooltip a little above the chart element that called it. We also add
			the dataset as a class so that we can create different styles for the toolips in
			the CSS file.
		*/

		return $(meta.chartTarget).append(str).find(".data-tooltip").addClass(dataset).position({
			at: 'top',
			of: $(meta.clickTarget),
			my: 'bottom',
			offset: "0 -5"
		});
	};

	numberOfBacterialGenomesMouseoutHandler = function(d, i, meta) {
		return $(meta.chartTarget).find(".data-tooltip").remove();
	};


	// Top5 chart handlers
	top5HostClickHandler = function(d, i, meta) {
		window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw=host_name:'+encodeURIComponent('"'+d.label+'"'),'GenomeList');
	};

	top5IsolationCountryClickHandler = function(d, i, meta) {
		window.open('/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=&kw=isolation_country:'+encodeURIComponent('"'+d.label+'"'),'GenomeList');
	};

	top5MouseoverHandler = function(d, i, meta) {
		var $clickTarget, $ct, barWidth, str, tipWidth;
		$ct = $(meta.chartTarget);
		$ct.find(".data-tooltip").remove();
		str = "<div class='data-tooltip'>" + d.label + "</div>";
		$clickTarget = $(meta.clickTarget);

		/*
			We need to manually get the width since SVG doesn't have width
			the way jQuery expects it.
		*/
		barWidth = $clickTarget.attr("width");
		//tipWidth = barWidth - 4;
		//return $(meta.chartTarget).append(str).find(".data-tooltip").css("width", tipWidth).addClass("top5").position({
		return $(meta.chartTarget).append(str).find(".data-tooltip").addClass("top5").position({
			at: 'center',
			of: $(meta.clickTarget),
			my: 'top',
			offset: Math.round(barWidth / 2) + " 5"
		});
	};

	top5MouseoutHandler = function(d, i, meta) {
		return $(meta.chartTarget).find(".data-tooltip").remove();
	};



	/*
		Here I am just setting up the charts we want to use on this page
		and invoking them. We assume there's a div with a class of `chart`
		as a child of the id for every chart type.
	*/
	$().ready(function(event) {
		var top5params, visibleChart;
	
		new GenomeStatusChart({
			target: "#dlp-genomes-genomeStatus",
			datafile: "/patric-common/data/genomes.json",
			headerSelector: "#dlp-genomes-genomeStatus-header h3",
			clickHandler: genomeStatusClickHandler,
			mouseoverHandler: genomeStatusMouseoverHandler,
			mouseoutHandler: genomeStatusMouseoutHandler
		});
		new NumberGenomesChart({
			target: "#dlp-genomes-numberGenomes",
			datafile: "/patric-common/data/genomes.json",
			headerSelector: "#dlp-genomes-numberGenomes-header h3",
			clickHandler: numberOfBacterialGenomesClickHandler,
			mouseoverHandler: numberOfBacterialGenomesMouseoverHandler,
			mouseoutHandler: numberOfBacterialGenomesMouseoutHandler
		});
		top5params = {
			target: "#dlp-genomes-chart-tab1",
			datafile: "/patric-common/data/genomes.json",
			headerSelector: "#dlp-genomes-chart-tab1 h4",
			descSelector: "#dlp-genomes-chart-tab1 .desc",
			clickHandler: top5HostClickHandler,
			mouseoverHandler: top5MouseoverHandler,
			mouseoutHandler: top5MouseoutHandler
		};
		return visibleChart = new Top5Chart(top5params, function() {
			var viz_size;
			viz_size = this.drawtarget.chart_size;
			new Top5Chart({
				target: "#dlp-genomes-chart-tab2",
				datafile: "/patric-common/data/genomes.json",
				size: viz_size,
				headerSelector: "#dlp-genomes-chart-tab2 h4",
				descSelector: "#dlp-genomes-chart-tab2 .desc",
				clickHandler: top5IsolationCountryClickHandler,
				mouseoverHandler: top5MouseoverHandler,
				mouseoutHandler: top5MouseoutHandler
			});
		});
	});
}).call(this);
