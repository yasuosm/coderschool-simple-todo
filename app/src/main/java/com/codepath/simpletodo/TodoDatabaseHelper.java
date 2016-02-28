package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annt on 2/28/16.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "TodoDatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODO = "todo";

    // Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_DUE_TIMESTAMP = "dueTimestamp";

    private static TodoDatabaseHelper instance;

    public static synchronized TodoDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new TodoDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_TEXT + " TEXT," +
                KEY_TODO_DUE_TIMESTAMP + " INTEGER" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    // Insert a item into the database
    public void addTodo(Todo item) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_TEXT, item.getText());
            values.put(KEY_TODO_DUE_TIMESTAMP, item.getDueTimestamp());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add item to database");
        } finally {
            db.endTransaction();
        }
    }

    public Todo getTodo(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, new String[]{KEY_TODO_ID, KEY_TODO_TEXT,
                        KEY_TODO_DUE_TIMESTAMP}, KEY_TODO_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (null != cursor) {
            cursor.moveToFirst();
        }

        Todo item = new Todo(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)),
                cursor.getLong(cursor.getColumnIndex(KEY_TODO_DUE_TIMESTAMP)));

        return item;
    }

    public List<Todo> getAllTodo() {
        List<Todo> items = new ArrayList<>();

        String TODO_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TODO);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                String text;
                long dueTimestamp;
                do {
                    Todo item = new Todo(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_TODO_DUE_TIMESTAMP)));
                    items.add(item);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public List<Todo> getAllTodoOrdered() {
        List<Todo> items = new ArrayList<>();

        String TODO_SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY (%s > 0) DESC, %s ASC",
                TABLE_TODO, KEY_TODO_DUE_TIMESTAMP, KEY_TODO_DUE_TIMESTAMP);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                String text;
                long dueTimestamp;
                do {
                    Todo item = new Todo(cursor.getInt(cursor.getColumnIndex(KEY_TODO_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TODO_TEXT)),
                            cursor.getLong(cursor.getColumnIndex(KEY_TODO_DUE_TIMESTAMP)));
                    items.add(item);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return items;
    }

    public int getTodoCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT * FROM " + TABLE_TODO;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateTodo(Todo item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO_TEXT, item.getText());
        values.put(KEY_TODO_DUE_TIMESTAMP, item.getDueTimestamp());

        return db.update(TABLE_TODO, values, KEY_TODO_ID + " = ?", new String[]{
                String.valueOf(item.getId())});
    }

    public void deleteTodo(Todo item) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TODO, KEY_TODO_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
}
