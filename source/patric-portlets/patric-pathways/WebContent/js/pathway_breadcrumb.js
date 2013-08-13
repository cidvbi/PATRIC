function writeBreadCrumb(){
	
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		obj = {taxonId:"", genomeId:"", need:hash.aT, map:hash.pId};
	
	if(Ext.getDom("genomeId")){
		if(Ext.getDom("genomeId").value != ""){
			obj.genomeId = Ext.getDom("genomeId").value;
			obj.taxonId = "";
		}else{
			obj.genomeId = "";
			obj.taxonId = Ext.getDom("taxonId").value;;
		}
	}else{
		if(Ext.getDom("cType") && Ext.getDom("cType").value == "taxon")
			obj.taxonId = Ext.getDom("cId").value;
		else if(Ext.getDom("cType"))
			obj.genomeId = Ext.getDom("cId").value;		
	}
		
		
	for(var i in hash)
		if(hash.hasOwnProperty(i))
			obj[i] = hash[i];
	
	if(property.pageType == "Finder")
		obj["search_on"] = Ext.getDom("search_on").value,
		obj["keyword"] = Ext.getDom("keyword").value;
	else
		obj["cType"] = Ext.getDom("cType").value; 
		
	var summary = Ext.getDom('grid_result_summary'),
		url = "/patric-pathways/jsp/get_breadcrumb_data.json.jsp";
	
	if(hash.aT == 0){
		
		Ext.Ajax.request({
		    url: url,
		    method: 'GET',
		    params: {val:Ext.JSON.encode(obj)},
		    success: function(response, opts) {
		    	summary.innerHTML = "<b> " + response.responseText +" unique pathway(s) found</b><br/>";		    
			}
		});
		
	}else if (hash.aT == 1){
		
		Ext.Ajax.request({
		    url: url,
		    method: 'GET',
		    params: {val:Ext.JSON.encode(obj)},
		    success: function(response1, opts1) {
				if(!hash.cwP){
					obj.need = 0;
					Ext.Ajax.request({
					    url: "/patric-pathways/jsp/get_breadcrumb_data.json.jsp",
					    method: 'GET',
					    params: {val:Ext.JSON.encode(obj)},
					    success: function(response, opts) {
									summary.innerHTML = "<b>"+ response1.responseText +" unique EC Number(s) found in" + response.responseText +"pathway(s) </b><br/>";
					    }
					});	
				}else{
					summary.innerHTML = "<b>"+response1.responseText+" unique EC Number(s) found in "+(property.pageType == "Finder"?property.breadcrumbParams.pName:Ext.getCmp("cb_pId").getRawValue())+" pathway</b><br/>";
				}
		   }
		});
			
	}else if(hash.aT = 2){
			
		Ext.Ajax.request({
		    url: url,
		    method: 'GET',
		    params: {val:Ext.JSON.encode(obj)},					
		    success: function(response1, opts1) {
				if(!hash.cwP){
					if(!hash.cwEC){
						obj.need = 0;
						Ext.Ajax.request({
						    url: url,
						    method: 'GET',
						    params: {val:Ext.JSON.encode(obj)},
						    success: function(response, opts) {
								summary.innerHTML = "<b>"+response1.responseText+" unique gene(s) found in"+response.responseText+" pathway(s)</b><br/>";						
							}
						});
					}else{
						summary.innerHTML = "<b>"+response1.responseText+" unique gene(s) found for "+(property.pageType == "Finder"?property.breadcrumbParams.ecN:Ext.getCmp("cb_ecN").getRawValue())+"</b><br/>";
					}
				}else{
					summary.innerHTML = "<b>"+response1.responseText+" unique gene(s) found in "+(property.pageType == "Finder"?property.breadcrumbParams.pName:Ext.getCmp("cb_pId").getRawValue())+" pathway</b><br/>";
				}
		    }
		});
	}
}