<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSearch" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="org.json.simple.*" 
%><%
	JSONObject json = new JSONObject();
	
	DBSearch conn_search = new DBSearch();
	
	//Iterator itr = null;
	
	Object obj = null;
	
	String id1 = "PATRIC Identifiers";
	String id2 = "REFSEQ Identifiers";
	
	ArrayList<ResultType> id_types = conn_search.getIDTypes();
	
	ResultType row = new ResultType();
	ResultType row0 = new ResultType();
	ResultType row1 = new ResultType();
	ResultType row2 = new ResultType();
	ResultType row21 = new ResultType();
	ResultType row3 = new ResultType();
	ResultType row4 = new ResultType();
	ResultType row5 = new ResultType();
	ResultType row6 = new ResultType();
	ResultType row7 = new ResultType();
	
	row0.put("id", id1);
	row0.put("value", "<h5>"+id1+"</h5>");
	
	row1.put("id", "PATRIC Locus Tag");
	row1.put("value", "PATRIC Locus Tag");
	row1.put("group", id1);
	row.put("id", "PATRIC ID");
	row.put("value", "PATRIC ID");
	row.put("group", id1);
	row2.put("id", "PSEED ID");
	row2.put("value", "PSEED ID");
	row2.put("group", id1);
	
	row21.put("id", id2);
	row21.put("value", "<h5>"+id2+"</h5>");
	
	row5.put("id", "RefSeq");
	row5.put("value", "RefSeq");
	row5.put("group", id2);
	row6.put("id", "RefSeq Locus Tag");
	row6.put("value", "RefSeq Locus Tag");
	row6.put("group", id2);
	row4.put("id", "Gene ID");
	row4.put("value", "Gene ID");
	row4.put("group", id2);
	row3.put("id", "GI");
	row3.put("value", "GI");
	row3.put("group", id2);
	
	row7.put("id", "Other Identifiers");
	row7.put("value", "<h5>Other Identifiers</h5>");
	
	id_types.add(0, row0);
	id_types.add(1, row1);	
	id_types.add(2, row);
	id_types.add(3, row2);
	id_types.add(4, row21);
	id_types.add(5, row3);
	id_types.add(6, row4);
	id_types.add(7, row5);
	id_types.add(8, row6);	
	id_types.add(9, row7);
	
	json.put("id_types",id_types);
		
	out.println(json.toString());
%>