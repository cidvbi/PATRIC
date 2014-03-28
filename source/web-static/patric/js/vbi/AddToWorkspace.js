var popup, signup_popup;

function addSelectedItems(workspace_type){
	// check whether user is logged in
	Ext.Ajax.request({
		method:'GET',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=LoginStatus&action=getLoginStatus',
		success: function(response, opts) {
			if(response.responseText == "false") {
				// if user is not logged in
				Ext.Ajax.request({
					method:'GET',
					url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=PopupShowedStatus&action=getPopupStatus',
					success: function(response, opts) {
						if (response.responseText == "false") {
							if (!signup_popup) {
								signup_popup = getSignUpPopupWindow(workspace_type);
								signup_popup.show();
							}
						} else {
							DecideToShowPopup(workspace_type);
						}
					}
				});
			} else {
				// user logged in
				setPopupStatus(workspace_type);
			}
		}
	});
}

function DecideToShowPopup(workspace_type){
	if (!popup) {
		popup = getCartWindow(workspace_type).show();
	} else {
		Ext.getCmp("ATGform").resetValues();
		store.load();
		//popup.workspace_type = workspace_type;
		popup.show();
	}
	
	// hide "Note to User" when user is logged in
	Ext.Ajax.request({
		method:'GET',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=LoginStatus&action=getLoginStatus',
		success: function(response, opts) {
			if(response.responseText == "true"){
				//ATGform.remove(note_to_user);
				Ext.getCmp("ATGform").child('#noteToUser').setVisible(false);
			}
		}
	});
	
}

function saveToGroup(fid, type) {
	var form = Ext.getCmp("ATGform");
	
	popup.hide();
	
	Ext.Ajax.request({
		method:'POST',
		params:{
			group_name: form.child('#groupName').getValue(),
			group_desc: form.child('#groupDesc').getValue(),
			group_type: type,
			tracks: fid,
			tags: form.child('#groupTag').getValue(),
			group_element: form.child("#groupElement").getValue()
		},
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=groupAction&action=create',
		success: function(response, opts) {
			updateCartInfo();
		}
	});
	return true;
}

Ext.define('WorkspaceGroups', {
	extend: 'Ext.data.Store',
	fields: ['name', 'description', 'tags'],
	proxy: {
		type: 'ajax',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=getGroupList',
		startParam: undefined,
		limitParam: undefined,
		pageParam: undefined,
		nocache: false,
		reader: {
			type: 'json'
		}
	},
	listeners: {
		beforeload: function(me, operation, eOpts) {
			me.proxy.extraParams.group_type = me.group_type;
		},
		load: function(me, records, successful, eOpts) {
			if (successful) {
				me.insert(0, {"name":"Create New Group"});
				me.insert(0, {"name":"None"});
			}
		}
	}, 
	autoLoad: true
});

