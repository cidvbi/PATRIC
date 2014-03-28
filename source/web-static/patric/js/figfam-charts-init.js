var fifamDistChart;
(function() {
	var figfamMouseoutHandler, figfamMouseoverHandler, genericClickHandler, genericMouseoutHandler, genericMouseoverHandler;

	//generic handlers
	genericClickHandler = function(d, i, meta) {
		if (typeof console !== "undefined" && console !== null) {
			console.log(d, i);
		}
		return typeof console !== "undefined" && console !== null ? console.log(meta) : void 0;
	};

	genericMouseoverHandler = function(d, i, meta) {
		return typeof console !== "undefined" && console !== null ? console.log("mouseOver i") : void 0;
	};

	genericMouseoutHandler = function(d, i, meta) {
		return typeof console !== "undefined" && console !== null ? console.log("mouseOut i") : void 0;
	};

	// figfam handlers
	figfamMouseoverHandler = function(d, i, meta) {
		var barwidth, content, str;
		$(meta.chartTarget).find(".data-tooltip").remove();
		barwidth = $(meta.clickTarget).attr("width");
		
		//console.log(d, i, meta);
		content = d[meta.set][meta.barindex];
		str = "<div class='data-tooltip'>" + content + "</div>";
		
		/*
			Position the tooltip a little above the chart element that called it. We also add
			the dataset as a class so that we can create different styles for the toolips in
			the CSS file.
		*/
		return $(meta.chartTarget).append(str).find(".data-tooltip").addClass(meta.set).position({
			at: 'top',
			of: $(meta.clickTarget),
			my: 'bottom',
			offset: (barwidth / 2) + " 3"
		});
	};

	figfamMouseoutHandler = function(d, i, meta) {
		return $(meta.chartTarget).find(".data-tooltip").remove();
	};

	// histogram handlers
	histogramMouseoverHandler = function(d, i, meta) {
		var $clickTarget, $ct, barWidth, str, tipWidth;
		$ct = $(meta.chartTarget);
		$ct.find(".data-tooltip").remove();
		str = "<div class='data-tooltip'>" + d.y + "</div>";
		$clickTarget = $(meta.clickTarget);

		/*
			We need to manually get the width since SVG doesn't have width
			the way jQuery expects it.
		*/
		barWidth = $clickTarget.attr("width");
		tipWidth = barWidth;

		return $(meta.chartTarget).append(str).find(".data-tooltip").addClass("top5").position({
			at: 'center',
			of: $(meta.clickTarget),
			my: 'top',
			offset: Math.round(barWidth / 2) + " 5"
		});
	};

	histogramMouseoutHandler = function(d, i, meta) {
		//console.log($(meta.chartTarget).find(".data-tooltip"));
		return $(meta.chartTarget).find(".data-tooltip").remove();
	};


	$().ready(function(event) {
		fifamDistChart = new HistogramChart({
			target: "#dlp-proteinfamilies-dist-genus",
			datafile: "/patric-common/data/proteinfamilies.json",
			headerSelector: "#dlp-proteinfamilies-dist-genus h4",
			descSelector: "#dlp-proteinfamilies-dist-genus .desc",
			clickHandler: genericClickHandler,
			mouseoverHandler: histogramMouseoverHandler,
			mouseoutHandler: histogramMouseoutHandler
		});
		
		new FigFamChart({
			target: "#dlp-proteinfamilies-dist-genera",
			datafile: "/patric-common/data/proteinfamilies.json",
			headerSelector: "#dlp-proteinfamilies-dist-genera-header h3",
			descSelector: "#dlp-proteinfamilies-dist-genera-header .desc",
			clickHandler: genericClickHandler,
			mouseoverHandler: figfamMouseoverHandler,
			mouseoutHandler: figfamMouseoutHandler
		});
		return;
	});
}).call(this);
