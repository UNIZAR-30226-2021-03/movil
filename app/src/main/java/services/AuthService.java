package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.EditInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


///////////////////SERVICIO PARA LAS TAREAS DE AUTENTICACIÓN///////////////////////////////
public class AuthService {

    ///////Funciones para SIGNUP///////////////

    //Interfaz Callback para recibir el código de error del backend
    public interface SignUpCallBack {
        void onFinish(Integer statusCode);
    }

    public static void SignUp(String email, String nickname, String password, Context context, final SignUpCallBack callBack) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("email", email);
            body.put("nickname", nickname);
            body.put("password", password);
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject");
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaSignUp, body,
                response -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(1);
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(error.networkResponse.statusCode);
                    }
                });
        requestQueue.add(request);
        //return errorCode;
    }

    ///////Funciones para LOGIN///////////////
    public interface LogInCallBack {
        void onFinish(String statusCode);
    }
    public static void LogIn(String email, String password, Context context, final LogInCallBack callBack){
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

    ///////Funciones para 2FA///////////////

    public interface _2FACallBack {
        void onFinish(String accessToken,String nickname);
    }
    public static void _2fa(String token, String code, Context context, final _2FACallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("_2faToken",token);
            body.put("code",code);
            Log.d("BODY", body.toString());
        } catch (JSONException e) {
            Log.d("JSONObject error", "Cannot create JSONObject");
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaToken, body,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    try {
                        callBack.onFinish(response.getString("accessToken"),response.getString("nickname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish((String.valueOf(error.networkResponse.statusCode)),"");
                    }
                });

        requestQueue.add(request);
    }

    public interface CheckSessionCallBack {
        void onFinish(Integer code);
    }
    public static void CheckSession(String accessToken,Context context, final CheckSessionCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Routes.rutaCheckSession, null,
                response -> {
                    callBack.onFinish(200);
                },
                error -> {
                    if (error.networkResponse != null) {
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


