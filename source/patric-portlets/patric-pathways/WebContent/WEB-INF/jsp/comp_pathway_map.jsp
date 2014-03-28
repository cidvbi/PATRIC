<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBPathways" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="javax.portlet.PortletSession" %>
<portlet:defineObjects/>
<%

DBPathways conn_pathways = new DBPathways();
ResultType item = null;
ArrayList<ResultType> ecAssignments = null;
ArrayList<ResultType> taxongenomecounts = null;

String pk = request.getParameter("param_key") != null || request.getParameter("param_key") != ""?request.getParameter("param_key"):"",
	dm = request.getParameter("display_mode") != null || request.getParameter("display_mode") != ""?request.getParameter("display_mode"):"",
	cType = request.getParameter("context_type") != null || request.getParameter("context_type") != ""?request.getParameter("context_type"):"",
	map = request.getParameter("map") != null || request.getParameter("map") != ""?request.getParameter("map"):"",
	algorithm = request.getParameter("algorithm") != null || request.getParameter("algorithm") != ""?request.getParameter("algorithm"):"",
	cId = request.getParameter("context_id") != null || request.getParameter("context_id") != ""?request.getParameter("context_id"):"",
	ec_number = request.getParameter("ec_number") != null || request.getParameter("ec_number") != ""?request.getParameter("ec_number"):"",
	feature_info_id = request.getParameter("feature_info_id") != null || request.getParameter("feature_info_id") != ""?request.getParameter("feature_info_id"):"",
	ec_names = "",
	occurrences = "",
	genomeId = "",
	taxonId = "",
	taxongenomecount_patric = "0",
	taxongenomecount_brc = "0",
	taxongenomecount_refseq = "0",
	pathway_name = "",
	pathway_class = "",
	definition = "",
	attributes = conn_pathways.getPathwayAttributes(map);

if(algorithm != null && !algorithm.equals("")){
	if(algorithm.equals("BRC") || algorithm.equals("Legacy BRC") || algorithm.equals("Legacy"))
		algorithm = "Curation";
	else if(algorithm.equals("PATRIC"))
		algorithm = "RAST";
}	
	
if(cType == null || cType.equals("")){	
	ResultType key = (ResultType) portletSession.getAttribute("key"+pk, PortletSession.APPLICATION_SCOPE);
	
	if(key != null && key.containsKey("genomeId") && !key.get("genomeId").toString().equals("")){
		taxonId = "";
		genomeId = key.get("genomeId");
		taxongenomecounts = new DBPathways().getTaxonGenomeCount(genomeId, "genomelist");
	} else{
		if(key != null && key.containsKey("taxonId") && !key.get("taxonId").toString().equals("")){
			taxonId = key.get("taxonId");
		}else{
			taxonId = "2";
		}
		taxongenomecounts = new DBPathways().getTaxonGenomeCount(taxonId, "taxon");
		genomeId = "";
	}
	
	if(key != null && key.containsKey("feature_info_id")){
		feature_info_id = key.get("feature_info_id");
	}	
	
}else if(cType.equals("taxon")){
	genomeId = "";
	if(cId == null || cId == "")
		taxonId = "2";
	else
		taxonId = cId;
	taxongenomecounts = new DBPathways().getTaxonGenomeCount(taxonId, "taxon");	
}else if(cType.equals("genome")){
	taxonId = "";
	genomeId = cId;
	taxongenomecounts = new DBPathways().getTaxonGenomeCount(genomeId, "genome");
}

if(dm != null && dm.equals("ec")){
	ecAssignments = new DBPathways().EC2ECProperties(ec_number, map);
	for ( Iterator<ResultType> iter = ecAssignments.iterator(); iter.hasNext(); ) {
		item = iter.next();
		ec_names = item.get("description");
		occurrences = item.get("occurrence");
	}
}else if(dm != null && dm.equals("feature")){
	ecAssignments = new DBPathways().aaSequence2ECAssignments(feature_info_id, map);
	for ( Iterator<ResultType> iter = ecAssignments.iterator(); iter.hasNext(); ) {
		item = iter.next();
		ec_number = item.get("ec_number");
		ec_names = item.get("description");
		occurrences = item.get("occurrence") ;
	}
}

