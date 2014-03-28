/**
 * @class VBI.GeneExpression.model.CategoryCount
 * @extends Ext.data.Model
 * 
 * This class defines a data model for category-and-count data in order to draw a chart.
 */
Ext.define('VBI.GeneExpression.model.CategoryCount', {
	extend: 'Ext.data.Model',
	idProperty: 'rownum',
	fields: [
		{name: 'rownum', type: 'int'},
		{name: 'category', type: 'string'},
		{name: 'count', type: 'int'}
	]
});
/**
 * @class VBI.GeneExpression.model.Gene
 * @extends Ext.data.Model
 *
 * This class defines a data model for gene properties combined with relevant expression data.
 *
 */
Ext.define('VBI.GeneExpression.model.Gene', {
	extend: 'Ext.data.Model',
	idProperty: 'pid',
	fields: [
		'exp_id', 'exp_accession', 'exp_platform', 'exp_samples', {name:'pid', type:'int'}, 'exp_locustag', 
		{name:'exp_pavg', type:'float', useNull:false}, {name:'exp_pratio', type:'float', useNull:false}, 
		{name:'exp_zscore', type:'float', useNull:false},
		'exp_name', 'exp_channels', 'exp_timepoint', 'exp_organism', 'exp_strain', 'exp_mutant', 'exp_condition', 'pmid',
		'na_feature_id', 'patric_locus_tag', 'figfam_id', 
		{name:'exp_geneid', type:'int'}
	]
});/**
 * @class VBI.GeneExpression.store.Conditions
 * @extends Ext.data.Store
 *
 * This class implements a store for one of metadata, Experimental Condition.
 */
Ext.define('VBI.GeneExpression.store.Conditions', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'condition'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'exp_stat'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(me, operation, eOpts) {
			me.proxy.extraParams = Ext.Object.merge(me.proxy.extraParams, VBI.GeneExpression.param);
		},
		load: function(me, records, successful, eOpts) {
			// copy top 5 data points (exclude N/A) to ConditionsTop5 store
			if (successful) {
				var data = new Array();
				for (i=0; i<records.length; i++) {
					if (records[i].get("rownum") < 7) {
						if (records[i].get("category")!="N/A") {
							data[i] = records[i].data;
						}
					}
				}
				data = Ext.Array.splice(Ext.Array.clean(data), 0, 5);
				Ext.getStore('ConditionsTop5').loadData(data);
				Ext.getStore('ConditionsTop5').sort('count', 'ASC');
			}
		}
	}
});/**
 * @class VBI.GeneExpression.store.ConditionsTop5
 * @extends Ext.data.Store
 *
 */
Ext.define('VBI.GeneExpression.store.ConditionsTop5', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	autoLoad: false
});/**
 * @class VBI.GeneExpression.store.Genes
 * @extends Ext.data.Store
 *
 * This class implements a store for genes.
 */
Ext.define('VBI.GeneExpression.store.Genes', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.Gene',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'features'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'results'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(store, operation, eOpts) {
			store.proxy.extraParams = Ext.Object.merge(store.proxy.extraParams, VBI.GeneExpression.param);
		},
		load: function(){
			this.updateRecordCount();
		}
	},
	updateRecordCount: function() {
		count = this.getCount();
		(count==1) ? countStr=count+' comparison' : countStr=count+' comparisons';
		Ext.getCmp('filterReport').setText("<b>"+countStr+"</b>");
	},
	filterField: function(fieldname, cutoff) {
		this.filter([
			Ext.create('Ext.util.Filter', {
				filterFn: function(item) {
					if (item.get(fieldname)>=cutoff || item.get(fieldname)<=(-1)*cutoff) {
						return true;
					} else {
						return false;
					}
				}
			})
		]);
	},
	filterOnFly: function(param) {
		this.clearFilter();
		if (param.keyword != null && param.keyword != "") {
			//this.filter("exp_name", param.keyword);
			this.filter([
				Ext.create('Ext.util.Filter', {property: "exp_name", value: param.keyword, root: 'data', anyMatch: true})
			]);
			// TODO: add search on accession, strain, mutant, condition, and timepoint, (pmid if possible)
		}
		if (param.log_ratio > 0) {
			this.filterField("exp_pratio", param.log_ratio);
		}
		if (param.zscore > 0) {
			this.filterField("exp_zscore", param.zscore);
		}
		//other filters
		/*
		if (param.accession != null && param.accession != "") {
			this.filter("exp_accession", param.accession);
		}*/
		
		this.updateRecordCount();
	}
});
/**
 * @class VBI.GeneExpression.store.LogRatios
 * @extends Ext.data.Store
 *
 * This class implements a store for Log Ratios.
 */
