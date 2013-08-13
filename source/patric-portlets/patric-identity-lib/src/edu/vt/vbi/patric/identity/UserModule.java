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
package edu.vt.vbi.patric.identity;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;

public interface UserModule extends org.jboss.portal.identity.UserModule {

	/**
	 * Retrieve a user by its email.
	 * @param userEmail
	 * @return the user
	 * @throws IdentityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchUserException
	 */
	User findUserByUserEmail(String userEmail) throws IdentityException, IllegalArgumentException, NoSuchUserException;
}
