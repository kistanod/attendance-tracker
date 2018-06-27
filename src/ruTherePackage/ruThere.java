package ruTherePackage;
// This is ruThere. This program is used as a demo to demostrate an update to a fix cell location on a Google sheet
// Todos: Please be sure to update the location of your client_secret.json file & the GoogleSheets id before running your program.
// Date: 6/1/18
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Date;
import java.util.Random;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class ruThere {

	private static final String databaseLocation = 	"/Users/kistanod/Projects/Java/ruThere/src/resources/database.json";
													//"D:/home/site/wwwroot/webapps/ROOT/WEB-INF/classes/resources/database.json";
													//"C:/Users/Dennis/IdeaProjects/ruThere/src/resources/database.json";
													//"/Users/Calvin/IdeaProjects/ruThere/src/resources/database.json";

					
	
	
	
	private static final String clientSecretLocation = "/Users/kistanod/Projects/Java/ruThere/src/resources/client_secret.json";
													//"D:/home/site/wwwroot/webapps/ROOT/WEB-INF/classes/resources/client_secret.json";
													//"C:/Users/Dennis/IdeaProjects/ruThere/src/resources/client_secret.json";
													//"/Users/Calvin/IdeaProjects/ruThere/src/resources/client_secret.json";
	
	
	
	
	private static final String credentialsLocation = "/Users/kistanod/Projects/Java/ruThere/src/resources/credentials/sheets.googleapis.com-java-quickstart.json";
													  //"D:/home/site/wwwroot/webapps/ROOT/WEB-INF/classes/resources/credentials/sheets.googleapis.com-java-quickstart.json";
													 //"C:/Users/Dennis/IdeaProjects/ruThere/src/resources/credentials/sheets.googleapis.com-java-quickstart.json";
													//"/Users/Calvin/IdeaProjects/ruThere/src/resources/credentials/sheets.googleapis.com-java-quickstart.json";
    /** Application name. */
    private static final String APPLICATION_NAME = "ruThere";

    /** Directory to store user credentials for this application. */
    
    private static final java.io.File DATA_STORE_DIR = new java.io.File(credentialsLocation);

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList( SheetsScopes.SPREADSHEETS );

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential authorize() throws IOException {
        // Load client secrets.
        // Todo: Change this text to the location where your client_secret.json resided
        InputStream in = new FileInputStream(
        		//"/Users/Calvin/IdeaProjects/ruThere/src/resources/client_secret.json");
        		//"D:/home/site/wwwroot/webapps/ROOT/WEB-INF/classes/resources/client_secret.json");
        clientSecretLocation);
        		
        		
            // ruThere.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static String getSpreadsheetId(Scanner kb, Sheets service) throws IOException{
        while(true) {
            try {
                System.out.print("Enter your spreadsheet ID--> ");
                String spreadsheetId = kb.nextLine();
                service.spreadsheets().values().get(spreadsheetId, "F1:F2");
                return spreadsheetId;
            } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
                System.out.println("Invalid Sheet ID");
            }
        }
    }
    //public static void main(String[] args) throws IOException {
    public static void generateCode(String email, String sheetName) throws IOException {
    	JSONObject professorInfo = (JSONObject) getEmailInfo(email);
        //String spreadsheetId = "1VZ63I-Wm-pPDM-MHNODscw9treysG-9JLUyZyAC7rj0";
        String spreadsheetId = (String) professorInfo.get("sheetId");
        Sheets service = getSheetsService();
        GoogleSheets mySheet = new GoogleSheets(spreadsheetId, service);
        mySheet.generateKeyFor(sheetName);
    }
    
    public static JSONObject validateEmail(String email) throws IOException {
    	JSONObject professorInfo = (JSONObject) getEmailInfo(email);
    	if(professorInfo != null) return professorInfo;
    	else return null;
    }
    
    public static boolean validatePassword(JSONObject professorInfo, String password) throws IOException {
    	System.out.println((String)professorInfo.get("password"));
    	if(((String)professorInfo.get("password")).equalsIgnoreCase(password)) {
    		return true;
    	}
    	else return false;
    }
    
    public static void submitAttendance(String email, String sheetName, String studentId, String key, String answer) throws IOException {
    	JSONObject professorInfo = (JSONObject) getEmailInfo(email);
        //String spreadsheetId = "1VZ63I-Wm-pPDM-MHNODscw9treysG-9JLUyZyAC7rj0";
        String spreadsheetId = (String) professorInfo.get("sheetId");
        Sheets service = getSheetsService();
        GoogleSheets mySheet = new GoogleSheets(spreadsheetId, service);
        mySheet.validateStudent(studentId, sheetName, key, answer);
    }

    //public static File getFile(String fileName) {
    //    return new File(ruThere.class.getClassLoader().getResource(fileName).getFile());
    //}

    public static Object getEmailInfo(String email){
        try {
            JSONObject database = (JSONObject) new JSONParser().parse(
                    new FileReader(
                            		//"/Users/Calvin/IdeaProjects/ruThere/src/resources/database.json")));
                            		//"D:/home/site/wwwroot/webapps/ROOT/WEB-INF/classes/resources/database.json")));
                            new File(databaseLocation)));
                            		
            Map emails = ((Map)database.get("emails"));
            Iterator<Map.Entry> iterator = emails.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = iterator.next();
                if(pair.getKey().equals(email)) {
                    return pair.getValue();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}