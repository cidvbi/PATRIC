<%@ page import="edu.vt.vbi.patric.common.SiteHelper" %>
<link rel="stylesheet" href="/patric-static/ibrc.css" type="text/css" />
<div id="fp">
	
	<div class="far2x">
		<h2>Rhizobiales Bioinformatics Resource Center</h2>
	</div>
	
	<div class="left" style="width:38%">
		<p>The goal of Rhizobiales Bioinformatics Resource Center (RhizobialesBRC) is to provide a comprehensive and accurate web-based resource for genomic and associated information on the sequenced Rhizobiales.  RhizobialesBRC has now become an integral part of PathoSystems Resource Integration Center (PATRIC) as PATRIC is being developed to provide rich data and analysis tools for all bacterial species, with a special focus on the NIAID Category A-C pathogens and those involved in emerging and re-emerging infectious diseases, as well as their neighbors. Some of the pathogen of interest to NIAID are closely related to the organisms RhizbialesBRC focused on, and its integration into PATRIC 2.0 provides high leverage to the communities interested in Rhizobiales. </p>	
	</div>

	<div class="right" style="width:60%">
		<table class="basic stripe">
		<thead>
			<tr>
				<th width="80">Genus name</th>
				<!-- <th width="25">Genomes from BRC</th> -->
				<th width="82">Link</th>
				<th>Description</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>Agrobacterium</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "357")%>
					<%=SiteHelper.getLinks("genome_list", "357")%>
					<%=SiteHelper.getLinks("feature_table", "357")%>
				</td>
				<td>Gram-negative soil bacteria in the family Rhizobiaceae of the order Rhizobiales. Many members of this group e.g. <i>Agrobacterium tumefaciens</i>, can cause disease on plants.
				</td>
			</tr>
			<tr>
				<td>Bartonella</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "773")%>
					<%=SiteHelper.getLinks("genome_list", "773")%>
					<%=SiteHelper.getLinks("feature_table", "773")%>
				</td>
				<td>Gram-negative bacteria in the family Bartonellaceae of the order Rhizobiales. <i>Bartonella</i> species can infect human and cause bartonellosis.
				</td>
			</tr>
			<tr>
				<td>Bradyrhizobium</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "374")%>
					<%=SiteHelper.getLinks("genome_list", "374")%>
					<%=SiteHelper.getLinks("feature_table", "374")%>
				</td>
				<td>Gram-negative, aerobic, rod-shaped bacteria in  the family Bradyrhizobiaceae of the order Rhizobiales. It is able to form nitrogen-fixing symbioses with leguminous plants. 
				</td>
			</tr>
			<tr>
				<td>Brucella</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "234")%>
					<%=SiteHelper.getLinks("genome_list", "234")%>
					<%=SiteHelper.getLinks("feature_table", "234")%>
				</td>
				<td><i>Brucella</i> are gram-negative, facultative, intracellular bacteria that are members of the order Rhizobiales. 
					They can infect many species of animals as well as man. 
					Human brucellosis is treatable with antibiotics, though the course of antibiotic treatment must be prolonged due to the intracellular nature of <i>Brucella</i>. 
					The genus <i>Brucella</i> consists of at least six species, designated on the basis of host preference, antigenic and biochemical characteristics.
				</td>
			</tr>
			
			<tr>
				<td>Mesorhizobium</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "68287")%>
					<%=SiteHelper.getLinks("genome_list", "68287")%>
					<%=SiteHelper.getLinks("feature_table", "68287")%>
				</td>
				<td>Gram-negative bacteria in  the family Phyllobacteriaceae of the order Rhizobiales. It is able to form nitrogen-fixing symbioses with leguminous plants.
				</td>
			</tr>
			
			<tr>
				<td>Rhizobium</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "379")%>
					<%=SiteHelper.getLinks("genome_list", "379")%>
					<%=SiteHelper.getLinks("feature_table", "379")%>
				</td>
				<td>Gram-negative bacteria in  the family Rhizobiaceae of the order Rhizobiales. It is able to form nitrogen-fixing symbioses with leguminous plants.
				</td>
			</tr>
			
			<tr>
				<td>Rhodopseudomonas</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "1073")%>
					<%=SiteHelper.getLinks("genome_list", "1073")%>
					<%=SiteHelper.getLinks("feature_table", "1073")%>
				</td>
				<td>Gram-negative, rod-shaped, phototrophic bacteria found in many types of marine environments and soils. It converts sunlight into energy and converts atmospheric carbon dioxide into biomass. It belongs to the family Bradyrhizobiaceae of the order Rhizobiales.
				</td>
			</tr>
			
			<tr>
				<td>Sinorhizobium</td>
				<!-- <td class="right"></td>-->
				<td>
					<%=SiteHelper.getLinks("taxon_overview", "28105")%>
					<%=SiteHelper.getLinks("genome_list", "28105")%>
					<%=SiteHelper.getLinks("feature_table", "28105")%>
				</td>
				<td>Gram-negative bacteria in  the family Rhizobiaceae of the order Rhizobiales. It is able to form nitrogen-fixing symbioses with leguminous plants.
				</td>
			</tr>
		</tbody>
		</table>
	</div>
	<div class="clear"></div>
</div>