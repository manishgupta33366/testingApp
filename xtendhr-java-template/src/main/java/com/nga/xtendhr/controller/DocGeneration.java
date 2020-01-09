package com.nga.xtendhr.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
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
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
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
			InputStream inputStream = new ByteArrayInputStream(templateTest.getTemplate()); // creating input-stream
																							// from
																							// template to create docx
																							// file
			XWPFDocument doc = new XWPFDocument(inputStream);

			replaceTags(doc, requestTagsArray); // Replace Tags in the doc

			Random random = new Random(); // to generate a random fileName
			int randomNumber = random.nextInt(987656554);
			FileOutputStream fileOutputStream = new FileOutputStream("GeneratedDoc_" + randomNumber); // Temp location

			if (!inPDF) {
				doc.write(fileOutputStream);// writing the updated Template to FileOutputStream // to save file
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
				PdfConverter.getInstance().convert(doc, fileOutputStream, options);
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

	private void replaceTags(XWPFDocument doc, JSONArray requestTagsArray) throws IOException, XmlException {
		// To replace Tags
		replaceParagraphTags(doc.getParagraphs(), requestTagsArray);
		replaceTableTags(doc.getTables(), requestTagsArray);
		replaceHeaderFooterTags(doc, requestTagsArray);
	}

	private void replaceHeaderFooterTags(XWPFDocument doc, JSONArray requestTagsArray)
			throws IOException, XmlException {
		// To replace Header and Footer Tags
		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc);

		// processing default Header
		XWPFHeader header = policy.getDefaultHeader();
		if (header != null) {
			replaceParagraphTags(header.getParagraphs(), requestTagsArray);
			replaceTableTags(header.getTables(), requestTagsArray);
		}
		// processing default footer
		XWPFFooter footer = policy.getDefaultFooter();
		if (footer != null) {
			replaceParagraphTags(footer.getParagraphs(), requestTagsArray);
			replaceTableTags(footer.getTables(), requestTagsArray);
		}
		// Processing Header and Footer of each page (In case there is of different
		// Header and Footer are set for each page)
		int numberOfPages = doc.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
		for (int i = 0; i < numberOfPages; i++) {
			// processing headers
			header = policy.getHeader(i);
			if (header != null) {
				replaceParagraphTags(header.getParagraphs(), requestTagsArray);
				replaceTableTags(header.getTables(), requestTagsArray);
			}
			// processing footers
			footer = policy.getFooter(i);
			if (footer != null) {
				replaceParagraphTags(footer.getParagraphs(), requestTagsArray);
				replaceTableTags(footer.getTables(), requestTagsArray);
			}
		}
	}

	private void replaceParagraphTags(List<XWPFParagraph> paragraphs, JSONArray requestTagsArray) {
		// To replace Tags in Paragraphs
		List<XWPFRun> runs;
		String text;
		JSONObject tagObject;
		for (XWPFParagraph p : paragraphs) {
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
							r.setText(text, 0); // setting The text to 'run' in the same document
						}
					}
				}
			}
		}
	}

	private void replaceTableTags(List<XWPFTable> tables, JSONArray requestTagsArray) {
		// To replace Tags in Tables
		for (XWPFTable xwpfTable : tables) {
			List<XWPFTableRow> row = xwpfTable.getRows();
			for (XWPFTableRow xwpfTableRow : row) {
				List<XWPFTableCell> cell = xwpfTableRow.getTableCells();
				for (XWPFTableCell xwpfTableCell : cell) {
					if (xwpfTableCell != null) {
						replaceParagraphTags(xwpfTableCell.getParagraphs(), requestTagsArray);
						List<XWPFTable> internalTables = xwpfTableCell.getTables();
						if (internalTables.size() != 0) {
							replaceTableTags(internalTables, requestTagsArray);
						}
					}
				}
			}
		}
	}

	@RequestMapping(value = "/uploadTestTemplate", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam(name = "templateId") String templateId,
			@RequestParam("file") MultipartFile multipartFile, HttpSession session) throws IOException {
		try {

			XWPFDocument document = new XWPFDocument(multipartFile.getInputStream());
			startProcessingWordFile(document);

			Random random = new Random(); // to generate a random fileName
			int randomNumber = random.nextInt(987656554);
			FileOutputStream fileOutputStream = new FileOutputStream("GeneratedDoc_" + randomNumber); // Temp location

			document.write(fileOutputStream);// writing the updated Template to FileOutputStream // to save file
			byte[] encoded = Files.readAllBytes(Paths.get("GeneratedDoc_" + randomNumber)); // reading the file
																							// generated from
																							// fileOutputStream

			TemplateTest templateTest = new TemplateTest();
			templateTest.setId(templateId);
			templateTest.setTemplate(encoded);
			templateTestService.create(templateTest);
			return ResponseEntity.ok().body("Success!!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private XWPFDocument startProcessingWordFile(XWPFDocument doc)
			throws FileNotFoundException, IOException, XmlException {
		formatParagraphTags(doc.getParagraphs());
		formatTableTags(doc.getTables());
		formatHeaderAndFooterTags(doc);
		return doc;
	}

	private void formatHeaderAndFooterTags(XWPFDocument doc) throws IOException, XmlException {
		// To format Header and Footer Tags
		XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc);

		// processing default Header
		XWPFHeader header = policy.getDefaultHeader();
		if (header != null) {
			formatParagraphTags(header.getParagraphs());
			formatTableTags(header.getTables());
		}
		// processing default footer
		XWPFFooter footer = policy.getDefaultFooter();
		if (footer != null) {
			formatParagraphTags(footer.getParagraphs());
			formatTableTags(footer.getTables());
		}
		// Processing Header and Footer of each page (In case there is of different
		// Header and Footer are set for each page)
		int numberOfPages = doc.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
		for (int i = 0; i < numberOfPages; i++) {
			// processing headers
			header = policy.getHeader(i);
			if (header != null) {
				formatParagraphTags(header.getParagraphs());
				formatTableTags(header.getTables());
			}
			// processing footers
			footer = policy.getFooter(i);
			if (footer != null) {
				formatParagraphTags(footer.getParagraphs());
				formatTableTags(footer.getTables());
			}
		}
	}

	private void formatParagraphTags(List<XWPFParagraph> paragraphs) {
		// To format paragraph Tags
		String pText;
		for (XWPFParagraph p : paragraphs) {
			System.out.println("Processing Paragraph...");
			List<XWPFRun> runs = p.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				pText = runs.get(i).getText(0);
				pText = pText == null ? "" : pText;// Check if pText is null
				if (pText.contains("@")) {// enter only if there is a "@" in the text
					i = createTagText(pText.lastIndexOf('@'), runs, i);
				}
			}
		}
	}

	private void formatTableTags(List<XWPFTable> tables) {
		// To format Table Tags
		for (XWPFTable xwpfTable : tables) {
			List<XWPFTableRow> row = xwpfTable.getRows();
			for (XWPFTableRow xwpfTableRow : row) {
				List<XWPFTableCell> cell = xwpfTableRow.getTableCells();
				for (XWPFTableCell xwpfTableCell : cell) {
					if (xwpfTableCell != null) {
						formatParagraphTags(xwpfTableCell.getParagraphs());
						List<XWPFTable> internalTables = xwpfTableCell.getTables();
						if (internalTables.size() != 0) {
							formatTableTags(internalTables);
						}
					}
				}
			}
		}
	}

	private int createTagText(int lastTagStartingAt, List<XWPFRun> runs, int runsOperatedTill) {
		if (runsOperatedTill == runs.size() - 1) { // return if last run as it will already be a completed tag
			return runsOperatedTill;
		}
		JSONObject isTag = checkIfItsATAG(runs, runsOperatedTill, null);
		if (isTag.getBoolean("isCase")) {
			return performRunOperations(runs, runsOperatedTill, null);
		}
		return runsOperatedTill++; // return with increment if its not a tag
	}

	private int performRunOperations(List<XWPFRun> runs, int runsOperatedTill, XWPFRun runToBeUpdated) {
		// This function is only called once it's confirmed that it's a tag
		String tempText;
		XWPFRun newRunToBeUpdated;
		String tagString;
		JSONObject isTag;
		if (runToBeUpdated == null) {
			newRunToBeUpdated = runs.get(runsOperatedTill);// save the run in temp
			tagString = runs.get(runsOperatedTill).getText(0);
		} else {
			newRunToBeUpdated = runToBeUpdated;
			tagString = runToBeUpdated.getText(0);
		}
		while (true) { // Now concatenate till '}' is found
			runsOperatedTill++;// move to next run
			tempText = runs.get(runsOperatedTill).getText(0);
			if (tempText.contains("}")) {
				// Now Checking if a new tag is started

				isTag = checkIfItsATAG(runs, runsOperatedTill, newRunToBeUpdated);
				if (isTag.getBoolean("isCase")) {
					runsOperatedTill = isTag.getInt("runsOperatedTill");
					tagString = tagString + tempText; // copy text to tagString
					System.out.println("inside case found:: tagString set: " + tagString);
					setRunText(tagString, newRunToBeUpdated, runs.get(runsOperatedTill)); // placing tagString at the
																							// correct run and removing
																							// text from the another run
					if (runsOperatedTill == runs.size() - 1) { // return if last run as it will be already complete tag
						return runsOperatedTill;
					}
					return performRunOperations(runs, runsOperatedTill, newRunToBeUpdated);
				}

				tagString = tagString + tempText; // copy text to tagString
				System.out.println("Found Closing Brace, text set: " + tagString);
				setRunText(tagString, newRunToBeUpdated, runs.get(runsOperatedTill)); // placing tagString at
																						// the correct run and
																						// removing text from
																						// the another run
				return (runsOperatedTill);// return till when runs are operated
			}
			System.out.println("tagString:*** " + tagString);
			tagString = tagString + tempText; // copy text to tagString
			runs.get(runsOperatedTill).setText("", 0); // remove text from run
		}
	}

	private void setRunText(String textToSet, XWPFRun runtoBeUpdatedWithText, XWPFRun runToBeUpdatedWithBlankText) {
		runToBeUpdatedWithBlankText.setText("", 0);
		runtoBeUpdatedWithText.setText(textToSet, 0);
	}

	private JSONObject checkIfItsATAG(List<XWPFRun> runs, int runsOperatedTill, XWPFRun runToBeUpdated) {

		/*
		 * First Checking if its CASE1
		 * 
		 * To check if the its exactly a tag
		 * 
		 * In case of CASE1 Its a CASE1 Tag only if the 'run' text contains '@' at the
		 * end and a '{' in the next run text
		 */

		int tempRunsOperatedTillForCase2 = runsOperatedTill;

		String tempText = runs.get(runsOperatedTill).getText(0);
		JSONObject responseObj = new JSONObject();
		if (tempText.lastIndexOf("@") != -1 && (tempText.length() == tempText.lastIndexOf("@") + 1)) { // '@' should be
																										// at the last
																										// character
			runsOperatedTill++; // increment runOperatedTill as we need to check '{' in next run text
			runsOperatedTill = checkIfTextIsNull(runs, runsOperatedTill, runToBeUpdated); // iterate Till text of runs
																							// are null / not having any
																							// text
			if (runs.get(runsOperatedTill).getText(0).charAt(0) == '{') {
				responseObj.put("isCase", true);
				responseObj.put("runsOperatedTill", runsOperatedTill);
				return responseObj;
			}
		}

		/*
		 * If its not CASE1 Check if it's CASE2
		 * 
		 * To check if the its exactly a tag
		 * 
		 * In case of CASE2 same run text must contain @{ then only its a tag else its
		 * not
		 */
		tempText = runs.get(tempRunsOperatedTillForCase2).getText(0);
		if (!(tempText.lastIndexOf("@") + 1 == tempText.length())) { // checking if '@' is at the last location, which
																		// is already checked in CASE1, indicates its
																		// not a tag
			if (tempText.charAt(tempText.lastIndexOf("@") + 1) == '{') {
				responseObj.put("isCase", true);
				responseObj.put("runsOperatedTill", tempRunsOperatedTillForCase2); // returning same index as need to
																					// operate from there
				return responseObj;
			}
		}
		responseObj.put("isCase", false);
		responseObj.put("runsOperatedTill", tempRunsOperatedTillForCase2++); // returning next index as its not a tag
		return responseObj;
	}

	private int checkIfTextIsNull(List<XWPFRun> runs, int runsOperatedTill, XWPFRun runToBeUpdated) {
		String tempText = runs.get(runsOperatedTill).getText(0);
		if (tempText.length() == 0) {
			runsOperatedTill++;
			return checkIfTextIsNull(runs, runsOperatedTill, runToBeUpdated); // recurse if its null
		}
		return runsOperatedTill; // return when its not null
	}
}
