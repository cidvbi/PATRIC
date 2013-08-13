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
// TODO: if organismId or genomeId is given, set key attribute to that it is selected.
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

	<div id="intro" class="searchtool-intro">
		<p>Feature Finder allows you to locate specific features(s) based on taxonomy (e.g., genus or species), feature type (e.g., CDS, rRNA, etc.), 
		keyword, sequence status, and/or annotation type.
		Select organism(s) and enter a keyword to search for features by Gene Name, Locus Tag, or Protein Function. 
		Refine search by specifying feature type, sequence status, and/or annotations.</p>
	</div>

	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" /> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px">
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" />
			Enter keyword</h3><br />
		<form action="#" onsubmit="return false;">
		<table class="querytable">
		<tr>
			<td><label for="feature_type">Feature Type:</label></td>
			<td><select id="feature_type" name="feature_type" size="1">
			<option value="">ALL</option>
	<%
			Iterator<String> itr = conn_summary.getListOfFeatureTypes(key).iterator();
			String fType = null;
			String _selected = "";
			
			while (itr.hasNext()) {
				fType = itr.next();
				if (fType.equals("CDS")) {
					_selected = "selected=\"selected\"";
				} else {
					_selected = "";
				}
				
				if (fType.equals("gene") == false) {
		%>
				<option value="<%=fType%>" <%=_selected%>><%=fType%></option>
		<%		}
			} %>
					</select>
				</td>
			</tr>
			<tr>
				<td><label for="keyword">Keyword:</label></td>
				<td><input type="text" id="keyword" name="keyword" size="32" value="" />
					<br /><span class="hint">Example: DNA polymerase</span> 
					<br /><span class="hint">Example: dnaN</span> 
					<br /><span class="hint">Example: VBIBruSui107850_0001</span>
				</td>
			</tr>
		<tr>
			<td><label for="annotation">Annotation:</label></td>
			<td><select id="annotation" name="annotation" size="1">
				<option value="">ALL</option>
			<%
			ArrayList<String> dbList = new ArrayList<String>();
			dbList.add("PATRIC");
			dbList.add("BRC");
			dbList.add("RefSeq");
			
			itr = dbList.iterator();
			String anno = null;
			_selected = "";
			
			while (itr.hasNext()) {
				anno = (String) itr.next(); 
				if (anno.equals("PATRIC"))
					_selected = "selected=\"selected\"";
				else
					_selected = "";
			%>
				<option value="<%=anno%>" <%=_selected%>><%=anno%></option>
		<%	} %>
				</select>
			</td>
		</tr>
		
		<tr>
			<td class="formaction" colspan="2"><input class="button rightarrow"  type="submit" value="Search" onclick="searchbykeyword('<%=cId%>', '<%=cType %>')" /></td>
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
var name = "Feature";
var url = "/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE";
var loggedIn = <%=loggedIn%>;

Ext.onReady(function(){

	updateFields();
	
	tabs = Ext.create('VBI.GenomeSelector.Panel', {
		renderTo: 'GenomeSelector',
		width: 480,
		height: 550,
		border:false,
		parentTaxon: <%=(ncbi_taxon_id==null)?"2":ncbi_taxon_id %>,
		organismName:'<%=name%>'
	});
});
//]]>
</script>