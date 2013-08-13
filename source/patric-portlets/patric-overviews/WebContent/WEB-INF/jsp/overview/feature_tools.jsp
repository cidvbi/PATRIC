<%@ page import="org.json.simple.JSONObject"
%><%@ page import="org.json.simple.JSONArray"
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface"
%><%@ page import="edu.vt.vbi.patric.dao.DBShared"
%><%
String fId = request.getParameter("context_id");
String dispRefseqLocusTag = null, dispPSeedId = null, dispSequenceID = null, dispNTSequence = null, dispProteinSequence = null;
JSONObject feature = new JSONObject();

if (fId != null) {
	// getting PATRIC feature info from Solr 
	SolrInterface solr = new SolrInterface();
	feature = solr.getPATRICFeature(fId);	
}

if (feature.isEmpty() == false) {
	
	if (feature.get("annotation").equals("PATRIC")) {
		
		dispRefseqLocusTag	= (String) feature.get("refseq_locus_tag");
		dispPSeedId 		= (String) feature.get("pseed_id");
		if (feature.containsKey("locus_tag") && feature.get("locus_tag")!=null) {
			dispSequenceID 		= feature.get("locus_tag").toString();
		}
		if (feature.get("refseq_locus_tag")!=null) {
			dispSequenceID += "|" + feature.get("refseq_locus_tag");
		}
		if (feature.get("product")!=null) {
			dispSequenceID += " " +  feature.get("product");
		}

	} else if (feature.get("annotation").equals("RefSeq")) {
		
		dispRefseqLocusTag	= (String) feature.get("locus_tag");
		dispSequenceID 		= feature.get("locus_tag") + " " +  feature.get("product");
	}
	
	// getting Protein Sequence
	DBShared conn_shared = new DBShared();
	dispProteinSequence = conn_shared.getFastaAASequence(fId);
	//dispNTSequence = conn_shared.getFastaNTSequence(fId);
	%>
	<div class="far2x">
		<input type="button" class="button close2x" title="Add Feature to Workspace" value="Add <%=feature.get("annotation") %> Feature to Workspace" onclick="saveFeature()" />

		<div class="close2x"><a href="/patric-common/jsp/fasta_download_handler.jsp?fastaaction=display&amp;fastatype=dna&amp;fastascope=Selected&amp;fids=<%=fId %>" 
				onclick="window.open(this.href,'mywin','width=920,height=500,resizable,scrollbars');return false" 
				target="_blank" style="text-decoration:none">
				View NT Sequence</a>
		</div>
		<div class="close2x"><a href="/patric-common/jsp/fasta_download_handler.jsp?fastaaction=display&amp;fastatype=protein&amp;fastascope=Selected&amp;fids=<%=fId %>" 
				onclick="window.open(this.href,'mywin','width=920,height=500,resizable,scrollbars');return false" 
				target="_blank" style="text-decoration:none">
				View AA Sequence</a>
		</div>
	</div>

	<% if (dispRefseqLocusTag != null || dispPSeedId != null || (dispSequenceID !=null && dispProteinSequence!=null)) { %>
	<h3 class="section-title normal-case close2x">
		<span class="wrap">External Tools</span>
	</h3>
	<div class="far2x">
		<% if (dispPSeedId != null) { %>
		<div class="close2x">
			<a href="http://pubseed.theseed.org/?page=Annotation&amp;feature=<%=dispPSeedId %>" target="_blank" style="text-decoration:none">The SEED Viewer</a>
		</div>
		<% } %>
		<% if (dispSequenceID != null && dispProteinSequence != null) { %>
		<div class="close2x">
			<a href="http://www.ncbi.nlm.nih.gov/Structure/cdd/wrpsb.cgi?SEQUENCE=%3E<%=dispSequenceID.replace(" ","%20") %>%0A<%=dispProteinSequence %>&amp;FULL" target="_blank" style="text-decoration:none">NCBI CDD Search</a>
		</div>
		<% } %>
		<% if (dispRefseqLocusTag != null) { %>
		<div class="close2x">
			<a href="http://string.embl.de/newstring_cgi/show_network_section.pl?identifier=<%=dispRefseqLocusTag %>" target="_blank" style="text-decoration:none">STRING: Protein-Protein Interactions</a>
		</div>
		<div class="close2x">
			<a href="http://stitch.embl.de/cgi/show_network_section.pl?identifier=<%=dispRefseqLocusTag %>" target="_blank" style="text-decoration:none">STITCH: Chemical-Protein Interactions</a>
		</div>
		<% } %>
		<% if (dispNTSequence != null) { %>
		<div class="close2x">
			<a href="#" onclick="runSixframeTranslation()" style="text-decoration:none">ExPASy Six-frame translation</a>
		</div>
		<% }  %>
	</div>
	<% } %>
	
	<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
	<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
	<script type="text/javascript">
	//<![CDATA[
	var $Page,
		ZeroClipboard = null,
		pageProperties = {cart:true};
		SetPageProperties(pageProperties);
		
	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();

	function saveFeature(){
		addSelectedItems("Feature");
	}
	btnGroupPopupSave.on('click', function(){
		if(saveToGroup(<%=fId%>, "Feature")){
			popup.hide();
		}
	});
	function runSixframeTranslation() {
		document.getElementById("sixframeTranslationForm").submit();
	}
	//]]>
	</script>
	
	<% if (dispNTSequence != null) { %>
	<form id="sixframeTranslationForm" action="http://web.expasy.org/cgi-bin/translate/dna_aa" method="POST" target="_blank">
	<input type="hidden" id="pre_text" name="pre_text" value="<%=dispNTSequence%>" />
	<input type="hidden" id="output" name="output" value="" />
	<input type="hidden" id="code" name="code" value="Standard" />
	<input type="hidden" id="mandatory" name="mandatory" value="" />
	</form>
	<% } %>
	
	<%
} else {
	%>&nbsp;<%
}
%>