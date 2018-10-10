package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 03-10-2018.
 */

public class SubCategoryRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private String SelfOrOther;
    private PageInputModel pageInput;

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

    public PageInputModel getPageInput() {
        return pageInput;
    }

    public void setPageInput(PageInputModel pageInput) {
        this.pageInput = pageInput;
    }

    static public SubCategoryRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, SubCategoryRequestModel.class);
    }


    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