Ext.define('VBI.GeneExpression.store.LogRatios', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'log_ratio'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'exp_stat'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(me, operation, eOpts) {
			me.proxy.extraParams = Ext.Object.merge(me.proxy.extraParams, VBI.GeneExpression.param);
			Ext.get("p-chartlogratio").mask("loading");
		},
		load: function() {
			Ext.get("p-chartlogratio").unmask();
		}
	}
});
/**
 * @class VBI.GeneExpression.store.Mutants
 * @extends Ext.data.Store
 *
 * This class implements a store for one of metadata, Gene Modification.
 */
Ext.define('VBI.GeneExpression.store.Mutants', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'mutant'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'exp_stat'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(me, operation, eOpts) {
			me.proxy.extraParams = Ext.Object.merge(me.proxy.extraParams, VBI.GeneExpression.param);
		},
		load: function(me, records, successful, eOpts) {
			// copy top 5 data points (exclude N/A) to Top5 store
			if (successful) {
				var data = new Array();
				for (i=0; i<records.length; i++) {
					if (records[i].get("rownum") < 7) {
						if (records[i].get("category")!="N/A") {
							data[i] = records[i].data;
						}
					}
				}
				data = Ext.Array.splice(Ext.Array.clean(data), 0, 5);
				Ext.getStore('MutantsTop5').loadData(data);
				Ext.getStore('MutantsTop5').sort('count', 'ASC');
			}
		}
	}
});
/**
 * @class VBI.GeneExpression.store.MutantsTop5
 * @extends Ext.data.Store
 *
 */
Ext.define('VBI.GeneExpression.store.MutantsTop5', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	autoLoad: false
});/**
 * @class VBI.GeneExpression.store.Strains
 * @extends Ext.data.Store
 *
 * This class implements a store for one of metadata, Strain.
 */
Ext.define('VBI.GeneExpression.store.Strains', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'strain'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'exp_stat'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(me, operation, eOpts) {
			me.proxy.extraParams = Ext.Object.merge(me.proxy.extraParams, VBI.GeneExpression.param);
			Ext.get("CategoryPieStrain").mask("loading");
		},
		load: function(me, records, successful, eOpts) {
			Ext.get("CategoryPieStrain").unmask();
			
			// copy top 5 data points (exclude N/A) to Top5 store
			if (successful) {
				var data = new Array();
				for (i=0; i<records.length; i++) {
					if (records[i].get("rownum") < 7) {
						if (records[i].get("category")!="N/A") {
							data[i] = records[i].data;
						}
					}
				}
				data = Ext.Array.splice(Ext.Array.clean(data), 0, 5);
				Ext.getStore('StrainsTop5').loadData(data);
				Ext.getStore('StrainsTop5').sort('count', 'ASC');
			}
		}
		
	}
});
/**
 * @class VBI.GeneExpression.store.StrainsTop5
 * @extends Ext.data.Store
 *
 */
Ext.define('VBI.GeneExpression.store.StrainsTop5', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	autoLoad: false
});/**
 * @class VBI.GeneExpression.store.ZScores
 * @extends Ext.data.Store
 *
 * This class implements a store for Z score.
 */
Ext.define('VBI.GeneExpression.store.ZScores', {
	extend: 'Ext.data.Store',
	model: 'VBI.GeneExpression.model.CategoryCount',
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/TranscriptomicsGeneExp/TranscriptomicsGeneExpWindow?action=b&cacheability=PAGE',
		extraParams: {
			storeType: 'z_score'
		},
		pageParam: undefined,
		startParam: undefined,
		limitParam: undefined,
		reader: {
			type: 'json',
			root: 'exp_stat'
		},
		noCache: false
	},
	autoLoad: true,
	listeners: {
		beforeload: function(store, operation, eOpts) {
			store.proxy.extraParams = Ext.Object.merge(store.proxy.extraParams, VBI.GeneExpression.param);
		}
	}
});
/**
 * @class VBI.GeneExpression.view.CategoryBarChart
 * @extends Ext.chart.Chart
 * @xtype categorybarchart
 *
 * This class implements a chart for category-and-count type data.
 * This class will be used for Top5 charts.
 */
