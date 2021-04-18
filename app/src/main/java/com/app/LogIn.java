package com.app;

import android.content.Intent;
import android.os.Bundle;
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

public class LogIn extends AppCompatActivity {

    private EditText mail;
    private EditText password;
    private EditText faCode;
    private int statusCode;
    private TextView errorConfirm;
    private TextView errorCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        errorConfirm = findViewById(R.id.confirmIncorrect);
    }

    public void onButtonShowPopupWindow(View view) {

        errorConfirm.setVisibility(View.GONE);

        //LogInServices.logIn("cbellvis99@gmail.com","Borque1", this);

        String auxMail = mail.getText().toString();
        String auxPassword = password.getText().toString();

        statusCode = LogInServices.logIn(auxPassword, auxMail, this);

        if(statusCode == 400){
            errorConfirm.setText("*No se cumplen los requisitos");
            errorConfirm.setVisibility(View.VISIBLE);
        }
        else if(statusCode == 401){
            errorConfirm.setText("*Contraseña incorrecta");
            errorConfirm.setVisibility(View.VISIBLE);
        }
        else if(statusCode == 404){
            errorConfirm.setText("*El usuario no existe");
            errorConfirm.setVisibility(View.VISIBLE);
        }
        else if(statusCode == 501){
            errorConfirm.setText("*No se puede enviar el email de verificación");
            errorConfirm.setVisibility(View.VISIBLE);
        }
        else if(statusCode == 500){
            errorConfirm.setText("*Server error");
            errorConfirm.setVisibility(View.VISIBLE);
        }

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_2fa, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched

        popupView.setOnTouchListener(new View.OnTouchListener() {
            public void checkCode(){
                faCode = findViewById(R.id.fa);
                errorCode = findViewById(R.id.IncorretCode);
                if (fa == ){
                    popupWindow.dismiss();
                }
                else{
                    errorCode.setText("*Codigo incorrecto");
                    errorCode.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}