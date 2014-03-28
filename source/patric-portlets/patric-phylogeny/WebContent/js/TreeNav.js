var selectedGenomes = "";

function flipTreeCheckBox(id, state) {
	(state == "unchecked")?state = "checked":state = "unchecked";
		
	addRemoveGenomes(id, state);
	document.getElementById(id + "_checkbox").innerHTML = "<img src=\"/patric/images/" + state + ".gif\" onclick=\"javascript:flipTreeCheckBox('" + id + "', '" + state + "')\"/>";

}

function addRemoveGenomes(id, state) {

	if (state == "checked") {
		if (selectedGenomes.length == 0)
			selectedGenomes += id;
		else
			selectedGenomes += "," + id;
	} else {
		if (selectedGenomes.indexOf("," + id) < 0) {
			if (selectedGenomes.indexOf(id + ",") == 0) {
				selectedGenomes = selectedGenomes.replace(id + ",", "");
			} else {
				selectedGenomes = selectedGenomes.replace(id, "");
			}
		} else {
			selectedGenomes = selectedGenomes.replace("," + id, "");
		}
	}
}

function clearSelectedGenomes() {
	var genomes = selectedGenomes.split(",");

	for (var i = 0; i < genomes.length; i++) {
		document.getElementById(genomes[i] + "_checkbox").innerHTML = "<img src=\"/patric/images/unchecked.gif\" onclick=\"javascript:flipTreeCheckBox('" + genomes[i] + "', 'unchecked')\"/>";
	}

	selectedGenomes = "";
}

