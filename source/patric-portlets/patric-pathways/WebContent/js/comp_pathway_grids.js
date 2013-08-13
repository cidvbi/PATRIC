Ext.define('Pathway', {
		extend: 'Ext.data.Model',
		fields: [
			{name:'idx',	type:'string'},
			{name:'pathway_id',	type:'string'},
			{name:'pathway_name',	type:'string'},
			{name:'pathway_class',	type:'string'},
			{name:'genome_count',	type:'string'},
			{name:'ec_count',	type:'string'},
			{name:'gene_count',		type:'string'},
			{name:'ec_cons',	type:'string'},
			{name:'gene_cons',	type:'string'},
			{name:'algorithm',	type:'string'}
		]
	});

Ext.define('Ec', {
	extend: 'Ext.data.Model',
	fields: [
		{name:'idx',	type:'string'},
		{name:'pathway_id',	type:'string'},
		{name:'pathway_name',	type:'string'},
		{name:'pathway_class',	type:'string'},
		{name:'algorithm',	type:'string'},
		{name:'ec_number',	type:'string'},
		{name:'ec_name',		type:'string'},
		{name:'genome_count',	type:'string'},
		{name:'gene_count',	type:'string'},
		{name:'algorithm',	type:'string'}
	]
});

Ext.define('Feature', {
	extend: 'Ext.data.Model',
	fields: [
		{name:'idx',	type:'string'},
		{name:'genome_name',	type:'string'},
		{name:'accession',	type:'string'},
		{name:'locus_tag',	type:'string'},
		{name:'na_feature_id',	type:'string'},
		{name:'algorithm',	type:'string'},
		{name:'gene',		type:'string'},
		{name:'product',	type:'string'},
		{name:'pathway_id',	type:'string'},
		{name:'pathway_name',	type:'string'},
		{name:'ec_number',	type:'string'},
		{name:'ec_name',	type:'string'},
		{name:'genome_info_id',	type:'string'}
	]
});


function createLayout(){
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;
	
	Ext.create('Ext.panel.Panel', {
		 id: 'tabLayout',
		 border: true,
		 autoScroll: false,
		 items:[{
			layout: 'fit',
			region:'north',
			border: false,
			height: 22,
			xtype: 'tabpanel',
			id: 'tabPanel',
			items: [{title:"Pathways", id:"0"},{title:"EC Numbers", id:"1"},{title:"Genes", id:"2"}],
			ClickFromTab: true, 
			listeners: {
				'tabchange': function(tabPanel, tab){
						hash.cwP = (hash.cwP && hash.cwP == "true")?true:false,
						hash.cwEC = (hash.cwEC && hash.cwEC == "true")?true:false;
						if(property.pageType == "Finder")KeepParameters();
						if(!this.ClickFromTab){
			        		loadGrid();
			        		this.ClickFromTab = true;
			        	}else{
			        		hash.aT = parseInt(tab.getId());
			        		hash.aP[tab.getId()] = 1,
				        	createURL();	
			        	}
					}
				}
			},{
				layout: 'fit',
				region:'center',
				id: 'centerPanel',
			 	contentEl: 'grid_result_summary',
				padding: '6 8',
				style: 'background-color:#DDE8F4',
				bodyStyle: 'background-color:#DDE8F4',
				bodyCls: 'bold',
				border: false
			},{
				layout:'fit',
				region:'south',
				id:'southPanel',
				html:'<div id="PATRICGrid"></div>',
				height:571,
				border:false,
				autoScroll:true
			}
		],
		renderTo: 'sample-layout'
	});
}

