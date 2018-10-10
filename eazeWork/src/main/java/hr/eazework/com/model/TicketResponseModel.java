package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 05-10-2018.
 */

public class TicketResponseModel implements Serializable {
    private GetTicketsResultModel GetTicketsResult;

    public GetTicketsResultModel getGetTicketsResult() {
        return GetTicketsResult;
    }

    public void setGetTicketsResult(GetTicketsResultModel getTicketsResult) {
        GetTicketsResult = getTicketsResult;
    }

    static public TicketResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
