(function() {
  var figfamMouseoutHandler, figfamMouseoverHandler, genericClickHandler, genericMouseoutHandler, genericMouseoverHandler;

  genericClickHandler = function(d, i, meta) {
    if (typeof console !== "undefined" && console !== null) {
      console.log(d);
    }
    if (typeof console !== "undefined" && console !== null) {
      console.log(i);
    }
    return typeof console !== "undefined" && console !== null ? console.log(meta) : void 0;
  };

  genericMouseoverHandler = function(d, i, meta) {
    return typeof console !== "undefined" && console !== null ? console.log("mouseOver i") : void 0;
  };

  genericMouseoutHandler = function(d, i, meta) {
    return typeof console !== "undefined" && console !== null ? console.log("mouseOut i") : void 0;
  };

  figfamMouseoverHandler = function(d, i, meta) {
    var barwidth, content, str;
    $(meta.chartTarget).find(".data-tooltip").remove();
    barwidth = $(meta.clickTarget).attr("width");
    if (typeof console !== "undefined" && console !== null) {
      //console.log(meta);
    }
    if (typeof console !== "undefined" && console !== null) {
      //console.log(d);
    }
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
  

  figfamMouseoutHandler = function(d, i, meta) {
    return $(meta.chartTarget).find(".data-tooltip").remove();
  };

  $().ready(function(event) {
	new HistogramChart({
		target: "#history-tab1",
		datafile: "/patric-common/data/figfamData.json",
		dataIndex: 0,
		headerSelector: "#history-tab1 h4",
		descSelector: "#history-tab1 .desc",
		clickHandler: genericClickHandler,
		mouseoverHandler: histogramMouseoverHandler,
		mouseoutHandler: histogramMouseoutHandler
	});
	new HistogramChart({
		target: "#history-tab2",
		datafile: "/patric-common/data/figfamData.json",
		dataIndex: 1,
		headerSelector: "#history-tab2 h4",
		descSelector: "#history-tab2 .desc",
		clickHandler: genericClickHandler,
		mouseoverHandler: histogramMouseoverHandler,
		mouseoutHandler: histogramMouseoutHandler
	});
	/*
	new HistogramChart({
      target: "#history-tab3",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 2,
      headerSelector: "#history-tab3 h4",
	  descSelector: "#history-tab3 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab4",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 3,
      headerSelector: "#history-tab4 h4",
	  descSelector: "#history-tab4 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab5",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 4,
      headerSelector: "#history-tab5 h4",
	  descSelector: "#history-tab5 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab6",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 5,
      headerSelector: "#history-tab6 h4",
	  descSelector: "#history-tab6 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab7",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 6,
      headerSelector: "#history-tab7 h4",
	  descSelector: "#history-tab7 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab8",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 7,
      headerSelector: "#history-tab8 h4",
	  descSelector: "#history-tab8 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab9",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 8,
      headerSelector: "#history-tab9 h4",
	  descSelector: "#history-tab9 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	new HistogramChart({
      target: "#history-tab10",
      datafile: "/patric-common/data/figfamData.json",
	  dataIndex: 9,
      headerSelector: "#history-tab10 h4",
	  descSelector: "#history-tab10 .desc",
      clickHandler: genericClickHandler,
	  mouseoverHandler: histogramMouseoverHandler,
      mouseoutHandler: histogramMouseoutHandler
    });
	*/
    return new FigFamChart({
      target: "#figfam",
      datafile: "/patric-common/data/figfamData.json",
      headerSelector: "#figfam-header h3",
      descSelector: "#figfam-header .desc",
      clickHandler: genericClickHandler,
      mouseoverHandler: figfamMouseoverHandler,
      mouseoutHandler: figfamMouseoutHandler
    });
  });

}).call(this);
