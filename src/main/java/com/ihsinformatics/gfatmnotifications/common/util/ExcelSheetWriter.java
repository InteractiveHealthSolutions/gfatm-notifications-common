package com.ihsinformatics.gfatmnotifications.common.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ihsinformatics.gfatmnotifications.common.model.Message;

public class ExcelSheetWriter {
	
	  private static String[] columns = {"EncounterType", "Patient", "contact", "SendTO","SendOn", "ScheduleDate","Location"};
	  
	  
	public static void writeFile(String fileName,List<Message> messageList) throws IOException, InvalidFormatException {
		
		  Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

	        /* CreationHelper helps us create instances of various things like DataFormat, 
	           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
	        CreationHelper createHelper = workbook.getCreationHelper();

	        // Create a Sheet
	        Sheet sheet = workbook.createSheet("SMS");

	        // Create a Font for styling header cells
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setFontHeightInPoints((short) 14);
	        headerFont.setColor(IndexedColors.RED.getIndex());

	        // Create a CellStyle with the font
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);

	        // Create a Row
	        Row headerRow = sheet.createRow(0);

	        // Create cells
	        for(int i = 0; i < columns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(columns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }

	        // Create Cell Style for formatting Date
	        CellStyle dateCellStyle = workbook.createCellStyle();
	        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

	        // Create Other rows and cells with employees data
	        int rowNum = 1;
	        for(Message message: messageList) {
	            Row row = sheet.createRow(rowNum++);

	            row.createCell(0)
	                    .setCellValue(message.getEncounterType());

	            row.createCell(1)
	                    .setCellValue(message.getPatientName());

	             row.createCell(2).setCellValue(message.getContactNumber());
	            
	            row.createCell(3)
                .setCellValue(message.getContactType());
	            row.createCell(4)
	                    .setCellValue(message.getSendOn());
	            
	            row.createCell(5)
                .setCellValue(message.getScheduleDate());
	            
	            row.createCell(6)
                .setCellValue(message.getLocation());
	     
	        }

			// Resize all columns to fit the content size
	     /*   for(int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }*/

	        // Write the output to a file
	        FileOutputStream fileOut = new FileOutputStream(fileName+".xlsx");
	        workbook.write(fileOut);
	        fileOut.close();

	        // Closing the workbook
	        workbook.close();
	        
	        System.out.println("EXCEL FILE IS CREATED");
	    
	}

}
