package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends NavigationDrawerActivity {

	private static final String UI = "ui";
	static final String NEWLIST = "newList";
	static final String OFFERLIST = "offerList";

	private ViewPager mViewPager;
	private SearchView searchView;
	private static final String SEARCH_TEXT = "search_text";
	private RecyclerView offersRecyclerView;
	private RecyclerView productsRecyclerView;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private boolean async = false;
	private AsyncTaskWithContext<Void, Void, Void> thread;
	private ArrayList<Product> newList;
	private ArrayList<Product> offerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (savedInstanceState != null) {
			async = savedInstanceState.getBoolean(UI);
			newList = (ArrayList<Product>) savedInstanceState.getSerializable(NEWLIST);
			offerList = (ArrayList<Product>) savedInstanceState.getSerializable(OFFERLIST);
			Log.d("PRODUCT", "onCreate: async " + async);
		}

		if (!User.getInstance().isLogged()) {
			SharedPreferences.Editor editor = getSharedPreferences("LOCAL_DATA", MODE_PRIVATE).edit();

			//TODO: Sacar esto una vez que no se permita login mas de una vez
			editor.remove("LOCAL_DATA_USER");
//			editor.remove("LOCAL_DATA_ORDERS");
			editor.apply();
		}

		/* AUTO LOG IN (REMEMBER ME)*/
		/*
		SharedPreferences preferences = getSharedPreferences("LOCAL_DATA", 0);
		String prefString = preferences.getString("LOCAL_DATA_USER", null);
		if(prefString != null){
			try {
				JSONObject prefJSON = new JSONObject(prefString);

				if(prefJSON != null){
					ServiceHandler sh = new ServiceHandler();
					Map<String, String> p = new HashMap<>();
					User.getInstance().setUsername(prefJSON.getJSONObject("account").getString("username"));
					User.getInstance().JSONDataParse(prefJSON);
					//User.getInstance().setAuthenticationToken();

					p.put("username", User.getInstance().getUsername());
					p.put("authentication_token", User.getInstance().getAuthenticationToken());

					String orderStr = sh.makeServiceCall("Order", "GetAllOrders", p,
							ServiceHandler.GET);
					User.getInstance().setOrderStr(orderStr);
					User.getInstance().setLogged(true);

					SharedPreferences.Editor editor = getSharedPreferences("LOCAL_DATA", MODE_PRIVATE).edit();

					editor.putString("LOCAL_DATA_USER", User.getInstance().getJsonStr());
					editor.putString("LOCAL_DATA_ORDERS", User.getInstance().getOrderStr());
					editor.apply();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
		if (!async) {
			thread = new AsyncTaskWithContext<Void, Void, Void>() {
				//thread.interrupt(); +++xtodo
				//--> ver el codigo que paso harry por facebook
				protected void onPreExecute(){
					mViewPager = (ViewPager)findViewById(R.id.container);
					mViewPager.inflate(getApplicationContext(),R.layout.progress_icon, (ViewGroup) findViewById(R.id.main_content));
					findViewById(R.id.my_progress).setVisibility(View.VISIBLE);
				}


				@Override
				protected Void doInBackground(Void... params) {
					try {
						newList = new ArrayList<>();
						offerList = new ArrayList<>();

						//Map<String, String> p = new HashMap<>();
						//p.put("page_size", String.valueOf(Product.getProductsAmount()));
						//String url = "http://eiffel.itba.edu.ar/hci/service3/Catalog" +
						//        ".groovy?method=GetAllProducts" +
						//		"&page_size=" + Product.getProductsAmount();

						//String url = "http://192.168.0.104:8080/?method=GetAllProducts";
						//ServiceHandler sh = new ServiceHandler();
						//String jsonStr = sh.makeServiceCall("Catalog", "GetAllProducts",
						//        p, ServiceHandler.GET);

						//Product.ProductJSONParseKey(matched, newList, offerList, jsonStr,
						// key);

						Product.getAllMainProductsByType(newList, offerList, getResources());


						//array.clear();

					} catch (Exception e) {
						e.printStackTrace();
					}
					async = true;
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {

					findViewById(R.id.my_progress).setVisibility(View.GONE);

					mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), newList, offerList);

					// Set up the ViewPager with the sections adapter.
					mViewPager = (ViewPager)findViewById(R.id.container);

					mViewPager.setAdapter(mSectionsPagerAdapter);

					TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
					tabLayout.setupWithViewPager(mViewPager);
					thread = null;
				}
			};
			thread.execute();
		} else {
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), newList, offerList);

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager)findViewById(R.id.container);

			mViewPager.setAdapter(mSectionsPagerAdapter);

			TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
			tabLayout.setupWithViewPager(mViewPager);
		}


		/* +++xnote: vamos a tener que agregar esto a todos los archivos que
		 queramos que hereden de la actividad principal, a menos que
		 implementemos lo del 'hijo' intermedio.
		  */
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("SEARCH", "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);

		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		searchView.setMaxWidth(10000);

		return true;
	}

	@Override /* +++xtodo: remove this behaviour */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.search) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putBoolean(UI, async);
		Log.d("PRODUCT", "onSaveInstanceState: ANTES");
		savedInstanceState.putSerializable(NEWLIST, newList);
		savedInstanceState.putSerializable(OFFERLIST, offerList);

		Log.d("PRODUCT", "onSaveInstanceState: DESPUES");
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
		Log.d("PRODUCT", "onSaveInstanceState: DESPUES");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can restore the view hierarchy
		Log.d("PRODUCT", "onRestoreInstanceState: ANTES DE LLEGAR");
		super.onRestoreInstanceState(savedInstanceState);

		Log.d("PRODUCT", "onRestoreInstanceState: LLEGA");
		newList = (ArrayList<Product>) savedInstanceState.getSerializable(NEWLIST);
		offerList = (ArrayList<Product>) savedInstanceState.getSerializable(OFFERLIST);
		Log.d("PRODUCT", "onRestoreInstanceState: SALE");

		Log.d("SEARCH", savedInstanceState.toString());
		// Restore state members from saved instance
	}


