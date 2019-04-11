package com.example.credibleinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="Credible";
    private static final String TABLE_NAME="User_Login";
    private static final int DB_VERSION=1;
    public DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"(ID INTEGER primary key AUTOINCREMENT ,EMAIL TEXT UNIQUE,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+TABLE_NAME+ "IF EXISTS");
    }

    public long storeUsers(Users u)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("EMAIL", u.getEmail());
        values.put("PASSWORD", u.getPassword());
        return db.insertOrThrow(TABLE_NAME, null, values);
    }

    public Cursor validate(String mail)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select EMAIL from "+TABLE_NAME+" where EMAIL = "+mail+"", null);
        return cursor;
    }
}
