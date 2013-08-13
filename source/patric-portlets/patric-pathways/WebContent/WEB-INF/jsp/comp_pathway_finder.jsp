<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="java.util.*" %>
<%
DBShared conn_shared = new DBShared();

String taxonId = "";
String genomeId = "";
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");

if (cType!=null && cId != null && cType.equals("taxon") && !cId.equals("")) {
	taxonId = cId;
} else if (cType!=null && cId != null && cType.equals("genome") && !cId.equals("")) {
	genomeId = cId;
}

String name = "";

if(cId == null || cId.equals(""))
	name = "Bacteria";
else{
	if(cType.equals("taxon"))
		name = conn_shared.getOrganismName(cId);
	else if (cType.equals("genome"))
		name = conn_shared.getGenomeName(cId);	
}

boolean loggedIn = false;
if(request.getUserPrincipal() == null){
	loggedIn = false;
}else
	loggedIn = true;

%>
<div style="padding:5px">
<div id="intro" class="searchtool-intro">
	<p>Comparative Pathway Tool allows you to identify a set of pathways based on taxonomy, EC number, pathway ID, pathway name and/or specific annotation type.  For more information on Comparative Pathway Tool please see our <a href="http://enews.patricbrc.org/comparative-pathway-tool-faqs/" target="_blank">Comparative Pathway Tool FAQs</a>.  For an explanation of various annotation sources, please see <a href="http://enews.patricbrc.org/faqs/annotation-faqs/" target="_blank">Annotation FAQs.</a></p>
</div>

<div id="result-meta" class="search-results-form-wrapper" style="display:none">
	<a href="#" id="modify-search"><img src="/patric/images/btn_modify_search.gif" id="modify-search-btn" alt="Modify Search Criteria" /></a>
</div>

<div id="slideBlock">

	<input type="hidden" id="cType" name="cType" value="" />
	<input type="hidden" id="cId" name="cId" value="" />
	<input type="hidden" id="genomeId" name="genomeId" value="<%=genomeId%>" />
	<input type="hidden" id="taxonId" name="taxonId" value="<%=taxonId%>" />
		
	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14"/> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>

	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px">
		
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14"/>
		Enter keyword</h3><br />
		<table>
		<tr>
			<td>
				<select id="search_on" name="search_on" size="1" >
					<option value="Keyword">Keyword</option>
					<option value="Map_ID">Pathway ID</option>
					<option value="Ec_Number">EC Number</option>
				</select>
				<br /><span class="small bold">Examples</span>
				<br /><span class="small">Keyword:</span>
				<br /><span class="small">Pathway ID:</span>
				<br /><span class="small">EC Number:</span>
			</td> 
		<td>
			<input type="text" id="keyword" name="keyword" title="Keyword OR Pathway ID OR EC Number" value="" />
				<br />
				<br /><span class="small">Metabolism</span>
				<br /><span class="small">00010</span>
				<br /><span class="small">1.1.1.1</span>
			</td>
		</tr>
		<tr>
			<td><label>Annotation : </label></td>
			<td>
				<select id="algorithm" name="algorithm" size="1">
					<option value="ALL">ALL</option>
					<option value="PATRIC">PATRIC</option>
					<option value="BRC">Legacy BRC</option>
					<option value="RefSeq">RefSeq</option>
				</select>
			</td>
		</tr>
		<tr>
			<td class="formaction" colspan="2">
				<input class="button" type="submit" value="Search" onclick="searchbykeyword()" />
			</td>
		</tr>
		</table>
	</div>
	<div class="clear"></div>
</div>

</div>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">

//<![CDATA[
var tabs = "";
var loggedIn = <%=loggedIn%>;

Ext.onReady(function(){
	updateFields();
	
	tabs = Ext.create('VBI.GenomeSelector.Panel', {
		renderTo: 'GenomeSelector',
		width: 480,
		height: 550,
		border:false,
		parentTaxon: <%=(taxonId.equals(""))?"2":taxonId %>,
		organismName:'<%=name%>'
	});	
});

function updateFields(){

	var parts = window.location.href.split("#");
	if(parts[1] != null){
		var hash = parts[1].split("&");
		for(var i=0; i< hash.length; i++){
			if(Ext.getDom(hash[i].split("=")[0]) != null)
				Ext.getDom(hash[i].split("=")[0]).value = Ext.urlDecode("result="+hash[i].split("=")[1]).result;
		}
	}
}

var store, combo, combo2, readerx;
function searchbykeyword() {

	Ext.Ajax.request({
		url: '<portlet:resourceURL />',
		method: 'POST',
		params: {cType: "<%=cType%>"
			,cId: "<%=cId%>"
			,sraction: "save_params"
			,genomeId: tabs.getSelectedInString() || "<%=genomeId%>"
			,search_on: Ext.getDom("search_on").value
			,taxonId: Ext.getDom('taxonId').value
			,keyword: Ext.getDom("keyword").value
		},
		success: function(rs) {
										
			if(Ext.getDom("search_on").value == "Map_ID" || Ext.getDom("search_on").value == "Pathway ID")
				document.location.href="PathwayFinder?cType=<%=cType%>&cId=<%=cId%>&dm=result&map="+Ext.getDom("keyword").value+"&ec_number=&algorithm="+Ext.getDom('algorithm').value+"&pk="+rs.responseText;
			else if(Ext.getDom("search_on").value == "Ec_Number" || Ext.getDom("search_on").value == "EC Number")
				document.location.href="PathwayFinder?cType=<%=cType%>&cId=<%=cId%>&dm=result&map=&ec_number="+Ext.getDom("keyword").value+"&algorithm="+Ext.getDom('algorithm').value+"&pk="+rs.responseText;
			else	
				document.location.href="PathwayFinder?cType=<%=cType%>&cId=<%=cId%>&dm=result&map=&ec_number=&algorithm="+Ext.getDom('algorithm').value+"&pk="+rs.responseText;
		}
	});
}
//]]>
</script>
