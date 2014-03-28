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

String _linkGenomeList = "GenomeList?cType=taxon&amp;cId=%d&amp;dataSource=RAST&amp;displayMode=&amp;pk=&amp;kw=%s";
%>
<p>
PATRIC, the PathoSystems Resource Integration Center, is a multi-faceted web-based bioinformatics resource 
that provides rich data and analysis tools for all bacterial species with an emphasis on the bacterial <i>Orders</i> 
that include NIAID category A-C priority pathogens.</p>

<%
// get higher level summary
ResultType Bacteria = null;
key.put("ncbi_taxon_id", "2");
Bacteria = conn_summary.getGenomeCount(key);

ResultType Actinobacteria = null;
key.put("ncbi_taxon_id", "201174");
Actinobacteria = conn_summary.getGenomeCount(key);

ResultType Proteobacteria = null;
key.put("ncbi_taxon_id", "1224");
Proteobacteria = conn_summary.getGenomeCount(key);

ResultType Chlamydiae = null;
key.put("ncbi_taxon_id", "204428");
Chlamydiae = conn_summary.getGenomeCount(key);

ResultType Firmicutes = null;
key.put("ncbi_taxon_id", "1239");
Firmicutes = conn_summary.getGenomeCount(key);

ResultType Spirochaetes = null;
key.put("ncbi_taxon_id", "203691");
Spirochaetes = conn_summary.getGenomeCount(key);

%>

<table class="basic stripe far2x">
<thead>
	<tr>
		<th scope="col">Super Kingdom</th>
		<th scope="col">Phylum</th>
		<th scope="col">Class</th>
		<th scope="col">Order</th>
		<th scope="col">Watchlist Genus</th>
		<th scope="col">BRC</th>
		<th scope="col">NIAID</th>
		<th scope="col">CDC Select</th>
		<th scope="col">CDC Notifiable</th>
		<th scope="col">No. genomes</th>
		<th scope="col">No. Complete genomes</th>
		<th scope="col">No. WGS genomes</th>
		<th scope="col">No. Plasmid only genomes</th>
	</tr>
</thead>
<tbody>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Mycobacterium"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
		<td rowspan="22"><b>Bacteria</b>
			<br/><%=Bacteria.get("cnt_all")%> genomes
		</td>
		<td>Actinobacteria
			<br/><%=Actinobacteria.get("cnt_all") %> genomes
		</td>
		<td>Actinobacteria</td>
		<td>Actinomycetales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Mycobacterium") %>">Mycobacterium</a></td>
		<td>BRC</td>
		<td>Category C</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Mycobacterium"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Mycobacterium"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Mycobacterium"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Bartonella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
		<td rowspan="14">Proteobacteria
			<br/><%=Proteobacteria.get("cnt_all")%> genomes
		</td>
		<td rowspan="4">Alphaproteobacteria</td>
		<td rowspan="2">Rhizobiales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Bartonella") %>">Bartonella</a></td>
		<td>&nbsp;</td>
		<td>Emerging</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bartonella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bartonella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bartonella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Brucella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Alphaproteobacteria</td> -->
<!-- 		<td>Rhizobiales</td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Brucella") %>">Brucella</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>overlap</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Brucella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Brucella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Brucella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Ehrlichia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Alphaproteobacteria</td> -->
		<td rowspan="2">Rickettsiales </td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Ehrlichia") %>">Ehrlichia</a></td>
		<td>&nbsp;</td>
		<td>Emerging</td>
		<td>USDA select</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Ehrlichia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Ehrlichia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Ehrlichia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Rickettsia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Alphaproteobacteria</td> -->
<!-- 		<td>Rickettsiales </td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Rickettsia") %>">Rickettsia</a></td>
		<td>BRC</td>
		<td>Category B+C</td>
		<td>HHS select</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Rickettsia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Rickettsia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Rickettsia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Burkholderia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
		<td>Betaproteobacteria</td>
		<td>Burkholderiales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Burkholderia") %>">Burkholderia</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>overlap</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Burkholderia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Burkholderia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Burkholderia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Campylobacter"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
		<td rowspan="2">Epsilonproteobacteria</td>
		<td rowspan="2">Campylobacterales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Campylobacter") %>">Campylobacter</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Campylobacter"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Campylobacter"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Campylobacter"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Helicobacter"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Epsilonproteobacteria</td> -->
