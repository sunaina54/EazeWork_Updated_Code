package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 28-09-2018.
 */

public class TicketPageInitRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private String SelfOrOther;
    private String TicektID;

    public AdvanceLoginDataRequestModel getLoginData() {
        return loginData;
    }

    public void setLoginData(AdvanceLoginDataRequestModel loginData) {
        this.loginData = loginData;
    }

    public String getSelfOrOther() {
        return SelfOrOther;
    }

    public void setSelfOrOther(String selfOrOther) {
        SelfOrOther = selfOrOther;
    }

    public String getTicektID() {
        return TicektID;
    }

    public void setTicektID(String ticektID) {
        TicektID = ticektID;
    }

    static public TicketPageInitRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketPageInitRequestModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
