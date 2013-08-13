Ext.define('TranscriptomicsUploader.model.GenomeName', {
	extend: 'Ext.data.Model',
	idProperty: 'ncbi_taxon_id',
	fields: [{
		name: 'ncbi_taxon_id',
		type: 'int'
	}, {
		name: 'genome_info_id',
		type: 'int'
	}, {
		name: 'display_name',
		type: 'string'
	}]
});/**
 * @class TranscriptomicsUploader.store.FileTypes
 * @extends Ext.data.Store
 *
 * This class implements the store for file types.
 */
Ext.define('TranscriptomicsUploader.store.DataTypes', {
	extend: 'Ext.data.Store',
	fields: ['name', 'text'],
	data: [	{name:"Transcriptomics", text:'Transcriptomics'},
			{name:"Proteomics", text:'Proteomics'},
			{name:"Phenomics", text:'Phenomics'}
	]
});
/**
 * @class TranscriptomicsUploader.store.FileFormats
 * @extends Ext.data.Store
 *
 * This class implements the store for file formats.
 */
Ext.define('TranscriptomicsUploader.store.FileFormats', {
	extend: 'Ext.data.Store',
	fields: ['name', 'text'],
	data: [{name:"matrix", text:'Gene Matrix'},{name:"list", text:'Gene List'}]
});
/**
 * @class TranscriptomicsUploader.store.FileTypes
 * @extends Ext.data.Store
 *
 * This class implements the store for file types.
 */
Ext.define('TranscriptomicsUploader.store.FileTypes', {
	extend: 'Ext.data.Store',
	fields: ['name', 'text'],
	data: [	{name:"csv", text:'Comma delimited (.csv)'}, 
			{name:"txt", text:'Tab delimited (.txt)'},
			{name:"xls", text:'Microsoft Excel (.xls)'},
			{name:"xlsx", text:'Microsoft Excel (.xlsx)'}
	]
});
/**
 * @class TranscriptomicsUploader.store.GeneIDTypes
 * @extends Ext.data.Store
 *
 * This class implements the store for gene ID types.
 */
Ext.define('TranscriptomicsUploader.store.GeneIDTypes', {
	extend: 'Ext.data.Store',
	fields: ['name', 'text'],
	data: [{name:"refseq_source_id", text:'RefSeq Locus Tag'}, {name:"source_id", text:'PATRIC Locus Tag'}]
});
/**
 * @class TranscriptomicsUploader.store.GenomeNames
 * @extends Ext.data.Store
 *
 * This class implements the store for GenomeNames.
 */
Ext.define('TranscriptomicsUploader.store.GenomeNames', {
	extend: 'Ext.data.Store',
	model: 'TranscriptomicsUploader.model.GenomeName',
	proxy: {
		type: 'ajax',
		url: '/patric-common/jsp/genomeselector_support.json.jsp',
		startParam: undefined,
		limitParam: undefined,
		pageParam: undefined,
		noCache: false,
		reader: {
			type: 'json',
			root: 'genomeList',
			totalProperty: 'totalCount'
		},
		extraParams: {
			mode: 'search',
			start: 2,
			searchon: 'azlist'
		}
	}
});
/**
 * @class TranscriptomicsUploader.store.WorkspaceGroups
 * @extends Ext.data.Store
 *
 * This class implements the store for WorkspaceGroups.
 */
Ext.define('TranscriptomicsUploader.store.WorkspaceGroups', {
	extend: 'Ext.data.Store',
	fields: ['name', 'description', 'tags'],
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getGroupList&group_type=ExpressionExperiment',
		startParam: undefined,
		limitParam: undefined,
		pageParam: undefined,
		noCache: false,
		reader: {
			type: 'json'
		}
	},
	listeners: {
		load: function(me, records, successful, eOpts) {
			if (successful) {
				me.insert(0, {"name":"Create New Group"});
				me.insert(0, {"name":"None"});
			}
		}
	}
});
/**
 * @class TranscriptomicsUploader.view.AddToGroup
 * @extends Ext.form.Panel
 * @xtype addtogroup
 *
 * This class implements a chart of condition types.
 */
