package com.app;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.content.ClipboardManager;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.Info;
import adapters.InfoAdapter;
import helpers.PasswordGenerator;
import services.InfoService;

public class EditInfo extends AppCompatActivity {

    private String accesToken;
    private String category_id;
    private Boolean is_new;
    private String info_id;
    private ProgressDialog dialog;
    private EditText name;
    private EditText username;
    private EditText password;
    private EditText url;
    private EditText description;
    private Boolean visible=false;
    private CheckBox upper,lower,numbers;
    private EditText tamaño;
    private EditText specialCharacters;
    private Intent i;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_description);
        dialog = new ProgressDialog(this);
        i = getIntent();
        accesToken = i.getStringExtra("accessToken");
        category_id = i.getStringExtra("category_id");
        is_new = i.getBooleanExtra("is_new",false);

        name = findViewById(R.id.name);
        username =  findViewById(R.id.username);
        password =  findViewById(R.id.password);
        url =  findViewById(R.id.url);
        description =  findViewById(R.id.description);

        if(!is_new){
            info_id = i.getStringExtra("info_id");
            fillData(true);
        }

    }

    //PARA RELLENAR LOS DATOS
    public void fillData(Boolean delay){
        name.setText(i.getStringExtra("name"));
        username.setText(i.getStringExtra("username"));
        password.setText(i.getStringExtra("password"));
        url.setText(i.getStringExtra("url"));
        description.setText(i.getStringExtra("description"));

        //date.setText(i.getStringExtra("date"))
    }

    public void saveInfo(View view) {
        String _name,_username,_password,_url,_description;
        //TODO : Sacar errores si los campos no son correctos y no avanzar (nombre,username y password no vacío) y URL es url
        _name = name.getText().toString();
        _username = username.getText().toString();
        _password = password.getText().toString();
        _url = url.getText().toString();
        _description = description.getText().toString();

        JSONObject body = new JSONObject();
        try{
            body.put("category_id",category_id);
            body.put("name",_name);
            body.put("username",_username);
            body.put("password",_password);
            body.put("url",_url);
            if(!_description.equals("")) {body.put("description",_description);}
        }catch(JSONException e){}

        class ResponseHandlerAdd {
            public void handler(Integer statusCode) {
                //TODO: Gestionar los errores, con un dialogo de error quiza?
                dialog.dismiss();
                finish();
            }
        }
        class ResponseHandlerUpdate {
            public void handler(Integer statusCode) {
                //TODO: Gestionar los errores, con un dialogo de error quiza?
                dialog.dismiss();
                finish();
            }
        }
        if(is_new) {
            ResponseHandlerAdd responseHandleradd = new ResponseHandlerAdd();

            dialog.setMessage("Cargando");
            dialog.show();
            InfoService.AddInfo(accesToken, body, this,
                    statusCode -> responseHandleradd.handler(statusCode));

        }else{
            try {
                body.put("info_id",info_id);
            } catch (JSONException e) {}
            ResponseHandlerUpdate responseHandlerupdate = new ResponseHandlerUpdate();

            dialog.setMessage("Cargando");
            dialog.show();
            InfoService.UpdateInfo(accesToken, body, this,
                    statusCode -> responseHandlerupdate.handler(statusCode));
        }
    }

    //Para el botón de atrás
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void commitGenerate(View v){
        Boolean use_upper,use_lower,use_numbers;
        int tam=10;
        String tmp;
        String specials;
        use_upper = upper.isChecked();
        use_lower = lower.isChecked();
        use_numbers = numbers.isChecked();
        specials = specialCharacters.getText().toString();

        tmp=tamaño.getText().toString();
        if(!tmp.equals("")){tam=Integer.parseInt(tmp);}

        PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(use_numbers).useLower(use_lower).useUpper(use_upper).build();
        String generated = generator.generate(tam);
        password.setText(generated);
        popupWindow.dismiss();
    }

    public void generatePassword(View v){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_generate_password, null);

        tamaño = (EditText) popupView.findViewById(R.id.tamanyo);
        specialCharacters = (EditText) popupView.findViewById(R.id.specialCharacters);
        upper = (CheckBox) popupView.findViewById(R.id.upper);
        lower = (CheckBox) popupView.findViewById(R.id.lower);
        numbers = (CheckBox) popupView.findViewById(R.id.numbers);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void setVisible(View v){
        if(!visible){
            password.setTransformationMethod(null); //Mostrar
            visible = true;
        }else{
            password.setTransformationMethod(new PasswordTransformationMethod());
            visible = false;
        }
    }

    public void copyPassword(View v){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Contraseña", password.getText().toString());
        clipboard.setPrimaryClip(clip);
    }


}
