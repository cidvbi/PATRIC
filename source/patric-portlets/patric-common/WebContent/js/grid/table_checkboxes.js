function flipHeaderCheckbox(src) {

	var checkState = src.split("/"), Page = $Page, grid = Page.getGrid(), name = Page.getPageProperties().name, checkbox = (Page.exemptList.some(function(element, index, array) {
			return name == element;
		})) ? getScratchObject(grid.id).checkbox : Page.getCheckBox();

	checkState = checkState[checkState.length-1].split(".")[0] === "checked" ? "unchecked" : "checked", checkbox.setCheckAll(checkState[checkState.length-1].split(".")[0] === "unchecked" ? true : false), adjustCheckBoxes(checkState);

}

function adjustCheckBoxes(checkState) {
	var bbar = null, Page = $Page, grid = Page.getGrid(), name = Page.getPageProperties().name, checkbox = (Page.exemptList.some(function(element, index, array) {
			return name == element;
		})) ? getScratchObject(grid.id).checkbox : Page.getCheckBox(), page_size = 0, ds = null, headerSet, checkSet, i, record, property = Page.getPageProperties(), hash = property.hash, docked_items;

	if (checkState == "checked" || checkState == "") {
		if (grid.getDockedItems('toolbar').length < 2) {
			if (hash.hasOwnProperty('cat')) {
				if (hash.cat == 1 || hash.cat == 3) {
					hash.cat == 1?grid.addDocked(createToolbar("cart", "", "Genome"), 0):grid.addDocked(createToolbar("cart", "", "Experiment"), 0);
					docked_items = grid.getDockedItems()[0].items.items;
					docked_items[2].disable();
					docked_items[4].items.items[1].disable();
					docked_items[6].disable();
				} else {
					grid.addDocked(createToolbar("cart", "", "Feature"), 0);
				}
				sHeight();
				grid.columns[1].setText("<a href=\"javascript:hideToolbar('hide');\"><span style=\"float:right\">Hide Toolbar</span></a> Select all (" + updateCountAtColumnHeader() + ") displayed " + ((hash.cat == 2) ? "genome(s)" : (hash.cat == 3)?"experiment(s)":"feature(s)"));
			}
		}
		bbar = grid.getDockedItems('toolbar')[1];
	} else {
		bbar = grid.getDockedItems('toolbar')[1];
	}
	if (!bbar)
		bbar = grid.getDockedItems('toolbar')[0];

	page_size = bbar.getPageSize(), ds = grid.getStore();

	if (checkState != "") {
		checkSet = (checkState == "checked");
		for ( i = 0; i < page_size; i++) {
			record = ds.getAt(i);
			(!record) ? i = page_size : record.set(checkbox.dataIndex, checkSet);
		}
	} else {
		checkState = "checked";
		for ( i = 0; i < page_size; i++) {
			record = ds.getAt(i);
			if (!record) {
				i = page_size;
			} else if (record.get(checkbox.dataIndex) != true) {
				i = page_size, checkState = "unchecked";
			}
		}
	}

	headerSet = Ext.getDom("checkbox_headerBox");

	if (headerSet) {
		if (checkState)
			headerSet.src = getCheckBoxImage(checkState);
	}
}