function createLoadComboBoxes(){
	
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;
	
	Ext.create('Ext.form.ComboBox', {
		id:'cb_pClass',
		renderTo: 'f_pathway_class',
		fieldLabel: 'Pathway Class',
	    displayField: 'name',
	    valueField: 'name',
	    width: 335,
	    labelWidth: 90,
	    editable: false,
	    typeHead: true,
	    store: Ext.create('Ext.data.Store', {
	    	fields:['name', 'value']
	    }),
	    queryMode: 'local',
	    listeners:{
	         'select': function(combo, records, eOpts){
	        	 hash.pClass = records[0].get("value").toLowerCase() == "all"?"":records[0].get("value");
	        	 loadCombo("cb_pId", "pathway");
	        	 loadCombo("cb_ecN", "ec");
	        	 loadCombo("cb_alg", "algorithm");
	         }
    	}
	});
	
	Ext.create('Ext.form.ComboBox', {
		id:'cb_pId',
		renderTo: 'f_pathway_name',
		fieldLabel: 'Pathway Name',
	    displayField: 'name',
	    valueField: 'value',
	    width: 305,
	    labelWidth: 90,
	    editable: false,
	    typeHead: true,
	    store: Ext.create('Ext.data.Store', {
	    	fields:['name', 'value']
	    }),
	    queryMode: 'local',
	    listeners:{
	         'select': function(combo, records, eOpts){
	        	hash.pId = records[0].get("value").toLowerCase() == "all"?"":records[0].get("value");
	        	//loadCombo("cb_pClass", "parent");
	        	loadCombo("cb_ecN", "ec");
	        	loadCombo("cb_alg", "algorithm");
	         }
	    }
	});
	
	Ext.create('Ext.form.ComboBox', {
		id:'cb_ecN',
		renderTo: 'f_ec_number',
		fieldLabel: 'EC Number',
	    displayField: 'name',
	    valueField: 'name',
	    width: 165,
	    labelWidth: 70,
	    editable: false,
	    typeHead: true,
	    store: Ext.create('Ext.data.Store', {
	    	fields:['name', 'value']
	    }),
	    queryMode: 'local',
	    listeners:{
	         'select': function(combo, records, eOpts){
	        	hash.ecN = records[0].get("value").toLowerCase() == "all"?"":records[0].get("value");
	        	/*
	        	loadCombo("cb_pClass", "parent");
	        	loadCombo("cb_pId", "pathway");
	        	*/
	        	loadCombo("cb_alg", "algorithm");
	         }
	    }
	});
	
	Ext.create('Ext.form.ComboBox', {
		id:'cb_alg',
		renderTo: 'f_algorithm',
		fieldLabel: 'Annotation',
	    displayField: 'name',
	    valueField: 'value',
	    width: 175,
	    labelWidth: 60,
	    editable: false,
	    typeHead: true,
	    store: Ext.create('Ext.data.Store', {
	    	fields:['name', 'value'],
	    	data:[{"name":"ALL", "value":"ALL"},
	    	      {"name":"PATRIC", "value":"RAST"},
	    	      {"name":"RefSeq", "value":"RefSeq"},
	    	      {"name":"Legacy BRC", "value":"Curation"}]
	    }),
	    queryMode: 'local',
	    listeners:{
	    	'select': function(combo, records, eOpts){
	    		hash.alg = records[0].get("value").toLowerCase() == "all"?"":records[0].get("value");
	    	}
	    }
	});
	
	
	loadCombo("cb_pClass", "parent");
	loadCombo("cb_pId", "pathway");
	loadCombo("cb_ecN", "ec");
	loadCombo("cb_alg", "algorithm");
	
}

function loadFBCD(){
	var tabs = Ext.getCmp("tabPanel"),
		id = tabs.getActiveTab().getId(),
		Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;
			
	SetLoadParameters();
	
	if(property.pageType == "Table")
		SetComboBoxes();
		
	if(hash.aT == parseInt(id)){
		loadGrid();
	}else{
		tabs.ClickFromTab = false;
		tabs.setActiveTab(hash.aT);
	}
	
	Ext.getDom("grid_result_summary").innerHTML = "Loading...";
}

function getExtraParams(){
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		which = hash.aT;
	
	return (property.pageType == "Table")?{
		cId: Ext.getDom("cId").value,
    	cType: Ext.getDom("cType").value,
    	algorithm: hash.alg,
    	pathway_class: hash.pClass,
    	pathway_id: hash.pId,
    	ec_number: hash.ecN,
    	need: which
	}:{
		pk: property.pk?property.pk:"",
    	algorithm: hash.alg,
    	pathway_id: hash.pId,
    	ec_number: hash.ecN,
    	need: which
	};
	
}

