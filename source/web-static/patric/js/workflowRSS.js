/* populate workflow pages via jsonp feed
 * requires jsonp feed via a php file on the wp server
 * REMEMBER TO CHANGE THE URL FROM DEV TO THE LIVE SITE, once ready.
 * slug must coorespond to the individual workflow entry
 */


function workflowRSS(url) {
	$.ajax({
        // url: 'http://patricenews-dev.vbi.vt.edu/php/workflow.php?slug=example-workflow',
		url: url,
        dataType: 'jsonp',
        jsonp: 'callback',
        jsonpCallback: 'jsonpCallback',
        success: function(){
       //     alert("success");
        }
    });

    jsonpCallback = function(data){
	
		// title area
		$('.workflow .wf-summary').append(data.summary[0]);
        $('.workflow .wf-headline').append(data.headline[0]);
		$('.workflow .wf-title-image').attr('src', data.summary_background_image);
		
		// bottom area
		$('.workflow .wf-bottom-right-headline').append(data.bottom_right_headline[0]);
		$('.workflow .wf-bottom-right').append(data.bottom_right[0]);
		$('.workflow .wf-dig-deeper-headline').append(data.bottom_left_headline[0]);
		
		// build template for tools list
		function toolsTemplate(title, content, image, url) {
			var template = 
				'<li class="clear">' + 
					'<figure class="figure-left">' +
						'<a href="' + url + '">' +
							'<img src="' + image + '" alt="' + title + '" /></a></figure>' + 
						'<p>' + 
							'<a href="' + url + '" class="upper">' + title + '</a> ' + content + 
						'</p>' +
				'</li>';
			
			return template;
		};
		
		// tools list
		$.each(data.bottom_left, function(i, item) {
			// mod into two columns
			if ((i % 2) == 0) {
				$('.workflow article.tools .column:first-child ul').append(toolsTemplate(this.post_title, this.post_content, this.tool_image_thumbnail, this.tool_url));
			} else {
				$('.workflow article.tools .column:last-child ul').append(toolsTemplate(this.post_title, this.post_content, this.tool_image_thumbnail, this.tool_url));
			}
		});
		
		
		/* BUILD STEPS */

		// build template for carousel slides
		function slideTemplate(slide_id, title, content, image, total_slides) {
			var template =
				'<li class="slide white-bullets">' +
					'<div class="wrapper-inner">' +
						'<figure class="figure-right figure-no-border">' +
							'<img src="' + image + '" />' +
						'</figure>' +
						'<h2 class="far">Step ' + slide_id + ' of ' + total_slides + '</h2>' +
						'<p>' + content + '</p>' +
						'<nav class="carousel-nav">' +
							'<ul class="no-decoration inline prev-next">' +
								'<li class="prev"><a href="#">Previous</a></li>' +
								'<li class="next"><a href="#">Next</a></li>' +
							'</ul>' +
						'</nav>' +
					'</div>' +
				'</li>';
			return template;
		};
		
		// build template for callout links
		function calloutLinkTemplate(callout_index, title, icon, activator_link_text) {
			var template =
				'<li class="overview-open clear" data-overview="' + callout_index + '">' +
					'<a href="#" class="arrow-slate-e">' +
						'<figure class="figure-left figure-icon figure-no-border">' +
							'<img src="' + icon + '" />' +
						'</figure>' +
						'<span>' + activator_link_text + '</span>' +
					'</a>' +
				'</li>';
			return template;
		};
		
		// build template for callouts
		function calloutTemplate(callout_index, title, content) {
			var template =
				'<div class="overview-text" data-overview="' + callout_index + '">' +
					'<p>' + content + '</p>' +
				'</div>';
			return template;
		};
		
		// assemble carousel slides and return to parent slide template
		function getCarouselSlides(carousel) {
			var myCarousel = "";
			var totalSlides = 0;
						
			$.each(carousel, function(i, item) {
				$.each(this.slides, function(j, item) {
					if (this.post_type) {
						totalSlides++;
					}
				});
			});
			
			$.each(carousel, function(i, item) {
				$.each(this.slides, function(j, item) {
					if (this.post_type) {
						myCarousel += slideTemplate((j + 1), this.post_title, this.post_content, this.image, totalSlides);
					}
				});
			});
			
			return myCarousel;
		};
		
		// assemble callout links and return to parent slide template
		function getCalloutLinks(callouts) {
			var myLinks = "";
			
			// console.log(callouts);
			
			$.each(callouts, function(i, item) {
				myLinks += calloutLinkTemplate((i + 1), this.post_title, this.icon, this.activator_link_text);
			});
			
			return myLinks;
		};
		
		// assemble callouts and return to parent slide template
		function getCallouts(callouts) {
			var myCallouts = "";
			
			// console.log(callouts);
			
			$.each(callouts, function(i, item) {
				myCallouts += calloutTemplate((i + 1), this.post_title, this.post_content);
			});
			
			return myCallouts;
		};
		
		// parent step template
		function stepTemplate(step_id, title, content, display_title, ribbon_headline, image, carousel_activator_text, callouts, carousel) {
			

			// determine if this step has a carousel attached to it.
			var hasCarousel = carousel[0].post_type;

			//determine if this step has callouts attached to it
			var hasCallouts = "";
			$.each(callouts, function(i, item) {
				hasCallouts = this.post_type;
			});


			var template = 
				'<div class="step">' + 
					'<div class="ribbon upper ondark">' +
						'<div class="title large">' + ribbon_headline + '</div>' +
						'<div class="inner"></div>' +
					'</div>';
			if(hasCarousel) {
				template +=
					'<div class="carousel linkstyle-highlight-b ondark no-underline-links">' +
						'<p><a class="button close-slate" href="#">close</a></p>' +
						'<ul class="carousel-container">' +
							getCarouselSlides(carousel) +
						'</ul>' +
					'</div>'; // close carousel div
				} //endif for carousel
			template +=
					'<div class="wrapper-base">' +
						'<div class="base">' +
							'<div class="inner">' +
								'<h2 class="xl constrain-width close2x">' + 
									'<span class="highlight-c mega">' + step_id + '.</span>' +
									display_title +
								'</h2>';
			if(hasCallouts) {
				template +=
								'<ul class="overview-links no-decoration no-underline-links right">' +
								getCalloutLinks(callouts) +
								'</ul>';
							} // endif for callouts (list of launchers)
			template +=
								'<p class="deep-indent">' + content + '</p>' +
							'</div>' + // close inner
						'</div>' + 	// close base
						'<aside class="support">' +
							'<img class="overlay" src="' + image + '" />' +
							'<div class="wrapper-support">' +
								'<div class="overview ondark linkstyle-highlight-b no-underline-links">' +
									'<p><a class="button close-slate" href="#">Close</a></p>' +
									getCallouts(callouts) +
								'</div>' + // close callout container
								'<div class="see-container">';
			if(hasCarousel) {
				template +=
									'<p class="see linkstyle-highlight-c no-underline-links largest upper sans-alternate"><a href="#">' + carousel_activator_text + '</a></p>';
							} // endif for carousel launcher ("see container")
			template +=
								'</div>' + // close see container
							'</div>' + // close wrapper-support
						'</aside>' +
					'</div>' + // close wrapper base
				'</div>';
			
			return template;
		};
		
		// send step data to templates
		$.each(data.steps, function(i, item) {
			// console.log(this.callouts);
			$('.workflow.wf-steps').append(stepTemplate((i + 1), this.post_title, this.post_content, this.display_title, this.ribbon_headline, this.image, this.carousel_activator_text, this.callouts, this.carousel));
		});
		
		//init the carousel and wfInit after data has loaded
		setTimeout(function() {
			initwfCarousel();
			wfControl();
		}, 100);
    }
};

// find a way to call this from the page, so the slug can be embedded into the page
// workflowRSS('http://patricenews-dev.vbi.vt.edu/php/workflow.php?slug=example-workflow');
