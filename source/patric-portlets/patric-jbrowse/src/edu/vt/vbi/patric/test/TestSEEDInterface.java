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
package edu.vt.vbi.patric.test;

import java.io.BufferedReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.theseed.servers.SAPserver;

public class TestSEEDInterface extends TestCase {

	private boolean testmode = false;

	public void testGetResult() {
		if (testmode == true) {
			String _feature = "fig|1038844.4.peg.1";

			SAPserver sapling = new SAPserver("http://servers.nmpdr.org/pseed/sapling/server.cgi");

			// CRResultSet crRS = new CRResultSet(_feature, sapling.compared_regions(_feature, 5, 10000));
			// System.out.println(crRS.toString());

			BufferedReader br = sapling.compared_regions(_feature, 5, 10000);
			String out;
			try {
				while ((out = br.readLine()) != null) {
					System.out.println(out);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestSEEDInterface.class);

	}
}
