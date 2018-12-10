package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 06-12-2018.
 */

public class UpdateEmpLocationDetailModel implements Serializable {
    private AdvanceLoginDataRequestModel loginData;
    private EmpLocationDetailModel empLocationDetail;

    static public UpdateEmpLocationDetailModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, UpdateEmpLocationDetailModel.class);
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public AdvanceLoginDataRequestModel getLoginData() {
        return loginData;
    }

    public void setLoginData(AdvanceLoginDataRequestModel loginData) {
        this.loginData = loginData;
    }

    public EmpLocationDetailModel getEmpLocationDetail() {
        return empLocationDetail;
    }

    public void setEmpLocationDetail(EmpLocationDetailModel empLocationDetail) {
        this.empLocationDetail = empLocationDetail;
    }
}
