/* populate homepage via jsonp feed
 * requires jsonp feed via a php file on the wp server
 * REMEMBER TO CHANGE THE URL FROM DEV TO THE LIVE SITE, once ready.
 */

/* pull jsonp data from wordpress */

homeRSS();

function homeRSS() {
	//var url = 'http://patricenews-dev.vbi.vt.edu/php/homepage.php?callback=?';
	var url = '/enews/php/homepage.php?callback=?';
	$.getJSON(url,function(data){
		
		// SAMPLE
		// $('.home #data_feed_headline span').text(data.data_feed_headline);
		
		// replace headlines
		$('.home .wp-headline').each(function() {
			$(this).text(data[$(this).attr('id')]);
			if ($(this).hasClass('section-title')) {
				$(this).wrapInner('<span class="wrap">');
			}
		});
		
		// build template for tools list
		function toolsTemplate(title, content, image, url) {
			var template = 
				'<li class="clear">' + 
					'<figure class="figure-right">' +
						'<a href="' + url + '">' +
							'<img src="' + image + '" alt="' + title + '" /></a></figure>' + 
					'<h4 class="bold">' +
						'<a href="' + url + '">' + title + '</a></h4>' +
					'<p>' + content + '</p>' +
				'</li>';
			
			return template;
		};
		
		// tools list
		$.each(data.tools, function(i, item) {
			// mod into two columns
			if ((i % 2) == 0) {
				$('.home article.tools .column:first-child ul').append(toolsTemplate(this.post_title, this.post_content, this.tool_image_thumbnail, this.tool_url));
			} else {
				$('.home article.tools .column:last-child ul').append(toolsTemplate(this.post_title, this.post_content, this.tool_image_thumbnail, this.tool_url));
			}
		});

		// build template for slides
		function slideTemplate(image, slide_headline, slide_subheadline, content, button_url, button_text) {
			var template = 
			'<li class="slide">' +
				'<div class="wrapper">' + 
					'<img class="right" src="' + image + '" />' +
					'<h2 class="xxxl sans-alternate upper highlight-e">' + slide_headline + '</h2>' +
					'<h3 class="xl highlight-f upper far">' + slide_subheadline + '</h3>' +
					'<p>' + content + '</p>' +
					'<p><a class="button upper sans-alternate largest highlight avoid-arrow" href="' + button_url + '">' + button_text + '</a></p>' +
			'</div></li>';
			
			return template;
			
		};
		
		// build template for slide nav
		function slideNavTemplate(i, nav_line1, nav_line2) {
			var template =
			'<li data-slide="' + i + '">' +
				'<h3 class="close highlight-b upper sans-alternate">' + nav_line1 + '</h3>' +
				'<h4 class="upper sans-alternate">' + nav_line2 + '</h4>' +
			'</li>';
			
			return template;
		}
		
		// slides
		$.each(data.slides, function(i, item) {
			// the slide itself
			$('.home .feature ul.nicefade_container').append(slideTemplate(this.slide_image, this.post_title, this.slide_subtitle, this.post_content, this.button_url, this.button_text));
			// the cooresponding arrow
			$('.home .feature ul.arrow').append('<li data-slide="' + (i + 1) + '"></li>');
			// nav item for the slide
			$('.home .feature .feature-nav ul.nicefade_index-list').append(slideNavTemplate((i + 1), this.slide_nav_line_1, this.slide_nav_line_2));
		});
		
		//init the slideshow after data has loaded
		initNicefade();
		
	});
};
