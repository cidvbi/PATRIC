<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />
<%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="figfamGroups.edu.vt.vbi.sql.FigFam" %>
<%@ page import="java.util.*" %>

<%
String resourceURL = (renderResponse.createResourceURL()).toString();

DBShared conn_shared = new DBShared();
HashMap<String, String> key = new HashMap<String,String>();
// TODO: if genomeId is given, set key attribute to that it is selected.

String ncbi_taxon_id = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

System.out.print(resourceURL);
String expander = "";
String taxonName = "";
ArrayList<ResultType> ids = null;
System.out.println("cId->"+cId);

if (cType!=null && cId!=null && cType.equals("taxon")) {
	ncbi_taxon_id = (cId.equals(""))?"2":cId;
	key.put("ncbi_taxon_id", ncbi_taxon_id);
	FigFam access = new FigFam();
	taxonName = access.getTaxonName(ncbi_taxon_id);
} 
expander = ((!taxonName.equals("Bacteria"))?"<div style='float:right'><div class='searchtool-inside'><h2 align='center'>Want to make comparisons not limited to " + taxonName + "?</h2><br />Use PATRIC's <a href='FIGfamSorter?cType=taxon&cId=&dm='>Protein Family Sorter</a> located in Searches & Tools.<br/><b><br/>* Any genome selections you have made will be discarded!<b/></div></div>":"");
String name = "";
if(cId == null || cId.equals("")) {
	name = "Bacteria";
}
else {
	if(cType.equals("taxon")) {
		name = conn_shared.getOrganismName(cId);
	} else if (cType.equals("genome")) {
		name = conn_shared.getGenomeName(cId);
	}
}

boolean loggedIn = false;
if (request.getUserPrincipal() == null) {
	loggedIn = false;
}
else {
	loggedIn = true;
}
%>

<div style="padding: 5px;" >
	<div id="intro" class="searchtool-intro">
	<p>The Protein Family Sorter tool enables researchers to examine the distribution of specific protein families, known as FIGfams, across different genomes. <i><b>Protein Family Sorter currently supports comparison up to 500 genomes.</b></i> Select genomes using 'Taxonomy Tree' or 'A-Z List' tab below or create custom Genome Groups using <a href="GenomeFinder?cType=taxon&cId=&dm=">Genome Finder</a>. For further explanation, please see 
	<a href="http://enews.patricbrc.org/protein-family-sorter/">Protein Family Sorter FAQs.</a>
	</p>
	</div>
	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" border="0" /> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px">
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" border="0" />
		Enter keyword</h3><br />
		<form action="#" onsubmit="return false;">
		<table border="0" cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
			<td class="first">Keyword </td>
			<td class="last"><div id="keyword"></div>
			</td>
		</tr>
		<tr>
			<td class="right" colspan="2"><div style="padding-top:5px;"><input class="button" type="submit" value="Search" onclick="checkGenomes()"></div></td>
		</tr>
		</tbody>
		</table><br/>
		
		<table>
		<tr>
			<td><%=expander %></td>
		</tr>
		</table>
		</form>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
var loggedIn = <%=loggedIn%>;
var tabs = "";

Ext.onReady(function(){
	tabs = Ext.create('VBI.GenomeSelector.Panel', {
		renderTo: 'GenomeSelector',
		width: 480,
		height: 550,
		border:false,
		parentTaxon: <%=(ncbi_taxon_id==null)?"2":ncbi_taxon_id %>,
		organismName:'<%=name%>'
	});
	
	Ext.create('Ext.form.field.TextArea', {
		id:'tb',
		renderTo: 'keyword',
		width: 285,
		height: 50
	});
});

function checkGenomes() {
	var idList = tabs.getSelectedInString();
	
	if(idList == null || idList.length == 0 ) {
		
		Ext.Ajax.request({
			url : '<%=resourceURL%>',
			method : 'GET',
			timeout : 600000,
			params : {
				callType : "getTaxonIds",
				taxonId : '<%=cId%>'
			},
			success : function(rs) {
				idList = Ext.JSON.decode(rs.responseText);
			}
		});
		
	}
	
	var idCounter = idList.split(",");
	if (Ext.getCmp("tb").getValue() == "") {
		if (idCounter.length > 500) {
			Ext.Msg.alert('More than 500 genomes selected!', 'Current resources can not handle more the 500 genomes with empty keyword.');
		}
		else {
			submitToFigFamSorter(idList);
		}
	}
	else {
		submitToFigFamSorter(idList);
	}
}

function submitToFigFamSorter(idList) {
	
	var value = Ext.getCmp("tb").getValue().trim().toLowerCase();
	value = value.replace(/,/g,"~").replace(/\n/g,"~");
	value = value.split("~");
	
	for(var i=0; i<value.length; i++){
		value[i] = value[i].trim();
	}
	
	/*for (var i=0; i<keyword.length; i++) {
		keyword[i] = "\""+keyword[i]+"\"";
	}*/
	Ext.Ajax.request({
		url: '<%=resourceURL%>',
		method: 'POST',
		timeout: 600000,
		params: {callType: "toSorter", genomeIds: idList, keyword:EncodeKeyword(value.join(" OR "))},
		success: function(rs) {
			document.location.href = "FIGfamSorter?dm=result&pk=" + rs.responseText + "#gs_0=0";
		}
	});
}
//]]>
</script>
