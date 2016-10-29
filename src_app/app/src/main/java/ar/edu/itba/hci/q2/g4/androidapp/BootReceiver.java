package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

/*
* This class checks wheter the user is Logged, in which case an alarm is set (See AlarmReceiver Class). The alarm runs
* INITIAL_ALARM_DELAY after it's created, and then it runs again every ALARM_INTERVAL amount of time.
* This class onReceive method is called on two cases: a) The system booted up. b) The user Signed In.
* */

public class BootReceiver extends BroadcastReceiver {

   // private AlarmManager mAlarmManager;
    //private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    //private PendingIntent mNotificationReceiverPendingIntent,
     //       mLoggerReceiverPendingIntent;

    private static final long INITIAL_ALARM_DELAY = 10 * 1000; //(10 seconds Interval)
    private static final long ALARM_INTERVAL = 10 * 1000;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(User.getInstance().isLogged()){
            setAlarm(context);
            /*
            // Get the AlarmManager Service
            mAlarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            // Create an Intent to broadcast to the AlarmNotificationReceiver

            mNotificationReceiverIntent = new Intent(context, AlarmReceiver.class);
            mNotificationReceiverIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);

            // Create an PendingIntent that holds the NotificationReceiverIntent
            mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                    context, 0, mNotificationReceiverIntent, 0);

            // Set repeating alarm to fire shortly after previous alarm
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                    ALARM_INTERVAL,
                    mNotificationReceiverPendingIntent);
            */
            //TODO: DELETE
            //To cancel toast uncomment the following line and run application.
            //mAlarmManager.cancel(mNotificationReceiverPendingIntent);
        }
    }

    public static void setAlarm(Context context){
       AlarmManager mAlarmManager;
        Intent mNotificationReceiverIntent;
        PendingIntent mNotificationReceiverPendingIntent;

        mAlarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmReceiver
        mNotificationReceiverIntent = new Intent(context, AlarmReceiver.class);
        mNotificationReceiverIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                context, 0, mNotificationReceiverIntent, 0);

        // Set repeating alarm to fire shortly after previous alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                ALARM_INTERVAL,
                mNotificationReceiverPendingIntent);
    }

}
