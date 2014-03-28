Ext.define('VBI.Workspace.model.ColumnBrowser', {
	extend: 'Ext.data.Model',
	idProperty: 'tagId',
	fields: [{name:'tagId', type:'int'}, 'name', 'tagType', 'type']
});
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
});Ext.define('VBI.Workspace.model.Feature', {
	extend: 'Ext.data.Model',
	idProperty: 'na_feature_id',
	fields: [
		{name:'genome_info_id',		type:'int'},
		{name:'gid',				type:'int'},
		{name:'genome_name',		type:'string'},
		{name:'accession',			type:'string'},
		{name:'locus_tag',			type:'string'},
		{name:'na_feature_id',		type:'int'},
		{name:'annotation',			type:'string'},
		{name:'feature_type',		type:'string'},
		{name:'start_max',			type:'int'},
		{name:'end_min',			type:'int'},
		{name:'na_length',			type:'int'},
		{name:'strand',				type:'string'},
		{name:'protein_id',			type:'string'},
		{name:'aa_length',			type:'int'},
		{name:'gene',				type:'string'},
		{name:'is_pseudo',			type:'string'},
		{name:'bound_moiety',		type:'string'},
		{name:'anticodon',			type:'string'},
		{name:'product',			type:'string'},
		{name:'refseq_locus_tag',	type:'string'},
		{name:'pseed_id',			type:'string'}
	]
});
Ext.define('VBI.Workspace.model.Genome', {
	extend: 'Ext.data.Model',
	idProperty: 'genome_info_id',
	fields: [
		{name:'genome_name',		type:'string'},
		{name:'genome_info_id',		type:'int'},
		{name:'gid', 				type:'int'},
		{name:'genome_status',		type:'string'},
		{name:'isolation_country',	type:'string'},
		{name:'host_name',			type:'string'},
		{name:'oxygen_requirement',	type:'string'},
		{name:'sporulation',		type:'string'},
		{name:'temperature_range',	type:'string'},
		{name:'disease',			type:'string'},
		{name:'habitat',			type:'string'},
		{name:'motility',			type:'string'},
		{name:'sequences',			type:'int'},
		{name:'collection_date',	type:'string'},
		{name:'genome_length',		type:'int'},
		{name:'complete',			type:'string'},
		{name:'rast_cds',			type:'int'},
		{name:'brc_cds',			type:'int'},
		{name:'refseq_cds',			type:'int'},
		{name:'chromosomes',		type:'int'},
		{name:'plasmids',			type:'int'},
		{name:'contigs',			type:'int'},
		{name:'ncbi_tax_id',		type:'int'},
		{name:'organism_name',		type:'string'},
		{name:'strain',				type:'string'},
		{name:'serovar',			type:'string'},
		{name:'biovar',				type:'string'},
		{name:'pathovar',			type:'string'},
		{name:'culture_collection',	type:'string'},
		{name:'type_strain',		type:'string'},
		{name:'project_status',		type:'string'},
		{name:'availability',		type:'string'},
		{name:'sequencing_centers',	type:'string'},
		{name:'completion_date',	type:'string'},
		{name:'publication',		type:'string'},
		{name:'completion_date',	type:'string'},
		{name:'ncbi_project_id',	type:'int'},
		{name:'refseq_project_id',	type:'int'},
		{name:'genbank_accessions',	type:'string'},
		{name:'refseq_accessions',	type:'string'},
		{name:'sequencing_status',	type:'string'},
		{name:'sequencing_platform',type:'string'},
		{name:'sequencing_depth',	type:'string'},
		{name:'assembly_method',	type:'string'},
		{name:'gc_content',			type:'string'},
		{name:'isolation_site',		type:'string'},
		{name:'isolation_source',	type:'string'},
		{name:'isolation_comments',	type:'string'},
		{name:'geographic_location',type:'string'},
		{name:'latitude',			type:'string'},
		{name:'longitude',			type:'string'},
		{name:'altitude',			type:'string'},
		{name:'depth',				type:'string'},
		{name:'host_gender',		type:'string'},
		{name:'host_age',			type:'string'},
		{name:'host_health',		type:'string'},
		{name:'body_sample_site',	type:'string'},
		{name:'body_sample_subsite',type:'string'},
		{name:'gram_stain',			type:'string'},
		{name:'cell_shape',			type:'string'},
		{name:'temperature_range',	type:'string'},
		{name:'optimal_temperature',type:'string'},
		{name:'salinity',			type:'string'},
		{name:'disease',			type:'string'},
		{name:'comments',			type:'string'},
		{name:'highlight'}
	]
});Ext.define('VBI.Workspace.model.Group', {
	extend: 'Ext.data.Model',
	idProperty: 'tagId',
	fields: [{name:'tagId', type:'int'}, 'name', {name:'members', type:'int'}, 'cdate', 
		'mdate', 'desc', 'type', 
		{name: 'thumb', convert: function(value, record) {
				return '/patric/images/workspace_'+record.get('type').toLowerCase()+'_group.png';
			}
		}
	]
});
Ext.define('VBI.Workspace.model.Mapping', {
	extend: 'Ext.data.Model',
	fields: [{name:'trackId', type:'int'}, {name:'tagId', type:'int'}]
});

Ext.define('VBI.Workspace.model.Station', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
	fields: [
		'id', 'name', 'type', 'leaf',
		{name: 'iconCls', type: 'string', defaultValue: 'x-tree-noicon'}
	]
});
Ext.define('VBI.Workspace.store.ColumnBrowser.Groups', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ColumnBrowser',
	model: 'VBI.Workspace.model.ColumnBrowser'
});

Ext.define('VBI.Workspace.store.ColumnBrowser.Tags', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ColumnBrowser',
	model: 'VBI.Workspace.model.ColumnBrowser',
	filterByTag: function(tagId) {
		this.clearFilter();
		this.filter([
			Ext.create("Ext.util.Filter", {filterFn: function(item) {
				return Ext.Array.contains(tagId, item.get("tagId"));
			}})
		]);
	}
});

Ext.define('VBI.Workspace.store.ColumnBrowser', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ColumnBrowser',
	model: 'VBI.Workspace.model.ColumnBrowser',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getFacets',
		reader: {
			type: 'json',
			root: 'results'
		},
		noCache: false
	},
	refresh: function(callback) {
		Ext.getStore('ColumnBrowser.Groups').removeAll(false);
		Ext.getStore('ColumnBrowser.Tags').removeAll(false);
		this.load(callback);
	},
	listeners: {
		load: function(store) {	
			store.clearFilter();
			store.filter("tagType", "Group");
			Ext.getStore('ColumnBrowser.Groups').add(store.getRange());
			store.clearFilter();
			store.filter("tagType", "String");
			Ext.getStore('ColumnBrowser.Tags').add(store.getRange());
		}
	}
});
Ext.define('VBI.Workspace.store.ExpressionExperiments', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.ExpressionExperiment',
	model: 'VBI.Workspace.model.ExpressionExperiment',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		actionMethods: {
			read: 'POST'
		},
		//url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport',
		api: {
			read: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getExperiments',
			update: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=updateExperimentInfo'
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
	//stateId: 'workspace_pagesize',
	remoteSort: false,
	filterByTracks: function(tracks) {
		//console.log("filtering experiments:", tracks);
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
			action: 'getSamples',
			expsource: 'User'
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
Ext.define('VBI.Workspace.store.Features', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.Feature',
	model: 'VBI.Workspace.model.Feature',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		actionMethods: {
			read: 'POST'
		},
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getFeatures',
		extraParams: {
			trackIds: ''
		},
		reader: {
			type: 'json',
			root: 'results',
			totalProperty: 'total'
		},
		noCache: false
	},
	pageSize: 20,
	stateId: 'workspace_pagesize',
	remoteSort: true,
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
Ext.define('VBI.Workspace.store.Genomes', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.Genome',
	model: 'VBI.Workspace.model.Genome',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		actionMethods: {
			read: 'POST'
		},
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getGenomes',
		extraParams: {
			trackIds: ''
		},
		reader: {
			type: 'json',
			root: 'results',
			totalProperty: 'total'
		},
		noCache: false
	},
	pageSize: 20,
	stateId: 'workspace_pagesize',
	remoteSort: true,
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
});Ext.define('VBI.Workspace.store.Groups', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.Group',
	model: 'VBI.Workspace.model.Group',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		api: {
			read: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getGroups',
			update: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=updateGroupInfo'
		},
		reader: {
			type: 'json',
			successProperty: 'success',
			root: 'results'
		},
		writer: {
			type: 'json',
			writeAllFields: false,
			root: 'group_info',
			encode: true
		},
		noCache: false
	},
	listeners: {
		write: function(store, operation) {
			Ext.getStore('ColumnBrowser').refresh();
		}
	},
	filterByTag: function(tagId) {
		this.clearFilter();
		this.filter([
			Ext.create("Ext.util.Filter", {filterFn: function(item) {
				return Ext.Array.contains(tagId, item.get("tagId"));
			}})
		]);
	}
});
Ext.define('VBI.Workspace.store.Mappings', {
	extend: 'Ext.data.Store',
	requires: 'VBI.Workspace.model.Mapping',
	model: 'VBI.Workspace.model.Mapping',
	autoLoad: true,
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getMappings',
		noCache: false
	},
	filterCriteria: {
		"Group": [],
		"String": []
	},
	setTrackFilter: function(criteria, tags) {
		if (criteria == "Group") {
			this.filterCriteria.Group = tags;
		}
		else if (criteria == "String") {
			this.filterCriteria.String = tags;
		}
	},
	getFilteredTracks: function() {
	
		//var tags = new Array(); 
		var tracks = new Array();
		var tracksInGroup = new Array();
		var tracksInTag = new Array();
		
		if (!Ext.isEmpty(this.filterCriteria.Group)) {
			var tags = Ext.Array.merge(this.filterCriteria.Group);
			this.each(function (record) {
				if (Ext.Array.contains (tags, record.get('tagId'))) {
					tracksInGroup.push(record.get('trackId'));
				}
			})
		}
		if (!Ext.isEmpty(this.filterCriteria.String)) {
			var tags = Ext.Array.merge(this.filterCriteria.String);
			this.each(function (record) {
				if (Ext.Array.contains (tags, record.get('tagId'))) {
					tracksInTag.push(record.get('trackId'));
				}
			})
		}
		//console.log(tracksInGroup, tracksInTag);
		
		if (!Ext.isEmpty(tracksInGroup) && !Ext.isEmpty(tracksInTag)) {
			if (tracksInGroup.length < tracksInTag.length) {
				Ext.Array.each(tracksInGroup, function (record) {
					if (Ext.Array.contains(tracksInTag, record)) {
						tracks.push(record);
					}
				});
			} else {
				Ext.Array.each(tracksInTag, function (record) {
					if (Ext.Array.contains(tracksInGroup, record)) {
						tracks.push(record);
					}
				});
			}
		}
		else if (!Ext.isEmpty(tracksInTag) && Ext.isEmpty(tracksInGroup)) {
			tracks = tracksInTag;
		}
		else if (!Ext.isEmpty(tracksInGroup) && Ext.isEmpty(tracksInTag)) {
			tracks = tracksInGroup;
		}
		else {
			this.each(function (record) {
				tracks.push(record.get('trackId'));
			})
		}
		
		return tracks;
	},
	getTagsByTrack: function(trackId) {
		var target = new Array();	
		if (Ext.isArray(trackId)) {
			this.each(function (record) {
				if  (Ext.Array.contains(trackId, record.get('trackId'))) {
					target.push(record.get('tagId'));
				}
			});
			
		} else if (Ext.isNumber(tracks)) {
			this.each(function(record) {
				if (record.get('trackId') == trackId) {
					target.push(record.get('tagId'));
				}
			});
		}
		target = Ext.Array.unique(target);
		return target;
	}
});
Ext.override(Ext.data.TreeStore, {
	load: function(options) {
		options = options || {};
		options.params = options.params || {};
		
		var me = this,
			node = options.node || me.tree.getRootNode(),
			root;
			
		// If there is not a node it means the user hasnt defined a rootnode yet. In this case lets just
		// create one for them.
		if (!node) {
			node = me.setRootNode({
				expanded: true
			});
		}
		
		if (me.clearOnLoad) {
			// this is what we changed.  added false
			node.removeAll(false);
		}
		
		Ext.applyIf(options, {
			node: node
		});
		options.params[me.nodeParam] = node ? node.getId() : 'root';
		
		if (node) {
			node.set('loading', true);
		}
		
		return me.callParent([options]);
	}
});

