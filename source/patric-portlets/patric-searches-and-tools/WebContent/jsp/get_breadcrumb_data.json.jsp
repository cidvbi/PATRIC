<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSearch" %>
<%

	HashMap<String, String> key = new HashMap<String, String>();
	
	String need = request.getParameter("need");
	
	DBSearch conn_search = new DBSearch();

	if(need.equals("ec")){
		
		String search_on = request.getParameter("search_on");
		String keyword = request.getParameter("keyword");
		String taxonId = request.getParameter("taxonId");
		String algorithm = request.getParameter("algorithm");
		String genomeId = request.getParameter("genomeId");
		
		key.put("keyword", keyword.trim());
		
		if (search_on!=null) {
			key.put("search_on",search_on.trim());
		}

		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			key.put("genomeId", genomeId);
		}else if (taxonId!=null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId",taxonId);
			}

		if (algorithm != null) {
			key.put("algorithm", algorithm);
		}
				
		try {

			int distinct_items = conn_search.getDistinctECFinderBreadCrumb(key);
			out.println(distinct_items);
			
		} catch (NullPointerException nex) {
		}
		
	}else if(need.equals("feature_ec")){
		
		String search_on = request.getParameter("search_on");
		String keyword = request.getParameter("keyword");
		String taxonId = request.getParameter("taxonId");
		String algorithm = request.getParameter("algorithm");
		String ec_number = request.getParameter("ec_number");
		String genomeId = request.getParameter("genomeId");
		
		key.put("search_on",search_on.trim());

		key.put("keyword",keyword.trim());
		
		
		if(!algorithm.equals("") && algorithm != null){
			
			//System.out.print("algorithm " + algorithm);
			
			key.put("algorithm", algorithm);
			
		}
		
		if(!ec_number.equals("") && ec_number != null){
			
			//System.out.print("ec_number " + ec_number);

			key.put("ec_number", ec_number);
			
		}
		
		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			key.put("genomeId", genomeId);
		}else if (taxonId!=null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId",taxonId);
			}
				
		try {

			int distinct_items = conn_search.getDistinctECFeatureFinderBreadCrumb(key);
			out.println(distinct_items);
			
			
		} catch (NullPointerException nex) {
		}
		
	}else if(need.equals("go")){
		
		String search_on = request.getParameter("search_on");
		String keyword = request.getParameter("keyword");
		String taxonId = request.getParameter("taxonId");
		String algorithm = request.getParameter("algorithm");
		String genomeId = request.getParameter("genomeId");
		
		key.put("keyword", keyword.trim());
		
		if (search_on!=null) {
			key.put("search_on",search_on.trim());
		}

		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			key.put("genomeId", genomeId);
		}else if (taxonId!=null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId",taxonId);
			}

		if (algorithm != null) {
			key.put("algorithm", algorithm);
		}
				
		try {

			int distinct_items = conn_search.getDistinctGOFinderBreadCrumb(key);
			out.println(distinct_items);
			
		} catch (NullPointerException nex) {
		}
		
	}else if(need.equals("feature_go")){
		
		String search_on = request.getParameter("search_on");
		String keyword = request.getParameter("keyword");
		String taxonId = request.getParameter("taxonId");
		String algorithm = request.getParameter("algorithm");
		String goId = request.getParameter("goId");
		String genomeId = request.getParameter("genomeId");
		
		key.put("search_on",search_on.trim());

		key.put("keyword",keyword.trim());
		
		
		if(!algorithm.equals("") && algorithm != null){
						
			key.put("algorithm", algorithm);
			
		}
		
		if(!goId.equals("") && goId != null){
			
			key.put("go_id", goId);
			
		}
		
		if (genomeId != null && !genomeId.equalsIgnoreCase("")) {
			key.put("genomeId", genomeId);
		}else if (taxonId!=null && !taxonId.equalsIgnoreCase("")) {
				key.put("taxonId",taxonId);
			}
				
		try {

			int distinct_items = conn_search.getDistinctGOFeatureFinderBreadCrumb(key);
			out.println(distinct_items);
			
			
		} catch (NullPointerException nex) {
		}
		
	}

	
%>