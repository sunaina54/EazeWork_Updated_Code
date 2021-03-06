package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Dell3 on 22-01-2018.
 */

public class TourRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private TourReqDetail tourReqDetail;

    public AdvanceLoginDataRequestModel getLoginData() {
        return loginData;
    }

    public void setLoginData(AdvanceLoginDataRequestModel loginData) {
        this.loginData = loginData;
    }

    public TourReqDetail getTourReqDetail() {
        return tourReqDetail;
    }

    public void setTourReqDetail(TourReqDetail tourReqDetail) {
        this.tourReqDetail = tourReqDetail;
    }

    static public TourRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TourRequestModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
