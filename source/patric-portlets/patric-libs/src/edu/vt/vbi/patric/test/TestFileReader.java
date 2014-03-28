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

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONObject;

import edu.vt.vbi.patric.common.ExpressionDataFileReader;

public class TestFileReader extends TestCase {

	private boolean testmode = false;

	@SuppressWarnings("unchecked")
	public void testGetResult() throws InvalidFormatException, IOException {

		if (testmode) {
			JSONObject config = new JSONObject();
			ExpressionDataFileReader reader;

			config.put("sampleFilePresent", "false");
			config.put("sampleURL", "http://dev.patricbrc.org/patric/testfiles/test_samples.txt");
			config.put("sampleFileType", "txt");
			config.put("dataURL", "http://dev.patricbrc.org/patric/testfiles/test_from_patric_svg.xls");
			config.put("dataFileType", "xls");
			config.put("dataFileFormat", "matrix");
			config.put("dataFileOrientation", "svg");

			System.out.println(config.toString());

			reader = new ExpressionDataFileReader(config);

			if (reader.doRead()) {

				reader.calculateExpStats();

				JSONObject a = reader.get("sample");
				reader.writeData("samples");
				System.out.println(a.toJSONString());
			}
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestFileReader.class);

	}
}
