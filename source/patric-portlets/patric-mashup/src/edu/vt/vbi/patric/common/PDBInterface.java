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
package edu.vt.vbi.patric.common;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import edu.vt.vbi.patric.common.xmlHandler.PDBAnnotationsHandler;
import edu.vt.vbi.patric.common.xmlHandler.PDBAnnotationsResolver;
import edu.vt.vbi.patric.common.xmlHandler.PDBDescriptionHandler;
import edu.vt.vbi.patric.common.xmlHandler.PDBGOTermsHandler;
import edu.vt.vbi.patric.common.xmlHandler.PDBLigandHandler;
import edu.vt.vbi.patric.common.xmlHandler.PDBPolymersHandler;
import edu.vt.vbi.patric.common.xmlHandler.PDBSequenceClusterHandler;

public class PDBInterface {

	private String baseUrlDescription = "http://www.pdb.org/pdb/rest/describePDB";

	private String baseUrlLigand = "http://www.pdb.org/pdb/rest/ligandInfo";

	private String baseUrlGOTerm = "http://www.pdb.org/pdb/rest/goTerms";

	private String baseUrlCluster = "http://www.pdb.org/pdb/rest/sequenceCluster";

	private String baseUrlAnnotations = "http://www.pdb.org/pdb/rest/das/pdbchainfeatures/features";

	private String baseUrlPolymers = "http://www.pdb.org/pdb/rest/describeMol";

	private XMLReader xmlReader = null;

	public PDBInterface() {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			xmlReader = spf.newSAXParser().getXMLReader();
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
	}

	public HashMap<String, String> getDescription(String pdbIDs) throws java.rmi.RemoteException {
		PDBDescriptionHandler handler = new PDBDescriptionHandler();
		try {
			String url = baseUrlDescription + "?structureId=" + pdbIDs;
			System.out.println(url);
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			// ex.printStackTrace();
			System.out.println("no data available");
			return null;
		}
		return handler.getParsedData();
	}

	public ArrayList<HashMap<String, String>> getLigands(String pdbID) throws java.rmi.RemoteException {
		PDBLigandHandler handler = new PDBLigandHandler();
		try {
			String url = baseUrlLigand + "?structureId=" + pdbID;
			System.out.println(url);
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return handler.getParsedData();
	}

	public ArrayList<HashMap<String, String>> getGOTerms(String pdbID) throws java.rmi.RemoteException {
		PDBGOTermsHandler handler = new PDBGOTermsHandler();
		try {
			String url = baseUrlGOTerm + "?structureId=" + pdbID;
			System.out.println(url);
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return handler.getParsedData();
	}

	public ArrayList<HashMap<String, String>> getSequenceCluster(String pdbID, int cluster)
			throws java.rmi.RemoteException {
		PDBSequenceClusterHandler handler = new PDBSequenceClusterHandler();
		try {
			String url = baseUrlCluster + "?structureId=" + pdbID + "&cluster=" + cluster;
			System.out.println(url);
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return handler.getParsedData();
	}

	public ArrayList<HashMap<String, String>> getAnnotations(String pdbID) throws java.rmi.RemoteException {
		PDBAnnotationsHandler handler = new PDBAnnotationsHandler();
		PDBAnnotationsResolver resolver = new PDBAnnotationsResolver();
		try {
			String url = baseUrlAnnotations + "?segment=" + pdbID;
			System.out.println(url);

			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);

			xmlReader.setContentHandler(handler);
			xmlReader.setEntityResolver(resolver);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return handler.getParsedData();
	}

	public ArrayList<String> getPolymers(String pdbID) throws java.rmi.RemoteException {
		PDBPolymersHandler handler = new PDBPolymersHandler();
		try {
			String url = baseUrlPolymers + "?structureId=" + pdbID;
			System.out.println(url);
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setConnectTimeout(EutilInterface.TIMEOUT_CONN);
			c.setReadTimeout(EutilInterface.TIMEOUT_READ);
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(c.getInputStream()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return handler.getParsedData();
	}
}
