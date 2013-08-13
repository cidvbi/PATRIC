Ext.onReady(function() {
	if (Ext.get("newsCol1")!=null) {
		populateNewsReader();
	}
	if (Ext.get("bacteriaCloud")!=null) {
		populateBacteriaCloud();
	}
});

function populateBacteriaCloud() {
	if(cloud == null) return;
	cloudList = Ext.get("bacteriaCloud");
	
	for(var i = 0; i < cloud.length; i++) {
		rankClass = "cloud-rank-" + cloud[i].rank;
		cloudList.insertHtml("beforeEnd","<li class=\""+rankClass+"\"><a href=\"/portal/portal/patric/Taxon?cType=taxon&cId="+cloud[i].cid+"\">"+cloud[i].scientific_name+"</a></li>\n");
	}
};

function populateNewsReader() {
	var eventTemplate = new Ext.Template([
			'<div class=\"has-icon icon-{categoryCode}\">',
				'<p class="smallest bold">{eventDate}</p>',
				'<h4 class="close"><a href="{link}">{title}</a></h4>',
				'<p class="small">{location}</p>',
				'<p class="small">{desc}</p>',
				'<hr/>',
			'</div>']);
	eventTemplate.compile();
	
	var newsTemplate = new Ext.Template([
			'<div class=\"has-icon icon-{categoryCode}\">',
				'<p class="smallest bold">{date}</p>',
				'<h4 class="close"><a href="{link}">{title}</a></h4>',
				'<p class="small">{desc}</p>',
				'<hr/>',
			'</div>']);
	newsTemplate.compile();	
	
	var stickyTemplate = new Ext.Template([
			'<div class=\"block has-icon icon-{categoryCode}\">',
				'<p class="small bold">{eventDate}</p>',
				'<h3 class="close"><a href="{link}">{title}</a></h3>',
				'<p class="small">{desc}</p>',
			'</div>']);	
	stickyTemplate.compile();
	
	numItems = rssData.entries.length;

	for (i = 0; i < numItems; i++) {
		entry = rssData.entries[i];
		if (entry.posttype != "feature") {
			var target = (i<3) ? "newsSlot" + (i+1) : "newsSlot3";
			//targetSlot = Ext.get(target).select(".colcontent");
			targetSlot = Ext.get(target);
			if (target == "newsSlot1" || target == "newsSlot2") {
				newContent = stickyTemplate.apply(entry);
			} else {
				newContent = (entry.posttype == "event") ? eventTemplate.apply(entry) : newsTemplate.apply(entry);
			}
			targetSlot.insertHtml("beforeEnd", newContent);
		}
		else 
		{
			Ext.getDom("featureImage").src = entry.image;
			Ext.getDom("featureCaption").innerHTML = entry.title + "<span>" + entry.subtitle + "</span>";
			Ext.get("feature").insertHtml("beforeEnd","<p>" + entry.desc + "</p>" + "<p class=\"block\"><a href=\""+entry.link+"\" class=\"double-arrow-link right\">More Information</a></p>");
		}
	}
	
	// add a last child class for IE.
	lastItem = Ext.select(".news:last-child", Ext.get("newsSlot3"));
	lastItem.addCls("last");
};

function submitBlast() {
	var keyword = Ext.getDom("blastKeyword").value;
	var programIndex = Ext.getDom("programIndex").selectedIndex;
	
	Ext.Ajax.request({
		url: '/portal/portal/patric/Blast/BlasterWindow?action=b&cacheability=PAGE',
		method: 'POST',
		params: {
			sequence: keyword,
			callType: 'formStore',
			programIndex: programIndex,
			dbIndex: (programIndex == '0')?'0':'2',
			sequence: keyword,
			queryFrom: "",
			queryTo: "",
			expectIndex: "3",
			matParamIndex: "3",
			alignment: "",
			lowFilter: "L",
			midFilter: "",
			geneCodeIndex: "0",
			dbCodeIndex: "0",
			oofAlignIndex: "19",
			advanced: "",
			overview: "on",
			alignViewIndex: "0",
			alignmentsIndex: "2",
			descriptionIndex: "3",
			schemaIndex: "0",
			fileName: ""
		},
		success: function(rs) {
			document.location.href="Blast?dm=submit&pk="+rs.responseText;
		}
	});
};