
/*
Superclass for all D3 charts. Does some basic error checking, checking for library 
requirements, populates @p with passed properties, and has some convenience methods 
included. 

There's probably a fair amount of stuff in the individual chart files that should
be moved here as the project develops.

## ABOUT self = this

This seems a redundant piece of code, but "this" always refers to the calling object, 
not the class. So when we create anonymous functions as event handlers -- a common pattern
in D3 -- we can't use "this" to access chart properties. "self = this" stores a reference
to the chart object itself, which then can be used in the anonymous function when "this"
could not.
*/


(function() {
  var GenericChart;

  window.GenericChart = GenericChart = (function() {

    function GenericChart(props) {
      if (!(typeof d3 !== "undefined" && d3 !== null)) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("These charts require D3");
        }
        return;
      }
      if (!(props != null)) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("Properties object is missing.");
        }
        return;
      }
      if (!(props != null ? props.target : void 0)) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("Charts require the property 'target'");
        }
      }
      if (!(props != null ? props.datafile : void 0)) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("Charts require the property 'datafile'");
        }
      }
      /* 
      		Initialize some critical class variables.
      */

      this.p = props;
    }

    /*
    	This is a convenience function for getting the size 
    	of an HTML element on the page.
    */


    GenericChart.prototype.getContainerSize = function(selectorString) {
      var $container, s_container;
      $container = $(selectorString + " .chart");
      if ($container.length === 0) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("Cannot find chart for selector (" + selectorString + " .chart)");
        }
        s_container = {
          width: 0,
          height: 0
        };
      } else {
        s_container = {
          width: $container.width(),
          height: $container.height()
        };
      }
      return s_container;
    };

    /*
    	This is a convenience function for making an SVG draw target
    	
    	params:
    		target // The ID of the container
    		multiplier // The multiplier of the target width. if null, actual height is used
    		margins [] // Array: top, right, bottom, left. Like CSS
    		size [] // Object with keys: width, height
    	
    	returns:
    		chart,canvas,canvas_size -- d3 selection objects
    */


    GenericChart.prototype.prepareSVGDrawTarget = function(params) {
      var canvas, chart, returnable, s_chart, s_chartCanvas, s_margins;
      if (!(params.size != null)) {
        s_chart = this.getContainerSize(params.target);
      } else {
        s_chart = params.size;
      }
      if ((params.margins != null)) {
        s_margins = params.margins;
      } else {
        s_margins = [0, 0, 0, 0];
      }
      s_chartCanvas = {
        width: s_chart.width - s_margins[1] - s_margins[3],
        height: s_chart.height - s_margins[0] - s_margins[2]
      };
      chart = d3.select(params.target + " .chart").insert("svg", ":first-child").attr("class", "svgChartContainer").attr("width", s_chart.width).attr("height", s_chart.height);
      canvas = chart.append("g").attr("class", "svgChartCanvas").attr("width", s_chartCanvas.width).attr("height", s_chartCanvas.height).attr("transform", "translate(" + s_margins[3] + "," + s_margins[0] + ")");
      returnable = {
        "chart": chart,
        "canvas": canvas,
        "canvas_size": s_chartCanvas,
        "chart_size": s_chart
      };
      return returnable;
    };

    return GenericChart;

  })();

}).call(this);

/*
This is a bar chart which we are going to handle using 
divs and CSS rather than svg. Since this is a sort of 
progress / completion bar, we can float the divs left
to make sure they touch each other. If we were using svg,
we would have to calculate that manually. 

Depending on how browsers handle rounding errors, however,
we may want to do that anyway.
*/


