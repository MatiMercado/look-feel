package ar.edu.itba.hci.q2.g4.androidapp;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.hci.q2.g4.androidapp.R;

public class SearchResultsActivity extends NavigationDrawerActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	private SearchView searchView;

	/*
	 * Keys for the savedInstanceState
	 */
	static final String MATCHED = "matched";
    static final String NEWLIST = "newList";
    static final String OFFERLIST = "offerList";

	static final String SEARCH_TEXT = "search_text";
	static final String NAV_BAR_OPTION = "nav_bar_option";

	CharSequence last_searchViewText = null;

	static final String UI = "ui";

	private boolean async = false;

	//static ArrayList<Product> array = new ArrayList<Product>();
	static ArrayList<Product> matched = new ArrayList<Product>();
    static ArrayList<Product> newList = new ArrayList<Product>();
    static ArrayList<Product> offerList = new ArrayList<Product>();

	AsyncTask<Void, Void, Void> thread = null;


	@Override
	protected void onDestroy() {
		if (thread != null) {
			if (thread.getStatus() == AsyncTask.Status.RUNNING || thread.getStatus() == AsyncTask.Status.PENDING) {
				thread.cancel(true);
				Log.d("PRODUCT", "onDestroy: ");
			}
		}
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("PRODUCT", "OnCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Get a support ActionBar corresponding to this toolbar
		android.support.v7.app.ActionBar ab = getSupportActionBar();

		// Enable the Up button
		if (ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.

		if (savedInstanceState != null) {
			async = savedInstanceState.getBoolean(UI);
            matched = (ArrayList<Product>) savedInstanceState.getSerializable(MATCHED);
            newList = (ArrayList<Product>) savedInstanceState.getSerializable(NEWLIST);
            offerList = (ArrayList<Product>) savedInstanceState.getSerializable(OFFERLIST);
			Log.d("PRODUCT", "onCreate: async " + async);
		}

		if (!async) {
			thread = new AsyncTask<Void, Void, Void>() {
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
						matched = new ArrayList<Product>();
                        newList = new ArrayList<Product>();
                        offerList = new ArrayList<Product>();

						if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
							String query = getIntent().getStringExtra(SearchManager.QUERY);

                            //Map<String, String> p = new HashMap<>();
                            //p.put("page_size", String.valueOf(Product.getProductsAmount()));
							//String url = "http://eiffel.itba.edu.ar/hci/service3/Catalog" +
                            //        ".groovy?method=GetAllProducts" +
							//		"&page_size=" + Product.getProductsAmount();

							//String url = "http://192.168.0.104:8080/?method=GetAllProducts";
							//ServiceHandler sh = new ServiceHandler();
							//String jsonStr = sh.makeServiceCall("Catalog", "GetAllProducts",
                            //        p, ServiceHandler.GET);
							String key = query;

							//Product.ProductJSONParseKey(matched, newList, offerList, jsonStr,
							// key);

							Product.productSearchByType(matched, newList, offerList, key, null,
                                    getResources());

							for (int i = 0; i < matched.size(); i++)
								Log.d("PRODUCT", "doInBackground: " + matched.get(i).getName() + " - " + matched.get(i).getPhoto());

                            for (int i = 0; i < newList.size(); i++)
                                Log.d("PRODUCT", "doInBackground: NUEVOS " + newList.get(i).getName() + " - " + newList.get(i).getPhoto());

                            for (int i = 0; i < offerList.size(); i++)
                                Log.d("PRODUCT", "doInBackground: OFERTAS " + offerList.get(i).getName() + " - " + offerList.get(i).getPhoto());


                            Log.d("PRODUCT", "LENS: " + matched.size() + " - " + newList.size() + " - " + offerList.size());
							//array.clear();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					async = true;
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {

					findViewById(R.id.my_progress).setVisibility(View.GONE);

					mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), matched, newList, offerList);

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
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), matched, newList, offerList);
            Log.d("PRODUCT", "onCreate: " + String.valueOf(matched == null) + " - " + String.valueOf(newList == null) + " - " + String.valueOf(offerList == null));

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
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putCharSequence(SEARCH_TEXT, searchView.getQuery());
		savedInstanceState.putBoolean(UI, async);
		Log.d("PRODUCT", "onSaveInstanceState: ANTES");
		savedInstanceState.putSerializable(MATCHED, matched);
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
		matched = (ArrayList<Product>) savedInstanceState.getSerializable(MATCHED);
        newList = (ArrayList<Product>) savedInstanceState.getSerializable(NEWLIST);
        offerList = (ArrayList<Product>) savedInstanceState.getSerializable(OFFERLIST);
		Log.d("PRODUCT", "onRestoreInstanceState: SALE");

		Log.d("SEARCH", savedInstanceState.toString());
		// Restore state members from saved instance
		last_searchViewText = savedInstanceState.getCharSequence(SEARCH_TEXT);
	}


	private void handleIntent(Intent intent) {

		if (intent == null) {
			Log.d("SEARCH", "Null intent");
			return;
		}
		Log.d("SEARCH", "handleIntent");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			//use the query to search your data somehow
            /* +++xdebug */
			android.support.v7.app.ActionBar ab = getSupportActionBar();

			// Enable the Up button
			if (ab != null) {
				if (searchView != null) {
					if (last_searchViewText == null) {
						searchView.setQuery(query, false);
						last_searchViewText = query;
						//mSectionsPagerAdapter.setQuery(query);
						//Accede solamente si se esta haciendo una nueva intent
						Log.d("___TEXT___", query);
						//Product.searchProducts(query, getLayoutInflater());
					} else {
						searchView.setQuery(last_searchViewText, false);
					}
					searchView.clearFocus();
				}
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("PRODUCT", "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);

		/* +++xdoing */
//		// Associate searchable configuration with the SearchView
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		searchView.setMaxWidth(10000);
		menu.findItem(R.id.search).expandActionView();

		MenuItem search = menu.findItem(R.id.search);

		MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem menuItem) {
				return false;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem menuItem) {
				Intent parentActivityIntent = new Intent(SearchResultsActivity.this, Main.class);
				parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				finish();
				startActivity(parentActivityIntent);
				return false;
			}
		});

//		searchView.setIconifiedByDefault(false); /* +++xcheck: this is
//		supposed to make the search bar extend */

		handleIntent(getIntent());

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String s) {
				matched = new ArrayList<Product>();
                newList = new ArrayList<Product>();
                offerList = new ArrayList<Product>();
				async = false;
				sa(s);
				finish();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

    public void sa(String s){
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SearchManager.QUERY, s);
        intent.setAction(Intent.ACTION_SEARCH);
        startActivity(intent);
	}

	/**
	 * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

		ArrayList<Product> array;
        ArrayList<Product> newList;
        ArrayList<Product> offerList;

		public SectionsPagerAdapter(FragmentManager fm, ArrayList<Product> array, ArrayList<Product> newList, ArrayList<Product> offerList) {
			super(fm);
            if (array == null || newList == null || offerList == null)
                Log.d("PRODUCT", "SectionsPagerAdapter: ATENCIOOOOOOOOON!!!!!!!!! NULL !!!!");
            this.array = array;
            this.newList = newList;
            this.offerList = offerList;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below)
			switch (position) {
                case 0:
                    Log.d("PRODUCT", "tabNumber: " + 0);
                    Log.d("PRODUCT", "array: " + toString(array));
                    return PlaceholderFragment.newInstance(position + 1, array);
                case 1:
                    Log.d("PRODUCT", "tabNumber: " + 1);
                    Log.d("PRODUCT", "array: " + toString(newList));
                    return PlaceholderFragment.newInstance(position + 1, newList);
                case 2:
                    Log.d("PRODUCT", "tabNumber: " + 2);
                    Log.d("PRODUCT", "array: " + toString(offerList));
                    return PlaceholderFragment.newInstance(position + 1, offerList);
                default:
                    Log.d("PRODUCT","Entro en el default del switch de getItem");
                    return PlaceholderFragment.newInstance(position + 1, array);
            }
		}

        private String toString(ArrayList<Product> array) {
            StringBuffer s = new StringBuffer();

            for (int i = 0 ; i < array.size() ; i++) {
                s.append(array.get(i).getName()).append(" || ");
            }

            return s.toString();
        }

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getResources().getString(R.string.tab_title_search_results);
				case 1:
					return getResources().getString(R.string.tab_title_new);
				case 2:
					return getResources().getString(R.string.tab_title_deals);
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

		private static ArrayList<Product> array;
        private static ArrayList<Product> newList;
        private static ArrayList<Product> offerList;

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<Product> arrayy) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);

            if (sectionNumber == 1) {
                array = arrayy;
            } else if (sectionNumber == 2) {
                newList = arrayy;
            } else {
                offerList = arrayy;
            }
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Initialize dataset, this data would usually come from a local content provider or
			// remote server.
			//initDataset();
		}

		/*
		 * Objects needed to manage the dynamic load of items on screen
		 */
		private static final String TAG = "RecyclerViewFragment";
		/* +++xdebug */
		private static final int DATASET_COUNT = 60;


		private ArrayList<FilterButton> noCategories = new ArrayList<>();
		protected List<Product> mDataset;
		private RecyclerView mRecyclerView;
        private RecyclerView mRecyclerView2;
        private RecyclerView mRecyclerView3;
		private RecyclerView.Adapter mAdapter;
        private RecyclerView.Adapter mAdapter2;
        private RecyclerView.Adapter mAdapter3;
		private RecyclerView.LayoutManager mLayoutManager;
        private RecyclerView.LayoutManager mLayoutManager2;
        private RecyclerView.LayoutManager mLayoutManager3;

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

