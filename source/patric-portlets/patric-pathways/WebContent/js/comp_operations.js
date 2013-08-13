function jg(element){
	this.cA = '#BEFDBE' ,
	this.cNA = '#D5E6D5',
	this.cS = '#99CCFF',
	this.cC = '#FF0000',
	this.cB = '#000000',
	this.painter = new jsGraphics(element);
}

function Creator(){
	
	this.data = {},
	this.paint = function(){
		var jg = pNS.jg,
			painter = jg.painter,
			bData,
			coordinates,
			algocount,
			algo;
		for(var i in this.data){
			bData = this.data[i];
			if(bData.type == "ec" && bData[getAlgorithm(Ext.getDom("algorithm").value)]){
				coordinates = bData.coordinates,
				algo = getAlgorithm(Ext.getDom("algorithm").value);
				for(var j in coordinates){
					if(bData.clicked)
						painter.setColor(jg.cC);
					else if(bData.selected)
						painter.setColor(jg.cS);
					else{
						algocount = parseInt(Ext.getDom("taxongenomecount_"+algo.toLowerCase()).value),
						painter.setColor((bData.genome_count[algo] < algocount)?jg.cNA:jg.cA);
					}
					painter.fillRect(coordinates[j].x + 1, coordinates[j].y + 1, 45, 16),
					painter.setColor(jg.cB),
					painter.setFont("arial,helvetica,sans-serif", "10px", Font.PLAIN),
					painter.drawString(bData.name, coordinates[j].x + 3, coordinates[j].y + 3);
				}
			}	
		}
		painter.paint();
	};
}

var pNS = {
	init: function(){
		Object.defineProperty(this, 'boxes', {enumerable:true, value:new Creator()}),
		Object.defineProperty(this, 'jg', {enumerable:false, value:new jg('map_div')}),
		Object.defineProperty(this, 'tooltip', {enumerable:false, value: Ext.create('Ext.tip.ToolTip', {  
			renderTo: 'div_tooltip_map', 
		    title: 'Tooltip',
		    id: 'tip',
		    width: 180,
		    height:65,
		    html: '',
		    visible: false
		})});
	},
	set: function(obj){
		var b = this.boxes.data[obj.name];
		if(!b){
			this.boxes.data[obj.name] = obj;
		}else{
			if(!this.isCoordinatePushed(b, obj))
				b.coordinates.push({x:obj.coordinates[0].x, y:obj.coordinates[0].y});
			b[obj.algorithm] = true;
			b.genome_count[obj.algorithm] = obj.genome_count;
		}
	},
	get: function(name){
		if(this.present(name))
			return this.boxes.data[name];
		else 
			return {};
	},
	present: function(name){
		if(this.boxes.data[name])
			return true;
		else
			return false;
	},
	isCoordinatePushed: function(b, obj){
		for(var i=0; i<b.coordinates.length; i++)
			if(b.x == obj.coordinates[0].x && b.y == obj.coordinates[0].y)
				return true;
		return false;
	}
};

function boxData(options){
	this.type = options.type,
	this.clicked = false,
	this.selected = false,
	this.name = options.name || null,
	this.description = options.description || null,
	this.width = parseInt(options.width) || 0,
	this.height = parseInt(options.height) || 0,
	this.coordinates = [{x:parseInt(options.x), y:parseInt(options.y)}] || null,
	this.genome_count = {"RefSeq":0, "PATRIC":0, "BRC":0},
	this.RefSeq = getAlgorithm(options.algorithm) == "RefSeq",
	this.PATRIC = getAlgorithm(options.algorithm) == "PATRIC",
	this.BRC = getAlgorithm(options.algorithm) == "BRC";
}

function getAlgorithm(pass, reverse){
	if(reverse){
		if(pass == "RAST" || pass == "PATRIC")
			return "RAST";
		else if(pass == "Curation" || pass == "Legacy BRC" || pass == "BRC")
			return "Curation";
		else
			return "RefSeq";
	}else{
		if(pass == "RAST" || pass == "PATRIC")
			return "PATRIC";
		else if(pass == "Curation" || pass == "Legacy BRC" || pass == "BRC")
			return "BRC";
		else if(pass == "RefSeq")
			return "RefSeq";
	}
	return null;
}

