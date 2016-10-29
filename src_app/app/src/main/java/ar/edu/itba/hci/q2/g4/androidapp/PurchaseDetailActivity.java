package ar.edu.itba.hci.q2.g4.androidapp;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PurchaseDetailActivity extends NavigationDrawerActivity {

	private CardView cardView;
	private ImageView imageView;
	private TextView textView;
	private TextDrawable.IBuilder mDrawableBuilder;
	private String orderID;
	private Purchase purchaseDetail;
	private final String PURCHASE_DETAIL = "purchaseDetail";

	private final int CONFIRMED = 2;
	private final int TRANSPORTED = 3;
	private final int DELIVERED = 4;

	private View mViewPager;

	///////////// __HARRY BROADCAST__////////////////
	//This receiver starts a new service to update the shop orders view.
	private static BroadcastReceiver receiver;
	private static String UPDATE_ORDER_INTENT = "ar.edu.itba.hci.q2.g4.androidapp.UPDATE_ORDER_INTENT";
	///////////// __HARRY BROADCAST__////////////////

	//Variables for thread handling
	private boolean async = false;
	static final String UI = "ui";
	AsyncTaskWithContext<Void, Void, Void> thread = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_detail);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Orden " + getIntent().getStringExtra("ORDER_ID"));
		setSupportActionBar(toolbar);

		/* +++xtodo: set the title accordingly to the id given on the intent
		 to make the request
		  */


		// Get a support ActionBar corresponding to this toolbar
		android.support.v7.app.ActionBar ab = getSupportActionBar();

		// Enable the Up button
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
		////////////////////////THREAD

		if (savedInstanceState != null) {
			async = savedInstanceState.getBoolean(UI);
			purchaseDetail = (Purchase)savedInstanceState.getSerializable(PURCHASE_DETAIL);
		}

		if (!async) {
			thread = new AsyncTaskWithContext<Void, Void, Void>() {
				//thread.interrupt(); +++xtodo
				//--> ver el codigo que paso harry por facebook

				protected void onPreExecute(){
					mViewPager = findViewById(R.id.container_purch_detail);
					mViewPager.inflate(getApplicationContext(),R.layout.progress_icon, (ViewGroup) findViewById(R.id.main_content));
					findViewById(R.id.my_progress).setVisibility(View.VISIBLE);
				}

				@Override
				protected Void doInBackground(Void... params) {
					try {

						orderID = getIntent().getExtras().getString("ORDER_ID");
						JSONObject purchaseJSON = User.getInstance().getOrderByID(Integer.valueOf(orderID));
						purchaseDetail = initPurchase(purchaseJSON.getJSONObject("order"));

					} catch (Exception e) {
						e.printStackTrace();
					}
					async = true;
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {

					findViewById(R.id.my_progress).setVisibility(View.GONE);

					//Purchase purchase = initPurchase();
					initUI(purchaseDetail);
					thread = null;
				}
			};
			thread.setContext(this); // Should be call always before execute
			thread.execute();
		}
		else {
			initUI(purchaseDetail);
		}

		///////////////////////


		/***********
		//TODO: Move
		 doInBackground
		//Purchase purchase = initPurchase();

		 on PostExecute
		//initUI(purchase);
		**********/

		/* +++xnote: vamos a tener que agregar esto a todos los archivos que
		 queramos que hereden de la actividad principal, a menos que
		 implementemos lo del 'hijo' intermedio.
		  */
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	//////////////OTHERS (onSave/onRestore Instance and onDestroy)

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putBoolean(UI, async);
		savedInstanceState.putSerializable(PURCHASE_DETAIL, purchaseDetail);

		super.onSaveInstanceState(savedInstanceState);

	}

	//why make this when we're getting the stored data before calling thread?
	//Should we get flag?
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can restore the view hierarchy

		purchaseDetail = (Purchase) savedInstanceState.getSerializable(PURCHASE_DETAIL);
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onDestroy() {
		if (thread != null) {
			if (thread.getStatus() == AsyncTask.Status.RUNNING || thread.getStatus() == AsyncTask.Status.PENDING) {
				thread.cancel(true);
				Log.d("__HDEBUG", "PurchasesDetail: onDestroy ");
			}
		}
		super.onDestroy();
	}



	//////////////////



	@Override
	protected void onResume(){
		///////////// __HARRY BROADCAST__////////////////


		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				abortBroadcast();
				Log.d("__HDEBUG:", "En PurchaseActivityDetail");
				Intent purchases = new Intent(context, PurchaseDetailActivity.class);
				purchases.putExtra("ORDER_ID", getIntent().getStringExtra("ORDER_ID"));
				context.startActivity(purchases);
				finish();

			}
		};
		IntentFilter intentFilter = new IntentFilter(UPDATE_ORDER_INTENT);
		intentFilter.setPriority(3);
		registerReceiver(receiver, intentFilter);
		///////////// __HARRY BROADCAST__/////////////////
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

	private Purchase initPurchase(JSONObject purchaseJSON) {

		//TODO: ADD ERROR HANDLING

		Purchase.state state = null;
		String orderID = null;
		String status = null;
		String addressID = null;

		String receiveDate = null; //receivedDate
		String shippedDate = null; //transportedDate
		String deliveredDate = null; //deliveredDate


		String price = null;
		String stateString = null;
		String stateStringLong = null;

		String addressName = null;
		String province = null;
		String city = null;
		String telephone = null;
		String zipCode = null;
		String street = null;
		String number = null;
		String floor = null;
		String door = null;

		try {

			orderID = purchaseJSON.getString("id");
			status = purchaseJSON.getString("status");

			//receiveDate = purchaseJSON.getString("receivedDate").substring(0, 10);
			receiveDate = getReceiveDate(purchaseJSON);

			shippedDate = getShippedDate(purchaseJSON); //transportedDate
			deliveredDate = getDeliveredDate(purchaseJSON); //deliveredDate

			price = User.getInstance().getPriceFromOrder(purchaseJSON);

			addressName = purchaseJSON.getJSONObject("address").getString("name");
			addressID = purchaseJSON.getJSONObject("address").getString("id");

			JSONObject address = User.getInstance().getAddressByID(addressID).getJSONObject("address");


			province = address.getString("province");
			city = address.getString("city");
			telephone = address.getString("phoneNumber");
			zipCode = address.getString("zipCode");
			street = address.getString("street");
			number = address.getString("number"); //It's the street number
			floor = getFloor(address);
			door = getDoor(address);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		switch (Integer.valueOf(status)){

			case CONFIRMED:
				state = Purchase.state.CONFIRMED;
				stateString = getResources().getString(R.string.order_state_confirmed);
				stateStringLong = getResources().getString(R.string.order_state_confirmed_long);
				break;
			case TRANSPORTED:
				state = Purchase.state.TRANSPORTED;
				stateString = getResources().getString(R.string.order_state_transported);
				stateStringLong = getResources().getString(R.string.order_state_transported_long);
				break;

			case DELIVERED:
				state = Purchase.state.DELIVERED;
				stateString = getResources().getString(R.string.order_state_delivered);
				stateStringLong = getResources().getString(R.string.order_state_delivered_long);
				break;

			default:
				state = null;

		}

		/*	public Purchase(Purchase.state state, String stateString,
		String stateStringLong, String id,
				String confirmedDate, String transportedDate,
				String deliveredDate, String price, String addressName,
				String province, String city, String telephone,
				String zipCode, String street, String number,
				String floor, String door) {*/
		return new Purchase(
				state, stateString, stateStringLong, orderID, receiveDate, shippedDate,
				deliveredDate, "$" + price, addressName, province, city,
				telephone, zipCode, street, number, floor, door);

		/* TODO: DELETE

	//public Purchase(Purchase.state state, String stateString, String id, String confirmedDate, String price)
		return new Purchase(
				Purchase.state.CONFIRMED, getResources().getString(R.string
				.order_state_confirmed), getResources().getString(R.string
				.order_state_confirmed_long), "152", "24/8/1992", "---",
				"---", "$192", "Casita", "Buenos Aires", "Buenos Aires",
				"4512-2121", "1414", "Blablabla", "123", "---", "---"
		);
		*/



	}
	//TODO: Make this for all possible fields that might be null (All dates, floor and deparment)
	private String getReceiveDate(JSONObject purchaseJSON){
		String receiveDate;
		try {
			receiveDate = purchaseJSON.getString("receivedDate").substring(0, 10);
		} catch (Exception e) {
			receiveDate = "---";
		}
		return receiveDate;

	}

	private String getShippedDate(JSONObject purchaseJSON){
		String shippedDate;
		try {
			shippedDate = purchaseJSON.getString("shippedDate").substring(0, 10);
		} catch (Exception e) {
			shippedDate = "---";
		}
		return shippedDate;
	}

	private String getDeliveredDate(JSONObject purchaseJSON){
		String deliveredDate;
		try {
			deliveredDate = purchaseJSON.getString("deliveredDate").substring(0, 10);
		} catch (Exception e) {
			deliveredDate = "---";
		}
		return deliveredDate;

	}

	private String getFloor(JSONObject address){
		String floor;
		try {
			floor = address.getString("floor");
			if(floor == null || floor.equals("null")){
				floor = "---";
			}
		} catch (Exception e) {
			floor = "---";
		}
		return floor;

	}

	private String getDoor(JSONObject address){
		String door;
		try {
			door = address.getString("gate");
			if(door == null || door.equals("null")){
				door = "---";
			}
		} catch (Exception e) {
			door = "---";
		}
		return door;

	}

	private void initUI(Purchase purchase) {

		int CONFIRMED = 0xff607D8B;
		int TRANSPORTED = 0xffFFC107;
		int DELIVERED = 0xff4CAF50;

		String string;
		String space = "\t\t\t";

		mDrawableBuilder = TextDrawable.builder()
				.round();
		/* first card */
		imageView = (ImageView)findViewById(R.id.order_detail_status);
		switch (purchase.getState()) {
			case CONFIRMED:
				imageView.setImageDrawable(mDrawableBuilder.build(
						purchase.getStateString(), CONFIRMED));
				break;
			case TRANSPORTED:
				imageView.setImageDrawable(mDrawableBuilder.build(
						purchase.getStateString(), TRANSPORTED));
				break;
			case DELIVERED:
				imageView.setImageDrawable(mDrawableBuilder.build(
						purchase.getStateString(), DELIVERED));
				break;
		}
		string = purchase.getStateStringLong();
		textView = (TextView) findViewById(R.id.order_detail_status_string);
		textView.setText(string);


		/* second card */
		string = getResources().getString(R.string
				.purchase_detail_price_title) + space +
				purchase.getPrice();
		textView = (TextView) findViewById(R.id.order_detail_price);
		textView.setText(string);


		/* third card */
		string = getResources().getString(R.string
				.purchase_detail_date_confirmed_title) + space + purchase
				.getConfirmedDate();
		textView = (TextView) findViewById(R.id.order_detail_date_confirmed);
		textView.setText(string);

		string = getResources().getString(R.string
				.purchase_detail_date_transported_title) + space + purchase
				.getTransportedDate();
		textView = (TextView) findViewById(R.id.order_detail_date_transported);
		textView.setText(string);


		string = getResources().getString(R.string
				.purchase_detail_date_delivered_title) + space + purchase
				.getDeliveredDate();
		textView = (TextView) findViewById(R.id.order_detail_date_delivered);
		textView.setText(string);


		/* fourth card */
		string = getResources().getString(R.string
				.purchase_detail_address_name_title) + space + purchase
				.getAddressName();
		textView = (TextView) findViewById(R.id.order_detail_address_name);
		textView.setText(string);

		string = getResources().getString(R.string
				.purchase_detail_province_title) + space + purchase
				.getProvince();
		textView = (TextView) findViewById(R.id.order_detail_province);
		textView.setText(string);

		string = getResources().getString(R.string
				.purchase_detail_city_title) + space + purchase
				.getCity();
		textView = (TextView) findViewById(R.id.order_detail_city);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_telephone_title) + space + purchase
				.getTelephone();
		textView = (TextView) findViewById(R.id.order_detail_telephone);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_zip_code_title) + space + purchase
				.getZipCode();
		textView = (TextView) findViewById(R.id.order_detail_zip_code);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_street_title) + space + purchase
				.getStreet();
		textView = (TextView) findViewById(R.id.order_detail_street);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_number_title) + space + purchase
				.getNumber();
		textView = (TextView) findViewById(R.id.order_detail_number);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_floor_title) + space + purchase
				.getFloor();
		textView = (TextView) findViewById(R.id.order_detail_floor);
		textView.setText(string);

		string = getResources().getString(R.string
				.order_detail_door_title) + space + purchase
				.getDoor();
		textView = (TextView) findViewById(R.id.order_detail_door);
		textView.setText(string);
	}
}
