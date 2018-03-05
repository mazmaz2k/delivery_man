package axeleration.com.finalproject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/* This class for statics functions */
class StaticFunctions {

    /* Return string of the current date with format */
    static String getCurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(c.getTime());
    }

    /* Return the future string of the date with format and hours to add */
    static String getFutureDate (String format, int hourFromNow){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, hourFromNow);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(c.getTime());
    }
}
