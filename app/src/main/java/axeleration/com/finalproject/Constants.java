package axeleration.com.finalproject;

import android.provider.BaseColumns;

final class Constants {

    static final int DB_VERSION = 1;
    static final String DB_NAME = "Delivery.db";

    static final String CREATE_SQL_TABLE_CLIENTS = "CREATE TABLE " +
            Constants.CLIENTS.TABLE_NAME + " (" +
            Constants.CLIENTS._ID + " INTEGER PRIMARY KEY, " +
            CLIENTS.FULL_NAME + " TEXT, " +
            CLIENTS.PHONE_NUMBER + " TEXT UNIQUE, " +
            CLIENTS.ADDRESS + " TEXT)";

    static final String CREATE_SQL_TABLE_TASKS = "CREATE TABLE " +
            Constants.TASKS.TABLE_NAME + " (" +
            Constants.TASKS._ID + " INTEGER PRIMARY KEY, " +
            TASKS.FULL_NAME + " TEXT, " +
            TASKS.PHONE_NUMBER + " TEXT, " +
            TASKS.ADDRESS + " TEXT, " +
            TASKS.CLIENT_ID + " TEXT, " +
            TASKS.IS_SIGN +" INTEGER, " +
            TASKS.CLIENT_NAME + " TEXT, " +
            TASKS.DATE + " DATE, " +
            TASKS.TIME + " TEXT)";

    static final String REMOVE_SQL_TABLE_CLIENTS = "DROP TABLE IF EXISTS " + Constants.CLIENTS.TABLE_NAME;
    static final String REMOVE_SQL_TABLE_TASKS = "DROP TABLE IF EXISTS " + Constants.TASKS.TABLE_NAME;


    private Constants() {
        throw new AssertionError("Can't create constants class");
    }

    static abstract class CLIENTS implements BaseColumns {
        static final String TABLE_NAME = "CLIENTS_TABLE";
        static final String FULL_NAME = "FULL_NAME";
        static final String PHONE_NUMBER = "PHONE_NUMBER";
        static final String ADDRESS = "ADDRESS";
    }

    static abstract class TASKS implements BaseColumns {
        static final String TABLE_NAME = "TASKS_TABLE";
        static final String FULL_NAME = "FULL_NAME";
        static final String PHONE_NUMBER = "PHONE_NUMBER";
        static final String CLIENT_ID = "CLIENT_ID";
        static final String ADDRESS = "ADDRESS";
        static final String IS_SIGN = "IS_SIGN";
        static final String CLIENT_NAME = "CLIENT_NAME";
        static final String DATE = "DATE";
        static final String TIME = "TIME";
    }

}
