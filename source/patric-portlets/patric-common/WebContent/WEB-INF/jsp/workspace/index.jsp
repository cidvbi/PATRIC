<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric/js/vbi/TranscriptomicsUpload.min.js"></script>
<script type="text/javascript" src="/patric-transcriptomics/js/TranscriptomicsGene.js"></script>
<script type="text/javascript" src="/patric-transcriptomics/js/namespace.js"></script>
<script type="text/javascript">var popup; </script>
<script type="text/javascript">
function launchTranscriptomicsUploader() {

	Ext.Ajax.request({
		url: '/portal/portal/patric/BreadCrumb/WorkspaceWindow?action=b&cacheability=PAGE',
		params: {
			action_type: "WSSupport",
			action: "getToken"
		},
		success: function(response) {
			var res = response.responseText;
			
			if (res != undefined && res != "") {
				uploader = Ext.create('TranscriptomicsUploader.view.Viewport',{
					callback: function() {
						// customize ending behavior either to refresh workspace or add message to the launch button
						Ext.getCmp("expressionexperimenttoolbar").refreshWorkspaceViews();
					}
				}).show();
			}
			else {
				getLoginUpPopupWindow('Upload Transcriptomics Data to Workspace','Upload Transcriptomics Data<br/> to Workspace','Register @ PATRIC To Upload Your Transcriptomics Data');
			}
		},
		failure: function(response) {
		}
	});
}
</script>
<script type="text/javascript" src="/patric/js/vbi/Workspace.min.js"></script>

<div id="wksp" style="width:100%;min-height:530px"></div>
<form id="fTableForm" action="#" method="post">

	<input type="hidden" id="tablesource" name="tablesource" value="" />
	<input type="hidden" id="fileformat" name="fileformat" value="" />

	<!-- fasta download specific param -->
	<input type="hidden" id="fastaaction" name="fastaaction" value="" />
	<input type="hidden" id="fastatype" name="fastatype" value="" />
	<input type="hidden" id="fastascope" name="fastascope" value="" />
	<input type="hidden" id="fids" name="fids" value="" />

	<input type="hidden" id="idType" name="idType" value="" />
</form>