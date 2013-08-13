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
	String genomedataurl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/patric-common/data/figfamData.json";
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
<style type="text/css" scoped>
	.history .bar-0 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-1 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-2 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-3 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-4 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-5 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-6 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-7 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-8 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-9 {
		fill: #9AC85C;
		stroke: #7AA93A; }
	.history .bar-10 {
		fill: #9AC85C;
		stroke: #7AA93A; }
</style>
<script type="text/javascript" src="/patric/js/libs/jquery.scrollTo-1.4.3-min.js"></script>
<script type="text/javascript" src="/patric/js/libs/waypoints.min.js"></script>
<script type="text/javascript" src="/patric/js/libs/d3.v3.min.js"></script>

<script type="text/javascript" src="/patric/js/figfam-charts-init.js"></script>
<script type="text/javascript" src="/patric/js/charts.js"></script>
<script type="text/javascript" src="/patric/js/landing-page-init.js"></script>
<%
JSONObject jsonTitle = (JSONObject)jsonData.get("proteinfamilies");
%>
<section class='sans-alternate workflow-title no-border letters-wider'>
	<div class='container'>
		<h1 class='upper down'><%=jsonTitle.get("title") %></h1>
		<% if(jsonTitle.get("content") != null) { %>
			<div class='large'><%=jsonTitle.get("content")%></div>
		<% } %>
	</div>
</section>
<div id='sticky-anchor'></div>
<div class='sticky'>
	<div class='container'>
		<ul class='no-decoration data-tab-toolbar upper large letters-wide'>
			<li class='left'><a class="active scrollTo" data-tab-target="tab1" style="background-image: url(/images/data-tab-icon-data.png)" href="javascript:void(0)">Data</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab2" style="background-image: url(/images/data-tab-icon-tools.png)" href="javascript:void(0)">Tools</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab3" style="background-image: url(/images/data-tab-icon-process.png)" href="javascript:void(0)">Process</a></li>
			<li class='left'><a class="scrollTo" data-tab-target="tab4" style="background-image: url(/images/data-tab-icon-download.png)" href="javascript:void(0)">Download</a></li>
		</ul>
	</div>
</div>
<div class="clear"></div>
<div class='container main-container'>
	<section class='main' role='main'>
		<div class='data-tab' id='tab1'>
			<div class='data-box collapse-bottom' id='figfam-header'>
				<h3 class='ribbon-title'><!-- FIGfam Title --></h3>
				<p class='desc'>Since this is a chart built by D3, this text should be replaced by relevant information from the data file.</p>
			</div><!-- /data-box -->
			<div class='far4x' id='figfam'>
				<div class='chart-wrapper'>
				<!-- This nav structure drives interactivity on the bar chart. -->
					<nav>
						<span class='label'>Scale</span>
						<ul class='scale'>
							<li class='real active'>Real Values</li>
							<li class='normalize'>Normalize</li>
						</ul>
						<span class='label'>Data Set</span>
						<ul class='dataset'>
							<li class='fvh active'>Functional vs. Hypothetical</li>
							<li class='cva'>Core vs. Accessory</li>
						</ul>
						<span class='label'>Order by</span>
						<ul class='sort'>
							<li class='index active'>Index</li>
							<li class='both'>Complete</li>
						</ul>
						<ul class='sort'>
							<li class='functional'>First Bar</li>
							<li class='hypothetical'>Second Bar</li>
						</ul>
					</nav>
					<!-- This is the div where the chart is actually drawn. -->
					<div class='chart'></div>
					<!-- The bar chart looks for this legend. There's some SVG in here for the circles. -->
					<p class='legend'>
						Legend:
						<svg height='24' width='24'>
							<circle class='bar1-sample' cx='12' cy='12' r='12'></circle>
						</svg>
						<span class='bar1-label'>Functional</span>
						<svg height='24' width='24'>
							<circle class='bar2-sample' cx='12' cy='12' r='12'></circle>
						</svg>
						<span class='bar2-label'>Hypothetical</span>
					</p>
				</div><!-- / chart-wrapper -->
			</div><!-- / figfam -->
			<div class='data-box popular-box tabbed hover-tabs no-underline-links'>
				<h3 class='ribbon-title'>Popular Genomes box<!-- Popular Genome Section Title -->
				</h3>
				<div class='group'>
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
						
						out.println("	<div class='data-box' id='history-tab" +(popCt+1) + "'>");
						out.println("		<h4>Chart Title Here</h4>");
						out.println("		<p></p>");
						out.println("		<div id='history'>");
						out.println("			<div class='chart'></div>");
						out.println("		</div>");
						out.println("	</div>");
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
				<p class='down'><a class="arrow-slate-e" href="#">View All Genomes</a></p>
			</div><!-- /data-box -->
		</div><!-- / data-tab -->
		<div class='data-tab' id='tab2'>
			<div class='data-box'>
				<% JSONObject jsonTools = (JSONObject) jsonData.get("tools"); %>
				<h3 class='ribbon-title'>
					<%=jsonTools.get("title")%>
				</h3>
				<%
					JSONArray tools = (JSONArray) jsonTools.get("tools");
					if((tools != null) && (tools.size() > 0))
					{
						out.println("<ul class='no-decoration inline tools-image-list'>");
						for(int toolct = 0; toolct < tools.size();toolct++)
						{
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
				<h3 class='ribbon-title'>
					<%=jsonDownload.get("title") %>
				</h3>
				<%
					if(jsonDownload.get("content") != null)
					{
						out.println(jsonDownload.get("content").toString());
					}
				%>
				<h3>This is a placeholder for Download Tools from jboss</h3>
			</div><!-- / data-box -->
		</div><!-- / data-tab -->
	</section>
</div>
<svg class='definitions'>
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
		<lineargradient id='bar-0' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(66,117,151)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(91,138,170)' stop-opacity='1'></stop>
		</lineargradient>
		<!-- Greenish -->
		<lineargradient id='bar-1' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(109,156,47)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(151,199,90)' stop-opacity='1'></stop>
		</lineargradient>
		<!-- Yellowish -->
		<lineargradient id='bar-2' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(246,218,98)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(251,232,153)' stop-opacity='1'></stop>
		</lineargradient>
		<!-- Blue-grayish -->
		<lineargradient id='bar-3' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(56,93,117)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(102,130,149)' stop-opacity='1'></stop>
		</lineargradient>
		<!-- Khaki -->
		<lineargradient id='bar-4' x1='0' x2='0' y1='0' y2='100%'>
			<stop offset='0' stop-color='rgb(230,218,174)' stop-opacity='1'></stop>
			<stop offset='100%' stop-color='rgb(206,192,142)' stop-opacity='1'></stop>
		</lineargradient>
	</defs>
</svg>