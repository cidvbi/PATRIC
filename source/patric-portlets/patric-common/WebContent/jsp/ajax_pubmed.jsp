<%@ page import="javax.xml.parsers.*"%><%@ page import="org.w3c.dom.*"%>
<%
	String pubmedId = request.getParameter("pubmedId");
	String retType = request.getParameter("rettype");

	String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml";
	String returnString = "";

	if (pubmedId != null && retType != null) {

		String pageUrl = baseUrl + "&id=" + pubmedId;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(pageUrl);

		if (retType.equalsIgnoreCase("abstract")) {
			try {
				returnString = doc.getElementsByTagName("AbstractText").item(0).getTextContent();
				if (returnString.equalsIgnoreCase("undefined") || returnString.equals("")) {
					returnString = "N/A";
				}
			}
			catch (NullPointerException nex) {
				returnString = "N/A";
			}
		}
		else if (retType.equalsIgnoreCase("doi")) {
		}
	}
%>
<%=returnString%>
