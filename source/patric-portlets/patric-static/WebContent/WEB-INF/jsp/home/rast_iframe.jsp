<p class="close">PATRIC includes a collaboration with the University of Chicago to provide an end-user genome annotation service using the RAST system.</p>
<iframe id="iframe_rast" src="http://rast.nmpdr.org/" height="500" seamless></iframe>

<script type="text/javascript">
Ext.onReady(function () {
	Ext.get('iframe_rast').setHeight(Math.max(500, Ext.getBody().getViewSize().height-395));
});
</script>