package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 17-10-2018.
 */

public class TicketResultModel extends BaseAppResponseModel implements Serializable {
    private String OpenCount;

    public String getOpenCount() {
        return OpenCount;
    }

    public void setOpenCount(String openCount) {
        OpenCount = openCount;
    }

    static public TicketResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TicketResultModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
