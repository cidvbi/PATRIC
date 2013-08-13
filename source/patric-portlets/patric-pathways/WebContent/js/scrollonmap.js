function ScrollOnMap(data, click_count){
		
	var flag = false,
		d = Ext.getCmp('kegg-map-panel').body,
		width = Ext.getCmp('kegg-map-panel').getSize().width,
		height = Ext.getCmp('kegg-map-panel').getSize().height,
		top = 0,
		left = 0,
		scroll = d.getScroll(),
		x = 0,
		y = 0,
		pns = pNS.get(data),
		coordinates = pns.coordinates,
		j;
	
	for(j=0; j<coordinates.length; j++){
		if(click_count == j){
			flag = true,
			x = coordinates[j].x, 
			y = coordinates[j].y,
			pns.clicked = true;
			break;
		}
	}
	
	if(!flag){
		ScrollOnMap(data, 0);
	}else{
		top = scroll.top, left = scroll.left;
		if(x-100<left && x-100>0)
			d.scroll('r',  left-x+200);
		else if(x+100>left+width)
			d.scroll('l', x+200-width+left);		
		if(y+100<top && y-100>0)
			d.scroll('t', top-y+200);					
		else if(y+100>top+height)
			d.scroll('b', y+200-height+top);	
		
		pNS.jg.painter.clear(),
		pNS.boxes.paint(),
		pns.clicked = false;
	}
	return click_count;
}