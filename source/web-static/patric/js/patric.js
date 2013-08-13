Ext.BLANK_IMAGE_URL = 'http://cdn.sencha.io/ext-4.1.1-gpl/resources/themes/images/default/tree/s.gif';

/* = Get Abstract w/ ajax and toggle layer 
-------------------------------------------------*/
function setPubmedAbstractLayer(target,btn,pmid) 
{
	if (!document.getElementById(target)) return;
	
	if(btn.className.indexOf("toggleButtonShow") != -1) {
		btn.className = "toggleButton toggleButtonHide";
		document.getElementById(target).style.display = 'none';
	}
	else 
	{
		btn.className = "toggleButton toggleButtonShow";
		document.getElementById(target).style.display = 'block';
		
		Ext.Ajax.request({
			url: '/patric-common/jsp/ajax_pubmed.jsp',
			method: 'GET',
			params: {rettype:'abstract',pubmedId:pmid},
			success: function(response, opts) {
				document.getElementById(target).innerHTML = response.responseText;
			}
		});
	}
};

/* = CART
-------------------------------------------------*/

function updateCartInfo() 
{
	Ext.Ajax.request({
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE&action_type=WSSupport&action=inlinestatus',
		success: function(rs, opts) {
			Ext.getDom("cart").innerHTML = rs.responseText;
		}
	});
};

Ext.onReady(updateCartInfo);

/****** ExtJS functions *******/
/* = toggle (show/hide) target layer 
-------------------------------------------------*/
function toggleLayer(layerID) 
{
	layer = Ext.get(layerID);
	try {
		if (layer.isDisplayed()) {
			layer.setDisplayed(false);
		} else {
			layer.setDisplayed(true);
		}
	} catch (e) {
		alert(e.message);
	}
};

function toggleBreadcrumb() 
{
	var btn = Ext.get("breadcrumb_btn");
	var list = Ext.query("li.full", Ext.getDom("breadcrumbs"));
	
	if (btn.dom.className.match("toggleButtonShow")) {
		//collapse
		Ext.Array.each(list, function(name, index) {
			Ext.get(name).setDisplayed(false);
		});
		btn.removeCls("toggleButtonShow");
		btn.addCls("toggleButtonHide");
	} else {
		//expand
		Ext.Array.each(list, function(name, index) {
			Ext.get(name).setDisplayed(true);
		});
		btn.addCls("toggleButtonShow");
		btn.removeCls("toggleButtonHide");
	}
};

/** global search **/
function search_all_header () 
{
	if (Ext.String.trim(Ext.getDom("global_search_keyword").value) == "" || Ext.getDom("global_search_keyword").value == "*") {
		Ext.Msg.alert('Warning', 'Please enter keyword.');
	} else {
		Ext.Ajax.request({
			url:'/portal/portal/patric/GenomicFeature/GenomicFeatureWindow?action=b&cacheability=PAGE',
			method: 'POST',
			params: {
				sraction: "save_params",
				keyword: EncodeKeyword(Ext.getDom("global_search_keyword").value)
			},
			success: function(rs) {         
				// document.location.href="GlobalSearch?cType=taxon&cId=&dm=&pk="+rs.responseText+"#key="+rs.responseText+"&pS=10&aP=1&cat=summary"
				document.location.href="GlobalSearch?cType=taxon&cId=&dm=&pk="+rs.responseText;
			}

		});
	}
};

function EncodeKeyword(data) {
        return data.replace(/\"/g, "%22").replace(/\//g, "%2F").replace(/'/g, "%27").trim().replace(/\:/g, "\\:").replace(/\(/g, "\\(").replace(/\)/g, "\\)").replace(/\[/g, "\\[").replace(/\]/g, "\\]").replace(/\{/g, "\\{").replace(/\}/g, "\\}");
};

function DecodeKeyword(data) {
        return data.replace(/%22/g, "\"").replace(/%2F/g, "/").replace(/%27/g, "'").trim().replace(/\\:/g, ":").replace(/\\\(/g, "(").replace(/\\\)/g, ")").replace(/\\\[/g, "[").replace(/\\\]/g, "]").replace(/\\\{/g, "{").replace(/\\\}/g, "}");
};

/** end of global search **/

Ext.override(Ext.selection.CellModel, {
	onSelectChange: function(record, isSelected, suppressEvent, commitFn) {
		var me = this,
			pos,
			eventName,
			view;
		
		if (isSelected) {
			pos = me.nextSelection;
			eventName = 'select';
		} else {
			pos = me.lastSelection || me.noSelection;
			eventName = 'deselect';
		}

		// CellModel may be shared between two sides of a Lockable.
		// The position must include a reference to the view in which the selection is current.
		// Ensure we use the view specifiied by the position.
		view = pos.view || me.primaryView;

		if ((suppressEvent || me.fireEvent('before' + eventName, me, record, pos.row, pos.column)) !== false &&
			commitFn() !== false) {
			
			if (isSelected) {
				view.onCellSelect(pos);
				//view.onCellFocus(pos);
			} else {
				view.onCellDeselect(pos);
				delete me.selection;
			}
		
			if (!suppressEvent) {
				me.fireEvent(eventName, me, record, pos.row, pos.column);
			}
		}
	}
});