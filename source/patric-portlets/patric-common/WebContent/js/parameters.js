function SetPageProperties(property) {
	$Page = {
		init : (function() {
			ZeroClipboard ? ZeroClipboard.setMoviePath('/patric-common/js/ZeroClipboard.swf') : "";
		})(),
		exemptList : ["TranscriptomicsGene", "FIGfamSorter", "CompPathwayMap"],
		pageProperties : property,
		btnGroupPopupSave : property.cart ? new Ext.Button({
			text : 'Save to Workspace'
		}) : null,
		getPageProperties : function() {
			return this.pageProperties;
		},
		getHash : function() {
			return this.getPageProperties().hash;
		},
		grid : null,
		getGrid : function() {
			return this.grid;
		},
		checkbox : null,
		getCheckBox : function() {
			return this.checkbox;
		},
		store : property.items && new Array(property.items.length),
		getStore : function(id) {
			return this.store && this.store[id];
		},
		getCurrentItemId : function() {
			return this.getPageProperties().hash.aT;
		},
		getCartSaveButton : function() {
			return this.btnGroupPopupSave;
		},
		equals : function() {
			(getHashEquality()) ? this.getPageProperties().loaderFunction() : "";
		},
		doLayout : function() {
			Ext.EventManager.onWindowResize(function() {
				var Page = $Page;
				Page.doTabLayout();
			});
			this.doTabLayout();
		},
		doTabLayout : function() {
			var width, Page = $Page, grid = Page.getGrid(), widthOffset = 10;

			width = $(window).width() - widthOffset - (Ext.getCmp("treePanel")?Ext.getCmp("treePanel").getWidth():0);
			
			Ext.getCmp("tabLayout") ? Ext.getCmp("tabLayout").setWidth(width) : "";
			Ext.getCmp('tabPanel') ? Ext.getCmp('tabPanel').setWidth(width) : "";
			Ext.getCmp('centerPanel') ? Ext.getCmp('centerPanel').setWidth(width) : "";
			Ext.getCmp('southPanel') ? Ext.getCmp('southPanel').setWidth(width) : "";
			Ext.getCmp('heatmap') ? Ext.getCmp('heatmap').setWidth(width) : "";
			grid ? grid.setWidth(width) : "";

		},
		stateId : property.stateId
	};
}

function SetIntervalOrAPI() {
	
	HistoryStateController = this.HistoryStateController || (function(){
		var api = Modernizr && Modernizr.history,
			instance = null; 
		var History = function(){
			this.pushState = function(state){
				return this.getOperations().getStateString(state);
			};
			this.replaceState = function(state){};
			this.popState = function(){};
			this.loaderFunction = function(){
				var Page = $Page, 
					property = Page.getPageProperties();
				property.loaderFunction();
			};
			this.getOperations = function(){
				return {
					getStateString: function(state){
						var j, hash = "", hi;
						for (var i in state) {
							if (Object.prototype.toString.call(state[i]) === "[object Array]") {
								hi = state[i];
								for ( j = 0; j < hi.length; j++)
									hash += i + j + "=" + hi[j] + "&";
							} else
								hash += i + "=" + state[i] + "&";
						}
						return hash.substring(0, hash.length - 1);
					},
					getHashEquality: function(){
						var parts = window.location.href.split("#"), 
							hash, 
							i, 
							j, 
							Page = $Page, property = Page.getPageProperties(), 
							ch = property.current_hash?property.current_hash.replace("#", "").split("&"):null, 
							flag = false;

						if (parts[1] && ch) {
							hash = parts[1].split("&");
							for ( j = 0; j < ch.length; j++) {
								for ( i = 0; i < hash.length; i++) {
									if (ch[j].split("=")[0] == hash[i].split("=")[0])
										if (ch[j].split("=")[1] != hash[i].split("=")[1]) {
											flag = true;
											break;
										} else
											flag = false;
								}
								if (flag)
									break;
							}
						}else if(parts[1]){
							flag = true;
						}
						
						return flag || false;
					},
					copy2Defaults: function(source){
						var Page = $Page, 
							property = Page.getPageProperties(), 
							destination = property.hash;
						
						for (i in source) {
							if (destination.hasOwnProperty(i)) {
								if (Object.prototype.toString.call(destination[i]) === "[object Array]") {
									for (var j = 0; j < destination[i].length; j++) {
										destination[i][j] = source[i][j];
									}
								} else {
									destination[i] = source[i];
								}
							}
						}
					},
					getStateFromString: function(character){
						var i, 
							split, 
							tst, 
							tst2,
							Page = $Page, 
							property = Page.getPageProperties(), 
							h = property.hash,
							href = window.location.href,
							tester = href.lastIndexOf(character) > 0?href.substring(href.lastIndexOf(character) + 1, href.length):null;
						
						if (tester) {
							property.current_hash = tester;
							hash = tester.split("&");
							for ( i = 0; i < hash.length; i++) {
								split = hash[i].split("=");
								tst = split[0].substring(split[0].length - 1, split[0].length), tst2 = split[0].substring(0, split[0].length - 1);
								if ( typeof parseInt(tst) === "number" && Object.prototype.toString.call(h[tst2]) === "[object Array]") {
									h[tst2][tst] = split[1];
								} else {
									h[split[0]] = split[1].replace(/%20/g, " ");
								}
							}
						}
						return h;
					}
				};
			};
		};
		var Hash = function(instance){
			this.superclass = instance;
			this.pushState = function(property){
				window.location.hash = this.superclass.pushState(property.hash);
			};
			this.popState = function(){
				var operations = this.superclass.getOperations();
				if(operations.getHashEquality()){
					operations.copy2Defaults(operations.getStateFromString("#"));
					this.superclass.loaderFunction();
				}
			};
			this.replaceState = function(h){
				window.location.hash = this.superclass.pushState(this.superclass.getOperations().getStateFromString("#"));
			};
			this.toString = function(){
				return "[object History]";
			};
		};
		var API = function(instance){
			this.superclass = instance;
			this.pushState = function(property){
				var suburl = window.location.href.split("patric/")[1];
				var url = suburl.substring(0, (suburl.lastIndexOf('#')>0?suburl.lastIndexOf('#'):suburl.length));
				history.pushState(property.hash, "", url+"#" + this.superclass.pushState(property.hash));
				this.superclass.loaderFunction();
			};
			this.popState = function(state){
				this.superclass.getOperations().copy2Defaults(state);
				this.superclass.loaderFunction();
			};
			this.replaceState = function(state){
				history.replaceState(this.superclass.getOperations().getStateFromString("#"), "", "");
			};
			this.toString = function(){
				return "[object API]";
			};
		};
		
		function constructor(){
			instance = new History();
			instance = api?new API(instance):new Hash(instance);

			return {
				'superclass': instance.superclass,
				'popState': instance.popState,
				'pushState': instance.pushState,
				'replaceState': instance.replaceState,
				'toString': instance.toString,
			};
		}
		return constructor();
	}());
	
	if(this.HistoryStateController.toString() === '[object API]'){
		this.HistoryStateController.replaceState();
		window.onpopstate = function(event) {
			if (event && event.state) {
				this.HistoryStateController.popState(event.state);
			}
		};
	}else{
		window.onhashchange = function(){
			this.HistoryStateController.popState();
		};
	}
	
}

function createURL() {
	var Page = $Page, 
		property = Page.getPageProperties();
	
	this.HistoryStateController.pushState(property);
}

function SetLoadParameters() {
	
	if(!this.HistoryStateController){
		SetIntervalOrAPI();
	}
	this.HistoryStateController.replaceState();
}