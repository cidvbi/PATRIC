<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />
<div id="fp">
	
	<div class="far2x">
		<h2>National Microbial Pathogen Data Resource (NMPDR)</h2>
	</div>
	
	<div class="left" style="width:38%">
		<img src="/patric/images/nmpdr_logo.png" alt="NMPDR BRC logo" />
		<p>You may have reached this page while looking for the National Microbial Pathogen Data Resource (NMPDR) BRC at www.nmpdr.org.  The Bioinformatics Resource Centers (BRCs) for Infectious Diseases program is a continuation and expansion of a program initiated in 2004 and consists of four new centers, each specializing in a different group of pathogens.  The PATRIC BRC is now responsible for all bacterial species in the NIAID <a href="http://www3.niaid.nih.gov/topics/emerging/list.htm" target="_blank">Category A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases</a>.
		</p>
		<p>For NMPDR users, we understand that the resource was valuable to your work. As such, we will be doing our very best to create a useful PATRIC resource to continue supporting your work. We realize that the transition will cause disruptions.  However, it is a priority for us to work with established BRC users and communities to identify and prioritize our transition efforts. We will have a survey online soon to help us identify your needs, but in the meantime, you may contact us at <a href="mailto:patric@vbi.vt.edu">patric@vbi.vt.edu</a> with questions and requests.</p>
		<p>We have concentrated on the transfer of genomic data for this initial release.  We anticipate adding new data, tools, and website features over the next several months.  We look forward to working with you during the next 5 years.</p>
	</div>
	
	<div class="right" style="width:60%">
		<table class="basic stripe">
		<thead>
			<tr>
				<th scope="col" width="80">Genus name</th>
				<th scope="col" width="25">Genomes from BRC</th>
				<th scope="col" width="82">Link</th>
				<th scope="col">Description</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>Campylobacter</td>
				<td class="right">12</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "194")%>
					<%=SiteHelper.getLinks("genome_list", "194")%>
					<%=SiteHelper.getLinks("feature_table", "194")%>
				</td>
				<td>The genus <i>Campylobacter</i>, meaning 'twisted bacteria', describes gram-negative bacteria
				  	that are members of the order Campylobacterales in the class Epsilonproteobacteria.
				  	At least a dozen species of <i>Campylobacter</i> have been implicated in human disease with <i>C. jejuni</i> and <i>C. coli</i> the most common.
				  	<i>Campylobacter jejuni</i> is now recognized as one of the main causes of bacterial foodborne disease in many developed countries.
				</td>
			</tr>
			<tr>
				<td>Chlamydia</td>
				<td class="right">1</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "810")%>
					<%=SiteHelper.getLinks("genome_list", "810")%>
					<%=SiteHelper.getLinks("feature_table", "810")%>
				</td>
				<td>The order Chlmydiales contians only one family, the Chlamydiaceae, and one genus, <i>Chlamydia</i>.
					Chlamydiae are obligate intracellular bacteria and four species are currently recognized, <i>C. pecorum</i>, <i>C. psittaci</i>,
					 <i>C. trachomatis</i> and <i>C. pneumoniae</i>.
					All except <i>C. pecorum</i> have been associated with human disease.
				</td>
			</tr>
			<tr>
				<td>Chlamydophila</td>
				<td class="right">5</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "83553")%>
					<%=SiteHelper.getLinks("genome_list", "83553")%>
					<%=SiteHelper.getLinks("feature_table", "83553")%>
				</td>
				<td><i>Chlamydophila</i> is a bacterial genus belonging to the family Chlamydiaceae,
				  	order Chlamydiales, class/phylum Chlamydiae.  Several species within this
				  	genus cause disease in humans, most notably <i>C. pneumoniae</i> and <i>C. psittaci</i>.
					<i>Chlamydophila pneumoniae</i> infects humans and is a major cause of pneumonia.
					<i>Chlamydophila psittaci</i> causes endemic avian chlamydiosis, epizootic outbreaks in mammals, and respiratory
				  	psittacosis in humans.
				</td>
			</tr>
			<tr>
				<td>Haemophilus</td>
				<td class="right">8</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "724")%>
					<%=SiteHelper.getLinks("genome_list", "724")%>
					<%=SiteHelper.getLinks("feature_table", "724")%>
				</td>
				<td><i>Haemophilus</i> is a genus of gram-negative, pleomorphic, coccobacilli bacteria
				  	belonging to the Pasteurellaceae family of the class Gammaproteobacteria. 
				  	The genus includes commensal organisms along with some significant pathogenic
				  	species such as <i>H. influenzae</i>, a cause of sepsis and bacterial meningitis in young children,
				  	and <i>H. ducreyi</i>, the causative agent of chancroid. All members are either aerobic or facultatively
				  	anaerobic.
				</td>
			</tr>
			<tr>
				<td>Listeria</td>
				<td class="right">19</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "1637")%>
					<%=SiteHelper.getLinks("genome_list", "1637")%>
					<%=SiteHelper.getLinks("feature_table", "1637")%>
				</td>
				<td>Species of the genus <i>Lister</i> are gram-positive bacilli that belong to the family Listeriaceae
				  in the order Bacilli. There are six species of <i>Listeria</i>.
				  Only <i>L. monocytogenes</i> is pathogenic for humans causing listerosis, an uncommon but
				  serious zoonotic infection contracted by eating contaminated food.
				</td>
			</tr>
			<tr>
				<td>Mycoplasma</td>
				<td class="right">12</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "2093")%>
					<%=SiteHelper.getLinks("genome_list", "2093")%>
					<%=SiteHelper.getLinks("feature_table", "2093")%>
				</td>
				<td><i>Mycoplasma</i> is one of several genera within the Mollicutes in the phylum
				  Tenericutes, a class of bacteria that have small genomes and lack a cell
				  wall.  Several species are pathogenic in humans, including <i>M. pneumoniae</i>, which is an important
				  cause of atypical pneumonia and other respiratory disorders, and <i>M. genitalium</i>, which is believed to be
				  involved in pelvic inflammatory diseases.</td>
			</tr>
			<tr>
				<td>Neisseria</td>
				<td class="right">5</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "482")%>
					<%=SiteHelper.getLinks("genome_list", "482")%>
					<%=SiteHelper.getLinks("feature_table", "482")%>
				</td>
				<td><i>Neisseria</i> is a genus of the epsilon group phylum Proteobacteria and are gram-negative
				  diplococci that resemble coffee beans when viewed microscopically.   They are
				  a large family of commensal bacteria that colonize the mucosal surfaces of
				  many animals.  Only two of the eleven species that colonize humans are
				  pathogens, <i>N. meningitidis</i> and <i>N. gonorrhoeae</i>.
				</td>
			</tr>
			<tr>
				<td>Staphylococcus</td>
				<td class="right">18</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "1279")%>
					<%=SiteHelper.getLinks("genome_list", "1279")%>
					<%=SiteHelper.getLinks("feature_table", "1279")%>
				</td>
				<td><i>Staphylococcus</i>, a spherical gram-positive bacteria that is immobile and forms grape-like clusters, is a member of the phylum Firmicutes.
					Most of the species within this genus are nonpathogenic.
					A notable exception is <i> S. aureus</i>, the most common cause of staph infections.</td>
			</tr>
			<tr>
				<td>Streptococcus</td>
				<td class="right">34</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "1301")%>
					<%=SiteHelper.getLinks("genome_list", "1301")%>
					<%=SiteHelper.getLinks("feature_table", "1301")%>
				</td>
				<td><i>Streptococcus</i> is a genus of spherical gram-positive bacteria belonging to the phylum Firmicutes.
					Among its pathogenic members are <i>S. pyogenes</i>, the cause of many important human diseases ranging from mild
				  	superficial skin infections to life-threatening systemic diseases, and <i>S. pneumoniae</i>,
				 	the most common cause of bacterial meningitis in adults and children.
				</td>
			</tr>
			<tr>
				<td>Treponema</td>
				<td class="right">2</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "157")%>
					<%=SiteHelper.getLinks("genome_list", "157")%>
					<%=SiteHelper.getLinks("feature_table", "157")%>
				</td>
				<td><i>Treponema</i> is a member of the phylum Spirochaetes. 
					<i>Treponema pallidum </i> is a motile spirochaete that is generally acquired by close
				  	sexual contact and can also be transmitted to a fetus during the later stages
				  	of pregnancy, giving rise to congenital syphilis.
				</td>
			</tr>
			<tr>
				<td>Ureaplasma</td>
				<td class="right">13</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "2129")%>
					<%=SiteHelper.getLinks("genome_list", "2129")%>
					<%=SiteHelper.getLinks("feature_table", "2129")%>
				</td>
				<td><i>Ureaplasma</i> is one of several genera within the class Mollicutes of the phylum Tenericutes. There are six
				  recognized <i>Ureaplasma</i> species. <i>Ureaplasma urealyticum</i> is part of the normal genital flora and is found 
					in about 70% of sexually active humans. It can also cause disease, including non-specific urethritis, infertility, chorioamnioitis, 
					stillbirth, premature birth, and in the perinatal period, pneumonia or meningitis.
				</td>
			</tr>
			<tr>
				<td>Vibrio</td>
				<td class="right">24</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "662")%>
					<%=SiteHelper.getLinks("genome_list", "662")%>
					<%=SiteHelper.getLinks("feature_table", "662")%>
				</td>
				<td><i>Vibrio</i> is a genus of gram-negative rod-shaped bacteria in the class Gammaproteobacteria and phylum
				  	Proteobacteria.  The genus <i>Vibrio</i> contains many pathogenic members, the most famous being <i>Vibrio cholerae</i>,
				 	a historically feared epidemic diarrheal disease.</td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear"></div>
</div>