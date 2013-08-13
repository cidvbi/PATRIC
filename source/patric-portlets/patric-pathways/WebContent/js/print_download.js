
function print_map()
{
	var newwindow = window.open('','deneme','height=768,width=1024,resizable=yes,scrollbars=yes'),
		childNodeArray = Ext.getDom('map_div').childNodes[0].childNodes,
		document = newwindow.document,
		i,
		j,
		text,
		style,
		left = "",
		top = "";
	
	newwindow.focus();

    document.write('<html><head><title>Pathway Image</title>'+
    		'<style>@media screen {.clip {position:relative; overflow: hidden;width: 700px;height: 650px;border: inset;}} '+
			'@media print {.noprint {visibility:hidden; height:0px; display:none} .printtitle {text-align: center; border:none; border-color:white; font-family:sans-serif; font-weight:bold; font-size:xx-large;}} .printbutton {align:center; margin-left: 30px;} .header {text-align: center; width: 700px; height: 50px;}</style>'+
			'</head><body>'+	
			'<div class=\"header\">'+
			'<input class=\"printtitle\" type=\"text\" size=\"40\" value=\"Enter a title for your map\" onfocus=\"this.select()\"</input>'+
			'<input class=\"printbutton noprint\" type=\"button\" value=\"Print\" onclick=\"window.print()\"></input><input class=\"printbutton noprint\" type=\"button\" value=\"Close\" onclick=\"window.close()\"></input></div>'+
			'<img src=\"/patric/images/pathways/map'+Ext.getDom("map").value+'.png\"></img>' +
			'<div id=\"map_div_print\">');
		
	for(i=0; i<childNodeArray.length; i++){
		
		text = '<div  style=\"',
		style = childNodeArray[i].style;
		
		if (style.borderLeft)
			text += 'border-left: '+ style.borderLeft + "; ";
		
		if (style.position)
			text += 'position: '+ style.position + "; ";
		
		if (style.left){
		
			left= "";
			for(j= style.left.length-3; j>=0; j--)
				left += style.left[j];
			
			left = strrev(left);
			
			if (BrowserDetect.browser == "Firefox" || BrowserDetect.browser == "Chrome" || BrowserDetect.browser == "Safari")
				left = parseInt(left) + 8;				
			else if (BrowserDetect.browser == "Explorer")
				left = parseInt(left) + 10;	

			text += 'left: '+ left + "px; ";
		}
		if (style.top)
			
			top = "";
			for(j = style.top.length-3; j>=0; j--)
				top += style.top[j];
			
			top = strrev(top);
			if (BrowserDetect.browser == "Firefox" || BrowserDetect.browser == "Chrome" || BrowserDetect.browser == "Safari")
				top = parseInt(top) + 58;	
			else if (BrowserDetect.browser == "Explorer")
				top = parseInt(top) + 65;
			
			text += 'top: '+ top + "px; ";
		
		if (style.width){			
			if (BrowserDetect.browser != "Explorer")
				text += 'width: '+ style.width + "; ";
			else if (BrowserDetect.browser == "Explorer")
				text += 'overflow: '+ style.overflow + "; ";
		}
		
		if (style.height)
			text += 'height: '+ style.height + "; ";
		if (style.fontWeight)
			text += 'font-weight: '+ style.fontWeight + "; ";
		if (style.fontFamily)
			text += 'font-family: '+ style.fontFamily + "; ";
		if (style.fontSize)
			text += 'font-size: '+ style.fontSize+ "; ";
		if (style.clip)
			text += 'clip: '+ style.clip + "; ";
		if (style.backgroundColor)
			text += 'background-color: '+ style.backgroundColor + "; ";
		if (style.color)
			text += 'color: '+ style.color + "; ";		
		if (childNodeArray[i].innerHTML)
			text += '\">'+childNodeArray[i].innerHTML+'</div>';
		else
			text += '\"></div>';
		
		document.write(text);
			
	}
	document.write('</div>');
	document.write('</body>');
	document.write('</html>');
	document.close();
}

