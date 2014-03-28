Ext.define('Ext.grid.PATRICSelectionModel', {
	extend : 'Ext.selection.CellModel',
	allowDeselect : true,
	selection : null, 
	mouseSelectionEnable: false, 
	checkSelectionEnable: false, 
	cr2: null, 
	firstSelectedCell : [0, 0], 
	selectedCellRange : [0, 0, 0, 0],
	clearSelections : function() {
		if (this.selectedCellRange) {
			var cr = this.selectedCellRange, row1 = cr[0], col1 = cr[1], row2 = cr[2], col2 = cr[3], r, c;
			this.cr2 = this.selectedCellRange;

			for ( r = row1; r <= row2; r++) {
				for ( c = col1; c <= col2; c++) {
					this.primaryView.onCellDeselect({
						row : r,
						column : c
					});
				}
			}
		}
	},
	handleMouseDown : function(view, record, item, index, e) {
		var position;
		this.clearSelections();
		if (e.target.className.indexOf('x-grid-checkheader') >= 0) {
			this.checkSelectionEnable = true;
		} else {
			this.mouseSelectionEnable = true, position = this.getCurrentPosition(), this.firstSelectedCell = [position.row, position.column], this.selectedCellRange = [(position.row < this.firstSelectedCell[0]) ? position.row : this.firstSelectedCell[0], (position.column < this.firstSelectedCell[1]) ? position.column : this.firstSelectedCell[1], (position.row < this.firstSelectedCell[0]) ? this.firstSelectedCell[0] : position.row, (position.column < this.firstSelectedCell[1]) ? this.firstSelectedCell[1] : position.column], this.selectCellRange();
		}
	},
	handleMouseUp : function(view, record, item, index, e) {
		var temp_name = "";
		property = $Page.getPageProperties(), hash = property.hash, temp_name;

		if (this.checkSelectionEnable) {
			if (hash && hash.cat) {
				temp_name = property.name[parseInt(hash.cat)];
			} else
				temp_name = property.name ? property.name : name;

			record.set(temp_name, !record.data[temp_name]);
			adjustCheckBoxes('');
			this.checkSelectionEnable = false;
		} else {
			this.mouseSelectionEnable = false;
			this.firstSelectedCell = [0, 0];
		}

	},
	handleContextMenu : function() {

		var row1 = this.cr2[0], col1 = this.cr2[1], row2 = this.cr2[2], col2 = this.cr2[3];

		this.selectedCellRange = [(row1 < row2) ? row1 : row2, (col1 < col2) ? col1 : col2, (row1 < row2) ? row2 : row1, (col1 < col2) ? col2 : col1], this.selectCellRange(), this.cr2 = [], this.mouseSelectionEnable = false;

	},
	handleMouseOver : function(view, record, item, rowIndex, e) {

		if (this.mouseSelectionEnable) {
			var cellIndex = (e.target.nodeName == "DIV") ? e.target.parentNode.cellIndex : e.target.cellIndex;
			this.clearSelections();
			this.selectedCellRange = [(rowIndex < this.firstSelectedCell[0]) ? rowIndex : this.firstSelectedCell[0], (cellIndex < this.firstSelectedCell[1]) ? cellIndex : this.firstSelectedCell[1], (rowIndex < this.firstSelectedCell[0]) ? this.firstSelectedCell[0] : rowIndex, (cellIndex < this.firstSelectedCell[1]) ? this.firstSelectedCell[1] : cellIndex];
			this.selectCellRange();
		}
	},
	getSelectedCellRange : function() {
		return this.selectedCellRange;
	},
	selectCellRange : function() {

		var cr = this.selectedCellRange, row1 = cr[0], col1 = cr[1], row2 = cr[2], col2 = cr[3], r, c;

		for ( r = row1; r <= row2; r++) {
			for ( c = col1; c <= col2; c++) {
				this.primaryView.onCellSelect({
					row : r,
					column : c
				});
			}
		}
	}
}); 