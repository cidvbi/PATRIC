<script type="text/javascript" src="/patric/js/vbi/PublicWorkspace.min.js"></script>
<script type="text/javascript" src="/patric/js/vbi/TranscriptomicsUpload.min.js"></script>
<script type="text/javascript">
var msgCt;
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
						if(msgCt == undefined){
							msgCt = Ext.DomHelper.insertFirst("uploader_div", {id:'atc-msg-div'}, true);
						}
						
						//var m = Ext.DomHelper.append(msgCt, '<div class="msg"><p> Your experiment data is added</p></div>', true).hide();
						//m.slideIn('l').ghost("l", {delay: 2000, remove: true});
						
						updateCartInfo();
					}
				}).show();
			}
			else {
				getLoginUpPopupWindow('Upload Transcriptomics Data to Workspace','Upload Transcriptomics Data<br/> to Workspace','Register @ PATRIC To Upload Your Transcriptomics Data');
				PopupModalLoading = false;
			}
		}
	});
}
</script>
<div class="right no-underline-links" style="width:270px; font-size:11px;">
	<img alt="upload your transcriptomics data" src="/patric/images/transcriptomics_uploader_icon.png" onclick="launchTranscriptomicsUploader()"
		class="left" style="cursor: pointer; padding:0px 10px 0px 20px" />
	<div id="uploader_div">
		<a href="javascript:void(0)" onclick="launchTranscriptomicsUploader()">Upload your experiment data</a>
		<br/>to analyze using PATRIC tools and
		<br/>to compare with published datasets.
		<a href="http://enews.patricbrc.org/faqs/transcriptomics-faqs/upload-transcriptomics-data-to-workspace-faqs/" class="double-arrow-link" target="_blank">More</a>
	</div>
</div>

<p>
	[The list below provides all of the available transcriptomics experiments and associated metadata 
	at this taxonomic level. The list of experiments can be filtered by metadata or keyword. 
	To learn more about PATRIC's transciptomics data and associated  metadata, 
	see <a href="http://enews.patricbrc.org/faqs/transcriptomics-faqs/transcriptomics-experiment-and-comparison-list-faqs" target=_blank>Transcriptomics FAQs</a>.]
</p>

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