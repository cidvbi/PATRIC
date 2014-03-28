<%
// process post params
String dataUrl = request.getParameter("data_url");
String fileType = request.getParameter("file_type");
String fileFormat = request.getParameter("file_format");

%>
<script type="text/javascript" src="/patric/js/vbi/AddToWorkspace.min.js"></script>
<script type="text/javascript" src="/patric/js/vbi/TranscriptomicsUpload.min.js"></script>
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
						if(!msgCt){
							msgCt = Ext.DomHelper.insertFirst("uploader_div", {id:'atc-msg-div'}, true);
						}
						
						var m = Ext.DomHelper.append(msgCt, '<div class="msg"><p> Your experiment data is added</p></div>', true).hide();
						m.slideIn('l').ghost("l", {delay: 2000, remove: true});	
						
						updateCartInfo();
					},
					"params":{
						"metaData":{"data_type":"Transcriptomics"}
					}
				}).show();
				
				//pre populate url
				if ("<%=dataUrl%>" != "null") {
					uploader.child("#steps").child("#step01").getForm().findField("remoteData_1").setValue("<%=dataUrl%>");
				}
				if ("<%=fileType%>" != "null") {
					uploader.child("#steps").child("#step01").getForm().findField("file0_type").setValue("<%=fileType%>");
				}
				if ("<%=fileFormat%>" != "null") {
					uploader.child("#steps").child("#step01").getForm().findField("file0_format").setValue("<%=fileFormat%>");
				}
			}
			else {
				getLoginUpPopupWindow('Upload Transcriptomics Data to Workspace','Upload Transcriptomics Data<br/> to Workspace','Register @ PATRIC To Upload Your Transcriptomics Data');
				PopupModalLoading = false;
			}
		},
		failure: function(response) {
			console.log("FAIL:", response);
		}
	});
}

Ext.onReady(function () {
	launchTranscriptomicsUploader();
});

</script>
<div id="atc-msg-div"></div>