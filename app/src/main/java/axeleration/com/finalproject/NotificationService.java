package axeleration.com.finalproject;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/* This is a service that runs for all app life and check if there is any urgent task and show them in notification */
public class NotificationService extends IntentService{

    private SQLiteDatabase db;
    boolean flag; // in charge to close the thread repeat.
    String phoneOfLastUrgent; // in charge to show the notification only once for each task.

    /* constructor */
    public NotificationService(){
        super("NotificationService");
        flag = true;
        phoneOfLastUrgent = "";
        Log.d("temp","somthing- constructor");
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase(); // open the db.
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Cursor cursor = null;
        while(flag) {
            try {
                Thread.sleep(30000);  // thread that sleep 30 sec.
                if(!db.isOpen()) {  // check if DB is close and if so exit.
                    return;
                }
                cursor = db.query(Constants.TASKS.TABLE_NAME,   // query to find all today tasks that the time is in the next 3 hours.
                        null,
                        Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=? AND " +  Constants.TASKS.DATETIME + "<=?",
                        new String[] {"0", StaticFunctions.getCurrentDate("yyyy-MM-dd HH:mm:ss"),  StaticFunctions.getFutureDate("yyyy-MM-dd HH:mm:ss",3)},
                        null,
                        null,
                        Constants.TASKS.DATETIME + " ASC");
                if(cursor.getCount()==0){
                    continue;
                }
                cursor.moveToFirst();
                if(phoneOfLastUrgent.equals(cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER))))
                   continue;
                    /* init all information from the first task in the table */
                final String phoneNumber = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
                final String clientName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.CLIENT_NAME));
                final String receiverName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
                final String address = cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));
                setNotificationCall(receiverName, clientName, phoneNumber, address);
                Log.d("temp","something- in tread");
                phoneOfLastUrgent = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));   // Receiver phone number string.

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(cursor != null)
            cursor.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }

    /* Display notification */
    public void setNotificationCall(String recvName, String clientName, String phoneNum, String address){
        /* setup notification and the notification channel */
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "my_channel_01";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // check SDK version
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }
        /* Intent that goes to google maps */
        Intent nevIntent = new Intent(Intent.ACTION_VIEW);
        nevIntent.setData(Uri.parse("google.navigation:q=" + address));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||  // check permission to send SMS
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission for sms not allowed",Toast.LENGTH_SHORT).show();
            return;
        }
        /* Intent for calling */
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNum));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {    // check permission to call.
            Toast.makeText(this,"Permission for calls not allowed",Toast.LENGTH_SHORT).show();
            return;
        }

        PendingIntent nev_pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), nevIntent, 0);
        PendingIntent call_pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), callIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)   // set notification button
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Package to deliver:")
                .setContentText("GO to "+ recvName+"\n"+ "from "+ clientName)
                .addAction(R.drawable.ic_call, "Call",call_pIntent)
                .addAction(R.drawable.ic_navigate,"Navigate",nev_pIntent);

        Intent resultIntent = new Intent(this, NotificationService.class);  // create intent to show notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);    // add parent to return to it when press the notification
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        if(notificationManager != null)
            notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