Ext.define('VBI.GeneExpression.view.CategoryBarChart', {
	extend: 'Ext.chart.Chart',
	alias: 'widget.categorybarchart',
	axes: [{
		type: 'Numeric',
		position: 'bottom',
		fields: ['count'],
		title: 'Comparisions',
		minimum: 0,
		majorTickSteps: 1
	}, {
		type: 'Category',
		position: 'left',
		fields: ['category']
	}],
	series: [{
		type: 'bar',
		yField: 'count',
		xField: 'category',
		label: {
			display: 'insideEnd',
			field: 'count',
			contrast: true
		}
	}],
	theme: 'PATRIC'
});
/**
 * @class VBI.GeneExpression.view.CategoryPieChart
 * @extends Ext.chart.Chart
 * @xtype categorypiechart
 *
 * This class implements a default setting for pie chart.
 */
Ext.define('VBI.GeneExpression.view.CategoryPieChart', {
	extend: 'Ext.chart.Chart',
	alias: 'widget.categorypiechart',
	animate: true,
	series: [{
		type: 'pie',
		field: 'count',
		highlight: {
			segment: {
				margin:20
			}
		},
		donut: 20,
		label: {
			field: 'category',
			display: 'rotate',
			contrast: true
		}
		/*,
		listeners: {
			'itemmouseup': function(item, obj){
				console.log(item);
				var param = new Object();
				param.condition = item.storeItem.getId();
				this.chart.fireEvent('filter', param);
				return true;
			}
		}*/
	}],
	theme: 'PATRIC'
});
/**
 * @class VBI.GeneExpression.view.FeatureGrid
 * @extends Ext.grid.Panel
 * @xtype featuregrid
 *
 * This class implements a grid of genes
 */
function BasicRenderer(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return value;
};

Ext.define('VBI.GeneExpression.view.FeatureGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.featuregrid',
	store: 'Genes',
	autoScroll: true,
	columns: [
		{dataIndex: 'exp_platform',		header: 'Platform',		flex: 1, hidden: true},
		{dataIndex: 'exp_samples',		header: 'Samples',		flex: 1, hidden: true},
		{dataIndex: 'exp_locustag',		header: 'Locus Tag',	flex: 1, hidden: true},
		{dataIndex: 'exp_name',			header: 'Title',		flex: 4, 
			renderer: function(value, metadata, record, rowIndex, colIndex, store) {
				var log_ratio = VBI.GeneExpression.param.log_ratio || 0,
					zscore = VBI.GeneExpression.param.zscore || 0;
				return Ext.String.format('<a href="TranscriptomicsGene?cType=taxon&cId={1}&dm=result&expId={2}&sampleId={3}&colId=&log_ratio={4}&zscore={5}" target="_blank">{0}</a>', value, 2, record.data.exp_id, record.data.pid, log_ratio, zscore);
			}
		},
		{dataIndex: 'pmid',				header: 'PubMed',		flex: 1, 
			renderer: function(value, metadata, record, rowIndex, colIndex, store) {
				if (value != undefined && value != "") {
					return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', value);
				} else {
					return "";
				}
			}
		},
		{dataIndex: 'exp_accession',	header: 'Accession',	flex: 1,
			renderer: function (value, metadata, record, rowIndex, colIndex, store) {
				if (value != undefined && value !="") {
					return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc={0}" target="_blank">{0}</a>', value);
				} else {
					return "";
				}
			}
		},
		{dataIndex: 'exp_strain',		header: 'Strain',		flex: 1, renderer:BasicRenderer},
		{dataIndex: 'exp_mutant',		header: 'Gene Modification', flex: 1,	renderer:BasicRenderer},
		{dataIndex: 'exp_condition',	header: 'Experimental Condition', flex: 2,	renderer:BasicRenderer},
		{dataIndex: 'exp_timepoint',	header: 'Time Point',	flex: 1,	align: 'center'},
		{dataIndex: 'exp_pavg',			header: 'Avg Intensity',flex: 1},
		{dataIndex: 'exp_pratio',		header: 'Log Ratio',	flex: 1},
		{dataIndex: 'exp_zscore',		header: 'Z-score',		flex: 1}
	]
});
/**
 * @class VBI.GeneExpression.view.FilterPanel
 * @extends Ext.panel.Panel
 * @xtype filterpanel
 *
 * This class implements a filter panel.
 */