<!-- 		<td>Campylobacterales</td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Helicobacter") %>">Helicobacter</a></td>
		<td>&nbsp;</td>
		<td>Emerging</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Helicobacter"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Helicobacter"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Helicobacter"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Francisella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
		<td rowspan="7">Gammaproteobacteria</td>
		<td rowspan="2">Vibrionales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Francisella") %>">Francisella</a></td>
		<td>BRC</td>
		<td>Category A</td>
		<td>overlap</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Francisella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Francisella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Francisella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Vibrio"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
<!-- 		<td>Vibrionales</td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Vibrio") %>">Vibrio</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Vibrio"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Vibrio"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Vibrio"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Escherichia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
		<td rowspan="4">Enterobacteriales </td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Escherichia") %>">Escherichia</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Escherichia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Escherichia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Escherichia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Salmonella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
<!-- 		<td>Enterobacteriales </td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Salmonella") %>">Salmonella</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Salmonella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Salmonella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Salmonella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Shigella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
<!-- 		<td>Enterobacteriales </td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Shigella") %>">Shigella</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>overlap</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Shigella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Shigella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Shigella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Yersinia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
<!-- 		<td>Enterobacteriales </td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Yersinia") %>">Yersinia</a></td>
		<td>BRC</td>
		<td>Category A+B</td>
		<td>HHS select</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Yersinia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Yersinia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Yersinia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Coxiella"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Proteobacteria</td> -->
<!-- 		<td>Gammaproteobacteria</td> -->
		<td>Thiotrichales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Coxiella") %>">Coxiella</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>overlap</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Coxiella"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Coxiella"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Coxiella"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Chlamydophila"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
		<td>Chlamydiae
			<br/><%=Chlamydiae.get("cnt_all") %> genomes
		</td>
		<td>Chlamydiia</td>
		<td>Chlamydiales </td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Chlamydophila") %>">Chlamydophila</a></td>
		<td>&nbsp;</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Chlamydophila"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Chlamydophila"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Chlamydophila"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Bacillus"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
		<td rowspan="5">Firmicutes
			<br/><%=Firmicutes.get("cnt_all") %> genomes
		</td>
		<td rowspan="4">Bacilli</td>
		<td rowspan="3">Bacillales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Bacillus") %>">Bacillus</a></td>
		<td>BRC</td>
		<td>Category A</td>
		<td>overlap</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Bacillus"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Listeria"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Firmicutes</td> -->
<!-- 		<td>Bacilli</td> -->
<!-- 		<td>Bacillales</td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Listeria") %>">Listeria</a></td>
		<td>BRC</td>
		<td>Category B</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Listeria"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Listeria"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Listeria"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Staphylococcus"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Firmicutes</td> -->
<!-- 		<td>Bacilli</td> -->
<!-- 		<td>Bacillales</td> -->
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Staphylococcus") %>">Staphylococcus</a></td>
		<td>BRC</td>
		<td>Re-emerging</td>
		<td>overlap</td>
		<td>&nbsp;</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Staphylococcus"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Staphylococcus"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Staphylococcus"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Streptococcus"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Firmicutes</td> -->
<!-- 		<td>Bacilli</td> -->
		<td>Lactobacillales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Streptococcus") %>">Streptococcus</a></td>
		<td>BRC</td>
		<td>Re-emerging</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Streptococcus"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Streptococcus"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Streptococcus"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Clostridium"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
<!-- 		<td>Firmicutes</td> -->
		<td>Clostridia</td>
		<td>Clostridiales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Clostridium") %>">Clostridium</a></td>
		<td>BRC</td>
		<td>Category A+B, Re-emerging</td>
		<td>overlap</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Clostridium"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Clostridium"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Clostridium"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
	<%
	key.put("ncbi_taxon_id", ""+h.get("Borrelia"));
	counts = conn_summary.getGenomeCount(key);
	%>
	<tr>
		<td>Spirochaetes
			<br/><%=Spirochaetes.get("cnt_all") %> genomes
		</td>
		<td>Spirochaetia</td>
		<td>Spirochaetales</td>
		<td><a href="Taxon?cType=taxon&amp;cId=<%=h.get("Borrelia") %>">Borrelia</a></td>
		<td>&nbsp;</td>
		<td>Emerging</td>
		<td>&nbsp;</td>
		<td>Yes</td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Borrelia"), "") %>"><%=counts.get("cnt_all") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Borrelia"), "genome_status:Complete") %>"><%=counts.get("cnt_complete") %></a></td>
		<td><a href="<%=String.format(_linkGenomeList, h.get("Borrelia"), "genome_status:WGS") %>"><%=counts.get("cnt_wgs") %></a></td>
		<td><%=counts.get("cnt_plasmid") %></td>
	</tr>
</tbody>
</table>
