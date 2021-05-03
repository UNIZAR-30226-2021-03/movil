package com.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import services.LogInServices;
import services.NewCategoriesServices;
import services.TokenServices;

public class Welcome extends AppCompatActivity {
    private String accesTokenWelcome;
    private PopupWindow popupWindow;
    private EditText newCategory;
    private ProgressDialog dialog;
    private TextView errorAdd;
    private static final int SIGN_OFF = Menu.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        dialog = new ProgressDialog(this);

        setTitle("Bienvenido");

        accesTokenWelcome = LogIn.accesToken;
    }

    /** Se llama cuando la actividad se crea por primera vez y genera un menú de opciones */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, SIGN_OFF, Menu.NONE, "Cerrar sesión");
        //test?
        return result;
    }

    /** Se llama cuando el usuario selecciona una opción del menú*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SIGN_OFF:
                setContentView(R.layout.activity_main);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonShowPopupWindowAddCategory(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_new_category, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        newCategory = (EditText) popupView.findViewById(R.id.newCategory);
        errorAdd = (TextView) popupView.findViewById(R.id.incorretCategory);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                setContentView(R.layout.activity_welcome);
                return true;
            }
        });
    }


    public void addCategory(View view){
        class ResponseHandler {
            public void handler(Integer statusCode) {
                if (statusCode==452) {
                    errorAdd.setText("*No se ha podido crear la categoría");
                    errorAdd.setVisibility(View.VISIBLE);
                } else if (statusCode==403) {
                    errorAdd.setText("*Ha ocurrido un fallo");
                    errorAdd.setVisibility(View.VISIBLE);
                }
                else{
                    popupWindow.dismiss();
                    //CREAR CUADRO CON LA CATEGORIA

                    //IR A PANTALLA DE LA ACTIVIDAD CREADA
                    setContentView(R.layout.activity_welcome);
                }
            }
        }
        String nameNewCategory = newCategory.getText().toString();
        //tokenCode = TokenServices.checkToken(statusCode, code, this);
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        NewCategoriesServices.newCategory(accesTokenWelcome,nameNewCategory, this,
                statusCode -> {
                    /* System.out.println(statusCode); */
                    dialog.dismiss();
                    responseHandler.handler(statusCode);
                });
    }

}
