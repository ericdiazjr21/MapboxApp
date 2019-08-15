package ericdiaz.program.gotennachallenge.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ericdiaz.program.gotennachallenge.model.Place;

public class PlacesDatabase extends SQLiteOpenHelper implements BaseDatabase {

    private static final String DATABASE_NAME = "PlacesDatabase.db";
    private static final String TABLE_NAME = "TransactionTable";
    private static final int SCHEMA = 1;
    private static PlacesDatabase singleDatabaseInstance;

    private PlacesDatabase(@NonNull final Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    public static PlacesDatabase getSingleDatabaseInstance(@NonNull final Context context) {
        if (singleDatabaseInstance == null) {
            singleDatabaseInstance = new PlacesDatabase(context);
        }
        return singleDatabaseInstance;
    }

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
    public boolean isDatabaseEmpty() {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME, null);
        return cursor == null;
    }

    @Override
    public void insertPlace(@NonNull final Place place) {
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME +
            " WHERE _id = '" + place.getId(),
          null);
        if (cursor == null) {
            getWritableDatabase().execSQL("INSERT INTO " + TABLE_NAME +
              "(_id, name, latitude, longitude, description) " +
              "VALUES('" +
              place.getId() + "', '" +
              place.getName() + "', '" +
              place.getLatitude() + "', '" +
              place.getLongitude() + "', '" +
              place.getDescription() + "', '" +
              "');");
        }
        cursor.close();
    }

    @Override
    public List<Place> getAllPlaces() {
        List<Place> allPlaces = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TABLE_NAME + ";", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    allPlaces.add(new Place(
                      cursor.getInt(cursor.getColumnIndex("_id")),
                      cursor.getString(cursor.getColumnIndex("name")),
                      cursor.getDouble(cursor.getColumnIndex("latitude")),
                      cursor.getDouble(cursor.getColumnIndex("longitude")),
                      cursor.getString(cursor.getColumnIndex("description"))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return allPlaces;
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
