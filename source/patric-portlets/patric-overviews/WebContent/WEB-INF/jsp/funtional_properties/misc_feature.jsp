<%@ page session="true" %>
<%
String comment = (String) request.getAttribute("comment");
if (comment != null) {
%>
<h4>Note</h4>
<table class="basic far2x">
<tbody>
	<tr>
		<td><%=comment %>&nbsp;</td>
	</tr>
</tbody>
</table>
<% } else { %>
 No information is available.
<% } %>