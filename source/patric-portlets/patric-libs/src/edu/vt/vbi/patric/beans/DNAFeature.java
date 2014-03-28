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
package edu.vt.vbi.patric.beans;

import org.apache.solr.client.solrj.beans.Field;

public class DNAFeature {
	// na_feature_id,locus_tag,start_max,end_min,strand,feature_type,product,gene,refseq_locus_tag
	@Field
	public long na_feature_id;

	@Field
	public String locus_tag;

	@Field("start_max")
	public int start;

	@Field("end_min")
	public int end;

	@Field
	public String strand;

	@Field
	public String feature_type;

	@Field
	public String product;

	@Field
	public String gene;

	@Field
	public String refseq_locus_tag;
}
