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
package edu.vt.vbi.patric.core.identity.ui.common;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
//import org.jboss.portal.identity.UserModule;
import edu.vt.vbi.patric.identity.UserModule;

/**
 * JBoss retrieve(actually, resets) password by username, but we want to do it by email, since it would be easier to remember than username.
 * 
 * @author Harry Yoo (hyun@vbi.vt.edu)
 * 
 */
public class IdentityUserBean extends org.jboss.portal.core.identity.ui.common.IdentityUserBean {

	public User findUserByUserEmail(String userEmail) throws IllegalArgumentException, NoSuchUserException,
			IdentityException {
		UserModule userModule = (UserModule) super.getUserModule();
		return userModule.findUserByUserEmail(userEmail);
	}

}
