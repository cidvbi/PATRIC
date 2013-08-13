var summary;
function createLayout(){
	
	Ext.create('Ext.panel.Panel', {
		border : true,
		autoScroll : false,
		width : 900,
		id: 'mainPanel',
		items : [{
			region : 'north',
			border : false,
			height : 240,
			id : 'descriptionPanel'
		}, {
			region : 'center',
			id : 'centerPanel',
			contentEl : 'information',
			border : false,
			split : false
		}, {
			region : 'south',
			id : 'southPanel',
			html : '<div id="PATRICGrid"></div>',
			height : 570,
			width : 900,
			border : false,
			autoScroll : true
		}],
		renderTo : 'sample-layout'
	});	
	
	fillDescriptionArea();
}

function fillDescriptionArea(){
	
	Ext.Ajax.request({
		url : $Page.getPageProperties().url[0],
		method : 'POST',
		timeout : 600000,
		params : {
			callType : "getSummary",
			eid: Ext.getDom("eid").value
		},
		success : function(rs) {
			summary = Ext.JSON.decode(rs.responseText).summary;
			$('h3 > span')[0].innerHTML = 'Experiment: ' +summary.accession;
			setTable();
		}
	});
}

function setTable(){
	
	var text = '<div id="tablePanel"><table class="basic stripe far2x">';
	var order = [{header:'Title', field:'title'}, 
	             {header:'Accession', field:'accession'},
	             {header:'Organism', field:'organism'},
	             {header:'Description', field:'description'}];
	
	for(var i=0; i<order.length; i++){
		text += '<tr class="alt">';
		
		if(order[i].field == 'accession'){
			text += '<th class="first" width="20%">'+order[i].header+' - Release Date</th>';
		}else{
			text += '<th class="first" width="20%">'+order[i].header+'</th>';
		}
		
		if(summary[order[i].field]){	
			if(order[i].field == "description"){
				if(summary[order[i].field].length > 600){
					text += '<td class="last"><div id="row_description" style="overflow : auto; ">'+summary[order[i].field].substring(0, 600) + ' <a onclick="ViewMore()">more</a></div></td>';
				}else{
					text += '<td class="last">'+summary[order[i].field]+'</td>';
				}
			}else if (order[i].field == "accession"){
				text += '<td class="last">'+summary[order[i].field]+' (<a target="_blank" href="http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc='+summary[order[i].field]+'">GEO</a>) - '+summary['release_date']+'</td>';
			}else{
				text += '<td class="last">'+summary[order[i].field]+'</td>';
			}
		}else{
			text += '<td class="last">-</td>';
		}
		text += '</tr>';
	}
	summary.def = text;
	summary.fullList = '<tr class="alt"><td class="no-underline-links" colspan="2">';
	summary.fullList += '<a class="arrow-slate-e-down" href="javascript:void(0)" onclick="showFullList()">View more</a>';
	summary.fullList +='</td></tr></table></div>';
	Ext.getDom("descriptionPanel").innerHTML = summary.def+summary.fullList;
	var now = Ext.get('tablePanel').getHeight();
	Ext.get("descriptionPanel").setHeight(now);
	Ext.getCmp("mainPanel").setHeight(Ext.getCmp("mainPanel").getHeight() - (240-now));
}

function ViewMore(){
	var orig = Ext.get('tablePanel').getHeight();
	Ext.getDom("row_description").innerHTML = summary.description + ' <a onclick="ViewLess()">less</a>';
	var now = Ext.get('tablePanel').getHeight();
	Ext.get("descriptionPanel").setHeight(now);
	Ext.getCmp("mainPanel").setHeight(Ext.getCmp("mainPanel").getHeight() + (now - orig));
}

function ViewLess(){
	var orig = Ext.get('tablePanel').getHeight();
	Ext.getDom("row_description").innerHTML = summary.description.substring(0, 600) + ' <a onclick="ViewMore()">more</a>';
	var now = Ext.get('tablePanel').getHeight();
	Ext.get("descriptionPanel").setHeight(now);
	Ext.getCmp("mainPanel").setHeight(Ext.getCmp("mainPanel").getHeight() - (orig-now));
}

function showFullList(){
	
	var rest = [{header:'Time Series', field:'timeseries'},
	            {header:'PI', field:'pi'}];
	var text = summary.def;
	
	for(var i=0; i<rest.length; i++){
		text +=  '<tr class="alt">';
		if(rest[i].field == "pi"){
			text += '<th class="first" width="20%">'+rest[i].header+' - Institution</th>';
		}else{
			text += '<tr class="alt"><th class="first" width="20%">'+rest[i].header+'</th>';
		}
		if(rest[i].field == "pi"){
			text += '<td class="last">'+(summary[rest[i].field]?summary[rest[i].field]:' ')+' - '+(summary['institution']?summary['institution']:' ')+'</td>';
		}else{
			text += '<td class="last">'+(summary[rest[i].field]?summary[rest[i].field]:'-')+'</td>';
		}
		text += '</tr>';
	}
	summary.lessList = '<tr class="alt"><td class="no-underline-links" colspan="2">';
	summary.lessList += '<a class="arrow-slate-e-up" href="javascript:void(0)" onclick="showLessList()">View less</a>';
	summary.lessList +='</td></tr></table></div>';
	
	var orig = Ext.get('tablePanel').getHeight();
	Ext.getDom("tablePanel").innerHTML = text + summary.lessList;
	var now = Ext.get('tablePanel').getHeight();
	Ext.get("descriptionPanel").setHeight(now);
	Ext.getCmp("mainPanel").setHeight(Ext.getCmp("mainPanel").getHeight() + now - orig);
}

function showLessList(){
	var orig = Ext.get('tablePanel').getHeight();
	Ext.getDom("descriptionPanel").innerHTML = summary.def + summary.fullList;
	var now = Ext.get('tablePanel').getHeight();
	Ext.get("descriptionPanel").setHeight(now);
	Ext.getCmp("mainPanel").setHeight(Ext.getCmp("mainPanel").getHeight() - (orig - now));
}


function CallBack() {

	var Page = $Page, property = Page.getPageProperties(), hash = property.hash, store = Page.getStore(0), grid = Page.getGrid();
	grid.setSortDirectionColumnHeader(hash.sort, hash.dir);
	Ext.getDom("grid_result_summary").innerHTML = ' <b>'+ store.totalCount + ' comparisons found	</b>';
}

function getExtraParams() {

	return {
		eid : Ext.getDom("eid").value,
		callType : 'getTable'
	};
}
function DownloadFile() {"use strict";

	var form = Ext.getDom("fTableForm");

	form.action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp", form.target = "", form.fileformat.value = arguments[0];
	getHashFieldsToDownload(form);
	form.submit();
}