for(int i =0; i < taxongenomecounts.size(); i++){
	ResultType g = (ResultType) taxongenomecounts.get(i);	
	if(g.get("algorithm").equals("RAST")){
		taxongenomecount_patric = g.get("count");
	}else if(g.get("algorithm").equals("Curation")){
		taxongenomecount_brc = g.get("count");
	}else if(g.get("algorithm").equals("RefSeq")){
		taxongenomecount_refseq = g.get("count");
	}
}

definition = attributes.split(";")[2];
pathway_name = attributes.split(";")[0];
pathway_class = attributes.split(";")[1];

%>
<style>
	.idvg-legend {
		background-color: transparent;
	}
	
	.idvg-legend-block {
		float: left;
		border: 1px #9EBEC8 solid;
		padding: 5px;
		margin:  2px;
	}
	
	#idvg-legend-edges {
		margin-left: 8px;
	}

	.idvg-legend-title {
		font-weight: bold;
		color: #0E4771;
		padding-bottom: 6px;
		padding-left: 4px;
		padding-top: 3px;
	}
	
	.idvg-legend-entry {
		margin-left: 8px;
		padding-bottom: 15px;
		line-height: 22px;
	}
	
	.idvg-legend-entry-title {
		padding-bottom: 2px;
		font-style: italic;
	}
	
	.idvg-legend-symbol img {
		height: 20px;
		width:  20px;
		padding-right: 4px;
		vertical-align: middle;
	}
	
	.idvg-legend-label {
		vertical-align: middle;
	}
	

.titleClass{
	color:#15428B;
	font-family:tahoma,arial,verdana,sans-serif;
	font-size:11px;
	font-weight:bold;
	padding:4px;
}

table.checkbox {
	font-family: tahoma,arial,verdana,sans-serif;
	font-weight: normal;
	font-size: 9px;
	color: #404040;
	width: 100%;
	background-color: #fafafa;
	border: 1px #6699CC solid;
	border-collapse: collapse;
	border-spacing: 2px;
	margin-top: 0px;
}
table.checkbox th {
	border-bottom: 2px solid #6699CC;
	background-color: #FFFFFF;
	text-align: center;
	font-family: tahoma,arial,verdana,sans-serif;
	font-size: 11px;
	color: #404040;
	padding-top:4px;
	padding-bottom:4px;
}
table.checkbox td {
	border-bottom: 1px dotted #6699CC;
	font-family: tahoma,arial,verdana,sans-serif;
	font-weight: normal;
	font-size: 10px;
	color: #000000;
	background-color: white;
	text-align: center;
	padding-top:3px;
	padding-bottom:3px;
}

</style>

<form id="fMapForm" action="#" method="post">
	<input type="hidden" id="tablesource" name="tablesource" value="MapFeatureTable" />
	<input type="hidden" id="cType" name="cType" value="<%=cType %>" />
	<input type="hidden" id="cId" name="cId" value="<%=cId %>" />
	<input type="hidden" id="taxonId" name="taxonId" value="<%=taxonId%>" />
	<input type="hidden" id="algorithm" name="algorithm" value="<%=algorithm %>" />
	<input type="hidden" id="taxongenomecount_patric" name="taxongenomecount_patric" value="<%=taxongenomecount_patric%>" />
	<input type="hidden" id="taxongenomecount_brc" name="taxongenomecount_brc" value="<%=taxongenomecount_brc%>" />
	<input type="hidden" id="taxongenomecount_refseq" name="taxongenomecount_refseq" value="<%=taxongenomecount_refseq%>" />
	<input type="hidden" id="genomeId" name="genomeId" value="<%=genomeId%>" />
	<input type="hidden" id="map" name="map" value="<%=map %>" /> 
	<input type="hidden" id="definition" name="definition" value="<%=definition %>" />
	<input type="hidden" id="ec_number" name="ec_number" value="<%=ec_number %>" /> 
	<input type="hidden" id="ec_names" name="ec_names" value="<%=ec_names %>" /> 
	<input type="hidden" id="feature_info_id" name="feature_info_id" value="<%=feature_info_id %>" />
	<input type="hidden" id="data" name="data" value="" />
	<input type="hidden" id="pk" name="pk" value="<%=pk %>" /> 
	<input type="hidden" id="mapaction" name="mapaction"/>
	<input type="hidden" id="dm" name="dm" value="<%=dm %>" /> 
	<input type="hidden" id="mapdiv" name="mapdiv"/>
	<input type="hidden" id="fids" name="fids" value="" /> 
	<input type="hidden" id="fileformat" name="fileformat" value="" />
	<input type="hidden" id="key" name="key" value="5" />
