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

import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.ArrayExpressInterface;
import junit.framework.TestCase;

public class TestArrayExpressInterface extends TestCase {

	private boolean testmode = false;

	public void testGetResult() {
		if (testmode == true) {
			ArrayExpressInterface aei = new ArrayExpressInterface();
			JSONObject result = null;
			try {
				result = aei.getResults("Staphylococcus", "");
				// result = aei.getResults("Mycobacterium tuberculosis", "");
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println(result.toString());
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestArrayExpressInterface.class);

	}
}
