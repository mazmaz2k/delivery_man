package axeleration.com.finalproject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/* This activity will show all Tasks and able to sort them */
public class ShowAllTasks extends AppCompatActivity {

    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;
    private int client_id;
    private TextView noTaskYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tasks);
        noTaskYet=findViewById(R.id.noTaskYet);
        client_id = getIntent().getIntExtra("client_id",-1);    // receive client id from another Intent
        listView = findViewById(R.id.listViewTaskes);   // find list view.
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open the db.
    }

    /* Menu options initialize */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(client_id == -1){    // find all the unfinished tasks.
            cursor =getCursor(client_id,null);

        }else { // find all the unfinished tasks for specific client.
            cursor =getCursor(client_id,null);
        }
        adapter = new TaskCursorAdapter(this, cursor);  // update the adapter.
        if(cursor.getCount()==0){
            noTaskYet.setVisibility(View.VISIBLE);
        }
        else {
            noTaskYet.setVisibility(View.INVISIBLE);

        }
        listView.setAdapter(adapter);
    }

    /* Option choose for a menu  */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_by_date: // sort by date.
                    cursor = getCursor(client_id,Constants.TASKS.DATETIME + " ASC");
                    break;
            case R.id.sort_by_name: // sort by name.
                    cursor = getCursor(client_id,Constants.TASKS.FULL_NAME + " ASC");
                    break;
            case R.id.sort_by_location: // sort by location.
                    try {
                         updateAllDBLocation();  //  update the distance between us and the client in the DB.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor =getCursor(client_id,Constants.TASKS.LOCATION + " ASC");
                    break;
            case R.id.sort_by_tooLate: //// sort by tasks that passe but not marked as done.
                    cursor  =  db.query(Constants.TASKS.TABLE_NAME,
                            null,
                            Constants.TASKS.DATETIME + "<?" + " AND " + Constants.TASKS.IS_SIGN + "=?",
                            new String[] {StaticFunctions.getCurrentDate("yyyy-MM-dd HH:mm:ss"), "0"},
                            null, null, null);
                    break;
            default:
                    onBackPressed();    // goes to the previous activity.
        }
        adapter = new TaskCursorAdapter(this,cursor);   // update the adapter.
        if(cursor.getCount()==0){
            noTaskYet.setVisibility(View.VISIBLE);
        }
        else {
            noTaskYet.setVisibility(View.INVISIBLE);

        }
        listView.setAdapter(adapter);
        return true;
    }

    /* Updates the distance between us and the client in the DB */
    private void updateAllDBLocation() {
        cursor = db.query(Constants.TASKS.TABLE_NAME, // get the cursor with non done tasks IS_SIGN = 0.
                null,
                Constants.TASKS.IS_SIGN + "=?", // 0 For non done tasks or 1 for tasks done.
                new String[]{"0"},
                null,null,null);

        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {    // for each row in DB.
            int ID = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID)); // get the ID.
            String address = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));  // get the address.
            Location location = StaticFunctions.getLocation(this, address);   // gets the location by address string.
            ContentValues values = StaticFunctions.getContentValues(location);  // values that should be set in the db.
            if(values == null) {    // nothing to set.
                continue;
            }
            db.update(Constants.TASKS.TABLE_NAME, values, Constants.TASKS._ID + "=?", new String[] {String.valueOf(ID)});   // update the DB ADDRESS field.
            cursor.moveToNext();    // go to the next row.
        }
    }

    private Cursor getCursor(int client_id,String orderBy){
        if(client_id == -1){    // find all the unfinished tasks.
            cursor = db.query(Constants.TASKS.TABLE_NAME,
                    null,
                    Constants.TASKS.IS_SIGN + "=?",
                    new String[]{"0"},
                    null,null,orderBy);
        }else { // find all the unfinished tasks for specific client.
            cursor = db.query(Constants.TASKS.TABLE_NAME,
                    null,
                    Constants.TASKS.CLIENT_ID+"=?" + " AND " + Constants.TASKS.IS_SIGN + "=?",
                    new String[] {String.valueOf(client_id), "0"},
                    null, null, orderBy);
        }
        return cursor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