</form>


<div>
<table width="100%" border="0" cellspacing="2">
	
	<tr style="height: 20px;">
	<td style="font-weight:bold; width:110px;"> Pathway ID : </td>
	<td width="20%"><%=map %></td>	
	<%if(cType.equals("feature") || (dm != null && dm.equals("feature"))){%>
	<td style="font-weight:bold; width:110px;"> EC Number : </td>
	<td>
	<%=ec_number%>		
	</td>
	<%} else if(cType.equals("ec") || (dm != null && dm.equals("ec"))){%>
	<td style="font-weight:bold; width:110px;"> EC Number : </td>
	<td>
	<%=ec_number%>		
	</td>
	<%} %>
	<td>
	</td>
		<td>
	</td>
		<td>
	</td>
	<td style="text-align:left; width:480px" rowspan="3" >
	<p style="line-height:200%;">
	 Pathways provide a visual representation of relationships between features.
	   For a description of what you can do from this Comparative Pathways Map page, please see
	    <a href="http://enews.patricbrc.org/comparative-pathway-tool-faqs/" target="_blank">Comparative Pathway Tool FAQs</a>.
	      For an explanation of various annotation sources, please see 
	      <a href="http://enews.patricbrc.org/annotation-faqs/" target="_blank">Annotation FAQs</a>.
	 </p>
	</td>
	</tr>
	<tr style="height: 20px;">
	<td style="font-weight:bold; width:110px;"> Pathway Name : </td><td><%=pathway_name %></td>
	<%if(cType.equals("feature") || (dm != null && (dm.equals("ec") || dm.equals("feature")))){%>
	<td style="font-weight:bold; width:110px;"> Description : </td>
	<td>
	<%= ec_names%>		
	</td>
	<%} else{%>
	<td>
	</td>
	<%} %>
	<td>
	</td>
		<td>
	</td>
		<td>
	</td>
	</tr>
	<tr style="height: 20px;">
	<td style="font-weight:bold; width:110px;"> Pathway Class : </td>
	<td><%=pathway_class %></td>
	<%if(cType.equals("feature") || cType.equals("ec") || (dm != null && (dm.equals("ec") || dm.equals("feature")))){%>
	<td style="font-weight:bold; width:110px;"> Occurrence : </td>
	<td>
		<%=occurrences %>
	</td>
	<%} else{%>
	<td>
	</td>
	<%} %>
	<td>
	</td>
		<td>
	</td>
		<td>
	</td>
	</tr>
	</table>
</div>

<div id="description_div">
</div>
<div id="definition_div">
<p align="justify">
    
    <%if(!definition.equals("")){ %>
    
    <%=definition %> 
    <br/>For more information. Please visit <a href="http://www.genome.jp/kegg/pathway/map/map<%=map %>.html" target="_blank">KEGG</a>.
	<%}else{ %>
		For more information. Please visit <a href="http://www.genome.jp/kegg/pathway/map/map<%=map %>.html" target="_blank">KEGG</a>.
	<%} %>
	</p>
	</div>
<div id="TabTable"></div>
<div id="kegg_panel_div"></div>
<div id="heatmap_panel_div"></div>


<div id ="map_div"></div>

<div id='heatmap' style='visibility:hidden;'>
<div id='flashTarget'></div>
</div>
<div id="legend" style="visibility:hidden; height:0px;">

	<div class="idvg-legend-title">Heatmap Cells</div>
	<p style="padding-left:4px;">Cell color represents the number of proteins from a specific genome in a given EC number.</p> 
	<br/>
	<div class="idvg-legend-entry">
		<span class="idvg-legend-symbol">
			<img src="/patric/images/heatmap/heatmap-black-0.png" alt=""/>
		</span>
		<span class="idvg-legend-label">0</span>
	</div>
	<div class="idvg-legend-entry">
		<span class="idvg-legend-symbol">
			<img src="/patric/images/heatmap/heatmap-yellow-1.png" alt=""/>
		</span>
		<span class="idvg-legend-label">1</span>
	</div>
	<div class="idvg-legend-entry">
		<span class="idvg-legend-symbol">
			<img src="/patric/images/heatmap/heatmap-orange-2.png" alt=""/>
		</span>
		<span class="idvg-legend-label">2</span>
	</div>
	<div class="idvg-legend-entry">
		<span class="idvg-legend-symbol">
			<img src="/patric/images/heatmap/heatmap-red-3.png" alt=""/>
		</span>
		<span class="idvg-legend-label">3+</span>
	</div>
