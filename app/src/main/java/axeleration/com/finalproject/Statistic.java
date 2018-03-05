package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/* This class for showing all Statistic activity. */

public class Statistic extends AppCompatActivity {

    private  Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        TextView finishFromDaily = findViewById(R.id.finishFromDaily);
        TextView finishFromAll = findViewById(R.id.finishFromAll);
        TextView urgentTasks = findViewById(R.id.urgentTasks);
        TextView finishThisWeekTasks = findViewById(R.id.finishThisWeekTasks);
        TextView finishThisMonthsTasks = findViewById(R.id.finishThisMonthsTasks);
        SQLiteDatabase db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        cursor = db.query(Constants.TASKS.TABLE_NAME,       //query to find all daily Tasks
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATE + "=? AND "+ Constants.TASKS.DATETIME + "<=?",
                new String[] {"0", getTodayDate(),  getDate(0)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int numOfDaily = cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME, //query to find all daily Tasks that we finished
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATE + "=? AND "+ Constants.TASKS.DATETIME + "<=?",
                new String[] {"1", getTodayDate(),  getDate(0)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int numOfDaily_finish = cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME,   //query to find all finish Tasks
                null,
                Constants.TASKS.IS_SIGN + "=?",
                new String[] {"1"},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int count_finishFromAll=cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME,       //query to find all Tasks
                null,
                Constants.TASKS.IS_SIGN + "=?",
                new String[] {"0"},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int count_All_tasks=cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME,      //query to find all daily Tasks in the next 3 hours
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=? AND " +  Constants.TASKS.DATETIME + "<=?",
                new String[] {"0", getCurrentDate(), getDate(3)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int urgentTasksCount=cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME,       //query to find all Tasks that we finish last week
                null,
                Constants.TASKS.IS_SIGN + "=? AND " +Constants.TASKS.DATETIME + ">=?",
                new String[] {"1", getDate(7)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int count_finishThisWeekTasks=cursor.getCount();
        cursor = db.query(Constants.TASKS.TABLE_NAME,  //query to find all Tasks that we finish last month
                null,
                Constants.TASKS.IS_SIGN + "=? AND " +Constants.TASKS.DATETIME + ">=?",
                new String[] {"1", getDate(1)},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");
        int count_finishThisMonthsTasks=cursor.getCount();

        finishFromDaily.setText(" "+numOfDaily_finish+" / "+numOfDaily);       //set text in all text views
        finishFromAll.setText(" "+count_finishFromAll+" / "+count_All_tasks);
        urgentTasks.setText(" "+urgentTasksCount+" / "+numOfDaily);
        finishThisWeekTasks.setText(" "+ count_finishThisWeekTasks);
        finishThisMonthsTasks.setText(" "+ count_finishThisMonthsTasks);
    }

    //find the current  time
    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }
    //find today Date
    private String getTodayDate (){
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    //find specific date and time(last week/last month / next 3 hours).
    private String getDate (int x){
        Calendar c = Calendar.getInstance();
        if (x==3){
            c.add(Calendar.HOUR, x);
        }else if (x==7){
            c.add(Calendar.DAY_OF_WEEK,-x);

        }else if(x==1){
            c.add(Calendar.MONTH,-x);

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
