package com.brightfuture.eduquiz.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brightfuture.eduquiz.model.Bookmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class BookmarkDBHelper extends SQLiteOpenHelper {

    //database version
    private static int db_version = 1;

    //database name
    private static String db_name = "quiz_bookmark2.db";
    //table name
    public static String TABLE_NAME = "tbl_bookmark";
    //colimn name
    public static String ID = "id";
    public static String QUESTION = "question";
    public static String ANSWER = "answer";
    public static String QUE_ID = "que_id";
    public static String SOLUTION = "extra_note";
    public static String IMAGE_URL = "image_url";
    public static String OPTION_A="option_a";
    public static String OPTION_B="option_b";
    public static String OPTION_C="option_c";
    public static String OPTION_D="option_d";
    private String db_path;
    private SQLiteDatabase db;
    private final Context context;


    ArrayList<Bookmark> bookArrayList = new ArrayList<>();


    public BookmarkDBHelper(Context context) {
        super(context, db_name, null, db_version);
        this.context = context;

        File database = context.getDatabasePath(db_name);
        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            db_path = context.getDatabasePath(db_name).toString().replace(db_name, "");

        } else {
            db_path = context.getDatabasePath(db_name).toString();

        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    //delete database
    public void db_delete() {
        File file = new File(db_path + db_name);
        if (file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }
    //Create a empty database on the system

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.v("DB Exists", "db exists");

        }

        boolean dbExist1 = checkDataBase();
        if (!dbExist1) {
            this.getWritableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    //Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = db_path + db_name;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException {
        String outFileName = db_path + db_name;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = context.getAssets().open(db_name);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    //insert chapter id in table to save as bookmarked
  /*  public void insertIntoDB(int que_id, String question, String answer, String solution, String image_url,String option_a,String option_b,String option_c,String option_d) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME + "(" + QUE_ID + "," + QUESTION + "," + ANSWER + "," + SOLUTION + "," + IMAGE_URL + " , " + OPTION_A + " ," + OPTION_B + " ," + OPTION_C + " ," + OPTION_D
                + ")VALUES('" + que_id + "', '" + question + "', '" + answer + "', '" + solution + "', '" + image_url + "', '" + option_a + "', '" + option_b + "', '" + option_c + "', '" + option_d + "');";
        db.execSQL(query);

    }*/

    public void insertIntoDB(int que_id, String question, String answer, String solution, String image_url,String option_a,String option_b,String option_c,String option_d) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(QUE_ID, que_id);
            values.put(QUESTION, question);
            values.put(ANSWER, answer);
            values.put(SOLUTION, solution);
            values.put(IMAGE_URL, image_url);
            values.put(OPTION_A, option_a);
            values.put(OPTION_B, option_b);
            values.put(OPTION_C, option_c);
            values.put(OPTION_D, option_d);

            db.insert(TABLE_NAME, null, values);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //delete bookmarked id
    public void delete_id(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String delete_query = " DELETE FROM " + TABLE_NAME + " WHERE " + QUE_ID + "=" + id;
        db.execSQL(delete_query);

    }

    //get Bookmarked list in android
    public ArrayList<Bookmark> getAllBookmarkedList() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY id ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Bookmark book = new Bookmark();
                book.setId(c.getInt(c.getColumnIndex(ID)));
                book.setQuestion(c.getString(c.getColumnIndex(QUESTION)));
                book.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                book.setQue_id(c.getInt(c.getColumnIndex(QUE_ID)));
                book.setSolution(c.getString(c.getColumnIndex(SOLUTION)));
                book.setImageUrl(c.getString(c.getColumnIndex(IMAGE_URL)));
                book.addOption(c.getString(c.getColumnIndex(OPTION_A)));
                book.addOption(c.getString(c.getColumnIndex(OPTION_B)));
                book.addOption(c.getString(c.getColumnIndex(OPTION_C)));
                book.addOption(c.getString(c.getColumnIndex(OPTION_D)));
                if (book.getOptions().size() == 4) {
                    bookmarkList.add(book);
                }


            } while (c.moveToNext());

        }
        return bookmarkList;
    }

    //get bookmarked id
    public int getBookmarks(int id) {
        int que_id = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + QUE_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                que_id = c.getInt(c.getColumnIndex(QUE_ID));
            } while (c.moveToNext());
        }
        return que_id;
    }

}
