package ar.edu.itba.hci.q2.g4.androidapp;

/**
 * Created by jpascale on 11/15/15.
 */

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import ar.edu.itba.hci.q2.g4.androidapp.R;

public class ServiceHandler {

    static private final String BASE_URL = "http://eiffel.itba.edu.ar/hci/service3/";
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    /*public String makeServiceCall(String service, String methods, Map<String, String> params, int
            handlerMethod) {

        return this.makeServiceCall(buildUrl(service, methods, params), handlerMethod, null);
    }*/

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    //TODO: Deprecated.
    public String makeServiceCall(String url, int handlerMethod,
                                  List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (handlerMethod == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (handlerMethod == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(URLEncoder.encode(url));

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
    public String makeServiceCall(String service, String methods, Map<String, String> params, int
                handlerMethod) {
        String u = buildUrl(service, methods, params);
        Log.d("__HDEBUG", u);

        try {

            URL url = new URL(u);

            /* +++xdebug */
            Log.d("URL", url.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //urlConnection.setConnectTimeout(10000);
            //urlConnection.setReadTimeout(10000);

            String response = null;

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
            } finally {
                urlConnection.disconnect();
            }

            return response;

        } catch (IOException e) {
            return null;
        }
    }
    static private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    /**
     * Getting an internet image
     * @param url - image url
     * @return Drawable object
     */

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            URL urll = new URL(url);
            URLConnection urlconn = urll.openConnection();
            urlconn.setConnectTimeout(1000);
            urlconn.setReadTimeout(1000);

            InputStream is = (InputStream) urlconn.getContent();
            if (is != null){
                Drawable d = Drawable.createFromStream(is, "src name");
                return d;
            }else{
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static private String buildUrl(String service, String method, Map<String, String> parameters) {
        String url = BASE_URL + service + ".groovy?method=" + method;

        if (parameters != null) {
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                url += "&" + e.getKey() + "=" + URLEncoder.encode(e.getValue());
            }
        }


        return url;

    }
}