(function() {
  var GenomeStatusChart,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.GenomeStatusChart = GenomeStatusChart = (function(_super) {

    __extends(GenomeStatusChart, _super);

    function GenomeStatusChart(props) {
      var self;
      GenomeStatusChart.__super__.constructor.call(this, props);
      self = this;
      d3.json(this.p.datafile, function(data) {
        return self.prepChart(data.genomeStatus);
      });
    }

    GenomeStatusChart.prototype.prepChart = function(response) {
      var bars, chart, chartCanvas, chartTitle, dataset, desc, s_chart, self, tabTitle, x_scale;
      self = this;
      s_chart = this.getContainerSize(this.p.target);
      
	  dataset = response.data;
	  
      chartTitle = response.chart_title;
      tabTitle = response.tab_title;
      desc = response.chart_desc;
      /*
      		set headlines, etc
      */

      $(this.p.headerSelector).text(chartTitle);
      $(this.p.descSelector).text(desc);
      /*
      		Look for an associated tab
      */

      $("[href=" + this.p.target + "]").text(tabTitle);
      chartCanvas = d3.select(this.p.target + " .chart").append("div").attr("class", "chartContainer").style("height", s_chart.height + "px").append("div").attr("class", "chartCanvas group").style("width", "100%");
      /*
      		If the container has no height specified, set a default
      		height of 66% of the width.
      */

      if (!(s_chart.height != null) || s_chart.height === 0) {
        s_chart.height = s_chart.width * .667;
      }
      /*
      		There's only one scale, and that is the 
      		horizontal scale. Here we build that by
      		adding the values of the array together
      		and mapping them onto a range of 0 - 100,
      		making a percent out of an arbitrary array.
      */

      x_scale = d3.scale.linear().range([0, s_chart.height]).domain([
        0, d3.sum(dataset, function(d, i) {
          return d.value;
        })
      ]);
      /*
      		And now, create the chart elements.
      		Remember, our scale expresses the 
      		values as percentages, so we have to 
      		add the percent symbol in the CSS 
      		declaration.
      */

      chart = chartCanvas.selectAll("div").data(dataset).enter();
      /*
      		Most of the appearance of the chart
      		is handled in the associated CSS file.
      		are are just setting height and width here.
      */

      chart.append("div").attr("class", function(d, i) {
        return "gsc_bar " + d.m_label;
      }).style("height", function(d, i) {
        return x_scale(d.value) + "px";
      }).on("click", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.clickHandler != null) {
		if(d.label == "Whole Shotgun Sequence")
		{
		console.log("WGS");
		//Ext.getDom("keyword".value = "WGS";
		}
		else
		{
		console.log(d.label);
		//Ext.getDom("keyword").value = d.label;
		}
		console.log("genome_status");

			//Ext.getDom("search_on").value = "genome_status";
          //searchbykeyword('', 'taxon');
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
      bars = chartCanvas.selectAll(".gsc_bar").data(dataset);
      /*
      		Add the text items to the div.
      */

      bars.append("span").attr("class", "value").text(function(d, i) {
        return d.reported;
      });
      return bars.append("span").attr("class", "reportedLabel").text(function(d, i) {
        return d.label;
      });
    };

    return GenomeStatusChart;

  })(GenericChart);

}).call(this);

/*
A line chart that supports / expects two values.
*/


(function() {
  var NumberGenomesChart,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.NumberGenomesChart = NumberGenomesChart = (function(_super) {

    __extends(NumberGenomesChart, _super);

    function NumberGenomesChart(props) {
      var self;
      NumberGenomesChart.__super__.constructor.call(this, props);
      self = this;
      d3.json(this.p.datafile, function(data) {
        return self.prepChart(data.numberGenomes);
      });
    }

    NumberGenomesChart.prototype.prepChart = function(response) {
      var canvas, chart, chartTitle, datagroups, dataset, genome_axis, genome_scale, maxGenomes, maxYear, minYear, s_chart, s_chartCanvas, s_margins, self, sequenced_line, total_line, year_axis, year_scale, _i, _results;
      chartTitle = response.chart_title;
      dataset = response.data;
      self = this;
      $(this.p.headerSelector).text(chartTitle);
      s_chart = this.getContainerSize(this.p.target);
      if (s_chart.height === 0) {
        s_chart.height = Math.round(s_chart.width * .66);
      }
      s_margins = {
        top: 6,
        left: 40,
        right: 6,
        bottom: 25
      };
      s_chartCanvas = {
        width: s_chart.width - s_margins.left - s_margins.right,
        height: s_chart.height - s_margins.top - s_margins.bottom
      };
	  
      /*
      		Set up the y scale, which is the number of genomes
      */

      maxGenomes = d3.max(dataset, function(d, i) {
        return d.total;
      });
      genome_scale = d3.scale.linear().range([s_chartCanvas.height, 0]).domain([0, maxGenomes]);
      /*
      		Create a line routine so we can join the dots.
      */

      sequenced_line = d3.svg.line().x(function(d) {
        return year_scale(d.year);
      }).y(function(d) {
        return genome_scale(d.sequenced);
      });
      total_line = d3.svg.line().x(function(d) {
        return year_scale(d.year);
      }).y(function(d) {
        return genome_scale(d.total);
      });
      /*
      		Set up the x scale, which is the year
      */

      maxYear = d3.max(dataset, function(d, i) {
        return d.year;
      });
      minYear = d3.min(dataset, function(d, i) {
        return d.year;
      });
      year_scale = d3.scale.linear().range([0, s_chartCanvas.width]).domain([minYear - 1, maxYear + 1]);
	  
      /*
      		Set up the x and y axii
      		negative tick-size the width of the chart results in horizontal rules
      */

      genome_axis = d3.svg.axis().scale(genome_scale).orient("left").tickSubdivide(1).tickSize(-s_chartCanvas.width).tickPadding(15);
      year_axis = d3.svg.axis().scale(year_scale).ticks(dataset.length + 2).tickSize(0).tickPadding(15).tickValues((function() {
        _results = [];
        for (var _i = minYear; minYear <= maxYear ? _i <= maxYear : _i >= maxYear; minYear <= maxYear ? _i++ : _i--){ _results.push(_i); }
        return _results;
      }).apply(this)).tickFormat(d3.format("d")).orient("bottom");
      /*
      		Create the chart container...
      */

      chart = d3.select(this.p.target + " .chart").append("svg").attr("class", "svgChartContainer").attr("width", s_chart.width).attr("height", s_chart.height);
      /* 
      		Add a background rect to give the drawn chart
      		area a different color background.
      */

      chart.append("rect").attr("width", s_chartCanvas.width + 2).attr("height", s_chartCanvas.height + 2).attr("class", "chart-background").attr("transform", "translate(" + (s_margins.left - 1) + "," + (s_margins.top - 1) + ")");
      
	  /*
      		Add the chart canvas over that.
      */

      canvas = chart.append("g").attr("class", "svgChartCanvas").attr("width", s_chartCanvas.width).attr("height", s_chartCanvas.height).attr("transform", "translate(" + s_margins.left + "," + s_margins.top + ")");
      /*
      		Add X and Y axis
      */

      canvas.append("g").attr("class", "y axis").call(genome_axis);
      canvas.append("g").attr("class", "x axis").call(year_axis);
      /*
      		Move the X Axis into place.
      */

      canvas.select(".x.axis").attr("transform", "translate(0," + (s_chart.height - s_margins.bottom - s_margins.top) + ")");
      datagroups = canvas.selectAll("datapoints").data(dataset);
      /* 
      		Build our lines. These take the entire data set of the line to make points
      		on a path, and then a single path is added to the canvas.
      */

      canvas.append("path").attr("d", total_line(dataset)).attr("class", "total line");
      canvas.append("path").attr("d", sequenced_line(dataset)).attr("class", "sequenced line");
      /* 
      		Now add the rectangles for each data point for both lines.
      */

      datagroups.enter().append("g").attr("class", "datapoints");
      canvas.selectAll(".datapoints").data(dataset).append("rect").attr("class", "point-total").attr("x", function(d, i) {
        return year_scale(d.year) - 3;
      }).attr("y", function(d, i) {
        return genome_scale(d.total) - 3;
      }).attr("width", "6").attr("height", "6").on("click", function(d, i) {
        var meta;
        meta = {
          "set": "point-total",
          "clickTarget": this
        };
        if (self.p.clickHandler != null) {
	
		
			//Ext.getDom("keyword").value = d.year;
			//Ext.getDom("search_on").value = "completion_date";
          //searchbykeyword('', 'taxon');
         // return self.p.clickHandler(d, i, meta);
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
		
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
      return canvas.selectAll(".datapoints").data(dataset).append("rect").attr("class", "point-sequenced").attr("x", function(d, i) {
        return year_scale(d.year) - 3;
      }).attr("y", function(d, i) {
        return genome_scale(d.sequenced) - 3;
      }).attr("width", "6").attr("height", "6").on("click", function(d, i) {
        var meta;
        meta = {
          "set": "point-sequenced",
          "clickTarget": this
        };
        if (self.p.clickHandler != null) {
          return self.p.clickHandler(d, i, meta);
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
    };

    return NumberGenomesChart;

  })(GenericChart);

}).call(this);

/*
This is a vertical bar chart. It's complicated by having optional icons
and a mechanism for "breaking" the bar if the bar is too tall. The 
'Reported' value is the value used internally for setting bar height.
If 'Reported' is present and < 'value', a "break" image element will
be added and the real value reported above the bar.
*/


(function() {
  var Top5Chart,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.Top5Chart = Top5Chart = (function(_super) {

    __extends(Top5Chart, _super);

    /*
    	The Top 5 charts are actually multiple top 5 charts
    	with data sets switched out based on user interaction.
    
    	Since this chart has state, we need some variables to 
    	hold that state in.
    */


    function Top5Chart(props, callback) {
      var self;
      Top5Chart.__super__.constructor.call(this, props);
      this.finished = callback || function() {};
      self = this;
	  var name = this.p.target;
	  var chartdata;
	  
	
	  
		 d3.json(this.p.datafile, function(data) {
		 
			if(name == "#chart-tab1")
			{
				chartdata = data.top5_1;
			}
			else if(name == "#chart-tab2")
			{
				if(data.top5_2 == undefined)
				{
					chartdata = JSON.parse(data.responseText).top5_2;
				}
				else
				{
					chartdata = data.top5_2;
				}
			}
			else
			{
				if(data.top5_3 == undefined)
				{
					chartdata = JSON.parse(data.responseText).top5_3;
				}
				else
				{
					chartdata = data.top5_3;
				}
			}	
        return self.prepChart(chartdata);
      });
	
    }

    Top5Chart.prototype.prepChart = function(response) {
      var bars, breakstr, canvas, chart, chartTitle, chartheight, chartwidth, data, desc, drawtargetParams, self, tabTitle, upperBound, xScale, yScale;
      self = this;
      data = response.data;
      chartTitle = response.chart_title;
      tabTitle = response.tab_title;
      desc = response.chart_desc;
      $(this.p.headerSelector).text(chartTitle);
      $(this.p.descSelector).text(desc);
      $("[href=" + this.p.target + "]").text(tabTitle);
      breakstr = "M 0 6 L 6 0 L 12 6 L 18 0 L 24 6 L 30 0 L 36 6 L 42 0 L 48 6 L 54 0";
      breakstr += "L 54 28 L 48 22 L 42 28 L 36 22 L 30 28 L 24 22 L 18 28 L 12 22 L 6 28 L 0 22 Z";
      drawtargetParams = {
        target: this.p.target,
        margins: [22, 0, 0, 0],
        size: this.p.size
      };
      this.drawtarget = this.prepareSVGDrawTarget(drawtargetParams);
      chart = this.drawtarget.chart;
      canvas = this.drawtarget.canvas;
      chartheight = this.drawtarget.canvas_size.height;
      chartwidth = this.drawtarget.canvas_size.width;
      upperBound = d3.max(data, function(d, i) {
        return d.reported || d.value;
      });
      yScale = d3.scale.linear().range([0, chartheight]).domain([0, upperBound]);
      xScale = d3.scale.linear().range([0, chartwidth]).domain([0, data.length]);
      bars = canvas.selectAll("g.bar").data(data);
      bars.enter().append("g").attr("class", function(d, i) {
        return "bar " + d.m_label;
      });
      bars.append("rect").attr("class", function(d, i) {
        return "bar-" + i;
      }).attr("height", function(d, i) {
        var val;
        val = d.reported || d.value;
        return yScale(val);
      }).attr("width", Math.floor(xScale(.8))).attr("x", function(d, i) {
        return (i * xScale(1)) + xScale(.1);
      }).attr("y", function(d, i) {
        var val;
        val = d.reported || d.value;
        return chartheight - yScale(val);
      }).on("click", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.clickHandler != null) {
          return self.p.clickHandler(d, i, meta);
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
      bars.append("text").attr("class", function(d, i) {
        return "label label-" + i;
      }).attr("x", function(d, i) {
        return (i * xScale(1)) + xScale(.5);
      }).attr("y", function(d, i) {
        var val;
        val = d.reported || d.value;
        return chartheight - yScale(val) - 6;
      }).attr("text-anchor", "middle").text(function(d) {
        return d.value;
      }).on("click", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.clickHandler != null) {
		if(response.chart_title == "Bacterial Host")
		{
          //Ext.getDom("keyword").value = d.value;
		  //Ext.getDom("search_on").value = "host_name";
          //searchbykeyword('', 'taxon');
		 }
		 if(response.chart_title == "Isolation Country")
		 {
		  //Ext.getDom("keyword").value = d.value;
		  //Ext.getDom("search_on").value = "isolation_country";
          //searchbykeyword('', 'taxon');
			
		 }
	
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
      bars.select(function(d) {
        if (d.icon != null) {
          return this;
        } else {
          return null;
        }
      }).append("image").attr("xlink:href", function(d) {
        return d.icon;
      }).attr("class", "icon").attr("preserveAspectRatio", "xMinYMax").attr("height", function(d, i) {
        var val;
        val = d.reported || d.value;
        return yScale(val);
      //}).attr("width", Math.floor(xScale(.8))).attr("x", function(d, i) {
      }).attr("width", Math.floor(xScale(.7))).attr("x", function(d, i) {
        //return (i * xScale(1)) + xScale(.1);
        return (i * xScale(1)) + xScale(.1) + 3;
      }).attr("y", function(d, i) {
        var val;
        val = d.reported || d.value;
        //return chartheight - yScale(val);
        return chartheight - yScale(val) + 3;
      }).on("click", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.clickHandler != null) {
		
        if(response.chart_title == "Bacterial Host")
		{
		console.log(d.label);
          //Ext.getDom("keyword").value = d.label;
		  //Ext.getDom("search_on").value = "host_name";
          //searchbykeyword('', 'taxon');
		 }
		 if(response.chart_title == "Isolation Country")
		 {
		 
		  //Ext.getDom("keyword").value = d.label;
		  //Ext.getDom("search_on").value = "isolation_country";
          //searchbykeyword('', 'taxon');
			
		 }
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      });
      bars.select(function(d) {
        if ((d.reported != null) && d.reported !== d.value) {
          return this;
        } else {
          return null;
        }
      }).append("path").attr("d", breakstr).attr("class", "sawtooth").attr("transform", function(d, i) {
        var scaleFactor, xpos, ypos;
        xpos = (i * xScale(1)) + xScale(.1);
        ypos = chartheight - yScale(d.reported / 2) - 14;
        scaleFactor = xScale(.8) / 54;
        return "translate(" + xpos + "," + ypos + ") scale(" + scaleFactor + ")";
      });
      this.finished();
      return "";
    };

    return Top5Chart;

  })(GenericChart);

}).call(this);

/*
FigFamChart

Complicated bar chart. This chart isn't very portable -- it assumes a lot about 
the HTML environment, including available controls. If controls are missing,
there should not be any problem, but the class names really need to match what's
in the source code here. 

This chart is a good example of things you can do with transitions, datasets, and
other interactivity with D3.
*/


(function() {
  var FigFamChart,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.FigFamChart = FigFamChart = (function(_super) {

    __extends(FigFamChart, _super);

    function FigFamChart(props) {
      var self;
      FigFamChart.__super__.constructor.call(this, props);
      self = this;
      d3.json(this.p.datafile, function(data) {
        return self.prepChart(data.FIGfams);
      });
    }

    /* 
    	Set up and build the chart.
    */


    FigFamChart.prototype.prepChart = function(response) {
      var $targ, barset, chart, chartTitle, dataset, datasetToggle, datum, desc, drawtarget, drawtargetParams, i, legendYPos, maxvalues, s_chartCanvas, scaleToggle, self, sortToggle, tabTitle, tickValues, yAxis, _fn, _i;
      chartTitle = response.chart_title;
      tabTitle = response.tab_title;
      desc = response.chart_desc;
      dataset = response.data;
      this.currentSort = "index";
      this.currentDataset = "fvh";
      this.chartheight = 0;
      this.ascendSort = true;
      this.normalize = false;
      $(this.p.headerSelector).text(chartTitle);
      $(this.p.descSelector).text(desc);
      $("[href=" + this.p.target + "]").text(tabTitle);
      this.data = (function() {
        var _i, _len, _results;
        _results = [];
        for (i = _i = 0, _len = dataset.length; _i < _len; i = ++_i) {
          datum = dataset[i];
          _results.push((function(datum) {
            var tpf;
            return tpf = {
              "index": i,
              "label": datum.pathogen,
              "genomes": datum.genomes,
              "total": parseInt(datum.total),
              "fvh": [parseInt(datum.functional), parseInt(datum.hypotheticals)],
              "cva": [parseInt(datum.core), parseInt(datum.accessory)]
            };
          })(datum));
        }
        return _results;
      })();
      this.max = d3.max((function() {
        var _i, _len, _ref, _results;
        _ref = this.data;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          datum = _ref[_i];
          _results.push(d3.sum(datum[this.currentDataset]));
        }
        return _results;
      }).call(this));
      /*
      		Set up the draw target params
	
      */

      drawtargetParams = {
        target: this.p.target,
        margins: [10, 0, 0, 30]
      };
      drawtarget = this.prepareSVGDrawTarget(drawtargetParams);
      chart = drawtarget.chart;
      this.canvas = drawtarget.canvas;
      s_chartCanvas = drawtarget.canvas_size;
      this.chartheight = drawtarget.canvas_size.height;
      this.pf_y_scale = d3.scale.linear().range([0, s_chartCanvas.height]).domain([100, 0]);
      this.pf_x_scale = d3.scale.linear().range([0, s_chartCanvas.width]).domain([0, this.data.length]);
      this.legend = chart.append("g").attr("class", "legend");
      legendYPos = this.chartheight - 20;
      this.canvas.selectAll("g.bar").data(this.data).enter().append("g").attr("class", "bar");
      this.bars = this.canvas.selectAll("g.bar").data(this.data);
      self = this;
      /* 
      		Find the nav structures
			
		
      */

      $targ = $(this.p.target);
      scaleToggle = $targ.find(".scale li");
      datasetToggle = $targ.find(".dataset li");
      sortToggle = $targ.find(".sort li");
      /*
      		Set up the scale toggle
      */

      if (scaleToggle != null) {
        scaleToggle.click(function(event) {
          var linkClass, possibleScales;
          possibleScales = ["normalize", "real"];
          linkClass = $(this).attr("class").split(" ").intersect(possibleScales).join("");
          if (linkClass === "head") {
            return;
          }
          scaleToggle.removeClass("active");
          $(this).addClass("active");
          if (linkClass === "normalize") {
            self.normalize = true;
          } else if (linkClass === "real") {
            self.normalize = false;
          }
          self.updateProteinFamilies(self.currentDataset, function() {
            return self.sortProteinFamilies(null);
          });
        });
      }
      /*
      		Set up the dataset toggle
      */

      if (datasetToggle != null) {
        datasetToggle.click(function(event) {
          var linkClass, possibleDatasets;
          datasetToggle.removeClass("active");
          $(this).addClass("active");
          possibleDatasets = ["fvh", "cva"];
          linkClass = $(this).attr("class").split(" ").intersect(possibleDatasets).join("");
          if (linkClass !== "head") {
            return self.updateProteinFamilies(linkClass, function() {
              return self.sortProteinFamilies(null);
            });
          }
        });
      }
      /*
      		Set up the sort controls
      */

      if (sortToggle != null) {
        sortToggle.click(function(event) {
          sortToggle.removeClass("active");
          $(this).addClass("active");
          return self.sortProteinFamilies($(this).attr("class"));
        });
      }
      maxvalues = d3.max(this.data, function(d) {
        var bardata;
        bardata = d[self.currentDataset];
        return bardata.length;
      });
      this.full_barWidth = self.pf_x_scale(1);
      this.drawn_barWidth = this.full_barWidth * .525;
      this.center_correction = (this.full_barWidth - this.drawn_barWidth) / 2;
      tickValues = (function() {
        var _i, _results;
        _results = [];
        for (i = _i = 1; _i <= 10; i = ++_i) {
          _results.push(i * 10);
        }
        return _results;
      })();
      yAxis = d3.svg.axis().scale(this.pf_y_scale).orient("left").tickPadding(0).tickSize(0).tickValues(tickValues);
      chart.append("g").attr("transform", "translate(" + drawtargetParams.margins[3] + "," + drawtargetParams.margins[0] + ")").call(yAxis).attr("class", "y axis");
      /*
      		Now build the bars...
      		This one routine should suffice for both
      */

      _fn = function(barset) {
        self.bars.append("rect").attr("class", "block-" + barset).attr("y", function(d, i) {
          var ancestorheight, bardata;
          bardata = d[self.currentDataset];
          ancestorheight = self.barheight(d3.sum(bardata.slice(0, barset)), d.total) || 0;
          return Math.round(self.chartheight - self.barheight(bardata[barset], d.total) - ancestorheight);
        }).attr("x", function(d, i) {
          return self.barPosition(i);
        }).attr("width", function() {
          return self.barWidth();
        }).attr("height", function(d, i) {
          var bardata;
          bardata = d[self.currentDataset];
          return Math.round(self.barheight(bardata[barset], d.total));
        }).on("click", function(d, i) {
          var meta;
          meta = {
            "set": self.currentDataset,
            "clickTarget": this,
            "chartTarget": self.p.target,
            "barindex": barset
          };
          if (self.p.clickHandler != null) {
            
          }
        }).on("mouseover", function(d, i) {
          var meta;
          meta = {
            "set": self.currentDataset,
            "clickTarget": this,
            "chartTarget": self.p.target,
            "barindex": barset
          };
          if (self.p.mouseoverHandler != null) {
            return self.p.mouseoverHandler(d, i, meta);
          }
        }).on("mouseout", function(d, i) {
          var meta;
          meta = {
            "set": self.currentDataset,
            "clickTarget": this,
            "chartTarget": self.p.target,
            "barindex": barset
          };
          if (self.p.mouseoutHandler != null) {
            return self.p.mouseoutHandler(d, i, meta);
          }
        });
      };
      for (barset = _i = 0; 0 <= maxvalues ? _i < maxvalues : _i > maxvalues; barset = 0 <= maxvalues ? ++_i : --_i) {
        _fn(barset);
      }
      /*
      		Place the text. We have a little height adjustment on the dy
      		to make sure text is centered in the block rather than set
      		along the baseline.
      */

      return this.bars.append("text").text(function(d, i) {
        return d.label;
      }).attr("y", Math.round(this.chartheight - 11)).attr("x", function(d, i) {
        return self.textPosition(i);
      }).attr("transform", function(d, i) {
        var x, y;
        y = Math.round(self.chartheight - 11);
        x = self.textPosition(i);
        return "rotate(270," + x + "," + y + ")";
      }).attr("dy", ".35em");
    };

    /*
    	Used to dynamically calculate the text postion based bar size.
    */


    FigFamChart.prototype.textPosition = function(index) {
      return Math.floor((this.full_barWidth * index) + this.drawn_barWidth);
    };

    /*
    	Figure out the X position of a given bar based on index
    */


    FigFamChart.prototype.barPosition = function(index) {
      return Math.floor(this.full_barWidth * index + this.center_correction);
    };

    /*
    	Get the width of a bar. This is really just returning a property at the moment,
    	but it's possible that we would want more complex logic here later.
    */


    FigFamChart.prototype.barWidth = function() {
      return Math.floor(this.drawn_barWidth);
    };

    /*
    	This chart always expresses values in terms of percentages.
    	So the question is -- which divosor do we use? If we're 
    	normalizing data, percentages are expressed in terms of 
    	percent of the individual whole; if we're using raw values,
    	it's percent of the largest total value.
    
    	We translate from an absolute scale to a percentage scale rather
    	than just calculating new scales because changing the scale
    	functions has nasty animation side-effects.
    	
    	We're subtracting chargheight here because the pf_y_scale
    	function returns the size of the chart area that is *not* 
    	our value, an unfortunate consequence of reversing the label
    	order in the charts. dyd3!
    */


    FigFamChart.prototype.barheight = function(value, total) {
      var divisor;
      divisor = this.normalize || !(total != null) || total === 0 ? total : this.max;
      return this.chartheight - this.pf_y_scale((value / divisor) * 100);
    };

    /*
    	Runs to update the protein family display. Happens on both scale changes
    	and dataset swaps.
    */


    FigFamChart.prototype.updateProteinFamilies = function(incomingDataset, callback) {
      var processed, self;
      if (incomingDataset !== this.currentDataset) {
        this.currentDataset = incomingDataset;
      }
      self = this;
      processed = 0;
      if (self.currentDataset === "fvh") {
        $(this.p.target).find(".legend .bar2-label").text("Hypothetical");
        $(this.p.target).find(".legend .bar1-label").text("Functional");
      } else if (self.currentDataset === "cva") {
        $(this.p.target).find(".legend .bar2-label").text("Accessory");
        $(this.p.target).find(".legend .bar1-label").text("Core");
      }
      this.bars.select("rect.block-0").transition().duration(600).attr("y", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        if (self.currentSort === "hypothetical") {
          return self.chartheight - self.barheight(bardata[0], d.total) - self.barheight(bardata[1], d.total);
        } else {
          return self.chartheight - self.barheight(bardata[0], d.total);
        }
      }).attr("height", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        return self.barheight(bardata[0], d.total);
      });
      return this.bars.select("rect.block-1").transition().each('end', function() {
        processed++;
        if (processed === self.data.length) {
          return callback();
        }
      }).duration(600).attr("y", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        if (self.currentSort === "hypothetical") {
          return self.chartheight - self.barheight(bardata[1], d.total);
        } else {
          return self.chartheight - self.barheight(bardata[0], d.total) - self.barheight(bardata[1], d.total);
        }
      }).attr("height", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        return self.barheight(bardata[1], d.total);
      });
    };

    /*
    	Sort the protein families based on passed criteria. This only sorts
    	the data arrays, it does not change the presentation of the bars. That
    	happens a little later.
    */


    FigFamChart.prototype.sortProteinFamilies = function(sortCriteria) {
      var allowedSorts, self;
      if (sortCriteria == null) {
        sortCriteria = null;
      }
      self = this;
      /*
      		Sort criteria are actually class names. Some scripts monkey with
      		the class names of elements after the fact, so we need to split
      		the class name apart and check for the key words we want.
      */

      if (sortCriteria != null) {
        allowedSorts = ["index", "both", "hypothetical", "functional"];
        sortCriteria = sortCriteria.split(' ').intersect(allowedSorts).join(" ");
        if (sortCriteria === this.currentSort) {
          this.ascendSort = !this.ascendSort;
        } else {
          this.currentSort = sortCriteria;
        }
      }
      /*
      		Index sort is the original order of the data file. 
      
      		Functional sort is the "functional" component, or 0th data element.
      
      		Hypothetical sort is the "hypothetical" component, or 1st data element.
      		Note that if we're sorting on this element, we will also want to change
      		bar stacking order. That's done further down.
      
      		"Both" sorts on the combined values of hypothetical and functional
      		components, or the sum of the values array.
      */

      if (this.currentSort === "index") {
        this.bars.sort(function(a, b) {
          var orderCode;
          orderCode = 0;
          if (a.index < b.index) {
            orderCode = -1;
          } else if (a.index > b.index) {
            orderCode = 1;
          }
          if (!self.ascendSort) {
            orderCode = orderCode * -1;
          }
          return orderCode;
        });
      } else if (this.currentSort === "functional") {
        this.bars.sort(function(a, b) {
          var aValues, bValues, orderCode;
          aValues = self.barheight(a[self.currentDataset][0], a.total);
          bValues = self.barheight(b[self.currentDataset][0], b.total);
          orderCode = 0;
          if (aValues < bValues) {
            orderCode = -1;
          } else if (aValues > bValues) {
            orderCode = 1;
          }
          if (!self.ascendSort) {
            orderCode = orderCode * -1;
          }
          return orderCode;
        });
      } else if (this.currentSort === "hypothetical") {
        this.bars.sort(function(a, b) {
          var aValues, bValues, orderCode;
          aValues = self.barheight(a[self.currentDataset][1], a.total);
          bValues = self.barheight(b[self.currentDataset][1], b.total);
          orderCode = 0;
          if (aValues < bValues) {
            orderCode = -1;
          } else if (aValues > bValues) {
            orderCode = 1;
          }
          if (!self.ascendSort) {
            orderCode = orderCode * -1;
          }
          return orderCode;
        });
      } else if (this.currentSort === "both") {
        this.bars.sort(function(a, b) {
          var aSum, aValues, bSum, bValues, orderCode;
          aValues = a[self.currentDataset];
          bValues = b[self.currentDataset];
          orderCode = 0;
          aSum = self.barheight(d3.sum(aValues), a.total);
          bSum = self.barheight(d3.sum(bValues), b.total);
          if (aSum < bSum) {
            orderCode = -1;
          } else if (aSum > bSum) {
            orderCode = 1;
          }
          if (!self.ascendSort) {
            orderCode = orderCode * -1;
          }
          return orderCode;
        });
      } else {
        if (typeof console !== "undefined" && console !== null) {
          console.log("Unknown sort: " + this.currentSort);
        }
      }
      /* 
      		Animate the 0-element bars into position. The y-value
      		changes if we've changed which bar size to sort by.
      */

      this.bars.select("rect.block-0").transition().duration(600).delay(function(d, i) {
        return 10 * i;
      }).attr("x", function(d, i) {
        return self.barPosition(i);
      }).attr("y", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        if (self.currentSort === "hypothetical") {
          return Math.round(self.chartheight - self.barheight(bardata[0], d.total) - self.barheight(bardata[1], d.total));
        } else {
          return Math.round(self.chartheight - self.barheight(bardata[0], d.total));
        }
      });
      /*
      		Animate the 1-element bars into position. Again, y-value
      		might be updated if our focus is on a different bar than last time.
      */

      this.bars.select("rect.block-1").transition().duration(600).delay(function(d, i) {
        return 15 * i;
      }).attr("x", function(d, i) {
        return self.barPosition(i);
      }).attr("y", function(d, i) {
        var bardata;
        bardata = d[self.currentDataset];
        if (self.currentSort === "hypothetical") {
          return Math.round(self.chartheight - self.barheight(bardata[1], d.total));
        } else {
          return Math.round(self.chartheight - self.barheight(bardata[0], d.total) - self.barheight(bardata[1], d.total));
        }
      });
      /*
      		Animate text into position. Text never changes y position once placed.
      */

      return this.bars.select("text").transition().duration(600).delay(function(d, i) {
        return 10 * i;
      }).attr("x", function(d, i) {
        return self.textPosition(i);
      }).attr("transform", function(d, i) {
        var x, y;
        y = self.chartheight - 11;
        x = self.pf_x_scale(i) + self.pf_x_scale(1) / 2;
        return "rotate(270," + x + "," + y + ")";
      });
    };

    return FigFamChart;

  })(GenericChart);

}).call(this);
(function() {
  var PieSample,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.PieSample = PieSample = (function(_super) {

    __extends(PieSample, _super);

    function PieSample(props) {
      var self;
      PieSample.__super__.constructor.call(this, props);
      self = this;
      this.data = d3.range(10).map(Math.random);
      this.prepChart();
    }

    PieSample.prototype.prepChart = function(response) {
      var arc, arcs, chart, color, drawtarget, drawtargetParams, pie, r, s_chartCanvas;
      drawtargetParams = {
        target: this.p.target,
        multiplier: 1,
        margins: [6, 40, 6, 40]
      };
      drawtarget = this.prepareSVGDrawTarget(drawtargetParams);
      chart = drawtarget.chart;
      this.canvas = drawtarget.canvas;
      s_chartCanvas = drawtarget.canvas_size;
      this.chartheight = drawtarget.canvas_size.height;
      r = s_chartCanvas.width * .5;
      color = d3.scale.category20c();
      pie = d3.layout.pie();
      arc = d3.svg.arc().outerRadius(r).innerRadius(0);
      this.canvas.attr("translate(" + r + "," + r + ")");
      if (typeof console !== "undefined" && console !== null) {
        console.log("A");
      }
      this.canvas.data([this.data]);
      arcs = this.canvas.selectAll("g.arc").data(pie).enter().append("g").attr("class", "arc").attr("transform", "translate(" + r + "," + r + ")");
      arcs.append("path").attr("fill", function(d, i) {
        return color(i);
      }).attr("d", arc);
      arcs.append("text").attr("transform", function(d) {
        return "translate(" + arc.centroid(d) + ")";
      }).attr("dy", ".35em").attr("text-anchor", "middle").attr("display", function(d) {
        var _ref;
        return (_ref = d.value > .15) != null ? _ref : {
          "null": "none"
        };
      }).text(function(d, i) {
        return (d.value * 100).toFixed(0);
      }).style("font-size", "11px");
      return typeof console !== "undefined" && console !== null ? console.log("D") : void 0;
    };

    return PieSample;

  })(GenericChart);

}).call(this);

/*
## Prototype charts for VBI

This file sets up and builds prototype charts using the 
d3js library for Patric. It relies on both d3js and 
jQuery.
*/


/*
Sometimes it is helpful to intersect two arrays. I'm extending
the prototype of the native Array object to add this capability
because I have been infected with Objective-C thinking.

This takes an array as an argument and returns the intersection
of the two. The logic is slightly convoluted, but very fast. 

We also check to see if there's already another intersection
routine added by another library or script; if so, then we don't
attempt to redefine it. But behavior that depends on the intersection
logic might be affected.
*/


(function() {

  if (!(Array.prototype.intersect != null)) {
    Array.prototype.intersect = function(a) {
      var e, l, o, p, r, _fn, _i, _j, _len, _len1;
      o = {};
      r = [];
      l = a.length;
      for (_i = 0, _len = a.length; _i < _len; _i++) {
        p = a[_i];
        o[p] = true;
      }
      l = this.length;
      _fn = function() {
        if (o[e] != null) {
          return r.push(e);
        }
      };
      for (_j = 0, _len1 = this.length; _j < _len1; _j++) {
        e = this[_j];
        _fn();
      }
      return r;
    };
  }

  if (!(Array.prototype.max != null)) {
    Array.prototype.max = function() {
      return Math.max.apply(Math, this);
    };
  }

  if (!(Array.prototype.min != null)) {
    Array.prototype.min = function() {
      return Math.min.apply(Math, this);
    };
  }

  /*
  Proper sizing for the popular genomes boxes. Attached to "load" instead of "ready" in order to 
  accomodate possible images loading after everything else.
  */


  $(window).load(function(event) {
    var $genomeData, $genomeList, $popbox, adjust, biggestbox, boxheights, genomeListHeight, targetsize;
    $popbox = $(".popular-box");
    if ($popbox.size() === 0) {
      return;
    }
    $genomeList = $popbox.find(".genome-list");
    genomeListHeight = $genomeList.height();
    $genomeData = $popbox.find(".genome-data");
    boxheights = $genomeData.map(function() {
      return $(this).height();
    }).get();
    biggestbox = boxheights.max();
    /* Find what the largest value is
    */

    targetsize = Math.max(biggestbox, genomeListHeight);
    /*
    	if box-sizing is border-box on the genome data panels (it is currently), we need to adjust our size
    	by the padding value to compensate for jQuery weirdness
    */

    if ($genomeData.css("box-sizing") === "border-box") {
      adjust = parseInt($genomeData.css("padding-top")) + parseInt($genomeData.css("padding-bottom"));
    } else {
      adjust = 0;
    }
    $genomeData.height(targetsize - adjust);
    return $genomeList.height(targetsize);
  });

}).call(this);

/*
This is a vertical bar chart. It's complicated by having optional icons
and a mechanism for "breaking" the bar if the bar is too tall. The 
'Reported' value is the value used internally for setting bar height.
If 'Reported' is present and < 'value', a "break" image element will
be added and the real value reported above the bar.
*/


(function() {
  var HistogramChart,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  window.HistogramChart = HistogramChart = (function(_super) {

    __extends(HistogramChart, _super);

    /*
    	The Top 5 charts are actually multiple top 5 charts
    	with data sets switched out based on user interaction.
    
    	Since this chart has state, we need some variables to 
    	hold that state in.
    */


    function HistogramChart(props, callback) {
      var self;
      HistogramChart.__super__.constructor.call(this, props);
      this.finished = callback || function() {};
      self = this;
	  var name = this.p.target;
	  var chartdata;
	  
	  //console.log(props);
		
		 d3.json(this.p.datafile, function(data) {
		 
				chartdata = data.popularGenomes.popularList[props.dataIndex];
		
        return self.prepChart(chartdata);
      });
	
    }

    HistogramChart.prototype.prepChart = function(response) {
	
	var bars, bg, canvas, chart, chartTitle, datagroups, dataset, genome_axis, genome_scale, maxGenomes, maxYear, minYear, s_chart, s_chartCanvas, s_margins, self, sequenced_line, total_line, year_axis, year_scale, _i, _results;
      chartTitle = response.popularName;
      
	  dataset = response.popularData;
	  
      self = this;
      $(this.p.headerSelector).text(chartTitle);
	  var cont = "#history-tab1";
	   for(var i =0; i <10;i++){
		if($("div.genome-data.right.half.group.ui-tabs-panel.ui-widget-content.ui-corner-bottom")[i].getAttribute("aria-expanded")=="true"){
			cont = "#history-tab" + (i+1);
			break;
		}
	  }
      s_chart = this.getContainerSize(cont);
	  
      if (s_chart.height === 0) {
        s_chart.height = Math.round(s_chart.width * .66);
      }
      s_margins = {
        top: 16,
        left: 40,
        right: 36,
        bottom: 25
      };
	  
      s_chartCanvas = {
        width: s_chart.width - s_margins.left - s_margins.right,
        height: s_chart.height - s_margins.top - s_margins.bottom
      };
	  
	
	  
	 
	  /*
      		Set up the y scale, which is the number of genomes
      */

      maxGenomes = 5000;
      genome_scale = d3.scale.linear().range([s_chartCanvas.height, 0]).domain([0, maxGenomes]);
   
 
      /*
      		Set up the x scale, which is the year
      */
		
      maxYear = 105
      minYear = 10
	  
      year_scale = d3.scale.linear().range([0, s_chartCanvas.width+2]).domain([0, 100]);
	  
      /*
      		Set up the x and y axii
      		negative tick-size the width of the chart results in horizontal rules
      */
	
      genome_axis = d3.svg.axis().scale(genome_scale).orient("left").tickSubdivide(1).tickSize(-s_chartCanvas.width).tickPadding(5);
	  
      year_axis = d3.svg.axis().scale(year_scale).orient("bottom");
	  
      /*
      		Create the chart container...
      */

      chart = d3.select(this.p.target + " .chart").append("svg").attr("class", "svgChartContainer").attr("width", s_chart.width).attr("height", s_chart.height);
      /* 
      		Add a background rect to give the drawn chart
      		area a different color background.
      */

      chart.append("rect").attr("width", s_chartCanvas.width + 2).attr("height", s_chartCanvas.height + 2).attr("class", "chart-background").attr("transform", "translate(" + (s_margins.left - 1) + "," + (s_margins.top - 11) + ")");
      
	  /*
      		Add the chart canvas over that.
      */
	
      canvas = chart.append("g").attr("class", "svgChartCanvas").attr("width", s_chartCanvas.width).attr("height", s_chartCanvas.height).attr("transform", "translate(" + s_margins.left + "," + (s_margins.top - 10) + ")");
      bg = chart.append("g").attr("width", s_chartCanvas.width + 2).attr("height", s_chartCanvas.height + 2).attr("class", "svgChartCanvas2").attr("transform", "translate(" + (s_margins.left - 1) + "," + (s_margins.top - 11) + ")");
	  /*
      		Add X and Y axis
      */
	  
	  canvas.append("g").attr("class", "y axis").call(genome_axis);
      canvas.append("g").attr("class", "x history_axis").call(year_axis);
	  
	  
      /*
      		Move the X Axis into place.
      */

      canvas.select(".x.history_axis").attr("transform", "translate(-1," + (s_chart.height - s_margins.bottom - s_margins.top) + ")");
	  
	  var legend = canvas.append("g").attr("class", "legendaxis").attr("width", "auto").attr("height", "auto");
	  var legend_text = legend.append("text").text("Mycobacterium Protein Families");
	  
	  canvas.select(".legendaxis").attr("transform", "translate(" + ((s_chart.width/3)-(183/3)) + "," + (s_chart.height - s_margins.bottom - s_margins.top + 28) + ")")
	  
	  
      datagroups = canvas.selectAll("datapoints").data(dataset);
      /* 
      		Build our lines. These take the entire data set of the line to make points
      		on a path, and then a single path is added to the canvas.
      */

	  
	  yScale = d3.scale.linear().range([0, s_chartCanvas.height + 2]).domain([0, 5000]);
      xScale = d3.scale.linear().range([0, s_chartCanvas.width + 2]).domain([0, 10]);
	 
	   bars = bg.selectAll("g.bar").data(dataset.reverse());
      bars.enter().append("g").attr("class", function(d, i) {
	  
        return "bar " + d.x;
      });
      bars.append("rect").attr("class", function(d, i) {
        return "bar-" + i;
      }).attr("height", function(d, i) {
        var val;
        val = d.y;
		
        return yScale(val);
      }).attr("width", Math.floor(xScale(.8))).attr("x", function(d, i) {
        return (i * xScale(1)) + xScale(.1);
      }).attr("y", function(d, i) {
        var val;
        val = d.y;
        return s_chartCanvas.height + 2 - yScale(val);
      }).on("click", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.clickHandler != null) {
		
        }
      }).on("mouseover", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoverHandler != null) {
          return self.p.mouseoverHandler(d, i, meta);
        }
      }).on("mouseout", function(d, i) {
        var meta;
        meta = {
          "clickTarget": this,
          "chartTarget": self.p.target
        };
        if (self.p.mouseoutHandler != null) {
          return self.p.mouseoutHandler(d, i, meta);
        }
      }); 
	  
      
    };

    return HistogramChart;

  })(GenericChart);

}).call(this);
