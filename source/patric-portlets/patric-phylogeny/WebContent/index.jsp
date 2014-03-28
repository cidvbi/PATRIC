<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSummary" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String hostName = request.getServerName();
boolean hasMultipleOrders = false;

if ((cType.equals("taxon") || cType.equals("genome")) && cId!=null) 
{
	String taxonId = "";
	
	if (cType.equals("genome")) {
		DBShared conn_shared = new DBShared();
		ResultType names = (ResultType) conn_shared.getNamesFromGenomeInfoId(cId);
		taxonId = names.get("ncbi_taxon_id");
	} else {
		taxonId = cId;
	}
	
	DBSummary conn_summary = new DBSummary();
	ArrayList<ResultType> items = (ArrayList<ResultType>) conn_summary.getOrderInTaxonomy(Integer.parseInt(taxonId));
	ResultType item = null;
	Iterator<ResultType> iter = null;
	if (items.size() > 1) {
		hasMultipleOrders = true;
	}
	%>
	<!--[if IE]><script type="text/javascript" src="<%=request.getContextPath() %>/js/excanvas_r73.js"></script><![endif]-->
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/genomeMaps.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath() %>/js/TreeNav.js"></script>
	
	<p>Chose an order to view its mulit-gene phylogenetic tree in either phylogram or cladogram format. 
			Use controls above the tree to show or hide support values, and to color the tree according to genus or species.  
			Click on a species name to visit that species overview page.  
			To learn more about interacting with trees and the method used to create them, please see <a href="http://enews.patricbrc.org/phylogeny-faqs/" target="_blank">Phylogeny FAQs</a>.
	</p>
	
	<div id="tree_controls">
		<div> Show phylogentic tree for the order
		<% if (hasMultipleOrders) { %>
			<select id="tree_select_list" onchange="selectChange()">
			<% for (int i=0; i<items.size(); i++) { %>
				<option value="<%=items.get(i).get("ncbi_tax_id")%>" <%=(i==0)?"selected='selected'":"" %>><%=items.get(i).get("name")%></option>
			<% } %>
			</select>
		<% } else { %>
			<%=items.get(0).get("name") %>
			<input type="hidden" id="tree_select_list" value="<%=items.get(0).get("ncbi_tax_id") %>"/>
		<% } %>
		</div>
		
		<div style="float:left;width:25%">
			<span style="float:left">Draw tree as</span>
			<div style="margin-left:85px">
				<input type="radio" name="tree_type" id="tree_type_phylogram" value="phylogram" onclick="phylogram()" /> <label for="tree_type_phylogram">phylogram</label> <br/>
				<input type="radio" name="tree_type" id="tree_type_cladogram" value="cladogram" onclick="cladogram()" checked="checked" /> <label for="tree_type_cladogram">cladogram</label>
			</div>
		</div>
		
		<div style="float:left;width:25%">
			<div style="float:left">Color tree by</div>
			<div style="margin-left:85px">
				<input type="radio" name="tree_color" id="tree_color_speices" value="species" onclick="colorSpecies()" /> <label for="tree_color_speices">species</label> <br/>
				<input type="radio" name="tree_color" id="tree_color_genus" value="genus" onclick="colorGenus()" checked="checked" /> <label for="tree_color_genus">genus</label> <br/>
				<input type="radio" name="tree_color" id="tree_color_off" value="off" onclick="colorOff()" /> <label for="tree_color_off">off</label>
			</div>
		</div>
		
		<div style="float:left;width:25%">
			<div style="float:left">Turn support values</div>
			<div style="margin-left:120px">
				<input type="radio" name="tree_supports" id="tree_supports_on" value="on" onclick="showSupports()" checked="checked" /> <label for="tree_supports_on">on</label> <br/>
				<input type="radio" name="tree_supports" id="tree_supports_off" value="off" onclick="hideSupports()" /> <label for="tree_supports_off">off</label>
			</div>
		</div>
		
		<div style="float:left;width:25%">
			<a id="nwk_link" href="/patric-phylogeny/resources/readNWK.jsp?taxonID=<%=items.get(0).get("ncbi_tax_id") %>" target="_blank">Download tree as Newick file</a>
		</div>
		<div>
			<input type="button" class="button" title="Add Genome(s) to Workspace" value="Add Genome(s) to Workspace" onclick="AddToCart()">
		</div>
		
		<div class="clear"></div>
	</div>
	
	<div id="TreeNavDiv" style="position:relative">
	<div id="TreeNavDiv_checkbox" style="width:100%;"></div></div>
	
	
<%	
} else { 
	%>wrong parameter<%	
}
%>
<script type="text/javascript" src="/patric/static/phylogeny/outgroups.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript">
//<![CDATA[
var $Page,
	ZeroClipboard = null,
	pageProperties = {cart:true};
