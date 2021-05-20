package services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;


import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import helpers.InputStreamVolleyRequest;
import helpers.VolleyMultipartRequest;

import static android.content.Context.DOWNLOAD_SERVICE;


public class FileService {

    public interface UploadFileCallback {
        void onFinish(int errorCode);
    }
    public static void UploadFile(String accessToken, String category_id, String info_id, byte[] data,String filename, Context context, UploadFileCallback callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Routes.rutaUploadFile+"?category_id="+category_id+"&info_id="+info_id;

        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken",accessToken);


        VolleyMultipartRequest request = new VolleyMultipartRequest( url,headers,
                new Response.Listener<NetworkResponse> () {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        callBack.onFinish(200);
                    }
                },
                new  Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            callBack.onFinish(error.networkResponse.statusCode);
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

    public interface DownloadFileCallback {
        void onFinish(int errorCode);
    }
    public static void DownloadFile(String accessToken, String file_id,String file_name, Context context, DownloadFileCallback callBack) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken",accessToken);




    }
}


