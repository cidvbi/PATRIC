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
import edu.vt.vbi.patric.common.PSICQUICInterface;
import junit.framework.TestCase;

public class TestPSICQUICInterface extends TestCase {

	boolean testmode = false;

	public void testGetResult() {
		if (testmode == true) {
			PSICQUICInterface eui = new PSICQUICInterface();
			try {
				String count = eui.getCounts("intact", "species:2");
				System.out.println("count=" + count);
				JSONObject result = eui.getResults("intact", "species:63363", 0, 5);
				System.out.println(result.toString());
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestPSICQUICInterface.class);

	}
}
