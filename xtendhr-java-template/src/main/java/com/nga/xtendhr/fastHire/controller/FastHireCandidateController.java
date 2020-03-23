package com.nga.xtendhr.fastHire.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.nga.xtendhr.fastHire.SF.DestinationClient;
import com.nga.xtendhr.fastHire.model.CodeList;
import com.nga.xtendhr.fastHire.model.CodeListText;
import com.nga.xtendhr.fastHire.model.Field;
import com.nga.xtendhr.fastHire.model.FieldDataFromSystem;
import com.nga.xtendhr.fastHire.model.FieldGroup;
import com.nga.xtendhr.fastHire.model.FieldText;
import com.nga.xtendhr.fastHire.model.MapCountryBusinessUnit;
import com.nga.xtendhr.fastHire.model.MapCountryBusinessUnitTemplate;
import com.nga.xtendhr.fastHire.model.MapFieldFieldGroup;
import com.nga.xtendhr.fastHire.model.MapTemplateFieldGroup;
import com.nga.xtendhr.fastHire.model.MapTemplateFieldProperties;
import com.nga.xtendhr.fastHire.model.SFAPI;
import com.nga.xtendhr.fastHire.model.Template;
import com.nga.xtendhr.fastHire.service.CodeListService;
import com.nga.xtendhr.fastHire.service.CodeListTextService;
import com.nga.xtendhr.fastHire.service.FieldDataFromSystemService;
import com.nga.xtendhr.fastHire.service.FieldGroupService;
import com.nga.xtendhr.fastHire.service.FieldTextService;
import com.nga.xtendhr.fastHire.service.MapCountryBusinessUnitService;
import com.nga.xtendhr.fastHire.service.MapCountryBusinessUnitTemplateService;
import com.nga.xtendhr.fastHire.service.MapFieldFieldGroupService;
import com.nga.xtendhr.fastHire.service.MapTemplateFieldGroupService;
import com.nga.xtendhr.fastHire.service.MapTemplateFieldPropertiesService;
import com.nga.xtendhr.fastHire.service.SFAPIService;
import com.nga.xtendhr.fastHire.utilities.DropDownKeyValue;

@RestController
@RequestMapping("/FastHireCandidate")
public class FastHireCandidateController {
	Logger logger = LoggerFactory.getLogger(FastHireCandidateController.class);

	public static final String destinationName = "prehiremgrSFTest";

	@Autowired
	MapCountryBusinessUnitService mapCountryBusinessUnitService;

	@Autowired
	MapCountryBusinessUnitTemplateService mapCountryBusinessUnitTemplateService;

	@Autowired
	MapTemplateFieldGroupService mapTemplateFieldGroupService;

	@Autowired
	MapTemplateFieldPropertiesService mapTemplateFieldPropertiesService;

	@Autowired
	SFAPIService sfAPIService;

	@Autowired
	FieldTextService fieldTextService;

	@Autowired
	CodeListService codeListService;

	@Autowired
	CodeListTextService codeListTextService;

	@Autowired
	FieldDataFromSystemService fieldDataFromSystemService;

	@Autowired
	FieldGroupService fieldGroupService;

	@Autowired
	MapFieldFieldGroupService mapFieldFieldGroupService;

	@GetMapping(value = "/UserDetails")
	public ResponseEntity<?> getUserDetails(HttpServletRequest request)
			throws NamingException, ClientProtocolException, IOException, URISyntaxException {
		String loggedInUser = request.getUserPrincipal().getName();
		loggedInUser = "sfadmin";

		DestinationClient destClient = new DestinationClient();
		destClient.setDestName(destinationName);
		destClient.setHeaderProvider();
		destClient.setConfiguration();
		destClient.setDestConfiguration();
		destClient.setHeaders(destClient.getDestProperty("Authentication"));

		// call to get local language of the logged in user

		HttpResponse userResponse = destClient.callDestinationGET("/User", "?$filter=userId eq '" + loggedInUser
				+ "'&$format=json&$select=userId,lastName,firstName,email,defaultLocale");
		String userResponseJsonString = EntityUtils.toString(userResponse.getEntity(), "UTF-8");
		JSONObject userResponseObject = new JSONObject(userResponseJsonString);
		userResponseObject = userResponseObject.getJSONObject("d").getJSONArray("results").getJSONObject(0);
		return ResponseEntity.ok().body(userResponseObject.toString());
	}

