package com.feeltest.smsgroup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
	
	public class DBManager {
		
		private static DBManager _sharedmanager = null;
		private static Context _sharedContext = null;



		private Sql databaseHelper;
		private SQLiteDatabase db;

		// Fonctions statiques
		public static DBManager sharedManager()
		{
			if(_sharedmanager == null){
				_sharedmanager = new DBManager(_sharedContext);
			}

			return _sharedmanager;
		}

		public static Context getSharedContext()
		{
			return _sharedContext;
		}

		public static void setSharedContext(Context context)
		{
			_sharedContext = context;
		}

		// Constructeur
		public DBManager(Context context)
		{
			databaseHelper = new Sql(context);
		}

		//Open the database
		public void open() throws SQLException
		{
			db = databaseHelper.getWritableDatabase();
			db.rawQuery("PRAGMA foreign_keys = ON",null);
		}

		//Close the database
		public void close()
		{
			db.close();
		}

		public void sauve(){
			 try {
	             File sd = Environment.getExternalStorageDirectory();
	             File data = Environment.getDataDirectory();

	             if (sd.canWrite()) {
	           // String currentDBPath = "//data//com.feeltest.smsgroup3//databases//smsgroup_db";
	            String currentDBPath =db.getPath();
	            String backupDBPath = "//feeltest_data//smsgroup.db";
			    File currentDB = new File(data, currentDBPath);

			    File backupDB = new File(sd, backupDBPath);
                backupDB.mkdir();
	 //   if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
	  //  }
	            }
			 }
			 catch (Exception e) {

	    Toast.makeText(getSharedContext(), e.toString(), Toast.LENGTH_LONG).show();
	    }
		}
		
		public void import_bd(){
			 try {
	            File sd = Environment.getExternalStorageDirectory();
	            File data = Environment.getDataDirectory();
	            if (db!=null) db.close();
	      //      if (data.canWrite()) {
	         String currentDBPath = "//data//com.feeltest.smsgroup//databases//smsgroup_db";
	          // String currentDBPath =db.getPath();
	           String backupDBPath = "//feeltest_data//smsgroup.db";
			    File currentDB = new File(data, currentDBPath);
			    File backupDB = new File(sd, backupDBPath);

	       FileChannel dst = new FileOutputStream(currentDB).getChannel();
	       FileChannel src = new FileInputStream(backupDB).getChannel();
	       dst.transferFrom(src, 0, src.size()); //src et dst sont invers�s par rapport a sauve()
	       src.close();
	       dst.close();
	       
	//   }
		 }
			 catch (Exception e) {

	    Toast.makeText(getSharedContext(), e.toString(), Toast.LENGTH_LONG).show();
	    }
		}

		public Groupe  savegroupe(Groupe nouveaugroupe) {
			db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
		       values.put("nom", nouveaugroupe.getnomgroup()); 
		       int id_groupe = (int)db.insert(Sql.DATABASE_TABLE_GROUP,null, values);
		       nouveaugroupe.setId(id_groupe);
			return 	nouveaugroupe;
			}

		public ArrayList<Groupe> getAllGroup() {

			if (db == null)
			{
				this.databaseHelper.getReadableDatabase();
			}
			ArrayList<Groupe> listegroupe = new ArrayList<Groupe>();
			listegroupe.clear();
			Cursor c;
				c = db.query(Sql.DATABASE_TABLE_GROUP, null, null, null, null, null, null);
			while (c.moveToNext())
			{
				String nom = c.getString(c.getColumnIndex("nom"));
				int id = c.getInt(c.getColumnIndex("id"));
				Groupe mongroupe= new Groupe(nom);
				mongroupe.setId(id);
				listegroupe.add(mongroupe);
			}
			c.close();
			return listegroupe;

		}

	public ArrayList<Testeur> getAllTesteur(int id_group) {
            if (db == null)
            {
                this.databaseHelper.getReadableDatabase();
            }
            ArrayList<Testeur> listetesteur = new ArrayList<Testeur>();
            listetesteur.clear();
            Cursor c;
            c = db.query(Sql.DATABASE_TABLE_TESTEUR, null, " id_group = "+ id_group, null, null, null, null);
            while (c.moveToNext())
            {
                String nom = c.getString(c.getColumnIndex("nomprenom"));
                String tel = c.getString(c.getColumnIndex("telephone"));
                int id = c.getInt(c.getColumnIndex("id"));

                Testeur montesteur= new Testeur(tel);
                montesteur.setId(id);
                montesteur.setNomPrenom(nom);
                montesteur.setId_group(id_group);
                listetesteur.add(montesteur);
            }
            c.close();
            return listetesteur;

        }

		public Testeur savetesteur(Testeur nouveautesteur) {
			db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
		       values.put("nomprenom", nouveautesteur.getNomPrenom()); 
		       values.put("id_group", nouveautesteur.getId_group());
		       values.put("telephone", nouveautesteur.getTel());
		       int id_testeur = (int)db.insert(Sql.DATABASE_TABLE_TESTEUR,null, values);
		       nouveautesteur.setId(id_testeur);
			return 	nouveautesteur;
		}

		public void deleteTesteur(ArrayList<Integer> checked) {
			for (int i = 0; i < checked.size(); i++) {
				db.delete(Sql.DATABASE_TABLE_TESTEUR, "id = "+ checked.get(i), null);
			}
			
		}

		public void deleteGroupe(int id_group) {
			db = databaseHelper.getWritableDatabase();
			Cursor c;
		
			c = db.query( Sql.DATABASE_TABLE_TESTEUR ,null," id_group = "+ id_group, null, null, null, null);
		
			while (c.moveToNext())
			{   int id = c.getInt(c.getColumnIndex("id"));
				moveTesteurto(id,0);								
			}
			db.delete(Sql.DATABASE_TABLE_GROUP, "id = "+ id_group, null);	
			c.close();
		}
		
		public void moveTesteurto(int id_testeur, int id_to) {
			ContentValues values = new ContentValues();
			values.put("id_group", id_to);
			int res = db.update(Sql.DATABASE_TABLE_TESTEUR,values, "id = "+ id_testeur, null);	
			Log.d("move : ",String.valueOf(res));
		}

		public int updateGroupe(int id_group3, String value) {
			ContentValues values = new ContentValues();
			values.put("nom", value);
			int res = db.update(Sql.DATABASE_TABLE_GROUP,values, "id = "+ id_group3, null);	
			return res;
			
		}

		public int update(Testeur testeur) {
			ContentValues values = new ContentValues();
			values.put("nomprenom", testeur.getNomPrenom());
			values.put("telephone", testeur.getTel());
			int res = db.update(Sql.DATABASE_TABLE_TESTEUR,values, "id = "+ testeur.getId(), null);	
			return res;
		}
        public long update_message(ArrayList<String> tel, ArrayList<String> nom, ArrayList<String> message, ArrayList<Long> date) {
            // effacement de la table message
            db.delete(Sql.DATABASE_TABLE_MESSAGE, null, null);
            // creation des enregsitrements
            ContentValues values = new ContentValues();
            long res = 0;
            for (int i = 0; i < nom.size(); i++) {
                values.put("_id", tel.get(i));
                values.put("telnom", tel.get(i) + " " + nom.get(i));
                values.put("body", message.get(i));
                values.put("date", date.get(i));
                res = db.insert(Sql.DATABASE_TABLE_MESSAGE, null, values);

            }

            return res;
        }

        public  Cursor getAllMessages() {
            if (db == null)
            {
                db = databaseHelper.getWritableDatabase();
            }

            Cursor c ;
            c = db.query(Sql.DATABASE_TABLE_MESSAGE,null,null,null,null,null,"date" + " DESC");
            return c;
        }

		public ArrayList<String> getAllGroupName() {
			if (db == null)
			{
				this.databaseHelper.getReadableDatabase();
			}
			ArrayList<String> listegroupe = new ArrayList<String>();
			listegroupe.clear();
			Cursor c;
				c = db.query(Sql.DATABASE_TABLE_GROUP, null, null, null, null, null, null);
			while (c.moveToNext())
			{
				String nom = c.getString(c.getColumnIndex("nom"));
				listegroupe.add(nom);
			}
			c.close();
			return listegroupe;

		}

		public  ArrayList<Integer> getAllGroupId() {
			if (db == null)
			{
				this.databaseHelper.getReadableDatabase();
			}
			ArrayList<Integer> listegroupe = new ArrayList<Integer>();
			listegroupe.clear();
			Cursor c;
				c = db.query(Sql.DATABASE_TABLE_GROUP, null, null, null, null, null, null);
			while (c.moveToNext())
			{
				Integer nom = c.getInt(c.getColumnIndex("id"));
				listegroupe.add(nom);
			}
			c.close();
			return listegroupe;
		}

		public int moveTesteursto(ArrayList<Integer> id_testeur, Integer id_group) {
			
			ContentValues values = new ContentValues();
			values.put("id_group", id_group);
			// fonction de la library google guava , permet de convertir facilement , ici list[int] vers list[string]
			List<String> list_id = Lists.transform(id_testeur, Functions.toStringFunction());
			int res = db.update(Sql.DATABASE_TABLE_TESTEUR,values, "id IN ("+makePlaceholders(id_testeur.size()) + ")", list_id.toArray(new String[0]));	
			Log.d("move : ",String.valueOf(res));
		return res;
		}
		
		String makePlaceholders(int len) {
		    if (len < 1) {
		        // It will lead to an invalid query anyway ..
		        throw new RuntimeException("No placeholders");
		    } else {
		        StringBuilder sb = new StringBuilder(len * 2 - 1);
		        sb.append("?");
		        for (int i = 1; i < len; i++) {
		            sb.append(",?");
		        }
		        return sb.toString();
		    }
		}
	}
		

