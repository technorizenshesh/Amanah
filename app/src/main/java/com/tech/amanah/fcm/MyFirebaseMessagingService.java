package com.tech.amanah.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.MusicManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.activities.MyOrdersAct;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import java.util.Random;;

public class MyFirebaseMessagingService
        extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notificationManager;
    private MediaPlayer mPlayer;
    Intent intent;
    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "fsfsdfd:" + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {

            Map<String, String> data = remoteMessage.getData();

            try {

                String title = "", key = "", status = "" , noti_type = "";

                JSONObject object = new JSONObject(data.get("message"));

                try {
                    key = object.getString("key");
                    status = object.getString("status");
                    noti_type = object.getString("noti_type");
                } catch (Exception e) {}

                if("DEV_FOOD".equals(noti_type)) {
                    title = "AmanahUser";
                    if ("Confirmed".equals(status) ||
                            "Accept".equals(status) ||
                            "Pickup".equals(status) ||
                            "Delivered".equals(status)) {
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);
                    }
                }

                sharedPref = SharedPref.getInstance(this);

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    if("Confirmed".equals(status)) {
                        displayCustomNotificationForOrders(status, title, getString(R.string.order_confirmed_text), object.toString());
                    } else if("Accept".equals(status)) {
                        displayCustomNotificationForOrders(status, title, getString(R.string.order_accept_text), object.toString());
                    } else if("Pickup".equals(status)) {
                        displayCustomNotificationForOrders(status, title, getString(R.string.order_pickup_text), object.toString());
                    } else if("Delivered".equals(status)) {
                        displayCustomNotificationForOrders(status, title, getString(R.string.order_devlivered_text), object.toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void displayCustomNotificationForOrders(String status, String title, String msg, String data) {

        intent = new Intent(this, MyOrdersAct.class);
        intent.putExtra("type", "dialog");
        intent.putExtra("data", data);
        intent.putExtra("object", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
            // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

}
