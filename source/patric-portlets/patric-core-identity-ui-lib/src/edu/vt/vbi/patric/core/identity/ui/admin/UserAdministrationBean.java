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
package edu.vt.vbi.patric.core.identity.ui.admin;

/**
 * Extends org.jboss.portal.core.identity.ui.admin.UserAdministrationBean.
 * It was intended to delete Alfresco account when JBoss account is deleted, but now we moved to polyomic system.
 * Currently, we don't do anything, since user deletion can be triggered by JBoss admin interface only.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * 
 */
public class UserAdministrationBean extends org.jboss.portal.core.identity.ui.admin.UserAdministrationBean {

	public String confirmedDelete() {

		String rtn = super.confirmedDelete();
		// will delete manually for now, since user deletion can be triggered by JBoss admin interface only.

		return rtn;
	}
}
