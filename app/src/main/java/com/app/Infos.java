package com.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.Category;
import adapters.Info;
import adapters.InfoAdapter;
import services.CategoryService;
import services.InfoService;

public class Infos extends AppCompatActivity {

    private String accesToken;
    private String category_id;
    private String category_name;
    private ProgressDialog dialog;
    private ListView lista;
    private Context ctx;

    /**Atributos de la clase para distinguir opciones de menú*/
    private static final int BORRAR_INFO = Menu.FIRST+1;
    private static final int VER_INFO = Menu.FIRST+2;
    private static final int SIGN_OFF = Menu.FIRST+4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        dialog = new ProgressDialog(this);
        Intent i = getIntent();
        accesToken = i.getStringExtra("accessToken");
        category_id = i.getStringExtra("category_id");
        category_name = i.getStringExtra("category_name");

        lista = findViewById(R.id.list_infos);
        ctx = this;

        setTitle(category_name);
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

    //Para el botón de atrás
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /** Se llama cuando la actividad se crea por primera vez y genera un menú contextual */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, BORRAR_INFO, Menu.NONE,"Borrar Contraseña");
        menu.add(Menu.NONE, VER_INFO, Menu.NONE, "Ver Contraseña");
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        InfoAdapter adapter = (InfoAdapter) lista.getAdapter();
        Info inf = adapter.getInfo((int) info.id);

        switch(item.getItemId()) {
            case BORRAR_INFO:
                deleteInfo(category_id,inf.get_id());
                return true;
            case VER_INFO:
                //infosActivity(cat.getName());
                return true;
        }
        return super.onContextItemSelected(item);
    }

    //PARA RELLENAR LA LISTA DE INFOS
    public void fillData(Boolean delay){
        class ResponseHandler {
            public void handler(JSONArray list) {
                // TODO: Comprobar fallos!! Códigos de ERROOORR
                ArrayList<Info> infos = Info.fromJson(list);
                InfoAdapter adapter = new InfoAdapter(ctx, infos);
                lista.setAdapter(adapter);
                dialog.dismiss();

            }
        }
        ResponseHandler responseHandler = new ResponseHandler();
        if(delay) {
            dialog.setMessage("Cargando");
            dialog.show();
        }
        //QUitar?
        SystemClock.sleep(300);
        InfoService.ListInfos(accesToken,category_id,this,
                list -> {
                    responseHandler.handler(list);
                });
    }

    public void addInfo(View view){
        //TODO: Abrir Actividad de rellenar campos
        // pasandole el accessToken y la category_id a la que pertenece.

        //TODO: Abrá que hacer el módulo de generer contraseñas aleatorias

        //Provisional:

        JSONObject body = new JSONObject();

        try{
            body.put("category_id",category_id);
            body.put("name","prueba");
            body.put("username","artur");
            body.put("password","1234");
            body.put("url","https://moodle.es");
            body.put("description","hola buenas tardes");

        }catch(JSONException e){

        }

        class ResponseHandler {
            public void handler(Integer statusCode) {
                //TODO: Gestionar los errores, con un dialogo de error quiza?

                //Refrescar lista
                fillData(false);
                dialog.dismiss();

            }
        }
        ResponseHandler responseHandler = new ResponseHandler();

        dialog.setMessage("Cargando");
        dialog.show();
        InfoService.AddInfo(accesToken, body, this,
                statusCode -> responseHandler.handler(statusCode));
    }

    public void deleteInfo(String category_id, String info_id){
        class ResponseHandler {
            public void handler(Integer statusCode) {
                //TODO: Gestionar los errores, con un dialogo de error quiza?
                if (statusCode==463) {

                } else if (statusCode==403) {

                }
                else if (statusCode==401) {

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
        InfoService.DeleteInfo(accesToken, category_id, info_id, this,
                statusCode -> responseHandler.handler(statusCode));
    }


    public void mainActivity() {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
        finish();
    }

}
