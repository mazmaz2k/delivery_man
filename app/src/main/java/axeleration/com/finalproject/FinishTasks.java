package axeleration.com.finalproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class FinishTasks extends AppCompatActivity {
    private SQLiteDatabase db;
    private FinishCursorAdapter adapter;
    private Cursor cursor;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_tasks);
        listView = findViewById(R.id.listViewFinishTaskes);
        db = DBHelperSingleton.getInstanceDBHelper(this).getWritableDatabase();
        cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=1",null,null,null,null);
        adapter = new FinishCursorAdapter(this, cursor);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

   public void updateList() {
       cursor = db.query(Constants.TASKS.TABLE_NAME,null,Constants.TASKS.IS_SIGN+"=1",null,null,null,null);
       adapter = new FinishCursorAdapter(this, cursor);
       listView.setAdapter(adapter);
   }
}
