package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

public class AllClients extends AppCompatActivity {


    private ListView list;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private  ClientCursorAdapter adapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sortCange(0);
                    return true;
                case R.id.navigation_dashboard:
                    sortCange(1);
                    return true;
                case R.id.navigation_notifications:
                    sortCange(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients);

        dbHelper= new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        list = findViewById(R.id.listView);
        Cursor cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
        adapter = new ClientCursorAdapter(this, cursor);
        list.setAdapter(adapter);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void sortCange(int idx){
        if(idx==0){
            Cursor cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
            adapter = new ClientCursorAdapter(this, cursor);
            list.setAdapter(adapter);
        }else if(idx==1){
            Cursor cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, Constants.CLIENTS.FULL_NAME);
            adapter = new ClientCursorAdapter(this, cursor);
            list.setAdapter(adapter);
        }else if(idx==2){
            Cursor cursor = db.query(Constants.CLIENTS.TABLE_NAME, null, null, null, null, null, Constants.CLIENTS.ADDRESS);
            adapter = new ClientCursorAdapter(this, cursor);
            list.setAdapter(adapter);
        }

    }
}
