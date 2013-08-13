<%@ page session="true" %>
<%@ page import="edu.vt.vbi.patric.common.CreateZip" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="edu.vt.vbi.patric.dao.DBSearch" %>
<%@ page import="edu.vt.vbi.patric.dao.ResultType" %>
<%@ page import="java.util.*" %>
<%

	HashMap<String, String> key = new HashMap<String,String>();

	// getting common params
	String genomeId = request.getParameter("genomeId");
	String taxonId = request.getParameter("taxonId");
	String filetype = request.getParameter("finalfiletype");
	String algorithm = request.getParameter("finalalgorithm");
	
	System.out.print(genomeId);
	System.out.print(taxonId);
	System.out.print(filetype);
	System.out.print(algorithm);
	
	key.put("genomeId", genomeId);
	key.put("taxonId", taxonId);

	ArrayList<ResultType> items;
	Object obj = null;
	DBSearch conn_search = new DBSearch();

	try {
		items = conn_search.getGenomeNames(key);
		CreateZip zip = new CreateZip();
		byte[] bytes = zip.ZipIt(items, algorithm.split(","), filetype.split(","));
		System.out.println(bytes.length);
		if(bytes.length > 0){
			response.setContentType("application/octetstream");
			response.setHeader("Cache-Control", "cache");
			response.setHeader("Content-Disposition", "attachment; filename=\"patric_downloads.zip\"");
			response.setContentLength(bytes.length);
			response.getOutputStream().write(bytes);
		}else{
			out.print("Sorry. The requested file(s) are not available.");
		}
	} catch (NullPointerException nex) {
	}	
%>