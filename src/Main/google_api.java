package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.json.JSONException;
/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */

/**
 * 使用google api
 * 記得加JSONException//public static void main(String[] args) throws IOException, JSONException{
 * google_api google = new google_api();
 * double count;
 * count = google.GetCount("颱風");
 * System.out.println(count);
 */

public class google_api {


  /*private static final String APPLICATION_NAME = "Hello Analytics";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String KEY_FILE_LOCATION = "/Users/finalily/Documents/workspace/Test/amy030619-d7ad57c61d45.p12";
  private static final String SERVICE_ACCOUNT_EMAIL = "871209266258-2ol9u7lr89npab9bdphvuct7i16vdlc4@developer.gserviceaccount.com";
  */
public double GetCount(String query) throws IOException, JSONException {
 
 String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
 //String query = "XXAR";
 String charset = "UTF-8";
 
 URL url = new URL(address + URLEncoder.encode(query, charset));

 StringBuilder builder = new StringBuilder();
 BufferedReader in = new BufferedReader(new InputStreamReader(
   url.openStream()));
 String str;
 
 while ((str = in.readLine()) != null) {
	 builder.append(str);
	 //System.out.println(str);
 }
 
 in.close();
 String response = builder.toString();

//顯示 Google ajax search api 回傳的結果

//System.out.println(response);

//使用 JSON 來解析結果

JSONObject json = new JSONObject(response);

//Google搜尋頁面數
double result ;
result=Double.valueOf(json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount"));

//System.out.println(result);
return result ;
}

  /*private static Analytics initializeAnalytics() throws Exception {
    // Initializes an authorized analytics service object.

    // Construct a GoogleCredential object with the service account email
    // and p12 file downloaded from the developer console.
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(JSON_FACTORY)
        .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
        .setServiceAccountPrivateKeyFromP12File(new File(KEY_FILE_LOCATION))
        .setServiceAccountScopes(AnalyticsScopes.all())
        .build();

    // Construct the Analytics service object.
    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();
  }


  private static String getFirstProfileId(Analytics analytics) throws IOException {
    // Get the first view (profile) ID for the authorized user.
    String profileId = null;

    // Query for the list of all accounts associated with the service account.
    Accounts accounts = analytics.management().accounts().list().execute();

    if (accounts.getItems().isEmpty()) {
      System.err.println("No accounts found");
    } else {
      String firstAccountId = accounts.getItems().get(0).getId();

      // Query for the list of properties associated with the first account.
      Webproperties properties = analytics.management().webproperties()
          .list(firstAccountId).execute();

      if (properties.getItems().isEmpty()) {
        System.err.println("No Webproperties found");
      } else {
        String firstWebpropertyId = properties.getItems().get(0).getId();

        // Query for the list views (profiles) associated with the property.
        Profiles profiles = analytics.management().profiles()
            .list(firstAccountId, firstWebpropertyId).execute();

        if (profiles.getItems().isEmpty()) {
          System.err.println("No views (profiles) found");
        } else {
          // Return the first (view) profile associated with the property.
          profileId = profiles.getItems().get(0).getId();
        }
      }
    }
    return profileId;
  }

  private static GaData getResults(Analytics analytics, String profileId) throws IOException {
    // Query the Core Reporting API for the number of sessions
    // in the past seven days.
    return analytics.data().ga()
        .get("ga:" + profileId, "7daysAgo", "today", "ga:sessions")
        .execute();
  }

  private static void printResults(GaData results) {
    // Parse the response from the Core Reporting API for
    // the profile name and number of sessions.
    if (results != null && !results.getRows().isEmpty()) {
      System.out.println("View (Profile) Name: "
        + results.getProfileInfo().getProfileName());
      System.out.println("Total Sessions: " + results.getRows().get(0).get(0));
    } else {
      System.out.println("No results found");
    }
  }*/
}