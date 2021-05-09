package com.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import services.LogInServices;
import services.SignUpServices;
import services.TokenServices;

public class LogIn extends AppCompatActivity {

    private EditText mail;
    private EditText password;
    private EditText faCode;
    private String statusCode;
    private String tokenCode;
    private TextView errorConfirm;
    private TextView errorCode;
    private PopupWindow popupWindow;
    private ProgressDialog dialog;
    private String accesToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        dialog = new ProgressDialog(this);

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        errorConfirm = findViewById(R.id.confirmIncorrectLog);

    }

    public void onButtonShowPopupWindow(View view) {

        //Clase interna para evaluar la respuesta del backend
        class ResponseHandler {
            public void handler(String response) {
                if (response.equals("400")) {
                    errorConfirm.setText("*No se cumplen los requisitos");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (response.equals("401")) {
                    errorConfirm.setText("*Contraseña incorrecta");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (response.equals("404")) {
                    errorConfirm.setText("*El usuario no existe");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (response.equals("501")) {
                    errorConfirm.setText("*No se puede enviar el email de verificación");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (response.equals("500")) {
                    errorConfirm.setText("*Server error");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else {
                    statusCode = response;
                    // inflate the layout of the popup window
                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_2fa, null);

                    errorCode = (TextView) popupView.findViewById(R.id.incorretCode);
                    faCode = (EditText) popupView.findViewById(R.id.fa);
                    // create the popup window
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    popupWindow = new PopupWindow(popupView, width, height, focusable);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                    // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            setContentView(R.layout.activity_log_in);
                            return true;
                        }
                    });
                }
            }
        }
        errorConfirm.setVisibility(View.GONE);

        //statusCode = LogInServices.logIn("cbellvis99@gmail.com","Borque1", this);

        String auxMail = mail.getText().toString();
        String auxPassword = password.getText().toString();
        ResponseHandler responseHandler = new ResponseHandler();

        //statusCode = LogInServices.logIn(auxMail, auxPassword,this);

        dialog.setMessage("Cargando");
        dialog.show();

        LogInServices.logIn(auxMail, auxPassword,this,
                statusCode -> {
                    // System.out.println(statusCode);
                    dialog.dismiss();
                    responseHandler.handler(statusCode);
                });

        //responseHandler.handler("200");dialog.dismiss();
    }

    public void checkCode(View view){
        //System.out.println("ENTRA EN CHECK CODE");
        class ResponseHandler {
            public void handler(String response2fa) {
                if (response2fa.equals("401")) {
                    errorCode.setText("*Codigo incorrecto");
                    errorCode.setVisibility(View.VISIBLE);
                }else if(response2fa.equals("500")){
                    errorCode.setText("*Error del servidor, intentelo más tarde");
                    errorCode.setVisibility(View.VISIBLE);
                } else{
                    accesToken = response2fa;
                    System.out.println(accesToken);
                    popupWindow.dismiss();
                    //IR A PANTALLA WELCOME
                    welcomeActivity(view,accesToken);
                }
            }
        }
        String code = faCode.getText().toString();
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        System.out.println(statusCode);
        TokenServices.checkToken(statusCode, code,this,
                response2fa -> {
                    /* System.out.println(statusCode); */
                    dialog.dismiss();
                    responseHandler.handler(response2fa);
                });
    }

    public void welcomeActivity(View view, String accessToken) {
        Intent i = new Intent(this,Welcome.class);
        i.putExtra("accessToken",accessToken);
        startActivity(i);
    }

}