function createToolbar(type, download_option, workspace_type) {
	var Page = $Page, height_small = 45, height = 68, property = Page.getPageProperties(), toolbar = null;

	var hide = Ext.create('Ext.container.ButtonGroup', {
		title : 'Columns',
		columns : 1,
		xtype : 'buttongroup',
		height : height,
		id : 'grid_column_toolbar_sh',
		items : [{
			scale : 'large',
			width : 125,
			iconAlign : 'left',
			text : 'Show/Hide',
			icon : '/patric/images/toolbar_hideshow.png',
			menu : []
		}]
	});

	var hide_small = Ext.create('Ext.container.ButtonGroup', {
		title : 'Columns',
		columns : 1,
		xtype : 'buttongroup',
		height : height_small,
		id : 'grid_column_toolbar_sh_min',
		items : [{
			scale : 'small',
			width : 120,
			iconAlign : 'left',
			text : 'Show/Hide',
			icon : '/patric/images/toolbar_hideshow_small.png',
			menu : []
		}]
	});

	var workspace = Ext.create('Ext.container.ButtonGroup', {
		title : 'Workspace',
		columns : 1,
		xtype : 'buttongroup',
		width : 132,
		height : height,
		id : 'workspace_toolbar',
		items : [{
			scale : 'large',
			//rowspan: 2,
			width : 125,
			iconAlign : 'left',
			//text:(workspace_type == "Feature")?'Add Feature(s)':'Add Genome(s)',
			text : 'Add ' + ((workspace_type == "ExpressionExperiment") ? 'Experiment' : workspace_type) + '(s)',
			icon : '/patric/images/toolbar_cart.png',
			handler : function() {
				if (Page.exemptList.some(function(element, index, array) {
						return property.name && property.name == element;
					}))
					checkAddClick(property.name);
				else
					callOperation('DoCart', 'No item(s) are selected. To add an item to cart, at least one item must be selected.');
			}
		}]
	});

	var workspace_small = Ext.create('Ext.container.ButtonGroup', {
		title : 'Workspace',
		columns : 1,
		xtype : 'buttongroup',
		width : 125,
		height : height_small,
		id : 'workspace_toolbar',
		items : [{
			scale : 'small',
			//rowspan: 2,
			width : 120,
			iconAlign : 'left',
			//text:(workspace_type == "Feature")?'Add Feature(s)':'Add Genome(s)',
			text : 'Add ' + ((workspace_type == "ExpressionExperiment") ? 'Experiment' : workspace_type) + '(s)',
			icon : '/patric/images/toolbar_cart_small.png',
			handler : function() {
				callOperation('DoCart', 'No item(s) are selected. To add an item to cart, at least one item must be selected.');
			}
		}]
	});

	var btn_grp_explist = Ext.create('Ext.container.ButtonGroup', {
		title : 'View',
		columns : 1,
		xtype : 'buttongroup',
		width : 125,
		height : height_small,
		items : [{
			scale : 'small',
			rowspan : 2,
			width : 120,
			iconAlign : 'left',
			text : 'Gene List',
			icon : '/patric/images/toolbar_icon_genelist.png',
			handler : function() {
				showExpDetail();
			}
		}]
	});

	var view = Ext.create('Ext.container.ButtonGroup', {
		title : 'View',
		columns : 1,
		xtype : 'buttongroup',
		width : 115,
		height : height,
		items : [{
			scale : 'small',
			iconAlign : 'left',
			text : 'FASTA DNA',
			icon : '/patric/images/toolbar_dna.png',
			handler : function() {
				if (Page.exemptList.some(function(element, index, array) {
						return property.name && property.name == element;
					}))
					submitFASTA(property.name, 'display', 'dna');
				else
					callOperation('DoFastaOperation', 'display', 'dna', 'No item(s) are selected. To view Fasta sequence, at least one item must be selected.');
			}
		}, {
			scale : 'small',
			iconAlign : 'left',
			text : 'FASTA Protein',
			icon : '/patric/images/toolbar_protein.png',
			handler : function() {
				if (Page.exemptList.some(function(element, index, array) {
						return property.name && property.name == element;
					}))
					submitFASTA(property.name, 'display', 'protein');
				else
					callOperation('DoFastaOperation', 'display', 'protein', 'No item(s) are selected. To view Fasta sequence, at least one item must be selected.');
			}
		}]
	});

	var download = Ext.create('Ext.container.ButtonGroup', {
		title : 'Download',
		columns : 1,
		xtype : 'buttongroup',
		width : 115,
		height : height,
		items : [{
			scale : 'small',
			iconAlign : 'left',
			width : 110,
			text : 'Table',
			icon : '/patric/images/toolbar_table.png',
			xtype : 'splitbutton',
			menu : (property.name.indexOf("FIGfamSorter") >= 0) ? [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "txt");
					else
						DownloadFile('txt');
				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "xls");
					else
						DownloadFile('xlsx');
				}
			}, {
				text : 'Family Details: Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					setAllDetails(property.name, "txt");
				}
			}, {
				text : 'Family Details: Excel File (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					setAllDetails(property.name, "xlsx");
				}
			}] : (property.name.indexOf("TranscriptomicsGene") >= 0) ? [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						DownloadTable(property.name, "txt");
					else
						DownloadFile('txt');
				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						DownloadTable(property.name, "xls");
					else
						DownloadFile('xlsx');
				}
			}, {
				text : 'Gene/Microarray (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					DownloadAllData(property.name);
				}
			}] : [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "txt");
					else
						DownloadFile('txt');
				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "xls");
					else
						DownloadFile('xlsx');
				}
			}]
		}, {
			scale : 'small',
			iconAlign : 'left',
			text : 'FASTA',
			width : 110,
			icon : '/patric/images/toolbar_fasta.png',
			xtype : 'splitbutton',
			menu : [{
				text : 'DNA',
				icon : '/patric/images/toolbar_dna.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitFASTA(property.name, 'download', 'dna');
					else
						callOperation('DoFastaOperation', 'download', 'dna', 'No item(s) are selected. To ddownload Fasta sequence, at least one item must be selected.');
				}
			}, {
				text : 'Protein',
				icon : '/patric/images/toolbar_protein.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitFASTA(property.name, 'download', 'protein');
					else
						callOperation('DoFastaOperation', 'download', 'protein', 'No item(s) are selected. To download Fasta sequence, at least one item must be selected.');
				}
			}, {
				text : 'DNA/Protein',
				icon : '/patric/images/toolbar_dna_protein.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitFASTA(property.name, 'download', 'both');
					else
						callOperation('DoFastaOperation', 'download', 'both', 'No item(s) are selected. To download Fasta sequence, at least one item must be selected.');
				}
			}]
		}]
	});

	var download_small = Ext.create('Ext.container.ButtonGroup', {
		title : 'Download',
		columns : 1,
		xtype : 'buttongroup',
		width : 115,
		height : height_small,
		items : [{
			scale : 'small',
			iconAlign : 'left',
			width : 110,
			text : 'Table',
			icon : '/patric/images/toolbar_table.png',
			xtype : 'splitbutton',
			menu : [{
				text : 'Text File (.txt)',
				icon : '/patric/images/toolbar_text.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "txt");
					else
						DownloadFile('txt');
				}
			}, {
				text : 'Excel file (.xlsx)',
				icon : '/patric/images/toolbar_excel.png',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						setTopOrtho(property.name, "xls");
					else
						DownloadFile('xlsx');
				}
			}]
		}]

	});

	var tools = Ext.create('Ext.container.ButtonGroup', {
		title : 'Tools',
		columns : 2,
		height : height,
		xtype : 'buttongroup',
		items : [{
			scale : 'small',
			iconAlign : 'left',
			width : 120,
			text : 'Pathway Summary',
			icon : '/patric/images/16x16-toolbar-icon-pathway.png',
			handler : function() {
				if (Page.exemptList.some(function(element, index, array) {
						return property.name && property.name == element;
					}))
					submitEnrichment(property.name);
				else
					callOperation('DoPathwayEnrichment', 'No item(s) are selected. To run pathway summary tool, at least one item must be selected.');
			}
		}, {
			scale : 'small',
			iconAlign : 'left',
			width : 140,
			text : 'Multiple Seq Alignment',
			icon : '/patric/images/16x16-toolbar-icon-msa.png',
			handler : function() {
				if (Page.exemptList.some(function(element, index, array) {
						return property.name && property.name == element;
					}))
					submitAlign(property.name);
				else
					callOperation('DoMsa', 'No item(s) are selected. To run MSA tool, at least one item must be selected.');
			}
		}, {
			scale : 'small',
			iconAlign : 'left',
			width : 120,
			text : 'MAP IDs to...',
			icon : '/patric/images/16x16-toolbar-icon-idmapping.png',
			menu : [{
				text : '<b>PATRIC Identifiers</b>',
				plain : true
			}, {
				text : 'PATRIC Locus Tag',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'PATRIC ID',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'PSEED ID',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : '<b>REFSEQ Identifiers</b>',
				plain : true
			}, {
				text : 'RefSeq Locus Tag',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'RefSeq',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'Gene ID',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'GI',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : '<b>Other Identifiers</b>',
				plain : true

			}, {
				text : 'Allergome',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'BioCyc',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'DIP',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'DisProt',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'DrugBank',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'ECO2DBASE',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EMBL',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EMBL-CDS',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EchoBASE',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EcoGene',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EnsemblGenome',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EnsemblGenome_PRO',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'EnsemblGenome_TRS',
				itemCls : 'x-menu-item-cstm',
				handler : function() {
					if (Page.exemptList.some(function(element, index, array) {
							return property.name && property.name == element;
						}))
						submitIDMapping(property.name, this.text);
					else
						callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
				}
			}, {
				text : 'More ...',
				itemCls : 'x-menu-item-cstm',
				menu : [{
					text : 'GeneTree',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'GenoList',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'GenomeReviews',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'HOGENOM',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'HSSP',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'KEGG',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'LegioList',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'Leproma',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'MEROPS',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'MINT',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'NMPDR',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'OMA',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'OrthoDB',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'PDB',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'PeroxiBase',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'PptaseDB',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'ProtClustDB',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'PseudoCAP',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'REBASE',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'Reactome',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'RefSeq_NT',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'TCDB',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'TIGR',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'TubercuList',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'UniParc',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'UniProtKB-ID',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'UniRef100',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'UniRef50',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'UniRef90',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'World-2DPAGE',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}, {
					text : 'eggNOG',
					itemCls : 'x-menu-item-cstm',
					handler : function() {
						if (Page.exemptList.some(function(element, index, array) {
								return property.name && property.name == element;
							}))
							submitIDMapping(property.name, this.text);
						else
							callOperation('DoIDMapping', this.text, 'No item(s) are selected. To run id mapping tool, at least one item must be selected.');
					}
				}]

			}]
		}]

	});

	var help = Ext.create('Ext.container.ButtonGroup', {
		title : 'Help',
		columns : 1,
		height : height,
		xtype : 'buttongroup',
		items : [{
			scale : 'large',
			rowspan : 2,
			width : 120,
			iconAlign : 'left',
			text : 'PATRIC FAQs',
			style : 'padding-left:5px; padding-right:5px;',
			icon : '/patric/images/toolbar_faq.png',
			handler : function() {
				window.open("http://enews.patricbrc.org/faqs/", "_new", "menubar=1,resizable=1,scrollbars=1, fullscreen=1, toolbar=1,titlebar=1,status=1");
			}
		}]
	});

	var help_small = Ext.create('Ext.container.ButtonGroup', {
		title : 'Help',
		columns : 1,
		height : 45,
		xtype : 'buttongroup',
		items : [{
			scale : 'small',
			rowspan : 2,
			width : 120,
			iconAlign : 'left',
			text : 'PATRIC FAQs',
			style : 'padding-left:5px; padding-right:5px;',
			icon : '/patric/images/toolbar_faq_small.png',
			handler : function() {
				window.open("http://enews.patricbrc.org/faqs/", "_new", "menubar=1,resizable=1,scrollbars=1, fullscreen=1, toolbar=1,titlebar=1,status=1");
			}
		}]
	});

	if (type == "cart") {

		toolbar = Ext.create('Ext.toolbar.Toolbar', {
			items : [workspace, '-', view, '-', download, '-', tools, '-', hide, '->', '-', help]
		});

	} else if (type == "group") {

		toolbar = Ext.create('Ext.toolbar.Toolbar', {
			items : [{
				title : 'Edit',
				columns : 2,
				xtype : 'buttongroup',
				width : 120,
				height : 80,
				items : [{
					scale : 'small',
					iconAlign : 'left',
					text : 'Remove',
					icon : '/patric/images/toolbar_workspace_remove.png',
					handler : removeItemFromGroup
				}]
			}, '-', view, '-', download, '-', tools, '->', '-', help]
		});

	} else if (type == "group_list") {

		toolbar = Ext.create('Ext.toolbar.Toolbar', {
			items : [{
				title : 'Edit',
				columns : 2,
				xtype : 'buttongroup',
				width : 120,
				height : 80,
				items : [{
					scale : 'small',
					iconAlign : 'left',
					text : 'Remove',
					icon : '/patric/images/toolbar_workspace_remove.png',
					handler : removeGroupFromCart
				}]
			}, '-', view, '-', download, '-', tools, '->', '-', help]
		});

	} else if (type == "exp_list") {

		toolbar = Ext.create('Ext.toolbar.Toolbar', {
			items : [workspace_small, '-', btn_grp_explist, '-', download_small, '-', hide_small, '->', '-', help_small]
		});

	} else {

		if (download_option == "") {

			toolbar = Ext.create('Ext.toolbar.Toolbar', {
				items : [workspace_small, '-', download_small, '-', hide_small, '->', '-', help_small]
			});

		} else if (download_option == "table_wo_workspace") {

			toolbar = Ext.create('Ext.toolbar.Toolbar', {
				items : [download_small, '-', hide_small, '->', '-', help_small]
			});
		}
	}

	return toolbar;

}
