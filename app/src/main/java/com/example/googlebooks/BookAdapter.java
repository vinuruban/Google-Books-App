package com.example.googlebooks;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<BookObject> {
    public BookAdapter(Activity context, ArrayList<BookObject> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Here we are overriding a method from the ArrayAdapter class!
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        BookObject currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        ImageView thumbnail = (ImageView) listItemView.findViewById(R.id.tb_imageView);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        thumbnail.setImageBitmap(currentBook.getThumbnail());

        TextView title = (TextView) listItemView.findViewById(R.id.title_textView);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        title.setText(currentBook.getTitle());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView description = (TextView) listItemView.findViewById(R.id.description_textView);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        description.setText(currentBook.getDescription());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView authors = (TextView) listItemView.findViewById(R.id.authors_textView);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        authors.setText(currentBook.getAuthors());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView publishedDate = (TextView) listItemView.findViewById(R.id.publishedDate_textView);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        publishedDate.setText(currentBook.getPublishedDate());

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