Ext.define('TranscriptomicsUploader.view.AddToGroup', {
	extend: 'Ext.form.Panel',
	alias: 'widget.addtogroup',
	border: false,
	bodyPadding: 10,
	fieldDefaults: {
		labelWidth: 80,
		anchor: '100%'
	},
	items: [{
		xtype: 'displayfield',
		value: '<b>Specify a Group for this Experiment (optional)</b>'
	}, {
		xtype: 'combobox',
		name: 'atg',
		fieldLabel: 'Add to Group',
		store: 'WorkspaceGroups',
		editable: false,
		displayField: 'name',
		valueField: 'name',
		id: 'test_this_form',
		listeners: {
			added: function (me, option) {
				me.select("None");
			},
			change: function (me, newValue, oldValue, eOpts) {
				var form = me.up("form");
				
				if (oldValue == undefined) {
					return false;
				}
				
				if (newValue == "None") {
					form.child("#groupName").setDisabled(true);
					form.child("#groupDesc").setDisabled(true);
				}
				else if (newValue == "Create New Group") {
					
					form.child("#groupName").setDisabled(false);
					form.child("#groupName").setValue("");
					form.child("#groupName").focus();
					form.child("#groupName").setReadOnly(false);
						
					form.child("#groupDesc").setDisabled(false);
					form.child("#groupDesc").setValue("");
					
					form.child("#groupTag").setValue("");
					
				} else {
					
					record = me.getStore().findRecord("name", newValue, undefined, undefined, undefined, true);
					
					form.child("#groupName").setDisabled(false);
					form.child("#groupName").setValue(record.get("name"));
					form.child("#groupName").setReadOnly(true);
					
					form.child("#groupDesc").setDisabled(false);
					form.child("#groupDesc").setValue(record.get("description"));
					form.child("#groupDesc").setReadOnly(true);
					
					form.child("#groupTag").setValue(record.get("tags"));
				}
			}
		}
	}, {
		xtype: 'textfield',
		itemId: 'groupName',
		name: 'group_name',
		emptyText: 'New group name',
		disabled: true
	}, {
		xtype: 'textareafield',
		itemId: 'groupDesc',
		name: 'group_desc',
		emptyText: 'Group Description',
		disabled: true
	}, {
		xtype: 'textfield',
		itemId: 'groupTag',
		name: 'tags',
		fieldLabel: 'Group Tags',
		emptyText: '(comma separated)'
	}],
	buttons: [{
		text: 'Previous',
		handler: function() {
			Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step03");
			Ext.getCmp("uploader").getComponent("steps").setActiveTab("step03");
		}
	}, {
		text: 'Save to Workspace',
		handler: function(me) {
			
			var collectionId = uploader.params.collectionId;
			
			var form = me.up('form').getForm();
			if (form.isValid()) {
				form.submit({
					url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
					params: {
						action_type: 'groupAction',
						action: 'create',
						group_type: 'ExpressionExperiment',
						tracks: collectionId
					},
					submitEmptyText: false,
					success: function(fm, action) {
						//Ext.Msg.alert('Success', 'Saved in workspace');
						if (uploader.callback != undefined) {
							uploader.callback();
						}
						uploader.close();
					},
					failure: function(fm, action) {
						console.log('Form submission failed', action);
					}
				});
			}
		}
	}]
});
/**
 * @class TranscriptomicsUploader.view.DescribeExperiment
 * @extends Ext.form.Panel
 * @xtype describeexperiment
 *
 * This class implements a chart of condition types.
 */
