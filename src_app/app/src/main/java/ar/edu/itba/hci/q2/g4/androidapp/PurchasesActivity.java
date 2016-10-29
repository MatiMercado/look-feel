package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PurchasesActivity extends NavigationDrawerActivity {

	private RecyclerView pruchasesRecyclerView;
	private LinearLayoutManager purchasesLayoutManager;
	private PurchasesAdapter purchasesAdapter;
	private ArrayList<Purchase> ordersDataSet;

	private View mViewPager;

	/*
	 * Keys for the savedInstanceState
	 */
	static final String ORDERS_DATA_SET = "ordersDataSet";

	//Variables for thread handling
	private boolean async = false;
	static final String UI = "ui";
	AsyncTaskWithContext<Void, Void, Void> thread = null;

	///////////// __HARRY BROADCAST__////////////////
	//This receiver starts a new service to update the shop orders view.
	private static  BroadcastReceiver receiver;
	private static String UPDATE_ORDER_INTENT = "ar.edu.itba.hci.q2.g4.androidapp.UPDATE_ORDER_INTENT";
	///////////// __HARRY BROADCAST__////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchases);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Get a support ActionBar corresponding to this toolbar
		android.support.v7.app.ActionBar ab = getSupportActionBar();

		// Enable the Up button
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}

		//////////THREAD
		if (savedInstanceState != null) {
			async = savedInstanceState.getBoolean(UI);
			ordersDataSet = (ArrayList<Purchase>)savedInstanceState.getSerializable(ORDERS_DATA_SET);
			Log.d("__HDEBUG", "BACKUP: " + ordersDataSet.toString());
		}

		if (!async) {
			thread = new AsyncTaskWithContext<Void, Void, Void>() {
				//thread.interrupt(); +++xtodo
				//--> ver el codigo que paso harry por facebook

				protected void onPreExecute(){
					mViewPager = findViewById(R.id.recyclerView);
					mViewPager.inflate(getApplicationContext(),R.layout.progress_icon, (ViewGroup) findViewById(R.id.main_content));
					findViewById(R.id.my_progress).setVisibility(View.VISIBLE);
				}

				@Override
				protected Void doInBackground(Void... params) {
					try {
						ordersDataSet = initOrdersDataSet();
					} catch (Exception e) {
						e.printStackTrace();
					}
					async = true;
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {

					findViewById(R.id.my_progress).setVisibility(View.GONE);

					if (ordersDataSet == null) {
						Intent parentActivityIntent = new Intent(getContext(), LoginActivity.class);
						parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						finish();
						startActivity(parentActivityIntent);
						return;
					}

					int spanCount;

					int orientation = getResources().getConfiguration().orientation;
					if (isXLargeTablet(getContext())) {
						if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
							spanCount = 3;
						} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
							spanCount = 2;
						}
					} else {
						spanCount = 1;
					}

					pruchasesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
					pruchasesRecyclerView.setHasFixedSize(true);
					purchasesLayoutManager = new GridLayoutManager(getContext(), spanCount);
					pruchasesRecyclerView.setLayoutManager(purchasesLayoutManager);


					purchasesAdapter = new PurchasesAdapter(ordersDataSet, getContext());
					pruchasesRecyclerView.setAdapter(purchasesAdapter);

					thread = null;
				}
			};
			thread.setContext(this); // Should be call always before execute
			thread.execute();
		}
		else {
			int spanCount;

			int orientation = getResources().getConfiguration().orientation;
			if (isXLargeTablet(getApplicationContext())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 3;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 2;
				}
			} else {
				spanCount = 1;
			}

			pruchasesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
			pruchasesRecyclerView.setHasFixedSize(true);
			purchasesLayoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
			pruchasesRecyclerView.setLayoutManager(purchasesLayoutManager);


			purchasesAdapter = new PurchasesAdapter(ordersDataSet, getApplicationContext());
			pruchasesRecyclerView.setAdapter(purchasesAdapter);
		}

		///////////END THREAD

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putBoolean(UI, async);
		savedInstanceState.putSerializable(ORDERS_DATA_SET, ordersDataSet);

		super.onSaveInstanceState(savedInstanceState);
	}

	//why make this when we're getting the stored data before calling thread?
	//Should we get flag?
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can restore the view hierarchy

		ordersDataSet = (ArrayList<Purchase>) savedInstanceState.getSerializable(ORDERS_DATA_SET);
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onDestroy() {
		if (thread != null) {
			if (thread.getStatus() == AsyncTask.Status.RUNNING || thread.getStatus() == AsyncTask.Status.PENDING) {
				thread.cancel(true);
				Log.d("__HDEBUG", "PurchasesActivity: onDestroy ");
			}
		}
		super.onDestroy();
	}



	@Override
	protected void onResume(){
		///////////// __HARRY BROADCAST__////////////////


		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				abortBroadcast();
				Log.d("__HDEBUG:", "En PurchasesActivity");
				Intent purchases = new Intent(context, PurchasesActivity.class);
				context.startActivity(purchases);
				finish();

			}
		};
		IntentFilter intentFilter = new IntentFilter(UPDATE_ORDER_INTENT);
		intentFilter.setPriority(2);
		registerReceiver(receiver, intentFilter);
		///////////// __HARRY BROADCAST__////////////////
		super.onResume();
	}

	@Override
	protected void onPause(){
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Overriden so as to avoid loading the search icon */

		return true;
	}

	private static boolean isSmallTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_SMALL;
	}

	private static boolean isNormalTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
	}

	private static boolean isLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	private ArrayList<Purchase> initOrdersDataSet() {

		ArrayList<Purchase> purchases = new ArrayList<>();

		Purchase.state [] states = {
				Purchase.state.CONFIRMED, Purchase.state.TRANSPORTED, Purchase.state.DELIVERED
		};

		String [] stateString = {
				getResources().getString(R.string.order_state_confirmed),
				getResources().getString(R.string.order_state_transported),
				getResources().getString(R.string.order_state_delivered),
		};

		JSONArray ordersList = User.getInstance().getAllOrders();

		try{

			for (int i = 0; i < ordersList.length(); i++) {

				// Variables used to create a single Purchase

				Integer stateIndex = Integer.valueOf(ordersList.getJSONObject(i).getString("status"));

				if (stateIndex > 1) {
					stateIndex -= 2; //Get the right index for the given state (Possible states: 2, 3, 4);
					Integer orderID = Integer.valueOf(ordersList.getJSONObject(i).getInt("id"));

					JSONObject order = User.getInstance().getOrderByID(orderID).getJSONObject("order");

					//getPriceFromOrder returns the totalPrice for a given order or "---" if an error is caught
					String price = User.getInstance().getPriceFromOrder(order);
					String confirmedDate;

					//First take the JSONObjects to make sure they are not null
					JSONObject confirmedDateJSON = ordersList.getJSONObject(i);

					if (confirmedDateJSON == null) {
						confirmedDate = "---";
					} else {
						confirmedDate = confirmedDateJSON.getString("receivedDate").substring(0,10);
					}

					purchases.add(new Purchase(states[stateIndex], stateString[stateIndex], String
							.valueOf(orderID), confirmedDate, "$" + price));
				}
			}
			return purchases;
		}
		catch(JSONException e){
			e.printStackTrace();
		}
		return null;

	}


}