Ext.define('VBI.Workspace.store.Stations', {
	extend: 'Ext.data.TreeStore',
	requires: 'VBI.Workspace.model.Station',
	model: 'VBI.Workspace.model.Station',
	autoLoad: false,
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getLibrary',
		noCache: false
	}
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
	if (typeof record.get('expid') == "number") {
		return Ext.String.format('<a href="javascript:void(0)" onclick="launchExperimentDetail({1})">{0}</a>', value, record.get('expid'));
	} else {
		return Ext.String.format('<a href="javascript:void(0)" onclick="launchExperimentDetail(\'{1}\')">{0}</a>', value, record.get('expid'));
	}
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
function renderGenomeName (value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="Genome?cType=genome&cId={0}">{1}</a>', record.data.gid, value);
};
function renderLocustag (value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="Feature?cType=feature&cId={0}">{1}</a>', record.data.na_feature_id, value);
};
function renderGenomeBrowserByFeature(value, metadata, record, rowIndex, colIndex, store) {
	//metadata.tdAttr = 'data-qtip="Genome Browser" data-qclass="x-tip"';
	var tracks = "DNA,",
		window_start = Math.max(0,(record.data.start_max-1000)),
		window_end = parseInt(record.data.end_min)+1000;
	
	if (record.data.feature_type != null && record.data.feature_type == "CDS") {
		tracks += record.data.feature_type;
	} else if (record.data.feature_type != null && record.data.feature_type.indexOf(/.*RNA/) != -1) {
		tracks += "RNA";
	} else {
		tracks += "Misc";
	}
	if (record.data.annotation == "PATRIC") {
		tracks += "(PATRIC)";
	} else if (record.data.annotation == "Legacy BRC") {
		tracks += "(BRC)";
	} else {
		tracks += "(RefSeq)";
	}
	
	return Ext.String.format('<a href="GenomeBrowser?cType=genome&cId={0}&loc={1}:{2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif"  alt="Genome Browser" style="margin:-4px" /></a>', 
			record.data.gid, record.data.accession, window_start, window_end, tracks);
};
Ext.define('VBI.Workspace.view.columns.Feature', {
	extend: 'VBI.Workspace.view.columns.HeaderContainer',
	defaults: {
		align:'center'
	},
	items: [
		{text:'Genome Name',			itemId:'Feature_genome_name',		dataIndex:'genome_name',		flex:3, align:'left', renderer:renderGenomeName},
		{text:'Accession',				itemId:'Feature_accession',			dataIndex:'accession',			flex:1, hidden:true, renderer:BasicRenderer},
		{text:'Locus Tag',				itemId:'Feature_locus_tag',			dataIndex:'locus_tag',			flex:2, align:'left', renderer:renderLocustag},
		{text:'RefSeq Locus Tag',		itemId:'Feature_refseq_locus_tag',	dataIndex:'refseq_locus_tag',	flex:2, renderer:BasicRenderer},
		{text:'Gene Symbol',			itemId:'Feature_gene',				dataIndex:'gene',				flex:1, renderer:BasicRenderer},
		{text:'Genome Browser',			itemId:'Feature_',					dataIndex:'',					flex:1, hidden:true, sortable: false, renderer:renderGenomeBrowserByFeature},
		{text:'Annotation',				itemId:'Feature_annotation',		dataIndex:'annotation',			flex:1, hidden:true, renderer:BasicRenderer},
		{text:'Feature Type',			itemId:'Feature_feature_type',		dataIndex:'feature_type',		flex:1, hidden:true, renderer:BasicRenderer},
		{text:'Start',					itemId:'Feature_start_max',			dataIndex:'start_max',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
		{text:'End',					itemId:'Feature_end_min',			dataIndex:'end_min',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
		{text:'Length (NT)',			itemId:'Feature_na_length',			dataIndex:'na_length',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
		{text:'Strand',					itemId:'Feature_strand',			dataIndex:'strand',				flex:1, hidden:true},
		{text:'Protein ID',				itemId:'Feature_refseq_protein_id',	dataIndex:'protein_id',			flex:1, hidden:true, renderer:BasicRenderer},
		{text:'Length (AA)',			itemId:'Feature_aa_length',			dataIndex:'aa_length',			flex:1, hidden:true, align:'right', renderer:BasicRenderer},
		{text:'Anticodon',				itemId:'Feature_anticodon',			dataIndex:'anticodon',			flex:1, hidden:true},
		{text:'Product Description',	itemId:'Feature_product',			dataIndex:'product',			flex:4, align:'left', renderer:BasicRenderer},
		{text:'Bound Moiety',			itemId:'Feature_bound_moiety',		dataIndex:'bound_moiety',		flex:1, hidden:true}
	]
});
function renderGenomeName(value, metadata, record, rowIndex, colIndex, store) {
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return Ext.String.format('<a href="Genome?cType=genome&cId={0}">{1}</a>', record.data.genome_info_id, value);
};
function renderGenomeBrowserByGenome(alue, metadata, record, rowIndex, colIndex, store) {
	var tracks = "DNA,CDS(PATRIC),RNA(PATRIC)";
	return Ext.String.format('<a href="GenomeBrowser?cType=genome&cId={0}&loc={2}..{3}&tracks={4}"><img src="/patric/images/icon_genome_browser.gif" alt="Genome Browser" style="margin:-4px" /></a>', 
			record.data.genome_info_id, '', 0, 10000, tracks);
};
function renderCDS_Count_RAST(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) {
		metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
		return Ext.String.format('<a href="FeatureTable?cType=genome&amp;cId={0}&amp;featuretype=CDS&amp;annotation=PATRIC&amp;filtertype=">{1}</a>', record.data.genome_info_id, value);
	} else {
		metadata.tdAttr = 'data-qtip="0" data-qclass="x-tip"';
		return 0;
	}
};
function renderCDS_Count_BRC(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) { 
		metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
		return Ext.String.format('<a href="FeatureTable?cType=genome&amp;cId={0}&amp;featuretype=CDS&amp;annotation=BRC&amp;filtertype=">{1}</a>', record.data.genome_info_id, value);
	} else {
		metadata.tdAttr = 'data-qtip="0" data-qclass="x-tip"';
		return 0;
	}
};
function renderCDS_Count_RefSeq(value, metadata, record, rowIndex, colIndex, store) {
	if (value != 0) {
		metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
		return Ext.String.format('<a href="FeatureTable?cType=genome&amp;cId={0}&amp;featuretype=CDS&amp;annotation=RefSeq&amp;filtertype=">{1}</a>', record.data.genome_info_id, value);
	} else {
		metadata.tdAttr = 'data-qtip="0" data-qclass="x-tip"';
		return 0;
	}
};

Ext.define('VBI.Workspace.view.columns.Genome', {
	extend: 'VBI.Workspace.view.columns.HeaderContainer',
	defaults: {
		align: 'center'
	},
	items: [
		{text:'Organism Name',			itemId:'Genome_genome_name',			dataIndex:'genome_name',				flex:2, align:'left', renderer:renderGenomeName},
		{text:'Genome Status',			itemId:'Genome_genome_status',			dataIndex:'genome_status',				flex:1}, 
		{text:'Genome Browser',			itemId:'Genome_genome_browser',			dataIndex:'genome_browser',				flex:1, hidden:true, sortable:false, renderer:renderGenomeBrowserByGenome},
		{text:'Size',					itemId:'Genome_genome_length',			dataIndex:'genome_length',				flex:1, hidden:true, align:'right'},
		{text:'Chromosome',				itemId:'Genome_chromosomes',			dataIndex:'chromosomes',				flex:1, hidden:true},
		{text:'Plasmids',				itemId:'Genome_plasmids',				dataIndex:'plasmids',					flex:1, hidden:true},
		{text:'Contigs',				itemId:'Genome_contigs',				dataIndex:'contigs',					flex:1, hidden:true},
		{text:'Sequences',				itemId:'Genome_sequences',				dataIndex:'sequences',					flex:1, hidden:true},
		{text:'PATRIC CDS',				itemId:'Genome_rast_cds',				dataIndex:'rast_cds',					flex:1, renderer:renderCDS_Count_RAST},
		{text:'Legacy BRC CDS',			itemId:'Genome_brc_cds',				dataIndex:'brc_cds',					flex:1, hidden:true, renderer:renderCDS_Count_BRC},
		{text:'RefSeq CDS',				itemId:'Genome_refseq_cds',				dataIndex:'refseq_cds',					flex:1, hidden:true, renderer:renderCDS_Count_RefSeq},
		{text:'Isolation Country',		itemId:'Genome_isolation_country',		dataIndex:'isolation_country',			flex:1},
		{text:'Host Name',				itemId:'Genome_host_name',				dataIndex:'host_name',					flex:1},
		{text:'Disease', 				itemId:'Genome_disease',				dataIndex:'disease',					flex:1},
		{text:'Collection Date', 		itemId:'Genome_collection_date',		dataIndex:'collection_date',			flex:1},
		{text:'Completion Date', 		itemId:'Genome_completion_date',		dataIndex:'completion_date',			flex:1},
		{text:'Strain',					itemId:'Genome_strain',					dataIndex:'strain', 					flex:1, hidden:true},
		{text:'Serovar',				itemId:'Genome_serovar',				dataIndex:'serovar',					flex:1, hidden:true},
		{text:'Biovar',					itemId:'Genome_biovar',					dataIndex:'biovar',						flex:1, hidden:true},
		{text:'Pathovar',				itemId:'Genome_pathovar',				dataIndex:'pathovar',					flex:1, hidden:true},
		{text:'Culture Collection',		itemId:'Genome_culture_collection',		dataIndex:'culture_collection',			flex:1, hidden:true},
		{text:'Type Strain',			itemId:'Genome_type_strain',			dataIndex:'type_strain',				flex:1, hidden:true},
		{text:'Project Status', 		itemId:'Genome_project_status',			dataIndex:'project_status',				flex:1, hidden:true},
		{text:'Availability', 			itemId:'Genome_availability',			dataIndex:'availability',				flex:1, hidden:true},
		{text:'Sequencing Center',		itemId:'Genome_sequencing_centers',		dataIndex:'sequencing_centers', 		flex:1, hidden:true},
		{text:'Publication', 			itemId:'Genome_publication',			dataIndex:'publication',				flex:1, hidden:true},
		{text:'NCBI Project Id', 		itemId:'Genome_ncbi_project',			dataIndex:'ncbi_project_id',			flex:1, hidden:true},
		{text:'RefSeq Project Id',		itemId:'Genome_refseq_project',			dataIndex:'refseq_project_id',			flex:1, hidden:true},
		{text:'Genbank Accessions',		itemId:'Genome_genbank_accessions',		dataIndex:'genbank_accessions',			flex:1, hidden:true},
		{text:'RefSeq Accessions',		itemId:'Genome_refseq_accessions',		dataIndex:'refseq_accessions',			flex:1, hidden:true},
		{text:'Sequencing Platform',	itemId:'Genome_sequencing_platform',	dataIndex:'sequencing_platform',		flex:1, hidden:true},
		{text:'Sequencing Depth',		itemId:'Genome_sequencing_depth',		dataIndex:'sequencing_depth',			flex:1, hidden:true},
		{text:'Assembly Method',		itemId:'Genome_assembly_method',		dataIndex:'assembly_method',			flex:1, hidden:true},
		{text:'GC Content',				itemId:'Genome_gc_content',				dataIndex:'gc_content',					flex:1, hidden:true},
		{text:'Isolation Site', 		itemId:'Genome_isolation_site',			dataIndex:'isolation_site',				flex:1, hidden:true},
		{text:'Isolation Source', 		itemId:'Genome_isolation_source',		dataIndex:'isolation_source',			flex:1, hidden:true},
		{text:'Isolation Comments',		itemId:'Genome_isolation_comments',		dataIndex:'isolation_comments',			flex:1, hidden:true},
		{text:'Geographic Location',	itemId:'Genome_geographic_location',	dataIndex:'geographic_location',		flex:1, hidden:true},
		{text:'Latitude',				itemId:'Genome_latitude',				dataIndex:'latitude',		 			flex:1, hidden:true},
		{text:'Longitude',				itemId:'Genome_longitude',				dataIndex:'longitude',		 			flex:1, hidden:true},
		{text:'Altitude', 				itemId:'Genome_altitude',				dataIndex:'altitude',		 			flex:1, hidden:true},
		{text:'Depth', 					itemId:'Genome_depth',					dataIndex:'depth', 			 			flex:1, hidden:true},
		{text:'Host Gender',			itemId:'Genome_host_gender',			dataIndex:'host_gender',	 			flex:1, hidden:true},
		{text:'Host Age', 				itemId:'Genome_host_age',				dataIndex:'host_age',		 			flex:1, hidden:true},
		{text:'Host Health',			itemId:'Genome_host_health',			dataIndex:'host_health',	 			flex:1, hidden:true},
		{text:'Body Sample Site',		itemId:'Genome_body_sample',			dataIndex:'body_sample_site',			flex:1, hidden:true},
		{text:'Body Sample Subsite',	itemId:'Genome_body_sample',			dataIndex:'body_sample_subsite',		flex:1, hidden:true},
		{text:'Gram Stain',				itemId:'Genome_gram_stain',				dataIndex:'gram_stain',					flex:1, hidden:true},
		{text:'Cell Shape',				itemId:'Genome_cell_shape',				dataIndex:'cell_shape',					flex:1, hidden:true},
		{text:'Motility',				itemId:'Genome_motility',				dataIndex:'motility',					flex:1, hidden:true},
		{text:'Sporulation',			itemId:'Genome_sporulation',			dataIndex:'sporulation',				flex:1, hidden:true},
		{text:'Temperature Range',		itemId:'Genome_temperature_range',		dataIndex:'temperature_range',			flex:1, hidden:true},
		{text:'Optimal Temperature',	itemId:'Genome_optimal_temperature',	dataIndex:'optimal_temperature',		flex:1, hidden:true},
		{text:'Salinity',				itemId:'Genome_salinity',				dataIndex:'salinity', 					flex:1, hidden:true},
		{text:'Oxygen Requirement',		itemId:'Genome_oxygen_requirement',		dataIndex:'oxygen_requirement', 		flex:1, hidden:true},
		{text:'Habitat',				itemId:'Genome_habitat',				dataIndex:'habitat',					flex:1, hidden:true},
		{text:'Others',					itemId:'Genome_comments',				dataIndex:'comments',					flex:1, hidden:true}
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
});Ext.define('VBI.Workspace.view.group.Browser', {
	extend: 'Ext.view.View',
	alias : 'widget.groupbrowser',
	tpl: [
			'<tpl for=".">',
				'<div class="thumb-wrap">',
					'<div class="thumb">',
						(!Ext.isIE6? '<img src="{thumb}"  alt="{name}" title="{name}" />' : 
						'<div style="width:76px;height:76px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\'{thumb}\')"></div>'),
					'</div>',
					'<div class="title">{[Ext.String.ellipsis(values.name, 20)]} ({members})</div>',
					'<div class="type">{type} Group</div>',
					'<div class="updated">Updated: {[Ext.Date.format(Ext.Date.parse(values.mdate, "Y-m-d H:i:s"), "x-date-relative")]}</div>',
				'</div>',
			'</tpl>'
	],
	autoScroll: true,
	multiSelect: true,
	itemSelector: 'div.thumb-wrap',
	cls: 'x-browser-view'
});
Ext.define('VBI.Workspace.view.group.DetailToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.detailtoolbar',
	items: [
		{ 
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
		}
	]
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
		itemId: 'datatype',
		name: 'datatype',
		renderer: function(value, record) {
			return value + ' Experiment';
		},
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
	}, { 
		xtype: 'button', 
		itemId: 'editInfoBtn', 
		text: 'Edit', 
		iconCls: 'x-btn-edit', 
		iconAlign: 'right', 
		scope: this, 
		enableToggle: true
	}],
	loadRecord: function(record) {
		//console.log(record);
		
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
			datatype: record.get("data_type") || 'Transcriptomics',
			description: record.get("desc"), 
			organism: record.get("organism"),
			pmid: (record.get("pmid")!=0)?record.get("pmid"):"",
			updated: Ext.Date.format(Ext.Date.parse(record.get("mdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			created: Ext.Date.format(Ext.Date.parse(record.get("cdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			file: record.get("origFileName")
		});
		
		if (record.get("source") == "PATRIC") {
			this.getComponent('editInfoBtn').setDisabled(true);
		} else {
			this.getComponent('editInfoBtn').setDisabled(false);
		}
	}, 
	
	startEdit: function() {
		//console.log(this.child('#title'));
		this.remove('title');
		this.insert(0, {
			xtype: 'textfield',
			name: 'title', 
			itemId: 'title', 
			fieldLabel: 'Experiment title',
			labelAlign: 'top',
			allowBlank: false
		});
		this.remove('datatype');
		this.insert(2, {
			xtype: 'combobox',
			name: 'datatype',
			itemId: 'datatype',
			store: {
				xtype: 'store',
				fields: ['name', 'text'],
				data: [
					{name:"Transcriptomics", text:'Transcriptomics'},
					{name:"Proteomics", text:'Proteomics'},
					{name:"Phenomics", text:'Phenomics'}]
			},
			queryMode: 'local',
			displayField: 'text',
			valueField: 'name',
			editable: false
		});
		this.remove('organism');
		this.insert(3, {
			xtype: 'textfield',
			name: 'organism', 
			itemId: 'organism', 
			fieldLabel: 'Platform Organism',
			labelAlign: 'top'
		});
		this.remove('pmid');
		this.insert(4, {
			xtype: 'numberfield',
			name: 'pmid', 
			itemId: 'pmid', 
			fieldLabel: 'Pubmed ID',
			labelWidth: 80,
			hideTrigger: true,
			keyNavEnabled: false,
			mouseWheelEnabled: false
		});
		this.remove('description');
		this.insert(5, {
			xtype: 'textareafield',
			name: 'description', 
			itemId: 'description', 
			fieldLabel: 'Description',
			labelAlign: 'top',
			allowBlank: true,
			grow: true, 
			growMin: 20, 
			growMax: 100
		});
		
		this.loadRecord(this.record);		
		this.getComponent('editInfoBtn').setText('Save');
	}, 
	
	finishEdit: function() {
		
		var newInfo = this.getForm().getValues();
		this.record.set("title", newInfo.title);
		this.record.set("data_type", newInfo.datatype);
		this.record.set("desc", newInfo.description);
		this.record.set("organism", newInfo.organism);
		this.record.set("pmid", newInfo.pmid);
		console.log(newInfo, this.record);
		Ext.getStore('ExpressionExperiments').sync();
		
		this.disableFields();
	}, 
	
	disableFields: function() {
		// disable the form elements by changing their types to displayfield
		this.remove('title');
		this.insert(0, {
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
		});
		
		this.remove('datatype');
		this.insert(2, {
			xtype: 'displayfield',
			itemId: 'datatype',
			name: 'datatype',
			renderer: function(value, record) {
				return value + ' Experiment';
			},
			fieldStyle: {
				paddingBottom: '5px',
				borderTop: '1px dashed #000000',
				borderBottom: '1px dashed #000000'
			}
		});
		
		this.remove('organism');
		this.insert(3, {
			xtype: 'displayfield',
			name: 'organism', 
			itemId: 'organism', 
			fieldLabel: 'Platform Organism',
			labelAlign: 'top'
		});
		
		this.remove('pmid');
		this.insert(4, {
			xtype: 'displayfield',
			name: 'pmid', 
			itemId: 'pmid', 
			fieldLabel: 'Pubmed ID',
			labelWidth: 80,
			renderer: function(value, record) {
				return Ext.String.format('<a href="http://www.ncbi.nlm.nih.gov/pubmed/{0}" target=_blank>{0}</a>', value);
			}
		});
		
		this.remove('description');
		this.insert(5, {
			xtype: 'displayfield',
			name: 'description', 
			itemId: 'description', 
			fieldLabel: 'Description',
			labelAlign: 'top'
		});
		this.loadRecord(this.record);
		this.getComponent('editInfoBtn').setText('Edit');
	}
});
/**
 * @class GroupExplorer.view.group.InfoEditor
 * @extends Ext.form.Panel
 * @xtype groupinfoeditor
 *
 * This class implements an info/edit panel for a single group.
 */
Ext.define('VBI.Workspace.view.group.GroupInfoEditor', {
	extend: 'Ext.form.Panel',
	alias: 'widget.groupinfoeditor',
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
		xtype: 'component', 
		id: 'group-viewer-thumb',
		tpl: [
			'<tpl for=".">',
				'<div>',
					'<img src="{thumb}" alt="{title}" title="{title}" />',
					'<span class="title">{title}</span>',
					'<div>{desc}</div>',
					'<div class="clear"></div>', 
				'</div>',
			'</tpl>'
		] 
	}, {
		xtype: 'displayfield',
		name: 'groupname', 
		itemId: 'groupname', 
		hideLabel: true, 
		allowBlank: false, 
		fieldStyle: {
			fontSize: '13px',
			fontWeight: 'bold', 
			paddingBottom: '5px',
			borderBottom: '1px dashed #000000'
		}
	}, {
		xtype: 'displayfield',
		name: 'description', 
		itemId: 'description', 
		fieldLabel: 'Description',
		labelAlign: 'top'
	}, {
		xtype: 'displayfield',
		name: 'tags',
		fieldLabel: 'Tags',
		labelWidth: 32
	}, {
		xtype: 'displayfield',
		name: 'updated',
		fieldLabel: 'Last Updated',
		labelWidth: 85
	}, {
		xtype: 'displayfield',
		name: 'created',
		fieldLabel: 'Created',
		labelWidth: 50
	}, { 
		xtype: 'button', 
		itemId: 'editInfoBtn', 
		text: 'Edit', 
		iconCls: 'x-btn-edit', 
		iconAlign: 'right', 
		scope: this, 
		enableToggle: true
	}],
	/**
	 * Loads a given record into the panel. Updates the dataview containing the 
	 * group thumbnail and the form containing the group details.
	 * @param {Ext.data.Model} record The data record to load.
	 */
	loadRecord: function(record) {
		//console.log(record.data);
		
		this.record = record;
		var title, desc;
		
		if (record.get("type") == "Feature") {
			title = "Feature Group";
			if (record.get("members") > 1) {
				desc = "(" + record.get("members") + " features)";
			} else {
				desc = "("+record.get("members")+" feature)";
			}
		} 
		else if (record.get("type") == "Genome")
		{
			title = "Genome Group";
			if (record.get("members") > 1) {
				desc = "(" + record.get("members") + " genomes)";
			} else {
				desc = "("+record.get("members")+" genome)";
			}
		}
		else if (record.get("type") == "ExpressionExperiment")
		{
			title = "Transcriptomics Experiment Group";
			if (record.get("members") > 1) {
				desc = "(" + record.get("members") + " experiments)";
			} else {
				desc = "("+record.get("members")+" experiment)";
			}
		} 
		else
		{
			title = "Unknown Group";
			if (record.get("members") > 1) {
				desc = "(" + record.get("members") + " members)";
			} else {
				desc = "("+record.get("members")+" member)";
			}
			
		}
		
		// update the thumbnail for the group
		//console.log(Ext.ComponentQuery.query('infoviewer > dataview')[0]);
		Ext.getCmp('group-viewer-thumb').update({
			thumb: record.get("thumb"),
			title: title,
			desc: desc
		});
		
		// update the form elements
		this.getForm().setValues({
			groupname: record.get("name"), 
			description: record.get("desc"), 
			tags: record.get("tags"),
			updated: Ext.Date.format(Ext.Date.parse(record.get("mdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			created: Ext.Date.format(Ext.Date.parse(record.get("cdate"), 'Y-m-d H:i:s'), 'M j, Y'),
			owner: record.get("owner")
		});
		
	}, 
	
	startEdit: function() {
		
		this.remove('groupname');
		this.insert(1, {
			xtype: 'textfield',
			name: 'groupname', 
			itemId: 'groupname', 
			hideLabel: true, 
			allowBlank: false,
			value: 'none'
		});
		
		this.remove('description');
		this.insert(2, {
			xtype: 'textareafield',
			name: 'description', 
			itemId: 'description', 
			fieldLabel: 'Description',
			labelAlign: 'top',
			allowBlank: true,
			grow: true, 
			growMin: 20, 
			growMax: 100, 
			value: 'none'
		});
		
		this.loadRecord(this.record);		
		this.getComponent('editInfoBtn').setText('Save');
	}, 
	
	finishEdit: function() {
		
		var newInfo = this.getForm().getValues();
		this.record.set("desc", newInfo.description);
		this.record.set("name", newInfo.groupname);
		Ext.getStore('Groups').sync();
		
		this.disableFields();
	}, 
	
	disableFields: function() {
		// disable the form elements by changing their types to displayfield
		this.remove('groupname');
		this.insert(1, {
			xtype: 'displayfield',
			name: 'groupname', 
			itemId: 'groupname', 
			hideLabel: true, 
			allowBlank: false, 
			fieldStyle: {
				fontSize: '13px',
				fontWeight: 'bold', 
				paddingBottom: '5px',
				borderBottom: '1px dashed #000000'
			}, 
			value: 'none'
		});
		this.remove('description');
		this.insert(2, {
			xtype: 'displayfield',
			name: 'description', 
			itemId: 'description', 
			fieldLabel: 'Description',
			labelAlign: 'top',
			value: ''
		});
		this.loadRecord(this.record);
		this.getComponent('editInfoBtn').setText('Edit');
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
	getSelectedGroup: function() {
		var viewport = Ext.getCmp('workspace_view'),
			selection,
			groupList = new Array();
		
		if (viewport.activeItem == "groupview") {
			selection = new Array();
			groupList.push( Ext.getCmp('workspace_groupinfoeditor').record.get("tagId") );
		} else {
			selection = Ext.getCmp('columnbrowser_groups').getSelectionModel().getSelection();
			Ext.Array.each(selection, function(item) {
				groupList.push(item.get("tagId"));
			});
		}
		return groupList;
	},
	getSelectedID: function(type) {
		var selection;
		
		if (Ext.getCmp('workspace_view').activeItem == "groupview") {
			selection = Ext.getCmp('workspace_detailview').child('#panel_grid').child('#experimentview').getSelectionModel().getSelection();
		} else {
			selection = Ext.getCmp('workspace_listview').child('#experimentview').getSelectionModel().getSelection();
		}
		
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "No experiment was selected");
			return null;
		} else {
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
	refreshWorkspaceViews: function() {
		
		Ext.getStore('Mappings').load({
			callback: function() {
				Ext.getStore('Groups').load({
					callback: function() {
						Ext.getStore('ColumnBrowser').refresh({
							callback:function() {
								Ext.getStore('Stations').load({
									callback: function() {
										Ext.getCmp('workspace_station').setDefault("ExpressionExperiments");
										updateCartInfo();
									}
								});
							}
						});
					}
				});
			}
		});
	},
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [{
		title: 'Workspace', 
		xtype: 'buttongroup',
		columns: 1,
		items:[{
			xtype: 'tbar_btn_remove',
			text: 'Remove Experiment(s)',
			width: 150,
			handler: function(btn, e) {
				var groupList = btn.findParentByType('toolbar').getSelectedGroup(),
					idList = btn.findParentByType('toolbar').getSelectedID(),
					me = this;
						
				if (idList == null) { return false; }
					
				//var idList = new Array();
				//idList.push('b5ed6492-f333-4ade-a40e-1a34fdd86cfa');
					
				if (groupList.length == 0) {
					// no group selected, delete from workspace
					Ext.Msg.show({
						msg: 'Do you want to delete this experiment from your workspace?',
						buttons: Ext.Msg.OKCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "ok" && idList.length > 0) {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
									params: {
										action_type: 'groupAction',
										action: 'removeTrack',
										removeFrom: 'workspace',
										idType: 'ExpressionExperiment',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
						
				} else {
							
					Ext.Msg.show({
						msg: 'Do you want to delete this genome from your selected groups? Click No if you want to delete from entire workspace',
						buttons: Ext.Msg.YESNOCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "yes") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
									params: {
										action_type: 'groupAction',
										action: 'removeTrack',
										removeFrom:'groups',
										groups: groupList.join(","),
										idType: 'ExpressionExperiment',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							} else if (buttonId == "no") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
									params: {
										action_type: 'groupAction',
										action: 'removeTrack',
										removeFrom:'workspace',
										idType: 'ExpressionExperiment',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
				}
			}
		},{
			xtype: 'tbar_btn_create',
			text: 'Add to Group',
			handler: function(btn, e) {
				var idList = btn.findParentByType('toolbar').getSelectedID(),
					me = this;
				
				if (idList == null) { return false; }
					
				var btnGroupPopupSave = new Ext.Button({
					text:'Save to Group',
					handler: function(btn, e) {
						//console.log("custom button for save to group - genome level");
						saveToGroup(idList.join(","), 'ExpressionExperiment');
						me.findParentByType('toolbar').refreshWorkspaceViews();
					}
				});
						
				popup = Ext.create('VBI.Workspace.view.Toolbar.window.AddToGroup', {
					title: 'Add Selected Experiments to Group',
					buttons: [btnGroupPopupSave,{
						text: 'Cancel',
						handler: function(){popup.hide();}
					}]
				}).show();
			}
		}]
	}, {
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
					
					if (item.get("source") == "PATRIC") {
						expIds.push(item.get("eid"));
					}
					else if (item.get("source") == "me") {
						colIds.push(item.get("expid"));
					}
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
	}, {
		title: 'Upload',
		xtype: 'buttongroup',
		width: 155,
		items: [{
			scale: 'large',
			text: 'Transcriptomics Data',
			icon: '/patric/images/transcriptomics_uploader_icon_32x32.png',
			handler: launchTranscriptomicsUploader
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
	refreshWorkspaceViews: function() {
		
		Ext.getStore('Mappings').load({
			callback: function() {
				Ext.getStore('Groups').load({
					callback: function() {
						Ext.getStore('ColumnBrowser').refresh({
							callback:function() {
								Ext.getStore('Stations').load({
									callback: function() {
										Ext.getCmp('workspace_station').setDefault("ExpressionExperiments");
										updateCartInfo();
									}
								});
							}
						});
					}
				});
			}
		});
	},
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [/* // comment out for now
		{
			title: 'Workspace', 
			xtype: 'buttongroup',
			columns: 1,
			items:[{
				xtype: 'tbar_btn_remove',
				text: 'Remove Sample(s)',
				width: 150,
				handler: function(btn, e) {
					var idList = btn.findParentByType('toolbar').getSelectedID();
					if (idList == null) { return false; }
					var me = this;
					
					// no group selected, delete from workspace
					Ext.Msg.show({
						msg: 'Do you want to delete this genome from your workspace?',
						buttons: Ext.Msg.OKCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "ok" && idList.length > 0) {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom: 'workspace',
										idType: 'Genome',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
				}
			},{
				xtype: 'tbar_btn_create',
				text: 'Add to Group',
				handler: function(btn, e) {
					var idList = btn.findParentByType('toolbar').getSelectedID();
					if (idList == null) { return false; }
					var me = this;
					
					var btnGroupPopupSave = new Ext.Button({
						text:'Save to Group',
						handler: function(btn, e) {
							//console.log("custom button for save to group - genome level");
							saveToGroup(idList.join(","), 'Genome');
								
							me.findParentByType('toolbar').refreshWorkspaceViews();
						}
					});
						
					popup = Ext.create('VBI.Workspace.view.Toolbar.window.AddToGroup', {
							title: 'Add Selected Genomes to Group',
							buttons: [btnGroupPopupSave,{
								text: 'Cancel',
								handler: function(){popup.hide();}
							}]
					}).show();
				}
			}]
		}, */
		{
			xtype: 'tbspacer',
			width: 220
		}, {
			title: 'View',
			xtype: 'buttongroup',
			width: 115,
			items: [{
				xtype: 'tbar_btn_genelist',
				handler: function(btn, e) {
					var selection = btn.findParentByType('toolbar').getSelectedID(),
						expId = Ext.getCmp('workspace_experimentinfoeditor').record.get("expid"),
						store = Ext.getStore("ExpressionExperiments"),
						maxComparisions = 100,
						param = "";
					//console.log(selection);
					if (selection != undefined) {
						if (selection.length >= maxComparisions) {
							alert("You have exceeded the limit of comparisons. Please lower than "+maxComparisions);
							return false;
						}
						if (store.getById(expId).get("source")=="PATRIC") {
							param = "&expId=" + expId + "&sampleId=" + selection.join(",") + "&colId=";
						} 
						else if (store.getById(expId).get("source")=="me") {
							param = "&expId=&sampleId=&colId=" + expId + ":" + selection.join("+").replace(new RegExp(expId, 'g'), '');
						}
					
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
Ext.define('VBI.Workspace.view.toolbar.Feature', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.featuretoolbar',
	getSelectedGroup: function() {
		var viewport = Ext.getCmp('workspace_view');
		var selection;
		var groupList = new Array();
		
		if (viewport.activeItem == "groupview") {
			selection = new Array();
			groupList.push( Ext.getCmp('workspace_groupinfoeditor').record.get("tagId") );
			
		} else {
			selection = Ext.getCmp('columnbrowser_groups').getSelectionModel().getSelection();
			Ext.Array.each(selection, function(item) {
				groupList.push(item.get("tagId"));
			});
		}
		return groupList;
	},
	getSelectedID: function() {
		var selection;
		
		if (Ext.getCmp('workspace_view').activeItem == "groupview") {
			selection = Ext.getCmp('workspace_detailview').child('#panel_grid').child('#featureview').getSelectionModel().getSelection();
		} else {
			selection = Ext.getCmp('workspace_listview').child('#featureview').getSelectionModel().getSelection();
		}
		
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "No feature was selected");
			return null;
		} else {
			var selectedIDs = new Array();
			Ext.Array.each(selection, function(item) {
				selectedIDs.push(item.get("na_feature_id"));
			});
			return selectedIDs;
		}
	},
	refreshWorkspaceViews: function() {
		Ext.getStore('Mappings').load({
			callback: function() {
				Ext.getStore('Groups').load({
					callback: function() {
						Ext.getStore('ColumnBrowser').refresh({
							callback:function() {
								Ext.getStore('Stations').load({
									callback: function() {
										Ext.getCmp('workspace_station').setDefault("Features");
										updateCartInfo();
									}
								});
							}
						});
					}
				});
			}
		});
	},
	items: [{
		title: 'Workspace', 
		xtype: 'buttongroup', 
		columns: 1,
		items:[{
			xtype:'tbar_btn_remove',
			handler: function(btn, e) {
				var groupList = this.findParentByType('toolbar').getSelectedGroup();
						
				var idList = this.findParentByType('toolbar').getSelectedID();
				if (idList == null) { return false; }
				var me = this;
				
				if (groupList.length == 0) {
					Ext.Msg.show({
						msg: 'Do you want to delete this feature from your workspace?',
						buttons: Ext.Msg.OKCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "ok" && idList.length > 0) {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom: 'workspace',
										idType: 'Feature',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
							
				} else {
							
					Ext.Msg.show({
						msg: 'Do you want to delete this feature from your selected groups? Click No if you want to delete from entire workspace',
						buttons: Ext.Msg.YESNOCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "yes") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom:'groups',
										groups: groupList.join(","),
										idType: 'Feature',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							} else if (buttonId == "no") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom:'workspace',
										idType: 'Feature',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
				}
			}
		}, {
			xtype: 'tbar_btn_create',
			handler: function(btn, e) {
				var idList = this.findParentByType('toolbar').getSelectedID();
				if (idList == null) { return false; }
				var me = this;
				
				var btnGroupPopupSave = new Ext.Button({
					text:'Save to Group',
					handler: function(btn, e) {
						//console.log("custom button for save to group - feature level");
						saveToGroup(idList.join(","), 'Feature');
						me.findParentByType('toolbar').refreshWorkspaceViews();
					}
				});
				
				popup = Ext.create('VBI.Workspace.view.Toolbar.window.AddToGroup', {
					title: 'Add Selected Features to Group',
					buttons: [btnGroupPopupSave,{
						text: 'Cancel',
						handler: function(){popup.hide();}
					}]
				}).show();
			}
		}]
	}, '-',
	{
		title: 'View',
		xtype: 'buttongroup',
		columns: 1,
		items: [{
			scale: 'small',
			iconAlign: 'left',
			text: 'FASTA DNA',
			icon: '/patric/images/toolbar_dna.png',
			handler: function(me) {
				var idList = me.findParentByType('featuretoolbar').getSelectedID();
				if (idList == null) { 
					return false; 
				} else {
					this.fireEvent('ShowDownloadFasta', 'display', 'dna', idList.join(","));
				}
			}
		}, {
			scale: 'small',
			iconAlign: 'left',
			text: 'FASTA Protein',
			icon: '/patric/images/toolbar_protein.png',
			handler: function(me) {
				var idList = me.findParentByType('featuretoolbar').getSelectedID();
				if (idList == null) { 
					return false; 
				} else {
					this.fireEvent('ShowDownloadFasta', 'display', 'protein', idList.join(","));
				}
			}
		}]
	},
	{
		title: 'Download',
		xtype: 'buttongroup',
		width: 115,
		items: [
			{
				scale: 'small',
				iconAlign: 'left',
				width: 110,
				text: 'Table',
				icon: '/patric/images/toolbar_table.png',
				xtype: 'splitbutton',
				menu: [{
					xtype: 'tbar_menu_dn_tb_txt',
					handler: function(me) {
						me.fireEvent('downloadGrid','txt');
					}
				},
				{
					xtype: 'tbar_menu_dn_tb_xls',
					handler: function(me) {
						me.fireEvent('downloadGrid','xlsx');
					}
				}]
			},
			{
				scale: 'small',
				iconAlign: 'left',
				text: 'FASTA',
				width: 110,
				icon: '/patric/images/toolbar_fasta.png',
				xtype: 'splitbutton',
				menu: [{
					xtype: 'tbar_menu_dn_dna',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent('ShowDownloadFasta', 'download', 'dna', idList.join(","));
						}
					}
				},
				{
					xtype: 'tbar_menu_dn_protein',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent('ShowDownloadFasta', 'download', 'protein', idList.join(","));
						}
					}
				},
				{
					xtype: 'tbar_menu_dn_dnaprotein',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent('ShowDownloadFasta', 'download', 'both', idList.join(","));
						}
					}
				}]
			}
		]
	},
	{
		title: 'Tools',
		xtype: 'buttongroup',
		columns: 2,
		items: [{
			xtype: 'tbar_btn_pathway', 
			width: 120,
			handler: function(me) {
				var idList = me.findParentByType('featuretoolbar').getSelectedID();
				if (idList == null) { 
					return false; 
				} else {
					processFigfamSelectedItems("", "pathway_enrichment", "", "", "", idList.join(","));
				}
			}
		}, {
			xtype: 'tbar_btn_msa',
			width: 140,
			handler: function(me) {
				var idList = me.findParentByType('featuretoolbar').getSelectedID();
				if (idList == null) { 
					return false; 
				} else {
					this.fireEvent('runMSAFeature', idList.join(","));
				}
			}
		}, {
			xtype: 'tbar_btn_mapidsto',
			width: 120,
			menu:{
				defaults:{
					height:16,
					style:'margin-left: 15px; margin-bottom: 0px; margin-right: 0px; margin-top: 5px;',
					plain:true
				},
				items:[{	
					text:'<b>PATRIC Identifiers</b>',
					style:'margin-left: 5px; margin-bottom: 0px; margin-right: 0px; margin-top: 5px;'
				},{
					text: 'PATRIC Locus Tag', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "PATRIC Locus Tag", idList.join(","));
						}
					}
				},{
					text: 'PATRIC ID', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "PATRIC ID", idList.join(","));
						}
					}
				},{
					text: 'PSEED ID', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "PSEED ID", idList.join(","));
						}
					}
				},{
					text:'<b>REFSEQ Identifiers</b>',
					style:'margin-left: 5px; margin-bottom: 0px; margin-right: 0px; margin-top: 5px;'
				},{
					text: 'RefSeq Locus Tag', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "RefSeq Locus Tag", idList.join(","));
						}
					}
				},{
					text: 'RefSeq', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "RefSeq", idList.join(","));
						}
					}
				},{
					text: 'Gene ID', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "Gene ID", idList.join(","));
						}
					}
				},{
					text: 'GI', 
					handler: function(me){
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "GI", idList.join(","));
						}
					}
				},{
					text:'<b>Other Identifiers</b>',
					style:'margin-left: 5px; margin-bottom: 0px; margin-right: 0px; margin-top: 5px;'
				},{
					text:'Allergome',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "Allergome", idList.join(","));
						}
					}
				},{
					text:'BioCyc',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "BioCyc", idList.join(","));
						}
					}
				},{
					text:'DIP',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "DIP", idList.join(","));
						}
					}
				},{
					text:'DisProt',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "DisProt", idList.join(","));
						}
					}
				},{
					text:'DrugBank',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "DrugBank", idList.join(","));
						}
					}
				},{
					text:'ECO2DBASE',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "ECO2DBASE", idList.join(","));
						}
					}
				},{
					text:'EMBL',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EMBL", idList.join(","));
						}
					}
				},{
					text:'EMBL-CDS',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EMBL-CDS", idList.join(","));
						}
					}
				},{
					text:'EchoBASE',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EchoBASE", idList.join(","));
						}
					}
				},{
					text:'EcoGene',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EcoGene", idList.join(","));
						}
					}
				},{
					text:'EnsemblGenome',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EnsemblGenome", idList.join(","));
						}
					}
				},{
					text:'EnsemblGenome_PRO',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EnsemblGenome_PRO", idList.join(","));
						}
					}
				},{
					text:'EnsemblGenome_TRS',
					handler: function(me) {
						var idList = me.findParentByType('featuretoolbar').getSelectedID();
						if (idList == null) { 
							return false; 
						} else {
							this.fireEvent("callIDMapping", "EnsemblGenome_TRS", idList.join(","));
						}
					}
				},
				{
					text:'More ...',
					itemCls:'x-menu-item-cstm',
					menu:[{
						text:'GeneTree',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "GeneTree", idList.join(","));
							}
						}
					},{
						text:'GenoList',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "GenoList", idList.join(","));
							}
						}
					},{
						text:'GenomeReviews',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "GenomeReviews", idList.join(","));
							}
						}
					},{
						text:'HOGENOM',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "HOGENOM", idList.join(","));
							}
						}
					},{
						text:'HSSP',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "HSSP", idList.join(","));
							}
						}
					},{
						text:'KEGG',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "KEGG", idList.join(","));
							}
						}
					},{
						text:'LegioList',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "LegioList", idList.join(","));
							}
						}
					},{
						text:'Leproma',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "Leproma", idList.join(","));
							}
						}
					},{
						text:'MEROPS',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "MEROPS", idList.join(","));
							}
						}
					},{
						text:'MINT',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "MINT", idList.join(","));
							}
						}
					},{
						text:'NMPDR',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "NMPDR", idList.join(","));
							}
						}
					},{
						text:'OMA',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "OMA", idList.join(","));
							}
						}
					},{
						text:'OrthoDB',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "OrthoDB", idList.join(","));
							}
						}
					},{
						text:'PDB',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "PDB", idList.join(","));
							}
						}
					},{
						text:'PeroxiBase',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "PeroxiBase", idList.join(","));
							}
						}
					},{
						text:'PptaseDB',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "PptaseDB", idList.join(","));
							}
						}
					},{
						text:'ProtClustDB',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "ProtClustDB", idList.join(","));
							}
						}
					},{
						text:'PseudoCAP',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "PseudoCAP", idList.join(","));
							}
						}
					},{
						text:'REBASE',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "REBASE", idList.join(","));
							}
						}
					},{
						text:'Reactome',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "Reactome", idList.join(","));
							}
						}
					},{
						text:'RefSeq_NT',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "RefSeq_NT", idList.join(","));
							}
						}
					},{
						text:'TCDB',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "TCDB", idList.join(","));
							}
						}
					},{
						text:'TIGR',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "TIGR", idList.join(","));
							}
						}
					},{
						text:'TubercuList',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "TubercuList", idList.join(","));
							}
						}
					},{
						text:'UniParc',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "UniParc", idList.join(","));
							}
						}
					},{
						text:'UniProtKB-ID',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "UniProtKB-ID", idList.join(","));
							}
						}
					},{
						text:'UniRef100',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "UniRef100", idList.join(","));
							}
						}
					},{
						text:'UniRef50',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "UniRef50", idList.join(","));
							}
						}
					},{
						text:'UniRef90',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "UniRef90", idList.join(","));
							}
						}
					},{
						text:'World-2DPAGE',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "World-2DPAGE", idList.join(","));
							}
						}
					},{
						text:'eggNOG',
						handler: function(me){
							var idList = me.findParentByType('featuretoolbar').getSelectedID();
							if (idList == null) { 
								return false; 
							} else {
								this.fireEvent("callIDMapping", "eggNOG", idList.join(","));
							}
						}
					}
					]
				}]
			}
		}]
	},
	{
		title: 'Columns',
		xtype: 'buttongroup',
		items: [{
			xtype: 'tbar_btn_showhide',
			menu: [],
			handler: function(me) {
				if (me.menu.items.length == 0) {
					var grid = me.findParentByType("gridpanel");
					
					if (grid != null) {
						me.menu = grid.headerCt.getMenu().child("#columnItem").menu;
					} else {
						var view = me.findParentByType("detailview")
						if (view != null) {
							grid = view.child("#panel_grid").child("#featureview");
							me.menu = grid.headerCt.getMenu().child("#columnItem").menu;
						}
					}
					//console.log(grid.headerCt.getMenu().child("#columnItem").menu.items.items.length);
					me.showMenu();
				}
			}
		}/*, {
			xtype: 'tbar_btn_resetcolumnstate',
			handler: function(me) {
				this.fireEvent("resetColumnState");
			}
		}*/]
	}, '->', '-',
	{
		xtype: 'tbar_btngrp_help'
	}],
	layout: {
		type: 'hbox',
		align: 'stretch'
	}
});