function drawMap(type){
	"use strict";
	
	var cType = Ext.getDom("cType").value,
		dm = Ext.getDom("dm").value,
		passObject = {"map":Ext.getDom("map").value, need:type};	
	
	if(type != "all")
		passObject.value = Ext.getDom(type).value;
	else{
		passObject.genomeId = Ext.getDom("genomeId").value,
		passObject.taxonId = Ext.getDom("taxonId").value;
	}
	
	Ext.getCmp('kegg-panel').mask();
	
	Ext.Ajax.request({
	    url: "/patric-pathways/jsp/draw_comp_map.json.jsp",
	    method: 'POST',
	    params: {val:Ext.JSON.encode(passObject)},
	    success: function(response, opts) {
	    	var decoded = Ext.JSON.decode(response.responseText);
	    	if(type != "all"){
	    		addSelectedtoStructure(decoded),
	    		createDom(),
	    		pNS.boxes.paint(),
	    		Ext.getCmp('kegg-panel').unmask();
	    	}else{
	    		createStructure(decoded);
	    		(dm == "feature" || dm == "featurelist")?drawMap("feature_info_id"):(cType == "ec" || dm == "ec")?drawMap("ec_number"):(createDom(),pNS.boxes.paint(),Ext.getCmp('kegg-panel').unmask());
	    	}
		}
	});
	
}

function addSelectedtoStructure(data){
	"use strict";
	
	var a = data.coordinates, 
		aa,
		i;
	
	for(i =0;i<a.length; i++){
		aa = pNS.get(a[i].ec_number),
		aa["selected"] = true;
	}
}

function createStructure(decoded){
	"use strict";
	
	var a = decoded.genome_x_y,
		b = decoded.genome_pathway_x_y,
		c = decoded.map_ids_in_map,
		d = decoded.all_coordinates,	
		obj,
		i,
		j,
		aa;
	
	pNS.init();
	
	// Process All ECs
	for(i = 0; i < d.length; i++){
		aa = d[i],
		aa.name = aa.ec,
		aa.width = 46,
		aa.height = 17,
		aa.type = "ec",
		obj = new boxData(aa);
		pNS.set(obj);
	}
	
	// Change presence of only given taxonomy EC	
	for(i = 0; i < a.length; i++){
		aa = pNS.get(a[i].ec_number),
		aa.genome_count[getAlgorithm(a[i].algorithm)] = parseInt(a[i].genome_count),
		aa[getAlgorithm(a[i].algorithm)] = true;
	}
	
	// Process ALL Pathways
	for(i=0; i<c.length; i++){
		for(j=0; j<b.length; j++){
			if("ec"+b[j].map_id === c[i].source_id){
				c[i].description = b[j].map_name,
				c[i].name = b[j].map_id,
				c[i].type = "path";
				break;
			}
		}		
		obj = new boxData(c[i]),
		pNS.set(obj);
	}

	// Change presence of only given taxonomy Paths
	for(i=0; i<b.length; i++){
		for(j=0; j<c.length; j++){
			if("ec"+b[i].map_id === c[j].source_id){
				aa = pNS.get(b[i].map_id),
				aa[getAlgorithm(b[i].algorithm)] = true;
				break;
			}
		}
	}
}


