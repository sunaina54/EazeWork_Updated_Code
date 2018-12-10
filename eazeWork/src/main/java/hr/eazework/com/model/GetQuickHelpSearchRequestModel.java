package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 04-12-2018.
 */

public class GetQuickHelpSearchRequestModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private QuickHelpParamModel quickHelpParam;

    public AdvanceLoginDataRequestModel getLoginData() {
        return loginData;
    }

    public void setLoginData(AdvanceLoginDataRequestModel loginData) {
        this.loginData = loginData;
    }

    public QuickHelpParamModel getQuickHelpParam() {
        return quickHelpParam;
    }

    public void setQuickHelpParam(QuickHelpParamModel quickHelpParam) {
        this.quickHelpParam = quickHelpParam;
    }


    static public GetQuickHelpSearchRequestModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetQuickHelpSearchRequestModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
