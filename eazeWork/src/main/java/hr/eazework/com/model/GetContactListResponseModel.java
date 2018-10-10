package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 09-10-2018.
 */

public class GetContactListResponseModel implements Serializable {
    private GetContactListResultModel GetContactListResult;

    public GetContactListResultModel getGetContactListResult() {
        return GetContactListResult;
    }

    public void setGetContactListResult(GetContactListResultModel getContactListResult) {
        GetContactListResult = getContactListResult;
    }

    static public GetContactListResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetContactListResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