//			View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
//			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//			textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//			return rootView;

			int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
			switch (tabNumber) {
				case 1:
					if (array == null || array.size() == 0) {
						View rootView = inflater.inflate(R.layout.my_text_view, container, false);
						TextView textview = (TextView) rootView.findViewById(R.id.textView);
						textview.setText(R.string.products_not_found);
						return rootView;
					}
					break;
				case 2:
					if (newList == null || newList.size() == 0) {
						View rootView = inflater.inflate(R.layout.my_text_view, container, false);
						TextView textview = (TextView) rootView.findViewById(R.id.textView);
						textview.setText(R.string.products_not_found);
						return rootView;
					}
					break;
				case 3:
					if (offerList == null || offerList.size() == 0) {
						View rootView = inflater.inflate(R.layout.my_text_view, container, false);
						TextView textview = (TextView) rootView.findViewById(R.id.textView);
						textview.setText(R.string.products_not_found);
						return rootView;
					}
					break;
			}


			View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
			rootView.setTag(TAG);

			mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

			// LinearLayoutManager is used here, this will layout the elements in a similar fashion
			// to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
			// elements are laid out.
			int spanCount = 1;
			int orientation = getResources().getConfiguration().orientation;
			if (isXLargeTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 3;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 2;
				}
			} else if (isLargeTablet(getActivity()) || isNormalTablet(getActivity())) {
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					spanCount = 1;
				} else {
					/* can be ORIENTATION_PORTRAIT or ORIENTATION_UNDEFINED */
					spanCount = 1;
				}
			} else {
				spanCount = 1;
			}
			mLayoutManager = new GridLayoutManager(getActivity(), spanCount);

            mRecyclerView.setLayoutManager(mLayoutManager);

            switch (tabNumber) {
                case 1:
                    mAdapter = new MyAdapter(array, noCategories, getContext());
                    break;
                case 2:
                    mAdapter = new MyAdapter(newList, noCategories, getContext());
                    break;
                case 3:
                    mAdapter = new MyAdapter(offerList, noCategories, getContext());
                    break;
            }
            // Set CustomAdapter as the adapter for RecyclerView.
            mRecyclerView.setAdapter(mAdapter);


