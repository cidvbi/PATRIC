$(document).ready(function () {
	
		
	$('.figure-left, .figure-right').imageWidth();
	$('footer .container, .window.two-col .wrapper').sameHeight();
	
	// removed jquery tabs at Harry's request. Uncomment below to use jquery tabs:
	// $('.tabs').tabs({
	// 	show: function(event, ui) { 
	// 		$('.tab-headers li').removeClass('sel');
	// 		$('.ui-state-active').addClass('sel'); 
	// 		}
	// });
	
	/* init image overlays */
	// if ie, force use of non-cached images to avoid .load function issues
	if ($.browser != undefined && $.browser.msie == true) {
		$('img').each(function(){  this.src = this.src + '?random=' + (new Date()).getTime()}).load(function() {
			$('.img-overlay').imgOverlay();
		});
	} else {
		$('img').load(function(){ 
			$('.img-overlay').imgOverlay();
		});
	};
	
	subMenu();
	browseData();
	markup();
	
	
});