	@GetMapping(value = "/FormGroups")
	public ResponseEntity<?> getFormGroups(
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws NamingException, ClientProtocolException, IOException, URISyntaxException {
		JSONObject returnObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		// get logged in User Id
		String loggedInUser = request.getUserPrincipal().getName();
		// adding dummy userId for now
		loggedInUser = tempUserId;

		Map<String, String> map = new HashMap<String, String>();
		DestinationClient destClientUser = new DestinationClient();
		destClientUser.setDestName(destinationName);
		destClientUser.setHeaderProvider();
		destClientUser.setConfiguration();
		destClientUser.setDestConfiguration();
		destClientUser.setHeaders(destClientUser.getDestProperty("Authentication"));
		HttpResponse responseUser = destClientUser.callDestinationGET("/EmpJob", "?$filter=userId eq '" + loggedInUser
				+ "' &$format=json&$expand=positionNav,positionNav/companyNav,userNav&$select=positionNav/companyNav/country,userNav/defaultLocale");
		String responseUserJson = EntityUtils.toString(responseUser.getEntity(), "UTF-8");
		logger.debug("responseUserJson" + responseUserJson);
		JSONObject userEmpJobObject = new JSONObject(responseUserJson);
		userEmpJobObject = userEmpJobObject.getJSONObject("d").getJSONArray("results").getJSONObject(0);

		map.put("country",
				userEmpJobObject.getJSONObject("positionNav").getJSONObject("companyNav").getString("country"));
		map.put("category", "CONFIRM");
		map.put("candidateId", loggedInUser);
		map.put("locale", userEmpJobObject.getJSONObject("userNav").getString("defaultLocale"));

		MapCountryBusinessUnit mapCountryBusinessUnit;
		if (businessUnitId == null) {
			List<MapCountryBusinessUnit> mapCountryBusinessUnitList = mapCountryBusinessUnitService
					.findByCountry(map.get("country"));
			mapCountryBusinessUnit = mapCountryBusinessUnitList.get(0);
		} else {
			mapCountryBusinessUnit = mapCountryBusinessUnitService.findByCountryBusinessUnit(map.get("country"),
					businessUnitId);
		}
		// getting the template per BUnit and Country
		List<MapCountryBusinessUnitTemplate> mapTemplateList = mapCountryBusinessUnitTemplateService
				.findByCountryBusinessUnitId(mapCountryBusinessUnit.getId());

		Date today = new Date();
		Template template = null;

		// getting valid template per category (Confirm)
		for (MapCountryBusinessUnitTemplate mapTemplate : mapTemplateList) {

			if (today.before(mapTemplate.getEndDate()) && today.after(mapTemplate.getStartDate())) {

				if (mapTemplate.getTemplate().getCategory().equalsIgnoreCase(map.get("category"))) {
					template = mapTemplate.getTemplate();
					break;
				}

			}
		}
		if (template != null) {
			List<MapTemplateFieldGroup> templateFieldGroups = mapTemplateFieldGroupService
					.findByTemplate(template.getId());
			if (templateFieldGroups.size() != 0) {
				Collections.sort(templateFieldGroups);
				for (MapTemplateFieldGroup tFieldGroup : templateFieldGroups) {
					// candidate app will only have fields which have is candidate visible = true
					if (tFieldGroup.getIsVisibleCandidate()) {
						Gson gson = new Gson();

						if (tFieldGroup.getFieldGroup() != null) {
							// setting the field Group
							JSONObject jsonObject = new JSONObject();
							tFieldGroup.getFieldGroup().setFieldGroupSeq(tFieldGroup.getFieldGroupSeq());
							String jsonString = gson.toJson(tFieldGroup.getFieldGroup());
							jsonObject.put("fieldGroup", new JSONObject(jsonString));
							jsonArray.put(jsonObject);

						}
					}
				}

			}
		}
		returnObject.put("hiringForm", jsonArray);
		return ResponseEntity.ok().body(returnObject.toString());
	}

	@GetMapping(value = "/FormTemplate")
	public ResponseEntity<?> getFormTemplateFields(
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId,
			@RequestParam(value = "fieldGroup", required = true) String fieldGroupId, HttpServletRequest request)
			throws NamingException, ParseException, IOException, URISyntaxException {
		JSONObject returnObject = new JSONObject();
		JSONArray returnArray = new JSONArray();

		// get logged in User Id
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;

		Map<String, String> map = new HashMap<String, String>();

		DestinationClient destClientUser = new DestinationClient();
		destClientUser.setDestName(destinationName);
		destClientUser.setHeaderProvider();
		destClientUser.setConfiguration();
		destClientUser.setDestConfiguration();
		destClientUser.setHeaders(destClientUser.getDestProperty("Authentication"));
		HttpResponse responseUser = destClientUser.callDestinationGET("/EmpJob", "?$filter=userId eq '" + userId
				+ "' &$format=json&$expand=positionNav,positionNav/companyNav,userNav&$select=positionNav/company,positionNav/department,position,positionNav/companyNav/country,userNav/firstName,userNav/lastName,userNav/defaultLocale");
		String responseUserJson = EntityUtils.toString(responseUser.getEntity(), "UTF-8");
		logger.debug("responseUserJson" + responseUserJson);
		JSONObject userEmpJobObject = new JSONObject(responseUserJson);
		userEmpJobObject = userEmpJobObject.getJSONObject("d").getJSONArray("results").getJSONObject(0);
//		 returnObject.put("userEmpJob", userEmpJobObject);
		map.put("position", userEmpJobObject.getString("position"));
		map.put("department", userEmpJobObject.getJSONObject("positionNav").getString("department"));
		map.put("company", userEmpJobObject.getJSONObject("positionNav").getString("company"));
		map.put("country",
				userEmpJobObject.getJSONObject("positionNav").getJSONObject("companyNav").getString("country"));
		map.put("category", "CONFIRM");
		map.put("candidateId", userId);
		map.put("locale", userEmpJobObject.getJSONObject("userNav").getString("defaultLocale"));
		map.put("firstName", userEmpJobObject.getJSONObject("userNav").getString("firstName"));
		map.put("lastName", userEmpJobObject.getJSONObject("userNav").getString("lastName"));

		logger.debug("map:" + map);
		// Get the Business Unit and Company Map
		MapCountryBusinessUnit mapCountryBusinessUnit;
		if (businessUnitId == null) {
			List<MapCountryBusinessUnit> mapCountryBusinessUnitList = mapCountryBusinessUnitService
					.findByCountry(map.get("country"));
			mapCountryBusinessUnit = mapCountryBusinessUnitList.get(0);
		} else {
			mapCountryBusinessUnit = mapCountryBusinessUnitService.findByCountryBusinessUnit(map.get("country"),
					businessUnitId);
		}
		// getting the template per BUnit and Country
		List<MapCountryBusinessUnitTemplate> mapTemplateList = mapCountryBusinessUnitTemplateService
				.findByCountryBusinessUnitId(mapCountryBusinessUnit.getId());

		Date today = new Date();
		Template template = null;

		// getting valid template per category (Confirm)
		for (MapCountryBusinessUnitTemplate mapTemplate : mapTemplateList) {

			if (today.before(mapTemplate.getEndDate()) && today.after(mapTemplate.getStartDate())) {

				if (mapTemplate.getTemplate().getCategory().equalsIgnoreCase(map.get("category"))) {
					template = mapTemplate.getTemplate();
					break;
				}

			}
		}

		if (template != null) {
			// get all field groups for the template
//				List<MapTemplateFieldGroup> templateFieldGroups = mapTemplateFieldGroupService.findByTemplate(template.getId());

			List<MapTemplateFieldGroup> templateFieldGroups = mapTemplateFieldGroupService
					.findByTemplateFieldGroup(template.getId(), fieldGroupId);
			if (templateFieldGroups.size() != 0) {
				HashMap<String, String> responseMap = new HashMap<>();
				Set<String> entities = new HashSet<String>();

				// get the details of Position for CONFIRM Hire Template value setting

				// get all the fields Entity Names Distinct
				for (MapTemplateFieldGroup fieldGroup : templateFieldGroups) {
					List<MapTemplateFieldProperties> fieldProperties = mapTemplateFieldPropertiesService
							.findByTemplateFieldGroup(fieldGroup.getId());
					for (MapTemplateFieldProperties fieldProp : fieldProperties) {
						if (fieldProp.getField().getEntityName() != null) {
							entities.add(fieldProp.getField().getEntityName());
						}
					}
				}

				// call the entity urls which are independent like Empjob
				for (String entity : entities) {

					SFAPI sfApi = sfAPIService.findById(entity, "GET");
					if (sfApi != null && sfApi.getTagSource().equalsIgnoreCase("UI")) {

						DestinationClient destClientPos = new DestinationClient();
						destClientPos.setDestName(destinationName);
						destClientPos.setHeaderProvider();
						destClientPos.setConfiguration();
						destClientPos.setDestConfiguration();
						destClientPos.setHeaders(destClientPos.getDestProperty("Authentication"));

						String url = sfApi.getUrl().replace("<" + sfApi.getReplaceTag() + ">",
								map.get(sfApi.getTagSourceValuePath()));

						HttpResponse responsePos = destClientPos.callDestinationGET(url, "");

						String responseString = EntityUtils.toString(responsePos.getEntity(), "UTF-8");
						responseMap.put(entity, responseString);

					}
				}

				// call the entity URLs which dependent on other entities
				for (String entity : entities) {

					SFAPI sfApi = sfAPIService.findById(entity, "GET");
					if (sfApi != null && !sfApi.getTagSource().equalsIgnoreCase("UI")) {
						DestinationClient destClientPos = new DestinationClient();
						destClientPos.setDestName(destinationName);
						destClientPos.setHeaderProvider();
						destClientPos.setConfiguration();
						destClientPos.setDestConfiguration();
						destClientPos.setHeaders(destClientPos.getDestProperty("Authentication"));

						JSONObject depEntityObj = new JSONObject(responseMap.get(sfApi.getTagSource()));

						JSONArray responseResult = depEntityObj.getJSONObject("d").getJSONArray("results");
						JSONObject positionEntity = responseResult.getJSONObject(0);

						// get the dependent value from the dependent entity

						String replaceValue = getValueFromPathJson(positionEntity, sfApi.getTagSourceValuePath(), map);

						// replacing the tag variable from the dependent entity data value
						if (replaceValue != null && replaceValue.length() != 0) {
							String url = sfApi.getUrl().replace("<" + sfApi.getReplaceTag() + ">", replaceValue);

							HttpResponse responsePos = destClientPos.callDestinationGET(url, "");
							if (responsePos.getStatusLine().getStatusCode() == 200) {

								String responseString = EntityUtils.toString(responsePos.getEntity(), "UTF-8");
								responseMap.put(entity, responseString);
							}
						}
					}
				}

				// Loop the field Group to create the response JSON array
				for (MapTemplateFieldGroup tFieldGroup : templateFieldGroups) {
					// candidate app will only have fields which have is candidate visible = true
					if (tFieldGroup.getIsVisibleCandidate()) {
						JSONObject fieldObject = new JSONObject();
						Gson gson = new Gson();

						if (tFieldGroup.getFieldGroup() != null) {
							// setting the field Group
							tFieldGroup.getFieldGroup().setFieldGroupSeq(tFieldGroup.getFieldGroupSeq());
							String jsonString = gson.toJson(tFieldGroup.getFieldGroup());
							fieldObject.put("fieldGroup", new JSONObject(jsonString));

							// creating the fields entity in the json per field group
							List<MapTemplateFieldProperties> mapTemplateFieldPropertiesList = mapTemplateFieldPropertiesService
									.findByTemplateFieldGroupCandidate(tFieldGroup.getId(), true);
//								List<MapTemplateFieldProperties> mapTemplateFieldPropertiesList = mapTemplateFieldPropertiesService.findByTemplateFieldGroup(tFieldGroup.getId());
							for (MapTemplateFieldProperties mapTemplateFieldProperties : mapTemplateFieldPropertiesList) {

								// setting field labels

								FieldText fieldText = fieldTextService.findByFieldLanguage(
										mapTemplateFieldProperties.getFieldId(), map.get("locale"));
								if (fieldText != null) {

									mapTemplateFieldProperties.getField().setName(fieldText.getName());
								}

								// setting the field values

								if (mapTemplateFieldProperties.getField().getEntityName() != null) {
									if (responseMap
											.get(mapTemplateFieldProperties.getField().getEntityName()) != null) {
										Field field = mapTemplateFieldProperties.getField();
										if ((field.getFieldType().equalsIgnoreCase("Picklist")
												|| field.getFieldType().equalsIgnoreCase("Entity")
												|| field.getFieldType().equalsIgnoreCase("Codelist"))
												&& mapTemplateFieldProperties.getIsEditableCandidate()) {
											logger.debug("editable picklist:"
													+ mapTemplateFieldProperties.getField().getName());
											SFAPI sourceEntity = sfAPIService.findById(
													mapTemplateFieldProperties.getField().getEntityName(), "GET");
											JSONObject responseObject;
											String valuePath;
											if (sourceEntity.getTagSource().equalsIgnoreCase("UI")) {
												responseObject = new JSONObject(
														responseMap.get(sourceEntity.getEntityName()));
												valuePath = mapTemplateFieldProperties.getField().getValueFromPath();
											} else {
												responseObject = new JSONObject(
														responseMap.get(sourceEntity.getTagSource()));
												valuePath = sourceEntity.getTagSourceValuePath();
											}

											logger.debug("responseObject:" + responseObject);
											JSONArray responseResult = responseObject.getJSONObject("d")
													.getJSONArray("results");
											logger.debug("responseResult:" + responseResult);
											if (responseResult.length() != 0) {
												JSONObject positionEntity = responseResult.getJSONObject(0);

												String value = getValueFromPathJson(positionEntity, valuePath, map);
												logger.debug("set value : " + value);
												mapTemplateFieldProperties.setValue(value);
												// now get value
												JSONObject valuePositionEntity = new JSONObject(responseMap
														.get(mapTemplateFieldProperties.getField().getEntityName()));
												valuePositionEntity = valuePositionEntity.getJSONObject("d")
														.getJSONArray("results").getJSONObject(0);
												String dropDownValue = getValueFromPathJson(valuePositionEntity,
														mapTemplateFieldProperties.getField().getValueFromPath(), map);
											}
										} else {
											logger.debug("non editable picklist or input:"
													+ mapTemplateFieldProperties.getField().getName());
											JSONObject responseObject = new JSONObject(responseMap
													.get(mapTemplateFieldProperties.getField().getEntityName()));

											JSONArray responseResult = responseObject.getJSONObject("d")
													.getJSONArray("results");

											if (responseResult.length() != 0) {
												JSONObject positionEntity = responseResult.getJSONObject(0);

												String value = getValueFromPathJson(positionEntity,
														mapTemplateFieldProperties.getField().getValueFromPath(), map);
												logger.debug("value" + value);
												if (value != null) {
													if (mapTemplateFieldProperties.getField().getFieldType()
															.equalsIgnoreCase("Codelist")) {

														List<CodeList> CodeList = codeListService.findByCountryField(
																mapTemplateFieldProperties.getFieldId(),
																map.get("country"));
														String codeListId = null;
														if (CodeList.size() == 1) {
															codeListId = CodeList.get(0).getId();
														} else {
															for (CodeList codeObject : CodeList) {

																for (MapTemplateFieldProperties existingField : mapTemplateFieldPropertiesList) {
																	if (existingField.getField().getId()
																			.equalsIgnoreCase(
																					codeObject.getDependentFieldId())) {
																		if (existingField.getValue() != null
																				&& existingField.getValue()
																						.equalsIgnoreCase(codeObject
																								.getDependentFieldValue())) {
																			codeListId = codeObject.getId();

																			break;
																		}
																	}
																}
																if (codeListId != null) {
																	break;
																}

															}
														}

														CodeListText clText = codeListTextService.findById(codeListId,
																map.get("locale"), value);
														if (clText != null) {
															value = clText.getDescription();
														} else {
															value = "";
														}
													}
													mapTemplateFieldProperties.setValue(value);
												} else {
													mapTemplateFieldProperties.setValue("");
												}
											} else {
												mapTemplateFieldProperties.setValue("");
											}
										}
									} else {
										mapTemplateFieldProperties.setValue("");
									}
								} else {

									if (mapTemplateFieldProperties.getValue() == null) {
										if (mapTemplateFieldProperties.getField().getInitialValue() != null) {
											mapTemplateFieldProperties
													.setValue(mapTemplateFieldProperties.getField().getInitialValue());
										} else {
											mapTemplateFieldProperties.setValue("");
										}
									}
								}

								// making the field input type if the is Editable Candidate false

								if (!mapTemplateFieldProperties.getIsEditableCandidate()) {
									mapTemplateFieldProperties.getField().setFieldType("Input");

									/// may be we need to call the picklist to get the labels instead of key value
									/// for few fields
								}

								if (mapTemplateFieldProperties.getIsVisibleCandidate()) {
									// setting drop down values if picklist, codelist, entity
									logger.debug("setting drop down values if picklist, codelist, entity");
									List<DropDownKeyValue> dropDown = new ArrayList<DropDownKeyValue>();
									List<FieldDataFromSystem> fieldDataFromSystemList;

									// switch case the picklist , entity and codelist to get the data from various
									// systems
									switch (mapTemplateFieldProperties.getField().getFieldType()) {
									case "Picklist":
										logger.debug("Picklist" + mapTemplateFieldProperties.getField().getName());
										fieldDataFromSystemList = fieldDataFromSystemService.findByFieldCountry(
												mapTemplateFieldProperties.getField().getId(), map.get("country"));

										if (fieldDataFromSystemList.size() != 0) {
											FieldDataFromSystem fieldDataFromSystem = fieldDataFromSystemList.get(0);
											DestinationClient destClient = new DestinationClient();
											destClient.setDestName(fieldDataFromSystem.getDestinationName());
											destClient.setHeaderProvider();
											destClient.setConfiguration();
											destClient.setDestConfiguration();
											destClient.setHeaders(destClient.getDestProperty("Authentication"));
											HttpResponse response = destClient.callDestinationGET(
													fieldDataFromSystem.getPath(), fieldDataFromSystem.getFilter());

											String responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
											JSONObject responseObject = new JSONObject(responseJson);

											JSONArray responseResult = responseObject.getJSONObject("d")
													.getJSONArray("results");
											for (int i = 0; i < responseResult.length(); i++) {
												DropDownKeyValue keyValue = new DropDownKeyValue();
												String key = (String) responseResult.getJSONObject(i)
														.get(fieldDataFromSystem.getKey());
												keyValue.setKey(key);

												JSONArray pickListLabels = responseResult.getJSONObject(i)
														.getJSONObject("picklistLabels").getJSONArray("results");
												for (int j = 0; j < pickListLabels.length(); j++) {
													if (pickListLabels.getJSONObject(j).get("locale").toString()
															.equalsIgnoreCase(map.get("locale"))) {
														keyValue.setValue(pickListLabels.getJSONObject(j).get("label")
																.toString());
													}
												}

												dropDown.add(keyValue);
											}

										}
										break;
									case "Entity":
										logger.debug("Entity" + mapTemplateFieldProperties.getField().getName());
										fieldDataFromSystemList = fieldDataFromSystemService.findByFieldCountry(
												mapTemplateFieldProperties.getField().getId(), map.get("country"));

										if (fieldDataFromSystemList.size() != 0) {
											FieldDataFromSystem fieldDataFromSystem = fieldDataFromSystemList.get(0);
											DestinationClient destClient = new DestinationClient();
											destClient.setDestName(fieldDataFromSystem.getDestinationName());
											destClient.setHeaderProvider();
											destClient.setConfiguration();
											destClient.setDestConfiguration();
											destClient.setHeaders(destClient.getDestProperty("Authentication"));
											HttpResponse response = destClient.callDestinationGET(
													fieldDataFromSystem.getPath(), fieldDataFromSystem.getFilter());

											String responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
											JSONObject responseObject = new JSONObject(responseJson);

											JSONArray responseResult = responseObject.getJSONObject("d")
													.getJSONArray("results");
											String valuePath = fieldDataFromSystem.getValue();
											String[] valuePathArray = valuePath.split("/");
											String keyPath = fieldDataFromSystem.getKey();
											String[] keyPathArray = keyPath.split("/");
											JSONObject temp = null;
											int index = 0;
											if (valuePathArray.length > 1 && keyPathArray.length > 1) {
												for (int k = 0; k < valuePathArray.length - 1; k++) {
													responseResult = responseResult.getJSONObject(0)
															.getJSONObject(valuePathArray[index])
															.getJSONArray("results");
													index = index + 1;
												}
											}

											for (int i = 0; i < responseResult.length(); i++) {
												temp = responseResult.getJSONObject(i);
												DropDownKeyValue keyValue = new DropDownKeyValue();
												if (valuePathArray[index].contains("<locale>")) {

													valuePathArray[index] = valuePathArray[index].replace("<locale>",
															map.get("locale"));
												}
												keyValue.setValue(temp.get(valuePathArray[index]).toString());
												keyValue.setKey(temp.get(keyPathArray[index]).toString());

												dropDown.add(keyValue);
											}
										}

										break;
									case "Codelist":
										logger.debug("Codelist" + mapTemplateFieldProperties.getField().getName());
										List<CodeList> codeList = codeListService.findByCountryField(
												mapTemplateFieldProperties.getField().getId(), map.get("country"));
										logger.debug("here 7");
										if (codeList.size() != 0) {
											if (codeList.size() == 1) {
												List<CodeListText> codeListValues = codeListTextService
														.findByCodeListIdLang(codeList.get(0).getId(),
																map.get("locale"));
												for (CodeListText value : codeListValues) {
													DropDownKeyValue keyValue = new DropDownKeyValue();
													keyValue.setKey(value.getValue());
													keyValue.setValue(value.getDescription());
													dropDown.add(keyValue);
												}
											}

										}
										break;
									}

									mapTemplateFieldProperties.setDropDownValues(dropDown);

								}
							}
							fieldObject.put("fields", mapTemplateFieldPropertiesList);
							returnArray.put(fieldObject);
						}
					}
				}

			}
		}

		returnObject.put("hiringForm", returnArray);
		return ResponseEntity.ok().body(returnObject.toString());

	}

	// Post call by Manish

	private ArrayList<String> separateCall(String requestBody, String userId, String businessUnitId,
			String fieldGroupID) throws NamingException, ClientProtocolException, IOException, URISyntaxException {
		JSONObject requestObj = new JSONObject(requestBody);

		logger.error("USerId: " + userId);

		// **************************Viplove code************************
		JSONObject returnObject = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();

		DestinationClient destClientUser = new DestinationClient();
		destClientUser.setDestName(destinationName);
		destClientUser.setHeaderProvider();
		destClientUser.setConfiguration();
		destClientUser.setDestConfiguration();
		destClientUser.setHeaders(destClientUser.getDestProperty("Authentication"));
		HttpResponse responseUser = destClientUser.callDestinationGET("/EmpJob", "?$filter=userId eq '" + userId
				+ "' &$format=json&$expand=positionNav,positionNav/companyNav,userNav&$select=positionNav/company,positionNav/department,position,positionNav/companyNav/country,userNav/firstName,userNav/lastName,userNav/defaultLocale");
		String responseUserJson = EntityUtils.toString(responseUser.getEntity(), "UTF-8");

		JSONObject userEmpJobObject = new JSONObject(responseUserJson);
		userEmpJobObject = userEmpJobObject.getJSONObject("d").getJSONArray("results").getJSONObject(0);
		returnObject.put("userEmpJob", userEmpJobObject);
		map.put("position", userEmpJobObject.getString("position"));
		map.put("department", userEmpJobObject.getJSONObject("positionNav").getString("department"));
		map.put("company", userEmpJobObject.getJSONObject("positionNav").getString("company"));
		map.put("country",
				userEmpJobObject.getJSONObject("positionNav").getJSONObject("companyNav").getString("country"));
		map.put("category", "CONFIRM");
		map.put("candidateId", userId);
		map.put("locale", userEmpJobObject.getJSONObject("userNav").getString("defaultLocale"));
		map.put("firstName", userEmpJobObject.getJSONObject("userNav").getString("firstName"));
		map.put("lastName", userEmpJobObject.getJSONObject("userNav").getString("lastName"));

		// Get the Business Unit and Company Map
		MapCountryBusinessUnit mapCountryBusinessUnit;
		if (businessUnitId == null) {
			List<MapCountryBusinessUnit> mapCountryBusinessUnitList = mapCountryBusinessUnitService
					.findByCountry(map.get("country"));
			mapCountryBusinessUnit = mapCountryBusinessUnitList.get(0);
		} else {
			mapCountryBusinessUnit = mapCountryBusinessUnitService.findByCountryBusinessUnit(map.get("country"),
					businessUnitId);
		}
		// getting the template per BUnit and Country
		List<MapCountryBusinessUnitTemplate> mapTemplateList = mapCountryBusinessUnitTemplateService
				.findByCountryBusinessUnitId(mapCountryBusinessUnit.getId());

		Date today = new Date();
		Template template = null;

		// getting valid template per category (Confirm)
		for (MapCountryBusinessUnitTemplate mapTemplate : mapTemplateList) {

			if (today.before(mapTemplate.getEndDate()) && today.after(mapTemplate.getStartDate())) {

				if (mapTemplate.getTemplate().getCategory().equalsIgnoreCase(map.get("category"))) {
					template = mapTemplate.getTemplate();
					break;
				}

			}
		}
		HashMap<String, JSONObject> sfEntityData = new HashMap<String, JSONObject>();
		if (template != null) {
			// get all field groups for the template
			List<MapTemplateFieldGroup> templateFieldGroups = mapTemplateFieldGroupService
					.findByTemplate(template.getId());

			List<Field> disabledFields = new ArrayList<Field>();
			List<String> entityNamesOfDisabledFields = new ArrayList<String>();
			List<MapTemplateFieldProperties> fieldProperties;
			for (MapTemplateFieldGroup fieldGroup : templateFieldGroups) {
				logger.error("fieldGroup.getId: " + fieldGroup.getId());
				fieldProperties = mapTemplateFieldPropertiesService.getCandidateIsEIsVFalseFields(fieldGroup.getId());

				for (MapTemplateFieldProperties fieldProperty : fieldProperties) {
					disabledFields.add(fieldProperty.getField());
					logger.error("disabledFieldID: " + fieldProperty.getFieldId());
					logger.error("disabledField: " + fieldProperty.getField().getTechnicalName());
					entityNamesOfDisabledFields.add(fieldProperty.getField().getEntityName());
				}
			}

			Set<String> entityNamesOfDisabledFieldsWithOutDuplicates = new LinkedHashSet<String>(
					entityNamesOfDisabledFields);

			JSONObject positionEntity;
			// call the entity urls which are independent like Empjob
			for (String entity : entityNamesOfDisabledFieldsWithOutDuplicates) {

				SFAPI sfApi = sfAPIService.findById(entity, "GET");
				if (sfApi != null && sfApi.getTagSource().equalsIgnoreCase("UI")) {
					DestinationClient destClientPos = new DestinationClient();
					destClientPos.setDestName(destinationName);
					destClientPos.setHeaderProvider();
					destClientPos.setConfiguration();
					destClientPos.setDestConfiguration();
					destClientPos.setHeaders(destClientPos.getDestProperty("Authentication"));

					String url = sfApi.getUrl().replace("<" + sfApi.getReplaceTag() + ">",
							map.get(sfApi.getTagSourceValuePath()));

					HttpResponse responsePos = destClientPos.callDestinationGET(url, "");
					logger.error("urlCalled.." + url);
					String responseString = EntityUtils.toString(responsePos.getEntity(), "UTF-8");

					// ***********retrive exact value from ValueFromPath
					JSONObject depEntityObj = new JSONObject(responseString);
					logger.error("responseString" + responseString);
					JSONArray responseResult = depEntityObj.getJSONObject("d").getJSONArray("results");
					logger.error("responseResult: " + responseResult.toString());
					if (responseResult.length() != 0) {
						positionEntity = responseResult.getJSONObject(0);
						sfEntityData.put(entity, positionEntity);
					}

				}
			}
			logger.error("sfEntityData: " + sfEntityData.toString());

			JSONObject parsedObj = parseRequestArray(requestObj.getJSONArray("hiringForm"));
			logger.error("parsedObj: " + parsedObj.toString());
//			logger.error("sfEntityData OutSide For: " + sfEntityData.toString());

			String value = null;
			HashMap<String, String> disabledFieldsTechnicalNameAndValue = new HashMap<String, String>();
			String valuePath;

			for (int i = 0; i < disabledFields.size(); i++) {
				valuePath = "";

				if (disabledFields.get(i).getEntityName() != null) {
					String entityName = disabledFields.get(i).getEntityName();

					SFAPI sfEntity = sfAPIService.findById(entityName, "GET");

					if (!sfEntity.getTagSource().equalsIgnoreCase("UI")) {
						valuePath = sfEntity.getTagSourceValuePath();

					} else {
						valuePath = disabledFields.get(i).getValueFromPath();
					}

					if (sfEntityData.containsKey(entityName)) {
						value = getValueFromPathJson(sfEntityData.get(entityName), valuePath, map);
					} else {
						value = null;
					}

				} else if (disabledFields.get(i).getInitialValue() != null) {
					value = disabledFields.get(i).getInitialValue();
				} else {
					value = null;
				}
				logger.error("value: " + value);
//				logger.error("EmpJobresponseString: " + EmpJobresponseString);

				disabledFieldsTechnicalNameAndValue.put(disabledFields.get(i).getTechnicalName(), value);
			}
			logger.error("fieldTechnicalNameValue" + disabledFieldsTechnicalNameAndValue);

			// Get All Fields of the FieldGroup and creating proper structure
			FieldGroup fieldGroup = fieldGroupService.findById(fieldGroupID);
			List<MapFieldFieldGroup> mapFieldFieldGroup = mapFieldFieldGroupService
					.findByFieldGroupId(fieldGroup.getId());
			JSONObject entityAndItsFields = new JSONObject();
			String tempEntityName;
			for (int i = 0; i < mapFieldFieldGroup.size(); i++) {
				tempEntityName = mapFieldFieldGroup.get(i).getEntityName();
				if (entityAndItsFields.has(tempEntityName)) {
					JSONObject tempObj = new JSONObject();
					tempObj.put("technicalName", mapFieldFieldGroup.get(i).getField().getTechnicalName());
					entityAndItsFields.getJSONArray(tempEntityName).put(tempObj);
				} else {
					JSONObject tempObj = new JSONObject();
					tempObj.put("technicalName", mapFieldFieldGroup.get(i).getField().getTechnicalName());
					JSONArray tempJSONArray = new JSONArray();
					tempJSONArray.put(tempObj);
					entityAndItsFields.put(tempEntityName, tempJSONArray);
				}
			}
			logger.error("entityAndItsFields: " + entityAndItsFields.toString());

			ArrayList<String> status = executeIndivisualPost(userId, parsedObj, disabledFieldsTechnicalNameAndValue,
					fieldGroupID, entityAndItsFields);

			return (status);
		}
		ArrayList<String> status = new ArrayList<String>();
		status.add("Error: No template found..:(");
		return status;
	}

	private ArrayList<String> executeIndivisualPost(String userId, JSONObject parsedObj,
			HashMap<String, String> responseMap, String callFor, JSONObject entityAndItsFields)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {

		// Executing PerPersonal request
		DestinationClient destClient = new DestinationClient();
		destClient.setDestName(destinationName);
		destClient.setHeaderProvider();
		destClient.setConfiguration();
		destClient.setDestConfiguration();
		destClient.setHeaders(destClient.getDestProperty("Authentication"));
		ArrayList<String> responseList = new ArrayList<String>();
		String urlToCall = "/PerPersonal?$filter=personIdExternal eq '" + userId + "'&$format=json";
		String result;
		// Get metadata for perPerson
		JSONObject perPerson__metadata = get__metadata(urlToCall, destClient);
		JSONObject perPersonalReqObj = new JSONObject();
		String value = null;
		@SuppressWarnings("unchecked")
		Iterator<String> keysItr = entityAndItsFields.keys();
		String entityName;
		while (keysItr.hasNext()) {
			entityName = keysItr.next();
			logger.error("Called for Entity: " + entityName);
			switch (entityName) {
			case "PerPersonal":
				perPersonalReqObj.put("__metadata", perPerson__metadata);
				value = getValue("gender", responseMap, parsedObj);
				perPersonalReqObj.put("gender", value != null ? value : JSONObject.NULL);
				value = getValue("maritalStatus", responseMap, parsedObj);
				perPersonalReqObj.put("maritalStatus", value != null ? value : JSONObject.NULL);
				value = getValue("nationality", responseMap, parsedObj);
				perPersonalReqObj.put("nationality", value != null ? value : JSONObject.NULL);
				value = getValue("salutation", responseMap, parsedObj);
				perPersonalReqObj.put("salutation", value != null ? value : JSONObject.NULL);
				value = getValue("initials", responseMap, parsedObj);
				perPersonalReqObj.put("initials", value != null ? value : JSONObject.NULL);
				value = getValue("nativePreferredLang", responseMap, parsedObj);
				perPersonalReqObj.put("nativePreferredLang", value != null ? value : JSONObject.NULL);
				logger.error("urlToCall---" + urlToCall);
				logger.error("Data sent:---" + perPersonalReqObj.toString());
				HttpResponse getResponse = destClient.callDestinationPOST("/upsert", "?$format=json",
						perPersonalReqObj.toString());
				result = EntityUtils.toString(getResponse.getEntity(), "UTF-8");
				responseList.add(result);
				break;

			case "PerPerson":
				// Posting PerPerson Data
				JSONObject perPersonReqObj = new JSONObject();
				value = getValue("placeOfBirth", responseMap, parsedObj);
				perPersonReqObj.put("placeOfBirth", value != null ? value : JSONObject.NULL);
				value = getValue("countryOfBirth", responseMap, parsedObj);
				perPersonReqObj.put("countryOfBirth", value != null ? value : JSONObject.NULL);
				perPersonReqObj.put("__metadata", new JSONObject().put("uri", "PerPerson('" + userId + "')"));
				logger.error("Data sent:-perPersonReqObj--" + perPersonReqObj.toString());
				getResponse = destClient.callDestinationPOST("/upsert", "?$format=json", perPersonReqObj.toString());
				result = EntityUtils.toString(getResponse.getEntity(), "UTF-8");
				responseList.add(result);
				break;

			case "PerPhone":
				// Add phone
				JSONObject addPhoneTypeObj = new JSONObject();
				addPhoneTypeObj.put("__metadata", new JSONObject().put("uri", "PerPhone(personIdExternal='" + userId
						+ "',phoneType='" + getValue("phoneType", responseMap, parsedObj) + "')"));
				value = getValue("phoneType", responseMap, parsedObj);
				addPhoneTypeObj.put("phoneType", value != null ? value : JSONObject.NULL);
				value = getValue("isPrimary", responseMap, parsedObj);
				addPhoneTypeObj.put("isPrimary", value != null ? Boolean.parseBoolean(value) : JSONObject.NULL);
				value = getValue("areaCode", responseMap, parsedObj);
				addPhoneTypeObj.put("areaCode", value != null ? value : JSONObject.NULL);
				value = getValue("countryCode", responseMap, parsedObj);
				logger.error("countryCode: " + value);
				addPhoneTypeObj.put("countryCode", value != null ? value : JSONObject.NULL);
				value = getValue("extension", responseMap, parsedObj);
				addPhoneTypeObj.put("extension", value != null ? value : JSONObject.NULL);
				value = getValue("phoneNumber", responseMap, parsedObj);
				addPhoneTypeObj.put("phoneNumber", value != null ? value : JSONObject.NULL);
				logger.error("Data sent:-addPhoneTypeObj--" + addPhoneTypeObj.toString());
				getResponse = destClient.callDestinationPOST("/upsert", "?$format=json", addPhoneTypeObj.toString());
				result = EntityUtils.toString(getResponse.getEntity(), "UTF-8");
				responseList.add(result);
				break;

			case "PerAddressDEFLT":
				// Add Address
				JSONObject addressTypeObj = new JSONObject();
				addressTypeObj.put("__metadata",
						new JSONObject().put("uri",
								"PerAddressDEFLT(addressType='" + getValue("addressType", responseMap, parsedObj)
										+ "',personIdExternal='" + userId + "',startDate=datetime'"
										+ getCurrentDateForURI() + "')"));
				addressTypeObj.put("personIdExternal", userId);
				value = getValue("addressType", responseMap, parsedObj);
				addressTypeObj.put("addressType", value != null ? value : JSONObject.NULL);
				value = getValue("address1", responseMap, parsedObj);
				addressTypeObj.put("address1", value != null ? value : JSONObject.NULL);
				value = getValue("address2", responseMap, parsedObj);
				addressTypeObj.put("address2", value != null ? value : JSONObject.NULL);
				value = getValue("city", responseMap, parsedObj);
				addressTypeObj.put("city", value != null ? value : JSONObject.NULL);
				value = getValue("zipCode", responseMap, parsedObj);
				addressTypeObj.put("zipCode", value != null ? value : JSONObject.NULL);
				value = getValue("country", responseMap, parsedObj);
				logger.error("country: " + value);
				addressTypeObj.put("country", value != null ? value : JSONObject.NULL);
				logger.error("Data sent:-addressTypeObj--" + addressTypeObj.toString());
				getResponse = destClient.callDestinationPOST("/upsert", "?$format=json", addressTypeObj.toString());
				result = EntityUtils.toString(getResponse.getEntity(), "UTF-8");
				responseList.add(result);
				break;

			case "PaymentInformationDetailV3":
				// Bank Payment
				JSONObject bankPaymentObj = new JSONObject();
				bankPaymentObj.put("__metadata", new JSONObject().put("uri", "PaymentInformationV3"));
				value = getValue("startDate", responseMap, parsedObj);
				bankPaymentObj.put("effectiveStartDate", value != null ? value : JSONObject.NULL);
				bankPaymentObj.put("worker", userId);
				JSONObject toPaymentInformationDetailV3Obj = new JSONObject();
				toPaymentInformationDetailV3Obj.put("__metadata",
						new JSONObject().put("uri", "PaymentInformationDetailV3"));

				value = getValue("startDate", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("PaymentInformationV3_effectiveStartDate",
						value != null ? value : JSONObject.NULL);
				toPaymentInformationDetailV3Obj.put("PaymentInformationV3_worker", userId);
				value = getValue("payType", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("payType", value != null ? value : JSONObject.NULL);
				value = getValue("currency", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("currency", value != null ? value : JSONObject.NULL);
				logger.error("JMK");
				value = getValue("bankCountry", responseMap, parsedObj);
				logger.error("bankCountry---" + value);
				toPaymentInformationDetailV3Obj.put("bankCountry", value != null ? value : JSONObject.NULL);
				value = getValue("paymentMethod", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("paymentMethod", value != null ? value : JSONObject.NULL);
				value = getValue("accountNumber", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("accountNumber", value != null ? value : JSONObject.NULL);
				value = getValue("bank", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("bank", value != null ? value : JSONObject.NULL);
				value = getValue("routingNumber", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("routingNumber", value != null ? value : JSONObject.NULL);
				value = getValue("businessIdentifierCode", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("businessIdentifierCode", value != null ? value : JSONObject.NULL);
				value = getValue("purpose", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("purpose", value != null ? value : JSONObject.NULL);
				value = getValue("iban", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("iban", value != null ? value : JSONObject.NULL);
				value = getValue("accountOwner", responseMap, parsedObj);
				toPaymentInformationDetailV3Obj.put("accountOwner", value != null ? value : JSONObject.NULL);
				bankPaymentObj.put("toPaymentInformationDetailV3", toPaymentInformationDetailV3Obj);
				logger.error("Data sent:-bankPaymentObj--" + bankPaymentObj.toString());
				getResponse = destClient.callDestinationPOST("/upsert", "?$format=json", bankPaymentObj.toString());
				result = EntityUtils.toString(getResponse.getEntity(), "UTF-8");
				responseList.add(result);
				break;
			}
		}

		return responseList;
	}

	@PostMapping(value = "/postFieldGroup")
	public ArrayList<String> postFieldGroup(@RequestBody String requestBody,
			@RequestParam(value = "fieldGroupID", required = true) String fieldGroupID,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, fieldGroupID);
		return responseList;
	}

	@PostMapping(value = "/PerPersonal")
	public ArrayList<String> perPersonal(@RequestBody String requestBody,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, "PerPersonal");
		return responseList;
	}

	@PostMapping(value = "/PerPerson")
	public ArrayList<String> perPerson(@RequestBody String requestBody,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, "PerPerson");
		return responseList;
	}

	@PostMapping(value = "/AddPhone")
	public ArrayList<String> addPhone(@RequestBody String requestBody,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, "AddPhone");
		return responseList;
	}

	@PostMapping(value = "/AddAddress")
	public ArrayList<String> addAddress(@RequestBody String requestBody,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, "AddAddress");
		return responseList;
	}

	@PostMapping(value = "/BankPayment")
	public ArrayList<String> bankPayment(@RequestBody String requestBody,
			@RequestParam(value = "businessUnit", required = false) String businessUnitId,
			@RequestParam(value = "candidateId", required = true) String tempUserId, HttpServletRequest request)
			throws ClientProtocolException, IOException, URISyntaxException, NamingException {
		String userId = request.getUserPrincipal().getName();
		// adding dummy userId for now
		userId = tempUserId;
		// Parse JSON Array
		logger.error("USerId: " + userId);
		ArrayList<String> responseList = new ArrayList<String>();
		responseList = separateCall(requestBody, userId, businessUnitId, "BankPayment");
		return responseList;
	}

	private JSONObject parseRequestArray(JSONArray jsonArray) {
		JSONObject jsonFieldsObject = new JSONObject();
		JSONObject tempFieldObj = new JSONObject();
		for (int i = 0; i < jsonArray.length(); i++) {
			for (int j = 0; j < jsonArray.getJSONObject(i).getJSONArray("fields").length(); j++) {
				tempFieldObj = jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j)
						.getJSONObject("field");
				logger.error("technicalName***: " + tempFieldObj.getString("technicalName"));
				if (jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).has("value")) {
					jsonFieldsObject.put(tempFieldObj.getString("technicalName"),
							jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("value"));
				} else {
					jsonFieldsObject.put(tempFieldObj.getString("technicalName"), "");
				}
				logger.error("jsonFieldsObject" + jsonFieldsObject.toString());
			}
		}
		return jsonFieldsObject;
	}

	private String getValue(String key, HashMap<String, String> responseMap, JSONObject parsedObj) {
		if (responseMap.get(key) != null) {
			if (responseMap.get(key).length() > 0) {
				logger.error("Returned key from responseMap: " + key + " *** Value: " + responseMap.get(key));
				return (responseMap.get(key));
			} else {
				logger.error("Returned key from responseMap: " + key + " *** Value: null");
				return (null);
			}
		} else if (parsedObj.has(key)) {
			if (parsedObj.getString(key).length() > 0) {
				logger.error("Returned key From parsedObj: " + key + " *** Value: " + parsedObj.get(key));
				return (parsedObj.getString(key));
			} else {
				logger.error("Returned key from parsedObj: " + key + " *** Value: null");
				return (null);
			}
		} else {
			return (null);
		}
	}

	private JSONObject get__metadata(String URL, DestinationClient destClient)
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpResponse getResponse = destClient.callDestinationGET(URL, "");
		String responseString = EntityUtils.toString(getResponse.getEntity(), "UTF-8");

		// parsing Position Details
		JSONObject jsonObj = new JSONObject(responseString);
		JSONObject __metadata = jsonObj.getJSONObject("d").getJSONArray("results").getJSONObject(0)
				.getJSONObject("__metadata");
		return __metadata;
	}

	public String getValueFromPathJson(JSONObject positionEntity, String path, Map<String, String> compareMap) {
		String[] techPathArray = path.split("/");
//	String [] techPathArray = mapTemplateFieldProperties.getField().getValueFromPath().split("/");
		JSONArray tempArray;
		logger.debug("techPathArray" + techPathArray.length);
		for (int i = 0; i < techPathArray.length; i++) {
			logger.debug("Step" + i + "techPathArray.length - 1" + (techPathArray.length - 1));
			if (i != techPathArray.length - 1) {
				logger.debug("techPathArray[" + i + "]" + techPathArray[i]);
				if (techPathArray[i].contains("[]")) {

					tempArray = positionEntity.getJSONArray(techPathArray[i].replace("[]", ""));
					logger.debug("tempArray" + i + tempArray);
					if (tempArray.length() != 0) {
						String findObjectKey = techPathArray[i + 1].substring(techPathArray[i + 1].indexOf("(") + 1,
								techPathArray[i + 1].indexOf(")"));
						for (int j = 0; j < tempArray.length(); j++) {

							logger.debug("findObjectKey" + j + findObjectKey);
							if (tempArray.getJSONObject(j).get(findObjectKey).toString()
									.equalsIgnoreCase(compareMap.get(findObjectKey))) {

								positionEntity = tempArray.getJSONObject(j);
								logger.debug("positionEntity" + j + positionEntity);
								techPathArray[i + 1] = techPathArray[i + 1].replace("(" + findObjectKey + ")", "");
								logger.debug("techPathArray[i+1]" + (i + 1) + techPathArray[i + 1]);
								break;

							}
						}
					} else {

						break;
					}
				} else if (techPathArray[i].contains("[0]")) {

					tempArray = positionEntity.getJSONArray(techPathArray[i].replace("[0]", ""));
					if (tempArray.length() != 0) {
						positionEntity = tempArray.getJSONObject(0);
					} else {
						break;
					}
				} else {

					try {
						logger.debug("Object no Array" + techPathArray[i]);
						positionEntity = positionEntity.getJSONObject(techPathArray[i]);
						logger.debug("Object no Array positionEntity" + positionEntity);
					} catch (JSONException exception) {

						break;
					}
				}
			} else {
				try {
					logger.debug("techPathArray[" + i + "]" + techPathArray[i]);
					if (techPathArray[i].contains("<locale>")) {
						logger.debug("with label techPathArray[" + i + "]" + techPathArray[i]);
						techPathArray[i] = techPathArray[i].replace("<locale>", compareMap.get("locale"));
					}
					logger.debug("positionEntity.get(techPathArray[i]).toString()" + i
							+ positionEntity.get(techPathArray[i]).toString());
					return positionEntity.get(techPathArray[i]).toString();
				} catch (JSONException exception) {
					return "";
				}
			}
		}
		return "";

	}

	private String getCurrentDateForURI() {
		Instant now = java.time.Clock.systemUTC().instant();
		logger.error("CurrentDateTime: " + now.toString());
		return (now.toString());
	}

}
