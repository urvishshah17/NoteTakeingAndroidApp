package com.example.jd.notetake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqlHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "NoteDataBase";

    public SqlHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_Note_TABLE = "CREATE TABLE NoteAppData ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "note TEXT, " +
                "latitude TEXT, "+"lng TEXT, "+ "img BLOB)";

        db.execSQL(CREATE_Note_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS books");

        this.onCreate(db);
    }

    private static final String T_Note = "NoteAppData";

    private static final String TC_ID = "id";
    private static final String TC_Image = "img";
    private static final String TC_Note = "note";
    private static final String TC_Lat = "latitude";
    private static final String TC_Lon = "lng";
    private static final String[] COLUMNS = {TC_ID, TC_Note, TC_Lat, TC_Lon, TC_Image};

    public void AddNote(DataNote Note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TC_Note, Note.getNoteName());
        values.put(TC_Lat, Note.getLatitude());
        values.put(TC_Lon,Note.getLongitude());
        values.put(TC_Image, Note.getImg());

        db.insert(T_Note,
                null,
                values);
        db.close();
    }

    public int updateNote(DataNote Note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("note", Note.getNoteName());
        values.put(TC_Lat, Note.getLatitude());
        values.put(TC_Lon,Note.getLongitude());
        values.put("img", Note.getImg());

        int i = db.update(T_Note,
                values,
                TC_ID + " = ?",
                new String[]{String.valueOf(Note.getId())});

        db.close();

        return i;

    }
    public DataNote getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(T_Note,
                        COLUMNS,
                        " id = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);

        if (cursor != null)
            cursor.moveToFirst();

        DataNote Note = new DataNote();
        Note.setId(Integer.parseInt(cursor.getString(0)));
        Note.setNoteName(cursor.getString(1));
        Note.setLatitude(cursor.getString(2));
        Note.setLongitude(cursor.getString(3));
        Note.setImg(cursor.getBlob(4));

        return Note;
    }

    public List<DataNote> getAllNotes() {
        List<DataNote> Notes = new ArrayList<>();

        String query = "SELECT  * FROM " + T_Note;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        DataNote Note = null;
        if (cursor.moveToFirst()) {
            do {
                Note = new DataNote();
                Note.setId(Integer.parseInt(cursor.getString(0)));
                Note.setNoteName(cursor.getString(1));
                Note.setLatitude(cursor.getString(2));
                Note.setLongitude(cursor.getString(3));
                Note.setImg(cursor.getBlob(3));

                Notes.add(Note);
            } while (cursor.moveToNext());
        }

        return Notes;
    }



    public void deleteNote(DataNote Note)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(T_Note,
                TC_ID + " = ?",
                new String[]{String.valueOf(Note.getId())});

        db.close();

    }
}