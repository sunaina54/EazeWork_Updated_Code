package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 03-10-2018.
 */

public class SubCategoryResponseModel implements Serializable {

    private GetCommonListResultModel GetCommonListResult;

    public GetCommonListResultModel getGetCommonListResult() {
        return GetCommonListResult;
    }

    public void setGetCommonListResult(GetCommonListResultModel getCommonListResult) {
        GetCommonListResult = getCommonListResult;
    }

    static public SubCategoryResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, SubCategoryResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
