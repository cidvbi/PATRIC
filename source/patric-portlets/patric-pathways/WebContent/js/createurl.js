function createURL(name){

	var url = "";
	
	if(name == "pathway_table" || name == "grid_pathway_single"){
		
		if(Ext.getDom("key") != null)
			url = url + "key=" + Ext.getDom("key").value;
		
		if(Ext.getDom("pS") != null)
			url = url + "&pS=" + Ext.getDom("pS").value;
		
		if(Ext.getDom("aP") != null)
			url = url + "&aP=" + Ext.getDom("aP").value;
		
		if(Ext.getDom("dir") != null)
			url = url + "&dir=" + Ext.getDom("dir").value;
		
		if(Ext.getDom("sort") != null)
			url = url + "&sort=" + Ext.getDom("sort").value;
		
	}else{
	
		if(Ext.getDom("key") != null)
			url = url + "key=" + Ext.getDom("key").value;
		
		if(Ext.getDom("pS") != null)
			url = url + "&pS=" + Ext.getDom("pS").value;
		
		if(Ext.getDom("aP") != null)
			url = url + "&aP=" + Ext.getDom("aP").value;
		
		if(Ext.getDom("aT") != null)
			url = url + "&aT=" + Ext.getDom("aT").value;
		
		if(Ext.getDom("cwP") != null)
			url = url + "&cwP=" + Ext.getDom("cwP").value;
		
		if(Ext.getDom("cwEc") != null)
			url = url + "&cwEc=" + Ext.getDom("cwEc").value;
		
		if(Ext.getDom("cF") != null)
			url = url + "&cF=" + Ext.getDom("cF").value ;
			
		if(Ext.getDom("pId") != null)
			url = url + "&pId=" + Ext.getDom("pId").value;
		
		if(Ext.getDom("pName") != null)
			url = url + "&pName=" + Ext.getDom("pName").value;
		
		if(Ext.getDom("ecN") != null )
			url = url + "&ecN=" + Ext.getDom("ecN").value;
		
		if(Ext.getDom("alg") != null)
			url = url + "&alg=" + Ext.getDom("alg").value;
				
		if(Ext.getDom("pClass") != null)
			url = url + "&pClass=" + Ext.getDom("pClass").value;
		
		if(Ext.getDom("psort") != null)
			url = url + "&psort=" + Ext.getDom("psort").value;
		
		if(Ext.getDom("pdir").value != null)
			url = url + "&pdir=" + Ext.getDom("pdir").value;
		
		if(Ext.getDom("esort").value != null)
			url = url + "&esort=" + Ext.getDom("esort").value;
		
		if(Ext.getDom("edir").value != null)
			url = url + "&edir=" + Ext.getDom("edir").value;
		
		if(Ext.getDom("fsort") != null)
			url = url + "&fsort=" + Ext.getDom("fsort").value;
		
		if(Ext.getDom("fdir") != null)
			url = url + "&fdir=" + Ext.getDom("fdir").value;
		
	}
	
	window.location.hash = url;

}