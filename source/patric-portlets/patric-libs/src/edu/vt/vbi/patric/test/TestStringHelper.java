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

import junit.framework.TestCase;
import edu.vt.vbi.patric.common.StringHelper;

public class TestStringHelper extends TestCase {

	private boolean testmode = false;

	public void testGetResult() {
		if (testmode == true) {
			String keyword = "\"outbreak\" or epidemic Or bore oR tailor and brucellosis and (disease_f:\"Homo sapiens\") OR rt_0008 OR rt0008 OR Rt-0008 AND (gid:\"111\") AND (feature_type_f:\"CDS\" OR feature_type_f:\"gene\") AND (rast_cds:[1 TO *])";
			System.out.println(StringHelper.parseSolrKeywordOperator(keyword));
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestStringHelper.class);
	}
}
