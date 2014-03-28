<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.common.PubMedHelper" %>
<%@ page import="edu.vt.vbi.patric.dao.DBShared" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.common.SolrInterface" %>
<%@ page import="org.json.simple.JSONObject" %>
<%
	
	String scopeUrl = "";
	String qScope = request.getParameter("scope");
	if (qScope!=null) {
		scopeUrl = "&amp;scope="+qScope;
	}
	
	String dateUrl = "";
	String qDate = request.getParameter("time");
	if (qDate!=null) {
		dateUrl = "&amp;time="+qDate;
	} else {
		qDate = "a";
	}
	
	String kwUrl = "";
	String qKeyword = request.getParameter("keyword");
	//System.out.print("keyword -- "+qKeyword);
	
	if (qKeyword!=null) {
		kwUrl = "&amp;kw="+qKeyword;
	} else {
		qKeyword = "none";
	}
	
	String cType = request.getParameter("context_type");
	String cId = request.getParameter("context_id");	
	String contextUrl = "cType="+cType+"&amp;cId="+cId;
	
	Hashtable <String, Vector<String>> hashKeyword = PubMedHelper.getKeywordHash();
	DBShared dbh_shared = new DBShared();
	String genome_name = "";
	String feature_name = "";
	String kleio_species = "";
			
	if (cType.equals("taxon")) {
		kleio_species = dbh_shared.getOrganismName(cId);
	} else if (cType.equals("genome")) {
		if (qScope==null) {
			qScope = "g";
		}
		ResultType names = dbh_shared.getNamesFromGenomeInfoId(cId);
		genome_name = names.get("genome_name");
		kleio_species = genome_name;
		
	} else if (cType.equals("feature")) {
		if (qScope==null) {
			qScope = "f";
		}
		SolrInterface solr = new SolrInterface();
		JSONObject names = solr.getFeature(cId);
		if (names.isEmpty() == false) {
			if (names.containsKey("genome_name")) {
				genome_name = names.get("genome_name").toString();
			}
			if (names.containsKey("product")) {
				feature_name = names.get("product").toString();
			} else if (names.containsKey("locus_tag")) {
				feature_name = names.get("locus_tag").toString();
			}
		}
		if (feature_name.equals("")) {
			kleio_species = genome_name;
		} else {
			kleio_species = feature_name;
		}
	}

