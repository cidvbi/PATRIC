function createURL(name) {

	var url = "";

	if (Ext.getDom("key") != null)
		url = url + "key=" + Ext.getDom("key").value;

	if (Ext.getDom("pS") != null)
		url = url + "&pS=" + Ext.getDom("pS").value;

	if (Ext.getDom("aP") != null)
		url = url + "&aP=" + Ext.getDom("aP").value;
	if (name == "prc_list") {
		if (Ext.getDom("sort") != null)
			url = url + "&sort=" + Ext.getDom("sort").value;

		if (Ext.getDom("dir") != null)
			url = url + "&dir=" + Ext.getDom("dir").value;
	}

	window.location.hash = url;

}