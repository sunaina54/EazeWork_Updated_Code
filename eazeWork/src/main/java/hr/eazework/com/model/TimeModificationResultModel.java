package hr.eazework.com.model;

import java.io.Serializable;

/**
 * Created by Dell3 on 31-01-2018.
 */

public class TimeModificationResultModel extends GenericResponse implements Serializable {
    private String ReqID;

    public String getReqID() {
        return ReqID;
    }

    public void setReqID(String reqID) {
        ReqID = reqID;
    }
}
