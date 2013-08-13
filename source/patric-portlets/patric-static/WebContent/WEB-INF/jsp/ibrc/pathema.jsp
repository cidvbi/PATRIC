<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />
<div id="fp">
	
	<div>
		<h2>Pathema</h2>
	</div>
	
	<div class="left" style="width:38%">
		<img src="/patric/images/pathema_logo.png" alt="Pathema BRC logo" />
		<p>You may have reached this page while looking for the Pathema BRC at pathema.jcvi.org.  The Bioinformatics Resource Centers (BRCs) for Infectious Diseases program is a continuation and expansion of a program initiated in 2004 and consists of four new centers, each specializing in a different group of pathogens.  The PATRIC BRC is now responsible for all bacterial species in the NIAID <a href="http://www3.niaid.nih.gov/topics/emerging/list.htm" target="_blank">Category A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases</a>.
		</p>
		<p>For Pathema users, we understand that the resource was valuable to your work. As such, we will be doing our very best to create a useful PATRIC resource to continue supporting your work. We realize that the transition will cause disruptions.  However, it is a priority for us to work with established BRC users and communities to identify and prioritize our transition efforts. We will have a survey online soon to help us identify your needs, but in the meantime, you may contact us at <a href="mailto:patric@vbi.vt.edu">patric@vbi.vt.edu</a> with questions and requests.</p>
		<p>We have concentrated on the transfer of genomic data for this initial release.  We anticipate adding new data, tools, and website features over the next several months.  We look forward to working with you during the next 5 years.</p>
	</div>
	
	<div class="right" style="width:60%">
		<div class="table-container with-border">
			<h2>List of Genera</h2>
			<table class="data-table">
				<thead>
				<tr>
					<th width="80">Genus name</th>
					<th width="30">Genomes from BRC</th>
					<th width="70">Link</th>
					<th>Description</th>
				</tr>
				</thead>
				<tr>
					<td>Bacillus</td>
					<td class="right">19*</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "1386")%>
						<%=SiteHelper.getLinks("genome_list", "1386")%>
						<%=SiteHelper.getLinks("feature_table", "1386")%>
					</td>
					<td><i>Bacillus</i> is a genus of rod-shaped bacteria in the phylum Firmicutes.
						<i>Bacillus</i> species, which contain both free-living and pathogenic species, are obligate aerobes.
						The most well-known species within the genus is <i>B. anthracis</i>, the causative agent of anthrax 
						that was used as a bioweapon in the 2001 anthrax letter attacks in the U.S.
					</td>
				</tr>
				<tr>
					<td>Burkholderia</td>
					<td class="right">16*</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "32008")%>
						<%=SiteHelper.getLinks("genome_list", "32008")%>
						<%=SiteHelper.getLinks("feature_table", "32008")%>
					</td>
					<td>Previously part of <i>Pseudomonas</i>, <i>Burkholderia</i> is a genus of gram-negative, motile, 
						obligately aerobic rod-shaped bacteria in the phylum Proteobacteria.
						<i>Burkholderia</i> is a genus best-known for its pathogenic members: <i>B. mallei</i>, responsible for glanders, 
						a disease that occurs mostly in horses and related animals; <i>B. pseudomallei</i>, causative agent of melioidosis; 
						and <i>B. cepacia</i>, an important source of pulmonary infection in people with cystic fibrosis.
					</td>
				</tr>
				<tr>
					<td>Clostridium</td>
					<td class="right">18*</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "1485")%>
						<%=SiteHelper.getLinks("genome_list", "1485")%>
						<%=SiteHelper.getLinks("feature_table", "1485")%>
					</td>
					<td><i>Clostridium</i> is a genus of gram-positive bacteria belonging to the phylum Firmicutes.
						They are obligate anaerobes capable of producing endospores. 
						<i>Clostridium botulinum</i>, usually found in soil and in aquatic sediments, can cause the disease botulism.
						It produces a neurotoxin that is lethal if not treated immediately and properly.
						<i>Clostridium perfringens</i> is commonly found in humans as a member of the normal flora but has also been shown to be
					  	a cause of human diseases such as gas gangrene (clostridial myonecrosis), food poisoning, necrotizing enterocolitis of infants, and enteritis
					  	necroticans.
					</td>
				</tr>
				<tr>
					<td>Pseudomonas</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "286")%>
						<%=SiteHelper.getLinks("genome_list", "286")%>
						<%=SiteHelper.getLinks("feature_table", "286")%>
					</td>
					<td><i>Pseudomonas</i> are aerobic, gram-negative rods that are members of the class Gammaproteobacteria in the phylum Proteobacteria.
						Species infectious to humans include <i>P. aeruginosa</i> and <i>P. oryzihabitans</i>.
					</td>
				</tr>
				<tr>
					<td>Ralstonia</td>
					<td class="right">1</td>
					<td>
						<%=SiteHelper.getLinks("taxon_overview", "48736")%>
						<%=SiteHelper.getLinks("genome_list", "48736")%>
						<%=SiteHelper.getLinks("feature_table", "48736")%>
					</td>
					<td><i>Ralstonia</i> is a genus in the class Betaproteobacteria  of the phylum Proteobacteria.
						It was previously included in the genus <i>Pseudomonas</i> and includes <i>R. solanacearum</i>, 
						a widely distributed soil-borne pathogen that causes a lethal wilting disease of more than 200 plants species.
						<i>Ralstonia pickettii</i> (formerly <i>Pseudomonas picketti</i> and <i>Burkholderia pickettii</i>) is a bacillus of relatively low virulence
					  	that is often associated with pseudobacteremia or asymptomatic colonization of patients.
					</td>
				</tr>
			</table>
			*Inclusion of additional genome data from Pathema is underway and will be made 
			available on the PATRIC website in a future release.
		</div>
		
	</div>
	<div class="clear"></div>
</div>
<br/><br/>