Ext.define('VBI.GeneExpression.view.FilterPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.filterpanel',
	bodyPadding: 5,
	layout: 'hbox',
	items: [{
		xtype: 'textfield',
		itemId: 'keyword',
		width: 200,
		hideLabel: true,
		allowBlank: true,
		value: '',
		emptyText: 'keyword',
		scope: this
	}, {
		xtype: 'tbspacer',
		width: 20
	}, {
		xtype: 'combobox',
		itemId: 'log_ratio',
		fieldLabel: '|Log Ratio|',
		queryMode: 'local',
		displayField: 'name',
		labelWidth: 65,
		width: 120,
		value: 0,
		store: Ext.create('Ext.data.Store', {
			fields: ['name'],
			data: [{name:"0"}, {name:"0.5"}, {name:"1"}, {name:"1.5"}, {name:"2"}, {name:"2.5"}, {name:"3"}]
		}),		
		editable: false
	}, {
		xtype: 'tbspacer',
		width: 20
	}, {
		xtype: 'combobox',
		itemId: 'zscore',
		fieldLabel: '|Z-score|',
		queryMode: 'local',
		displayField: 'name',
		labelWidth: 55,
		width: 120,
		value: 0,
		store: Ext.create('Ext.data.Store', {
			fields: ['name'],
			data: [{name:"0"}, {name:"0.5"}, {name:"1"}, {name:"1.5"}, {name:"2"}, {name:"2.5"}, {name:"3"}]
		}),			
		editable: false
	}, {
		xtype: 'tbspacer',
		width: 20
	}, {
		xtype: 'button',
		text: 'Filter',
		handler: function() {
			var param = new Object();
			param.keyword 	= this.ownerCt.getComponent("keyword").getValue();
			param.log_ratio = this.ownerCt.getComponent("log_ratio").getValue();
			param.zscore 	= this.ownerCt.getComponent("zscore").getValue();
			// fire filter
			this.fireEvent('filter', param);
		}
	}, {
		xtype: 'tbspacer',
		width: 20
	}, {
		xtype: 'button',
		text: 'Reset Filter',
		handler: function() {
			// reset interface
			this.ownerCt.getComponent("keyword").setValue('');
			this.ownerCt.getComponent("log_ratio").setValue(0);
			this.ownerCt.getComponent("zscore").setValue(0);
			// fire reset
			this.fireEvent('reset');
		}
	}, {
		xtype: 'tbspacer',
		width: 20
	}, {
		xtype: 'button',
		text: 'Show All Comparisons',
		handler: function() {
			// reset interface
			this.ownerCt.getComponent("log_ratio").setValue(0);
			this.ownerCt.getComponent("zscore").setValue(0);
			// fire filter
			this.fireEvent('showall');
		}
	}]
});
/**
 * This defines a PATRIC theme for pie/bar charts.
 */
Ext.chart.theme.PATRIC = Ext.extend(Ext.chart.theme.Base, {
	constructor: function(config) {
		Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
			colors: ['rgb(56, 93, 117)','rgb(109,156,47)','rgb(246, 218, 98)','rgb(147, 181, 208)','rgb(172, 233, 93)','rgb(206, 192, 142)']
		}, config));
	}
});

/**
 * @class VBI.GeneExpression.view.Viewport
 * @extends Ext.panel.Panel
 *
 * This class implements a viewport.
 */
 
