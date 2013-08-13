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

import java.util.*;
import java.util.zip.*;
import java.io.*;

import edu.vt.vbi.patric.dao.ResultType;

public class CreateZip {

	public static void main(String args[]) throws IOException {

	}

	public byte[] ZipIt(ArrayList<ResultType> items, String[] algorithm, String[] filetype) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		BufferedInputStream bis;

		String[] files = new String[1000];
		File file;

		int bytesRead;

		byte[] buffer = new byte[1024];

		int j = 0;

		for (int i = 0; i < items.size(); i++) {

			ResultType g = (ResultType) items.get(i);

			String folder = g.get("genomeNames");

			file = new File("/storage/brcdownloads/patric2/genomes/" + folder);

			if (!file.exists()) {

				System.err.println("Skipping Folder: " + "/storage/brcdownloads/patric2/genomes/" + folder);
				continue;

			}
			else {

				for (int k = 0; k < algorithm.length; k++) {
					String temp = algorithm[k];

					for (int m = 0; m < filetype.length; m++) {
						if (filetype[m].equalsIgnoreCase(".fna")) {
							algorithm[k] = "";
						}
						else {
							algorithm[k] = temp;
						}

						file = new File("/storage/brcdownloads/patric2/genomes/" + folder + "/" + folder + algorithm[k]
								+ filetype[m]);

						if (!file.exists()) {
							System.err.println("Skipping File: " + "/storage/brcdownloads/patric2/genomes/" + folder
									+ "/" + folder + algorithm[k] + filetype[m]);
						}
						else {
							files[j] = "/storage/brcdownloads/patric2/genomes/" + folder + "/" + folder + algorithm[k]
									+ filetype[m];
							j++;
						}

					}

				}

			}

		}

		if (j > 0) {
			for (int i = 0; i < j; i++) {
				file = new File(files[i]);
				bis = new BufferedInputStream(new FileInputStream(file));
				ZipEntry entry = new ZipEntry(file.getName());
				zos.putNextEntry(entry);
				while ((bytesRead = bis.read(buffer)) != -1) {
					zos.write(buffer, 0, bytesRead);
				}
				bis.close();
				zos.closeEntry();
			}
			zos.close();
		}

		return baos.toByteArray();
	}

}
