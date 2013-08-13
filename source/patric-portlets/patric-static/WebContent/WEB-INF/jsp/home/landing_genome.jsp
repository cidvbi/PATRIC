<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.parser.ParseException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.apache.http.client.ResponseHandler" %>
<%@ page import="org.apache.http.client.methods.HttpGet" %>
<%@ page import="org.apache.http.impl.client.BasicResponseHandler" %>
<%@ page import="org.apache.http.impl.client.DefaultHttpClient" %>
<%
	String genomedataurl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/patric-common/data/genomeData.json";
	DefaultHttpClient httpclient = new DefaultHttpClient();
	HttpGet httpRequest = new HttpGet(genomedataurl);
	JSONObject jsonData = null;
	try {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String strResponseBody = httpclient.execute(httpRequest, responseHandler);
		
		//System.out.println("parsing:"+strResponseBody);
		
		JSONParser parser = new JSONParser();
		jsonData = (JSONObject) parser.parse(strResponseBody);
		
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParseException e) {
		e.printStackTrace();
	} finally {
		httpclient.getConnectionManager().shutdown();
	}
	
%>
<style>
.top-5 .bar-0 {fill: url(#bar-0); }
.top-5 .bar-1 {fill: url(#bar-1); }
.top-5 .bar-2 {fill: url(#bar-2); }
.top-5 .bar-3 {fill: url(#bar-3); }
.top-5 .bar-4 {fill: url(#bar-4); }
.top-5 .sawtooth {fill: #f4f4f4; }
</style>

<script type="text/javascript" src="/patric/js/libs/d3.v3.min.js"></script>
<script type="text/javascript" src="/patric/js/libs/jquery.scrollTo-1.4.3-min.js"></script>
<script type="text/javascript" src="/patric/js/libs/waypoints.min.js"></script>

<script type="text/javascript" src="/patric/js/genome-charts-init.js"></script>
<script type="text/javascript" src="/patric/js/charts.js"></script>
<script type="text/javascript" src="/patric/js/landing-page-init.js"></script>
<%
JSONObject jsonGenomes = (JSONObject)jsonData.get("genomes");
%>
<section class='sans-alternate workflow-title no-border letters-wider'>
	<div class='container'>
		<h1 class='upper down'><%=jsonGenomes.get("title") %></h1>
		<% if(jsonGenomes.get("content") != null) { %>
			<div class='large'><%=jsonGenomes.get("content")%></div>
		<% } %>
	</div>
</section>
<div id='sticky-anchor'></div>
<div class='sticky'>
	<div class='container'>
		<ul class='no-decoration data-tab-toolbar upper large letters-wide'>
			<li class='left'><a class="active scrollTo" data-tab-target="tab1" style="background-image: url(/patric/images/data-tab-icon-data.png)" href="javascript:void(0)">Data</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab2" style="background-image: url(/patric/images/data-tab-icon-tools.png)" href="javascript:void(0)">Tools</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab3" style="background-image: url(/patric/images/data-tab-icon-process.png)" href="javascript:void(0)">Process</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab4" style="background-image: url(/patric/images/data-tab-icon-download.png)" href="javascript:void(0)">Download</a></li>
		</ul>
	</div>
</div>
<div class="clear"></div>
<div class='container main-container'>
	<section class='main' role='main'>
		<div class='data-tab' id='tab1'>
			<% JSONObject jsonTopline = (JSONObject)jsonData.get("topline"); %>
			<h3><%=jsonTopline.get("title") %></h3>
			<% if(jsonTopline.get("content") != null) { %>
				<div class='large'><%=jsonTopline.get("content")%></div>
			<% } %>
			<div class='group'>
			<!-- Genome status chart (stacked bar) -->
				<div class='col span-8 append-1'>
					<div class='data-box' id='genomeStatus-header'>
						<h3 class='ribbon-title'><!-- Genome Status Title --></h3>
						<div id='genomeStatus'>
							<div class='chart'></div>
						</div><!-- / genomeStatus -->
					</div><!-- data-box -->
				</div><!-- / span-8 -->
				<!-- Number of genomes (Two-value line chart ) -->
				<div class='col span-13 append-1'>
					<div class='data-box' id='numberGenomes-header'>
						<h3 class='ribbon-title'><!-- Genome Number Title --></h3>
						<div id='numberGenomes'>
							<div class='chart'></div>
						</div><!-- / numberGenomes -->
					</div><!-- / data-box -->
				</div><!-- / span-13 -->
				<!-- Three tabs with a vertical bar chart. -->
				<div class='col span-13 last'>
					<div class='tabbed'>
					<!-- Tab controls are identified seperately. -->
						<ul class='tab-headers no-decoration inline no-underline-links'>
							<li><a href="#chart-tab1">Tab Title 1</a></li>
							<li><a href="#chart-tab2">Tab Title 2</a></li>
						</ul>
					<!-- First tab contents -->
						<div class='data-box' id='chart-tab1'>
							<h4>Chart Title Here</h4>
							<p class='desc'>Very brief sentence</p>
								<div class='top-5'>
									<div class='chart'></div>
								</div><!-- / top-5 -->
						</div><!-- / data-box -->
					<!-- Second tab contents -->
						<div class='data-box' id='chart-tab2'>
							<h4>Chart Title Here</h4>
							<p class='desc'>Very brief sentence</p>
							<div class='top-5'>
								<div class='chart'></div>
							</div><!-- / top-5 -->
						</div><!-- / data-box -->
					<!-- Third tab contents -->
					</div><!-- / tabbed -->
				</div><!-- / span-13 -->
			</div><!-- / group -->
			<div class='data-box popular-box tabbed hover-tabs no-underline-links'>
				<h3 class='ribbon-title'>Popular Genomes<!-- Popular Genomes Title--></h3>
				<div class='group'>
				<!-- 
				genome-data groups are the panels in this control. 
				There's one for each genome listed in the ul below.
				Furthermore, each panel is divided into two colums.
				This structure repeats.
				-->
					<%
						JSONArray popularItems = (JSONArray) ((JSONObject)jsonData.get("popularGenomes")).get("popularList");
						for(int popCt = 0; popCt < popularItems.size(); popCt++)
						{
							//JSONObject pop = popularItems.getJSONObject(popCt);
							JSONObject pop = (JSONObject)popularItems.get(popCt);
							out.println("<div class='genome-data right half group' id='genome-tab" + (popCt+1) + "'>");
							JSONArray popularData = new JSONArray();
							//popularData = pop.getJSONArray("popularData");
							popularData = (JSONArray)pop.get("popularData");
							out.println("<div class='column'>");
							for(int left = 0; left < 2; left++)
							{
								//JSONObject leftitem = popularData.getJSONObject(left);
								JSONObject leftitem = (JSONObject)popularData.get(left);
								out.println("<a class='genome-data-item clear' href='" + leftitem.get("link") + "'>");
								out.println("<div class='genome-data-icon left' style='background-image: url(" + leftitem.get("picture") + ")'></div>");
								out.println("<h3 class='down2x close highlight-e'>" + leftitem.get("data") + "</h3>");
								out.println("<p class='small'>" + leftitem.get("description") + "</p>");
								out.println("</a>");
							}
							out.println("</div>");
							out.println("<div class='column'>");
							for(int right = 0; right < 2; right++)
							{
								//JSONObject rightitem = popularData.getJSONObject(right+2);
								JSONObject rightitem = (JSONObject)popularData.get(right+2);
								out.println("<a class='genome-data-item clear' href='" + rightitem.get("link") + "'>");
								out.println("<div class='genome-data-icon left' style='background-image: url(" + rightitem.get("picture") + ")'></div>");
								out.println("<h3 class='down2x close highlight-e'>" + rightitem.get("data") + "</h3>");
								out.println("<p class='small'>" + rightitem.get("description") + "</p>");
								out.println("</a>");
							}
							out.println("</div>");
							out.println("<p><a class='arrow-slate-e' href='" + pop.get("link") + "'>View this genome in PATRIC</a></p>");
							out.println("</div>"); 	
						}
						out.println("<ul class='no-decoration genome-list tab-headers third'>");
						for(int nameCt = 0; nameCt < popularItems.size(); nameCt++)
						{
							//JSONObject name = popularItems.getJSONObject(nameCt);
							JSONObject name = (JSONObject)popularItems.get(nameCt);
							out.println("<li>");
							out.println("<a data-genome-href='" + name.get("link") + "' class='genome-link' href='#genome-tab" + (nameCt+1) + "'>" + name.get("popularName") + "</a>");
							out.println("<div class='arrow'></div>");
							out.println("</li>");
						}
						out.println("</ul>");	
					%>
				</div><!-- / group -->
				<p class='down'><a class="arrow-slate-e" href="/portal/portal/patric/GenomeList?cType=taxon&cId=2&dataSource=&displayMode=&pk=">View All Genomes</a></p>
			</div><!-- /data-box -->
		</div><!-- / data-tab -->
		<div class='data-tab' id='tab2'>
			<div class='data-box'>
				<% JSONObject jsonTools = (JSONObject) jsonData.get("tools"); %>
				<h3 class='ribbon-title'><!-- Tools Section Title -->
					<%=jsonTools.get("title")%>
				</h3>
				<%
					JSONArray tools = (JSONArray) jsonTools.get("tools");
					if((tools != null) && (tools.size() > 0))
					{
						out.println("<ul class='no-decoration inline tools-image-list'>");
						for(int toolct = 0; toolct < tools.size();toolct++)
						{
							//JSONObject tool = tools.getJSONObject(toolct);
							JSONObject tool = (JSONObject)tools.get(toolct);
							out.println("<li>");
							out.println("<a href='" + tool.get("url") +"'>");
							out.println("<img alt='" + tool.get("title") + "' src='" + tool.get("image") + "' />");
							out.println("<div class='overlay'>");
							out.println("<p>" + tool.get("title") + "</p>");
							out.println("</div>");
							out.println("</a>");
							out.println("</li>");
						}
						out.println("</ul>");
					}
					if(jsonTools.get("content") != null)
					{
						out.println(jsonTools.get("content").toString());
					}
					JSONArray workflows = (JSONArray)jsonTools.get("workflows");
					if((workflows != null ) && (workflows.size() > 0))
					{
						for(int wfct = 0; wfct < workflows.size();wfct++)
						{
							//JSONObject wf = workflows.getJSONObject(wfct);
							JSONObject wf = (JSONObject)workflows.get(wfct);
							out.println("<h3 class='no-underline-links close'><a href='" + wf.get("url") + "'>" + wf.get("title") + "</a></h3>");
							out.println("<p>" + wf.get("summary") + "</p>");
						}
					}
				%>
			</div><!-- / data-box -->
		</div><!-- / data-tab -->
		<div class='data-tab' id='tab3'>
			<div class='data-box'>
				<% JSONObject jsonProcess = (JSONObject) jsonData.get("process"); %>
				<h3 class='ribbon-title'>
					<%=jsonProcess.get("title") %>
				</h3>
				<%
					out.println(jsonProcess.get("content").toString());
					if(jsonProcess.get("image") != null)
					{
						out.println("<img alt='' src='" + jsonProcess.get("image").toString() + "' />");
					}
				%>
			</div><!-- / data-box -->
		</div><!-- / data-tab -->
		<div class='data-tab' id='tab4'>
			<div class='data-box'>
				<% JSONObject jsonDownload = (JSONObject) jsonData.get("download"); %>
				<h3 class='ribbon-title'><!-- Download Section Title -->
					<%=jsonDownload.get("title") %>
				</h3>
				<% if(jsonDownload.get("content") != null) { %>
					<%=jsonDownload.get("content") %>
				<% } %>
				<h3>This is a placeholder for Download Tools from jboss</h3>
			</div><!-- / data-box -->
		</div><!-- / data-tab -->
	</section>
</div>
<svg class='definitions' >
	<defs>
		<!-- Gradients used for FigFam bars -->
		<lineargradient id='grad1' x1='0' x2='0' y1='100%' y2='0'>
			<stop offset='0' stop-color='rgb(149,0,0)' stop-opacity='1'></stop>
			<stop offset='.6' stop-color='rgb(149,7,50)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(255,180,0)' stop-opacity='1'></stop>
		</lineargradient>
		<lineargradient id='grad2' x1='0' x2='0' y1='100%' y2='0'>
			<stop offset='0' stop-color='rgb(3,87,182)'></stop>
			<stop offset='100%' stop-color='rgb(3,180,255)'></stop>
		</lineargradient>
		<!-- Hatch pattern for FigFam bars. -->
		<pattern height='8' id='tile1' patternUnits='userSpaceOnUse' width='8' x='0' y='0'>
			<rect class='function' height='8' width='8' x='0' y='0'></rect>
			<line class='function' x1='0' x2='8' y1='0' y2='8'></line>
		</pattern>
		<!-- Gradient defs for the top5 bar charts -->
		<!-- Bluish -->
		<linearGradient id='bar-0' x1='0' x2='0' y1='0%' y2='100%'>
			<stop offset='0%' stop-color='rgb(66,117,151)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(91,138,170)' stop-opacity='1'></stop>
		</linearGradient>
		<!-- Greenish -->
		<linearGradient id='bar-1' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(109,156,47)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(151,199,90)' stop-opacity='1'></stop>
		</linearGradient>
		<!-- Yellowish -->
		<linearGradient id='bar-2' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(246,218,98)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(251,232,153)' stop-opacity='1'></stop>
		</linearGradient>
		<!-- Blue-grayish -->
		<linearGradient id='bar-3' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(56,93,117)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(102,130,149)' stop-opacity='1'></stop>
		</linearGradient>
		<!-- Khaki -->
		<linearGradient id='bar-4' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(230,218,174)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(206,192,142)' stop-opacity='1'></stop>
		</linearGradient>
	</defs>
</svg>