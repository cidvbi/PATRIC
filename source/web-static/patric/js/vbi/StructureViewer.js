Ext.define('VBI.Strcuture.model.Ligand', {
	extend: 'Ext.data.Model',
	fields:["chemicalName", "chemicalID", "smiles", "formula"],
	idProperty: "chemicalID"
});
Ext.define('VBI.Structure.model.Chain', {
	extend: 'Ext.data.Model',
	fields:['chainID'],
	idProperty: 'chainID'
});
Ext.define('VBI.Structure.model.Annotation', {
	extend: 'Ext.data.Model',
	fields:['id','START','END','link','METHOD','type','note','ext_id'],
	idProperty: 'id'
});


Ext.define('VBI.Structure.LigandsPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.ligandspanel',
	border: false,
	items: [{
		xtype: 'grid',
		id: 'structure_grid_ligands',
		store: Ext.create('Ext.data.Store', {
			storeId: 'structure_ligands',
			model: 'VBI.Strcuture.model.Ligand',
			proxy: {
				type: 'ajax',
				noCache: false,
				startParam: undefined,
				limitParam: undefined,
				pageParam: undefined,
				url: '/patric-mashup/jsp/structureviewer_support.json.jsp?mode=ligands',
				reader: {
					type: 'json',
					root: 'results',
					totalProperty: 'totalCount'
				}
			},
			autoLoad: false
		}),
		columns: [
			{header: 'ID', width: 40, sortable: false, dataIndex: "chemicalID"},
			{header: 'Name', width: 200, sortable: false, dataIndex: "chemicalName"}],
		height: 220,
		width: '100%',
		listeners: {
			scope: this,
			itemclick: function (grd, record, item, index, e, eOpts) {
				document.getElementById('appMain').script(getJmolDecoration("ligand", record.getId()));
				Ext.getCmp('structure_detail_panel').update("ligand", record.data);
				resetApperanceControls(false);
			}
		}
	}, {
		xtype: 'button',
		text: 'Show Pocket',
		listeners: {
			click: function (btn, e) {
				//console.log(g.getSelectionModel().getSelection());
				var t = Ext.getCmp('structure_grid_ligands').getSelectionModel().getSelection()[0];
				if (t==null) {
					alert("Please select a ligand to display first.");
				} else {
					document.getElementById('appMain').script(getJmolDecoration("pocket", t.getId()));
					resetApperanceControls(false);
				}
			}
		}
	}],
	initComponent: function() {
		Ext.StoreManager.lookup('structure_ligands').load({params:{"pdb_id": this.pdbID}});
		this.callParent();
	}
});

Ext.define('VBI.Structure.ChainPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.chainpanel',
	border: false,
	items: [{
		xtype: 'grid',
		store: Ext.create('Ext.data.Store', {
			storeId: 'structure_chains',
			proxy: {
				type: 'ajax',
				noCache: false,
				startParam: undefined,
				limitParam: undefined,
				pageParam: undefined,
				url: '/patric-mashup/jsp/structureviewer_support.json.jsp?mode=chains',
				reader: {
					type: 'json',
					root: 'results',
					totalProperty:'totalCount'
				}
			},
			model: 'VBI.Structure.model.Chain'
		}),
		columns: [
			{header: 'Chain', width: 240, sortable: false, dataIndex: 'chainID'}],
		listeners: {
			scope: this,
			itemclick: function (grd, record, item, index, e, eOpts) {
				document.getElementById('appMain').script(getJmolDecoration("chain", record.getId() ));
				resetApperanceControls(false);
			}
		}
	}],
	initComponent: function() {
		Ext.StoreManager.lookup('structure_chains').load({params:{"pdb_id":this.pdbID}});
		this.callParent();
	}
});

Ext.define('VBI.Structure.AnnotationPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.annotationpanel',
	border: false,
	items: [{
		xtype: 'combo',
		id: 'annotation_chain',
		height: 18,
		width: 250,
		store: 'structure_chains',
		queryMode:'local',
		emptyText:'select a chain to retieve annotations',
		valueField:'chainID',
		displayField:'chainID',
		forceSelection:false,
		editable:false,
		triggerAction:'all',
		listeners: {
			select: function(combo, records, eOpts) {
				Ext.StoreManager.lookup('structure_annotations').load({params:{"chain_id":combo.getValue(), "pdb_id": this.pdbID}});
			}
		}
	}, {
		xtype: 'grid',
		store: Ext.create('Ext.data.Store', {
			storeId: 'structure_annotations',
			proxy: {
				type: 'ajax',
				noCache: false,
				startParam: undefined,
				limitParam: undefined,
				pageParam: undefined,
				url: '/patric-mashup/jsp/structureviewer_support.json.jsp?mode=annotations',
				reader: {
					root: 'results',
					totalProperty:'totalCount'
				}
			},
			model: 'VBI.Structure.model.Annotation'
		}),
		columns: [
			{header: 'Source', width: 50, sortable: false, dataIndex: 'METHOD'},
			{header: 'Type', width: 190, sortable : false, dataIndex: 'type'}
		],
		listeners: {
			scope: this,
			itemclick: function (grd, record, item, index, e, eOpts) {
				Ext.getCmp('structure_detail_panel').update("annotation", record.data);
				domain = record.get("START")+"-"+record.get("END")+":"+Ext.getCmp("annotation_chain").getValue();
				document.getElementById('appMain').script(getJmolDecoration("domain", domain));
				resetApperanceControls(false);
				
			}
		}
	}],
	initComponent: function() {
		this.items[0].pdbID = this.pdbID;
		this.callParent();
	}
});


