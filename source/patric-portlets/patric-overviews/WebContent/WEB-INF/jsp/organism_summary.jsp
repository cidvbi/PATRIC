<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" 
%><%@ page import="java.util.HashMap" 
%><%@ page import="java.util.Formatter" 
%><%@ page import="java.util.Random" 
%><%@ page import="edu.vt.vbi.patric.dao.DBSummary" 
%><%@ page import="edu.vt.vbi.patric.dao.ResultType"
%><%
HashMap<String,Integer> h = new HashMap<String,Integer>();
h.put("Bacillus",1386);
h.put("Bartonella",773);
h.put("Borrelia",138);
h.put("Brucella",234);
h.put("Burkholderia",32008);
h.put("Campylobacter",194);
h.put("Chlamydophila",83553);
h.put("Clostridium",1485);
h.put("Coxiella",776);
h.put("Ehrlichia",943);
h.put("Escherichia",561);
h.put("Francisella",262);
h.put("Helicobacter",209);
h.put("Listeria",1637);
h.put("Mycobacterium",1763);
h.put("Rickettsia",780);
h.put("Salmonella",590);
h.put("Shigella",620);
h.put("Staphylococcus",1279);
h.put("Streptococcus",1301);
h.put("Vibrio",662);
h.put("Yersinia",629);

DBSummary conn_summary = new DBSummary();
HashMap<String,String> key = new HashMap<String,String>();
key.put("data_source", "PATRIC");
ResultType counts = null;


String _linkGenomeList = "GenomeList?cType=taxon&amp;cId=%d&amp;dataSource=PATRIC&amp;displayMode=%s#key=%d&amp;pS=20&amp;aP=1&amp;dir=ASC&amp;sort=genome_name";
Random generator = new Random();
%>
<h3>
PATRIC, the PathoSystems Resource Integration Center, is a multi-faceted web-based bioinformatics resource 
that provides rich data and analysis tools for all bacterial species with an emphasis on the bacterial <i>Orders</i> 
that include NIAID category A-C priority pathogens.</h3>


	<div class="table-container">
		<table style="width:100%">
		<tr>
			<th>Phylum:Order</th>
			<th>Watchlist Genus</th>
			<th>BRC</th>
			<th>NIAID</th>
			<th>CDC Select</th>
			<th>CDC Notifiable</th>
			<th>No. genomes</th>
			<th>No. Complete genomes</th>
			<th>No. WGS genomes</th>
			<th>No. Plasmid only genomes</th>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Bacillus"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Firmicutes:Bacillales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Bacillus") %>">Bacillus</a></td>
			<td>BRC</td>
			<td>Category A</td>
			<td>overlap</td>
			<td></td>
			<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "", (generator.nextInt(10000) + 1)) %>"><%=counts.get("cnt_all") %></a></td>
			<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "complete", (generator.nextInt(10000) + 1)) %>"><%=counts.get("cnt_complete") %></a></td>
			<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "wgs", (generator.nextInt(10000) + 1)) %>"><%=counts.get("cnt_wgs") %></a></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Listeria"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Firmicutes:Bacillales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Listeria") %>">Listeria</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Staphylococcus"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Firmicutes:Bacillales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Staphylococcus") %>">Staphylococcus</a></td>
			<td>BRC</td>
			<td>Re-emerging</td>
			<td>overlap</td>
			<td></td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Clostridium"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Firmicutes:Clostridiales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Clostridium") %>">Clostridium</a></td>
			<td>BRC</td>
			<td>Category A+B, Re-emerging</td>
			<td>overlap</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Streptococcus"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Firmicutes:Lactobacillales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Streptococcus") %>">Streptococcus</a></td>
			<td>BRC</td>
			<td>Re-emerging</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		
		<%
		key.put("ncbi_taxon_id", ""+h.get("Borrelia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Spirochaetes:Spirochaetales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Borrelia") %>">Borrelia</a></td>
			<td></td>
			<td>Emerging</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Mycobacterium"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Actinobacteria:Actinomycetales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Mycobacterium") %>">Mycobacterium</a></td>
			<td>BRC</td>
			<td>Category C</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Chlamydophila"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Chlamydiae/Verrucomicrobia: Chlamydiales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Chlamydophila") %>">Chlamydophila</a></td>
			<td></td>
			<td>Category B</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Campylobacter"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Epsilonproteobacteria: Campylobacterales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Campylobacter") %>">Campylobacter</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td></td>
			<td></td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Helicobacter"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Epsilonproteobacteria: Campylobacterales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Helicobacter") %>">Helicobacter</a></td>
			<td></td>
			<td>Emerging</td>
			<td></td>
			<td></td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Ehrlichia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Alphaproteobacteria:Rickettsiales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Ehrlichia") %>">Ehrlichia</a></td>
			<td></td>
			<td>Emerging</td>
			<td>USDA select</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Rickettsia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Alphaproteobacteria:Rickettsiales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Rickettsia") %>">Rickettsia</a></td>
			<td>BRC</td>
			<td>Category B+C</td>
			<td>HHS select</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Bartonella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Alphaproteobacteria:Rhizobiales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Bartonella") %>">Bartonella</a></td>
			<td></td>
			<td>Emerging</td>
			<td></td>
			<td></td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Brucella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Alphaproteobacteria:Rhizobiales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Brucella") %>">Brucella</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td>overlap</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Burkholderia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Betaproteobacteria:Burkholderiales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Burkholderia") %>">Burkholderia</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td>overlap</td>
			<td></td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Coxiella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: 'Thiotrichales+Legionellales' </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Coxiella") %>">Coxiella</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td>overlap</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Francisella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: 'Thiotrichales+Legionellales' </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Francisella") %>">Francisella</a></td>
			<td>BRC</td>
			<td>Category A</td>
			<td>overlap</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Vibrio"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria:Vibrionales</td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Vibrio") %>">Vibrio</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Escherichia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: Enterobacteriales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Escherichia") %>">Escherichia</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Salmonella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: Enterobacteriales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Salmonella") %>">Salmonella</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td></td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Shigella"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: Enterobacteriales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Shigella") %>">Shigella</a></td>
			<td>BRC</td>
			<td>Category B</td>
			<td>overlap</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		<%
		key.put("ncbi_taxon_id", ""+h.get("Yersinia"));
		counts = conn_summary.getGenomeCount(key);
		%>
		<tr>
			<td>Gammaproteobacteria: Enterobacteriales </td>
			<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Yersinia") %>">Yersinia</a></td>
			<td>BRC</td>
			<td>Category A+B</td>
			<td>HHS select</td>
			<td>Yes</td>
			<td><%=counts.get("cnt_all") %></td>
			<td><%=counts.get("cnt_complete") %></td>
			<td><%=counts.get("cnt_wgs") %></td>
			<td><%=counts.get("cnt_plasmid") %></td>
		</tr>
		</table>
	</div>
