<%@ page session="true" 
%><%@ page import="edu.vt.vbi.patric.common.ImageBuilder" 
%><%@ page import="java.awt.Color"
%><%@ page import="java.io.OutputStream"
%><%@ page import="javax.servlet.ServletOutputStream"
%><%
	int i = 0, j = 0, k=0, left=0, top=0, width=0, height=0, flag=0;
	String html="", color="";
	
	// getting common params
	String map_id = request.getParameter("map"); 
	String filename = map_id + "patric.png";

	String div = request.getParameter("mapdiv");

	String[] div_split = div.split("AA"); 
	
	ImageBuilder img = new ImageBuilder(map_id);
	
	response.setContentType("application/octetstream");
	response.setHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
	response.setHeader("Cache-Control", "cache");
	
	for(i = 0; i < div_split.length - 1; i++ ){
		
		String[] div_split_split = div_split[i].split(";");
		flag = 0;
		for(j=0; j<div_split_split.length; j++){
			
			String[] insider = div_split_split[j].split(":");
			
			
			for(k=0; k<insider.length-1; k++){
				
			//	System.out.println(insider[k].trim());
				insider[k] = insider[k].trim();
				
				if(insider[k].equals("left")){

					left = Integer.parseInt(insider[k+1].trim());
					
				}
				if(insider[k].equals("top")){
					
					top = Integer.parseInt(insider[k+1].trim());
					
				}
				if(insider[k].equals("width")){
										
					width = Integer.parseInt(insider[k+1].trim());
	
				}
				if(insider[k].equals("height")){
					
					height = Integer.parseInt(insider[k+1].trim());
					
				}
				if(insider[k].equals("color")){
					
					color = insider[k+1].trim();
					
				}
				if(insider[k].equals("innerHTML")){
					
					flag = 1;
					
					html = insider[k+1].trim();
					
				}
			
			}
			
			
		}
		if (flag == 1){
			color = "0,0,0";
			img.drawonImage("text", html, left, top, -1, -1, color);
		}else {
			img.drawonImage("fill", "", left, top, height, width, color);
		}
	
	}
	
	byte[] bytes = img.getByteArray();
	
	response.setContentLength(bytes.length);

	ServletOutputStream outs = response.getOutputStream();

	outs.write(bytes);

	outs.flush();

%>