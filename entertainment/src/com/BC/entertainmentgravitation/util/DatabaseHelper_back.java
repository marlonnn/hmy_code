package com.BC.entertainmentgravitation.util;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.BC.entertainmentgravitation.entity.GeTui;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper_back extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something  
    // appropriate for your app  
    private static final String DATABASE_NAME = "getui.db";  
    
    // any time you make changes to your database objects, you may have to  
    // increase the database version  
    private static final int DATABASE_VERSION = 1; 
    //数据库默认路径SDCard
    private static String DATABASE_PATH = Environment.getExternalStorageDirectory()  
            + "/getui.db";  
    private Context mContext; 
    
    public DatabaseHelper_back(Context context) {  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        mContext = context;  
        initDtaBasePath();  
        try {  
      
            File f = new File(DATABASE_PATH);  
            if (!f.exists()) {  
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH, null);  
                onCreate(db);  
                db.close();  
            }  
        } catch (Exception e) {  
        }  
    }  
    //如果没有SDCard 默认存储在项目文件目录下  
    
    private void initDtaBasePath() {  
        if (!ExistSDCard()) {  
            DATABASE_PATH = mContext.getFilesDir().getAbsolutePath() + "/getui.db";  
        }  
    }
    
    private boolean  ExistSDCard(){
    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) 
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    @Override  
    public synchronized SQLiteDatabase getWritableDatabase() {  
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);  
    }  
      
    public synchronized SQLiteDatabase getReadableDatabase() {  
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);  
    }  
    
    /** 
     * This is called when the database is first created. Usually you should 
     * call createTable statements here to create the tables that will store 
     * your data. 
     */  
    @Override  
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {  
        try {  
            TableUtils.createTable(connectionSource, GeTui.class);  
        } catch (java.sql.SQLException e) {  
            throw new RuntimeException(e);  
        }  
    } 
    
    /** 
     * This is called when your application is upgraded and it has a higher 
     * version number. This allows you to adjust the various data to match the 
     * new version number. 
     */  
    @Override  
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,  
            int newVersion) {  
        try {  
            TableUtils.dropTable(connectionSource, GeTui.class, true);  
            onCreate(db, connectionSource);  
        } catch (java.sql.SQLException e) {  
            throw new RuntimeException(e);  
        }  
    }
    
    public void deleteDB() {  
        if (mContext != null) {  
            File f = mContext.getDatabasePath(DATABASE_NAME);  
            if (f.exists()) {  
                f.delete();  
            } else {  
                mContext.deleteDatabase(DATABASE_NAME);  
            }  
      
            File file = mContext.getDatabasePath(DATABASE_PATH);  
            if (file.exists()) {  
                file.delete();  
            }  
        }  
    } 
    
    /** 
     * Close the database connections and clear any cached DAOs. 
     */  
    @Override  
    public void close() {  
        super.close();  
    } 
}
