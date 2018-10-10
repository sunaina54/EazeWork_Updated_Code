package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SUNAINA on 09-10-2018.
 */

public class GetContactListResultModel extends GenericResponse
        implements Serializable {
    private ArrayList<ContactListModel> contactList;

    public ArrayList<ContactListModel> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<ContactListModel> contactList) {
        this.contactList = contactList;
    }

    static public GetContactListResultModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, GetContactListResultModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
