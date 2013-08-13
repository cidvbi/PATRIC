var legend = '<div style="font-size: 80%;">Legend: <br><span style="background: none repeat scroll 0% 0% rgb(0, 255, 0);"><font color="#003300">GENE or PROTEIN</font></span><span style="background: none repeat scroll 0% 0% rgb(255, 255, 0);"><font color="#800000">METABOLITE</font></span><!--   				<span style="background: #ffff00"><font color="#800000">SYSBIO</font></span> --><span style="background: none repeat scroll 0% 0% rgb(255, 204, 51);"><font color="black">BACTERIA</font></span><span style="background: none repeat scroll 0% 0% rgb(204, 204, 0);"><font color="black">ORGAN</font></span><span style="background: none repeat scroll 0% 0% rgb(204, 255, 0);"><font color="#008000">SYMPTOM or DISEASE</font></span><br><span style="background: none repeat scroll 0% 0% rgb(102, 0, 255);"><font color="#ffffff">PROCEDURE</font></span><span style="background: none repeat scroll 0% 0% rgb(255, 0, 0);"><font color="#ffffff">INDICATOR</font></span><br><span style="background: none repeat scroll 0% 0% rgb(255, 170, 255);"><font color="#990000">Acronym</font></span></div>'
var facets = ['content', 'GENE', 'PROTEIN', 'MESHHEADING', 'METABOLITE', 'DRUG', 'BACTERIA', 'SYMPTOM', 'DISEASE', 'ORGAN', 'DIAG_PROC', 'THERAPEUTIC_PROC', 'INDICATOR'];

function createTree() {

	Ext.define('VBI.treeNode', {
		extend : 'Ext.tree.tristate.Model',
		fields : [{
			name : 'text',
			type : 'string'
		}, {
			name : 'parentId',
			type : 'int'
		}, {
			name : 'id',
			type : 'string'
		}]
	});

	var pluginView = Ext.create('Ext.tree.tristate.Plugin', {});

	treeDS = Ext.create('Ext.data.TreeStore', {
		model : 'VBI.treeNode',
		autoLoad : true,
		proxy : {
			type : 'ajax',
			url : '/portal/portal/patric/KLEIO/KLEIOPortletWindow?action=b&cacheability=PAGE',
			noCache : false,
			extraParams : {
				type : "tree",
				keyword : static_keyword
			}
		}
	});

	tree = Ext.create('Ext.tree.Panel', {
		selectedTerm : {},
		hiddenNodes : {},
		autoScroll : true,
		height : 790,
		border : false,
		rootVisible : false,
		isTreeLoaded : false,
		renderTo : 'GenericSelector',
		store : treeDS,
		hideHeaders : true,
		viewConfig : {
			plugins : [pluginView]
		},
		columns : [{
			xtype : 'tristatetreecolumn',
			flex : 1,
			dataIndex : 'text'
		}],
		listeners : {
			scope : this,
			itemclick : function(view, record, item, index, e, options) {
				var node = treeDS.getNodeById(record.raw.id);
				var parentNode = node.parentNode;
				var n = new Array();
				if (node.id.indexOf("_more") != -1) {

					for (var i = 0; i < node.childNodes.length; i++) {
						n.push(node.childNodes[i]);
					}

					for (var i = 0; i < n.length; i++) {
						parentNode.appendChild(n[i]);
					}

					tree.hiddenNodes[parentNode.raw.id] = node;
					parentNode.removeChild(node);

				} else if (node.id.indexOf("_less") != -1) {

					var end = parentNode.childNodes.length;
					var start = 4;
					if (parentNode.childNodes[0].id == parentNode.id + '_clear') {
						start = start + 1;
					}

					if (parentNode.childNodes[end - 1].id == parentNode.id + '_more') {
						end = end - 1;
					}

					for (var i = start; i < end; i++) {
						n.push(parentNode.childNodes[i]);
					}

					tree.hiddenNodes[parentNode.raw.id].data.expanded = false;
					tree.hiddenNodes[parentNode.raw.id].appendChild(n);
					parentNode.appendChild(tree.hiddenNodes[parentNode.raw.id]);

				}
			},
			checkchange : function() {
				getOnlyKeywordData();

				tree.selectedTerm = {};

				var root_children = tree.getRootNode().childNodes;
				for (var i = 0; i < root_children.length; i++) {
					root_children[i].raw.checked = false;
				}

				if (tree.view.getChecked().length > 0) {

					Ext.each(tree.view.getChecked(), function(node) {
						var flag = false;
						var parentID = node.raw.parentID;

						if (parentID.indexOf("_more") != -1) {
							parentID = parentID.split("_more")[0];
						}

						treeDS.getNodeById(parentID).raw.checked = true;

						var temp = new Array();
						if (tree.selectedTerm[parentID] == null) {
							temp.push(parentID + ":" + "\"" + node.internalId.split("##")[0] + "\"");
						} else {
							temp = tree.selectedTerm[parentID];
							if (temp.join(",").indexOf(parentID + ":" + node.internalId.split("##")[0]) == -1)
								temp.push(parentID + ":" + "\"" + node.internalId.split("##")[0] + "\"");

						}

						tree.selectedTerm[parentID] = temp;

					}, this);
				} else {
					for (var i = 1; i < facets.length; i++) {
						tree.selectedTerm[facets[i]] = null;
					}
					var root_children = tree.getRootNode().childNodes;
					for (var i = 0; i < root_children.length; i++) {
						root_children[i].raw.checked = false;
					}

				}
				var tree_keyword = TreeJOIN();
				if (Ext.getDom("kleio_keyword").value != "") {
					if (tree_keyword != "")
						Ext.getDom("kleio_keyword").value += " AND " + tree_keyword;
				} else {
					Ext.getDom("kleio_keyword").value += tree_keyword;
				}
			}
		}
	});
}

