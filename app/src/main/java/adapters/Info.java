package adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Info {

    private String _id;
    private String name;
    private String username;
    private String password;
    private String url;
    private String decription;
    private String creation_date;
    private String file_id;
    private String file_name;

    public Info(JSONObject object){
        try {
            this._id = object.getString("_id");
            this.name = object.getString("name");
            this.username = object.getString("username");
            this.password = object.getString("password");
            this.creation_date = object.getString("creation_date");

            if(object.has("url")){
                this.url = object.getString("url");
            }
            if(object.has("decription")){
                this.decription = object.getString("decription");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<Info> fromJson(JSONArray jsonObjects) {
        ArrayList<Info> infos = new ArrayList<Info>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                infos.add(new Info(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return infos;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}
