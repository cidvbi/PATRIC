<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.*" 
%><%
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

if(cId == null || cId.equals("")) {
	name = "Bacteria";
} else {
	if(cType.equals("taxon")) {
		name = conn_shared.getOrganismName(cId);
	} else if (cType.equals("genome")) {
		name = conn_shared.getGenomeName(cId);
	}
}

boolean loggedIn = false;
if(request.getUserPrincipal() == null){
	loggedIn = false;
} else {
	loggedIn = true;
}
%>
<style type="text/css" media="screen" scoped>
	.querytable label {
		display: block;
		padding-left: 15px;
		text-indent: -15px;
		font-weight: normal;
		text-align: left;
	}
	.querytable td {
		vertical-align: top;
	}
	.queryForm label {
		display: block;
		text-align: left;
	}
	input {
		padding: 0;
		margin:0;
		vertical-align: middle;
		horizontal-align:left;
		position: relative;
		top: -1px;
		*overflow: hidden;
	}
</style>

	<div id="intro" class="searchtool-intro">
		<p>To use PATRIC's download tool, select one or more genomes of interest, then one or more annotation sources, followed by the file types.
		  For further explanation, please see <a href="http://enews.patricbrc.org/download-data-faqs/download-data-tool-faqs/" target="_blank">Download Data Tool FAQs</a>.</p>
	</div>

	<div id="result-meta" class="search-results-form-wrapper" style="display:none">
		<a href="#" id="modify-search"><img src="/patric/images/btn_modify_search.gif" id="modify-search-btn" alt="Modify Search Criteria" /></a>
	</div>

	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" /> Select organism(s)</h3>
		<%=OrganismTreeBuilder.buildOrganismTreeListView() %>
	</div>
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:450px">
		<form id="searchForm" name="searchForm" action="#" method="post" onsubmit="return false;">
		<input type="hidden" id="cType" name="cType" value="" />
		<input type="hidden" id="cId" name="cId" value="" />
		<input type="hidden" id="genomeId" name="genomeId" value="" />
		<input type="hidden" id="taxonId" name="taxonId" value="<%=(ncbi_taxon_id!=null)?ncbi_taxon_id:"" %>" />
		<input type="hidden" id="finalfiletype" name="finalfiletype" value="" />
		<input type="hidden" id="finalalgorithm" name="finalalgorithm" value="" />

			<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" />
				Choose Annotation Source</h3>
				
			<div class="far"><label><input type="checkbox" name="algorithm" value=".PATRIC" checked="checked"/> PATRIC</label>
			<br/><label><input type="checkbox" name="algorithm" value=".BRC"/> Legacy BRC</label>
			<br/><label><input type="checkbox" name="algorithm" value=".RefSeq"/> RefSeq</label>
			</div>
			
			<h3><img src="/patric/images/number3.gif" alt="3" height="14" width="14" />
				Choose File Type</h3>
			
			<table class="querytable">
			<tr>
				<td>
					<label><input type="checkbox" name="filetype" value=".fna" id="fna" checked="checked"/> Genomic Sequences in FASTA (*.fna)</label>
					<label><input type="checkbox" name="filetype" value=".faa" id="faa"/> Protein Sequences in FASTA (*.faa)</label>
					<label><input type="checkbox" name="filetype" value=".gbf" id="gbf"/> All annotations in GenBank file format (*.gbf)</label>				
				</td>
				<td>
					<label><input type="checkbox" name="filetype" value=".ffn" id="ffn"/> DNA Sequences of Protein Coding Genes (*.ffn)</label>
					<label><input type="checkbox" name="filetype" value=".frn" id="frn"/> DNA Sequences of RNA Coding Genes (*.frn)</label>
				</td>
			</tr>
			<tr>
				<td>
					<label><input type="checkbox" name="filetype" value=".features.tab" id="features"/> All genomic features in tab-delimited format (*.features)</label>
					<label><input type="checkbox" name="filetype" value=".cds.tab" id="cds"/> Protein coding genes in tab-delimited format (*.cds)</label>
					<label><input type="checkbox" name="filetype" value=".rna.tab" id="rna"/> RNAs in tab-delimited format (*.rna)</label>		
					<label><input type="checkbox" name="filetype" value=".figfam" id="figfam"/> FIGfam assignments in tab-delimited format (*.figfam)</label>				
				</td>
				<td>
					<label><input type="checkbox" name="filetype" value=".go" id="go"/> GO function assignments in tab-delimited format (*.go)</label>
					<label><input type="checkbox" name="filetype" value=".ec" id="ec"/> EC assignments in tab-delimited format (*.ec)</label>
					<label><input type="checkbox" name="filetype" value=".path" id="path"/> Pathway assignments in tab-delimited format (*.path)</label>
				</td>
			</tr>
			</table>
			<p class="right-align-text small"><input type="submit" value="Download" onclick="download()" class="button" style="cursor:pointer" /></p>
		</form>
	</div>
	<div class="clear"></div>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript">