</div>

<div id ="div_tooltip_map">
<div style="display:none;">
	<div id="content-tip">
</div>
</div>
</div>

<div id='PATRICGrid'></div>
<div id="copy-button" style="display:none"></div>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-pathways/js/wz_jsgraphics.js"></script>
<script type="text/javascript" src="/patric-pathways/js/comp_operations.js"></script>
<script type="text/javascript" src="/patric-pathways/js/browser_detect.js"></script>
<script type="text/javascript" src="/patric-pathways/js/print_download.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICSelectionModel.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/PATRICGrid.js"></script>    
<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/copybutton.js"></script>
<script type="text/javascript" src="/patric-common/js/grid/pagingbar.js"></script>
<script type="text/javascript" src="/patric-pathways/js/scrollonmap.js"></script>
<script type="text/javascript" src="/patric-pathways/js/createheatmapdata.js"></script>
<script type="text/javascript" src="/patric-common/js/heatmap/heatmapDatatypes.js"></script>
<script type="text/javascript" src="/patric-common/js/heatmap/swfobject.js"></script>
<script type="text/javascript" src="/patric-pathways/js/heatmap/loadHeatmap.js"></script>
<script type="text/javascript" src="/patric-pathways/js/heatmap/heatmapMediator.js"></script>

<script>
//<![CDATA[
var $Page;
var pageProperties = {cart:true};
SetPageProperties(pageProperties);


var firstload = true;

ZeroClipboard.setMoviePath('/patric-common/js/ZeroClipboard.swf');

var current_hash = "";
var name = "pathway_map_table";
var grid = null;
var axis = "";

