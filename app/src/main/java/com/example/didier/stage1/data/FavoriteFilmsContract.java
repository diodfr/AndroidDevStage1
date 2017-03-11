package com.example.didier.stage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for favorite movies
 */

public class FavoriteFilmsContract {

    public static final String CONTENT_AUTHORITY = "com.example.didier.stage1";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for movie app.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES_FILMS = "favorites";

    /* Inner class that defines the table contents of the Favorite table */
    public static final class FavoriteEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES_FILMS)
                .build();

        /* Used internally as the name of our favorites table. */
        public static final String TABLE_NAME = "favorites";

        /* name of movie */
        public static final String COLUMN_NAME = "Name";

        public static Uri buildFavoriteUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
