Ext.define('VBI.Workspace.model.ExpressionExperiment', {
	extend: 'Ext.data.Model',
	idProperty: 'expid',
	fields: [ 	
		'accession', 'author', 'condition', 'description', {name:'eid', type:'int'},
		'expid', 'genes', 'institution', 'mutant', 'organism',
		'pi', {name:'platforms', type:'int'}, 'pmid', 'release_date', {name:'samples', type:'int'},
		'strain', 'timeseries', 'title',
		'source', 'origFileName', 'desc', 'cdate', 'mdate', 'data_type'
	]
});
Ext.define('VBI.Workspace.model.ExpressionSample', {
	extend: 'Ext.data.Model',
	idProperty: 'pid',
	fields: [
		'accession', {name:'channels', type:'int'}, 'condition', {name:'eid', type:'int'}, {name:'expmean', type:'float'},
		'expname', {name:'expstddev', type:'float'}, {name:'genes', type:'int'}, 'mutant', 'organism', 
		'pid', 'platform', 'release_date', 'samples', {name:'sig_log_ratio', type:'int'}, {name:'sig_z_score', type:'int'},
		'strain', 'timepoint',
		'organism', 'source'
	]
});Ext.define('VBI.Workspace.store.ExpressionExperiments', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ExpressionExperiment',
	model: 'VBI.Workspace.model.ExpressionExperiment',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		actionMethods: {
			read: 'POST'
		},
		api: {
			read: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getPublicExperiments'
		},
		extraParams: {
			trackIds: ''
		},
		reader: {
			type: 'json',
			root: 'results',
			totalProperty: 'total'
		},
		writer: {
			type: 'json',
			writeAllFields: false,
			root: 'experiment_info',
			encode: true
		},
		noCache: false
	},
	pageSize: 20,
	remoteSort: false,
	filterByTracks: function(tracks) {
		if (Ext.isArray(tracks)) {
			this.getProxy().extraParams.trackIds = tracks.join(",");
			this.load();
		}
		else if (Ext.isNumber(tracks)) {
			this.getProxy().extraParams.trackIds = tracks;
			this.load();
		}
	}
});
Ext.define('VBI.Workspace.store.ExpressionSamples', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ExpressionSample',
	model: 'VBI.Workspace.model.ExpressionSample',
	autoLoad: false,
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport',
		extraParams: {
			action: 'getPublicSamples'
		},
		reader: {
			type: 'json',
			root: 'results',
			totalProperty: 'total'
		},
		startParam: undefined,
		limitParam: undefined,
		pageParam: undefined,
		noCache: false
	},
	remoteSort: false
});
Ext.define('VBI.Workspace.view.columns.HeaderContainer', {
	applyColumnsState: function(columns) {
		if (!columns || !columns.length) {
			return;
		}

		var me 		= this,
			items	= me.items.items,
			count 	= items.length,
			i 		= 0,
			length 	= columns.length,
			c, col, columnState, index;

			for (c = 0; c < length; c++) {
				columnState = columns[c];

				for (index = count; index--; ) {
					col = items[index];
					//if (col.getStateId && col.getStateId() == columnState.id) {
					if (col.itemId == columnState.id) { //changed by Harry
						if (i !== index) {
							me.moveHeader(index, i);
						}

						if (col.applyColumnState) {
							col.applyColumnState(columnState);
						}
						++i;
						break;
					}
				}
			}
	},
	getColumnsState: function () {
		var me = this,
			columns = [],
			state;

		me.items.each(function (col) {
			state = col.getColumnState && col.getColumnState();
			if (state) {
				state.id = col.itemId; //added
				columns.push(state);
			}
		});

		return columns;
	}
});function renderExperimentTitle (value, metadata, record, rowIndex, colIndex, store) {
	return Ext.String.format('<a href="javascript:void(0);" onclick="launchExperimentDetail(\'{1}\')">{0}</a>', value, record.get('expid'));
};
function renderGeneCount (value, metadata, record, rowIndex, colIndex, store) {
	if (record.get("source") != "PATRIC") {
		return value;
	}
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=&cId=&dm=result&expId={1}&sampleId=&log_ratio=0&zscore=0">{0}</a>', 
			value, record.get("eid"));
	} else {
		return 0;
	}
};
function renderPubmedID (value, metadata, record, rowIndex, colIndex, store) {
	if (value != undefined && value != "") {
		return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', value);
	} else {
		return "";
	}
};

