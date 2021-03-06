package com.ihsinformatics.gfatmnotifications.common.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.Message;

public class CsvFileWriter {

	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	// CSV file header
	private static final String FILE_HEADER = "contact,message,project,send on";

	public static void writeCsvFile(String fileName, List<Message> messageList) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(fileName);

			// Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());

			// Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			// Write a new student object list to the CSV file
			for (Message message : messageList) {
				fileWriter.append(message.getContact());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(message.getPreparedMessage());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(Context.PROJECT_NAME);
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(message.getSendOn());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}
}
