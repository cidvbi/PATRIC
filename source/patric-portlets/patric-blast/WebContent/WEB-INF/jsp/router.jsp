<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%@ page import="java.util.*" %>

<%
String windowID = renderRequest.getWindowID();
String resourceURL = (renderResponse.createResourceURL()).toString();
String contextPath = renderResponse.encodeURL(renderRequest.getContextPath());

String pk = request.getParameter("param_key");
String dm = request.getParameter("display_mode");
HashMap<String,String> key = null;

if (pk != null) {
	key = (HashMap<String,String>) portletSession.getAttribute("key" + pk);
} 
if(key == null || pk == null || pk == ""){
	pk = "";	
	key = new HashMap<String,String>();
	key.put("programIndex", "0");
	key.put("dbIndex", "0");
	key.put("sequence", "");
	key.put("queryFrom", "");
	key.put("queryTo", "");
	key.put("expectIndex", "3");
	key.put("matParamIndex", "3");
	key.put("alignment", "");
	key.put("lowFilter", "L");
	key.put("midFilter", "");
	key.put("geneCodeIndex", "0");
	key.put("dbCodeIndex", "0");
	key.put("oofAlignIndex", "19");
	key.put("advanced", "");
	key.put("overview", "on");
	key.put("alignViewIndex", "0");
	key.put("alignmentsIndex", "2");
	key.put("descriptionIndex", "3");
	key.put("schemaIndex", "0");
	key.put("fileName", "");
}
%>
<script type="text/javascript" src="/patric-common/js/ZeroClipboard.js"></script>
<script type="text/javascript" src="/patric-common/js/parameters.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/namespace.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/GetBlast.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/searchResult.js"></script>					
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>

<form id='<%=windowID%>_keyStore' >
	<input name='programIndex' type='hidden'
			value="<%=key.get("programIndex") %>" />
			
	<input name='dbIndex' type='hidden'
			value="<%=key.get("dbIndex") %>" />
			
	<input name='genome' type='hidden'
			value="<%=key.get("genome") %>" />
			
	<input name='sequence' type='hidden'
			value="<%=key.get("sequence") %>" />

	<input name='queryFrom' type='hidden'
			value="<%=key.get("queryFrom") %>" />
			
	<input name='queryTo' type='hidden'
			value="<%=key.get("queryTo") %>" />
			
	<input name='expectIndex' type='hidden'
			value="<%=key.get("expectIndex") %>" />
			
	<input name='matParamIndex' type='hidden'
			value="<%=key.get("matParamIndex") %>" />

	<input name='alignment' type='hidden'
			value="<%=key.get("alignment") %>" />
			
	<input name='lowFilter' type='hidden'
			value="<%=key.get("lowFilter") %>" />

	<input name='midFilter' type='hidden'
			value="<%=key.get("midFilter") %>" />
			
	<input name='geneCodeIndex' type='hidden'
			value="<%=key.get("geneCodeIndex") %>" />
			
	<input name='dbCodeIndex' type='hidden'
			value="<%=key.get("dbCodeIndex") %>" />
	
	<input name='oofAlignIndex' type='hidden'
			value="<%=key.get("oofAlignIndex") %>" />
			
	<input name='advanced' type='hidden'
			value="<%=key.get("advanced") %>" />
	
	<input name='overview' type='hidden'
			value="<%=key.get("overview") %>" />
	
	<input name='alignViewIndex' type='hidden'
			value="<%=key.get("alignViewIndex") %>" />
			
	<input name='alignmentsIndex' type='hidden'
			value="<%=key.get("alignmentsIndex") %>" />
			
	<input name='descriptionIndex' type='hidden'
			value="<%=key.get("descriptionIndex") %>" />
			
	<input name='schemaIndex' type='hidden'
			value="<%=key.get("schemaIndex") %>" />

	<input name='fileName' type='hidden'
			value="<%=key.get("fileName") %>" />
</form>


<div id="<%=windowID%>" style="width:100%"></div>
<iframe id="<%=windowID%>_catch" name="<%=windowID%>_catch" style = "visibility:hidden;"></iframe>
<script type="text/javascript">
var $Page;
var pageProperties = {cart:true};
SetPageProperties(pageProperties);

Ext.onReady(function() {
	var pSet = '<%=pk%>';
	if (pSet != '') {
		BlastOnReady('<%=windowID%>', '<%=resourceURL%>', '<%=contextPath%>',
		 			 pSet, '<%=dm%>');
		
	} else {
  		Ext.Ajax.request({
    		url: '<%=resourceURL%>',
    		method: 'POST',
			params: 
				{callType: "formStore",
    			 programIndex: "0",
    			 dbIndex: "0",
    		     sequence: "",
    			 queryFrom: "",
    			 queryTo: "",
    		     expectIndex: "3",
    		     matParamIndex: "3",
    			 alignment: "",
    			 lowFilter: "L",
    			 midFilter: "",
    			 geneCodeIndex: "0",
    			 dbCodeIndex: "0",
    		     oofAlignIndex: "19",
    		     advanced: "",
    		     overview: "on",
    		     alignViewIndex: "0",
    		     alignmentsIndex: "2",
    		     descriptionIndex: "3",
    		     schemaIndex: "0",
    		     fileName: ""
				},
    		success: function(rs) {
				//relocate to result page
				document.location.href = 
	  				"Blast?dm=result&pk=" + rs.responseText;
			}
 	 	});
	}
}
);
 	 
</script>
