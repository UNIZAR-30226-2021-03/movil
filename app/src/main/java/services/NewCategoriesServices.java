package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewCategoriesServices {
    public interface VolleyCallBack {
        void onFinish(Integer statusCode);
    }
    public static void newCategory(String accessToken, String name, Context context, final VolleyCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("name",name);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject body");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaCategory, body,
                response -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(1);
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(error.networkResponse.statusCode);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("accessToken", accessToken);
                        return params;
                }};
        requestQueue.add(request);
    }
}
