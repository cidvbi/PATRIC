<%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.SiteHelper" 
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.common.PDBInterface" 
%><%@ page import="edu.vt.vbi.patric.common.xmlHandler.*"
%><%@ page import="edu.vt.vbi.patric.common.SolrInterface" 
%><%@ page import="org.json.simple.JSONObject"
%><%@ page import="org.json.simple.JSONArray"
%><%

String pdbID = request.getParameter("pdb_id");
String chainID = request.getParameter("chain_id");

String _context_path = request.getContextPath();
String _codebase = _context_path+"/jmol";
String _datafile = "http://"+request.getServerName()+_context_path+"/jsp/readPDB.jsp?pdbID="+pdbID;

String urlNCBIStructure = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=structure&cmd=DetailsSearch&term=";
String urlPDB = "http://www.pdb.org/pdb/explore/explore.do?structureId=";
String urlSSGCID = "http://www.ssgcid.org/";
String urlCSGID = "http://www.csgid.org/";

String nameSSGCID = "Seattle Structural Genomics Center for Infectious Disease";
String nameCSGID = "Center for Structural Genomics of Infectious Diseases";

PDBInterface api = new PDBInterface();
HashMap<String,String> description = api.getDescription(pdbID);

//DBSummary conn_summary = new DBSummary();
//ArrayList<ResultType> beir = conn_summary.getBEIRClonesByPDB(pdbID);
//String beir_clone_id = "";
//String urlBEIR = SiteHelper.getExternalLinks("BEIR");
String target = null;
JSONObject feature = null;