Ext.define('VBI.GeneExpression.view.Viewport', {
	extend: 'Ext.panel.Panel', 
	renderTo: 'expression_panel',
	layout: 'border',
	minHeight: 600,
	minWidth: 700,
	width: 967,
	height: 700,
	items: [ {
		region: 'north',
		border: false,
		height: 35,
		xtype: 'filterpanel',
		id: 'p-filterpanel'
	}, {
		region: 'south',
		border: false,
		height: 400,
		xtype: 'featuregrid',
		dockedItems: [{
			xtype: 'toolbar',
			height: 30,
			dock: 'top',
			items: ['->', {
				xtype: 'tbtext',
				text: 'Download table in '
			}, {
				xtype: 'tbspacer',
				width: 5
			}, {
				xtype: 'button',
				scale: 'small',
				iconAlign: 'left',
				text: 'Excel file (.xlsx)',
				icon: '/patric/images/toolbar_excel.png',
				handler: function(me) {
					me.fireEvent('downloadGrid','xlsx');
				}
			}, '|', 
			{
				xtype: 'button',
				scale: 'small',
				iconAlign: 'left',
				text: 'Text file (.txt)',
				icon: '/patric/images/toolbar_text.png',
				handler: function(me) {
					me.fireEvent('downloadGrid','txt');
				}
			}]
		}/*, {
			xtype: 'pagingtoolbar',
			dock: 'bottom',
			store: 'Genes'
		}*/]
	}, {
		region: 'center',
		layout: {
			type: 'hbox'
		},
		items: [
		// bar chart for log ratio & z score
		{
			xtype: 'tabpanel',
			width: 480,
			height: 241,
			items: [{
				xtype: 'chart',
				title: 'Log Ratio',
				id: 'p-chartlogratio',
				store: 'LogRatios',
				theme: 'PATRIC',
				axes: [{
					type: 'Numeric',
					position: 'left',
					fields: ['count'],
					title: 'Comparisons',
					minimum: 0
				}, {
					type: 'Category',
					position: 'bottom',
					fields: ['category'],
					title: 'Log Ratio'
				}],
				series: [{
					type: 'column',
					yField: 'count',
					xField: 'category',
					label: {
						display: 'insideEnd',
						field: 'count',
						contrast: true
					}
				}]
			}, {
				xtype: 'chart',
				title: 'Z-score',
				id: 'p-chartzscore',
				store: 'ZScores',
				theme: 'PATRIC',
				axes: [{
					type: 'Numeric',
					position: 'left',
					fields: ['count'],
					title: 'Comparisons',
					minimum: 0
				}, {
					type: 'Category',
					position: 'bottom',
					fields: ['category'],
					title: 'Z-score'
				}],
				series: [{
					type: 'column',
					yField: 'count',
					xField: 'category',
					label: {
						display: 'insideEnd',
						field: 'count',
						contrast: true
					}
				}]
			}]
		}, 
		// pie/bar chart for meta data
		{
			xtype: 'tabpanel',
			width: 485,
			height: 241,
			items: [{
				xtype: 'tabpanel',
				title: 'Strain',
				tabPosition: 'left',
				bodyBorder: false,
				tabBar: {
					plain: true,
					baseCls: ''
				},
				items:[{
					xtype: 'categorypiechart',
					title: 'All',
					iconCls: 'icon-pie-chart',
					id: 'CategoryPieStrain',
					store: 'Strains'
				},{
					xtype: 'categorybarchart',
					title: 'Top 5',
					iconCls: 'icon-bar-chart',
					id: 'CategoryBarStrain',
					store: 'StrainsTop5'
				}]
			}, {
				xtype: 'tabpanel',
				title: 'Gene Modification',
				tabPosition: 'left',
				bodyBorder: false,
				tabBar: {
					plain: true,
					baseCls: ''
				},
				items: [{
					xtype: 'categorypiechart',
					title: 'All',
					iconCls: 'icon-pie-chart',
					id: 'CategoryPieMutant',
					store: 'Mutants'
				},{
					xtype: 'categorybarchart',
					title: 'Top 5',
					iconCls: 'icon-bar-chart',
					id: 'CategoryBarMutant',
					store: 'MutantsTop5'
				}]
			}, {
				xtype: 'tabpanel',
				title: 'Experimental Condition',
				tabPosition: 'left',
				bodyBorder: false,
				tabBar: {
					plain: true,
					baseCls: ''
				},
				items: [{
					xtype: 'categorypiechart',
					title: 'All',
					iconCls: 'icon-pie-chart',
					id: 'CategoryPieCondition',
					store: 'Conditions'
				},{
					xtype: 'categorybarchart',
					title: 'Top 5',
					iconCls: 'icon-bar-chart',
					id: 'CategoryBarCondition',
					store: 'ConditionsTop5'
				}]
			}]
		}]
	}],
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		items: [{
			xtype: 'tbtext',
			height: 15,
			text: '',
			id: 'filterReport'
		}]
	}]
});
/**
 * @class VBI.GeneExpression.controller.ViewController
 * @extends Ext.app.Controller
 *
 * This class implements a controller.
 */
