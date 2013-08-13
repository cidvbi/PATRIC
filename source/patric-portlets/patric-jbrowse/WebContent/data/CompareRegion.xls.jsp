<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject" 
%><%@ page import="edu.vt.vbi.patric.common.ExcelHelper" 
%><%@ page import="edu.vt.vbi.patric.common.CRFeature" 
%><%@ page import="edu.vt.vbi.patric.common.CRResultSet" 
%><%@ page import="edu.vt.vbi.patric.common.CRTrack" 
%><%@ page import="org.theseed.servers.SAPserver" 
%><%@ page import="java.io.OutputStream" 
%><%@ page import="javax.portlet.PortletSession" 
%><%
//String _key = request.getParameter("key");
//System.out.println("key:"+_key);
//CRResultSet crRS = (CRResultSet) session.getAttribute(_key);

//String _feature = request.getParameter("feature"); //pin feature
String _cId = "17814375";
DBSummary conn_summary = new DBSummary();
HashMap<String,ResultType> pseedMap = conn_summary.getPSeedMapping("PATRIC", _cId);
String _feature = pseedMap.get(_cId).get("pseed_id");
			
String _window = "10000"; //request.getParameter("window"); //window size
int _numRegion = 5; // Integer.parseInt(request.getParameter("regions")); //number of genomes to compare
int _numRegion_buffer = 10;	
SAPserver sapling = new SAPserver("http://servers.nmpdr.org/pseed/sapling/server.cgi");
CRResultSet crRS = new CRResultSet(_feature, sapling.compared_regions(_feature, _numRegion+_numRegion_buffer, Integer.parseInt(_window)/2));

CRTrack crTrack = null;
CRFeature crFeature = null;
String genome_name = null;

ArrayList<String> _tbl_header = new ArrayList<String>();
ArrayList<String> _tbl_field = new ArrayList<String>();
JSONArray _tbl_source = new JSONArray();

_tbl_header.addAll(Arrays.asList(new String[] {"Genome Name", "Feature", "Start", "End", "Strand", "FigFam", "Product", "Group"}));
_tbl_field.addAll(Arrays.asList(new String[] {"genome_name", "feature_id", "start", "end", "strand", "figfam_id", "product", "group_id"}));

	if (crRS != null && crRS.getGenomeNames().size() >0) {
			
		for (Integer idx: crRS.keySet()) {
			
			crTrack = crRS.get(idx);
			genome_name = crTrack.getGenomeName();
			
			for (int i=0; i<crTrack.size(); i++) {
				
				crFeature = crTrack.get(i);
				JSONObject f = new JSONObject();
				f.put("genome_name", genome_name);
				f.put("feature_id", crFeature.getfeatureID());
				f.put("start", 		crFeature.getStartPosition());
				f.put("end", 		crFeature.getEndPosition());
				f.put("strand", 	crFeature.getStrand());
				f.put("figfam_id",	crFeature.getFigfam());
				f.put("product", 	crFeature.getProduct());
				f.put("group_id", 	crFeature.getGrpNum());
				
				_tbl_source.add(f);
			}
		}
	}

	response.setContentType("application/octetstream");
	response.setHeader("Content-Disposition", "attachment; filename=\"CompareRegionView.xlsx\"");
	
	OutputStream outs = response.getOutputStream();
	ExcelHelper excel = new ExcelHelper("xssf", _tbl_header, _tbl_field, _tbl_source);
	excel.buildSpreadsheet();
	excel.writeSpreadsheettoBrowser(outs);
%>