Ext.define('VBI.Workspace.view.columns.ExpressionExperiment', {
	defaults: {
		align: 'center',
		draggable: false,
		resizable: false
	},
	items: [
		{text:'Source',			itemId:'Experiment_source',		dataIndex:'source',		flex:1, align:'left'}, 
		{text:'Data Type',		itemId:'Experiment_datatype',	dataIndex:'data_type',	flex:1, align:'left'},
		{text:'Title',			itemId:'Experiment_title',		dataIndex:'title',		flex:4, align:'left', renderer:renderExperimentTitle}, 
		{text:'Comparisons',	itemId:'Experiment_samples',	dataIndex:'samples',	flex:1},
		{text:'Genes',			itemId:'Experiment_genes',		dataIndex:'genes',		flex:1, renderer:renderGeneCount}, 
		{text:'PubMed',			itemId:'Experiment_pmid',		dataIndex:'pmid',		flex:1, renderer:renderPubmedID}, 
		{text:'Organism',		itemId:'Experiment_organism',	dataIndex:'organism',	flex:3, align:'left', renderer:BasicRenderer}
	]
});
function linkToGeneList(value, metadata, record, rowIndex, colIndex, store) {
	if (record.get("source") != "PATRIC") {
		return value;
	}
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=&cId=&dm=result&expId={1}&sampleId={2}&log_ratio={3}&zscore={4}">{0}</a>', 
			value, record.get("eid"), record.get("pid"), 0, 0);
	} else {
		return 0;
	}
};
function linkToGeneListFold(value, metadata, record, rowIndex, colIndex, store) {
	if (record.get("source") != "PATRIC") {
		return value;
	}
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=&cId=&dm=result&expId={1}&sampleId={2}&log_ratio={3}&zscore={4}">{0}</a>', 
			value, record.get("eid"), record.get("pid"), 1, 0);
	} else {
		return 0;
	}
};
function linkToGeneListZScore(value, metadata, record, rowIndex, colIndex, store) {
	if (record.get("source") != "PATRIC") {
		return value;
	}
	if (value != 0) {
		return Ext.String.format('<a href="TranscriptomicsGene?cType=&cId=&dm=result&expId={1}&sampleId={2}&log_ratio={3}&zscore={4}">{0}</a>', 
			value, record.get("eid"), record.get("pid"), 0, 2);
	} else {
		return 0;
	}
};

Ext.define('VBI.Workspace.view.columns.ExpressionSample', {
	defaults: {
		align:'center',
		draggable: false,
		resizable: false
	},
	items: [
		{text:'Accession',						itemId:'Sample_accession',		dataIndex:'accession',		flex:1, align:'left' ,hidden:true}, 
		{text:'Title',							itemId:'Sample_expname',		dataIndex:'expname',		flex:4, align:'left', renderer:BasicRenderer}, 
		{text:'Genes',							itemId:'Sample_genes',			dataIndex:'genes',			flex:1, renderer:linkToGeneList}, 
		{text:'Significant genes(Log Ratio)',	itemId:'Sample_sig_log_ratio',	dataIndex:'sig_log_ratio',	flex:1, renderer:linkToGeneListFold}, 
		{text:'Significant genes(Z Score)',		itemId:'Sample_sig_z_score',	dataIndex:'sig_z_score',	flex:1, renderer:linkToGeneListZScore}, 
		{text:'Organism',						itemId:'Sample_organism',		dataIndex:'organism',		flex:1, align:'left', hidden: true, renderer:BasicRenderer}, 
		{text:'Strain', 						itemId:'Sample_strain',			dataIndex:'strain',			flex:1, align:'left', renderer:BasicRenderer}, 
		{text:'Gene Modification',				itemId:'Sample_mutant',			dataIndex:'mutant',			flex:1, align:'left', renderer:BasicRenderer}, 
		{text:'Experimental Condition',			itemId:'Sample_condision',		dataIndex:'condition',		flex:1, align:'left', renderer:BasicRenderer}, 
		{text:'Time Point',						itemId:'Sample_timepoint',		dataIndex:'timepoint',		flex:1, align:'left', renderer:BasicRenderer}
	]
});
Ext.define('VBI.Workspace.view.columns.HeaderContainer', {
	applyColumnsState: function(columns) {
		if (!columns || !columns.length) {
			return;
		}

		var me 		= this,
			items	= me.items.items,
			count 	= items.length,
			i 		= 0,
			length 	= columns.length,
			c, col, columnState, index;

			for (c = 0; c < length; c++) {
				columnState = columns[c];

				for (index = count; index--; ) {
					col = items[index];
					//if (col.getStateId && col.getStateId() == columnState.id) {
					if (col.itemId == columnState.id) { //changed by Harry
						if (i !== index) {
							me.moveHeader(index, i);
						}

						if (col.applyColumnState) {
							col.applyColumnState(columnState);
						}
						++i;
						break;
					}
				}
			}
	},
	getColumnsState: function () {
		var me = this,
			columns = [],
			state;

		me.items.each(function (col) {
			state = col.getColumnState && col.getColumnState();
			if (state) {
				state.id = col.itemId; //added
				columns.push(state);
			}
		});

		return columns;
	}
});Ext.define('VBI.Workspace.view.group.DetailToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.detailtoolbar',
	items: [{ 
		text: '<font color=#fff><b>All Groups</b></font>', 
		iconCls: 'leftarrow',
		overCls: '',
		pressedCls: '',
		style: {
			'background-color': '#0a4773'
		},
		minWidth: 95,
		itemId: 'backButton', 
		scope: this
	}]
});/**
 * @class GroupExplorer.view.group.InfoEditor
 * @extends Ext.form.Panel
 * @xtype groupinfoeditor
 *
 * This class implements an info/edit panel for a single group.
 */
