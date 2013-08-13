<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.vt.vbi.patric.common.FASTAHelper" %>
<%@ page import="edu.vt.vbi.patric.common.SQLHelper" %>
<%@ page import="edu.vt.vbi.patric.common.StringHelper" %>
<%@ page import="edu.vt.vbi.patric.dao.*" %>
<%
	String _filename = "sequence.fasta";
	ArrayList<ResultType> _tbl_source = null;
	ArrayList<String> _fasta_feature_ids = new ArrayList<String>();
	
	// getting common params
	String _fasta_action = request.getParameter("fastaaction"); // download or display
	String _fasta_type 	= request.getParameter("fastatype"); // DNA(NA), Protein (AA) or both (ALL)
	String _fasta_scope = request.getParameter("fastascope"); // all features (ALL) or selected (SEL)
	String _tablesource = request.getParameter("tablesource");

		
	if (_fasta_action == null || _fasta_type == null || _fasta_scope == null) {
		System.out.println("Error");
		_fasta_action = null;
	}
	if (_fasta_type.equalsIgnoreCase("NA") || _fasta_type.equalsIgnoreCase("AA") || _fasta_type.equalsIgnoreCase("ALL")) 
	{
		
		String fids = request.getParameter("fids");
		//System.out.print(fids);
		
		if (fids!=null && fids.contains(",")) {
			String[] arr_fids = fids.split(",");
			_fasta_feature_ids.addAll(Arrays.asList(arr_fids));
		}
		else if (fids!=null && !fids.equalsIgnoreCase("")) {
			_fasta_feature_ids.add(fids);
		}
	}
	
	if (_fasta_action == null) {
	}
	else if (_fasta_action.equalsIgnoreCase("download")) 
	{
		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\""+_filename+"\"");
		
		StringBuilder output = new StringBuilder();
		
		// print contents
		Iterator<String> itr = _fasta_feature_ids.iterator();
		String _fid = "";
		while (itr.hasNext()) {
			_fid = itr.next();
			if (_fasta_type.equalsIgnoreCase("NA")) {
				output.append(FASTAHelper.getFASTANASequence(_fid));
			}
			else if (_fasta_type.equalsIgnoreCase("AA")) {
				output.append(FASTAHelper.getFASTAAASequence(_fid));
			}
			else if (_fasta_type.equalsIgnoreCase("ALL")) {
				output.append(FASTAHelper.getFASTANASequence(_fid));
				output.append(FASTAHelper.getFASTAAASequence(_fid));
			}
			output.append("\n");
		}
		out.println(output.toString());
		
	} 
	else if (_fasta_action.equalsIgnoreCase("display")) 
	{
		StringBuilder output = new StringBuilder();
		output.append("<div class=\"fixed-width-font\">\n");
		output.append("<pre>\n");
		try {
			// print contents
			Iterator<String> itr = _fasta_feature_ids.iterator();
			String _fid = "";
			while (itr.hasNext()) {
				_fid = itr.next();
				if (_fasta_type.equalsIgnoreCase("NA")) {
					output.append(FASTAHelper.getFASTANASequence(_fid));
				}
				else if (_fasta_type.equalsIgnoreCase("AA")) {
					output.append(FASTAHelper.getFASTAAASequence(_fid));
				}
				else if (_fasta_type.equalsIgnoreCase("ALL")) {
					output.append(FASTAHelper.getFASTANASequence(_fid));
					output.append("<br/>\n");
					output.append(FASTAHelper.getFASTAAASequence(_fid));
				}
				output.append("<br/>\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			output.append(ex.toString());
		}
		output.append("</pre>");
		output.append("</div>");
		
		%>
		<jsp:include page="/jsp/popup_header.jsp" />
		<%=output.toString() %>
		<jsp:include page="/jsp/popup_footer.jsp" />
		<%
	}
%>