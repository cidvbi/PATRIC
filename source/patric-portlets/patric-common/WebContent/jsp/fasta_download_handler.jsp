<%@ page session="true"%><%@ page import="java.util.*"%><%@ page
	import="edu.vt.vbi.patric.common.FASTAHelper"%><%@ page
	import="edu.vt.vbi.patric.common.SQLHelper"%><%@ page
	import="edu.vt.vbi.patric.common.StringHelper"%><%@ page
	import="edu.vt.vbi.patric.dao.*"%>
<%
	//out.println();

	DBSearch conn_search = new DBSearch();
	DBSummary conn_summary = new DBSummary();

	String _filename = "sequence.fasta";
	ArrayList<ResultType> _tbl_source = null;
	ArrayList<String> _fasta_feature_ids = new ArrayList<String>();

	// getting common params
	String _fasta_action = request.getParameter("fastaaction"); // download or display
	String _fasta_type = request.getParameter("fastatype"); // DNA(NA), Protein (AA) or both (ALL)
	String _fasta_scope = request.getParameter("fastascope"); // all features (ALL) or selected (SEL)
	String _tablesource = request.getParameter("tablesource");
	String fids = request.getParameter("fids");
	// getting sort params

	/*System.out.println("fasta_type:"+_fasta_type);
	System.out.println("fasta_scope:"+_fasta_scope);
	System.out.println("fasta_action:"+_fasta_action);
	System.out.println("fids:"+fids);
	 */

	HashMap<String, String> key = new HashMap<String, String>();
	HashMap<String, String> sort = new HashMap<String, String>();

	if (_fasta_action == null || _fasta_type == null || _fasta_scope == null) {
		System.out.println("Error");
		_fasta_action = null;
	}
	if (_fasta_type.equalsIgnoreCase("dna") || _fasta_type.equalsIgnoreCase("protein")
			|| _fasta_type.equalsIgnoreCase("both")) {
		//System.out.println(_fasta_type);

		if (_fasta_scope.equalsIgnoreCase("Selected")) {

			if (fids != null && fids.contains(",")) {
				String[] arr_fids = fids.split(",");
				_fasta_feature_ids.addAll(Arrays.asList(arr_fids));
			}
			else if (fids != null && !fids.equalsIgnoreCase("")) {
				_fasta_feature_ids.add(fids);
			}
		}
	}

	if (_fasta_action == null) {
	}
	else if (_fasta_action.equalsIgnoreCase("download")) {

		//System.out.println(_fasta_action);

		response.setContentType("application/octetstream");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + _filename + "\"");

		StringBuilder output = new StringBuilder();

		// print contents
		Iterator<String> itr = _fasta_feature_ids.iterator();
		String _fid = "";
		while (itr.hasNext()) {
			_fid = itr.next();
			if (_fasta_type.equalsIgnoreCase("dna")) {
				output.append(FASTAHelper.getFASTANASequence(_fid));
			}
			else if (_fasta_type.equalsIgnoreCase("protein")) {
				output.append(FASTAHelper.getFASTAAASequence(_fid));
			}
			else if (_fasta_type.equalsIgnoreCase("both")) {
				output.append(FASTAHelper.getFASTANASequence(_fid));
				output.append(FASTAHelper.getFASTAAASequence(_fid));
			}
			output.append("\n");
		}
		out.println(output.toString());

	}
	else if (_fasta_action.equalsIgnoreCase("display")) {
		//System.out.println(_fasta_action);
		StringBuilder output = new StringBuilder();
		output.append("<div class=\"fixed-width-font\">\n");
		output.append("<pre>\n");
		try {
			// print contents
			Iterator<String> itr = _fasta_feature_ids.iterator();
			String _fid = "";
			while (itr.hasNext()) {
				_fid = itr.next();
				if (_fasta_type.equalsIgnoreCase("dna")) {
					output.append(FASTAHelper.getFASTANASequence(_fid));
				}
				else if (_fasta_type.equalsIgnoreCase("protein")) {
					output.append(FASTAHelper.getFASTAAASequence(_fid));
				}
				else if (_fasta_type.equalsIgnoreCase("both")) {
					output.append(FASTAHelper.getFASTANASequence(_fid));
					output.append("<br/>\n");
					output.append(FASTAHelper.getFASTAAASequence(_fid));
				}
				output.append("<br/>\n");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			output.append(ex.toString());
		}
		output.append("</pre>");
		output.append("</div>");
%>
<jsp:include page="/jsp/popup_header.jsp" />
<%=output.toString()%>
<jsp:include page="/jsp/popup_footer.jsp" />
<%
	}
%>