Ext.define('TranscriptomicsUploader.view.DescribeExperiment', {
	extend: 'Ext.form.Panel',
	alias: 'widget.describeexperiment',
	border: false,
	bodyPadding: 10,
	fieldDefaults: {
		labelWidth: 110,
		labelAlign: 'right',
		anchor: '100%'
	},	
	items: [{
		xtype: 'displayfield',
		value: '<b>Provide additional information for this experiment</b>'
	}, {
		xtype: 'combobox',
		name: 'data_type',
		fieldLabel: 'Data Type',
		value: 'Transcriptomics',
		store: 'DataTypes',
		queryMode: 'local',
		displayField: 'text',
		valueField: 'name',
		editable: false,
		afterRender: function() {
			var viewport = this.findParentByType('window');
			if (viewport.params != undefined 
				&& viewport.params.metaData != undefined
				&& viewport.params.metaData.data_type != undefined) {
				this.setValue(viewport.params.metaData.data_type);
			}
		}
	}, {
		xtype: 'textfield',
		name: 'experiment_title',
		fieldLabel: 'Experiment Title',
		emptyText: 'Title',
		allowBlank: false
	}, {
		xtype: 'textareafield',
		name: 'experiment_description',
		fieldLabel: 'Experiment <br/> Description',
		emptyText: 'Description (optional)'
	}, {
		xtype: 'textfield',
		name: "organism_name",
		fieldLabel: 'Organism Name',
		emptyText: 'Organism Name (optional)'
	}, {
		xtype: 'numberfield',
		name: "pubmed_id",
		fieldLabel: 'Pubmed ID',
		hideTrigger: true,
		keyNavEnabled: false,
		mouseWheelEnabled: false,
		emptyText: 'Pubmed ID (optional)'
	}],
	buttons: [{
		text: 'Previous',
		handler: function() {
			Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step02");
			Ext.getCmp("uploader").getComponent("steps").setActiveTab("step02");
		}
	}, {
		text: 'Next',
		formBind: true,
		disabled: true,
		handler: function(me, e) {
			
			var collectionId = uploader.params.collectionId;
			var extra = uploader.params;
			extra.parsed.snapshot = undefined;
			var form = me.up('form').getForm();
			
			Ext.Ajax.request({
				url: '/portal/portal/patric/BreadCrumb/TranscriptomicsUploaderWindow?action=b&cacheability=PAGE',
				params: {
					mode: "save_experiment",
					collectionId: collectionId,
					extra: Ext.JSON.encode(extra),
					data_type: form.findField("data_type").getValue(),
					experiment_title: form.findField("experiment_title").getValue(),
					experiment_description: form.findField("experiment_description").getValue(),
					organism_name: form.findField("organism_name").getValue(),
					pubmed_id: form.findField("pubmed_id").getValue()
				},
				timeout: 60000,
				success: function(response) {
					//console.log('Success', response);
					Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step04");
					Ext.getCmp("uploader").getComponent("steps").setActiveTab("step04");
				},
				failure: function(response) {
					console.log('Failure', response);
				}
			});
		}
	}]
});
/**
 * @class TranscriptomicsUploader.view.MapGeneIdentifiers
 * @extends Ext.form.Panel
 * @xtype mapgeneidentifiers
 *
 * This class implements a chart of condition types.
 */
