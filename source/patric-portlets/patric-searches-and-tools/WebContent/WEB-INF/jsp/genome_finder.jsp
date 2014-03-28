<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.*" 
%><%
DBSummary conn_summary = new DBSummary();
DBShared conn_shared = new DBShared();
HashMap<String, String> key = new HashMap<String,String>();
String ncbi_taxon_id = null;
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
if (cType!=null && cId!=null) {
	if (cType.equals("taxon") && !cId.equals("")) {
		ncbi_taxon_id = cId;
		key.put("ncbi_taxon_id", ncbi_taxon_id);
	}else if (cType.equals("taxon")){
		ncbi_taxon_id = "2";
		key.put("ncbi_taxon_id", ncbi_taxon_id);
	}
	if (cType.equals("genome") && !cId.equals("")) {
		ResultType names = conn_shared.getNamesFromGenomeInfoId(cId);
		ncbi_taxon_id = names.get("ncbi_taxon_id");
		key.put("genome_info_id", cId);
	}
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

	<p>The Genome Finder allows you to search for all PATRIC genomes based on genome names and available metadata.
		To learn more, see <a href="http://enews.patricbrc.org/genome-finder-faqs/" target="_blank">Genome Finder FAQs</a>.
	</p>

	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" /> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px">
	
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" /> Enter keyword</h3><br />

		<form action="#" onsubmit="return false;">

		<select class="left" id="search_on" name="search_on" size="1" onchange="Combo_Change()" style="width:150px;"></select>
		<div class="right far2x">
			<textarea id="keyword" name="keyword" rows="5" cols="30"><%=(key!=null && key.containsKey("keyword") && !key.get("keyword").equalsIgnoreCase(""))?key.get("keyword"):""%></textarea>
			<br/>
			<input class="button" type="submit" value="Search" onclick="searchbykeyword('<%=cId%>', '<%=cType %>')" />
		</div>
		<div class="clear"></div>

		</form>
		
		<div class="callout" id='expander'></div>
	</div>
	<div class="clear"></div>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/search_common.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
var tabs = "";
var name = "Genome";
var url = "/portal/portal/patric/GenomeFinder/GenomeFinderWindow?action=b&cacheability=PAGE";
var loggedIn = <%=loggedIn%>;

Ext.onReady(function(){

	tabs = Ext.create('VBI.GenomeSelector.Panel', {
		renderTo: 'GenomeSelector',
		width: 480,
		height: 550,
		border:false,
		parentTaxon: <%=(ncbi_taxon_id==null)?"2":ncbi_taxon_id %>,
		organismName:'<%=name%>'
	});	
	
	loadComboFacets();
	updateFields();	
	Combo_Change();
});
//]]>
</script>