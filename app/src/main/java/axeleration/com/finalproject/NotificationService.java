package axeleration.com.finalproject;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class NotificationService extends IntentService{

    private Cursor cursor;
    private SQLiteDatabase db;
    boolean showOnlyOnce=true;
    boolean flag;

    public NotificationService(){
        super("NotificationService");
        flag = true;
        db = DBHelperSingleton.getInstanceDBHelper(this).getReadableDatabase();
        Log.d("temp", "creating service notification");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        while(flag) {
            try {
                Thread.sleep(10000);
                Log.d("temp","Service Runs");
                if(!db.isOpen()) {
                    return;
                }
                cursor=db.query(Constants.TASKS.TABLE_NAME,
                        null,
                        Constants.TASKS.IS_SIGN + "=? AND " + Constants.TASKS.DATETIME + ">=? AND " +  Constants.TASKS.DATETIME + "<=?",
                        new String[] {"0", getCurrentDate(), getDate()},
                        null,
                        null,
                        Constants.TASKS.DATETIME + " ASC");
                if(cursor.getCount()!=0 && showOnlyOnce)
                {
                    cursor.moveToFirst();
                    final String phoneNumber = cursor.getString(cursor.getColumnIndex(Constants.TASKS.PHONE_NUMBER));
                    final String clientName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.CLIENT_NAME));
                    final String receiverName = cursor.getString(cursor.getColumnIndex(Constants.TASKS.FULL_NAME));
                    final String address =cursor.getString(cursor.getColumnIndex(Constants.TASKS.ADDRESS));
                    setNotificationCall(receiverName,clientName,phoneNumber,address);
                    showOnlyOnce=false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }

    public void setNotificationCall(String recvName,String clientName,String phoneNum,String address){
        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = "my_channel_01";


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("temp", "inside");

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
            notificationManager.createNotificationChannel(mChannel);
        }
        Intent nevIntent = new Intent(Intent.ACTION_VIEW);
        nevIntent.setData(Uri.parse("google.navigation:q=" + address));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission for sms not allowed",Toast.LENGTH_SHORT).show();
            return;
        }
        //todo : send sms when press nevigate
//        SmsManager.getDefault().sendTextMessage(phoneNum,"","Hello " + recvName + ",\nIm the delivery boy.\nI'm on my way\nPlease be available on the next 2 hours.\nThank you.",null,null);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNum));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission for calls not allowed",Toast.LENGTH_SHORT).show();
            return;
        }

        PendingIntent nev_pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), nevIntent, 0);
        PendingIntent call_pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), callIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Package to deliver:")
                .setContentText("GO to "+ recvName+"\n"+ "from "+ clientName)
                .addAction(R.drawable.ic_call, "Call",call_pIntent)
                .addAction(R.mipmap.ic_launcher, "Kick", call_pIntent)
                .addAction(R.drawable.ic_navigate,"Navigate",nev_pIntent);

        Intent resultIntent = new Intent(this, NotificationService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    private String getDate (){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(c.getTime());
    }





}
