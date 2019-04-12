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

    private static final String TABLE = "memo";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIME = "created";

    private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String CREATE_TABLE = "create table " + TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TITLE + " text, " + COLUMN_CONTENT + " text, " + COLUMN_TIME + " datetime default(datetime('now', 'localtime')))";
    private static final String DROP_TABLE = "drop table " + TABLE;

    private List<Memo> memos = new ArrayList<>();

    private MemoDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static MemoDBHelper getInstance(Context context) {
        return new MemoDBHelper(context, TABLE, null, 1);
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
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        return db.insert(TABLE, null, values);
    }

    public long update(Memo memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        values.put(COLUMN_TIME, formatDate(memo.getCreated()));
        return db.update(TABLE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(memo.getId())});
    }

    public long clear() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, null, null);
    }

    public long delete(Memo memo) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(memo.getId())});
    }

    public List<Memo> getAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, null, null);
        memos.clear();
        while (cursor.moveToNext()) {
            memos.add(new Memo(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)),
                    parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)))));
        }

        cursor.close();

        return memos;
    }

    @Nullable
    public Memo get(int id) {
        if (id < 0) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.moveToNext()) {
            return new Memo(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)),
                    parseDate(cursor.getString(cursor.getColumnIndex(COLUMN_TIME))));
        }

        cursor.close();

        return null;
    }

    private Date parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
        return format.format(date);
    }
}
