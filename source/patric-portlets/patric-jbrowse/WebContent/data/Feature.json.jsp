<%@ page import="java.util.*"
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary"
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%@ page import="edu.vt.vbi.patric.beans.DNAFeature"
%><%
String _accession = request.getParameter("accession");
String _algorithm = request.getParameter("algorithm");

String _accn = null;
String _sid = null;

if (_accession!=null && _algorithm!=null) {
	
	String[] names = _accession.split("\\|");
	if (names[3] != null) {
		_accn = names[3];
	}
	if (names[1] != null) {
		_sid = names[1];
	}
	//System.out.println(_accession+","+_accn+","+_sid);
	
	HashMap<String,String> key = new HashMap<String,String>();
	key.put("accession",_accn);
	key.put("algorithm",_algorithm);
	key.put("sid", _sid);

	DBSummary conn_summary = new DBSummary();
	//ArrayList<ResultType> features = conn_summary.getFeatures(key);
	List<DNAFeature> features = conn_summary.getDNAFeatures(key);
	
	ArrayList<Integer> hist = conn_summary.getHistogram(key);
	//calculate avg
	int hist_sum = 0;
	double hist_avg = 0;
	for (int i=0; i< hist.size(); i++) {
		hist_sum += hist.get(0);
	}
	if (hist.size() > 0) {
		hist_avg = hist_sum / hist.size();
	}
	//
	StringBuilder nclist = new StringBuilder();
	Formatter formatter = new Formatter(nclist, Locale.US);
	
	int features_count = features.size();

	for (int i=0; i<features_count; i++) {
		if (nclist.length()>0) {
			nclist.append(",");
		}
		DNAFeature f = features.get(i);
		//System.out.println(f.start+","+f.end+","+f.strand+","+f.na_feature_id+","+f.locus_tag+","+f.feature_type+","+f.product);
		formatter.format("[0, %d, %d, %d, %d, \"%s\", %d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %d]",
			(f.start-1),
			f.start,
			f.end,
			(f.strand.equals("+")?1:-1),
			f.strand,

			f.na_feature_id,
			f.locus_tag,
			_algorithm,
			f.feature_type,
			(f.product != null)?f.product.replace("\\","").replace("\"", "\\\""):"",

			f.gene,
			f.refseq_locus_tag,
			(f.feature_type.equals("CDS")?0:(f.feature_type.contains("RNA")?1:2))
		);
		/*
		formatter.format("[0, %d, %s, %s, %d, \"%s\", %s, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %d]",
			(Integer.parseInt(features.get(i).get("start_max"))-1),
			features.get(i).get("start_max"),
			features.get(i).get("end_min"),
			(features.get(i).get("strand").equals("+")?1:-1),
			features.get(i).get("strand"),

			features.get(i).get("na_feature_id"),
			features.get(i).get("locus_tag"),
			_algorithm,
			features.get(i).get("feature_type"),
			features.get(i).get("product").replace("\\","").replace("\"", "\\\""),

			features.get(i).get("gene"),
			features.get(i).get("refseq_locus_tag"),
			(features.get(i).get("feature_type").equals("CDS")?0:(features.get(i).get("feature_type").contains("RNA")?1:2))
		);
		*/
	}
	response.setContentType("application/json");
%>
{
	"featureCount": <%=features_count %>,
	"formatVersion": 1,
	"histograms": {
		"meta": [{
			"arrayParams": {
				"chunkSize": 10000,
				"length": <%=hist.size()%>,
				"urlTemplate": "Hist.json.jsp?accession=<%=_accession%>&algorithm=<%=_algorithm%>&chunk={Chunk}&format=.json"
			},
			"basesPerBin": "10000"
		}],
		"stats": [{
			"basesPerBin": "10000",
			"max": <%=(hist.isEmpty())?"0":Collections.max(hist)%>,
			"mean": <%=hist_avg%>
		}]
	},
	"intervals": {
		"classes": [{
			"attributes": [
				"Start", "Start_str", "End", "Strand", "strand_str",
				"id", "locus_tag", "source", "type", "product",
				"gene", "refseq", "phase"
			],
			"isArrayAttr": {}
		}],
		"count": <%=features_count %>,
		"lazyClass": 5,
		"maxEnd": 20000,
		"minStart": 1,
		"nclist": [<%=nclist.toString() %>],
		"urlTemplate": "lf-{Chunk}.json"
	}
}
<%	} else {	%>
	wrong parameters!
<%	}	%>