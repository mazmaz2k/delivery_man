package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/* This class for showing all Statistic activity. */
public class Statistic extends AppCompatActivity {

    private  Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        /* Find all the needed text views from the layout */
        TextView finishFromDaily = findViewById(R.id.finishFromDaily);
        TextView finishFromAll = findViewById(R.id.finishFromAll);
        TextView urgentTasks = findViewById(R.id.urgentTasks);
        TextView finishThisWeekTasks = findViewById(R.id.finishThisWeekTasks);
        TextView finishThisMonthsTasks = findViewById(R.id.finishThisMonthsTasks);

        SQLiteDatabase db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();  // open db.

        cursor = getStatisticsCursor(db, "0", 0);  // query to find all daily Tasks.
        int numOfDaily = cursor.getCount();

        cursor = getStatisticsCursor(db, "1", 0); // query to find all daily Tasks that we finished.
        int numOfDaily_finish = cursor.getCount();

        cursor = getStatisticsCursor(db, "1");  // query to find all finish tasks.
        int count_finishFromAll = cursor.getCount();

        cursor = getStatisticsCursor(db, "0"); // query to find all Tasks.
        int count_All_tasks=cursor.getCount();

        cursor = getStatisticsCursor(db, "1", 7); // query to find all Tasks that we finish last week.
        int count_finishThisWeekTasks=cursor.getCount();

        cursor = getStatisticsCursor(db, "1", 1); // query to find all Tasks that we finish last month.
        int count_finishThisMonthsTasks=cursor.getCount();

        cursor = db.query(Constants.TASKS.TABLE_NAME,      // query to find all daily Tasks in the next 3 hours.
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=? AND " +  Constants.TASKS.DATETIME + "<=?",
                new String[] {"0", StaticFunctions.getCurrentDate("yyyy-MM-dd HH:mm:ss"), getDate(3)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int urgentTasksCount = cursor.getCount();

        /* String format of data */
        String finishFromDailyString = numOfDaily_finish + " / " + (numOfDaily+numOfDaily_finish);
        String finishFromAllString = count_finishFromAll + " / "+ (count_All_tasks + count_finishFromAll);
        String urgentTasksString = urgentTasksCount + " / " + numOfDaily;
        String finishThisWeekTasksString = String.valueOf(count_finishThisWeekTasks);
        String finishThisMonthsTasksString = String.valueOf(count_finishThisMonthsTasks);

        /* Set the text view from format data */
        finishFromDaily.setText(finishFromDailyString);
        finishFromAll.setText(finishFromAllString);
        urgentTasks.setText(urgentTasksString);
        finishThisWeekTasks.setText(finishThisWeekTasksString);
        finishThisMonthsTasks.setText(finishThisMonthsTasksString);
    }

    /* Return the cursor from db with time interval */
    private Cursor getStatisticsCursor(SQLiteDatabase db, String isSign, int timeInterval) {    // isSign = 0 => task not done, isSign = 1 task is done
        return cursor = db.query(Constants.TASKS.TABLE_NAME,  //query to find all Tasks that we finish last month
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=?",
                new String[] {isSign, getDate(timeInterval)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
    }

    /* Return the cursor from db without time interval */
    private Cursor getStatisticsCursor(SQLiteDatabase db, String isSign) {    // isSign = 0 => task not done, isSign = 1 task is done
        return cursor = db.query(Constants.TASKS.TABLE_NAME,  //query to find all Tasks that we finish last month
                null,
                Constants.TASKS.IS_SIGN + "=?",
                new String[] {isSign},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
    }

    /* find specific date and time(last week/last month / next 3 hours) */
    private String getDate (int timeInterval){
        Calendar c = Calendar.getInstance();
        if (timeInterval == 3){
            c.add(Calendar.HOUR, timeInterval);
        }else if (timeInterval == 7){
            c.add(Calendar.DAY_OF_WEEK,-timeInterval);
        }else if(timeInterval == 1){
            c.add(Calendar.MONTH,-timeInterval);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
