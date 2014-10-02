package com.example.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	public static final String DATABASE_NAME = "myDB.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "myTable";
	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dbHelper = new DBHelper(getApplicationContext());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		for (int i = 0; i < 5; i++) {
			dbHelper.insertValues();
		}		
		Log.v(TAG, "Before updating...");
		dbHelper.exploreDB();
				
		dbHelper.updateAColumnAtOnce();
		Log.v(TAG, "After updating...");
		dbHelper.exploreDB();
	}
	
	//	db helper
	public class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+TABLE_NAME+ "(dummy1 INTEGER PRIMARY KEY AUTOINCREMENT,dummy2 TEXT NOT NULL);");	//No I18N
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME); //No I18N
			onCreate(db);
		}		
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			
			if(!db.isReadOnly()) {
				db.execSQL("PRAGMA foreign_keys=ON;");	// No I18N
			}
		}
		
		//	adding values inside db
		public void insertValues() {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cValues = new ContentValues();
			
			cValues.put("dummy2", "value : FirstValue");
			
			db.insert(TABLE_NAME, null, cValues);
			
			db.close();
		}
		
		//	updating a column(all its rows) at once
		public void updateAColumnAtOnce() {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cValues = new ContentValues();
			cValues.put("dummy2", "value : UpdatedValue");
			
			//	this will replace values in an entire column at once
			db.update(TABLE_NAME, cValues, null, null);
			
			db.close();
		}
		
		//	exploring db
		public void exploreDB() {
			SQLiteDatabase db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+";",null);
			
			if(cursor != null && cursor.moveToFirst()){
				do {
					Log.v(TAG, "value is "+cursor.getString(1));
				} while (cursor.moveToNext());				
			}
			
			cursor.close();
			db.close();
		}
	}
}