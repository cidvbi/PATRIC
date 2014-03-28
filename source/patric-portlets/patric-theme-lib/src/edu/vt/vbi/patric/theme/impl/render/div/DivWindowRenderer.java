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
package edu.vt.vbi.patric.theme.impl.render.div;

import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.io.PrintWriter;

/**
 * Implementation of a WindowRenderer, based on div tags.
 * 
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
/**
 * Modified to simplify and delegate layout to CSS.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 *
 */
public class DivWindowRenderer extends AbstractObjectRenderer implements WindowRenderer {
	public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException {
		PrintWriter out = rendererContext.getWriter();
		// out.print("<div class=\"portlet-container\">");

		out.print("<h3 class=\"section-title normal-case close2x\">");
		rendererContext.render(wrc.getDecoration());
		out.print("</h3>");

		// out.print("<div class=\"portlet-content-center\">");
		rendererContext.render(wrc.getPortlet());
		// out.print("</div>");
		// out.print("</div>");

		// removed table structure design for decoration purpose and delegated layout to css
		/*
		 * out.print("<table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
		 * 
		 * out.print("<tr><td class=\"portlet-titlebar-left\"></td>");
		 * out.print("<td class=\"portlet-titlebar-center\">"); rendererContext.render(wrc.getDecoration());
		 * out.print("</td><td class=\"portlet-titlebar-right\"></td></tr>");
		 * 
		 * out.print("<tr><td class=\"portlet-content-left\"></td>");
		 * out.print("<td class=\"portlet-body\"><div class=\"portlet-content-center\">");
		 * rendererContext.render(wrc.getPortlet());
		 * out.print("</div></td><td class=\"portlet-content-right\"></td></tr>");
		 * 
		 * out.print("<tr><td class=\"portlet-footer-left\"></td>");
		 * out.print("<td class=\"portlet-footer-center\"></td>");
		 * out.print("<td class=\"portlet-footer-right\"></td></tr>"); 
		 * 
		 * out.print("</table></div>");
		 */
	}
}
