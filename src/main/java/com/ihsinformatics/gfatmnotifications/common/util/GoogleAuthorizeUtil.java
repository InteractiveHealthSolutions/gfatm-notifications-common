package com.ihsinformatics.gfatmnotifications.common.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleAuthorizeUtil {

	 public static Credential authorize() throws IOException, GeneralSecurityException {    
	        
	        GoogleCredential credential = null;

	        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

	        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	        try {

	          InputStream jsonFileStream =
	        		  GoogleAuthorizeUtil.class.getClassLoader().getResourceAsStream("rulebook-1600666585678-338bf65ee72e.json");

	          GoogleCredential readJsonFile = GoogleCredential
	              .fromStream(jsonFileStream, httpTransport, JSON_FACTORY).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

	          credential = new GoogleCredential.Builder().setTransport(readJsonFile.getTransport())
	              .setJsonFactory(readJsonFile.getJsonFactory())
	              .setServiceAccountId(readJsonFile.getServiceAccountId())
	              .setServiceAccountScopes(readJsonFile.getServiceAccountScopes())
	              .setServiceAccountPrivateKey(readJsonFile.getServiceAccountPrivateKey()).build();
	        } catch (IOException exception) {
	          exception.printStackTrace();
	        }
	        return credential;
	        
	    }
	
}