function CallBack(){
	var Page = $Page;
	
	writeBreadCrumb();
	if(Page.getGrid().sortchangeOption)
		Page.getGrid().setSortDirectionColumnHeader();
}

function ShowECTab(pathway_id, pathway_name, pathway_class, algorithm){

	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;
	
	hash.aT = 1,
	hash.aP[1] = 1,
	hash.pId = pathway_id,
	(property.gridType == "Table")?hash.pClass = pathway_class:"",
	hash.alg = algorithm == "PATRIC"?"RAST":algorithm == "Legacy BRC"?"Curation":algorithm,
	hash.cwP = true,
	hash.cwEC = false,
	(property.pageType == "Finder" && Ext.getDom("search_on").value == "Ec_Number")?"":hash.ecN = '',
	(property.pageType == "Finder")?property.breadcrumbParams.pName = pathway_name:"";
	(property.gridType == "Table")?SetComboBoxes():"";
	property.reconfigure = true;
	createURL();
}

function ShowFeatureTab(pathway_id, pathway_name, pathway_class, ec_number, algorithm){

	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;

	hash.aT = 2,
	hash.aP[2] = 1,
	hash.pId = pathway_id,
	(property.gridType == "Table")?hash.pClass = pathway_class:"",
	hash.alg = algorithm == "PATRIC"?"RAST":algorithm == "Legacy BRC"?"Curation":algorithm,
	hash.cwP = (ec_number?false:true),
	hash.cwEC = (ec_number?true:false),
	ec_number?hash.ecN = ec_number:"",
	(property.pageType == "Finder")?property.breadcrumbParams.pName = pathway_name:"",
	(property.pageType == "Finder")?property.breadcrumbParams.ecN = ec_number:"";
	(property.gridType == "Table")?SetComboBoxes():"";
	property.reconfigure = true;
	createURL();
}

function filter() {

	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		alg = Ext.getCmp('cb_alg').getValue(),
		pId = Ext.getCmp('cb_pId').getValue(),
		pClass = Ext.getCmp('cb_pClass').getValue(),
		ecN = Ext.getCmp('cb_ecN').getValue();
	
	hash.aP[hash.aT] = 1,
	hash.cwP = (pId.toLowerCase() == "all")?false:true,
	hash.cwEC = (ecN.toLowerCase() == "all")?false:true,
	hash.alg = (alg.toLowerCase() == "all")?"":alg=="PATRIC"?"RAST":alg == "Legacy BRC"?"Curation":alg,
	hash.pId = (pId.toLowerCase() == "all")?"":pId,
	hash.pClass = (pClass.toLowerCase() == "all")?"":pClass,
	hash.ecN = (ecN.toLowerCase() == "all")?"":ecN,
			
	createURL();
}

function SetComboBoxes(){
	
	var hash = $Page.getPageProperties().hash,
		alg = Ext.getCmp('cb_alg'),
		pId = Ext.getCmp('cb_pId'),
		pClass = Ext.getCmp('cb_pClass'),
		ecN = Ext.getCmp('cb_ecN'),
		timeoutId = null;
	
	function setInputs(){
		if(pId.getStore().data.items.length > 0 && pClass.getStore().data.items.length  > 0 && ecN.getStore().data.items.length > 0){
			(hash.alg == "")?alg.setValue("ALL"):alg.setValue(hash.alg == "RAST"?"PATRIC":hash.alg == "Curation"?"Legacy BRC":hash.alg),
			(hash.pId == "")?pId.setValue("ALL"):pId.setValue(hash.pId),
			(hash.pClass == "")?pClass.setValue("ALL"):pClass.setValue(hash.pClass),
			(hash.ecN == "")?ecN.setValue("ALL"):ecN.setValue(hash.ecN);
			clearTimeout(timeoutId);
		}
	}
	
	if(pId.getStore().data.items.length == 0 && pClass.getStore().data.items.length == 0 && ecN.getStore().data.items.length == 0){
		timeoutId = setInterval(setInputs, 1000);
	}else{
		setInputs();
	}
	
}

