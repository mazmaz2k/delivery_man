package axeleration.com.finalproject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/* Custom cursor adapter class */
public class FinishCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    /* Constructor */
    FinishCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }

    /* New view of the row with the data of list view */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.done_task_item, parent, false);
    }

    /* Manipulation for each row in the list view */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final TextView receiverName = view.findViewById(R.id.name); // Text view of the receiver name.
        final TextView receiverPhone = view.findViewById(R.id.phone);   // Text view of the receiver phone number.
        final TextView senderName = view.findViewById(R.id.senderName); // Text view of the sender name.
        final Button deleteTask = view.findViewById(R.id.deleteTask);   // Button to delete a task.

        final String receiver_phone_number = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER)); // receiver phone number
        final String receiver_name = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));    // receiver name
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));  // receiver id
        final String clientName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.CLIENT_NAME)); // client name

        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // On click listener to delete the task.
                ((FinishTasks)context).removeRow(receiver_id);  // call the function from FinishTasks activity.
            }
        });

        senderName.setText(clientName);
        receiverName.setText(receiver_name);
        receiverPhone.setText(receiver_phone_number);
    }
}
