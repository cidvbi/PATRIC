<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<portlet:defineObjects />
<%
String windowID = resourceRequest.getWindowID();
String seqDescribe = (String)(resourceRequest.getAttribute("SEQ_DESCRIBE"));
if (seqDescribe == null) {
	seqDescribe = "sequence from text field";
}
%>

<H2> Executing Blast Search on </H2>
<%=seqDescribe%>

<div id="<%=windowID%>_waitBar"></div>
