<%@ page import="java.util.*" 
%><%@ page import="edu.vt.vbi.patric.dao.*" 
%><%@ page import="org.json.simple.*" 
%><%@ page import="edu.vt.vbi.patric.common.PDBInterface" 
%><%@ page import="edu.vt.vbi.patric.common.xmlHandler.*" 
%><%

	PDBInterface api = new PDBInterface();
	String _mode = request.getParameter("mode");
	String _pdb_id = request.getParameter("pdb_id");
	String _chain_id = request.getParameter("chain_id");

	ArrayList<HashMap<String,String>> items = null;
	HashMap<String,String> item = null;
	JSONObject json = new JSONObject();

	//result = api.getGOTerms("4hhb");
	if (_mode !=null && _pdb_id !=null) {
		if (_mode.equals("ligands")) {
			items = api.getLigands(_pdb_id);
			try {
				json.put("totalCount", items.size());
				JSONArray results = new JSONArray();

				for (int i=0; i<items.size(); i++) {
					JSONObject obj = new JSONObject();
					obj.putAll(items.get(i));
					results.add(obj);
				}
				json.put("results",results);
			}
			catch (NullPointerException nex) {
				json.put("totalCount",0);
				json.put("results","[]");
			}
			out.println(json.toString());
		}
		else if (_mode.equals("chains")) {
			ArrayList<String> list = api.getPolymers(_pdb_id);
			
			try {
				json.put("totalCount", list.size());
				JSONArray results = new JSONArray();

				for (int i=0; i<list.size(); i++) {
					JSONObject obj = new JSONObject();
					obj.put("chainID", list.get(i));
					results.add(obj);
				}
				json.put("results",results);
			}
			catch (NullPointerException nex) {
				json.put("totalCount",0);
				json.put("results","[]");
			}
			out.println(json.toString());
		} 
		else if (_mode.equals("sequencecluster") && _chain_id != null) {
			items = api.getSequenceCluster(_pdb_id+"."+_chain_id, 40);
			try {
				json.put("totalCount", items.size());
				JSONArray results = new JSONArray();

				for (int i=0; i<items.size(); i++) {
					JSONObject obj = new JSONObject();
					obj.putAll(items.get(i));
					obj.put("pdbChain", obj.get("name"));
					results.add(obj);
				}
				json.put("results",results);
			}
			catch (NullPointerException nex) {
				json.put("totalCount",0);
				json.put("results","[]");
			}
			out.println(json.toString());
		}
		else if (_mode.equals("annotations")) {
			String p = _pdb_id;
			if (_chain_id != null) {
				p += "."+_chain_id;
			}
			items = api.getAnnotations(p);
			try {
				json.put("totalCount", items.size());
				JSONArray results = new JSONArray();

				for (int i=0; i<items.size(); i++) {
					JSONObject obj = new JSONObject();
					obj.putAll(items.get(i));
					results.add(obj);
				}
				json.put("results",results);
			}
			catch (NullPointerException nex) {
				json.put("totalCount",0);
				json.put("results","[]");
			}
			out.println(json.toString());
		}
	}
%>