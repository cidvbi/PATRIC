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
package edu.vt.vbi.patric.core.identity.ui.actions;

import javax.faces.event.ActionEvent;

import org.jboss.portal.core.identity.ui.IdentityUIUser;

/**
 * Extends org.jboss.portal.core.identity.ui.actions.CreateUserAction.
 * To use this class, you need to point this as a managed bean class in deploy/jboss-portal-ha.sar/portal-identity.sar/portal-identity.war/WEB-INF/faces-config.xml
 * 
 * To build this class, you need libraries like below,
 * deploy/jboss-portal-ha.sar/portal-identity.sar/portal-identity.war/WEB-INF/lib portal-core-identity-ui-lib.jar
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * 
 */
public class CreateUserAction extends org.jboss.portal.core.identity.ui.actions.CreateUserAction {
	private boolean debug_on = false;

	private IdentityUIUser uiUser;

	public void register(ActionEvent ev) {

		uiUser = super.getUiUser();
		super.register(ev);
		super.setUiUser(uiUser);

		if (debug_on) {
			System.out.println("*** edu.vt.vbi.patric.core.identity.ui.actions.CreateUserAction is running ");
		}
		// We does not need to override this class, since we do not use alfresco anymore
	}
}
