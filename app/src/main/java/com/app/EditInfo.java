package com.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Browser;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.content.ClipboardManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsService;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.customtabs.CustomTabsCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.Info;
import adapters.InfoAdapter;
import helpers.PasswordGenerator;
import services.AuthService;
import services.FileService;
import services.InfoService;
import services.Routes;

public class EditInfo extends AppCompatActivity {

    private String accessToken,category_id,info_id,file_id,fileName;
    private Boolean is_new,visible=false;
    private ProgressDialog dialog;
    private AlertDialog.Builder dialogError;
    private EditText name,username,password,url,description,tamanyo,specialCharacters;
    private TextView errorName,errorUser,errorPassword,errorURL,entropiaText,fecha;
    private CheckBox upper,lower,numbers;
    private Button downloadFile;
    private Intent i;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_description);
        dialog = new ProgressDialog(this);
        dialogError = new AlertDialog.Builder(this);
        i = getIntent();
        accessToken = i.getStringExtra("accessToken");
        category_id = i.getStringExtra("category_id");
        is_new = i.getBooleanExtra("is_new",false);

        name = findViewById(R.id.name);
        username =  findViewById(R.id.username);
        password =  findViewById(R.id.password);
        url =  findViewById(R.id.url);
        description =  findViewById(R.id.description);
        downloadFile   = findViewById(R.id.downloadFile);
        fecha = findViewById(R.id.fecha);
        errorName = findViewById(R.id.nameError);
        errorUser = findViewById(R.id.userError);
        errorPassword = findViewById(R.id.passwordError);
        errorURL = findViewById(R.id.urlError);
        entropiaText = findViewById(R.id.entropia);
        entropiaText.setVisibility(View.GONE);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                calcEntropy(s.toString());
            }
        });

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
        setTitle(i.getStringExtra("name"));
        String aux = i.getStringExtra("file_name");
        file_id=i.getStringExtra("file_id");

        String[] parts1 = i.getStringExtra("date").split("T");
        fecha.setText("Fecha: "+parts1[0]);

        if(!aux.equals("")){
            downloadFile.setText(aux);
        }
    }

    public void saveInfo(View view) {
        String _name,_username,_password,_url,_description;
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
            if(!_url.equals("")) {body.put("url",_url);}
            if(!_description.equals("")) {body.put("description",_description);}
        }catch(JSONException e){}

        class ResponseHandlerAdd {
            public void handler(Integer statusCode) {
                if (statusCode==403 || statusCode ==500) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            finish();
                        }
                    });
                    dialogError.show();
                }
                else if (statusCode==401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logInActivity();
                        }
                    });
                    dialogError.show();
                }
                else if (statusCode==462){
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("No se ha podido crear la contraseña");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                }else {
                    dialog.dismiss();
                    finish();
                }
            }
        }
        class ResponseHandlerUpdate {
            public void handler(Integer statusCode) {
                if (statusCode==403 || statusCode==500) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            finish();
                        }
                    });
                    dialogError.show();
                }
                else if (statusCode==401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logInActivity();
                        }
                    });
                    dialogError.show();
                }
                else if (statusCode==464){
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("No se ha podido actualizar la contraseña");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                }else{
                    dialog.dismiss();
                    finish();
                }
            }
        }
        if(_name.equals("")){
            //Nombre vacio
            errorName.setVisibility(View.VISIBLE);
        }
        else if(_username.equals("")){
            //Usuario vacio
            errorUser.setVisibility(View.VISIBLE);
        }
        else if(_password.equals("")){
            //Contraseña vacio
            errorPassword.setVisibility(View.VISIBLE);
        }
        else if(!_url.equals("") && !android.util.Patterns.WEB_URL.matcher(_url).matches()){
            errorURL.setVisibility(View.VISIBLE);
        }
        else{
            //Los campos son correctos
            if(is_new) {
                ResponseHandlerAdd responseHandleradd = new ResponseHandlerAdd();

                dialog.setMessage("Cargando");
                dialog.show();
                InfoService.AddInfo(accessToken, body, this,
                        statusCode -> responseHandleradd.handler(statusCode));

            }else{
                try {
                    body.put("info_id",info_id);
                } catch (JSONException e) {}
                ResponseHandlerUpdate responseHandlerupdate = new ResponseHandlerUpdate();

                dialog.setMessage("Cargando");
                dialog.show();
                InfoService.UpdateInfo(accessToken, body, this,
                        statusCode -> responseHandlerupdate.handler(statusCode));
            }
        }


    }

    //Para el botón de atrás
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @SuppressLint("ResourceAsColor")
    public void commitGenerate(View v){
        Boolean use_upper,use_lower,use_numbers;
        int tam=10;
        String tmp;
        String specials;
        use_upper = upper.isChecked();
        use_lower = lower.isChecked();
        use_numbers = numbers.isChecked();
        specials = specialCharacters.getText().toString();

        tmp=tamanyo.getText().toString();
        if(!tmp.equals("")){tam=Integer.parseInt(tmp);}

        PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(use_numbers).useLower(use_lower).useUpper(use_upper).useSpecial(specials).build();

        String generated = generator.generate(tam);
        password.setText(generated);
        calcEntropy(generated);
        popupWindow.dismiss();
    }

    public void generatePassword(View v){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_generate_password, null);

        tamanyo = (EditText) popupView.findViewById(R.id.tamanyo);
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
        Toast.makeText(EditInfo.this,"Contraseña copiada",Toast.LENGTH_LONG).show();
    }


    public void logInActivity() {
        Intent i = new Intent(this,LogIn.class);
        startActivity(i);
        finish();
    }



    public void uploadFile (View v){

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(EditInfo.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(EditInfo.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(EditInfo.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            }
        } else {
            chooseFile();
        }
    }

    public void chooseFile(){
        // Construct an intent for opening a folder
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/* .pdf .txt"); //De moemnto solo parece abrir la gelería
        startActivityForResult(Intent.createChooser(intent,"Select File"),200);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        //Llamamos a super para informar a la clase padre que la llamada a la actividad a finalizado
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Uri filepath=data.getData();
            fileName=filepath.getLastPathSegment();
            commitUpload(getFileDataFromUri(this, filepath));
        }
    }

    public void commitUpload(byte[] data){
        Context ctx=this;
        class ResponseHandlerUpload {
            public void handler(Integer statusCode,String _file_id) {
                 if (statusCode == 403 || statusCode == 500) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logInActivity();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 472) {
                     dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("No se ha podido subir el nuevo archivo");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                 }else if(statusCode==200){
                     file_id = _file_id;
                     downloadFile.setText(fileName);
                     dialog.dismiss();
                 }
            }
        }
        class ResponseHandlerDelete {
            public void handler(Integer statusCode) {
                if (statusCode == 403 || statusCode == 500) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logInActivity();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 473) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("No se ha podido subir el nuevo archivo");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                } else{
                    ResponseHandlerUpload responseHandlerupdate = new ResponseHandlerUpload();
                    dialog.setMessage("Cargando");
                    dialog.show();
                    FileService.UploadFile(accessToken, category_id, info_id, data, fileName, ctx,
                            (errorCode, _file_id) -> responseHandlerupdate.handler(errorCode, _file_id));
                }
            }
        }
        if(!file_id.equals("")) {
            ResponseHandlerDelete responseHandlerupdate = new ResponseHandlerDelete();
            FileService.DeleteFile(accessToken, category_id, info_id, file_id, this,
                    statusCode -> responseHandlerupdate.handler(statusCode));
        }else{
            ResponseHandlerUpload responseHandlerupdate = new ResponseHandlerUpload();
            dialog.setMessage("Cargando");
            dialog.show();
            FileService.UploadFile(accessToken, category_id,info_id,data,fileName, this,
                    (errorCode, file_id) -> responseHandlerupdate.handler(errorCode,file_id));
        }

    }

    public byte[] getFileDataFromUri(Context context, Uri uri) {
        try{
            InputStream inputStream=getContentResolver().openInputStream(uri);
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }catch(Exception e){
            return null;
        }
    }

    public void deleteFile(View v) {
        commitDeleteFile(true);
    }

    public void commitDeleteFile(Boolean loading){
        class ResponseHandlerUpdate {
            public void handler(Integer statusCode) {
                if (statusCode == 403 || statusCode == 500) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logInActivity();
                        }
                    });
                    dialogError.show();
                } else if (statusCode == 473) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("No se ha podido subir el nuevo archivo");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                }else if (statusCode==200){
                    downloadFile.setText("NO HAY ARCHIVOS SUBIDOS");
                    file_id="";
                    if(loading) {
                        dialog.dismiss();
                    }
                }
            }
        }
        if(!file_id.equals("")) {
            ResponseHandlerUpdate responseHandlerupdate = new ResponseHandlerUpdate();

            if(loading) {
                dialog.setMessage("Cargando");
                dialog.show();
            }
            FileService.DeleteFile(accessToken, category_id, info_id, file_id, this,
                    statusCode -> responseHandlerupdate.handler(statusCode));
        }
    }

    public void downloadFile(View v) {
        class ResponseHandler {
            public void handler(Integer statusCode) {
                if (statusCode==403) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Ha ocurrido un fallo inesperado, intentelo más tarde");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogError.show();
                }
                else if (statusCode==401) {
                    dialog.dismiss();
                    dialogError.setTitle("Aviso");
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.setCancelable(false);
                    dialogError.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            logInActivity();
                        }
                    });
                    dialogError.show();
                }else{
                    commitDownload();
                }
            }
        }
        if(!file_id.equals("")) {
            ResponseHandler responseHandlerupdate = new ResponseHandler();
            AuthService.CheckSession(accessToken,
                    this,
                    code -> responseHandlerupdate.handler(code));
        }
    }
    public void commitDownload() {
        String _url = Routes.rutaUploadFile + "?file_id=" + file_id;

        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW, Uri.parse(_url));
        Bundle bundle = new Bundle();
        bundle.putString("accessToken", accessToken);
        browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle);
        startActivity(browserIntent);
    }

    private void calcEntropy(String _password){

        int alphabetLength = 0;
        if(!_password.equals(_password.toLowerCase())){alphabetLength+=26;}
        if(!_password.equals(_password.toUpperCase())){alphabetLength+=26;}
        if(_password.matches("[0-9]+")){alphabetLength+=10;}
        for (int i = 0; i < _password.length(); i++) {
            if (_password.substring(i, i+1).matches("[^A-Za-z0-9]")) {
                alphabetLength++;
            }
        }

        double entropy = _password.length()* Math.log(alphabetLength)/Math.log(2);
        entropy=Math.floor(entropy);
        entropiaText.setText("Entropía: "+String.valueOf((int)entropy));
        //Valoración entropía
        if(entropy<28){
            entropiaText.setTextColor(getResources().getColor(R.color.error));
            entropiaText.setVisibility(View.VISIBLE);
        }
        else if(entropy>=28 && entropy<36){
            entropiaText.setTextColor(getResources().getColor(R.color.debil));
            entropiaText.setVisibility(View.VISIBLE);
        }
        else if(entropy>=36 && entropy<60){
            entropiaText.setTextColor(getResources().getColor(R.color.razonable));
            entropiaText.setVisibility(View.VISIBLE);
        }
        else if(entropy>=60 && entropy<128){
            entropiaText.setTextColor(getResources().getColor(R.color.muySegura));
            entropiaText.setVisibility(View.VISIBLE);
        }
        else if(entropy>=128){
            entropiaText.setTextColor(getResources().getColor(R.color.segura));
            entropiaText.setVisibility(View.VISIBLE);
        }


    }

}
