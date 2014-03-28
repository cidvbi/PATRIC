
/* init scrollTo for data landing pages */
var scrollToInit = function() {
	$('.scrollTo').click(function(event) {
		event.preventDefault();
		var tabId = $(this).data('tab-target');
		
		$.scrollTo(".data-tab[id='" + tabId + "']", 800, {
			onAfter: function() {
				if (tabId == 'tab1') {
					$.scrollTo(".data-tab[id='tab1']", 800, {offset:-120});
				} else {
					$.scrollTo(".data-tab[id='" + tabId + "']", 800, {offset:-50});
				}
			}
		});
	});
};

var waypointInit = function() {
	$('.data-tab').waypoint(function(direction) {
		var tabId = $(this).attr('id');
		$('.scrollTo').removeClass('active');
		$(".scrollTo[data-tab-target='" + tabId + "']").addClass('active');
		$.waypoints('refresh');
	}, {
		offset: 50
	});
};


/**
 * sticky toolbars - bar sticks to top after user has scrolled past it
 */
var stickyRelocate = function() {
	if ($('#sticky-anchor').length) {
		var window_top = $(window).scrollTop();
		var div_top = $('#sticky-anchor').offset().top;

		if (window_top > div_top)
			$('.sticky').addClass('active')
		else
			$('.sticky').removeClass('active');
	}
};

$(function() {
	$(window).scroll(stickyRelocate);
	stickyRelocate();
});

/*
 * grabs target for popular-box genome links and redirects on click
 * since the jqueryui tab target is using up the usual href
 */
 var genomeLinks = function() {
 	$('.genome-link').click(function(event) {
 		window.location = $(this).data('genome-href');
 	});
 }

//scripts
$(document).ready(function () {
	$('.tabbed').tabs();
	$('.tabbed.hover-tabs').tabs({
		event: "mouseover"
	});

	$('body').removeClass('home');
	$('body').addClass('light-bg');
	$('.workflow-title').height(71);
		
	scrollToInit();
	waypointInit();
	genomeLinks();
});
