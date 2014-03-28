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
package edu.vt.vbi.patric.cache;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class ENewsGenerator {

	String sourceURL = "http://enews.patricbrc.org/php/rssAdapter.php";

	public void setSourceURL(String url) {
		sourceURL = url;
	}

	public boolean createCacheFile(String filePath) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(sourceURL);
		boolean isSuccess = false;

		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String strResponseBody = httpclient.execute(httpRequest, responseHandler);

			if (strResponseBody.length() > 0) {
				PrintWriter enews_out = new PrintWriter(new FileWriter(filePath));
				enews_out.println(strResponseBody);
				enews_out.close();
			}
			isSuccess = true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			httpclient.getConnectionManager().shutdown();
		}
		return isSuccess;
	}
}
