Ext.onReady(function () {
	Ext.create('Ext.panel.Panel', {
		bodyPadding: 5,
		layout: 'hbox',
		renderTo: 'PATRICGridFilter',
		items: [
			{
				xtype: 'tbspacer',
				width: 20
			},
			{
				xtype: 'combobox',
				itemId: 'cutoffValue',
				fieldLabel: 'Correlation Cutoff',
				queryMode: 'local',
				displayField: 'name',
				labelWidth: 100,
				width: 180,
				value: 0.4,
				store: Ext.create('Ext.data.Store', {
					fields: ['name'],
					data: [{name:"1"}, {name:"0.8"}, {name:"0.6"}, {name:"0.4"}, {name:"0.2"}, {name:"0"}]
				}),		
				editable: false
			},
			{
				xtype: 'tbspacer',
				width: 20
			},
			{
				xtype: 'combobox',
				itemId: 'cutoffDir',
				fieldLabel: 'Correlation',
				queryMode: 'local',
				displayField: 'name',
				labelWidth: 65,
				width: 150,
				value: 'positive',
				store: Ext.create('Ext.data.Store', {
					fields: ['name'],
					data: [{name:"positive"}, {name:"negative"}]
				}),			
				editable: false
			},
			{
				xtype: 'tbspacer',
				width: 20
			}, /*
			{
				xtype: 'textfield',
				itemId: 'keyword',
				width: 200,
				emptyText: 'keyword'
			},
			{
				xtype: 'tbspacer',
				width: 20
			}, */
			{
				xtype: 'button',
				text: 'Filter',
				handler: function() {
					var Page = $Page,
            property = Page.getPageProperties(),
            hash = property.hash;
						hash.cutoffValue	= this.ownerCt.getComponent("cutoffValue").getValue(), 
						hash.cutoffDir	= this.ownerCt.getComponent("cutoffDir").getValue(),
						hash.cutoffValue *= (hash.cutoffDir=="positive")?1:-1,
						Page = $Page,
						property = Page.getPageProperties(),
						hash = property.hash,
						hash.sort[0] = 'correlation',
						hash.dir[0] = (cutoffDir=="positive")?'DESC':'ASC',
						createURL();
				}
			}
		]
	});
});
