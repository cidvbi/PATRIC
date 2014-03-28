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
package edu.vt.vbi.patric.proteinfamily;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ResourceResponse;

public class PngWriter {
	public static void returnPng(String pngPath, ResourceResponse resp) {
		try {
			File f = new File(pngPath);
			if (f.exists() && f.isFile()) {
				resp.setContentType("image/png");
				FileInputStream fis = new FileInputStream(f);

				PrintWriter out = resp.getWriter();

				int i = 0;
				while ((i = fis.read()) != -1) {
					out.write(i);
				}
				fis.close();
				out.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}
