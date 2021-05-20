package com.app;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
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
import services.FileService;
import services.InfoService;
import services.Routes;

public class EditInfo extends AppCompatActivity {

    private String accessToken,category_id,info_id,file_id,fileName;
    private Boolean is_new,visible=false;
    private ProgressDialog dialog,dialogError;
    private EditText name,username,password,url,description,tamaño,specialCharacters;
    private TextView errorName,errorUser,errorPassword,errorURL;
    private CheckBox upper,lower,numbers;
    private Button downloadFile;
    private Intent i;
    private PopupWindow popupWindow;
    private CustomTabsSession mSession;
    private CustomTabsServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_description);
        dialog = new ProgressDialog(this);
        dialogError = new ProgressDialog(this);
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
        errorName = findViewById(R.id.nameError);
        errorUser = findViewById(R.id.userError);
        errorPassword = findViewById(R.id.passwordError);
        errorURL = findViewById(R.id.urlError);


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

        if(!aux.equals("")){
            downloadFile.setText(aux);
        }

        //date.setText(i.getStringExtra("date"))
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
            body.put("url",_url);
            if(!_description.equals("")) {body.put("description",_description);}
        }catch(JSONException e){}

        class ResponseHandlerAdd {
            public void handler(Integer statusCode) {
                if (statusCode==403) {
                    dialog.dismiss();
                    dialogError.setMessage("Ha ocurrido un fallo");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                }
                else if (statusCode==401) {
                    dialog.dismiss();
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                    logInActivity();
                }
                else if (statusCode==462){
                    dialog.dismiss();
                    dialogError.setMessage("No se ha podido crear la contraseña");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                }
                dialog.dismiss();
                finish();
            }
        }
        class ResponseHandlerUpdate {
            public void handler(Integer statusCode) {
                if (statusCode==403) {
                    dialog.dismiss();
                    dialogError.setMessage("Ha ocurrido un fallo");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                }
                else if (statusCode==401) {
                    dialog.dismiss();
                    dialogError.setMessage("Su sesión ha expirado, vuelva a iniciar sesión");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                    logInActivity();
                }
                else if (statusCode==464){
                    dialog.dismiss();
                    dialogError.setMessage("No se ha podido actualizar la contraseña");
                    dialogError.show();

                    dialogError.setCanceledOnTouchOutside(true);
                }
                dialog.dismiss();
                finish();
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
        else if(!android.util.Patterns.WEB_URL.matcher(_url).matches()){
            errorURL.setVisibility(View.VISIBLE);
        }
        else{

            //Los campos no son vacios
            if(is_new) {
                dialogError.setCanceledOnTouchOutside(false);
                ResponseHandlerAdd responseHandleradd = new ResponseHandlerAdd();

                dialog.setMessage("Cargando");
                dialog.show();
                InfoService.AddInfo(accessToken, body, this,
                        statusCode -> responseHandleradd.handler(statusCode));

            }else{
                try {
                    body.put("info_id",info_id);
                } catch (JSONException e) {}
                dialogError.setCanceledOnTouchOutside(false);
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
                .useDigits(use_numbers).useLower(use_lower).useUpper(use_upper).useSpecial(specials).build();
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
            Uri filepath=data.getData(); //TODO: sacar el filename
            fileName=filepath.getLastPathSegment();
            fileName+=".jpeg";
            commitUpload(getFileDataFromUri(this, filepath));
        }
    }

    public void commitUpload(byte[] data){
        commitDeleteFile(false);
        class ResponseHandlerUpdate {
            public void handler(Integer statusCode) {
                //TODO: ERRORES
                downloadFile.setText(fileName);
                dialog.dismiss();
            }
        }

        ResponseHandlerUpdate responseHandlerupdate = new ResponseHandlerUpdate();

        dialog.setMessage("Cargando");
        dialog.show();
        FileService.UploadFile(accessToken, category_id,info_id,data,fileName, this,
                statusCode -> responseHandlerupdate.handler(statusCode));

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
                //TODO: ERRORES
                if(loading) {
                    downloadFile.setText("NO HAY ARCHIVOS SUBIDOS");
                    file_id="";
                    dialog.dismiss();
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
      /*  if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(EditInfo.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(EditInfo.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            }
        } else {
            commitDownload();
        }*/
        commitDownload();
    }
    public void commitDownload() {
        if(!file_id.equals("")) {
            String url = Routes.rutaUploadFile + "?file_id=" + file_id;
            CustomTabsIntent intent = constructExtraHeadersIntent(mSession);
            intent.launchUrl(EditInfo.this, Uri.parse(url));
        }
    }

    private CustomTabsIntent constructExtraHeadersIntent(CustomTabsSession session) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder(session).build();

        // Example non-cors-whitelisted headers.
        Bundle headers = new Bundle();
        headers.putString("accessToken", accessToken);
        intent.intent.putExtra(Browser.EXTRA_HEADERS, headers);

        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set up a callback that launches the intent after session was validated.
        CustomTabsCallback callback = new CustomTabsCallback() {
            @Override
            public void onRelationshipValidationResult(int relation, @NonNull Uri requestedOrigin,
                                                       boolean result, @Nullable Bundle extras) {
                // Can launch custom tabs intent after session was validated as the same origin.
                downloadFile.setEnabled(true);
            }
        };

        // Set up a connection that warms up and validates a session.
        mConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(@NonNull ComponentName name,
                                                     @NonNull CustomTabsClient client) {
                // Create session after service connected.
                mSession = client.newSession(callback);
                client.warmup(0);
                String url = Routes.rutaUploadFile + "?file_id=" + file_id;
                // Validate the session as the same origin to allow cross origin headers.
                mSession.validateRelationship(CustomTabsService.RELATION_USE_AS_ORIGIN,
                        Uri.parse(url), null);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };

        //Add package names for other browsers that support Custom Tabs and custom headers.
        List<String> packageNames = Arrays.asList(
                "com.google.android.apps.chrome",
                "com.chrome.canary",
                "com.chrome.dev",
                "com.chrome.beta",
                "com.android.chrome"
        );
        String packageName =
                CustomTabsClient.getPackageName(EditInfo.this, packageNames, false);
        if (packageName == null) {
            Toast.makeText(getApplicationContext(), "Package name is null.", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Bind the custom tabs service connection.
            CustomTabsClient.bindCustomTabsService(this, packageName, mConnection);
        }

    }


}
