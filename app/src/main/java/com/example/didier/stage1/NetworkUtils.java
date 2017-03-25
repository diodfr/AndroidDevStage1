package com.example.didier.stage1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    static final String FILM_NETWORK = "FILM_NETWORK";
    private static final String BASE_URL_IMAGE = "http://image.tmdb.org/t/p/";
    private static final String BASE_URL_DB = "http://api.themoviedb.org/3";
    private static final String MOVIE_POPULAR = "movie/popular";
    private static final String MOVIE_RATED = "movie/top_rated";
    private static final String MOVIE_DETAIL = "movie";
    private static final String MOVIE_VIDEOS = "videos";
    private static final String MOVIE_REVIEWS = "reviews";


    private static String getOptimumDefinition(int definition) {
        if (definition < 154) {
            return "w92";
        } else if (definition < 185) {
            return "w154";
        } else if (definition < 342) {
            return "w185";
        } else if (definition < 500) {
            return "w342";
        } else if (definition < 780) {
            return "w500";
        } else if (definition < 1000) {
            return "w780";
        }

        return "original";
    }

    public static Uri getImageURL(String imageName) {
        Uri imageUri = Uri.parse(BASE_URL_IMAGE).buildUpon()
                .appendPath("w342")
                .appendEncodedPath(imageName)
                .build();
        Log.d(FILM_NETWORK, imageUri.toString());
        return imageUri;
    }

    public static Uri getImageURL(String imageName, int definition) {
        String definitionString = getOptimumDefinition(definition);
        Uri imageUri = Uri.parse(BASE_URL_IMAGE).buildUpon()
                .appendPath(definitionString)
                .appendEncodedPath(imageName)
                .build();
        Log.d(FILM_NETWORK, imageUri.toString());
        return imageUri;
    }

    public static URL getAPIURL(String apiKey, boolean popular, int page) {
        Uri api_key = Uri.parse(BASE_URL_DB).buildUpon()
                .appendEncodedPath(popular ? MOVIE_POPULAR : MOVIE_RATED)
                .appendQueryParameter("api_key", apiKey)
                .appendQueryParameter("page", Integer.toString(page))
                .build();

        try {
            return new URL(api_key.toString());
        } catch (MalformedURLException e) {
            Log.e(FILM_NETWORK, e.getLocalizedMessage());
        }
        return null;
    }

    public static URL getDetailAPIURL(String apiKey, int filmId) {
        Uri api_key = Uri.parse(BASE_URL_DB).buildUpon()
                .appendEncodedPath(MOVIE_DETAIL)
                .appendEncodedPath(String.valueOf(filmId))
                .appendQueryParameter("api_key", apiKey)
                .build();

        try {
            return new URL(api_key.toString());
        } catch (MalformedURLException e) {
            Log.e(FILM_NETWORK, e.getLocalizedMessage());
        }
        return null;
    }

    public static URL getVideosAPIURL(String apiKey, int filmId) {
        Uri api_key = Uri.parse(BASE_URL_DB).buildUpon()
                .appendEncodedPath(MOVIE_DETAIL)
                .appendEncodedPath(String.valueOf(filmId))
                .appendEncodedPath(MOVIE_VIDEOS)
                .appendQueryParameter("api_key", apiKey)
                .build();

        try {
            return new URL(api_key.toString());
        } catch (MalformedURLException e) {
            Log.e(FILM_NETWORK, e.getLocalizedMessage());
        }
        return null;
    }

    public static URL getReviewsAPIURL(String apiKey, int filmId) {
        Uri api_key = Uri.parse(BASE_URL_DB).buildUpon()
                .appendEncodedPath(MOVIE_DETAIL)
                .appendEncodedPath(String.valueOf(filmId))
                .appendEncodedPath(MOVIE_REVIEWS)
                .appendQueryParameter("api_key", apiKey)
                .build();

        try {
            return new URL(api_key.toString());
        } catch (MalformedURLException e) {
            Log.e(FILM_NETWORK, e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(FILM_NETWORK, url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
