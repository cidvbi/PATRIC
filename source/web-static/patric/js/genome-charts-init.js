
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
  var genericClickHandler, genericMouseoutHandler, genericMouseoverHandler, genomeStatusMouseoverHandler, genomeStatusMouseoutHandler,  numGenomesMouseoutHandler, numGenomesMouseoverHandler, top5MouseoutHandler, top5MouseoverHandler;

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
    return typeof console !== "undefined" && console !== null ? console.log("mouseOver " + i) : void 0;
  };

  genericMouseoutHandler = function(d, i, meta) {
    return typeof console !== "undefined" && console !== null ? console.log("mouseOut " + i) : void 0;
  };

  genomeStatusMouseoverHandler = function(d, i, meta) {
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

  
 
  numGenomesMouseoverHandler = function(d, i, meta) {
    var content, dataset, str;
    $(meta.chartTarget).find(".data-tooltip").remove();
    dataset = $(meta.clickTarget).attr("class");
    if (dataset === "point-total") {
      content = "Total: " + d.total;
    } else if (dataset === "point-sequenced") {
      content = "Sequenced: " + d.sequenced;
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
  
  

  numGenomesMouseoutHandler = function(d, i, meta) {
    return $(meta.chartTarget).find(".data-tooltip").remove();
  };

  /*
  Top5 custom handlers
  */


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
    tipWidth = barWidth - 4;
    return $(meta.chartTarget).append(str).find(".data-tooltip").css("width", tipWidth).addClass("top5").position({
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
      target: "#genomeStatus",
      datafile: "/patric-common/data/genomeData.json",
      headerSelector: "#genomeStatus-header h3",
      clickHandler: genericClickHandler,
	  mouseoverHandler: genomeStatusMouseoverHandler,
      mouseoutHandler: genomeStatusMouseoutHandler
    });
    new NumberGenomesChart({
      target: "#numberGenomes",
      datafile: "/patric-common/data/genomeData.json",
      headerSelector: "#numberGenomes-header h3",
      clickHandler: genericClickHandler,
      mouseoverHandler: numGenomesMouseoverHandler,
      mouseoutHandler: numGenomesMouseoutHandler
    });
    top5params = {
      target: "#chart-tab1",
      datafile: "/patric-common/data/genomeData.json",
      headerSelector: "#chart-tab1 h4",
      descSelector: "#chart-tab1 .desc",
      clickHandler: genericClickHandler,
      mouseoverHandler: top5MouseoverHandler,
      mouseoutHandler: top5MouseoutHandler
    };
    return visibleChart = new Top5Chart(top5params, function() {
      var viz_size;
      viz_size = this.drawtarget.chart_size;
      new Top5Chart({
        target: "#chart-tab2",
        datafile: "/patric-common/data/genomeData.json",
        size: viz_size,
        headerSelector: "#chart-tab2 h4",
        descSelector: "#chart-tab2 .desc",
        clickHandler: genericClickHandler,
        mouseoverHandler: top5MouseoverHandler,
        mouseoutHandler: top5MouseoutHandler
      });
	/*
      return new Top5Chart({
        target: "#chart-tab3",
        datafile: "/patric-common/data/genomeData.json",
        size: viz_size,
        headerSelector: "#chart-tab3 h4",
        descSelector: "#chart-tab3 .desc",
        clickHandler: genericClickHandler,
        mouseoverHandler: top5MouseoverHandler,
        mouseoutHandler: top5MouseoutHandler
      });
	*/
    });
  });

}).call(this);
