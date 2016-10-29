package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/*
* When the alarm is fired, an IntentService is created to check updates in the user's orders.
* (See UpdatesIntentService)
* */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO: DELEETE
        Log.d("__HDEBUG:", "AlarmReceiver: Alarm activated.");


        /*... 1. Start Service to make an API request, and store the new data.
              2. Before finishing, if changes were found the service must broadcast an intent to the
              UpdatesReceiver and other receiver in the OrderScreen.
              3. The OrdereScreenReceiver should receive the intent first,
              update the current view should the user be in it
               and prevent the intent from spreading further. */

        /*Preguntas:
        * 1. Service o AsynkTask? Como hacer la request? Como storeo data en preferences?
        * 2. Que mostrar en la notificacion del cambio de orden (la que ya hice): Mostrar que cambio
        * el estado de alguna compra? Que se compro un nuevo producto? Necesito pasarle informacion
        * del service?
        * 3.
        *
        * Respuestas:
        * 2. Revisar el putExtra del intent
        * */

       // Intent myIntent = new Intent(context, ServiceClassName.class);
       // context.startService(myIntent);

        Intent updatesServiceIntent = new Intent(context, UpdatesIntentService.class);
        context.startService(updatesServiceIntent);


    }
    
}
