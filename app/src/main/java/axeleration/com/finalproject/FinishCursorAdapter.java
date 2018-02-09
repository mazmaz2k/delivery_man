package axeleration.com.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class FinishCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    FinishCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.done_task_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView receiverName = view.findViewById(R.id.name);
        TextView receiverPhone = view.findViewById(R.id.phone);
        TextView senderName = view.findViewById(R.id.senderName);
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));
        final String clientName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.CLIENT_NAME));
        Button deleteTask = view.findViewById(R.id.deleteTask);
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FinishTasks)context).removeRow(receiver_id);
            }
        });
        senderName.setText(clientName);
        receiverName.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME)));
        receiverPhone.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER)));
    }


}
