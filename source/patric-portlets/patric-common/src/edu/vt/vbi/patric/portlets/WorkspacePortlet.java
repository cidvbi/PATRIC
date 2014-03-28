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
package edu.vt.vbi.patric.portlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.vt.vbi.patric.common.PolyomicHandler;
import edu.vt.vbi.patric.common.SolrInterface;
import edu.vt.vbi.patric.common.UIPreference;
import edu.vt.vbi.patric.common.Workspace;
import edu.vt.vbi.patric.dao.ResultType;

public class WorkspacePortlet extends GenericPortlet {

	@Override
	protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException, UnavailableException {
		// do nothing
	}

	public void enumerateParameters(ResourceRequest request) {
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String k = params.nextElement();
			System.out.println(k + ":" + request.getParameterValues(k)[0]);
		}
	}

	private PolyomicHandler getPolyomicHandler(ResourceRequest request) {

		PolyomicHandler polyomic = new PolyomicHandler();
		PortletSession p_session = request.getPortletSession(true);
		String token = (String) p_session.getAttribute("PolyomicAuthToken", PortletSession.APPLICATION_SCOPE);
		Long defaultWId = (Long) p_session.getAttribute("DefaultWorkspaceID", PortletSession.APPLICATION_SCOPE);
		// System.out.println("getWorkspace::token="+token+","+"defaultWId="+defaultWId);

		if (token == null) {
			String userName = request.getUserPrincipal().getName();
			polyomic.authenticate(userName);
			p_session.setAttribute("PolyomicAuthToken", polyomic.getAuthenticationToken(), PortletSession.APPLICATION_SCOPE);
		}
		else {
			polyomic.setAuthenticationToken(token);
		}
		if (defaultWId == null) {
			polyomic.retrieveDefaultWorkspace();
			defaultWId = polyomic.getDefaultWorkspaceID();
			p_session.setAttribute("DefaultWorkspaceID", defaultWId, PortletSession.APPLICATION_SCOPE);
		}
		else {
			polyomic.setDefaultWorkspaceID(defaultWId);
		}

		return polyomic;
	}

	private Workspace getValidWorkspace(ResourceRequest request) {
		PolyomicHandler polyomic = new PolyomicHandler();
		PortletSession p_session = request.getPortletSession(true);
		Workspace ws_from_session = (Workspace) p_session.getAttribute("workspace", PortletSession.APPLICATION_SCOPE);
		Workspace ws = null;

		if (request.getUserPrincipal() == null) {
			if (ws_from_session != null) {
				ws = ws_from_session;
			}
			else {
				ws = new Workspace();
				saveWorkspace(request, ws);
			}
		}
		else {
			// polyomic or session.
			String userName = request.getUserPrincipal().getName();
			String token = (String) p_session.getAttribute("PolyomicAuthToken", PortletSession.APPLICATION_SCOPE);
			Long defaultWId = (Long) p_session.getAttribute("DefaultWorkspaceID", PortletSession.APPLICATION_SCOPE);
			if (token == null) {
				polyomic.authenticate(userName);
				p_session.setAttribute("PolyomicAuthToken", polyomic.getAuthenticationToken(), PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setAuthenticationToken(token);
			}
			if (defaultWId == null) {
				polyomic.retrieveDefaultWorkspace();
				defaultWId = polyomic.getDefaultWorkspaceID();
				p_session.setAttribute("DefaultWorkspaceID", defaultWId, PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setDefaultWorkspaceID(defaultWId);
			}
			ws = polyomic.getWorkspaceData(defaultWId);
		}
		return ws;
	}

	private UIPreference getValidUIPreference(ResourceRequest request) {
		PolyomicHandler polyomic = new PolyomicHandler();
		PortletSession p_session = request.getPortletSession(true);
		UIPreference uiPref_from_session = (UIPreference) p_session.getAttribute("preference", PortletSession.APPLICATION_SCOPE);
		UIPreference uiPref = null;

		if (request.getUserPrincipal() == null) {
			if (uiPref_from_session != null) {
				uiPref = uiPref_from_session;
			}
			else {
				uiPref = new UIPreference();
				saveUIPreference(request, uiPref);
			}
		}
		else {
			String userName = request.getUserPrincipal().getName();
			String token = (String) p_session.getAttribute("PolyomicAuthToken", PortletSession.APPLICATION_SCOPE);
			Long defaultWId = (Long) p_session.getAttribute("DefaultWorkspaceID", PortletSession.APPLICATION_SCOPE);
			if (token == null) {
				polyomic.authenticate(userName);
				p_session.setAttribute("PolyomicAuthToken", polyomic.getAuthenticationToken(), PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setAuthenticationToken(token);
			}
			if (defaultWId == null) {
				polyomic.retrieveDefaultWorkspace();
				defaultWId = polyomic.getDefaultWorkspaceID();
				p_session.setAttribute("DefaultWorkspaceID", defaultWId, PortletSession.APPLICATION_SCOPE);
			}
			else {
				polyomic.setDefaultWorkspaceID(defaultWId);
			}
			uiPref = polyomic.getUIPreference(defaultWId);
		}
		return uiPref;
	}

	public void saveWorkspace(ResourceRequest request, Workspace ws) {
		if (request.getUserPrincipal() != null) {
			PolyomicHandler polyomic = getPolyomicHandler(request);
			Long defaultWId = polyomic.getDefaultWorkspaceID();
			polyomic.setWorkspaceData(defaultWId, ws);

			PortletSession p_session = request.getPortletSession(true);
			p_session.setAttribute("workspace", ws, PortletSession.APPLICATION_SCOPE);
		}
		else {
			PortletSession p_session = request.getPortletSession(true);
			p_session.setAttribute("workspace", ws, PortletSession.APPLICATION_SCOPE);
		}
	}

	public void saveUIPreference(ResourceRequest request, UIPreference uiPref) {
		if (request.getUserPrincipal() != null) {
			PolyomicHandler polyomic = getPolyomicHandler(request);
			Long defaultWId = polyomic.getDefaultWorkspaceID();
			polyomic.setUIPreference(defaultWId, uiPref);

			PortletSession p_session = request.getPortletSession(true);
			p_session.setAttribute("preference", uiPref, PortletSession.APPLICATION_SCOPE);
		}
		else {
			PortletSession p_session = request.getPortletSession(true);
			p_session.setAttribute("preference", uiPref, PortletSession.APPLICATION_SCOPE);
		}
	}

	@SuppressWarnings("unchecked")
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		String action_type = request.getParameter("action_type");
		String action = request.getParameter("action");

		if (action_type != null && action != null) {
			// read workspace from session
			Workspace ws = getValidWorkspace(request);
			// System.out.println(ws.getWorkspace().toJSONString());

			if (action_type.equals("groupAction")) {
				if (action.equals("create")) {
					// this.enumerateParameters(request);
					String grp_name = request.getParameter("group_name");
					String grp_desc = request.getParameter("group_desc");
					String grp_type = request.getParameter("group_type");
					String tracks = request.getParameter("tracks");
					String str_tags = request.getParameter("tags");
					String fid = request.getParameter("fid"); // this is a legacy parameter, but used by GSE
					String grp_element = request.getParameter("group_element");

					if (grp_name == null || grp_name.equals("")) {
						grp_name = "(default)";
					}
					if (grp_desc == null) {
						grp_desc = "";
					}
					if (grp_type == null) {
						grp_type = "Feature";
					}
					if (tracks == null && fid != null) { // exception handling for GSE
						tracks = fid;
					}
					// Speical expception. if group is created from feature level, but user wanted to stop as Genome group,
					// convert feature IDs to Genome IDs
					// System.out.println("grp_type:"+grp_type+", grp_element:"+grp_element);
					if (grp_type.equals("Feature") && (grp_element != null && grp_element.equals("Genome"))
							&& (tracks != null && tracks.equals("") == false)) {

						SolrInterface solr = new SolrInterface();
						solr.setCurrentInstance("GenomicFeature");
						JSONObject f = solr.queryFacet("na_feature_id:(" + tracks.replaceAll(",", " OR ") + ")", "gid");
						JSONArray GIDs = (JSONArray) f.get("facet");
						String gid = "";
						if (GIDs.size() > 0) {
							for (Object g : GIDs) {
								JSONObject genome = (JSONObject) g;
								if (gid.equals("") == false) {
									gid += ",";
								}
								gid += genome.get("value");
							}
							tracks = gid;
							grp_type = "Genome";
						}
					}

					int tagId = ws.findTag("Group", grp_name, grp_type);
					if (tagId >= 0) {
						// add members to existing group
						if (tracks != null) {
							if (tracks.contains(",")) { // multiple entries
								HashSet<Integer> trackIds = ws.addTracks(grp_type, tracks);
								// add mappings
								for (int trackId : trackIds) {
									if (ws.isMappingExist(tagId, trackId) == false) {
										ws.addMapping(tagId, trackId);
									}
								}
								// add tags
								if (str_tags != null && !str_tags.equals("")) {
									ws.addTagging(str_tags, trackIds);
								}
								// update group info: member count, date
								ws.updateGroupTag(tagId, null, null);
							}
							else {
								int trackId = -1;
								try {
									Long internalId = Long.parseLong(tracks);
									trackId = ws.addTrack(grp_type, internalId);
								}
								catch (NumberFormatException nfe) {
									trackId = ws.addTrack(grp_type, tracks);
								}
								// add mapping
								if (ws.isMappingExist(tagId, trackId) == false) {
									ws.addMapping(tagId, trackId);
								}
								// add tags
								if (str_tags != null) {
									HashSet<Integer> trackIds = new HashSet<Integer>();
									trackIds.add(trackId);
									ws.addTagging(str_tags, trackIds);
								}
								ws.updateGroupTag(tagId, null, null);
							}
						}
					}
					else {
						// create a new entry
						if (tracks != null) {
							HashSet<Integer> trackIds = null;
							if (tracks.contains(",")) {
								trackIds = ws.addTracks(grp_type, tracks);
							}
							else {
								int trackId = -1;
								try {
									Long internalId = Long.parseLong(tracks);
									trackId = ws.addTrack(grp_type, internalId);
								}
								catch (NumberFormatException nfe) {
									trackId = ws.addTrack(grp_type, tracks);
								}
								trackIds = new HashSet<Integer>();
								trackIds.add(trackId);
							}
							// create tags
							int member_count = trackIds.size();
							tagId = ws.addGroup(grp_name, grp_type, grp_desc, member_count, null);

							// add mappings
							ws.addMapping(tagId, trackIds);

							// add tags
							if (str_tags != null) {
								ws.addTagging(str_tags, trackIds);
							}
						}
						else {
							// ERROR
							this.enumerateParameters(request);
						}
					}

					saveWorkspace(request, ws);
				}
				else if (action.equals("removeGroup")) {
					String paramIdList = request.getParameter("idList");
					HashSet<Integer> tagIds = null;
					if (paramIdList != null && !paramIdList.equals("")) {
						tagIds = new HashSet<Integer>();
						if (paramIdList.contains(",")) {
							for (String id : paramIdList.split(",")) {
								tagIds.add(Integer.parseInt(id));
							}
						}
						else {
							tagIds.add(Integer.parseInt(paramIdList));
						}
					}
					// find associated tracks
					ArrayList<JSONObject> mapping = null;
					ArrayList<Integer> trackIds = new ArrayList<Integer>();
					for (int tagId : tagIds) {
						mapping = ws.findMappingByTagId(tagId);
						for (JSONObject track : mapping) {
							trackIds.add(Integer.parseInt(track.get("trackId").toString()));
						}
					}
					// remove group tag & mapping
					ws.removeTags(tagIds);
					ws.removeMapping(tagIds, null);

					// remove associated tracks
					for (int trackId : trackIds) {
						HashSet<Integer> thisTrack = new HashSet<Integer>();
						thisTrack.add(trackId);
						// if the track has group association
						if (ws.hasAssociation("Group", trackId)) {
							// do not delete track, but mapping (done)
						}
						else if (ws.hasAssociation("String", trackId)) { // if the track has string tag association only
							// delete tag association
							// find tags;
							ArrayList<JSONObject> maps = ws.findMappingByTrackId(trackId);
							HashSet<Integer> strTagIds = new HashSet<Integer>();
							for (JSONObject map : maps) {
								strTagIds.add(Integer.parseInt(map.get("tagId").toString()));
							}
							ws.removeMapping(strTagIds, thisTrack);
							// if there is not other feature that associated the
							// tags, then delete the tag
							for (int tagId : strTagIds) {
								if (ws.countAssociation(tagId) == 0) {
									ws.removeTag(tagId);
								}
							}
							// delete track
							ws.removeTracks(thisTrack);
							// delete mapping (done)
						}
						else {
							ws.removeTracks(thisTrack);
							ws.removeMapping(null, thisTrack);
						}
					}

					saveWorkspace(request, ws);
				}
				else if (action.equals("removeTrack")) {
					String paramRemoveFrom = request.getParameter("removeFrom");
					String paramGroups = request.getParameter("groups");
					String paramIdType = request.getParameter("idType");
					String paramIdList = request.getParameter("idList");
					HashSet<Integer> groups = null;
					HashSet<Object> internalIds = null;

					if (paramGroups != null && !paramGroups.equals("")) {
						groups = new HashSet<Integer>();
						if (paramGroups.contains(",")) {
							for (String id : paramGroups.split(",")) {
								groups.add(Integer.parseInt(id));
							}
						}
						else {
							groups.add(Integer.parseInt(paramGroups));
						}
					}

					if (paramIdList != null && !paramIdList.equals("")) {
						internalIds = new HashSet<Object>();
						if (paramIdList.contains(",")) {
							for (String id : paramIdList.split(",")) {
								try {
									internalIds.add(Long.parseLong(id));
								}
								catch (NumberFormatException nfe) {
									internalIds.add(id);
								}
							}
						}
						else {
							try {
								internalIds.add(Long.parseLong(paramIdList));
							}
							catch (NumberFormatException nfe) {
								internalIds.add(paramIdList);
							}
						}
					}

					HashSet<Integer> trackIds = ws.findTracks(paramIdType, internalIds);

					if (paramRemoveFrom != null && paramRemoveFrom.equals("workspace")) {
						// need to update associated groups. collect groups
						// first.
						if (groups == null) {
							groups = new HashSet<Integer>();
						}
						for (int trackId : trackIds) {
							ArrayList<JSONObject> maps = ws.findMappingByTrackId(trackId);
							for (JSONObject map : maps) {
								groups.add(Integer.parseInt(map.get("tagId").toString()));
							}
						}

						ws.removeTracks(trackIds);
						ws.removeMapping(null, trackIds);

						// update group info
						for (Integer tagId : groups) {
							ws.updateGroupTag(tagId, null, null);
						}
					}
					else if (paramRemoveFrom != null && paramRemoveFrom.equals("groups")) {
						// check if track is associated to other groups/tags
						ws.removeMapping(groups, trackIds);

						// update group info
						for (Integer tagId : groups) {
							ws.updateGroupTag(tagId, null, null);
						}

						for (int trackId : trackIds) {
							HashSet<Integer> thisTrack = new HashSet<Integer>();
							thisTrack.add(trackId);

							// if the track has group association
							if (ws.hasAssociation("Group", trackId)) {
								// do not delete track, but mapping (done)
							}
							else if (ws.hasAssociation("String", trackId)) { // if the track has string tag association only
								// delete tag association
								// find tags;
								ArrayList<JSONObject> maps = ws.findMappingByTrackId(trackId);
								HashSet<Integer> tagIds = new HashSet<Integer>();
								for (JSONObject map : maps) {
									tagIds.add(Integer.parseInt(map.get("tagId").toString()));
								}
								ws.removeMapping(tagIds, thisTrack);
								// if there is not other feature that associated
								// the tags, then delete the tag
								for (int tagId : tagIds) {
									if (ws.countAssociation(tagId) == 0) {
										ws.removeTag(tagId);
									}
								}
								// delete track
								ws.removeTracks(thisTrack);
								// delete mapping (done)
							}
							else {
								ws.removeTracks(thisTrack);
								ws.removeMapping(null, thisTrack);
							}
						}
					}

					saveWorkspace(request, ws);
				}
				else if (action.equals("updateGroupInfo")) {
					String groupInfo = request.getParameter("group_info");

					JSONObject new_group = null;
					JSONParser parser = new JSONParser();

					try {
						new_group = (JSONObject) parser.parse(groupInfo);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}

					String new_group_name = null, new_group_desc = null;

					if (new_group.containsKey("name")) {
						new_group_name = new_group.get("name").toString();
					}
					if (new_group.containsKey("desc")) {
						new_group_desc = new_group.get("desc").toString();
					}

					ws.updateGroupTag(Integer.parseInt(new_group.get("tagId").toString()), new_group_name, new_group_desc);
					saveWorkspace(request, ws);
				}
				else if (action.equals("updateExperimentInfo")) {
					String strExpUpdated = request.getParameter("experiment_info");
					String collectionId = null;

					JSONObject jsonExpUpdated = null;
					JSONParser parser = new JSONParser();

					try {
						jsonExpUpdated = (JSONObject) parser.parse(strExpUpdated);
						collectionId = jsonExpUpdated.get("expid").toString();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}

					PolyomicHandler polyomic = getPolyomicHandler(request);
					JSONObject jsonExpOrig = polyomic.getCollection(collectionId, "experiment");

					if (jsonExpOrig.containsKey("title") && jsonExpUpdated.containsKey("title")) {
						jsonExpOrig.put("title", jsonExpUpdated.get("title").toString());
					}
					if (jsonExpOrig.containsKey("desc") && jsonExpUpdated.containsKey("desc")) {
						jsonExpOrig.put("desc", jsonExpUpdated.get("desc").toString());
					}
					if (jsonExpOrig.containsKey("organism") && jsonExpUpdated.containsKey("organism")) {
						jsonExpOrig.put("organism", jsonExpUpdated.get("organism").toString());
					}
					if (jsonExpOrig.containsKey("pmid") && jsonExpUpdated.containsKey("pmid")) {
						jsonExpOrig.put("pmid", jsonExpUpdated.get("pmid").toString());
					}
					if (jsonExpUpdated.containsKey("data_type")) {
						jsonExpOrig.put("data_type", jsonExpUpdated.get("data_type").toString());
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String timestamp = sdf.format(Calendar.getInstance().getTime());
					jsonExpOrig.put("mdate", timestamp);

					polyomic.saveJSONtoCollection(collectionId, "experiment.json", jsonExpOrig, PolyomicHandler.CONTENT_EXPERIMENT);
					polyomic.refreshWorkspaceCollection(collectionId);
				}

				response.setContentType("application/json");
				response.getWriter().write("{'success':true}");
				response.getWriter().close();
			}
			else if (action_type.equals("WSSupport")) {
				if (action.equals("getLibrary")) {
					JSONArray library = new JSONArray();
					JSONObject data = new JSONObject();
					data.put("id", 1);
					data.put("name", "OWNED BY ME");
					data.put("expanded", true);
					data.put("children", ws.getLibraryByDataType());
					library.add(data);

					response.setContentType("application/json");
					library.writeJSONString(response.getWriter());
					response.getWriter().close();
				}
				else if (action.equals("getGroups")) {
					JSONObject rtn = new JSONObject();
					JSONArray groups = ws.getGroups();

					rtn.put("success", true);
					rtn.put("results", groups);

					response.setContentType("application/json");
					rtn.writeJSONString(response.getWriter());
					response.getWriter().close();
				}
				else if (action.equals("getFacets")) {
					JSONObject rtn = new JSONObject();
					JSONArray tags = ws.getTags();

					rtn.put("success", true);
					rtn.put("results", tags);

					response.setContentType("application/json");
					rtn.writeJSONString(response.getWriter());
					response.getWriter().close();
				}
				else if (action.equals("getTracks")) {
					JSONObject rtn = new JSONObject();
					JSONArray tags = ws.getTags();

					rtn.put("success", true);
					rtn.put("results", tags);

					response.setContentType("application/json");
					rtn.writeJSONString(response.getWriter());
					response.getWriter().close();
				}
				else if (action.equals("getMappings")) {
					JSONArray mapping = ws.getMapping();

					response.setContentType("application/json");
					mapping.writeJSONString(response.getWriter());
					response.getWriter().close();
				}
				else if (action.equals("getGenomes")) {
					String trackIds = request.getParameter("trackIds");
					JSONObject filter = new JSONObject();
					filter.put("key", "trackId");
					filter.put("value", trackIds);
					JSONArray tracks = ws.getTracks(filter);

					HashMap<String, Object> key = new HashMap<String, Object>();
					key.put("tracks", tracks);
					key.put("startParam", request.getParameter("start"));
					key.put("limitParam", request.getParameter("limit"));
					key.put("sortParam", request.getParameter("sort"));

					SolrInterface solr = new SolrInterface();
					JSONObject res = solr.getGenomesByID(key);

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					writer.close();
				}
				else if (action.equals("getFeatures")) {
					String trackIds = request.getParameter("trackIds");
					JSONObject filter = new JSONObject();
					filter.put("key", "trackId");
					filter.put("value", trackIds);
					JSONArray tracks = null;

					tracks = ws.getTracks(filter);

					HashMap<String, Object> key = new HashMap<String, Object>();
					key.put("tracks", tracks);
					key.put("startParam", request.getParameter("start"));
					key.put("limitParam", request.getParameter("limit"));
					key.put("sortParam", request.getParameter("sort"));

					SolrInterface solr = new SolrInterface();
					JSONObject res = solr.getFeaturesByID(key);

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					// writer.write(res.toString());
					writer.close();
				}
				else if (action.equals("getPublicExperiments")) {
					JSONArray collectionIDs = null;
					DefaultHttpClient httpclient = new DefaultHttpClient();
					String hostname = System.getProperty("java.rmi.server.hostname");
					String url = "http://" + hostname + "/patric/static/publicworkspace.json";
					System.out.println(url);
					HttpGet httpRequest = new HttpGet(url);
					try {
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						String strResponseBody = httpclient.execute(httpRequest, responseHandler);

						JSONParser parser = new JSONParser();
						collectionIDs = (JSONArray) parser.parse(strResponseBody);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					catch (ParseException e) {
						e.printStackTrace();
					}
					finally {
						httpclient.getConnectionManager().shutdown();
					}

					/*
					 * ArrayList<String> collectionIds = new ArrayList<String>(); collectionIds.add("502fd1d4-37f5-4e6c-99b9-a721d65d3558");
					 * collectionIds.add("d368a37e-e378-4d12-b5e3-ecc1370dc023"); collectionIds.add("79dfe766-c28c-4272-bb40-6846be48a753");
					 * collectionIds.add("c6cc8806-39f5-401a-a8aa-1a8215384a45");
					 */
					JSONArray results = new JSONArray();

					PolyomicHandler polyomic = new PolyomicHandler();
					polyomic.setAuthenticationToken("");

					// for (String cId : collectionIds) {
					for (Object cId : collectionIDs) {
						JSONObject collection = polyomic.getCollection(cId.toString(), "experiment");
						if (collection != null) {
							collection.put("source", "Public");

							results.add(collection);
						}
					}

					JSONObject res = new JSONObject();
					int totalUser = results.size();

					res.put("total", totalUser);
					res.put("results", results);

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					// writer.write(res.toString());
					writer.close();
				}
				else if (action.equals("getPublicSamples")) {
					String expId = request.getParameter("expid");
					// String expSource = request.getParameter("expsource");
					String strSampleIds = request.getParameter("sampleIds");
					JSONObject res = new JSONObject();
					List<String> sampleIds = null;
					if (strSampleIds != null) {
						sampleIds = Arrays.asList(strSampleIds.split(","));
					}

					PolyomicHandler polyomic = new PolyomicHandler();
					polyomic.setAuthenticationToken("");
					JSONArray samples = polyomic.getSamples(expId, sampleIds);
					res.put("total", samples.size());
					res.put("results", samples);

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					// writer.write(res.toString());
					writer.close();
				}
				else if (action.equals("getExperiments")) {
					String trackIds = request.getParameter("trackIds");
					JSONObject filter = new JSONObject();
					filter.put("key", "trackId");
					filter.put("value", trackIds);
					JSONArray tracksMixed = ws.getTracks(filter);
					JSONArray tracksPATRIC = new JSONArray();

					ArrayList<String> collectionIds = new ArrayList<String>();

					for (Object tr : tracksMixed) {
						JSONObject jsonTrk = (JSONObject) tr;

						try {
							Integer.parseInt(jsonTrk.get("internalId").toString());
							tracksPATRIC.add(jsonTrk);
						}
						catch (NumberFormatException nfe) {
							collectionIds.add(jsonTrk.get("internalId").toString());
						}
					}

					// reading USER Experiments
					PolyomicHandler polyomic = null;
					JSONObject resUser = null;
					if (request.getUserPrincipal() != null) {
						polyomic = getPolyomicHandler(request);
						resUser = polyomic.getExperiments(collectionIds);
					}

					// reading PATRIC Experiments
					HashMap<String, Object> key = new HashMap<String, Object>();
					key.put("tracks", tracksPATRIC);
					key.put("startParam", request.getParameter("start"));
					key.put("limitParam", request.getParameter("limit"));
					if (request.getParameter("sort") != null
							&& (request.getParameter("sort").contains("\"property\":\"source\"") || request.getParameter("sort").contains(
									"\"property\":\"organism\""))) {
						// solr does not support sorting on multi-valued fields
						// source fields does not exist in solr config
						key.put("sortParam", null);
					}
					else {
						key.put("sortParam", request.getParameter("sort"));
					}
					// System.out.println("key:"+key.toString());
					SolrInterface solr = new SolrInterface();
					JSONObject resPATRIC = solr.getExperimentsByID(key);

					// merging
					JSONObject res = new JSONObject();
					int totalPATRIC = 0;
					if (resPATRIC.containsKey("total")) {
						totalPATRIC = Integer.parseInt(resPATRIC.get("total").toString());
					}
					int totalUser = 0;
					if (resUser != null && resUser.containsKey("total")) {
						totalUser = Integer.parseInt(resUser.get("total").toString());
					}
					JSONArray results = new JSONArray();
					if (resPATRIC.containsKey("results")) {
						for (Object exp : (JSONArray) resPATRIC.get("results")) {
							JSONObject jsonExp = (JSONObject) exp;
							jsonExp.put("source", "PATRIC");
							results.add(jsonExp);
						}
					}
					if (resUser != null && resUser.containsKey("results")) {
						for (Object exp : (JSONArray) resUser.get("results")) {
							JSONObject jsonExp = (JSONObject) exp;
							jsonExp.put("source", "me");
							results.add(jsonExp);
						}
					}

					res.put("total", totalPATRIC + totalUser);
					res.put("results", results);

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					// writer.write(res.toString());
					writer.close();
				}
				else if (action.equals("getSamples")) {
					String expId = request.getParameter("expid");
					String expSource = request.getParameter("expsource");
					String strSampleIds = request.getParameter("sampleIds");
					JSONObject res = new JSONObject();
					List<String> sampleIds = null;
					if (strSampleIds != null) {
						sampleIds = Arrays.asList(strSampleIds.split(","));
					}

					if (expSource.equals("User")) {
						PolyomicHandler polyomic = getPolyomicHandler(request);
						JSONArray samples = polyomic.getSamples(expId, sampleIds);
						res.put("total", samples.size());
						res.put("results", samples);
					}
					else if (expSource.equals("PATRIC")) {
						SolrInterface solr = new SolrInterface();
						solr.setCurrentInstance("GENEXP_Experiment");
						ResultType rtKey = new ResultType();
						rtKey.put("keyword", "expid:" + expId);
						JSONObject object = solr.getData(rtKey, null, null, 0, 10000, false, false, false);

						JSONObject obj = (JSONObject) object.get("response");
						JSONArray obj1 = (JSONArray) obj.get("docs");
						String solrId = "";

						for (Object ob : obj1) {
							JSONObject doc = (JSONObject) ob;
							if (solrId.length() == 0) {
								solrId += doc.get("eid").toString();
							}
							else {
								solrId += "," + doc.get("eid").toString();
							}
						}

						ResultType key = new ResultType();
						key.put("keyword", "eid:" + solrId);
						solr.setCurrentInstance("GENEXP_Sample");
						JSONObject samples = solr.getData(key, null, null, 0, 10000, false, false, false);
						obj = (JSONObject) samples.get("response");

						JSONArray rtnDocs = new JSONArray();
						for (Object ob : (JSONArray) obj.get("docs")) {
							JSONObject tuple = (JSONObject) ob;
							tuple.put("source", "PATRIC");
							rtnDocs.add(tuple);
						}

						res.put("total", obj.get("numFound"));
						res.put("results", rtnDocs);
					}

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					// writer.write(res.toString());
					res.writeJSONString(writer);
					writer.close();
				}
				else if (action.equals("getToken")) {
					String token = null;

					if (request.getUserPrincipal() != null) {
						PolyomicHandler polyomic = getPolyomicHandler(request);
						token = polyomic.getAuthenticationToken();
					}
					else {
						token = "";
					}
					response.setContentType("text/plain");
					PrintWriter writer = response.getWriter();
					writer.write(token);
					writer.close();
				}
				else if (action.equals("inlinestatus")) {
					String userInfo_prefix = "<a href=\"/portal/portal/patric/GroupManagement?mode=\">";
					String userInfo_postfix = "</a>";

					String status = "<span id=\"numItems\">";

					try {
						int countTracks = ws.getTrackCount();
						int countGroups = ws.getGroupCount();

						if (countTracks == 0) {
							status += "No Items, ";
						}
						else if (countTracks > 1) {
							status += countTracks + " Items, ";
						}
						else {
							status += "1 Item, ";
						}
						if (countGroups == 0) {
							status += "No Groups";
						}
						else if (countGroups > 1) {
							status += countGroups + " Groups";
						}
						else {
							status += "1 Group";
						}
					}
					catch (Exception ex) {
						status += ex.getMessage();
					}
					status += "</span>";

					response.setContentType("text/plain");
					PrintWriter writer = response.getWriter();
					writer.write(userInfo_prefix + status + userInfo_postfix);
					writer.close();
				}
				else if (action.equals("getGenomeGroupList")) {
					JSONArray res = new JSONArray();
					JSONObject grp = null;

					JSONObject filters = new JSONObject();
					filters.put("key", "type");
					filters.put("value", "Genome");
					JSONArray groups = ws.getGroups(filters);
					for (int id = 0; id < groups.size(); id++) {
						JSONObject group = (JSONObject) groups.get(id);
						grp = new JSONObject();
						grp.put("id", Integer.toString(id));
						grp.put("name", group.get("name").toString());
						grp.put("leaf", false);
						grp.put("collapsed", true);
						// get genomes associted in this group
						int tagId = Integer.parseInt(group.get("tagId").toString());
						ArrayList<JSONObject> members = ws.findMappingByTagId(tagId);

						HashSet<Integer> trackIds = new HashSet<Integer>();
						for (JSONObject member : members) {
							trackIds.add(Integer.parseInt(member.get("trackId").toString()));
						}

						JSONArray tracks = ws.getTracks(trackIds);

						HashMap<String, Object> key = new HashMap<String, Object>();
						key.put("tracks", tracks);

						SolrInterface solr = new SolrInterface();
						JSONObject solr_res = solr.getGenomesByID(key);
						JSONArray genomes = (JSONArray) solr_res.get("results");

						JSONArray children = new JSONArray();
						if (genomes != null) {
							for (Object genome : genomes) {
								JSONObject jsonGenome = (JSONObject) genome;
								JSONObject resGenome = new JSONObject();
								resGenome.put("id", Integer.toString(id) + "_" + Integer.parseInt(jsonGenome.get("genome_info_id").toString()));
								resGenome.put("parentID", Integer.toString(id));
								resGenome.put("name", jsonGenome.get("genome_name").toString());
								resGenome.put("leaf", true);
								resGenome.put("genome_info_id", jsonGenome.get("genome_info_id"));
								resGenome.put("ncbi_taxon_id", Integer.parseInt(jsonGenome.get("ncbi_tax_id").toString()));

								children.add(resGenome);
							}
						}
						grp.put("children", children);
						res.add(grp);
					}

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					writer.close();
				}
				else if (action.equals("getGroupList")) {
					String grp_type = request.getParameter("group_type");
					JSONArray res = new JSONArray();
					JSONObject grp = null;
					JSONObject filters = new JSONObject();
					filters.put("key", "type");
					filters.put("value", grp_type);
					JSONArray groups = ws.getGroups(filters);

					for (int id = 0; id < groups.size(); id++) {
						JSONObject group = (JSONObject) groups.get(id);
						grp = new JSONObject();

						grp.put("name", group.get("name").toString());
						grp.put("description", group.get("desc").toString());

						ArrayList<JSONObject> mapping_trks = ws.findMappingByTagId(Integer.parseInt(group.get("tagId").toString()));
						ArrayList<JSONObject> mapping_tags = null;
						HashSet<Integer> tagIDs = new HashSet<Integer>();

						for (JSONObject track : mapping_trks) {
							mapping_tags = ws.findMappingByTrackId(Integer.parseInt(track.get("trackId").toString()));

							for (JSONObject tag : mapping_tags) {
								tagIDs.add(Integer.parseInt(tag.get("tagId").toString()));
							}
						}
						String strTags = "";
						for (int tagId : tagIDs) {
							JSONObject t = ws.findTagByTagId(tagId);
							if (t.get("tagType").equals("String")) {
								if (strTags.length() > 0) {
									strTags += ",";
								}
								strTags += t.get("name").toString();
							}
						}
						grp.put("tags", strTags);
						res.add(grp);
					}

					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					res.writeJSONString(writer);
					writer.close();
				}
				// for debugging purpose
				else if (action.equals("status")) {
					response.setContentType("application/json");
					PrintWriter writer = response.getWriter();
					ws.getWorkspace().writeJSONString(writer);
					writer.close();
				}
				else {
					response.setContentType("application/json");
					response.getWriter().write("sorry");
					response.getWriter().close();
				}
			}
			else if (action_type.equals("GSESupport")) {
				if (action.equals("group_list")) {
					// PersistentCartGroup group = null;
					JSONArray groups = ws.getGroups();
					StringBuffer output = new StringBuffer();

					output.append("<group_set>\n");
					for (int i = 0; i < groups.size(); i++) {
						JSONObject group = (JSONObject) groups.get(i);

						output.append("\t<group>\n");
						output.append("\t\t<idx>" + group.get("tagId").toString() + "</idx>\n");
						output.append("\t\t<name>" + group.get("name").toString() + "</name>\n");
						output.append("\t</group>\n");
					}
					output.append("</group_set>");

					response.setContentType("text/xml");
					response.getWriter().write(output.toString());
					response.getWriter().close();
				}
				else if (action.equals("groups")) {
					String strTagIds = request.getParameter("groupIds");
					JSONObject filter = new JSONObject();
					filter.put("key", "tagId");
					filter.put("value", strTagIds);

					JSONArray groups = ws.getGroups(filter);
					JSONObject group = null;
					StringBuffer o = new StringBuffer();

					o.append("<group_set>\n");

					for (int i = 0; i < groups.size(); i++) {
						group = (JSONObject) groups.get(i);

						o.append("\t<group>\n");
						o.append("\t\t<name>" + group.get("name") + "</name>\n");
						o.append("\t\t<description>" + ((group.get("desc") != null) ? group.get("desc") : "") + "</description>\n");
						o.append("\t\t<members>\n");

						ArrayList<JSONObject> members = ws.findMappingByTagId(Integer.parseInt(group.get("tagId").toString()));

						HashSet<Integer> trackIds = new HashSet<Integer>();
						for (JSONObject member : members) {
							trackIds.add(Integer.parseInt(member.get("trackId").toString()));
						}
						JSONArray tracks = ws.getTracks(trackIds);
						for (int m = 0; m < tracks.size(); m++) {
							JSONObject member = (JSONObject) tracks.get(m);
							o.append(member.get("internalId").toString() + "\n");
						}

						o.append("\t\t</members>\n");
						o.append("\t</group>\n");
					}
					o.append("</group_set>");

					//
					response.setContentType("text/xml");
					response.getWriter().write(o.toString());
					response.getWriter().close();
				}
				else if (action.equals("items")) {

					HashSet<Integer> trackIds = new HashSet<Integer>();
					String strTagIds = request.getParameter("groupIds");
					String groupType = "";
					String _tagId = null;

					if (strTagIds.contains(",")) {
						for (String tagId : strTagIds.split(",")) {
							ArrayList<JSONObject> mappings = ws.findMappingByTagId(Integer.parseInt(tagId));
							for (JSONObject mapping : mappings) {
								trackIds.add(Integer.parseInt(mapping.get("trackId").toString()));
							}
							_tagId = tagId;
						}
					}
					else {
						ArrayList<JSONObject> mappings = ws.findMappingByTagId(Integer.parseInt(strTagIds));
						for (JSONObject mapping : mappings) {
							trackIds.add(Integer.parseInt(mapping.get("trackId").toString()));
						}
						_tagId = strTagIds;
					}

					JSONArray tracks = ws.getTracks(trackIds);

					// get group type
					JSONObject filter = new JSONObject();
					filter.put("key", "tagId");
					filter.put("value", _tagId);
					JSONArray gr = ws.getTags(filter);
					groupType = ((JSONObject) gr.get(0)).get("type").toString();

					SolrInterface solr = new SolrInterface();
					JSONObject res = null;
					HashMap<String, Object> key = new HashMap<String, Object>();
					key.put("tracks", tracks);

					StringBuffer out_sb = new StringBuffer();
					JSONArray items = null;
					JSONObject item = null;

					if (groupType.equals("Feature")) {
						res = solr.getFeaturesByID(key);
						items = (JSONArray) res.get("results");

						out_sb.append("Feature Id \t Genome Name \t Accession \t Locus Tag \t Annotation \t Feature Type \t Start \t End \t Length(NT) \t Strand \t");
						out_sb.append("Protein Id \t Length(AA) \t Gene Symbol \t Product \n");

						for (int i = 0; i < items.size(); i++) {
							item = (JSONObject) items.get(i);
							out_sb.append(item.get("na_feature_id") + "\t");
							out_sb.append(item.get("genome_name") + "\t");
							out_sb.append(item.get("accession") + "\t");
							out_sb.append(item.get("locus_tag") + "\t");
							out_sb.append(item.get("annotation") + "\t");
							out_sb.append(item.get("feature_type") + "\t");
							out_sb.append(item.get("start_max") + "\t");
							out_sb.append(item.get("end_min") + "\t");
							out_sb.append(item.get("na_length") + "\t");
							out_sb.append(item.get("strand") + "\t");
							out_sb.append(item.get("protein_id") + "\t");
							out_sb.append(item.get("aa_length") + "\t");
							out_sb.append(item.get("gene") + "\t");
							out_sb.append(item.get("product") + "\t");
							out_sb.append("\n");
						}
					}
					else if (groupType.equals("Genome")) {
						res = solr.getGenomesByID(key);
						items = (JSONArray) res.get("results");

						out_sb.append("Genome Id \t Genome Name \t Size \t PATRIC CDS \t Legacy BRC CDS \t RefSeq CDS \t Chromosome \t Plasmid \t Contig \t NCBI Taxon \n");

						for (int i = 0; i < items.size(); i++) {
							item = (JSONObject) items.get(i);
							out_sb.append(item.get("genome_info_id") + "\t");
							out_sb.append(item.get("genome_name") + "\t");
							out_sb.append(item.get("length") + "\t");
							out_sb.append(item.get("rast_cds") + "\t");
							out_sb.append(item.get("brc_cds") + "\t");
							out_sb.append(item.get("refseq_cds") + "\t");
							out_sb.append(item.get("chromosome") + "\t");
							out_sb.append(item.get("plasmid") + "\t");
							out_sb.append(item.get("contig") + "\t");
							out_sb.append(item.get("ncbi_tax_id") + "\t");
							out_sb.append("\n");
						}
					}
					else {
						// error
					}

					response.setContentType("text/plain");
					response.getWriter().write(out_sb.toString());
					response.getWriter().close();
				}
			}
			else if (action_type.equals("LoginStatus")) {
				if (action.equals("getLoginStatus")) {
					if (request.getUserPrincipal() == null) {
						response.setContentType("text/plain");
						response.getWriter().write("false");
						response.getWriter().close();
					}
					else {
						response.setContentType("text/plain");
						response.getWriter().write("true");
						response.getWriter().close();
					}
				}
			}
			else if (action_type.equals("PopupShowedStatus")) {
				PortletSession session = request.getPortletSession(true);
				if (action.equals("getPopupStatus")) {
					String popupshowed = (String) session.getAttribute("popupshowed", PortletSession.APPLICATION_SCOPE);
					if (popupshowed == null) {
						popupshowed = "false";
					}
					response.setContentType("text/plain");
					response.getWriter().write(popupshowed);
					response.getWriter().close();
				}
				else if (action.equals("setPopupStatus")) {
					session.setAttribute("popupshowed", "ture", PortletSession.APPLICATION_SCOPE);
					response.setContentType("text/plain");
					response.getWriter().write("true");
					response.getWriter().close();
				}
			}
			else if (action_type.equals("HTTPProvider")) {

				UIPreference uiPref = getValidUIPreference(request);

				if (action.equals("storage")) {
					if (request.getMethod().equals("GET")) {

						String strUIPref = uiPref.getStateList().toJSONString();
						// System.out.println("reading UIPreference:" + strUIPref);

						response.getWriter().write(strUIPref);
						response.getWriter().close();
					}
					else if (request.getMethod().equals("POST")) {

						JSONParser parser = new JSONParser();
						JSONObject param = new JSONObject();
						JSONArray params = new JSONArray();
						try {
							Object rt = parser.parse(request.getReader());
							if (rt instanceof JSONObject) {
								param = (JSONObject) rt;
								uiPref.setState(param);
							}
							else if (rt instanceof JSONArray) {
								params = (JSONArray) rt;
								uiPref.setStateList(params);
							}
							else {
								System.out.println("[ERROR] " + rt.toString());
							}

							this.saveUIPreference(request, uiPref);

							// System.out.println("HTTPProvider.storage.POST:" + param.toJSONString());
						}
						catch (ParseException e) {
							e.printStackTrace();
						}

						response.getWriter().write("");
						response.getWriter().close();
					}
				}
				else if (action.equals("remove")) {
					if (request.getParameter("name") != null) {
						uiPref.resetState(request.getParameter("name").toString());
						this.saveUIPreference(request, uiPref);
					}

					response.getWriter().write("");
					response.getWriter().close();
				}
				else if (action.equals("reset")) {
					uiPref.reset();
					this.saveUIPreference(request, uiPref);

					response.getWriter().write("");
					response.getWriter().close();
				}
			}
		}
	}
}
