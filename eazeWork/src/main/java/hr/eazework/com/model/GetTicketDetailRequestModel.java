package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 16-10-2018.
 */

public class GetTicketDetailRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private String TicketID;
    private String SimpleOrAdvance;

    public String getSimpleOrAdvance() {
        return SimpleOrAdvance;
    }

    public void setSimpleOrAdvance(String simpleOrAdvance) {
        SimpleOrAdvance = simpleOrAdvance;
    }

    public AdvanceLoginDataRequestModel getLoginData() {
        return loginData;
    }

    public void setLoginData(AdvanceLoginDataRequestModel loginData) {
        this.loginData = loginData;
    }

    public String getTicketID() {
        return TicketID;
    }

    public void setTicketID(String ticketID) {
        TicketID = ticketID;
    }

    static public GetTicketDetailRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetTicketDetailRequestModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
