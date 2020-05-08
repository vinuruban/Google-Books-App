package com.example.googlebooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class QueryUtils {


    private QueryUtils() {
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Query the USGS dataset and return a list of {@link BookObject} objects.
     */

    public static List<BookObject> fetchBookData(String requestUrl) {

        //Below is the code to sleep. Used to check the ProgressBar!
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<BookObject> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }



    //    CODE BELOW IS NEEDED TO EXECUTE THE NETWORK REQUEST. NO NEED TO AMEND IT!!



    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }



    //    BELOW IS THE CODE THAT I MUST AMEND. IT DETAILS HOW TO EXTRACT SPECIFIC DATA WITHIN THE JSON



    /**
     * Return a list of {@link BookObject} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<BookObject> extractFeatureFromJson(String bookJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<BookObject> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject jsonObj = new JSONObject(bookJSON);

            // Getting JSON Array node
            JSONArray bookArray = jsonObj.getJSONArray("items");
            // looping through All bookArray
            for (int i = 0; i < bookArray.length(); i++) {

                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfoObj = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfoObj.getString("title");

                String description = volumeInfoObj.getString("description");

                JSONArray authorsArray = volumeInfoObj.getJSONArray("authors");
                String authorsToString = authorsArray.toString();
                String authors = authorsToString.replaceAll("\",\"", ", ").replaceAll(Pattern.quote("["), "").replaceAll("\"", "").replaceAll(Pattern.quote("]"), "");

                String publishedDate = volumeInfoObj.getString("publishedDate");

                JSONObject imageLinksObj = volumeInfoObj.getJSONObject("imageLinks");
                String thumbnailUrl = imageLinksObj.getString("thumbnail");
                Bitmap thumbnail = null;
                try {
                    InputStream in = new java.net.URL(thumbnailUrl).openStream();
                    thumbnail = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: " + e.getMessage());
                }

                books.add(new BookObject(thumbnail, title, description, authors, publishedDate));
                }
            } catch(JSONException e){
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }

            // Return the list of books
            return books;

    }

}
