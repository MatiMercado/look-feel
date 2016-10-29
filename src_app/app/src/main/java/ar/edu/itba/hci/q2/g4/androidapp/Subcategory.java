package ar.edu.itba.hci.q2.g4.androidapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jpascale on 11/18/15.
 */
public class Subcategory implements Serializable, FilterButton {

	private static String type = FilterButton.SUBCATEGORY;

    private int id;
    private String name;
    private String filters;

    public Subcategory(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Subcategory(int id, String name, String filters){
        this.id = id;
        this.name = name;
        this.filters = filters;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

	public String getFilters() {
		return filters;
	}

	public String getType() {
		return type;
	}

	public static ArrayList<FilterButton> getSubcategories(int id, String filters){
        ServiceHandler sh = new ServiceHandler();
        Map<String, String> p = new HashMap<>();
        ArrayList<FilterButton> subcategories = new ArrayList<>();

        p.put("id", String.valueOf(id));

        if (filters != null){
            p.put("filters", "[" + filters + "]");
        }

        String jsonStr = sh.makeServiceCall("Catalog", "GetAllSubcategories", p, ServiceHandler
                .GET);
        Log.d("CAT", "getSubcategories: " + jsonStr);

        try {
            JSONObject json = new JSONObject(jsonStr);
            if (json.has("error"))
                return null;

            JSONArray arr = json.getJSONArray("subcategories");

            for (int i = 0; i < arr.length(); i++){
                JSONObject obj = arr.getJSONObject(i);
                subcategories.add(new Subcategory(obj.getInt("id"), obj.getString("name"), filters));
            }

            return subcategories;

        } catch (JSONException e){
            e.printStackTrace();
            e.getCause();
            return null;
        }
    }
}
