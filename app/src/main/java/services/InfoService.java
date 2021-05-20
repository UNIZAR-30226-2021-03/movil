package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InfoService {

    public interface ListInfosCallBack {
        void onFinish(JSONArray list);
    }
    public static void ListInfos(String accessToken,String category_id, Context context, final ListInfosCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaListInfos + category_id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                (JSONArray response) -> {
                    Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response);
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                            JSONObject res = new JSONObject();
                            try {
                                res.put("statuscode", String.valueOf(error.networkResponse.statusCode));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JSONArray out = new JSONArray();
                            out.put(res);
                            callBack.onFinish(out);
                        }
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

    ////Función para Renombrar Categorias
    public interface AddInfoCallBack {
        void onFinish(Integer statusCode);
    }

    public static void AddInfo(String accessToken, JSONObject body, Context context, final AddInfoCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaAddInfo, body,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(200);
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

    ////Función para Eliminar infos
    public interface DeleteInfoCallBack {
        void onFinish(Integer statusCode);
    }

    public static void DeleteInfo(String accessToken, String category_id, String info_id, Context context, final DeleteInfoCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaDeleteInfo + info_id + "&category_id=" + category_id;
        System.out.println(url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(200);
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

    ////Función para Actualizar Infos
    public interface UpdateInfoCallBack {
        void onFinish(Integer statusCode);
    }

    public static void UpdateInfo(String accessToken,JSONObject body, Context context, final UpdateInfoCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Routes.rutaUpdateInfo, body,
                response -> {
                    callBack.onFinish(200);
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
