package axeleration.com.finalproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/* This class for showing all future daily tasks. */
public class DailyAssignments extends AppCompatActivity {

    private Cursor cursor;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;
    private ListView listView;
    private TextView noTaskYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_assignments);
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open db from singleton object.
        noTaskYet=findViewById(R.id.noTaskYet);
    }

    /* Menu options initialize */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_nav,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Option choose for a menu  */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_by_date:     // sort by date
                cursor = getTaskCursor(Constants.TASKS.DATETIME);   // get cursor order by date time.
                break;
            case R.id.sort_by_name:     // sort by name
                cursor = getTaskCursor(Constants.TASKS.FULL_NAME);  // get cursor order by full name.
                break;
            case R.id.sort_by_location:   // sort by location
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateAllDBLocation();      //  update the distance between us and the client in the DB.

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                cursor = getTaskCursor(Constants.TASKS.LOCATION);   // get cursor order by location latitude and longitude.
                break;
            default:
                onBackPressed();        // back button pressed.
                break;
        }
        adapter = new TaskCursorAdapter(this,cursor);
        listView.setAdapter(adapter);
        return true;
    }

    /* Updates the distance between us and the client in the DB */
    private void updateAllDBLocation() {
        cursor = db.query(Constants.TASKS.TABLE_NAME,   // get the cursor with non done tasks IS_SIGN = 0.
                null,
                Constants.TASKS.IS_SIGN + "=?", // 0 For non done tasks or 1 for tasks done.
                new String[] {"0"},
                null,
                null,
                null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {    // for each row in DB.
            int ID = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID)); // get the ID.
            String address = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));  // get the ADDRESS.
            Location location = StaticFunctions.getLocation(this, address);   // gets the location by address string.
            ContentValues values = StaticFunctions.getContentValues(location);  // values that should be set in the db.
            if(values == null) {    // nothing to set.
                continue;
            }
            db.update(Constants.TASKS.TABLE_NAME, values, Constants.TASKS._ID + "=?", new String[] {String.valueOf(ID)});   // update the DB ADDRESS field.
            cursor.moveToNext();    // go to the next row.
        }
    }

    /* Update the view list onResume the activity */
    @Override
    protected void onResume() {
        super.onResume();
        listView = findViewById(R.id.listViewTaskes);
        cursor = getTaskCursor(Constants.TASKS.DATETIME);
        adapter = new TaskCursorAdapter(this, cursor);
        if(cursor.getCount()==0){
            noTaskYet.setVisibility(View.VISIBLE);
        }
        else {
            noTaskYet.setVisibility(View.INVISIBLE);

        }
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    /* Return cursor after open the DB */
    private Cursor getTaskCursor(String orderBy) {
        cursor = db.query(Constants.TASKS.TABLE_NAME,
                null,
                Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATE + "=? AND "+ Constants.TASKS.DATETIME + ">=?",   // query to get today date and future time
                new String[] {"0", StaticFunctions.getCurrentDate("d-M-yyyy"), StaticFunctions.getCurrentDate("yyyy-MM-dd HH:mm:ss")},
                null,
                null,
                orderBy + " ASC");
        return cursor;
    }
}
