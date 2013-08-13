<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="java.util.*" %>
<%
DBShared conn_shared = new DBShared();
HashMap<String, String> key = new HashMap<String,String>();

String ncbi_taxon_id = null;
String genome_info_id = null;
String feature_info_id = null;
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

	<div id="intro" class="searchtool-intro">
		<p>The Enzyme Commission (EC) number is a numerical classification scheme for enzymes based on the chemical reactions they catalyze.  The EC Search tool enables researchers to locate proteins annotated with specified EC criteria.  For further explanation, please see <a href="http://enews.patricbrc.org/ec-search-faqs/" target="_blank">EC Search FAQs</a>.</p>
	</div>

	<input type="hidden" id="taxonId" name="taxonId" value="<%=(ncbi_taxon_id!=null)?ncbi_taxon_id:"" %>" />
	
	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" /> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px">
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" />
		Enter keyword</h3><br />
		<form action="#" onsubmit="return false;">
		<table>
		<tr>
			<td>
				<select id="search_on" name="search_on" size="1">
				 	<option value="Keyword" >Keyword</option>
					<option value="ec_number">EC Number</option>
					<option value="ec_name" >EC Name</option>
				</select>
				<br /><span class="small bold">Examples</span>
				<br /><span class="small">Keyword:</span>
				<br /><span class="small">EC Number:</span>
				<br /><span class="small">EC Name:</span>
			</td>
			<td>
				<input type="text" id="keyword" name="keyword" size="32" title="keyword or EC Number or EC Name" />
				<br />
				<br /><span class="small">ligase</span>
				<br /><span class="small">1.1.1.1</span>
				<br /><span class="small">Alcohol dehydrogenase</span>
			</td>
		</tr>
		<tr>
			<td><label for="annotation">Annotation : </label></td>
			<td>
				<select id="annotation" name="annotation" size="1">
					<option value="" selected="selected">ALL</option>
					<option value="PATRIC">PATRIC</option>
					<option value="Legacy BRC">Legacy BRC</option>
					<option value="RefSeq">RefSeq</option>
				</select>
			</td>
		</tr>
		<tr>
			<td class="right" colspan="2"><input class="button"  type="submit" value="Search" onclick="searchbykeyword('<%=cId%>', '<%=cType %>')" /></td>
		</tr>
		</table>
		</form>
	</div>
	<div class="clear"></div>
<script type="text/javascript" src="/patric-searches-and-tools/js/solrKeyword.js"></script>
<script type="text/javascript" src="/patric-searches-and-tools/js/search_common.js"></script>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
var tabs = "";
var name = "EC";
var url = "/portal/portal/patric/ECSearch/ECSearchWindow?action=b&cacheability=PAGE";
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
	updateFields();
});
//]]>
</script>