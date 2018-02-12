package edu.temple.encryption;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;

public class Keys extends ContentProvider {
    private static final int KEYS = 1;
    Context appContext;
    SharedPreferences sharedPrefs;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sURIMatcher.addURI("thisapp.Keys", "keys", KEYS);
    }


    public Keys() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match)
        {
            case KEYS:
                return "keys";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //sharedPreferences.edit().putString("test1", "test1val").commit();
        sharedPrefs.edit().putString("privateKey", (String)(values.get("privateKey"))).putString("publicKey", (String)(values.get("publicKey"))).apply();
        //sharedPrefs.edit().putString("publicKey", (String)(values.get("publicKey"))).apply();

        return uri;
    }

    /**
     * set the shared preferences
     * @param p
     */
    public void setSharedPrefs(SharedPreferences p){
        this.sharedPrefs = p;
    }

    @Override
    public boolean onCreate() {
        sharedPrefs = getContext().getApplicationContext().getSharedPreferences("myprefs", MODE_PRIVATE);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] columns = {"privateKey", "publicKey"};
        MatrixCursor cursor = new MatrixCursor(columns);

        String[] row = new String[2];
        row[0] = sharedPrefs.getString("privateKey", null);
        row[1] = sharedPrefs.getString("publicKey", null);
        cursor.addRow(row);

        return cursor;
//        int match = sURIMatcher.match(uri);
//        switch (match)
//        {
//            case PRIVATE_KEYS:
//                return "vnd.android.cursor.dir/person";
//            case PUBLIC_KEYS:
//                return "vnd.android.cursor.item/person";
//            default:
//                return null;
//        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