var TreeNav = {
	TreeNav : function(treeDivId) {
		//get the div to be used to hold the tree viewer
		var canvasDiv = document.getElementById(treeDivId);
		var canvas_checkboxes_Div = document.getElementById(treeDivId + "_checkbox");

		//create a canvas element and add it to the div
		var canvas = document.createElement('canvas');
		canvasDiv.appendChild(canvas);

		// if it is IE...
		if ( typeof G_vmlCanvasManager != 'undefined') {
			// you've to reassign the variable to the new element created by initElement
			canvas = G_vmlCanvasManager.initElement(canvas);
		}
		canvas.getContext("2d").font = "10px helvetica";
		var canvasFont = canvas.getContext("2d").font;

		//default font size, in case context.font isn't set
		var fontSize = 10;
		var contextFont = canvas.getContext("2d").font;
		if (contextFont != null) {
			fontSize = parseInt(contextFont.split("px")[0]);
		}

		var labelLinkDiv = document.createElement('div');
		labelLinkDiv.id = "label_div";
		canvasDiv.appendChild(labelLinkDiv);
		labelLinkDiv.style.position = "absolute";
		if (contextFont != null) {
			labelLinkDiv.style.font = contextFont;
		}
		labelLinkDiv.style.top = 20;
		labelLinkDiv.style.left = 20;

		var tipColors = new Array();
		//var tipLinkIds = new Array();
		var tipLinkIds = genomeMap;
		var outgroupColor = "#000000";
		var mainColor = "#000000";
		var backgroundColor = "#FFFFFF";
		var linkColor = "#1B2C49";
		//var linkURLPrefix = "http://www.google.com/search?q=";
		var linkURLPrefix = "Genome?cType=genome&cId=";
		var linkURLSuffix = "";

		var tree = null;

		//fullRainbow holds a 2d array of RGB values for a spectrum of
		//colors used to color tip labels
		var fullRainbow;

		//autocolor determines if any coloring should be done
		//automatically based on taxon names
		var autocolor = true;

		//color species determines if the species name should be colored separately
		//from the genus name. This results in a genus name with one color and
		//the species name with a different color for each species within a
		//colored genus.
		var colorSpecies = false;

		var autoresize = true;
		//var heightPerTip = fontSize;
		var heightPerTip = 14;
		var topMargin = 20;
		var bottomMargin = 20;
		var leftMargin = 20;
		var rightMargin = 350;
		var tipCount;
		var minHeight;
		var maxHeight;
		var treeString;
		var supportValueCutoff = 90;
		var tipYs = new Array();
		var tipXs = new Array();
		var tipLabels = new Array();
		var tipGenera = new Array();
		var tipSpecies = new Array();
		var mouseOverLabel = "";
		var hexCodes = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"];

		if (canvasDiv.addEventListener) {
			canvasDiv.addEventListener("click", mouseClick, false);
			canvasDiv.addEventListener("mousemove", mouseMove, false);
			canvasDiv.addEventListener("mouseout", mouseOut, false);
		} else if (canvasDiv.attachEvent) {
			//need to add listeners for IE
			//IE events are disabled because performance is too bad.
			//IE version doesn't provide links for tip labels
			//		canvasDiv.attachEvent("onclick", mouseClick);
			//		canvasDiv.attachEvent("onmousemove", mouseMove);
			//		canvasDiv.attachEvent("onmouseout", mouseOut);
		} else {
			canvasDiv.onclick = mouseClick;
			canvasDiv.onmousemove = mouseMove;
			canvasDiv.onmouseout = mouseOut;
		}
		var canvasXOffset = canvasDiv.style.left;
		var canvasYOffset = canvasDiv.style.top;

		function mouseOut(e) {
			var relTarg = e.relatedTarget || e.fromElement;
			var source = e.target || e.srcElement;
			if (source == canvasDiv || source.id == "label_link") {
				mouseOverLabel = null;
				labelLinkDiv.innerHTML = "";
			} else if (relTarg.id != "label_link") {
				mouseOverLabel = null;
				labelLinkDiv.innerHTML = "";
			}
			drawTree();
		}

		function mouseMove(e) {
			var mouseX;
			var mouseY;
			var source = e.target || e.srcElement;
			if (source.id == "label_link") {
			} else {
				if (e.offsetX) {
					mouseX = e.offsetX;
					mouseY = e.offsetY;
				} else if (e.layerX) {
					mouseX = e.layerX;
					mouseY = e.layerY;
				}

				var tipAt = getTipAtPoint(mouseX, mouseY);
				if (tipAt != mouseOverLabel) {
					labelLinkDiv.innerHTML = "";
					
Label = tipAt;
					if (tipAt == null) {
					} else {
						var id = tipLinkIds[tipAt];
						if (id == null) {
							id = tipAt;
						}
						var url = linkURLPrefix + id + linkURLSuffix;
					}
					drawTree();
				}
			}
		}

		function mouseClick(e) {
			var mouseX;
			var mouseY;
			if (e.offsetX) {
				mouseX = e.offsetX;
				mouseY = e.offsetY;
			} else if (e.layerX) {
				mouseX = e.layerX;
				mouseY = e.layerY;
			}
			var tipAt = getTipAtPoint(mouseX, mouseY);
			if (tipAt != null) {
				var id = tipLinkIds[tipAt];
				if (id == null) {
					id = tipAt;
				}
				var url = linkURLPrefix + id + linkURLSuffix;
			}
		}

		/**
		 * Sets whether or not the div containing this viewer should be
		 * resized based on the amount of height needed to show the full tree.
		 */
		this.useAutoresize = function(ar) {
			autoresize = ar;
		}
		/**
		 * Sets whether or not to use autocoloring of tip labels based
		 * on inferred genus and species names. Default is true. Call
		 * with ac=false to disable autocolor. If set to false, any
		 * current tip colors will be removed.
		 */
		this.useAutocolor = function(ac) {
			autocolor = ac;
			if (autocolor) {
				if (tree != undefined) {
					this.setTipColors();
					drawTree();
				}
			} else {
				tipColors = new Array();
				if (tree != undefined) {
					drawTree();
				}
			}
		}

		this.useColorSpecies = function(useCS) {
			colorSpecies = useCS;
			this.useAutocolor(useCS);
		}
		/**
		 * Provide an array of names for outgroup members. Names must exactly
		 * match the names as they appear in the tree string. This triggers
		 * a re-rooting of the tree.
		 */
		this.setOutgroup = function(outgroup) {
			for (var i = 0; i < outgroup.length; i++) {
				tipColors[outgroup[i]] = outgroupColor;
			}

			tree.setOutgroup(outgroup);
			tree.ladderizeUp();
			if (autocolor) {
				this.setTipColors();
			}
			drawTree();
		}
		/**
		 * Attempts to automatically assign colors to some tip labels
		 * base on genus and species. Common genera are assigned a color
		 * and common species within a genus are assigned shades of the
		 * genus color. Genus colors are from a hard-coded 7-color
		 * rainbow. Yellow is not included because it doesn't show up well,
		 * making the labels colored in yellow difficult to read.
		 */
		this.setTipColors = function() {
			var genusSets = getGenusSpeciesSets(2);
			var commonGenera = genusSets[0];
			var genusToSpecies = genusSets[1];

			var rainbow = getRGBRainbow(commonGenera.length, 21);
			//		console.log(rainbow);
			var speciesToColor = new Array();

			var length = Math.min(commonGenera.length, rainbow.length);

			if (colorSpecies) {
				for (var i = 0; i < length; i++) {
					var genusColor = this.getColorHex(rainbow[i][0], rainbow[i][1], rainbow[i][2]);
					var speciesInGenus = genusToSpecies[commonGenera[i]];
					var speciesRainbow = getRGBRainbow(speciesInGenus.length + 1, 16);
					var speciesColorShades = this.getColorShades(rainbow[i][0], rainbow[i][1], rainbow[i][2], speciesInGenus.length);
					var sLength = speciesInGenus.length;
					var sColorIndex = 0;
					for (var j = 0; j < sLength && sColorIndex < speciesRainbow.length; j++) {
						var speciesColor = this.getColorHex(speciesRainbow[sColorIndex][0], speciesRainbow[sColorIndex][1], speciesRainbow[sColorIndex][2]);
						if (speciesColor == genusColor) {
							//						console.log("skipping genus color at " + sColorIndex);
							sColorIndex++;
							speciesColor = this.getColorHex(speciesRainbow[sColorIndex][0], speciesRainbow[sColorIndex][1], speciesRainbow[sColorIndex][2]);
						}
						sColorIndex++;
						speciesToColor[speciesInGenus[j]] = [genusColor, speciesColor];

					}
				}
			} else {
				for (var i = 0; i < length; i++) {
					var speciesInGenus = genusToSpecies[commonGenera[i]];
					var genusColor = this.getColorHex(rainbow[i][0], rainbow[i][1], rainbow[i][2]);
					var sLength = speciesInGenus.length;
					for (var j = 0; j < sLength; j++) {
						speciesToColor[speciesInGenus[j]] = genusColor;
					}
				}

			}

			for (var i = tree.tipLabels.length - 1; i >= 0; i--) {
				var fields = tree.tipLabels[i].split("_");
				if (fields.length > 1) {
					var species = fields[0] + "_" + fields[1];
					var speciesColor = speciesToColor[species];
					if (speciesColor != undefined) {
						this.setLabelColor(tree.tipLabels[i], speciesColor);
					}
				}
			}
			//		drawTree();
		}
		function getRGBRainbow(n, offset) {
			var r;
			var rainbowLength;
			var fullRainbow = new Array();
			fullRainbow[0] = [255, 0, 0];
			fullRainbow[1] = [255, 51, 0];
			fullRainbow[2] = [255, 102, 0];
			fullRainbow[3] = [255, 153, 0];
			fullRainbow[4] = [0, 255, 0];
			fullRainbow[5] = [1, 255, 51];
			fullRainbow[6] = [0, 255, 102];
			fullRainbow[7] = [0, 255, 153];
			fullRainbow[8] = [0, 153, 255];
			fullRainbow[9] = [1, 102, 255];
			fullRainbow[10] = [0, 51, 255];
			fullRainbow[11] = [0, 0, 255];
			fullRainbow[12] = [51, 0, 255];
			fullRainbow[13] = [102, 0, 255];
			fullRainbow[14] = [153, 0, 255];
			fullRainbow[15] = [204, 0, 255];
			fullRainbow[16] = [255, 0, 255];
			fullRainbow[17] = [255, 0, 204];
			fullRainbow[18] = [255, 0, 153];
			fullRainbow[19] = [255, 0, 102];
			fullRainbow[20] = [255, 0, 51];

			rainbowLength = fullRainbow.length;
			var spacing = Math.floor(rainbowLength / (n));
			spacing = Math.max(spacing, 1);
			var subRainbow = new Array();
			for (var i = 0; i < n; i++) {
				var index = spacing * i;
				index = (index + offset) % rainbowLength;
				subRainbow[i] = fullRainbow[index];
			}

			//populate return array with non-adjacent entries from subRainbow
			r = new Array();
			var subLength = subRainbow.length;
			var desiredDistance = Math.ceil(subLength / 3);
			var indexUsed = new Array();
			var index = 0;
			for (var i = 0; i < subLength; i++) {
				while (indexUsed[index]) {
					index++;
				}
				r[i] = subRainbow[index];
				indexUsed[index] = true;
				index = (index + desiredDistance) % subLength;
			}

			return r;
		}

		/*function getRGBRainbow(n, offset) {
		 var r;
		 var values = [255,204,153,102,51,0];
		 var rainbowLength;
		 if(fullRainbow == null) {
		 fullRainbow = new Array();

		 var valueCount = values.length;
		 var rainbowIndex = 0;
		 var high = [0,0,0,0,0];
		 var low =  [5,5,5,5,5];
		 var rise = [5,4,3,2,1];
		 var fall = [0,1,2,3,4];

		 var rPattern = high.concat(fall,low,low,rise,high);
		 var gPattern = rise.concat(high,high,fall,low,low);
		 var bPattern = low.concat(low,rise,high,high,fall);

		 rainbowLength = rPattern.length;
		 for(var i = 0; i < rainbowLength; i++) {
		 if(!(rPattern[i] < 2 && gPattern[i] < 2)) { //avoid yellows
		 fullRainbow[rainbowIndex] =
		 [values[rPattern[i]], values[gPattern[i]], values[bPattern[i]]];
		 rainbowIndex++;
		 }
		 }
		 }

		 rainbowLength = fullRainbow.length;
		 var spacing = Math.floor(rainbowLength / (n));
		 spacing = Math.max(spacing, 1);
		 var subRainbow = new Array();
		 for(var i = 0; i < n; i++) {
		 var index = spacing * i;
		 index = (index + offset) % rainbowLength;
		 subRainbow[i] = fullRainbow[index];
		 }

		 //populate return array with non-adjacent entries from subRainbow
		 r = new Array();
		 var subLength = subRainbow.length;
		 var desiredDistance = Math.ceil(subLength/3);
		 var indexUsed = new Array();
		 var index = 0;
		 for(var i = 0; i < subLength; i++) {
		 while(indexUsed[index]) {
		 index++;
		 }
		 r[i] = subRainbow[index];
		 indexUsed[index] = true;
		 index = (index + desiredDistance) % subLength;
		 }

		 return r;
		 }
		 */
		this.getColorShades = function(r, g, b, shades) {
			//		console.log("getColorShades() " + r + " " + g + " " + b + " " + shades);
			var colorShades = new Array();

			var rInc = Math.floor(r / (shades * 1.5));
			var gInc = Math.floor(g / (shades * 1.5));
			var bInc = Math.floor(b / (shades * 1.5));
			//		console.log("rInc: " + rInc + " gInc: " + gInc + " bInc: " + bInc);

			colorShades[0] = this.getColorHex(r, g, b);
			for (var i = 1; i < shades; i++) {
				var divisor = shades - i + 1;
				colorShades[i] = this.getColorHex(r - rInc * i, g - gInc * i, b - bInc * i);
			}
			//		console.log("colorShades " + colorShades);
			return colorShades;
		}
		/**
		 * Converts integer R, G, and B values to hex color strings.
		 */
		this.getColorHex = function(r, g, b) {
			//get values for r
			var r1 = Math.floor(r / 16) % 16;
			var r2 = r % 16;

			//get values for g
			var g2 = g % 16;
			var g1 = Math.floor(g / 16) % 16;

			//get values for b
			var b2 = b % 16;
			var b1 = Math.floor(b / 16) % 16;

			//assemble the hex string
			var hex = "#" + hexCodes[r1] + hexCodes[r2] + hexCodes[g1] + hexCodes[g2] + hexCodes[b1] + hexCodes[b2];
			return hex;
		}
		/**
		 * Sets the color for outgroup labels. The default color is red
		 * (#FF0000).
		 */
		this.setOutgroupColor = function(color) {
			outgroupColor = color;
		}
		/**
		 * Set the color for a specific label. Must exactly match the label
		 * as it appears in the tree string.
		 */
		this.setLabelColor = function(label, color) {
			tipColors[label] = color;
		}
		/**
		 * Set the id to be used in the link for the specified label. This id
		 * will be placed between the link prefix and link suffix to generate
		 * the url link for the label.
		 */
		this.setLabelLinkId = function(label, id) {
			tipLinkIds[label] = id;
		}
		/**
		 * Set the url prefix for label links.
		 */
		this.setLinkURLPrefix = function(prefix) {
			linkURLPrefix = prefix;
		}
		/**
		 * Set the url suffix for label links.
		 */
		this.setLinkURLSuffix = function(suffix) {
			linkURLSuffix = suffix;
		}
		function getGenusSpeciesSets(minToInclude) {
			var genusSets = new Array();
			var genusToSpecies = new Array();
			var genusToSpeciesSeen = new Array();
			var speciesSets = new Array();
			var uniqueList = new Array();

			var tipList = tree.getTipLabels();
			for (var i = tipList.length - 1; i >= 0; i--) {
				var fields = tipList[i].split("_");
				var genus = fields[0];
				var species = genus;
				if (fields.length > 1) {
					species = fields[0] + "_" + fields[1];
				}

				if (genusSets[genus] == undefined) {
					uniqueList.push(genus);
					genusSets[genus] = new Array();
					genusSets[genus].push(tipList[i]);
					genusToSpecies[genus] = new Array();
					genusToSpeciesSeen[genus] = new Array();
					genusToSpecies[genus].push(species);
					genusToSpeciesSeen[genus][species] = 1;
				} else {
					genusSets[genus].push(tipList[i]);
					if (genusToSpeciesSeen[genus][species] == undefined) {
						genusToSpecies[genus].push(species);
						genusToSpeciesSeen[genus][species] = 1;
					} else {
						genusToSpeciesSeen[genus][species]++;
					}
				}
			}

			//sort genera by occurrence
			function sortGeneraFunc(a, b) {
				var r = genusSets[b].length - genusSets[a].length;
				return r;
			}

			uniqueList = uniqueList.sort(sortGeneraFunc);

			//remove any genera that don't occur the required number of times
			for (var i = uniqueList.length - 1; i >= 0; i--) {
				if (genusSets[uniqueList[i]].length < minToInclude) {
					uniqueList.splice(i, 1);
				} else {
					//sort species within genus by occurrence
					//sort species by occurrence
					function sortSpeciesFunc(a, b) {
						var r = genusToSpeciesSeen[uniqueList[i]][b] - genusToSpeciesSeen[uniqueList[i]][a];
						return r;
					}

					genusToSpecies[uniqueList[i]] = genusToSpecies[uniqueList[i]].sort(sortSpeciesFunc);
				}
			}

			return [uniqueList, genusToSpecies];
		}

		/**
		 * Set the tree to be displayed. Must be a valid newick-format tree.
		 * Branch lengths and branch support values are optional.
		 */
		this.setTree = function(t) {
			treeString = t;
			tipColors = new Array();
			//maintain current display mode for new tree
			var displayMode;
			if (tree != null) {
				displayMode = tree.treeDisplayMode;
			}
			tree = null;
			refreshTree();
			//get tree labels and prepare them for display, including
			//parsing out genus and species portion
			var treeTipLabels = tree.getTipLabels();
			tipLabels = new Array();
			tipGenera = new Array();
			tipSpecies = new Array();
			for (var i = treeTipLabels.length - 1; i >= 0; i--) {
				var label = treeTipLabels[i].replace(/_/g, " ");
				var firstSpace = label.indexOf(" ");
				tipGenera[treeTipLabels[i]] = label.substring(0, firstSpace);
				tipSpecies[treeTipLabels[i]] = label.substring(firstSpace + 1);
				tipLabels[treeTipLabels[i]] = label;
			}

			if (displayMode != null) {
				tree.treeDisplayMode = displayMode;
			}
			tree.ladderizeUp();
			if (autocolor) {
				this.setTipColors();
			}

			var tiny = false;
			if (tiny) {
				heightPerTip = 4;
			}

			if (autoresize) {
				var height = heightPerTip * treeTipLabels.length;
				height += 50;
				canvasDiv.style.height = height;
			}

			drawTree();
		}
		/**
		 * Causes the tree to be redrawn in phylogram mode (using branch lengths)
		 */
		this.showPhylogram = function() {
			tree.treeDisplayMode = tree.PHYLOGRAM;
			tree.refresh();
			drawTree();
		}
		/**
		 * Causes the tree to be redrawn in cladogram mode (ignoring branch lengths)
		 */
		this.showCladogram = function() {
			tree.treeDisplayMode = tree.CLADOGRAM;
			tree.refresh();
			drawTree();
		}
		/**
		 * Sets a cutoff value, below which branch support values will be displayed.
		 * For most trees, support values range between 0 and 100. Setting to 0
		 * results in no support values being displayed. Setting >100 typically
		 * results in all support values being displayed.
		 */
		this.setSupportValueCutoff = function(cutoff) {
			supportValueCutoff = cutoff;
			drawTree();
		}
		/**
		 * Returns the label for the tip at position x,y in the canvas. If there
		 * is no label at this point, null is returned.
		 */
		function getTipAtPoint(x, y) {
			var r = null;
			for (var i = tipXs.length - 1; i >= 0; i--) {
				if (tipXs[i][0] <= x && tipXs[i][1] >= x && tipYs[i][0] <= y && tipYs[i][1] >= y) {
					var label = tree.tipLabels[i];
					r = label;
				}
			}
			return r;
		}

		/**
		 * Returns a Tree Object representing the currently set tree string.
		 */
		function getTree() {
			this.tree = new Tree(treeString);
			this.tree.ladderizeUp();
			this.tipDistancesFromRoot
			this.leaves

			return this.tree;
		}

		/**
		 * Refreshes the state of the Tree when something has changed.
		 */
		function refreshTree() {
			tree = getTree();
			var tipLabels = tree.getTipLabels();
			tipCount = tipLabels.length;
			for (var i = tipLabels.length - 1; i >= 0; i--) {
			}
			minHeight = (heightPerTip * tipCount) + topMargin + bottomMargin;
			//maxHeight = minHeight * 1.5;
			maxHeight = minHeight;
		}

		/**
		 * Draws the tree on the canvas.
		 */
		function drawTree() {
			//		var startTime = new Date().getTime();
			if (tree == null) {
				refreshTree();
			}
			//canvas.width = 0;
			canvas.width = canvasDiv.offsetWidth - 20;
			canvas.height = Math.max(minHeight, canvasDiv.offsetHeight - 20);
			canvas.height = Math.min(canvas.height, maxHeight);

			var yCoords = tree.nodeCoordinates;
			var tipNodes = tree.tips;

			var scaleX = Math.max(canvas.width - rightMargin - leftMargin, 0) / tree.maxDistanceFromRoot;

			var maxLeafY = yCoords[tipNodes[tipNodes.length-1]][0];
			var scaleY = Math.max(canvas.height - topMargin - bottomMargin, 0) / maxLeafY;

			var context = canvas.getContext("2d");

			context.font = contextFont;
			context.fillStyle = "#000000";
			context.strokeStyle = "#000000";
			context.lineWidth = 2;
			var zero = 0;
			var branchXCoordinates = tree.branchXCoordinates;

			for (var i = 0; i < yCoords.length; i++) {
				var yCoord = yCoords[i][0] * scaleY + topMargin;
				var x1 = branchXCoordinates[i][0] * scaleX + leftMargin;
				if (isNaN(x1)) {
					x1 = 0;
				}
				var x2 = branchXCoordinates[i][1] * scaleX + leftMargin;

				//draw the branch
				context.beginPath();
				context.moveTo(x1, yCoord);
				context.lineTo(x2, yCoord);
				context.stroke();
				//place the support value above the branch, if appropriate
				var labelVSpace = 2;
				if (tree.branchSupports[i] < supportValueCutoff) {
					var supportX = x1 + (x2 - x1) / 2;
					supportX = Math.min(supportX, (x2 - 15));
					if (context.fillText) {
						context.fillText(tree.branchSupports[i], supportX, yCoord - labelVSpace);
					}
				}

				//now draw the vertical line representing the node
				var halfHeight = yCoords[i][1] * scaleY;
				var topY = yCoord - halfHeight;
				var botY = yCoord + halfHeight;
				//some node may end up with zero height as a result of
				//node collapsing. The line created from these still
				//fills in a pixel, so skip over any nodes with zero length
				if (topY != botY) {
					context.beginPath();
					context.moveTo(x2, topY);
					context.lineTo(x2, botY);
					context.stroke();
				}
			}

			//draw the tip labels
			var labelPadSpace = 20;
			for (var i = 0; i < tree.tipLabels.length; i++) {
				var thisLabel = tree.tipLabels[i];
				var leafY = yCoords[tipNodes[i]][0];
				var yCoord = (leafY * scaleY) + topMargin + 2;

				var xCoord = ((tree.tipDistancesFromRoot[i] * scaleX) + leftMargin + labelPadSpace);

				var adjustedLeafLabel = tipLabels[thisLabel];
				var color = tipColors[thisLabel];
				if (color == null) {
					context.fillStyle = mainColor;
				} else {
					context.fillStyle = color;
				}

				var textBounds = context.measureText(adjustedLeafLabel);
				var tipx = [xCoord, xCoord + textBounds.width];
				var tipy = [yCoord - fontSize, yCoord];
				tipXs[i] = tipx;
				tipYs[i] = tipy;
				//if the mouse is over this label, put the link div here
				if (thisLabel == mouseOverLabel) {
					link = true;
					var id = tipLinkIds[thisLabel];
					/*
					 if(id == null) {
					 id = thisLabel;
					 }
					 var url = linkURLPrefix + id + linkURLSuffix;
					 */
					labelLinkDiv.style.setProperty("top", tipy[0] + 1 + "px", "");
					labelLinkDiv.style.setProperty("left", tipx[0] + "px", "");
					labelLinkDiv.innerHTML = "<div id=\"label_div\" style=\"white-space:nowrap;\">";
					if (id != null && isNaN(id) == false) {
						labelLinkDiv.innerHTML += "<a id=\"label_link\" href=\"" + linkURLPrefix + id + linkURLSuffix + "\">" + adjustedLeafLabel + "</a>";
					} else {
						labelLinkDiv.innerHTML += adjustedLeafLabel;
					}
					labelLinkDiv.innerHTML += "</div>";
				} else {
					if (colorSpecies) {
						if (color != null) {
							var genusColor = color[0];
							context.fillStyle = genusColor;
						}
						var genus = tipGenera[thisLabel];
						var theRest = tipSpecies[thisLabel];
						context.fillText(genus, xCoord, yCoord);
						var genusLength = context.measureText(genus).width;
						if (color != null) {
							var speciesColor = color[1];
							context.fillStyle = speciesColor;
						}
						context.fillText(" " + theRest, (xCoord + genusLength), yCoord);
					} else {
						context.fillText(adjustedLeafLabel, xCoord, yCoord);
					}
				}

				if (tipLinkIds[thisLabel] != null && isNaN(tipLinkIds[thisLabel]) == false) {
					if (document.getElementById(tipLinkIds[thisLabel] + "_checkbox") == null) {
						var checkbox_div = document.createElement('div');
						checkbox_div.id = tipLinkIds[thisLabel] + "_checkbox";
						checkbox_div.style.position = "absolute";
						checkbox_div.style.top = (tipy[0] - 2) + "px";
						checkbox_div.style.left = (tipx[0] - 15) + "px";
						checkbox_div.innerHTML = "<img src=\"/patric/images/unchecked.gif\" onclick=\"javascript:flipTreeCheckBox('" + tipLinkIds[thisLabel] + "', 'unchecked');\"/>";
						canvas_checkboxes_Div.appendChild(checkbox_div);
					} else {
						checkbox_div = document.getElementById(tipLinkIds[thisLabel] + "_checkbox");
						checkbox_div.style.top = (tipy[0] - 2) + "px";
						checkbox_div.style.left = (tipx[0] - 15) + "px";
					}
				}

			}

			//		var endTime = new Date().getTime();
			//		var drawTime = endTime - startTime;
			//		console.log("draw time (ms): " + drawTime);
		}

		function log(message) {
			if (window.console && window.console.log) {
				window.console.log(message);
			}
		}

		/**
		 * Called when the browser has been resized and the tree needs to be redrawn
		 * based on the new size.
		 */
		this.resized = function() {
			drawTree();
		}
		/**
		 * Draw the tree.
		 */
		this.initialize = function() {
			drawTree();
		}
	}
};

