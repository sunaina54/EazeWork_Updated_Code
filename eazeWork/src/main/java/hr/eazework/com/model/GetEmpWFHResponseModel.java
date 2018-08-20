package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Dell3 on 12-01-2018.
 */

public class GetEmpWFHResponseModel implements Serializable {
    private GetEmpWFHRequestsResultModel GetEmpAttendanceRequestsResult;

    public GetEmpWFHRequestsResultModel getGetEmpAttendanceRequestsResult() {
        return GetEmpAttendanceRequestsResult;
    }

    public void setGetEmpAttendanceRequestsResult(GetEmpWFHRequestsResultModel getEmpAttendanceRequestsResult) {
        GetEmpAttendanceRequestsResult = getEmpAttendanceRequestsResult;
    }



    static public GetEmpWFHResponseModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetEmpWFHResponseModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
