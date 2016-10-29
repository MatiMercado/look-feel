package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ar.edu.itba.hci.q2.g4.androidapp.Home;
import ar.edu.itba.hci.q2.g4.androidapp.R;

public class UpdatesReceiver extends BroadcastReceiver {

    private static final int ORDER_CHANGED_ID = 1;
    private final long[] mVibratePattern = { 0, 200, 200, 300 };
    private final Uri soundURI = Uri
            .parse("android.resource://ar.edu.itba.hci.q2.g4.androidapp/"
                    + R.raw.power_rangers);


    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Text Elements
    private CharSequence tickerText;
    private CharSequence contentTitle;
    private CharSequence contentText;
    private static Integer newOrders = 0, modifiedOrders = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("__HDEBUG:", "UPDATERECEIVER: En otra vista: Sending Notification");


        mNotificationIntent = new Intent(context,
                PurchasesActivity.class);
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        tickerText = context.getResources().getString(R.string.order_tickerText).toString();
        contentTitle = context.getResources().getString(R.string.order_contentTitle).toString();
        contentText = context.getResources().getString(R.string.order_contentText).toString();

        contentText = "";
        newOrders = intent.getIntExtra("newOrders", 0);
        modifiedOrders = intent.getIntExtra("modifiedOrders", 0);

        //TODO: Find a way to reset them
        if(newOrders>0){
            contentText = contentText + "Ordenes nuevas: " + newOrders.toString() + " \n";
        }
        if(modifiedOrders>0){
            contentText = contentText + "Ordenes modificadas: " + modifiedOrders.toString() + "\n";
        }

        if(User.getInstance().getNotificationsSet()) {
            Log.d("__HDEBUG","Las Notificacions estan Activadas");
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT < 16) {
                Notification.Builder notificationBuilder = new Notification.Builder(
                        context)
                        .setTicker(tickerText)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setAutoCancel(true)
                        .setContentTitle(contentTitle)
                        .setContentText(
                                contentText)
                        .setContentIntent(mContentIntent)
                                //.setSound(soundURI)
                        .setVibrate(mVibratePattern);

                mNotificationManager.notify(ORDER_CHANGED_ID, notificationBuilder.getNotification());

            } else {
                Notification.Builder notificationBuilder = new Notification.Builder(
                        context)
                        .setTicker(tickerText)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setAutoCancel(true)
                        .setContentTitle(contentTitle)
                        .setContentText(
                                contentText)
                        .setContentIntent(mContentIntent)
                        .setStyle(new Notification.BigTextStyle().bigText((CharSequence) contentText))
                                //.setSound(soundURI)
                        .setVibrate(mVibratePattern);

                mNotificationManager.notify(ORDER_CHANGED_ID, notificationBuilder.build());
            }
        }
        else{
            Log.d("__HDEBUG","Las Notificacions estan Desactivadas");
        }
    }
}
