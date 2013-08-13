/*******************************************************************************
 * Copyright 2013 Virginia Polytechnic Institute and State University
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

import java.io.PrintWriter;

import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;

/**
 * Implementation of a decoration renderer, based on div tags.
 * 
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 10479 $, $LastChangedDate: 2008-04-02 07:50:58 -0400 (Wed, 02 Apr 2008) $
 * @see org.jboss.portal.theme.render.renderer.DecorationRenderer
 */
/**
 * Modified to simplify and delegate layout to CSS.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 *
 */
public class DivDecorationRenderer extends AbstractObjectRenderer implements DecorationRenderer {

	public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException {
		// PrintWriter markup = rendererContext.getWriter();

		renderTitle(rendererContext, drc);

		// markup.print("<div class=\"portlet-mode-container\">");
		// renderTriggerableActions(rendererContext, drc, ActionRendererContext.MODES_KEY);
		// renderTriggerableActions(rendererContext, drc, ActionRendererContext.WINDOWSTATES_KEY);
		// markup.print("</div>");
	}

	private static void renderTitle(RendererContext ctx, DecorationRendererContext drc) {
		PrintWriter out = ctx.getWriter();
		// out.print("<div class=\"portlet-titlebar-decoration\"></div>");
		// out.print("<span class=\"portlet-titlebar-title\">");
		out.print(drc.getTitle());
	}

}
