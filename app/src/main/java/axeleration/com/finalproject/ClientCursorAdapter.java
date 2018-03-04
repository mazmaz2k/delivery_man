package axeleration.com.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/* Custom cursor adapter class */
public class ClientCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    /* Constructor */
    ClientCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }
    /* New view of the row with the data of list view */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.client_item, parent, false);
    }

    /* Manipulation for each row in the list view */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView text = view.findViewById(R.id.name); // Text view of the name.
        final TextView phone = view.findViewById(R.id.phone);   // Text view of the phone number.
        final Button newTask = view.findViewById(R.id.addTaskBtn);  // Button to add new task for the specific client.
        final Button showAllTasks = view.findViewById(R.id.showTskBtn); // Button to show all tasks for the specific client.
        final Button callBtm = view.findViewById(R.id.call); // Button to make a call.


        final String clientName = cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.FULL_NAME)); // Client name string.
        final int id = cursor.getInt(cursor.getColumnIndex(Constants.CLIENTS._ID));     // Client id number.
        final String phoneNumber = cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.PHONE_NUMBER)); // Client phone String.
        showAllTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // On click listener to show all tasks per client.
                Intent i = new Intent(context, ShowAllTasks.class);
                i.putExtra("client_id", id);
                context.startActivity(i);
            }
        });
        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // On click listener to create new task per client.
                Intent i = new Intent(context, NewTask.class);
                i.putExtra("clientName", cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.FULL_NAME)));
                i.putExtra("client_id", id);
                context.startActivity(i);
            }
        });
        callBtm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {   // On click listener to make a call.
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { // if call permission not granted.
                    Toast.makeText(context,"Permission not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(intent);  // permission granted, make a call.
            }
        });
        text.setText(clientName);   // Set client name to text view.
        phone.setText(cursor.getString(cursor.getColumnIndex(Constants.CLIENTS.PHONE_NUMBER))); // Set phone number to text view.

    }
}