Ext.define('VBI.Workspace.view.toolbar.Genome', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.genometoolbar',
	getSelectedGroup: function() {
		var viewport = Ext.getCmp('workspace_view');
		var selection;
		var groupList = new Array();
		
		if (viewport.activeItem == "groupview") {
			selection = new Array();
			groupList.push( Ext.getCmp('workspace_groupinfoeditor').record.get("tagId") );
			
		} else {
			selection = Ext.getCmp('columnbrowser_groups').getSelectionModel().getSelection();
			Ext.Array.each(selection, function(item) {
				groupList.push(item.get("tagId"));
			});
		}
		return groupList;
	},
	getSelectedID: function() {
		var selection;
		
		if ( Ext.getCmp('workspace_view').activeItem == "groupview") {
			selection = Ext.getCmp('workspace_detailview').child('#panel_grid').child('#genomeview').getSelectionModel().getSelection();
		} else {
			selection = Ext.getCmp('workspace_listview').child('#genomeview').getSelectionModel().getSelection();
		}
		
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "No genome was selected");
			return null;
		} else {
			var selectedIDs = new Array();
			Ext.Array.each(selection, function(item) {
				selectedIDs.push(item.get("genome_info_id"));
			});
			return selectedIDs;
		}
	},
	refreshWorkspaceViews: function() {
		
		Ext.getStore('Mappings').load({
			callback: function() {
				Ext.getStore('Groups').load({
					callback: function() {
						Ext.getStore('ColumnBrowser').refresh({
							callback:function() {
								Ext.getStore('Stations').load({
									callback: function() {
										Ext.getCmp('workspace_station').setDefault("Genomes");
										updateCartInfo();
									}
								});
							}
						});
					}
				});
			}
		});
	},
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [{
		title: 'Workspace', 
		xtype: 'buttongroup',
		columns: 1, 
		items:[{
			xtype:'tbar_btn_remove',
			handler: function(btn, e) {
				var groupList = btn.findParentByType('genometoolbar').getSelectedGroup(),
				idList = btn.findParentByType('genometoolbar').getSelectedID(),
				me = this;
				
				if (idList == null) { return false; }
					
				if (groupList.length == 0) {
					// no group selected, delete from workspace
					Ext.Msg.show({
						msg: 'Do you want to delete this genome from your workspace?',
						buttons: Ext.Msg.OKCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "ok" && idList.length > 0) {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom: 'workspace',
										idType: 'Genome',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
						
				} else {
							
					Ext.Msg.show({
						msg: 'Do you want to delete this genome from your selected groups? Click No if you want to delete from entire workspace',
						buttons: Ext.Msg.YESNOCANCEL,
						icon: Ext.Msg.QUESTION,
						fn: function(buttonId, opt) {
							if (buttonId == "yes") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom:'groups',
										groups: groupList.join(","),
										idType: 'Genome',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							} else if (buttonId == "no") {
								Ext.Ajax.request({
									url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=removeTrack',
									params: {
										removeFrom:'workspace',
										idType: 'Genome',
										idList: idList.join(",")
									},
									success: function(response) {
										me.findParentByType('toolbar').refreshWorkspaceViews();
									}
								});
							}
						}
					});
				}
			}
		},{
			xtype:'tbar_btn_create',
			handler: function(btn, e) {
				var idList = btn.findParentByType('genometoolbar').getSelectedID();
				if (idList == null) { return false; }
				var me = this;
				var btnGroupPopupSave = new Ext.Button({
					text:'Save to Group',
					handler: function(btn, e) {
						//console.log("custom button for save to group - genome level");
						saveToGroup(idList.join(","), 'Genome');
							
						me.findParentByType('toolbar').refreshWorkspaceViews();
					}
				});
				
				popup = Ext.create('VBI.Workspace.view.Toolbar.window.AddToGroup', {
					title: 'Add Selected Genomes to Group',
					buttons: [btnGroupPopupSave,{
						text: 'Cancel',
						handler: function(){popup.hide();}
					}]
				}).show();
			}
		}]
	}, '-',
	{
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
	},
	{
		title: 'Columns',
		xtype: 'buttongroup',
		items: [{
			xtype: 'tbar_btn_showhide',
			menu: [],
			handler: function(me) {
				if (me.menu.items.length == 0) {
					var grid = me.findParentByType("gridpanel");
					
					if (grid != null) {
						me.menu = grid.headerCt.getMenu().child("#columnItem").menu;
					} else {
						var view = me.findParentByType("detailview")
						if (view != null) {
							grid = view.child("#panel_grid").child("#genomeview");
							me.menu = grid.headerCt.getMenu().child("#columnItem").menu;
						}
					}
					//console.log(grid.headerCt.getMenu().child("#columnItem").menu);
					me.showMenu();
				}
			}
		}/*, {
			xtype: 'tbar_btn_resetcolumnstate',
			handler: function(me) {
				this.fireEvent("resetColumnState");
			}
		}*/]
	}, '->', '-',
	{
		xtype: 'tbar_btngrp_help'
	}]
});
Ext.define('VBI.Workspace.view.toolbar.Global', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.globaltoolbar',
	switchViewButtons: function(cView) {
		if (cView == null) {
			cView = Ext.getCmp('workspace_view').activeItem;
		}
		if (cView == 'listview') {
			this.child('#btnItemView').toggle(true, true);
			this.child('#btnGroupView').toggle(false, true);
		} else {
			this.child('#btnItemView').toggle(false, true);
			this.child('#btnGroupView').toggle(true, true);
		}
	},
	items: [
		{
			xtype: 'tbtext',
			text: '<b>Workspace</b>'
		},
		'->', /*
		{
			text: '(new feature group)',
			handler: function() {
				Ext.Ajax.request({
					url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=create',
					params: {
						group_name:'',
						group_desc:'description2',
						group_type:'Feature',	//Feature or Genome
						tracks:'30098156,30169012,30176074,30216134,30255345,30285150,30330130,30312096,30300283,30405530,30558208,30532095,30509534,30465308,30653216,30639677,30645960,30722337,30680621,30938147', //na_feature_id or genome_info_id
						tags:'tag1, tag2, tag3, tag4' //tags delimitted by comma (,)
					},
					disableCaching: false
				});
			}
		},
		{
			text: '(new genome group)',
			handler: function() {
				Ext.Ajax.request({
					url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=create',
					params: {
						group_name:'genome group',
						group_desc:'description',
						group_type:'Genome',	//Feature or Genome
						tracks:'38055,25663,113143', //na_feature_id or genome_info_id
						tags:'' //tags delimitted by comma (,)
					},
					disableCaching: false
				});
			}
		}, 
		{
			text: '(status)',
			handler: function() {
				Ext.Ajax.request({
					url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=status',
					success: function(response) {
						console.log(response.responseText);
					}
				});
			}
		}, 
		'-', */
		{
			xtype: 'button',
			itemId: 'btnItemView',
			text: 'Item View',
			icon: '/patric/images/workspace_item_view_icon.png',
			enableToggle: true,
			handler: function(me, e) {
				me.fireEvent('switchToListView');
				me.ownerCt.switchViewButtons('listview');
			}
		},
		'-',
		{
			xtype: 'button',
			itemId: 'btnGroupView',
			text: 'Group View',
			icon: '/patric/images/workspace_group_view_icon.png',
			enableToggle: true,
			handler: function(me, e) {
				me.fireEvent('switchToGroupView');
				me.ownerCt.switchViewButtons('groupview');
			}
		}
	]
});Ext.define('VBI.Workspace.view.toolbar.Group', {
	extend: 'Ext.toolbar.Toolbar',
	alias: 'widget.grouptoolbar',
	border: 0,
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	getSelectedID: function() {
		var selection = Ext.getCmp('workspace_groupbrowser').getSelectionModel().getSelection();
		if (selection.length == 0) {
			Ext.Msg.alert("Alert", "no group selected");
			return null;
		} else {
			var selectedIDs = new Array();
			Ext.Array.each(selection, function(item) {
				selectedIDs.push(item.get("tagId"));
			});
			return selectedIDs;
		}
	},
	getSelectedGroupType: function() {
		var selection = Ext.getCmp('workspace_groupbrowser').getSelectionModel().getSelection();
		if (selection.length > 0) {
			return selection[0].get('type');
		}
	},
	refreshWorkspaceViews: function() {
		Ext.getStore('ColumnBrowser').refresh({
			callback:function() {
				Ext.getStore('Groups').load({
					callback: function() {
						Ext.getStore('Stations').load();
						updateCartInfo();
					}
				});
			}
		});
	},
	items: [{
		title: 'Workspace', 
		xtype: 'buttongroup', 
		columns: 1,
		items:[{
			xtype:'tbar_btn_remove',
			handler: function(me, e) {
						
				var idList = me.findParentByType('grouptoolbar').getSelectedID();
				if (idList == null) { return false; }
						
				Ext.Msg.show({
					msg: 'Do you want to delete this group from your workspace?',
					buttons: Ext.Msg.OKCANCEL,
					icon: Ext.Msg.QUESTION,
					fn: function(buttonId, opt) {
						if (buttonId == "ok" && idList.length > 0) {
							Ext.Ajax.request({
								url:'/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
								params: {
									action_type: 'groupAction',
									action: 'removeGroup',
									idList: idList.join(",")
								},
								success: function(response) {
									me.findParentByType('toolbar').refreshWorkspaceViews();
								}
							});
						}
					}
				});
						
			}
		}]
	}, '-',
	{
		title: 'Tools',
		xtype: 'buttongroup',
		items: [{
			scale: 'large',
			iconAlign: 'left',
			text: 'Group Explorer',
			icon: '/patric/images/toolbar_gse.png',
			handler: function(me, e) {
				var idList = me.findParentByType('grouptoolbar').getSelectedID();
				if (idList == null) { return false; }
						
				var type = me.findParentByType('grouptoolbar').getSelectedGroupType();
						
				me.fireEvent("runGroupExplorer", idList.join(","), type);
			}
		}]
	}, {
		title: 'Sort',
		xtype: 'buttongroup',
		columns: 3,
		items: [{
			text: 'Name Ascending',
			icon: '/patric/images/hmenu-asc.gif',
			handler: function() {
				Ext.getStore('Groups').sort('name','ASC');
			}
		}, {
			text: 'Oldest First',
			icon: '/patric/images/calendar_icon.png',
			handler: function() {
				Ext.getStore('Groups').sort('mdate','ASC');
			}
		}, {
			text: 'Smallest First',
			icon: '/patric/images/sort_count_asc.png',
			handler: function() {
				Ext.getStore('Groups').sort('members','ASC');
			}
		}, {
			text: 'Name Descending',
			icon: '/patric/images/hmenu-desc.gif',
			handler: function() {
				Ext.getStore('Groups').sort('name','DESC');
			}
		}, {
			text: 'Newest First',
			icon: '/patric/images/calendar_icon.png',
			handler: function() {
				Ext.getStore('Groups').sort('mdate','DESC');
			}
		}, {
			text: 'Largest First',
			icon: '/patric/images/sort_count_des.png',
			handler: function() {
				Ext.getStore('Groups').sort('members','DESC');
			}
		}]
	},
	'->', '-',
	{
		xtype: 'tbar_btngrp_help'
	}]
});Ext.define('VBI.Workspace.view.toolbar.Paging', {
	extend: 'Ext.toolbar.Paging',
	alias: 'widget.patricpagingtoolbar',
	beforePageSizeText: 'Show',
	afterPageSizeText: 'per page',
	displayMsg: 'Displaying record {0} - {1} of {2}',
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
		}, {
			itemId: 'prev',
			tooltip: me.prevText,
			overflowText: me.prevText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-prev',
			disabled: true,
			handler: me.movePrevious,
			scope: me
		}, '-',
		me.beforePageText, {
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
		}, {
			xtype: 'tbtext',
			itemId: 'afterTextItem',
			text: Ext.String.format(me.afterPageText, 1)
		}, '-', {
			itemId: 'next',
			tooltip: me.nextText,
			overflowText: me.nextText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-next',
			disabled: true,
			handler: me.moveNext,
			scope: me
		}, {
			itemId: 'last',
			tooltip: me.lastText,
			overflowText: me.lastText,
			iconCls: Ext.baseCSSPrefix + 'tbar-page-last',
			disabled: true,
			handler: me.moveLast,
			scope: me
		}, '->', '-', /* modification start */
		me.beforePageSizeText, {
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
			initComponent: function() {
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
						var value = field.getValue(),
							valueIsNull = value === null;

						if (valueIsNull == false) {
							me.updateStore();
						}
					}
				}
			}
		},
		me.afterPageSizeText, {
			itemId: 'refresh',
			text: 'Apply',
			style: {
				'border-color': '#81a4d0',
				'background-color': '#dbeeff',
				'background-image': '-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler: function() {
				me.updateStore();
			},
			scope: me
		}, '-', {
			itemId: 'saveState',
			text: 'Apply to ALL tables',
			style: {
				'border-color': '#81a4d0',
				'background-color': '#dbeeff',
				'background-image': '-webkit-linear-gradient(top,#dbeeff,#d0e7ff 48%,#bbd2f0 52%,#bed6f5)'
			},
			handler: function() {
				if (me.updateStore() != false) {
					me.child('#pagesize').fireEvent('savePageSize');
				}
			},
			scope: me
		}, '-'];
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
	}/*,
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
	}*/
});
Ext.define('VBI.Workspace.view.ColumnBrowser', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.columnbrowser',
	border: false,
	layout: {
		type: 'hbox',
		align: 'stretch'
	},
	items: [{
		xtype: 'grid', //column browser
		id: 'columnbrowser_groups',
		flex: 1,
		border: true,
		store: 'ColumnBrowser.Groups',
		columns: [{
			header: 'Groups', 
			dataIndex: 'name', 
			flex: 1
		}],
		multiSelect: true,
		dockedItems: [{
			xtype:'toolbar',
			dock:'bottom',
			items: [{
				text:'reset',
				handler: function(btn, e) {
					btn.fireEvent("columnbrowserfilter_reset", "groups");
				}
			}]
		}],
		listeners: {
			'selectionchange': {
				fn: function (model, selected, options) { 
					if (selected.length > 0) {
						this.fireEvent("columnbrowserfilter", "groups", selected);
					}
				}
			}
		}
	}, {
		xtype: 'grid',
		id: 'columnbrowser_tags',
		flex: 1,
		border: true,
		store: 'ColumnBrowser.Tags',
		columns:[{
			header: 'Tags', 
			dataIndex: 'name', 
			flex: 1
		}],
		multiSelect: true,
		dockedItems: [{
			xtype:'toolbar',
			dock:'bottom',
			items: [{
				text:'reset',
				handler: function() {
					this.fireEvent("columnbrowserfilter_reset", "tags");
				}
			}]
		}],
		listeners: {
			'selectionchange': {
				fn: function (model, selected, options) { 
					if (selected.length > 0) {
						this.fireEvent("columnbrowserfilter", "tags", selected);
					}
				}
			}
		}
	}]
});

