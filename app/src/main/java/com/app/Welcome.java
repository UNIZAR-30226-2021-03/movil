package com.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;

import adapters.Category;
import adapters.CategoryAdapter;
import services.CategoryService;

public class Welcome extends AppCompatActivity {
    private String accesTokenWelcome;
    private PopupWindow popupWindow;
    private EditText newCategoryName;
    private ProgressDialog dialog;
    private TextView errorAdd;
    private ListView lista;
    private String working_id;
    private Context ctx;

    /**Atributos de la clase para distinguir opciones de menú*/
    private static final int BORRAR_CAT = Menu.FIRST+1;
    private static final int VER_CAT = Menu.FIRST+2;
    private static final int RENOMBRAR_CAT = Menu.FIRST+3;
    private static final int SIGN_OFF = Menu.FIRST+4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        dialog = new ProgressDialog(this);
        Intent i = getIntent();
        accesTokenWelcome = i.getStringExtra("accessToken");
        lista = findViewById(R.id.list_categories);
        ctx = this;

        setTitle("Bienvenido");
        fillData(true);
        registerForContextMenu(lista);

    }

    /** Se llama cuando la actividad se crea por primera vez y genera un menú de opciones */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, SIGN_OFF, Menu.NONE, "Cerrar sesión");
        return result;
    }

    /** Se llama cuando el usuario selecciona una opción del menú*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SIGN_OFF:
                mainActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Se llama cuando la actividad se crea por primera vez y genera un menú contextual */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, BORRAR_CAT, Menu.NONE,"Borrar Categoría");
        menu.add(Menu.NONE, VER_CAT, Menu.NONE, "Ver Categoría");
        menu.add(Menu.NONE, RENOMBRAR_CAT, Menu.NONE, "Renombrar Categoría");
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CategoryAdapter adapter = (CategoryAdapter) lista.getAdapter();
        Category cat = adapter.getCat((int) info.id);
        working_id=cat.get_id();

        switch(item.getItemId()) {
            case BORRAR_CAT:
                deleteCategory();
                return true;
            case VER_CAT:
                //TODO: Iniciar nueva actividad
                //System.out.println(cat.get_id());
                //System.out.println(cat.getName());
                return true;
            case RENOMBRAR_CAT:
                renameCategory();
                //System.out.println(cat.get_id());
                //System.out.println(cat.getName());
                return true;
        }
        return super.onContextItemSelected(item);
    }

    //PARA RELLENAR LA LISTA DE CATÉGORÍAS
    public void fillData(Boolean delay){
        class ResponseHandler {
            public void handler(JSONArray list) {
                // TODO: Comprobar fallos!! Códigos de ERROOORR
                ArrayList<Category> categorias = Category.fromJson(list);
                CategoryAdapter adapter = new CategoryAdapter(ctx, categorias);
                lista.setAdapter(adapter);
            }
        }
        ResponseHandler responseHandler = new ResponseHandler();
        if(delay) {
            dialog.setMessage("Cargando");
            dialog.show();
        }
        //QUitar?
        SystemClock.sleep(300);
        CategoryService.ListCategories(accesTokenWelcome,this,
                list -> {
                    if(delay) {
                        dialog.dismiss();
                    }
                    responseHandler.handler(list);
                });
    }

    //PARA AÑADIR NUEVAS CATEGORÍAS
    public void addCategory(View view) {
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

    public void commitAdd(View view){
        class ResponseHandler {
            public void handler(Integer statusCode) {
                if (statusCode==452) {
                    errorAdd.setText("*No se ha podido crear la categoría");
                    errorAdd.setVisibility(View.VISIBLE);
                } else if (statusCode==403) {
                    errorAdd.setText("*Ha ocurrido un fallo");
                    errorAdd.setVisibility(View.VISIBLE);
                }
                else if (statusCode==200){
                    popupWindow.dismiss();
                    //Refrescar lista
                    fillData(false);
                }
            }
        }
        String name = newCategoryName.getText().toString();
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        CategoryService.NewCategory(accesTokenWelcome,name, this,
                (Integer statusCode) -> {
                    dialog.dismiss();
                    responseHandler.handler(statusCode);
                });
    }


    //PARA RENOMBRAR CATÉGORÍAS
    public void renameCategory() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_rename_category, null);

        newCategoryName = popupView.findViewById(R.id.nameCategory);
        errorAdd = popupView.findViewById(R.id.incorretCategory);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

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

    public void commitRename(View view){
        class ResponseHandler {
            public void handler(Integer statusCode) {
                if (statusCode==454) {
                    errorAdd.setText("*No se ha podido actualizar la categoría");
                    errorAdd.setVisibility(View.VISIBLE);
                } else if (statusCode==403) {
                    errorAdd.setText("*Ha ocurrido un fallo");
                    errorAdd.setVisibility(View.VISIBLE);
                }else if (statusCode==401) {
                    errorAdd.setText("*La sesión ha caducado, vuelva a iniciar sesión");
                    errorAdd.setVisibility(View.VISIBLE);
                }
                else if (statusCode==200){
                    popupWindow.dismiss();
                    //Refrescar lista
                    fillData(false);
                }
            }
        }
        String name = newCategoryName.getText().toString();
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        CategoryService.RenameCategory(accesTokenWelcome,working_id,name, this,
                (Integer statusCode) -> {
                    dialog.dismiss();
                    responseHandler.handler(statusCode);
                });
    }

    //PARA BORRAR CATÉGORÍAS
    public void deleteCategory(){
        class ResponseHandler {
            public void handler(Integer statusCode) {
                if (statusCode==453) {
                    errorAdd.setText("*No se ha podido actualizar la categoría");
                    errorAdd.setVisibility(View.VISIBLE);
                } else if (statusCode==403) {
                    errorAdd.setText("*Ha ocurrido un fallo");
                    errorAdd.setVisibility(View.VISIBLE);
                }else if (statusCode==401) {
                    errorAdd.setText("*La sesión ha caducado, vuelva a iniciar sesión");
                    errorAdd.setVisibility(View.VISIBLE);
                }
                else if (statusCode==200){
                    //Refrescar lista
                    fillData(false);
                    dialog.dismiss();
                }
            }
        }
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        CategoryService.DeleteCategory(accesTokenWelcome,working_id, this,
                (Integer statusCode) -> {

                    responseHandler.handler(statusCode);
                });
    }

    public void mainActivity() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }
}