Ext.onReady(function(){

	Ext.state.Manager.set("PathwayHeatmapState", null);
	
	if (Ext.get("tabs_pathways")) {
		Ext.get("tabs_pathways").addCls("sel");
	}

	Ext.define('Map', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'ec_number',	type:'string'},
			{name:'genome_count',	type:'int'},
			{name:'gene_count',	type:'int'},
			{name:'occurrence',	type:'int'},
			{name:'ec_name',	type:'string'}
		]
	});
	
	var algo = getAlgorithm(Ext.getDom("algorithm").value);
	
	var myTextItem_kegg = Ext.create('Ext.Toolbar.TextItem', {text: 'Total # of Genomes : <b>0</b>',
		listeners:{
			render: function(){
				this.setText('Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+algo.toLowerCase()).value+'</b>');		
			}
		}
	});

	var myTextItem_heatmap = Ext.create('Ext.Toolbar.TextItem', {text: 'Total # of Genomes : <b>0</b>',
		listeners:{
			render: function(){
				this.setText('Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+algo.toLowerCase()).value+'</b>');		
			}
		}
	});
	
	var t1 = Ext.create('Ext.toolbar.Toolbar', {
		items:['Annotation Source : ', {
			text: '<b>'+getAlgorithm(Ext.getDom("algorithm").value)+'</b>',
		   	xtype:'splitbutton',
		   	ctCls:'x-btn-over',
			menu: [{
				text: 'PATRIC',
				handler: function(){
	  				refresh("PATRIC");
				}
			},{
				text: 'Legacy BRC',
				handler: function(){
					refresh("Legacy BRC");
				}
			},{
				text: 'RefSeq',
					handler: function(){
						refresh("RefSeq");
					} 
			}]
		},
		'->', myTextItem_kegg
	]});

	var t2 = Ext.create('Ext.toolbar.Toolbar', {
		items:['Annotation Source : ', {
			text: '<b>'+getAlgorithm(Ext.getDom("algorithm").value)+'</b>',	
			xtype:'splitbutton',
		   	ctCls:'x-btn-over',
			menu: [{
				text: 'PATRIC',
				handler: function(){
					refreshHeatmap("PATRIC");  				
				}
			},{
				text: 'Legacy BRC',
				handler: function(){
					refreshHeatmap("Legacy BRC"); 
				}
			},{
				text: 'RefSeq',
				handler: function(){
					refreshHeatmap("RefSeq"); 
				} 
			}]
		}, {xtype: 'tbspacer', width: 30}, {
			text : "Flip Axis",
			handler : function() {
				if(axis == "Transpose"){
					axis = "";
				}else{
					axis = "Transpose";
				}
				flipAxises();			
				flashShouldRefreshData(jQuery(heatmapid)[0]);
			}
		},
		{xtype: 'tbspacer', width: 48},  myTextItem_heatmap
	]});

	Ext.create('Ext.tab.Panel', {
		activeTab: 0,
		renderTo: 'TabTable',
		id:	'tabs', 
		height: 25,
		enableTabScroll:true,
		border: false,
		layoutOnTabChange: true,
		forceFit: true,
		items: [{title:"KEGG Map", id:"kegg-tab"},{title:'Heatmap', id:'heatmap-tab'}],
	    listeners: {
		    tabchange: function(){

				if(Ext.getCmp('tabs').getActiveTab().getId() == "kegg-tab"){
					
					Ext.state.Manager.set("PathwayHeatmapState", displayStateOfFlash(jQuery(heatmapid)[0]));
					
					if(Ext.getCmp('kegg-panel')){		

						if(getAlgorithm(Ext.getDom("algorithm").value) == "BRC")
		            		Ext.getCmp('kegg_matrix_panel_div').getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = "Legacy BRC";
		            	else
		            		Ext.getCmp('kegg_matrix_panel_div').getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = getAlgorithm(Ext.getDom("algorithm").value);						
	  					
	  					loadGrid();
	  					Ext.getCmp('kegg-panel').setVisible(true);
						
					}
					
					if(Ext.getCmp('heatmap_panel')){
						Ext.getCmp('heatmap_panel').setVisible(false);
					}
					
					myTextItem_kegg.setText('Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+getAlgorithm(Ext.getDom("algorithm").value).toLowerCase()).value+'</b>');
					
						
				}else if(Ext.getCmp('tabs').getActiveTab().getId() == "heatmap-tab"){

					if(Ext.getCmp('kegg-panel')){
						
						Ext.getCmp('kegg-panel').setVisible(false);
					}

					if(Ext.getCmp('heatmap_panel')){
											
						Ext.getCmp('heatmap_panel').setVisible(true);

						Ext.getDom('heatmap').style.visibility = 'visible';
						
		            	if(firstload == true)
			            	loadHeatMap("load");
		            	else{
			            	firstload = false;
			            	processData_GenomeColumn("refresh");
		            	}
		            	if(getAlgorithm(Ext.getDom("algorithm").value) == "BRC")
		            		Ext.getCmp('heatmap-panel').getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = "Legacy BRC";
		            	else
		            		Ext.getCmp('heatmap-panel').getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = getAlgorithm(Ext.getDom("algorithm").value);
		            	
					}
					myTextItem_heatmap.setText('Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+getAlgorithm(Ext.getDom("algorithm").value).toLowerCase()).value+'</b>');				
				}
			}
		}
	});


	Ext.create('Ext.panel.Panel', {
       	renderTo:'kegg_panel_div',
       	layout: 'border',
       	height:650,
       	id:'kegg-panel',
       	items: [{
            region: 'center',
            id:'kegg-map-panel',
            title: 'KEGG Map',   
			split:true,
			contentEl: 'map_div',
			html: '<img id = \"map_img\" src=\"/patric/images/pathways/map<%=map%>.png\" alt="" >',
			autoScroll: true,
			border:false,
			tbar: [{
			   		scale: 'small',  
			   		iconAlign: 'left', 
			   		width: 110,
			   		text:'Legend', 
			   		xtype:'splitbutton',
			   		menu: [{
			   					text: 'Annotated',
			   					icon: '/patric/images/BEFDBE.png'
			   		 		}, 
			   		 		{
			   		 			text: 'Not annotated',
			   		 			icon: '/patric/images/D5E6D5.png'
		   		 			},{
		   		 				text: 'Selected',
			   		 			icon: '/patric/images/99CCFF.png'
		   		 			}]
		   	 		},'->','-',{
           	   	 cls: 'x-btn-text-icon'
           	    ,icon: '/patric/images/pathways/save.gif'
           	    ,text: 'Save Map'
				,handler: function(){

	            	Ext.getDom("fMapForm").action = "/patric-pathways/jsp/map_download_handler.jsp";
	        		Ext.getDom("mapaction").value = "download";
	        		Ext.getDom("mapdiv").value = download_map();
	        		Ext.getDom("fMapForm").target = "";
	        		Ext.getDom("fMapForm").submit();
				
				}
			}, 
			{
        	    cls: 'x-btn-text-icon'
        	    ,icon: '/patric/images/pathways/printer.gif'
        	    ,text: 'Print Map'
	            ,handler: function(){

				 print_map();
	            
				}
           	}]
   		},{
            region: 'west',
            contentEl: 'PATRICGrid',
            id:'kegg_matrix_panel_div',
            title: 'EC Table',   
            border:false,
			width: 380,
            collapsed:false,
            layout:'fit',
            collapsible:true,
            split:true,
            tbar : t1
   		}]
		
	});

	Ext.create('Ext.panel.Panel', {
		renderTo:'heatmap_panel_div'
		,hidden:true
       	,height:650
       	,layout:'border'
       	,id: "heatmap_panel"
       	,items: [{
            region: 'center',
            id:'heatmap-panel',
            title: 'HeatMap',   
			border:false,
			contentEl: 'heatmap',
			tbar:t2
   		},{
			region: 'west',
			id: 'west-panel', 
			stateful: true,
			stateEvents: ['collapse','expand','resize'],
			stateId: 'PWhmLengendPanel',
			split: true,
			collapsible:true,
			width: 200,
			bodyStyle:'padding:2px; background-color:white;',
			title:'Legend',
			contentEl: 'legend',
			collapsed:false,
			border: false,
			autoScroll: false,
			html: Ext.getDom("legend").innerHTML
        }]
		
	});

	description_panel =  Ext.create('Ext.panel.Panel',{
		
		renderTo: 'description_div'
		,contentEl: 'definition_div'
		,region: 'north'
		,height: 80
		,border:false
		,collapsible: true
		,bodyStyle:'padding:2px;'
		,title: 'Pathway Description'
		,id: 'description_panel'
		,autoScroll: true
		
	});
    
	drawMap("all");
	loadGrid("firstload");
	overrideButtonActions();  
    
});

function refresh(that){
	var algo = Ext.getDom("algorithm").value,
		panel = Ext.getCmp('kegg_matrix_panel_div');
	
	if(algo != that){
		panel.getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = that,
		panel.setWidth(380),
		panel.doLayout();
  		panel.getDockedItems()[1].items.items[3].el.dom.innerHTML = 'Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+getAlgorithm(that).toLowerCase()).value+'</b>',	
		Ext.getDom("algorithm").value = getAlgorithm(that, true),
		loadGrid("refresh");
	}
	
}

function refreshHeatmap(that){
	var algo = Ext.getDom("algorithm").value,
		panel = Ext.getCmp('heatmap-panel');

	if(algo != that){
		panel.getDockedItems()[1].items.items[1].btnEl.dom.childNodes[0].childNodes[0].innerHTML = that;
		Ext.getDom("algorithm").value = getAlgorithm(that, true),
		Ext.getCmp('heatmap-panel').getDockedItems()[1].items.items[5].el.dom.innerHTML = 'Total # of Genomes : <b>'+Ext.getDom("taxongenomecount_"+getAlgorithm(that).toLowerCase()).value+'</b>';
		loadGrid("refresh");
	}
	
}

function loadGrid(action){
	
	Ext.create('Ext.data.Store', {
		storeId: 'ds',
		model: 'Map',
		proxy: {
			type: 'ajax',
			url:'<portlet:resourceURL/>',
			noCache:false,
			timeout: 600000, //10*60*1000
			reader: {
				type:'json',
				root:'results',
				totalProperty:'total'	
			},
			extraParams: {
				genomeId: Ext.getDom("genomeId").value 
				,taxonId: Ext.getDom("taxonId").value 
				,cType: Ext.getDom("cType").value 
				,map:Ext.getDom("map").value 
				,pk: Ext.getDom("pk").value
				,algorithm: Ext.getDom("algorithm").value
				,sort:'ec_number'
				,dir:'ASC'

			}
		},
		listeners:{
			'datachanged':function(){
			
				if(action != "firstload"){
	
					if(Ext.getCmp('tabs').getActiveTab().getId() == "heatmap-tab"){
						loadHeatMap("refresh");
					}
					if(Ext.getCmp('tabs').getActiveTab().getId() == "kegg-tab"){
					  	pNS.boxes.paint();
					}
									
				}
			}
		}
	});

	scm = [{header:'EC Number', dataIndex:'ec_number', width: 70, align:'center', sortable: true, renderer:renderTextWeight},
    	    {header:'Genome Count', dataIndex:'genome_count', width: 60, align:'center', sortable: true, renderer:renderTextWeight},
    	    {header:'Feature Count', dataIndex:'gene_count', width: 60, align:'center', sortable: true, renderer:renderMapTableGene},
    	    {header:'Genome Count Not Present', dataIndex:'genome_count', width: 80, align:'center', sortable: true, renderer:renderTextWeight},
    	    {header:'Occurrence', dataIndex:'occurrence', width: 70, align:'center', sortable: true, renderer:renderTextWeight},
    	    {header:'Description', dataIndex:'ec_name', width: 120, align:'left', sortable: true, hidden:true, renderer:renderTextWeight}];

	if(grid == null){
	
		grid = Ext.create('Ext.grid.PATRICGrid',{
			store: 'ds',
			columns: scm,
			viewConfig: {forceFit:true},
			renderTo:'PATRICGrid',
			height:595
		});
				
	}else{
		grid.reconfigure('ds');
	}
	
	Ext.getStore('ds').load();

}

function overrideButtonActions() 
{
	var current_ec = "", click = 0;
	
	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();
	
	btnGroupPopupSave.on('click', function(){
		saveToGroup(Ext.getDom("fids").value, "Feature");		
	});

	grid.on('itemmousedown', function(view, record, item, index, e) {	
    	var record = grid.getStore().getAt(index),		    
    		fieldName = grid.columns[0].dataIndex,
	    	data = record.get(fieldName);

		(current_ec != data)?(current_ec = data,click = 0):click++;		
		click = ScrollOnMap(data, click);
	});		   
}

function renderTextWeight(value, metadata, record, rowIndex, colIndex, store){

	var algo = Ext.getDom("algorithm").value,
		rgc = record.data.genome_count,
		gc = parseInt(Ext.getDom("taxongenomecount_"+getAlgorithm(algo).toLowerCase()).value);

	if(rgc == gc){
		return Ext.String.format('<b>{0}</b>', (colIndex == 3)?gc - value:value);
	}else{
		return Ext.String.format('{0}' , (colIndex == 3)?gc - value:value);
	}
	
}

function renderMapTableGene(value, metadata, record, rowIndex, colIndex, store){
	
	var algo = getAlgorithm(Ext.getDom("algorithm").value),
		rgc = parseInt(record.data.genome_count),
		gc = parseInt(Ext.getDom("taxongenomecount_"+getAlgorithm(algo).toLowerCase()).value),
		cType = Ext.getDom("cType").value || null;

	if(rgc == gc){
		if(cType && (cType == "taxon" || cType == "genome"))
			return Ext.String.format('<a href=\"CompPathwayTable?cType={0}&amp;cId={1}&amp;algorithm={2}&amp;ec_number={3}&amp;map=\" target=\"_blank\"><b>{4}</b></a>',Ext.getDom("cType").value, Ext.getDom("cId").value, algo, record.data.ec_number, value);
		else
			return Ext.String.format('<a href=\"PathwayFinder?cType=&amp;cId=&amp;dm=result&amp;pk={0}&amp;algorithm={1}&amp;ec_number={2}&amp;map=\" target=\"_blank\"><b>{3}</b></a>',Ext.getDom("pk").value, algo, record.data.ec_number, value);		
	}else{ 
		if(cType && (cType == "taxon" || cType == "genome"))
			return Ext.String.format('<a href=\"CompPathwayTable?cType={0}&amp;cId={1}&amp;algorithm={2}&amp;ec_number={3}&amp;map=\" target=\"_blank\">{4}</a>',Ext.getDom("cType").value, Ext.getDom("cId").value, algo, record.data.ec_number, value);
		else
			return Ext.String.format('<a href=\"PathwayFinder?cType=&amp;cId=&amp;dm=result&amp;pk={0}&amp;algorithm={1}&amp;ec_number={2}&amp;map=\" target=\"_blank\">{3}</a>',Ext.getDom("pk").value, algo, record.data.ec_number, value);
	}
			
}

//]]>
</script>

