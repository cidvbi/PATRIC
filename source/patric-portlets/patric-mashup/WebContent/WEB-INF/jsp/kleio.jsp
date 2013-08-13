<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

DBShared conn_shared = new DBShared();

String name="";

if(cId != null && cId != "")
	name = conn_shared.getOrganismName(cId);

%>
<style type="text/css">
#PATRICGrid .x-grid3-cell-inner, .x-grid3-hd-inner { 
	white-space:normal !important; 
}
#PATRICGrid .x-grid3-cell-inner b {
	color: #1b2c49;
	font-size: 1.1em;
	font-family: Verdana;
}

#PATRICGrid .x-grid3-cell-inner {
	font-size: 1em;
	font-family: Verdana;
}

#PATRICGrid .x-grid3-cell-inner {
	padding: 9px;
}

.test-input { 
	border: 1.5pt solid #2D7597;
	font-size: 9pt;
	height: 18pt;
	width: 550pt;
}

</style>
<div id="container"></div>
<div id="GenomeSelector" style="padding-left: 5px; padding-right: 5px;">
</div>

<div id="pubmed_container">

<table border="0" cellspacing="8" cellpadding="0" width="320">
<tr>
	<td style="font-size:110%; color:red;">
		<b>CONTENT:<%=name %> AND </b><input type="text" class="test-input" id="keyword" name="keyword" value=""/>
		<br /><span class="hint" style="color:black">e.g. GENE:lipopolysaccharide AND PROTEIN:p70 </span>
	</td>
	<td><input type="submit" value="Search" onclick="searchkleio()" /></td>
</tr>
</table>
<div style="margin-left:0px" class="table-container">
<table width="100%"><tr><td>
	<div id="grid_result_summary"></div>
</td></tr></table>
</div>
<div id="PATRICGrid" style="margin-left:0px"></div>
</div>
<script type="text/javascript" src="/patric-common/js/TreeNodeTriStateUI.js"></script>
<script type="text/javascript" src="/patric-mashup/js/kleio.js"></script>

<script>

var panel, tree;
var static_keyword = "CONTENT:<%=name%>";

var legend = '<div style="font-size: 80%;">Legend: <br><span style="background: none repeat scroll 0% 0% rgb(0, 255, 0);"><font color="#003300">GENE or PROTEIN</font></span><span style="background: none repeat scroll 0% 0% rgb(255, 255, 0);"><font color="#800000">METABOLITE</font></span><!--   				<span style="background: #ffff00"><font color="#800000">SYSBIO</font></span> --><span style="background: none repeat scroll 0% 0% rgb(255, 204, 51);"><font color="black">BACTERIA</font></span><span style="background: none repeat scroll 0% 0% rgb(204, 204, 0);"><font color="black">ORGAN</font></span><span style="background: none repeat scroll 0% 0% rgb(204, 255, 0);"><font color="#008000">SYMPTOM or DISEASE</font></span><br><span style="background: none repeat scroll 0% 0% rgb(153, 0, 0);"><font color="#ffffff">PHENOMENON</font></span><span style="background: none repeat scroll 0% 0% rgb(102, 0, 255);"><font color="#ffffff">PROCEDURE</font></span><span style="background: none repeat scroll 0% 0% rgb(255, 0, 0);"><font color="#ffffff">INDICATOR</font></span><br><span style="background: none repeat scroll 0% 0% rgb(255, 170, 255);"><font color="#990000">Acronym</font></span></div>';

var ds = new Ext.data.Store({
	proxy: new Ext.data.HttpProxy({
		url:'<portlet:resourceURL/>',
		method: 'GET'
	}),
	reader:  new Ext.data.JsonReader({
		totalProperty: 'total',
		root: 'result',
		fields: ['authors','journal','date','pmid','title']
	})
});

var grid;

var scm = new Ext.grid.ColumnModel({
	defaults: {
		sortable:true
	},
	columns: [{header: "Publication", id:'x-grid3-cell-inner', renderer:renderArticle}]
});

