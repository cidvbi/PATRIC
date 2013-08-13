<%@ page session="true" %>
<%
String hostName=request.getServerName();
String _grIdxString = request.getParameter("group_id");
String _grTypeString = request.getParameter("group_type");

if (_grIdxString != null) {
%>
<div id="ExplorerInfo" class="table-container">
<table style="width:100%"><tr><td>
	The Group Explorer allows you to compare and contrast selected groups from within
	 your Workspace using a Venn diagram-based interactive visualization.  
	 To learn more see
	<a href="http://enews.patricbrc.org/group-explorer-faqs/" target="_blank">Group Explorer FAQs</a>. 
</td></tr></table>
</div>
<div style="height:600px">
	<object type="application/x-java-applet" height="95%" width="100%">
		<param name="code" value="edu.vt.vbi.ci.app.vennCompare.VennCompareApp.class" />
		<param name="archive" value="/patric-common/applet/VennCompareApp.jar" />
		<param name="data_url" value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=items&groupIds=<%=_grIdxString%>">
		<param name="groups_url" value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=groups&groupIds=<%=_grIdxString%>">
		<param name="list_url" value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=group_list">
		<param name="create_group_url" value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=create&group_type=<%=_grTypeString%>">
		Applet failed to run. No Java plug-in was found
	</object>
<%-- 
	<applet archive="/patric-common/applet/VennCompareApp.jar"
	code="edu.vt.vbi.ci.app.vennCompare.VennCompareApp.class" 
	width="100%" height="95%" MAYSCRIPT>
		<param name="data_url"		value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=items&groupIds=<%=_grIdxString%>">
		<param name="groups_url"	value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=groups&groupIds=<%=_grIdxString%>">
		<param name="list_url"		value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=GSESupport&action=group_list">
		<param name="create_group_url" value="http://<%=hostName %>/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=create&group_type=<%=_grTypeString%>">
	<!--	<param name="debug" value="true">  -->
	</applet>
--%>
</div>
<% } else { %>
<div></div>
<% } %>