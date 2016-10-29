package ar.edu.itba.hci.q2.g4.androidapp;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Semaphore;


/*
* Checks the server for updates. Should it find any updates, an Intent is Broadcast in order: First to
* UpdateOrdersVuewReceiver, and second to UpdatesReceiver.
* */
public class UpdatesIntentService extends IntentService {

    private static String UPDATE_ORDER_INTENT = "ar.edu.itba.hci.q2.g4.androidapp.UPDATE_ORDER_INTENT";

       public UpdatesIntentService() {
        super("UpdatesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d("__HDEBUG:", "UPDATEINTENTSERVICE: Atendiendo el Service de la alarma");
        int newOrders, modifiedOrders;

        //newOrders = User.getInstance().checkNewOrders();
        //modifiedOrders = User.getInstance().checkModifiedOrders();

        newOrders = User.getInstance().checkNewOrders(getSharedPreferences("LOCAL_DATA", MODE_PRIVATE));
        modifiedOrders = User.getInstance().checkModifiedOrders(getSharedPreferences("LOCAL_DATA", MODE_PRIVATE));

        Log.d("__HDEBUG:", "User Nuevas: " + newOrders);
        Log.d("__HDEBUG:", "User Modificadas: " + modifiedOrders);

        if(User.getInstance().getNotificationsSet()){
            Log.d("__HDEBUG:", "UPDATE SERVICE: NOTIFICATIONS ON");
        }else{
            Log.d("__HDEBUG:", "UPDATE SERVICE: NOTIFICATINOS OFF");
        }

        if (newOrders > 0 || modifiedOrders > 0){
            //TODO: Send Extra data on Intent?

            User.getInstance().updateUserOrders(getApplicationContext().getSharedPreferences("LOCAL_DATA", 0));

            Intent updateOrderIntent = new Intent();
            updateOrderIntent.setAction(UPDATE_ORDER_INTENT);

            updateOrderIntent.putExtra("newOrders", newOrders);
            updateOrderIntent.putExtra("modifiedOrders", modifiedOrders);

            //sendBroadcast(updateOrderIntent);
            sendOrderedBroadcast(updateOrderIntent, Manifest.permission.VIBRATE);
        }
    }
}
