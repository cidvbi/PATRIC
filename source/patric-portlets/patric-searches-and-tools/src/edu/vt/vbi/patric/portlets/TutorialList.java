/*******************************************************************************
 * Copyright 2014 Virginia Polytechnic Institute and State University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.vt.vbi.patric.portlets;

import java.io.*;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.net.URL;

public class TutorialList extends GenericPortlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException,
			UnavailableException {
		response.setContentType("text/html");
		response.setTitle("Tutorial List");

		PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/tutorial_list.jsp");
		prd.include(request, response);
	}
	
	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		
		JSONObject jsonResult = new JSONObject();
		
		URL jsonURL = new URL("http://enews.patricbrc.org/tutorial_landing/tutorial-landing-page-1-3/?req=passphrase");
		
		InputStream dh = null;
		int cp;	
		
		StringBuilder json_builder = new StringBuilder();
		try 
		{
			dh	= jsonURL.openStream();
			while ((cp = dh.read()) != -1)
			{
				json_builder.append((char) cp);
			}
			try {
				jsonResult.put("tutorials", (JSONObject) new JSONParser().parse(json_builder.toString()));
			}catch(Exception e){
			}
		} 
		finally
		{
			if(dh != null){
				dh.close();
			}
		}
		
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.write(jsonResult.toString());
		writer.close();
	}
}
