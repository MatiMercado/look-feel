package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import ar.edu.itba.hci.q2.g4.androidapp.R;

public class HelpActivity extends NavigationDrawerActivity {

    ListView list;
    Button how_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition     = position;
                String  itemValue    = (String) list.getItemAtPosition(position);

//                Toast.makeText(getApplicationContext(), R.string.Construction, Toast.LENGTH_SHORT).show();
	            Snackbar.make(view, R.string.Construction, Snackbar.LENGTH_SHORT)
			            .setAction("Action", null).show();
            }
        });

        how_to = (Button) findViewById(R.id.button);

        how_to.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//              Toast.makeText(getApplicationContext(), R.string.Construction, Toast.LENGTH_SHORT).show();
	            Snackbar.make(v, R.string.Construction, Snackbar.LENGTH_SHORT)
			            .setAction("Action", null).show();
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