Ext.define('VBI.Workspace.view.group.ExperimentInfoEditor', {
	extend: 'Ext.form.Panel',
	alias: 'widget.experimentinfoeditor',
	width: 225, 
	minWidth: 225, 
	bodyPadding: 5, 
	cls: 'x-infoeditor-view',
	fieldDefaults: {
		labelAlign: 'left',
		anchor: '100%',
		labelStyle: 'font-weight:bold'
	},
	items: [{
		xtype: 'displayfield', 
		itemId: 'title',
		name: 'title',
		hideLabel: true,
		fieldStyle: {
			fontSize: '15px',
			fontWeight: 'bold'
		},
		style: {
			marginBottom: '0px'
		}
	}, {
		xtype: 'displayfield', 
		name: 'samples',
		hideLabel: true,
		fieldStyle: {
			paddingTop: '0px'
		}
	}, {
		xtype: 'displayfield',
		value: 'Transcriptomics Experiment', 
		fieldStyle: {
			paddingBottom: '5px',
			borderTop: '1px dashed #000000',
			borderBottom: '1px dashed #000000'
		}
	}, {
		xtype: 'displayfield',
		name: 'organism', 
		itemId: 'organism', 
		fieldLabel: 'Platform Organism',
		labelAlign: 'top'
	}, {
		xtype: 'displayfield',
		name: 'pmid', 
		itemId: 'pmid', 
		fieldLabel: 'Pubmed ID',
		labelWidth: 80,
		renderer: function(value, record) {
			return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target=_blank>{0}</a>', value);
		}
	}, {
		xtype: 'displayfield',
		name: 'description', 
		itemId: 'description', 
		fieldLabel: 'Description',
		labelAlign: 'top'
	}, {
		xtype: 'displayfield',
		name: 'updated',
		fieldLabel: 'Last modified',
		labelWidth: 90,
		value: 'none'
	}, {
		xtype: 'displayfield',
		name: 'created',
		fieldLabel: 'Uploaded',
		labelWidth: 70,
		value: 'none'
	}, {
		xtype: 'displayfield',
		name: 'file',
		fieldLabel: 'Source file',
		labelWidth: 70,
		value: 'none'
	}],
	loadRecord: function(record) {
		this.record = record;
		
		var subtitle;
		if (record.get("samples") > 1) {
			subtitle = "(" + record.get("samples") + " comparisons)";
		} else {
			subtitle = "(" + record.get("samples") + " comparison)";
		}
		
		this.getForm().setValues({
			title: record.get("title"), 
			samples: subtitle,
			description: record.get("desc"), 
			organism: record.get("organism"),
			pmid: (record.get("pmid")!=0)?record.get("pmid"):"",
			updated: Ext.Date.format(Ext.Date.parse(record.get("mdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			created: Ext.Date.format(Ext.Date.parse(record.get("cdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			file: record.get("origFileName")
		});
	}
});
Ext.define('VBI.Workspace.view.selection.CheckboxModel', {
	extend: 'Ext.selection.CheckboxModel',
	getHeaderConfig: function() {
		var me = this,
			showCheck = me.showHeaderCheckbox !== false;
		return {
			itemId: me.itemId, //added
			isCheckerHd: showCheck,
			text : '&#160;',
			width: me.headerWidth,
			sortable: false,
			draggable: false,
			resizable: false,
			hideable: false,
			menuDisabled: true,
			dataIndex: '',
			cls: showCheck ? Ext.baseCSSPrefix + 'column-header-checkbox ' : '',
			renderer: Ext.Function.bind(me.renderer, me),
			editRenderer: me.editRenderer || me.renderEmpty,
			locked: me.hasLockedHeader()
		};
	}
});Ext.define('VBI.Workspace.view.toolbar.ExpressionExperiment', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.expressionexperimenttoolbar',
	getSelectedID: function(type) {
		var selection = Ext.getCmp('workspace_listview_expview').child('#experimentview').getSelectionModel().getSelection();
		
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "No experiment was selected");
			return null;
		}
		else {
			var selectedIDs = new Array();
			Ext.Array.each(selection, function(item) {
				if (type != undefined) {
					selectedIDs.push(item.get(type));
				} else {
					selectedIDs.push(item.get("expid"));
				}
			});
			return selectedIDs;
		}
	},
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [{
		title: 'View',
		xtype: 'buttongroup',
		width: 115,
		items: [{
			xtype: 'tbar_btn_genelist',
			handler: function(btn, e) {
				var selection = btn.findParentByType('toolbar').getSelectedID(),
					expIds = new Array(),
					colIds = new Array(),
					store = Ext.getStore("ExpressionExperiments"),
					countComparisons = 0,
					maxComparisions = 100,
					param;
				//console.log(selection);
					
				Ext.Array.each(selection, function(expid) {
					item = store.getById(expid);
					countComparisons += item.get("samples");
					colIds.push(item.get("expid"));
				});
				//console.log(countComparisons);
				if (countComparisons >= maxComparisions) {
					alert("You have exceeded the limit of comparisons. Please lower than "+maxComparisions);
					return false;
				}
				
				param = "&expId=" + expIds.join(",") + "&sampleId=&colId=" + colIds.join(",");
				if (expIds.length > 0 || colIds.length >0) {
					//console.log (expIds, colIds);
					this.fireEvent('runGeneList', param);
				}
			}
		}]
	}, {
		title: 'Download',
		xtype: 'buttongroup',
		width: 115,
		items: [{
			scale: 'small',
			iconAlign: 'left',
			width: 110,
			text: 'Table',
			icon: '/patric/images/toolbar_table.png',
			xtype: 'splitbutton',
			menu: [{
				xtype: 'tbar_menu_dn_tb_txt',
				handler: function() {
					this.fireEvent('downloadGrid','txt');
				}
			}, {
				xtype: 'tbar_menu_dn_tb_xls',
				handler: function() {
					this.fireEvent('downloadGrid','xlsx');
				}
			}]
		}]
	}, '->', '-',
	{
		xtype: 'tbar_btngrp_help'
	}]
});
Ext.define('VBI.Workspace.view.toolbar.ExpressionSample', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.expressionsampletoolbar',
	getSelectedID: function() {
		var selection = Ext.getCmp('workspace_detailview').child('#panel_grid').child('#experimentdetail').getSelectionModel().getSelection();
		
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "No sample was selected");
			return null;
		} else {
			var selectedIDs = new Array();
			Ext.Array.each(selection, function(item) {
				selectedIDs.push(item.get("pid"));
			});
			return selectedIDs;
		}
	},
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [{
			xtype: 'button',
			scale: 'small',
			iconAlign: 'left',
			text: '<< Experiment list',
			handler: function(btn, e) {
				this.fireEvent('viewExpList');
			}
		}, {
			xtype: 'tbspacer',
			width: 112
		}, {
			title: 'View',
			xtype: 'buttongroup',
			width: 115,
			columns: 1,
			items: [{
				xtype: 'tbar_btn_genelist',
				handler: function(btn, e) {
					var selection = btn.findParentByType('toolbar').getSelectedID(),
						expId = Ext.getCmp('workspace_experimentinfoeditor').record.get("expid"),
						store = Ext.getStore("ExpressionExperiments"),
						maxComparisions = 100,
						param = "";
					console.log(selection);
					if (selection != undefined) {
						if (selection.length >= maxComparisions) {
							alert("You have exceeded the limit of comparisons. Please lower than "+maxComparisions);
							return false;
						}
						param = "&expId=&sampleId=&colId=" + expId + ":" + selection.join("+").replace(new RegExp(expId, 'g'), '');
						
						//console.log(param);
						this.fireEvent('runGeneList', param);
					}
				}
			}]
		}, {
			title: 'Download',
			xtype: 'buttongroup',
			width: 115,
			items: [{
				scale: 'small',
				iconAlign: 'left',
				width: 110,
				text: 'Table',
				icon: '/patric/images/toolbar_table.png',
				xtype: 'splitbutton',
				menu: [{
					xtype: 'tbar_menu_dn_tb_txt',
					handler: function() {
						this.fireEvent('downloadGrid','txt');
					}
				},
				{
					xtype: 'tbar_menu_dn_tb_xls',
					handler: function() {
						this.fireEvent('downloadGrid','xlsx');
					}
				}]
			}]
		}, '->', '-',
		{
			xtype: 'tbar_btngrp_help'
		}
	]
});
Ext.define('VBI.Workspace.view.toolbar.Paging', {
	extend: 'Ext.toolbar.Paging',
	alias: 'widget.patricpagingtoolbar',
	beforePageSizeText: 'Show',
	afterPageSizeText: 'per page',
	displayMsg : 'Displaying record {0} - {1} of {2}',
	displayInfo: true,
	maxPageSize: 5000,
	maskOnDisable: true,
	getPagingItems: function() {
		var me = this;
		return [{
			itemId: 'first',
			tooltip: me.firstText,
			overflowText: me.firstText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-first',
			disabled: true,
			handler: me.moveFirst,
			scope: me
		},{
			itemId: 'prev',
			tooltip: me.prevText,
			overflowText: me.prevText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-prev',
			disabled: true,
			handler: me.movePrevious,
			scope: me
		},
		'-',
		me.beforePageText,
		{
			xtype: 'numberfield',
			itemId: 'inputItem',
			name: 'inputItem',
			cls: Ext.baseCSSPrefix + 'tbar-page-number',
			allowDecimals: false,
			minValue: 1,
			hideTrigger: true,
			enableKeyEvents: true,
			keyNavEnabled: false,
			selectOnFocus: true,
			submitValue: false,
			width: me.inputItemWidth,
			margins: '-1 2 3 2',
			listeners: {
				scope: me,
				keydown: me.onPagingKeyDown,
				blur: me.onPagingBlur
			}
		},{
			xtype: 'tbtext',
			itemId: 'afterTextItem',
			text: Ext.String.format(me.afterPageText, 1)
		},
		'-',
		{
			itemId: 'next',
			tooltip: me.nextText,
			overflowText: me.nextText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-next',
			disabled: true,
			handler: me.moveNext,
			scope: me
		},{
			itemId: 'last',
			tooltip: me.lastText,
			overflowText: me.lastText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-last',
			disabled: true,
			handler: me.moveLast,
			scope: me
		},
		'->','-', 
		/* modification start */
		me.beforePageSizeText,
		{
			xtype: 'numberfield',
			itemId: 'pagesize',
			cls: Ext.baseCSSPrefix + 'tbar-page-number',
			allowDecimals: false,
			minValue: 1,
			maxValue: 64000,
			hideTrigger: true,
			enableKeyEvents: true,
			selectOnFocus: true,
			submitValue: false,
			width: 50,
			margins: '-1 2 3 2',
			value: me.pageSize,
			stateful: true,
			stateId: 'pagesize',
			stateEvents: ['savePageSize'],
			applyState: function(state) {
				if (state != undefined && state.value != undefined) {
					if (me.maxPageSize < state.value) {
						this.setValue(me.maxPageSize);
					} else {
						me.store.pageSize = state.value;
						this.setValue(state.value);
					}
				}
			},
			initComponent: function () {
				var me = this;
				if (me.allowOnlyWhitespace === false) {
					me.allowBlank = false;
				}
				me.callParent();
				me.addEvents('autosize', 'keydown', 'keyup', 'keypress', 'change');
				//me.addStateEvents('change');
				me.setGrowSizePolicy();
				if (Ext.state.Manager.get("pagesize") == undefined || Ext.state.Manager.get("pagesize").value == undefined) {
					me.setValue(20);
				}
		    },
			listeners: {
				scope: me,
				specialKey: function(field, e) {
					if (e.getKey() == e.ENTER) {
						var	value = field.getValue(),
							valueIsNull = value === null;
					
						if (valueIsNull == false) {
							me.updateStore();
						}
					}
				}
			}
		},
		me.afterPageSizeText,
		{
			itemId: 'refresh',
			text: 'Apply',
			style: {
				'border-color':'#81a4d0',
				'background-color':'#dbeeff',
				'background-image':'-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler: function(){
				me.updateStore();
			},
			scope: me
		},
		'-', 
		{
			itemId: 'saveState',
			text: 'Apply to ALL tables',
			style: {
				'border-color':'#81a4d0',
				'background-color':'#dbeeff',
				'background-image':'-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler: function(){
				if (me.updateStore() != false) {
					
					me.child('#pagesize').fireEvent('savePageSize');
					
					var dt = new Date();
					dt.setTime(dt.getTime() + 1000);
					while (new Date().getTime() < dt.getTime());
				}
			},
			scope: me
		},
		'-'
		];
	},
	showMessageTip: function(title, msg) {
		var me = this,
			tip = Ext.create('Ext.tip.QuickTip', {
				target: me.child('#pagesize').el,
				closable: true,
				autoHide: false,
				anchor: 'bottom',
				bodyPadding: 10,
				title: title,
				html: msg
			}).show();
	},
	updateStore: function() {
		var me = this,
			store = me.getStore(),
			pagesize = me.child('#pagesize').getValue();

		if (pagesize > me.maxPageSize) {

			this.showMessageTip('Warning', 'Maximum number of visible rows must be smaller than ' + me.maxPageSize + '.');
			return false;

		} else {

			if (pagesize != store.pageSize) {
				store.currentPage = 1;
				store.pageSize = pagesize;
				store.load();
			}
		}
	},
	initComponent: function() {
		var me = this;
		//getting store
		if (me.store != null && typeof me.store == 'string') {
			me.store = Ext.getStore(me.store);
		}
		//set pageSize
		if (me.store != null && me.store.pageSize != null) {
			if (typeof me.store.pageSize == 'string') {
				me.pageSize = parseInt(me.store.pageSize);
			} else if (typeof me.store.pageSize == 'number') {
				me.pageSize = me.store.pageSize;
			}
		}
		this.callParent();
	},
	listeners: {
		afterlayout: function(me, e) {
			var pgsize = me.child('#pagesize'),
				global = Ext.state.Manager.get("pagesize");
			if (global != undefined && global.value != undefined) {
				if (pgsize.value != global.value) {
					pgsize.setValue(global.value);
					me.updateStore();
				}
			}
		}
	}
});Ext.define('VBI.Workspace.view.DetailView', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.detailview',
	requires: [
		'VBI.Workspace.view.group.ExperimentInfoEditor',
		'VBI.Workspace.view.toolbar.ExpressionSample',
		'VBI.Workspace.view.selection.CheckboxModel'
	],
	border: false,
	layout: 'border',
	items: [{
		itemId: 'panel_info',
		region: 'west',
		xtype: 'panel',
		layout: 'card',
		border: false,
		activeItem: 'experimentinfo',
		items:[{
			itemId: 'experimentinfo',
			xtype: 'experimentinfoeditor',
			id: 'workspace_experimentinfoeditor'
		}]
	}, {
		itemId: 'panel_toolbar',
		region: 'north',
		xtype: 'panel',
		layout: 'card',
		border: false,
		activeItem: 'experiment',
		items: [{
			itemId: 'experiment',
			xtype: 'expressionexperimenttoolbar',
			id: 'expressionexperimenttoolbar'
		}, {
			itemId: 'sample',
			xtype: 'expressionsampletoolbar'
		}]
	}, {
		itemId: 'panel_grid',
		region: 'center',
		xtype: 'panel',
		layout: 'card',
		border: false,
		activeItem: 'experimentview',
		items: [{
			// expression experiment group detail view
			itemId: 'experimentview',
			xtype: 'gridpanel',
			store: 'ExpressionExperiments',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.ExpressionExperiment'),
			stateId: 'experimentlist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'patricpagingtoolbar',
				store: 'ExpressionExperiments',
				dock: 'bottom',
				displayInfo: true
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}, {
			// expression experiment detail view
			itemId: 'experimentdetail',
			xtype: 'gridpanel',
			store: 'ExpressionSamples',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.ExpressionSample'),
			stateId: 'comparisonlist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}]
	}]
});
Ext.define('VBI.Workspace.view.ListView', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.listview',
	requires: [
		'VBI.Workspace.view.toolbar.ExpressionExperiment',
		'VBI.Workspace.view.columns.ExpressionExperiment',
		'VBI.Workspace.view.selection.CheckboxModel'
	],
	border: false,
	layout: 'border',
	items: [{
		xtype: 'panel',
		region: 'center',
		layout: 'card',
		activeItem: 'experimentview',
		id: 'workspace_listview_expview',
		flex: 1,
		border: false,
		items: [{
			// list of expression experiments
			itemId: 'experimentview',
			xtype: 'gridpanel',
			store: 'ExpressionExperiments',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.ExpressionExperiment'),
			stateful: true,
			stateId: 'experimentlist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'expressionexperimenttoolbar',
				dock: 'top'
			}, {
				xtype: 'patricpagingtoolbar',
				store: 'ExpressionExperiments',
				dock: 'bottom'
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}]
	}]
});
/**
* define Toolbar Buttons
*/

