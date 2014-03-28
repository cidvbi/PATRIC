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

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageBuilder {

	int w;

	int h;

	BufferedImage image;

	Graphics2D g2d;

	final String MAP_FILE_ROOT_DEV = "/home/oral/workspace/labs/patric-pathways/pathways";

	final String MAP_FILE_ROOT_TEST = "/opt/jboss-patric/jboss-deploy/deploy/jboss-web.deployer/ROOT.war/patric/images/pathways";

	final String MAP_FILE_ROOT_PROD = "/opt/jboss-home/jboss-patric/deploy/jboss-web.deployer/ROOT.war/patric/images/pathways";

	public ImageBuilder(String map_id) {

		try {

			if ((new File(MAP_FILE_ROOT_TEST + "/map" + map_id + ".png")).exists()) {

				image = ImageIO.read(new File(MAP_FILE_ROOT_TEST + "/map" + map_id + ".png"));
			}
			else if ((new File(MAP_FILE_ROOT_PROD + "/map" + map_id + ".png")).exists()) {

				image = ImageIO.read(new File(MAP_FILE_ROOT_PROD + "/map" + map_id + ".png"));
			}
			else if ((new File(MAP_FILE_ROOT_DEV + "/map" + map_id + ".png")).exists()) {

				image = ImageIO.read(new File(MAP_FILE_ROOT_DEV + "/map" + map_id + ".png"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		this.w = image.getWidth();
		this.h = image.getHeight();
	}

	public void drawonImage(String type, String text, int left, int top, int height, int width, String color) {

		g2d = (Graphics2D) image.createGraphics();

		String[] colors = color.split(",");
		g2d.setColor(new Color(Float.parseFloat(colors[0].trim()) / 255, Float.parseFloat(colors[1].trim()) / 255,
				Float.parseFloat(colors[2].trim()) / 255));

		if (type.equals("fill")) {
			g2d.fillRect(left, top, width, height);
		}
		else if (type.equals("text")) {
			g2d.setFont(new Font("Arial", Font.PLAIN, 9));
			g2d.drawString(text, left, top + 8);
		}

	}

	public byte[] getByteArray() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		byte[] bytesOut = baos.toByteArray();
		baos.close();
		return bytesOut;
	}

}
