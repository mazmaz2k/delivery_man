package axeleration.com.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

/* This class for showing all finish tasks activity */
public class FinishTasks extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_tasks);

        listView = findViewById(R.id.listViewFinishTaskes);
        db = DBHelperSingleton.getInstanceDBHelper(this).getWritableDatabase(); // open db from singleton object.

        updateList();   // updates the list view of the activity.
    }

    /* Update the list view of the activity */
    public void updateList() {
        cursor = db.query(Constants.TASKS.TABLE_NAME,   // Return the cursor of all finish tasks
                null,
                Constants.TASKS.IS_SIGN + "=?", // IS_SIGN = 0 = task is not finish yet, IS_SIGN = 1 = task in finished with signature.
                new String[] {"1"},
                null,
                null,
                null);
        FinishCursorAdapter adapter = new FinishCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    /* Removes the row from list view and build alert dialog for confirmation */
    public void removeRow(final int receiverID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    // build alert dialog.
        builder.setMessage("Are you sure you want to delete this?")             // set the message of the dialog.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {                // Yes button.
                       db.delete(Constants.TASKS.TABLE_NAME, Constants.TASKS._ID + "=?", new String[]{String.valueOf(receiverID)}); // delete for specific row.
                       Toast.makeText(FinishTasks.this, "This row has been deleted.", Toast.LENGTH_SHORT).show();
                       updateList();    // update the list view.
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // No button.
                   public void onClick(DialogInterface dialog, int id) {}   // Close the dialog and do nothing.
               });
        builder.create().show();    // Show the dialog.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();    // back button press go to the previous activity.
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
