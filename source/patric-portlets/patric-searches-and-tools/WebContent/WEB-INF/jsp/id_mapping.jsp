<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="edu.vt.vbi.patric.common.OrganismTreeBuilder" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.*" 
%><portlet:defineObjects/><%
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
String pk = request.getParameter("param_key");
String to = request.getParameter("to") == null?"UniProtKB-ID":request.getParameter("to");
String from = request.getParameter("from") == null?"PATRIC Locus Tag":request.getParameter("from");
String keyword = request.getParameter("id") == null?"":request.getParameter("id");


ResultType key = null;

if(pk != null){
	key = (ResultType) portletSession.getAttribute("key"+pk);

	if(key != null){
		to = key.get("to");
		from = key.get("from");
		keyword = key.get("keyword");
	}
}

//System.out.print(request.getParameter("display_mode"));

if (request.getParameter("display_mode").equals(""))
	keyword = "";


boolean loggedIn = false;
if(request.getUserPrincipal() == null){
	loggedIn = false;
}else
	loggedIn = true;

%>
	<div id="intro" class="searchtool-intro"> 
		<p>The ID Mapping Tool enables researchers to locate synonymous identifiers across multiple-source databases.  
		For further explanation, please see <a href="http://enews.patricbrc.org/id-mapping-tool-faqs/">ID Mapping Tool FAQs</a>.</p> 
	</div> 
 
	<div id="result-meta" class="search-results-form-wrapper" style="display:none"> 
		<a href="#" id="modify-search"><img src="/patric/images/btn_modify_search.gif" id="modify-search-btn" alt="Modify Search Criteria" /></a> 
	</div> 

	<form id="searchForm" name="searchForm" action="#" method="post" onsubmit="return false;"> 

	<div class="left" style="width:480px">
		<h3><img src="/patric/images/number1.gif" alt="1" height="14" width="14" /> IDs</h3><br/> 
		<textarea id="keyword" name="keyword" cols="60" rows="7"><%=keyword %></textarea> 
	</div>
	
	<div class="left" style="width:25px">&nbsp;</div>
	<div class="left" style="width:375px"> 
		<h3><img src="/patric/images/number2.gif" alt="2" height="14" width="14" /> ID Types</h3><br/> 
		<table class="querytable">
		<tr> 
			<td><b>FROM</b> ID Type:</td><td><div id="from"></div></td> 
		</tr>
		<tr>
			<td><b>TO</b> ID Type:</td><td><div id="to"></div></td> 
		</tr>
		<tr>
			<td class="formaction" colspan="2"><input class="button rightarrow" type="submit" value="Search" onclick="searchbykeyword()" /></td> 
		</tr>
		</table>
	</div>
	</form>
	<div class="clear"></div>
<script type="text/javascript"> 
//<![CDATA[
var store, combo, combo2, readerx;
var loggedIn = <%=loggedIn%>;
var combo_prev_value = "PATRIC Locus Tag", combo2_prev_value = "UniProtKB-ID";
Ext.onReady(function(){

	store = new Ext.data.JsonStore({
	    root: 'id_types',
	    fields: ['id', 'value', 'group']
	});
	
	Ext.Ajax.request({
	    url: "/patric-searches-and-tools/jsp/idmapping_filter_populate.jsp",
	    method: 'GET',
	    success: function(response, opts) {
	
	        readerx = Ext.JSON.decode(response.responseText);
	        
	        store.loadData(readerx.id_types);
	        combo.setValue('<%=from%>');
	        combo2.setValue('<%=to%>');
	    }
	});
	
	combo = Ext.create('Ext.form.ComboBox',{
	    store: store,
	    displayField:'value',
	    queryMode: 'local',
	    hideTrigger: false,  //hide trigger so it doesn't look like a combobox.
	    renderTo: 'from',
	    width: 230,
	    triggerAction: 'all',
	    blankText: "PATRIC Locus Tag",
	    editable:false,
	    listeners: {
            'select' : function(){
 				
 				
 				if(combo.rawValue.indexOf("Identifiers") > 0)
 					combo.setValue(combo_prev_value);
 				else
 					combo_prev_value = combo.rawValue;

	        	if(combo.rawValue != "PATRIC Locus Tag")
	    			combo2.setValue("PATRIC Locus Tag");
    		}
		}
	      
	  });
 	
	combo2 = Ext.create('Ext.form.ComboBox', {
	    store: store,
	    displayField:'value',
	    queryMode: 'local',
	    hideTrigger: false,  //hide trigger so it doesn't look like a combobox.
	    renderTo: 'to',
	    width: 230, 
	    triggerAction: 'all',
	    blankText: "UniProtKB-ID",
	    editable:false,
	    listeners: {
            'select' : function(){
 
 				if(combo2.rawValue.indexOf("Identifiers") > 0)
 					combo2.setValue(combo2_prev_value);
 				else
 					combo2_prev_value = combo2.rawValue;
 				
	        	if(combo2.rawValue != "PATRIC Locus Tag")
	    			combo.setValue("PATRIC Locus Tag");
    		}
		}
	});
 	  
});
 
function searchbykeyword() 
{
	var genomeId;

	if(Ext.getDom("keyword").value == "")
		
		Ext.MessageBox.alert("Empty Search Box...");

	else{

		var value = Ext.getDom("keyword").value;

		value = value.replace(/,/g,"~");
		value = value.replace(/\n/g,"~");
		
		var size = value.split("~").length;
	
		if(size > 5000){
			
			Ext.MessageBox.alert(size+" IDs", 'Current resources can not handle more than 5000 ids...');
			
		}else{
		
			Ext.Ajax.request({
				url: '/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE',
				method: 'POST',
				params: {cType: "taxon",
					cId: "",
					sraction: "save_params",
					keyword: Ext.getDom("keyword").value.trim(),
					from: combo.getValue(),
					to: combo2.getValue()
				},
				success: function(rs) {
					//relocate to result page
					document.location.href="IDMapping?cType=taxon&cId=&dm=result&pk="+rs.responseText;
				}
			});
		}
	}
}
//]]>
</script>