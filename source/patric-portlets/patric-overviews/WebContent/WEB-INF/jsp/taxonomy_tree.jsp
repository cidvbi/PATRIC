<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="edu.vt.vbi.patric.dao.DBShared" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType" 
%><%@ page import="java.util.*" 
%><%
String ncbi_taxon_id = null; //request.getParameter("ncbi_taxon_id");

String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
if (cType!=null && cType.equals("taxon") && cId!=null) {
	ncbi_taxon_id = cId;
}

String rootClassName = "";
if (ncbi_taxon_id == null) {
	rootClassName = "Bacteria";
	ncbi_taxon_id = "2";
}
else {
	//rootClassName;
	DBShared conn_shared = new DBShared();
	ArrayList<ResultType>  parents = conn_shared.getTaxonParentTree(ncbi_taxon_id);
	if (parents.size() > 0) {
		ResultType node = parents.get(0);
		rootClassName = node.get("name");
	}
}

String txUrlBase = "/patric-common/jsp/genomeselector_support.json.jsp?mode=txtree&start=";
String txUrlAll = "/patric-common/txtree-bacteria.js";
String txUrl = "";
if (ncbi_taxon_id.equals("2")) {
	txUrl = txUrlAll;
} else {
	txUrl = txUrlBase+ncbi_taxon_id;
}
%>
<div id="bacteria-tree" class="left"></div>
<div class="callout right" style="width:250px">
	If you know the taxonomy of your genomes of interest, you can follow this taxonomy tree (based on NCBI Taxonomy)
	 and then use one of the links (eg., Sequence List) for more information at any taxonomic level.
	  Alternatively, use our <a href="GenomeFinder?cType=taxon&amp;cId=&amp;dm=">Genome Finder</a> to search for specific genome(s) by name.
</div>
<div class="clear"></div>
<script type="text/javascript">
//<![CDATA[
var store, tree;

Ext.onReady(function(){
	Ext.define('Taxon', {
		extend: 'Ext.data.Model',
		fields: [
			{name: 'name',		type: 'string'},
			{name: 'node_count',type: 'int'},
			{name: 'leaf',		type: 'boolean'},
			{name: 'id',		type: 'int'},
			{name: 'rank',		type: 'string'}
		]
	});

    store = Ext.create('Ext.data.TreeStore', {
        model: 'Taxon',
        proxy: {
            type: 'ajax',
            url:'<%=txUrl %>',
            noCache: false,
            sortParam: false
        },
        sortParam: undefined,
        listeners: {
            load: function() {
                var r = this.getRootNode();
                r.expand(false, false);
                r.firstChild.expand(false, false);
            }
        }
    });

	tree = Ext.create('Ext.tree.Panel', {
		width: 700,
		height: 600,
		renderTo: 'bacteria-tree',
		//useArrows: true,
		rootVisible: false,
		store: store,
		columns: [{
			xtype: 'treecolumn', //this is so we know which column will show the tree
			text: 'Name',
			flex: 2,
			dataIndex: 'name'
		}, {
			text: 'Rank',
			flex: 1,
			dataIndex: 'rank',
			align: 'center'
		}, {
			text: 'Genomes',
			flex: 1,
			dataIndex: 'node_count',
			align: 'center'
		}, {
			xtype: 'templatecolumn',
			text: 'Links',
			flex: 1,
			dataIndex: 'id',
			sortable: false,
			tpl: Ext.create('Ext.XTemplate', '{id:this.formatLinks}', {
				formatLinks: function(v) {
					var link = Ext.String.format('<a href="Taxon?cType=taxon&cId={0}"><img src="/patric/images/icon_taxon.gif" alt="Taxonomy Overview" title="Taxonomy Overview" /></a>', v);
					
					link += Ext.String.format(' <a href="GenomeList?cType=taxon&cId={0}&dataSource=&displayMode=&pk="><img src="/patric/images/icon_sequence_list.gif" alt="Genome List" title="Genome List" /></a>', v);
					link += Ext.String.format(' <a href="FeatureTable?cType=taxon&cId={0}&annotation=&filtertype=&featuretype="><img src="/patric/images/icon_table.gif" alt="Feature Table" title="Feature Table" /></a>', v, Math.floor(Math.random()*100001));
					link += Ext.String.format(' <a href="Literature?cType=taxon&cId={0}"><img src="/patric/images/icon_pubmed.gif" alt="PubMed" title="PubMed" /></a>', v);
					
					return link;
				}
			})
		}]
    });
});

Ext.onReady(function () {
	if (Ext.get("tabs_taxontree")!=null) {
		Ext.get("tabs_taxontree").addCls("sel");
	}
});
//]]>
</script>
