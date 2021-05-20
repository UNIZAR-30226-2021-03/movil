package services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.annotations.SerializedName;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helpers.VolleyMultipartRequest;


public class FileService {

    public interface UploadFileCallback {
        void onFinish(int errorCode,String file_id);
    }
    public static void UploadFile(String accessToken, String category_id, String info_id, byte[] data,String filename, Context context, UploadFileCallback callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaUploadFile+"?category_id="+category_id+"&info_id="+info_id;

        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken",accessToken);


        VolleyMultipartRequest request = new VolleyMultipartRequest(url, headers,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SERVICIO",response);
                        System.out.println(response);
                        JSONObject res = null;
                        try {
                            res = new JSONObject(response);
                            System.out.println(res.toString());
                        } catch (JSONException e) {
                            callBack.onFinish(200, "");
                        }
                        try {
                            callBack.onFinish(200, res.getString("file_id"));
                        } catch (JSONException e) {
                            callBack.onFinish(200, "");
                        }
                    }
                },
                new  Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            callBack.onFinish(error.networkResponse.statusCode,"");
                        }
                    }
            }){
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart(filename,data, "image/jpeg"));
                return params;
            }
        };
        requestQueue.add(request);
    }

    public interface DeleteFileCallback {
        void onFinish(int errorCode);
    }
    public static void DeleteFile(String accessToken, String category_id, String info_id, String file_id, Context context, DeleteFileCallback callBack) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaDeleteFile + "?file_id="+file_id+"&category_id="+category_id+"&info_id="+info_id;

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
}


