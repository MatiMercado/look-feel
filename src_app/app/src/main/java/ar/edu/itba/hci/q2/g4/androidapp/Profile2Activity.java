package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Profile2Activity extends NavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile22);

	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);

	    // Get a support ActionBar corresponding to this toolbar
	    android.support.v7.app.ActionBar ab = getSupportActionBar();

	    // Enable the Up button
	    if (ab != null) {
		    ab.setDisplayHomeAsUpEnabled(true);
	    }


        TextView list = (TextView) findViewById(R.id.Buys);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(Profile2Activity.this, PurchasesActivity.class);
//                next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(next);
//                finish();
            }
        });

	    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
	    navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		/* Overriden so as to avoid loading the search icon */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // this is item ID of that back part which you want
            case android.R.id.home:
                // Control will come here when back/or app icon is pressed
                Intent parentActivityIntent = new Intent(this, Main.class);
                parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
