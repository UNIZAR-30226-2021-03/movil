package services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpServices {
    //static int errorCode;

    //Interfaz Callback para recibir el código de error del backend
    public interface VolleyCallBack {
        void onFinish(Integer statusCode);
    }

    public static void signUp(String email, String nickname, String password, Context context, final VolleyCallBack callBack) {
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
                    //Log.d("Response", "Success Response: " + response.toString());
                    callBack.onFinish(1);
                },
                error -> {
                    if (error.networkResponse != null) {
                        //Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);
                        callBack.onFinish(error.networkResponse.statusCode);
                    }
                });
        requestQueue.add(request);
        //return errorCode;
    }
}
    /*
    static Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("Response", "Success Response: " + response.toString());

            errorCode = 1;
            callBack.onFinish()
        }
    };

    static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null) {
                Log.d("Error", "Error Response code: " + error.networkResponse.statusCode);

                errorCode = error.networkResponse.statusCode;
            }

        }
    };*/

/*
public class SignUpServices extends AsyncTask<String,Void,Integer> {
    private Context ctx;
    private static Integer errorCode=99;

    public interface Result {
        void processFinish(Integer errorCode);
    }
    public Result delegate;

    public SignUpServices(Context context,  Result _delegate) {
        ctx= context;
        delegate=_delegate;
    }

    @Override
    protected Integer doInBackground(String... params){
        Log.d("doInBackground","entrar");
        String email= params[0];
        String nickname= params[0];
        String password= params[0];


        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        JSONObject body = new JSONObject();
        try {
            //input your API parameters
            body.put("email",email);
            body.put("nickname",nickname);
            body.put("password",password);

        } catch (JSONException e) {}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Routes.rutaSignUp,
                body,
                listener,
                errorListener);

        return requestQueue.add(request);
        /*
        try {
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("doInBackground","salir "+errorCode.toString());
        //return errorCode;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d("OnPostExecute","Código de respuesta: " + integer.toString());
        delegate.processFinish(integer);
    }


    static Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("Response","Success Response: " + response.toString());

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
*/

/*

*/
    //POSIBLE SOLUCION ASINCORONA
    /*public class SignUpServices extends AsyncTask<String,Integer,String> {
        static int errorCode;
        private ProgressDialog dialog = new ProgressDialog(SignUpServices.this);

        public static int signUp(String email, String nickname, String password, Context context){
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject body = new JSONObject();
            try {
                //input your API parameters
                body.put("email",email);
                body.put("nickname",nickname);
                body.put("password",password);

            } catch (JSONException e) {

            }
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Routes.rutaSignUp,
                    body,
                    listener,
                    errorListener);

            requestQueue.add(request);
            return errorCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Cargando");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response","Success Response: " + response.toString());

                    errorCode = 1;
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        Log.d("Error","Error Response code: " + error.networkResponse.statusCode);

                        errorCode = error.networkResponse.statusCode;
                    }

                }
            };

            return "process finished";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
        }*/



        // Enter the correct url for your api service site
        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Routes.rutaSignUp, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.println(Log.INFO,"a",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.println(Log.INFO,"e",error.toString());
            }
        });
        //requestQueue.add(jsonObjectRequest);
//}


*/
