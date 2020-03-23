package com.nga.xtendhr.fastHire.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.fastHire.connections.HttpConnectionGET;
import com.nga.xtendhr.fastHire.utilities.CommonFunctions;
import com.nga.xtendhr.fastHire.utilities.ConstantManager;
import com.nga.xtendhr.fastHire.utilities.URLManager;

@RestController
@RequestMapping(value = ConstantManager.genAPI)
public class Language {

	private String userID;
	private static final String configName = "sfconfigname";
	private static final Logger logger = LoggerFactory.getLogger(Language.class);

	@GetMapping(value = ConstantManager.localLang, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getUserInfo(HttpServletRequest request) {

		Map<String, String> queryParams = new HashMap<>();
		
		// Get LoggedInUser
		this.userID = new CommonFunctions().getLoggedInUserNew(request);
		queryParams.put("userId", this.userID);

		URLManager genURL = new URLManager(queryParams, getClass().getSimpleName(), configName);
		String urlToCall = genURL.formURLToCall();
		logger.info(ConstantManager.lineSeparator + ConstantManager.urlLog + urlToCall + ConstantManager.lineSeparator);

		// Get details from server
		URI uri = CommonFunctions.convertToURI(urlToCall);
		HttpConnectionGET httpConnectionGET = new HttpConnectionGET(uri, URLManager.dConfiguration,
				Language.class);

		String result = httpConnectionGET.connectToServer();
		return result;
	}

}