Ext.define('VBI.GeneExpression.controller.ViewController', {
	extend: 'Ext.app.Controller',
	models: ['Gene', 'CategoryCount'],
	views: ['FilterPanel', 'FeatureGrid', 'CategoryPieChart', 'CategoryBarChart'],
	stores: [
		'Genes',
		'Strains', 'Mutants', 'Conditions', 'StrainsTop5', 'MutantsTop5', 'ConditionsTop5',
		'LogRatios', 'ZScores'
	],
	init: function() {
		this.control({
			'filterpanel button': {
				reset: this.resetFilter,
				filter: this.doFilter,
				showall: this.showAll
			},
			'featuregrid > toolbar button': {
				downloadGrid: this.downloadGrid
			}/*,
			'categorypiechart': {
				filter: this.doFilter
			}*/
		})
	},
	resetFilter: function() {
		var param = new Object({keyword:'', threshold:'', strain:'', mutant:'', condition:''});
		VBI.GeneExpression.param = Ext.Object.merge(VBI.GeneExpression.param, param);
		
		// reload
		Ext.getStore('Genes').clearFilter();
		Ext.getStore('Genes').updateRecordCount();
		Ext.getStore('LogRatios').load();
		Ext.getStore('ZScores').load();
		Ext.getStore('Strains').load();
		Ext.getStore('Mutants').load();
		Ext.getStore('Conditions').load();
	},
	doFilter: function(param) {
		if (param != null) {
			VBI.GeneExpression.param = Ext.Object.merge(VBI.GeneExpression.param, param);
		}
		
		Ext.getStore('Genes').filterOnFly(VBI.GeneExpression.param);
		Ext.getStore('LogRatios').load();
		Ext.getStore('ZScores').load();
		Ext.getStore('Strains').load();
		Ext.getStore('Mutants').load();
		Ext.getStore('Conditions').load();
	},
	showAll: function() {
		var param = new Object({sampleId:'', keyword:'', threshold:'', strain:'', mutant:'', condition:''});
		VBI.GeneExpression.param = Ext.Object.merge(VBI.GeneExpression.param, param);
		
		// reload
		Ext.getStore('Genes').proxy.extraParams = Ext.Object.merge(Ext.getStore('Genes').proxy.extraParams, VBI.GeneExpression.param);
		Ext.getStore('Genes').load();
		Ext.getStore('LogRatios').load();
		Ext.getStore('ZScores').load();
		Ext.getStore('Strains').load();
		Ext.getStore('Mutants').load();
		Ext.getStore('Conditions').load();
	},
	downloadGrid: function(type) {
		
		var idList = new Array();
		var store = Ext.getStore("Genes");
		Ext.Array.each(store.getRange(), function(item) {
			idList.push(item.get('pid'));
		});
		var fids = {"na_feature_id":VBI.GeneExpression.param.featureId, "pid": idList.join(',')};
		
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "GeneExpression";
		Ext.getDom("fileformat").value = type;
		Ext.getDom("fids").value = Ext.JSON.encode(fids);
		Ext.getDom("idType").value = "GeneExpression-Pid";
		Ext.getDom("fTableForm").submit();
	}
});
Ext.application({
	name: 'VBI.GeneExpression',
	controllers: ['ViewController'],
	param: {},
	autoCreateViewport: true,
	launch: function() {
		var param = new Object();
		param.featureId = Ext.getDom('featureId').value;
		param.sampleId 	= Ext.getDom("sampleId")?Ext.getDom('sampleId').value:"";
		param.log_ratio = Ext.getDom("log_ratio")?Ext.getDom('log_ratio').value:0;
		param.zscore 	= Ext.getDom("zscore")?Ext.getDom('zscore').value:0;
		if (param.log_ratio > 0 || param.zscore > 0) {
			fp = Ext.getCmp("p-filterpanel");
			fp.getComponent("log_ratio").setValue(param.log_ratio);
			fp.getComponent("zscore").setValue(param.zscore);
		}
		VBI.GeneExpression.param = param;
	}
});	