Ext.define('VBI.Structure.DetailPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.svdetailpanel',
	id: 'structure_detail_panel',
	update: function (from, data) {
		//console.log(data);
		if (from == "ligand") {
			_dp = ["<div class='table-container'><table width='100%' cellspacing=0 style='data-table'>", 
				"<tr><td>ID </td><td>", data.chemicalID, "</td></tr>",
				"<tr><td width='15'>Type</td><td class='nowrap'>", data.chemicalName, "</td></tr>",
				"<tr><td>smiles </td><td class='nowrap'>", data.smiles, "</td></tr>",
				"<tr><td>formula </td><td>", data.formula, "</td></tr>",
				"</table></div>"].join('');
			//console.log(this);
			this.body.dom.innerHTML = _dp;
		} else if (from == "annotation") {
			_dp = ["<div class='table-container'><table width='100%' cellspacing=0 style='data-table'>",
				"<tr><td>Type </td><td>", data.METHOD, "</td></tr>",
				"<tr><td width='15'>Name </td><td class='nowrap'>", data.type, "</td></tr>",
				"<tr><td>Location </td><td>", data.START, "..", data.END, "</td></tr>"].join('');
			if (data.link == "") {
				_dp += ["<tr><td>ID </td><td>", data.ext_id, "</td></tr>"].join('');
			}
			else {
				_dp += ["<tr><td>ID </td><td><a href='", data.link,"' target='_blank'>", data.ext_id, "</a></td></tr>"].join('');
			} 
			_dp += ["</table></div>"].join('');
			
			this.body.dom.innerHTML = _dp;
		}
	}
});


Ext.define('VBI.Structure.Viewer', {
	extend: 'Ext.panel.Panel',
	layout: 'border',
	initComponent: function() {
		
		this.items = [{
				region: 'west',
				width: 250,
				border: false,
				collapsible: true,
				layout: 'border',
				items: [{
						region: 'center',
						border: false,
						layout: 'accordion',
						items: [{
							xtype: 'ligandspanel',
							title: 'Ligands',
							pdbID: this.pdbID,
							width: 250,
							height: 250
						}, {
							xtype: 'chainpanel',
							title: 'Chains',
							pdbID: this.pdbID,
							width: 250,
							height: 250,
							autoScroll: true
						}, {
							xtype: 'annotationpanel',
							title: 'Annotations',
							pdbID: this.pdbID,
							width: 250,
							height: 250
						}]
					},
					{
						region: 'south',
						xtype: 'svdetailpanel',
						title: 'Detail View',
						height: 300
					}
				]
			}, {
				region: 'center', 
				contentEl: 'sv_mainviewer'
			}, {
				region: 'north', 
				contentEl: 'sv_overview', 
				border: false
			}, {
				region: 'east', 
				width: 250, 
				collapsible: true, 
				contentEl: 'sv_controls'
			}
		];
		
		this.callParent();
	}
});


function getJmolDecoration(type, value) {
	
	if (type == "onload") {
		return "isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; " +
			"cartoons on; color white; select (helix,sheet); cartoons 200; select sheet; color [200,200,100]; "+
			"select helix; color [225,40,40]; select ligand; wireframe 60; spacefill 75; color [100,100,255]; ";
	}
	else if (type == "ligand" || type == "protein") {
		return "isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; " +
				"select protein; cartoons on; color cpk; select (helix,sheet); cartoons 200; " +
				"select ["+value+"]; center selected; wireframe 60; spacefill 75; color cpk";
	}
	else if (type == "chain") {
		return "isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; " +
				"select *:"+value+"; center selected; cartoons on; color [150,80,250]; select *:"+value+" and (sheet,helix); cartoons 200";
	}
	else if (type == "pocket") {
		return "isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; " +
				"select protein; cartoons on; color cpk; select (helix,sheet); cartoons 200; " +
				"select within(6.5,["+value+"]) and protein; center selected; wireframe 60; spacefill 60; color gold; isosurface ignore (ligand,solvent) solvent 0.6 " +
				"select ["+value+"]; wireframe 60; spacefill 75";
	}
	else if (type == "domain") {
		//value: 1-77:A
		return "isosurface off; select all; wireframe off; spacefill off; cartoons off; backbone off; " +
				"select "+value+"; center selected; cartoons on; color [150,80,250]; select "+value+" and (sheet,helix); cartoons 200; color cpk";
	}
}
