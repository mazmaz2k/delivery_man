package axeleration.com.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class FinishCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private SQLiteDatabase db;

    FinishCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
        db = DBHelperSingleton.getInstanceDBHelper(context).getReadableDatabase();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.done_task_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);
        TextView senderName = view.findViewById(R.id.senderName);
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));
        Button deleteTask = view.findViewById(R.id.deleteTask);
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.delete(Constants.TASKS.TABLE_NAME, Constants.TASKS._ID + "=?", new String[]{String.valueOf(receiver_id)});
                                Toast.makeText(context, "This row has been deleted.", Toast.LENGTH_SHORT).show();
                                ((FinishTasks)context).updateList();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                builder.create().show();
            }
        });
        senderName.setText(String.valueOf(receiver_id));
        name.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME)));
        phone.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER)));
    }
}
