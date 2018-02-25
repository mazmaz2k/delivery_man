package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class AllClients extends AppCompatActivity {


    private ListView list;
    private SQLiteDatabase db;
    private ClientCursorAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();

        list = findViewById(R.id.listView);
        cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
        adapter = new ClientCursorAdapter(this, cursor);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_by_name:
                cursor = db.query(Constants.CLIENTS.TABLE_NAME,null,null,null,null,null,Constants.TASKS.FULL_NAME + " ASC");
                break;
            default:
                onBackPressed();
        }
        adapter = new ClientCursorAdapter(this,cursor);
        list.setAdapter(adapter);
        return true;
    }
}
