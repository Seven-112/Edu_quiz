package com.brightfuture.eduquiz.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    //database version
    private static int db_version = 1;

    //database name
    private static String db_name = "quiz_level.db";

    //database path string
    private String db_path;


    private SQLiteDatabase db;
    private final Context context;

    //table name
    public static String TABLE_NAME = "level";

    //column names

    public static String CATE_ID = "cat_id";
    public static String SUB_CATE_ID = "sub_cat_id";
    public static String LEVEL_NO = "level_no";


    public DBHelper(Context context) {
        super(context, db_name, null, db_version);
        this.context = context;

        File database = context.getDatabasePath(db_name);
        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            db_path = context.getDatabasePath(db_name).toString().replace(db_name, "");

        } else {
            //if database  exist get database from path
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

    }

    //delete database
    public void db_delete() {
        File file = new File(db_path + db_name);
        if (file.exists()) {
            file.delete();

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

    /*
     * insert level no
     */
    public void insertIntoDB(int cat_id, int sub_cat_id, int level_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME + " (" + CATE_ID + "," + SUB_CATE_ID + "," + LEVEL_NO + ") VALUES('" + cat_id + "', '" + sub_cat_id + "', '" + level_no + "');";
        db.execSQL(query);

    }

    /*
     *with this method we check if categoryId & subCategoryId is already exist or not in our database
     */
    public boolean isExist(int cat_id, int sub_cat_id) {
        db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ( " + CATE_ID + " = " + cat_id + " AND " + SUB_CATE_ID + " = " + sub_cat_id + ")", null);
        boolean exist = (cur.getCount() > 0);
        cur.close();
        System.out.println("---isExit  " + (cur.getCount() > 0));
        return exist;

    }

    /*
     * get level
     */
    public int GetLevelById(int cat_id, int sub_cat_id) {
        int level = 1;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE  (" + CATE_ID + "=" + cat_id + " AND " + SUB_CATE_ID + "=" + sub_cat_id + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                level = c.getInt(c.getColumnIndex(LEVEL_NO));
            } while (c.moveToNext());
        }
        return level;
    }

    /*
     * Update lavel
     */
    public void UpdateLevel(int cat_id, int sub_cat_id, int level_no) {
        db = this.getReadableDatabase();

        db.execSQL("update " + TABLE_NAME + " set level_no=" + level_no + " where (cat_id =" + cat_id + "  and  " + sub_cat_id + " = " + sub_cat_id + ")");
    }
}
