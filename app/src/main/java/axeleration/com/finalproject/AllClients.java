package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/* This class for showing all clients activity. */
public class AllClients extends AppCompatActivity {

    private ListView list;
    private SQLiteDatabase db;
    private ClientCursorAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open db from singleton object.

        list = findViewById(R.id.listView); // get a list view.
        cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);    // show all client from db.
        adapter = new ClientCursorAdapter(this, cursor);    // initial the custom cursor adapter.
        list.setAdapter(adapter);   // set adapter to the list view.
    }

    /* Menu options initialize */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Option choose for a menu  */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_by_name: // choose the sort by name option.
                cursor = db.query(Constants.CLIENTS.TABLE_NAME,null,null,null,null,null,Constants.TASKS.FULL_NAME + " ASC");
                break;
            default:    // choose the back button option.
                onBackPressed();    // go to the previous screen.
        }
        /* real time update the list view */
        adapter = new ClientCursorAdapter(this,cursor);
        list.setAdapter(adapter);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
