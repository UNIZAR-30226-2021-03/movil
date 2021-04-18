package com.app;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import services.SignUpServices;

public class SignUp extends AppCompatActivity {

    private EditText apodo;
    private EditText mail;
    private EditText password;
    private EditText confirmPassword;
    private TextView errorConfirm;
    private int statusCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        apodo = findViewById(R.id.apodo);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        errorConfirm = findViewById(R.id.confirmIncorrect);
    }

    public void onButtonShowPopupWindowClick(View view) {

        //SignUpServices.signUp("cbellvis99@gmail.com","Borque","Borque1", this);

        errorConfirm.setVisibility(View.GONE);

        String auxApodo = apodo.getText().toString();
        String auxMail = mail.getText().toString();
        String auxPassword = password.getText().toString();
        String auxConfirmPassword = confirmPassword.getText().toString();


        if(auxPassword.equals(auxConfirmPassword)){
            statusCode = SignUpServices.signUp(auxMail, auxApodo, auxPassword, this);

            if(statusCode == 400){
                errorConfirm.setText("*No se cumplen los requisitos");
                errorConfirm.setVisibility(View.VISIBLE);
            }
            else if(statusCode == 409){
                errorConfirm.setText("*Ya ese existe un usuario con este email");
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
            else{
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_verification, null);

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
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        setContentView(R.layout.activity_log_in);
                        return true;
                    }
                });
            }

        }
        else{
            errorConfirm.setText("Las contraseñas no coinciden");
            errorConfirm.setVisibility(View.VISIBLE);
        }


    }
}
