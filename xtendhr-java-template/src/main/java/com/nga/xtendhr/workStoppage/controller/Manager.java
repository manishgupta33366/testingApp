package com.nga.xtendhr.workStoppage.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nga.xtendhr.workStoppage.model.StoppageDetails;
import com.nga.xtendhr.workStoppage.service.StoppageDetailsService;

/*
 * AppName: WorkStoppage
 * Employee WorkStoppage Manager Controller
 * 
 * @author	:	Manish Gupta  
 * @email	:	manish.g@ngahr.com
 * @version	:	0.0.1
 */

@RestController
@RequestMapping("/WorkStoppage/Employee")
public class Manager {
	@Autowired
	StoppageDetailsService stoppageDetailsService;

	Logger logger = LoggerFactory.getLogger(Manager.class);

	@GetMapping(value = "/getAllApprovedStoppageDetails")
	public ResponseEntity<?> getAllApprovedStoppageDetails(HttpServletRequest request) {
		try {
			return ResponseEntity.ok().body(stoppageDetailsService.findAllApproved());
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/getAllNonApprovedStoppageDetails")
	public ResponseEntity<?> getAllNonApprovedStoppageDetails(HttpServletRequest request) {
		try {
			return ResponseEntity.ok().body(stoppageDetailsService.findAllNotApproved());
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/approveWorkStoppage")
	public ResponseEntity<?> approveWorkStoppage(@RequestBody String requestData, HttpServletRequest request) {
		try {
			JSONArray requestArray = new JSONObject(requestData).getJSONArray("data");
			StoppageDetails stoppageDetails = new StoppageDetails();
			JSONObject tempJsonObject;
			for (int i = 0; i < requestArray.length(); i++) {
				tempJsonObject = requestArray.getJSONObject(i);
				stoppageDetails = stoppageDetailsService.findById(tempJsonObject.getString("id"));
				stoppageDetails.setIsApproved(tempJsonObject.getBoolean("isApproved"));
				stoppageDetailsService.update(stoppageDetails);
			}
			return ResponseEntity.ok().body("Success!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