Ext.define('VBI.Workspace.view.Toolbar.button.GeneList', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_genelist',
	scale: 'small',
	iconAlign: 'left',
	text: 'Gene List',
	width: 110,
	icon: '/patric/images/toolbar_icon_genelist.png'
});

Ext.define('VBI.Workspace.view.Toolbar.button.ShowHide', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_showhide',
	scale: 'large',
	iconAlign: 'left',
	icon: '/patric/images/toolbar_hideshow.png',
	text: 'Show/Hide'
});

Ext.define('VBI.Workspace.view.Toolbar.button.ResetColumnState', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_resetcolumnstate',
	scale: 'large',
	iconAlign: 'left',
	text: 'Reset<br/>Configs'
});

/**
* define menu Items
*/
Ext.define('VBI.Workspace.view.Toolbar.menu.Download_Table_Txt', {
	extend: 'Ext.menu.Item',
	alias: 'widget.tbar_menu_dn_tb_txt',
	scale: 'small',
	iconAlign: 'left',
	text: 'Text File (.txt)',
	icon: '/patric/images/toolbar_text.png'
});

Ext.define('VBI.Workspace.view.Toolbar.menu.Download_Table_Xls', {
	extend: 'Ext.menu.Item',
	alias: 'widget.tbar_menu_dn_tb_xls',
	scale: 'small',
	iconAlign: 'left',
	text: 'Excel file (.xlsx)',
	icon: '/patric/images/toolbar_excel.png'
});


