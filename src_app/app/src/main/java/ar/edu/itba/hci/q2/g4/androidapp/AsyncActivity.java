package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ar.edu.itba.hci.q2.g4.androidapp.R;

public class AsyncActivity extends Activity {

    Button button;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        button = (Button) findViewById(R.id.button2);
        image = (ImageView) findViewById(R.id.imageView2);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String requestUrl = "http://eiffel.itba.edu.ar/hci/service3/Common.groovy?method=GetAllStates";

                new AsyncTask<Object, Void, Drawable>() {

                    ImageView image;


                    @Override
                    protected Drawable doInBackground(Object... params) {
                        image = (ImageView) params[1];
                        ServiceHandler sh = new ServiceHandler();

                        String jsonStr = sh.makeServiceCall("Common", "GetAllStates", null,
                                ServiceHandler.GET);

                        Log.d("Response: ", "> " + jsonStr);

                        Drawable a = ServiceHandler.LoadImageFromWebOperations("http://eiffel.itba.edu.ar/hci/service3/images/camvm1.jpg");

                        return a;
                    }

                    @Override
                    protected void onPostExecute(Drawable drawable) {
                        image.setImageDrawable(drawable);
                    }

                }.execute(requestUrl, image);

            }

        });
    }
}



