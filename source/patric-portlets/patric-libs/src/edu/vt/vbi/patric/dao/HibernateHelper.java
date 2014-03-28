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
package edu.vt.vbi.patric.dao;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Basic Hibernate helper class, handles SessionFactory, Session and Transaction. You can also use a static initializer for the initial SessionFactory
 * creation and holds Session and Transactions in thread local variables. All exceptions are wrapped in an unchecked InfrastructureException.
 * 
 */
public class HibernateHelper {

	private static Log log = LogFactory.getLog(HibernateHelper.class);

	private static HashMap<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();

	public static final ThreadLocal<HashMap<String, Session>> sessionMapsThreadLocal = new ThreadLocal<HashMap<String, Session>>();

	public static Session currentSession(String key) throws HibernateException {

		HashMap<String, Session> sessionMaps = sessionMapsThreadLocal.get();

		if (sessionMaps == null) {
			sessionMaps = new HashMap<String, Session>();
			sessionMapsThreadLocal.set(sessionMaps);
		}

		// Open a new Session, if this Thread has none yet
		Session s = sessionMaps.get(key);
		if (s == null) {
			s = sessionFactoryMap.get(key).openSession();
			sessionMaps.put(key, s);
		}

		return s;
	}

	public static SessionFactory getSessionFactory(String k) throws HibernateException {
		return (sessionFactoryMap.get(k));
	}

	public static Session currentSession() throws HibernateException {
		return currentSession("");
	}

	public static void closeSessions() throws HibernateException {
		HashMap<String, Session> sessionMaps = sessionMapsThreadLocal.get();
		sessionMapsThreadLocal.set(null);
		if (sessionMaps != null) {
			for (Session session : sessionMaps.values()) {
				if (session.isOpen())
					session.close();
			}
			;
		}
	}

	public static void closeSession() {
		HashMap<String, Session> sessionMaps = sessionMapsThreadLocal.get();
		sessionMapsThreadLocal.set(null);
		if (sessionMaps != null) {
			Session session = sessionMaps.get("");
			if (session != null && session.isOpen())
				session.close();
		}
	}

	public static void buildSessionFactory(String key, String path) {
		try {
			// Create the SessionFactory
			SessionFactory sessionFactory = new Configuration().configure(path).buildSessionFactory();
			sessionFactoryMap.put(key, sessionFactory);
		}
		catch (Throwable ex) {

			log.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);

		} // end of the try - catch block
	}

	public static void closeSession(String key) {
		HashMap<String, Session> sessionMaps = sessionMapsThreadLocal.get();
		if (sessionMaps != null) {
			Session session = sessionMaps.get(key);
			if (session != null && session.isOpen())
				session.close();
		}
	}

} // end of the class
