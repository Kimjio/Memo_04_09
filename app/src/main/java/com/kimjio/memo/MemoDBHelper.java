package com.kimjio.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoDBHelper extends SQLiteOpenHelper {

    public static final String TABLE = "memo";
    private static final String CREATE_TABLE = "create table memo(id integer primary key autoincrement, title text, content text, created datetime default(datetime('now', 'localtime')))";
    private static final String DROP_TABLE = "drop table memo";

    public MemoDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public long insert(Memo memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", memo.getTitle());
        values.put("content", memo.getContent());
        return db.insert(TABLE, null, values);
    }

    public long update(Memo memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", memo.getTitle());
        values.put("content", memo.getContent());
        values.put("created", formatDate(memo.getCreated()));
        return db.update(TABLE, values, "id=?", new String[]{String.valueOf(memo.getId())});
    }

    public long clear() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, null, null);
    }

    public long delete(Memo memo) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, "id=?", new String[]{String.valueOf(memo.getId())});
    }

    public List<Memo> getAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, null, null);
        List<Memo> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(new Memo(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    parseDate(cursor.getString(cursor.getColumnIndex("created")))));
        }

        cursor.close();

        return result;
    }

    @Nullable
    public Memo get(int id) {
        if (id < 0) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, "id == " + id, null, null, null, null, null);
        if (cursor.moveToNext()) {
            return new Memo(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    parseDate(cursor.getString(cursor.getColumnIndex("created"))));
        }

        cursor.close();

        return null;
    }

    private Date parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
}
