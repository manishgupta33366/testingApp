package com.nga.xtendhr.workStoppage.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.nga.xtendhr.workStoppage.model.StoppageDetails;
import com.nga.xtendhr.workStoppage.service.StoppageDetailsService;

/*
 * AppName: WorkStoppage
 * Employee WorkStoppage Employee Controller
 * 
 * @author	:	Manish Gupta  
 * @email	:	manish.g@ngahr.com
 * @version	:	0.0.1
 */

@RestController
@RequestMapping("/WorkStoppage/Employee")
public class Employee {

	@Autowired
	StoppageDetailsService stoppageDetailsService;

	Logger logger = LoggerFactory.getLogger(Employee.class);

	@GetMapping(value = "/login")
	public ResponseEntity<?> login(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			String loggedInUser = request.getUserPrincipal().getName();
			loggedInUser = loggedInUser.equals("S0014379281") || loggedInUser.equals("S0018269301")
					|| loggedInUser.equals("S0019013022") || loggedInUser.equals("S0020227452") ? "E00000815"
							: loggedInUser;
			// if (session != null) {
			// session.invalidate();
			// }
			session = request.getSession(true);
			session.setAttribute("loginStatus", "Success");
			session.setAttribute("loggedInUser", loggedInUser);
			JSONObject response = new JSONObject();
			response.put("login", "success");
			return ResponseEntity.ok().body(response.toString());// True to create a new session for the logged-in user
																	// as its the initial call
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/createWorkStoppage")
	public ResponseEntity<?> createWorkStoppage(@RequestBody String requestData, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(false);
			JSONObject requestObj = new JSONObject(requestData);
			StoppageDetails stoppageDetails = new StoppageDetails();
			Random random = new Random(); // to generate a random fileName
			int randomNumber = random.nextInt(987656554);
			stoppageDetails.setId(Integer.toString(randomNumber));
			// now converting encoded file back to bytes array
			stoppageDetails.setDocument(Base64.getDecoder().decode((String) session.getAttribute("file")));
			stoppageDetails.setEmployeeId((String) session.getAttribute("userId"));
			stoppageDetails.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(requestObj.getString("startDate")));
			stoppageDetails.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(requestObj.getString("endDate")));
			stoppageDetails.setStoppageType((String) session.getAttribute("stoppageType"));
			stoppageDetails.setDocumentType((String) session.getAttribute("fileName"));
			stoppageDetails.setIsApproved(false);
			stoppageDetails.setIsTherapeutic(requestObj.getBoolean("isTherapeutic"));
			if (requestObj.getBoolean("isTherapeutic")) {
				stoppageDetails.setTherapyStartDate(
						new SimpleDateFormat("yyyy-MM-dd").parse(requestObj.getString("therapyStartDate")));
				stoppageDetails.setTherapyEndDate(
						new SimpleDateFormat("yyyy-MM-dd").parse(requestObj.getString("therapyEndDate")));
			}
			String contentType = (String) session.getAttribute("contentType");
			logger.debug("Uploaded Orignal FileName: " + (String) session.getAttribute("fileName") + " ::: contentType:"
					+ contentType);
			stoppageDetailsService.create(stoppageDetails);
			return ResponseEntity.ok().body("Success!!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/callAzureCognitiveApi")
	public ResponseEntity<?> callAzureCognitiveApi(@RequestParam("file") MultipartFile multipartFile,
			HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession(true);
		}
		// encoding file to base64
		session.setAttribute("file", Base64.getEncoder().encodeToString(multipartFile.getBytes()));
		session.setAttribute("fileName", multipartFile.getName());
		session.setAttribute("fileName", multipartFile.getContentType());

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("user-file", multipartFile.getBytes());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Prediction-Key", "56cbaeb01116458182c9810633d151d6");
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(
				"https://southcentralus.api.cognitive.microsoft.com/customvision/v3.0/Prediction/f6830796-4ee9-4bd1-87fd-57df2afe01dc/classify/iterations/Iteration2/image",
				HttpMethod.POST, requestEntity, String.class);
		JSONObject responseObj = new JSONObject(response.getBody());
		responseObj.put("userId", request.getUserPrincipal().getName());
		session.setAttribute("userId", request.getUserPrincipal().getName());
		session.setAttribute("stoppageType",
				responseObj.getJSONArray("predictions").getJSONObject(0).getString("tagName"));
		return ResponseEntity.ok().body(responseObj.toString());
	}

	@GetMapping(value = "/getMyRequests")
	public ResponseEntity<?> getStoppageDetails(HttpServletRequest request) {
		try {
			return ResponseEntity.ok()
					.body(stoppageDetailsService.findByEmployeeId(request.getUserPrincipal().getName()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
