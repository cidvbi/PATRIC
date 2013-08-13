<%
	String staticRoot = System.getProperty("web.static", "/patric");
	String resourceRoot = System.getProperty("web.resource", "");
%>
<script type="text/javascript" src="<%=staticRoot%>/static/bacteriacloud_data.js"></script>
<script type="text/javascript" src="/patric-common<%=resourceRoot%>/js/enews_data.js"></script>
<script type="text/javascript" src="<%=staticRoot%>/js/frontpage.min.js"></script>
<script type="text/javascript" src="<%=staticRoot%>/js/homeRSS.min.js"></script>

<section class='feature'>
	<div class='container' id='nicefade_wrapper'>
		<ul class='nicefade_container'></ul>
		<ul class='arrow'></ul>
	</div>
	<nav class='feature-nav'>
		<div class='container'>
			<ul class='no-decoration nicefade_index-list'></ul>
		</div>
	</nav>
</section>
<div class='container main-container'>
	<section class='main' role='main'>
		<section class='two-thirds-col'>
			<div class='column'>
				<article class='tools'>
					<h2 class='wp-headline section-title section-title-b' id='tools_headline'>What else can I do?</h2>
					<div class='two-col'>
						<div class='column'>
							<ul class='no-decoration no-underline-links'></ul>
						</div>
						<div class='column'>
							<ul class='no-decoration no-underline-links'></ul>
						</div>
					</div>
					<div class="right">
						<a class="double-arrow-link" href="Tools">Show all available
							tools</a>
					</div>
				</article>
			</div>
			<div class='column has-border'>
				<div class='wrapper'>
					<form>
						<h2 class='wp-headline light sans-alternate' id='form_headline'>BLAST against all Bacteria</h2>
						<div class='metal-select-container far'>
							<select id="programIndex" class='smallest half'>
								<option value="blastn">blastn</option>
								<option value="blastp">blastp</option>
							</select>
						</div>
						<textarea id="blastKeyword" class='close2x tall full no-border'
							placeholder='Enter your Sequence'></textarea>
						<br /> <input class='button upper smallest bold highlight'
							type='button' value='Blast' onclick='submitBlast();' /> <a
							href="/portal/portal/patric/Blast?dm=results&amp;pk=">Advanced
							Blast</a>
					</form>
					<hr class='img-hr-third far2x' />
					<h3 class='wp-headline light sans-alternate upper close2x'
						id='word_cloud_headline'>What are users viewing now?</h3>
					<ul id="bacteriaCloud"
						class='wordcloud inline no-underline-links no-decoration center-text'></ul>
				</div>
			</div>
		</section>
	</section>
</div>

<div class='bottom no-underline-links'>
	<div class='container tight-paragraphs'>
		<div class='column-narrow'>
			<figure class='caption-bottom ondark'>
				<img id="featureImage" src='http://placehold.it/230x165' alt="blank image for placeholder" />
				<figcaption>
					<div id="featureCaption" class='wrapper'></div>
				</figcaption>
			</figure>
			<div id="feature" class='block'></div>
		</div>
		<div class='column-wide'>
			<h2 class='close2x light'>
				eNews <a class="smallest double-arrow-link" href="http://enews.patricbrc.org/">More</a>
			</h2>
			<div id="newsCol1" class='column'>
				<div id="newsSlot1"></div>
				<div id="newsSlot2"></div>
			</div>
			<div id="newsCol2" class='column last'>
				<div id="newsSlot3" class='block'></div>
			</div>
		</div>
	</div>
</div>
