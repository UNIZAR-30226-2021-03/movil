package com.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import services.ListCategoriesServices;
import services.LogInServices;
import services.NewCategoriesServices;
import services.TokenServices;

public class Welcome extends AppCompatActivity {
    private String accesTokenWelcome;
    private PopupWindow popupWindow;
    private EditText newCategoryName;
    private ProgressDialog dialog;
    private TextView errorAdd;
    private static final int SIGN_OFF = Menu.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        dialog = new ProgressDialog(this);
        Intent i = getIntent();
        accesTokenWelcome = i.getStringExtra("accessToken");

        setTitle("Bienvenido");
        reloadScreen();

    }

    public void reloadScreen(){
        class ResponseHandler {
            public void handler(String list) {
                System.out.println(list);
                if (list.equals("403")) {
                    //HACER ALGO
                }
                else{
                    //LISTAR CATEGORÍAS
                }
            }
        }
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        ListCategoriesServices.listCategories(accesTokenWelcome,this,
                list -> {
                    dialog.dismiss();
                    responseHandler.handler(list);
                });
    }



    public void addCategoryName(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_new_category, null);

        newCategoryName = popupView.findViewById(R.id.nameCategory);
        errorAdd = popupView.findViewById(R.id.incorretCategory);
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
                setContentView(R.layout.activity_welcome);
                return true;
            }
        });
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
                //INICIAR ACTIVIDAD
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    reloadScreen();
                    //setContentView(R.layout.activity_welcome);
                }
            }
        }
        String name = newCategoryName.getText().toString();
        //tokenCode = TokenServices.checkToken(statusCode, code, this);
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        NewCategoriesServices.newCategory(accesTokenWelcome,name, this,
                (Integer statusCode) -> {
                    dialog.dismiss();
                    responseHandler.handler(statusCode);
                });
    }

}
