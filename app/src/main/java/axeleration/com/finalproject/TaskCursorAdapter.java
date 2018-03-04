package axeleration.com.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/* Custom cursor adapter class */
public class TaskCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    /* Constructor */
    TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }

    /* New view of the row with the data of list view */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.task_item, parent, false);
    }

    /* Manipulation for each row in the list view */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final TextView text = view.findViewById(R.id.name); // Text view of the name.
        final TextView phone = view.findViewById(R.id.phone); // Text view of the phone number.
        final TextView time = view.findViewById(R.id.time); // Text view of the date and time.
        final Button navigate = view.findViewById(R.id.nevigateToLoacationBtn); // Button to make a navigation.
        final Button callBtm = view.findViewById(R.id.call);    // Button to make a call.
        final Button doneTaskBtn= view.findViewById(R.id.finishedThisTaskBtn); // Button to finish a task.

        final String receiverTime = cursor.getString(cursor.getColumnIndex(Constants.TASKS.DATETIME));  // String for date and time.
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));  //  Int for receiver id number.
        final int id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS.CLIENT_ID)); // Client ID number.
        final String receiverPhoneNumber = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));   // Receiver phone number string.
        final String receiverName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME)); // Receiver name string.
        final String receiverAddress = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));    // Receiver address string.
        doneTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // On click listener to finish the task and goes to the signature activity.
                Intent i = new Intent(context, SignaturePad.class);
                i.putExtra("receiver_name", receiverName);
                i.putExtra("client_id", id);
                i.putExtra("receiver_id", receiver_id);
                i.putExtra("phone_number", receiverPhoneNumber);
                context.startActivity(i);
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    // On click listener to navigate with google maps to the task location.
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("google.navigation:q=" + receiverAddress));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) { // check for permission to send SMS.
                    Toast.makeText(context,"Permission for sms not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                /* Send SMS if permission granted */
                SmsManager.getDefault().sendTextMessage(receiverPhoneNumber,"","Hello " + receiverName + ",\nIm the delivery boy.\nI'm on my way\nPlease be available on the next 2 hours.\nThank you.",null,null);
                context.startActivity(i);
            }
        });
        callBtm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {    // On click listener to call the receiver.
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + receiverPhoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { // if permission not granted.
                    Toast.makeText(context,"Permission for calls not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(intent);  // permission granted and call activity start.
            }
        });
        time.setText(receiverTime); // Set receiver time to text view.
        text.setText(receiverName); // Set receiver name to text view.
        phone.setText(receiverPhoneNumber); // Set receiver phone number to text view.
    }
}
