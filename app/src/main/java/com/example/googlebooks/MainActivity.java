package com.example.googlebooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    /** Tag for log messages */
    private static String LOG_TAG = MainActivity.class.getName();

    /** Adapter for the list of books */
    private BookAdapter adapter;

    /** URL for book data from the USGS dataset */
    private String urlAttachment =
            "https://www.googleapis.com/books/v1/volumes?q=";
    private String url = "";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /** TextView that is displayed when the list is empty */
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
            String text = editText.getText().toString();
            String convertText = text.replaceAll(" ", "+").toLowerCase();
            url = urlAttachment + convertText;
                Log.e(LOG_TAG, "URL : " + url);

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                // Create a new adapter that takes an EMPTY list of books as input
                adapter = new BookAdapter(MainActivity.this, new ArrayList<BookObject>());

                final ListView bookListView = (ListView) findViewById(R.id.list);

                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                bookListView.setAdapter(adapter);

                //Below is the view that will be viewed if there is no internet connection or no data retrieved
                emptyStateTextView = (TextView) findViewById(R.id.empty_view);
                bookListView.setEmptyView(emptyStateTextView);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                    // Start the AsyncTask to fetch the earthquake data
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(url);
                    Log.e(LOG_TAG, "Passes BookAsyncTask call");
                } else {
                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    loadingIndicator.setVisibility(View.GONE);

                    // Update empty state with no connection error message
                    emptyStateTextView.setText(R.string.no_internet_connection);
                }

            }
        });

    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<BookObject>> {

        @Override
        protected List<BookObject> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            List<BookObject> result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<BookObject> bookObjects) {
            Log.e(LOG_TAG, "Passes onPostExecute");

            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No books found."
            emptyStateTextView.setText(R.string.no_books);

            // Clear the adapter of previous earthquake data
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (bookObjects != null && !bookObjects.isEmpty()) {
                adapter.addAll(bookObjects); //remember, we earlier passed an empty list into the adapter (" adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>()); "). Now, we add all the data into the adapter.
            }
        }
    }
}
