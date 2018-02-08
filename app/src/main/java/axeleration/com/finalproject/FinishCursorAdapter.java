package axeleration.com.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mazma on 08/02/2018.
 */

public class FinishCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private SQLiteDatabase db;
    public FinishCursorAdapter(Context context, Cursor c) {
        super(context, c);
        inflater = LayoutInflater.from(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.done_task_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);
        TextView senderName =view.findViewById(R.id.senderName);
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));
        final String receiver_phone_number = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
        final String receiver_name = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
        final int id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS.CLIENT_ID));
        final String receiverPhoneNumber = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
        final String receiverName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
        Button deleteTask = view.findViewById(R.id.deleteTask);
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(context);
                db=dbHelper.getReadableDatabase();
                db.delete(Constants.TASKS.TABLE_NAME, Constants.TASKS._ID + "=?", new String[]{String.valueOf(receiver_id)});
                Toast.makeText(context, "remove item", Toast.LENGTH_SHORT).show();

            }

        });
        senderName.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.CLIENT_ID)));
        name.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME)));
        phone.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER)));
    }
}