%>
<div class="table-container" id="filter">
<h2>Filter Publications</h2>
<% if (cType.equals("feature")) { %>
<hr/>
<h3>By Scope:</h3>
	<a <%=(qScope.equals("g"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%>&amp;scope=g<%=dateUrl%><%=kwUrl%>" style="padding-left:15px"><%=genome_name %></a><br/>
	<a <%=(qScope.equals("f"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%>&amp;scope=f<%=dateUrl%><%=kwUrl%>" style="padding-left:15px"><%=feature_name %></a><br/>
<br/>
<% } %>

<hr/>
<h3>By Date:</h3>
	<a id="date_f" <%=(qDate.equals("f"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('f', '');" style="padding-left:15px">Coming Soon</a><br/>
	<a id="date_w" <%=(qDate.equals("w"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('w', '');" style="padding-left:15px">Past Week</a><br/>
	<a id="date_m" <%=(qDate.equals("m"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('m', '');" style="padding-left:15px">Past Month</a><br/>
	<a id="date_y" <%=(qDate.equals("y"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('y', '');" style="padding-left:15px">Past Year</a><br/>
	<a id="date_a" <%=(qDate.equals("")||qDate.equals("a"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('a', '');" style="padding-left:15px">All</a><br/>
<br/>

<hr/>
<h3>By Keyword:</h3>
<%
	String k = "";
	Enumeration<String> e = hashKeyword.keys();
	while (e.hasMoreElements()) {
       	k = e.nextElement();
	%>
	<a id="keyword_<%=k%>" <%=(qKeyword.equals(k))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('', '<%=k %>');" style="padding-left:15px"><%=k %></a><br/>
<%	} %>
	<a id="keyword_none" <%=(qKeyword.equals("")||qKeyword.equals("none"))?"class=\"curSel\"":"" %> href="javascript:loadPubMed('', 'none');" style="padding-left:15px">All</a><br/>
</div>

<div id="SearchSummary">
<p>PATRIC provides enhanced literature search and text mining techniques to identify 
	genes, proteins, diseases, drugs, organisms, and other entities of interest. 
	Text mining of annotated UK Medline abstracts is powered by NaCTeM.  
	To learn more, please see 
	<a href="http://enews.patricbrc.org/literature-faqs/" target="_blank">Literature FAQs</a>.
</p></div>

<div id="grid_result_summary"></div>

<div id="sample_layout"></div>
<div id="PubMed"></div>
<div id="pubmed_result"></div>
 
<form id="pubmed_search" action="#">
<input type="hidden" id="context_type" name="genome_info_id" value="<%=cType %>" />
<input type="hidden" id="context_id" name="feature_info_id" value="<%=cId %>" />
<input type="hidden" id="scope" name="scope" value="<%=qScope %>" />
<input type="hidden" id="date" name="date" value="<%=qDate%>" />
<input type="hidden" id="keyword" name="keyword" value="<%=qKeyword %>" />
</form>
<script type="text/javascript">
//<![CDATA[
var grid;

Ext.define('PubMed', {
	extend: 'Ext.data.Model',
	fields: [
		{name:'abbrAuthorList',	type:'int'},
		{name:'FullJournalName',	type:'string'},
		{name:'PubDate',	type:'date'},
		{name:'pubmed_id',	type:'string'},
		{name:'fullAuthorList',	type:'string'},
		{name:'Title',	type:'string'}
	]
});

Ext.onReady(function()
{
	Ext.create('Ext.panel.Panel', {
		id: 'pubmed-overview-panel',
		border: true,
		renderTo: 'PubMed',
		layout: 'border',
		autoScroll:true,
		height:815,
		header:false,
		items: [{
			region: 'center',
			title: 'Publications',
			border:false,
			contentEl: 'pubmed_result',
			split:true
		},{
			region: 'west',
			title: 'Filter Publications',
			border:false,
			contentEl: 'filter',
			width: 180,
			collapsed:false,
			collapsible:true,
			split:true
		}],
		listeners:{
			'collapse': function(){
				var w = parseInt(pubmed_orig_center_width)+parseInt(pubmed_orig_left_width-27);
				grid.setWidth(w);
			},
			'expand': function(){
				var width = Ext.get("pubmed-overview-panel").dom.childNodes[0].childNodes[0].childNodes[0].style.width.split("px")[0];
				var w = 0;
				if(width-pubmed_orig_left_width > 0){
					w = pubmed_orig_center_width-(width-pubmed_orig_left_width);
					grid.setWidth(w);
				}else{
					w = parseInt(pubmed_orig_center_width)+parseInt(pubmed_orig_left_width-width);
					grid.setWidth(w);
				}
			},
			'resize': function(){
				if(grid != null && tree != null){
					tree_grid.doLayout();
					var width = Ext.get("pubmed-overview-panel").dom.childNodes[0].childNodes[0].childNodes[0].style.width.split("px")[0];
					var w = 0;
					if(width-pubmed_orig_left_width > 0) {
						w = pubmed_orig_center_width-(width-pubmed_orig_left_width);
					} else {
						w = parseInt(pubmed_orig_center_width)+parseInt(pubmed_orig_left_width-width);
					}
					grid.setWidth(w);
				}
			}
		}
	});

	var pubmed_orig_left_width = Ext.get("pubmed-overview-panel").dom.childNodes[0].childNodes[1].style.width.split("px")[0];
	var pubmed_orig_center_width = Ext.get("pubmed-overview-panel").dom.childNodes[0].childNodes[0].style.width.split("px")[0];

	Ext.create('Ext.data.Store', {
		storeId: 'ds',
		model: 'PubMed',
		proxy: {
			type: 'ajax',
			url:'<portlet:resourceURL />',
			timeout: 600000, //10*60*1000
			reader: {
				type:'json',
				root:'results',
				totalProperty:'total'
			}
		},
		autoLoad: false,
		pageSize: 10,
		listeners:{
			datachanged: function(){
					Ext.getDom('grid_result_summary').innerHTML = "<b>"+Ext.getStore('ds').getTotalCount()+" publications found</b>";
			}
		}
	});

	if (Ext.get("tabs_literature")!=null) {
		Ext.get("tabs_literature").addCls("sel");
	}

	createLoadTables();

});

function loadPubMed(date, keyword){
	if(date != ""){
		Ext.get("date_"+Ext.getDom("date").value).removeCls('curSel')
		Ext.get("date_"+date).addCls('curSel')
		Ext.getDom("date").value = date;
	}
	if(keyword != ""){
		Ext.get("keyword_"+Ext.getDom("keyword").value).removeCls('curSel')
		Ext.get("keyword_"+keyword).addCls('curSel')
		Ext.getDom("keyword").value = keyword;
	}	
	createLoadTables();
}

function createLoadTables(){

	var scm =  [{header: "Publication", sortable:false, menuDisabled:true, flex:1, id:'x-grid-cell-inner', renderer:renderPubMed, width:825}]

	if(grid == null){
		grid = Ext.create('Ext.grid.Panel', {
			store: 'ds', // use the datasource
			viewConfig: {
				headersDisabled: true,
				forceFit:true
			},
			bbar: Ext.create('Ext.PagingToolbar', {
				pageSize: 10,
				store:'ds',
				displayInfo: true,
				displayMsg: 'Displaying features {0} - {1} of {2}',
				emptyMsg: "No features to display"
			}),
			columns: scm,
			border: false,
			renderTo: "pubmed_result",
			autoScroll: true,
			height: 790
		});
	}
		
	if(Ext.getStore('ds').proxy.extraParams.date != Ext.getDom("date").value 
		|| Ext.getStore('ds').proxy.extraParams.keyword != Ext.getDom("keyword").value){

		Ext.getStore('ds').proxy.extraParams = {
			cType:	Ext.getDom("context_type").value,
			cId:	Ext.getDom("context_id").value,
			scope:	Ext.getDom("scope").value,
			date:	Ext.getDom("date").value,
			keyword:Ext.getDom("keyword").value
		};

		Ext.getStore('ds').load();
		grid.reconfigure('ds');
	}else{
		Ext.getDom('grid_result_summary').innerHTML = Ext.getStore('ds').getTotalCount()+" publications found";
	}
		
}

function renderPubMed(value, p, record) {

	var title = record.data.Title;
	var authors = record.data.fullAuthorList;
	var journal_name = record.data.FullJournalName;
	var pub_date = record.data.PubDate;
	var pmid = record.data.pubmed_id;

	var strPub = Ext.String.format("<b>{0}</b><br/>{1}<br/>{2} ({3})", title, authors, journal_name, pub_date);
	var strPubmedLink;
	if (pmid != null) {
		strPubmedLink = Ext.String.format(', PubMed: <a href="http://view.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', pmid);
	}
	else {
		strPubmedLink = ", PubMed: Not Available";
	}	
	var strAbstract;
	if (pmid != null) {
		strAbstract = Ext.String.format('<br /><img src="/patric/images/spacer.gif" onclick="setPubmedAbstractLayer(\'Abstract_{0}\',this,\'{0}\')" class="toggleButton toggleButtonHide" /> Abstract <div id="Abstract_{0}" style="display:none;padding:10px;white-space:normal"></div>',
			pmid);
	} else {
		strAbstract = "";
	}
	return strPub+strPubmedLink+strAbstract;
}
//]]>
</script>