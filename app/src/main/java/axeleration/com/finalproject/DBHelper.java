package axeleration.com.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
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