function loadCombo(id, need){

	var obj = {},
		hash = $Page.getPageProperties().hash,
		list = {"pId":1, "pClass":1, "ecN":1, "alg":1};
	
	for(var i in hash)
		if(list[i] && hash.hasOwnProperty(i))
			obj[i] = hash[i];
	
	obj["cType"] = Ext.getDom("cType").value;
	obj["cId"] = Ext.getDom("cId").value;
	obj["need"] = need;
	
	Ext.Ajax.request({
	    url: "/patric-pathways/jsp/filter_populate.jsp",
	    method: 'GET',
	    params: {val:Ext.JSON.encode(obj)},
	    success: function(response, opts){
	    	var decoded = Ext.JSON.decode(response.responseText);
	    	decoded[need].sort(sortRowsData(id =="cb_pId"?"name":"value"));
	    	Ext.getCmp(id).getStore().loadData(decoded[need]);
	    }
	});
}

function sortRowsData(value) {
    return function (a, b) {
    	if(a[value] == "ALL")
    		return -1;
    	if(a[value] < b[value])
    		return -1;
    	if(a[value] > b[value])
    		return 1;
    	return 0;
    };
}


function renderAvgECCount(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+record.data.ec_count+'" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"ShowECTab('"+record.data.pathway_id+"','"+record.data.pathway_name+"','"+record.data.pathway_class+"','"+record.data.algorithm+"');\"/>"+record.data.ec_count+"</a>");
		
}

function renderPathwayName(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+record.data.pathway_name+'" data-qclass="x-tip"';
	if(!Ext.getDom("pk"))
		return Ext.String.format('<a href=CompPathwayMap?cType={0}&amp;cId={1}&amp;dm=&amp;feature_info_id=&amp;ec_number=&amp;map={2}&amp;pk={3}&amp;algorithm={4}>{5}</a>', Ext.getDom("cType").value, Ext.getDom("cId").value, record.data.pathway_id, '', record.data.algorithm, record.data.pathway_name);
	else
		return Ext.String.format('<a href=CompPathwayMap?cType=&amp;cId=&amp;dm=&amp;feature_info_id=&amp;ec_number=&amp;map={0}&amp;pk={1}&amp;algorithm={2}>{3}</a>', record.data.pathway_id, Ext.getDom("pk").value, record.data.algorithm, record.data.pathway_name);
	
}

function renderPathwayFeature(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+record.data.pathway_name+'" data-qclass="x-tip"';
	if(!Ext.getDom("pk"))
		return Ext.String.format('<a href=CompPathwayMap?cType={0}&amp;cId={1}&amp;dm=feature&amp;feature_info_id={2}&amp;ec_number=&amp;map={3}&amp;algorithm={4}>{5}</a>', Ext.getDom("cType").value, Ext.getDom("cId").value, record.data.na_feature_id,  record.data.pathway_id, record.data.algorithm, record.data.pathway_name);

	else
		return Ext.String.format('<a href=CompPathwayMap?cType=&amp;cId=&amp;dm=feature&amp;feature_info_id={0}&amp;ec_number=&amp;map={1}&amp;pk={2}&amp;algorithm={3}>{4}</a>', record.data.na_feature_id, record.data.pathway_id,  Ext.getDom("pk").value, record.data.algorithm, record.data.pathway_name);
	
}

function renderPathwayEc(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+record.data.pathway_name+'" data-qclass="x-tip"';
	if(!Ext.getDom("pk"))
		return Ext.String.format('<a href=CompPathwayMap?cType={0}&amp;cId={1}&amp;dm=ec&amp;ec_number={2}&amp;feature_info_id=&amp;map={3}&amp;pk=&amp;algorithm={4}>{5}</a>', Ext.getDom("cType").value, Ext.getDom("cId").value, record.data.ec_number, record.data.pathway_id, record.data.algorithm, record.data.pathway_name);
		
	else
		return Ext.String.format('<a href=CompPathwayMap?cType=&amp;cId=&amp;dm=ec&amp;ec_number={0}&amp;feature_info_id=&amp;map={1}&amp;pk={2}&amp;algorithm={3}>{4}</a>', record.data.ec_number, record.data.pathway_id,  Ext.getDom("pk").value, record.data.algorithm, record.data.pathway_name);
	
}

