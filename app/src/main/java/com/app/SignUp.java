package com.app;

import android.annotation.SuppressLint;
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

import services.AuthService;

public class SignUp extends AppCompatActivity {

    private EditText apodo,mail,password,confirmPassword;
    private TextView errorConfirm,errorEmail;
    private ProgressDialog dialog;
    private String auxApodo,auxMail,auxPassword,auxConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dialog = new ProgressDialog(this);

        apodo = findViewById(R.id.apodo);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        errorConfirm = findViewById(R.id.confirmIncorrectSign);
        errorEmail = findViewById(R.id.incorrectMail);
    }

    public void onButtonShowPopupWindowClick(View view) {

        //Clase interna para evaluar la respuesta del backend
        class ResponseHandler{
            public void handler(Integer statusCode){
                if (statusCode == 400) {
                    errorConfirm.setText("*No se cumplen los requisitos");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (statusCode == 409) {
                    errorConfirm.setText("*Ya ese existe un usuario con este email");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (statusCode == 501) {
                    errorConfirm.setText("*No se puede enviar el email de verificación");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else if (statusCode == 500) {
                    errorConfirm.setText("*Server error");
                    errorConfirm.setVisibility(View.VISIBLE);
                } else {


                    // inflate the layout of the popup window
                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_verification, null);
                    // create the popup window
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    ((TextView)popupWindow.getContentView().findViewById(R.id.mailVerification)).setText(auxMail);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            logInActivity();
                            return true;
                        }
                    });
                }
            }
        }

        errorConfirm.setVisibility(View.GONE);

        auxApodo = apodo.getText().toString();
        auxMail = mail.getText().toString();
        auxPassword = password.getText().toString();
        auxConfirmPassword = confirmPassword.getText().toString();
        ResponseHandler responseHandler = new ResponseHandler();

        if(!auxPassword.equals(auxConfirmPassword)){
            errorConfirm.setText("*Las contraseñas no coinciden");
            errorConfirm.setVisibility(View.VISIBLE);
        }
        if( !auxMail.matches("[a-z0-9.]+@[a-z0-9]+.[a-z]{3}")){
            errorEmail.setText("*El email introducido no es correcto");
            errorEmail.setVisibility(View.VISIBLE);
        }
        if (auxPassword.equals(auxConfirmPassword) && auxMail.matches("[a-z0-9.]+@[a-z0-9]+.[a-z]{3}")) {
            dialog.setMessage("Cargando");
            dialog.show();
            AuthService.SignUp(auxMail, auxApodo, auxPassword,this,
                    statusCode -> {
                        dialog.dismiss();
                        responseHandler.handler(statusCode);
                    });
        }

    }

    public void logInActivity() {
        Intent i = new Intent(this,LogIn.class);
        startActivity(i);
        finish();
    }
}