function download_map()
{
	var childNodeArray = document.getElementById('map_div').childNodes[0].childNodes,	
		divs = "",
		i,
		j,
		text,
		style,
		left,
		top,
		bol,
		width,
		height,
		color = "",
		colors,
		a,
		b,
		c;
	
	
	if (BrowserDetect.browser == "Firefox" || BrowserDetect.browser == "Chrome" || BrowserDetect.browser == "Safari"){
		for(i=0; i<childNodeArray.length; i++){
			text = "", left = "", top = "",	style = childNodeArray[i].style;
			if (style.left){
				for(j= style.left.length-3; j>=0; j--)
					left += style.left[j];
				left = strrev(left);
				text += 'left:'+ left + ";";
			}			
			if (style.top){					
				for(j= style.top.length-3; j>=0; j--)
					top += style.top[j];
				top = strrev(top);
				text += 'top:'+ top + ';';
			}
			if (childNodeArray[i].innerHTML)
				text += 'innerHTML:'+childNodeArray[i].innerHTML+';';
			else if (style.clip){
				width='', height='', bol = [], color = "";
				if (BrowserDetect.browser == "Chrome" || BrowserDetect.browser == "Safari"){
					bol = style.clip.split(" ");
					for(j = 0; j < bol[1].length-2; j++)
						width += bol[1][j];
	
					for(j = 0; j < bol[2].length-2; j++)
						height += bol[2][j];
				}else if (BrowserDetect.browser == "Firefox" ){
					bol = style.clip.split(",");
					for(j = 1; j < bol[1].length-2; j++)
						width += bol[1][j];
	
					for(j = 1; j < bol[2].length-2; j++)
						height += bol[2][j];
				}
				text += 'width:'+ width + ';';
				text += 'height:'+ height + ';';
			if (style.backgroundColor)
				for(j= 4; j< style.backgroundColor.length-1; j++){
					color += style.backgroundColor[j];
				}				
				colors = color.split(","), a = colors[0], b = colors[1], c = colors[2];
				text += 'color:'+ a +"," +b+","+c;
			}
			divs += text + " AA ";				
		}
	}else if(BrowserDetect.browser == "Explorer"){
		for(i=0; i<childNodeArray.length; i++){
			text = '', style = childNodeArray[i].style;
			if (style.left){
				left= '';
				for(j= style.left.length-3; j>=0; j--)
					left += style.left[j];
				left = strrev(left);
				text += 'left:'+ left + ";";
			}
			if (style.top){
				top= '';
				for(j= style.top.length-3; j>=0; j--)
					top += style.top[j];
				top = strrev(top);
				text += 'top:'+ top + ';';
			}

			if (childNodeArray[i].innerHTML)
				text += 'innerHTML:'+childNodeArray[i].innerHTML+';';
			else {
					if(style.borderLeft){
						width = '',	bol = style.borderLeft.split(" "); 
						for(j = 0; j < bol[1].length-2; j++)
							width += bol[1][j];
						text += 'width:'+ width + ';';
					}
					if (style.height){						
						height= '';
						for(j= 0; j<childchildNodeArray[i].style.height.length-2; j++)
							height += childchildNodeArray[i].style.height[j];
						text += 'height:'+ height + ';';
					}	
					if(style.backgroundColor){
						a = HexToR(style.backgroundColor), b = HexToG(style.backgroundColor), c = HexToB(style.backgroundColor);
						text += 'color:'+ a +"," +b+","+c;
					}
			}
			divs += text + " AA ";
		}
	}
	return divs;
	
}

function strrev(str) {
	   if (!str) return '';
	   var i =0, revstr='';
	   for (i = str.length-1; i>=0; i--)
	       revstr+=str.charAt(i);
	   return revstr;
}

function HexToR(h) {return parseInt((cutHex(h)).substring(0,2),16);}
function HexToG(h) {return parseInt((cutHex(h)).substring(2,4),16);}
function HexToB(h) {return parseInt((cutHex(h)).substring(4,6),16);}
function cutHex(h) {return (h.charAt(0)=="#") ? h.substring(1,7):h;}
