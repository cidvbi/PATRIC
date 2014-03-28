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
package edu.vt.vbi.patric.core.controller.command.mapper;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;

/**
 * This class was intended to invalidate Alfresco ticket when user sign out.
 * To use this class, you need to point this as a managed bean in jboss-portal-ha.sar/META-INF/jboss-service.xml
 * <mbean code="edu.vt.vbi.patric.core.controller.command.mapper.SignOutCommandFactoryService"
 * 
 * After we migrate Alfresco to polyomic system, this method is no longer used.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * 
 */
public class SignOutCommandFactoryService extends
		org.jboss.portal.core.controller.command.mapper.SignOutCommandFactoryService {
	public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host,
			String contextPath, String requestPath) {
		// call original method
		return super.doMapping(controllerContext, invocation, host, contextPath, requestPath);
	}
}
