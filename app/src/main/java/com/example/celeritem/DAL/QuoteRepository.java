package com.example.celeritem.DAL;

import android.util.Log;

import com.example.celeritem.Interfaces.IQuoteDataAccess;
import com.example.celeritem.Interfaces.IQuoteListener;
import com.example.celeritem.MainActivity;
import com.example.celeritem.Model.Quote;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class QuoteRepository implements IQuoteDataAccess {

    /**
     * Gets a quote with an http request and uses the listener object as a callback to handle the object
     * @param listener
     */
    @Override
    public void getQuote(final IQuoteListener listener) {
        String url = "https://type.fit/api/quotes";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Quote quote = generateQuote(response);
                listener.onSuccess(quote);
            }
        });
    }

    /**
     * Makes an Quote object from the response object
     * @param response
     * @return a Quote object holding information about the quote such as author and content.
     */
    private Quote generateQuote(JSONArray response) {
        try {
            int random = (int) (Math.random() * response.length());
            String text = response.getJSONObject(random).getString("text");
            String author = response.getJSONObject(random).getString("author");
            if (author.equals("null"))
                author = "Unknown";

            Log.e(MainActivity.TAG, text);
            Log.e(MainActivity.TAG, author);
            return new Quote(author, text);
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "An error occurred parsing a JSON object");
            return new Quote("Error", "No quote found");
        }
    }
}
