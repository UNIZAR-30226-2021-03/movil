package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListCategoriesServices {
    public interface VolleyCallBack {
        void onFinish(String list);
    }
    public static void listCategories(String accessToken, Context context, final VolleyCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        /*
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Routes.rutaListCategories,null,
                (JSONObject response) -> {
                    System.out.println("Respuesta List");
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response.toString());
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(String.valueOf(error.networkResponse.statusCode));
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accessToken", accessToken);
                        return params;
                    }
                };
        requestQueue.add(request);*/
        /*
        StringRequest request = new StringRequest(Request.Method.GET, Routes.rutaListCategories,
                (String response) -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response);
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(String.valueOf( error.networkResponse.statusCode));
                    }

                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accessToken", accessToken);
                return params;
            }
        };*/

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Routes.rutaListCategories,null,
                (JSONArray response) -> {
                    System.out.println("Respuesta List");
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response.toString());
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(String.valueOf(error.networkResponse.statusCode));
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accessToken", accessToken);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
