<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.JSONArray" 
%><%@ page import="org.json.simple.JSONObject" 
%><%
String cType = request.getParameter("cType");
String cId = request.getParameter("cId");

if (cType!=null && cId!=null) {
	DBSummary conn_summary = new DBSummary();
	HashMap<String,String> key = new HashMap<String,String>();
	
	if (cType.equals("genome")) {
		key.put("genome_info_id",cId);
	} else if (cType.equals("feature")) {
		key.put("feature_info_id",cId);
	}
	
	ArrayList<ResultType> seqs = conn_summary.getRefSeqs(key);

	JSONArray jsonResult = new JSONArray();
	for (int i=0; i<seqs.size(); i++) {
		JSONObject seq = new JSONObject();
		seq.put("length", Integer.parseInt(seqs.get(i).get("length")));
		seq.put("name", seqs.get(i).get("accession"));
		seq.put("sid", seqs.get(i).get("sequence_info_id"));
		seq.put("start", 0);
		seq.put("end", Integer.parseInt(seqs.get(i).get("length")));
		seq.put("seqDir", "");
		seq.put("seqChunkSize", Integer.parseInt(seqs.get(i).get("length")));
		jsonResult.add(seq);
	}
	response.setContentType("application/json");
	jsonResult.writeJSONString(out);
}
else {
	%>wrong parameter<%	
} 
%>