Ext.define('VBI.Workspace.view.DetailView', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.detailview',
	requires: [
		'VBI.Workspace.view.group.GroupInfoEditor', 'VBI.Workspace.view.group.ExperimentInfoEditor',
		'VBI.Workspace.view.toolbar.Group', 'VBI.Workspace.view.toolbar.ExpressionSample',
		'VBI.Workspace.view.columns.Genome', 'VBI.Workspace.view.columns.Feature',
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
		activeItem: 'groupinfo',
		items:[{
			itemId: 'groupinfo',
			xtype: 'groupinfoeditor',
			id: 'workspace_groupinfoeditor'
		}, {
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
		activeItem: 'group',
		items: [{
			itemId: 'group',
			xtype: 'grouptoolbar'
		}, {
			itemId: 'feature',
			xtype: 'featuretoolbar'
		}, {
			itemId: 'genome',
			xtype: 'genometoolbar'
		}, {
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
		activeItem: 'genomeview',
		items: [{
			// feature group detail view
			itemId: 'featureview',
			xtype: 'gridpanel',
			store: 'Features',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.Feature'),
			stateful: true,
			stateId: 'featurelist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'patricpagingtoolbar',
				store: 'Features',
				dock: 'bottom',
				displayInfo: true
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}, {
			// genome group detail view
			itemId: 'genomeview',
			xtype: 'gridpanel',
			store: 'Genomes',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.Genome'),
			stateful: true,
			stateId: 'genomelist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'patricpagingtoolbar',
				store: 'Genomes',
				dock: 'bottom',
				displayInfo: true
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}, {
			// expression experiment group detail view
			itemId: 'experimentview',
			xtype: 'gridpanel',
			store: 'ExpressionExperiments',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.ExpressionExperiment'),
			stateful: true,
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
			stateful: true,
			stateId: 'comparisonlist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}]
	}]
});
Ext.define('VBI.Workspace.view.GroupView', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.groupview',
	requires: [
		'VBI.Workspace.view.toolbar.Group',
		'VBI.Workspace.view.group.Browser'
	],
	border: true,
	dockedItems:[{
		xtype: 'grouptoolbar'
	}],
	autoScroll: true,
	items:[{
		xtype: 'groupbrowser',
		id: 'workspace_groupbrowser',
		store: 'Groups'
	}]
});
Ext.define('VBI.Workspace.view.ListView', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.listview',
	requires: [
		'VBI.Workspace.view.ColumnBrowser',
		'VBI.Workspace.view.toolbar.Feature', 'VBI.Workspace.view.columns.Feature',
		'VBI.Workspace.view.toolbar.Genome', 'VBI.Workspace.view.columns.Genome',
		'VBI.Workspace.view.toolbar.ExpressionExperiment', 'VBI.Workspace.view.columns.ExpressionExperiment',
		'VBI.Workspace.view.selection.CheckboxModel'
	],
	border: false,
	layout: 'border',
	items: [{
		xtype: 'columnbrowser', //column browser
		title: 'Column Browser',
		width: 200,
		region: 'west',
		collapsible: true,
		resizable: true
	}, {
		xtype: 'panel',
		region: 'center',
		layout: 'card',
		activeItem: 'featureview',
		id: 'workspace_listview',
		flex: 1,
		border: false,
		items: [{
			// list of features
			itemId: 'featureview',
			xtype: 'gridpanel',
			store: 'Features',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.Feature'),
			stateful: true,
			stateId: 'featurelist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'featuretoolbar',
				dock: 'top'
			}, {
				xtype: 'patricpagingtoolbar',
				store: 'Features',
				dock: 'bottom'
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}, {
			// list of genomes
			itemId: 'genomeview',
			xtype: 'gridpanel',
			store: 'Genomes',
			border: false,
			columns: Ext.create('VBI.Workspace.view.columns.Genome'),
			stateful: true,
			stateId: 'genomelist',
			stateEvents: ['hide', 'show', 'columnmove', 'columnresize'],
			dockedItems: [{
				xtype: 'genometoolbar',
				dock: 'top'
			}, {
				xtype: 'patricpagingtoolbar',
				store: 'Genomes',
				dock: 'bottom'
			}],
			selModel: Ext.create('VBI.Workspace.view.selection.CheckboxModel', {itemId:'checkBOX'})
		}, {
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
Ext.define('VBI.Workspace.view.StationsList', {
	extend: 'Ext.tree.Panel',
	alias: 'widget.stationslist',
	store: 'Stations',
	rootVisible: false,
	hideHeaders: true,
	useArrows: true,
	columns:[{
		xtype: 'treecolumn',
		dataIndex: 'name', 
		flex: 1
	}],
	setDefault: function(station) {
		if (station == "Features") 
		{
			this.getView().select(2);
			var node = this.getStore().getNodeById(2);
			this.fireEvent('itemclick', this.getView(), node);
		}
		else if (station == "Genomes") 
		{
			this.getView().select(1);
			var node = this.getStore().getNodeById(3);
			this.fireEvent('itemclick', this.getView(), node);
		}
		else if (station == "ExpressionExperiments")
		{
			this.getView().select(3);
			var node = this.getStore().getNodeById(4);
			this.fireEvent('itemclick', this.getView(), node);
		}
	}
});
/**
* define Toolbar Buttons
*/
Ext.define('VBI.Workspace.view.Toolbar.button.Remove', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_remove',
	scale: 'small',
	iconAlign: 'left',
	text: 'Remove',
	width: 110,
	icon: '/patric/images/toolbar_workspace_remove.png'
});

Ext.define('VBI.Workspace.view.Toolbar.button.Create', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_create',
	scale: 'small',
	iconAlign: 'left',
	text: 'Create',
	width: 110,
	icon: '/patric/images/toolbar_workspace_add.png'
});

Ext.define('VBI.Workspace.view.Toolbar.button.Pathway', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_pathway',
	scale: 'small',
	iconAlign: 'left',
	text: 'Pathway Summary',
	icon: '/patric/images/16x16-toolbar-icon-pathway.png'
});

Ext.define('VBI.Workspace.view.Toolbar.button.MSA', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_msa',
	scale: 'small',
	iconAlign: 'left',
	text: 'Multiple Seq Alignment',
	icon: '/patric/images/16x16-toolbar-icon-msa.png'
});

Ext.define('VBI.Workspace.view.Toolbar.button.MapIDsTo', {
	extend: 'Ext.Button',
	alias: 'widget.tbar_btn_mapidsto',
	scale: 'small',
	iconAlign: 'left',
	text: 'MAP IDs to...',
	icon: '/patric/images/16x16-toolbar-icon-idmapping.png'
});

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
	//icon: '/patric/images/toolbar_hideshow.png',
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

Ext.define('VBI.Workspace.view.Toolbar.menu.Download_dna', {
	extend: 'Ext.menu.Item',
	alias: 'widget.tbar_menu_dn_dna',
	scale: 'small',
	iconAlign: 'left',
	text: 'DNA',
	icon: '/patric/images/toolbar_dna.png',
});

Ext.define('VBI.Workspace.view.Toolbar.menu.Download_protein', {
	extend: 'Ext.menu.Item',
	alias: 'widget.tbar_menu_dn_protein',
	scale: 'small',
	iconAlign: 'left',
	text: 'Protein',
	icon: '/patric/images/toolbar_protein.png'
});

Ext.define('VBI.Workspace.view.Toolbar.menu.Download_dnaprotein', {
	extend: 'Ext.menu.Item',
	alias: 'widget.tbar_menu_dn_dnaprotein',
	scale: 'small',
	iconAlign: 'left',
	text: 'DNA/Protein',
	icon: '/patric/images/toolbar_dna_protein.png'
});

/**
* define Toolbar ButtonGroups
*/
Ext.define('VBI.Workspace.view.Toolbar.buttongroup.Help', {
	extend: 'Ext.container.ButtonGroup',
	alias: 'widget.tbar_btngrp_help',
	title: 'Help',
	items: [{
		scale: 'large',
		text: 'FAQs',
		icon: '/patric/images/toolbar_faq.png',
		handler: function() {
			window.open("http://enews.patricbrc.org/faqs/", "_new", "menubar=1,resizable=1,scrollbars=1, fullscreen=1, toolbar=1,titlebar=1,status=1");
		}
	}]
});

/**
* define Modal 
*/
Ext.define('VBI.Workspace.view.Toolbar.window.AddToGroup', {
	extend: 'Ext.Window',
	alias: 'widget.tbar_window_addtogroup',
	layout:'fit',
	width:350,
	height:300,
	closeAction:'hide',
	modal: true,
	items: [{
		xtype: 'addtoworkspace',
		id: 'ATGform'
	}]
});
Ext.define('VBI.Workspace.view.Viewport', {
	extend: 'Ext.container.Viewport',
	requires: [
		'VBI.Workspace.view.Toolbar',
		'VBI.Workspace.view.toolbar.Global',
		'VBI.Workspace.view.toolbar.Paging',
		'VBI.Workspace.view.StationsList',
		'VBI.Workspace.view.ListView',
		'VBI.Workspace.view.GroupView',
		'VBI.Workspace.view.DetailView'
	],
	layout: 'border',
	items: [{
		region: 'center',
		xtype: 'panel',
		dockedItems: [{
			dock: 'top',
			xtype: 'globaltoolbar',
			id: 'workspace_globaltoolbar',
			height: 30
		}],
		layout: 'border',
		items: [{
			region: 'west',
			xtype: 'panel',
			border: false,
			items: [{
				xtype: 'stationslist',
				id: 'workspace_station',
				border: false,
				width: 150,
				height: 250
			}, {
				xtype: 'panel',
				layout: 'vbox',
				border: false,
				cls: 'no-underline-links',
				items: [{
					xtype: 'component',
					autoEl: {
						tag: 'img',
						onclick: "launchTranscriptomicsUploader()",
						style: "cursor: pointer",
						src: "/patric/images/transcriptomics_uploader_ad.png"
					},
					margin: '0 25px',
					height: 99
				}, {
					xtype: 'displayfield',
					width: 130,
					margin: '0 0 0 10px',
					value: '<a href="javascript:void(0)" onclick="launchTranscriptomicsUploader()">Upload your transcriptomics data</a> to analyze using PATRIC tools and to compare with other published datasets.'
				}, {
					xtype: 'component',
					overCls: 'no-underline-links',
					autoEl: {
						tag: 'a',
						href: 'http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs/',
						html: "Learn more",
						target: '_blank',
						cls: 'double-arrow-link'
					},
					padding: '0 0 0 10px'
				}]
			}]
		}, {
			region: 'center',
			xtype: 'panel',
			id: 'workspace_view',
			layout: 'card',
			stateful: true,
			activeItem: 'listview',
			stateEvents:['updateview'],
			getState: function() {
				return { activeItem: this.activeItem };
			},
			applyState: function(state) {
				if (state != undefined && this.activeItem != state.activeItem ) {
					this.activeItem = state.activeItem;
					this.fireEvent('updateview');
				}
			},
			border: false,
			items: [{
				itemId: 'listview',
				xtype: 'listview'
			}, {
				itemId: 'groupview',
				xtype: 'groupview'
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
});Ext.define('VBI.Workspace.controller.ColumnBrowser', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'columnbrowser > grid': {
				columnbrowserfilter: this.onColumnBrowserFilter
			},
			'columnbrowser > grid > toolbar > button': {
				columnbrowserfilter_reset: this.onColumnBrowserReset
			}
		});
	},
	onColumnBrowserReset: function(type) {
		
		var storeMap = Ext.getStore('Mappings');
		var storeFeatures = Ext.getStore('Features');
		
		if (type == "groups") {	
		
			storeMap.setTrackFilter("Group", null);
			storeMap.setTrackFilter("String", null);
			storeFeatures.filterByTracks(storeMap.getFilteredTracks());
			
			this.resetColumnBrowserGroupsUI();
			this.resetColumnBrowserTagsUI();
		}
		else if (type == "tags") {
			
			storeMap.setTrackFilter("String", null);
			storeFeatures.filterByTracks(storeMap.getFilteredTracks());
			
			this.resetColumnBrowserTagsUI();
		}
	},
	onColumnBrowserFilter: function(type, selected) {
		
		var storeMap = Ext.getStore('Mappings');
		var storeCBTag = Ext.getStore('ColumnBrowser.Tags');
		var storeFeatures = Ext.getStore('Features');
		var storeGenomes = Ext.getStore('Genomes');
		var storeExpressionExperiments = Ext.getStore('ExpressionExperiments');
		var storeGroups = Ext.getStore('Groups');
		
		if (type == "groups") {
			
			// 1. get Prepared
				var targetGroupTags = new Array();
				Ext.each(selected, function(r) {
					targetGroupTags.push(r.get("tagId"));
				});
				
			// 2. update Tracks
				//var targetTracks = storeMap.getTracksByTag(targetGroupTags);
				storeMap.setTrackFilter("Group", targetGroupTags);
				storeMap.setTrackFilter("String", null);
				// UI - reset column browser tag list
				this.resetColumnBrowserTagsUI();
				
				var targetTracks = storeMap.getFilteredTracks();
				storeFeatures.filterByTracks(targetTracks);
				storeGenomes.filterByTracks(targetTracks);
				storeExpressionExperiments.filterByTracks(targetTracks);
				
			// 3. update Tags
				var targetStringTags = storeMap.getTagsByTrack(targetTracks);
				storeCBTag.filterByTag(targetStringTags);
				
			// 4. update Group view - done
				storeGroups.filterByTag(targetGroupTags);
			
		} else if (type == "tags") {
			
			// 1. get Prepared
				var targetStringTags = new Array();
				Ext.each(selected, function(r) {
					targetStringTags.push(r.get("tagId"));
				});
				
			// 2. update Track view
				storeMap.setTrackFilter("String", targetStringTags);
				var targetTracks = storeMap.getFilteredTracks();
				storeFeatures.filterByTracks(targetTracks);
				storeGenomes.filterByTracks(targetTracks);
			
			// 3. update Group view
			//	storeGroups.filterByTag(targetStringTags);
			// TODO: should we go through the Tracks?
		}
	},
	resetColumnBrowserGroupsUI: function() {
		Ext.getCmp('columnbrowser_groups').getSelectionModel().deselectAll();
	},
	resetColumnBrowserTagsUI: function() {
		Ext.getCmp('columnbrowser_tags').getSelectionModel().deselectAll();
	}
});
Ext.define('VBI.Workspace.controller.Experiment', {
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
				runGeneList: this.runGeneList
			},
			'expressionsampletoolbar menuitem': {
				downloadGrid: this.downloadSamples
			},
			'experimentinfoeditor > button': {
				click: this.toggleEditorFields
			}
		});
	},
	runGeneList: function(param) {
		document.location.href = "/portal/portal/patric/TranscriptomicsGene?cType=&cId=&dm=result&log_ratio=&zscore=&pk="+param;
	},
	toggleEditorFields: function(button, event, options) {
		var target = button.findParentByType('experimentinfoeditor');
		button.pressed ? target.startEdit() : target.finishEdit();
	},
	downloadExperiments: function(type) {
		var store = Ext.getStore("ExpressionExperiments"),
			PATRICExperiments = new Array(),
			USERExperiments = new Array();
		
		Ext.Array.each(store.getRange(), function(item) {
			
			if (item.get("source") == "PATRIC") {
				//PATRICExperiments.push(item.internalId);
				PATRICExperiments.push({trackType:"ExpressionExperiment", internalId: item.internalId});
			} else if (item.get("source") == "me") {
				USERExperiments.push(item.internalId);
			}
		});
		
		var fids = {
			"PATRICExperiments": PATRICExperiments,
			"USERExperiments": USERExperiments
		};
		
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "Workspace";
		Ext.getDom("fileformat").value = type;
		//Ext.getDom("fids").value = idList.join(",");
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
		//Ext.getDom("fids").value = idList.join(",");
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
});
Ext.define('VBI.Workspace.controller.Feature', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'featuretoolbar button': {
				runMSAFeature: this.runMSAFeature,
				ShowDownloadFasta: this.ShowDownloadFasta,
				callIDMapping: this.runIDMapping,
				resetColumnState: this.resetColumnState
			},
			'featuretoolbar menuitem': {
				downloadGrid: this.downloadGrid
			}
		});
	},
	runMSAFeature: function(selected) {
		Ext.Ajax.request({
			url: "/portal/portal/patric/FIGfamSorter/FigFamSorterWindow?action=b&cacheability=PAGE",
			method: 'POST',
			params: {featureIds: selected, callType:"toAligner"},
			success: function(response, opts) {
				document.location.href = "TreeAlignerB?cType=&cId=&pk=" + response.responseText;
			}
		});
	},
	runIDMapping: function(to, selected) {	
		Ext.Ajax.request({
			url: "/portal/portal/patric/IDMapping/IDMappingWindow?action=b&cacheability=PAGE",
			method: 'POST',
			params: {keyword: selected, from:'PATRIC ID', to:to, sraction:'save_params'},
			success: function(response, opts) {
				document.location.href = "IDMapping?cType=&cId=&dm=result&pk="+response.responseText+"#key="+Math.floor(Math.random()*10001)+"&pS=20&aP=1&dir=ASC&sort=genome_name,accession,start_max";
			}
		});
	},
	ShowDownloadFasta: function(action, type, selected) {
		Ext.getDom("fTableForm").action = "/patric-common/jsp/fasta_download_handler.jsp";
		Ext.getDom("fastaaction").value = action;
		Ext.getDom("fastascope").value = "Selected";
		Ext.getDom("fastatype").value = type;
		Ext.getDom("fids").value = selected;

		if (action == "display") {
			window.open("","disp","width=920,height=400,scrollbars,resizable");
			Ext.getDom("fTableForm").target = "disp";
		} else {
			Ext.getDom("fTableForm").target = "";
		}
		Ext.getDom("fTableForm").submit();
	},
	downloadGrid: function(type) {
		var idList = new Array();
		var store = Ext.getStore("Features");
		Ext.Array.each(store.getRange(), function(item) {
			idList.push(item.internalId);
		});
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "Workspace";
		Ext.getDom("fileformat").value = type;
		Ext.getDom("fids").value = idList.join(",");
		Ext.getDom("idType").value = "Feature";
		Ext.getDom("fTableForm").submit();
	},
	resetColumnState: function() {
		var baseUrl = "/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=HTTPProvider";
		var param = "&action=remove&name=featurelist";
		Ext.Ajax.request({
			url: baseUrl+param,
			method: 'GET',
			success: function(response, opts) {
				//console.log("resetColumnState: success");
			}
		});
	}
});
Ext.define('VBI.Workspace.controller.Genome', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'genometoolbar menuitem': {
				downloadGrid: this.downloadGrid
			},
			'genometoolbar button': {
				resetColumnState: this.resetColumnState
			}
		});
	},
	downloadGrid: function(type) {
		var idList = new Array(),
			store = Ext.getStore("Genomes");
			
		Ext.Array.each(store.getRange(), function(item) {
			idList.push(item.internalId);
		});
		Ext.getDom("fTableForm").action = "/patric-searches-and-tools/jsp/grid_download_handler.jsp";
		Ext.getDom("fTableForm").target = "";
		Ext.getDom("tablesource").value = "Workspace";
		Ext.getDom("fileformat").value = type;
		Ext.getDom("fids").value = idList.join(",");
		Ext.getDom("idType").value = "Genome";
		Ext.getDom("fTableForm").submit();
	},
	resetColumnState: function() {
		var baseUrl = "/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=HTTPProvider";
		var param = "&action=remove&name=genomelist";
		Ext.Ajax.request({
			url: baseUrl+param,
			method: 'GET',
			success: function(response, opts) {
				//console.log("resetColumnState: success");
			}
		});
	}
});
Ext.define('VBI.Workspace.controller.GlobalToolbar', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'globaltoolbar button': {
				switchToListView: this.onSwitchToListView,
				switchToGroupView: this.onSwitchToGroupView
			}
		});
	},
	onSwitchToListView: function(button, e) {
		var vp = Ext.getCmp('workspace_view');
		vp.applyState({activeItem:'listview'});
		vp.getLayout().setActiveItem('listview');
	},
	onSwitchToGroupView: function(button, e) {
		var vp = Ext.getCmp('workspace_view');
		vp.applyState({activeItem:'groupview'});
		vp.getLayout().setActiveItem('groupview');
	}
});
Ext.define('VBI.Workspace.controller.Group', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'grouptoolbar button': {
				runGroupExplorer: this.runGroupExplorer
			},
			'groupbrowser': {
				itemdblclick: this.switchToGroupViewer
			},
			'detailtoolbar > button': {
				click: this.switchToBrowser
			},
			'groupinfoeditor > button': {
				click: this.toggleEditorFields
			},
			'detailview': {
				viewExpDetail: this.viewExperimentDetail
			}
		});
	},
	runGroupExplorer: function(selected, type) {
		
		document.location.href = "/portal/portal/patric/GroupManagement?mode=gse&groupType="+type+"&groupId="+selected;
	},
	switchToGroupViewer: function(view, record, item, index, e, options) {
		
		var detailview = Ext.getCmp('workspace_detailview');
		detailview.child('#panel_info').getLayout().setActiveItem('groupinfo');
		
		//console.log(view, record, item, index, e, options);
		// data processing
		var storeMap = Ext.getStore('Mappings');
		storeMap.setTrackFilter("Group", record.getId());
		storeMap.setTrackFilter("String", null);
		var targetTracks = storeMap.getFilteredTracks();
		
		if (record.get('type') == 'Feature') {
			
			detailview.child('#panel_toolbar').getLayout().setActiveItem('feature');
			detailview.child('#panel_grid').getLayout().setActiveItem('featureview');
			Ext.getStore('Features').filterByTracks(targetTracks);
			
			Ext.getCmp('workspace_groupinfoeditor').loadRecord(record);
		}
		else if (record.get('type') == 'Genome') {
			
			detailview.child('#panel_toolbar').getLayout().setActiveItem('genome');
			detailview.child('#panel_grid').getLayout().setActiveItem('genomeview');
			Ext.getStore('Genomes').filterByTracks(targetTracks);
			
			Ext.getCmp('workspace_groupinfoeditor').loadRecord(record);
		}
		else if (record.get('type') == 'ExpressionExperiment') {
			
			detailview.child('#panel_toolbar').getLayout().setActiveItem('experiment');
			detailview.child('#panel_grid').getLayout().setActiveItem('experimentview');
			Ext.getStore('ExpressionExperiments').filterByTracks(targetTracks);
			
			Ext.getCmp('workspace_groupinfoeditor').loadRecord(record);
		}
		
		//switch to group detail view page
		Ext.getCmp('workspace_view').getLayout().setActiveItem('detailview');
	},
	viewExperimentDetail: function(expid) {
		//console.log(expid, typeof expid);
		//console.log('viewExperimentDetail is called');
		var detailview = Ext.getCmp('workspace_detailview');
		detailview.child('#panel_info').getLayout().setActiveItem('experimentinfo');
		detailview.child('#panel_toolbar').getLayout().setActiveItem('sample');
		detailview.child('#panel_grid').getLayout().setActiveItem('experimentdetail');
		
		Ext.getStore('ExpressionSamples').getProxy().setExtraParam("expid", expid);
		if (typeof expid == "number") {
			Ext.getStore('ExpressionSamples').getProxy().setExtraParam("expsource", "PATRIC");
		} else {
			Ext.getStore('ExpressionSamples').getProxy().setExtraParam("expsource", "User");
		}
		Ext.getStore('ExpressionSamples').load();
		
		var record = Ext.getStore('ExpressionExperiments').getById(expid);
		Ext.getCmp('workspace_experimentinfoeditor').loadRecord(record);
		Ext.getCmp('workspace_view').getLayout().setActiveItem('detailview');
	},
	switchToBrowser: function(button, event, options) {
		//button.findParentByType('groupview').showGroupBrowser();
	},
	/**
		* Toggles the editability of the group info panel form fields.
		* @param {Ext.button.Button} button The button that fired the event.
		* @param {Event} event The event that was fired.
		* @param {Object} options Options passed to the event object.
	*/		
	toggleEditorFields: function(button, event, options) {
		var target = button.findParentByType('groupinfoeditor');
		button.pressed ? target.startEdit() : target.finishEdit();
	}
});
Ext.define('VBI.Workspace.controller.Station', {
	extend: 'Ext.app.Controller',
	init: function() {
		this.control({
			'stationslist': {
				itemclick: this.onStationSelect
			}
		});
	},
	onStationSelect: function(view, record, item, index, e, options) {
		//console.log("on stations select: fired");
		// 1. get Prepared
			//var selectedType = selected[0].get("type");
			var selectedType = record.get("type");
			//console.log(selectedType);
			if (selectedType == "") { return false; }
			
			var storeMap = Ext.getStore('Mappings');
			var storeCBGrp = Ext.getStore('ColumnBrowser.Groups');
			var storeCBTag = Ext.getStore('ColumnBrowser.Tags');
			var storeFeatures = Ext.getStore('Features');
			var storeGenomes = Ext.getStore('Genomes');
			var storeGroups = Ext.getStore('Groups');
			var storeExpressionExperiments = Ext.getStore('ExpressionExperiments');
			
		// 2. update ColumnBrowser
			
			// 2.1 update groups
			storeCBGrp.clearFilter();
			storeCBGrp.filter("type", selectedType);
			
			// 2.2 update tags- moved after step 3
		
		// 3. update Track view
			var targetTags = new Array();
			Ext.Array.each(storeCBGrp.getRange(), function(grp) {
				targetTags.push(grp.get("tagId"));
			});
			//console.log("target tags:",targetTags);
			
			storeMap.setTrackFilter("Group", targetTags);
			storeMap.setTrackFilter("String", null);
			var targetTracks = storeMap.getFilteredTracks();
			//console.log("targetTracks:",targetTracks);
			
			if (selectedType === "Feature") {
				if (targetTags.length > 0) {
					storeFeatures.filterByTracks(targetTracks);
				}
				this.showListPanel('featureview');
			} 
			else if (selectedType === "Genome") {
				if (targetTags.length > 0) {
					storeGenomes.filterByTracks(targetTracks);
				}
				this.showListPanel('genomeview');
			} 
			else if (selectedType === "ExpressionExperiment") {
				if (targetTags.length > 0) {
					storeExpressionExperiments.filterByTracks(targetTracks);
				}
				this.showListPanel('experimentview');
			}
			
		// 3.5 update column browser tags
			var targetTags = storeMap.getTagsByTrack(targetTracks);
			storeCBTag.filterByTag(targetTags);
			
		// 3.6 update UI
			Ext.getCmp('columnbrowser_groups').getSelectionModel().deselectAll();
			Ext.getCmp('columnbrowser_tags').getSelectionModel().deselectAll();
		
		// 4. update Group view
			storeGroups.clearFilter();
			storeGroups.filter("type", selectedType);
	},
	showListPanel: function(viewType) {
		Ext.getCmp('workspace_view').getLayout().setActiveItem(Ext.getCmp('workspace_view').activeItem);
		Ext.getCmp('workspace_listview').getLayout().setActiveItem(viewType);
	}
});
Ext.Loader.setConfig({
	enabled: true
});