function createDom(){
	"use strict";
	
	var j,
		b,
		w,
		h,
		x,
		y,
		x1,
		x2,
		y1,
		y2,
		area,
		getById = document.getElementById.bind(document),
		map = document.createElement("map"),
		title = "",
		body = "",
		div,
		tip = getById("content-tip"),
		d1 = getById("div_tooltip_map"),
		image = getById('map_img'),
		random = 0,
		algo;
	
	map.name = "CompMap", map.id = "CompMap";
	
	for(var p in pNS){
		if(typeof pNS[p] !== "function"){
			algo = pNS[p].data;
			if(algo){
				for(var i in algo){
					if(algo[i].type == "ec")
						title = "<label style=\"font-weight:bold;\"/>EC Number : ", body = "<label style=\"font-weight:bold;\"/>Description : ";
					else
						title = "<label style=\"font-weight:bold;\"/>Pathway ID : ", body = "<label style=\"font-weight:bold;\"/>Pathway Name : ";
					title += "</label>" + algo[i].name, 
					body += "</label>" + algo[i].description;					
					if(algo[i].type == "path" && algo[i][getAlgorithm(Ext.getDom("algorithm").value)])
						body += getURLforPATH(algo[i]);
					w = algo[i].width,
					h = algo[i].height,
					b = algo[i].coordinates;
					for(j=0; j < b.length; j++){
						x = b[j].x, 
						y = b[j].y,
						div = document.createElement("div"),
						div.setAttribute("id", random+"_divoa"),
						div.innerHTML = body,
						tip.appendChild(div),
						x1 = x - 2, y1 = y - 2, x2 = x + w + 2, y2 = y + h + 2,
						area = document.createElement("area"),
						area.shape = "rect",
						area.coords = x1 + "," + y1 + "," + x2 + "," + y2 + ",",
						area.setAttribute("id", random+"_a"),
						area.setAttribute("onmouseover", "createTooltip("+random+", '"+title+"')"),
						map.appendChild(area);
						random++;
					}
				}
			}
			
		}
	}
	
	d1.appendChild(map);
	image.setAttribute("usemap","#CompMap");
}

function createTooltip(id, title){
	
	var style = Ext.getDom("tip").style,
		event = Ext.EventObject.browserEvent;
	pNS.tooltip.body.update(title+"<br/>"+Ext.getDom(id+"_divoa").innerHTML);
	
	style.top = event.pageY - 300 + 'px';
	style.left = event.pageX + 'px';
	style.display = '';
	
}

function getURLforPATH(obj){
	var text = "",
		pk = Ext.getDom("pk")?Ext.getDom("pk").value:"",
		algorithm = obj[getAlgorithm(Ext.getDom("algorithm").value)]?getAlgorithm(Ext.getDom("algorithm").value):"PATRIC",
		map = obj.name,
		taxonId = Ext.getDom("taxonId")?Ext.getDom("taxonId").value:"",
		genomeId = Ext.getDom("genomeId")?Ext.getDom("genomeId").value:"",
		cType = Ext.getDom("cType")?Ext.getDom("cType").value:"",
		cId = Ext.getDom("cId")?Ext.getDom("cId").value:"",		
		dm = Ext.getDom("dm")?Ext.getDom("dm").value:"",
		feature_info_id = Ext.getDom("feature_info_id")?Ext.getDom("feature_info_id").value:"",
		ec_number = Ext.getDom("ec_number")?Ext.getDom("ec_number").value:"",
		pretext = "<br> <a href=\"CompPathwayMap?",
		posttext = "&map="+map+"&algorithm="+algorithm+"\" />Click here to view map "+map+"</a>";
	
	if(cType === "taxon"){
		if(dm === "feature")
			text = pretext+"cType="+cType+"&dm="+dm+"&cId="+taxonId+"&feature_info_id="+feature_info_id+"&pk="+pk+posttext;
		else if(dm === "ec")
			text = pretext+"cType="+cType+"&dm="+dm+"&cId="+taxonId+"&ec_number="+ec_number+"&pk="+pk+posttext;
		else
			text = pretext+"cType="+cType+"&cId="+taxonId+posttext;
	}else if(cType === "genome"){
		if(dm === "feature" || dm === "featurelist")
			text = pretext+"cType="+cType+"&dm="+dm+"&cId="+genomeId+"&pk="+pk+posttext;
		else if(dm === "ec")
			text = pretext+"cType="+cType+"&dm="+dm+"&cId="+genomeId+"&pk="+pk+posttext;
		else
			text = pretext+"cType="+cType+"&cId="+genomeId+posttext;
	}else if(cType === "feature"){
		text = pretext+"cType="+cType+"&cId="+cId+"&pk="+pk+posttext;
	}else{
		if(dm === "path")
			text =  pretext+"dm=path&pk="+pk+posttext;
		else if(dm === "ec")
			text =  pretext+"cId="+ec_number+"&dm="+dm+"&pk="+pk+posttext;
		else if(dm === "feature")
			text =  pretext+"cId="+cId+"&dm="+dm+"&pk="+pk+posttext;
	}
	return text;
}
