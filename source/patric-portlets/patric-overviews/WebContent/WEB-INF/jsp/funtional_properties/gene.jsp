<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%
ResultType geneInfo = (ResultType) request.getAttribute("gene");
%>

<h4>Gene Properties</h4>
<table class="basic far2x">
<tbody>
	<tr>
		<th width="30%">Gene Symbol</th>
		<td><%=geneInfo.get("gene") %>&nbsp;</td>
	</tr>
</tbody>
</table>
