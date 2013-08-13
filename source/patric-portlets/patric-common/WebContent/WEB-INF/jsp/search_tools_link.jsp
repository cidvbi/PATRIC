<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%
String param = "";
String cType = request.getParameter("context_type");
String cId = request.getParameter("context_id");
if (cType!=null && cType.equals("taxon")) {
	param = "?cType=taxon&amp;cId="+cId+"&amp;dm=";
} else if (cType!=null && cType.equals("genome")) {
	param = "?cType=genome&amp;cId="+cId+"&amp;dm=";
}

%>
<ul class="no-underline-links no-decoration">
	<li><a href="GenomeFinder<%=param%>" class="tools-icon icon-genomefinder" 
			title="Genome Finder allows you to locate specific genome(s) based on taxonomy, keyword, replicon size, sequence status, and/or sequence type.">
		Genome Finder</a>
	</li>
	
	<li><a href="GenomicFeature<%=param%>" class="tools-icon icon-featurefinder" 
			title="Feature Finder allows you to locate specific features(s) based on taxonomy, feature type, keyword, sequence status, and/or annotation type.">
		Feature Finder</a>
	</li>

	<li><a href="PathwayFinder<%=param%>" class="tools-icon icon-comparativepathway" 
			title="Comparative Pathway Tool allows you to identify a set of pathways based on taxonomy, EC number, pathway name and/or annotation type.">
		Comparative Pathway Tool</a>
	</li>

	<li><a href="FIGfamSorter<%=param%>" class="tools-icon icon-proteinfamily" 
			title="Protein Family Sorter allows you to identify and filter sets of protein families associated with specified Phylum, Classes, Orders, Families, Genus, Species or Genomes.">
		Protein Family Sorter</a>
	</li>
		
	<li><a href="GOSearch<%=param%>" class="tools-icon icon-gosearch" 
			title="The Gene Ontology (GO) project, part of a major bioinformatics initiative, provides an ontology of defined terms representing gene product properties. The GO Search tool enables researchers to locate proteins annotated with specified GO criteria.">
		GO Search</a>
	</li>
	
	<li><a href="ECSearch<%=param%>" class="tools-icon icon-ecsearch" 
			title="The Enzyme Commission (EC) number is a numerical classification scheme for enzymes based on the chemical reactions they catalyze. The EC Search tool enables researchers to locate proteins annotated with specified EC criteria.">
		EC Search</a>
	</li>
</ul>