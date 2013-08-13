<%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.SiteHelper"
%><%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.common.PDBInterface"
%><%@ page import="edu.vt.vbi.patric.common.xmlHandler.*"
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

DBSummary conn_summary = new DBSummary();
ArrayList<ResultType> beir = conn_summary.getBEIRClonesByPDB(pdbID);
String beir_clone_id = "";
String urlBEIR = SiteHelper.getExternalLinks("BEIR");

if (pdbID != null && description != null) {


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
	<%-- 
	<div id="jmol_sub_wrapper" style="display:none">
		<script type="text/javascript">
			//jmolInitialize("<%=_codebase%>/");
			//jmolApplet(["100%","100%"], "","appSub");
		</script>
	</div>--%>
	
	<div id="sv_mainviewer" style="z-index:0">
		 <%-- 
		<script type="text/javascript">
			jmolInitialize("<%=_codebase%>/",false);
			jmolApplet(["100%",600], "load <%=_datafile%>;"+getJmolDecoration("onload"),"appMain");
		</script>
		--%>
		
		<applet name="appMain" code="JmolApplet" archive="<%=_codebase %>/JmolApplet.jar" mayscript="true" height="700" width="100%">
			<param name="load" value="<%=_datafile %>">
			<param name="progressbar" value="true">
			<param name="boxmessage" value="loading">
			<param name="script" value="isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; 
			cartoons on; color white; select (helix,sheet); cartoons 200; select sheet; color [200,200,100];
			select helix; color [225,40,40]; select ligand; wireframe 60; spacefill 75; color [100,100,255]">
		</applet>
		<%--
		<table cellspacing="0" width="100%" style="">
			<tr>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					<b>Zoom</b>
				</td>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					<b>Rotate</b>
				</td>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					<b>Options</b>
				</td>
			</tr>
			<tr>
				<td style="background-color:#E5E8EE;text-align:center;">
					<img src="/patric/images/icon_3d_zoom.gif" />
				</td>
				<td style="background-color:#E5E8EE;text-align:center;">
					<img src="/patric/images/icon_3d_rotate.gif" />
				</td>
				<td style="background-color:#E5E8EE;text-align:center;">
					<img src="/patric/images/icon_3d_options.gif" />
				</td>
			</tr>
			<tr>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					Mouse scroll wheel, or <br />Shift+Left mouse button.
				</td>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					Left mouse button
				</td>
				<td style="background-color:#E5E8EE; width:200px;text-align:center;">
					Right mouse button, or <br />Ctrl+Left mouse button
				</td>
			</tr>
		</table>--%>
	</div>
	
	<div id="sv_overview" class="table-container">
		<table width="100%" cellspacing=0 class="data-table">
			<tr>
				<td width="120">Title</td>
				<td colspan=3><%=description.get("title") %></td>
			</tr>
			<tr>
				<td>Organism </td>
				<td><%=description.get("organism") %></td>
				<td width="120">Experiment Method </td>
				<td><%=description.get("expMethod") %></td>
			</tr>
			<tr>
				<td>Keywords </td>
				<td><%=description.get("keywords") %></td>
				<td>External Resources</td>
				<td><a href="<%=urlNCBIStructure %><%=pdbID %>" target="_blank">NCBI Structure</a>, <a href="<%=urlPDB %><%=pdbID %>" target="_blank">Protein Data Bank (PDB)</a></td>
			</tr>
	<%  if (description.get("citation_authors")!=null) { %>			
			<tr>
				<td>Citation </td>
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
					<td>Deposition</td>
					<td colspan=3><a href="<%=urlSSGCID%>" target="_blank"><%=nameSSGCID %></a></td>
				</tr>
			<% 	} else if (description.get("citation_authors").toLowerCase().contains("csgid")) { %>
				<tr>
					<td>Deposition</td>
					<td colspan=3><a href="<%=urlCSGID%>" target="_blank"><%=nameCSGID %></a></td>
				</tr>
			<%	} %>
	<%  } %>
	
	<% if (beir.size() > 0) { %>
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
	<% } %>
	
		</table>
	</div>
	

	<script type="text/javascript">
	function updateJmol(type) {
		var c, v;
		
		if (type == "wireframe") {
			c = document.getElementById("wireframesMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "on") {
				document.appMain.script("select all;wireframe 30;");
			} else if (v == "off") {
				document.appMain.script("select all;wireframe off;");
			}
		}
		else if (type == "cartoon") {
			c = document.getElementById("cartoonsMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "thin") {
				document.appMain.script("select all;cartoons 100;");
			} else if (v == "thick") {
				document.appMain.script("select all;cartoons 200;");
			} else if (v == "off") {
				document.appMain.script("select all;cartoons off;");
			}
		}
		else if (type == "spacefill") {
			c = document.getElementById("spacefillMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "small") {
				document.appMain.script("select all;spacefill 25%;");
			} else if (v == "large") {
				document.appMain.script("select all;spacefill 50%;");
			} else if (v == "off") {
				document.appMain.script("select all;spacefill off;");
			}
		}
		else if (type == "surface") {
			c = document.getElementById("surfaceMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "translucent") {
				document.appMain.script("select all;isosurface delete color lightblue resolution 0 sasurface 1.4 translucent;");
			} else if (v == "opaque") {
				document.appMain.script("select all;isosurface delete color lightblue resolution 0 sasurface 1.4 opaque;");
			} else if (v == "off") {
				document.appMain.script("select all;isosurface off;");
			}
		}
		else if (type == "label") {
			c = document.getElementById("labelsMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "element") {
				document.appMain.script("select all;label off;font label 12;color labels JMOL;label %e;");
			} else if (v == "atom") {
				document.appMain.script("select all;label off;font label 12;color labels JMOL;label %a;");
			} else if (v == "off") {
				document.appMain.script("select all;label off;");
			}
		}
		else if (type == "show") {
			c = document.getElementById("showMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "surface residue") {
				document.appMain.script("display *;hide surfacedistance != 0;");
			} else { //everything
				document.appMain.script("display *;");
			}
		}
		else if (type == "resetApperance") {
			resetApperanceControls();
			document.appMain.script("set showAxes false;");
			document.appMain.script("select all;label off;display *;");//label off & display everything
			document.appMain.script(getJmolDecoration("onload"));
		}
		// navigation related types
		else if (type == "spin") {
			c = document.getElementById("spinMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "x") {
				document.appMain.script("select all;set spin X 40;set spin Y 0;set spin Z 0;spin on;");
			} else if (v == "y") {
				document.appMain.script("select all;set spin Y 40;set spin X 0;set spin Z 0;spin on;");
			} else if (v == "z") {
				document.appMain.script("select all;set spin Z 40;set spin Y 0;set spin X 0;spin on;");
			} else { //off
				document.appMain.script("select all;spin off;");
			}				
		}
		else if (type == "zoom") {
			c = document.getElementById("zoomMenu");
			v = c.options[c.selectedIndex].value;
			if (v == "50") {
				document.appMain.script("select all;zoom 50;");
			} else if (v == "150") {
				document.appMain.script("select all;zoom 150;");
			} else { // 100
				document.appMain.script("select all;zoom 100;");
			}
		}
		else if (type == "resetNavigation") {
			resetNavigationControls();
			document.appMain.script("select all;zoom 100;spin off;");
		}
	}
	</script>
	<div id="sv_controls">
		<div class="table-container">
				<h2>Appearance</h2>	
				<table cellspacing="0" width="100%" class="data-table" style="">
				<tr>
					<td>Wireframes</td>
					<td>
						<select id="wireframesMenu" onchange="updateJmol('wireframe')">
							<option value="">set ...</option>
							<option value="off">off</option>
							<option value="on">on</option>
						</select>
						<%-- <script type="text/javascript">
							jmolMenu([
								["select all;wireframe off;", "off", true],
								["select all;wireframe 30;", "on"]
								]
								,1,"wireframesMenu");
						</script> --%>
					</td>
				</tr>
				<tr>
					<td>Cartoons</td>
					<td>
						<select id="cartoonsMenu" onchange="updateJmol('cartoon')">
							<option value="">set ...</option>
							<option value="off">off</option>
							<option value="thin">thin</option>
							<option value="thick">thick</option>
						</select>
						<%--<script type="text/javascript">
							jmolMenu([
								["select all;cartoons off;", "off"],
								["select all;cartoons 100;", "thin"],
								["select all;cartoons 200;", "thick", true]
								]
								,1,"cartoonsMenu");
						</script>--%>
					</td>
				</tr>
				<tr>
					<td>Spacefill</td>
					<td>
						<select id="spacefillMenu" onchange="updateJmol('spacefill')">
							<option value="">set ...</option>
							<option value="off">off</option>
							<option value="small">small</option>
							<option value="large">large</option>
						</select>
						<%--<script type="text/javascript">
							jmolMenu([
								["select all;spacefill off;", "off", true],
								["select all;spacefill 25%;", "small"],
								["select all;spacefill 50%;", "large"]
								]
								,1,"spacefillMenu");
						</script>--%>
					</td>
				</tr>
				<tr>
					<td>Surface</td>
					<td>
						<select id="surfaceMenu" onchange="updateJmol('surface')">
							<option value="">set ...</option>
							<option value="off">off</option>
							<option value="translucent">translucent</option>
							<option value=opaque>opaque</option>
						</select>
						<%--<script type="text/javascript">
							jmolMenu([
								["select all;isosurface off;", "off", true],
								["select all;isosurface delete color lightblue resolution 0 sasurface 1.4 translucent;", "translucent"],
								["select all;isosurface delete color lightblue resolution 0 sasurface 1.4 opaque;", "opaque"]
								]
								,1,"surfaceMenu");
						</script>--%>
					</td>
				</tr>
				<tr>
					<td>Labels</td>
					<td>
						<select id="labelsMenu" onchange="updateJmol('label')">
							<option value="off">off</option>
							<option value="element">element</option>
							<option value="atom">atom</option>
						</select>
						<%--<script type="text/javascript">
							jmolMenu([
								["select all;label off;", "off", true],
								["select all;label off;font label 12;color labels JMOL;label %e;", "element"],
								["select all;label off;font label 12;color labels JMOL;label %a;", "atom"],
								["select all;label off;font label 12;color labels JMOL;select *:<?=$chain?>;label %m;", "residue"]
								]
								,1,"labelsMenu");
						</script>--%>
					</td>
				</tr>
				<tr>
					<td>Show</td>
					<td>
						<select id="showMenu" onchange="updateJmol('show')">
							<option value="everything">everything</option>
							<option value="surface residue">surface residue</option>
						</select>
						<%--<script type="text/javascript">
							jmolMenu([
								["display *;", "everything", true],
								["display *;hide not *:<?=$chain?>;", "chain"],
								["display *;hide surfacedistance != 0;", "surface residue"]
								]
								,1,"showMenu");
						</script>--%>
					</td>
				</tr>
				</table>
				<br />
			<!-- 
				<style>
				#showMenu{
					width: 9.5em;
				}
				#surfaceMenu{
					width: 8em;
				}
				</style>
			-->
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

				<div style="padding: 0em 0em 0em 0px; text-align: right;">
					<input type="button" value="Reset" onclick="updateJmol('resetApperance')">
					<%--
					<script type="text/javascript">
					jmolButton(getJmolDecoration("onload")+";javascript resetApperanceControls();", "Reset");
					</script> --%>
				</div>
			</div>
			<div class="table-container" style="margin-top:0px;">
					<h2>Navigation</h2>	
					<table cellspacing="0" width="100%" class="data-table" style="font-size: 1em;">
					<tr>
						<td>Spin</td>
						<td>
							<select id="spinMenu" onchange="updateJmol('spin')">
								<option value="off">off</option>
								<option value="x">X Axis</option>
								<option value="y">Y Axis</option>
								<option value="z">Z Axis</option>
							</select>
							<%--<script type="text/javascript">
								jmolMenu([
									["select all;spin off;", "off", true],
									["select all;set spin X 40;set spin Y 0;set spin Z 0;spin on;", "X Axis"],
									["select all;set spin Y 40;set spin X 0;set spin Z 0;spin on;", "Y Axis"],
									["select all;set spin Z 40;set spin Y 0;set spin X 0;spin on;", "Z Axis"]
									]
									,1,"spinMenu");
							</script>--%>
						</td>
					</tr>
					<tr>
						<td>Zoom</td>
						<td>
							<select id="zoomMenu" onchange="updateJmol('zoom')">
								<option value="50">50%</option>
								<option value="100" selected="selected">100%</option>
								<option value="150">150%</option>
							</select>
							<%--<script type="text/javascript">
								jmolMenu([
									["select all;zoom 50;", "50%"],
									["select all;zoom 100;", "100%", true],
									["select all;zoom 150;", "150%"]
									]
									,1,"zoomMenu");
							</script>--%>
						</td>
					</tr>
					</table>
					<br />

					<script type="text/javascript">
					//A little javascript to save the default values of each of the dropdowns
					spinDefault = document.getElementById("spinMenu").value;
					zoomDefault = document.getElementById("zoomMenu").value;

					function resetNavigationControls()
					{
						document.getElementById("spinMenu").value = spinDefault;
						document.getElementById("zoomMenu").value = zoomDefault;
					}
					</script>

					<div style="padding: 0em 0em 0em 0px; text-align: right;">
						<input type="button" value="Reset" onclick="updateJmol('resetNavigation')">
						<%--<script type="text/javascript">
							jmolButton("select all;zoom 100;spin off;javascript resetNavigationControls();", "Reset");
						</script>--%>
					</div>
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