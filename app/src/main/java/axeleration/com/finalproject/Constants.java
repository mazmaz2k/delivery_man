package axeleration.com.finalproject;

import android.provider.BaseColumns;

final class Constants {

    static final int VERSION = 1;
    static final String DB_NAME = "Clients.db";
    static final String CREATE_SQL_TABLE = "CREATE TABLE " +
            Constants.ASU.TABLE_NAME + " (" +
            Constants.ASU._ID + " INTEGER PRIMARY KEY, " +
            Constants.ASU.LATITUDE + " TEXT, " +
            Constants.ASU.LONGITUDE + " TEXT, ";

    static final String REMOVE_SQL_TABLE = "DROP TABLE IF EXISTS " + Constants.ASU.TABLE_NAME;

    private Constants() {
        throw new AssertionError("Can't create constants class");
    }

    static abstract class ASU implements BaseColumns {
        static final String TABLE_NAME = "ASU_TABLE";
        static final String FULL_NAME = "FULL_NAME";
        static final String PHONE_NUMBER = "PHONE_NUMBER";
        static final String LATITUDE = "LATITUDE";
        static final String LONGITUDE = "LONGITUDE";

    }

}