SetPageProperties(pageProperties);

var treeNav, orderMap = new Array();
orderMap["2037"] = "Actinomycetales";
orderMap["1385"] = "Bacillales";
orderMap["80840"] = "Burkholderiales";
orderMap["213849"] = "Campylobacterales";
orderMap["51291"] = "Chlamydiales";
orderMap["186802"] = "Clostridiales";
orderMap["91347"] = "Enterobacteriales";
orderMap["186826"] = "Lactobacillales";
orderMap["118969"] = "Legionellales";
orderMap["356"] = "Rhizobiales";
orderMap["766"] = "Rickettsiales";
orderMap["136"] = "Spirochaetales";
orderMap["72273"] = "Thiotrichales";
orderMap["135623"] = "Vibrionales";

function treeNav_init() {
    treeNav = new TreeNav.TreeNav('TreeNavDiv');
    selectChange();
}
function updateConfiguration() {

    if (Ext.getDom("tree_type_phylogram").checked) {
    	phylogram();
    } else {
    	cladogram();
    }

    if (Ext.getDom("tree_color_genus").checked) {
    	colorGenus();
    } else if (Ext.getDom("tree_color_speices").checked) {
    	colorSpecies();
    } else {
    	colorOff();
    }

    if (Ext.getDom("tree_supports_on").checked) {
    	showSupports();
    } else {
        hideSupports();
    }
}

function selectChange() {
	var sl = document.getElementById('tree_select_list');
	var tx = null;
	if (<%=hasMultipleOrders%>) {
		tx = sl.options[sl.selectedIndex].value;
	} else {
		tx = sl.value;
	}

	Ext.getDom("TreeNavDiv_checkbox").innerHTML = "";
	updateTree(tx);
	Ext.getDom("nwk_link").href = "/patric-phylogeny/resources/readNWK.jsp?taxonID="+tx;
}

function phylogram() {
    treeNav.showPhylogram();
}

function cladogram() {
    treeNav.showCladogram();
}

function showSupports() {
    treeNav.setSupportValueCutoff(100);
}

function hideSupports() {
    treeNav.setSupportValueCutoff(0);
}

function colorGenus() {
    treeNav.useColorSpecies(false);
    treeNav.useAutocolor(true);
}

function colorSpecies() {
    treeNav.useColorSpecies(true);
}

function colorOff() {
    treeNav.useAutocolor(false);
}

function updateTree(tID) {

	Ext.Ajax.request({
		url: "/patric/static/phylogeny/"+tID+".tree",
		disableCaching: false,
		method: 'GET',
		params: {},
		success: function(rs) {
			treeNav.setTree(rs.responseText);
			var outgrp = outgroups[orderMap[tID]];
			//console.log(tID, orderMap[tID], outgrp);
			
			if (outgrp.length > 0) {
				//console.log("updating outgroup:"+outgrp);
				treeNav.setOutgroup(outgrp);
			}
			updateConfiguration();
		}
	});
}

function AddToCart(){
	if (selectedGenomes == "") {
		alert("No item(s) are selected. To add to group, at least one item must be selected.");
	} else {
		addSelectedItems("Genome");
	}
}

Ext.onReady(function () {
	
	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();
	
	
	treeNav_init();
	
	if (Ext.get("tabs_phylogeny")!=null) {
		Ext.get("tabs_phylogeny").addCls("sel");
	}
	
	if(btnGroupPopupSave != null){
		btnGroupPopupSave.on('click', function(){
			saveToGroup(selectedGenomes, "Genome");
			clearSelectedGenomes();
		});
	}	
});
//]]>
</script>
