package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 05-10-2018.
 */

public class TicketSubmitRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private String SelfOrOther;
    private TicketDetailModel ticketDetail;

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
        this.SelfOrOther = selfOrOther;
    }

    public TicketDetailModel getTicketDetail() {
        return ticketDetail;
    }

    public void setTicketDetail(TicketDetailModel ticketDetail) {
        this.ticketDetail = ticketDetail;
    }

    static public TicketSubmitRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketSubmitRequestModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
