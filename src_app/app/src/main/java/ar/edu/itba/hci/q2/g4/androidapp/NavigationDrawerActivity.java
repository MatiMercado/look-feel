package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.res.Configuration;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.ViewGroup;
import android.widget.TextView;

public class NavigationDrawerActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	/* +++xdebug label */



	ActionBarDrawerToggle toggle;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_drawer);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();


		final NavigationView navigationView;

		navigationView = (NavigationView) findViewById(R.id.nav_view);

		if(!isLogged()){

			navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer_not_log);

		}else{
			navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer);

		}

		navigationView.setNavigationItemSelectedListener(this);


		if(isLogged()){
			navigationView.getMenu().clear();
			navigationView.inflateMenu(R.menu.log_menu);

			View rootView = getLayoutInflater().inflate(R.layout.nav_header_navigation_drawer, (ViewGroup) findViewById(R.id.nav_view));
			TextView t = (TextView) rootView.findViewById(R.id.email_user);
			t.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (flag) {
						navigationView.getMenu().clear();
						navigationView.getMenu().add(User.getInstance().getUsername());
						navigationView.inflateMenu(R.menu.close_session);
						flag = false;
					} else {
						navigationView.getMenu().clear();
						navigationView.inflateMenu(R.menu.log_menu);
						flag = true;
					}
				}

			});
		}else{
			View rootView = getLayoutInflater().inflate(R.layout.nav_header_navigation_drawer_not_log, (ViewGroup) findViewById(R.id.nav_view));
			TextView t = (TextView) rootView.findViewById(R.id.email_user);
			t.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View view) {
					DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
					drawer.closeDrawer(GravityCompat.START);
					Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			});

		}

	}

	private boolean isLogged(){
		return User.getInstance().isLogged();

	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.


//////////////////////////////////////////////////////

		final NavigationView navigationView;

		navigationView = (NavigationView) findViewById(R.id.nav_view);


		if(isLogged()){
			navigationView.getMenu().clear();
			navigationView.inflateMenu(R.menu.log_menu);

			View rootView = getLayoutInflater().inflate(R.layout.nav_header_navigation_drawer, (ViewGroup) findViewById(R.id.nav_view));
			TextView t = (TextView) rootView.findViewById(R.id.email_user);
			t.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (flag) {
						navigationView.getMenu().clear();
						navigationView.getMenu().add(User.getInstance().getUsername());
						navigationView.inflateMenu(R.menu.close_session);
						flag = false;
					} else {
						navigationView.getMenu().clear();
						navigationView.inflateMenu(R.menu.log_menu);
						flag = true;
					}
				}

			});
		}else{
			View rootView = getLayoutInflater().inflate(R.layout.nav_header_navigation_drawer_not_log, (ViewGroup) findViewById(R.id.nav_view));
			TextView t = (TextView) rootView.findViewById(R.id.email_user);
			t.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View view) {
					DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
					drawer.closeDrawer(GravityCompat.START);
					Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			});

		}
		///////////////////////////////////////////


		toggle.syncState();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		toggle.onConfigurationChanged(newConfig);

	}

	@Override
	public void onBackPressed() {

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		searchView.setMaxWidth(10000);
//		searchView.setIconifiedByDefault(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
//			+++xremove does not cover all the cases --> introduces bugs
//			case android.R.id.home: /* home/up button */
//				finish();
//				return true;
			case R.id.action_settings:
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/* +++xtodo: implementar una clase hija que llame a todos los super
	.onMethod() que no esten definidos (sobre el navigation drawer para que
	esta clase pueda resolver qué comportamiento darle a esta herramienta
	ver si se puede setear o no el activo
	 */
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		String TAG_TITLE = "title";
		String TAG_AGE = "age";
		String TAG_GENRE = "genre";
		
		if (id == R.id.nav_home) {
			Intent parentActivityIntent = new Intent(this, Main.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();

		} else if (id == R.id.nav_women) {
			Intent intent = new Intent(this, CategoryActivity.class);
			intent.putExtra(TAG_TITLE, getResources().getString(R.string.category_women_title));
			intent.putExtra(TAG_AGE, "Adulto");
			intent.putExtra(TAG_GENRE, "Femenino");
			startActivity(intent);
		} else if (id == R.id.nav_men) {
			Intent intent = new Intent(this, CategoryActivity.class);
			intent.putExtra(TAG_TITLE, getResources().getString(R.string.category_men_title));
			intent.putExtra(TAG_AGE, "Adulto");
			intent.putExtra(TAG_GENRE, "Masculino");
			startActivity(intent);
		} else if (id == R.id.nav_girl) {
			Intent intent = new Intent(this, CategoryActivity.class);
			intent.putExtra(TAG_TITLE, getResources().getString(R.string.category_girls_title));
			intent.putExtra(TAG_AGE, "Infantil");
			intent.putExtra(TAG_GENRE, "Femenino");
			startActivity(intent);
		} else if (id == R.id.nav_boy) {
			Intent intent = new Intent(this, CategoryActivity.class);
			intent.putExtra(TAG_TITLE, getResources().getString(R.string.category_boys_title));
			intent.putExtra(TAG_AGE, "Infantil");
			intent.putExtra(TAG_GENRE, "Masculino");
			startActivity(intent);
		} else if (id == R.id.nav_baby) {
			Intent intent = new Intent(this, CategoryActivity.class);
			intent.putExtra(TAG_TITLE, getResources().getString(R.string.category_kid_title));
			intent.putExtra(TAG_AGE, "Bebe");
			startActivity(intent);
		} else if (id == R.id.nav_profile) {
			Intent intent = new Intent(this, Profile2Activity.class);
			startActivity(intent);

		} else if (id == R.id.nav_manage) {

			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);

		} else if (id == R.id.nav_help) {
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}


	public void closeSession(MenuItem item) {

		User.getInstance().LogOut();

		// Cancel Alarm
		AlarmManager mAlarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
		Intent mNotificationReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
		PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, mNotificationReceiverIntent, 0);
		mAlarmManager.cancel(mNotificationReceiverPendingIntent);

		//Go to main Activity
		Intent mainIntent = new Intent(NavigationDrawerActivity.this,
				Main.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(mainIntent);
		finish();
	}

}
