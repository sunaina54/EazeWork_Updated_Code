package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 03-10-2018.
 */

public class GetCommonListResultModel extends GenericResponse implements Serializable {
    private ArrayList<CategoryListItem> list;

    public ArrayList<CategoryListItem> getList() {
        return list;
    }

    public void setList(ArrayList<CategoryListItem> list) {
        this.list = list;
    }

    static public GetCommonListResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetCommonListResultModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}