//            if (tabNumber == 1) {
//
//                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
//                mLayoutManager = new GridLayoutManager(getActivity(), spanCount);
//                mRecyclerView.setLayoutManager(mLayoutManager);
//
////            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
////            Log.d("PRODUCT", "tabNumber: " + tabNumber);
////            Log.d("PRODUCT", "array: " + toString(array));
//
//                mAdapter = new MyAdapter(array);
//                // Set CustomAdapter as the adapter for RecyclerView.
//                mRecyclerView.setAdapter(mAdapter);
//            } else if (tabNumber == 2) {
//                mRecyclerView.setLayoutManager(mLayoutManager);
//
////            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
////            Log.d("PRODUCT", "tabNumber: " + tabNumber);
////            Log.d("PRODUCT", "array: " + toString(array));
//
//                mAdapter = new MyAdapter(array);
//                // Set CustomAdapter as the adapter for RecyclerView.
//                mRecyclerView.setAdapter(mAdapter);
//            }



			//SearchResultsRequest srr = new SearchResultsRequest();
			return rootView;
		}

        private String toString(ArrayList<Product> array) {
            StringBuffer s = new StringBuffer();

            for (int i = 0 ; i < array.size() ; i++) {
                s.append(array.get(i).getName()).append(" || ");
            }

            return s.toString();
        }

        // This method creates an ArrayList that has three Product objects
		// Checkout the project associated with this tutorial on Github if
		// you want to use the same images.
		/*private void initDataset(){
			mDataset = new ArrayList<>();

			int[] images = {
					R.drawable.sample_0,
					R.drawable.sample_1,
					R.drawable.sample_2,
					R.drawable.sample_3,
					R.drawable.sample_4,
					R.drawable.sample_5,
					R.drawable.sample_6,
					R.drawable.sample_7
			};

			int counter = 1;
			for (int i = 0 ; i < 4 ; i++) {
				for (int j = 0 ; j < 7 ; j++, counter++) {
					mDataset.add(new Product("Product " + counter,
							i+j + " years old", "$199.99", images[j], null));
				}
			}
		}*/
	}
}