Ext.application({
	name: 'VBI.Workspace',
	autoCreateViewport: true,
	init: function() {
	},
	launch: function() {
		// This is fired as soon as the page is ready
		var task = new Ext.util.DelayedTask(function() {
				Ext.getCmp('workspace_station').setDefault("Features");
				Ext.getCmp('workspace_globaltoolbar').switchViewButtons();
		});
		task.delay(500);
		Ext.fly(document.body).setStyle('overflow', 'auto');
	},
	id: 'workspace',
	models: ['ColumnBrowser', 'Station', 'Feature', 'Genome', 'Group'],
	stores: ['ColumnBrowser', 'ColumnBrowser.Groups', 'ColumnBrowser.Tags', 'Stations', 'Features', 'Genomes', 'Groups', 'Mappings', 
		'ExpressionExperiments', 'ExpressionSamples'],
	controllers: ['ColumnBrowser', 'Station', 'Feature', 'Genome', 'Group', 'GlobalToolbar', 'Experiment']
});

Date.prototype.getDayOfYear = function(){
	var daysPerMonth = {
		1 : 31,	// jan
		2 : 28,	// feb
		3 : 31,	// mar
		4 : 30,	// apr
		5 : 31,	// may
		6 : 30,	// jun
		7 : 31,	// jul
		8 : 31,	// aug
		9 : 30,	// sep
		10: 31,	// oct
		11: 30,	// nov
		12: 31	// dec
	};

	// account for leap year
	if ((this.getFullYear()-1900)%4==0) 
		daysPerMonth[2] = 29;

	var doy = this.getDate();
	var i=this.getMonth()-1;
	while (i>0) {
		doy+=daysPerMonth[i];
		i--;
	}
	return doy;
};
Ext.Date.formatFunctions['x-date-relative'] = formatRelativeDate;

