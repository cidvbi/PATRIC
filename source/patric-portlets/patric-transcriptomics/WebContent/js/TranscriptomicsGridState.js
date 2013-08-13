function TranscriptomicsGridState(master, stateObject, instance) {

	this.master = master, this.pageAt = 1;
	this.pageSize = Ext.state.Manager.get('pagesize') && Ext.state.Manager.get('pagesize').value ? Ext.state.Manager.get('pagesize').value : 20;
	this.sortField = master.sortField;
	this.sortDir = master.sortDir;
	this.steps = 0, this.key = '0';
	if (stateObject != null) {
		this.stateId = "gs_" + instance;
		this.serveURL = stateObject.serveURL;
		this.sampleFilter = stateObject.sampleFilter;
		this.regex = stateObject.regex;
		this.regexGN = stateObject.regexGN;
		this.upFold = stateObject.upFold;
		this.downFold = stateObject.downFold;
		this.upZscore = stateObject.upZscore;
		this.downZscore = stateObject.downZscore;
		this.significantGenes = stateObject.significantGenes;
		this.ClusterRowOrder = stateObject.ClusterRowOrder;
		this.ClusterColumnOrder = stateObject.ClusterColumnOrder;
		this.heatmapState = stateObject.heatmapState;
		this.heatmapAxis = stateObject.heatmapAxis;
		this.colorScheme = stateObject.colorScheme;
		this.filterOffset = stateObject.filterOffset;
	} else {
		this.stateId = null;
		this.serveURL = null;
		this.sampleFilter = null;
		this.regex = null;
		this.regexGN = null;
		this.upFold = null;
		this.downFold = null;
		this.upZscore = null;
		this.downZscore = null;
		this.significantGenes = null;
		this.ClusterRowOrder = [];
		this.ClusterColumnOrder = [];
		this.heatmapState = null;
		this.heatmapAxis = 'Transpose';
		this.colorScheme = 'rgb';
		this.filterOffset = 0;
	}

	this.doFiltersMatch = TranscriptomicsGridCompareFilters;
	this.storeInSession = TranscriptomicsGridStore;
	this.checkKey = TranscriptomicsGridHashCheck;
}

function TranscriptomicsGridHashCheck(random) {
	var sendId = this.stateId;
	if (this.key != random) {
		Ext.Ajax.request({
			url : this.serveURL,
			method : 'POST',
			timeout : 600000,
			params : {
				callType : "getState",
				keyType : this.stateId,
				random : random
			},
			success : function(rs) {
				catchTranscriptomicsRestore(rs, sendId, random);
			}
		});
	}
}

function catchTranscriptomicsRestore(ajaxHttp, gridStateId, random) {
	var gridState = getScratchObject(gridStateId);
	var parts = Ext.JSON.decode(ajaxHttp.responseText);

	for (var i = 0; i < parts.length; i++) {

		gridState.pageAt = parts[i].pageAt;
		gridState.sampleFilter = parts[i].sampleFilter;
		gridState.regex = parts[i].regex;
		gridState.regexGN = parts[i].regexGN;
		gridState.upFold = parts[i].upFold;
		gridState.downFold = parts[i].downFold;
		gridState.upZscore = parts[i].upZscore;
		gridState.downZscore = parts[i].downZscore;
		gridState.significantGenes = parts[i].significantGenes;
		gridState.ClusterRowOrder = parts[i].ClusterRowOrder.split(";");
		gridState.ClusterColumnOrder = parts[i].ClusterColumnOrder.split(";");
		gridState.heatmapState = parts[i].heatmapState;
		gridState.heatmapAxis = parts[i].heatmapAxis;
		gridState.colorScheme = parts[i].colorScheme;
		gridState.filterOffset = parts[i].filterOffset;
		gridState.steps = parts[i].steps - 0;
		gridState.key = random;

	}
	(gridState.master).applyBackup();
}

function TranscriptomicsGridStore() {
	if ((this.serveURL != null) && ((this.master).allowHashing)) {++this.steps;
		var passId = this.stateId;
		Ext.Ajax.request({
			url : this.serveURL,
			method : 'POST',
			timeout : 600000,
			params : {
				callType : "saveState",
				keyType : passId,
				pageAt : this.pageAt,
				sampleFilter : this.sampleFilter,
				regex : this.regex,
				regexGN : this.regexGN,
				upFold : this.upFold,
				downFold : this.downFold,
				upZscore : this.upZscore,
				downZscore : this.downZscore,
				significantGenes : this.significantGenes,
				ClusterRowOrder : this.ClusterRowOrder.join(";"),
				ClusterColumnOrder : this.ClusterColumnOrder.join(";"),
				heatmapState : this.heatmapState,
				heatmapAxis : this.heatmapAxis,
				colorScheme : this.colorScheme,
				filterOffset : this.filterOffset
			},
			success : function(rs) {
				catchTranscriptomicsStore(rs, passId);
			}
		});
	}
}

function catchTranscriptomicsStore(ajaxHttp, gridStateId) {
	var gridState = getScratchObject(gridStateId);
	gridState.key = ajaxHttp.responseText;
	setStateChange(gridStateId, gridState.key);
}

function TranscriptomicsGridCompareFilters(other) {
	return (this.sampleFilter = other.sampleFilter);
}

function updatePaging(stateId, pageAt) {
	var tracker = getScratchObject(stateId);
	tracker.gridState.pageAt = pageAt;
	tracker.gridState.storeInSession();
}

function updateFilterState(gridId, stateObject) {
	var gridObject = getScratchObject(gridId);
	var tracker = gridObject.gridState;
	tracker.pageAt = 1;
	tracker.sampleFilter = stateObject.sampleFilter;
	tracker.regex = stateObject.regex;
	tracker.regexGN = stateObject.regexGN;
	tracker.upFold = stateObject.upFold;
	tracker.downFold = stateObject.downFold;
	tracker.upZscore = stateObject.upZscore;
	tracker.downZscore = stateObject.downZscore;
	tracker.significantGenes = stateObject.significantGenes;
	tracker.ClusterRowOrder = stateObject.ClusterRowOrder;
	tracker.ClusterColumnOrder = stateObject.ClusterColumnOrder;
	tracker.heatmapState = stateObject.heatmapState;
	tracker.heatmapAxis = stateObject.heatmapAxis;
	tracker.colorScheme = stateObject.colorScheme;
	tracker.filterOffset = stateObject.filterOffset;
	tracker.storeInSession();
}

function cacheInitialTranscriptomics(gridId) {
	var gridObject = getScratchObject(gridId);
	var gridState = gridObject.gridState;
	cacheObject(gridState.stateId, gridState);
	hashStates[gridState.stateId] = gridState;
	var firstKey = findFirstTracker(gridState.stateId);
	if (firstKey == 0) {
		gridState.storeInSession();
	} else {
		gridState.checkKey(firstKey);
	}
}