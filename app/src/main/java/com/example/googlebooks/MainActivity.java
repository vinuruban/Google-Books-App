package com.example.googlebooks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookObject>> {


    /** Tag for log messages */
    private static String LOG_TAG = BookLoader.class.getName();

    /** Adapter for the list of books */
    private BookAdapter adapter;

    /** URL for book data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=harry+potter";

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

        // Create a new adapter that takes an EMPTY list of books as input
        adapter = new BookAdapter(this, new ArrayList<BookObject>());

        ListView bookListView = (ListView) findViewById(R.id.list);

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
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            Log.e(LOG_TAG, "Passes initLoader");
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }


//        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            // The code in this method will be executed when the numbers category is clicked on.
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                BookObject currentBookObject = adapter.getItem(position);
//                Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(currentBookObject.getUrl()));
//                startActivity(implicit);
//            }
//        });

    }

    @Override
    public Loader<List<BookObject>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "Passes onCreateLoader");
        // Create a new loader for the given URL
        return new BookLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<BookObject>> loader, List<BookObject> bookObjects) {
        Log.e(LOG_TAG, "Passes onLoadFinished");

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        emptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        adapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (bookObjects != null && !bookObjects.isEmpty()) {
            adapter.addAll(bookObjects);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BookObject>> loader) {
        Log.e(LOG_TAG, "Passes onLoaderReset");
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    /**
     * Loads a list of books by using an AsyncTask to perform the
     * network request to the given URL.
     */
    public static class BookLoader extends AsyncTaskLoader<List<BookObject>> {

        /** Query URL */
        private String mUrl;

        /**
         * Constructs a new {@link BookLoader}.
         *
         * @param context of the activity
         * @param url to load data from
         */
        public BookLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            Log.e(LOG_TAG, "Passes onStartLoading");

            forceLoad(); //load starts in the loadInBackground()
        }

        /**
         * This is on a background thread.
         */
        @Override
        public List<BookObject> loadInBackground() {
            Log.e(LOG_TAG, "Passes loadInBackground");
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of books.
            List<BookObject> bookObjects = QueryUtils.fetchBookData(mUrl);
            Log.e(LOG_TAG, "Passes QueryUtils.fetchBookData");
            return bookObjects;
        }
    }
}
