package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyAssignments extends AppCompatActivity {

    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_assignments);

        listView = findViewById(R.id.listViewTaskes);
        getTodayDate();
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        cursor=db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=0 AND "+Constants.TASKS.DATE + "=?",new String[]{ getTodayDate()},null,null,Constants.TASKS.TIME+" ASC");
        adapter = new TaskCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }

    private String getTodayDate (){
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        Log.d("temp",""+sdf.format(new Date()));
        return sdf.format(new Date());


    }


}
