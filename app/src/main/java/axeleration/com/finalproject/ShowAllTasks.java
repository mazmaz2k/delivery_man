package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLData;

public class ShowAllTasks extends AppCompatActivity {

    private Cursor cursor;
    private  ListView view;
    private  DBHelper dbHelper;
    private SQLiteDatabase db;
    private TaskCursorAdapter adapter;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    cursor=db.query(Constants.TASKS.TABLE_NAME,null,null,null,null,null,Constants.TASKS.FULL_NAME+" DESC");
                    view.setAdapter(adapter);
                    return true;
                case R.id.navigation_dashboard:
                    cursor=db.query(Constants.TASKS.TABLE_NAME,null,null,null,null,null,Constants.TASKS.FULL_NAME+" DESC");
                    view.setAdapter(adapter);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_tasks);
        final int client_id = getIntent().getIntExtra("client_id",-1);
        Log.d("temp",""+client_id);

        view =findViewById(R.id.listViewTaskes);
        dbHelper =new DBHelper(this);
        db=dbHelper.getReadableDatabase();
        if(client_id==-1){
            cursor= db.query(Constants.TASKS.TABLE_NAME,null,null,null,null,null,null);

        }else {
            cursor = db.query(Constants.TASKS.TABLE_NAME, null, Constants.TASKS.CLIENT_ID+"=?", new String[] {client_id+""}, null, null, null);
        }
        adapter=new TaskCursorAdapter(this,cursor);
        view.setAdapter(adapter);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
