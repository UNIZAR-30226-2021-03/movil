package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ListCategoriesServices {
    public interface VolleyCallBack {
        void onFinish(String list);
    }
    public static void listCategories(String accessToken, Context context, final VolleyCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject header = new JSONObject();
        try {
            //input your API parameters
            header.put("accessToken",accessToken);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject header");
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Routes.rutaListCategories, header,
                response -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response.toString());
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(String.valueOf(error.networkResponse.statusCode));
                    }
                });
        requestQueue.add(request);
    }
}
