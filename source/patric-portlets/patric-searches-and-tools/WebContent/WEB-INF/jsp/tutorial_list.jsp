<div id = "video_tutorials"></div>
<div class="clear"></div>
<div id = "workflow_tutorials"></div>
<div class="clear"></div>
<script type="text/javascript">
var rowitems = 3;
var vt = 0, wt = 0, data, subdata = [], item, id, pass1, pass2;;
var types = ["video_tutorials",
             "workflow_tutorials"];
             
var labels = {};
             
Ext.Ajax.request({
	url : "/portal/portal/patric/Tutorials/TutorialListWindow?action=b&cacheability=PAGE",
	method : 'POST',
	success : function(response, opts) {
		data = Ext.JSON.decode(response.responseText);
		data = data.tutorials;
		for(var i=0; i<types.length; i++){
			labels[types[i]] = data[types[i]+"_label"];
			subdata = data[[types[i]]];
			if(labels[types[i]]){
				Ext.getDom(types[i]).innerHTML = "<h2 class=\"section-title normal-case far\"><span class=\"wrap\">"+labels[types[i]]+"</span></h2>";
			}
			for(var j=0; j<subdata.length; j++){
				item = subdata[j],	id = "", pass1 = "", pass2 = "";
				if(types[i] == "video_tutorials"){
					pass2 = "vt";
					pass1 = vt;
					id = vt < rowitems?types[i]:"column_vt_" + vt % rowitems;
					vt++;
				}else if(types[i] == "workflow_tutorials"){
					pass2 = "wt";
					pass1 = wt;
					id = wt < rowitems?types[i]:"column_wt_"+ wt % rowitems;
					wt++;
				}
				if(id){
					Ext.getDom(id).innerHTML += toolsTemplate(item.post_title, item.post_content, item.tool_image_thumbnail, item.url, pass1, pass2);
				}
			}
		}
	}
});

function toolsTemplate(title, content, image, url, i, type) {
	var div = "", style_r = "margin-right: 17px; width: 311px", style_l = "width: 311px";
	
	if(i < rowitems){
		style =(i == (rowitems-1))?style_l:style_r;
		div += "<div class=\"left\" style=\""+style+"\">";
		div += "<ul id = \"column_"+type+"_"+i+"\" class=\"no-decoration no-underline-links\">";
	}
	div += "<li class=\"clear\"><figure class=\"figure-right\"><a href='" + url + "'><img src='" + image + "' alt='" + title + "' /></a></figure><h4 class=\"bold\"><a href='" + url + "'>" + title + "</a></h4><p>" + content + "</p></li>";
	if(i < rowitems)
		div += "</ul></div>";
	
	return div;
}              
</script>
