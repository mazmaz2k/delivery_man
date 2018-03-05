package axeleration.com.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* Singleton for DB Open Helper */
public class DBHelperSingleton extends SQLiteOpenHelper {

    private static DBHelperSingleton helper;

    private DBHelperSingleton(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    /* Return the instance of DBHelper */
    static DBHelperSingleton getInstanceDBHelper(Context context) {
        if(helper == null) {    // if not initialize yet.
            helper = new DBHelperSingleton(context);    // initial
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.CREATE_SQL_TABLE_CLIENTS);
        db.execSQL(Constants.CREATE_SQL_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.REMOVE_SQL_TABLE_CLIENTS);
        db.execSQL(Constants.REMOVE_SQL_TABLE_TASKS);
        onCreate(db);
    }
}
