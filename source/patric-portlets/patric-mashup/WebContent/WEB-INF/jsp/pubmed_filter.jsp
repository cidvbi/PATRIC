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


	if (cType.equals("taxon")) {
		
	} else if (cType.equals("genome")) {
		if (qScope==null) {
			qScope = "g";
		}
		ResultType names = dbh_shared.getNamesFromGenomeInfoId(cId);
		genome_name = names.get("genome_name");
		
	} else if (cType.equals("feature")) {
		if (qScope==null) {
			qScope = "f";
		}
		SolrInterface solr = new SolrInterface();
		JSONObject names = solr.getFeature(cId);
		genome_name = names.get("genome_name").toString();
		feature_name = names.get("product").toString();
	}
%>
<div class="table-container" id="filter" style="width:160px;float:left;line-height:1.7em;margin:0px">
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
	<a <%=(qDate.equals("f"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%>&amp;time=f<%=kwUrl%>" style="padding-left:15px">Coming Soon</a><br/>
	<a <%=(qDate.equals("w"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%>&amp;time=w<%=kwUrl%>" style="padding-left:15px">Past Week</a><br/>
	<a <%=(qDate.equals("m"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%>&amp;time=m<%=kwUrl%>" style="padding-left:15px">Past Month</a><br/>
	<a <%=(qDate.equals("y"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%>&amp;time=y<%=kwUrl%>" style="padding-left:15px">Past Year</a><br/>
	<a <%=(qDate.equals("")||qDate.equals("a"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%>&amp;time=a<%=kwUrl%>" style="padding-left:15px">All</a><br/>
<br/>

<hr/>
<h3>By Keyword:</h3>
<%
	String k = "";
	Enumeration<String> e = hashKeyword.keys();
	while (e.hasMoreElements()) {
       	k = e.nextElement();
	%>
	<a <%=(qKeyword.equals(k))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%><%=dateUrl%>&amp;kw=<%=k%>" style="padding-left:15px"><%=k %></a><br/>
<%	} %>
	<a <%=(qKeyword.equals("")||qKeyword.equals("none"))?"class=\"curSel\"":"" %> href="?<%=contextUrl%><%=scopeUrl%><%=dateUrl%>&amp;kw=none" style="padding-left:15px">All</a><br/>
</div>