//<![CDATA[
var menu1=null;
var tabs= "";
var loggedIn = <%=loggedIn%>;
Ext.onReady(function() {
	tabs = Ext.create('VBI.GenomeSelector.Panel', {
		renderTo: 'GenomeSelector',
		width: 480,
		height: 550,
		border:false,
		parentTaxon: <%=(ncbi_taxon_id==null)?"2":ncbi_taxon_id %>,
		organismName:'<%=name%>'
	});
});

function getSelectedFileTypes(){
	var c_value = "";
	for (var i=0; i < document.searchForm.filetype.length; i++){
		if (document.searchForm.filetype[i].checked){
			c_value = c_value + document.searchForm.filetype[i].value + ",";
		}
	}
	return c_value;
}

function getSelectedAlgorithm(){
	var c_value = "";
	for (var i=0; i < document.searchForm.algorithm.length; i++){
		if (document.searchForm.algorithm[i].checked){
			c_value = c_value + document.searchForm.algorithm[i].value + ",";
		}
	}
	return c_value;
}

function download() {

	var genomes, size;

	Ext.getDom("searchForm").action = "/patric-searches-and-tools/jsp/filedownload.jsp";
	Ext.getDom("finalfiletype").value = getSelectedFileTypes(); 
	Ext.getDom("finalalgorithm").value = getSelectedAlgorithm();
	Ext.getDom("searchForm").target = "";

	if(!tabs.getSelectedInString()){
		Ext.getDom("genomeId").value = "";
		
		Ext.Ajax.request({
			url: "/patric-searches-and-tools/jsp/get_genome_count.jsp",
			method: 'GET',
			params: {taxonId:Ext.getDom("taxonId").value, data_source:getSelectedAlgorithm()},
			success: function(response, opts) {
				size = response.responseText;
				if(size > 100){
					Ext.MessageBox.alert(size+" genomes", 'Current resources can not handle more than 100 genomes..');
				}else{
					
					if(Ext.getDom("finalfiletype").value != "" && Ext.getDom("finalalgorithm").value != "") {
						Ext.getDom("searchForm").submit();
					} else {
						Ext.MessageBox.alert('Error', 'Please choose at least one Annotation source and one File format');
					}
				}
			}
		});
	} else {
		if("<%=cId%>" != "" && "<%=cId%>" != null && (tabs.getSelectedInString() == null || tabs.getSelectedInString() == "")){
			
			Ext.Ajax.request({
				url: "/patric-searches-and-tools/jsp/get_genome_count.jsp",
				method: 'GET',
				params: {taxonId:Ext.getDom("taxonId").value, data_source:getSelectedAlgorithm()},
				success: function(response, opts) {
					size = response.responseText;
					if(size > 100) {
						Ext.MessageBox.alert(size+" genomes", 'Current resources can not handle more than 100 genomes..');
					} else {
						
						if(Ext.getDom("finalfiletype").value != "" && Ext.getDom("finalalgorithm").value != ""){
							
							Ext.Ajax.request({
								url: "/patric-searches-and-tools/jsp/filedownload.jsp",
								method: 'GET',
								params: {genomeId:Ext.getDom("genomeId").value, taxonId:Ext.getDom("taxonId").value, finalfiletype:Ext.getDom("finalfiletype").value, finalalgorithm:Ext.getDom("finalalgorithm").value},
								success: function(response, opts) {
									alert(response.responseText);
								}
							});
						} else {
							Ext.MessageBox.alert('Error', 'Please choose at least one Annotation source and one File format');
						}
					}
				}
			});
		} else {
			genomes = tabs.getSelectedInString();
			size = genomes.split(",").length;
			if(size > 100) {
				Ext.MessageBox.alert(size+" genomes", 'Current resources can not handle more than 100 genomes..');
			} else {
				Ext.getDom("genomeId").value = genomes;
				if(Ext.getDom("finalfiletype").value != "" && Ext.getDom("finalalgorithm").value != "") {
					Ext.getDom("searchForm").submit();
				} else {
					Ext.MessageBox.alert('Error', 'Please choose at least one Annotation source and one File format');
				}
			}
		}
	}
}
//]]>
</script>