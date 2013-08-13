<%@ page import="edu.vt.vbi.patric.common.KLEIOInterface" %><%@ page import="org.json.simple.*" %><%@ page import="java.util.*" %><%

	
	HashMap<String, String> colors = new HashMap<String, String>();

	/*colors.put("PUBLICATIONTYPE", "#ffffff");
	colors.put("MESHHEADING", "#ffffff");
	colors.put("DRUG", "#ffffff");*/
	colors.put("DISEASE", "#ccff00:#008000"); //ok
	colors.put("SYMPTOM", "#ccff00:#008000"); //ok
	/*colors.put("DIAG_PROC", "#ffffff");
	colors.put("THERAPEUTIC_PROC", "#ffffff");
	colors.put("GENERAL_PHENOM", "#ffffff");
	colors.put("HUMAN_PHENOM", "#ffffff");
	colors.put("NATURAL_PHENOM", "#ffffff");*/
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
	
		// System.out.println(entity.toString());
		
		html = new StringBuffer(document.get("abstract").toString());
		
		StringBuffer z = new StringBuffer();
		
	    for(int i=0; i<html.length(); i++)
	    {
	        char c = html.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           z.append("&#"+(int)c+";");
	        }
	        else
	        {
	            z.append(c);
	        }
	    }
	    
	    html = z;
	    
		int counter = 0;
		
		HashMap<String, String> coordinate_map = new HashMap<String, String>();
		
		for(int i=0; i<entity.size(); i++){
			
			JSONObject obj = (JSONObject)entity.get(i);			
			
			if(obj.get("location").toString().equalsIgnoreCase("AbstractText")){
				
				// System.out.print("/////////////////////");
				
				int begin = Integer.parseInt(obj.get("begin").toString());
				int end = Integer.parseInt(obj.get("end").toString());
				String replacedtext = obj.get("surfaceForm").toString();
				
				String background="#6600ff", font="#ffffff";
													
				//System.out.print("replacedtext "+replacedtext);
				//System.out.print("begin "+begin);
				//System.out.print("end "+end);
				
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
						
						
						
						/*
						System.out.println(counter);
						System.out.println((begin+(counter*70))+"-"+ (end+(counter*70)));
						System.out.println(html.subSequence(begin+(counter*70), end+(counter*70)));
						*/
						int original_index = 0;
						
						if(begin+(counter*70) > 0)
							original_index = html.indexOf(replacedtext, (begin+(counter*70) - 10));
						
						
						if(!coordinate_map.containsKey(Integer.toString(begin)) && original_index != -1){
						
							//System.out.print("----------------");
							
							//System.out.print("original_index "+original_index);
							//System.out.print("begin+(counter*70) "+(begin+(counter*70)-10));
							
							begin = original_index;
							end = replacedtext.length() + begin;
							//System.out.print("newbegin "+begin);
							//System.out.print("newend "+end);
							
							html = html.replace(begin, end, "<span style=\"background: "+background+"\"><font color=\""+font+"\">" +html.subSequence(begin, end)+"</font></span>");
							counter++;
							coordinate_map.put(Integer.toString(begin), "");
							
						}
						
						
					}
				}
				
				/*System.out.println(background);
				System.out.println(font);*/
				
			}
			
		}
		
		
		
	} catch (NullPointerException nex) {
	}

	out.println(html);


%>