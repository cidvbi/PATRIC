<%@ page import="edu.vt.vbi.patric.common.KLEIOInterface" %><%@ page import="org.json.simple.*" %><%@ page import="java.util.*" %><%
	
	HashMap<String, String> colors = new HashMap<String, String>();

	colors.put("DISEASE", "#ccff00:#008000"); //ok
	colors.put("SYMPTOM", "#ccff00:#008000"); //ok
	colors.put("INDICATOR", "#ff0000:#ffffff"); // ok
	colors.put("GENE_OR_PROTEIN", "#00ff00:#003300"); // ok
	colors.put("GENE", "#00ff00:#003300"); // ok
	colors.put("PROTEIN", "#00ff00:#003300");// ok
	colors.put("METABOLITE", "#ffff00:#800000");//ok
	colors.put("BACTERIA", "#ffcc33:#000000"); //ok
	colors.put("ORGAN", "#cccc00:#000000"); //ok
	colors.put("PROCEDURE", "#6600ff:#ffffff"); //ok
	colors.put("PHENOMENON", "#990000:#ffffff"); //ok
	
	KLEIOInterface api = new KLEIOInterface();
	
	JSONObject document = null;
	JSONArray entity = null;
	StringBuffer html = new StringBuffer("");
	StringBuffer test = new StringBuffer("");

	try {

		String id = request.getParameter("pubmedId");
		document = api.getDocument(id);
		entity = (JSONArray)api.getNamedEntities(id).get("result");
		
		html = new StringBuffer(document.get("abstract").toString());
		
		StringBuffer z = new StringBuffer();
		
		for(int i=0; i<html.length(); i++)
		{
			char c = html.charAt(i);
			if(c > 127 || c=='"' || c=='<' || c=='>') {
				z.append("&#"+(int)c+";");
			} else {
				z.append(c);
			}
		}
		
		html = z;
		
		int counter = 0;
		
		HashMap<String, String> coordinate_map = new HashMap<String, String>();
		
		for(int i=0; i<entity.size(); i++){
			
			JSONObject obj = (JSONObject)entity.get(i);
			
			if(obj.get("location").toString().equalsIgnoreCase("AbstractText")){
				
				int begin = Integer.parseInt(obj.get("begin").toString());
				int end = Integer.parseInt(obj.get("end").toString());
				String replacedtext = obj.get("surfaceForm").toString();
				
				String background="#6600ff", font="#ffffff";
				
				if(obj.get("namedEntity") != null){
					
					Object namedEntity = obj.get("namedEntity");
					
					if(colors.get(namedEntity) != null)
					{
						background = colors.get(namedEntity).split(":")[0];
						font = colors.get(namedEntity).split(":")[1];
					}
					
					if(namedEntity == (Object)"PROCEDURE"
							|| namedEntity == (Object)"DIAG_PROC"
							|| namedEntity == (Object)"THERAPEUTIC_PROC"){
					
						background = colors.get("PR0CEDURE").split(":")[0];
						font = colors.get("PR0CEDURE").split(":")[1];
						
					}
					
					if(namedEntity != "PHENOMENON"
							&& namedEntity != "GENERAL_PHENOM"
							&& namedEntity != "HUMAN_PHENOM"
							&& namedEntity != "NATURAL_PHENOM"){
						int original_index = 0;
						
						if(begin+(counter*70) > 0)
							original_index = html.indexOf(replacedtext, (begin+(counter*70) - 10));
						
						if(!coordinate_map.containsKey(Integer.toString(begin)) && original_index != -1){
							begin = original_index;
							end = replacedtext.length() + begin;

							html = html.replace(begin, end, "<span style=\"background: "+background+"\"><font color=\""+font+"\">" +html.subSequence(begin, end)+"</font></span>");
							counter++;
							coordinate_map.put(Integer.toString(begin), "");
							
						}
					}
				}
			}
		}
	} catch (NullPointerException nex) {
	}

	out.println(html);
%>