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

		String[] files = new String[10000];

		File file;

		int bytesRead;

		byte[] buffer = new byte[1024];

		int j = 0;
		boolean flag;

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
					for (int m = 0; m < filetype.length; m++) {

						flag = false;

						file = new File("/storage/brcdownloads/patric2/genomes/" + folder + "/" + folder
								+ (filetype[m].equals(".fna") ? "" : algorithm[k]) + filetype[m]);

						if (!file.exists()) {
							System.err.println("Skipping File: " + file.getAbsolutePath());
						}
						else {
							// System.out.println("File: " + file.getAbsolutePath());
							for (int z = 0; z < files.length; z++) {
								if (files[z] != null && files[z].equals(file.getAbsolutePath())) {
									flag = true;
									break;
								}
							}
							// System.out.println(flag);
							if (!flag) {
								files[j] = file.getAbsolutePath();
								j++;
							}
						}
					}
				}
			}
		}
		// System.out.println(j);
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