Ext.onReady(function() {
	panel = new Ext.Panel({
		id: 'overview-panel',
		border:false,
		renderTo: 'container',
		height:910,
		layout: 'border',
		items: [{
			region: 'center',
			id:'pubmed-center-panel',
			title: 'Publications',
			border:false,
			contentEl: 'pubmed_container',
			split:true
		},{
			region: 'west',
			id:'tree-west-panel',
			title: 'Facets',
			border:false,
			contentEl: 'GenomeSelector',
			width: 320,
			collapsed:false,
			collapsible:true,
			split:true
		}]
	});

	tree = new Ext.tree.TreePanel({
		selectedParents: null,
		selectedIDs: null,
		changing: false,
		autoScroll:true,
		height:910,
		border: false,
		rootVisible: false,
		isTreeLoaded: false,
		renderTo:'GenomeSelector',
		cls: 'x-tree-noicon',
		root: new Ext.tree.AsyncTreeNode({
			loader: new Ext.tree.TreeLoader({
				dataUrl: '<portlet:resourceURL />',
				uiProviders: {
					tristate: Ext.tree.TreeNodeTriStateUI
				},
				baseParams: {type:"tree", keyword:static_keyword},
				baseAttrs: {"uiProvider":"tristate","checked":false},
			})
		}),
		listeners: {
			scope: this,
			checkchange: {
				fn: this.checkchange
			},
			click: function(node) {
				if (node.expanded == true) {
					node.collapse();
				} else {
					node.expand();
				}
			},
			checkchange: function(node, checked) {
				tree.selectedIDs = new Array();
	            tree.selectedParents = new Array();
				if(Ext.getDom("keyword").value != null && Ext.getDom("keyword").value != ""){
					
	            	var splitted = Ext.getDom("keyword").value.split(new RegExp(" AND | OR "));
	            	
	            	for(var i = 0; i< splitted.length; i++){
	            		
	            		if(splitted[i].split(":")[0] == "CONTENT"){
	            			tree.selectedParents.push("CONTENT");
	            			tree.selectedIDs.push("CONTENT:"+splitted[i].split(":")[1]);
	        	        }
	            	}
	
				}

				if (!this.changing) {
		            this.changing = true;
		            var _e = node.expanded;
		            node.expand(false, false,
		            function(node) {
		                node.cascade(function(node) {
		                    var _e = node.expanded;
		                    node.expand(false, false,
		                    function(node) {
		                        node.getUI().toggleCheck(checked);
		                    }
		                    );
		                    if (_e == false) {
		                        node.collapse();
		                    }
		                });
		                node.bubble(function(node) {
		                    if (node.parentNode) node.getUI().updateCheck(false)
		                });
		            });
		            
		            if (_e == false) {
		                node.collapse();
		            }

		            
		            this.changing = false;

			        Ext.each(tree.getChecked(),
			        function(node) {
			            if (node.attributes.parentID != null) {
		
			            	var flag = false;
			            	
			            	for(var i =0; i< tree.selectedParents.length; i++){
		
			            		if(node.attributes.parentID == tree.selectedParents[i]){
		
			            			flag = true;
				            		
			            		}
			            		
			            	}
		
			            	if(flag == false){
			            		tree.selectedParents.push(node.attributes.parentID);
			            	}
		
			            	tree.selectedIDs.push(node.attributes.parentID + ":"+node.attributes.id);

			            	Ext.getDom("keyword").value = TreeJOIN();
			            }
			        });
		       	}
 
			}
		}

	});

	searchkleio();
});

function TreeJOIN(){

	var joined_keyword = "";
	
	for(var i =0; i< tree.selectedParents.length; i++){

		var temp = new Array();

		for(var j =0; j< tree.selectedIDs.length; j++){

			var splitted = tree.selectedIDs[j].split(":");

			if(splitted[0] == tree.selectedParents[i]){

				temp.push(tree.selectedIDs[j]);
				
			}
			
		}

		if(i == 0)

			joined_keyword += temp.join(' OR ');

		else{

			joined_keyword += " AND " + temp.join(' OR ');

		}
		
	}
	
	return joined_keyword;
	
}

function searchkleio(){

	panel.getEl().mask( 'Loading...', 'x-mask-loading');
	
	if(Ext.getDom("keyword").value != null && Ext.getDom("keyword").value != ""){
		
		Ext.getDom("keyword").value = processKeyword(Ext.getDom("keyword").value);
		
	}
		
	if(Ext.getDom("keyword").value != null && Ext.getDom("keyword").value != ""){

		static_keyword += " AND " + Ext.getDom("keyword").value;
		
	}
	

	tree.selectedIDs = null;
	tree.selectedParents = null;
	
	ds.baseParams = {keyword:static_keyword, type:"grid"};

	if(grid == null){

		grid = new Ext.grid.GridPanel(
		    {
		        store: ds, // use the datasource
		        cm: scm,
		        viewConfig:
		        {
		        	headersDisabled: true
		        },
		        bbar: new Ext.PagingToolbar({
		                pageSize: 10,
		                store: ds,
		                displayInfo: true,
		                displayMsg: 'Displaying {0} - {1} of {2}',
		                emptyMsg: "No data to display"
		        }),
		        renderTo: "PATRICGrid",
		        autoScroll:true,
		        autoExpandColumn:'x-grid3-cell-inner',
		        height:776
		    });
		
	}else{
		grid.setWidth(grid.getWidth());
		grid.reconfigure(ds, scm);
		tree.getRootNode().attributes.loader.baseParams = {keyword:static_keyword, type:"tree"};
		tree.getRootNode().reload();
	}

	ds.load({
       params: {"start":0, "limit":10},
       callback:function(r, options, success) { 
			var msg = "";
			if (success) {
				msg = "<b>"+ds.getTotalCount()+" publications found</b><br/>";
			} else {

				msg = "<span style=\"color:red;font-weight:bold\">An Error Occurred While Loading Your Data.</span>"+
					" <br/><br/>";
			}
			Ext.getDom('grid_result_summary').innerHTML = msg;

			if(grid.store.data.items.length >= 10){
		           
	       		grid.setHeight(776);
	       		panel.setHeight(910);
		       	tree.setHeight(870);
		       	
	       }else if(grid.store.data.items.length == 1){
	           
		       	
		       	panel.setHeight(grid.store.data.items.length*390);
		       	grid.setHeight(panel.getHeight()-90);
		       	tree.setHeight(panel.getHeight()-40);
		       	
	       }else{
	           
		       	panel.setHeight(grid.store.data.items.length*200);
		       	grid.setHeight(panel.getHeight()-80);
		       	tree.setHeight(panel.getHeight()-40);
	       }

			panel.getEl().unmask();
		}

    });

	

	static_keyword = "CONTENT:<%=name%>";

}
</script>


