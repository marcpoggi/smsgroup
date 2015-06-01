package com.feeltest.smsgroup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Sql extends SQLiteOpenHelper {

	static final String dbName="smsgroup_db";
	private static final int DATABASE_VERSION =5;
	public static final String DATABASE_TABLE_GROUP = "groupe";
	public static final String DATABASE_TABLE_TESTEUR = "testeur";
    public static final String DATABASE_TABLE_MESSAGE = "message";

	private static final String DATABASE_CREATE_GROUP =
			"CREATE TABLE " + DATABASE_TABLE_GROUP + " (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"nom TEXT );";

	private static final String DATABASE_CREATE_TESTEUR =
			"CREATE TABLE " + DATABASE_TABLE_TESTEUR + " (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"nomprenom TEXT , " +
					"telephone TEXT , "+
					"id_group INTEGER,"+
		 			"FOREIGN KEY(id_group) REFERENCES groupe(id) ON DELETE SET NULL);";
    private static final String DATABASE_CREATE_MESSAGE =
            "CREATE TABLE " + DATABASE_TABLE_MESSAGE + " (" +
                    "_id TEXT PRIMARY KEY, " +
                    "telnom TEXT , " +
                    "body TEXT , "
                    +"date INTEGER )";

	private static Context mcontext;
	private static String ORPHELIN; 
	
	public Sql(Context context)
	{
		super(context, dbName, null, DATABASE_VERSION);
		mcontext= context;
		ORPHELIN = mcontext.getResources().getString(R.string.orphelin);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Cr�ation de la base si elle n'est pas cr�e
		Log.d("group: ", DATABASE_CREATE_GROUP);
		db.execSQL(DATABASE_CREATE_TESTEUR);
		db.execSQL(DATABASE_CREATE_GROUP);
        db.execSQL(DATABASE_CREATE_MESSAGE);
		// Cr�ation du groupe des orphelins
		db.execSQL("INSERT INTO "+ DATABASE_TABLE_GROUP+" VALUES (0,'"+ ORPHELIN +"');");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 if (oldVersion < 2)
	        {// rajout d'un champ alias dans table testeur
	            db.execSQL("ALTER TABLE testeur	ADD alias");
	            Log.d("database ","Application upgraded to 2 level ");
	        }
        if (oldVersion < 3)
        {// rajout de la table message
            db.execSQL(DATABASE_CREATE_MESSAGE);
            Log.d("database ","Application upgraded to 3 level ");
        }
        if (oldVersion < 5)
        {// rajout de la table message
            db.execSQL("ALTER TABLE "+ DATABASE_TABLE_MESSAGE + " ADD date INTEGER");
            Log.d("database ","Application upgraded to 5 level ");
        }
	}
}