//	private static int[] initOffersDataSet() {
//		return new int[]{
//				R.drawable.optimized_slide1,
//				R.drawable.optimized_slide2,
//				R.drawable.optimized_slide3
//		};
//	}
//
//
//	private static int[] initProductsDataSet() {
//
//		int[] images = {
//				R.drawable.sample_0,
//				R.drawable.sample_1,
//				R.drawable.sample_2,
//				R.drawable.sample_3,
//				R.drawable.sample_4,
//				R.drawable.sample_5,
//				R.drawable.sample_6,
//				R.drawable.sample_7
//		};
//
//		int[] arr = new int[7 * 4];
//		int counter = 0;
//		for (int i = 0; i < 4; i++) {
//			for (int j = 0; j < 7; j++, counter++) {
//				arr[counter] = images[j];
//			}
//		}
//
//		return arr;
//	}


	/**
	 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

		private ArrayList<Product> newList;
		private ArrayList<Product> offerList;

		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm,
		                            ArrayList<Product> newList,
		                            ArrayList<Product> offerList) {
			super(fm);
			this.newList = newList;
			this.offerList = offerList;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment2 (defined as a static inner class below).
			switch (position) {
				case 0:
					Log.d("PRODUCT", "tabNumber: " + 0);
					return PlaceholderFragment.newInstance(position + 1, offerList);
				default:
					Log.d("PRODUCT","Entro en el default del switch de getItem");
					return PlaceholderFragment.newInstance(position + 1, newList);
			}
			/* +++xnote: aca tendria que hacer alguna logica tipo if 0 -->
				productos y pasarle lo que tenga que ver con el criterio de
				busqueda utilizado
			 */
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getResources().getString(R.string.offers);
				case 1:
					return getResources().getString(R.string.outstanding);
			}
			return null;
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		return super.onNavigationItemSelected(item);
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends android.support.v4.app.Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		private static ArrayList<Product> newList;
		private static ArrayList<Product> offerList;

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber,
		                                              ArrayList<Product> arrayy) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);

			if (sectionNumber == 1) {
				offerList = arrayy;
			} else {
				newList = arrayy;
			}
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * Objects needed to manage the dynamic load of items on screen
		 */
		private static final String TAG = "RecyclerViewFragment";
		/* +++xdebug */
		private static final int DATASET_COUNT = 5;

		private RecyclerView mRecyclerView;
		private RecyclerView.Adapter mAdapter;
		private RecyclerView.LayoutManager mLayoutManager;

		private RecyclerView offersRecyclerView;
		private RecyclerView productsRecyclerView;

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

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			Log.d("SEARCH", "onCreateView");
//			View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
//			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//			textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//			return rootView;

			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
				return onCreateViewOffers(inflater, container,
						savedInstanceState);
			} else {
				return onCreateViewOutstanding(inflater, container,
						savedInstanceState);
			}
		}

		private View onCreateViewOffers(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.main_recycler_view, container, false);
			rootView.setTag(TAG);

			offersRecyclerView = (RecyclerView) rootView.findViewById(R.id
					.recyclerView);
			offersRecyclerView.setHasFixedSize(true);
			int spanCount = 2;
			int orientation = getResources().getConfiguration().orientation;
			if (isXLargeTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 5;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 4;
				}
			} else if (isLargeTablet(getActivity()) || isNormalTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 3;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 2;
				}
			} else {
				spanCount = 2;
			}
			LinearLayoutManager offersLayoutManager = new GridLayoutManager
					(getActivity(), spanCount);
			offersRecyclerView.setLayoutManager(offersLayoutManager);

			OffersAdapter offersAdapter = new OffersAdapter(offerList, getContext());
			offersRecyclerView.setAdapter(offersAdapter);

			return rootView;
		}

		private View onCreateViewOutstanding(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.main_recycler_view, container,
					false);
			rootView.setTag(TAG);

			productsRecyclerView = (RecyclerView) rootView.findViewById(R.id
					.recyclerView);
			productsRecyclerView.setHasFixedSize(true);
			int spanCount = 2;
			int orientation = getResources().getConfiguration().orientation;
			if (isXLargeTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 5;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 4;
				}
			} else if (isLargeTablet(getActivity()) || isNormalTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 3;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 2;
				}
			} else {
				spanCount = 2;
			}
			LinearLayoutManager productsLayoutManager = new GridLayoutManager
					(getActivity(), spanCount);
			productsRecyclerView.setLayoutManager(productsLayoutManager);

			ProductsAdapter productsAdapter = new ProductsAdapter(newList, getContext());
			productsRecyclerView.setAdapter(productsAdapter);

			return rootView;
		}


	}
}