if (pdbID != null && description != null) {
	
	ResultType key = new ResultType();
	
	// read associated features for given PDB ID
	SolrInterface solr = new SolrInterface();
	solr.setCurrentInstance("GenomicFeature");
	key.put("keyword", "ids:PDB|"+ pdbID);
	JSONObject res = solr.getData(key, null, null, 0, -1, false, false, false);
	JSONArray features = (JSONArray)((JSONObject)res.get("response")).get("docs");
	
	// retrieve structural meta data.
	solr.setCurrentInstance("Structural-Genomics");
	ArrayList<String> targetIDs = new ArrayList<String>();
	
	for (Object f : features) {
		feature = (JSONObject) f;
		key.put("keyword", "\"PATRIC_ID:"+ feature.get("na_feature_id") + "\"");
		JSONObject rest = solr.getData(key, null, null, 0, -1, false, false, false);
		JSONArray strctArr = (JSONArray)((JSONObject)rest.get("response")).get("docs");
		if (strctArr.isEmpty() == false) {
			JSONObject strct = (JSONObject) strctArr.get(0);
			targetIDs.add((String)strct.get("target_id"));
		}
	}
%>

<script type="text/javascript" src="<%=_codebase %>/Jmol.js" ></script>
<script type="text/javascript" src="/patric/js/vbi/StructureViewer.min.js" ></script>
<script type="text/javascript">
var viewer = null;

Ext.onReady(function() {
	viewer = Ext.create('VBI.Structure.Viewer', {
		renderTo:'sv',
		width: '100%',
		height: 820,
		pdbID: '<%=pdbID%>'
	});
});
</script>
	<div id="sv"></div>
	<div id="sv_mainviewer" style="z-index:0">
		<object id="appMain" type="application/x-java-applet" height="700" width="100%">
			<param name="code" value="JmolApplet" />
			<param name="archive" value="<%=_codebase %>/JmolApplet.jar" />
			<param name="load" value="<%=_datafile %>" />
			<param name="progressbar" value="true" />
			<param name="boxmessage" value="loading" />
			<param name="script" value="isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; 
				cartoons on; color white; select (helix,sheet); cartoons 200; select sheet; color [200,200,100];
				select helix; color [225,40,40]; select ligand; wireframe 60; spacefill 75; color [100,100,255]" />
			Applet failed to run. No Java plug-in was found. You can download Java JRE from <a href="http://java.com/en/download/" target=_blank>here</a>
		</object>
	</div>
	
	<div id="sv_overview">
		<table class="basic">
		<tr>
			<th scope="row" width="120">Title</th>
			<td colspan=3><%=description.get("title") %></td>
		</tr>
		<tr>
			<th scope="row">Organism </th>
			<td><%=(description.get("organism")!=null)?description.get("organism"):"&nbsp;" %></td>
			<th scope="row" width="120">Experiment Method </th>
			<td><%=description.get("expMethod") %></td>
		</tr>
		<tr>
			<th scope="row">Keywords</th>
			<td><%=description.get("keywords") %></td>
			<th scope="row">External Resources</th>
			<td><a href="<%=urlNCBIStructure %><%=pdbID %>" target="_blank">NCBI Structure</a>, <a href="<%=urlPDB %><%=pdbID %>" target="_blank">Protein Data Bank (PDB)</a></td>
		</tr>
	<% if (description.get("citation_authors")!=null) { %>
		<tr>
			<th scope="row">Citation </th>
			<td colspan=3>
		<% 	if (description.get("pubmedId")!=null && !description.get("pubmedId").equals(""))  { %>
					<a href="http://view.ncbi.nlm.nih.gov/pubmed/<%=description.get("pubmedId") %>" target="_blank"><%=description.get("citation_authors") %></a>
		<%	} else { %>
					<%=description.get("citation_authors") %>
		<%	} %>
			</td>
		</tr>
		<!-- deposition centers -->
			<%	if (description.get("citation_authors").toLowerCase().contains("ssgcid")) { %>
		<tr>
			<th scope="row">Deposition</th>
			<td colspan=3><a href="<%=urlSSGCID%>" target="_blank"><%=nameSSGCID %></a></td>
		</tr>
			<% 	} else if (description.get("citation_authors").toLowerCase().contains("csgid")) { %>
		<tr>
			<th scope="row">Deposition</th>
			<td colspan=3><a href="<%=urlCSGID%>" target="_blank"><%=nameCSGID %></a></td>
		</tr>
			<%	} %>
	<% } %>
	
	<%-- if (beir.size() > 0) { %>
		<tr>
			<th scope="row">BEIR Clones</th>
			<td colspan=3> 
				<% for (int u=0; u<beir.size(); u++) { 
					beir_clone_id = beir.get(u).get("beir_clone_id");
					beir_clone_id = beir_clone_id.replace("NRS","NR"); //temporary solution for NRS
				%>
				<a href="<%=urlBEIR%><%=beir_clone_id %>" target="_blank"><%=beir_clone_id %></a> (<%=beir.get(u).get("clone_name") %>)&nbsp; &nbsp;
				<% } %>
			</td>
		</tr>
	<% } --%>

	<% if (features.size() > 0) { %>
		<tr>
			<th scope="row">PATRIC Features</th>
			<td colspan=3>
				<% for (int u=0; u<features.size(); u++) { 
					feature = (JSONObject) features.get(u);
				%>
				<a href="Feature?cType=feature&cId=<%=feature.get("na_feature_id") %>" target="_blank"><%=feature.get("locus_tag") %></a>
				<% } %>
			</td>
	<% } %>
		
	<% if (targetIDs.size() > 0) { %>
		<tr>
			<th scope="row">Structure</th>
			<td colspan=3>
				<% for (int u=0; u<targetIDs.size(); u++) { 
					target = targetIDs.get(u);
				%>
				<a href="https://apps.sbri.org/SSGCIDTargetStatus/Target/<%=target %>" target="_blank"><%=target %></a>
				<% } %>
			</td>
	<% } %>
		</table>
	</div>

	<script type="text/javascript">
	function updateJmol(type) {
		var c, v;
		var appMain = document.getElementById("appMain");
		
		if (type == "wireframe") {
			c = document.getElementById("wireframesMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "on") {
				appMain.script("select all;wireframe 30;");
			} else if (v == "off") {
				appMain.script("select all;wireframe off;");
			}
		}
		else if (type == "cartoon") {
			c = document.getElementById("cartoonsMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "thin") {
				appMain.script("select all;cartoons 100;");
			} else if (v == "thick") {
				appMain.script("select all;cartoons 200;");
			} else if (v == "off") {
				appMain.script("select all;cartoons off;");
			}
		}
		else if (type == "spacefill") {
			c = document.getElementById("spacefillMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "small") {
				appMain.script("select all;spacefill 25%;");
			} else if (v == "large") {
				appMain.script("select all;spacefill 50%;");
			} else if (v == "off") {
				appMain.script("select all;spacefill off;");
			}
		}
		else if (type == "surface") {
			c = document.getElementById("surfaceMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "translucent") {
				appMain.script("select all;isosurface delete color lightblue resolution 0 sasurface 1.4 translucent;");
			} else if (v == "opaque") {
				appMain.script("select all;isosurface delete color lightblue resolution 0 sasurface 1.4 opaque;");
			} else if (v == "off") {
				appMain.script("select all;isosurface off;");
			}
		}
		else if (type == "label") {
			c = document.getElementById("labelsMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "element") {
				appMain.script("select all;label off;font label 12;color labels JMOL;label %e;");
			} else if (v == "atom") {
				appMain.script("select all;label off;font label 12;color labels JMOL;label %a;");
			} else if (v == "off") {
				appMain.script("select all;label off;");
			}
		}
		else if (type == "show") {
			c = document.getElementById("showMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "surface residue") {
				appMain.script("display *;hide surfacedistance != 0;");
			} else { //everything
				appMain.script("display *;");
			}
		}
		else if (type == "resetApperance") {
			resetApperanceControls();
			appMain.script("set showAxes false;");
			appMain.script("select all;label off;display *;");//label off & display everything
			appMain.script(getJmolDecoration("onload"));
		}
		// navigation related types
		else if (type == "spin") {
			c = document.getElementById("spinMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "x") {
				appMain.script("select all;set spin X 40;set spin Y 0;set spin Z 0;spin on;");
			} else if (v == "y") {
				appMain.script("select all;set spin Y 40;set spin X 0;set spin Z 0;spin on;");
			} else if (v == "z") {
				appMain.script("select all;set spin Z 40;set spin Y 0;set spin X 0;spin on;");
			} else { //off
				appMain.script("select all;spin off;");
			}
		}
		else if (type == "zoom") {
			c = document.getElementById("zoomMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "50") {
				appMain.script("select all;zoom 50;");
			} else if (v == "150") {
				appMain.script("select all;zoom 150;");
			} else { // 100
				appMain.script("select all;zoom 100;");
			}
		}
		else if (type == "resetNavigation") {
			resetNavigationControls();
			appMain.script("select all;zoom 100;spin off;");
		}
	}
	</script>
	<div id="sv_controls">
		<h2>Appearance</h2>	
		<label for="wireframeMenu">Wireframes:</label>
		<select id="wireframesMenu" onchange="updateJmol('wireframe')">
			<option value="">set ...</option>
			<option value="off">off</option>
			<option value="on">on</option>
		</select>
		<br/>
		<label for="cartoonsMenu">Cartoons:</label>
		<select id="cartoonsMenu" onchange="updateJmol('cartoon')">
			<option value="">set ...</option>
			<option value="off">off</option>
			<option value="thin">thin</option>
			<option value="thick">thick</option>
		</select>
		<br/>
		<label for="spacefillMenu">Spacefill:</label>
		<select id="spacefillMenu" onchange="updateJmol('spacefill')">
			<option value="">set ...</option>
			<option value="off">off</option>
			<option value="small">small</option>
			<option value="large">large</option>
		</select>
		<br/>
		<label for="surfaceMenu">Surface:</label>
		<select id="surfaceMenu" onchange="updateJmol('surface')">
			<option value="">set ...</option>
			<option value="off">off</option>
			<option value="translucent">translucent</option>
			<option value=opaque>opaque</option>
		</select>
		<br/>
		<label for="labelsMenu">Labels:</label>
		<select id="labelsMenu" onchange="updateJmol('label')">
			<option value="off">off</option>
			<option value="element">element</option>
			<option value="atom">atom</option>
		</select>
		<br/>
		<label for="showMenu">Show:</label>
		<select id="showMenu" onchange="updateJmol('show')">
			<option value="everything">everything</option>
			<option value="surface residue">surface residue</option>
		</select>
		<br />
		<script type="text/javascript">
		//A little javascript to save the default values of each of the dropdowns
		wireframesDefault = document.getElementById("wireframesMenu").value;
		cartoonsDefault = document.getElementById("cartoonsMenu").value;
		spacefillDefault = document.getElementById("spacefillMenu").value;
		surfaceDefault = document.getElementById("surfaceMenu").value;
		labelsDefault = document.getElementById("labelsMenu").value;
		showDefault = document.getElementById("showMenu").value;

		function resetApperanceControls(flag) {
			document.getElementById("wireframesMenu").value = wireframesDefault;
			document.getElementById("cartoonsMenu").value = cartoonsDefault;
			document.getElementById("spacefillMenu").value = spacefillDefault;
			document.getElementById("surfaceMenu").value = surfaceDefault;
			if (flag != false) {
				document.getElementById("labelsMenu").value = labelsDefault;
				document.getElementById("showMenu").value = showDefault;
			}
		}
		</script>

		<div class="far2x">
			<input type="button" class="right" value="Reset" onclick="updateJmol('resetApperance')">
		</div>

		<h2>Navigation</h2>	
		<label for="spinMenu">Spin:</label>
		<select id="spinMenu" onchange="updateJmol('spin')">
			<option value="off">off</option>
			<option value="x">X Axis</option>
			<option value="y">Y Axis</option>
			<option value="z">Z Axis</option>
		</select>
		<br/>
		<label for="zoomMenu">Zoom:</label>
		<select id="zoomMenu" onchange="updateJmol('zoom')">
			<option value="50">50%</option>
			<option value="100" selected="selected">100%</option>
			<option value="150">150%</option>
		</select>

		<script type="text/javascript">
		//A little javascript to save the default values of each of the dropdowns
		spinDefault = document.getElementById("spinMenu").value;
		zoomDefault = document.getElementById("zoomMenu").value;

		function resetNavigationControls() {
			document.getElementById("spinMenu").value = spinDefault;
			document.getElementById("zoomMenu").value = zoomDefault;
		}
		</script>
		<div>
			<input type="button" class="right" value="Reset" onclick="updateJmol('resetNavigation')">
		</div>
	</div>
<%
} else {
	%>No data available.<%
}
%>
<script type="text/javascript">
Ext.onReady(function () {
	if (Ext.get("tabs_structureviewer")!=null) {
		Ext.get("tabs_structureviewer").addCls("sel");
	}
});
</script>