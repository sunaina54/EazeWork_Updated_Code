package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 16-08-2018.
 */

public class GetAnnouncementResultResponseModel implements Serializable {
        private GetAnnouncementResult GetAnnouncementResult;

    public hr.eazework.com.model.GetAnnouncementResult getGetAnnouncementResult() {
        return GetAnnouncementResult;
    }

    public void setGetAnnouncementResult(hr.eazework.com.model.GetAnnouncementResult getAnnouncementResult) {
        GetAnnouncementResult = getAnnouncementResult;
    }

    static public GetAnnouncementResultResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetAnnouncementResultResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
