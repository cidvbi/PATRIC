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
package edu.vt.vbi.patric.identity.db;

import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.HibernateException;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.db.HibernateUserImpl;

public class HibernateUserModuleImpl extends org.jboss.portal.identity.db.HibernateUserModuleImpl {

	private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(HibernateUserModuleImpl.class);

	/**
	 * 
	 * @param realEmail
	 * @return the user
	 * @throws IdentityException
	 */
	public User findUserByUserEmail(String realEmail) throws IdentityException {
		if (realEmail != null) {
			try {
				Session session = super.getCurrentSession();
				Query query = session.createQuery("from HibernateUserImpl where realEmail=:realEmail");
				query.setParameter("realEmail", realEmail);
				query.setCacheable(true);
				HibernateUserImpl user = (HibernateUserImpl) query.uniqueResult();
				if (user == null) {
					throw new NoSuchUserException("No such user " + realEmail);
				}
				return user;
			}
			catch (HibernateException e) {
				String message = "Cannot find user by name " + realEmail;
				log.error(message, e);
				throw new IdentityException(message, e);
			}
		}
		else {
			throw new IllegalArgumentException("User Email cannot be null");
		}
	}

}
