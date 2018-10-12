package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 12-10-2018.
 */

public class GetTicketDetailResponseModel implements Serializable {
    private GetTicketDetailResultModel GetTicketDetailResult;

    public GetTicketDetailResultModel getGetTicketDetailResult() {
        return GetTicketDetailResult;
    }

    public void setGetTicketDetailResult(GetTicketDetailResultModel getTicketDetailResult) {
        GetTicketDetailResult = getTicketDetailResult;
    }

    static public GetTicketDetailResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetTicketDetailResponseModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
