package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends NavigationDrawerActivity {

	private static final String PRODUCT = "product";
	private static final String UI = "ui";
	private static final String TAG_TITLE = "title";
	private static final String TAG_AGE = "age";
	private static final String TAG_GENRE = "genre";
	private static final String TAG_FILTERS = "filters";
	private static final String TAG_ID = "id";
	private static final String TAG_CATEGORY_ID = "category_id";
	private static final String TAG_CATEGORY_NAME = "category_name";
	private static final String TAG_SUBCATEGORY_ID = "subcategory_id";
	private static final String TAG_SUBCATEGORY_NAME = "subcategory_name";
	private static final String TAG_COMES_FROM_CATEGORY_SEARCH = "category_search";

	private View mViewPager;

	private RecyclerView photosRecyclerView;
	private TextView textView;
	private RecyclerView recommendedRecyclerView;
	private boolean async = false;

	private AsyncTaskWithContext<Void, Void, Void> thread = null;
	private Product product;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(getIntent().getStringExtra(TAG_TITLE));
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

		if (savedInstanceState != null) {
			async = savedInstanceState.getBoolean(UI);
			product = (Product) savedInstanceState.getSerializable(PRODUCT);
			Log.d("PRODUCT", "onCreate: async " + async);
		}

		if (!async) {
			thread = new AsyncTaskWithContext<Void, Void, Void>() {
				//thread.interrupt(); +++xtodo
				//--> ver el codigo que paso harry por facebook

				protected void onPreExecute(){
					mViewPager = findViewById(R.id.container_prod_detail);
					mViewPager.inflate(getApplicationContext(),R.layout.progress_icon, (ViewGroup) findViewById(R.id.main_content));
					findViewById(R.id.my_progress).setVisibility(View.VISIBLE);
				}

				@Override
				protected Void doInBackground(Void... params) {
					try {

						int productId = getIntent().getIntExtra(TAG_ID, -1);

						product = Product.getProductById(productId, getResources());

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

					initUI(product);
					thread = null;
				}
			};
			thread.execute();
		} else {
			initUI(product);
		}


//		/* doInBackground */
//		Product product = initProduct();

//		/* on PostExecute */
//		initUI(product);

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
		savedInstanceState.putBoolean(UI, async);
		Log.d("PRODUCT", "onSaveInstanceState: ANTES");
		savedInstanceState.putSerializable(PRODUCT, product);

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
		async = savedInstanceState.getBoolean(UI);
		product = (Product) savedInstanceState.getSerializable(PRODUCT);

		Log.d("PRODUCT", "onRestoreInstanceState: SALE");

		Log.d("SEARCH", savedInstanceState.toString());
		// Restore state members from saved instance
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* Overriden so as to avoid loading the search icon */

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			// this is item ID of that back part which you want
			/* so as not to reference a null later on */
			if (product == null) {
//				// Control will come here when back/or app icon is pressed
//				Intent parentActivityIntent = new Intent(this, Main.class);
//				parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(parentActivityIntent);
//				finish();
				return true;
			}

			boolean comesFromCategorySearch = getIntent().getBooleanExtra(TAG_COMES_FROM_CATEGORY_SEARCH, false);
			if (comesFromCategorySearch) {
				onBackPressed();
				return true;
			}

			Intent parentActivityIntent = new Intent(this, SubSubcategoryActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			parentActivityIntent.putExtra(TAG_TITLE, product.getSubcategory().getName());
			parentActivityIntent.putExtra(TAG_ID, product.getSubcategory().getId());

			String filters = null;
			String age = product.getAge();
			String genre = product.getGenre();
			if (genre == null) {
				Log.d("BEBES", "genre is null");
				filters = "{\"id\":2,\"value\":\"" + age + "\"}";
			} else {
				Log.d("BEBES", "genre is NOT null");
				filters = "{\"id\":1,\"value\":\"" + genre + "\"},{\"id\":2,\"value\":\"" + age + "\"}";
			}
			parentActivityIntent.putExtra(TAG_FILTERS, filters);

			parentActivityIntent.putExtra(TAG_CATEGORY_ID, product.getCategory().getId());
			parentActivityIntent.putExtra(TAG_CATEGORY_NAME, product.getCategory().getName());
			parentActivityIntent.putExtra(TAG_AGE, age);
			parentActivityIntent.putExtra(TAG_GENRE, genre);

			startActivity(parentActivityIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initUI(Product product) {

		/* Load product images */ /* +++xtodo: juan me tiene que cargar y crear el metodo para cargar todos los drawables */
		photosRecyclerView = (RecyclerView) findViewById(R.id.product_images_recycler_view);
		photosRecyclerView.setHasFixedSize(true);
		LinearLayoutManager photosLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		photosRecyclerView.setLayoutManager(photosLayoutManager);
		PhotosProductDetailAdapter productDetailAdapter = new PhotosProductDetailAdapter(product.getPhotoList(), this);
		photosRecyclerView.setAdapter(productDetailAdapter);

		/* Load product data */
		String string;

		string = product.getBrand();
		textView = (TextView) findViewById(R.id.product_brand);
		textView.setText(string);

		string = product.getDescription();
		textView = (TextView) findViewById(R.id.product_description);
		textView.setText(string);

		string = product.getPrice();
		textView = (TextView) findViewById(R.id.product_price);
		textView.setText(string);

		string = product.getSizes();
		textView = (TextView) findViewById(R.id.product_sizes);
		textView.setText(string);

		string = product.getColors();
		textView = (TextView) findViewById(R.id.product_colors);
		textView.setText(string);


		/* Load recommended */
		recommendedRecyclerView = (RecyclerView) findViewById(R.id.product_recommended_recycler_view);
		recommendedRecyclerView.setHasFixedSize(true);
		LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		recommendedRecyclerView.setLayoutManager(recommendedLayoutManager);
		RecommendedProductDetailAdapter recommendedProductDetailAdapter = new RecommendedProductDetailAdapter(product.getRecommended(), this);
		recommendedRecyclerView.setAdapter(recommendedProductDetailAdapter);

	}

	/*************************************************************************/
	/* +++xdebug implementations */
//	private Product initProduct() {
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
//		Product product = new Product(0,"Recommended", "Brand", "$150", R.drawable.sample_7, ContextCompat.getDrawable(getApplicationContext(), R.drawable.sample_7));
//
//		List<Product> recommended =  new ArrayList<>();
//
//		for (int i = 0 ; i < 15 ; i++) {
//			recommended.add(product);
//		}
//
//		return new Product(
//			"Try me Preppy", "Zara", "$359.99", R.drawable.sample_0, ContextCompat.getDrawable(getApplicationContext(), R.drawable.sample_0), images,
//				"Composición: Componentes no disponibles.", "32 - 33 - 34",
//				"Sin Especificar", recommended);
//	}
//
//	private void initUI(Product product) {
//
//		/* Load product images */
//		photosRecyclerView = (RecyclerView) findViewById(R.id.product_images_recycler_view);
//		photosRecyclerView.setHasFixedSize(true);
//		LinearLayoutManager photosLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//		photosRecyclerView.setLayoutManager(photosLayoutManager);
//		PhotosProductDetailAdapter productDetailAdapter = new PhotosProductDetailAdapter(product.getPhotosIds(), this);
//		photosRecyclerView.setAdapter(productDetailAdapter);
//
//		/* Load product data */
//		String string;
//
//		string = product.getBrand();
//		textView = (TextView) findViewById(R.id.product_brand);
//		textView.setText(string);
//
//		string = product.getDescription();
//		textView = (TextView) findViewById(R.id.product_description);
//		textView.setText(string);
//
//		string = product.getPrice();
//		textView = (TextView) findViewById(R.id.product_price);
//		textView.setText(string);
//
//		string = product.getSizes();
//		textView = (TextView) findViewById(R.id.product_sizes);
//		textView.setText(string);
//
//		string = product.getColors();
//		textView = (TextView) findViewById(R.id.product_colors);
//		textView.setText(string);
//
//
//		/* Load recommended */
//		recommendedRecyclerView = (RecyclerView) findViewById(R.id.product_recommended_recycler_view);
//		recommendedRecyclerView.setHasFixedSize(true);
//		LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//		recommendedRecyclerView.setLayoutManager(recommendedLayoutManager);
//		RecommendedProductDetailAdapter recommendedProductDetailAdapter = new RecommendedProductDetailAdapter(product.getRecommended(), this);
//		recommendedRecyclerView.setAdapter(recommendedProductDetailAdapter);
//
//	}
}
