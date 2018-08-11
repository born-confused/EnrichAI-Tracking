/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package product.enrichai.com.enrichaitracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "traccar.db";
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private CSVWriter csvWriter;

    public interface DatabaseHandler<T> {
        void onComplete(boolean success, T result);
    }

    private static abstract class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {

        private DatabaseHandler<T> handler;
        private RuntimeException error;

        public DatabaseAsyncTask(DatabaseHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                return executeMethod();
            } catch (RuntimeException error) {
                this.error = error;
                return null;
            }
        }

        protected abstract T executeMethod();

        @Override
        protected void onPostExecute(T result) {
            handler.onComplete(error == null, result);
        }
    }

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
        createCSV();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE position (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tripId INTEGER," +
                "deviceId TEXT," +
                "deviceType REAL," +
                "time INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "altitude REAL," +
                "speed REAL," +
                "course REAL," +
                "accuracy REAL," +
                "battery INTEGER," +
                "mock INTEGER)");

        db.execSQL("CREATE TABLE buffer (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tripId INTEGER," +
                "deviceId TEXT," +
                "deviceType REAL," +
                "time INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "altitude REAL," +
                "speed REAL," +
                "course REAL," +
                "accuracy REAL," +
                "battery INTEGER," +
                "mock INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS position;");
        db.execSQL("DROP TABLE IF EXISTS buffer;");
        onCreate(db);
    }

    private void insertPosition(Position position) {
        ContentValues values = new ContentValues();
        values.put("tripId", position.getTripId());
        values.put("deviceId", position.getDeviceId());
        values.put("deviceType", position.getDeviceType());
        values.put("time", position.getTime().getTime());
        values.put("latitude", position.getLatitude());
        values.put("longitude", position.getLongitude());
        values.put("altitude", position.getAltitude());
        values.put("speed", position.getSpeed());
        values.put("course", position.getCourse());
        values.put("accuracy", position.getAccuracy());
        values.put("battery", position.getBattery());
        values.put("mock", position.getMock() ? 1 : 0);


        db.insertOrThrow("buffer", null, values);
        db.insertOrThrow("position", null, values);
        Log.d(TAG, values.toString());
    }

    public void insertPositionAsync(final Position position, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                insertPosition(position);
                return null;
            }
        }.execute();
    }

    public ArrayList<Position> select() {
        ArrayList<Position> position_list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM position", null);
        try {
            int total_count = cursor.getCount();
            Log.d(TAG, "No. of rows = "+total_count);
            cursor.moveToFirst();
            while(total_count > 0) {
                Position position = new Position();

                position.setId(cursor.getLong(cursor.getColumnIndex("id")));
                position.setTripId(cursor.getInt(cursor.getColumnIndex("tripId")));
                position.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceId")));
                position.setDeviceType(cursor.getString(cursor.getColumnIndex("deviceType")));
                Long date = cursor.getLong(cursor.getColumnIndex("time"));
                position.setTime(new Date(date));
                position.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")) );
                position.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                position.setAltitude(cursor.getDouble(cursor.getColumnIndex("altitude")));
                position.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
                position.setCourse(cursor.getDouble(cursor.getColumnIndex("course")));
                position.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
                position.setBattery(cursor.getInt(cursor.getColumnIndex("battery")));
                position.setMock(cursor.getInt(cursor.getColumnIndex("mock")) > 0);


                try{
                    String body[] ={position.getId()+"", position.getTripId()+"", position.getDeviceId()+"",
                            position.getDeviceType()+"",
                            position.getTime()+"", position.getLatitude()+"",position.getLongitude()+"",
                            position.getAltitude()+"", position.getSpeed()+"", position.getCourse()+"",
                            position.getAccuracy()+"", position.getBattery()+"", position.getMock()+""};
                    csvWriter.writeNext(body);
                }catch (Exception e){
                    e.printStackTrace();
                }

                position_list.add(position);
                cursor.moveToNext();

                total_count--;
                Log.d(TAG, position.toString());
            }
        } finally {
            cursor.close();
            try {
                csvWriter.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Size of the list  = "+position_list.size());
        return position_list;
    }

    public void selectAsync(DatabaseHandler<ArrayList<Position>> handler) {
        new DatabaseAsyncTask<ArrayList<Position>>(handler) {
            @Override
            protected ArrayList<Position> executeMethod() {
                return select();
            }
        }.execute();
    }


    //***************************THIS SECTION IS FOR BUFFER TABLE*****************************//

    public Position selectPosition() {
        Position position = new Position();

        Cursor cursor = db.rawQuery("SELECT * FROM buffer ORDER BY id LIMIT 1", null);
        try {
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                position.setId(cursor.getLong(cursor.getColumnIndex("id")));
                position.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceId")));
                position.setDeviceType(cursor.getString(cursor.getColumnIndex("deviceType")));
                position.setTime(new Date(cursor.getLong(cursor.getColumnIndex("time"))));
                position.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                position.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                position.setAltitude(cursor.getDouble(cursor.getColumnIndex("altitude")));
                position.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
                position.setCourse(cursor.getDouble(cursor.getColumnIndex("course")));
                position.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
                position.setBattery(cursor.getInt(cursor.getColumnIndex("battery")));
                position.setMock(cursor.getInt(cursor.getColumnIndex("mock")) > 0);

            } else {
                return null;
            }
        } finally {
            cursor.close();
        }

        return position;
    }

    public void selectPositionAsync(DatabaseHandler<Position> handler) {
        new DatabaseAsyncTask<Position>(handler) {
            @Override
            protected Position executeMethod() {
                return selectPosition();
            }
        }.execute();
    }

    public void deletePosition(long id) {
        if (db.delete("buffer", "id = ?", new String[] { String.valueOf(id) }) != 1) {
            throw new SQLException();
        }
        Log.d("DatabaseHelper", id + " deleted");
    }

    public void deletePositionAsync(final long id, DatabaseHandler<Void> handler) {
        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                deletePosition(id);
                return null;
            }
        }.execute();
    }


    private void createCSV(){
        /*
         File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "report.csv");
         */
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "");
        if(!exportDir.exists())
            exportDir.mkdirs();
        File file = new File(exportDir, "locations.csv");
        try{
            file.createNewFile();
            csvWriter = new CSVWriter(new FileWriter(file));
            String header[] ={"Id", "Trip Id", "Device Id", "Device Type", "Time", "Latitude", "Longitude",
                    "Altitude", "Speed", "Course", "Accuracy", "Battery", "Mock"};
            csvWriter.writeNext(header);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}



