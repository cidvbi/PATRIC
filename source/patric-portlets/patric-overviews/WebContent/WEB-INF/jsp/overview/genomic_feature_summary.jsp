<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSummary" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String _viewOption = request.getParameter("view");

String context_link = "";

HashMap<String,String> key = new HashMap<String, String>();

if (cType!=null && cType.equals("genome") && !cId.equals("")) { 
	key.put("genome_info_id",cId);
	context_link = "cType=genome&amp;cId="+cId;
}
else if (cType!=null && cType.equals("taxon") && !cId.equals("")) { 
	key.put("ncbi_taxon_id",cId);
	context_link = "cType=taxon&amp;cId="+cId;
}

	key.put("view",_viewOption);

if (key.size()==0 || context_link.equals("")) {
	// no context param
	%>Please check parameters.<%
} else {
	ArrayList<ResultType> items = new DBSummary().getNAFeatureSummary(key);
	String name, patric, brc, refseq, identicals = null;
	ResultType item = null;
	
	%>
	<table class="basic stripe far2x">
	<thead>
		<tr>
			<th width="40%"></th>
			<th scope="col" width="20%">PATRIC</th>
			<th scope="col" width="20%">Legacy BRC</th>
			<th scope="col" width="20%">RefSeq</th>
		</tr>
	</thead>
	<tbody>
	<%
		String hash_key_others = "&amp;pS=20&amp;aP=1&amp;dir=ASC&amp;sort=genome_name,accession,start_max&amp;sS=All&amp;fT=";
		boolean alt = false;
		for ( Iterator<ResultType> iter = items.iterator(); iter.hasNext(); ) 
		{
			item = iter.next();
			name = item.get("name");
			if (name.equalsIgnoreCase("gene") == false) {
				
				alt = !alt;
				
				if (!item.get("patric").equals("")) {
					patric = "<a href=\"FeatureTable?"+context_link+"&amp;featuretype="+name+"&amp;annotation=PATRIC&amp;filtertype=\">"+item.get("patric")+"</a>";
				} else {
					patric = "0";
				}
				if (!item.get("brc").equals("")) {
					brc = "<a href=\"FeatureTable?"+context_link+"&amp;featuretype="+name+"&amp;annotation=BRC&amp;filtertype=\">"+item.get("brc")+"</a>";
				} else {
					brc = "0";
				}
				if (!item.get("refseq").equals("")) {
					refseq = "<a href=\"FeatureTable?"+context_link+"&amp;featuretype="+name+"&amp;annotation=RefSeq&amp;filtertype=\">"+item.get("refseq")+"</a>";
				} else {
					refseq = "0";
				}
		%>
			<tr <%=(alt==true)?"class=\"alt\"":"" %>>
				<th scope="row"><%=item.get("name") %></th>
				<td class="right-align-text"><%=patric %></td>
				<td class="right-align-text"><%=brc %></td>
				<td class="right-align-text last"><%=refseq %></td>
			</tr>
		<%
			}
		}
	%>
		<tr>
			<td class="no-underline-links" colspan="4"><%
				if (_viewOption.equals("full")) {
					%><a class="arrow-slate-e-up" href="javascript:void(0)" onClick="showShortList()">View less feature types</a><%
				} else {
					%><a class="arrow-slate-e-down" href="javascript:void(0)" onClick="showFullList()">View more feature types</a><%
				}
			%></td>
		</tr>
	</tbody>
	</table>
<% } %>