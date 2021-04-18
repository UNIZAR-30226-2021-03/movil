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
    static int errorCode;
    public static int logIn(String email, String password, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("email",email);
            body.put("password",password);
        } catch (JSONException e) {

        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Routes.rutaLogIn,
                body,
                listener,
                errorListener);

        requestQueue.add(request);

        return errorCode;
    }

    static Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("Response","Success Response: " + response.toString());

            errorCode = 1;
        }
    };

    static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null) {
                Log.d("Error","Error Response code: " + error.networkResponse.statusCode);

                errorCode = error.networkResponse.statusCode;
            }
        }
    };

}
