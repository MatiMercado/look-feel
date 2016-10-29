package ar.edu.itba.hci.q2.g4.androidapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jpascale on 11/17/15.
 */
public class User {
    public static String LOCAL_USER_DATA_KEY = "USER_DATA";

    private static User instance = null;

    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;

    //info
    private int id;
    private String username;
    private String authenticationToken;
    private String email;

    private String firstName;
    private String lastName;

    private String gender;
    private String identityCard;

    private boolean notificationsSet = true;

    private boolean logged = false;
    private String jsonStr = null;
    private String orderStr = null;

    private User(){
    }

    public String getJsonStr(){
        return jsonStr;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setAuthenticationToken(String authenticationToken){
        this.authenticationToken = authenticationToken;
    }

    public String getUsername(){
        return this.username;
    }
    public String getAuthenticationToken(){
        return this.authenticationToken;
    }

    public void setOrderStr(String orderStr){
        this.orderStr = orderStr;
    }

    public String getOrderStr(){
        return orderStr;
    }

    public void setNotications(){
        this.notificationsSet = true;
    }

    public void clearNotifications(){
        this.notificationsSet = false;
    }

    public void setLogged(boolean logged){
        this.logged = logged;
    }

    public boolean getNotificationsSet(){
        //this.notificationsSet = PreferenceManager.
        //SharedPreferences.Editor editor = getSharedPreferences();
        return this.notificationsSet;
    }

  //  public boolean LogIn(String username, String password){

    public boolean LogIn(String username, String password, SharedPreferences.Editor editor){
        //"http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=SignIn&username=" + username + "&password=" + password;

        this.editor = editor;

        ServiceHandler sh = new ServiceHandler();
        Map<String, String> p = new HashMap<>();
        p.put("username", username);
        p.put("password", password);

        this.jsonStr = sh.makeServiceCall("Account", "SignIn", p, ServiceHandler.GET);


        try {
            JSONObject json = new JSONObject(jsonStr);

            if (json.has("error")){
                return false;
            }

            this.username = username;
            JSONDataParse(json);

            p = new HashMap<>();
            p.put("username", this.username);
            p.put("authentication_token", this.authenticationToken);

            this.orderStr = sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET);

            this.logged = true;

            editor.putString("LOCAL_DATA_USER", User.getInstance().getJsonStr());
            editor.putString("LOCAL_DATA_ORDERS", User.getInstance().getOrderStr());
            editor.apply();

            return true;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
    }

    public void JSONDataParse(JSONObject json){
        try {
            this.authenticationToken = json.getString("authenticationToken");
            json = json.getJSONObject("account");

            this.id = json.getInt("id");

            this.email = json.getString("email");

            this.firstName = json.getString("firstName");
            this.lastName = json.getString("lastName");

            this.gender = json.getString("gender");
            this.identityCard = json.getString("identityCard");
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void LogOut(){
        editor.remove("LOCAL_DATA_USER");
        editor.remove("LOCAL_DATA_ORDERS");
        editor.apply();
        instance = null;
    }

    public boolean checkLocalDataLogin(SharedPreferences preferences){

        String json = preferences.getString("LOCAL_DATA_USER", null);

        if (json != null){
            try {
                JSONDataParse(new JSONObject(json));
                Log.d("LOCAL", "checkLocalDataLogin: " + json);
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }

    public static String getAPILoginURL(String username, String password){
        return "http://eiffel.itba.edu.ar/hci/service3/Account.groovy?method=SignIn&username=" + username + "&password=" + password;
        //return "http://192.168.0.104:8080/?method=SignIn&username=" + username + "&password=" + password;
    }

    public String getAPIOrderURL() {
        if (!this.logged) {
            return "http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders&username=" + username + "&authentication_token=" + authenticationToken;
        }

        return null;
    }

    public int checkNewOrders(SharedPreferences preferences){
        int oldOrders = 0;
        int newOrders = 0;

        String json = preferences.getString("LOCAL_DATA_USER", null);
        String jsonOrders = preferences.getString("LOCAL_DATA_ORDERS", null);
        JSONObject userData;

        Map<String, String> p = new HashMap<>();
        //p.put("username", this.username);
        //p.put("authentication_token", this.authenticationToken);
        ServiceHandler sh = new ServiceHandler();

        if (json == null){
            Log.d("HDEBUG","User is not logged in. Exiting...");
            return 0;
        }

        try {
            userData = new JSONObject(json);
            p.put("username", userData.getJSONObject("account").getString("username"));
            p.put("authentication_token", userData.getString("authenticationToken"));
        }
        catch (JSONException e){
            e.printStackTrace();
            return 0;
        }

        try{
            JSONArray oldOrdersList = new JSONObject(jsonOrders).getJSONArray("orders");
            JSONArray newOrdersList = new JSONObject(sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET)).getJSONArray("orders");

            for (int i = 0; i < oldOrdersList.length(); i++){
                if (Integer.valueOf(oldOrdersList.getJSONObject(i).getString("status")) > 1)
                    oldOrders++;
            }

            for (int i = 0; i < newOrdersList.length(); i++){
                if (Integer.valueOf(newOrdersList.getJSONObject(i).getString("status")) > 1)
                    newOrders++;
            }

            Log.d("LOCAL", "checkNewOrders: " + (newOrders - oldOrders));

            return newOrders - oldOrders;

        } catch (JSONException e){
            e.printStackTrace();
        }

        return 0;
    }

    public JSONArray getAllOrders(){
        Map<String, String> p = new HashMap<>();
        p.put("username", this.username);
        p.put("authentication_token", this.authenticationToken);

        ServiceHandler sh = new ServiceHandler();

        if (!logged){
            Log.d("HDEBUG","getAllOrders: User is not logged in. Exiting...");
            return null;

        }

        try{
            JSONArray newOrdersList = new JSONObject(sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET)).getJSONArray("orders");

            return newOrdersList;

        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getPriceFromOrder(JSONObject order){
        Double totalPrice = 0.0;
        try {
            Log.d("__HDEBUG", order.toString());
            JSONArray items = order.getJSONArray("items");
            for (int i = 0; i < items.length(); i++){
                String itemPrice = items.getJSONObject(i).getString("price");
                if(itemPrice != null){
                    totalPrice += Double.valueOf(itemPrice);
                }
            }
            return totalPrice.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "---";

    }

    public JSONObject getOrderByID(Integer orderID){
        Map<String, String> p = new HashMap<>();
        p.put("username", this.username);
        p.put("authentication_token", this.authenticationToken);
        p.put("id", orderID.toString());

        ServiceHandler sh = new ServiceHandler();

        if (!logged){
            Log.d("HDEBUG","getOrderByID: User is not logged in. Exiting...");
            return null;
        }

        try{
            JSONObject order = new JSONObject(sh.makeServiceCall("Order", "GetOrderById", p,
                    ServiceHandler.GET));

            return order;

        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public int checkModifiedOrders(SharedPreferences preferences){
        ServiceHandler sh = new ServiceHandler();
        int ret = 0;

        String json = preferences.getString("LOCAL_DATA_USER", null);
        String jsonOrders = preferences.getString("LOCAL_DATA_ORDERS", null);
        JSONObject userData;

        Map<String, String> p = new HashMap<>();
        //p.put("username", this.username);
        //p.put("authentication_token", this.authenticationToken);

        if (json == null){
            Log.d("HDEBUG","User is not logged in. Exiting...");
            return 0;
        }

        try {
            userData = new JSONObject(json);
            p.put("username", userData.getJSONObject("account").getString("username"));
            p.put("authentication_token", userData.getString("authenticationToken"));
        }
        catch (JSONException e){
            e.printStackTrace();
            return 0;
        }

        try{
            JSONArray oldOrdersList = new JSONObject(jsonOrders).getJSONArray("orders");
            JSONArray newOrdersList = new JSONObject(sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET)).getJSONArray("orders");

            Log.d("LOCAL", "Old: checkModifiedOrders: " + oldOrdersList.toString());
            Log.d("LOCAL", "New: checkModifiedOrders: " + newOrdersList.toString());
            int diff = newOrdersList.length() - oldOrdersList.length();

            boolean found = false;
            for (int i = 0; i < oldOrdersList.length(); i++){
                found = false;
                for (int j = 0; j < newOrdersList.length() && !found; j++) {

                    if (oldOrdersList.getJSONObject(i).getInt("id") == newOrdersList
                            .getJSONObject(j).getInt("id") && !oldOrdersList.getJSONObject(i)
                            .getString("status").equals(newOrdersList.getJSONObject(j)
                                    .getString("status"))){
                        ret++;
                        found = true;
                    }
                /*int oldstat = Integer.valueOf(oldOrdersList.getJSONObject(i).getString("status"));
                int newstat = Integer.valueOf(newOrdersList.getJSONObject(i + diff).getString
                        ("status"));

                if (oldstat != newstat) {
                    Log.d("LOCAL", "checkModifiedOrders: " + oldstat + " - " + newstat);
                    ret++;
                }*/
                }
            }

            Log.d("LOCAL", "checkModifiedOrders: " + ret);

            return ret;

        } catch(JSONException e){
            e.printStackTrace();
            return 0;
        }
    }

    public void updateUserOrders(SharedPreferences preferences){

        ServiceHandler sh = new ServiceHandler();
        Map<String, String> p = new HashMap<>();
        SharedPreferences.Editor myEditor = preferences.edit();

        String localDataUser;
        String localDataOrders;


        if(this != null && this.username != null && this.authenticationToken != null) {
            p.put("username", this.username);
            p.put("authentication_token", this.authenticationToken);
            this.orderStr = sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET);

            editor.putString("LOCAL_DATA_ORDERS", User.getInstance().getOrderStr());
            editor.apply();
            return;
        }
        else{
            String json = preferences.getString("LOCAL_DATA_USER", null);
            String jsonOrders = preferences.getString("LOCAL_DATA_ORDERS", null);
            JSONObject userData;


            try {
                userData = new JSONObject(json);
                p.put("username", userData.getJSONObject("account").getString("username"));
                p.put("authentication_token", userData.getString("authenticationToken"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }


            json = sh.makeServiceCall("Order", "GetAllOrders", p,
                        ServiceHandler.GET);

            myEditor.putString("LOCAL_DATA_ORDERS", jsonOrders);
            myEditor.putString("LOCAL_DATA", json);
            //editor.apply();
            myEditor.apply();
            //Log.d("__HDEBBUG", "OPERATIOOOOOOOOOOOOOOOOOOOON" + res);


            Log.d("__HDEBBUG", preferences.getString("LOCAL_DATA_ORDERS", null));
            Log.d("__HDEBBUG", "" + preferences.getString("LOCAL_DATA_ORDERS", null).equals(sh.makeServiceCall("Order", "GetAllOrders", p,
                    ServiceHandler.GET)));
        }


    }

    public JSONObject getAddressByName(String addressName){

        Map<String, String> p = new HashMap<>();
        p.put("username", this.username);
        p.put("authentication_token", this.authenticationToken);
        p.put("name", addressName);

        ServiceHandler sh = new ServiceHandler();

        if (!logged){
            Log.d("HDEBUG","getAddressByName: User is not logged in. Exiting...");
            return null;
        }

        try{
            JSONObject order = new JSONObject(sh.makeServiceCall("Account", "GetAddressesByName", p,
                    ServiceHandler.GET));

            return order;

        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getAddressByID(String orderID){

        Map<String, String> p = new HashMap<>();
        p.put("username", this.username);
        p.put("authentication_token", this.authenticationToken);
        p.put("id", orderID);

        ServiceHandler sh = new ServiceHandler();

        if (!logged){
            Log.d("HDEBUG","getAddressByName: User is not logged in. Exiting...");
            return null;
        }

        try{
            JSONObject order = new JSONObject(sh.makeServiceCall("Account", "GetAddressById", p,
                    ServiceHandler.GET));

            return order;

        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////
    ///////////AGREGADA POR COMODIDAD

    public boolean isLogged(){
        return this.logged;
    }

    public String getFirstName(){
        return this.firstName;
    }
    public String getEmail(){
        return this.firstName;
    }
}
