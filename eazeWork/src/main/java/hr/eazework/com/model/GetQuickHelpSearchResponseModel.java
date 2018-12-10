package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 04-12-2018.
 */

public class GetQuickHelpSearchResponseModel implements Serializable {
    private GetQuickHelpSearchResultModel GetQuickHelpSearchResult;

    public GetQuickHelpSearchResultModel getGetQuickHelpSearchResult() {
        return GetQuickHelpSearchResult;
    }

    public void setGetQuickHelpSearchResult(GetQuickHelpSearchResultModel getQuickHelpSearchResult) {
        GetQuickHelpSearchResult = getQuickHelpSearchResult;
    }

    static public GetQuickHelpSearchResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetQuickHelpSearchResponseModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