Ext.define('AddToWorkspace', {
	extend: 'Ext.form.Panel',
	alias: 'widget.addtoworkspace',
	border: false,
	bodyPadding: 10,
	fieldDefaults:{
		labelWidth: 80,
		anchor: '100%'
	},
	items: [{
		xtype: 'radiogroup',
		itemId: 'groupElement',
		columns: 2,
		vertical: false,
		hidden: true,
		items: [{
			boxLabel: 'Save as Feature Group', name: 'groupElement', inputValue: 'Feature', checked: true,
			listeners: {
				focus: {
					fn: function(me, e, eopts) {
						// read existing feature groups
						var form = me.up("form");
						var atg = form.child("#atg");
						atg.getStore().group_type = "Feature";
						atg.getStore().load();
					}
				}
			}
		}, {
			boxLabel: 'Save as Genome Group', name: 'groupElement', inputValue: 'Genome',
			listeners: {
				focus: {
					fn: function(me, e, eopts) {
						// read existing genome groups
						var form = me.up("form");
						var atg = form.child("#atg");
						atg.getStore().group_type = "Genome";
						atg.getStore().load();
					}
				}
			}
		}]
	}, {
		xtype:'combobox',
		itemId: 'atg',
		fieldLabel: 'Add to group',
		store: 'WorkspaceGroups',
		editable: false,
		displayField: 'name',
		valueField: 'name',
		listeners: {
			added: function(me, option) {
				me.select("None");
			},
			change: function(me, newValue, oldValue, options) {
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
				} 
				else {
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
		emptyText: 'Description',
		disabled: true
	}, {
		xtype: 'textfield',
		itemId: 'groupTag',
		name: 'tags',
		fieldLabel: 'Tags',
		emptyText: '(comma separated)'
	}, {
		xtype: 'displayfield',
		itemId: 'noteToUser',
		value:'<br/><u>Note:</u> As Guest, your Workspace items persist until you close your browser.' +
			'To save your Workspace items, ' + 
			'<a target="_blank" href="https://www.patricbrc.org/portal/portal/patric/MyAccount/PATRICUserPortletWindow?_jsfBridgeViewId=%2Fjsf%2Findex.xhtml&amp;action=1">register</a>' + 
			' or <a href="javascript:LoginonPopupClick();">login</a>.'
	}],
	resetValues: function() {
		var me = this;
		me.child("#groupName").setValue("");
		me.child("#groupDesc").setValue("");
		me.child("#groupTag").setValue("");
		me.child('#atg').select("None");
	}
});

// cart-window
function getCartWindow(workspace_type) {
	var Page = $Page, btnGroupPopupSave = Page.getCartSaveButton();

	store =  Ext.create('WorkspaceGroups', {
		id: 'WorkspaceGroups',
		group_type: workspace_type
	});
	
	popup = Ext.create('Ext.Window', {
		layout:'fit',
		width:350,
		closeAction:'hide',
		modal: true,
		title: "Add Selected " + workspace_type + "(s) to Workspace",
		items: [{
			xtype: 'addtoworkspace',
			id: 'ATGform'
		}],
		buttons: [btnGroupPopupSave,{
			text: 'Cancel',
			handler: function(){popup.hide();}
		}],
		workspace_type: workspace_type
	});
	
	// when feature is added, check whether user want to save as genomes
	if (workspace_type == "Feature") {
		popup.child("#ATGform").child("#groupElement").setVisible(true);
	}
	
	return popup;
}

function getSignUpPopupWindow(workspace_type){
	signup_popup = Ext.create('Ext.Window', {
		closeAction:'hide',
		modal: true,
		width: 330,
		title:'WANT TO SAVE WORKSPACE ITEMS?',
		items: [{
			xtype: 'panel',
			bodyPadding: 7,
			border: false,
			items: [{
				xtype: 'displayfield',
				value: 'WANT TO SAVE <br/> WORKSPACE ITEMS?',
				fieldStyle: {
					fontSize: '16px',
					fontWeight: 'bold',
					textAlign: 'center'
				}
			}, {
				xtype: 'imagecomponent',
				src: '/patric/images/horizonal_rule_302x2.png'
			}, {
				xtype: 'displayfield',
				value: 'Login to your PATRIC Account',
				fieldStyle: {
					fontSize: '14px',
					fontWeight: 'bold'
				}
			}, {
				xtype: 'component',
				autoEl: {
					tag: 'div',
					id: 'window_login-modal',
					html: '<div id="window_login-modal-msg" style="width:255px;height:109px"><div id="window_loginIframe" class="login-content"></div></div>'
				},
				padding: '5 25px'
			}, {
				xtype: 'imagecomponent',
				src: '/patric/images/horizonal_rule_OR_302x9.png'
			}, {
				xtype: 'displayfield',
				value: 'Register @ PATRIC',
				fieldStyle: {
					fontSize: '14px',
					fontWeight: 'bold'
				}
			}, {
				xtype: 'displayfield',
				value: '<a target="_blank" '+
							'href="https://www.patricbrc.org/portal/portal/patric/MyAccount/PATRICUserPortletWindow?_jsfBridgeViewId=%2Fjsf%2Findex.xhtml&amp;action=1">Sign up</a> ' + 
					'for a PATRIC account to save custom sets of workspace genomes and more <br> ' + 
					'<a target="_blank" href="http://enews.patricbrc.org/faqs/workspace-faqs/registration-faqs/" style="float: right;padding: 0px 20px;">Learn more</a>'
			}, {
				xtype: 'imagecomponent',
				src: '/patric/images/horizonal_rule_OR_302x9.png'
			}, {
				xtype: 'displayfield',
				value: 'Work as Guest',
				fieldStyle: {
					fontSize: '14px',
					fontWeight: 'bold'
				}
			}, {
				xtype: 'displayfield',
				value: 'As Guest, your Workspace items persists until you close your browser.<br> '+
					'<input style="width:120px; height:20px; top: 1px; padding: 0px 13px; position:relative; left: 180px; cursor: pointer;" ' + 
						'onclick="javascript:setPopupStatus(\''+workspace_type+'\');return false;" value="Continue as Guest" class="login-button" />'
			}]
		}],
		listeners:{
			hide: function() {
				setPopupStatus(workspace_type);
			},
			show: function() {
				PopupModalLoading = true;
				alertModal('login-modal','login-modal-msg', window.location.href.replace("portal/portal", "portal/auth/portal"));
			}
		}
	});
	
	return signup_popup;
}

function LoginonPopupClick(){
	popup.hide();
	alertModal('login-modal','login-modal-msg', window.location.href.replace("portal/portal", "portal/auth/portal"));
}

function setPopupStatus(workspace_type){
	PopupModalLoading = false;
	isPopupModalLoaded = true;
	if(signup_popup != null) {
		signup_popup.destroy();
	}
	Ext.Ajax.request({
		method:'GET',
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=PopupShowedStatus&action=setPopupStatus',
		success: function(response, opts) {
			DecideToShowPopup(workspace_type);
		}
	});
}

function getLoginUpPopupWindow(windowlTitle, msgTitle, actionTitle){
	Ext.create('Ext.Window', {
		modal: true,
		width: 330,
		title: windowlTitle,
		items: [{
			xtype: 'panel',
			bodyPadding: 7,
			border: false,
			items: [{
				xtype: 'displayfield',
				value: msgTitle,
				fieldStyle: {
					fontSize: '16px',
					fontWeight: 'bold',
					textAlign: 'center'
				}
			}, {
				xtype: 'imagecomponent',
				src: '/patric/images/horizonal_rule_302x2.png'
			}, {
				xtype: 'displayfield',
				value: 'Login to your PATRIC Account',
				fieldStyle: {
					fontSize: '14px',
					fontWeight: 'bold'
				}
			}, {
				xtype: 'component',
				autoEl: {
					tag: 'div',
					id: 'window_login-modal',
					html: '<div id="window_login-modal-msg" style="width:255px;height:109px"><div id="window_loginIframe" class="login-content"></div></div>'
				},
				padding: '5 25px'
			}, {
				xtype: 'imagecomponent',
				src: '/patric/images/horizonal_rule_OR_302x9.png'
			}, {
				xtype: 'displayfield',
				value: actionTitle,
				fieldStyle: {
					fontSize: '14px',
					fontWeight: 'bold'
				}
			}, {
				xtype: 'displayfield',
				value: '<a target="_blank" href="https://www.patricbrc.org/portal/portal/patric/MyAccount/PATRICUserPortletWindow?_jsfBridgeViewId=%2Fjsf%2Findex.xhtml&amp;action=1">Sign up</a> ' + 
					'for a PATRIC account to save custom sets of workspace genomes and more <br> ' + 
					'<a target="_blank" href="http://enews.patricbrc.org/faqs/workspace-faqs/registration-faqs/" style="float: right;padding: 0px 20px;">Learn more</a>'
			}]
		}],
		listeners:{
			show: function(me, eOpts) {
				PopupModalLoading = true;
				alertModal('login-modal','login-modal-msg', window.location.href.replace("portal/portal", "portal/auth/portal"));
				PopupModalLoading = false;
			}
		}
	}).show();
}
