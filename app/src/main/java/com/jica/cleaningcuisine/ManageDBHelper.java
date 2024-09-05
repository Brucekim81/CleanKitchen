package com.jica.cleaningcuisine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ManageDBHelper extends SQLiteOpenHelper {

    public ManageDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE calendar_data(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "selected_items TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS calendar_data");
        onCreate(db);
    }

    public void insert(int year, int month, int day, String selectedItems) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("selected_items", selectedItems);
        db.insert("calendar_data", null, values);
        db.close();
    }

    public List<CalendarData> queryAll() {
        List<CalendarData> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("calendar_data", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            int month = cursor.getInt(cursor.getColumnIndex("month"));
            int day = cursor.getInt(cursor.getColumnIndex("day"));
            String selectedItems = cursor.getString(cursor.getColumnIndex("selected_items"));
            data.add(new CalendarData(year, month, day, selectedItems));
        }

        cursor.close();
        db.close();
        return data;
    }

    public int getCountForItem(String item) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM calendar_data WHERE selected_items LIKE ?", new String[]{"%" + item + "%"});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
