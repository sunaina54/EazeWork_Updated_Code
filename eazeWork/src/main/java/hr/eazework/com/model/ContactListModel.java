package hr.eazework.com.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by SUNAINA on 28-09-2018.
 */

public class ContactListModel extends GenericResponse implements Serializable {

    private String CustomerCorpID;
    private String CustomerEmpID;
    private String EmpCode;
    private String Name;

    public String getCustomerCorpID() {
        return CustomerCorpID;
    }

    public void setCustomerCorpID(String customerCorpID) {
        CustomerCorpID = customerCorpID;
    }

    public String getCustomerEmpID() {
        return CustomerEmpID;
    }

    public void setCustomerEmpID(String customerEmpID) {
        CustomerEmpID = customerEmpID;
    }

    public String getEmpCode() {
        return EmpCode;
    }

    public void setEmpCode(String empCode) {
        EmpCode = empCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    static public ContactListModel create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, ContactListModel.class);
    }
    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
