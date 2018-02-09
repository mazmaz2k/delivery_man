package axeleration.com.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

   public void removeRow(final int receiverID) {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setMessage("Are you sure you want to delete this?")
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       db.delete(Constants.TASKS.TABLE_NAME, Constants.TASKS._ID + "=?", new String[]{String.valueOf(receiverID)});
                       Toast.makeText(FinishTasks.this, "This row has been deleted.", Toast.LENGTH_SHORT).show();
                       updateList();
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {}
               });
       builder.create().show();
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
