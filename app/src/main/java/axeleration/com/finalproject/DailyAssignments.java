package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyAssignments extends AppCompatActivity {

    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_assignments);

        ListView listView = findViewById(R.id.listViewTaskes);
        SQLiteDatabase db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        cursor = db.query(Constants.TASKS.TABLE_NAME,
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATE + "=? AND "+ Constants.TASKS.DATETIME + ">=?",
                new String[] {"0", getTodayDate(),  getDate()},
                null,
                null,
                Constants.TASKS.DATETIME + " ASC");

        TaskCursorAdapter adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }

    private String getTodayDate (){
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String getDate (){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
