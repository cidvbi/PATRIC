<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />

<div id="fp">
	
	<div>
		<h2>Enteropathogen Resource Integration Center (ERIC)</h2>
	</div>
	
	<div class="left" style="width:38%">
		<img src="/patric/images/eric_logo.png" alt="ERIC BRC logo" />
		<p>You may have reached this page while looking for the Enteropathogen Resource Integration Center (ERIC) BRC at www.ericbrc.org.  The Bioinformatics Resource Centers (BRCs) for Infectious Diseases program is a continuation and expansion of a program initiated in 2004 and consists of four new centers, each specializing in a different group of pathogens.  The PATRIC BRC is now responsible for all bacterial species in the NIAID <a href="http://www3.niaid.nih.gov/topics/emerging/list.htm" target="_blank">Category A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases</a>.
		</p>
		<p>For ERIC users, we understand that the resource was valuable to your work. As such, we will be doing our very best to create a useful PATRIC resource to continue supporting your work. We realize that the transition will cause disruptions.  However, it is a priority for us to work with established BRC users and communities to identify and prioritize our transition efforts. We will have a survey online soon to help us identify your needs, but in the meantime, you may contact us at <a href="mailto:patric@vbi.vt.edu">patric@vbi.vt.edu</a> with questions and requests.</p>
		<p>We have concentrated on the transfer of genomic data for this initial release.  We anticipate adding new data, tools, and website features over the next several months.  We look forward to working with you during the next 5 years.</p>
	</div>
	
	<div class="right" style="width:60%">
		<div class="table-container with-border">
			<h2>List of Genera</h2>
			<table class="data-table">
				<thead>
				<tr>
					<th width="80">Genus name</th>
					<th width="25">Genomes from BRC</th>
					<th width="70">Link</th>
					<th>Description</th>
				</tr>
				</thead>
				<tr>
					<td>Escherichia</td>
					<td class="right">37</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "561")%>
						<%=SiteHelper.getLinks("genome_list", "561")%>
						<%=SiteHelper.getLinks("feature_table", "561")%>
					</td>
					<td><i>Escherichia</i>, a genus of gram-negative rod-shaped bacteria, belongs to the family Enterobacteriaceae. 
						<i>Escherichia coli</i> has become a model organism for studying many of life's essential processes partly due to 
							its rapid growth rate and simple nutritional requirements.
						Normally <i>E. coli</i> serves a useful function in the body by suppressing the growth of harmful bacterial species
						 and by synthesizing appreciable amounts of vitamins. 
						A minority of <i> E. coli </i> strains are capable of causing human illness by several different mechanisms.
						<i>E. coli</i> serotype O157:H7 is a rare variety of <i>E. coli</i> that produces large quantities of one or more related, potent
				  		toxins that cause severe damage to the lining of the intestine.
					</td>
				</tr>
				<tr>
					<td>Salmonella</td>
					<td class="right">29</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "590")%>
						<%=SiteHelper.getLinks("genome_list", "590")%>
						<%=SiteHelper.getLinks("feature_table", "590")%>
					</td>
					<td><i>Salmonella</i> is a genus of rod-shaped, gram-negative bacteria in the family
					  	Enterobacteriaceae.  This genus has two species: <i>S. enterica</i>, which is subdivided into over 2,000 serovars, and <i>S. bongori</i>.
					  	Some serovars of <i>S. enterica</i>, such as <i>S. typhi</i>, cause systemic infections and typhoid fever. 
						Others, like <i>S. typhimurium</i>, cause gastroenteritis.
					</td>
				</tr>
				<tr>
					<td>Shigella</td>
					<td class="right">9</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "620")%>
						<%=SiteHelper.getLinks("genome_list", "620")%>
						<%=SiteHelper.getLinks("feature_table", "620")%>
					</td>
					<td><i>Shigella</i> is a group of gram-negative, facultative intracellular pathogens in the family
					  Enterobacteriaceae. Recognized as the etiologic agents of bacillary dysentery
					  or shigellosis in the 1890s, <i>Shigella </i> was adopted as a genus in the 1950s and subgrouped into four
					  species: <i>S. dysenteriae, S. flexneri, S.boydii</i> and <i>S. sonnei</i>(also designated as serogroups A to D).
					</td>
				</tr>
				<tr>
					<td>Yersinia</td>
					<td class="right">25</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "629")%>
						<%=SiteHelper.getLinks("genome_list", "629")%>
						<%=SiteHelper.getLinks("feature_table", "629")%>
					</td>
					<td><i>Yersinia</i>, a gram-negative rod-shaped bacterium, is a genus of bacteria in the family
					  Enterobacteriaceae. <i>Yersinia pestis</i>, the causative agent of plague, whose  most common clinical form
					  is acute febrile lymphadenitis, is more commonly known as bubonic plague. 
					  Other members of the genus, <i>Y. entercolitica</i> and <i>Y. pseudotuberculosis</i>, are also human pahtogens.
					</td>
				</tr>
				
				
				<tr>
					<td>Citrobacter</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "544")%>
						<%=SiteHelper.getLinks("genome_list", "544")%>
						<%=SiteHelper.getLinks("feature_table", "544")%>
					</td>
					<td><i>Citrobacter</i> is a genus of gram-negative coliform bacteria in the
					  	Enterobacteriaceae family and the phylum Proteobacteria.  Three members of
					  	the genus, <i>C. amalonaticus</i>, <i>C. diversus</i> and <i>C. freundii</i> are linked to human disease
					  	with <i>C. diversus</i> linked to frequent nosocomial outbreaks of neonatal meningitis.
					</td>
				</tr>
				<tr>
					<td>Cronobacter</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "413496")%>
						<%=SiteHelper.getLinks("genome_list", "413496")%>
						<%=SiteHelper.getLinks("feature_table", "413496")%>
					</td>
					<td><i>Cronobacter</i>, a genus of gram-negative, rod-shaped bacteria in the family
						  Enterobacteriaceae and the phylum Proteobacteria, is recognised as causative
						  agents of neonatal bacteraemia, meningitis and necrotising enterocolitis.
					</td>
				</tr>
				<tr>
					<td>Enterobacter</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "547")%>
						<%=SiteHelper.getLinks("genome_list", "547")%>
						<%=SiteHelper.getLinks("feature_table", "547")%>
					</td>
					<td>Some members of the genus <i>Enterobacter</i> are important nosocomial pathogens.
						Several strains of these gram-negative rod-shaped bacteria in the family Enterobacteriaceae
						 cause opportunistic infections in immunocompromised patients.
						Two clinically-important species from this genus are <i>E. aerogenes</i> and <i>E. cloacae</i>.
					</td>
				</tr>
				<tr>
					<td>Erwinia</td>
					<td class="right">2</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "551")%>
						<%=SiteHelper.getLinks("genome_list", "551")%>
						<%=SiteHelper.getLinks("feature_table", "551")%>
					</td>
					<td>Current members of the genus <i>Erwinia</i> are primarily pathogens of plants.
						They are gram-negative rod-shaped bacteria in the family Enterobacteriaceae.
						The genus was named for the first phytobacteriologist, Erwin Smith.
					</td>
				</tr>
				
				<tr>
					<td>Klebsiella</td>
					<td class="right">2</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "570")%>
						<%=SiteHelper.getLinks("genome_list", "570")%>
						<%=SiteHelper.getLinks("feature_table", "570")%>
					</td>
					<td><i>Klebsiella</i> is a genus of non-motile, gram-negative, rod shaped bacteria in
					  	the family Enterobacteriaceae with a prominent polysaccharide-based capsule. 
					  	Three species in the genus <i>Klebsiella</i> are associated with illness in humans:
						<i>K. pneumoniae</i> is a primary pathogen capable of causing urinary tract infections and pneumonia in otherwise
					  	healthy people, <i>K. oxytoca</i> can cause a variety of nosocomial infections, 
						and <i>K. granulomatis</i> causes chronic genital ulcerative disease.
					</td>
				</tr>
				<tr>
					<td>Pectobacterium</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "122277")%>
						<%=SiteHelper.getLinks("genome_list", "122277")%>
						<%=SiteHelper.getLinks("feature_table", "122277")%>
					</td>
					<td><i>Pectobacterium</i> are gram-negative rod-shaped bacterium in the family Enterobacteriaceae that are pathogens of angiosperms 
						with a broad host range. <i>Pectobacterium carotovorum</i> (syn <i>Erwinia carotovora</i>) is a plant pathogen able to cause disease
						in almost any plant tissue it invades.
					</td>
				</tr>
				<tr>
					<td>Photorhabdus</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "29487")%>
						<%=SiteHelper.getLinks("genome_list", "29487")%>
						<%=SiteHelper.getLinks("feature_table", "29487")%>
					</td>
					<td><i>Photorhabdus</i> is a gram-negative member of the family Enterobacteriaceae that
					  	lives in a mutualistic association with a <i>Heterorhabditis</i> nematode worm. 
						The nematode worm burrows into insect prey and regurgitates <i>Photorhabdus</i>,
					  	which goes on to kill the insect. Once the insect host is dead the bacteria
					  	bioconvert the tissues into more bacteria. The nematode feeds off the growing
					  	bacteria until the insect tissues are exhausted, whereupon they reassociate
					  	and leave the cadaver in search of new prey. This highly efficient
					  	partnership has been used for many years as a biological crop protection
					  	agent.  In addition to its well-described role as an insect pathogen one
					  	species of the genus, <i>P. asymbiotica</i>, causes infection in otherwise healthy humans.
					</td>
				</tr>
				<tr>
					<td>Proteus</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "583")%>
						<%=SiteHelper.getLinks("genome_list", "583")%>
						<%=SiteHelper.getLinks("feature_table", "583")%>
					</td>
					<td><i>Proteus</i> is a gram-negative member of the family Enterobacteriaceae.
						Several species in this genus are opportunistic human pathogens.  Most prominent is <i>P. mirabilis</i> which causes wound and
					  urinary tract infections.  Another species, <i>P. vulgaris</i>, can cause urinary tract and wound
					  infections and is a common cause of sinus and respiratory infections.
					</td>
				</tr>
				
				<tr>
					<td>Serratia</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "613")%>
						<%=SiteHelper.getLinks("genome_list", "613")%>
						<%=SiteHelper.getLinks("feature_table", "613")%>
					</td>
					<td><i>Serratia</i> is a genus of gram-negative, facultatively anaerobic, rod-shaped bacteria of
					  the Enterobacteriaceae family. <i>Serratia marcescens</i> is the primary pathogenic species and is commonly found in the
					  respiratory and urinary tracts of hospitalized adults and in the gastrointestinal system of children. Rare reports have described disease
					  resulting from infection with <i>S. plymuthica</i>, <i>S.liquefaciens</i>, <i>S. rubidaea</i>, <i>S. odorifera</i> and <i>S. fonticola</i>.
					</td>
				</tr>
			</table>
		</div>
	</div>
	<div class="clear"></div>
</div>
<br/><br/>