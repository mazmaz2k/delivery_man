package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

public class AllClients extends AppCompatActivity {


    private ListView list;
    private SQLiteDatabase db;
    private ClientCursorAdapter adapter;
    private Cursor cursor;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sortChange(0);
                    return true;
                case R.id.navigation_dashboard:
                    sortChange(1);
                    return true;
                case R.id.navigation_notifications:
                    sortChange(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients);

        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();

        list = findViewById(R.id.listView);
        cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
        adapter = new ClientCursorAdapter(this, cursor);
        list.setAdapter(adapter);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void sortChange(int idx){
        if(idx==0){
            cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
        }else if(idx==1){
            cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, Constants.CLIENTS.FULL_NAME);
        }else if(idx==2){
            cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, Constants.CLIENTS.ADDRESS);
        }
        adapter = new ClientCursorAdapter(this, cursor);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
