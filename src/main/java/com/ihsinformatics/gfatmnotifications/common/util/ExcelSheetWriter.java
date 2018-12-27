package com.ihsinformatics.gfatmnotifications.common.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ihsinformatics.gfatmnotifications.common.model.Message;

public class ExcelSheetWriter {

	private static String[] columns = { "NotificationType", "EncounterType", "Message", "Contact", "PreparedOn",
			"SendOn", "Recipient", "Rule" };

	public static void writeFile(String fileName, List<Message> messageList)
			throws IOException, InvalidFormatException {
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		// Create a Sheet
		Sheet sheet = workbook.createSheet("Notifications");
		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLUE.getIndex());
		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		Row headerRow = sheet.createRow(0);
		// Create cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}
		// Create Cell Style for formatting Date
//		CreationHelper createHelper = workbook.getCreationHelper();
//		CellStyle dateCellStyle = workbook.createCellStyle();
//		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

		// Create Other rows and cells with employees data
		int rowNum = 1;
		for (Message message : messageList) {
			int cellNum = 0;
			Row row = sheet.createRow(rowNum++);
			row.createCell(cellNum++).setCellValue(message.getRule().getType().toString());
			row.createCell(cellNum++).setCellValue(message.getEncounterType());
			row.createCell(cellNum++).setCellValue(message.getPreparedMessage());
			row.createCell(cellNum++).setCellValue(message.getContact());
			row.createCell(cellNum++).setCellValue(message.getPreparedOn());
			row.createCell(cellNum++).setCellValue(message.getSendOn());
			row.createCell(cellNum++).setCellValue(message.getRecipient());
			row.createCell(cellNum).setCellValue(message.getRule().toString());
		}
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(fileName);
		workbook.write(fileOut);
		fileOut.close();
		// Closing the workbook
		workbook.close();
	}
}
