<script type="text/javascript" src="/patric/js/workflowRSS.min.js" ></script>

<section class='workflow-title sans-alternate center-text'>
	<div class='container'>
		<section class='img-overlay img-height'>
			<img class='wf-title-image' src='' />
			<div>
				<p class='wf-summary large'></p>
				<h1 class='wf-headline'></h1>
			</div>
		</section>
	</div>
</section>

<div class='container main-container'>
	<section class='main' role='main'>
		<div class='workflow sans-alternate wf-steps'></div>
	</section>
</div>

<div class='bottom bottom-alternate sans-alternate'>
	<div class='container'>
		<div class='column-half'>
			<h2 class='far light wf-dig-deeper-headline'></h2>
			<article class='tools'>
			<div class='column'>
				<ul class='no-decoration'></ul>
			</div>
			<div class='column'>
				<ul class='no-decoration'></ul>
			</div>
			</article>
		</div>
		<div class='column-half last'>
			<div class='deep-indent'>
				<h2 class='far light wf-bottom-right-headline'></h2>
				<p class='wf-bottom-right'></p>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
Ext.onReady(function () {
	Ext.getBody().removeCls('home');
	Ext.getBody().addCls('workflow light-bg');
<%	
	String paramPage = request.getParameter("page");
	if (paramPage != null) {
		%>workflowRSS('/enews/php/workflow.php?slug=<%=paramPage%>');<%
	}
%>
});
</script>