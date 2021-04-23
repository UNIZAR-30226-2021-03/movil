package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInServices {
    //static String token;

    public interface VolleyCallBack {
        void onFinish(String statusCode);
    }
    public static void logIn(String email, String password, Context context, final VolleyCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("email",email);
            body.put("password",password);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaLogIn, body,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish("1");
                },
                error -> {
                    if (error.networkResponse != null) {
                        //Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish((String.valueOf(error.networkResponse.statusCode)));
                    }
                });

        requestQueue.add(request);
        /*JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Routes.rutaLogIn,
                body,
                listener,
                errorListener);*/



        //return token;
    }

   /* static Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("Response","Success Response: " + response.toString());
            try {
                token = response.getString("_2faToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null) {
                Log.d("Error","Error Response code: " + error.networkResponse.statusCode);

                token = String.valueOf(error.networkResponse.statusCode);
            }
        }
    };*/

}

