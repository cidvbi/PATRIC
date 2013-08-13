<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
DBShared dbh_shared = new DBShared();

%>
<div id="gp_tbl">
	<span class="right">Retrieving data...&nbsp;
		<img src="/patric/images/icon_please_wait.gif" alt="Please Wait" style="vertical-align:middle" />
	</span>
	<div style="clear:both"></div>
</div>

<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/metadata.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/json2.js" ></script>
<script type="text/javascript">
//<![CDATA[
var $Page,
	ZeroClipboard = null,
	pageProperties = {cart:true};
SetPageProperties(pageProperties);

function saveGenome(){
	addSelectedItems("Genome");
}

var decoded;
Ext.onReady(function () {
	
	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();

	setInterval("InsertTable()", 400 );

	Ext.Ajax.request({
		url: '/portal/portal/patric/Taxon/SequenceSummaryWindow?action=b&cacheability=PAGE',
		method: 'GET',
		params: {context_type:'<%=cType%>',context_id:'<%=cId%>'},
		success: function(rs) {
			Ext.getDom("gp_tbl").innerHTML = rs.responseText;
		},
		failure: function(rs) {
			Ext.getDom("gf_tbl").innerHTML = "Data is not available now. Please try again later.";
		}
	});
	
	if('<%=cType%>' == "genome"){
		Ext.Ajax.request({
			url: "/portal/portal/patric/GenomeFinder/GenomeFinderWindow?action=b&cacheability=PAGE",
			method: 'GET',
			params: {need:"from_genome", keyword:'<%=cId%>'},
			success: function(response, opts) {
				decoded = Ext.JSON.decode(response.responseText);
				InsertTable();
			}
		});
	}

	//prepare for adding to workspace
	btnGroupPopupSave.on('click', function(){
		if(saveToGroup("<%=cId %>", "Genome")){
			popup.hide();
		}	
	});

	//set tabs
	if (Ext.get("tabs_genomeoverview")!=null) {
		Ext.get("tabs_genomeoverview").addCls("sel");
	}
});

function InsertTable(){

	var table = Ext.getDom("metadata-td");
	
	if(table != null && table.tBodies[0].rows.length == 1 ){

		for(var i=0; i< metadataGenomeSummaryID.length; i++){
		
			var value = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]];
			var nextRow = table.tBodies[0].rows.length;
			var row = table.tBodies[0].insertRow(nextRow);
			//var cell0 = row.insertCell(0);
			var cell0 = document.createElement('th');
			row.appendChild(cell0);
			
			cell0.rowSpan = value.length;
			cell0.innerHTML = metadataGenomeSummaryID[i].split("_").join(" ");
			cell0.style.width="15%";
			
			var cell = row.insertCell(1);
			cell.innerHTML = "<span>"+metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].name+"</span> ";
			cell.style.display = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].style;
			cell.id=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_1";
			cell.style.width="30%";
			
			cell = row.insertCell(2);
			if(decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text] != null) {
				cell.innerHTML = "<span class=\"nowrap\">"+decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text]+"</span>";
			}
			else {
				cell.innerHTML = "&nbsp;";
			}
			cell.style.display = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].style;
			cell.style.width="40%";
			cell.id=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_2";
			cell.setAttribute('class', 'last');
			
			for(var j=1; j<value.length; j++){

				nextRow = table.tBodies[0].rows.length;
				row = table.tBodies[0].insertRow(nextRow);

				cell = row.insertCell(0);
				cell.innerHTML = "<span>"+metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].name+"</span> ";
				cell.style.display = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].style;
				cell.id=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_1";
				cell.style.width="30%";
				cell = row.insertCell(1);
				
				if(decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text] != null) {
					if (metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].link != null
							&& decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text] != 0 
							&& decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text] != "-") {
						cell.innerHTML = "<a href=\""
							+ Ext.String.format(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].link, decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].value])
							+ "\""
							+ " class=\""+ metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].linkClass + "\""
							+ " target=_blank>" 
							+ decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text] + "</a>";
					}
					else {
						cell.innerHTML = "<span class=\"nowrap\">"+decoded[metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text]+"</span>";
					}
				}
				else {
					cell.innerHTML = "&nbsp;";
				}
				cell.style.display = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].style;
				cell.style.width="40%";	
				cell.id=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_2";
				if (metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].cellClass != null) {
					cell.setAttribute('class', metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].cellClass + " last");
				}
				else {
					cell.setAttribute('class', 'last');
				}
			}
		}
		
		Ext.getDom("click_for_more").innerHTML += "<a class=\"arrow-slate-e-down\" href=\"javascript:ShowMore()\">Click for more</a>";	
	}
}

function ShowMore()
{
	for(var i=0; i< metadataGenomeSummaryID.length; i++)
	{
		var value = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]];
		for(var j=0; j<value.length; j++){
			Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_1").style.display="table-cell";
			Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_2").style.display="table-cell";
		}
		
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_1").style.borderTop="2px solid black";
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_2").style.borderTop="2px solid black";
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_2").parentNode.childNodes[0].style.borderTop="2px solid black";
	}
	
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][0].text+"_1").parentNode.childNodes[2].style.display="none";
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][0].text+"_2").parentNode.childNodes[1].style.display="none";
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][1].text+"_1").style.borderTop="2px solid black";
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][1].text+"_2").style.borderTop="2px solid black";
	
	Ext.getDom("click_for_more").innerHTML = "<a class=\"arrow-slate-e-up\" href=\"javascript:ShowLess()\">Click for less</a>";
}

function ShowLess()
{
	for(var i=0; i< metadataGenomeSummaryID.length; i++)
	{
		var value = metadataGenomeSummaryValue[metadataGenomeSummaryID[i]];	
		for(var j=0; j<value.length; j++){
			Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_1").style.display=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].style;
			Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].text+"_2").style.display=metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][j].style;
		}
		
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_1").style.borderTop="";
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_2").style.borderTop="";
		Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[i]][0].text+"_2").parentNode.childNodes[0].style.borderTop="";
	}
	
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][0].text+"_1").parentNode.childNodes[2].style.display="table-cell";
	Ext.getDom(metadataGenomeSummaryValue[metadataGenomeSummaryID[6]][0].text+"_1").parentNode.childNodes[1].style.display="table-cell";
	
	Ext.getDom("click_for_more").innerHTML = "<a class=\"arrow-slate-e-down\" href=\"javascript:ShowMore()\">Click for more</a>";
}
//]]>
</script>
