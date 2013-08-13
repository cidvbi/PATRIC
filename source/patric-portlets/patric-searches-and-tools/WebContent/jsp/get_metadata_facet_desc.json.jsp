<%@ page import="java.util.*" 
%><%
	HashMap<String, String> descriptions = new HashMap<String, String>();
	
	//String start = "<h2 class=\"section-title close2x\"><span class=\"wrap\">New</span></h2>";
	String start = "";
	String end = "";

	String a = start
		+ "<p class=\"largest far\">Find Genomes by Specifying Descriptive Search Summary Terms</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span>&nbsp;<span>Escherichia coli</span><br/><span>Escherichia coli USA 2006</span><br/><span>Escherichia \"Homo sapiens\"</span></p>"
		+ end;
		
	descriptions.put("Keyword", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes by specifying country from which the bacterial DNA sample was isolated.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>USA</span><br/><span>USA or Brazil or China</span></p>"
		+ end;
	
	descriptions.put("isolation_country", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes by specifying genome completion status.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>Complete</span>, <span>WGS</span>, <span>Plasmid</span></p>"
		+ end;
	
	descriptions.put("genome_status", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes that have been isolated from a specific host.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>\"Homo sapiens\"</span><br/><span>pig or swine or sus</span></p>"
		+ end;
	
	descriptions.put("host_name", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes associated with a specific disease.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>Tuberculosis</span><br/><span>Gastroenteritis or \"gas gangrene\"</span></p>"
		+ end;
	
	descriptions.put("disease", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes by the year in which the bacterial sample was collected.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>2006</span><br/><span>2010 or 2009</span></p>"
		+ end;
	
	descriptions.put("collection_date", a);
	
	a = start
		+ "<p class=\"largest far\">Find genomes by the year in which sequencing was completed and/or the genome sequence was released.</p>"
		+ "<p class=\"close2x\"><span class=\"bold\">Examples:</span><span>2011</span><br/><span>2010 or 2009</span></p>"
		+ end;
	
	descriptions.put("completion_date", a);

	String search_on = request.getParameter("search_on");

	out.println(descriptions.get(search_on).toString());
%>