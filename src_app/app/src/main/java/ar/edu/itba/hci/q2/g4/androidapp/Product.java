package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.hci.q2.g4.androidapp.ServiceHandler.LoadImageFromWebOperations;

public class Product implements Serializable {
	private static final String PRODUCTS_RAW_JSON = "products";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_PRICE = "price";
	private static final String TAG_IMAGE_ARRAY = "imageUrl";
	private static final String TAG_ATTR_ARRAY = "attributes";
	private static final String TAG_ATTR_VALUES_ARRAY = "values";
	private static final String TAG_TOTAL = "total";
    private static final String TAG_ERROR = "error";


    private static final int ATTR_GENRE_ID = 1;
    private static final int ATTR_AGE_ID = 2;
    private static final int ATTR_COLOR_ID = 4;
    private static final int ATTR_BRAND_ID = 9;
    private static final int ATTR_NEW_ID = 6;
    private static final int ATTR_OFFER_ID = 5;

    private static final String PARAMS_PAGE_SIZE = "page_size";
    private static final String PARAMS_FILTERS = "filters";

    private static String IMG_LINK_DEFAULT = "http://static1.squarespace.com/static/5399bfafe4b0f6cc34fb1314/t/54223226e4b053b279b5fb0d/1411527207101/imagen-no-disponible.jpg";


	private static final String TAG = "RecyclerViewFragment";

	//	private final int id; +++xtodo at new branches
    private int id;
    private final String name;
    private final String brand;
    private final String price;
    private final int photoId; //TODO: remove
    transient private final Drawable photo;
    private int [] photosIds;
    private String description;
    private String sizes;
    private String colors;
    private List<Product> recommended;
    private Category category;
    private Subcategory subcategory;
    transient private ArrayList<Drawable> photoList;

    private String genre;
    private String age;

    boolean new_prod = false;
    boolean offer_prod = false;

	Product(int id, String name, String brand, String price, int photoId, Drawable photo) {
		this.id = id;
        this.name = name;
		this.brand = brand;
		this.price = price;
		this.photoId = photoId;
		this.photo = photo;
	}

	public Product(String name, String brand, String price, int photoId,
	               Drawable photo, int[] photosIds, String description,
	               String sizes, String colors, List<Product> recommended) {
		this.name = name;
		this.brand = brand;
		this.price = price;
		this.photoId = photoId;
		this.photo = photo;
		this.photosIds = photosIds;
		this.description = description;
		this.sizes = sizes;
		this.colors = colors;
		this.recommended = recommended;
	}

    ArrayList<Drawable> getPhotoList(){
        return photoList;
    }

    public String getAge(){
        return age;
    }

    public String getGenre(){
        return genre;
    }


    public Category getCategory(){
        return category;
    }

    public Subcategory getSubcategory(){
        return subcategory;
    }

    public boolean isNew(){
        return new_prod;
    }

    public int getId() {
        return id;
    }

    public boolean isOffer(){
        return offer_prod;
    }

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public String getPrice() {
		return price;
	}

	public int getPhotoId() {
		return photoId;
	}

	public Drawable getPhoto(){
		return this.photo;
	}

	public int[] getPhotosIds() {
		return photosIds;
	}

	public String getDescription() {
		return description;
	}

	public String getSizes() {
		return sizes;
	}

	public String getColors() {
		return colors;
	}

	public List<Product> getRecommended() {
		return recommended;
	}

    public void setPhotoList(ArrayList<Drawable> photoList){
        this.photoList = photoList;
    }

