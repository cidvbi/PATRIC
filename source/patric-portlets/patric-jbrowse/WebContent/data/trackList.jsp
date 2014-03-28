<%
response.setContentType("application/json");
%>
{
	"tracks": [
		{
			"type": "SequenceTrack",
			"urlTemplate": "getSequence.jsp?accession={refseq}&chunk=",
			"key": "Reference sequence",
			"label": "DNA",
			"chunkSize": 20000,
			"maxExportSpan": 10000000
		}
		,{
			"type": "FeatureTrack",
			"urlTemplate": "Feature.json.jsp?accession={refseq}&algorithm=PATRIC&format=.json",
			"storeClass": "JBrowse/Store/SeqFeature/NCList",
			"key": "PATRIC Annotation",
			"label": "PATRICGenes",
			"style": {
				"label": "function( feature ) { return feature.get('locus_tag'); }"
			},
			"hooks": {
				"modify": "function(track, feature, div) { div.style.backgroundColor = ['#17487d','#5190d5','#c7daf1'][feature.get('phase')];}"
			},
			"tooltip": "<div style='line-height:1.7em'><b>{locus_tag}</b> | {refseq} | {gene}<br>{product}<br>{type}: {start_str} .. {end} ({strand_str})<br> <i>Click for detail information</i></div>",
			"metadata": {
				"Description": "PATRIC annotated genes"
			},
			"maxExportFeatures": 10000,
			"maxExportSpan": 10000000
		}
		, {
			"type": "FeatureTrack",
			"urlTemplate": "Feature.json.jsp?accession={refseq}&algorithm=RefSeq&format=.json",
			"key": "RefSeq Annotation",
			"label": "RefSeqGenes",
			"style": {
				"className": "feature3",
				"label": "function( feature ) { return feature.get('locus_tag'); }"
			},
			"hooks": {
				"modify": "function(track, feature, div) { div.style.backgroundColor = ['#4c5e22','#9ab957','#c4d59b'][feature.get('phase')];}"
			},
			"tooltip": "<div style='line-height:1.7em'><b>{locus_tag}</b> | {gene}<br>{product}<br>{type}: {start_str} .. {end} ({strand_str})<br> <i>Click for detail information</i></div>",
			"metadata": {
				"Description": "RefSeq annotated genes"
			},
			"maxExportFeatures": 10000,
			"maxExportSpan": 10000000
		}
		, {
			"type": "FeatureTrack",
			"urlTemplate": "Feature.json.jsp?accession={refseq}&algorithm=BRC&format=.json",
			"key": "Legacy BRC Annotation",
			"label": "LegacyGenes",
			"style": {
				"className": "feature3",
				"label": "function( feature ) { return feature.get('locus_tag'); }"
			},
			"hooks": {
				"modify": "function(track, feature, div) { div.style.backgroundColor = ['#98471c','#f39641','#f9d5b5'][feature.get('phase')];}"
			},
			"tooltip": "<div style='line-height:1.7em'><b>{locus_tag}</b><br>{product}<br>{type}: {start_str} .. {end} ({strand_str})<br> <i>Click for detail information</i></div>",
			"metadata": {
				"Description": "Legacy BRC annotated genes"
			},
			"maxExportFeatures": 10000,
			"maxExportSpan": 10000000
		}
		<%--, {
			"type": "JBrowse/View/Track/Alignments2",
			"storeClass": "JBrowse/Store/SeqFeature/BAM",
			"urlTemplate": "/rnaseq/datasets/e31f696dd5f4d830/display?to_ext=bam",
			"baiUrlTemplate": "/rnaseq/dataset/get_metadata_file?hda_id=e31f696dd5f4d830&metadata_name=bam_index",
			"key": "Alignment Sample",
			"label": "bam_test_alignment",
			"metadata": {
				"Description": "PATRIC test bam"
			}
		}, {
			"type": "JBrowse/View/Track/SNPCoverage",
			"storeClass": "JBrowse/Store/SeqFeature/BAM",
			"urlTemplate": "/rnaseq/datasets/e31f696dd5f4d830/display?to_ext=bam",
			"baiUrlTemplate": "/rnaseq/dataset/get_metadata_file?hda_id=e31f696dd5f4d830&metadata_name=bam_index",
			"key": "Coverage Sample",
			"label": "bam_test_coverage"
		}, {
			"type": "JBrowse/View/Track/Wiggle/Density",
			"storeClass": "JBrowse/Store/BigWig",
			"urlTemplate": "/jbrowse/data/MarkDups_R73-L2-P2_dedup.bw",
			"key": "BigWig Sample Density",
			"label": "bigwig_test_density",
			"scale": "log",
			"bicolor_pivot": "mean"
		}, {
			"type": "JBrowse/View/Track/Wiggle/XYPlot",
			"storeClass": "JBrowse/Store/BigWig",
			"urlTemplate": "/jbrowse/data/MarkDups_R73-L2-P2_dedup.bw",
			"key": "BigWig Sample XY",
			"label": "bigwig_test_xyplot",
			"variance_band": true,
			"scale": "log",
			"style": {
				"height": 100
			}
		} --%>
	],
	"formatVersion": 1
}
