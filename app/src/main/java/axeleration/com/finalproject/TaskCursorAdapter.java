package axeleration.com.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TaskCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    TaskCursorAdapter(Context context, Cursor c) {

        super(context, c);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.task_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {// TODO: Add name of the client to the sms send.
        TextView text = view.findViewById(R.id.name);
        TextView phone = view.findViewById(R.id.phone);
        final int receiver_id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS._ID));
        final String receiver_phone_number = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
        final String receiver_name = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
        final int id = cursor.getInt(cursor.getColumnIndex(Constants.TASKS.CLIENT_ID));
        final String receiverPhoneNumber = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
        final String receiverName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
        Button navigate=view.findViewById(R.id.nevigateToLoacationBtn);
        Button callBtm = view.findViewById(R.id.call);
        final String address = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));
        Button doneTaskBtn= view.findViewById(R.id.finishedThisTaskBtn);
        doneTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SignaturePad.class);
                i.putExtra("receiver_name", receiver_name);
                i.putExtra("client_id", id);
                i.putExtra("receiver_id", receiver_id);
                i.putExtra("phone_number", receiver_phone_number);
                context.startActivity(i);
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("google.navigation:q=" + address));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,"Permission for sms not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                SmsManager.getDefault().sendTextMessage(receiverPhoneNumber,"","Hello " + receiverName + ",\nIm the delivery boy.\nI'm on my way\nPlease be available on the next 2 hours.\nThank you.",null,null);
                context.startActivity(i);
            }
        });
        callBtm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + receiverPhoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,"Permission for calls not allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(intent);
            }
        });
        text.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME)));
        phone.setText(cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER)));

    }
}
