
/* init scrollTo for data landing pages */
var scrollToInit = function() {
	$('.scrollTo').click(function(event) {
		event.preventDefault();
		$.scrollTo(".data-tab[id='" + $(this).data('tab-target') + "']", 800, {
			// need to conditionally set offset here
			// because of sticky bar
			offset: -50
		});

		// $('.scrollTo').removeClass('active');
		// $(this).addClass('active');
	});
};

var waypointInit = function() {
	$('.data-tab').waypoint(function() {
		// alert($(this).attr('id'));
		$('.scrollTo').removeClass('active');
		$(".scrollTo[data-tab-target='" + $(this).attr('id') + "']").addClass('active');
	}, {
		offset: 'bottom-in-view'
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