Ext.define('TranscriptomicsUploader.view.MapGeneIdentifiers', {
	extend: 'Ext.form.Panel',
	alias: 'widget.mapgeneidentifiers',
	border: false,
	bodyPadding: 10,
	fieldDefaults: {
		labelAlign: 'right'
	},
	id: 'MapGeneIdentifiersPanel',
	initParsedResult: function() {
		var me = this;
		
		if (uploader.params.parsed == null) {
			return false;
		}
		var p = uploader.params.parsed;
		var strLabel =  "<b>"+p.origFileName+" ("+p.countGeneIDs+" gene IDs, "+p.countSamples+" comparisons)";
		me.getComponent("parsed_label").setValue(strLabel);
		
		var data = [], fields = [], cols = [], grid, header;
		
		if (p.snapshot!= undefined && p.snapshot.length > 0) {
			
			header = p.snapshot[0].line.split("\t");
			
			for (i=0; i<header.length; i++) {
				if (i==0) {
					fields.push("C"+i);
					cols.push({
						text: "Gene ID",
						dataIndex: "C"+i
					});
				} else {
					fields.push("C"+i);
					cols.push({
						text: "Column "+i,
						dataIndex: "C"+i
					});
				}
			}
			
			for (rowId=1; rowId<p.snapshot.length; rowId++) {
				
				row = p.snapshot[rowId].line.split("\t");
				tmpDataRow = {};
				for (i=0; i<header.length; i++) {
					tmpDataRow["C"+i] = row[i];
				}
				data.push(tmpDataRow);
			}
		}
		
		grid = Ext.create('Ext.grid.Panel', {
			store: {
				data: data,
				fields: fields
			},
			columns: cols,
			height: 100
		});
		//console.log(me.child('#parsed_result'),Ext.getDom(me.child('#parsed_result').id));
		grid.render(me.child('#parsed_result').el, 0);
	},
	items: [{
		xtype: 'displayfield',
		itemId: 'parsed_label',
		value: ''
	}, {
		itemId: 'parsed_result',
		height: 100,
		anchor: '100%',
		readOnly: true
	}, {
		xtype: 'displayfield',
		padding: '10 0 0 0',
		value: '<b>Specify Gene ID type to map data into PATRIC</b>'
	}, {
		xtype: 'container',
		layout: 'hbox',
		padding: '0 0 5px 0',
		items: [{
			xtype: 'combobox',
			fieldLabel: 'Gene ID Type',
			name: 'geneIdType',
			width: 300,
			value: 'refseq_source_id',
			store: 'GeneIDTypes',
			queryMode: 'local',
			displayField: 'text',
			valueField: 'name',
			editable: false
		}, {
			xtype: 'component',
			autoEl: {
				tag: 'a',
				href: 'http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs/#gene-id-type',
				html: "what's this?",
				target: '_blank'
			},
			listeners: {
				afterrender: function(link) {
					link.mon(link.el, 'click', function() { 
						Ext.create('Ext.tip.ToolTip', {
							target: link.el,
							html: 'Gene ID Type describes the gene identifier scheme used in your source file.  PATRIC uses Gene ID Type to map your data to existing PATRIC functional annotations.'
						});
					}, this);
				}
			},
			padding: '0 0 0 15px'
		}, {
			xtype: 'label',
			text: "Don't see your ID Type (",
			padding: '0 0 0 5px'
		}, {
			xtype: 'component',
			autoEl: {
				tag: 'a',
				href: 'http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs',
				html: "FAQ",
				target: '_blank'
			}
		}, {
			xtype: 'label',
			text: ')'
		}]
	}, {
		xtype: 'container',
		layout: 'hbox',
		padding: '10 0 0 0',
		items: [{
			xtype: 'displayfield',
			value: '<b>Map your genes into PATRIC</b>',
			width: 200
		}, {
			xtype: 'button',
			id: 'map_genes_btn',
			text: 'Map Genes',
			margin: '0 0 0 20px',
			//disabled: true,
			handler: function(me) {
				var form = me.up('form').getForm();
				var collectionId 	= uploader.params.collectionId;
				var gene_id_type	= form.findField("geneIdType").getValue();
				
				//console.log(ncbi_taxon_id, form, gene_id_type);
				var myMask = new Ext.LoadMask(uploader.body, {msg:"Mapping genes"});
				myMask.show();
				
				Ext.Ajax.request({
					url: '/portal/portal/patric/BreadCrumb/TranscriptomicsUploaderWindow?action=b&cacheability=PAGE',
					params: {
						mode: 'map_genes',
						collectionId: collectionId,
						geneIdType: gene_id_type
					},
					timeout: 300000,
					success: function(response) {
						mapped_result = Ext.JSON.decode(response.responseText);
						uploader.params.mapping = mapped_result;
						//form.findField("mapping_result").setValue(mapped_result.msg);
						//console.log(mapped_result);
						
						pPanel = me.findParentByType('panel');
						pPanel.child('#mapping_result').child('#msg').setValue(mapped_result.msg);
						var icon;
						if (mapped_result.geneMissed == 0) {
							icon = '/patric/images/icon-green-check-22x22.png';
						}
						else if (mapped_result.geneMissed == mapped_result.geneTotal) {
							icon = '/patric/images/icon-red-x-22x22.png';
						}
						else {
							icon = '/patric/images/icon-orange-warning-22x22.png';
						}
						pPanel.child('#mapping_result').child('#icon').setSrc(icon);
						
						//enable next button
						Ext.getCmp("next_btn_to_experiment").setDisabled(false);
						
						myMask.hide();
					},
					failure: function(response) {
						console.log(response.status);
						myMask.hide();
					}
				});
				
			}
		}]
	},{
		xtype: 'container',
		layout: 'hbox',
		padding: '15px 0 0 0',
		itemId: 'mapping_result',
		items: [{
			xtype: 'imagecomponent',
			itemId: 'icon',
			src: '',
			height: 22,
			width: 22
		}, {
			xtype: 'displayfield',
			itemId: 'msg',
			name: 'mapping_result',
			padding: '0 0 0 3px',
			value: 'You must map your genes into PATRIC before procedding to the next step'
		}]
	}],
	buttons: [{
		text: 'Previous',
		handler: function() {
			Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step01");
			Ext.getCmp("uploader").getComponent("steps").setActiveTab("step01");
			this.findParentByType('panel').child('#mapping_result').child('#msg').reset();
			this.findParentByType('panel').child('#mapping_result').child('#icon').setSrc('');
			//console.log(uploader.params);
		}
	}, {
		text: 'Next',
		id: 'next_btn_to_experiment',
		disabled: true,
		handler: function() {
			Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step03");
			Ext.getCmp("uploader").getComponent("steps").setActiveTab("step03");
		}
	}]
});
/**
 * @class TranscriptomicsUploader.view.SpecifyFile
 * @extends Ext.form.Panel
 * @xtype specifyfile
 *
 * This class implements a chart of condition types.
 */
