<%@ page import="java.util.*"%><%@ page import="edu.vt.vbi.patric.dao.*"%><%@ page
	import="org.json.simple.*"%><%@ page
	import="edu.vt.vbi.patric.common.OrganismTreeBuilder"%>
<%
	DBSummary conn_summary = new DBSummary();
	String _mode = request.getParameter("mode");
	String _query = request.getParameter("query");
	String _start = request.getParameter("start");
	String _searchon = request.getParameter("searchon");

	HashMap<String, String> key = new HashMap<String, String>();
	key.put("ncbi_taxon_id", _start);

	ArrayList<ResultType> items = null;
	JSONObject json = new JSONObject();

	response.setContentType("application/json");
	if (_mode.equals("search")) {
		if (_query != null && _query.length() >= 4) {
			key.put("keyword", _query);
			if (_searchon != null && _searchon.equals("txtree")) {
				items = conn_summary.getTaxonomyTreeForGenomeSelector(key);
			}
			else {
				items = conn_summary.getGenomeListForGenomeSelector(key);
			}
		}
		try {
			json.put("totalCount", items.size());
			JSONArray results = new JSONArray();

			for (int i = 0; i < items.size(); i++) {
				ResultType g = (ResultType) items.get(i);
				JSONObject obj = new JSONObject();
				obj.putAll(g);
				if (_searchon.equals("txtree")) {
					obj.put("display_name", obj.get("class_name"));
				}
				else {
					obj.put("display_name", obj.get("genome_name"));
				}
				results.add(obj);
			}
			json.put("genomeList", results);
		}
		catch (NullPointerException nex) {
			json.put("totalCount", 0);
			json.put("genomeList", "[]");
		}
		json.put("keyword", _query);
		out.println(json.toString());
	}
	else if (_mode.equals("txtree")) {

		JSONArray list = OrganismTreeBuilder.buildGenomeTree(key);
		out.println(list.toString());

	}
	else if (_mode.equals("azlist")) {

		JSONArray list = OrganismTreeBuilder.buildGenomeList(key);
		out.println(list.toString());
	}
	else if (_mode.equals("tgm")) {
		JSONArray list = OrganismTreeBuilder.buildTaxonGenomeMapping(key);
		out.println(list.toString());
	}
	else {

	}
%>