package com.nga.xtendhr.controller;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nga.xtendhr.model.TemplateTest;
import com.nga.xtendhr.service.TemplateTestService;

/*
 * AppName: DocGen
 * Complete DocGen code
 * 
 * @author	:	Manish Gupta  
 * @email	:	manish.g@ngahr.com
 * @version	:	0.0.1
 */

@RestController
@RequestMapping("/DocGen")
public class DocGeneration {
	@Autowired
	TemplateTestService templateTestService;

	@PostMapping(value = "/downloadTestTemplate")
	public void downloadTestTemplate(@RequestParam(name = "templateId") String templateId,
			@RequestParam(name = "inPDF") Boolean inPDF, @RequestBody String requestData, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			JSONArray requestTagsArray = new JSONObject(requestData).getJSONArray("tagsArray");
			TemplateTest templateTest = templateTestService.findById(templateId).get(0);// Template saved in DB
			InputStream inputStream = new ByteArrayInputStream(templateTest.getTemplate()); // creating inputstream from
																							// template to create docx
																							// file
			XWPFDocument docx = new XWPFDocument(inputStream);
			JSONObject tagObject;
			// using XWPFWordExtractor Class
			List<XWPFRun> runs;
			String text;
			for (XWPFParagraph p : docx.getParagraphs()) {
				runs = p.getRuns();
				if (runs != null) {
					for (XWPFRun r : runs) {
						text = r.getText(0);
						System.out.println(text);
						for (int i = 0; i < requestTagsArray.length(); i++) {
							tagObject = requestTagsArray.getJSONObject(i);
							if (text != null && text.contains(tagObject.getString("tag"))) {
								text = text.replace(tagObject.getString("tag"), tagObject.getString("value"));// replacing
																												// tag
																												// key
																												// with
																												// tag
																												// value
								r.setText(text, 0); // setting The text to run in the same document
							}
						}
					}
				}
			}

			Random random = new Random(); // to generate a random fileName
			int randomNumber = random.nextInt(987656554);
			FileOutputStream fileOutputStream = new FileOutputStream("GeneratedDoc_" + randomNumber); // Temp location

			if (!inPDF) {
				docx.write(fileOutputStream);// writing the updated Template to FileOutputStream // to save file
				byte[] encoded = Files.readAllBytes(Paths.get("GeneratedDoc_" + randomNumber)); // reading the file
																								// generated from
																								// fileOutputStream
				InputStream convertedInputStream = new ByteArrayInputStream(encoded);
				response.setContentType("application/msword");
				response.addHeader("Content-Disposition", "attachment; filename=" + "GeneratedDoc-" + ".docx"); // format
																												// is //
																												// important
				IOUtils.copy(convertedInputStream, response.getOutputStream());
			} else {
				PdfOptions options = PdfOptions.create().fontEncoding("windows-1250");
				PdfConverter.getInstance().convert(docx, fileOutputStream, options);
				byte[] encoded = Files.readAllBytes(Paths.get("GeneratedDoc_" + randomNumber)); // reading the file
																								// generated from
																								// fileOutputStream
				InputStream convertedInputStream = new ByteArrayInputStream(encoded);
				response.setContentType("application/pdf");
				response.addHeader("Content-Disposition", "attachment; filename=" + "GeneratedDoc-" + ".pdf"); // format
																												// is
																												// important
				IOUtils.copy(convertedInputStream, response.getOutputStream());
			}
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/uploadTestTemplate", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam(name = "templateId") String templateId,
			@RequestParam("file") MultipartFile multipartFile, HttpSession session) throws IOException {
		try {
			TemplateTest templateTest = new TemplateTest();
			templateTest.setId(templateId);
			templateTest.setTemplate(multipartFile.getBytes());
			templateTestService.create(templateTest);
			return ResponseEntity.ok().body("Success!!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