Ext.define('TranscriptomicsUploader.view.SpecifyFile', {
	extend: 'Ext.form.Panel',
	alias: 'widget.specifyfile',
	border: false,
	bodyPadding: 10,
	fieldDefaults: {
		msgTarget: 'under',
		labelAlign: 'right'
	},
	items: [{
		xtype: 'displayfield',
		value: '<b>Specify omic data type</b>'
	}, {
		xtype: 'combobox',
		name: 'data_type',
		fieldLabel: 'Data Type',
		labelWidth: 180,
		width: 350,
		store: 'DataTypes',
		queryMode: 'local',
		displayField: 'text',
		valueField: 'name',
		editable: false,
		afterRender: function() {
			var viewport = this.findParentByType('window');
			if (viewport.params != undefined 
				&& viewport.params.metaData != undefined
				&& viewport.params.metaData.data_type != undefined) {
				this.setValue(viewport.params.metaData.data_type);
			}
		}
	}, {
		xtype: 'displayfield',
		value: '<b>Specify the file type to upload</b>'
	}, {
		xtype: 'combobox',
		name: 'file0_type',
		fieldLabel: 'File Type',
		labelWidth: 180,
		width: 350,
		value: 'txt',
		store: 'FileTypes',
		queryMode: 'local',
		displayField: 'text',
		valueField: 'name',
		editable: false
	}, {
		xtype: 'container',
		layout: 'hbox',
		padding: '0 0 5px 0',
		items: [{
			xtype: 'combobox',
			name: 'file0_format',
			fieldLabel: 'File Format',
			labelWidth: 180,
			width: 350,
			value: 'matrix',
			store: 'FileFormats',
			queryMode: 'local',
			displayField: 'text',
			valueField: 'name',
			editable: false
		}, {
			xtype: 'component',
			autoEl: {
				tag: 'a',
				href: 'http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs/#gene-matrix',
				html: "what's this?",
				target: '_blank'
			},
			listeners: {
				afterrender: function(link) {
					link.mon(link.el, 'click', function() { 
						Ext.create('Ext.tip.ToolTip', {
							target: link.el,
							html: 'File format describes how the expression data is orgnaized in your source file.  Gene Matrix format specifies gene identifiers in rows, samples in columns, and interior cells contain expression values as log-ratio.  Gene List format specifies gene ID, sample, and expressions value as log-ratio per row.'
						});
					}, this);
				}
			},
			padding: '0 0 0 15px'
		}]
	}, {
		xtype: 'container',
		padding: '5 80',
		items: [{
			xtype: 'imagecomponent',
			src: '/patric/images/transcriptomics_uploader_rule.png'
		}, {
			xtype: 'imagecomponent',
			src: '/patric/images/transcriptomics_uploader_rule.png'
		}]
	}, {
		xtype: 'container',
		layout: 'hbox',
		items: [{
			xtype: 'filefield',
			name: 'file0',
			buttonOnly: true,
			buttonText: 'Choose File',
			fieldLabel: 'Specify a file on your computer',
			labelWidth: 180,
			width: 256,
			listeners: {
				'change': function (me, value, eOpts) {
					var arr = value.split("\\");
					var filename = arr[arr.length-1];
					//console.log(arr, filename, me.up('form').getForm().findField("expression_filename"));
					me.up('form').getForm().findField("expression_filename").setValue(filename);
				}
			}
		}, {
			xtype: 'displayfield',
			name: 'expression_filename',
			padding: '0 0 0 20px'
		}]
	},{
		xtype: 'imagecomponent',
		padding: '5 80',
		src: '/patric/images/transcriptomics_uploader_rule_or.png'
	},{
		xtype: 'textfield',
		name: 'remoteData_1',
		fieldLabel: 'Specify a URL for a file',
		labelWidth: 180,
		anchor: '100%',
		validator: function(value) {
			var file1 = this.up('form').getForm().findField("file0");
			
			if (file1.isValid() && file1.getValue()!="") {
				return true;
			} else {
				// validate url form
				if (Ext.form.field.VTypes.url(value)) { 
					return true;
				} else {
					return false;
				}
			}
		}
	},{
		xtype: 'displayfield',
		padding: '15 0 0 0',
		value: '<b>Specify the comparison metadata to upload (optional)</b>'
	}, {
		// sample file type
		xtype: 'container',
		layout: 'hbox',
		padding: '0 0 5px 0',
		items: [{
			xtype: 'combobox',
			name: 'file1_type',
			fieldLabel: 'File Type',
			labelWidth: 180,
			width: 350,
			value: 'txt',
			store: 'FileTypes',
			queryMode: 'local',
			displayField: 'text',
			valueField: 'name',
			editable: false
		}, {
			xtype: 'component',
			autoEl: {
				tag: 'a',
				href: 'http://enews.patricbrc.org/data-upload-templates/',
				html: "download template",
				target: '_blank'
			},
			padding: '0 0 0 15px'
		}]
	}, {
		xtype: 'container',
		layout: 'hbox',
		items: [{
			xtype: 'filefield',
			name: 'file1',
			buttonOnly: true,
			buttonText: 'Choose File',
			fieldLabel: 'Specify a file on your computer',
			labelWidth: 180,
			width: 256,
			listeners: {
				'change': function (me, value, eOpts) {
					var arr = value.split("\\");
					var filename = arr[arr.length-1];
					me.up('form').getForm().findField("sample_filename").setValue(filename);
				}
			}
		}, {
			xtype: 'displayfield',
			name: 'sample_filename',
			padding: '0 0 0 20px'
		}]
	}],
	buttons: [{
		text: 'Next',
		formBind: true,
		disabled: true,
		handler: function(me, e) {
			
			Ext.Ajax.request({
				url: '/portal/portal/patric/BreadCrumb/TranscriptomicsUploaderWindow?action=b&cacheability=PAGE',
				params: {
					mode: "create_collection"
				},
				success: function(response) {
					var jsonResponse = Ext.JSON.decode(response.responseText);
					var authToken = jsonResponse.token;
					var collectionId = jsonResponse.collection;
					var baseUrl = jsonResponse.url;
					var success = jsonResponse.success;
					var expMetaData = {};
					
					/*
					var baseUrl = uploader.params.baseUrl;
					var authToken = uploader.params.authToken;
					var collectionId = uploader.params.collectionId;
					var success = true;
					//console.log("token="+authToken,"collection="+collectionId);
					*/
					
					if (success) {
					
						uploader.params = {"baseUrl": baseUrl, "authToken":authToken, "collectionId": collectionId};
						var form = me.up('form').getForm();
						//
						expMetaData["data_type"] = form.findField("data_type").getValue();
						uploader.params["metaData"] = expMetaData;
						
						var params = {
							file0_content: "expression",
							file0_orientation: "svg"
						};
						// sample metadata (optional)
						if (form.findField("file1").getValue() != "") {
							Ext.applyIf(params, {
								file1_format: "list",
								file1_content: "sample"
							});
						}
						// url upload
						if (form.findField("remoteData_1").getValue() != "") {
							Ext.applyIf (params, {
								remoteData_1_type: form.findField("file0_type").getValue(),
								remoteData_1_format: form.findField("file0_format").getValue(),
								remoteData_1_content: "expression",
								remoteData_1_orientation: "svg"
							});
						}
						if (form.isValid()) {
							form.submit({
								url: baseUrl+'/Collection/'+collectionId+"?http_accept=application/json&http_authorized_session=polyomic%20authorization_token%3D"+authToken,
								params: params,
								success: function(fm, action) {
									//console.log('success', action);
									
									// clear filenames
									fm.findField("expression_filename").setValue("");
									fm.findField("sample_filename").setValue("");
									//fm.findField("file0").setValue();
									//fm.findField("file0").validate();
									
									// parse
									var myMask = new Ext.LoadMask(uploader.body, {msg:"Uploading your file"});
									myMask.show();
									
									Ext.Function.defer(function() {
										Ext.Ajax.request({
											url: '/portal/portal/patric/BreadCrumb/TranscriptomicsUploaderWindow?action=b&cacheability=PAGE',
											params: {
												mode: 'parse_collection',
												collectionId: collectionId
											},
											timeout: 300000,
											success: function(response) {
												uploader.params.parsed = Ext.JSON.decode(response.responseText);
												myMask.hide();
												
												if (uploader.params.parsed.success == true) {
													
													Ext.getCmp("uploader").getComponent("breadcrumb").setActiveTab("step02");
													Ext.getCmp("uploader").getComponent("steps").setActiveTab("step02");
													
													Ext.getCmp("MapGeneIdentifiersPanel").initParsedResult();
													
												} else {
													Ext.Msg.alert('Fail', uploader.params.parsed.msg);
												}
											},
											failure: function(response) {
												console.log('Parsing failed', response);
											}
										});
									}, 1000);
								},
								failure: function(fm, action) {
									console.log('Form submission failed', action);
								}
							});
						}
						// end of file upload
					}
					else {
						// "create_collection" mode is failed
						Ext.Msg.alert("Status", jsonResponse.msg);
					}
					
				}
			});
			
		}
	}]
});
Ext.define('TranscriptomicsUploader.view.Viewport', {
	extend: 'Ext.window.Window', 
	title: 'Upload Transcriptomics Data to Workspace',
	width: 600,
	resizable: false,
	id: "uploader",
	params: {
		//collectionId: "80ed8f0c-a155-4d3b-b1ed-c7a64f5c9cef",
		//authToken: "7bdef354fda3a014fd26e19d2c3d7a41e2c9b88ccc64d14f10fa1228ebad114ded06a5bd101813c1",
		//baseUrl: "http://hyun-imac.vbi.vt.edu:8888"
	},
	items: [{
		xtype: 'tabpanel',
		itemId: 'breadcrumb',
		border: false,
		tabBar: {
			hidden: true
		},
		height: 50,
		items: [{
			xtype: 'imagecomponent',
			itemId: 'step01',
			padding: 3,
			src: '/patric/images/transcriptomics_uploader_step1.png'
		}, {
			xtype: 'imagecomponent',
			itemId: 'step02',
			padding: 3,
			src: '/patric/images/transcriptomics_uploader_step2.png'
		}, {
			xtype: 'imagecomponent',
			itemId: 'step03',
			padding: 3,
			src: '/patric/images/transcriptomics_uploader_step3.png'
		}, {
			xtype: 'imagecomponent',
			itemId: 'step04',
			padding: 3,
			src: '/patric/images/transcriptomics_uploader_step4.png'
		}]
	}, {
		xtype: 'tabpanel',
		itemId: 'steps',
		border: false,
		tabBar: {
			hidden: true
		},
		//activeTab: 'step03',
		items: [{
			xtype: 'specifyfile',
			itemId: 'step01',
			title: 'Specify File'
		}, {
			xtype: 'mapgeneidentifiers',
			itemId: 'step02',
			title: 'Map Gene Identifiers'
		}, {
			xtype: 'describeexperiment',
			itemId: 'step03',
			title: 'Describe Experiment'
		}, {
			xtype: 'addtogroup',
			itemId: 'step04',
			title: 'Add to Group'
		}]
	}]
});
Ext.define('TranscriptomicsUploader.controller.ViewController', {
	extend: 'Ext.app.Controller',
	models: ['GenomeName'],
	views: 	['SpecifyFile', 'MapGeneIdentifiers', 'DescribeExperiment', 'AddToGroup'],
	stores: ['DataTypes', 'FileTypes', 'FileFormats', 'GeneIDTypes', 'GenomeNames', 'WorkspaceGroups']
});
Ext.application({
	name: 'TranscriptomicsUploader',
	controllers: ['ViewController']
	/*,
	launch: function() {
		uploader = Ext.create('TranscriptomicsUploader.view.Viewport',{}).show();
	}*/
});	
