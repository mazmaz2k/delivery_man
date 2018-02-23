package axeleration.com.finalproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by mazma on 18/02/2018.
 */

public class NotificationService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name' Used to name the worker thread, important only for debugging.
     */
    public static final String NOTIFICATION_CHANNEL_ID = "4655";

    boolean flag;
    NotificationCompat.Builder notification;
    public NotificationService(){
        super("NotificationService");

    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean flag = true;
        while(flag) {
            try {
                Thread.sleep(10000);
                setNotificationCall();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
    }

    public void setNotificationCall(){
        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            String CHANNEL_ID = "my_channel_01";
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
        Intent intent = new Intent(this,NewTask.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_01")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("new")
                .setContentText("new")
                .addAction(R.drawable.ic_call, "Call",pIntent);

        Intent resultIntent = new Intent(this, NotificationService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
