<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />
<div id="fp">
	
	<div class="far2x">
		<h2>BioHealthBase</h2>
	</div>
	
	<div class="left" style="width:38%">
		<img src="/patric/images/bhb_logo.png" alt="BHB BRC logo" />
		<p>You may have reached this page while looking for the BioHealthBase (BHB) BRC at www.biohealthbase.org.  The Bioinformatics Resource Centers (BRCs) for Infectious Diseases program is a continuation and expansion of a program initiated in 2004 and consists of four new centers, each specializing in a different group of pathogens.  The PATRIC BRC is now responsible for all bacterial species in the NIAID <a href="http://www3.niaid.nih.gov/topics/emerging/list.htm" target="_blank">Category A-C Priority Pathogen lists for biodefense research, and pathogens causing emerging/reemerging infectious diseases</a>.
		</p>
		<p>For BHB users, we understand that the resource was valuable to your work. As such, we will be doing our very best to create a useful PATRIC resource to continue supporting your work. We realize that the transition will cause disruptions.  However, it is a priority for us to work with established BRC users and communities to identify and prioritize our transition efforts. We will have a survey online soon to help us identify your needs, but in the meantime, you may contact us at <a href="mailto:patric@vbi.vt.edu">patric@vbi.vt.edu</a> with questions and requests.</p>
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
				<td>Francisella</td>
				<td class="right">9</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "262")%>
					<%=SiteHelper.getLinks("genome_list", "262")%>
					<%=SiteHelper.getLinks("feature_table", "262")%>
				</td>
				<td><i>Francisella</i>, a gram-negative coccobacillus and the only genus within the family Francisellaceae, 
					is a member of the phylum Proteobacteria. 
					There are two species within the <i>Francisella</i> genus: <i>tularensis</i> and <i>philomiragia</i>.
					<i>Francisella tularensis</i> is the causative agent of the zoonotic disease tularemia, also
				  	known as rabbit fever or deer-fly fever.
				</td>
			</tr>
			<tr>
				<td>Mycobacterium</td>
				<td class="right">19</td>
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "1763")%>
					<%=SiteHelper.getLinks("genome_list", "1763")%>
					<%=SiteHelper.getLinks("feature_table", "1763")%>
				</td>
				<td><i>Mycobacterium</i>, a genus of Actinobacteria, are aerobic, nonmotile, and rod-shaped bacteria.
					<i>Mycobacterium tuberculosis</i> is one of
				  	most successful pathogens of mankind, infecting one-third of the global
					population and claiming two million lives every year. The bacteria has the ability to persist
					in the form of a long-term asymptomatic infection,
					referred to as latent tuberculosis. Approximately eight million people
					develop active tuberculosis (TB) every year, with two million dying from the
					disease. In addition to this already huge burden of disease, it is estimated
					that up to two billion people have been infected with the causative agent, <i>M. tuberculosis</i>.
				</td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear"></div>
</div>