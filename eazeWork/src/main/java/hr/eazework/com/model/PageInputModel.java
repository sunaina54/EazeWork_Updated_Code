package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 03-10-2018.
 */

public class PageInputModel implements Serializable {
    private String ParamType;
    private String ParamCode ;

    public String getParamType() {
        return ParamType;
    }

    public void setParamType(String paramType) {
        ParamType = paramType;
    }

    public String getParamCode() {
        return ParamCode;
    }

    public void setParamCode(String paramCode) {
        ParamCode = paramCode;
    }

    static public PageInputModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, PageInputModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
