<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />
<div id="fp">
	
	<div class="far2x">
		<h2>PATRIC</h2>
	</div>
	
	<div class="left" style="width:38%">
		<img src="/patric/images/patric_logo.png" alt="PATRIC BRC logo" />
		<p>You may have reached this page while looking for the PATRIC BRC at patric.vbi.vt.edu.  The Bioinformatics Resource Centers (BRCs) for Infectious Diseases program is a continuation and expansion of a program initiated in 2004 and consists of four new centers, each specializing in a different group of pathogens.  The PATRIC BRC is now responsible for all bacterial species in the NIAID <a href="http://www3.niaid.nih.gov/topics/emerging/list.htm" target="_blank">Category A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases</a>.
		</p>
		<p>For PATRIC users, we understand that the resource was valuable to your work. As such, we will be doing our very best to create a useful PATRIC resource to continue supporting your work. We realize that the transition will cause disruptions.  However, it is a priority for us to work with established BRC users and communities to identify and prioritize our transition efforts. We will have a survey online soon to help us identify your needs, but in the meantime, you may contact us at <a href="mailto:patric@vbi.vt.edu">patric@vbi.vt.edu</a> with questions and requests.</p>
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
				<td>Brucella</td>
				<td class="right">38</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "234")%>
					<%=SiteHelper.getLinks("genome_list", "234")%>
					<%=SiteHelper.getLinks("feature_table", "234")%>
				</td>
				<td><i>Brucella</i> are gram-negative, facultative, intracellular bacteria that are members of the order Rhizobiales.
					They can infect many species of animals as well as man. Human brucellosis is treatable with antibiotics, though the course of
				  	antibiotic treatment must be prolonged due to the intracellular nature of <i>Brucella</i>.
					The genus <i>Brucella</i> consists of at least six species, designated on the basis of host preference, antigenic and
				  	biochemical characteristics.
				</td>
			</tr>
			<tr>
				<td>Coxiella</td>
				<td class="right">6</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "776")%>
					<%=SiteHelper.getLinks("genome_list", "776")%>
					<%=SiteHelper.getLinks("feature_table", "776")%>
				</td>
				<td><i>Coxiella</i> is a genus best known for the species <i>C. burnetii</i>, the causative agent of Q fever.
					<i>Coxiella</i> is a gram-negative member of the order Legionellales in the class Gammaproteobacteria.
					Q fever is a category B bioterrorism agent that is highly infective to both humans and livestock.
				</td>
			</tr>
			<tr>
				<td>Rickettsia</td>
				<td class="right">15</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "780")%>
					<%=SiteHelper.getLinks("genome_list", "780")%>
					<%=SiteHelper.getLinks("feature_table", "780")%>
				</td>
				<td>Rickettsiae are a group of gram-negative bacteria belonging to the class Alphaproteobacteria.
					The order Rickettsiales comprises three families: Holosporaceae, Anaplasmataceae and Rickettsiaceae.
					<i>Rickettsia</i> spp. are grouped in Rickettsiaceae and many species are pathogenic to humans.
					Included in these is <i>R. prowazekii</i>, the etiologic agent of epidemic typhus, 
					and <i>R. rickettsii</i>, which causes Rocky Mountain spotted fever.
				</td>
			</tr>
			<tr>
				<td>Ochrobactrum</td>
				<td class="right">1</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "528")%>
					<%=SiteHelper.getLinks("genome_list", "528")%>
					<%=SiteHelper.getLinks("feature_table", "528")%>
				</td>
				<td><i>Ochrobactrum</i> is a gram-negative genus of bacteria in the family Brucellaceae of the order Rhizobiales.
					Closely related to <i>Brucella</i>, members of this genus have a wide distribution in environmental
				  	and water sources. <i>Ochrobactrum anthropi</i> is an emerging opportunist pathogen in immunocompromised patients.
				</td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear"></div>
</div>