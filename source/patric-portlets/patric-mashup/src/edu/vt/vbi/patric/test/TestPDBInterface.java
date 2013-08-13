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
package edu.vt.vbi.patric.test;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import edu.vt.vbi.patric.common.PDBInterface;

public class TestPDBInterface extends TestCase {

	private boolean testmode = false;

	public void testGetResult() {
		if (testmode == true) {
			PDBInterface api = new PDBInterface();
			ArrayList<HashMap<String, String>> result = null;
			// HashMap<String,String> result = null;
			// ArrayList<String> result = null;

			try {
				// result = api.getLigands("4hhb");
				// result = api.getDescription("3GOA"); // return hashmap
				// result = api.getGOTerms("4hhb");
				// result = api.getSequenceCluster("4hhb.A", 40);
				result = api.getAnnotations("1A8R.B");
				// result = api.getPolymers("3op9"); // return arraylist<string>
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			// System.out.println("total:"+result.size());
			System.out.println(result.toString());
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestPDBInterface.class);

	}
}
