var currentData;

function loadHeatMap(){
	
	var map = Ext.getDom("map").value,
		genomeId = Ext.getDom("genomeId").value,
		taxonId = Ext.getDom("taxonId").value,
		algorithm = Ext.getDom("algorithm").value;
	
	Ext.getCmp('heatmap-panel').getEl().mask( 'Loading...',
            'x-mask-loading');
	
	Ext.Ajax.request({
	    url: "/patric-pathways/jsp/heatmap_data.json.jsp",
	    method: 'GET',
	    params: {genomeId:genomeId, map:map, taxonId:taxonId, algorithm:algorithm},
	    synchronous: true,
	    success: function(response, opts) {
	    	processData_GenomeColumn(eval(Ext.JSON.decode(response.responseText)));	    		
	    	Ext.getCmp('heatmap-panel').getEl().unmask();			
	    }
	});
	
}

function processData_GenomeColumn(flash_data){
	
	rows = new Array();
	cols = new Array();
	var i, 
		j, 
		k,
		ec_number,
		ec_name,
		labelColor,
		rowColor,
		data = grid.store.data,
		iMax,
		flag = false,
		colorStop = [],
		heatmapState = null;

	for(j = 0; j < data.length; j++){

		ec_number = data.items[j].data.ec_number;
		ec_name = data.items[j].data.ec_name;
		
		labelColor = ((j % 2) == 0) ? 0x000066 : null;
		rowColor = ((j % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;
		
		rows[j] = new Row(j, ec_number, ec_number+' - '+ec_name, labelColor, rowColor);	
		
	}
	
	iMax = 0;
	for(i = 0; i < flash_data.genomes.length; i++){

		var hexString = "";
		
		for(j =0; j < rows.length; j++){
			flag = false;
			for(k = 0; k < flash_data.data.length; k++){
	
				 if(rows[j].rowID == flash_data.data[k].ec_number && flash_data.data[k].genome_info_id == flash_data.genomes[i].genome_info_id){
					 					 
					 hexString += flash_data.data[k].gene_count;
					 flag = true;	
				 }
				 
				 if(flag == true){
					 
					 var iSendDecimal = parseInt(flash_data.data[k].gene_count, 16);
					 
					 if(iMax <= iSendDecimal){
						 iMax = iSendDecimal;
					 }
					 
					break;
				}
			}
			if(flag == false){
				hexString += "00";
			}
		}
		
		labelColor = ((i % 2) == 0) ? 0x000066 : null;
		rowColor = ((i % 2) == 0) ? 0xF4F4F4 : 0xd6e4f4;

		cols[i] = new Column(i, flash_data.genomes[i].genome_info_id, flash_data.genomes[i].genome_name, hexString, labelColor, rowColor, {'instances': "",'families': "",'min': "0",'max': "0"});
			
	}

	if(iMax == 1)
		colorStop = [new ColorStop(1/iMax,0xfadb4e)];
	else if(iMax == 2)
		colorStop = [new ColorStop(1/2,0xfadb4e), new ColorStop(2/2, 0xf6b437)];
	else if(iMax >= 3)
		colorStop = [new ColorStop(1/iMax,0xfadb4e), new ColorStop(2/iMax,0xf6b437), new ColorStop(3/iMax,0xff6633), new ColorStop(iMax/iMax,0xff6633)];
	
	currentData = {'rows':rows,
		'columns':cols, 
		'colorStops':colorStop, 
		'rowLabel': 'EC Numbers', 
		'colLabel':'Genomes', 
		'rowTrunc':'end', 
		'colTrunc':'mid',
		'offset':1,
		'digits':2,
		'countLabel':'Members',
		'negativeBit':false,
		'cellLabelField':'',
		'cellLabelsOverrideCount':false,
		'beforeCellLabel':'',
		'afterCellLabel':''
	};

	loadHeatmap();
	
	// setTimeout(function(){(jQuery(heatmapid)[0])?flashShouldRefreshData(jQuery(heatmapid)[0]):loadHeatmap();}, 500);
	// setTimeout(function(){(jQuery(heatmapid)[0])?flashShouldRefreshData(jQuery(heatmapid)[0]):loadHeatmap();}, 500);
	
	if(axis == "Transpose"){
		flipAxises();
	}
	
	heatmapState = Ext.state.Manager.get("PathwayHeatmapState");
	
	function callHeatmapStateRestore(){
		updateDisplayStateInFlash(jQuery(heatmapid)[0], heatmapState);
	}
	
	if(heatmapState){
		setTimeout(function(){callHeatmapStateRestore();}, 1500);
    }
	
	
}

function getCellData_ECGenome(col, row){
	var parts = [],
		i = 0,
		j;
	
	if(row != null && row!="" ){
		for(i=0; i< rows.length; i++){			
			if(rows[i].rowID == row){
				parts[0] = rows[i].rowLabel; 
				break;
			}
		}
	}else
		parts[0] = "";
		
	if(col != null && col != "" ){
		for(j=0; j< rows.length; j++){
			for(i=0; i< cols.length; i++){
				if(cols[i].colID == col && rows[j].rowID == row){			
					parts[1] = cols[i].colLabel; 
					break;
				}
			}
		}
	}else
		parts[1] = "";
		
	return parts;
}