function getOnlyKeywordData() {

	if (Ext.getDom("kleio_keyword").value != null && Ext.getDom("kleio_keyword").value != "") {
		var splitted = Ext.getDom("kleio_keyword").value.split(new RegExp(" AND | OR | or | and | Or | oR | And | aNd | anD  | ANd | AnD | aND"));
		Ext.getDom("kleio_keyword").value = "";
		for (var i = 0; i < splitted.length; i++) {
			if (facets.join(",").indexOf(splitted[i].split(":")[0]) == -1 && facets.join(",").indexOf(splitted[i].split(":")[0].substring(1)) == -1) {
				if (Ext.getDom("kleio_keyword").value != "")
					Ext.getDom("kleio_keyword").value += " OR ";
				Ext.getDom("kleio_keyword").value += splitted[i];
			}
		}
	}

}

function TreeJOIN() {

	var joined_keyword = "";

	var temp = new Array();

	for (var i = 0; i < facets.length; i++) {

		if (tree.selectedTerm[facets[i]] != null) {

			if (joined_keyword != "")
				joined_keyword += " AND ";

			temp = tree.selectedTerm[facets[i]];
			joined_keyword += temp.join(' OR ');

		}

	}

	return joined_keyword;

}

function searchkleio() {
	if (Ext.getDom("kleio_keyword").value == "") {
		static_keyword = Ext.getDom("kleio_name").value;
	} else {
		static_keyword = Ext.getDom("kleio_name").value + " AND " + Ext.getDom("kleio_keyword").value;
	}

	tree.selectedIDs = null;
	tree.selectedParents = null;
	createLoadTables('kleio-tab');
}

function renderArticle(value, p, record) {

	var title = record.data.title;
	var authors = record.data.authors;
	var journal_name = record.data.journal;
	var pub_date = record.data.date;
	var pmid = record.data.pmid;

	var strPub = Ext.String.format("<b>{0}</b><br/>{1}<br/>{2} ({3})", title, authors, journal_name, pub_date);
	var strPubmedLink;
	if (pmid != null) {
		strPubmedLink = Ext.String.format(', PubMed: <a href="http://view.ncbi.nlm.nih.gov/pubmed/{0}" target="_blank">{0}</a>', pmid);
	} else {

		strPubmedLink = ", PubMed: Not Available";
	}
	var strAbstract;
	if (pmid != null) {
		strAbstract = Ext.String.format('<br /><img src="/patric/images/spacer.gif" alt="" onclick="setAbstractLayer(\'kleio_Abstract_{0}\',this,\'{0}\')" class="toggleButton toggleButtonHide" /> Abstract <div id="kleio_Abstract_{0}" style="display:none;padding:15px 20px 0px 20px"></div>', pmid);
	} else {
		strAbstract = "";
	}
	return strPub + strPubmedLink + strAbstract;
}

function setAbstractLayer(target, btn, pmid) {
	if (!document.getElementById(target))
		return;

	if (btn.className.indexOf("toggleButtonShow") != -1) {
		btn.className = "toggleButton toggleButtonHide";
		document.getElementById(target).style.display = 'none';
	} else {
		btn.className = "toggleButton toggleButtonShow";
		document.getElementById(target).style.display = 'block';
		document.getElementById(target).style.height = '30px';

		Ext.get(target).mask('Loading...');

		Ext.Ajax.request({
			url : '/patric-mashup/jsp/kleio_pubmed.jsp',
			method : 'GET',
			params : {
				pubmedId : pmid
			},
			success : function(rs, opts) {

				Ext.getDom(target).innerHTML = rs.responseText + legend;

				Ext.get(target).unmask();
				document.getElementById(target).style.height = '';
			}
		});
	}
}
