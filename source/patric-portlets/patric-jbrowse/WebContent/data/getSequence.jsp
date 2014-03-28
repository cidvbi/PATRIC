<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="edu.vt.vbi.patric.common.StringHelper" 
%><%
String _accession = request.getParameter("accession");
String _st = request.getParameter("start");
String _nd = request.getParameter("end");
String _accn = null;
String _sid = null;

int start = 1;
int end = 0;
int length = -1;

if (_st != null && _nd != null) {
	start = Integer.parseInt(_st);
	end = Integer.parseInt(_nd);
	if (start<0) { start = 0; }
	length = end-start+1;
}

response.setContentType("text/javascript");

if (_accession!=null) {
	
	String[] names = _accession.split("\\|");
	if (names[3] != null) {
		_accn = names[3];
	}
	if (names[1] != null) {
		_sid = names[1];
	}
	
	DBShared conn_shared = new DBShared();
	String seq = conn_shared.getNASequence(_sid, _accn, start, length);
	
	if (length > 0) {
		ResultType meta = conn_shared.getSequenceInfoByAccession(_sid, _accn);
		String header = ">" + _accession + "   "+meta.get("sequence_description")+" ("+start+".."+end+") ["+meta.get("genome_name")+"] \n";
		
		seq = header + StringHelper.chunk_split(seq, 60, "\n");
		out.println(seq);
	}
	else {
		if (seq != null) {
			out.println(seq);
		}
		else {
			out.println("no sequence available");
		}
	}
}
else {
	out.println("wrong parameters!");
}
%>