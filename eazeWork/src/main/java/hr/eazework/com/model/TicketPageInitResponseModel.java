package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 28-09-2018.
 */

public class TicketPageInitResponseModel implements Serializable {
    private GetTicketPageInitResultModel GetTicketPageInitResult;

    public GetTicketPageInitResultModel getGetTicketPageInitResult() {
        return GetTicketPageInitResult;
    }

    public void setGetTicketPageInitResult(GetTicketPageInitResultModel getTicketPageInitResult) {
        GetTicketPageInitResult = getTicketPageInitResult;
    }

    static public TicketPageInitResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketPageInitResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
