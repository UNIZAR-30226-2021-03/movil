package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


////////////////////////////////////SERVICIO PARA GESTIONAR CATEGORÍAS//////////
public class CategoryService {

    ////Función para crear Categorias
    public interface NewCategoryCallBack {
        void onFinish(Integer statusCode);
    }

    public static void NewCategory(String accessToken, String name, Context context, final NewCategoryCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("name",name);
        } catch (JSONException e) {
            //Log.d("JSONObject error", "Cannot create JSONObject body");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Routes.rutaCategory, body,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(200);
                },
                error -> {
                    if (error.networkResponse != null) {
                       // Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
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

    public interface ListCategoryCallBack {
        void onFinish(JSONArray list, Integer statusCode);
    }
    public static void ListCategories(String accessToken, Context context, final ListCategoryCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Routes.rutaListCategories,null,
                (JSONArray response) -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(response,200);
                },
                error -> {
                    if (error.networkResponse != null) {
                        callBack.onFinish(null,error.networkResponse.statusCode);
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
    public interface RenameCategoryCallBack {
        void onFinish(Integer statusCode);
    }

    public static void RenameCategory(String accessToken, String category_id,String name, Context context, final RenameCategoryCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("name",name);
            body.put("category_id",category_id);
        } catch (JSONException e) {
            //Log.d("JSONObject error", "Cannot create JSONObject body");
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Routes.rutaUpdateCategory, body,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(200);
                },
                error -> {
                    if (error.networkResponse != null) {
                        //Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
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

    ////Función para Delete Categorias
    public interface DeleteCategoryCallBack {
        void onFinish(Integer statusCode);
    }

    public static void DeleteCategory(String accessToken, String category_id, Context context, final DeleteCategoryCallBack callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaDeleteCategory + category_id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(200);
                },
                error -> {
                    if (error.networkResponse != null) {
                       // Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
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