    public static ArrayList<Product> productSearch(String key, String filters, final Resources
            res, String pageSize) throws Exception{
//Product(int id, String name, String brand, String price, int photoId, Drawable photo)
        ServiceHandler sh = new ServiceHandler();
        ArrayList<Product> array = new ArrayList<>();
        Map<String, String> p = new HashMap<>();

        p.put(PARAMS_PAGE_SIZE, pageSize);
        p.put("name", key);

        if (filters != null)
            p.put(PARAMS_FILTERS, filters);

        String jsonStr = sh.makeServiceCall("Catalog", "GetProductsByName", p, ServiceHandler.GET);

        try {

            JSONObject rawObject = new JSONObject(jsonStr);

            if (rawObject.has(TAG_ERROR))
                throw new Exception("Se ha producido un error en la consulta.");

            JSONArray jsonarr = rawObject.getJSONArray("products");

            for (int i = 0; i < jsonarr.length(); i++){
                JSONObject obj = jsonarr.getJSONObject(i);

                int id;
                String name, brand, price;
                brand = "Sin especificar";
                Drawable photo;

                id = obj.getInt(TAG_ID);
                name = obj.getString(TAG_NAME);
                price = "$" + obj.getString(TAG_PRICE);


                //Load brand, check if there's a new/offer attribute

                JSONArray attributesArray = obj.getJSONArray(TAG_ATTR_ARRAY);
                Log.d("SEARCH", "productSearch: ATTR: ARRAY  - " + attributesArray.toString());

                if (attributesArray.length() > 0)
                    brand = attributesArray.getJSONObject(0).getJSONArray(TAG_ATTR_VALUES_ARRAY)
                            .getString(0);


                //Load photo
                JSONArray imageArray = obj.getJSONArray(TAG_IMAGE_ARRAY);
	            photo = null;
	            if (imageArray != null) {
		            for (int j = 0 ; j < imageArray.length() && photo == null ; j++) {
			            photo = LoadImageFromWebOperations(imageArray.get(j).toString());
		            }
	            }
	            if (photo == null) {
		            photo = res.getDrawable(R.drawable.ic_no_image);
	            }


                Product product = new Product(id, name, brand, price, 0, photo);
//                product.setType();

                array.add(product);
            }

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void productSearchByType(ArrayList<Product> arr, ArrayList<Product> newList,
                                           ArrayList<Product> offerList, String key, String
                                                   filters, final Resources res){

        ArrayList<Product> orig = null;
	    ArrayList<Product> news = null;
	    ArrayList<Product> offers = null;

	    String origFilters;
	    String newsFilters;
	    String offersFilters;

	    String pageSize = String.valueOf(getProductsAmount());

	    if (filters == null) {
		    origFilters = "[]";
		    newsFilters = "[" + NEWS_FILTER + "]";
		    offersFilters = "[" + OFFERS_FILTER + "]";
	    } else {
		    origFilters = "[" + filters + "]";
		    newsFilters = "[" + filters + "," + NEWS_FILTER + "]";
		    offersFilters = "[" + filters + "," + OFFERS_FILTER + "]";
	    }

        try{
            orig = productSearch(key, origFilters, res, pageSize);
	        news = productSearch(key, newsFilters, res, pageSize);
	        offers = productSearch(key, offersFilters, res, pageSize);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (orig == null || news == null || offers == null) {
	        return;
        }

	    arr.addAll(orig);
	    newList.addAll(news);
	    offerList.addAll(offers);

	    orig = null;
		news = null;
	    offers = null;
    }


	public static int getProductsAmount(){
		ServiceHandler sh = new ServiceHandler();
		String jsonStr = sh.makeServiceCall("Catalog", "GetAllProducts", null, ServiceHandler
                .GET);

		try {
			JSONObject json = new JSONObject(jsonStr);
			return json.getInt(TAG_TOTAL);

		} catch(Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

    public static ArrayList<Product> getAllProducts(String filters, String pageSize, Resources res){
        ServiceHandler sh = new ServiceHandler();
        Map<String, String> p = new HashMap<>();
        ArrayList<Product> arr = new ArrayList<>();

        p.put(PARAMS_PAGE_SIZE, pageSize);

        if (filters != null) {
            p.put("filters", filters);
        }


        String jsonStr = sh.makeServiceCall("Catalog", "GetAllProducts", p, ServiceHandler.GET);
        Log.d("GET", "getAllProducts: " + jsonStr);

        try {
            JSONObject o = new JSONObject(jsonStr);

            if (o.has("error"))
                return null;

            JSONArray jsonarr = o.getJSONArray("products");

            for (int i = 0 ; i < jsonarr.length() ; i++){
                JSONObject aux = (JSONObject) jsonarr.get(i);

                int id = aux.getInt(TAG_ID);
                String name = aux.getString(TAG_NAME);
                String brand;
                String price = "$" + aux.getString(TAG_PRICE);
                Drawable photo;

                Log.d("GET", "getAllProducts: NAME:" + name);
                //Load Brand
                JSONArray attributesArray = aux.getJSONArray(TAG_ATTR_ARRAY);

                if (attributesArray.length() > 0) {
                    JSONObject aux2 = (JSONObject) attributesArray.get(0);
                    if (aux.length() > 0) {
                        brand = (String) aux2.getJSONArray("values").get(0);
                    } else {
                        brand = "";
                    }

                } else {
                    brand = "";
                }

                //Load photo
                JSONArray imageArray = aux.getJSONArray(TAG_IMAGE_ARRAY);
	            photo = null;
	            if (imageArray != null) {
		            for (int j = 0 ; j < imageArray.length() && photo == null ; j++) {
			            photo = LoadImageFromWebOperations(imageArray.get(j).toString());
		            }
	            }
	            if (photo == null) {
		            photo = res.getDrawable(R.drawable.ic_no_image);
	            }

                Product prod = new Product(id, name, brand, price, 0, photo);
//                prod.setType();

                arr.add(prod);
            }

            return arr;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }

    public static void getAllProductsByType(ArrayList<Product> arr, ArrayList<Product> newList,
                                            ArrayList<Product> offerList, String filters,
                                            Resources res){
	    ArrayList<Product> orig = null;
	    ArrayList<Product> news = null;
	    ArrayList<Product> offers = null;

	    String origFilters;
	    String newsFilters;
	    String offersFilters;

	    String pageSize = String.valueOf(getProductsAmount());

	    if (filters == null) {
		    origFilters = "[]";
		    newsFilters = "[" + NEWS_FILTER + "]";
		    offersFilters = "[" + OFFERS_FILTER + "]";
	    } else {
		    origFilters = "[" + filters + "]";
		    newsFilters = "[" + filters + "," + NEWS_FILTER + "]";
		    offersFilters = "[" + filters + "," + OFFERS_FILTER + "]";
	    }

	    try{
		    orig = getAllProducts(origFilters,  String.valueOf(50), res);
		    news = getAllProducts(newsFilters, pageSize, res);
		    offers = getAllProducts(offersFilters, pageSize, res);
	    } catch (Exception e){
		    e.printStackTrace();
	    }

	    if (orig == null || news == null || offers == null) {
		    return;
	    }

	    arr.addAll(orig);
	    newList.addAll(news);
	    offerList.addAll(offers);

	    orig = null;
	    news = null;
	    offers = null;

    }

    /*********************************************************************************/

    public static ArrayList<Product> getProductsByCategoryId(int catid, String filters, String pageSize,
                                                             Resources res){
        ServiceHandler sh = new ServiceHandler();
        ArrayList<Product> arr = new ArrayList<>();
        Map<String, String> p = new HashMap<>();

        p.put("page_size", pageSize);
        p.put("id", String.valueOf(catid));

        if (filters != null){
            p.put("filters", filters);
        }

        String jsonStr = sh.makeServiceCall("Catalog", "GetProductsByCategoryId", p, ServiceHandler
                .GET);

        try {
            JSONObject o = new JSONObject(jsonStr);

            if (o.has("error"))
                return null;

            JSONArray jsonarr = o.getJSONArray("products");

            for (int i = 0 ; i < jsonarr.length() ; i++){
                JSONObject aux = (JSONObject) jsonarr.get(i);

                int id = aux.getInt(TAG_ID);
                String name = aux.getString(TAG_NAME);
                String brand;
                String price = "$" + aux.getString(TAG_PRICE);
                Drawable photo;

                Log.d("GET", "getAllProducts: NAME:" + name);
                //Load Brand
                JSONArray attributesArray = aux.getJSONArray(TAG_ATTR_ARRAY);

                if (attributesArray.length() > 0) {
                    JSONObject aux2 = (JSONObject) attributesArray.get(0);
                    if (aux.length() > 0) {
                        brand = (String) aux2.getJSONArray("values").get(0);
                    } else {
                        brand = "";
                    }

                } else {
                    brand = "";
                }

                //Load photo
                JSONArray imageArray = aux.getJSONArray(TAG_IMAGE_ARRAY);
	            photo = null;
	            if (imageArray != null) {
		            for (int j = 0 ; j < imageArray.length() && photo == null ; j++) {
			            photo = LoadImageFromWebOperations(imageArray.get(j).toString());
		            }
	            }
	            if (photo == null) {
		            photo = res.getDrawable(R.drawable.ic_no_image);
	            }

                Product prod = new Product(id, name, brand, price, 0, photo);
//                prod.setType();

                arr.add(prod);
            }

            return arr;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getProductsByCategoryIdByType(ArrayList<Product> arr,
                                                     ArrayList<Product> newList,
                                                     ArrayList<Product> offerList,
                                                     int catid, String filters,
                                                     Resources res){

	    ArrayList<Product> orig = null;
	    ArrayList<Product> news = null;
	    ArrayList<Product> offers = null;

	    String origFilters;
	    String newsFilters;
	    String offersFilters;

	    String pageSize = String.valueOf(getProductsAmount());

	    if (filters == null) {
		    origFilters = "[]";
		    newsFilters = "[" + NEWS_FILTER + "]";
		    offersFilters = "[" + OFFERS_FILTER + "]";
	    } else {
		    origFilters = "[" + filters + "]";
		    newsFilters = "[" + filters + "," + NEWS_FILTER + "]";
		    offersFilters = "[" + filters + "," + OFFERS_FILTER + "]";
	    }

	    try{
		    orig = getProductsByCategoryId(catid, origFilters, pageSize, res);
		    news = getProductsByCategoryId(catid, newsFilters, pageSize, res);
		    offers = getProductsByCategoryId(catid, offersFilters, pageSize, res);
	    } catch (Exception e){
		    e.printStackTrace();
	    }

	    if (orig == null || news == null || offers == null) {
		    return;
	    }

	    arr.addAll(orig);
	    newList.addAll(news);
	    offerList.addAll(offers);

	    orig = null;
	    news = null;
	    offers = null;
    }


    public static ArrayList<Product> getProductsBySubcategoryId(int subcatid,
                                                                String filters,
                                                                String pageSize,
                                                                Resources res){
        ServiceHandler sh = new ServiceHandler();
        ArrayList<Product> arr = new ArrayList<>();
        Map<String, String> p = new HashMap<>();

        p.put("page_size", pageSize);
        p.put("id", String.valueOf(subcatid));

        if (filters != null){
            p.put("filters", filters);
        }

        String jsonStr = sh.makeServiceCall("Catalog", "GetProductsBySubcategoryId", p,
                ServiceHandler
                .GET);

        try {
            JSONObject o = new JSONObject(jsonStr);

            if (o.has("error"))
                return null;

            JSONArray jsonarr = o.getJSONArray("products");

            for (int i = 0 ; i < jsonarr.length() ; i++){
                JSONObject aux = (JSONObject) jsonarr.get(i);

                int id = aux.getInt(TAG_ID);
                String name = aux.getString(TAG_NAME);
                String brand;
                String price = "$" + aux.getString(TAG_PRICE);
                Drawable photo;

                Log.d("GET", "getAllProducts: NAME:" + name);
                //Load Brand
                JSONArray attributesArray = aux.getJSONArray(TAG_ATTR_ARRAY);

                if (attributesArray.length() > 0) {
                    JSONObject aux2 = (JSONObject) attributesArray.get(0);
                    if (aux.length() > 0) {
                        brand = (String) aux2.getJSONArray("values").get(0);
                    } else {
                        brand = "";
                    }

                } else {
                    brand = "";
                }

                //Load photo
                JSONArray imageArray = aux.getJSONArray(TAG_IMAGE_ARRAY);
	            photo = null;
	            if (imageArray != null) {
		            for (int j = 0 ; j < imageArray.length() && photo == null ; j++) {
			            photo = LoadImageFromWebOperations(imageArray.get(j).toString());
		            }
	            }
	            if (photo == null) {
		            photo = res.getDrawable(R.drawable.ic_no_image);
	            }

                Product prod = new Product(id, name, brand, price, 0, photo);
//                prod.setType();

                arr.add(prod);
            }

            return arr;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getProductsBySubcategoryIdByType(ArrayList<Product> arr,
                                                        ArrayList<Product> newList,
                                                        ArrayList<Product> offerList,
                                                        int catid, String filters,
                                                        Resources res){

	    ArrayList<Product> orig = null;
	    ArrayList<Product> news = null;
	    ArrayList<Product> offers = null;

	    String origFilters;
	    String newsFilters;
	    String offersFilters;

	    String pageSize = String.valueOf(getProductsAmount());

	    if (filters == null) {
		    origFilters = "[]";
		    newsFilters = "[" + NEWS_FILTER + "]";
		    offersFilters = "[" + OFFERS_FILTER + "]";
	    } else {
		    origFilters = "[" + filters + "]";
		    newsFilters = "[" + filters + "," + NEWS_FILTER + "]";
		    offersFilters = "[" + filters + "," + OFFERS_FILTER + "]";
	    }

	    try{
		    orig = getProductsBySubcategoryId(catid, origFilters, pageSize, res);
		    news = getProductsBySubcategoryId(catid, newsFilters, pageSize, res);
		    offers = getProductsBySubcategoryId(catid, offersFilters, pageSize, res);
	    } catch (Exception e){
		    e.printStackTrace();
	    }

	    if (orig == null || news == null || offers == null) {
		    return;
	    }

	    arr.addAll(orig);
	    newList.addAll(news);
	    offerList.addAll(offers);

	    orig = null;
	    news = null;
	    offers = null;
    }

    public static Product getProductById (int id, Resources res){
        ServiceHandler sh = new ServiceHandler();
        Map<String, String> p = new HashMap<>();

        p.put("id", String.valueOf(id));
        String jsonStr = sh.makeServiceCall("Catalog", "GetProductById", p, ServiceHandler.GET);

        //Product creating params
        String name, brand, price, description, sizes, colors;
        Drawable photo, firstPhoto;
        int[] photoId = new int[1];
        boolean isnew = false;
        boolean isoffer = false;

        String genre = null;
        String age = null;

        try{

            JSONObject raw = new JSONObject(jsonStr);

            if (raw.has("error"))
                return null;

            JSONObject obj = raw.getJSONObject("product");
            JSONArray attributesArray = obj.getJSONArray(TAG_ATTR_ARRAY);


            name = obj.getString("name");
            price = "$" + obj.getInt("price");


            //Load photo
            JSONArray imageArray = obj.getJSONArray(TAG_IMAGE_ARRAY);
//            Log.d("PRODUCT", "getAllProducts: " + imageArray.get(0));

            photo = null;
            ArrayList<Drawable> photoL = new ArrayList<>();
	        firstPhoto = null;
	        if (imageArray != null) {
		        for (int i = 0 ; i < imageArray.length() ; i++) {
			        photo = LoadImageFromWebOperations(imageArray.get(i).toString());
			        if (photo != null) {
				        if (firstPhoto == null) {
					        firstPhoto = photo;
				        }
				        photoL.add(photo);
			        }
		        }
	        }
	        if (firstPhoto == null) {
		        firstPhoto = res.getDrawable(R.drawable.ic_no_image);
		        photoL.add(firstPhoto);
	        }


            //Iterate over attributes
            brand = colors = sizes = description = "Sin especificar";
            for (int i = 0; i < attributesArray.length(); i++){

                JSONObject attr = attributesArray.getJSONObject(i);
                int attr_id = attr.getInt(TAG_ID);
                JSONArray values;

                switch (attr_id){

                    case ATTR_GENRE_ID:
                        genre = attr.getJSONArray(TAG_ATTR_VALUES_ARRAY).getString(0);
                        break;

                    case ATTR_AGE_ID:
                        age = attr.getJSONArray(TAG_ATTR_VALUES_ARRAY).getString(0);
                        break;

                    case ATTR_BRAND_ID:
                        values = attr.getJSONArray(TAG_ATTR_VALUES_ARRAY);
                        brand = values.getString(0);
                        break;

                    case ATTR_COLOR_ID:
                        values = attr.getJSONArray(TAG_ATTR_VALUES_ARRAY);
                        colors = values.getString(0);
                        if (values.length() > 1)
                            for (int j = 1; j < values.length(); j++)
                                colors = colors + " - " + values.getString(j);
                        break;

                    case ATTR_NEW_ID:
                        isnew = true;
                        break;

                    case ATTR_OFFER_ID:
                        isoffer = true;
                        break;
                }

                if (attr.getString("name").startsWith("Material-")){
                    description = "Composición: " + attr.getJSONArray("values").getString(0);
                }

                if (attr.getString("name").startsWith("Talle")){
                    values = attr.getJSONArray("values");
                    sizes = values.getString(0);

                    for (int j = 1; j < values.length(); j++){
                        sizes = sizes + " - " + values.getString(j);
                    }
                }

            }

            int recomended_id = obj.getJSONObject("subcategory").getInt(TAG_ID);

            Product product = new Product(name, brand, price, 0, firstPhoto, photoId,
                    description, sizes, colors,
		            getProductsBySubcategoryId(recomended_id, null,
				            String.valueOf(getProductsAmount()), res)); /* +++xchange --> improve with filters */

            if (isnew)
                product.new_prod = true;

            if (isoffer)
                product.offer_prod = true;

            product.setPhotoList(photoL);

            JSONObject cat = obj.getJSONObject("category");
            product.category = new Category(cat.getInt(TAG_ID), cat.getString(TAG_NAME));

            cat = obj.getJSONObject("subcategory");
            product.subcategory = new Subcategory(cat.getInt(TAG_ID), cat.getString(TAG_NAME));

            product.genre = genre;
            product.age = age;


            ///////////////////DEBUGGING PURPOSE//////////////////////////

            Log.d("GET", "getProductById: " + product.getName() + " - " + product.getBrand() + " " +
                    "- " + product.getPrice() + " - " + product.getDescription() + " - " +
                    product.getSizes() + " - " + product.getColors());

            if (product.getRecommended() == null)
                Log.d("GET", "getProductById: PRODUCT RECOMENDED ES NULL");
            else
                for (Product a : product.getRecommended())
                    Log.d("GET", "getProductById: RECOMENDED:" + a.getName());

            for (int i = 0; i < product.getPhotoList().size(); i++)
                Log.d("GET", "getProductById: PHOTO LIST INDEX " + i + " " + product.getPhotoList
                        ().get(i).toString());

            Log.d("GET", "getProductById: + Category y Sub: " + product.getCategory().getId() + " - " +
                    product.getCategory().getName() + " - " + product.getSubcategory().getId() +
                    " - " + product.getSubcategory().getName());

            Log.d("GET", "getProductById: Genre: " + product.getGenre() + " - Age: " + product
                    .getAge());

            ////////////////////////////////////////////////////////////


            return product;

        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }



	private static String OFFERS_FILTER = "{\"id\":5,\"value\":\"Oferta\"}";
	private static String NEWS_FILTER = "{\"id\":6,\"value\":\"Nuevo\"}";

    public static void getAllMainProductsByType(ArrayList<Product> newList,
                                                ArrayList<Product> offerList,
                                                Resources res) {

	    ArrayList<Product> news = null;
	    ArrayList<Product> offers = null;

	    String newsFilters;
	    String offersFilters;

	    String pageSize = String.valueOf(16);


	    newsFilters = "[" + NEWS_FILTER + "]";
	    offersFilters = "[" + OFFERS_FILTER + "]";

	    try{
		    news = getAllProducts(newsFilters, pageSize, res);
		    offers = getAllProducts(offersFilters, pageSize, res);
	    } catch (Exception e){
		    e.printStackTrace();
	    }

	    if (news == null || offers == null) {
		    return;
	    }

	    newList.addAll(news);
	    offerList.addAll(offers);

	    news = null;
	    offers = null;
    }



}