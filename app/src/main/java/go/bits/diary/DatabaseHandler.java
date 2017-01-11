package go.bits.diary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Rushikesh on 31/12/16
 */

class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Diary";
    private static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME_SCRIBBLES = "_Scribbles";
    private static final String TABLE_NAME_SCRIBBLE_DATES = "_Scribbles_Dates";
    private static final String TABLE_NAME_TOPIC_NAMES = "_Topic_Names";
    private static final String KEY_TEXT = "Text";
    private static final String KEY_TOPIC_NAME = "Topic_Name";
    private static final String KEY_DATE = "Date";
    private static final String PREFIX_TOPIC = "_Topic";
    private static final String KEY_ID = "_ID";
    @SuppressLint("SimpleDateFormat")
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private boolean isReserved(String Topic){
        return Objects.equals(Topic, TABLE_NAME_SCRIBBLES)
                || Objects.equals(Topic, TABLE_NAME_TOPIC_NAMES)
                || Objects.equals(Topic, TABLE_NAME_SCRIBBLE_DATES)
                || this.getTOPIC_NAMES().contains(Topic);
    }

    @SuppressWarnings("WeakerAccess")
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_SCRIBBLES_TABLE = "CREATE TABLE " + TABLE_NAME_SCRIBBLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TEXT + " TEXT, "
                + KEY_DATE + " TEXT"
                + ")";
        String CREATE_DATES_TABLE = "CREATE TABLE " + TABLE_NAME_SCRIBBLE_DATES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_DATE + " TEXT"
                + ")";
        String CREATE_TOPIC_NAMES_TABLE = "CREATE TABLE " + TABLE_NAME_TOPIC_NAMES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_TOPIC_NAME + " TEXT"
                + ")";
        database.execSQL(CREATE_SCRIBBLES_TABLE);
        database.execSQL(CREATE_DATES_TABLE);
        database.execSQL(CREATE_TOPIC_NAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO (1) : DELETE ALL TABLES
        onCreate(database);
    }

    /**
     *
     * @param Topic the name of new topic
     * @return 1 if TOPIC_NAME already exists
     */
    int addTopic(String Topic){
        if(isReserved(Topic)){
            return 1;
        }
        else {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_TOPIC_NAME, Topic);
            long TOPIC_SUFFIX = database.insert(TABLE_NAME_TOPIC_NAMES, null, values);
            String CREATE_TABLE = " CREATE TABLE " + PREFIX_TOPIC + String.valueOf(TOPIC_SUFFIX) + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY, "
                    + KEY_TEXT + " TEXT"
                    + ")";
            database.execSQL(CREATE_TABLE);
            return 0;
        }
    }

    void addToday(){
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dateFormat.format(new Date()));
        SQLiteDatabase db = this.getWritableDatabase();
        long id =  db.insert(TABLE_NAME_SCRIBBLE_DATES, null, values);
    }

    void removeDate(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_SCRIBBLE_DATES, KEY_DATE + "=?", new String[]{date});
    }
    
     private String getTABLE_NAME(String Topic){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME_TOPIC_NAMES, 
                new String[]{KEY_ID}, KEY_TOPIC_NAME + "=?", new String[]{Topic},
                null, null, null, null);
        cursor.moveToFirst();
        String SUFFIX = String.valueOf(cursor.getLong(0));
        cursor.close();
        return PREFIX_TOPIC + SUFFIX;
    }

    /**
     *
     * @param Topic name of topic to be removed, cannot be same as $TABLE_NAME_SCRIBBLES
     * @return  1 if tries to delete TABLE_NAME_SCRIBBLES
     */
    @SuppressWarnings("unused")
    int removeTopic(String Topic){
        List <String> FORBIDDEN = new ArrayList<>();
        FORBIDDEN.add(TABLE_NAME_SCRIBBLE_DATES);
        FORBIDDEN.add(TABLE_NAME_SCRIBBLES);
        FORBIDDEN.add(TABLE_NAME_TOPIC_NAMES);
        if(FORBIDDEN.contains(Topic)){
            return 1;
        }
        SQLiteDatabase database = this.getWritableDatabase();
        String TABLE_NAME = this.getTABLE_NAME(Topic);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.delete(TABLE_NAME_TOPIC_NAMES, KEY_TOPIC_NAME +" =?", new String[]{Topic});
        return 0;
    }

    /**
     *
     * @param NOTE text
     * @param Topic : null => TABLE_NAME_SCRIBBLES, today
     */
    void addNote(String NOTE, @Nullable String Topic){
        if(NOTE.length() == 0){
            return;
        }
        String TABLE_NAME;
        if(Topic == null || Objects.equals(Topic, TABLE_NAME_SCRIBBLES)){
            TABLE_NAME = TABLE_NAME_SCRIBBLES;
        }
        else {
            TABLE_NAME = this.getTABLE_NAME(Topic);
        }
        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, NOTE);
        Log.d("text", NOTE);

        SQLiteDatabase database = this.getWritableDatabase();
        if(Objects.equals(TABLE_NAME, TABLE_NAME_SCRIBBLES)) {
            List<String> SCRIBBLE_DATES = this.getSCRIBBLE_DATES();
            String DATE = dateFormat.format(new Date());
            values.put(KEY_DATE, DATE);
            if(!SCRIBBLE_DATES.contains(DATE)){
                ContentValues values1 = new ContentValues();
                values1.put(KEY_DATE, DATE);
                database.insert(TABLE_NAME_SCRIBBLE_DATES, null, values1);
            }
        }
        @SuppressWarnings("unused") long id = database.insert(TABLE_NAME, null, values);
    }

    List<String> getSCRIBBLE_DATES(){
        SQLiteDatabase database = this.getReadableDatabase();
        String rawQuery = "SELECT * FROM " + TABLE_NAME_SCRIBBLE_DATES;
        Cursor cursor = database.rawQuery(rawQuery, null);
        List<String> SCRIBBLE_DATES = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                SCRIBBLE_DATES.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return SCRIBBLE_DATES;
    }

    List<Note> getScribblesDated(Date date){
        String DATE = dateFormat.format(date);
        SQLiteDatabase database = this.getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME_SCRIBBLES,
                new String[]{KEY_ID, KEY_TEXT},
                KEY_DATE + "=?", new String[]{DATE},
                null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Long ID = cursor.getLong(0);
                String NoteText = cursor.getString(1);
                Note note = new Note(ID, NoteText, null);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return notes;
    }

    Note getNote(String Topic, Long ID){
        SQLiteDatabase database = this.getReadableDatabase();
        Note note;
        String TABLE_NAME;
        if(Objects.equals(Topic, TABLE_NAME_SCRIBBLES)){
            TABLE_NAME = TABLE_NAME_SCRIBBLES;
        }
        else {
            TABLE_NAME = this.getTABLE_NAME(Topic);
        }
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{KEY_TEXT},
                KEY_ID + "=?", new String[]{String.valueOf(ID)},
                null, null, null, null);
        cursor.moveToFirst();
        note = new Note(ID, cursor.getString(0), Topic);
        cursor.close();
        
        return note;
    }
    List<String> getTOPIC_NAMES(){
        SQLiteDatabase database = this.getWritableDatabase();
        String rawQuery = "SELECT * FROM " + TABLE_NAME_TOPIC_NAMES;
        Cursor cursor = database.rawQuery(rawQuery, null);
        List <String> TOPIC_NAMES = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                TOPIC_NAMES.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return TOPIC_NAMES;
    }

    List<Note> getTopicNotes(String Topic){
        SQLiteDatabase database = this.getReadableDatabase();
        String TABLE_NAME = this.getTABLE_NAME(Topic);
        List<Note> notes = new ArrayList<>();
        String rawQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(rawQuery, null);
        if(cursor.moveToNext()){
            do{
                Long ID = cursor.getLong(0);
                String Text = cursor.getString(1);
                Note note = new Note(ID, Text, Topic);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    int updateNote(Note note){
        Long ID = note.getID();
        String Text = note.getText();
        String TABLE_NAME;
        if(Objects.equals(note.getFolderName(), TABLE_NAME_SCRIBBLES)){
            TABLE_NAME = TABLE_NAME_SCRIBBLES;
        }
        else {
            TABLE_NAME = this.getTABLE_NAME(note.getFolderName());
        }
        ContentValues values = new ContentValues();
        SQLiteDatabase database = this.getWritableDatabase();
        values.put(KEY_TEXT, Text);
        return database.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(ID)});
    }
    /**
     * Be Warned!
     * @param note to be deleted
     */
    void removeNote(Note note){
        Long ID = note.getID();
        String TABLE_NAME;
        if(Objects.equals(note.getFolderName(), TABLE_NAME_SCRIBBLES)){
            TABLE_NAME = TABLE_NAME_SCRIBBLES;
        }
        else {
            TABLE_NAME = this.getTABLE_NAME(note.getFolderName());
        }
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, KEY_ID + "=?", new String[]{String.valueOf(ID)});
    }
}