/**
 * The Tree Object. Created with a valid newick-format tree string.
 */
function Tree(newickTree) {
	this.NO_PARENT_INDICATOR = -1;
	this.EXPANDED = 1;
	this.COLLAPSED = 2;
	this.CLADOGRAM = 1;
	this.PHYLOGRAM = 2;

	this.nodeParentPointers
	this.nodeChildPointers
	this.tipLabels
	this.branchLengths
	this.branchSupports
	this.nodeStates
	this.preorderTraversalSequence
	this.nodeDescendantLeafCounts
	this.nodeDistancesFromRoot
	this.nodeCoordinates
	this.branchXCoordinates
	this.cladogramBranchLengths
	this.maxDistanceFromRoot
	this.rooted
	this.rootIndex = -1;
	this.tips
	this.tipDistancesFromRoot
	this.tipDistancesFromPrevious
	//this.treeDisplayMode = this.PHYLOGRAM;
	this.treeDisplayMode = this.CLADOGRAM;
	this.leaves
	this.topLevelNode

	/**
	 * Parse the tree string and populate the tree data structures.
	 */
	this.parseTree = function(treeString) {
		var openParenChar = '(';
		var closeParenChar = ')';
		var openParen = "(";
		var closeParen = ")";
		var colon = ':';
		var commaChar = ',';
		var openSquare = '[';
		var closeSquare = ']';
		//		var EXPANDED = 1;
		//		var COLLAPSED = 2;

		//remove ';' from the end, if it is there
		if (treeString.charAt(treeString.length - 1) == ';') {
			treeString = treeString.substring(0, treeString.length - 1);
		}

		//count how many open parentheses and how many
		//close parentheses
		var openCount = 0;
		var closeCount = 0;
		var treeStringLength = treeString.length;
		for (var i = 0; i < treeStringLength; i++) {
			if (treeString.charAt(i) == openParenChar) {
				openCount++;
			} else if (treeString.charAt(i) == closeParenChar) {
				closeCount++;
			}
		}

		if (openCount != closeCount) {
			//something is wrong with the tree string. There should be equal
			//numbers of open and close parentheses
		} else {
			//Find leaves
			//leaves are flanked by commas, or by (, or by ,)
			var commaString = ",";
			var commaSplits = treeString.split(commaString);
			this.leaves = new Array();

			var leafBranchLengths = new Array();
			var commaSplitsLength = commaSplits.length;
			for (var i = 0; i < commaSplitsLength; i++) {
				var startsWithOpenParen = commaSplits[i].charAt(0) == openParen;
				var endsWithCloseParen = commaSplits[i].charAt(commaSplits[i].length - 1) == closeParen;
				if (startsWithOpenParen && !endsWithCloseParen) {
					this.leaves[i] = commaSplits[i].substring(commaSplits[i].lastIndexOf(openParen) + 1);
				} else if (endsWithCloseParen && !startsWithOpenParen) {
					this.leaves[i] = commaSplits[i].substring(0, commaSplits[i].indexOf(closeParen));
				} else {
					this.leaves[i] = commaSplits[i];
				}

				//if leaf contains branch distance info, remove it
				var indexOfColon = this.leaves[i].indexOf(colon);
				if (indexOfColon > -1) {
					var lengthString = this.leaves[i].substring(indexOfColon + 1);
					var indexOfParen = lengthString.indexOf(closeParen);
					if (indexOfParen > -1) {
						lengthString = lengthString.substring(0, indexOfParen);
					}
					var leafBranchLength = lengthString;
					this.leaves[i] = this.leaves[i].substring(0, indexOfColon);
					leafBranchLengths[i] = leafBranchLength;
				}
			}

			//for each leaf, find the index in the tree where the
			//leaf starts
			var leafNodePairs = new Array();
			for (var i = 0; i < this.leaves.length; i++) {
				leafNodePairs[i] = new Array();
				//The following line is causing a bug when there
				//is one leaf name that is a substring of another
				//leaf name. If the leaf with the name that is a
				//substring is after the leaf with the superstring
				//name, then the indexOf() search returns the
				//index for the wrong leaf.
				leafNodePairs[i][0] = treeString.indexOf(this.leaves[i]);
				leafNodePairs[i][1] = leafNodePairs[i][0] + this.leaves[i].length - 1;
				//make sure correct index of the leaf string was found.
				//If it is the correct index, then the next character
				//should be a comma, colon, or close parenthesis.
				//This is to correct for the above mentioned bug.
				var followingChar = treeString.charAt(leafNodePairs[i][1] + 1);
				while (followingChar != commaChar && followingChar != closeParenChar && followingChar != colon) {
					//the index for a superstring of the leaf name was
					//found.
					//Check for the next occurrence
					leafNodePairs[i][0] = treeString.indexOf(this.leaves[i], leafNodePairs[i][1] + 1);
					leafNodePairs[i][1] = leafNodePairs[i][0] + this.leaves[i].length - 1;
					followingChar = treeString.charAt(leafNodePairs[i][1] + 1);
				}
			}

			//tracks the locations open parentheses that have not yet
			//been paired with a closing parenthesis
			var openStack = new Array();
			var internalNodePairs = new Array();

			//index of the next pair to be set
			var pairPointer = 0;

			//index of the current position in opens - next write
			//position
			var openStackPointer = 0;

			for (var i = 0; i < treeString.length; i++) {
				if (treeString.charAt(i) == openParenChar) {
					openStack[openStackPointer] = i;
					openStackPointer++;
				} else if (treeString.charAt(i) == closeParenChar) {
					openStackPointer--;
					var openForPair = openStack[openStackPointer];
					var closeForPair = i;
					internalNodePairs[pairPointer] = new Array();
					internalNodePairs[pairPointer][0] = openForPair;
					internalNodePairs[pairPointer][1] = closeForPair;
					pairPointer++;
				}
			}

			//nodePairs is a concatenation of leafNodePairs and
			//internalNodePairs
			var nodePairs = leafNodePairs.concat(internalNodePairs);
			//			for each node in nodePairs, gives the index for the
			//nodes parent node (also an index in nodePairs)

			//support and length are stored with the same index as
			//the child node of the branch. This is a good way to
			//do it because when the branches are created (see below)
			//they are done starting with the child node index

			this.nodeParentPointers = new Array();
			this.branchLengths = new Array();
			this.branchSupports = new Array();
			nodeStrings = new Array();
			for (var i = 0; i < nodePairs.length; i++) {
				this.nodeParentPointers[i] = this.NO_PARENT_INDICATOR;
				var nodeStart = nodePairs[i][0];
				var nodeEnd = nodePairs[i][1];

				//determine the branch length and support value for
				//the branch leading to this node, if these values
				//are provided.
				var firstCandidateIndex = nodeEnd + 1;
				var nextCommaIndex = treeString.indexOf(commaChar, firstCandidateIndex);
				var nextCloseParenIndex = treeString.indexOf(closeParen, firstCandidateIndex);
				var nextColonIndex = treeString.indexOf(colon, firstCandidateIndex);
				var treeEnd = treeString.length;
				var branchLength;
				//			double branchSupport = Double.NaN;
				var valueEnd = treeEnd;
				if (nextCommaIndex > -1) {
					valueEnd = Math.min(valueEnd, nextCommaIndex);
				}
				if (nextCloseParenIndex > -1) {
					valueEnd = Math.min(valueEnd, nextCloseParenIndex);
				}
				var supportEnd = -1;
				if (nextColonIndex >= 0) {
					supportEnd = Math.min(valueEnd, nextColonIndex);
				}
				if (supportEnd > firstCandidateIndex) {
					//there is a branch support value
					var branchSupportString = treeString.substring(firstCandidateIndex, supportEnd);
					this.branchSupports[i] = branchSupportString;
				}

				if (nextColonIndex > nodeEnd && nextColonIndex < valueEnd) {
					//there is a length
					var branchLengthString = treeString.substring(nextColonIndex + 1, valueEnd);
					//see if support value is present, between square
					//brackets after the branch length. This is an alternate
					//way of encoding the support values in newick format
					var openSquareIndex = branchLengthString.indexOf(openSquare);
					if (openSquareIndex > -1) {
						var closeSquareIndex = branchLengthString.indexOf(closeSquare, openSquareIndex);
						var branchSupportString = branchLengthString.substring(openSquareIndex + 1, closeSquareIndex);
						this.branchSupports[i] = branchSupportString;
						branchLengthString = branchLengthString.substring(0, openSquareIndex);
					}
					try {
						branchLength = parseFloat(branchLengthString);
					} catch(nfe) {
						branchLength = 0;
					}
				}
				//				console.log("branchSupport for " + i + ": " + this.branchSupports[i]);
				this.branchLengths[i] = branchLength;

				//look until a node is found such that this node
				//is between the start and end of that node.
				//Because the internal nodes are parsed in a way
				//that finds the most internal nodes first,
				//the first node found containing another node
				//should be the correct one.
				for (var j = 0; j < nodePairs.length; j++) {
					if (nodePairs[j][0] < nodeStart && nodePairs[j][1] > nodeEnd) {
						this.nodeParentPointers[i] = j;
						j = nodePairs.length;
						//break out of the loop here
					}
				}
			}

			//at this point, the tree can be completely represented by
			// the String[] leaves and the int[] nodeParentPointers
			//The indices in leaves map directly to parentPointers.
			//Additional indices in parentPointers correspond to
			//internal nodes. Branch lengths and branch supports
			//are tracked in parallel arrays (if they are present
			//in the input tree string)

			//			nodeStrings = new String[nodeParentPointers.length];
			//populate nodeStrings
			for (var i = 0; i < this.nodeParentPointers.length; i++) {
				var nodeString = treeString.substring(nodePairs[i][0], nodePairs[i][1] + 1);
				nodeStrings[i] = nodeString;
			}
		}

		this.populateChildPointers();

		//determine if tree is rooted. If rooted there will be one entry
		//in nodeParentPointer that == NO_PARENT_INDICATOR
		//This node will also have exactly 2 child nodes
		var rootedAt = -1;
		var noParentsFound = 0;
		for (var i = 0; i < this.nodeParentPointers.length; i++) {
			if (this.nodeParentPointers[i] == this.NO_PARENT_INDICATOR) {
				rootedAt = i;
				noParentsFound++;
			}
		}

		if (noParentsFound == 1) {
			if (this.nodeChildPointers[rootedAt].length == 2) {
				this.rooted = true;
			} else {
				this.rooted = false;
			}
		}
	}

	this.populateChildPointers = function() {
		nodeChildCounters = new Array();

		//count how many child nodes there are for each parent node
		for (var i = 0; i < this.nodeParentPointers.length; i++) {
			if (this.nodeParentPointers[i] > -1 && this.nodeParentPointers[i] < this.nodeParentPointers.length) {
				nodeChildCounters[this.nodeParentPointers[i]]++;
			}
		}

		//if highest-level node has two child nodes, then consider the
		//tree to be rooted. Otherwise, it is unrooted.
		if (nodeChildCounters[nodeChildCounters.length - 1] > 2) {
			this.rooted = false;
		} else {
			this.rooted = true;
		}

		//allocate nodeChildPointers arrays
		this.nodeChildPointers = new Array();
		for (var i = 0; i < this.nodeParentPointers.length; i++) {
			this.nodeChildPointers[i] = new Array();
		}

		//populate nodeChildPointers
		for (var i = 0; i < this.nodeParentPointers.length; i++) {
			var nodeIndex = this.nodeParentPointers[i];
			if (nodeIndex > -1 && nodeIndex < this.nodeParentPointers.length) {
				this.nodeChildPointers[nodeIndex].push(i);
			}
		}
	}
	/**
	 * Returns an array containing the state (collapsed or explanded) of
	 * each node. This is not yet implemented, and currently just returns
	 * an empty array.
	 */
	this.getNodeStates = function() {
		if (this.nodeStates == null) {
			this.nodeStates = new Array();
		}
		return this.nodeStates;
	}
	/**
	 * Returns the top-level node, which is either the root node or the base
	 * multifurcating node in an unrooted tree.
	 */
	this.getTopLevelNode = function() {
		var r = -1;
		for (var i = 0; i < this.nodeParentPointers.length; i++) {
			if (this.nodeParentPointers[i] == this.NO_PARENT_INDICATOR && this.nodeChildPointers[i].length > 0) {
				r = i;
			}
		}
		return r;
	}
	/**
	 * Determines the order to visit the nodes in order to do a pre-order
	 * traversal of the tree. This array can be used backwards to do a
	 * post-order traversal, so there is no need for a separate post-order
	 * traversal sequence.
	 */
	this.calcPreorderTraversalSequence = function() {
		var nodeStates = this.getNodeStates();
		//only traverse EXPANDED nodes (actually, nodes without a COLLAPSED
		//ancestor node. The highest=level COLLAPSED node is traversed,
		//and represents the entire collapsed clade)
		this.preorderTraversalSequence = new Array();
		var topIndex = this.getTopLevelNode();
		var traversalIndex = 0;

		//element 0 in the stack has the pointer to the current stack top
		var nodeStack = new Array();
		nodeStack[0] = 1;
		nodeStack[1] = topIndex;
		var currentNode;
		while (nodeStack[0] > 0) {
			currentNode = nodeStack[nodeStack[0]--];
			this.preorderTraversalSequence[traversalIndex++] = currentNode;
			if (nodeStates[currentNode] != this.COLLAPSED) {
				//don't add descendants of COLLAPSED nodes to the stack
				for (var i = this.nodeChildPointers[currentNode].length - 1; i >= 0; i--) {
					nodeStack[++nodeStack[0]] = this.nodeChildPointers[currentNode][i];
				}
			}
		}
	}
	/**
	 * Returns an array of the tips in display order from top to bottom.
	 * The values in the returned array are node index values, not tip labels.
	 */
	this.getTips = function() {
		if (this.tips == null) {
			var nodeStates = this.getNodeStates();
			var leafLabels = this.leaves;
			this.tips = new Array();
			var tipIndex = 0;
			for (var i = 0; i < this.preorderTraversalSequence.length; i++) {
				var node = this.preorderTraversalSequence[i];
				if (this.nodeDescendantLeafCounts[node] == 1 || nodeStates[node] == this.COLLAPSED) {
					//see if any ancestor nodes are COLLAPSED, if not, then
					//this node is a tip
					var parent = this.nodeParentPointers[node];
					var collapsedParentFound = false;
					while (parent >= 0 && !collapsedParentFound) {
						collapsedParentFound = nodeStates[parent] == this.COLLAPSED;
						parent = this.nodeParentPointers[parent];
					}
					if (!collapsedParentFound) {
						this.tips[tipIndex++] = node;
					}
				}
			}
		}
		return this.tips;
	}
	/**
	 * Returns an array of the tip labels in display order from top to
	 * bottom.
	 */
	this.getTipLabels = function() {
		if (this.tipLabels == null) {
			var tips = this.getTips();
			this.tipLabels = new Array();
			for (var i = 0; i < tips.length; i++) {
				this.tipLabels.push(this.leaves[tips[i]]);
			}
		}
		return this.tipLabels;
	}
	/**
	 * Calculates the y coordinate values used to draw the vertical node lines
	 * on the tree. Each node has 2 values, a y coordinate for the center of
	 * the node line and a half-height value for how far above and below the
	 * center point the line should extend. These values need to be scaled
	 * according to the available drawing space.
	 */
	this.calcNodeCoordinates = function() {
		if (this.nodeCoordinates == null) {
			this.nodeCoordinates = new Array();
			//use postorder traversal to determine y coordinates. The scale of
			//y coordinates is 1 per leaf. The top leaf is 0 and the bottom leaf
			//is leaves.length - 1
			var tips = this.getTips();
			var tipYDistances = this.getTipDistanceFromPrevious();
			this.nodeCoordinates[tips[0]] = new Array();
			this.nodeCoordinates[tips[0]][0] = 0;
			this.nodeCoordinates[tips[0]][1] = 0;

			for (var i = 1; i < tips.length; i++) {
				this.nodeCoordinates[tips[i]] = new Array();
				this.nodeCoordinates[tips[i]][0] = this.nodeCoordinates[tips[i-1]][0] + tipYDistances[i];
				this.nodeCoordinates[tips[i]][1] = 0;
			}

			var nodeStates = this.getNodeStates();
			for (var i = this.preorderTraversalSequence.length - 1; i >= 0; i--) {
				var node = this.preorderTraversalSequence[i];
				var children = this.nodeChildPointers[node];
				if (children.length > 0 && nodeStates[node] != this.COLLAPSED) {
					//this is not a leaf. set the y coordinate to half way between
					//the smallest and the largest y coords for the children.
					//Children should already be sorted from smallest to largest
					var maxY = 0;
					var minY = 100000000;
					for (var j = 0; j < children.length; j++) {
						var childY = this.nodeCoordinates[children[j]][0];
						maxY = Math.max(childY, maxY);
						minY = Math.min(childY, minY);
					}
					var halfNode = (maxY - minY) / 2;
					this.nodeCoordinates[node] = new Array();
					this.nodeCoordinates[node][0] = minY + halfNode;
					this.nodeCoordinates[node][1] = halfNode;

				}
			}
		}
		return this.nodeCoordinates;
	}
	/**
	 * Returns an array of distances between each tip and the tip immediately
	 * above it in the display. These values need to be scaled according to
	 * the available drawing space.
	 */
	this.getTipDistanceFromPrevious = function() {
		if (this.tipDistancesFromPrevious == null) {
			var tips = this.getTips();
			this.tipDistancesFromPrevious = new Array();
			for (var i = 0; i < tips.length; i++) {
				this.tipDistancesFromPrevious[i] = 1.0;
			}
		}
		return this.tipDistancesFromPrevious;
	}
	/**
	 * Calculates the number of leaves that are descendants of each node.
	 * This information can be used (among other things) to determine how
	 * much vertical height to allow for nodes when drawing.
	 */
	this.calcNodeDescendantLeaves = function() {
		this.nodeDescendantLeafCounts = new Array();

		//for postorder traversal, go through preorderTraversalSequence backwards
		for (var i = this.preorderTraversalSequence.length - 1; i >= 0; i--) {
			var node = this.preorderTraversalSequence[i];
			var children = this.nodeChildPointers[node];
			//initialize count to zero
			this.nodeDescendantLeafCounts[node] = 0;
			if (children.length == 0) {
				//if this node is a leaf, count it as having 1 descendant (itself).
				//yes, it's weird, but it works out right.
				this.nodeDescendantLeafCounts[node] = 1;
			}
			for (var j = 0; j < children.length; j++) {
				this.nodeDescendantLeafCounts[node] += this.nodeDescendantLeafCounts[children[j]];
			}
		}

	}
	/**
	 * Calculates the x coordinate values needed for drawing the branches
	 * and nodes. Each node has two x values. The first is the starting point
	 * for the branch leading to the node. The second is the end of the branch
	 * and the location of the vertical line representing the node.
	 */
	this.calcBranchXCoordinates = function() {
		this.branchXCoordinates = new Array();
		var branchLengths = this.branchLengths;
		if (this.treeDisplayMode == this.CLADOGRAM) {
			branchLengths = this.getCladogramBranchLengths();
		}
		for (var i = 0; i < this.preorderTraversalSequence.length; i++) {
			var node = this.preorderTraversalSequence[i];
			this.branchXCoordinates[node] = new Array();
			this.branchXCoordinates[node][1] = this.nodeDistancesFromRoot[node];
			this.branchXCoordinates[node][0] = this.branchXCoordinates[node][1] - branchLengths[node];
		}
	}
	/**
	 * Returns the branch lengths to be used to draw the tree as a cladogram,
	 * ignoring any branch lengths that are associated with the tree.
	 */
	this.getCladogramBranchLengths = function() {
		if (this.cladogramBranchLengths != null) {
			//nothing special needs to be done
		} else {
			//if currentTree does not have cladogram branch lengths,
			//then create them.

			//traverse the tree, setting branch lengths for cladogram
			//display. Each node has a depth from the tip, which is
			//the maximum number of nodes along the path to the tips.
			//This represents the number of branch length units to be
			//split up along each path from the node to the tips.

			//do postorder traversal to get the max distance to a tip for each node
			var maxStepsToTip = new Array();
			for (var i = this.preorderTraversalSequence.length - 1; i >= 0; i--) {
				var node = this.preorderTraversalSequence[i];
				maxStepsToTip[node] = 0;
				var childNodes = this.nodeChildPointers[node];
				for (var j = 0; j < childNodes.length; j++) {
					maxStepsToTip[node] = Math.max(maxStepsToTip[node], maxStepsToTip[childNodes[j]]);
				}
				//add a step for this node
				maxStepsToTip[node]++;
			}

			//do a preorder traversal dividing up the amount of distance available
			//betwen branches
			var maxSteps = maxStepsToTip[this.preorderTraversalSequence[0]];

			var remainingLength = new Array();
			this.cladogramBranchLengths = new Array();
			remainingLength[this.preorderTraversalSequence[0]] = maxSteps;
			var nodeParentPointers = this.nodeParentPointers;
			for (var i = 1; i < this.preorderTraversalSequence.length; i++) {
				var node = this.preorderTraversalSequence[i];
				var childNodes = this.nodeChildPointers[node];
				this.cladogramBranchLengths[node] = remainingLength[nodeParentPointers[node]] - maxStepsToTip[node];
				remainingLength[node] = (remainingLength[this.nodeParentPointers[node]] - this.cladogramBranchLengths[node]);
			}
		}

		return this.cladogramBranchLengths;
	}
	/**
	 * Calculates the distance from the root for each node in the tree.
	 */
	this.calcNodeDistancesFromRoot = function() {
		var branchLengths = this.branchLengths;
		if (this.treeDisplayMode == this.CLADOGRAM) {
			branchLengths = this.getCladogramBranchLengths();
		}

		this.nodeDistancesFromRoot = new Array();
		this.nodeDistancesFromRoot[this.preorderTraversalSequence[0]] = 0;
		for (var i = 1; i < this.preorderTraversalSequence.length; i++) {
			var nodeIndex = this.preorderTraversalSequence[i];
			var parentIndex = this.nodeParentPointers[nodeIndex];
			//total distance from root equals distance from root to parent
			//plus distance from parent to this node
			this.nodeDistancesFromRoot[nodeIndex] = this.nodeDistancesFromRoot[parentIndex] + branchLengths[nodeIndex];
		}
	}
	/**
	 * Calculates the distances from the root for each tip in the tree.
	 */
	this.calcTipDistancesFromRoot = function() {
		this.tipDistancesFromRoot = new Array();
		this.maxDistanceFromRoot = 0;
		var tips = this.getTips();
		for (var i = 0; i < tips.length; i++) {
			this.tipDistancesFromRoot[i] = this.nodeDistancesFromRoot[tips[i]];
			this.maxDistanceFromRoot = Math.max(this.maxDistanceFromRoot, this.tipDistancesFromRoot[i]);
		}
	}
	/**
	 * Roots the tree between the two indicated nodes.
	 */
	this.rootBetweenNodes = function(nodeA, nodeB, rootPoint) {
		//determine if nodeA is the parent of nodeB, or if nodeB is the
		//parent of nodeA. If neither of these is the case, then the tree
		//cannot be rooted between these two nodes, so exit without doing
		//anything (maybe change this to returning some error code)
		var nodeAIsParent = this.nodeParentPointers[nodeB] == nodeA;
		var nodeBIsParent = this.nodeParentPointers[nodeA] == nodeB;

		if (nodeAIsParent || nodeBIsParent) {
			var parentNode, childNode;
			if (nodeAIsParent) {
				parentNode = nodeA;
				childNode = nodeB;
			} else {
				rootPoint = 1 - rootPoint;
				//keep rootPoint relative to parentNode
				parentNode = nodeB;
				childNode = nodeA;
			}

			//ensure that the tree is unrooted. If it is not already unrooted,
			//then unroot it.
			this.unroot();

			//If a root node does not already exist, create a new node,
			//which will be the root. Its parent will be
			//NO_PARENT_INDICATOR. Its children will be nodeA and nodeB
			//for the node that was the parent, convert parents to children
			if (this.rootIndex == -1) {
				this.nodeParentPointers.push(this.NO_PARENT_INDICATOR);
				this.rootIndex = this.nodeChildPointers.length;
				this.nodeChildPointers.push([parentNode, childNode]);

				//increase branchLengths array
				this.branchLengths.push(0);

				//increase branchSupports array
				this.branchSupports.push("");
			}

			//add branches from the root to each node (nodeA and nodeB).
			//These branches come from splitting the branch connecting nodeA
			//and nodeB to each other.
			var branchLength = this.branchLengths[childNode];
			var parentLength = branchLength * rootPoint;
			var childLength = branchLength * (1 - rootPoint);
			var branchSupport = this.branchSupports[childNode];

			var grandparentNode = this.nodeParentPointers[parentNode];
			if (grandparentNode >= 0) {
				this.convertParentsToChildren(grandparentNode, parentNode);
			}

			this.nodeParentPointers[parentNode] = this.rootIndex;
			this.nodeParentPointers[childNode] = this.rootIndex;
			this.branchLengths[parentNode] = parentLength;
			this.branchLengths[childNode] = childLength;
			this.branchSupports[parentNode] = branchSupport;
			this.nodeChildPointers[this.rootIndex] = [parentNode, childNode];

			//remove childNode as a child of parentNode
			this.removeChildNode(parentNode, childNode);

			this.rooted = true;
			this.cladogramBranchLengths = null;
			this.refresh();
		}
	}
	/**
	 * This function is used by rootBetweenNodes() to change the parent-child
	 * relationship of nodes during the re-rooting process.
	 */
	this.convertParentsToChildren = function(parentNode, childNode) {
		if (this.nodeParentPointers[parentNode] == this.NO_PARENT_INDICATOR) {
			//this is the end of the recursion. We have reached the node
			//that has no parent
			this.nodeParentPointers[parentNode] = childNode;
		} else {
			//recursively call this method with the parentNode as the child
			//and the parentNode's parent as the parent.
			this.convertParentsToChildren(this.nodeParentPointers[parentNode], parentNode);
		}

		//remove childNode from the list of children of parentNode
		var newChildPointers = new Array();
		var index = 0;
		for (var i = 0; i < this.nodeChildPointers[parentNode].length; i++) {
			if (this.nodeChildPointers[parentNode][i] != childNode) {
				newChildPointers[index++] = this.nodeChildPointers[parentNode][i];
			}
		}
		this.nodeChildPointers[parentNode] = newChildPointers;

		//add parentNode as a child of childNode
		this.nodeChildPointers[childNode].push(parentNode);

		//replace parent of parentNode with childNode
		this.nodeParentPointers[parentNode] = childNode;

		//the branch that used to lead to childNode now leads to parentNode
		//so update branchLengths
		this.branchLengths[parentNode] = this.branchLengths[childNode];
	}
	/**
	 * This function is used by rootBetweenNodes() to remove the child
	 *  node as a child of the parent node.
	 */
	this.removeChildNode = function(parentNode, childNode) {
		//remove childNode as a child of parentNode
		var newChildrenOfParent = new Array();
		var newChildIndex = 0;
		for (var i = 0; i < this.nodeChildPointers[parentNode].length; i++) {
			if (this.nodeChildPointers[parentNode][i] != childNode) {
				newChildrenOfParent[newChildIndex] = this.nodeChildPointers[parentNode][i];
				newChildIndex++;
			}
		}
		this.nodeChildPointers[parentNode] = newChildrenOfParent;
	}
	/**
	 * Returns true is the tree is currently rooted.
	 */
	this.isRooted = function() {
		return this.rooted;
	}
	/**
	 * Causes the tree to be unrooted.
	 */
	this.unroot = function() {
		if (this.isRooted()) {
			//find the root and remove it, making one child of the root
			//a child of the other child of the root
			var root = 0;
			for (var i = 0; i < this.nodeParentPointers.length; i++) {
				if (this.nodeParentPointers[i] == this.NO_PARENT_INDICATOR) {
					root = i;
					break;
				}
			}

			var rootChildren = this.nodeChildPointers[root];
			//We'll make rootChildren[0] the parent of rootChildren[1].
			//This will make rootChildren[0] at least a trifurcation
			var newParent = rootChildren[0];
			var newChild = rootChildren[1];

			this.nodeParentPointers[newChild] = newParent;
			this.nodeChildPointers[newParent].push(newChild);

			//remove child pointers from defunct root
			this.nodeChildPointers[root] = [];

			//the new parent now has no parent
			this.nodeParentPointers[newParent] = this.NO_PARENT_INDICATOR;

			//adjust branch length for newChild by adding the previous
			//branch length for newParent
			this.branchLengths[newChild] += this.branchLengths[newParent];
			this.branchLengths[newParent] = 0;
			this.rooted = false;
		}
	}
	/**
	 * Triggers an update of several tree data structures.
	 */
	this.refresh = function() {
		this.tipDistancesFromPrevious = null;
		this.tipLabels = null;
		this.tips = null;

		this.calcPreorderTraversalSequence();
		this.calcNodeDescendantLeaves();
		this.calcNodeDistancesFromRoot();
		this.calcTipDistancesFromRoot();

		this.nodeCoordinates = null;
		this.calcNodeCoordinates();

		this.calcBranchXCoordinates();
		this.getTipLabels();
	}
	/**
	 * Orients the child nodes of each node so that the node with the
	 * greatest number of descendant nodes is listed first.
	 */
	this.ladderizeUp = function() {
		var leafCounts = this.nodeDescendantLeafCounts;
		for (var i = 0; i < this.nodeChildPointers.length; i++) {
			if (this.nodeChildPointers[i].length > 1) {
				this.nodeChildPointers[i].sort(function(a, b) {
					var nodeADesc = leafCounts[a];
					var nodeBDesc = leafCounts[b];
					var r = nodeBDesc - nodeADesc;
					return r;
				});
			}
		}
		this.refresh();
	}
	/**
	 * Sets the outgroup tips. The tree is re-rooted based on the outgroup,
	 * by finding the branch that has the maximum concentration of outgoup
	 * taxa on one side. It is not always possible to root so that the outgroup
	 * are all on one side and the ingroup are all on the other side, becasue
	 * the outgroup may not be monophyletic.
	 */
	this.setOutgroup = function(outgroup) {
		//re-root at some branch leading to an ingroup member
		var ingroupMember = this.leaves.length - 1;
		while (outgroup.indexOf(this.leaves[ingroupMember]) > 0) {
			ingroupMember--;
		}
		var parent = this.nodeParentPointers[ingroupMember];
		this.rootBetweenNodes(ingroupMember, parent, 0.5);

		//for each node, build an array of all leaf names that are
		//descendants of the node
		var nodeDescLists = new Array();
		//do post-order traversal
		for (var i = this.preorderTraversalSequence.length - 1; i >= 0; i--) {
			var node = this.preorderTraversalSequence[i];
			nodeDescLists[node] = new Array();
			if (node < this.leaves.length) {
				nodeDescLists[node].push(this.leaves[node]);
			} else {
				var children = this.nodeChildPointers[node];
				for (var j = children.length - 1; j >= 0; j--) {

					nodeDescLists[node] = nodeDescLists[node].concat(nodeDescLists[children[j]]);
				}
			}
		}

		//find the node that is the most enriched for outgroup members
		var outMinusInCounts = new Array();
		var max = 0;
		var maxIndex = 0;
		for (var i = nodeDescLists.length - 1; i >= 0; i--) {
			var ingroupCount = 0;
			var descList = nodeDescLists[i];
			for (var j = outgroup.length - 1; j >= 0; j--) {
				if (descList.indexOf(outgroup[j]) >= 0) {
					ingroupCount++;
				}
			}
			var outMinusInCount = 2 * ingroupCount - descList.length;
			outMinusInCounts[i] = outMinusInCount;
			if (outMinusInCount > max) {
				max = outMinusInCount;
				maxIndex = i;
			}
		}

		//find the parent of the most outgroup-enriched node
		var parent = this.nodeParentPointers[maxIndex];

		//root between the outgroup-enriched node and its parent
		this.rootBetweenNodes(maxIndex, parent, 0.5);
	}

	this.parseTree(newickTree);
	this.refresh();

	//need to define Array.indexOf function for browsers that don't already have it (IE)
	//This implementation is copied from Mozilla Developers Connection
	if (!Array.prototype.indexOf) {
		Array.prototype.indexOf = function(searchElement /*, fromIndex */) {"use strict";

			if (this ===
				void 0 || this === null)
				throw new TypeError();

			var t = Object(this);
			var len = t.length >>> 0;
			if (len === 0)
				return -1;

			var n = 0;
			if (arguments.length > 0) {
				n = Number(arguments[1]);
				if (n !== n)
					n = 0;
				else if (n !== 0 && n !== (1 / 0) && n !== -(1 / 0))
					n = (n > 0 || -1) * Math.floor(Math.abs(n));
			}

			if (n >= len)
				return -1;

			var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);

			for (; k < len; k++) {
				if ( k in t && t[k] === searchElement)
					return k;
			}
			return -1;
		};
	}

};