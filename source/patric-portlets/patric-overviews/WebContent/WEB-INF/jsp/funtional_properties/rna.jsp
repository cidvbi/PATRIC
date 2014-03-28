<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
ResultType summary = (ResultType) request.getAttribute("rna");
%>

<h4>RNA Properties</h4>
<table class="basic stripe far2x">
<tbody>
	<% if (summary.containsKey("gene") && !summary.get("gene").equals("")) { %>
	<tr>
		<th scope="row">Gene</th>
		<td><%=summary.get("gene")%></td>
	</tr>
	<% } %>
	<% if (summary.containsKey("label") && !summary.get("label").equals("")) { %>
	<tr>
		<th scope="row">Label</th>
		<td><%=summary.get("label")%></td>
	</tr>
	<% } %>
	<tr>
		<th scope="row">Anticodon</th>
		<td><%=summary.get("anticodon")%>&nbsp;</td>
	</tr>
	<tr>
		<th scope="row" width="30%">Product</th>
		<td><%=summary.get("product")%>&nbsp;</td>
	</tr>
	<% if (summary.containsKey("comment") && !summary.get("comment").equals("")) { %>
	<tr>
		<th scope="row">Comment</th>
		<td><%=summary.get("comment")%></td>
	</tr>
	<% } %>
	<% if (summary.containsKey("structure") && !summary.get("structure").equals("")) { %>
	<tr>
		<th scope="row">Structure</th>
		<td><span class="fixed-width-font"><%=summary.get("structure")%></span></td>
	</tr>
	<% } %>
</tbody>
</table>
