function GroupGridState(master, stateObject, instance) {
	this.master = master;
	this.pageAt = 1;
	this.pageSize = Ext.state.Manager.get('pagesize') && Ext.state.Manager.get('pagesize').value ? Ext.state.Manager.get('pagesize').value : 20;
	this.sortField = master.sortField;
	this.sortDir = master.sortDir;
	this.syntonyId = "";
	this.sortDir = "ASC";
	this.steps = 0;
	this.key = '0';
	if (stateObject != null) {
		this.stateId = "gs_" + instance;
		this.serveURL = stateObject.serveURL;
		this.regex = stateObject.regex;
		this.filter = stateObject.filter?stateObject.filter:Array(stateObject.genomeIds.length+1).join(" ");
		this.perfectFamMatch = stateObject.perfectFamMatch;
		this.minnumber_of_members = stateObject.minnumber_of_members;
		this.maxnumber_of_members = stateObject.maxnumber_of_members;
		this.minnumber_of_species = stateObject.minnumber_of_species;
		this.maxnumber_of_species = stateObject.maxnumber_of_species;
		this.ClusterRowOrder = stateObject.ClusterRowOrder;
		this.ClusterColumnOrder = stateObject.ClusterColumnOrder;
		this.heatmapAxis = stateObject.heatmapAxis;
		this.colorScheme = stateObject.colorScheme;
		this.heatmapState = stateObject.heatmapState;
	} else {
		this.stateId = null;
		this.serveURL = null;
		this.regex = null;
		this.filter = null;
		this.perfectFamMatch = null;
		this.minnumber_of_members = null;
		this.maxnumber_of_members = null;
		this.minnumber_of_species = null;
		this.maxnumber_of_species = null;
		this.ClusterRowOrder = [];
		this.ClusterColumnOrder = [];
		this.heatmapAxis = '';
		this.colorScheme = 'rgb';
		this.heatmapState = null;
	}
	this.doFiltersMatch = GroupGridCompareFilters;
	this.storeInSession = GroupGridStore;
	this.checkKey = GroupGridHashCheck;
}

function GroupGridHashCheck(random) {
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
				catchGroupRestore(rs, sendId, random);
			}
		});
	}
}

function catchGroupRestore(ajaxHttp, gridStateId, random) {
	var ajaxBack = ajaxHttp.responseText;
	var gridState = getScratchObject(gridStateId);
	var oldSyntony = gridState.syntonyId;
	var parts = ajaxBack.split("\t");
	if (parts.length == 15) {
		gridState.pageAt = parts[0] - 0;
		gridState.syntonyId = parts[1];
		gridState.regex = parts[2];
		gridState.filter = parts[3];
		gridState.perfectFamMatch = parts[4];
		gridState.minnumber_of_members = parts[5];
		gridState.maxnumber_of_members = parts[6];
		gridState.minnumber_of_species = parts[7];
		gridState.maxnumber_of_species = parts[8];
		gridState.steps = parts[9] - 0;
		gridState.ClusterRowOrder = parts[10].split(";");
		gridState.ClusterColumnOrder = parts[11].split(";");
		gridState.heatmapAxis = parts[12];
		gridState.colorScheme = parts[13];
		gridState.heatmapState = parts[14];
		(gridState.master).applyBackup(oldSyntony);
		gridState.key = random;
	}

}

function GroupGridStore() {
	if ((this.serveURL != null) && ((this.master).allowHashing)) {++this.steps;
		var passId = this.stateId;
		Ext.Ajax.request({
			url : this.serveURL,
			method : 'POST',
			timeout : 600000,
			params : {
				callType : "saveState",
				keyType : this.stateId,
				pageAt : this.pageAt,
				syntonyId : this.syntonyId,
				regex : this.regex,
				filter : this.filter,
				perfectFamMatch : this.perfectFamMatch,
				minnumber_of_members : this.minnumber_of_members,
				maxnumber_of_members : this.maxnumber_of_members,
				minnumber_of_species : this.minnumber_of_species,
				maxnumber_of_species : this.maxnumber_of_species,
				steps : this.steps,
				ClusterRowOrder : (this.ClusterRowOrder == null) ? "" : this.ClusterRowOrder.join(";"),
				ClusterColumnOrder : (this.ClusterColumnOrder == null) ? "" : this.ClusterColumnOrder.join(";"),
				heatmapAxis : this.heatmapAxis,
				colorScheme : this.colorScheme,
				heatmapState : this.heatmapState
			},
			success : function(rs) {
				catchGroupStore(rs, passId);
			}
		});
	}
}

function catchGroupStore(ajaxHttp, gridStateId) {
	var gridState = getScratchObject(gridStateId);
	gridState.key = ajaxHttp.responseText;
	setStateChange(gridStateId, gridState.key);
}

function GroupGridCompareFilters(other) {
	return ((this.regex == other.regex) && (this.perfectFamMatch = other.perfectFamMatch) && (this.minnumber_of_members = other.minnumber_of_members) && (this.maxnumber_of_members = other.maxnumber_of_members) && (this.minnumber_of_species = other.minnumber_of_species) && (this.maxnumber_of_species = other.maxnumber_of_species) && (this.filter = other.filter));
}

function updatePaging(windowID, pageAt) {
	var tracker = getScratchObject(windowID);
	tracker.gridState.pageAt = pageAt;
	tracker.gridState.storeInSession();
}

function updateFilterState(gridId, stateObject) {
	var gridObject = getScratchObject(gridId);
	gridObject.validHeat = false;
	var tracker = gridObject.gridState;
	tracker.pageAt = 1;
	tracker.regex = stateObject.regex;
	tracker.filter = stateObject.filter;
	tracker.perfectFamMatch = stateObject.perfectFamMatch;
	tracker.minnumber_of_members = stateObject.minnumber_of_members;
	tracker.maxnumber_of_members = stateObject.maxnumber_of_members;
	tracker.minnumber_of_species = stateObject.minnumber_of_species;
	tracker.maxnumber_of_species = stateObject.maxnumber_of_species;
	tracker.ClusterRowOrder = stateObject.ClusterRowOrder;
	tracker.ClusterColumnOrder = stateObject.ClusterColumnOrder;
	tracker.heatmapAxis = stateObject.heatmapAxis;
	tracker.colorScheme = stateObject.colorScheme;
	tracker.heatmapState = stateObject.heatmapState;
	tracker.syntonyId = stateObject.syntonyId;
	tracker.storeInSession();
}

function cacheInitialGroup(gridId) {
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