/**
	* This is our custom friendly Date formatter. It accepts an Ext.Date and 
	* formats it according to temporal distance from now:
	* 
	* <ul>
	* <li>A few seconds ago</li>
	* <li>A few minutes ago</li>
	* <li>About an hour ago</li>
	* <li>Today at [time]</li>
	* <li>Yesterday at [time]</li>
	* <li>Last [Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday]</li>
	* <li>About [1-6] weeks ago</li>
	* <li>[date]</li>
	* </ul>
	*
	* @param {Ext.Date} dateExt The date to format.
	* @return {String} The formatted date string.
	*
	* Due to a bug in ExtJS 4.0.2a, this function is not passed any arguments. so 
	* we just use 'this' for the Date to format.
	*
	* Due to the dysfunctional state of the Ext.Date object in ExtJDS 4.0.2a, 
	* we mostly use javascript Date functions instead of Ext.Date functions.
	*
*/
function formatRelativeDate(dateExt) {
	var dateExt = this;	// handle the no-argument bug
	var ms = Ext.Date.getElapsed(dateExt);
	var fullDate = Ext.Date.format(dateExt, 'M j, Y');	// Jan 1, 2001
	var fullTime = Ext.Date.format(dateExt, 'g:i a');		// 3:01 pm

	// handle the easy cases by ms comparisons
	// less than a minute
	if (ms<6e4) 
		return 'A few seconds ago';
	
	// less than 50 minutes ago
	if (ms<3e6) 
		return 'A few minutes ago';
	
	// less than 70 minutes ago
	if (ms<4.2e6) 
		return 'About an hour ago';
	

	// if not within the past hour, need some more sophistication
	// NOTE: calculations below all use javascript Date objects!

	// convert incoming Ext.Date to javascipt Date
	var dateStr = Ext.Date.format(dateExt, "d F, Y H:i:s");
	var date = new Date(Ext.Date.format(dateExt, "d F, Y H:i:s"));
	//var nt70 = new Date("01 January, 1970 00:00:00");
	//var msSince = Ext.Date.getElapsed(nt70, dateExt);
	//var date = new Date(msSince);

	// find out the now
	//var nowExt = Ext.Date.now();
	var now = new Date();
	var daysAgo = now.getDayOfYear()-date.getDayOfYear();
	var weeksAgo = Math.floor(daysAgo/7);
	var partialDaysAgo = (now.getDayOfYear()-date.getDayOfYear())%7;
	
	// more than a year ago
	if (now.getFullYear()-date.getFullYear() > 0) 
		return fullDate;
		
	// today (same year, same month, same day)
	if (now.getMonth()==date.getMonth() && now.getDate()==date.getDate()) 
		return 'Today at ' + fullTime;
	
	// yesterday (same year, same month, previous day)
	if (now.getMonth()==date.getMonth() && now.getDate()-date.getDate()==1) 
		return 'Yesterday at ' + fullTime;
	
	// within the past week
	if (weeksAgo==0 || (weeksAgo==1 && partialDaysAgo==0)) 
		return 'Last ' + Ext.Date.format(dateExt, 'l');
	
	// within the past ten days
	if (daysAgo<11) 
		return daysAgo + ' days ago';


	// 1-6 weeks ago
	// exact weeks
	if (weeksAgo<7 && partialDaysAgo==0) 
		return weeksAgo + ' weeks ago';
	
	// partial weeks
	if (weeksAgo<7 && partialDaysAgo<4) 
		return 'About ' + weeksAgo + ' weeks ago';
	if (weeksAgo<7 && partialDaysAgo>3) 
		return 'About ' + (weeksAgo+1) + ' weeks ago';

	return fullDate;
};

function BasicRenderer(value, metadata, record, rowIndex, colIndex, store){
	metadata.tdAttr = 'data-qtip="'+value+'" data-qclass="x-tip"';
	return value;
}

function launchExperimentDetail(expid) {
	Ext.getCmp("workspace_detailview").fireEvent('viewExpDetail', expid);
}