package ericdiaz.program.gotennachallenge.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import ericdiaz.program.gotennachallenge.model.Place;

/**
 * basic SQLiteOpenHelper implementation for storing places data
 * <p>
 * Created: 8/15/19
 *
 * @author Eric Diaz
 */

public final class PlacesDatabase extends SQLiteOpenHelper implements BaseDatabase {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private static final String DATABASE_NAME = "PlacesDatabase.db";
    private static final String TABLE_NAME = "PlacesTable";
    private static final int SCHEMA = 1;
    private static PlacesDatabase singleDatabaseInstance;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    private PlacesDatabase(@NonNull final Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    //==============================================================================================
    // Class Static Methods
    //==============================================================================================

    public static PlacesDatabase getSingleDatabaseInstance(@NonNull final Context context) {
        if (singleDatabaseInstance == null) {
            singleDatabaseInstance = new PlacesDatabase(context);
        }
        return singleDatabaseInstance;
    }

    //==============================================================================================
    // Class Life-cycle Methods
    //==============================================================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
          " (" +
          "_id INTEGER PRIMARY KEY, " +
          "name TEXT, " +
          "latitude REAL, " +
          "longitude REAL," +
          "description TEXT" +
          ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //==============================================================================================
    // BaseDatabase Interface Methods Implementation
    //==============================================================================================

    @Override
    public boolean isEmpty() {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME, null);
        return cursor.getCount() == 0;
    }

    @Override
    public void insertPlace(@NonNull final Place place) {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME +
            " WHERE _id = '" + place.getId() + "';",
          null);

        if (cursor.getCount() == 0) {
            getWritableDatabase().execSQL("INSERT INTO " + TABLE_NAME +
              "(_id, name, latitude, longitude, description) " +
              "VALUES('" +
              place.getId() + "', '" +
              place.getName() + "', '" +
              place.getLatitude() + "', '" +
              place.getLongitude() + "', '" +
              place.getDescription() + "');");
        }
        cursor.close();
    }

    @Override
    public Place[] getAllPlaces() {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME + ";", null);

        Place[] placesArray = cursor != null ? new Place[cursor.getCount()] : null;

        int index = 0;

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    placesArray[index] = (new Place(
                      cursor.getInt(cursor.getColumnIndex("_id")),
                      cursor.getString(cursor.getColumnIndex("name")),
                      cursor.getDouble(cursor.getColumnIndex("latitude")),
                      cursor.getDouble(cursor.getColumnIndex("longitude")),
                      cursor.getString(cursor.getColumnIndex("description"))
                    ));

                    index++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return placesArray;
    }

    @Override
    public void deletePlace(@NonNull Place place) {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME +
            " WHERE _id = '" + place.getId(), null);

        if (cursor != null) {
            getWritableDatabase().execSQL("DELETE * FROM " + TABLE_NAME +
              " WHERE _id = '" + place.getId());
        }
    }
}