function renderGeneCountEc(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+record.data.gene_count+'" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"ShowFeatureTab('"+record.data.pathway_id+"','"+record.data.pathway_name+"','"+record.data.pathway_class+"','"+record.data.ec_number+"','"+record.data.algorithm+"');\"/>"+record.data.gene_count+"</a>");
	
}

function renderGeneCountPathway(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+record.data.gene_count+'" data-qclass="x-tip"';
	return Ext.String.format("<a href=\"javascript:void(0);\" onclick=\"ShowFeatureTab('"+record.data.pathway_id+"','"+record.data.pathway_name+"','"+record.data.pathway_class+"','','"+record.data.algorithm+"');\"/>"+record.data.gene_count+"</a>");
		
}

function getSelectedFeatures(actiontype, showdownload, fastatype, to){

	var pid = [],
		ecid = [],
		aid = [],
		cType = "",
		cId = "",
		Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash,
		sl = Page.getCheckBox().getSelections(),
		i,
		fids = property.fids;
			
	if(property.pageType == "Finder"){
		if(Ext.getDom("genomeId").value != ""){
			cId =  Ext.getDom("genomeId").value;
			cType = "genome";
		}else {
			cId =  Ext.getDom("taxonId").value;
			cType = "taxon";
		}
	}else{
		cType = Ext.getDom("cType").value;
		cId = Ext.getDom("cId").value;
	}
	
	if(hash.aT != "2"){
					
		for (i=0; i<sl.length; i++) {
			pid.push("'" +sl[i].data.pathway_id+"'");
			
			if(sl[i].data.algorithm == "PATRIC")
				aid.push("'RAST'");
			else if(sl[i].data.algorithm == "Legacy BRC")
				aid.push("'Curation'");
			else
				aid.push("'RefSeq'");
						
			if (hash.aT == "1")
				ecid.push("'" + sl[i].data.ec_number +"'");		
		}
		
		if(Ext.getDom("ecN").value)
	    	ecid.push("'" + Ext.getDom("ecN").value +"'");
		
		Ext.Ajax.request({
		    url: "/patric-pathways/jsp/get_na_feature_ids.json.jsp",
		    method: 'POST',
		    params: {cId:cId, cType:cType, map:pid.join(","), algorithm:aid.join(","), ec_number:ecid.join(",")},
		    success: function(response, opts) {
		    	var na_features = Ext.JSON.decode(response.responseText);			
		    	for(i = 0; i < na_features.genes.length; i++)
		    		fids.push(na_features.genes[i].genes);		    	
			}
		});
		
	}else{
		for (i=0; i<sl.length;i++) {
			fids.push(sl[i].data.na_feature_id);
		}
	}
}

function KeepParameters(){
	"use strict";
	
	var Page = $Page,
		property = Page.getPageProperties(),
		hash = property.hash;

	if(!(hash.cwP || hash.cwEC))
	    hash.alg = Ext.getDom("alg").value;
	if(Ext.getDom("search_on").value == "Ec_Number"){
		hash.ecN = Ext.getDom("ecN").value;
		hash.pId = "";
	}else if(Ext.getDom("search_on").value == "Map_ID"){
		hash.pId = Ext.getDom("pId").value;
		hash.ecN = "";
	}
	
}

function DownloadFile(){
	"use strict";
	
	var form = Ext.getDom("fTableForm");
	
	form.action = "/patric-pathways/jsp/grid_download_handler.jsp";
	form.fileformat.value = arguments[0];
	form.target = "";
	getHashFieldsToDownload(form);
	form.submit();
}