/**
* define Toolbar ButtonGroups
*/
Ext.define('VBI.Workspace.view.Toolbar.buttongroup.Help', {
	extend: 'Ext.container.ButtonGroup',
	alias: 'widget.tbar_btngrp_help',
	title: 'Help',
	items: [{
		scale: 'small',
		text: 'PATRIC FAQs',
		icon: '/patric/images/toolbar_faq_small.png',
		handler: function() {
			window.open("http://enews.patricbrc.org/faqs/", "_new", "menubar=1,resizable=1,scrollbars=1, fullscreen=1, toolbar=1,titlebar=1,status=1");
		}
	}]
});Ext.define('VBI.Workspace.view.Viewport', {
	extend: 'Ext.container.Viewport',
	requires: [
		'VBI.Workspace.view.Toolbar',
		'VBI.Workspace.view.toolbar.Paging',
		'VBI.Workspace.view.ListView',
		'VBI.Workspace.view.DetailView'
	],
	layout: 'border',
	items: [{
		region: 'center',
		xtype: 'panel',
		layout: 'border',
		items: [{
			region: 'center',
			xtype: 'panel',
			id: 'workspace_view',
			layout: 'card',
			activeItem: 'listview',
			border: false,
			items: [{
				itemId: 'listview',
				xtype: 'listview',
				id: 'workspace_listview',
			}, {
				itemId: 'detailview',
				xtype: 'detailview',
				id: 'workspace_detailview'
			}]
		}]
	}],
	onRender: function() {
		var me = this;
		me.callParent(arguments);
		me.width = Ext.Element.getViewportWidth() - 15;
		me.height = Math.max(580, Ext.Element.getViewportHeight() - 320);
	},
	initComponent : function() {
		var me = this,
			html = document.body.parentNode,
			el;
			
		me.callParent(arguments);
		me.el = el = Ext.get('wksp');
	},
	fireResize: function(width, height) {
		if (width != this.width || height != this.height) {
			this.setSize(width - 15, Math.max(580, height - 320));
		}
	}
});Ext.define('VBI.Workspace.controller.Experiment', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'expressionexperimenttoolbar button': {
				runGeneList: this.runGeneList
			},
			'expressionexperimenttoolbar menuitem': {
				downloadGrid: this.downloadExperiments
			},
			'expressionsampletoolbar button': {
				runGeneList: this.runGeneList,
				viewExpList: this.viewExperimentList
			},
			'expressionsampletoolbar menuitem': {
				downloadGrid: this.downloadSamples
			},
			'detailview': {
				viewExpDetail: this.viewExperimentDetail
			}
		});
	},
	runGeneList: function(param) {
		document.location.href = "/portal/portal/patric/TranscriptomicsGene?cType=&cId=&dm=result&log_ratio=&zscore="+param;
	},
	viewExperimentList: function() {
		Ext.getCmp('workspace_view').getLayout().setActiveItem('listview');
	},
	viewExperimentDetail: function(expid) {
		var detailview = Ext.getCmp('workspace_detailview');
		detailview.child('#panel_info').getLayout().setActiveItem('experimentinfo');
		detailview.child('#panel_toolbar').getLayout().setActiveItem('sample');
		detailview.child('#panel_grid').getLayout().setActiveItem('experimentdetail');
		
		Ext.getStore('ExpressionSamples').getProxy().setExtraParam("expid", expid);
		Ext.getStore('ExpressionSamples').load();
		
		var record = Ext.getStore('ExpressionExperiments').getById(expid);
		Ext.getCmp('workspace_experimentinfoeditor').loadRecord(record);
		Ext.getCmp('workspace_view').getLayout().setActiveItem('detailview');
	},
	downloadExperiments: function(type) {
		var store = Ext.getStore("ExpressionExperiments"),
			USERExperiments = new Array();
		
		Ext.Array.each(store.getRange(), function(item) {			
			USERExperiments.push(item.internalId);
		});
		
		var fids = {
			"USERExperiments": USERExperiments
		};
		
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "Workspace";
		Ext.getDom("fileformat").value = type;
		Ext.getDom("idType").value = "ExpressionExperiment";
		
		Ext.Ajax.request({
			url: "/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getToken",
			success: function(response) {
				fids.token = response.responseText;
				Ext.getDom("fids").value = Ext.JSON.encode(fids);
				Ext.getDom("fTableForm").submit();
			}
		});
	},
	downloadSamples: function(type) {
		var store	= Ext.getStore("ExpressionSamples"),
			expInfo	= Ext.getCmp('workspace_experimentinfoeditor').record,
			fids = {
				source: expInfo.get("source"),
				eid: expInfo.get("eid"),
				expId: expInfo.get("expid")
			};
		
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "Workspace";
		Ext.getDom("fileformat").value = type;
		Ext.getDom("idType").value = "ExpressionSample";
		
		Ext.Ajax.request({
			url: "/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getToken",
			success: function(response) {
				fids.token = response.responseText;
				Ext.getDom("fids").value = Ext.JSON.encode(fids);
				Ext.getDom("fTableForm").submit();
			}
		});
	}
});Ext.Loader.setConfig({
	enabled: true
});

Ext.application({
	name: 'VBI.Workspace',
	autoCreateViewport: true,
	init: function() {
	},
	launch: function() {
		// This is fired as soon as the page is ready
		Ext.fly(document.body).setStyle('overflow', 'auto');
	},
	id: 'workspace',
	models: ['ExpressionExperiment', 'ExpressionSample'],
	stores: ['ExpressionExperiments', 'ExpressionSamples'],
	controllers: ['Experiment']
});

function BasicRenderer(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="' + value + '" data-qclass="x-tip"';
	return value;
}

function launchExperimentDetail(expid) {
	Ext.getCmp("workspace_detailview").fireEvent('viewExpDetail', expid);
}