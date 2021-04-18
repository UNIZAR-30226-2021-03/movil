package services;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenServices {
    static String accessToken;
    public static String checkToken(String token, String code, Context context){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("_2faToken",token);
            body.put("code",code);
        } catch (JSONException e) {

        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Routes.rutaToken,
                body,
                listener,
                errorListener);

        requestQueue.add(request);

        return accessToken;
    }

    static Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("Response","Success Response: " + response.toString());
            try {
                accessToken = response.getString("accessToken");
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

                accessToken = String.valueOf(error.networkResponse.statusCode);
            }
        }
    };

}
