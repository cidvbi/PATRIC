<div id = "comparative_analyses_tools"></div>
<div class="clear"></div>
<div id = "specialized_searches"></div>
<div class="clear"></div>
<div id = "annotation_pipeline_tools"></div>
<div class="clear"></div>
<div id = "visual_browsers"></div>
<div class="clear"></div>
<script type="text/javascript">
//<![CDATA[
var rowitems = 3;
var ap = 0, ca = 0, ss = 0, vb = 0, data, subdata = [], item, id, pass1, pass2;;
var types = ["comparative_analyses_tools",
			"specialized_searches",
			"annotation_pipeline_tools",
			"visual_browsers"];
var labels = {};

Ext.Ajax.request({
	url : "/portal/portal/patric/Tools/ToolListWindow?action=b&cacheability=PAGE",
	method : 'POST',
	success : function(response, opts) {
		data = Ext.JSON.decode(response.responseText);
		data = data.tools;
		for(var i=0; i<types.length; i++){
			labels[types[i]] = data[types[i]+"_label"];
			subdata = data[[types[i]]];
			if(labels[types[i]]){
				Ext.getDom(types[i]).innerHTML = "<h2 class=\"section-title normal-case far\"><span class=\"wrap\">"+labels[types[i]]+"</span></h2>";
			}
			for(var j=0; j<subdata.length; j++){
				item = subdata[j],	id = "", pass1 = "", pass2 = "";
				if(types[i] == "comparative_analyses_tools"){
					pass2 = "ap";
					pass1 = ap;
					id = ap < rowitems?types[i]:"column_ap_" + ap % rowitems;
					ap++;
				}else if(types[i] == "specialized_searches"){
					pass2 = "ca";
					pass1 = ca;
					id = ca < rowitems?types[i]:"column_ca_"+ ca % rowitems;
					ca++;
				}else if(types[i] == "annotation_pipeline_tools"){
					pass2 = "ss";
					pass1 = ss;
					id = ss < rowitems?types[i]:"column_ss_"+ ss % rowitems;
					ss++;
				}else if(types[i] == "visual_browsers"){
					pass2 = "vb";
					pass1 = vb;
					id = vb < rowitems?types[i]:"column_vb_"+ vb % rowitems;
					vb++;
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
//]]>
</script>
