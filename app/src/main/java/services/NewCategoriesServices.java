package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NewCategoriesServices {
    public interface VolleyCallBack {
        void onFinish(String statusCode);
    }
    public static void newCategory(String accessToken, String name, Context context, final NewCategoriesServices.VolleyCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        JSONObject header = new JSONObject();
        try {
            //input your API parameters
            header.put("accessToken",accessToken);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject header");
        }
        try {
            //input your API parameters
            body.put("name",name);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject body");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaCategory, body,
                response -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    try {
                        callBack.onFinish(response.getString("_2faToken"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish((String.valueOf(error.networkResponse.statusCode)));
                    }
                });

        requestQueue.add(request);
    }
}