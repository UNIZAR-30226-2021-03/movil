package adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Category {

    private String _id;
    private String name;

    public Category(String _id, String name){
        this._id=_id;
        this.name=name;
    }

    public Category(JSONObject object){
        try {
            this.name = object.getString("name");
            this._id = object.getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    // User.fromJson(jsonArray);
    public static ArrayList<Category> fromJson(JSONArray jsonObjects) {
        ArrayList<Category> users = new ArrayList<Category>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                users.add(new Category(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
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
}
